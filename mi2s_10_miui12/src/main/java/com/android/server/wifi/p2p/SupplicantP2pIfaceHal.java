package com.android.server.wifi.p2p;

import android.hardware.wifi.supplicant.V1_0.ISupplicant;
import android.hardware.wifi.supplicant.V1_0.ISupplicantIface;
import android.hardware.wifi.supplicant.V1_0.ISupplicantNetwork;
import android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface;
import android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIfaceCallback;
import android.hardware.wifi.supplicant.V1_0.ISupplicantP2pNetwork;
import android.hardware.wifi.supplicant.V1_0.SupplicantStatus;
import android.hardware.wifi.supplicant.V1_1.ISupplicant;
import android.hidl.manager.V1_0.IServiceManager;
import android.hidl.manager.V1_0.IServiceNotification;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.os.IHwBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.wifi.p2p.SupplicantP2pIfaceHal;
import com.android.server.wifi.util.NativeUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import miui.telephony.phonenumber.Prefix;
import vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendor;
import vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorIface;
import vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorP2PIface;
import vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorP2PIfaceCallback;

public class SupplicantP2pIfaceHal {
    private static final int DEFAULT_GROUP_OWNER_INTENT = 6;
    private static final int DEFAULT_OPERATING_CLASS = 81;
    private static final int RESULT_NOT_VALID = -1;
    private static final String TAG = "SupplicantP2pIfaceHal";
    private static final Pattern WPS_DEVICE_TYPE_PATTERN = Pattern.compile("^(\\d{1,2})-([0-9a-fA-F]{8})-(\\d{1,2})$");
    /* access modifiers changed from: private */
    public static boolean sVerboseLoggingEnabled = true;
    private SupplicantP2pIfaceCallback mCallback = null;
    private ISupplicantIface mHidlSupplicantIface = null;
    private IServiceManager mIServiceManager = null;
    private ISupplicant mISupplicant = null;
    private ISupplicantP2pIface mISupplicantP2pIface = null;
    private ISupplicantVendor mISupplicantVendor;
    private ISupplicantVendorP2PIface mISupplicantVendorP2pIface = null;
    /* access modifiers changed from: private */
    public Object mLock = new Object();
    private final WifiP2pMonitor mMonitor;
    private final IHwBinder.DeathRecipient mServiceManagerDeathRecipient = new IHwBinder.DeathRecipient() {
        public final void serviceDied(long j) {
            SupplicantP2pIfaceHal.this.lambda$new$0$SupplicantP2pIfaceHal(j);
        }
    };
    private final IServiceNotification mServiceNotificationCallback = new IServiceNotification.Stub() {
        public void onRegistration(String fqName, String name, boolean preexisting) {
            synchronized (SupplicantP2pIfaceHal.this.mLock) {
                if (SupplicantP2pIfaceHal.sVerboseLoggingEnabled) {
                    Log.i(SupplicantP2pIfaceHal.TAG, "IServiceNotification.onRegistration for: " + fqName + ", " + name + " preexisting=" + preexisting);
                }
                if (!SupplicantP2pIfaceHal.this.initSupplicantService()) {
                    Log.e(SupplicantP2pIfaceHal.TAG, "initalizing ISupplicant failed.");
                    SupplicantP2pIfaceHal.this.supplicantServiceDiedHandler();
                } else {
                    Log.i(SupplicantP2pIfaceHal.TAG, "Completed initialization of ISupplicant interfaces.");
                }
            }
        }
    };
    private final IHwBinder.DeathRecipient mSupplicantDeathRecipient = new IHwBinder.DeathRecipient() {
        public final void serviceDied(long j) {
            SupplicantP2pIfaceHal.this.lambda$new$1$SupplicantP2pIfaceHal(j);
        }
    };
    private final IHwBinder.DeathRecipient mSupplicantVendorDeathRecipient = new IHwBinder.DeathRecipient() {
        public final void serviceDied(long j) {
            SupplicantP2pIfaceHal.this.lambda$new$2$SupplicantP2pIfaceHal(j);
        }
    };
    private SupplicantVendorP2pIfaceCallback mVendorCallback = null;

    public /* synthetic */ void lambda$new$0$SupplicantP2pIfaceHal(long cookie) {
        Log.w(TAG, "IServiceManager died: cookie=" + cookie);
        synchronized (this.mLock) {
            supplicantServiceDiedHandler();
            this.mIServiceManager = null;
        }
    }

    public /* synthetic */ void lambda$new$1$SupplicantP2pIfaceHal(long cookie) {
        Log.w(TAG, "ISupplicant/ISupplicantStaIface died: cookie=" + cookie);
        synchronized (this.mLock) {
            supplicantServiceDiedHandler();
        }
    }

    public /* synthetic */ void lambda$new$2$SupplicantP2pIfaceHal(long cookie) {
        synchronized (this.mLock) {
            Log.w(TAG, "ISupplicantVendor/ISupplicantVendorP2PIface died: cookie=" + cookie);
            supplicantVendorServiceDiedHandler();
        }
    }

    public SupplicantP2pIfaceHal(WifiP2pMonitor monitor) {
        this.mMonitor = monitor;
    }

    private boolean linkToServiceManagerDeath() {
        IServiceManager iServiceManager = this.mIServiceManager;
        if (iServiceManager == null) {
            return false;
        }
        try {
            if (iServiceManager.linkToDeath(this.mServiceManagerDeathRecipient, 0)) {
                return true;
            }
            Log.wtf(TAG, "Error on linkToDeath on IServiceManager");
            supplicantServiceDiedHandler();
            this.mIServiceManager = null;
            return false;
        } catch (RemoteException e) {
            Log.e(TAG, "IServiceManager.linkToDeath exception", e);
            return false;
        }
    }

    public static void enableVerboseLogging(int verbose) {
        sVerboseLoggingEnabled = verbose > 0;
        SupplicantP2pIfaceCallback.enableVerboseLogging(verbose);
    }

