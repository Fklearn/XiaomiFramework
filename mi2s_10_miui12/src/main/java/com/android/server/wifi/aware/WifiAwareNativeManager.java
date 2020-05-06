package com.android.server.wifi.aware;

import android.hardware.wifi.V1_0.IWifiNanIface;
import android.hardware.wifi.V1_0.WifiStatus;
import android.os.Handler;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.wifi.HalDeviceManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import javax.annotation.concurrent.GuardedBy;

public class WifiAwareNativeManager {
    private static final String TAG = "WifiAwareNativeManager";
    private static final boolean VDBG = false;
    boolean mDbg = false;
    /* access modifiers changed from: private */
    public final HalDeviceManager mHalDeviceManager;
    /* access modifiers changed from: private */
    public volatile Handler mHandler;
    /* access modifiers changed from: private */
    public InterfaceAvailableForRequestListener mInterfaceAvailableForRequestListener = new InterfaceAvailableForRequestListener();
    @GuardedBy("mLock")
    private InterfaceDestroyedListener mInterfaceDestroyedListener;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    @GuardedBy("mLock")
    private int mReferenceCount = 0;
    private final WifiAwareNativeCallback mWifiAwareNativeCallback;
    /* access modifiers changed from: private */
    public final WifiAwareStateManager mWifiAwareStateManager;
    /* access modifiers changed from: private */
    public volatile IWifiNanIface mWifiNanIface = null;

    WifiAwareNativeManager(WifiAwareStateManager awareStateManager, HalDeviceManager halDeviceManager, WifiAwareNativeCallback wifiAwareNativeCallback) {
        this.mWifiAwareStateManager = awareStateManager;
        this.mHalDeviceManager = halDeviceManager;
        this.mWifiAwareNativeCallback = wifiAwareNativeCallback;
    }

    public android.hardware.wifi.V1_2.IWifiNanIface mockableCastTo_1_2(IWifiNanIface iface) {
        return android.hardware.wifi.V1_2.IWifiNanIface.castFrom(iface);
    }

    public void start(Handler handler) {
        this.mHandler = handler;
        this.mHalDeviceManager.initialize();
        this.mHalDeviceManager.registerStatusListener(new HalDeviceManager.ManagerStatusListener() {
            public void onStatusChanged() {
                if (WifiAwareNativeManager.this.mHalDeviceManager.isStarted()) {
                    WifiAwareNativeManager.this.mHalDeviceManager.registerInterfaceAvailableForRequestListener(3, WifiAwareNativeManager.this.mInterfaceAvailableForRequestListener, WifiAwareNativeManager.this.mHandler);
                } else {
                    WifiAwareNativeManager.this.awareIsDown();
                }
            }
        }, this.mHandler);
        if (this.mHalDeviceManager.isStarted()) {
            this.mHalDeviceManager.registerInterfaceAvailableForRequestListener(3, this.mInterfaceAvailableForRequestListener, this.mHandler);
        }
    }

