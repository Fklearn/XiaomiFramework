package com.android.server.wifi.aware;

import android.app.AppOpsManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.wifi.aware.Characteristics;
import android.net.wifi.aware.DiscoverySession;
import android.net.wifi.aware.IWifiAwareDiscoverySessionCallback;
import android.net.wifi.aware.IWifiAwareMacAddressProvider;
import android.net.wifi.aware.IWifiAwareManager;
import android.net.wifi.aware.PublishConfig;
import android.net.wifi.aware.SubscribeConfig;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.server.wifi.Clock;
import com.android.server.wifi.FrameworkFacade;
import com.android.server.wifi.WifiInjector;
import com.android.server.wifi.util.WifiPermissionsUtil;
import com.android.server.wifi.util.WifiPermissionsWrapper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class WifiAwareServiceImpl extends IWifiAwareManager.Stub {
    private static final String TAG = "WifiAwareService";
    private static final boolean VDBG = false;
    private AppOpsManager mAppOps;
    /* access modifiers changed from: private */
    public Context mContext;
    boolean mDbg = false;
    /* access modifiers changed from: private */
    public final SparseArray<IBinder.DeathRecipient> mDeathRecipientsByClientId = new SparseArray<>();
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private int mNextClientId = 1;
    private WifiAwareShellCommand mShellCommand;
    /* access modifiers changed from: private */
    public WifiAwareStateManager mStateManager;
    /* access modifiers changed from: private */
    public final SparseIntArray mUidByClientId = new SparseIntArray();
    private WifiPermissionsUtil mWifiPermissionsUtil;

    public WifiAwareServiceImpl(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
    }

    public int getMockableCallingUid() {
        return getCallingUid();
    }

    public void start(HandlerThread handlerThread, WifiAwareStateManager awareStateManager, WifiAwareShellCommand awareShellCommand, WifiAwareMetrics awareMetrics, WifiPermissionsUtil wifiPermissionsUtil, WifiPermissionsWrapper permissionsWrapper, FrameworkFacade frameworkFacade, WifiAwareNativeManager wifiAwareNativeManager, WifiAwareNativeApi wifiAwareNativeApi, WifiAwareNativeCallback wifiAwareNativeCallback) {
        FrameworkFacade frameworkFacade2 = frameworkFacade;
        Log.i(TAG, "Starting Wi-Fi Aware service");
        this.mWifiPermissionsUtil = wifiPermissionsUtil;
        this.mStateManager = awareStateManager;
        this.mShellCommand = awareShellCommand;
        this.mStateManager.start(this.mContext, handlerThread.getLooper(), awareMetrics, wifiPermissionsUtil, permissionsWrapper, new Clock());
        final FrameworkFacade frameworkFacade3 = frameworkFacade;
        final WifiAwareStateManager wifiAwareStateManager = awareStateManager;
        final WifiAwareNativeManager wifiAwareNativeManager2 = wifiAwareNativeManager;
        final WifiAwareNativeApi wifiAwareNativeApi2 = wifiAwareNativeApi;
        final WifiAwareNativeCallback wifiAwareNativeCallback2 = wifiAwareNativeCallback;
        frameworkFacade2.registerContentObserver(this.mContext, Settings.Global.getUriFor("wifi_verbose_logging_enabled"), true, new ContentObserver(new Handler(handlerThread.getLooper())) {
            public void onChange(boolean selfChange) {
                WifiAwareServiceImpl wifiAwareServiceImpl = WifiAwareServiceImpl.this;
                wifiAwareServiceImpl.enableVerboseLogging(frameworkFacade3.getIntegerSetting(wifiAwareServiceImpl.mContext, "wifi_verbose_logging_enabled", 0), wifiAwareStateManager, wifiAwareNativeManager2, wifiAwareNativeApi2, wifiAwareNativeCallback2);
            }
        });
        enableVerboseLogging(frameworkFacade2.getIntegerSetting(this.mContext, "wifi_verbose_logging_enabled", 0), awareStateManager, wifiAwareNativeManager, wifiAwareNativeApi, wifiAwareNativeCallback);
    }

    /* access modifiers changed from: private */
    public void enableVerboseLogging(int verbose, WifiAwareStateManager awareStateManager, WifiAwareNativeManager wifiAwareNativeManager, WifiAwareNativeApi wifiAwareNativeApi, WifiAwareNativeCallback wifiAwareNativeCallback) {
        boolean dbg;
        if (verbose > 0) {
            dbg = true;
        } else {
            dbg = false;
        }
        this.mDbg = dbg;
        awareStateManager.mDbg = dbg;
        if (awareStateManager.mDataPathMgr != null) {
            awareStateManager.mDataPathMgr.mDbg = dbg;
            WifiInjector.getInstance().getWifiMetrics().getWifiAwareMetrics().mDbg = dbg;
        }
        wifiAwareNativeCallback.mDbg = dbg;
        wifiAwareNativeManager.mDbg = dbg;
        wifiAwareNativeApi.mDbg = dbg;
    }

    public void startLate() {
        Log.i(TAG, "Late initialization of Wi-Fi Aware service");
        this.mStateManager.startLate();
    }

    public boolean isUsageEnabled() {
        enforceAccessPermission();
        return this.mStateManager.isUsageEnabled();
    }

    public Characteristics getCharacteristics() {
        enforceAccessPermission();
        if (this.mStateManager.getCapabilities() == null) {
            return null;
        }
        return this.mStateManager.getCapabilities().toPublicCharacteristics();
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b1, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(android.os.IBinder r20, java.lang.String r21, android.net.wifi.aware.IWifiAwareEventCallback r22, android.net.wifi.aware.ConfigRequest r23, boolean r24) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            r11 = r21
            r12 = r22
            r13 = r24
            r19.enforceAccessPermission()
            r19.enforceChangePermission()
            int r14 = r19.getMockableCallingUid()
            android.app.AppOpsManager r0 = r1.mAppOps
            r0.checkPackage(r14, r11)
            if (r12 == 0) goto L_0x00ea
            if (r2 == 0) goto L_0x00e2
            if (r13 == 0) goto L_0x0026
            int r0 = r19.getMockableCallingUid()
            r1.enforceLocationPermission(r11, r0)
        L_0x0026:
            if (r23 == 0) goto L_0x002e
            r19.enforceNetworkStackPermission()
            r15 = r23
            goto L_0x0038
        L_0x002e:
            android.net.wifi.aware.ConfigRequest$Builder r0 = new android.net.wifi.aware.ConfigRequest$Builder
            r0.<init>()
            android.net.wifi.aware.ConfigRequest r0 = r0.build()
            r15 = r0
        L_0x0038:
            r15.validate()
            int r16 = getCallingPid()
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            int r0 = r1.mNextClientId     // Catch:{ all -> 0x00df }
            int r4 = r0 + 1
            r1.mNextClientId = r4     // Catch:{ all -> 0x00df }
            r10 = r0
            monitor-exit(r3)     // Catch:{ all -> 0x00df }
            boolean r0 = r1.mDbg
            if (r0 == 0) goto L_0x007c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "connect: uid="
            r0.append(r3)
            r0.append(r14)
            java.lang.String r3 = ", clientId="
            r0.append(r3)
            r0.append(r10)
            java.lang.String r3 = ", configRequest"
            r0.append(r3)
            r0.append(r15)
            java.lang.String r3 = ", notifyOnIdentityChanged="
            r0.append(r3)
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "WifiAwareService"
            android.util.Log.v(r3, r0)
        L_0x007c:
            com.android.server.wifi.aware.WifiAwareServiceImpl$2 r0 = new com.android.server.wifi.aware.WifiAwareServiceImpl$2
            r0.<init>(r10, r2)
            r9 = r0
            r0 = 0
            r2.linkToDeath(r9, r0)     // Catch:{ RemoteException -> 0x00b3 }
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            android.util.SparseArray<android.os.IBinder$DeathRecipient> r0 = r1.mDeathRecipientsByClientId     // Catch:{ all -> 0x00aa }
            r0.put(r10, r9)     // Catch:{ all -> 0x00aa }
            android.util.SparseIntArray r0 = r1.mUidByClientId     // Catch:{ all -> 0x00aa }
            r0.put(r10, r14)     // Catch:{ all -> 0x00aa }
            monitor-exit(r3)     // Catch:{ all -> 0x00aa }
            com.android.server.wifi.aware.WifiAwareStateManager r3 = r1.mStateManager
            r4 = r10
            r5 = r14
            r6 = r16
            r7 = r21
            r8 = r22
            r17 = r9
            r9 = r15
            r18 = r10
            r10 = r24
            r3.connect(r4, r5, r6, r7, r8, r9, r10)
            return
        L_0x00aa:
            r0 = move-exception
            r17 = r9
            r18 = r10
        L_0x00af:
            monitor-exit(r3)     // Catch:{ all -> 0x00b1 }
            throw r0
        L_0x00b1:
            r0 = move-exception
            goto L_0x00af
        L_0x00b3:
            r0 = move-exception
            r17 = r9
            r18 = r10
            r3 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "Error on linkToDeath - "
            r0.append(r4)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "WifiAwareService"
            android.util.Log.e(r4, r0)
            r0 = 1
            r12.onConnectFail(r0)     // Catch:{ RemoteException -> 0x00d4 }
            goto L_0x00de
        L_0x00d4:
            r0 = move-exception
            r4 = r0
            r0 = r4
            java.lang.String r4 = "WifiAwareService"
            java.lang.String r5 = "Error on onConnectFail()"
            android.util.Log.e(r4, r5)
        L_0x00de:
            return
        L_0x00df:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00df }
            throw r0
        L_0x00e2:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "Binder must not be null"
            r0.<init>(r3)
            throw r0
        L_0x00ea:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "Callback must not be null"
            r0.<init>(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.aware.WifiAwareServiceImpl.connect(android.os.IBinder, java.lang.String, android.net.wifi.aware.IWifiAwareEventCallback, android.net.wifi.aware.ConfigRequest, boolean):void");
    }

    public void disconnect(int clientId, IBinder binder) {
        enforceAccessPermission();
        enforceChangePermission();
        int uid = getMockableCallingUid();
        enforceClientValidity(uid, clientId);
        if (this.mDbg) {
            Log.v(TAG, "disconnect: uid=" + uid + ", clientId=" + clientId);
        }
        if (binder != null) {
            synchronized (this.mLock) {
                IBinder.DeathRecipient dr = this.mDeathRecipientsByClientId.get(clientId);
                if (dr != null) {
                    binder.unlinkToDeath(dr, 0);
                    this.mDeathRecipientsByClientId.delete(clientId);
                }
                this.mUidByClientId.delete(clientId);
            }
            this.mStateManager.disconnect(clientId);
            return;
        }
        throw new IllegalArgumentException("Binder must not be null");
    }

    public void terminateSession(int clientId, int sessionId) {
        enforceAccessPermission();
        enforceChangePermission();
        enforceClientValidity(getMockableCallingUid(), clientId);
        this.mStateManager.terminateSession(clientId, sessionId);
    }

    public void publish(String callingPackage, int clientId, PublishConfig publishConfig, IWifiAwareDiscoverySessionCallback callback) {
        enforceAccessPermission();
        enforceChangePermission();
        int uid = getMockableCallingUid();
        this.mAppOps.checkPackage(uid, callingPackage);
        enforceLocationPermission(callingPackage, getMockableCallingUid());
        if (callback == null) {
            throw new IllegalArgumentException("Callback must not be null");
        } else if (publishConfig != null) {
            publishConfig.assertValid(this.mStateManager.getCharacteristics(), this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.rtt"));
            enforceClientValidity(uid, clientId);
            this.mStateManager.publish(clientId, publishConfig, callback);
        } else {
            throw new IllegalArgumentException("PublishConfig must not be null");
        }
    }

    public void updatePublish(int clientId, int sessionId, PublishConfig publishConfig) {
        enforceAccessPermission();
        enforceChangePermission();
        if (publishConfig != null) {
            publishConfig.assertValid(this.mStateManager.getCharacteristics(), this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.rtt"));
            enforceClientValidity(getMockableCallingUid(), clientId);
            this.mStateManager.updatePublish(clientId, sessionId, publishConfig);
            return;
        }
        throw new IllegalArgumentException("PublishConfig must not be null");
    }

    public void subscribe(String callingPackage, int clientId, SubscribeConfig subscribeConfig, IWifiAwareDiscoverySessionCallback callback) {
        enforceAccessPermission();
        enforceChangePermission();
        int uid = getMockableCallingUid();
        this.mAppOps.checkPackage(uid, callingPackage);
        enforceLocationPermission(callingPackage, getMockableCallingUid());
        if (callback == null) {
            throw new IllegalArgumentException("Callback must not be null");
        } else if (subscribeConfig != null) {
            subscribeConfig.assertValid(this.mStateManager.getCharacteristics(), this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.rtt"));
            enforceClientValidity(uid, clientId);
            this.mStateManager.subscribe(clientId, subscribeConfig, callback);
        } else {
            throw new IllegalArgumentException("SubscribeConfig must not be null");
        }
    }

    public void updateSubscribe(int clientId, int sessionId, SubscribeConfig subscribeConfig) {
        enforceAccessPermission();
        enforceChangePermission();
        if (subscribeConfig != null) {
            subscribeConfig.assertValid(this.mStateManager.getCharacteristics(), this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.rtt"));
            enforceClientValidity(getMockableCallingUid(), clientId);
            this.mStateManager.updateSubscribe(clientId, sessionId, subscribeConfig);
            return;
        }
        throw new IllegalArgumentException("SubscribeConfig must not be null");
    }

    public void sendMessage(int clientId, int sessionId, int peerId, byte[] message, int messageId, int retryCount) {
        enforceAccessPermission();
        enforceChangePermission();
        if (retryCount != 0) {
            enforceNetworkStackPermission();
        }
        if (message != null && message.length > this.mStateManager.getCharacteristics().getMaxServiceSpecificInfoLength()) {
            throw new IllegalArgumentException("Message length longer than supported by device characteristics");
        } else if (retryCount < 0 || retryCount > DiscoverySession.getMaxSendRetryCount()) {
            throw new IllegalArgumentException("Invalid 'retryCount' must be non-negative and <= DiscoverySession.MAX_SEND_RETRY_COUNT");
        } else {
            enforceClientValidity(getMockableCallingUid(), clientId);
            this.mStateManager.sendMessage(clientId, sessionId, peerId, message, messageId, retryCount);
        }
    }

    public void requestMacAddresses(int uid, List peerIds, IWifiAwareMacAddressProvider callback) {
        enforceNetworkStackPermission();
        this.mStateManager.requestMacAddresses(uid, peerIds, callback);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.wifi.aware.WifiAwareShellCommand r0 = r8.mShellCommand
            r1 = r8
            r2 = r9
            r3 = r10
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r14
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.aware.WifiAwareServiceImpl.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            pw.println("Permission Denial: can't dump WifiAwareService from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("Wi-Fi Aware Service");
        synchronized (this.mLock) {
            pw.println("  mNextClientId: " + this.mNextClientId);
            pw.println("  mDeathRecipientsByClientId: " + this.mDeathRecipientsByClientId);
            pw.println("  mUidByClientId: " + this.mUidByClientId);
        }
        this.mStateManager.dump(fd, pw, args);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private void enforceClientValidity(int uid, int clientId) {
        synchronized (this.mLock) {
            int uidIndex = this.mUidByClientId.indexOfKey(clientId);
            if (uidIndex < 0 || this.mUidByClientId.valueAt(uidIndex) != uid) {
                throw new SecurityException("Attempting to use invalid uid+clientId mapping: uid=" + uid + ", clientId=" + clientId);
            }
        }
    }

    private void enforceAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE", TAG);
    }

    private void enforceChangePermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CHANGE_WIFI_STATE", TAG);
    }

    private void enforceLocationPermission(String callingPackage, int uid) {
        this.mWifiPermissionsUtil.enforceLocationPermission(callingPackage, uid);
    }

    private void enforceNetworkStackPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_STACK", TAG);
    }
}