    public boolean initialize() {
        if (sVerboseLoggingEnabled) {
            Log.i(TAG, "Registering ISupplicant service ready callback.");
        }
        synchronized (this.mLock) {
            if (this.mIServiceManager != null) {
                Log.i(TAG, "Supplicant HAL already initialized.");
                return true;
            }
            this.mISupplicant = null;
            this.mISupplicantVendor = null;
            this.mISupplicantP2pIface = null;
            this.mISupplicantVendorP2pIface = null;
            try {
                this.mIServiceManager = getServiceManagerMockable();
                if (this.mIServiceManager == null) {
                    Log.e(TAG, "Failed to get HIDL Service Manager");
                    return false;
                } else if (!linkToServiceManagerDeath()) {
                    return false;
                } else {
                    if (this.mIServiceManager.registerForNotifications(ISupplicant.kInterfaceName, "default", this.mServiceNotificationCallback)) {
                        return true;
                    }
                    Log.e(TAG, "Failed to register for notifications to android.hardware.wifi.supplicant@1.0::ISupplicant");
                    this.mIServiceManager = null;
                    return false;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Exception while trying to register a listener for ISupplicant service: " + e);
                supplicantServiceDiedHandler();
                return false;
            }
        }
    }

    private boolean linkToSupplicantDeath() {
        ISupplicant iSupplicant = this.mISupplicant;
        if (iSupplicant == null) {
            return false;
        }
        try {
            if (iSupplicant.linkToDeath(this.mSupplicantDeathRecipient, 0)) {
                return true;
            }
            Log.wtf(TAG, "Error on linkToDeath on ISupplicant");
            supplicantServiceDiedHandler();
            return false;
        } catch (RemoteException e) {
            Log.e(TAG, "ISupplicant.linkToDeath exception", e);
            return false;
        }
    }

    private boolean linkToSupplicantVendorDeath() {
        synchronized (this.mLock) {
            if (this.mISupplicantVendor == null) {
                return false;
            }
            try {
                if (this.mISupplicantVendor.linkToDeath(this.mSupplicantVendorDeathRecipient, 0)) {
                    return true;
                }
                Log.wtf(TAG, "Error on linkToDeath on ISupplicantVendor");
                supplicantVendorServiceDiedHandler();
                return false;
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantVendor.linkToDeath exception", e);
                return false;
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0025, code lost:
        if (initSupplicantVendorService() != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0027, code lost:
        android.util.Log.e(TAG, "Failed to init SupplicantVendor service");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean initSupplicantService() {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            r1 = 0
            android.hardware.wifi.supplicant.V1_0.ISupplicant r2 = r6.getSupplicantMockable()     // Catch:{ RemoteException -> 0x0032 }
            r6.mISupplicant = r2     // Catch:{ RemoteException -> 0x0032 }
            android.hardware.wifi.supplicant.V1_0.ISupplicant r2 = r6.mISupplicant     // Catch:{ all -> 0x0030 }
            if (r2 != 0) goto L_0x0018
            java.lang.String r2 = "SupplicantP2pIfaceHal"
            java.lang.String r3 = "Got null ISupplicant service. Stopping supplicant HIDL startup"
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x0030 }
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            return r1
        L_0x0018:
            boolean r2 = r6.linkToSupplicantDeath()     // Catch:{ all -> 0x0030 }
            if (r2 != 0) goto L_0x0020
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            return r1
        L_0x0020:
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            boolean r0 = r6.initSupplicantVendorService()
            if (r0 != 0) goto L_0x002e
            java.lang.String r0 = "SupplicantP2pIfaceHal"
            java.lang.String r1 = "Failed to init SupplicantVendor service"
            android.util.Log.e(r0, r1)
        L_0x002e:
            r0 = 1
            return r0
        L_0x0030:
            r1 = move-exception
            goto L_0x004b
        L_0x0032:
            r2 = move-exception
            java.lang.String r3 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0030 }
            r4.<init>()     // Catch:{ all -> 0x0030 }
            java.lang.String r5 = "ISupplicant.getService exception: "
            r4.append(r5)     // Catch:{ all -> 0x0030 }
            r4.append(r2)     // Catch:{ all -> 0x0030 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0030 }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x0030 }
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            return r1
        L_0x004b:
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.SupplicantP2pIfaceHal.initSupplicantService():boolean");
    }

    private boolean initSupplicantVendorService() {
        synchronized (this.mLock) {
            try {
                this.mISupplicantVendor = getSupplicantVendorMockable();
                if (this.mISupplicantVendor != null) {
                    Log.e(TAG, "Discover ISupplicantVendor service successfull");
                }
                if (this.mISupplicantVendor == null) {
                    Log.e(TAG, "Got null ISupplicantVendor service. Stopping supplicantVendor HIDL startup");
                    return false;
                } else if (!linkToSupplicantVendorDeath()) {
                    return false;
                } else {
                    return true;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantVendor.getService exception: " + e);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private boolean linkToSupplicantP2pIfaceDeath() {
        ISupplicantP2pIface iSupplicantP2pIface = this.mISupplicantP2pIface;
        if (iSupplicantP2pIface == null) {
            return false;
        }
        try {
            if (iSupplicantP2pIface.linkToDeath(this.mSupplicantDeathRecipient, 0)) {
                return true;
            }
            Log.wtf(TAG, "Error on linkToDeath on ISupplicantP2pIface");
            supplicantServiceDiedHandler();
            return false;
        } catch (RemoteException e) {
            Log.e(TAG, "ISupplicantP2pIface.linkToDeath exception", e);
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0022, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0024, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0025, code lost:
        android.util.Log.e(TAG, "ISupplicantVendorP2PIface.linkToDeath exception", r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002d, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x002f, code lost:
        throw r1;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:4:0x0006, B:7:0x0008] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean linkToSupplicantVendorP2pIfaceDeath(vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorP2PIface r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            r1 = 0
            if (r6 != 0) goto L_0x0008
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r1
        L_0x0008:
            android.os.IHwBinder$DeathRecipient r2 = r5.mSupplicantVendorDeathRecipient     // Catch:{ RemoteException -> 0x0024 }
            r3 = 0
            boolean r2 = r6.linkToDeath(r2, r3)     // Catch:{ RemoteException -> 0x0024 }
            if (r2 != 0) goto L_0x001e
            java.lang.String r2 = "SupplicantP2pIfaceHal"
            java.lang.String r3 = "Error on linkToDeath on ISupplicantVendorP2PIface"
            android.util.Log.wtf(r2, r3)     // Catch:{ RemoteException -> 0x0024 }
            r5.supplicantVendorServiceDiedHandler()     // Catch:{ RemoteException -> 0x0024 }
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r1
        L_0x001e:
            r1 = 1
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r1
        L_0x0022:
            r1 = move-exception
            goto L_0x002e
        L_0x0024:
            r2 = move-exception
            java.lang.String r3 = "SupplicantP2pIfaceHal"
            java.lang.String r4 = "ISupplicantVendorP2PIface.linkToDeath exception"
            android.util.Log.e(r3, r4, r2)     // Catch:{ all -> 0x0022 }
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r1
        L_0x002e:
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.SupplicantP2pIfaceHal.linkToSupplicantVendorP2pIfaceDeath(vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorP2PIface):boolean");
    }

    public boolean setupIface(String ifaceName) {
        ISupplicantIface ifaceHwBinder;
        synchronized (this.mLock) {
            if (this.mISupplicantP2pIface != null) {
                return false;
            }
            if (isV1_1()) {
                ifaceHwBinder = addIfaceV1_1(ifaceName);
            } else {
                ifaceHwBinder = getIfaceV1_0(ifaceName);
            }
            if (ifaceHwBinder == null) {
                Log.e(TAG, "initSupplicantP2pIface got null iface");
                return false;
            }
            this.mISupplicantP2pIface = getP2pIfaceMockable(ifaceHwBinder);
            if (!linkToSupplicantP2pIfaceDeath()) {
                return false;
            }
            if (!(this.mISupplicantP2pIface == null || this.mMonitor == null)) {
                this.mCallback = new SupplicantP2pIfaceCallback(ifaceName, this.mMonitor);
                if (!registerCallback(this.mCallback)) {
                    Log.e(TAG, "Callback registration failed. Initialization incomplete.");
                    return false;
                }
            }
            if (!vendor_setupIface(ifaceName)) {
                Log.e(TAG, "Failed to create vendor setupiface");
            }
            return true;
        }
    }

    public boolean vendor_setupIface(String ifaceName) {
        WifiP2pMonitor wifiP2pMonitor;
        if (checkSupplicantVendorP2pIfaceAndLogFailure("vendor_setupIface")) {
            Log.e(TAG, "Already created vendor setupinterface");
            return true;
        }
        ISupplicantVendorIface Vendor_ifaceHwBinder = null;
        if (isVendor_2_0()) {
            Log.e(TAG, "Try to get Vendor HIDL@2.0 interface");
            Vendor_ifaceHwBinder = getVendorIfaceV2_0(ifaceName);
        }
        if (Vendor_ifaceHwBinder == null) {
            Log.e(TAG, "Failed to get vendor iface binder");
            return false;
        }
        this.mISupplicantVendorP2pIface = getVendorP2pIfaceMockable(Vendor_ifaceHwBinder);
        if (this.mISupplicantVendorP2pIface == null) {
            Log.e(TAG, "Failed to get ISupplicantVendorP2PIface proxy");
            return false;
        }
        Log.e(TAG, "Successful get Vendor p2p interface");
        if (!linkToSupplicantVendorP2pIfaceDeath(this.mISupplicantVendorP2pIface)) {
            return false;
        }
        if (!(this.mISupplicantVendorP2pIface == null || (wifiP2pMonitor = this.mMonitor) == null)) {
            this.mVendorCallback = new SupplicantVendorP2pIfaceCallback(ifaceName, wifiP2pMonitor);
            if (!registerVendorCallback(this.mVendorCallback)) {
                Log.e(TAG, "Callback registration failed. Initialization incomplete.");
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x005b, code lost:
        if (checkSupplicantVendorAndLogFailure("getVendorInterface") != false) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005e, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r9.mISupplicantVendor.getVendorInterface(r5, new com.android.server.wifi.p2p.$$Lambda$SupplicantP2pIfaceHal$MdaAIgUkYZlqiOm5NAp8i_QQXY(r9, r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x006a, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        android.util.Log.e(TAG, "ISupplicantVendor.getInterface exception: " + r4);
        supplicantVendorServiceDiedHandler();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0085, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorIface getVendorIfaceV2_0(java.lang.String r10) {
        /*
            r9 = this;
            java.lang.Object r0 = r9.mLock
            monitor-enter(r0)
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x00ab }
            r1.<init>()     // Catch:{ all -> 0x00ab }
            r2 = 0
            java.lang.String r3 = "listVendorInterfaces"
            java.lang.String r4 = "listVendorInterfaces"
            boolean r4 = r9.checkSupplicantVendorAndLogFailure(r4)     // Catch:{ RemoteException -> 0x008f }
            if (r4 != 0) goto L_0x0015
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return r2
        L_0x0015:
            vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendor r4 = r9.mISupplicantVendor     // Catch:{ RemoteException -> 0x008f }
            com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$_f6b29tHTaNi_qISRyWyT6Bl0SE r5 = new com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$_f6b29tHTaNi_qISRyWyT6Bl0SE     // Catch:{ RemoteException -> 0x008f }
            r5.<init>(r1)     // Catch:{ RemoteException -> 0x008f }
            r4.listVendorInterfaces(r5)     // Catch:{ RemoteException -> 0x008f }
            int r3 = r1.size()     // Catch:{ all -> 0x00ab }
            if (r3 != 0) goto L_0x002f
            java.lang.String r3 = "SupplicantP2pIfaceHal"
            java.lang.String r4 = "Got zero HIDL supplicant vendor ifaces. Stopping supplicant vendor HIDL startup."
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x00ab }
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return r2
        L_0x002f:
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r3 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ all -> 0x00ab }
            java.lang.String r4 = "getVendorInterface"
            r3.<init>(r4)     // Catch:{ all -> 0x00ab }
            java.util.Iterator r4 = r1.iterator()     // Catch:{ all -> 0x00ab }
        L_0x003a:
            boolean r5 = r4.hasNext()     // Catch:{ all -> 0x00ab }
            if (r5 == 0) goto L_0x0087
            java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x00ab }
            android.hardware.wifi.supplicant.V1_0.ISupplicant$IfaceInfo r5 = (android.hardware.wifi.supplicant.V1_0.ISupplicant.IfaceInfo) r5     // Catch:{ all -> 0x00ab }
            int r6 = r5.type     // Catch:{ all -> 0x00ab }
            r7 = 1
            if (r6 != r7) goto L_0x0086
            java.lang.String r6 = r5.name     // Catch:{ all -> 0x00ab }
            boolean r6 = r10.equals(r6)     // Catch:{ all -> 0x00ab }
            if (r6 == 0) goto L_0x0086
            java.lang.String r4 = "getVendorInterface"
            java.lang.String r6 = "getVendorInterface"
            boolean r6 = r9.checkSupplicantVendorAndLogFailure(r6)     // Catch:{ RemoteException -> 0x006a }
            if (r6 != 0) goto L_0x005f
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return r2
        L_0x005f:
            vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendor r6 = r9.mISupplicantVendor     // Catch:{ RemoteException -> 0x006a }
            com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$MdaA-IgUkYZlqiOm5NAp8i_QQXY r7 = new com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$MdaA-IgUkYZlqiOm5NAp8i_QQXY     // Catch:{ RemoteException -> 0x006a }
            r7.<init>(r3)     // Catch:{ RemoteException -> 0x006a }
            r6.getVendorInterface(r5, r7)     // Catch:{ RemoteException -> 0x006a }
            goto L_0x0087
        L_0x006a:
            r4 = move-exception
            java.lang.String r6 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r7.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r8 = "ISupplicantVendor.getInterface exception: "
            r7.append(r8)     // Catch:{ all -> 0x00ab }
            r7.append(r4)     // Catch:{ all -> 0x00ab }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00ab }
            android.util.Log.e(r6, r7)     // Catch:{ all -> 0x00ab }
            r9.supplicantVendorServiceDiedHandler()     // Catch:{ all -> 0x00ab }
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return r2
        L_0x0086:
            goto L_0x003a
        L_0x0087:
            java.lang.Object r2 = r3.getResult()     // Catch:{ all -> 0x00ab }
            vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorIface r2 = (vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorIface) r2     // Catch:{ all -> 0x00ab }
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return r2
        L_0x008f:
            r3 = move-exception
            java.lang.String r4 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r5.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = "ISupplicantVendor.listInterfaces exception: "
            r5.append(r6)     // Catch:{ all -> 0x00ab }
            r5.append(r3)     // Catch:{ all -> 0x00ab }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00ab }
            android.util.Log.e(r4, r5)     // Catch:{ all -> 0x00ab }
            r9.supplicantVendorServiceDiedHandler()     // Catch:{ all -> 0x00ab }
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return r2
        L_0x00ab:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.SupplicantP2pIfaceHal.getVendorIfaceV2_0(java.lang.String):vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorIface");
    }

    public /* synthetic */ void lambda$getVendorIfaceV2_0$3$SupplicantP2pIfaceHal(ArrayList supplicantIfaces, SupplicantStatus status, ArrayList ifaces) {
        if (checkSupplicantVendorStatusAndLogFailure(status, "listVendorInterfaces")) {
            supplicantIfaces.addAll(ifaces);
        }
    }

    public /* synthetic */ void lambda$getVendorIfaceV2_0$4$SupplicantP2pIfaceHal(SupplicantResult supplicantVendorIface, SupplicantStatus status, ISupplicantVendorIface iface) {
        if (checkSupplicantVendorStatusAndLogFailure(status, "getVendorInterface")) {
            supplicantVendorIface.setResult(status, iface);
        }
    }

    private ISupplicantIface getIfaceV1_0(String ifaceName) {
        ArrayList<ISupplicant.IfaceInfo> supplicantIfaces = new ArrayList<>();
        try {
            this.mISupplicant.listInterfaces(new ISupplicant.listInterfacesCallback(supplicantIfaces) {
                private final /* synthetic */ ArrayList f$0;

                {
                    this.f$0 = r1;
                }

                public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                    SupplicantP2pIfaceHal.lambda$getIfaceV1_0$5(this.f$0, supplicantStatus, arrayList);
                }
            });
            if (supplicantIfaces.size() == 0) {
                Log.e(TAG, "Got zero HIDL supplicant ifaces. Stopping supplicant HIDL startup.");
                supplicantServiceDiedHandler();
                return null;
            }
            SupplicantResult<ISupplicantIface> supplicantIface = new SupplicantResult<>("getInterface()");
            Iterator<ISupplicant.IfaceInfo> it = supplicantIfaces.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ISupplicant.IfaceInfo ifaceInfo = it.next();
                if (ifaceInfo.type == 1 && ifaceName.equals(ifaceInfo.name)) {
                    try {
                        this.mISupplicant.getInterface(ifaceInfo, new ISupplicant.getInterfaceCallback() {
                            public final void onValues(SupplicantStatus supplicantStatus, ISupplicantIface iSupplicantIface) {
                                SupplicantP2pIfaceHal.lambda$getIfaceV1_0$6(SupplicantP2pIfaceHal.SupplicantResult.this, supplicantStatus, iSupplicantIface);
                            }
                        });
                        break;
                    } catch (RemoteException e) {
                        Log.e(TAG, "ISupplicant.getInterface exception: " + e);
                        supplicantServiceDiedHandler();
                        return null;
                    }
                }
            }
            return supplicantIface.getResult();
        } catch (RemoteException e2) {
            Log.e(TAG, "ISupplicant.listInterfaces exception: " + e2);
            return null;
        }
    }

    static /* synthetic */ void lambda$getIfaceV1_0$5(ArrayList supplicantIfaces, SupplicantStatus status, ArrayList ifaces) {
        if (status.code != 0) {
            Log.e(TAG, "Getting Supplicant Interfaces failed: " + status.code);
            return;
        }
        supplicantIfaces.addAll(ifaces);
    }

    static /* synthetic */ void lambda$getIfaceV1_0$6(SupplicantResult supplicantIface, SupplicantStatus status, ISupplicantIface iface) {
        if (status.code != 0) {
            Log.e(TAG, "Failed to get ISupplicantIface " + status.code);
            return;
        }
        supplicantIface.setResult(status, iface);
    }

    private ISupplicantIface addIfaceV1_1(String ifaceName) {
        synchronized (this.mLock) {
            ISupplicant.IfaceInfo ifaceInfo = new ISupplicant.IfaceInfo();
            ifaceInfo.name = ifaceName;
            ifaceInfo.type = 1;
            SupplicantResult<ISupplicantIface> supplicantIface = new SupplicantResult<>("addInterface(" + ifaceInfo + ")");
            try {
                android.hardware.wifi.supplicant.V1_1.ISupplicant supplicant_v1_1 = getSupplicantMockableV1_1();
                if (supplicant_v1_1 == null) {
                    Log.e(TAG, "Can't call addIface: ISupplicantP2pIface is null");
                    return null;
                }
                supplicant_v1_1.addInterface(ifaceInfo, new ISupplicant.addInterfaceCallback() {
                    public final void onValues(SupplicantStatus supplicantStatus, ISupplicantIface iSupplicantIface) {
                        SupplicantP2pIfaceHal.lambda$addIfaceV1_1$7(SupplicantP2pIfaceHal.SupplicantResult.this, supplicantStatus, iSupplicantIface);
                    }
                });
                ISupplicantIface result = supplicantIface.getResult();
                return result;
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicant.addInterface exception: " + e);
                supplicantServiceDiedHandler();
                return null;
            }
        }
    }

    static /* synthetic */ void lambda$addIfaceV1_1$7(SupplicantResult supplicantIface, SupplicantStatus status, ISupplicantIface iface) {
        if (status.code == 0 || status.code == 5) {
            supplicantIface.setResult(status, iface);
            return;
        }
        Log.e(TAG, "Failed to get ISupplicantIface " + status.code);
    }

    public boolean teardownIface(String ifaceName) {
        synchronized (this.mLock) {
            if (this.mISupplicantP2pIface == null) {
                return false;
            }
            if (isV1_1() && !removeIfaceV1_1(ifaceName)) {
                Log.e(TAG, "Failed to remove iface = " + ifaceName);
                return false;
            } else if (this.mISupplicantVendorP2pIface == null) {
                Log.e(TAG, "Trying to teardown unknown vendor interface");
                return false;
            } else {
                this.mISupplicantVendorP2pIface = null;
                return true;
            }
        }
    }

    private boolean removeIfaceV1_1(String ifaceName) {
        synchronized (this.mLock) {
            try {
                android.hardware.wifi.supplicant.V1_1.ISupplicant supplicant_v1_1 = getSupplicantMockableV1_1();
                if (supplicant_v1_1 == null) {
                    Log.e(TAG, "Can't call removeIface: ISupplicantP2pIface is null");
                    return false;
                }
                ISupplicant.IfaceInfo ifaceInfo = new ISupplicant.IfaceInfo();
                ifaceInfo.name = ifaceName;
                ifaceInfo.type = 1;
                SupplicantStatus status = supplicant_v1_1.removeInterface(ifaceInfo);
                if (status.code != 0) {
                    Log.e(TAG, "Failed to remove iface " + status.code);
                    return false;
                }
                this.mCallback = null;
                this.mISupplicantP2pIface = null;
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicant.removeInterface exception: " + e);
                supplicantServiceDiedHandler();
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void supplicantVendorServiceDiedHandler() {
        synchronized (this.mLock) {
            this.mISupplicantVendor = null;
            this.mISupplicantVendorP2pIface = null;
        }
    }

    /* access modifiers changed from: private */
    public void supplicantServiceDiedHandler() {
        synchronized (this.mLock) {
            this.mISupplicant = null;
            this.mISupplicantVendor = null;
            this.mISupplicantP2pIface = null;
            this.mISupplicantVendorP2pIface = null;
        }
    }

    public boolean isInitializationStarted() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIServiceManager != null;
        }
        return z;
    }

    public boolean isInitializationComplete() {
        return this.mISupplicant != null;
    }

    /* access modifiers changed from: protected */
    public IServiceManager getServiceManagerMockable() throws RemoteException {
        return IServiceManager.getService();
    }

    /* access modifiers changed from: protected */
    public android.hardware.wifi.supplicant.V1_0.ISupplicant getSupplicantMockable() throws RemoteException {
        try {
            return android.hardware.wifi.supplicant.V1_0.ISupplicant.getService();
        } catch (NoSuchElementException e) {
            Log.e(TAG, "Failed to get ISupplicant", e);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public ISupplicantVendor getSupplicantVendorMockable() throws RemoteException {
        ISupplicantVendor service;
        synchronized (this.mLock) {
            try {
                service = ISupplicantVendor.getService();
            } catch (NoSuchElementException e) {
                Log.e(TAG, "Failed to get ISupplicant", e);
                return null;
            } catch (Throwable th) {
                throw th;
            }
        }
        return service;
    }

    /* access modifiers changed from: protected */
    public android.hardware.wifi.supplicant.V1_1.ISupplicant getSupplicantMockableV1_1() throws RemoteException {
        android.hardware.wifi.supplicant.V1_1.ISupplicant castFrom;
        synchronized (this.mLock) {
            try {
                castFrom = android.hardware.wifi.supplicant.V1_1.ISupplicant.castFrom(android.hardware.wifi.supplicant.V1_0.ISupplicant.getService());
            } catch (NoSuchElementException e) {
                Log.e(TAG, "Failed to get ISupplicant", e);
                return null;
            } catch (Throwable th) {
                throw th;
            }
        }
        return castFrom;
    }

    /* access modifiers changed from: protected */
    public ISupplicantP2pIface getP2pIfaceMockable(ISupplicantIface iface) {
        return ISupplicantP2pIface.asInterface(iface.asBinder());
    }

    /* access modifiers changed from: protected */
    public android.hardware.wifi.supplicant.V1_2.ISupplicantP2pIface getP2pIfaceMockableV1_2() {
        ISupplicantP2pIface iSupplicantP2pIface = this.mISupplicantP2pIface;
        if (iSupplicantP2pIface == null) {
            return null;
        }
        return android.hardware.wifi.supplicant.V1_2.ISupplicantP2pIface.castFrom(iSupplicantP2pIface);
    }

    /* access modifiers changed from: protected */
    public ISupplicantP2pNetwork getP2pNetworkMockable(ISupplicantNetwork network) {
        return ISupplicantP2pNetwork.asInterface(network.asBinder());
    }

    /* access modifiers changed from: protected */
    public ISupplicantVendorP2PIface getVendorP2pIfaceMockable(ISupplicantVendorIface iface) {
        return ISupplicantVendorP2PIface.asInterface(iface.asBinder());
    }

    private boolean isV1_1() {
        boolean z;
        synchronized (this.mLock) {
            z = false;
            try {
                if (getSupplicantMockableV1_1() != null) {
                    z = true;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicant.getService exception: " + e);
                supplicantServiceDiedHandler();
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
        return z;
    }

    private boolean isVendor_2_0() {
        boolean z;
        synchronized (this.mLock) {
            z = false;
            try {
                if (getSupplicantVendorMockable() != null) {
                    z = true;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantVendor.getService exception: " + e);
                supplicantServiceDiedHandler();
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
        return z;
    }

    private boolean checkSupplicantVendorAndLogFailure(String methodStr) {
        synchronized (this.mLock) {
            if (this.mISupplicantVendor != null) {
                return true;
            }
            Log.e(TAG, "Can't call " + methodStr + ", ISupplicantVendor is null");
            return false;
        }
    }

    private boolean checkSupplicantVendorP2pIfaceAndLogFailure(String methodStr) {
        synchronized (this.mLock) {
            if (this.mISupplicantVendorP2pIface != null) {
                return true;
            }
            Log.e(TAG, "Can't call " + methodStr + ", ISupplicantVendorP2PIface is null");
            return false;
        }
    }

    private boolean checkSupplicantVendorStatusAndLogFailure(SupplicantStatus status, String methodStr) {
        synchronized (this.mLock) {
            if (status.code != 0) {
                Log.e(TAG, "ISupplicantVendor." + methodStr + " failed: " + status);
                return false;
            }
            if (sVerboseLoggingEnabled) {
                Log.d(TAG, "ISupplicantVendor." + methodStr + " succeeded");
            }
            return true;
        }
    }

    private boolean checkVendorStatusAndLogFailure(SupplicantStatus status, String methodStr) {
        synchronized (this.mLock) {
            if (status.code != 0) {
                Log.e(TAG, "ISupplicantVendorP2pIface." + methodStr + " failed: " + status);
                return false;
            }
            if (sVerboseLoggingEnabled) {
                Log.d(TAG, "ISupplicantVendorP2PIface." + methodStr + " succeeded");
            }
            return true;
        }
    }

    protected static void logd(String s) {
        if (sVerboseLoggingEnabled) {
            Log.d(TAG, s);
        }
    }

    protected static void logCompletion(String operation, SupplicantStatus status) {
        if (status == null) {
            Log.w(TAG, operation + " failed: no status code returned.");
        } else if (status.code == 0) {
            logd(operation + " completed successfully.");
        } else {
            Log.w(TAG, operation + " failed: " + status.code + " (" + status.debugMessage + ")");
        }
    }

    private boolean checkSupplicantP2pIfaceAndLogFailure(String method) {
        if (this.mISupplicantP2pIface != null) {
            return true;
        }
        Log.e(TAG, "Can't call " + method + ": ISupplicantP2pIface is null");
        return false;
    }

    private boolean checkSupplicantP2pIfaceAndLogFailureV1_2(String method) {
        if (getP2pIfaceMockableV1_2() != null) {
            return true;
        }
        Log.e(TAG, "Can't call " + method + ": ISupplicantP2pIface is null");
        return false;
    }

    private int wpsInfoToConfigMethod(int info) {
        if (info == 0) {
            return 0;
        }
        if (info == 1) {
            return 1;
        }
        if (info == 2 || info == 3) {
            return 2;
        }
        Log.e(TAG, "Unsupported WPS provision method: " + info);
        return -1;
    }

    public String getName() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("getName")) {
                return null;
            }
            SupplicantResult<String> result = new SupplicantResult<>("getName()");
            try {
                this.mISupplicantP2pIface.getName(new ISupplicantIface.getNameCallback() {
                    public final void onValues(SupplicantStatus supplicantStatus, String str) {
                        SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, str);
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            String result2 = result.getResult();
            return result2;
        }
    }

    public boolean registerCallback(ISupplicantP2pIfaceCallback receiver) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("registerCallback")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("registerCallback()");
            try {
                result.setResult(this.mISupplicantP2pIface.registerCallback(receiver));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean registerVendorCallback(ISupplicantVendorP2PIfaceCallback receiver) {
        synchronized (this.mLock) {
            if (!checkSupplicantVendorP2pIfaceAndLogFailure("registerVendorCallback")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("registerVendorCallback()");
            try {
                result.setResult(this.mISupplicantVendorP2pIface.registerVendorCallback(receiver));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantVendorP2pIface exception: " + e);
                supplicantVendorServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean find(int timeout) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("find")) {
                return false;
            }
            if (timeout < 0) {
                Log.e(TAG, "Invalid timeout value: " + timeout);
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("find(" + timeout + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.find(timeout));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean stopFind() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("stopFind")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("stopFind()");
            try {
                result.setResult(this.mISupplicantP2pIface.stopFind());
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean flush() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("flush")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("flush()");
            try {
                result.setResult(this.mISupplicantP2pIface.flush());
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean serviceFlush() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("serviceFlush")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("serviceFlush()");
            try {
                result.setResult(this.mISupplicantP2pIface.flushServices());
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean setPowerSave(String groupIfName, boolean enable) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setPowerSave")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("setPowerSave(" + groupIfName + ", " + enable + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.setPowerSave(groupIfName, enable));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean setGroupIdle(String groupIfName, int timeoutInSec) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setGroupIdle")) {
                return false;
            }
            if (timeoutInSec < 0) {
                Log.e(TAG, "Invalid group timeout value " + timeoutInSec);
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("setGroupIdle(" + groupIfName + ", " + timeoutInSec + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.setGroupIdle(groupIfName, timeoutInSec));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean setSsidPostfix(String postfix) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setSsidPostfix")) {
                return false;
            }
            if (postfix == null) {
                Log.e(TAG, "Invalid SSID postfix value (null).");
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("setSsidPostfix(" + postfix + ")");
            try {
                ISupplicantP2pIface iSupplicantP2pIface = this.mISupplicantP2pIface;
                result.setResult(iSupplicantP2pIface.setSsidPostfix(NativeUtil.decodeSsid("\"" + postfix + "\"")));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            } catch (IllegalArgumentException e2) {
                Log.e(TAG, "Could not decode SSID.", e2);
                return false;
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public String connect(WifiP2pConfig config, boolean joinExistingGroup) {
        if (config == null) {
            return null;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setSsidPostfix")) {
                return null;
            }
            if (config.deviceAddress == null) {
                Log.e(TAG, "Could not parse null mac address.");
                return null;
            } else if (config.wps.setup != 0 || TextUtils.isEmpty(config.wps.pin)) {
                try {
                    byte[] peerAddress = NativeUtil.macAddressToByteArray(config.deviceAddress);
                    int provisionMethod = wpsInfoToConfigMethod(config.wps.setup);
                    if (provisionMethod == -1) {
                        Log.e(TAG, "Invalid WPS config method: " + config.wps.setup);
                        return null;
                    }
                    String preSelectedPin = TextUtils.isEmpty(config.wps.pin) ? Prefix.EMPTY : config.wps.pin;
                    boolean persistent = config.netId == -2;
                    int goIntent = 0;
                    if (!joinExistingGroup) {
                        int groupOwnerIntent = config.groupOwnerIntent;
                        if (groupOwnerIntent < 0 || groupOwnerIntent > 15) {
                            groupOwnerIntent = 6;
                        }
                        goIntent = groupOwnerIntent;
                    }
                    SupplicantResult<String> result = new SupplicantResult<>("connect(" + config.deviceAddress + ")");
                    try {
                        this.mISupplicantP2pIface.connect(peerAddress, provisionMethod, preSelectedPin, joinExistingGroup, persistent, goIntent, new ISupplicantP2pIface.connectCallback() {
                            public final void onValues(SupplicantStatus supplicantStatus, String str) {
                                SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, str);
                            }
                        });
                    } catch (RemoteException e) {
                        Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                        supplicantServiceDiedHandler();
                    }
                    String result2 = result.getResult();
                    return result2;
                } catch (Exception e2) {
                    Log.e(TAG, "Could not parse peer mac address.", e2);
                    return null;
                }
            } else {
                Log.e(TAG, "Expected empty pin for PBC.");
                return null;
            }
        }
    }

    public boolean cancelConnect() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("cancelConnect")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("cancelConnect()");
            try {
                result.setResult(this.mISupplicantP2pIface.cancelConnect());
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean provisionDiscovery(WifiP2pConfig config) {
        if (config == null) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("provisionDiscovery")) {
                return false;
            }
            int targetMethod = wpsInfoToConfigMethod(config.wps.setup);
            if (targetMethod == -1) {
                Log.e(TAG, "Unrecognized WPS configuration method: " + config.wps.setup);
                return false;
            }
            if (targetMethod == 1) {
                targetMethod = 2;
            } else if (targetMethod == 2) {
                targetMethod = 1;
            }
            if (config.deviceAddress == null) {
                Log.e(TAG, "Cannot parse null mac address.");
                return false;
            }
            try {
                byte[] macAddress = NativeUtil.macAddressToByteArray(config.deviceAddress);
                SupplicantResult<Void> result = new SupplicantResult<>("provisionDiscovery(" + config.deviceAddress + ", " + config.wps.setup + ")");
                try {
                    result.setResult(this.mISupplicantP2pIface.provisionDiscovery(macAddress, targetMethod));
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                boolean isSuccess = result.isSuccess();
                return isSuccess;
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse peer mac address.", e2);
                return false;
            }
        }
    }

    public boolean invite(WifiP2pGroup group, String peerAddress) {
        if (TextUtils.isEmpty(peerAddress)) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("invite")) {
                return false;
            }
            if (group == null) {
                Log.e(TAG, "Cannot invite to null group.");
                return false;
            } else if (group.getOwner() == null) {
                Log.e(TAG, "Cannot invite to group with null owner.");
                return false;
            } else if (group.getOwner().deviceAddress == null) {
                Log.e(TAG, "Group owner has no mac address.");
                return false;
            } else {
                try {
                    byte[] ownerMacAddress = NativeUtil.macAddressToByteArray(group.getOwner().deviceAddress);
                    if (peerAddress == null) {
                        Log.e(TAG, "Cannot parse peer mac address.");
                        return false;
                    }
                    try {
                        byte[] peerMacAddress = NativeUtil.macAddressToByteArray(peerAddress);
                        SupplicantResult<Void> result = new SupplicantResult<>("invite(" + group.getInterface() + ", " + group.getOwner().deviceAddress + ", " + peerAddress + ")");
                        try {
                            result.setResult(this.mISupplicantP2pIface.invite(group.getInterface(), ownerMacAddress, peerMacAddress));
                        } catch (RemoteException e) {
                            Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                            supplicantServiceDiedHandler();
                        }
                        boolean isSuccess = result.isSuccess();
                        return isSuccess;
                    } catch (Exception e2) {
                        Log.e(TAG, "Peer mac address parse error.", e2);
                        return false;
                    }
                } catch (Exception e3) {
                    Log.e(TAG, "Group owner mac address parse error.", e3);
                    return false;
                }
            }
        }
    }

    public boolean reject(String peerAddress) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("reject")) {
                return false;
            }
            if (peerAddress == null) {
                Log.e(TAG, "Cannot parse rejected peer's mac address.");
                return false;
            }
            try {
                byte[] macAddress = NativeUtil.macAddressToByteArray(peerAddress);
                SupplicantResult<Void> result = new SupplicantResult<>("reject(" + peerAddress + ")");
                try {
                    result.setResult(this.mISupplicantP2pIface.reject(macAddress));
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                boolean isSuccess = result.isSuccess();
                return isSuccess;
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse peer mac address.", e2);
                return false;
            }
        }
    }

    public String getDeviceAddress() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("getDeviceAddress")) {
                return null;
            }
            SupplicantResult<String> result = new SupplicantResult<>("getDeviceAddress()");
            try {
                this.mISupplicantP2pIface.getDeviceAddress(new ISupplicantP2pIface.getDeviceAddressCallback() {
                    public final void onValues(SupplicantStatus supplicantStatus, byte[] bArr) {
                        SupplicantP2pIfaceHal.lambda$getDeviceAddress$10(SupplicantP2pIfaceHal.SupplicantResult.this, supplicantStatus, bArr);
                    }
                });
                String result2 = result.getResult();
                return result2;
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
                return null;
            }
        }
    }

    static /* synthetic */ void lambda$getDeviceAddress$10(SupplicantResult result, SupplicantStatus status, byte[] address) {
        String parsedAddress = null;
        try {
            parsedAddress = NativeUtil.macAddressFromByteArray(address);
        } catch (Exception e) {
            Log.e(TAG, "Could not process reported address.", e);
        }
        result.setResult(status, parsedAddress);
    }

    public String getSsid(String address) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("getSsid")) {
                return null;
            }
            if (address == null) {
                Log.e(TAG, "Cannot parse peer mac address.");
                return null;
            }
            try {
                byte[] macAddress = NativeUtil.macAddressToByteArray(address);
                SupplicantResult<String> result = new SupplicantResult<>("getSsid(" + address + ")");
                try {
                    this.mISupplicantP2pIface.getSsid(macAddress, new ISupplicantP2pIface.getSsidCallback() {
                        public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                            SupplicantP2pIfaceHal.lambda$getSsid$11(SupplicantP2pIfaceHal.SupplicantResult.this, supplicantStatus, arrayList);
                        }
                    });
                    String result2 = result.getResult();
                    return result2;
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                    return null;
                }
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse mac address.", e2);
                return null;
            }
        }
    }

    static /* synthetic */ void lambda$getSsid$11(SupplicantResult result, SupplicantStatus status, ArrayList ssid) {
        String ssidString = null;
        if (ssid != null) {
            try {
                ssidString = NativeUtil.removeEnclosingQuotes(NativeUtil.encodeSsid(ssid));
            } catch (Exception e) {
                Log.e(TAG, "Could not encode SSID.", e);
            }
        }
        result.setResult(status, ssidString);
    }

    public boolean reinvoke(int networkId, String peerAddress) {
        if (TextUtils.isEmpty(peerAddress) || networkId < 0) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("reinvoke")) {
                return false;
            }
            if (peerAddress == null) {
                Log.e(TAG, "Cannot parse peer mac address.");
                return false;
            }
            try {
                byte[] macAddress = NativeUtil.macAddressToByteArray(peerAddress);
                SupplicantResult<Void> result = new SupplicantResult<>("reinvoke(" + networkId + ", " + peerAddress + ")");
                try {
                    result.setResult(this.mISupplicantP2pIface.reinvoke(networkId, macAddress));
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                boolean isSuccess = result.isSuccess();
                return isSuccess;
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse mac address.", e2);
                return false;
            }
        }
    }

    public boolean groupAdd(int networkId, boolean isPersistent) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("groupAdd")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("groupAdd(" + networkId + ", " + isPersistent + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.addGroup(isPersistent, networkId));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean groupAdd(String networkName, String passphrase, boolean isPersistent, int freq, String peerAddress, boolean join) {
        byte[] macAddress;
        android.hardware.wifi.supplicant.V1_2.ISupplicantP2pIface ifaceV12;
        StringBuilder sb;
        SupplicantResult supplicantResult;
        String str = networkName;
        synchronized (this.mLock) {
            try {
                if (!checkSupplicantP2pIfaceAndLogFailureV1_2("groupAdd_1_2")) {
                    return false;
                }
                ArrayList<Byte> ssid = NativeUtil.decodeSsid("\"" + str + "\"");
                try {
                    macAddress = NativeUtil.macAddressToByteArray(peerAddress);
                    ifaceV12 = getP2pIfaceMockableV1_2();
                    sb = new StringBuilder();
                    sb.append("groupAdd(");
                    sb.append(str);
                    sb.append(", ");
                    sb.append(TextUtils.isEmpty(passphrase) ? "<Empty>" : "<Non-Empty>");
                    sb.append(", ");
                } catch (Exception e) {
                    boolean z = isPersistent;
                    int i = freq;
                    String str2 = peerAddress;
                    boolean z2 = join;
                    Log.e(TAG, "Could not parse mac address.", e);
                    return false;
                }
                try {
                    sb.append(isPersistent);
                    sb.append(", ");
                    try {
                        sb.append(freq);
                        sb.append(", ");
                        try {
                            sb.append(peerAddress);
                            sb.append(", ");
                            try {
                                sb.append(join);
                                sb.append(")");
                                supplicantResult = new SupplicantResult(sb.toString());
                                supplicantResult.setResult(ifaceV12.addGroup_1_2(ssid, passphrase, isPersistent, freq, macAddress, join));
                            } catch (RemoteException e2) {
                                Log.e(TAG, "ISupplicantP2pIface exception: " + e2);
                                supplicantServiceDiedHandler();
                            } catch (Throwable th) {
                                e = th;
                                throw e;
                            }
                            boolean isSuccess = supplicantResult.isSuccess();
                            return isSuccess;
                        } catch (Throwable th2) {
                            e = th2;
                            boolean z3 = join;
                            throw e;
                        }
                    } catch (Throwable th3) {
                        e = th3;
                        String str3 = peerAddress;
                        boolean z32 = join;
                        throw e;
                    }
                } catch (Throwable th4) {
                    e = th4;
                    int i2 = freq;
                    String str32 = peerAddress;
                    boolean z322 = join;
                    throw e;
                }
            } catch (Throwable th5) {
                e = th5;
                boolean z4 = isPersistent;
                int i22 = freq;
                String str322 = peerAddress;
                boolean z3222 = join;
                throw e;
            }
        }
    }

    public boolean groupAdd(boolean isPersistent) {
        return groupAdd(-1, isPersistent);
    }

    public boolean groupRemove(String groupName) {
        if (TextUtils.isEmpty(groupName)) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("groupRemove")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("groupRemove(" + groupName + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.removeGroup(groupName));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public int getGroupCapability(String peerAddress) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("getGroupCapability")) {
                return -1;
            }
            if (peerAddress == null) {
                Log.e(TAG, "Cannot parse peer mac address.");
                return -1;
            }
            try {
                byte[] macAddress = NativeUtil.macAddressToByteArray(peerAddress);
                SupplicantResult<Integer> capability = new SupplicantResult<>("getGroupCapability(" + peerAddress + ")");
                try {
                    this.mISupplicantP2pIface.getGroupCapability(macAddress, new ISupplicantP2pIface.getGroupCapabilityCallback() {
                        public final void onValues(SupplicantStatus supplicantStatus, int i) {
                            SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, Integer.valueOf(i));
                        }
                    });
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                if (!capability.isSuccess()) {
                    return -1;
                }
                int intValue = capability.getResult().intValue();
                return intValue;
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse group address.", e2);
                return -1;
            }
        }
    }

    public boolean configureExtListen(boolean enable, int periodInMillis, int intervalInMillis) {
        if (enable && intervalInMillis < periodInMillis) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("configureExtListen")) {
                return false;
            }
            if (!enable) {
                periodInMillis = 0;
                intervalInMillis = 0;
            }
            if (periodInMillis >= 0) {
                if (intervalInMillis >= 0) {
                    SupplicantResult<Void> result = new SupplicantResult<>("configureExtListen(" + periodInMillis + ", " + intervalInMillis + ")");
                    try {
                        result.setResult(this.mISupplicantP2pIface.configureExtListen(periodInMillis, intervalInMillis));
                    } catch (RemoteException e) {
                        Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                        supplicantServiceDiedHandler();
                    }
                    boolean isSuccess = result.isSuccess();
                    return isSuccess;
                }
            }
            Log.e(TAG, "Invalid parameters supplied to configureExtListen: " + periodInMillis + ", " + intervalInMillis);
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00ed, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setListenChannel(int r9, int r10) {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            java.lang.String r1 = "setListenChannel"
            boolean r1 = r8.checkSupplicantP2pIfaceAndLogFailure(r1)     // Catch:{ all -> 0x00ee }
            r2 = 0
            if (r1 != 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x00ee }
            return r2
        L_0x000e:
            r1 = 1
            if (r9 < r1) goto L_0x0067
            r3 = 11
            if (r9 > r3) goto L_0x0067
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r3 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ all -> 0x00ee }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ee }
            r4.<init>()     // Catch:{ all -> 0x00ee }
            java.lang.String r5 = "setListenChannel("
            r4.append(r5)     // Catch:{ all -> 0x00ee }
            r4.append(r9)     // Catch:{ all -> 0x00ee }
            java.lang.String r5 = ", "
            r4.append(r5)     // Catch:{ all -> 0x00ee }
            r5 = 81
            r4.append(r5)     // Catch:{ all -> 0x00ee }
            java.lang.String r6 = ")"
            r4.append(r6)     // Catch:{ all -> 0x00ee }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00ee }
            r3.<init>(r4)     // Catch:{ all -> 0x00ee }
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface r4 = r8.mISupplicantP2pIface     // Catch:{ RemoteException -> 0x0044 }
            android.hardware.wifi.supplicant.V1_0.SupplicantStatus r4 = r4.setListenChannel(r9, r5)     // Catch:{ RemoteException -> 0x0044 }
            r3.setResult(r4)     // Catch:{ RemoteException -> 0x0044 }
            goto L_0x005e
        L_0x0044:
            r4 = move-exception
            java.lang.String r5 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ee }
            r6.<init>()     // Catch:{ all -> 0x00ee }
            java.lang.String r7 = "ISupplicantP2pIface exception: "
            r6.append(r7)     // Catch:{ all -> 0x00ee }
            r6.append(r4)     // Catch:{ all -> 0x00ee }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ee }
            android.util.Log.e(r5, r6)     // Catch:{ all -> 0x00ee }
            r8.supplicantServiceDiedHandler()     // Catch:{ all -> 0x00ee }
        L_0x005e:
            boolean r4 = r3.isSuccess()     // Catch:{ all -> 0x00ee }
            if (r4 != 0) goto L_0x0066
            monitor-exit(r0)     // Catch:{ all -> 0x00ee }
            return r2
        L_0x0066:
            goto L_0x006b
        L_0x0067:
            if (r9 == 0) goto L_0x006b
            monitor-exit(r0)     // Catch:{ all -> 0x00ee }
            return r2
        L_0x006b:
            if (r10 < 0) goto L_0x00ec
            r3 = 165(0xa5, float:2.31E-43)
            if (r10 > r3) goto L_0x00ec
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x00ee }
            r2.<init>()     // Catch:{ all -> 0x00ee }
            if (r10 < r1) goto L_0x00a7
            if (r10 > r3) goto L_0x00a7
            r1 = 14
            if (r10 > r1) goto L_0x0082
            r1 = 2407(0x967, float:3.373E-42)
            goto L_0x0084
        L_0x0082:
            r1 = 5000(0x1388, float:7.006E-42)
        L_0x0084:
            int r3 = r10 * 5
            int r1 = r1 + r3
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface$FreqRange r3 = new android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface$FreqRange     // Catch:{ all -> 0x00ee }
            r3.<init>()     // Catch:{ all -> 0x00ee }
            r4 = 1000(0x3e8, float:1.401E-42)
            r3.min = r4     // Catch:{ all -> 0x00ee }
            int r4 = r1 + -5
            r3.max = r4     // Catch:{ all -> 0x00ee }
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface$FreqRange r4 = new android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface$FreqRange     // Catch:{ all -> 0x00ee }
            r4.<init>()     // Catch:{ all -> 0x00ee }
            int r5 = r1 + 5
            r4.min = r5     // Catch:{ all -> 0x00ee }
            r5 = 6000(0x1770, float:8.408E-42)
            r4.max = r5     // Catch:{ all -> 0x00ee }
            r2.add(r3)     // Catch:{ all -> 0x00ee }
            r2.add(r4)     // Catch:{ all -> 0x00ee }
        L_0x00a7:
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r1 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ all -> 0x00ee }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ee }
            r3.<init>()     // Catch:{ all -> 0x00ee }
            java.lang.String r4 = "setDisallowedFrequencies("
            r3.append(r4)     // Catch:{ all -> 0x00ee }
            r3.append(r2)     // Catch:{ all -> 0x00ee }
            java.lang.String r4 = ")"
            r3.append(r4)     // Catch:{ all -> 0x00ee }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00ee }
            r1.<init>(r3)     // Catch:{ all -> 0x00ee }
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface r3 = r8.mISupplicantP2pIface     // Catch:{ RemoteException -> 0x00cc }
            android.hardware.wifi.supplicant.V1_0.SupplicantStatus r3 = r3.setDisallowedFrequencies(r2)     // Catch:{ RemoteException -> 0x00cc }
            r1.setResult(r3)     // Catch:{ RemoteException -> 0x00cc }
            goto L_0x00e6
        L_0x00cc:
            r3 = move-exception
            java.lang.String r4 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ee }
            r5.<init>()     // Catch:{ all -> 0x00ee }
            java.lang.String r6 = "ISupplicantP2pIface exception: "
            r5.append(r6)     // Catch:{ all -> 0x00ee }
            r5.append(r3)     // Catch:{ all -> 0x00ee }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00ee }
            android.util.Log.e(r4, r5)     // Catch:{ all -> 0x00ee }
            r8.supplicantServiceDiedHandler()     // Catch:{ all -> 0x00ee }
        L_0x00e6:
            boolean r3 = r1.isSuccess()     // Catch:{ all -> 0x00ee }
            monitor-exit(r0)     // Catch:{ all -> 0x00ee }
            return r3
        L_0x00ec:
            monitor-exit(r0)     // Catch:{ all -> 0x00ee }
            return r2
        L_0x00ee:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00ee }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.SupplicantP2pIfaceHal.setListenChannel(int, int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0150, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean serviceAdd(android.net.wifi.p2p.nsd.WifiP2pServiceInfo r14) {
        /*
            r13 = this;
            java.lang.Object r0 = r13.mLock
            monitor-enter(r0)
            java.lang.String r1 = "serviceAdd"
            boolean r1 = r13.checkSupplicantP2pIfaceAndLogFailure(r1)     // Catch:{ all -> 0x0153 }
            r2 = 0
            if (r1 != 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r2
        L_0x000e:
            if (r14 != 0) goto L_0x0019
            java.lang.String r1 = "SupplicantP2pIfaceHal"
            java.lang.String r3 = "Null service info passed."
            android.util.Log.e(r1, r3)     // Catch:{ all -> 0x0153 }
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r2
        L_0x0019:
            java.util.List r1 = r14.getSupplicantQueryList()     // Catch:{ all -> 0x0153 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0153 }
        L_0x0021:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0153 }
            r4 = 1
            if (r3 == 0) goto L_0x0151
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0153 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0153 }
            if (r3 != 0) goto L_0x0039
            java.lang.String r1 = "SupplicantP2pIfaceHal"
            java.lang.String r4 = "Invalid service description (null)."
            android.util.Log.e(r1, r4)     // Catch:{ all -> 0x0153 }
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r2
        L_0x0039:
            java.lang.String r5 = " "
            java.lang.String[] r5 = r3.split(r5)     // Catch:{ all -> 0x0153 }
            int r6 = r5.length     // Catch:{ all -> 0x0153 }
            r7 = 3
            if (r6 >= r7) goto L_0x005b
            java.lang.String r1 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0153 }
            r4.<init>()     // Catch:{ all -> 0x0153 }
            java.lang.String r6 = "Service specification invalid: "
            r4.append(r6)     // Catch:{ all -> 0x0153 }
            r4.append(r3)     // Catch:{ all -> 0x0153 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0153 }
            android.util.Log.e(r1, r4)     // Catch:{ all -> 0x0153 }
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r2
        L_0x005b:
            r6 = 0
            java.lang.String r7 = "upnp"
            r8 = r5[r2]     // Catch:{ RemoteException -> 0x012a }
            boolean r7 = r7.equals(r8)     // Catch:{ RemoteException -> 0x012a }
            r8 = 2
            if (r7 == 0) goto L_0x00c0
            r7 = 0
            r9 = r5[r4]     // Catch:{ NumberFormatException -> 0x00a7 }
            r10 = 16
            int r9 = java.lang.Integer.parseInt(r9, r10)     // Catch:{ NumberFormatException -> 0x00a7 }
            r7 = r9
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r9 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ RemoteException -> 0x012a }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x012a }
            r10.<init>()     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r11 = "addUpnpService("
            r10.append(r11)     // Catch:{ RemoteException -> 0x012a }
            r4 = r5[r4]     // Catch:{ RemoteException -> 0x012a }
            r10.append(r4)     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r4 = ", "
            r10.append(r4)     // Catch:{ RemoteException -> 0x012a }
            r4 = r5[r8]     // Catch:{ RemoteException -> 0x012a }
            r10.append(r4)     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r4 = ")"
            r10.append(r4)     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r4 = r10.toString()     // Catch:{ RemoteException -> 0x012a }
            r9.<init>(r4)     // Catch:{ RemoteException -> 0x012a }
            r6 = r9
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface r4 = r13.mISupplicantP2pIface     // Catch:{ RemoteException -> 0x012a }
            r8 = r5[r8]     // Catch:{ RemoteException -> 0x012a }
            android.hardware.wifi.supplicant.V1_0.SupplicantStatus r4 = r4.addUpnpService(r7, r8)     // Catch:{ RemoteException -> 0x012a }
            r6.setResult(r4)     // Catch:{ RemoteException -> 0x012a }
            goto L_0x0127
        L_0x00a7:
            r4 = move-exception
            java.lang.String r8 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x012a }
            r9.<init>()     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r10 = "UPnP Service specification invalid: "
            r9.append(r10)     // Catch:{ RemoteException -> 0x012a }
            r9.append(r3)     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r9 = r9.toString()     // Catch:{ RemoteException -> 0x012a }
            android.util.Log.e(r8, r9, r4)     // Catch:{ RemoteException -> 0x012a }
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r2
        L_0x00c0:
            java.lang.String r7 = "bonjour"
            r9 = r5[r2]     // Catch:{ RemoteException -> 0x012a }
            boolean r7 = r7.equals(r9)     // Catch:{ RemoteException -> 0x012a }
            if (r7 == 0) goto L_0x0128
            r7 = r5[r4]     // Catch:{ RemoteException -> 0x012a }
            if (r7 == 0) goto L_0x0127
            r7 = r5[r8]     // Catch:{ RemoteException -> 0x012a }
            if (r7 == 0) goto L_0x0127
            r7 = 0
            r9 = 0
            r10 = r5[r4]     // Catch:{ Exception -> 0x011d }
            byte[] r10 = com.android.server.wifi.util.NativeUtil.hexStringToByteArray(r10)     // Catch:{ Exception -> 0x011d }
            java.util.ArrayList r10 = com.android.server.wifi.util.NativeUtil.byteArrayToArrayList(r10)     // Catch:{ Exception -> 0x011d }
            r7 = r10
            r10 = r5[r8]     // Catch:{ Exception -> 0x011d }
            byte[] r10 = com.android.server.wifi.util.NativeUtil.hexStringToByteArray(r10)     // Catch:{ Exception -> 0x011d }
            java.util.ArrayList r10 = com.android.server.wifi.util.NativeUtil.byteArrayToArrayList(r10)     // Catch:{ Exception -> 0x011d }
            r9 = r10
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r10 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ RemoteException -> 0x012a }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x012a }
            r11.<init>()     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r12 = "addBonjourService("
            r11.append(r12)     // Catch:{ RemoteException -> 0x012a }
            r4 = r5[r4]     // Catch:{ RemoteException -> 0x012a }
            r11.append(r4)     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r4 = ", "
            r11.append(r4)     // Catch:{ RemoteException -> 0x012a }
            r4 = r5[r8]     // Catch:{ RemoteException -> 0x012a }
            r11.append(r4)     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r4 = ")"
            r11.append(r4)     // Catch:{ RemoteException -> 0x012a }
            java.lang.String r4 = r11.toString()     // Catch:{ RemoteException -> 0x012a }
            r10.<init>(r4)     // Catch:{ RemoteException -> 0x012a }
            r6 = r10
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface r4 = r13.mISupplicantP2pIface     // Catch:{ RemoteException -> 0x012a }
            android.hardware.wifi.supplicant.V1_0.SupplicantStatus r4 = r4.addBonjourService(r7, r9)     // Catch:{ RemoteException -> 0x012a }
            r6.setResult(r4)     // Catch:{ RemoteException -> 0x012a }
            goto L_0x0127
        L_0x011d:
            r4 = move-exception
            java.lang.String r8 = "SupplicantP2pIfaceHal"
            java.lang.String r10 = "Invalid bonjour service description."
            android.util.Log.e(r8, r10)     // Catch:{ RemoteException -> 0x012a }
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r2
        L_0x0127:
            goto L_0x0144
        L_0x0128:
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r2
        L_0x012a:
            r4 = move-exception
            java.lang.String r7 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0153 }
            r8.<init>()     // Catch:{ all -> 0x0153 }
            java.lang.String r9 = "ISupplicantP2pIface exception: "
            r8.append(r9)     // Catch:{ all -> 0x0153 }
            r8.append(r4)     // Catch:{ all -> 0x0153 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0153 }
            android.util.Log.e(r7, r8)     // Catch:{ all -> 0x0153 }
            r13.supplicantServiceDiedHandler()     // Catch:{ all -> 0x0153 }
        L_0x0144:
            if (r6 == 0) goto L_0x014f
            boolean r4 = r6.isSuccess()     // Catch:{ all -> 0x0153 }
            if (r4 != 0) goto L_0x014d
            goto L_0x014f
        L_0x014d:
            goto L_0x0021
        L_0x014f:
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r2
        L_0x0151:
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            return r4
        L_0x0153:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0153 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.SupplicantP2pIfaceHal.serviceAdd(android.net.wifi.p2p.nsd.WifiP2pServiceInfo):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:71:0x014d, code lost:
        return false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0143 A[Catch:{ RemoteException -> 0x0127 }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x014c A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean serviceRemove(android.net.wifi.p2p.nsd.WifiP2pServiceInfo r12) {
        /*
            r11 = this;
            java.lang.Object r0 = r11.mLock
            monitor-enter(r0)
            java.lang.String r1 = "serviceRemove"
            boolean r1 = r11.checkSupplicantP2pIfaceAndLogFailure(r1)     // Catch:{ all -> 0x0150 }
            r2 = 0
            if (r1 != 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r2
        L_0x000e:
            if (r12 != 0) goto L_0x0019
            java.lang.String r1 = "SupplicantP2pIfaceHal"
            java.lang.String r3 = "Null service info passed."
            android.util.Log.e(r1, r3)     // Catch:{ all -> 0x0150 }
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r2
        L_0x0019:
            java.util.List r1 = r12.getSupplicantQueryList()     // Catch:{ all -> 0x0150 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0150 }
        L_0x0021:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0150 }
            r4 = 1
            if (r3 == 0) goto L_0x014e
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0150 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0150 }
            if (r3 != 0) goto L_0x0039
            java.lang.String r1 = "SupplicantP2pIfaceHal"
            java.lang.String r4 = "Invalid service description (null)."
            android.util.Log.e(r1, r4)     // Catch:{ all -> 0x0150 }
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r2
        L_0x0039:
            java.lang.String r5 = " "
            java.lang.String[] r5 = r3.split(r5)     // Catch:{ all -> 0x0150 }
            int r6 = r5.length     // Catch:{ all -> 0x0150 }
            r7 = 3
            if (r6 >= r7) goto L_0x005b
            java.lang.String r1 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0150 }
            r4.<init>()     // Catch:{ all -> 0x0150 }
            java.lang.String r6 = "Service specification invalid: "
            r4.append(r6)     // Catch:{ all -> 0x0150 }
            r4.append(r3)     // Catch:{ all -> 0x0150 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0150 }
            android.util.Log.e(r1, r4)     // Catch:{ all -> 0x0150 }
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r2
        L_0x005b:
            r6 = 0
            java.lang.String r7 = "upnp"
            r8 = r5[r2]     // Catch:{ RemoteException -> 0x0127 }
            boolean r7 = r7.equals(r8)     // Catch:{ RemoteException -> 0x0127 }
            if (r7 == 0) goto L_0x00bf
            r7 = 0
            r8 = r5[r4]     // Catch:{ NumberFormatException -> 0x00a6 }
            r9 = 16
            int r8 = java.lang.Integer.parseInt(r8, r9)     // Catch:{ NumberFormatException -> 0x00a6 }
            r7 = r8
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r8 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ RemoteException -> 0x0127 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0127 }
            r9.<init>()     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r10 = "removeUpnpService("
            r9.append(r10)     // Catch:{ RemoteException -> 0x0127 }
            r4 = r5[r4]     // Catch:{ RemoteException -> 0x0127 }
            r9.append(r4)     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r4 = ", "
            r9.append(r4)     // Catch:{ RemoteException -> 0x0127 }
            r4 = 2
            r10 = r5[r4]     // Catch:{ RemoteException -> 0x0127 }
            r9.append(r10)     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r10 = ")"
            r9.append(r10)     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r9 = r9.toString()     // Catch:{ RemoteException -> 0x0127 }
            r8.<init>(r9)     // Catch:{ RemoteException -> 0x0127 }
            r6 = r8
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface r8 = r11.mISupplicantP2pIface     // Catch:{ RemoteException -> 0x0127 }
            r4 = r5[r4]     // Catch:{ RemoteException -> 0x0127 }
            android.hardware.wifi.supplicant.V1_0.SupplicantStatus r4 = r8.removeUpnpService(r7, r4)     // Catch:{ RemoteException -> 0x0127 }
            r6.setResult(r4)     // Catch:{ RemoteException -> 0x0127 }
            goto L_0x010c
        L_0x00a6:
            r4 = move-exception
            java.lang.String r8 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0127 }
            r9.<init>()     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r10 = "UPnP Service specification invalid: "
            r9.append(r10)     // Catch:{ RemoteException -> 0x0127 }
            r9.append(r3)     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r9 = r9.toString()     // Catch:{ RemoteException -> 0x0127 }
            android.util.Log.e(r8, r9, r4)     // Catch:{ RemoteException -> 0x0127 }
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r2
        L_0x00bf:
            java.lang.String r7 = "bonjour"
            r8 = r5[r2]     // Catch:{ RemoteException -> 0x0127 }
            boolean r7 = r7.equals(r8)     // Catch:{ RemoteException -> 0x0127 }
            if (r7 == 0) goto L_0x010d
            r7 = r5[r4]     // Catch:{ RemoteException -> 0x0127 }
            if (r7 == 0) goto L_0x010c
            r7 = 0
            r8 = r5[r4]     // Catch:{ Exception -> 0x0102 }
            byte[] r8 = com.android.server.wifi.util.NativeUtil.hexStringToByteArray(r8)     // Catch:{ Exception -> 0x0102 }
            java.util.ArrayList r8 = com.android.server.wifi.util.NativeUtil.byteArrayToArrayList(r8)     // Catch:{ Exception -> 0x0102 }
            r7 = r8
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r8 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ RemoteException -> 0x0127 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0127 }
            r9.<init>()     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r10 = "removeBonjourService("
            r9.append(r10)     // Catch:{ RemoteException -> 0x0127 }
            r4 = r5[r4]     // Catch:{ RemoteException -> 0x0127 }
            r9.append(r4)     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r4 = ")"
            r9.append(r4)     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r4 = r9.toString()     // Catch:{ RemoteException -> 0x0127 }
            r8.<init>(r4)     // Catch:{ RemoteException -> 0x0127 }
            r6 = r8
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pIface r4 = r11.mISupplicantP2pIface     // Catch:{ RemoteException -> 0x0127 }
            android.hardware.wifi.supplicant.V1_0.SupplicantStatus r4 = r4.removeBonjourService(r7)     // Catch:{ RemoteException -> 0x0127 }
            r6.setResult(r4)     // Catch:{ RemoteException -> 0x0127 }
            goto L_0x010c
        L_0x0102:
            r4 = move-exception
            java.lang.String r8 = "SupplicantP2pIfaceHal"
            java.lang.String r9 = "Invalid bonjour service description."
            android.util.Log.e(r8, r9)     // Catch:{ RemoteException -> 0x0127 }
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r2
        L_0x010c:
            goto L_0x0141
        L_0x010d:
            java.lang.String r4 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0127 }
            r7.<init>()     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r8 = "Unknown / unsupported P2P service requested: "
            r7.append(r8)     // Catch:{ RemoteException -> 0x0127 }
            r8 = r5[r2]     // Catch:{ RemoteException -> 0x0127 }
            r7.append(r8)     // Catch:{ RemoteException -> 0x0127 }
            java.lang.String r7 = r7.toString()     // Catch:{ RemoteException -> 0x0127 }
            android.util.Log.e(r4, r7)     // Catch:{ RemoteException -> 0x0127 }
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r2
        L_0x0127:
            r4 = move-exception
            java.lang.String r7 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0150 }
            r8.<init>()     // Catch:{ all -> 0x0150 }
            java.lang.String r9 = "ISupplicantP2pIface exception: "
            r8.append(r9)     // Catch:{ all -> 0x0150 }
            r8.append(r4)     // Catch:{ all -> 0x0150 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0150 }
            android.util.Log.e(r7, r8)     // Catch:{ all -> 0x0150 }
            r11.supplicantServiceDiedHandler()     // Catch:{ all -> 0x0150 }
        L_0x0141:
            if (r6 == 0) goto L_0x014c
            boolean r4 = r6.isSuccess()     // Catch:{ all -> 0x0150 }
            if (r4 != 0) goto L_0x014a
            goto L_0x014c
        L_0x014a:
            goto L_0x0021
        L_0x014c:
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r2
        L_0x014e:
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            return r4
        L_0x0150:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0150 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.SupplicantP2pIfaceHal.serviceRemove(android.net.wifi.p2p.nsd.WifiP2pServiceInfo):boolean");
    }

    public String requestServiceDiscovery(String peerAddress, String query) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("requestServiceDiscovery")) {
                return null;
            }
            if (peerAddress == null) {
                Log.e(TAG, "Cannot parse peer mac address.");
                return null;
            }
            try {
                byte[] macAddress = NativeUtil.macAddressToByteArray(peerAddress);
                if (query == null) {
                    Log.e(TAG, "Cannot parse service discovery query: " + query);
                    return null;
                }
                try {
                    ArrayList<Byte> binQuery = NativeUtil.byteArrayToArrayList(NativeUtil.hexStringToByteArray(query));
                    SupplicantResult<Long> result = new SupplicantResult<>("requestServiceDiscovery(" + peerAddress + ", " + query + ")");
                    try {
                        this.mISupplicantP2pIface.requestServiceDiscovery(macAddress, binQuery, new ISupplicantP2pIface.requestServiceDiscoveryCallback() {
                            public final void onValues(SupplicantStatus supplicantStatus, long j) {
                                SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, new Long(j));
                            }
                        });
                    } catch (RemoteException e) {
                        Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                        supplicantServiceDiedHandler();
                    }
                    Long value = result.getResult();
                    if (value == null) {
                        return null;
                    }
                    String l = value.toString();
                    return l;
                } catch (Exception e2) {
                    Log.e(TAG, "Could not parse service query.", e2);
                    return null;
                }
            } catch (Exception e3) {
                Log.e(TAG, "Could not process peer MAC address.", e3);
                return null;
            }
        }
    }

    public boolean cancelServiceDiscovery(String identifier) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("cancelServiceDiscovery")) {
                return false;
            }
            if (identifier == null) {
                Log.e(TAG, "cancelServiceDiscovery requires a valid tag.");
                return false;
            }
            try {
                long id = Long.parseLong(identifier);
                SupplicantResult<Void> result = new SupplicantResult<>("cancelServiceDiscovery(" + identifier + ")");
                try {
                    result.setResult(this.mISupplicantP2pIface.cancelServiceDiscovery(id));
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                boolean isSuccess = result.isSuccess();
                return isSuccess;
            } catch (NumberFormatException e2) {
                Log.e(TAG, "Service discovery identifier invalid: " + identifier, e2);
                return false;
            }
        }
    }

    public boolean setMiracastMode(int mode) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setMiracastMode")) {
                return false;
            }
            byte targetMode = 0;
            if (mode == 1) {
                targetMode = 1;
            } else if (mode == 2) {
                targetMode = 2;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("setMiracastMode(" + mode + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.setMiracastMode(targetMode));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean startWpsPbc(String groupIfName, String bssid) {
        if (TextUtils.isEmpty(groupIfName)) {
            Log.e(TAG, "Group name required when requesting WPS PBC. Got (" + groupIfName + ")");
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("startWpsPbc")) {
                return false;
            }
            try {
                byte[] macAddress = NativeUtil.macAddressToByteArray(bssid);
                SupplicantResult<Void> result = new SupplicantResult<>("startWpsPbc(" + groupIfName + ", " + bssid + ")");
                try {
                    result.setResult(this.mISupplicantP2pIface.startWpsPbc(groupIfName, macAddress));
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                boolean isSuccess = result.isSuccess();
                return isSuccess;
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse BSSID.", e2);
                return false;
            }
        }
    }

    public boolean startWpsPinKeypad(String groupIfName, String pin) {
        if (TextUtils.isEmpty(groupIfName) || TextUtils.isEmpty(pin)) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("startWpsPinKeypad")) {
                return false;
            }
            if (groupIfName == null) {
                Log.e(TAG, "Group name required when requesting WPS KEYPAD.");
                return false;
            } else if (pin == null) {
                Log.e(TAG, "PIN required when requesting WPS KEYPAD.");
                return false;
            } else {
                SupplicantResult<Void> result = new SupplicantResult<>("startWpsPinKeypad(" + groupIfName + ", " + pin + ")");
                try {
                    result.setResult(this.mISupplicantP2pIface.startWpsPinKeypad(groupIfName, pin));
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                boolean isSuccess = result.isSuccess();
                return isSuccess;
            }
        }
    }

    public String startWpsPinDisplay(String groupIfName, String bssid) {
        if (TextUtils.isEmpty(groupIfName)) {
            return null;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("startWpsPinDisplay")) {
                return null;
            }
            if (groupIfName == null) {
                Log.e(TAG, "Group name required when requesting WPS KEYPAD.");
                return null;
            }
            try {
                byte[] macAddress = NativeUtil.macAddressToByteArray(bssid);
                SupplicantResult<String> result = new SupplicantResult<>("startWpsPinDisplay(" + groupIfName + ", " + bssid + ")");
                try {
                    this.mISupplicantP2pIface.startWpsPinDisplay(groupIfName, macAddress, new ISupplicantP2pIface.startWpsPinDisplayCallback() {
                        public final void onValues(SupplicantStatus supplicantStatus, String str) {
                            SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, str);
                        }
                    });
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                String result2 = result.getResult();
                return result2;
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse BSSID.", e2);
                return null;
            }
        }
    }

    public boolean cancelWps(String groupIfName) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("cancelWps")) {
                return false;
            }
            if (groupIfName == null) {
                Log.e(TAG, "Group name required when requesting WPS KEYPAD.");
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("cancelWps(" + groupIfName + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.cancelWps(groupIfName));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean enableWfd(boolean enable) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("enableWfd")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("enableWfd(" + enable + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.enableWfd(enable));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean setWfdDeviceInfo(String info) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setWfdDeviceInfo")) {
                return false;
            }
            if (info == null) {
                Log.e(TAG, "Cannot parse null WFD info string.");
                return false;
            }
            try {
                byte[] wfdInfo = NativeUtil.hexStringToByteArray(info);
                SupplicantResult<Void> result = new SupplicantResult<>("setWfdDeviceInfo(" + info + ")");
                try {
                    result.setResult(this.mISupplicantP2pIface.setWfdDeviceInfo(wfdInfo));
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                boolean isSuccess = result.isSuccess();
                return isSuccess;
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse WFD Device Info string.");
                return false;
            }
        }
    }

    public boolean setWfdR2DeviceInfo(String info) {
        synchronized (this.mLock) {
            if (!checkSupplicantVendorP2pIfaceAndLogFailure("setWfdR2DeviceInfo")) {
                return false;
            }
            if (info == null) {
                Log.e(TAG, "Cannot parse null WFD info string.");
                return false;
            }
            try {
                byte[] wfdInfo = NativeUtil.hexStringToByteArray(info);
                SupplicantResult<Void> result = new SupplicantResult<>("setWfdDeviceInfo(" + info + ")");
                try {
                    result.setResult(this.mISupplicantVendorP2pIface.setWfdR2DeviceInfo(wfdInfo));
                } catch (RemoteException e) {
                    Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                    supplicantServiceDiedHandler();
                }
                boolean isSuccess = result.isSuccess();
                return isSuccess;
            } catch (Exception e2) {
                Log.e(TAG, "Could not parse WFD Device Info string.");
                return false;
            }
        }
    }

    public boolean removeNetwork(int networkId) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("removeNetwork")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("removeNetwork(" + networkId + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.removeNetwork(networkId));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    private List<Integer> listNetworks() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("listNetworks")) {
                return null;
            }
            SupplicantResult<ArrayList> result = new SupplicantResult<>("listNetworks()");
            try {
                this.mISupplicantP2pIface.listNetworks(new ISupplicantIface.listNetworksCallback() {
                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, arrayList);
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            List<Integer> result2 = result.getResult();
            return result2;
        }
    }

    private ISupplicantP2pNetwork getNetwork(int networkId) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("getNetwork")) {
                return null;
            }
            SupplicantResult<ISupplicantNetwork> result = new SupplicantResult<>("getNetwork(" + networkId + ")");
            try {
                this.mISupplicantP2pIface.getNetwork(networkId, new ISupplicantIface.getNetworkCallback() {
                    public final void onValues(SupplicantStatus supplicantStatus, ISupplicantNetwork iSupplicantNetwork) {
                        SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, iSupplicantNetwork);
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            if (result.getResult() == null) {
                Log.e(TAG, "getNetwork got null network");
                return null;
            }
            ISupplicantP2pNetwork p2pNetworkMockable = getP2pNetworkMockable(result.getResult());
            return p2pNetworkMockable;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01f3, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean loadGroups(android.net.wifi.p2p.WifiP2pGroupList r15) {
        /*
            r14 = this;
            java.lang.Object r0 = r14.mLock
            monitor-enter(r0)
            java.lang.String r1 = "loadGroups"
            boolean r1 = r14.checkSupplicantP2pIfaceAndLogFailure(r1)     // Catch:{ all -> 0x01f4 }
            r2 = 0
            if (r1 != 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x01f4 }
            return r2
        L_0x000e:
            java.util.List r1 = r14.listNetworks()     // Catch:{ all -> 0x01f4 }
            if (r1 == 0) goto L_0x01f2
            boolean r3 = r1.isEmpty()     // Catch:{ all -> 0x01f4 }
            if (r3 == 0) goto L_0x001c
            goto L_0x01f2
        L_0x001c:
            java.util.Iterator r2 = r1.iterator()     // Catch:{ all -> 0x01f4 }
        L_0x0020:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x01f4 }
            if (r3 == 0) goto L_0x01ef
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x01f4 }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x01f4 }
            int r4 = r3.intValue()     // Catch:{ all -> 0x01f4 }
            android.hardware.wifi.supplicant.V1_0.ISupplicantP2pNetwork r4 = r14.getNetwork(r4)     // Catch:{ all -> 0x01f4 }
            if (r4 != 0) goto L_0x004d
            java.lang.String r5 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r6.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r7 = "Failed to retrieve network object for "
            r6.append(r7)     // Catch:{ all -> 0x01f4 }
            r6.append(r3)     // Catch:{ all -> 0x01f4 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01f4 }
            android.util.Log.e(r5, r6)     // Catch:{ all -> 0x01f4 }
            goto L_0x0020
        L_0x004d:
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r5 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ all -> 0x01f4 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r6.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r7 = "isCurrent("
            r6.append(r7)     // Catch:{ all -> 0x01f4 }
            r6.append(r3)     // Catch:{ all -> 0x01f4 }
            java.lang.String r7 = ")"
            r6.append(r7)     // Catch:{ all -> 0x01f4 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01f4 }
            r5.<init>(r6)     // Catch:{ all -> 0x01f4 }
            com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$46EeN9ldrxoq85aJ06n8leLnqa0 r6 = new com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$46EeN9ldrxoq85aJ06n8leLnqa0     // Catch:{ RemoteException -> 0x0071 }
            r6.<init>()     // Catch:{ RemoteException -> 0x0071 }
            r4.isCurrent(r6)     // Catch:{ RemoteException -> 0x0071 }
            goto L_0x008b
        L_0x0071:
            r6 = move-exception
            java.lang.String r7 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r8.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r9 = "ISupplicantP2pIface exception: "
            r8.append(r9)     // Catch:{ all -> 0x01f4 }
            r8.append(r6)     // Catch:{ all -> 0x01f4 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x01f4 }
            android.util.Log.e(r7, r8)     // Catch:{ all -> 0x01f4 }
            r14.supplicantServiceDiedHandler()     // Catch:{ all -> 0x01f4 }
        L_0x008b:
            boolean r6 = r5.isSuccess()     // Catch:{ all -> 0x01f4 }
            if (r6 == 0) goto L_0x01e6
            java.lang.Object r6 = r5.getResult()     // Catch:{ all -> 0x01f4 }
            java.lang.Boolean r6 = (java.lang.Boolean) r6     // Catch:{ all -> 0x01f4 }
            boolean r6 = r6.booleanValue()     // Catch:{ all -> 0x01f4 }
            if (r6 == 0) goto L_0x009f
            goto L_0x01e6
        L_0x009f:
            android.net.wifi.p2p.WifiP2pGroup r6 = new android.net.wifi.p2p.WifiP2pGroup     // Catch:{ all -> 0x01f4 }
            r6.<init>()     // Catch:{ all -> 0x01f4 }
            int r7 = r3.intValue()     // Catch:{ all -> 0x01f4 }
            r6.setNetworkId(r7)     // Catch:{ all -> 0x01f4 }
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r7 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ all -> 0x01f4 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r8.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r9 = "getSsid("
            r8.append(r9)     // Catch:{ all -> 0x01f4 }
            r8.append(r3)     // Catch:{ all -> 0x01f4 }
            java.lang.String r9 = ")"
            r8.append(r9)     // Catch:{ all -> 0x01f4 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x01f4 }
            r7.<init>(r8)     // Catch:{ all -> 0x01f4 }
            com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$lCGSBqf91PgBm-3BGOVVFuWRwao r8 = new com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$lCGSBqf91PgBm-3BGOVVFuWRwao     // Catch:{ RemoteException -> 0x00cf }
            r8.<init>()     // Catch:{ RemoteException -> 0x00cf }
            r4.getSsid(r8)     // Catch:{ RemoteException -> 0x00cf }
            goto L_0x00e9
        L_0x00cf:
            r8 = move-exception
            java.lang.String r9 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r10.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r11 = "ISupplicantP2pIface exception: "
            r10.append(r11)     // Catch:{ all -> 0x01f4 }
            r10.append(r8)     // Catch:{ all -> 0x01f4 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x01f4 }
            android.util.Log.e(r9, r10)     // Catch:{ all -> 0x01f4 }
            r14.supplicantServiceDiedHandler()     // Catch:{ all -> 0x01f4 }
        L_0x00e9:
            boolean r8 = r7.isSuccess()     // Catch:{ all -> 0x01f4 }
            if (r8 == 0) goto L_0x01ce
            java.lang.Object r8 = r7.getResult()     // Catch:{ all -> 0x01f4 }
            if (r8 == 0) goto L_0x01ce
            java.lang.Object r8 = r7.getResult()     // Catch:{ all -> 0x01f4 }
            java.util.ArrayList r8 = (java.util.ArrayList) r8     // Catch:{ all -> 0x01f4 }
            boolean r8 = r8.isEmpty()     // Catch:{ all -> 0x01f4 }
            if (r8 != 0) goto L_0x01ce
            java.lang.Object r8 = r7.getResult()     // Catch:{ all -> 0x01f4 }
            java.util.ArrayList r8 = (java.util.ArrayList) r8     // Catch:{ all -> 0x01f4 }
            java.lang.String r8 = com.android.server.wifi.util.NativeUtil.encodeSsid(r8)     // Catch:{ all -> 0x01f4 }
            java.lang.String r8 = com.android.server.wifi.util.NativeUtil.removeEnclosingQuotes(r8)     // Catch:{ all -> 0x01f4 }
            r6.setNetworkName(r8)     // Catch:{ all -> 0x01f4 }
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r8 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ all -> 0x01f4 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r9.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r10 = "getBssid("
            r9.append(r10)     // Catch:{ all -> 0x01f4 }
            r9.append(r3)     // Catch:{ all -> 0x01f4 }
            java.lang.String r10 = ")"
            r9.append(r10)     // Catch:{ all -> 0x01f4 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x01f4 }
            r8.<init>(r9)     // Catch:{ all -> 0x01f4 }
            com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$RmZHbucfmdwizVZtP749hOrrV-c r9 = new com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$RmZHbucfmdwizVZtP749hOrrV-c     // Catch:{ RemoteException -> 0x0137 }
            r9.<init>()     // Catch:{ RemoteException -> 0x0137 }
            r4.getBssid(r9)     // Catch:{ RemoteException -> 0x0137 }
            goto L_0x0151
        L_0x0137:
            r9 = move-exception
            java.lang.String r10 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r11.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r12 = "ISupplicantP2pIface exception: "
            r11.append(r12)     // Catch:{ all -> 0x01f4 }
            r11.append(r9)     // Catch:{ all -> 0x01f4 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x01f4 }
            android.util.Log.e(r10, r11)     // Catch:{ all -> 0x01f4 }
            r14.supplicantServiceDiedHandler()     // Catch:{ all -> 0x01f4 }
        L_0x0151:
            boolean r9 = r8.isSuccess()     // Catch:{ all -> 0x01f4 }
            if (r9 == 0) goto L_0x0178
            java.lang.Object r9 = r8.getResult()     // Catch:{ all -> 0x01f4 }
            byte[] r9 = (byte[]) r9     // Catch:{ all -> 0x01f4 }
            boolean r9 = com.android.internal.util.ArrayUtils.isEmpty(r9)     // Catch:{ all -> 0x01f4 }
            if (r9 != 0) goto L_0x0178
            android.net.wifi.p2p.WifiP2pDevice r9 = new android.net.wifi.p2p.WifiP2pDevice     // Catch:{ all -> 0x01f4 }
            r9.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.Object r10 = r8.getResult()     // Catch:{ all -> 0x01f4 }
            byte[] r10 = (byte[]) r10     // Catch:{ all -> 0x01f4 }
            java.lang.String r10 = com.android.server.wifi.util.NativeUtil.macAddressFromByteArray(r10)     // Catch:{ all -> 0x01f4 }
            r9.deviceAddress = r10     // Catch:{ all -> 0x01f4 }
            r6.setOwner(r9)     // Catch:{ all -> 0x01f4 }
        L_0x0178:
            com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult r9 = new com.android.server.wifi.p2p.SupplicantP2pIfaceHal$SupplicantResult     // Catch:{ all -> 0x01f4 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r10.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r11 = "isGo("
            r10.append(r11)     // Catch:{ all -> 0x01f4 }
            r10.append(r3)     // Catch:{ all -> 0x01f4 }
            java.lang.String r11 = ")"
            r10.append(r11)     // Catch:{ all -> 0x01f4 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x01f4 }
            r9.<init>(r10)     // Catch:{ all -> 0x01f4 }
            com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$FHS8JFuQKAI3YMplws47sZvI6gY r10 = new com.android.server.wifi.p2p.-$$Lambda$SupplicantP2pIfaceHal$FHS8JFuQKAI3YMplws47sZvI6gY     // Catch:{ RemoteException -> 0x019c }
            r10.<init>()     // Catch:{ RemoteException -> 0x019c }
            r4.isGo(r10)     // Catch:{ RemoteException -> 0x019c }
            goto L_0x01b6
        L_0x019c:
            r10 = move-exception
            java.lang.String r11 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r12.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r13 = "ISupplicantP2pIface exception: "
            r12.append(r13)     // Catch:{ all -> 0x01f4 }
            r12.append(r10)     // Catch:{ all -> 0x01f4 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x01f4 }
            android.util.Log.e(r11, r12)     // Catch:{ all -> 0x01f4 }
            r14.supplicantServiceDiedHandler()     // Catch:{ all -> 0x01f4 }
        L_0x01b6:
            boolean r10 = r9.isSuccess()     // Catch:{ all -> 0x01f4 }
            if (r10 == 0) goto L_0x01c9
            java.lang.Object r10 = r9.getResult()     // Catch:{ all -> 0x01f4 }
            java.lang.Boolean r10 = (java.lang.Boolean) r10     // Catch:{ all -> 0x01f4 }
            boolean r10 = r10.booleanValue()     // Catch:{ all -> 0x01f4 }
            r6.setIsGroupOwner(r10)     // Catch:{ all -> 0x01f4 }
        L_0x01c9:
            r15.add(r6)     // Catch:{ all -> 0x01f4 }
            goto L_0x0020
        L_0x01ce:
            java.lang.String r8 = "SupplicantP2pIfaceHal"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f4 }
            r9.<init>()     // Catch:{ all -> 0x01f4 }
            java.lang.String r10 = "group ssid is invalid! resultSsid = "
            r9.append(r10)     // Catch:{ all -> 0x01f4 }
            r9.append(r7)     // Catch:{ all -> 0x01f4 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x01f4 }
            android.util.Log.e(r8, r9)     // Catch:{ all -> 0x01f4 }
            goto L_0x0020
        L_0x01e6:
            java.lang.String r6 = "SupplicantP2pIfaceHal"
            java.lang.String r7 = "Skipping current network"
            android.util.Log.i(r6, r7)     // Catch:{ all -> 0x01f4 }
            goto L_0x0020
        L_0x01ef:
            monitor-exit(r0)     // Catch:{ all -> 0x01f4 }
            r0 = 1
            return r0
        L_0x01f2:
            monitor-exit(r0)     // Catch:{ all -> 0x01f4 }
            return r2
        L_0x01f4:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x01f4 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.SupplicantP2pIfaceHal.loadGroups(android.net.wifi.p2p.WifiP2pGroupList):boolean");
    }

    public boolean setWpsDeviceName(String name) {
        if (name == null) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setWpsDeviceName")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("setWpsDeviceName(" + name + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.setWpsDeviceName(name));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    public boolean setWpsDeviceType(String typeStr) {
        try {
            Matcher match = WPS_DEVICE_TYPE_PATTERN.matcher(typeStr);
            if (match.find()) {
                if (match.groupCount() == 3) {
                    short categ = Short.parseShort(match.group(1));
                    byte[] oui = NativeUtil.hexStringToByteArray(match.group(2));
                    short subCateg = Short.parseShort(match.group(3));
                    byte[] bytes = new byte[8];
                    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
                    byteBuffer.putShort(categ);
                    byteBuffer.put(oui);
                    byteBuffer.putShort(subCateg);
                    synchronized (this.mLock) {
                        if (!checkSupplicantP2pIfaceAndLogFailure("setWpsDeviceType")) {
                            return false;
                        }
                        SupplicantResult<Void> result = new SupplicantResult<>("setWpsDeviceType(" + typeStr + ")");
                        try {
                            result.setResult(this.mISupplicantP2pIface.setWpsDeviceType(bytes));
                        } catch (RemoteException e) {
                            Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                            supplicantServiceDiedHandler();
                        }
                        boolean isSuccess = result.isSuccess();
                        return isSuccess;
                    }
                }
            }
            Log.e(TAG, "Malformed WPS device type " + typeStr);
            return false;
        } catch (IllegalArgumentException e2) {
            Log.e(TAG, "Illegal argument " + typeStr, e2);
            return false;
        }
    }

    public boolean setWpsConfigMethods(String configMethodsStr) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setWpsConfigMethods")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("setWpsConfigMethods(" + configMethodsStr + ")");
            short configMethodsMask = 0;
            String[] configMethodsStrArr = configMethodsStr.split("\\s+");
            for (String stringToWpsConfigMethod : configMethodsStrArr) {
                configMethodsMask = (short) (stringToWpsConfigMethod(stringToWpsConfigMethod) | configMethodsMask);
            }
            try {
                result.setResult(this.mISupplicantP2pIface.setWpsConfigMethods(configMethodsMask));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public String getNfcHandoverRequest() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("getNfcHandoverRequest")) {
                return null;
            }
            SupplicantResult<ArrayList> result = new SupplicantResult<>("getNfcHandoverRequest()");
            try {
                this.mISupplicantP2pIface.createNfcHandoverRequestMessage(new ISupplicantP2pIface.createNfcHandoverRequestMessageCallback() {
                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, arrayList);
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            if (!result.isSuccess()) {
                return null;
            }
            String hexStringFromByteArray = NativeUtil.hexStringFromByteArray(NativeUtil.byteArrayFromArrayList(result.getResult()));
            return hexStringFromByteArray;
        }
    }

    public String getNfcHandoverSelect() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("getNfcHandoverSelect")) {
                return null;
            }
            SupplicantResult<ArrayList> result = new SupplicantResult<>("getNfcHandoverSelect()");
            try {
                this.mISupplicantP2pIface.createNfcHandoverSelectMessage(new ISupplicantP2pIface.createNfcHandoverSelectMessageCallback() {
                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, arrayList);
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            if (!result.isSuccess()) {
                return null;
            }
            String hexStringFromByteArray = NativeUtil.hexStringFromByteArray(NativeUtil.byteArrayFromArrayList(result.getResult()));
            return hexStringFromByteArray;
        }
    }

    public boolean initiatorReportNfcHandover(String selectMessage) {
        if (selectMessage == null) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("initiatorReportNfcHandover")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("initiatorReportNfcHandover(" + selectMessage + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.reportNfcHandoverInitiation(NativeUtil.byteArrayToArrayList(NativeUtil.hexStringToByteArray(selectMessage))));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            } catch (IllegalArgumentException e2) {
                Log.e(TAG, "Illegal argument " + selectMessage, e2);
                return false;
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean responderReportNfcHandover(String requestMessage) {
        if (requestMessage == null) {
            return false;
        }
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("responderReportNfcHandover")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("responderReportNfcHandover(" + requestMessage + ")");
            try {
                result.setResult(this.mISupplicantP2pIface.reportNfcHandoverResponse(NativeUtil.byteArrayToArrayList(NativeUtil.hexStringToByteArray(requestMessage))));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            } catch (IllegalArgumentException e2) {
                Log.e(TAG, "Illegal argument " + requestMessage, e2);
                return false;
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean setClientList(int networkId, String clientListStr) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("setClientList")) {
                return false;
            }
            if (TextUtils.isEmpty(clientListStr)) {
                Log.e(TAG, "Invalid client list");
                return false;
            }
            ISupplicantP2pNetwork network = getNetwork(networkId);
            if (network == null) {
                Log.e(TAG, "Invalid network id ");
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("setClientList(" + networkId + ", " + clientListStr + ")");
            try {
                ArrayList<byte[]> clients = new ArrayList<>();
                for (String clientStr : Arrays.asList(clientListStr.split("\\s+"))) {
                    clients.add(NativeUtil.macAddressToByteArray(clientStr));
                }
                result.setResult(network.setClientList(clients));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            } catch (IllegalArgumentException e2) {
                Log.e(TAG, "Illegal argument " + clientListStr, e2);
                return false;
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public String getClientList(int networkId) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("getClientList")) {
                return null;
            }
            ISupplicantP2pNetwork network = getNetwork(networkId);
            if (network == null) {
                Log.e(TAG, "Invalid network id ");
                return null;
            }
            SupplicantResult<ArrayList> result = new SupplicantResult<>("getClientList(" + networkId + ")");
            try {
                network.getClientList(new ISupplicantP2pNetwork.getClientListCallback() {
                    public final void onValues(SupplicantStatus supplicantStatus, ArrayList arrayList) {
                        SupplicantP2pIfaceHal.SupplicantResult.this.setResult(supplicantStatus, arrayList);
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            if (!result.isSuccess()) {
                return null;
            }
            String str = (String) result.getResult().stream().map($$Lambda$22Qhg7RQJlXihi83tqGgsfFMs.INSTANCE).collect(Collectors.joining(" "));
            return str;
        }
    }

    public boolean saveConfig() {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailure("saveConfig")) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("saveConfig()");
            try {
                result.setResult(this.mISupplicantP2pIface.saveConfig());
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    public boolean setMacRandomization(boolean enable) {
        synchronized (this.mLock) {
            if (!checkSupplicantP2pIfaceAndLogFailureV1_2("setMacRandomization")) {
                return false;
            }
            android.hardware.wifi.supplicant.V1_2.ISupplicantP2pIface ifaceV12 = getP2pIfaceMockableV1_2();
            if (ifaceV12 == null) {
                return false;
            }
            SupplicantResult<Void> result = new SupplicantResult<>("setMacRandomization(" + enable + ")");
            try {
                result.setResult(ifaceV12.setMacRandomization(enable));
            } catch (RemoteException e) {
                Log.e(TAG, "ISupplicantP2pIface exception: " + e);
                supplicantServiceDiedHandler();
            }
            boolean isSuccess = result.isSuccess();
            return isSuccess;
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static short stringToWpsConfigMethod(java.lang.String r5) {
        /*
            int r0 = r5.hashCode()
            r1 = 8
            r2 = 4
            r3 = 2
            r4 = 1
            switch(r0) {
                case -1781962557: goto L_0x0099;
                case -1419358249: goto L_0x008f;
                case -1134657068: goto L_0x0085;
                case -614489202: goto L_0x007a;
                case -522593958: goto L_0x006f;
                case -423872603: goto L_0x0065;
                case -416734217: goto L_0x005b;
                case 3388229: goto L_0x0050;
                case 3599197: goto L_0x0046;
                case 102727412: goto L_0x003b;
                case 179612103: goto L_0x0030;
                case 1146869903: goto L_0x0024;
                case 1671764162: goto L_0x0019;
                case 2010140181: goto L_0x000e;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x00a4
        L_0x000e:
            java.lang.String r0 = "int_nfc_token"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r2
            goto L_0x00a5
        L_0x0019:
            java.lang.String r0 = "display"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 3
            goto L_0x00a5
        L_0x0024:
            java.lang.String r0 = "physical_push_button"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 10
            goto L_0x00a5
        L_0x0030:
            java.lang.String r0 = "ext_nfc_token"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 5
            goto L_0x00a5
        L_0x003b:
            java.lang.String r0 = "label"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r3
            goto L_0x00a5
        L_0x0046:
            java.lang.String r0 = "usba"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 0
            goto L_0x00a5
        L_0x0050:
            java.lang.String r0 = "p2ps"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 11
            goto L_0x00a5
        L_0x005b:
            java.lang.String r0 = "push_button"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 7
            goto L_0x00a5
        L_0x0065:
            java.lang.String r0 = "nfc_interface"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 6
            goto L_0x00a5
        L_0x006f:
            java.lang.String r0 = "physical_display"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 13
            goto L_0x00a5
        L_0x007a:
            java.lang.String r0 = "virtual_display"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 12
            goto L_0x00a5
        L_0x0085:
            java.lang.String r0 = "keypad"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r1
            goto L_0x00a5
        L_0x008f:
            java.lang.String r0 = "ethernet"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r4
            goto L_0x00a5
        L_0x0099:
            java.lang.String r0 = "virtual_push_button"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 9
            goto L_0x00a5
        L_0x00a4:
            r0 = -1
        L_0x00a5:
            switch(r0) {
                case 0: goto L_0x00e0;
                case 1: goto L_0x00df;
                case 2: goto L_0x00de;
                case 3: goto L_0x00dd;
                case 4: goto L_0x00da;
                case 5: goto L_0x00d7;
                case 6: goto L_0x00d4;
                case 7: goto L_0x00d1;
                case 8: goto L_0x00ce;
                case 9: goto L_0x00cb;
                case 10: goto L_0x00c8;
                case 11: goto L_0x00c5;
                case 12: goto L_0x00c2;
                case 13: goto L_0x00bf;
                default: goto L_0x00a8;
            }
        L_0x00a8:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid WPS config method: "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x00bf:
            r0 = 16392(0x4008, float:2.297E-41)
            return r0
        L_0x00c2:
            r0 = 8200(0x2008, float:1.149E-41)
            return r0
        L_0x00c5:
            r0 = 4096(0x1000, float:5.74E-42)
            return r0
        L_0x00c8:
            r0 = 1152(0x480, float:1.614E-42)
            return r0
        L_0x00cb:
            r0 = 640(0x280, float:8.97E-43)
            return r0
        L_0x00ce:
            r0 = 256(0x100, float:3.59E-43)
            return r0
        L_0x00d1:
            r0 = 128(0x80, float:1.794E-43)
            return r0
        L_0x00d4:
            r0 = 64
            return r0
        L_0x00d7:
            r0 = 16
            return r0
        L_0x00da:
            r0 = 32
            return r0
        L_0x00dd:
            return r1
        L_0x00de:
            return r2
        L_0x00df:
            return r3
        L_0x00e0:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.SupplicantP2pIfaceHal.stringToWpsConfigMethod(java.lang.String):short");
    }

    private static class SupplicantResult<E> {
        private String mMethodName;
        private SupplicantStatus mStatus = null;
        private E mValue = null;

        SupplicantResult(String methodName) {
            this.mMethodName = methodName;
            SupplicantP2pIfaceHal.logd("entering " + this.mMethodName);
        }

        public void setResult(SupplicantStatus status, E value) {
            SupplicantP2pIfaceHal.logCompletion(this.mMethodName, status);
            SupplicantP2pIfaceHal.logd("leaving " + this.mMethodName + " with result = " + value);
            this.mStatus = status;
            this.mValue = value;
        }

        public void setResult(SupplicantStatus status) {
            SupplicantP2pIfaceHal.logCompletion(this.mMethodName, status);
            SupplicantP2pIfaceHal.logd("leaving " + this.mMethodName);
            this.mStatus = status;
        }

        public boolean isSuccess() {
            SupplicantStatus supplicantStatus = this.mStatus;
            return supplicantStatus != null && (supplicantStatus.code == 0 || this.mStatus.code == 5);
        }

        public E getResult() {
            if (isSuccess()) {
                return this.mValue;
            }
            return null;
        }
    }
}
