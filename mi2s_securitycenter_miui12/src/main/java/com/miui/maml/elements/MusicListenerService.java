package com.miui.maml.elements;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteController;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Deprecated
public class MusicListenerService extends NotificationListenerService implements RemoteController.OnClientUpdateListener {
    public static final String ACTION = "android.service.notification.MusicListenerService";
    private static final int BITMAP_HEIGHT = 1024;
    private static final int BITMAP_WIDTH = 1024;
    private static final String LOG_TAG = "MusicListenerService";
    private IBinder mBinder = new RCBinder();
    private List<WeakReference<RemoteController.OnClientUpdateListener>> mClientUpdateListeners = new CopyOnWriteArrayList();
    private Context mContext;
    private RemoteController mRemoteController;
    private boolean mRemoteControllerEnabled;

    public class RCBinder extends Binder {
        public RCBinder() {
        }

        public MusicListenerService getService() {
            return MusicListenerService.this;
        }
    }

    private void disableRemoteController() {
        if (this.mRemoteControllerEnabled) {
            ((AudioManager) this.mContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).unregisterRemoteController(this.mRemoteController);
            this.mRemoteControllerEnabled = false;
        }
    }

    private void enableRemoteController() {
        if (!this.mRemoteControllerEnabled) {
            AudioManager audioManager = (AudioManager) this.mContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            this.mRemoteController = new RemoteController(this.mContext, this);
            try {
                this.mRemoteControllerEnabled = audioManager.registerRemoteController(this.mRemoteController);
            } catch (Exception e) {
                Log.w(LOG_TAG, "fail to register RemoteController!", e);
            }
            if (this.mRemoteControllerEnabled) {
                this.mRemoteController.setArtworkConfiguration(1024, 1024);
                this.mRemoteController.setSynchronizationMode(1);
                return;
            }
            Log.w(LOG_TAG, "fail to register RemoteController!");
        }
    }

    public RemoteController getRemoteController() {
        return this.mRemoteController;
    }

    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Log.d(LOG_TAG, "onBind: success");
            return this.mBinder;
        }
        Log.d(LOG_TAG, "onBind: fail");
        return null;
    }

    public void onClientChange(boolean z) {
        for (WeakReference next : this.mClientUpdateListeners) {
            RemoteController.OnClientUpdateListener onClientUpdateListener = (RemoteController.OnClientUpdateListener) next.get();
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientChange(z);
            } else {
                this.mClientUpdateListeners.remove(next);
            }
        }
        if (this.mClientUpdateListeners.isEmpty()) {
            disableRemoteController();
        }
    }

    public void onClientFolderInfoBrowsedPlayer(String str) {
    }

    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
        for (WeakReference next : this.mClientUpdateListeners) {
            RemoteController.OnClientUpdateListener onClientUpdateListener = (RemoteController.OnClientUpdateListener) next.get();
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientMetadataUpdate(metadataEditor);
            } else {
                this.mClientUpdateListeners.remove(next);
            }
        }
        if (this.mClientUpdateListeners.isEmpty()) {
            disableRemoteController();
        }
    }

    public void onClientNowPlayingContentChange() {
    }

    public void onClientPlayItemResponse(boolean z) {
    }

    public void onClientPlaybackStateUpdate(int i) {
        for (WeakReference next : this.mClientUpdateListeners) {
            RemoteController.OnClientUpdateListener onClientUpdateListener = (RemoteController.OnClientUpdateListener) next.get();
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientPlaybackStateUpdate(i);
            } else {
                this.mClientUpdateListeners.remove(next);
            }
        }
        if (this.mClientUpdateListeners.isEmpty()) {
            disableRemoteController();
        }
    }

    public void onClientPlaybackStateUpdate(int i, long j, long j2, float f) {
        for (WeakReference next : this.mClientUpdateListeners) {
            RemoteController.OnClientUpdateListener onClientUpdateListener = (RemoteController.OnClientUpdateListener) next.get();
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientPlaybackStateUpdate(i, j, j2, f);
            } else {
                this.mClientUpdateListeners.remove(next);
            }
        }
        if (this.mClientUpdateListeners.isEmpty()) {
            disableRemoteController();
        }
    }

    public void onClientTransportControlUpdate(int i) {
        for (WeakReference next : this.mClientUpdateListeners) {
            RemoteController.OnClientUpdateListener onClientUpdateListener = (RemoteController.OnClientUpdateListener) next.get();
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientTransportControlUpdate(i);
            } else {
                this.mClientUpdateListeners.remove(next);
            }
        }
        if (this.mClientUpdateListeners.isEmpty()) {
            disableRemoteController();
        }
    }

    public void onClientUpdateNowPlayingEntries(long[] jArr) {
    }

    public void onCreate() {
        this.mContext = getApplicationContext();
        this.mRemoteController = new RemoteController(this.mContext, this);
    }

    public void onDestroy() {
        disableRemoteController();
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        super.onNotificationPosted(statusBarNotification);
        Log.d(LOG_TAG, "onNotificationPosted: pkg = " + statusBarNotification.getPackageName());
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        super.onNotificationRemoved(statusBarNotification);
    }

    public void registerClientUpdateListener(RemoteController.OnClientUpdateListener onClientUpdateListener) {
        enableRemoteController();
        for (WeakReference<RemoteController.OnClientUpdateListener> weakReference : this.mClientUpdateListeners) {
            RemoteController.OnClientUpdateListener onClientUpdateListener2 = (RemoteController.OnClientUpdateListener) weakReference.get();
            if (onClientUpdateListener2 != null && onClientUpdateListener2.equals(onClientUpdateListener)) {
                return;
            }
        }
        this.mClientUpdateListeners.add(new WeakReference(onClientUpdateListener));
    }

    public void unregisterClientUpdateListener(RemoteController.OnClientUpdateListener onClientUpdateListener) {
        for (WeakReference next : this.mClientUpdateListeners) {
            RemoteController.OnClientUpdateListener onClientUpdateListener2 = (RemoteController.OnClientUpdateListener) next.get();
            if (onClientUpdateListener2 == null || onClientUpdateListener2.equals(onClientUpdateListener)) {
                this.mClientUpdateListeners.remove(next);
            }
        }
        if (this.mClientUpdateListeners.isEmpty()) {
            disableRemoteController();
        }
    }
}
