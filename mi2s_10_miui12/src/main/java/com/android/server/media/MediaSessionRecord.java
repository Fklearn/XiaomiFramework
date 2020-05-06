package com.android.server.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.AudioManagerInternal;
import android.media.AudioSystem;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.session.ISession;
import android.media.session.ISessionCallback;
import android.media.session.ISessionController;
import android.media.session.ISessionControllerCallback;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import android.view.KeyEvent;
import com.android.server.LocalServices;
import com.android.server.slice.SliceClientPermissions;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MediaSessionRecord implements IBinder.DeathRecipient {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final int OPTIMISTIC_VOLUME_TIMEOUT = 1000;
    private static final String TAG = "MediaSessionRecord";
    /* access modifiers changed from: private */
    public AudioAttributes mAudioAttrs;
    private AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public AudioManagerInternal mAudioManagerInternal;
    private final Runnable mClearOptimisticVolumeRunnable = new Runnable() {
        public void run() {
            boolean needUpdate = MediaSessionRecord.this.mOptimisticVolume != MediaSessionRecord.this.mCurrentVolume;
            int unused = MediaSessionRecord.this.mOptimisticVolume = -1;
            if (needUpdate) {
                MediaSessionRecord.this.pushVolumeUpdate();
            }
        }
    };
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final ControllerStub mController;
    /* access modifiers changed from: private */
    public final ArrayList<ISessionControllerCallbackHolder> mControllerCallbackHolders = new ArrayList<>();
    /* access modifiers changed from: private */
    public int mCurrentVolume = 0;
    /* access modifiers changed from: private */
    public boolean mDestroyed = false;
    /* access modifiers changed from: private */
    public long mDuration = -1;
    /* access modifiers changed from: private */
    public Bundle mExtras;
    /* access modifiers changed from: private */
    public long mFlags;
    /* access modifiers changed from: private */
    public final MessageHandler mHandler;
    /* access modifiers changed from: private */
    public boolean mIsActive = false;
    /* access modifiers changed from: private */
    public PendingIntent mLaunchIntent;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public int mMaxVolume = 0;
    /* access modifiers changed from: private */
    public PendingIntent mMediaButtonReceiver;
    /* access modifiers changed from: private */
    public MediaMetadata mMetadata;
    /* access modifiers changed from: private */
    public String mMetadataDescription;
    /* access modifiers changed from: private */
    public int mOptimisticVolume = -1;
    private final int mOwnerPid;
    private final int mOwnerUid;
    /* access modifiers changed from: private */
    public final String mPackageName;
    /* access modifiers changed from: private */
    public PlaybackState mPlaybackState;
    /* access modifiers changed from: private */
    public List<MediaSession.QueueItem> mQueue;
    /* access modifiers changed from: private */
    public CharSequence mQueueTitle;
    /* access modifiers changed from: private */
    public int mRatingType;
    /* access modifiers changed from: private */
    public final MediaSessionService mService;
    private final SessionStub mSession;
    /* access modifiers changed from: private */
    public final SessionCb mSessionCb;
    /* access modifiers changed from: private */
    public final Bundle mSessionInfo;
    private final MediaSession.Token mSessionToken;
    /* access modifiers changed from: private */
    public final String mTag;
    private final int mUserId;
    /* access modifiers changed from: private */
    public int mVolumeControlType = 2;
    /* access modifiers changed from: private */
    public int mVolumeType = 1;

    public MediaSessionRecord(int ownerPid, int ownerUid, int userId, String ownerPackageName, ISessionCallback cb, String tag, Bundle sessionInfo, MediaSessionService service, Looper handlerLooper) {
        this.mOwnerPid = ownerPid;
        this.mOwnerUid = ownerUid;
        this.mUserId = userId;
        this.mPackageName = ownerPackageName;
        this.mTag = tag;
        this.mSessionInfo = sessionInfo;
        this.mController = new ControllerStub();
        this.mSessionToken = new MediaSession.Token(this.mController);
        this.mSession = new SessionStub();
        this.mSessionCb = new SessionCb(cb);
        this.mService = service;
        this.mContext = this.mService.getContext();
        this.mHandler = new MessageHandler(handlerLooper);
        this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        this.mAudioManagerInternal = (AudioManagerInternal) LocalServices.getService(AudioManagerInternal.class);
        this.mAudioAttrs = new AudioAttributes.Builder().setUsage(1).build();
    }

    public ISession getSessionBinder() {
        return this.mSession;
    }

    public ISessionController getControllerBinder() {
        return this.mController;
    }

    public MediaSession.Token getSessionToken() {
        return this.mSessionToken;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getTag() {
        return this.mTag;
    }

    public PendingIntent getMediaButtonReceiver() {
        return this.mMediaButtonReceiver;
    }

    public long getFlags() {
        return this.mFlags;
    }

    public boolean hasFlag(int flag) {
        return (this.mFlags & ((long) flag)) != 0;
    }

    public int getUid() {
        return this.mOwnerUid;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public boolean isSystemPriority() {
        return (this.mFlags & 65536) != 0;
    }

    public void adjustVolume(String packageName, String opPackageName, int pid, int uid, ISessionControllerCallback caller, boolean asSystemService, int direction, int flags, boolean useSuggested) {
        int flags2;
        int i = direction;
        int previousFlagPlaySound = flags & 4;
        if (isPlaybackActive() || hasFlag(65536)) {
            flags2 = flags & -5;
        } else {
            flags2 = flags;
        }
        if (this.mVolumeType == 1) {
            postAdjustLocalVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttrs), direction, flags2, opPackageName, pid, uid, asSystemService, useSuggested, previousFlagPlaySound);
            String str = packageName;
            boolean z = asSystemService;
        } else if (this.mVolumeControlType != 0) {
            if (i == 101 || i == -100) {
                String str2 = packageName;
                boolean z2 = asSystemService;
            } else if (i == 100) {
                String str3 = packageName;
                boolean z3 = asSystemService;
            } else {
                if (DEBUG) {
                    Log.w(TAG, "adjusting volume, pkg=" + packageName + ", asSystemService=" + asSystemService + ", dir=" + i);
                } else {
                    String str4 = packageName;
                    boolean z4 = asSystemService;
                }
                this.mSessionCb.adjustVolume(packageName, pid, uid, caller, asSystemService, direction);
                int volumeBefore = this.mOptimisticVolume;
                if (volumeBefore < 0) {
                    volumeBefore = this.mCurrentVolume;
                }
                this.mOptimisticVolume = volumeBefore + i;
                this.mOptimisticVolume = Math.max(0, Math.min(this.mOptimisticVolume, this.mMaxVolume));
                this.mHandler.removeCallbacks(this.mClearOptimisticVolumeRunnable);
                this.mHandler.postDelayed(this.mClearOptimisticVolumeRunnable, 1000);
                if (volumeBefore != this.mOptimisticVolume) {
                    pushVolumeUpdate();
                }
                this.mService.notifyRemoteVolumeChanged(flags2, this);
                if (DEBUG) {
                    Log.d(TAG, "Adjusted optimistic volume to " + this.mOptimisticVolume + " max is " + this.mMaxVolume);
                    return;
                }
                return;
            }
            Log.w(TAG, "Muting remote playback is not supported");
        }
    }

    /* access modifiers changed from: private */
    public void setVolumeTo(String packageName, String opPackageName, int pid, int uid, ISessionControllerCallback caller, int value, int flags) {
        if (this.mVolumeType == 1) {
            final int volumeValue = value;
            final int legacyStreamType = AudioAttributes.toLegacyStreamType(this.mAudioAttrs);
            final int i = flags;
            final String str = opPackageName;
            final int i2 = uid;
            this.mHandler.post(new Runnable() {
                public void run() {
                    try {
                        MediaSessionRecord.this.mAudioManagerInternal.setStreamVolumeForUid(legacyStreamType, volumeValue, i, str, i2);
                    } catch (IllegalArgumentException | SecurityException e) {
                        Log.e(MediaSessionRecord.TAG, "Cannot set volume: stream=" + legacyStreamType + ", value=" + volumeValue + ", flags=" + i, e);
                    }
                }
            });
            int i3 = value;
            int i4 = flags;
        } else if (this.mVolumeControlType == 2) {
            int value2 = Math.max(0, Math.min(value, this.mMaxVolume));
            this.mSessionCb.setVolumeTo(packageName, pid, uid, caller, value2);
            int volumeBefore = this.mOptimisticVolume;
            if (volumeBefore < 0) {
                volumeBefore = this.mCurrentVolume;
            }
            this.mOptimisticVolume = Math.max(0, Math.min(value2, this.mMaxVolume));
            this.mHandler.removeCallbacks(this.mClearOptimisticVolumeRunnable);
            this.mHandler.postDelayed(this.mClearOptimisticVolumeRunnable, 1000);
            if (volumeBefore != this.mOptimisticVolume) {
                pushVolumeUpdate();
            }
            this.mService.notifyRemoteVolumeChanged(flags, this);
            if (DEBUG) {
                Log.d(TAG, "Set optimistic volume to " + this.mOptimisticVolume + " max is " + this.mMaxVolume);
            }
        }
    }

    public boolean isActive() {
        return this.mIsActive && !this.mDestroyed;
    }

    public PlaybackState getPlaybackState() {
        return this.mPlaybackState;
    }

    public boolean isPlaybackActive() {
        PlaybackState playbackState = this.mPlaybackState;
        return MediaSession.isActiveState(playbackState == null ? 0 : playbackState.getState());
    }

    public int getPlaybackType() {
        return this.mVolumeType;
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAudioAttrs;
    }

    public int getVolumeControl() {
        return this.mVolumeControlType;
    }

    public int getMaxVolume() {
        return this.mMaxVolume;
    }

    public int getCurrentVolume() {
        return this.mCurrentVolume;
    }

    public int getOptimisticVolume() {
        return this.mOptimisticVolume;
    }

    public boolean isTransportControlEnabled() {
        return hasFlag(2);
    }

    public void binderDied() {
        this.mService.sessionDied(this);
    }

    public void onDestroy() {
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                this.mDestroyed = true;
                this.mHandler.post(9);
            }
        }
    }

    public ISessionCallback getCallback() {
        return this.mSessionCb.mCb;
    }

    public boolean sendMediaButton(String packageName, int pid, int uid, boolean asSystemService, KeyEvent ke, int sequenceId, ResultReceiver cb) {
        return this.mSessionCb.sendMediaButton(packageName, pid, uid, asSystemService, ke, sequenceId, cb);
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + this.mTag + " " + this);
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("  ");
        String indent = sb.toString();
        pw.println(indent + "ownerPid=" + this.mOwnerPid + ", ownerUid=" + this.mOwnerUid + ", userId=" + this.mUserId);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(indent);
        sb2.append("package=");
        sb2.append(this.mPackageName);
        pw.println(sb2.toString());
        pw.println(indent + "launchIntent=" + this.mLaunchIntent);
        pw.println(indent + "mediaButtonReceiver=" + this.mMediaButtonReceiver);
        pw.println(indent + "active=" + this.mIsActive);
        pw.println(indent + "flags=" + this.mFlags);
        pw.println(indent + "rating type=" + this.mRatingType);
        pw.println(indent + "controllers: " + this.mControllerCallbackHolders.size());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(indent);
        sb3.append("state=");
        PlaybackState playbackState = this.mPlaybackState;
        sb3.append(playbackState == null ? null : playbackState.toString());
        pw.println(sb3.toString());
        pw.println(indent + "audioAttrs=" + this.mAudioAttrs);
        pw.println(indent + "volumeType=" + this.mVolumeType + ", controlType=" + this.mVolumeControlType + ", max=" + this.mMaxVolume + ", current=" + this.mCurrentVolume);
        StringBuilder sb4 = new StringBuilder();
        sb4.append(indent);
        sb4.append("metadata: ");
        sb4.append(this.mMetadataDescription);
        pw.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(indent);
        sb5.append("queueTitle=");
        sb5.append(this.mQueueTitle);
        sb5.append(", size=");
        List<MediaSession.QueueItem> list = this.mQueue;
        sb5.append(list == null ? 0 : list.size());
        pw.println(sb5.toString());
    }

    public String toString() {
        return this.mPackageName + SliceClientPermissions.SliceAuthority.DELIMITER + this.mTag + " (userId=" + this.mUserId + ")";
    }

    private void postAdjustLocalVolume(int stream, int direction, int flags, String callingOpPackageName, int callingPid, int callingUid, boolean asSystemService, boolean useSuggested, int previousFlagPlaySound) {
        int uid;
        String opPackageName;
        boolean z = asSystemService;
        if (DEBUG) {
            Log.w(TAG, "adjusting local volume, stream=" + stream + ", dir=" + direction + ", asSystemService=" + z + ", useSuggested=" + useSuggested);
        } else {
            int i = stream;
            int i2 = direction;
            boolean z2 = useSuggested;
        }
        if (z) {
            opPackageName = this.mContext.getOpPackageName();
            uid = 1000;
        } else {
            opPackageName = callingOpPackageName;
            uid = callingUid;
        }
        MessageHandler messageHandler = this.mHandler;
        final boolean z3 = useSuggested;
        final int i3 = stream;
        final int i4 = direction;
        final int i5 = flags;
        final String str = opPackageName;
        AnonymousClass2 r9 = r0;
        final int i6 = uid;
        MessageHandler messageHandler2 = messageHandler;
        final int i7 = previousFlagPlaySound;
        AnonymousClass2 r0 = new Runnable() {
            public void run() {
                try {
                    if (!z3) {
                        MediaSessionRecord.this.mAudioManagerInternal.adjustStreamVolumeForUid(i3, i4, i5, str, i6);
                    } else if (AudioSystem.isStreamActive(i3, 0)) {
                        MediaSessionRecord.this.mAudioManagerInternal.adjustSuggestedStreamVolumeForUid(i3, i4, i5, str, i6);
                    } else {
                        MediaSessionRecord.this.mAudioManagerInternal.adjustSuggestedStreamVolumeForUid(Integer.MIN_VALUE, i4, i7 | i5, str, i6);
                    }
                } catch (IllegalArgumentException | SecurityException e) {
                    Log.e(MediaSessionRecord.TAG, "Cannot adjust volume: direction=" + i4 + ", stream=" + i3 + ", flags=" + i5 + ", opPackageName=" + str + ", uid=" + i6 + ", useSuggested=" + z3 + ", previousFlagPlaySound=" + i7, e);
                }
            }
        };
        messageHandler2.post(r9);
    }

    private void logCallbackException(String msg, ISessionControllerCallbackHolder holder, Exception e) {
        Log.v(TAG, msg + ", this=" + this + ", callback package=" + holder.mPackageName + ", exception=" + e);
    }

    /* access modifiers changed from: private */
    public void pushPlaybackStateUpdate() {
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
                    ISessionControllerCallbackHolder holder = this.mControllerCallbackHolders.get(i);
                    try {
                        holder.mCallback.onPlaybackStateChanged(this.mPlaybackState);
                    } catch (DeadObjectException e) {
                        this.mControllerCallbackHolders.remove(i);
                        logCallbackException("Removing dead callback in pushPlaybackStateUpdate", holder, e);
                    } catch (RemoteException e2) {
                        logCallbackException("unexpected exception in pushPlaybackStateUpdate", holder, e2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void pushMetadataUpdate() {
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
                    ISessionControllerCallbackHolder holder = this.mControllerCallbackHolders.get(i);
                    try {
                        holder.mCallback.onMetadataChanged(this.mMetadata);
                    } catch (DeadObjectException e) {
                        this.mControllerCallbackHolders.remove(i);
                        logCallbackException("Removing dead callback in pushMetadataUpdate", holder, e);
                    } catch (RemoteException e2) {
                        logCallbackException("unexpected exception in pushMetadataUpdate", holder, e2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void pushQueueUpdate() {
        ParceledListSlice parceledListSlice;
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
                    ISessionControllerCallbackHolder holder = this.mControllerCallbackHolders.get(i);
                    try {
                        ISessionControllerCallback access$400 = holder.mCallback;
                        if (this.mQueue == null) {
                            parceledListSlice = null;
                        } else {
                            parceledListSlice = new ParceledListSlice(this.mQueue);
                        }
                        access$400.onQueueChanged(parceledListSlice);
                    } catch (DeadObjectException e) {
                        this.mControllerCallbackHolders.remove(i);
                        logCallbackException("Removing dead callback in pushQueueUpdate", holder, e);
                    } catch (RemoteException e2) {
                        logCallbackException("unexpected exception in pushQueueUpdate", holder, e2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void pushQueueTitleUpdate() {
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
                    ISessionControllerCallbackHolder holder = this.mControllerCallbackHolders.get(i);
                    try {
                        holder.mCallback.onQueueTitleChanged(this.mQueueTitle);
                    } catch (DeadObjectException e) {
                        this.mControllerCallbackHolders.remove(i);
                        logCallbackException("Removing dead callback in pushQueueTitleUpdate", holder, e);
                    } catch (RemoteException e2) {
                        logCallbackException("unexpected exception in pushQueueTitleUpdate", holder, e2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void pushExtrasUpdate() {
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
                    ISessionControllerCallbackHolder holder = this.mControllerCallbackHolders.get(i);
                    try {
                        holder.mCallback.onExtrasChanged(this.mExtras);
                    } catch (DeadObjectException e) {
                        this.mControllerCallbackHolders.remove(i);
                        logCallbackException("Removing dead callback in pushExtrasUpdate", holder, e);
                    } catch (RemoteException e2) {
                        logCallbackException("unexpected exception in pushExtrasUpdate", holder, e2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void pushVolumeUpdate() {
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                MediaController.PlaybackInfo info = getVolumeAttributes();
                for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
                    ISessionControllerCallbackHolder holder = this.mControllerCallbackHolders.get(i);
                    try {
                        holder.mCallback.onVolumeInfoChanged(info);
                    } catch (DeadObjectException e) {
                        this.mControllerCallbackHolders.remove(i);
                        logCallbackException("Removing dead callback in pushVolumeUpdate", holder, e);
                    } catch (RemoteException e2) {
                        logCallbackException("unexpected exception in pushVolumeUpdate", holder, e2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void pushEvent(String event, Bundle data) {
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
                    ISessionControllerCallbackHolder holder = this.mControllerCallbackHolders.get(i);
                    try {
                        holder.mCallback.onEvent(event, data);
                    } catch (DeadObjectException e) {
                        this.mControllerCallbackHolders.remove(i);
                        logCallbackException("Removing dead callback in pushEvent", holder, e);
                    } catch (RemoteException e2) {
                        logCallbackException("unexpected exception in pushEvent", holder, e2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void pushSessionDestroyed() {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
                    ISessionControllerCallbackHolder holder = this.mControllerCallbackHolders.get(i);
                    try {
                        holder.mCallback.onSessionDestroyed();
                    } catch (DeadObjectException e) {
                        this.mControllerCallbackHolders.remove(i);
                        logCallbackException("Removing dead callback in pushSessionDestroyed", holder, e);
                    } catch (RemoteException e2) {
                        logCallbackException("unexpected exception in pushSessionDestroyed", holder, e2);
                    }
                }
                this.mControllerCallbackHolders.clear();
            }
        }
    }

    /* access modifiers changed from: private */
    public PlaybackState getStateWithUpdatedPosition() {
        PlaybackState state;
        long duration;
        long position;
        synchronized (this.mLock) {
            state = this.mPlaybackState;
            duration = this.mDuration;
        }
        PlaybackState result = null;
        if (state != null && (state.getState() == 3 || state.getState() == 4 || state.getState() == 5)) {
            long updateTime = state.getLastPositionUpdateTime();
            long currentTime = SystemClock.elapsedRealtime();
            if (updateTime > 0) {
                long position2 = ((long) (state.getPlaybackSpeed() * ((float) (currentTime - updateTime)))) + state.getPosition();
                if (duration >= 0 && position2 > duration) {
                    position = duration;
                } else if (position2 < 0) {
                    position = 0;
                } else {
                    position = position2;
                }
                PlaybackState.Builder builder = new PlaybackState.Builder(state);
                builder.setState(state.getState(), position, state.getPlaybackSpeed(), currentTime);
                result = builder.build();
            }
        }
        return result == null ? state : result;
    }

    /* access modifiers changed from: private */
    public int getControllerHolderIndexForCb(ISessionControllerCallback cb) {
        IBinder binder = cb.asBinder();
        for (int i = this.mControllerCallbackHolders.size() - 1; i >= 0; i--) {
            if (binder.equals(this.mControllerCallbackHolders.get(i).mCallback.asBinder())) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public MediaController.PlaybackInfo getVolumeAttributes() {
        synchronized (this.mLock) {
            if (this.mVolumeType == 2) {
                MediaController.PlaybackInfo playbackInfo = new MediaController.PlaybackInfo(this.mVolumeType, this.mVolumeControlType, this.mMaxVolume, this.mOptimisticVolume != -1 ? this.mOptimisticVolume : this.mCurrentVolume, this.mAudioAttrs);
                return playbackInfo;
            }
            int volumeType = this.mVolumeType;
            AudioAttributes attributes = this.mAudioAttrs;
            int stream = AudioAttributes.toLegacyStreamType(attributes);
            return new MediaController.PlaybackInfo(volumeType, 2, this.mAudioManager.getStreamMaxVolume(stream), this.mAudioManager.getStreamVolume(stream), attributes);
        }
    }

    private final class SessionStub extends ISession.Stub {
        private SessionStub() {
        }

        public void destroySession() throws RemoteException {
            long token = Binder.clearCallingIdentity();
            try {
                MediaSessionRecord.this.mService.destroySession(MediaSessionRecord.this);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void sendEvent(String event, Bundle data) throws RemoteException {
            MediaSessionRecord.this.mHandler.post(6, event, data == null ? null : new Bundle(data));
        }

        public ISessionController getController() throws RemoteException {
            return MediaSessionRecord.this.mController;
        }

        /* JADX INFO: finally extract failed */
        public void setActive(boolean active) throws RemoteException {
            boolean unused = MediaSessionRecord.this.mIsActive = active;
            long token = Binder.clearCallingIdentity();
            try {
                MediaSessionRecord.this.mService.updateSession(MediaSessionRecord.this);
                Binder.restoreCallingIdentity(token);
                MediaSessionRecord.this.mHandler.post(7);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        public void setFlags(int flags) throws RemoteException {
            if ((flags & 65536) != 0) {
                MediaSessionRecord.this.mService.enforcePhoneStatePermission(Binder.getCallingPid(), Binder.getCallingUid());
            }
            long unused = MediaSessionRecord.this.mFlags = (long) flags;
            if ((65536 & flags) != 0) {
                long token = Binder.clearCallingIdentity();
                try {
                    MediaSessionRecord.this.mService.setGlobalPrioritySession(MediaSessionRecord.this);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
            MediaSessionRecord.this.mHandler.post(7);
        }

        public void setMediaButtonReceiver(PendingIntent pi) throws RemoteException {
            PendingIntent unused = MediaSessionRecord.this.mMediaButtonReceiver = pi;
            long token = Binder.clearCallingIdentity();
            try {
                MediaSessionRecord.this.mService.onMediaButtonReceiverChanged(MediaSessionRecord.this);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void setLaunchPendingIntent(PendingIntent pi) throws RemoteException {
            PendingIntent unused = MediaSessionRecord.this.mLaunchIntent = pi;
        }

        public void setMetadata(MediaMetadata metadata, long duration, String metadataDescription) throws RemoteException {
            MediaMetadata temp;
            synchronized (MediaSessionRecord.this.mLock) {
                if (metadata == null) {
                    temp = null;
                } else {
                    temp = new MediaMetadata.Builder(metadata).build();
                }
                if (temp != null) {
                    temp.size();
                }
                MediaMetadata unused = MediaSessionRecord.this.mMetadata = temp;
                long unused2 = MediaSessionRecord.this.mDuration = duration;
                String unused3 = MediaSessionRecord.this.mMetadataDescription = metadataDescription;
            }
            MediaSessionRecord.this.mHandler.post(1);
        }

        /* JADX INFO: finally extract failed */
        public void setPlaybackState(PlaybackState state) throws RemoteException {
            int newState = 0;
            int oldState = MediaSessionRecord.this.mPlaybackState == null ? 0 : MediaSessionRecord.this.mPlaybackState.getState();
            if (state != null) {
                newState = state.getState();
            }
            synchronized (MediaSessionRecord.this.mLock) {
                PlaybackState unused = MediaSessionRecord.this.mPlaybackState = state;
            }
            long token = Binder.clearCallingIdentity();
            try {
                MediaSessionRecord.this.mService.onSessionPlaystateChanged(MediaSessionRecord.this, oldState, newState);
                Binder.restoreCallingIdentity(token);
                MediaSessionRecord.this.mHandler.post(2);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        public void setQueue(ParceledListSlice queue) throws RemoteException {
            synchronized (MediaSessionRecord.this.mLock) {
                List unused = MediaSessionRecord.this.mQueue = queue == null ? null : queue.getList();
            }
            MediaSessionRecord.this.mHandler.post(3);
        }

        public void setQueueTitle(CharSequence title) throws RemoteException {
            CharSequence unused = MediaSessionRecord.this.mQueueTitle = title;
            MediaSessionRecord.this.mHandler.post(4);
        }

        public void setExtras(Bundle extras) throws RemoteException {
            synchronized (MediaSessionRecord.this.mLock) {
                Bundle unused = MediaSessionRecord.this.mExtras = extras == null ? null : new Bundle(extras);
            }
            MediaSessionRecord.this.mHandler.post(5);
        }

        public void setRatingType(int type) throws RemoteException {
            int unused = MediaSessionRecord.this.mRatingType = type;
        }

        public void setCurrentVolume(int volume) throws RemoteException {
            int unused = MediaSessionRecord.this.mCurrentVolume = volume;
            MediaSessionRecord.this.mHandler.post(8);
        }

        /* JADX INFO: finally extract failed */
        public void setPlaybackToLocal(AudioAttributes attributes) throws RemoteException {
            boolean typeChanged;
            synchronized (MediaSessionRecord.this.mLock) {
                typeChanged = MediaSessionRecord.this.mVolumeType == 2;
                int unused = MediaSessionRecord.this.mVolumeType = 1;
                if (attributes != null) {
                    AudioAttributes unused2 = MediaSessionRecord.this.mAudioAttrs = attributes;
                } else {
                    Log.e(MediaSessionRecord.TAG, "Received null audio attributes, using existing attributes");
                }
            }
            if (typeChanged) {
                long token = Binder.clearCallingIdentity();
                try {
                    MediaSessionRecord.this.mService.onSessionPlaybackTypeChanged(MediaSessionRecord.this);
                    Binder.restoreCallingIdentity(token);
                    MediaSessionRecord.this.mHandler.post(8);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(token);
                    throw th;
                }
            }
        }

        /* JADX INFO: finally extract failed */
        public void setPlaybackToRemote(int control, int max) throws RemoteException {
            boolean typeChanged;
            synchronized (MediaSessionRecord.this.mLock) {
                boolean z = true;
                if (MediaSessionRecord.this.mVolumeType != 1) {
                    z = false;
                }
                typeChanged = z;
                int unused = MediaSessionRecord.this.mVolumeType = 2;
                int unused2 = MediaSessionRecord.this.mVolumeControlType = control;
                int unused3 = MediaSessionRecord.this.mMaxVolume = max;
            }
            if (typeChanged) {
                long token = Binder.clearCallingIdentity();
                try {
                    MediaSessionRecord.this.mService.onSessionPlaybackTypeChanged(MediaSessionRecord.this);
                    Binder.restoreCallingIdentity(token);
                    MediaSessionRecord.this.mHandler.post(8);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(token);
                    throw th;
                }
            }
        }
    }

    class SessionCb {
        /* access modifiers changed from: private */
        public final ISessionCallback mCb;

        SessionCb(ISessionCallback cb) {
            this.mCb = cb;
        }

        public boolean sendMediaButton(String packageName, int pid, int uid, boolean asSystemService, KeyEvent keyEvent, int sequenceId, ResultReceiver cb) {
            if (asSystemService) {
                try {
                    this.mCb.onMediaButton(MediaSessionRecord.this.mContext.getPackageName(), Process.myPid(), 1000, createMediaButtonIntent(keyEvent), sequenceId, cb);
                    return true;
                } catch (RemoteException e) {
                    Slog.e(MediaSessionRecord.TAG, "Remote failure in sendMediaRequest.", e);
                    return false;
                }
            } else {
                this.mCb.onMediaButton(packageName, pid, uid, createMediaButtonIntent(keyEvent), sequenceId, cb);
                return true;
            }
        }

        public boolean sendMediaButton(String packageName, int pid, int uid, ISessionControllerCallback caller, boolean asSystemService, KeyEvent keyEvent) {
            if (asSystemService) {
                try {
                    this.mCb.onMediaButton(MediaSessionRecord.this.mContext.getPackageName(), Process.myPid(), 1000, createMediaButtonIntent(keyEvent), 0, (ResultReceiver) null);
                    return true;
                } catch (RemoteException e) {
                    Slog.e(MediaSessionRecord.TAG, "Remote failure in sendMediaRequest.", e);
                    return false;
                }
            } else {
                this.mCb.onMediaButtonFromController(packageName, pid, uid, caller, createMediaButtonIntent(keyEvent));
                return true;
            }
        }

        public void sendCommand(String packageName, int pid, int uid, ISessionControllerCallback caller, String command, Bundle args, ResultReceiver cb) {
            try {
                this.mCb.onCommand(packageName, pid, uid, caller, command, args, cb);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in sendCommand.", e);
            }
        }

        public void sendCustomAction(String packageName, int pid, int uid, ISessionControllerCallback caller, String action, Bundle args) {
            try {
                this.mCb.onCustomAction(packageName, pid, uid, caller, action, args);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in sendCustomAction.", e);
            }
        }

        public void prepare(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            try {
                this.mCb.onPrepare(packageName, pid, uid, caller);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in prepare.", e);
            }
        }

        public void prepareFromMediaId(String packageName, int pid, int uid, ISessionControllerCallback caller, String mediaId, Bundle extras) {
            try {
                this.mCb.onPrepareFromMediaId(packageName, pid, uid, caller, mediaId, extras);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in prepareFromMediaId.", e);
            }
        }

        public void prepareFromSearch(String packageName, int pid, int uid, ISessionControllerCallback caller, String query, Bundle extras) {
            try {
                this.mCb.onPrepareFromSearch(packageName, pid, uid, caller, query, extras);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in prepareFromSearch.", e);
            }
        }

        public void prepareFromUri(String packageName, int pid, int uid, ISessionControllerCallback caller, Uri uri, Bundle extras) {
            try {
                this.mCb.onPrepareFromUri(packageName, pid, uid, caller, uri, extras);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in prepareFromUri.", e);
            }
        }

        public void play(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            try {
                this.mCb.onPlay(packageName, pid, uid, caller);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in play.", e);
            }
        }

        public void playFromMediaId(String packageName, int pid, int uid, ISessionControllerCallback caller, String mediaId, Bundle extras) {
            try {
                this.mCb.onPlayFromMediaId(packageName, pid, uid, caller, mediaId, extras);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in playFromMediaId.", e);
            }
        }

        public void playFromSearch(String packageName, int pid, int uid, ISessionControllerCallback caller, String query, Bundle extras) {
            try {
                this.mCb.onPlayFromSearch(packageName, pid, uid, caller, query, extras);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in playFromSearch.", e);
            }
        }

        public void playFromUri(String packageName, int pid, int uid, ISessionControllerCallback caller, Uri uri, Bundle extras) {
            try {
                this.mCb.onPlayFromUri(packageName, pid, uid, caller, uri, extras);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in playFromUri.", e);
            }
        }

        public void skipToTrack(String packageName, int pid, int uid, ISessionControllerCallback caller, long id) {
            try {
                this.mCb.onSkipToTrack(packageName, pid, uid, caller, id);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in skipToTrack", e);
            }
        }

        public void pause(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            try {
                this.mCb.onPause(packageName, pid, uid, caller);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in pause.", e);
            }
        }

        public void stop(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            try {
                this.mCb.onStop(packageName, pid, uid, caller);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in stop.", e);
            }
        }

        public void next(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            try {
                this.mCb.onNext(packageName, pid, uid, caller);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in next.", e);
            }
        }

        public void previous(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            try {
                this.mCb.onPrevious(packageName, pid, uid, caller);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in previous.", e);
            }
        }

        public void fastForward(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            try {
                this.mCb.onFastForward(packageName, pid, uid, caller);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in fastForward.", e);
            }
        }

        public void rewind(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            try {
                this.mCb.onRewind(packageName, pid, uid, caller);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in rewind.", e);
            }
        }

        public void seekTo(String packageName, int pid, int uid, ISessionControllerCallback caller, long pos) {
            try {
                this.mCb.onSeekTo(packageName, pid, uid, caller, pos);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in seekTo.", e);
            }
        }

        public void rate(String packageName, int pid, int uid, ISessionControllerCallback caller, Rating rating) {
            try {
                this.mCb.onRate(packageName, pid, uid, caller, rating);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in rate.", e);
            }
        }

        public void setPlaybackSpeed(String packageName, int pid, int uid, ISessionControllerCallback caller, float speed) {
            try {
                this.mCb.onSetPlaybackSpeed(packageName, pid, uid, caller, speed);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in setPlaybackSpeed.", e);
            }
        }

        public void adjustVolume(String packageName, int pid, int uid, ISessionControllerCallback caller, boolean asSystemService, int direction) {
            if (asSystemService) {
                try {
                    this.mCb.onAdjustVolume(MediaSessionRecord.this.mContext.getPackageName(), Process.myPid(), 1000, (ISessionControllerCallback) null, direction);
                } catch (RemoteException e) {
                    Slog.e(MediaSessionRecord.TAG, "Remote failure in adjustVolume.", e);
                }
            } else {
                this.mCb.onAdjustVolume(packageName, pid, uid, caller, direction);
            }
        }

        public void setVolumeTo(String packageName, int pid, int uid, ISessionControllerCallback caller, int value) {
            try {
                this.mCb.onSetVolumeTo(packageName, pid, uid, caller, value);
            } catch (RemoteException e) {
                Slog.e(MediaSessionRecord.TAG, "Remote failure in setVolumeTo.", e);
            }
        }

        private Intent createMediaButtonIntent(KeyEvent keyEvent) {
            Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
            mediaButtonIntent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
            return mediaButtonIntent;
        }
    }

    class ControllerStub extends ISessionController.Stub {
        ControllerStub() {
        }

        public void sendCommand(String packageName, ISessionControllerCallback caller, String command, Bundle args, ResultReceiver cb) {
            MediaSessionRecord.this.mSessionCb.sendCommand(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, command, args, cb);
        }

        public boolean sendMediaButton(String packageName, ISessionControllerCallback cb, KeyEvent keyEvent) {
            return MediaSessionRecord.this.mSessionCb.sendMediaButton(packageName, Binder.getCallingPid(), Binder.getCallingUid(), cb, false, keyEvent);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0058, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void registerCallback(java.lang.String r6, android.media.session.ISessionControllerCallback r7) {
            /*
                r5 = this;
                com.android.server.media.MediaSessionRecord r0 = com.android.server.media.MediaSessionRecord.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.media.MediaSessionRecord r1 = com.android.server.media.MediaSessionRecord.this     // Catch:{ all -> 0x0059 }
                boolean r1 = r1.mDestroyed     // Catch:{ all -> 0x0059 }
                if (r1 == 0) goto L_0x0016
                r7.onSessionDestroyed()     // Catch:{ Exception -> 0x0013 }
                goto L_0x0014
            L_0x0013:
                r1 = move-exception
            L_0x0014:
                monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                return
            L_0x0016:
                com.android.server.media.MediaSessionRecord r1 = com.android.server.media.MediaSessionRecord.this     // Catch:{ all -> 0x0059 }
                int r1 = r1.getControllerHolderIndexForCb(r7)     // Catch:{ all -> 0x0059 }
                if (r1 >= 0) goto L_0x0057
                com.android.server.media.MediaSessionRecord r1 = com.android.server.media.MediaSessionRecord.this     // Catch:{ all -> 0x0059 }
                java.util.ArrayList r1 = r1.mControllerCallbackHolders     // Catch:{ all -> 0x0059 }
                com.android.server.media.MediaSessionRecord$ISessionControllerCallbackHolder r2 = new com.android.server.media.MediaSessionRecord$ISessionControllerCallbackHolder     // Catch:{ all -> 0x0059 }
                com.android.server.media.MediaSessionRecord r3 = com.android.server.media.MediaSessionRecord.this     // Catch:{ all -> 0x0059 }
                int r4 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0059 }
                r2.<init>(r7, r6, r4)     // Catch:{ all -> 0x0059 }
                r1.add(r2)     // Catch:{ all -> 0x0059 }
                boolean r1 = com.android.server.media.MediaSessionRecord.DEBUG     // Catch:{ all -> 0x0059 }
                if (r1 == 0) goto L_0x0057
                java.lang.String r1 = "MediaSessionRecord"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0059 }
                r2.<init>()     // Catch:{ all -> 0x0059 }
                java.lang.String r3 = "registering controller callback "
                r2.append(r3)     // Catch:{ all -> 0x0059 }
                r2.append(r7)     // Catch:{ all -> 0x0059 }
                java.lang.String r3 = " from controller"
                r2.append(r3)     // Catch:{ all -> 0x0059 }
                r2.append(r6)     // Catch:{ all -> 0x0059 }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0059 }
                android.util.Log.d(r1, r2)     // Catch:{ all -> 0x0059 }
            L_0x0057:
                monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                return
            L_0x0059:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.media.MediaSessionRecord.ControllerStub.registerCallback(java.lang.String, android.media.session.ISessionControllerCallback):void");
        }

        public void unregisterCallback(ISessionControllerCallback cb) {
            synchronized (MediaSessionRecord.this.mLock) {
                int index = MediaSessionRecord.this.getControllerHolderIndexForCb(cb);
                if (index != -1) {
                    MediaSessionRecord.this.mControllerCallbackHolders.remove(index);
                }
                if (MediaSessionRecord.DEBUG) {
                    Log.d(MediaSessionRecord.TAG, "unregistering callback " + cb.asBinder());
                }
            }
        }

        public String getPackageName() {
            return MediaSessionRecord.this.mPackageName;
        }

        public String getTag() {
            return MediaSessionRecord.this.mTag;
        }

        public Bundle getSessionInfo() {
            return MediaSessionRecord.this.mSessionInfo;
        }

        public PendingIntent getLaunchPendingIntent() {
            return MediaSessionRecord.this.mLaunchIntent;
        }

        public long getFlags() {
            return MediaSessionRecord.this.mFlags;
        }

        public MediaController.PlaybackInfo getVolumeAttributes() {
            return MediaSessionRecord.this.getVolumeAttributes();
        }

        public void adjustVolume(String packageName, String opPackageName, ISessionControllerCallback caller, int direction, int flags) {
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            long token = Binder.clearCallingIdentity();
            try {
                MediaSessionRecord.this.adjustVolume(packageName, opPackageName, pid, uid, caller, false, direction, flags, false);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void setVolumeTo(String packageName, String opPackageName, ISessionControllerCallback caller, int value, int flags) {
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            long token = Binder.clearCallingIdentity();
            try {
                MediaSessionRecord.this.setVolumeTo(packageName, opPackageName, pid, uid, caller, value, flags);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void prepare(String packageName, ISessionControllerCallback caller) {
            MediaSessionRecord.this.mSessionCb.prepare(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller);
        }

        public void prepareFromMediaId(String packageName, ISessionControllerCallback caller, String mediaId, Bundle extras) {
            MediaSessionRecord.this.mSessionCb.prepareFromMediaId(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, mediaId, extras);
        }

        public void prepareFromSearch(String packageName, ISessionControllerCallback caller, String query, Bundle extras) {
            MediaSessionRecord.this.mSessionCb.prepareFromSearch(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, query, extras);
        }

        public void prepareFromUri(String packageName, ISessionControllerCallback caller, Uri uri, Bundle extras) {
            MediaSessionRecord.this.mSessionCb.prepareFromUri(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, uri, extras);
        }

        public void play(String packageName, ISessionControllerCallback caller) {
            MediaSessionRecord.this.mSessionCb.play(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller);
        }

        public void playFromMediaId(String packageName, ISessionControllerCallback caller, String mediaId, Bundle extras) {
            MediaSessionRecord.this.mSessionCb.playFromMediaId(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, mediaId, extras);
        }

        public void playFromSearch(String packageName, ISessionControllerCallback caller, String query, Bundle extras) {
            MediaSessionRecord.this.mSessionCb.playFromSearch(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, query, extras);
        }

        public void playFromUri(String packageName, ISessionControllerCallback caller, Uri uri, Bundle extras) {
            MediaSessionRecord.this.mSessionCb.playFromUri(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, uri, extras);
        }

        public void skipToQueueItem(String packageName, ISessionControllerCallback caller, long id) {
            MediaSessionRecord.this.mSessionCb.skipToTrack(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, id);
        }

        public void pause(String packageName, ISessionControllerCallback caller) {
            MediaSessionRecord.this.mSessionCb.pause(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller);
        }

        public void stop(String packageName, ISessionControllerCallback caller) {
            MediaSessionRecord.this.mSessionCb.stop(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller);
        }

        public void next(String packageName, ISessionControllerCallback caller) {
            MediaSessionRecord.this.mSessionCb.next(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller);
        }

        public void previous(String packageName, ISessionControllerCallback caller) {
            MediaSessionRecord.this.mSessionCb.previous(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller);
        }

        public void fastForward(String packageName, ISessionControllerCallback caller) {
            MediaSessionRecord.this.mSessionCb.fastForward(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller);
        }

        public void rewind(String packageName, ISessionControllerCallback caller) {
            MediaSessionRecord.this.mSessionCb.rewind(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller);
        }

        public void seekTo(String packageName, ISessionControllerCallback caller, long pos) {
            MediaSessionRecord.this.mSessionCb.seekTo(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, pos);
        }

        public void rate(String packageName, ISessionControllerCallback caller, Rating rating) {
            MediaSessionRecord.this.mSessionCb.rate(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, rating);
        }

        public void setPlaybackSpeed(String packageName, ISessionControllerCallback caller, float speed) {
            MediaSessionRecord.this.mSessionCb.setPlaybackSpeed(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, speed);
        }

        public void sendCustomAction(String packageName, ISessionControllerCallback caller, String action, Bundle args) {
            MediaSessionRecord.this.mSessionCb.sendCustomAction(packageName, Binder.getCallingPid(), Binder.getCallingUid(), caller, action, args);
        }

        public MediaMetadata getMetadata() {
            MediaMetadata access$1600;
            synchronized (MediaSessionRecord.this.mLock) {
                access$1600 = MediaSessionRecord.this.mMetadata;
            }
            return access$1600;
        }

        public PlaybackState getPlaybackState() {
            return MediaSessionRecord.this.getStateWithUpdatedPosition();
        }

        public ParceledListSlice getQueue() {
            ParceledListSlice parceledListSlice;
            synchronized (MediaSessionRecord.this.mLock) {
                parceledListSlice = MediaSessionRecord.this.mQueue == null ? null : new ParceledListSlice(MediaSessionRecord.this.mQueue);
            }
            return parceledListSlice;
        }

        public CharSequence getQueueTitle() {
            return MediaSessionRecord.this.mQueueTitle;
        }

        public Bundle getExtras() {
            Bundle access$2200;
            synchronized (MediaSessionRecord.this.mLock) {
                access$2200 = MediaSessionRecord.this.mExtras;
            }
            return access$2200;
        }

        public int getRatingType() {
            return MediaSessionRecord.this.mRatingType;
        }
    }

    private class ISessionControllerCallbackHolder {
        /* access modifiers changed from: private */
        public final ISessionControllerCallback mCallback;
        /* access modifiers changed from: private */
        public final String mPackageName;
        private final int mUid;

        ISessionControllerCallbackHolder(ISessionControllerCallback callback, String packageName, int uid) {
            this.mCallback = callback;
            this.mPackageName = packageName;
            this.mUid = uid;
        }
    }

    private class MessageHandler extends Handler {
        private static final int MSG_DESTROYED = 9;
        private static final int MSG_SEND_EVENT = 6;
        private static final int MSG_UPDATE_EXTRAS = 5;
        private static final int MSG_UPDATE_METADATA = 1;
        private static final int MSG_UPDATE_PLAYBACK_STATE = 2;
        private static final int MSG_UPDATE_QUEUE = 3;
        private static final int MSG_UPDATE_QUEUE_TITLE = 4;
        private static final int MSG_UPDATE_SESSION_STATE = 7;
        private static final int MSG_UPDATE_VOLUME = 8;

        public MessageHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MediaSessionRecord.this.pushMetadataUpdate();
                    return;
                case 2:
                    MediaSessionRecord.this.pushPlaybackStateUpdate();
                    return;
                case 3:
                    MediaSessionRecord.this.pushQueueUpdate();
                    return;
                case 4:
                    MediaSessionRecord.this.pushQueueTitleUpdate();
                    return;
                case 5:
                    MediaSessionRecord.this.pushExtrasUpdate();
                    return;
                case 6:
                    MediaSessionRecord.this.pushEvent((String) msg.obj, msg.getData());
                    return;
                case 8:
                    MediaSessionRecord.this.pushVolumeUpdate();
                    return;
                case 9:
                    MediaSessionRecord.this.pushSessionDestroyed();
                    return;
                default:
                    return;
            }
        }

        public void post(int what) {
            post(what, (Object) null);
        }

        public void post(int what, Object obj) {
            obtainMessage(what, obj).sendToTarget();
        }

        public void post(int what, Object obj, Bundle data) {
            Message msg = obtainMessage(what, obj);
            msg.setData(data);
            msg.sendToTarget();
        }
    }
}
