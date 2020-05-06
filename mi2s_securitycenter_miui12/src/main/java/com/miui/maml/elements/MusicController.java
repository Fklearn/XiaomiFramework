package com.miui.maml.elements;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import java.util.List;

public class MusicController {
    private static final String TAG = "MAML_MusicController";
    private Context mContext;
    private Handler mHandler;
    private MediaController.Callback mMediaCallback = new MediaController.Callback() {
        public void onAudioInfoChanged(MediaController.PlaybackInfo playbackInfo) {
            super.onAudioInfoChanged(playbackInfo);
        }

        public void onExtrasChanged(Bundle bundle) {
            super.onExtrasChanged(bundle);
        }

        public void onMetadataChanged(MediaMetadata mediaMetadata) {
            super.onMetadataChanged(mediaMetadata);
            Log.d(MusicController.TAG, "onMetadataChanged");
            if (MusicController.this.mUpdateListener != null) {
                MusicController.this.mUpdateListener.onClientMetadataUpdate(mediaMetadata);
            }
        }

        public void onPlaybackStateChanged(PlaybackState playbackState) {
            super.onPlaybackStateChanged(playbackState);
            Log.d(MusicController.TAG, "onPlaybackStateChanged");
            if (MusicController.this.mUpdateListener == null) {
                return;
            }
            if (playbackState != null) {
                MusicController.this.mUpdateListener.onClientPlaybackStateUpdate(playbackState.getState());
                MusicController.this.mUpdateListener.onClientPlaybackActionUpdate(playbackState.getActions());
                return;
            }
            MusicController.this.mUpdateListener.onClientPlaybackStateUpdate(0);
        }

        public void onQueueChanged(List<MediaSession.QueueItem> list) {
            super.onQueueChanged(list);
        }

        public void onQueueTitleChanged(CharSequence charSequence) {
            super.onQueueTitleChanged(charSequence);
        }

        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.d(MusicController.TAG, "onSessionDestroyed");
            if (MusicController.this.mUpdateListener != null) {
                MusicController.this.mUpdateListener.onSessionDestroyed();
            }
        }

        public void onSessionEvent(String str, Bundle bundle) {
            super.onSessionEvent(str, bundle);
            Log.d(MusicController.TAG, "onSessionEvent");
        }
    };
    private MediaController mMediaController;
    private MediaSessionManager mSessionManager;
    private MediaSessionManager.OnActiveSessionsChangedListener mSessionsChangedListener = new MediaSessionManager.OnActiveSessionsChangedListener() {
        public void onActiveSessionsChanged(List<MediaController> list) {
            MusicController.this.resetMediaController(list);
            Log.d(MusicController.TAG, "onActiveSessionsChanged");
        }
    };
    /* access modifiers changed from: private */
    public OnClientUpdateListener mUpdateListener;

    public interface OnClientUpdateListener {
        void onClientChange();

        void onClientMetadataUpdate(MediaMetadata mediaMetadata);

        void onClientPlaybackActionUpdate(long j);

        void onClientPlaybackStateUpdate(int i);

        void onSessionDestroyed();
    }

    public MusicController(Context context, Handler handler) {
        this.mContext = context.getApplicationContext();
        this.mHandler = handler;
        this.mSessionManager = (MediaSessionManager) this.mContext.getSystemService("media_session");
        init();
    }

    private void clearMediaController() {
        Log.d(TAG, "clearMediaController");
        if (this.mMediaController != null) {
            OnClientUpdateListener onClientUpdateListener = this.mUpdateListener;
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientChange();
            }
            try {
                this.mMediaController.unregisterCallback(this.mMediaCallback);
            } catch (Exception unused) {
                Log.e(TAG, "unregister MediaController.Callback failed");
            }
            this.mMediaController = null;
        }
    }

    private void initMediaController() {
        Log.d(TAG, "initMediaController");
        MediaController mediaController = this.mMediaController;
        if (mediaController != null) {
            try {
                mediaController.registerCallback(this.mMediaCallback, this.mHandler);
            } catch (Exception unused) {
                Log.e(TAG, "register MediaController.Callback failed");
            }
        }
    }

    /* access modifiers changed from: private */
    public void resetMediaController(List<MediaController> list) {
        Log.d(TAG, "resetMediaController");
        clearMediaController();
        if (list != null) {
            if (list.size() > 0) {
                this.mMediaController = list.get(0);
            }
            initMediaController();
            updateInfoToListener();
        }
    }

    private void updateInfoToListener() {
        OnClientUpdateListener onClientUpdateListener;
        Log.d(TAG, "updateInfoToListener");
        if (this.mMediaController != null && (onClientUpdateListener = this.mUpdateListener) != null) {
            onClientUpdateListener.onClientChange();
            PlaybackState playbackState = this.mMediaController.getPlaybackState();
            if (playbackState != null) {
                this.mUpdateListener.onClientPlaybackStateUpdate(playbackState.getState());
            }
            this.mUpdateListener.onClientMetadataUpdate(this.mMediaController.getMetadata());
        }
    }

    public void finish() {
        Log.d(TAG, "finish");
        this.mSessionManager.removeOnActiveSessionsChangedListener(this.mSessionsChangedListener);
        clearMediaController();
    }

    public String getClientPackageName() {
        MediaController mediaController = this.mMediaController;
        if (mediaController != null) {
            return mediaController.getPackageName();
        }
        return null;
    }

    public long getEstimatedMediaPosition() {
        PlaybackState playbackState;
        MediaController mediaController = this.mMediaController;
        if (mediaController == null || (playbackState = mediaController.getPlaybackState()) == null) {
            return 0;
        }
        return playbackState.getPosition();
    }

    public void init() {
        Log.d(TAG, "init");
        resetMediaController(this.mSessionManager.getActiveSessions((ComponentName) null));
        this.mSessionManager.addOnActiveSessionsChangedListener(this.mSessionsChangedListener, (ComponentName) null, this.mHandler);
    }

    public boolean isMusicActive() {
        PlaybackState playbackState;
        MediaController mediaController = this.mMediaController;
        if (mediaController == null || (playbackState = mediaController.getPlaybackState()) == null) {
            return false;
        }
        int state = playbackState.getState();
        return state == 3 || state == 6;
    }

    public void rating(Rating rating) {
        try {
            if (this.mMediaController != null) {
                this.mMediaController.getTransportControls().setRating(rating);
            }
        } catch (Exception e) {
            Log.w(TAG, "RATING_KEY_BY_USER: failed: " + e);
        }
    }

    public void registerListener(OnClientUpdateListener onClientUpdateListener) {
        this.mUpdateListener = onClientUpdateListener;
        updateInfoToListener();
    }

    public void reset() {
        resetMediaController(this.mSessionManager.getActiveSessions((ComponentName) null));
    }

    public boolean seekTo(long j) {
        try {
            if (this.mMediaController == null) {
                return false;
            }
            this.mMediaController.getTransportControls().seekTo(j);
            return true;
        } catch (Exception e) {
            Log.w(TAG, " seekTo failed: " + e);
            return false;
        }
    }

    public boolean sendMediaKeyEvent(int i, int i2) {
        try {
            if (this.mMediaController == null) {
                return false;
            }
            KeyEvent keyEvent = new KeyEvent(i, i2);
            keyEvent.setSource(4098);
            return this.mMediaController.dispatchMediaButtonEvent(keyEvent);
        } catch (Exception e) {
            Log.w(TAG, "Send media key event failed: " + e);
            return false;
        }
    }

    public void unregisterListener() {
        this.mUpdateListener = null;
    }
}