    @VisibleForTesting
    public IWifiNanIface getWifiNanIface() {
        IWifiNanIface iWifiNanIface;
        synchronized (this.mLock) {
            iWifiNanIface = this.mWifiNanIface;
        }
        return iWifiNanIface;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00b3, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void tryToGetAware() {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            boolean r1 = r7.mDbg     // Catch:{ all -> 0x00dc }
            if (r1 == 0) goto L_0x0029
            java.lang.String r1 = "WifiAwareNativeManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00dc }
            r2.<init>()     // Catch:{ all -> 0x00dc }
            java.lang.String r3 = "tryToGetAware: mWifiNanIface="
            r2.append(r3)     // Catch:{ all -> 0x00dc }
            android.hardware.wifi.V1_0.IWifiNanIface r3 = r7.mWifiNanIface     // Catch:{ all -> 0x00dc }
            r2.append(r3)     // Catch:{ all -> 0x00dc }
            java.lang.String r3 = ", mReferenceCount="
            r2.append(r3)     // Catch:{ all -> 0x00dc }
            int r3 = r7.mReferenceCount     // Catch:{ all -> 0x00dc }
            r2.append(r3)     // Catch:{ all -> 0x00dc }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00dc }
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x00dc }
        L_0x0029:
            android.hardware.wifi.V1_0.IWifiNanIface r1 = r7.mWifiNanIface     // Catch:{ all -> 0x00dc }
            r2 = 1
            if (r1 == 0) goto L_0x0035
            int r1 = r7.mReferenceCount     // Catch:{ all -> 0x00dc }
            int r1 = r1 + r2
            r7.mReferenceCount = r1     // Catch:{ all -> 0x00dc }
            monitor-exit(r0)     // Catch:{ all -> 0x00dc }
            return
        L_0x0035:
            com.android.server.wifi.HalDeviceManager r1 = r7.mHalDeviceManager     // Catch:{ all -> 0x00dc }
            if (r1 == 0) goto L_0x00d0
            android.os.Handler r1 = r7.mHandler     // Catch:{ all -> 0x00dc }
            if (r1 != 0) goto L_0x003f
            goto L_0x00d0
        L_0x003f:
            com.android.server.wifi.aware.WifiAwareNativeManager$InterfaceDestroyedListener r1 = new com.android.server.wifi.aware.WifiAwareNativeManager$InterfaceDestroyedListener     // Catch:{ all -> 0x00dc }
            r3 = 0
            r1.<init>()     // Catch:{ all -> 0x00dc }
            r7.mInterfaceDestroyedListener = r1     // Catch:{ all -> 0x00dc }
            com.android.server.wifi.HalDeviceManager r1 = r7.mHalDeviceManager     // Catch:{ all -> 0x00dc }
            com.android.server.wifi.aware.WifiAwareNativeManager$InterfaceDestroyedListener r3 = r7.mInterfaceDestroyedListener     // Catch:{ all -> 0x00dc }
            android.os.Handler r4 = r7.mHandler     // Catch:{ all -> 0x00dc }
            android.hardware.wifi.V1_0.IWifiNanIface r1 = r1.createNanIface(r3, r4)     // Catch:{ all -> 0x00dc }
            if (r1 != 0) goto L_0x005e
            java.lang.String r2 = "WifiAwareNativeManager"
            java.lang.String r3 = "Was not able to obtain an IWifiNanIface (even though enabled!?)"
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x00dc }
            r7.awareIsDown()     // Catch:{ all -> 0x00dc }
            goto L_0x00b2
        L_0x005e:
            boolean r3 = r7.mDbg     // Catch:{ all -> 0x00dc }
            if (r3 == 0) goto L_0x0069
            java.lang.String r3 = "WifiAwareNativeManager"
            java.lang.String r4 = "Obtained an IWifiNanIface"
            android.util.Log.v(r3, r4)     // Catch:{ all -> 0x00dc }
        L_0x0069:
            android.hardware.wifi.V1_2.IWifiNanIface r3 = r7.mockableCastTo_1_2(r1)     // Catch:{ RemoteException -> 0x00b4 }
            if (r3 != 0) goto L_0x007b
            com.android.server.wifi.aware.WifiAwareNativeCallback r4 = r7.mWifiAwareNativeCallback     // Catch:{ RemoteException -> 0x00b4 }
            r5 = 0
            r4.mIsHal12OrLater = r5     // Catch:{ RemoteException -> 0x00b4 }
            com.android.server.wifi.aware.WifiAwareNativeCallback r4 = r7.mWifiAwareNativeCallback     // Catch:{ RemoteException -> 0x00b4 }
            android.hardware.wifi.V1_0.WifiStatus r4 = r1.registerEventCallback(r4)     // Catch:{ RemoteException -> 0x00b4 }
            goto L_0x0085
        L_0x007b:
            com.android.server.wifi.aware.WifiAwareNativeCallback r4 = r7.mWifiAwareNativeCallback     // Catch:{ RemoteException -> 0x00b4 }
            r4.mIsHal12OrLater = r2     // Catch:{ RemoteException -> 0x00b4 }
            com.android.server.wifi.aware.WifiAwareNativeCallback r4 = r7.mWifiAwareNativeCallback     // Catch:{ RemoteException -> 0x00b4 }
            android.hardware.wifi.V1_0.WifiStatus r4 = r3.registerEventCallback_1_2(r4)     // Catch:{ RemoteException -> 0x00b4 }
        L_0x0085:
            int r5 = r4.code     // Catch:{ RemoteException -> 0x00b4 }
            if (r5 == 0) goto L_0x00ad
            java.lang.String r2 = "WifiAwareNativeManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x00b4 }
            r5.<init>()     // Catch:{ RemoteException -> 0x00b4 }
            java.lang.String r6 = "IWifiNanIface.registerEventCallback error: "
            r5.append(r6)     // Catch:{ RemoteException -> 0x00b4 }
            java.lang.String r6 = statusString(r4)     // Catch:{ RemoteException -> 0x00b4 }
            r5.append(r6)     // Catch:{ RemoteException -> 0x00b4 }
            java.lang.String r5 = r5.toString()     // Catch:{ RemoteException -> 0x00b4 }
            android.util.Log.e(r2, r5)     // Catch:{ RemoteException -> 0x00b4 }
            com.android.server.wifi.HalDeviceManager r2 = r7.mHalDeviceManager     // Catch:{ RemoteException -> 0x00b4 }
            r2.removeIface(r1)     // Catch:{ RemoteException -> 0x00b4 }
            r7.awareIsDown()     // Catch:{ RemoteException -> 0x00b4 }
            monitor-exit(r0)     // Catch:{ all -> 0x00dc }
            return
        L_0x00ad:
            r7.mWifiNanIface = r1     // Catch:{ all -> 0x00dc }
            r7.mReferenceCount = r2     // Catch:{ all -> 0x00dc }
        L_0x00b2:
            monitor-exit(r0)     // Catch:{ all -> 0x00dc }
            return
        L_0x00b4:
            r2 = move-exception
            java.lang.String r3 = "WifiAwareNativeManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00dc }
            r4.<init>()     // Catch:{ all -> 0x00dc }
            java.lang.String r5 = "IWifiNanIface.registerEventCallback exception: "
            r4.append(r5)     // Catch:{ all -> 0x00dc }
            r4.append(r2)     // Catch:{ all -> 0x00dc }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00dc }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x00dc }
            r7.awareIsDown()     // Catch:{ all -> 0x00dc }
            monitor-exit(r0)     // Catch:{ all -> 0x00dc }
            return
        L_0x00d0:
            java.lang.String r1 = "WifiAwareNativeManager"
            java.lang.String r2 = "tryToGetAware: mHalDeviceManager is null!? or mHandler is null!?"
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x00dc }
            r7.awareIsDown()     // Catch:{ all -> 0x00dc }
            monitor-exit(r0)     // Catch:{ all -> 0x00dc }
            return
        L_0x00dc:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00dc }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.aware.WifiAwareNativeManager.tryToGetAware():void");
    }

    public void releaseAware() {
        if (this.mDbg) {
            Log.d(TAG, "releaseAware: mWifiNanIface=" + this.mWifiNanIface + ", mReferenceCount=" + this.mReferenceCount);
        }
        if (this.mWifiNanIface != null) {
            if (this.mHalDeviceManager == null) {
                Log.e(TAG, "releaseAware: mHalDeviceManager is null!?");
                return;
            }
            synchronized (this.mLock) {
                this.mReferenceCount--;
                if (this.mReferenceCount == 0) {
                    this.mInterfaceDestroyedListener.active = false;
                    this.mInterfaceDestroyedListener = null;
                    this.mHalDeviceManager.removeIface(this.mWifiNanIface);
                    this.mWifiNanIface = null;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void awareIsDown() {
        synchronized (this.mLock) {
            if (this.mDbg) {
                Log.d(TAG, "awareIsDown: mWifiNanIface=" + this.mWifiNanIface + ", mReferenceCount =" + this.mReferenceCount);
            }
            this.mWifiNanIface = null;
            this.mReferenceCount = 0;
            this.mWifiAwareStateManager.disableUsage();
        }
    }

    private class InterfaceDestroyedListener implements HalDeviceManager.InterfaceDestroyedListener {
        public boolean active;

        private InterfaceDestroyedListener() {
            this.active = true;
        }

        public void onDestroyed(String ifaceName) {
            if (WifiAwareNativeManager.this.mDbg) {
                Log.d(WifiAwareNativeManager.TAG, "Interface was destroyed: mWifiNanIface=" + WifiAwareNativeManager.this.mWifiNanIface + ", active=" + this.active);
            }
            if (this.active && WifiAwareNativeManager.this.mWifiNanIface != null) {
                WifiAwareNativeManager.this.awareIsDown();
            }
        }
    }

    private class InterfaceAvailableForRequestListener implements HalDeviceManager.InterfaceAvailableForRequestListener {
        private InterfaceAvailableForRequestListener() {
        }

        public void onAvailabilityChanged(boolean isAvailable) {
            if (WifiAwareNativeManager.this.mDbg) {
                Log.d(WifiAwareNativeManager.TAG, "Interface availability = " + isAvailable + ", mWifiNanIface=" + WifiAwareNativeManager.this.mWifiNanIface);
            }
            synchronized (WifiAwareNativeManager.this.mLock) {
                if (isAvailable) {
                    WifiAwareNativeManager.this.mWifiAwareStateManager.enableUsage();
                } else if (WifiAwareNativeManager.this.mWifiNanIface == null) {
                    WifiAwareNativeManager.this.mWifiAwareStateManager.disableUsage();
                }
            }
        }
    }

    private static String statusString(WifiStatus status) {
        if (status == null) {
            return "status=null";
        }
        return status.code + " (" + status.description + ")";
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("WifiAwareNativeManager:");
        pw.println("  mWifiNanIface: " + this.mWifiNanIface);
        pw.println("  mReferenceCount: " + this.mReferenceCount);
        this.mWifiAwareNativeCallback.dump(fd, pw, args);
        this.mHalDeviceManager.dump(fd, pw, args);
    }
}
