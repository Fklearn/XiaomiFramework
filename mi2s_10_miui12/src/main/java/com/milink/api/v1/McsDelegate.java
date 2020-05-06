package com.milink.api.v1;

import android.os.Handler;
import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDelegate;
import com.milink.api.v1.type.ErrorCode;

public class McsDelegate extends IMcsDelegate.Stub {
    /* access modifiers changed from: private */
    public MilinkClientManagerDelegate mDelegate = null;
    private Handler mHandler = new Handler();

    public void setDelegate(MilinkClientManagerDelegate delegate) {
        this.mDelegate = delegate;
    }

    public void onConnected() throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onConnected();
                    }
                }
            });
        }
    }

    public void onConnectedFailed() throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onConnectedFailed(ErrorCode.ConnectTimeout);
                    }
                }
            });
        }
    }

    public void onDisconnected() throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onDisconnected();
                    }
                }
            });
        }
    }

    public void onLoading() throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onLoading();
                    }
                }
            });
        }
    }

    public void onPlaying() throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onPlaying();
                    }
                }
            });
        }
    }

    public void onStopped() throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onStopped();
                    }
                }
            });
        }
    }

    public void onPaused() throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onPaused();
                    }
                }
            });
        }
    }

    public void onVolume(final int volume) throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onVolume(volume);
                    }
                }
            });
        }
    }

    public void onNextAudio(final boolean isAuto) throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onNextAudio(isAuto);
                    }
                }
            });
        }
    }

    public void onPrevAudio(final boolean isAuto) throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDelegate.this.mDelegate != null) {
                        McsDelegate.this.mDelegate.onPrevAudio(isAuto);
                    }
                }
            });
        }
    }
}
