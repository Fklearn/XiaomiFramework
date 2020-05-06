package com.android.server.wifi;

import android.content.Context;
import android.hardware.wifi.hostapd.V1_0.HostapdStatus;
import android.hardware.wifi.hostapd.V1_0.IHostapd;
import android.hardware.wifi.hostapd.V1_1.IHostapd;
import android.hardware.wifi.hostapd.V1_1.IHostapdCallback;
import android.hidl.manager.V1_0.IServiceManager;
import android.hidl.manager.V1_0.IServiceNotification;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.IHwBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.wifi.HostapdHal;
import com.android.server.wifi.WifiNative;
import com.android.server.wifi.util.NativeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.concurrent.ThreadSafe;
import miui.telephony.phonenumber.Prefix;
import vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor;
import vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendorIfaceCallback;
import vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor;
import vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendorIfaceCallback;

@ThreadSafe
public class HostapdHal {
    @VisibleForTesting
    public static final String HAL_INSTANCE_NAME = "default";
    private static final String TAG = "HostapdHal";
    private final List<IHostapd.AcsChannelRange> mAcsChannelRanges;
    private String mCountryCode = null;
    private WifiNative.HostapdDeathEventHandler mDeathEventHandler;
    /* access modifiers changed from: private */
    public long mDeathRecipientCookie = 0;
    private final boolean mEnableAcs;
    private final boolean mEnableIeee80211AC;
    /* access modifiers changed from: private */
    public final Handler mEventHandler;
    private HostapdDeathRecipient mHostapdDeathRecipient;
    private HostapdVendorDeathRecipient mHostapdVendorDeathRecipient;
    private android.hardware.wifi.hostapd.V1_0.IHostapd mIHostapd;
    private IHostapdVendor mIHostapdVendor;
    /* access modifiers changed from: private */
    public IServiceManager mIServiceManager = null;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private ServiceManagerDeathRecipient mServiceManagerDeathRecipient;
    private final IServiceNotification mServiceNotificationCallback = new IServiceNotification.Stub() {
        public void onRegistration(String fqName, String name, boolean preexisting) {
            synchronized (HostapdHal.this.mLock) {
                if (HostapdHal.this.mVerboseLoggingEnabled) {
                    Log.i(HostapdHal.TAG, "IServiceNotification.onRegistration for: " + fqName + ", " + name + " preexisting=" + preexisting);
                }
                if (!HostapdHal.this.initHostapdService()) {
                    Log.e(HostapdHal.TAG, "initalizing IHostapd failed.");
                    HostapdHal.this.hostapdServiceDiedHandler(HostapdHal.this.mDeathRecipientCookie);
                } else {
                    Log.i(HostapdHal.TAG, "Completed initialization of IHostapd.");
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public HashMap<String, WifiNative.SoftApListener> mSoftApListeners = new HashMap<>();
    private final List<IHostapdVendor.AcsChannelRange> mVendorAcsChannelRanges;
    /* access modifiers changed from: private */
    public boolean mVerboseLoggingEnabled = false;

    private class ServiceManagerDeathRecipient implements IHwBinder.DeathRecipient {
        private ServiceManagerDeathRecipient() {
        }

        public void serviceDied(long cookie) {
            HostapdHal.this.mEventHandler.post(new Runnable(cookie) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    HostapdHal.ServiceManagerDeathRecipient.this.lambda$serviceDied$0$HostapdHal$ServiceManagerDeathRecipient(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$serviceDied$0$HostapdHal$ServiceManagerDeathRecipient(long cookie) {
            synchronized (HostapdHal.this.mLock) {
                Log.w(HostapdHal.TAG, "IServiceManager died: cookie=" + cookie);
                HostapdHal.this.hostapdServiceDiedHandler(HostapdHal.this.mDeathRecipientCookie);
                IServiceManager unused = HostapdHal.this.mIServiceManager = null;
            }
        }
    }

    private class HostapdDeathRecipient implements IHwBinder.DeathRecipient {
        private HostapdDeathRecipient() {
        }

        public void serviceDied(long cookie) {
            HostapdHal.this.mEventHandler.post(new Runnable(cookie) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    HostapdHal.HostapdDeathRecipient.this.lambda$serviceDied$0$HostapdHal$HostapdDeathRecipient(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$serviceDied$0$HostapdHal$HostapdDeathRecipient(long cookie) {
            synchronized (HostapdHal.this.mLock) {
                Log.w(HostapdHal.TAG, "IHostapd/IHostapd died: cookie=" + cookie);
                HostapdHal.this.hostapdServiceDiedHandler(cookie);
            }
        }
    }

    private class HostapdVendorDeathRecipient implements IHwBinder.DeathRecipient {
        private HostapdVendorDeathRecipient() {
        }

        public void serviceDied(long cookie) {
            HostapdHal.this.mEventHandler.post(new Runnable(cookie) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    HostapdHal.HostapdVendorDeathRecipient.this.lambda$serviceDied$0$HostapdHal$HostapdVendorDeathRecipient(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$serviceDied$0$HostapdHal$HostapdVendorDeathRecipient(long cookie) {
            synchronized (HostapdHal.this.mLock) {
                Log.w(HostapdHal.TAG, "IHostapdVendor died: cookie=" + cookie);
                HostapdHal.this.hostapdServiceDiedHandler(cookie);
            }
        }
    }

    public HostapdHal(Context context, Looper looper) {
        this.mEventHandler = new Handler(looper);
        this.mEnableAcs = context.getResources().getBoolean(17891601);
        this.mEnableIeee80211AC = context.getResources().getBoolean(17891602);
        this.mAcsChannelRanges = toAcsChannelRanges(context.getResources().getString(17039805));
        this.mServiceManagerDeathRecipient = new ServiceManagerDeathRecipient();
        this.mHostapdDeathRecipient = new HostapdDeathRecipient();
        this.mHostapdVendorDeathRecipient = new HostapdVendorDeathRecipient();
        this.mVendorAcsChannelRanges = toVendorAcsChannelRanges(context.getResources().getString(17039805));
    }

    /* access modifiers changed from: package-private */
    public void enableVerboseLogging(boolean enable) {
        synchronized (this.mLock) {
            this.mVerboseLoggingEnabled = enable;
            setLogLevel(enable);
        }
    }

    private boolean isV1_1() {
        synchronized (this.mLock) {
            boolean z = false;
            if (this.mIServiceManager == null) {
                Log.e(TAG, "isV1_1: called but mServiceManager is null!?");
                return false;
            }
            try {
                if (this.mIServiceManager.getTransport(IHostapd.kInterfaceName, "default") != 0) {
                    z = true;
                }
                return z;
            } catch (RemoteException e) {
                Log.e(TAG, "Exception while operating on IServiceManager: " + e);
                handleRemoteException(e, "getTransport");
                return false;
            }
        }
    }

    private boolean linkToServiceManagerDeath() {
        synchronized (this.mLock) {
            if (this.mIServiceManager == null) {
                return false;
            }
            try {
                if (this.mIServiceManager.linkToDeath(this.mServiceManagerDeathRecipient, 0)) {
                    return true;
                }
                Log.wtf(TAG, "Error on linkToDeath on IServiceManager");
                hostapdServiceDiedHandler(this.mDeathRecipientCookie);
                this.mIServiceManager = null;
                return false;
            } catch (RemoteException e) {
                Log.e(TAG, "IServiceManager.linkToDeath exception", e);
                this.mIServiceManager = null;
                return false;
            }
        }
    }

    public boolean initialize() {
        synchronized (this.mLock) {
            if (this.mVerboseLoggingEnabled) {
                Log.i(TAG, "Registering IHostapd service ready callback.");
            }
            this.mIHostapd = null;
            this.mIHostapdVendor = null;
            if (this.mIServiceManager != null) {
                return true;
            }
            try {
                this.mIServiceManager = getServiceManagerMockable();
                if (this.mIServiceManager == null) {
                    Log.e(TAG, "Failed to get HIDL Service Manager");
                    return false;
                } else if (!linkToServiceManagerDeath()) {
                    return false;
                } else {
                    if (this.mIServiceManager.registerForNotifications(android.hardware.wifi.hostapd.V1_0.IHostapd.kInterfaceName, "default", this.mServiceNotificationCallback)) {
                        return true;
                    }
                    Log.e(TAG, "Failed to register for notifications to android.hardware.wifi.hostapd@1.0::IHostapd");
                    this.mIServiceManager = null;
                    return false;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Exception while trying to register a listener for IHostapd service: " + e);
                hostapdServiceDiedHandler(this.mDeathRecipientCookie);
                this.mIServiceManager = null;
                return false;
            }
        }
    }

    private boolean linkToHostapdDeath() {
        synchronized (this.mLock) {
            if (this.mIHostapd == null) {
                return false;
            }
            try {
                android.hardware.wifi.hostapd.V1_0.IHostapd iHostapd = this.mIHostapd;
                HostapdDeathRecipient hostapdDeathRecipient = this.mHostapdDeathRecipient;
                long j = this.mDeathRecipientCookie + 1;
                this.mDeathRecipientCookie = j;
                if (iHostapd.linkToDeath(hostapdDeathRecipient, j)) {
                    return true;
                }
                Log.wtf(TAG, "Error on linkToDeath on IHostapd");
                hostapdServiceDiedHandler(this.mDeathRecipientCookie);
                return false;
            } catch (RemoteException e) {
                Log.e(TAG, "IHostapd.linkToDeath exception", e);
                return false;
            }
        }
    }

    private boolean registerCallback(IHostapdCallback callback) {
        synchronized (this.mLock) {
            try {
                IHostapd iHostapdV1_1 = getHostapdMockableV1_1();
                if (iHostapdV1_1 == null) {
                    return false;
                }
                boolean checkStatusAndLogFailure = checkStatusAndLogFailure(iHostapdV1_1.registerCallback(callback), "registerCallback_1_1");
                return checkStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "registerCallback_1_1");
                return false;
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x003d, code lost:
        if (initHostapdVendorService() != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x003f, code lost:
        android.util.Log.e(TAG, "Failed to init HostapdVendor service");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean initHostapdService() {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            r1 = 0
            android.hardware.wifi.hostapd.V1_0.IHostapd r2 = r6.getHostapdMockable()     // Catch:{ RemoteException -> 0x0063, NoSuchElementException -> 0x004a }
            r6.mIHostapd = r2     // Catch:{ RemoteException -> 0x0063, NoSuchElementException -> 0x004a }
            android.hardware.wifi.hostapd.V1_0.IHostapd r2 = r6.mIHostapd     // Catch:{ all -> 0x0048 }
            if (r2 != 0) goto L_0x0018
            java.lang.String r2 = "HostapdHal"
            java.lang.String r3 = "Got null IHostapd service. Stopping hostapd HIDL startup"
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return r1
        L_0x0018:
            boolean r2 = r6.linkToHostapdDeath()     // Catch:{ all -> 0x0048 }
            r3 = 0
            if (r2 != 0) goto L_0x0023
            r6.mIHostapd = r3     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return r1
        L_0x0023:
            boolean r2 = r6.isV1_1()     // Catch:{ all -> 0x0048 }
            if (r2 == 0) goto L_0x0038
            com.android.server.wifi.HostapdHal$HostapdCallback r2 = new com.android.server.wifi.HostapdHal$HostapdCallback     // Catch:{ all -> 0x0048 }
            r2.<init>()     // Catch:{ all -> 0x0048 }
            boolean r2 = r6.registerCallback(r2)     // Catch:{ all -> 0x0048 }
            if (r2 != 0) goto L_0x0038
            r6.mIHostapd = r3     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return r1
        L_0x0038:
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            boolean r0 = r6.initHostapdVendorService()
            if (r0 != 0) goto L_0x0046
            java.lang.String r0 = "HostapdHal"
            java.lang.String r1 = "Failed to init HostapdVendor service"
            android.util.Log.e(r0, r1)
        L_0x0046:
            r0 = 1
            return r0
        L_0x0048:
            r1 = move-exception
            goto L_0x007c
        L_0x004a:
            r2 = move-exception
            java.lang.String r3 = "HostapdHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0048 }
            r4.<init>()     // Catch:{ all -> 0x0048 }
            java.lang.String r5 = "IHostapd.getService exception: "
            r4.append(r5)     // Catch:{ all -> 0x0048 }
            r4.append(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0048 }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return r1
        L_0x0063:
            r2 = move-exception
            java.lang.String r3 = "HostapdHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0048 }
            r4.<init>()     // Catch:{ all -> 0x0048 }
            java.lang.String r5 = "IHostapd.getService exception: "
            r4.append(r5)     // Catch:{ all -> 0x0048 }
            r4.append(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0048 }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return r1
        L_0x007c:
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.HostapdHal.initHostapdService():boolean");
    }

    public boolean addAccessPoint(String ifaceName, WifiConfiguration config, WifiNative.SoftApListener listener) {
        HostapdStatus status;
        synchronized (this.mLock) {
            IHostapd.IfaceParams ifaceParams = new IHostapd.IfaceParams();
            ifaceParams.ifaceName = WifiApManager.buildSoftapdIfaceAndConf(ifaceName);
            ifaceParams.hwModeParams.enable80211N = true;
            ifaceParams.hwModeParams.enable80211AC = this.mEnableIeee80211AC;
            try {
                ifaceParams.channelParams.band = getBand(config);
                if (this.mEnableAcs) {
                    ifaceParams.channelParams.enableAcs = true;
                    ifaceParams.channelParams.acsShouldExcludeDfs = true;
                } else {
                    if (ifaceParams.channelParams.band == 2) {
                        Log.d(TAG, "ACS is not supported on this device, using 2.4 GHz band.");
                        ifaceParams.channelParams.band = 0;
                    }
                    ifaceParams.channelParams.enableAcs = false;
                    ifaceParams.channelParams.channel = config.apChannel;
                }
                IHostapd.NetworkParams nwParams = new IHostapd.NetworkParams();
                nwParams.ssid.addAll(NativeUtil.stringToByteArrayList(config.SSID));
                nwParams.isHidden = config.hiddenSSID;
                nwParams.encryptionType = getEncryptionType(config);
                nwParams.pskPassphrase = config.preSharedKey != null ? config.preSharedKey : Prefix.EMPTY;
                if (!checkHostapdAndLogFailure("addAccessPoint")) {
                    return false;
                }
                try {
                    if (isV1_1()) {
                        IHostapd.IfaceParams ifaceParams1_1 = new IHostapd.IfaceParams();
                        ifaceParams1_1.V1_0 = ifaceParams;
                        if (this.mEnableAcs) {
                            ifaceParams1_1.channelParams.acsChannelRanges.addAll(this.mAcsChannelRanges);
                        }
                        android.hardware.wifi.hostapd.V1_1.IHostapd iHostapdV1_1 = getHostapdMockableV1_1();
                        if (iHostapdV1_1 == null) {
                            return false;
                        }
                        status = iHostapdV1_1.addAccessPoint_1_1(ifaceParams1_1, nwParams);
                    } else {
                        status = this.mIHostapd.addAccessPoint(ifaceParams, nwParams);
                    }
                    if (!checkStatusAndLogFailure(status, "addAccessPoint")) {
                        return false;
                    }
                    this.mSoftApListeners.put(ifaceName, listener);
                    return true;
                } catch (RemoteException e) {
                    handleRemoteException(e, "addAccessPoint");
                    return false;
                }
            } catch (IllegalArgumentException e2) {
                Log.e(TAG, "Unrecognized apBand " + config.apBand);
                return false;
            }
        }
    }

    public boolean removeAccessPoint(String ifaceName) {
        synchronized (this.mLock) {
            if (!checkHostapdAndLogFailure("removeAccessPoint")) {
                return false;
            }
            try {
                if (!checkStatusAndLogFailure(this.mIHostapd.removeAccessPoint(ifaceName), "removeAccessPoint")) {
                    return false;
                }
                this.mSoftApListeners.remove(ifaceName);
                return true;
            } catch (RemoteException e) {
                handleRemoteException(e, "removeAccessPoint");
                return false;
            }
        }
    }

    public Pair<Boolean, String> doHostapdCommand(String command) {
        return new Pair<>(false, Prefix.EMPTY);
    }

    public boolean registerDeathHandler(WifiNative.HostapdDeathEventHandler handler) {
        if (this.mDeathEventHandler != null) {
            Log.e(TAG, "Death handler already present");
        }
        this.mDeathEventHandler = handler;
        return true;
    }

    public boolean deregisterDeathHandler() {
        if (this.mDeathEventHandler == null) {
            Log.e(TAG, "No Death handler present");
        }
        this.mDeathEventHandler = null;
        return true;
    }

    private void clearState() {
        synchronized (this.mLock) {
            this.mIHostapd = null;
            this.mIHostapdVendor = null;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void hostapdServiceDiedHandler(long r4) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            long r1 = r3.mDeathRecipientCookie     // Catch:{ all -> 0x0020 }
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 == 0) goto L_0x0012
            java.lang.String r1 = "HostapdHal"
            java.lang.String r2 = "Ignoring stale death recipient notification"
            android.util.Log.i(r1, r2)     // Catch:{ all -> 0x0020 }
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return
        L_0x0012:
            r3.clearState()     // Catch:{ all -> 0x0020 }
            com.android.server.wifi.WifiNative$HostapdDeathEventHandler r1 = r3.mDeathEventHandler     // Catch:{ all -> 0x0020 }
            if (r1 == 0) goto L_0x001e
            com.android.server.wifi.WifiNative$HostapdDeathEventHandler r1 = r3.mDeathEventHandler     // Catch:{ all -> 0x0020 }
            r1.onDeath()     // Catch:{ all -> 0x0020 }
        L_0x001e:
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return
        L_0x0020:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.HostapdHal.hostapdServiceDiedHandler(long):void");
    }

    public boolean isInitializationStarted() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIServiceManager != null;
        }
        return z;
    }

    public boolean isInitializationComplete() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIHostapd != null;
        }
        return z;
    }

    public boolean startDaemon() {
        synchronized (this.mLock) {
            try {
                getHostapdMockable();
            } catch (RemoteException e) {
                Log.e(TAG, "Exception while trying to start hostapd: " + e);
                hostapdServiceDiedHandler(this.mDeathRecipientCookie);
                return false;
            } catch (NoSuchElementException e2) {
                Log.d(TAG, "Successfully triggered start of hostapd using HIDL");
            }
        }
        return true;
    }

    public void terminate() {
        synchronized (this.mLock) {
            if (checkHostapdAndLogFailure("terminate")) {
                try {
                    this.mIHostapd.terminate();
                } catch (RemoteException e) {
                    handleRemoteException(e, "terminate");
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public IServiceManager getServiceManagerMockable() throws RemoteException {
        IServiceManager service;
        synchronized (this.mLock) {
            service = IServiceManager.getService();
        }
        return service;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public android.hardware.wifi.hostapd.V1_0.IHostapd getHostapdMockable() throws RemoteException {
        android.hardware.wifi.hostapd.V1_0.IHostapd service;
        synchronized (this.mLock) {
            service = android.hardware.wifi.hostapd.V1_0.IHostapd.getService();
        }
        return service;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public android.hardware.wifi.hostapd.V1_1.IHostapd getHostapdMockableV1_1() throws RemoteException {
        android.hardware.wifi.hostapd.V1_1.IHostapd castFrom;
        synchronized (this.mLock) {
            try {
                castFrom = android.hardware.wifi.hostapd.V1_1.IHostapd.castFrom(this.mIHostapd);
            } catch (NoSuchElementException e) {
                Log.e(TAG, "Failed to get IHostapd", e);
                return null;
            } catch (Throwable th) {
                throw th;
            }
        }
        return castFrom;
    }

    private static int getEncryptionType(WifiConfiguration localConfig) {
        int authType = localConfig.getAuthType();
        if (authType == 0) {
            return 0;
        }
        if (authType == 1) {
            return 1;
        }
        if (authType != 4) {
            return 0;
        }
        return 2;
    }

    private static int getVendorEncryptionType(WifiConfiguration localConfig) {
        int authType = localConfig.getAuthType();
        if (authType == 0) {
            return 0;
        }
        if (authType == 1) {
            return 1;
        }
        if (authType == 4) {
            return 2;
        }
        if (authType == 8) {
            return 3;
        }
        if (authType != 9) {
            return 0;
        }
        return 4;
    }

    private static int getBand(WifiConfiguration localConfig) {
        int i = localConfig.apBand;
        if (i == -1) {
            return 2;
        }
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        throw new IllegalArgumentException();
    }

    private List<IHostapd.AcsChannelRange> toAcsChannelRanges(String channelListStr) {
        ArrayList<IHostapd.AcsChannelRange> acsChannelRanges = new ArrayList<>();
        for (String channelRange : channelListStr.split(",")) {
            IHostapd.AcsChannelRange acsChannelRange = new IHostapd.AcsChannelRange();
            try {
                if (channelRange.contains("-")) {
                    String[] channels = channelRange.split("-");
                    if (channels.length != 2) {
                        Log.e(TAG, "Unrecognized channel range, length is " + channels.length);
                    } else {
                        int start = Integer.parseInt(channels[0]);
                        int end = Integer.parseInt(channels[1]);
                        if (start > end) {
                            Log.e(TAG, "Invalid channel range, from " + start + " to " + end);
                        } else {
                            acsChannelRange.start = start;
                            acsChannelRange.end = end;
                        }
                    }
                } else {
                    acsChannelRange.start = Integer.parseInt(channelRange);
                    acsChannelRange.end = acsChannelRange.start;
                }
                acsChannelRanges.add(acsChannelRange);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Malformed channel value detected: " + e);
            }
        }
        return acsChannelRanges;
    }

    private boolean checkHostapdAndLogFailure(String methodStr) {
        synchronized (this.mLock) {
            if (this.mIHostapd != null) {
                return true;
            }
            Log.e(TAG, "Can't call " + methodStr + ", IHostapd is null");
            return false;
        }
    }

    private boolean checkStatusAndLogFailure(HostapdStatus status, String methodStr) {
        synchronized (this.mLock) {
            if (status.code != 0) {
                Log.e(TAG, "IHostapd." + methodStr + " failed: " + status.code + ", " + status.debugMessage);
                return false;
            }
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "IHostapd." + methodStr + " succeeded");
            }
            return true;
        }
    }

    private void handleRemoteException(RemoteException e, String methodStr) {
        synchronized (this.mLock) {
            hostapdServiceDiedHandler(this.mDeathRecipientCookie);
            Log.e(TAG, "IHostapd." + methodStr + " failed with exception", e);
        }
    }

    private class HostapdCallback extends IHostapdCallback.Stub {
        private HostapdCallback() {
        }

        public void onFailure(String ifaceName) {
            Log.w(HostapdHal.TAG, "Failure on iface " + ifaceName);
            WifiNative.SoftApListener listener = (WifiNative.SoftApListener) HostapdHal.this.mSoftApListeners.get(ifaceName);
            if (listener != null) {
                listener.onFailure();
            }
        }
    }

    private boolean isVendorV1_1() {
        synchronized (this.mLock) {
            boolean z = false;
            if (this.mIServiceManager == null) {
                Log.e(TAG, "isVendorV1_1: called but mServiceManager is null!?");
                return false;
            }
            try {
                if (this.mIServiceManager.getTransport(vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor.kInterfaceName, "default") != 0) {
                    z = true;
                }
                return z;
            } catch (RemoteException e) {
                Log.e(TAG, "Exception while operating on IServiceManager: " + e);
                handleRemoteException(e, "getTransport");
                return false;
            }
        }
    }

    private boolean linkToHostapdVendorDeath() {
        synchronized (this.mLock) {
            if (this.mIHostapdVendor == null) {
                return false;
            }
            try {
                if (this.mIHostapdVendor.linkToDeath(this.mHostapdVendorDeathRecipient, this.mDeathRecipientCookie)) {
                    return true;
                }
                Log.wtf(TAG, "Error on linkToDeath on IHostapdVendor");
                hostapdServiceDiedHandler(this.mDeathRecipientCookie);
                return false;
            } catch (RemoteException e) {
                Log.e(TAG, "IHostapdVendor.linkToDeath exception", e);
                return false;
            }
        }
    }

    public boolean initHostapdVendorService() {
        synchronized (this.mLock) {
            try {
                this.mIHostapdVendor = getHostapdVendorMockable();
                if (this.mIHostapdVendor == null) {
                    Log.e(TAG, "Got null IHostapdVendor service. Stopping hostapdVendor HIDL startup");
                    return false;
                } else if (linkToHostapdVendorDeath()) {
                    return true;
                } else {
                    this.mIHostapdVendor = null;
                    return false;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "IHostapdVendor.getService exception: " + e);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0151, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean addVendorAccessPoint(java.lang.String r17, android.net.wifi.WifiConfiguration r18, com.android.server.wifi.WifiNative.SoftApListener r19) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            java.lang.Object r5 = r1.mLock
            monitor-enter(r5)
            java.lang.String r0 = "addVendorAccessPoint"
            r6 = r0
            com.android.server.wifi.WifiInjector r0 = com.android.server.wifi.WifiInjector.getInstance()     // Catch:{ all -> 0x0178 }
            com.android.server.wifi.WifiApConfigStore r0 = r0.getWifiApConfigStore()     // Catch:{ all -> 0x0178 }
            r7 = r0
            vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor$VendorIfaceParams r0 = new vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor$VendorIfaceParams     // Catch:{ all -> 0x0178 }
            r0.<init>()     // Catch:{ all -> 0x0178 }
            r8 = r0
            android.hardware.wifi.hostapd.V1_0.IHostapd$IfaceParams r0 = r8.ifaceParams     // Catch:{ all -> 0x0178 }
            r9 = r0
            r9.ifaceName = r2     // Catch:{ all -> 0x0178 }
            android.hardware.wifi.hostapd.V1_0.IHostapd$HwModeParams r0 = r9.hwModeParams     // Catch:{ all -> 0x0178 }
            r10 = 1
            r0.enable80211N = r10     // Catch:{ all -> 0x0178 }
            android.hardware.wifi.hostapd.V1_0.IHostapd$HwModeParams r0 = r9.hwModeParams     // Catch:{ all -> 0x0178 }
            boolean r11 = r1.mEnableIeee80211AC     // Catch:{ all -> 0x0178 }
            r0.enable80211AC = r11     // Catch:{ all -> 0x0178 }
            java.lang.String r0 = r1.mCountryCode     // Catch:{ all -> 0x0178 }
            if (r0 != 0) goto L_0x0034
            java.lang.String r0 = ""
            goto L_0x0036
        L_0x0034:
            java.lang.String r0 = r1.mCountryCode     // Catch:{ all -> 0x0178 }
        L_0x0036:
            r8.countryCode = r0     // Catch:{ all -> 0x0178 }
            java.lang.String r0 = ""
            r8.bridgeIfaceName = r0     // Catch:{ all -> 0x0178 }
            r11 = 0
            android.hardware.wifi.hostapd.V1_0.IHostapd$ChannelParams r0 = r9.channelParams     // Catch:{ IllegalArgumentException -> 0x015c }
            int r12 = getBand(r18)     // Catch:{ IllegalArgumentException -> 0x015c }
            r0.band = r12     // Catch:{ IllegalArgumentException -> 0x015c }
            boolean r0 = r1.mEnableAcs     // Catch:{ all -> 0x0178 }
            if (r0 == 0) goto L_0x0053
            android.hardware.wifi.hostapd.V1_0.IHostapd$ChannelParams r0 = r9.channelParams     // Catch:{ all -> 0x0178 }
            r0.enableAcs = r10     // Catch:{ all -> 0x0178 }
            android.hardware.wifi.hostapd.V1_0.IHostapd$ChannelParams r0 = r9.channelParams     // Catch:{ all -> 0x0178 }
            r0.acsShouldExcludeDfs = r10     // Catch:{ all -> 0x0178 }
            goto L_0x006f
        L_0x0053:
            android.hardware.wifi.hostapd.V1_0.IHostapd$ChannelParams r0 = r9.channelParams     // Catch:{ all -> 0x0178 }
            int r0 = r0.band     // Catch:{ all -> 0x0178 }
            r12 = 2
            if (r0 != r12) goto L_0x0065
            java.lang.String r0 = "HostapdHal"
            java.lang.String r12 = "ACS is not supported on this device, using 2.4 GHz band."
            android.util.Log.d(r0, r12)     // Catch:{ all -> 0x0178 }
            android.hardware.wifi.hostapd.V1_0.IHostapd$ChannelParams r0 = r9.channelParams     // Catch:{ all -> 0x0178 }
            r0.band = r11     // Catch:{ all -> 0x0178 }
        L_0x0065:
            android.hardware.wifi.hostapd.V1_0.IHostapd$ChannelParams r0 = r9.channelParams     // Catch:{ all -> 0x0178 }
            r0.enableAcs = r11     // Catch:{ all -> 0x0178 }
            android.hardware.wifi.hostapd.V1_0.IHostapd$ChannelParams r0 = r9.channelParams     // Catch:{ all -> 0x0178 }
            int r12 = r3.apChannel     // Catch:{ all -> 0x0178 }
            r0.channel = r12     // Catch:{ all -> 0x0178 }
        L_0x006f:
            android.hardware.wifi.hostapd.V1_0.IHostapd$NetworkParams r0 = new android.hardware.wifi.hostapd.V1_0.IHostapd$NetworkParams     // Catch:{ all -> 0x0178 }
            r0.<init>()     // Catch:{ all -> 0x0178 }
            r12 = r0
            java.util.ArrayList<java.lang.Byte> r0 = r12.ssid     // Catch:{ all -> 0x0178 }
            java.lang.String r13 = r3.SSID     // Catch:{ all -> 0x0178 }
            java.util.ArrayList r13 = com.android.server.wifi.util.NativeUtil.stringToByteArrayList(r13)     // Catch:{ all -> 0x0178 }
            r0.addAll(r13)     // Catch:{ all -> 0x0178 }
            boolean r0 = r3.hiddenSSID     // Catch:{ all -> 0x0178 }
            r12.isHidden = r0     // Catch:{ all -> 0x0178 }
            int r0 = getEncryptionType(r18)     // Catch:{ all -> 0x0178 }
            r12.encryptionType = r0     // Catch:{ all -> 0x0178 }
            java.lang.String r0 = r3.preSharedKey     // Catch:{ all -> 0x0178 }
            if (r0 == 0) goto L_0x0091
            java.lang.String r0 = r3.preSharedKey     // Catch:{ all -> 0x0178 }
            goto L_0x0093
        L_0x0091:
            java.lang.String r0 = ""
        L_0x0093:
            r12.pskPassphrase = r0     // Catch:{ all -> 0x0178 }
            java.lang.String r0 = "addVendorAccessPoint"
            boolean r0 = r1.checkHostapdVendorAndLogFailure(r0)     // Catch:{ all -> 0x0178 }
            if (r0 != 0) goto L_0x009f
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            return r11
        L_0x009f:
            boolean r0 = r7.getDualSapStatus()     // Catch:{ RemoteException -> 0x0153 }
            if (r0 == 0) goto L_0x00b1
            java.lang.String r0 = r7.getBridgeInterface()     // Catch:{ RemoteException -> 0x0153 }
            if (r0 == 0) goto L_0x00ad
            r13 = r0
            goto L_0x00af
        L_0x00ad:
            java.lang.String r13 = ""
        L_0x00af:
            r8.bridgeIfaceName = r13     // Catch:{ RemoteException -> 0x0153 }
        L_0x00b1:
            boolean r0 = r16.isVendorV1_1()     // Catch:{ RemoteException -> 0x0153 }
            if (r0 == 0) goto L_0x0120
            vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor r0 = r16.getHostapdVendorMockableV1_1()     // Catch:{ RemoteException -> 0x0153 }
            if (r0 != 0) goto L_0x00c7
            java.lang.String r10 = "HostapdHal"
            java.lang.String r13 = "Failed to get V1_1.IHostapdVendor"
            android.util.Log.e(r10, r13)     // Catch:{ RemoteException -> 0x0153 }
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            return r11
        L_0x00c7:
            boolean r13 = r1.mVerboseLoggingEnabled     // Catch:{ RemoteException -> 0x0153 }
            r1.setLogLevel(r13)     // Catch:{ RemoteException -> 0x0153 }
            vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor$VendorIfaceParams r13 = new vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor$VendorIfaceParams     // Catch:{ RemoteException -> 0x0153 }
            r13.<init>()     // Catch:{ RemoteException -> 0x0153 }
            r13.VendorV1_0 = r8     // Catch:{ RemoteException -> 0x0153 }
            vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor$VendorChannelParams r14 = r13.vendorChannelParams     // Catch:{ RemoteException -> 0x0153 }
            android.hardware.wifi.hostapd.V1_0.IHostapd$ChannelParams r15 = r9.channelParams     // Catch:{ RemoteException -> 0x0153 }
            r14.channelParams = r15     // Catch:{ RemoteException -> 0x0153 }
            int r14 = getVendorEncryptionType(r18)     // Catch:{ RemoteException -> 0x0153 }
            r13.vendorEncryptionType = r14     // Catch:{ RemoteException -> 0x0153 }
            java.lang.String r14 = r3.oweTransIfaceName     // Catch:{ RemoteException -> 0x0153 }
            if (r14 == 0) goto L_0x00e6
            java.lang.String r14 = r3.oweTransIfaceName     // Catch:{ RemoteException -> 0x0153 }
            goto L_0x00e8
        L_0x00e6:
            java.lang.String r14 = ""
        L_0x00e8:
            r13.oweTransIfaceName = r14     // Catch:{ RemoteException -> 0x0153 }
            boolean r14 = r1.mEnableAcs     // Catch:{ RemoteException -> 0x0153 }
            if (r14 == 0) goto L_0x00f7
            vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor$VendorChannelParams r14 = r13.vendorChannelParams     // Catch:{ RemoteException -> 0x0153 }
            java.util.ArrayList<vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor$AcsChannelRange> r14 = r14.acsChannelRanges     // Catch:{ RemoteException -> 0x0153 }
            java.util.List<vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor$AcsChannelRange> r15 = r1.mVendorAcsChannelRanges     // Catch:{ RemoteException -> 0x0153 }
            r14.addAll(r15)     // Catch:{ RemoteException -> 0x0153 }
        L_0x00f7:
            android.hardware.wifi.hostapd.V1_0.HostapdStatus r14 = r0.addVendorAccessPoint_1_1(r13, r12)     // Catch:{ RemoteException -> 0x0153 }
            java.lang.String r15 = "addVendorAccessPoint"
            boolean r15 = r1.checkVendorStatusAndLogFailure(r14, r15)     // Catch:{ RemoteException -> 0x0153 }
            if (r15 == 0) goto L_0x011f
            com.android.server.wifi.HostapdHal$HostapdVendorIfaceHalCallbackV1_1 r15 = new com.android.server.wifi.HostapdHal$HostapdVendorIfaceHalCallbackV1_1     // Catch:{ RemoteException -> 0x0153 }
            r15.<init>(r2, r4)     // Catch:{ RemoteException -> 0x0153 }
            java.lang.String r10 = r9.ifaceName     // Catch:{ RemoteException -> 0x0153 }
            boolean r10 = r1.registerVendorCallback_1_1(r10, r15)     // Catch:{ RemoteException -> 0x0153 }
            if (r10 != 0) goto L_0x011c
            java.lang.String r10 = "HostapdHal"
            java.lang.String r11 = "Failed to register Hostapd Vendor callback"
            android.util.Log.e(r10, r11)     // Catch:{ RemoteException -> 0x0153 }
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            r5 = 0
            return r5
        L_0x011c:
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            r5 = 1
            return r5
        L_0x011f:
            goto L_0x014f
        L_0x0120:
            vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor r0 = r1.mIHostapdVendor     // Catch:{ RemoteException -> 0x0153 }
            android.hardware.wifi.hostapd.V1_0.HostapdStatus r0 = r0.addVendorAccessPoint(r8, r12)     // Catch:{ RemoteException -> 0x0153 }
            java.lang.String r10 = "addVendorAccessPoint"
            boolean r10 = r1.checkVendorStatusAndLogFailure(r0, r10)     // Catch:{ RemoteException -> 0x0153 }
            if (r10 == 0) goto L_0x014f
            vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor r10 = r1.mIHostapdVendor     // Catch:{ RemoteException -> 0x0153 }
            if (r10 == 0) goto L_0x014f
            com.android.server.wifi.HostapdHal$HostapdVendorIfaceHalCallback r10 = new com.android.server.wifi.HostapdHal$HostapdVendorIfaceHalCallback     // Catch:{ RemoteException -> 0x0153 }
            r10.<init>(r2, r4)     // Catch:{ RemoteException -> 0x0153 }
            java.lang.String r11 = r9.ifaceName     // Catch:{ RemoteException -> 0x0153 }
            vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor r13 = r1.mIHostapdVendor     // Catch:{ RemoteException -> 0x0153 }
            boolean r11 = r1.registerVendorCallback(r11, r13, r10)     // Catch:{ RemoteException -> 0x0153 }
            if (r11 != 0) goto L_0x014c
            java.lang.String r11 = "HostapdHal"
            java.lang.String r13 = "Failed to register Hostapd Vendor callback"
            android.util.Log.e(r11, r13)     // Catch:{ RemoteException -> 0x0153 }
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            r5 = 0
            return r5
        L_0x014c:
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            r5 = 1
            return r5
        L_0x014f:
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            r5 = 0
            return r5
        L_0x0153:
            r0 = move-exception
            java.lang.String r10 = "addVendorAccessPoint"
            r1.handleRemoteException(r0, r10)     // Catch:{ all -> 0x0178 }
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            r5 = 0
            return r5
        L_0x015c:
            r0 = move-exception
            java.lang.String r10 = "HostapdHal"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0178 }
            r11.<init>()     // Catch:{ all -> 0x0178 }
            java.lang.String r12 = "Unrecognized apBand "
            r11.append(r12)     // Catch:{ all -> 0x0178 }
            int r12 = r3.apBand     // Catch:{ all -> 0x0178 }
            r11.append(r12)     // Catch:{ all -> 0x0178 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0178 }
            android.util.Log.e(r10, r11)     // Catch:{ all -> 0x0178 }
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            r5 = 0
            return r5
        L_0x0178:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0178 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.HostapdHal.addVendorAccessPoint(java.lang.String, android.net.wifi.WifiConfiguration, com.android.server.wifi.WifiNative$SoftApListener):boolean");
    }

    public boolean removeVendorAccessPoint(String ifaceName) {
        synchronized (this.mLock) {
            WifiApConfigStore wifiApConfigStore = WifiInjector.getInstance().getWifiApConfigStore();
            if (!checkHostapdVendorAndLogFailure("removeVendorAccessPoint")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mIHostapdVendor.removeVendorAccessPoint(ifaceName), "removeVendorAccessPoint");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "removeVendorAccessPoint");
                return false;
            }
        }
    }

    public boolean setHostapdParams(String cmd) {
        synchronized (this.mLock) {
            if (!checkHostapdVendorAndLogFailure("setHostapdParams")) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(this.mIHostapdVendor.setHostapdParams(cmd), "setHostapdParams");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setHostapdParams");
                return false;
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor getHostapdVendorMockable() throws RemoteException {
        vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor service;
        synchronized (this.mLock) {
            service = vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor.getService();
        }
        return service;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor getHostapdVendorMockableV1_1() throws RemoteException {
        vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor castFrom;
        synchronized (this.mLock) {
            try {
                castFrom = vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor.castFrom(this.mIHostapdVendor);
            } catch (NoSuchElementException e) {
                Log.e(TAG, "Failed to get IHostapdVendorV1_1", e);
                return null;
            } catch (Throwable th) {
                throw th;
            }
        }
        return castFrom;
    }

    private List<IHostapdVendor.AcsChannelRange> toVendorAcsChannelRanges(String channelListStr) {
        ArrayList<IHostapdVendor.AcsChannelRange> acsChannelRanges = new ArrayList<>();
        for (String channelRange : channelListStr.split(",")) {
            IHostapdVendor.AcsChannelRange acsChannelRange = new IHostapdVendor.AcsChannelRange();
            try {
                if (channelRange.contains("-")) {
                    String[] channels = channelRange.split("-");
                    if (channels.length != 2) {
                        Log.e(TAG, "Unrecognized channel range, length is " + channels.length);
                    } else {
                        int start = Integer.parseInt(channels[0]);
                        int end = Integer.parseInt(channels[1]);
                        if (start > end) {
                            Log.e(TAG, "Invalid channel range, from " + start + " to " + end);
                        } else {
                            acsChannelRange.start = start;
                            acsChannelRange.end = end;
                        }
                    }
                } else {
                    acsChannelRange.start = Integer.parseInt(channelRange);
                    acsChannelRange.end = acsChannelRange.start;
                }
                acsChannelRanges.add(acsChannelRange);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Malformed channel value detected: " + e);
            }
        }
        return acsChannelRanges;
    }

    public void setCountryCode(String countryCode) {
        this.mCountryCode = countryCode;
    }

    public boolean isVendorHostapdHal() {
        return this.mIHostapdVendor != null;
    }

    private boolean checkHostapdVendorAndLogFailure(String methodStr) {
        synchronized (this.mLock) {
            if (this.mIHostapdVendor != null) {
                return true;
            }
            Log.e(TAG, "Can't call " + methodStr + ", IHostapdVendor is null");
            return false;
        }
    }

    private boolean checkVendorStatusAndLogFailure(HostapdStatus status, String methodStr) {
        synchronized (this.mLock) {
            if (status.code != 0) {
                Log.e(TAG, "IHostapdVendor." + methodStr + " failed: " + status.code + ", " + status.debugMessage);
                return false;
            }
            if (this.mVerboseLoggingEnabled) {
                Log.e(TAG, "IHostapdVendor." + methodStr + " succeeded");
            }
            return true;
        }
    }

    private class HostapdVendorIfaceHalCallback extends IHostapdVendorIfaceCallback.Stub {
        private WifiNative.SoftApListener mSoftApListener;

        HostapdVendorIfaceHalCallback(String ifaceName, WifiNative.SoftApListener listener) {
            this.mSoftApListener = listener;
        }

        public void onStaConnected(byte[] bssid) {
            this.mSoftApListener.onStaConnected(NativeUtil.macAddressFromByteArray(bssid));
        }

        public void onStaDisconnected(byte[] bssid) {
            this.mSoftApListener.onStaDisconnected(NativeUtil.macAddressFromByteArray(bssid));
        }
    }

    private boolean registerVendorCallback(String ifaceName, vendor.qti.hardware.wifi.hostapd.V1_0.IHostapdVendor service, IHostapdVendorIfaceCallback callback) {
        synchronized (this.mLock) {
            if (service == null) {
                return false;
            }
            try {
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(service.registerVendorCallback(ifaceName, callback), "registerVendorCallback");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "registerVendorCallback");
                return false;
            }
        }
    }

    private class HostapdVendorIfaceHalCallbackV1_1 extends IHostapdVendorIfaceCallback.Stub {
        private WifiNative.SoftApListener mSoftApListener;

        HostapdVendorIfaceHalCallbackV1_1(String ifaceName, WifiNative.SoftApListener listener) {
            this.mSoftApListener = listener;
        }

        public void onStaConnected(byte[] bssid) {
            this.mSoftApListener.onStaConnected(NativeUtil.macAddressFromByteArray(bssid));
        }

        public void onStaDisconnected(byte[] bssid) {
            this.mSoftApListener.onStaDisconnected(NativeUtil.macAddressFromByteArray(bssid));
        }

        public void onFailure(String ifaceName) {
            Log.w(HostapdHal.TAG, "Failure on iface " + ifaceName);
            this.mSoftApListener.onFailure();
        }
    }

    private boolean registerVendorCallback_1_1(String ifaceName, vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendorIfaceCallback callback) {
        synchronized (this.mLock) {
            try {
                vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor iHostapdVendorV1_1 = getHostapdVendorMockableV1_1();
                if (iHostapdVendorV1_1 == null) {
                    return false;
                }
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(iHostapdVendorV1_1.registerVendorCallback_1_1(ifaceName, callback), "registerVendorCallback_1_1");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "registerVendorCallback_1_1");
                return false;
            }
        }
    }

    public boolean setLogLevel(boolean turnOnVerbose) {
        int logLevel;
        synchronized (this.mLock) {
            if (!isVendorV1_1()) {
                return false;
            }
            if (turnOnVerbose) {
                logLevel = 2;
            } else {
                logLevel = 3;
            }
            boolean debugParams = setDebugParams(logLevel, false, false);
            return debugParams;
        }
    }

    private boolean setDebugParams(int level, boolean showTimestamp, boolean showKeys) {
        synchronized (this.mLock) {
            try {
                vendor.qti.hardware.wifi.hostapd.V1_1.IHostapdVendor iHostapdVendorV1_1 = getHostapdVendorMockableV1_1();
                if (iHostapdVendorV1_1 == null) {
                    return false;
                }
                boolean checkVendorStatusAndLogFailure = checkVendorStatusAndLogFailure(iHostapdVendorV1_1.setDebugParams(level, false, false), "setDebugParams");
                return checkVendorStatusAndLogFailure;
            } catch (RemoteException e) {
                handleRemoteException(e, "setDebugParams");
                return false;
            }
        }
    }
}
