package com.android.server.accessibility;

import android.content.res.Resources;
import android.hardware.fingerprint.IFingerprintClientActiveCallback;
import android.hardware.fingerprint.IFingerprintService;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;
import java.util.ArrayList;
import java.util.List;

public class FingerprintGestureDispatcher extends IFingerprintClientActiveCallback.Stub implements Handler.Callback {
    private static final String LOG_TAG = "FingerprintGestureDispatcher";
    private static final int MSG_REGISTER = 1;
    private static final int MSG_UNREGISTER = 2;
    private final List<FingerprintGestureClient> mCapturingClients = new ArrayList(0);
    private final IFingerprintService mFingerprintService;
    private final Handler mHandler;
    private final boolean mHardwareSupportsGestures;
    private final Object mLock;
    private boolean mRegisteredReadOnlyExceptInHandler;

    public interface FingerprintGestureClient {
        boolean isCapturingFingerprintGestures();

        void onFingerprintGesture(int i);

        void onFingerprintGestureDetectionActiveChanged(boolean z);
    }

    public FingerprintGestureDispatcher(IFingerprintService fingerprintService, Resources resources, Object lock) {
        this.mFingerprintService = fingerprintService;
        this.mHardwareSupportsGestures = resources.getBoolean(17891458);
        this.mLock = lock;
        this.mHandler = new Handler(this);
    }

    public FingerprintGestureDispatcher(IFingerprintService fingerprintService, Resources resources, Object lock, Handler handler) {
        this.mFingerprintService = fingerprintService;
        this.mHardwareSupportsGestures = resources.getBoolean(17891458);
        this.mLock = lock;
        this.mHandler = handler;
    }

    public void updateClientList(List<? extends FingerprintGestureClient> clientList) {
        if (this.mHardwareSupportsGestures) {
            synchronized (this.mLock) {
                this.mCapturingClients.clear();
                for (int i = 0; i < clientList.size(); i++) {
                    FingerprintGestureClient client = (FingerprintGestureClient) clientList.get(i);
                    if (client.isCapturingFingerprintGestures()) {
                        this.mCapturingClients.add(client);
                    }
                }
                if (this.mCapturingClients.isEmpty()) {
                    if (this.mRegisteredReadOnlyExceptInHandler) {
                        this.mHandler.obtainMessage(2).sendToTarget();
                    }
                } else if (!this.mRegisteredReadOnlyExceptInHandler) {
                    this.mHandler.obtainMessage(1).sendToTarget();
                }
            }
        }
    }

    public void onClientActiveChanged(boolean nonGestureFingerprintClientActive) {
        if (this.mHardwareSupportsGestures) {
            synchronized (this.mLock) {
                for (int i = 0; i < this.mCapturingClients.size(); i++) {
                    this.mCapturingClients.get(i).onFingerprintGestureDetectionActiveChanged(!nonGestureFingerprintClientActive);
                }
            }
        }
    }

    public boolean isFingerprintGestureDetectionAvailable() {
        if (!this.mHardwareSupportsGestures) {
            return false;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            return !this.mFingerprintService.isClientActive();
        } catch (RemoteException e) {
            return false;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0024, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0029, code lost:
        if (r0 >= r2.size()) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002b, code lost:
        r2.get(r0).onFingerprintGesture(r1);
        r0 = r0 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0037, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFingerprintGesture(int r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            java.util.List<com.android.server.accessibility.FingerprintGestureDispatcher$FingerprintGestureClient> r1 = r4.mCapturingClients     // Catch:{ all -> 0x003a }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x003a }
            r2 = 0
            if (r1 == 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            return r2
        L_0x000e:
            switch(r5) {
                case 280: goto L_0x001a;
                case 281: goto L_0x0017;
                case 282: goto L_0x0015;
                case 283: goto L_0x0013;
                default: goto L_0x0011;
            }     // Catch:{ all -> 0x003a }
        L_0x0011:
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            goto L_0x0039
        L_0x0013:
            r1 = 1
            goto L_0x001c
        L_0x0015:
            r1 = 2
            goto L_0x001c
        L_0x0017:
            r1 = 8
            goto L_0x001c
        L_0x001a:
            r1 = 4
        L_0x001c:
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x003a }
            java.util.List<com.android.server.accessibility.FingerprintGestureDispatcher$FingerprintGestureClient> r3 = r4.mCapturingClients     // Catch:{ all -> 0x003a }
            r2.<init>(r3)     // Catch:{ all -> 0x003a }
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            r0 = 0
        L_0x0025:
            int r3 = r2.size()
            if (r0 >= r3) goto L_0x0037
            java.lang.Object r3 = r2.get(r0)
            com.android.server.accessibility.FingerprintGestureDispatcher$FingerprintGestureClient r3 = (com.android.server.accessibility.FingerprintGestureDispatcher.FingerprintGestureClient) r3
            r3.onFingerprintGesture(r1)
            int r0 = r0 + 1
            goto L_0x0025
        L_0x0037:
            r0 = 1
            return r0
        L_0x0039:
            return r2
        L_0x003a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.FingerprintGestureDispatcher.onFingerprintGesture(int):boolean");
    }

    public boolean handleMessage(Message message) {
        if (message.what == 1) {
            long identity = Binder.clearCallingIdentity();
            try {
                this.mFingerprintService.addClientActiveCallback(this);
                this.mRegisteredReadOnlyExceptInHandler = true;
            } catch (RemoteException e) {
                Slog.e(LOG_TAG, "Failed to register for fingerprint activity callbacks");
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
            Binder.restoreCallingIdentity(identity);
            return false;
        } else if (message.what == 2) {
            long identity2 = Binder.clearCallingIdentity();
            try {
                this.mFingerprintService.removeClientActiveCallback(this);
            } catch (RemoteException e2) {
                Slog.e(LOG_TAG, "Failed to unregister for fingerprint activity callbacks");
            } catch (Throwable th2) {
                Binder.restoreCallingIdentity(identity2);
                throw th2;
            }
            Binder.restoreCallingIdentity(identity2);
            this.mRegisteredReadOnlyExceptInHandler = false;
            return true;
        } else {
            Slog.e(LOG_TAG, "Unknown message: " + message.what);
            return false;
        }
    }
}
