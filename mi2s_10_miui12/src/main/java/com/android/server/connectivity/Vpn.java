package com.android.server.connectivity;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.LocalSocket;
import android.net.Network;
import android.net.NetworkAgent;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkMisc;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.UidRange;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemService;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnInfo;
import com.android.internal.net.VpnProfile;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.ArrayUtils;
import com.android.server.net.BaseNetworkObserver;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Vpn {
    private static final boolean LOGD = true;
    private static final int MAX_ROUTES_TO_EVALUATE = 150;
    private static final long MOST_IPV4_ADDRESSES_COUNT = 3650722201L;
    private static final BigInteger MOST_IPV6_ADDRESSES_COUNT = BigInteger.ONE.shiftLeft(128).multiply(BigInteger.valueOf(85)).divide(BigInteger.valueOf(100));
    private static final String NETWORKTYPE = "VPN";
    private static final String TAG = "Vpn";
    private static final long VPN_LAUNCH_IDLE_WHITELIST_DURATION_MS = 60000;
    private boolean mAlwaysOn;
    @GuardedBy({"this"})
    private Set<UidRange> mBlockedUsers;
    @VisibleForTesting
    protected VpnConfig mConfig;
    /* access modifiers changed from: private */
    public Connection mConnection;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public volatile boolean mEnableTeardown;
    /* access modifiers changed from: private */
    public String mInterface;
    private boolean mIsPackageTargetingAtLeastQ;
    /* access modifiers changed from: private */
    public LegacyVpnRunner mLegacyVpnRunner;
    private boolean mLockdown;
    private List<String> mLockdownWhitelist;
    private final Looper mLooper;
    private final INetworkManagementService mNetd;
    @VisibleForTesting
    protected NetworkAgent mNetworkAgent;
    @VisibleForTesting
    protected final NetworkCapabilities mNetworkCapabilities;
    /* access modifiers changed from: private */
    public final NetworkInfo mNetworkInfo;
    /* access modifiers changed from: private */
    public INetworkManagementEventObserver mObserver;
    private int mOwnerUID;
    private String mPackage;
    /* access modifiers changed from: private */
    public PendingIntent mStatusIntent;
    private final SystemServices mSystemServices;
    private final int mUserHandle;

    private native boolean jniAddAddress(String str, String str2, int i);

    /* access modifiers changed from: private */
    public native int jniCheck(String str);

    private native int jniCreate(int i);

    private native boolean jniDelAddress(String str, String str2, int i);

    private native String jniGetName(int i);

    private native void jniReset(String str);

    private native int jniSetAddresses(String str, String str2);

    public Vpn(Looper looper, Context context, INetworkManagementService netService, int userHandle) {
        this(looper, context, netService, userHandle, new SystemServices(context));
    }

    @VisibleForTesting
    protected Vpn(Looper looper, Context context, INetworkManagementService netService, int userHandle, SystemServices systemServices) {
        this.mEnableTeardown = true;
        this.mAlwaysOn = false;
        this.mLockdown = false;
        this.mLockdownWhitelist = Collections.emptyList();
        this.mBlockedUsers = new ArraySet();
        this.mObserver = new BaseNetworkObserver() {
            public void interfaceStatusChanged(String interfaze, boolean up) {
                synchronized (Vpn.this) {
                    if (!up) {
                        if (Vpn.this.mLegacyVpnRunner != null) {
                            Vpn.this.mLegacyVpnRunner.check(interfaze);
                        }
                    }
                }
            }

            public void interfaceRemoved(String interfaze) {
                synchronized (Vpn.this) {
                    if (interfaze.equals(Vpn.this.mInterface) && Vpn.this.jniCheck(interfaze) == 0) {
                        PendingIntent unused = Vpn.this.mStatusIntent = null;
                        Vpn.this.mNetworkCapabilities.setUids((Set) null);
                        Vpn.this.mConfig = null;
                        String unused2 = Vpn.this.mInterface = null;
                        if (Vpn.this.mConnection != null) {
                            Vpn.this.mContext.unbindService(Vpn.this.mConnection);
                            Connection unused3 = Vpn.this.mConnection = null;
                            Vpn.this.agentDisconnect();
                        } else if (Vpn.this.mLegacyVpnRunner != null) {
                            Vpn.this.mLegacyVpnRunner.exit();
                            LegacyVpnRunner unused4 = Vpn.this.mLegacyVpnRunner = null;
                        }
                    }
                }
            }
        };
        this.mContext = context;
        this.mNetd = netService;
        this.mUserHandle = userHandle;
        this.mLooper = looper;
        this.mSystemServices = systemServices;
        this.mPackage = "[Legacy VPN]";
        this.mOwnerUID = getAppUid(this.mPackage, this.mUserHandle);
        this.mIsPackageTargetingAtLeastQ = doesPackageTargetAtLeastQ(this.mPackage);
        try {
            netService.registerObserver(this.mObserver);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Problem registering observer", e);
        }
        this.mNetworkInfo = new NetworkInfo(17, 0, NETWORKTYPE, "");
        this.mNetworkCapabilities = new NetworkCapabilities();
        this.mNetworkCapabilities.addTransportType(4);
        this.mNetworkCapabilities.removeCapability(15);
        updateCapabilities((Network) null);
        loadAlwaysOnPackage();
    }

    public void setEnableTeardown(boolean enableTeardown) {
        this.mEnableTeardown = enableTeardown;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void updateState(NetworkInfo.DetailedState detailedState, String reason) {
        Log.d(TAG, "setting state=" + detailedState + ", reason=" + reason);
        this.mNetworkInfo.setDetailedState(detailedState, reason, (String) null);
        NetworkAgent networkAgent = this.mNetworkAgent;
        if (networkAgent != null) {
            networkAgent.sendNetworkInfo(this.mNetworkInfo);
        }
        updateAlwaysOnNotification(detailedState);
    }

    public synchronized NetworkCapabilities updateCapabilities(Network defaultNetwork) {
        if (this.mConfig == null) {
            return null;
        }
        Network[] underlyingNetworks = this.mConfig.underlyingNetworks;
        boolean isAlwaysMetered = false;
        if (underlyingNetworks == null && defaultNetwork != null) {
            underlyingNetworks = new Network[]{defaultNetwork};
        }
        if (this.mIsPackageTargetingAtLeastQ && this.mConfig.isMetered) {
            isAlwaysMetered = true;
        }
        applyUnderlyingCapabilities((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class), underlyingNetworks, this.mNetworkCapabilities, isAlwaysMetered);
        return new NetworkCapabilities(this.mNetworkCapabilities);
    }

    @VisibleForTesting
    public static void applyUnderlyingCapabilities(ConnectivityManager cm, Network[] underlyingNetworks, NetworkCapabilities caps, boolean isAlwaysMetered) {
        boolean hadUnderlyingNetworks;
        Network[] networkArr = underlyingNetworks;
        NetworkCapabilities networkCapabilities = caps;
        boolean z = true;
        int[] transportTypes = {4};
        int downKbps = 0;
        int downKbps2 = 0;
        boolean upKbps = isAlwaysMetered;
        boolean metered = false;
        boolean roaming = false;
        if (networkArr != null) {
            int length = networkArr.length;
            hadUnderlyingNetworks = false;
            boolean congested = false;
            boolean roaming2 = false;
            boolean metered2 = upKbps;
            int upKbps2 = 0;
            int downKbps3 = 0;
            int[] transportTypes2 = transportTypes;
            int i = 0;
            while (i < length) {
                Network underlying = networkArr[i];
                NetworkCapabilities underlyingCaps = cm.getNetworkCapabilities(underlying);
                if (underlyingCaps != null) {
                    hadUnderlyingNetworks = true;
                    int[] transportTypes3 = underlyingCaps.getTransportTypes();
                    int length2 = transportTypes3.length;
                    int[] transportTypes4 = transportTypes2;
                    int i2 = 0;
                    while (i2 < length2) {
                        transportTypes4 = ArrayUtils.appendInt(transportTypes4, transportTypes3[i2]);
                        i2++;
                        underlying = underlying;
                    }
                    downKbps3 = NetworkCapabilities.minBandwidth(downKbps3, underlyingCaps.getLinkDownstreamBandwidthKbps());
                    upKbps2 = NetworkCapabilities.minBandwidth(upKbps2, underlyingCaps.getLinkUpstreamBandwidthKbps());
                    z = true;
                    metered2 |= !underlyingCaps.hasCapability(11);
                    roaming2 |= !underlyingCaps.hasCapability(18);
                    congested |= !underlyingCaps.hasCapability(20);
                    transportTypes2 = transportTypes4;
                }
                i++;
                networkArr = underlyingNetworks;
            }
            ConnectivityManager connectivityManager = cm;
            transportTypes = transportTypes2;
            downKbps = downKbps3;
            downKbps2 = upKbps2;
            upKbps = metered2;
            metered = roaming2;
            roaming = congested;
        } else {
            ConnectivityManager connectivityManager2 = cm;
            hadUnderlyingNetworks = false;
        }
        if (!hadUnderlyingNetworks) {
            upKbps = true;
            metered = false;
            roaming = false;
        }
        networkCapabilities.setTransportTypes(transportTypes);
        networkCapabilities.setLinkDownstreamBandwidthKbps(downKbps);
        networkCapabilities.setLinkUpstreamBandwidthKbps(downKbps2);
        networkCapabilities.setCapability(11, !upKbps ? z : false);
        networkCapabilities.setCapability(18, !metered ? z : false);
        if (roaming) {
            z = false;
        }
        networkCapabilities.setCapability(20, z);
    }

    public synchronized void setLockdown(boolean lockdown) {
        enforceControlPermissionOrInternalCaller();
        setVpnForcedLocked(lockdown);
        this.mLockdown = lockdown;
        if (this.mAlwaysOn) {
            saveAlwaysOnPackage();
        }
    }

    public synchronized boolean getLockdown() {
        return this.mLockdown;
    }

    public synchronized boolean getAlwaysOn() {
        return this.mAlwaysOn;
    }

    public boolean isAlwaysOnPackageSupported(String packageName) {
        enforceSettingsPermission();
        if (packageName == null) {
            return false;
        }
        PackageManager pm = this.mContext.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfoAsUser(packageName, 0, this.mUserHandle);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Can't find \"" + packageName + "\" when checking always-on support");
        }
        if (appInfo == null || appInfo.targetSdkVersion < 24) {
            return false;
        }
        Intent intent = new Intent("android.net.VpnService");
        intent.setPackage(packageName);
        List<ResolveInfo> services2 = pm.queryIntentServicesAsUser(intent, 128, this.mUserHandle);
        if (services2 == null || services2.size() == 0) {
            return false;
        }
        for (ResolveInfo rInfo : services2) {
            Bundle metaData = rInfo.serviceInfo.metaData;
            if (metaData != null && !metaData.getBoolean("android.net.VpnService.SUPPORTS_ALWAYS_ON", true)) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean setAlwaysOnPackage(String packageName, boolean lockdown, List<String> lockdownWhitelist) {
        enforceControlPermissionOrInternalCaller();
        if (!setAlwaysOnPackageInternal(packageName, lockdown, lockdownWhitelist)) {
            return false;
        }
        saveAlwaysOnPackage();
        return true;
    }

    @GuardedBy({"this"})
    private boolean setAlwaysOnPackageInternal(String packageName, boolean lockdown, List<String> lockdownWhitelist) {
        List<String> list;
        boolean z = false;
        if ("[Legacy VPN]".equals(packageName)) {
            Log.w(TAG, "Not setting legacy VPN \"" + packageName + "\" as always-on.");
            return false;
        }
        if (lockdownWhitelist != null) {
            for (String pkg : lockdownWhitelist) {
                if (pkg.contains(",")) {
                    Log.w(TAG, "Not setting always-on vpn, invalid whitelisted package: " + pkg);
                    return false;
                }
            }
        }
        if (packageName == null) {
            packageName = "[Legacy VPN]";
            this.mAlwaysOn = false;
        } else if (!setPackageAuthorization(packageName, true)) {
            return false;
        } else {
            this.mAlwaysOn = true;
        }
        if (this.mAlwaysOn && lockdown) {
            z = true;
        }
        this.mLockdown = z;
        if (!this.mLockdown || lockdownWhitelist == null) {
            list = Collections.emptyList();
        } else {
            list = Collections.unmodifiableList(new ArrayList(lockdownWhitelist));
        }
        this.mLockdownWhitelist = list;
        if (isCurrentPreparedPackage(packageName)) {
            updateAlwaysOnNotification(this.mNetworkInfo.getDetailedState());
            setVpnForcedLocked(this.mLockdown);
        } else {
            prepareInternal(packageName);
        }
        return true;
    }

    private static boolean isNullOrLegacyVpn(String packageName) {
        return packageName == null || "[Legacy VPN]".equals(packageName);
    }

    public synchronized String getAlwaysOnPackage() {
        enforceControlPermissionOrInternalCaller();
        return this.mAlwaysOn ? this.mPackage : null;
    }

    public synchronized List<String> getLockdownWhitelist() {
        return this.mLockdown ? this.mLockdownWhitelist : null;
    }

    @GuardedBy({"this"})
    private void saveAlwaysOnPackage() {
        long token = Binder.clearCallingIdentity();
        try {
            this.mSystemServices.settingsSecurePutStringForUser("always_on_vpn_app", getAlwaysOnPackage(), this.mUserHandle);
            this.mSystemServices.settingsSecurePutIntForUser("always_on_vpn_lockdown", (!this.mAlwaysOn || !this.mLockdown) ? 0 : 1, this.mUserHandle);
            this.mSystemServices.settingsSecurePutStringForUser("always_on_vpn_lockdown_whitelist", String.join(",", this.mLockdownWhitelist), this.mUserHandle);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @GuardedBy({"this"})
    private void loadAlwaysOnPackage() {
        long token = Binder.clearCallingIdentity();
        try {
            String alwaysOnPackage = this.mSystemServices.settingsSecureGetStringForUser("always_on_vpn_app", this.mUserHandle);
            boolean z = false;
            if (this.mSystemServices.settingsSecureGetIntForUser("always_on_vpn_lockdown", 0, this.mUserHandle) != 0) {
                z = true;
            }
            boolean alwaysOnLockdown = z;
            String whitelistString = this.mSystemServices.settingsSecureGetStringForUser("always_on_vpn_lockdown_whitelist", this.mUserHandle);
            setAlwaysOnPackageInternal(alwaysOnPackage, alwaysOnLockdown, TextUtils.isEmpty(whitelistString) ? Collections.emptyList() : Arrays.asList(whitelistString.split(",")));
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0024, code lost:
        r11 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        ((com.android.server.DeviceIdleController.LocalService) com.android.server.LocalServices.getService(com.android.server.DeviceIdleController.LocalService.class)).addPowerSaveTempWhitelistApp(android.os.Process.myUid(), r0, 60000, r13.mUserHandle, false, "vpn");
        r2 = new android.content.Intent("android.net.VpnService");
        r2.setPackage(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0057, code lost:
        if (r13.mContext.startServiceAsUser(r2, android.os.UserHandle.of(r13.mUserHandle)) == null) goto L_0x005a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005a, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005e, code lost:
        return r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x005f, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        android.util.Log.e(TAG, "VpnService " + r2 + " failed to start", r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x007f, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0080, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0081, code lost:
        android.os.Binder.restoreCallingIdentity(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0084, code lost:
        throw r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startAlwaysOnVpn() {
        /*
            r13 = this;
            monitor-enter(r13)
            java.lang.String r0 = r13.getAlwaysOnPackage()     // Catch:{ all -> 0x0085 }
            r9 = 1
            if (r0 != 0) goto L_0x000a
            monitor-exit(r13)     // Catch:{ all -> 0x0085 }
            return r9
        L_0x000a:
            boolean r1 = r13.isAlwaysOnPackageSupported(r0)     // Catch:{ all -> 0x0085 }
            r10 = 0
            if (r1 != 0) goto L_0x0017
            r1 = 0
            r13.setAlwaysOnPackage(r1, r10, r1)     // Catch:{ all -> 0x0085 }
            monitor-exit(r13)     // Catch:{ all -> 0x0085 }
            return r10
        L_0x0017:
            android.net.NetworkInfo r1 = r13.getNetworkInfo()     // Catch:{ all -> 0x0085 }
            boolean r1 = r1.isConnected()     // Catch:{ all -> 0x0085 }
            if (r1 == 0) goto L_0x0023
            monitor-exit(r13)     // Catch:{ all -> 0x0085 }
            return r9
        L_0x0023:
            monitor-exit(r13)     // Catch:{ all -> 0x0085 }
            long r11 = android.os.Binder.clearCallingIdentity()
            java.lang.Class<com.android.server.DeviceIdleController$LocalService> r1 = com.android.server.DeviceIdleController.LocalService.class
            java.lang.Object r1 = com.android.server.LocalServices.getService(r1)     // Catch:{ all -> 0x0080 }
            com.android.server.DeviceIdleController$LocalService r1 = (com.android.server.DeviceIdleController.LocalService) r1     // Catch:{ all -> 0x0080 }
            int r2 = android.os.Process.myUid()     // Catch:{ all -> 0x0080 }
            r4 = 60000(0xea60, double:2.9644E-319)
            int r6 = r13.mUserHandle     // Catch:{ all -> 0x0080 }
            r7 = 0
            java.lang.String r8 = "vpn"
            r3 = r0
            r1.addPowerSaveTempWhitelistApp(r2, r3, r4, r6, r7, r8)     // Catch:{ all -> 0x0080 }
            android.content.Intent r2 = new android.content.Intent     // Catch:{ all -> 0x0080 }
            java.lang.String r3 = "android.net.VpnService"
            r2.<init>(r3)     // Catch:{ all -> 0x0080 }
            r2.setPackage(r0)     // Catch:{ all -> 0x0080 }
            android.content.Context r3 = r13.mContext     // Catch:{ RuntimeException -> 0x005f }
            int r4 = r13.mUserHandle     // Catch:{ RuntimeException -> 0x005f }
            android.os.UserHandle r4 = android.os.UserHandle.of(r4)     // Catch:{ RuntimeException -> 0x005f }
            android.content.ComponentName r3 = r3.startServiceAsUser(r2, r4)     // Catch:{ RuntimeException -> 0x005f }
            if (r3 == 0) goto L_0x005a
            goto L_0x005b
        L_0x005a:
            r9 = r10
        L_0x005b:
            android.os.Binder.restoreCallingIdentity(r11)
            return r9
        L_0x005f:
            r3 = move-exception
            java.lang.String r4 = "Vpn"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0080 }
            r5.<init>()     // Catch:{ all -> 0x0080 }
            java.lang.String r6 = "VpnService "
            r5.append(r6)     // Catch:{ all -> 0x0080 }
            r5.append(r2)     // Catch:{ all -> 0x0080 }
            java.lang.String r6 = " failed to start"
            r5.append(r6)     // Catch:{ all -> 0x0080 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0080 }
            android.util.Log.e(r4, r5, r3)     // Catch:{ all -> 0x0080 }
            android.os.Binder.restoreCallingIdentity(r11)
            return r10
        L_0x0080:
            r1 = move-exception
            android.os.Binder.restoreCallingIdentity(r11)
            throw r1
        L_0x0085:
            r0 = move-exception
            monitor-exit(r13)     // Catch:{ all -> 0x0085 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.startAlwaysOnVpn():boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002b, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x006b, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean prepare(java.lang.String r4, java.lang.String r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            r0 = 1
            r1 = 0
            if (r4 == 0) goto L_0x0043
            boolean r2 = r3.mAlwaysOn     // Catch:{ all -> 0x0041 }
            if (r2 == 0) goto L_0x0011
            boolean r2 = r3.isCurrentPreparedPackage(r4)     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x0011
            monitor-exit(r3)
            return r1
        L_0x0011:
            boolean r2 = r3.isCurrentPreparedPackage(r4)     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x002c
            java.lang.String r2 = "[Legacy VPN]"
            boolean r2 = r4.equals(r2)     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x002a
            boolean r2 = r3.isVpnUserPreConsented(r4)     // Catch:{ all -> 0x0041 }
            if (r2 == 0) goto L_0x002a
            r3.prepareInternal(r4)     // Catch:{ all -> 0x0041 }
            monitor-exit(r3)
            return r0
        L_0x002a:
            monitor-exit(r3)
            return r1
        L_0x002c:
            java.lang.String r2 = "[Legacy VPN]"
            boolean r2 = r4.equals(r2)     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x0043
            boolean r2 = r3.isVpnUserPreConsented(r4)     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x0043
            java.lang.String r0 = "[Legacy VPN]"
            r3.prepareInternal(r0)     // Catch:{ all -> 0x0041 }
            monitor-exit(r3)
            return r1
        L_0x0041:
            r4 = move-exception
            goto L_0x0068
        L_0x0043:
            if (r5 == 0) goto L_0x006a
            java.lang.String r2 = "[Legacy VPN]"
            boolean r2 = r5.equals(r2)     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x0054
            boolean r2 = r3.isCurrentPreparedPackage(r5)     // Catch:{ all -> 0x0041 }
            if (r2 == 0) goto L_0x0054
            goto L_0x006a
        L_0x0054:
            r3.enforceControlPermission()     // Catch:{ all -> 0x0041 }
            boolean r2 = r3.mAlwaysOn     // Catch:{ all -> 0x0041 }
            if (r2 == 0) goto L_0x0063
            boolean r2 = r3.isCurrentPreparedPackage(r5)     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x0063
            monitor-exit(r3)
            return r1
        L_0x0063:
            r3.prepareInternal(r5)     // Catch:{ all -> 0x0041 }
            monitor-exit(r3)
            return r0
        L_0x0068:
            monitor-exit(r3)
            throw r4
        L_0x006a:
            monitor-exit(r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.prepare(java.lang.String, java.lang.String):boolean");
    }

    private boolean isCurrentPreparedPackage(String packageName) {
        return getAppUid(packageName, this.mUserHandle) == this.mOwnerUID;
    }

    private void prepareInternal(String newPackage) {
        long token = Binder.clearCallingIdentity();
        try {
            if (this.mInterface != null) {
                this.mStatusIntent = null;
                agentDisconnect();
                jniReset(this.mInterface);
                this.mInterface = null;
                this.mNetworkCapabilities.setUids((Set) null);
            }
            if (this.mConnection != null) {
                try {
                    this.mConnection.mService.transact(16777215, Parcel.obtain(), (Parcel) null, 1);
                } catch (Exception e) {
                }
                this.mContext.unbindService(this.mConnection);
                this.mConnection = null;
            } else if (this.mLegacyVpnRunner != null) {
                this.mLegacyVpnRunner.exit();
                this.mLegacyVpnRunner = null;
            }
            this.mNetd.denyProtect(this.mOwnerUID);
        } catch (Exception e2) {
            Log.wtf(TAG, "Failed to disallow UID " + this.mOwnerUID + " to call protect() " + e2);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
        Log.i(TAG, "Switched from " + this.mPackage + " to " + newPackage);
        this.mPackage = newPackage;
        this.mOwnerUID = getAppUid(newPackage, this.mUserHandle);
        this.mIsPackageTargetingAtLeastQ = doesPackageTargetAtLeastQ(newPackage);
        try {
            this.mNetd.allowProtect(this.mOwnerUID);
        } catch (Exception e3) {
            Log.wtf(TAG, "Failed to allow UID " + this.mOwnerUID + " to call protect() " + e3);
        }
        this.mConfig = null;
        updateState(NetworkInfo.DetailedState.IDLE, "prepare");
        setVpnForcedLocked(this.mLockdown);
        Binder.restoreCallingIdentity(token);
    }

    public boolean setPackageAuthorization(String packageName, boolean authorized) {
        enforceControlPermissionOrInternalCaller();
        int uid = getAppUid(packageName, this.mUserHandle);
        if (uid == -1 || "[Legacy VPN]".equals(packageName)) {
            return false;
        }
        long token = Binder.clearCallingIdentity();
        try {
            ((AppOpsManager) this.mContext.getSystemService("appops")).setMode(47, uid, packageName, authorized ? 0 : 1);
            return true;
        } catch (Exception e) {
            Log.wtf(TAG, "Failed to set app ops for package " + packageName + ", uid " + uid, e);
            return false;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private boolean isVpnUserPreConsented(String packageName) {
        return ((AppOpsManager) this.mContext.getSystemService("appops")).noteOpNoThrow(47, Binder.getCallingUid(), packageName) == 0;
    }

    private int getAppUid(String app, int userHandle) {
        int result;
        if ("[Legacy VPN]".equals(app)) {
            return Process.myUid();
        }
        PackageManager pm = this.mContext.getPackageManager();
        long token = Binder.clearCallingIdentity();
        try {
            result = pm.getPackageUidAsUser(app, userHandle);
        } catch (PackageManager.NameNotFoundException e) {
            result = -1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
        Binder.restoreCallingIdentity(token);
        return result;
    }

    private boolean doesPackageTargetAtLeastQ(String packageName) {
        if ("[Legacy VPN]".equals(packageName)) {
            return true;
        }
        try {
            if (this.mContext.getPackageManager().getApplicationInfoAsUser(packageName, 0, this.mUserHandle).targetSdkVersion >= 29) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Can't find \"" + packageName + "\"");
            return false;
        }
    }

    public NetworkInfo getNetworkInfo() {
        return this.mNetworkInfo;
    }

    public int getNetId() {
        NetworkAgent networkAgent = this.mNetworkAgent;
        if (networkAgent != null) {
            return networkAgent.netId;
        }
        return 0;
    }

    private LinkProperties makeLinkProperties() {
        boolean allowIPv4 = this.mConfig.allowIPv4;
        boolean allowIPv6 = this.mConfig.allowIPv6;
        LinkProperties lp = new LinkProperties();
        lp.setInterfaceName(this.mInterface);
        if (this.mConfig.addresses != null) {
            for (LinkAddress address : this.mConfig.addresses) {
                lp.addLinkAddress(address);
                allowIPv4 |= address.getAddress() instanceof Inet4Address;
                allowIPv6 |= address.getAddress() instanceof Inet6Address;
            }
        }
        if (this.mConfig.routes != null) {
            for (RouteInfo route : this.mConfig.routes) {
                lp.addRoute(route);
                InetAddress address2 = route.getDestination().getAddress();
                allowIPv4 |= address2 instanceof Inet4Address;
                allowIPv6 |= address2 instanceof Inet6Address;
            }
        }
        if (this.mConfig.dnsServers != null) {
            for (String dnsServer : this.mConfig.dnsServers) {
                InetAddress address3 = InetAddress.parseNumericAddress(dnsServer);
                lp.addDnsServer(address3);
                allowIPv4 |= address3 instanceof Inet4Address;
                allowIPv6 |= address3 instanceof Inet6Address;
            }
        }
        lp.setHttpProxy(this.mConfig.proxyInfo);
        if (!allowIPv4) {
            lp.addRoute(new RouteInfo(new IpPrefix(Inet4Address.ANY, 0), 7));
        }
        if (!allowIPv6) {
            lp.addRoute(new RouteInfo(new IpPrefix(Inet6Address.ANY, 0), 7));
        }
        StringBuilder buffer = new StringBuilder();
        if (this.mConfig.searchDomains != null) {
            for (String domain : this.mConfig.searchDomains) {
                buffer.append(domain);
                buffer.append(' ');
            }
        }
        lp.setDomains(buffer.toString().trim());
        return lp;
    }

    @VisibleForTesting
    static boolean providesRoutesToMostDestinations(LinkProperties lp) {
        List<RouteInfo> routes = lp.getAllRoutes();
        if (routes.size() > 150) {
            return true;
        }
        Comparator<IpPrefix> prefixLengthComparator = IpPrefix.lengthComparator();
        TreeSet<IpPrefix> ipv4Prefixes = new TreeSet<>(prefixLengthComparator);
        TreeSet<IpPrefix> ipv6Prefixes = new TreeSet<>(prefixLengthComparator);
        for (RouteInfo route : routes) {
            if (route.getType() != 7) {
                IpPrefix destination = route.getDestination();
                if (destination.isIPv4()) {
                    ipv4Prefixes.add(destination);
                } else {
                    ipv6Prefixes.add(destination);
                }
            }
        }
        if (NetworkUtils.routedIPv4AddressCount(ipv4Prefixes) <= MOST_IPV4_ADDRESSES_COUNT && NetworkUtils.routedIPv6AddressCount(ipv6Prefixes).compareTo(MOST_IPV6_ADDRESSES_COUNT) < 0) {
            return false;
        }
        return true;
    }

    private boolean updateLinkPropertiesInPlaceIfPossible(NetworkAgent agent, VpnConfig oldConfig) {
        if (oldConfig.allowBypass != this.mConfig.allowBypass) {
            Log.i(TAG, "Handover not possible due to changes to allowBypass");
            return false;
        } else if (!Objects.equals(oldConfig.allowedApplications, this.mConfig.allowedApplications) || !Objects.equals(oldConfig.disallowedApplications, this.mConfig.disallowedApplications)) {
            Log.i(TAG, "Handover not possible due to changes to whitelisted/blacklisted apps");
            return false;
        } else {
            agent.sendLinkProperties(makeLinkProperties());
            return true;
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    public void agentConnect() {
        LinkProperties lp = makeLinkProperties();
        this.mNetworkCapabilities.addCapability(12);
        this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTING, (String) null, (String) null);
        NetworkMisc networkMisc = new NetworkMisc();
        networkMisc.allowBypass = this.mConfig.allowBypass && !this.mLockdown;
        this.mNetworkCapabilities.setEstablishingVpnAppUid(Binder.getCallingUid());
        this.mNetworkCapabilities.setUids(createUserAndRestrictedProfilesRanges(this.mUserHandle, this.mConfig.allowedApplications, this.mConfig.disallowedApplications));
        long token = Binder.clearCallingIdentity();
        try {
            this.mNetworkAgent = new NetworkAgent(this.mLooper, this.mContext, NETWORKTYPE, this.mNetworkInfo, this.mNetworkCapabilities, lp, 101, networkMisc, -2) {
                public void unwanted() {
                }
            };
            Binder.restoreCallingIdentity(token);
            this.mNetworkInfo.setIsAvailable(true);
            updateState(NetworkInfo.DetailedState.CONNECTED, "agentConnect");
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    private boolean canHaveRestrictedProfile(int userId) {
        long token = Binder.clearCallingIdentity();
        try {
            return UserManager.get(this.mContext).canHaveRestrictedProfile(userId);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void agentDisconnect(NetworkAgent networkAgent) {
        if (networkAgent != null) {
            NetworkInfo networkInfo = new NetworkInfo(this.mNetworkInfo);
            networkInfo.setIsAvailable(false);
            networkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, (String) null, (String) null);
            networkAgent.sendNetworkInfo(networkInfo);
        }
    }

    /* access modifiers changed from: private */
    public void agentDisconnect() {
        if (this.mNetworkInfo.isConnected()) {
            this.mNetworkInfo.setIsAvailable(false);
            updateState(NetworkInfo.DetailedState.DISCONNECTED, "agentDisconnect");
            this.mNetworkAgent = null;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 20 */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:77:0x01a7=Splitter:B:77:0x01a7, B:97:0x0228=Splitter:B:97:0x0228} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized android.os.ParcelFileDescriptor establish(com.android.internal.net.VpnConfig r21) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            monitor-enter(r20)
            android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x022c }
            android.os.UserManager r0 = android.os.UserManager.get(r0)     // Catch:{ all -> 0x022c }
            r3 = r0
            int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x022c }
            int r4 = r1.mOwnerUID     // Catch:{ all -> 0x022c }
            r5 = 0
            if (r0 == r4) goto L_0x0017
            monitor-exit(r20)
            return r5
        L_0x0017:
            java.lang.String r0 = r1.mPackage     // Catch:{ all -> 0x022c }
            boolean r0 = r1.isVpnUserPreConsented(r0)     // Catch:{ all -> 0x022c }
            if (r0 != 0) goto L_0x0021
            monitor-exit(r20)
            return r5
        L_0x0021:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ all -> 0x022c }
            java.lang.String r4 = "android.net.VpnService"
            r0.<init>(r4)     // Catch:{ all -> 0x022c }
            r4 = r0
            java.lang.String r0 = r1.mPackage     // Catch:{ all -> 0x022c }
            java.lang.String r6 = r2.user     // Catch:{ all -> 0x022c }
            r4.setClassName(r0, r6)     // Catch:{ all -> 0x022c }
            long r6 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x022c }
            int r0 = r1.mUserHandle     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            android.content.pm.UserInfo r0 = r3.getUserInfo(r0)     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            boolean r8 = r0.isRestricted()     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            if (r8 != 0) goto L_0x01f8
            android.content.pm.IPackageManager r8 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            r9 = 0
            int r10 = r1.mUserHandle     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            android.content.pm.ResolveInfo r8 = r8.resolveService(r4, r5, r9, r10)     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            if (r8 == 0) goto L_0x01dc
            java.lang.String r9 = "android.permission.BIND_VPN_SERVICE"
            android.content.pm.ServiceInfo r10 = r8.serviceInfo     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            java.lang.String r10 = r10.permission     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            boolean r9 = r9.equals(r10)     // Catch:{ RemoteException -> 0x020a, all -> 0x0205 }
            if (r9 == 0) goto L_0x01bb
            android.os.Binder.restoreCallingIdentity(r6)     // Catch:{ all -> 0x022c }
            com.android.internal.net.VpnConfig r0 = r1.mConfig     // Catch:{ all -> 0x022c }
            r8 = r0
            java.lang.String r0 = r1.mInterface     // Catch:{ all -> 0x022c }
            r9 = r0
            com.android.server.connectivity.Vpn$Connection r0 = r1.mConnection     // Catch:{ all -> 0x022c }
            r10 = r0
            android.net.NetworkAgent r0 = r1.mNetworkAgent     // Catch:{ all -> 0x022c }
            r11 = r0
            android.net.NetworkCapabilities r0 = r1.mNetworkCapabilities     // Catch:{ all -> 0x022c }
            java.util.Set r0 = r0.getUids()     // Catch:{ all -> 0x022c }
            r12 = r0
            int r0 = r2.mtu     // Catch:{ all -> 0x022c }
            int r0 = r1.jniCreate(r0)     // Catch:{ all -> 0x022c }
            android.os.ParcelFileDescriptor r0 = android.os.ParcelFileDescriptor.adoptFd(r0)     // Catch:{ all -> 0x022c }
            r13 = r0
            int r0 = r13.getFd()     // Catch:{ RuntimeException -> 0x01a3 }
            java.lang.String r0 = r1.jniGetName(r0)     // Catch:{ RuntimeException -> 0x01a3 }
            r14 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x01a3 }
            r0.<init>()     // Catch:{ RuntimeException -> 0x01a3 }
            r15 = r0
            java.util.List r0 = r2.addresses     // Catch:{ RuntimeException -> 0x01a3 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ RuntimeException -> 0x01a3 }
        L_0x0090:
            boolean r16 = r0.hasNext()     // Catch:{ RuntimeException -> 0x01a3 }
            if (r16 == 0) goto L_0x00c0
            java.lang.Object r16 = r0.next()     // Catch:{ RuntimeException -> 0x00ba }
            android.net.LinkAddress r16 = (android.net.LinkAddress) r16     // Catch:{ RuntimeException -> 0x00ba }
            r17 = r16
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x00ba }
            r5.<init>()     // Catch:{ RuntimeException -> 0x00ba }
            r18 = r0
            java.lang.String r0 = " "
            r5.append(r0)     // Catch:{ RuntimeException -> 0x00ba }
            r0 = r17
            r5.append(r0)     // Catch:{ RuntimeException -> 0x00ba }
            java.lang.String r5 = r5.toString()     // Catch:{ RuntimeException -> 0x00ba }
            r15.append(r5)     // Catch:{ RuntimeException -> 0x00ba }
            r0 = r18
            r5 = 0
            goto L_0x0090
        L_0x00ba:
            r0 = move-exception
            r17 = r3
            r15 = r4
            goto L_0x01a7
        L_0x00c0:
            java.lang.String r0 = r15.toString()     // Catch:{ RuntimeException -> 0x01a3 }
            int r0 = r1.jniSetAddresses(r14, r0)     // Catch:{ RuntimeException -> 0x01a3 }
            r5 = 1
            if (r0 < r5) goto L_0x0194
            com.android.server.connectivity.Vpn$Connection r0 = new com.android.server.connectivity.Vpn$Connection     // Catch:{ RuntimeException -> 0x01a3 }
            r5 = 0
            r0.<init>()     // Catch:{ RuntimeException -> 0x01a3 }
            r5 = r0
            android.content.Context r0 = r1.mContext     // Catch:{ RuntimeException -> 0x01a3 }
            r17 = r3
            android.os.UserHandle r3 = new android.os.UserHandle     // Catch:{ RuntimeException -> 0x0191 }
            r19 = r15
            int r15 = r1.mUserHandle     // Catch:{ RuntimeException -> 0x0191 }
            r3.<init>(r15)     // Catch:{ RuntimeException -> 0x0191 }
            r15 = 67108865(0x4000001, float:1.504633E-36)
            boolean r0 = r0.bindServiceAsUser(r4, r5, r15, r3)     // Catch:{ RuntimeException -> 0x0191 }
            if (r0 == 0) goto L_0x0175
            r1.mConnection = r5     // Catch:{ RuntimeException -> 0x0191 }
            r1.mInterface = r14     // Catch:{ RuntimeException -> 0x0191 }
            java.lang.String r0 = r1.mPackage     // Catch:{ RuntimeException -> 0x0191 }
            r2.user = r0     // Catch:{ RuntimeException -> 0x0191 }
            java.lang.String r0 = r1.mInterface     // Catch:{ RuntimeException -> 0x0191 }
            r2.interfaze = r0     // Catch:{ RuntimeException -> 0x0191 }
            r15 = r4
            long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ RuntimeException -> 0x01a1 }
            r2.startTime = r3     // Catch:{ RuntimeException -> 0x01a1 }
            r1.mConfig = r2     // Catch:{ RuntimeException -> 0x01a1 }
            if (r8 == 0) goto L_0x0108
            android.net.NetworkAgent r0 = r1.mNetworkAgent     // Catch:{ RuntimeException -> 0x01a1 }
            boolean r0 = r1.updateLinkPropertiesInPlaceIfPossible(r0, r8)     // Catch:{ RuntimeException -> 0x01a1 }
            if (r0 == 0) goto L_0x0108
            goto L_0x0118
        L_0x0108:
            r0 = 0
            r1.mNetworkAgent = r0     // Catch:{ RuntimeException -> 0x01a1 }
            android.net.NetworkInfo$DetailedState r0 = android.net.NetworkInfo.DetailedState.CONNECTING     // Catch:{ RuntimeException -> 0x01a1 }
            java.lang.String r3 = "establish"
            r1.updateState(r0, r3)     // Catch:{ RuntimeException -> 0x01a1 }
            r20.agentConnect()     // Catch:{ RuntimeException -> 0x01a1 }
            r1.agentDisconnect(r11)     // Catch:{ RuntimeException -> 0x01a1 }
        L_0x0118:
            if (r10 == 0) goto L_0x011f
            android.content.Context r0 = r1.mContext     // Catch:{ RuntimeException -> 0x01a1 }
            r0.unbindService(r10)     // Catch:{ RuntimeException -> 0x01a1 }
        L_0x011f:
            if (r9 == 0) goto L_0x012a
            boolean r0 = r9.equals(r14)     // Catch:{ RuntimeException -> 0x01a1 }
            if (r0 != 0) goto L_0x012a
            r1.jniReset(r9)     // Catch:{ RuntimeException -> 0x01a1 }
        L_0x012a:
            java.io.FileDescriptor r0 = r13.getFileDescriptor()     // Catch:{ IOException -> 0x0159 }
            boolean r3 = r2.blocking     // Catch:{ IOException -> 0x0159 }
            libcore.io.IoUtils.setBlocking(r0, r3)     // Catch:{ IOException -> 0x0159 }
            java.lang.String r0 = "Vpn"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x022c }
            r3.<init>()     // Catch:{ all -> 0x022c }
            java.lang.String r4 = "Established by "
            r3.append(r4)     // Catch:{ all -> 0x022c }
            java.lang.String r4 = r2.user     // Catch:{ all -> 0x022c }
            r3.append(r4)     // Catch:{ all -> 0x022c }
            java.lang.String r4 = " on "
            r3.append(r4)     // Catch:{ all -> 0x022c }
            java.lang.String r4 = r1.mInterface     // Catch:{ all -> 0x022c }
            r3.append(r4)     // Catch:{ all -> 0x022c }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x022c }
            android.util.Log.i(r0, r3)     // Catch:{ all -> 0x022c }
            monitor-exit(r20)
            return r13
        L_0x0159:
            r0 = move-exception
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ RuntimeException -> 0x01a1 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x01a1 }
            r4.<init>()     // Catch:{ RuntimeException -> 0x01a1 }
            r16 = r5
            java.lang.String r5 = "Cannot set tunnel's fd as blocking="
            r4.append(r5)     // Catch:{ RuntimeException -> 0x01a1 }
            boolean r5 = r2.blocking     // Catch:{ RuntimeException -> 0x01a1 }
            r4.append(r5)     // Catch:{ RuntimeException -> 0x01a1 }
            java.lang.String r4 = r4.toString()     // Catch:{ RuntimeException -> 0x01a1 }
            r3.<init>(r4, r0)     // Catch:{ RuntimeException -> 0x01a1 }
            throw r3     // Catch:{ RuntimeException -> 0x01a1 }
        L_0x0175:
            r15 = r4
            r16 = r5
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ RuntimeException -> 0x01a1 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x01a1 }
            r3.<init>()     // Catch:{ RuntimeException -> 0x01a1 }
            java.lang.String r4 = "Cannot bind "
            r3.append(r4)     // Catch:{ RuntimeException -> 0x01a1 }
            java.lang.String r4 = r2.user     // Catch:{ RuntimeException -> 0x01a1 }
            r3.append(r4)     // Catch:{ RuntimeException -> 0x01a1 }
            java.lang.String r3 = r3.toString()     // Catch:{ RuntimeException -> 0x01a1 }
            r0.<init>(r3)     // Catch:{ RuntimeException -> 0x01a1 }
            throw r0     // Catch:{ RuntimeException -> 0x01a1 }
        L_0x0191:
            r0 = move-exception
            r15 = r4
            goto L_0x01a7
        L_0x0194:
            r17 = r3
            r19 = r15
            r15 = r4
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ RuntimeException -> 0x01a1 }
            java.lang.String r3 = "At least one address must be specified"
            r0.<init>(r3)     // Catch:{ RuntimeException -> 0x01a1 }
            throw r0     // Catch:{ RuntimeException -> 0x01a1 }
        L_0x01a1:
            r0 = move-exception
            goto L_0x01a7
        L_0x01a3:
            r0 = move-exception
            r17 = r3
            r15 = r4
        L_0x01a7:
            libcore.io.IoUtils.closeQuietly(r13)     // Catch:{ all -> 0x022c }
            r20.agentDisconnect()     // Catch:{ all -> 0x022c }
            r1.mConfig = r8     // Catch:{ all -> 0x022c }
            r1.mConnection = r10     // Catch:{ all -> 0x022c }
            android.net.NetworkCapabilities r3 = r1.mNetworkCapabilities     // Catch:{ all -> 0x022c }
            r3.setUids(r12)     // Catch:{ all -> 0x022c }
            r1.mNetworkAgent = r11     // Catch:{ all -> 0x022c }
            r1.mInterface = r9     // Catch:{ all -> 0x022c }
            throw r0     // Catch:{ all -> 0x022c }
        L_0x01bb:
            r17 = r3
            r15 = r4
            java.lang.SecurityException r3 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x0203 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0203 }
            r4.<init>()     // Catch:{ RemoteException -> 0x0203 }
            java.lang.String r5 = r2.user     // Catch:{ RemoteException -> 0x0203 }
            r4.append(r5)     // Catch:{ RemoteException -> 0x0203 }
            java.lang.String r5 = " does not require "
            r4.append(r5)     // Catch:{ RemoteException -> 0x0203 }
            java.lang.String r5 = "android.permission.BIND_VPN_SERVICE"
            r4.append(r5)     // Catch:{ RemoteException -> 0x0203 }
            java.lang.String r4 = r4.toString()     // Catch:{ RemoteException -> 0x0203 }
            r3.<init>(r4)     // Catch:{ RemoteException -> 0x0203 }
            throw r3     // Catch:{ RemoteException -> 0x0203 }
        L_0x01dc:
            r17 = r3
            r15 = r4
            java.lang.SecurityException r3 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x0203 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0203 }
            r4.<init>()     // Catch:{ RemoteException -> 0x0203 }
            java.lang.String r5 = "Cannot find "
            r4.append(r5)     // Catch:{ RemoteException -> 0x0203 }
            java.lang.String r5 = r2.user     // Catch:{ RemoteException -> 0x0203 }
            r4.append(r5)     // Catch:{ RemoteException -> 0x0203 }
            java.lang.String r4 = r4.toString()     // Catch:{ RemoteException -> 0x0203 }
            r3.<init>(r4)     // Catch:{ RemoteException -> 0x0203 }
            throw r3     // Catch:{ RemoteException -> 0x0203 }
        L_0x01f8:
            r17 = r3
            r15 = r4
            java.lang.SecurityException r3 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x0203 }
            java.lang.String r4 = "Restricted users cannot establish VPNs"
            r3.<init>(r4)     // Catch:{ RemoteException -> 0x0203 }
            throw r3     // Catch:{ RemoteException -> 0x0203 }
        L_0x0203:
            r0 = move-exception
            goto L_0x020e
        L_0x0205:
            r0 = move-exception
            r17 = r3
            r15 = r4
            goto L_0x0228
        L_0x020a:
            r0 = move-exception
            r17 = r3
            r15 = r4
        L_0x020e:
            java.lang.SecurityException r3 = new java.lang.SecurityException     // Catch:{ all -> 0x0227 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0227 }
            r4.<init>()     // Catch:{ all -> 0x0227 }
            java.lang.String r5 = "Cannot find "
            r4.append(r5)     // Catch:{ all -> 0x0227 }
            java.lang.String r5 = r2.user     // Catch:{ all -> 0x0227 }
            r4.append(r5)     // Catch:{ all -> 0x0227 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0227 }
            r3.<init>(r4)     // Catch:{ all -> 0x0227 }
            throw r3     // Catch:{ all -> 0x0227 }
        L_0x0227:
            r0 = move-exception
        L_0x0228:
            android.os.Binder.restoreCallingIdentity(r6)     // Catch:{ all -> 0x022c }
            throw r0     // Catch:{ all -> 0x022c }
        L_0x022c:
            r0 = move-exception
            monitor-exit(r20)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.establish(com.android.internal.net.VpnConfig):android.os.ParcelFileDescriptor");
    }

    private boolean isRunningLocked() {
        return (this.mNetworkAgent == null || this.mInterface == null) ? false : true;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isCallerEstablishedOwnerLocked() {
        return isRunningLocked() && Binder.getCallingUid() == this.mOwnerUID;
    }

    private SortedSet<Integer> getAppsUids(List<String> packageNames, int userHandle) {
        SortedSet<Integer> uids = new TreeSet<>();
        for (String app : packageNames) {
            int uid = getAppUid(app, userHandle);
            if (uid != -1) {
                uids.add(Integer.valueOf(uid));
            }
        }
        return uids;
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Set<UidRange> createUserAndRestrictedProfilesRanges(int userHandle, List<String> allowedApplications, List<String> disallowedApplications) {
        Set<UidRange> ranges = new ArraySet<>();
        addUserToRanges(ranges, userHandle, allowedApplications, disallowedApplications);
        if (canHaveRestrictedProfile(userHandle)) {
            long token = Binder.clearCallingIdentity();
            try {
                List<UserInfo> users = UserManager.get(this.mContext).getUsers(true);
                Binder.restoreCallingIdentity(token);
                for (UserInfo user : users) {
                    if (VpnInjector.isSpecialUser(this.mContext, userHandle, user.id) || (user.isRestricted() && user.restrictedProfileParentId == userHandle)) {
                        addUserToRanges(ranges, user.id, allowedApplications, disallowedApplications);
                    }
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }
        return ranges;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void addUserToRanges(Set<UidRange> ranges, int userHandle, List<String> allowedApplications, List<String> disallowedApplications) {
        if (allowedApplications != null) {
            int start = -1;
            int stop = -1;
            for (Integer intValue : getAppsUids(allowedApplications, userHandle)) {
                int uid = intValue.intValue();
                if (start == -1) {
                    start = uid;
                } else if (uid != stop + 1) {
                    ranges.add(new UidRange(start, stop));
                    start = uid;
                }
                stop = uid;
            }
            if (start != -1) {
                ranges.add(new UidRange(start, stop));
            }
        } else if (disallowedApplications != null) {
            UidRange userRange = UidRange.createForUser(userHandle);
            int start2 = userRange.start;
            for (Integer intValue2 : getAppsUids(disallowedApplications, userHandle)) {
                int uid2 = intValue2.intValue();
                if (uid2 == start2) {
                    start2++;
                } else {
                    ranges.add(new UidRange(start2, uid2 - 1));
                    start2 = uid2 + 1;
                }
            }
            if (start2 <= userRange.stop) {
                ranges.add(new UidRange(start2, userRange.stop));
            }
        } else {
            ranges.add(UidRange.createForUser(userHandle));
        }
    }

    private static List<UidRange> uidRangesForUser(int userHandle, Set<UidRange> existingRanges) {
        UidRange userRange = UidRange.createForUser(userHandle);
        List<UidRange> ranges = new ArrayList<>();
        for (UidRange range : existingRanges) {
            if (userRange.containsRange(range)) {
                ranges.add(range);
            }
        }
        return ranges;
    }

    public void onUserAdded(int userHandle) {
        UserInfo user = UserManager.get(this.mContext).getUserInfo(userHandle);
        if (user.isRestricted() && user.restrictedProfileParentId == this.mUserHandle) {
            synchronized (this) {
                Set<UidRange> existingRanges = this.mNetworkCapabilities.getUids();
                if (existingRanges != null) {
                    try {
                        addUserToRanges(existingRanges, userHandle, this.mConfig.allowedApplications, this.mConfig.disallowedApplications);
                        this.mNetworkCapabilities.setUids(existingRanges);
                    } catch (Exception e) {
                        Log.wtf(TAG, "Failed to add restricted user to owner", e);
                    }
                }
                setVpnForcedLocked(this.mLockdown);
            }
        }
    }

    public void onUserRemoved(int userHandle) {
        UserInfo user = UserManager.get(this.mContext).getUserInfo(userHandle);
        if (user.isRestricted() && user.restrictedProfileParentId == this.mUserHandle) {
            synchronized (this) {
                Set<UidRange> existingRanges = this.mNetworkCapabilities.getUids();
                if (existingRanges != null) {
                    try {
                        existingRanges.removeAll(uidRangesForUser(userHandle, existingRanges));
                        this.mNetworkCapabilities.setUids(existingRanges);
                    } catch (Exception e) {
                        Log.wtf(TAG, "Failed to remove restricted user to owner", e);
                    }
                }
                setVpnForcedLocked(this.mLockdown);
            }
        }
    }

    public synchronized void onUserStopped() {
        setLockdown(false);
        this.mAlwaysOn = false;
        agentDisconnect();
    }

    @GuardedBy({"this"})
    private void setVpnForcedLocked(boolean enforce) {
        List<String> exemptedPackages;
        if (isNullOrLegacyVpn(this.mPackage)) {
            exemptedPackages = null;
        } else {
            exemptedPackages = new ArrayList<>(this.mLockdownWhitelist);
            exemptedPackages.add(this.mPackage);
        }
        Set<UidRange> removedRanges = new ArraySet<>(this.mBlockedUsers);
        Set<UidRange> addedRanges = Collections.emptySet();
        if (enforce) {
            addedRanges = createUserAndRestrictedProfilesRanges(this.mUserHandle, (List<String>) null, exemptedPackages);
            for (UidRange range : addedRanges) {
                if (range.start == 0) {
                    addedRanges.remove(range);
                    if (range.stop != 0) {
                        addedRanges.add(new UidRange(1, range.stop));
                    }
                }
            }
            removedRanges.removeAll(addedRanges);
            addedRanges.removeAll(this.mBlockedUsers);
        }
        setAllowOnlyVpnForUids(false, removedRanges);
        setAllowOnlyVpnForUids(true, addedRanges);
    }

    @GuardedBy({"this"})
    private boolean setAllowOnlyVpnForUids(boolean enforce, Collection<UidRange> ranges) {
        if (ranges.size() == 0) {
            return true;
        }
        try {
            this.mNetd.setAllowOnlyVpnForUids(enforce, (UidRange[]) ranges.toArray(new UidRange[ranges.size()]));
            if (enforce) {
                this.mBlockedUsers.addAll(ranges);
            } else {
                this.mBlockedUsers.removeAll(ranges);
            }
            return true;
        } catch (RemoteException | RuntimeException e) {
            Log.e(TAG, "Updating blocked=" + enforce + " for UIDs " + Arrays.toString(ranges.toArray()) + " failed", e);
            return false;
        }
    }

    public VpnConfig getVpnConfig() {
        enforceControlPermission();
        return this.mConfig;
    }

    /* Debug info: failed to restart local var, previous not found, register: 1 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    @java.lang.Deprecated
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void interfaceStatusChanged(java.lang.String r2, boolean r3) {
        /*
            r1 = this;
            monitor-enter(r1)
            android.net.INetworkManagementEventObserver r0 = r1.mObserver     // Catch:{ RemoteException -> 0x000c, all -> 0x0009 }
            r0.interfaceStatusChanged(r2, r3)     // Catch:{ RemoteException -> 0x0007, all -> 0x0009 }
            goto L_0x000d
        L_0x0007:
            r0 = move-exception
            goto L_0x000d
        L_0x0009:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        L_0x000c:
            r0 = move-exception
        L_0x000d:
            monitor-exit(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.interfaceStatusChanged(java.lang.String, boolean):void");
    }

    private void enforceControlPermission() {
        this.mContext.enforceCallingPermission("android.permission.CONTROL_VPN", "Unauthorized Caller");
    }

    private void enforceControlPermissionOrInternalCaller() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONTROL_VPN", "Unauthorized Caller");
    }

    private void enforceSettingsPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_SETTINGS", "Unauthorized Caller");
    }

    private class Connection implements ServiceConnection {
        /* access modifiers changed from: private */
        public IBinder mService;

        private Connection() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            this.mService = service;
        }

        public void onServiceDisconnected(ComponentName name) {
            this.mService = null;
        }
    }

    /* access modifiers changed from: private */
    public void prepareStatusIntent() {
        long token = Binder.clearCallingIdentity();
        try {
            this.mStatusIntent = VpnConfig.getIntentForStatusPanel(this.mContext);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public synchronized boolean addAddress(String address, int prefixLength) {
        if (!isCallerEstablishedOwnerLocked()) {
            return false;
        }
        boolean success = jniAddAddress(this.mInterface, address, prefixLength);
        this.mNetworkAgent.sendLinkProperties(makeLinkProperties());
        return success;
    }

    public synchronized boolean removeAddress(String address, int prefixLength) {
        if (!isCallerEstablishedOwnerLocked()) {
            return false;
        }
        boolean success = jniDelAddress(this.mInterface, address, prefixLength);
        this.mNetworkAgent.sendLinkProperties(makeLinkProperties());
        return success;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003b, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean setUnderlyingNetworks(android.net.Network[] r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            boolean r0 = r5.isCallerEstablishedOwnerLocked()     // Catch:{ all -> 0x003d }
            r1 = 0
            if (r0 != 0) goto L_0x000a
            monitor-exit(r5)
            return r1
        L_0x000a:
            r0 = 0
            if (r6 != 0) goto L_0x0012
            com.android.internal.net.VpnConfig r1 = r5.mConfig     // Catch:{ all -> 0x003d }
            r1.underlyingNetworks = r0     // Catch:{ all -> 0x003d }
            goto L_0x003a
        L_0x0012:
            com.android.internal.net.VpnConfig r2 = r5.mConfig     // Catch:{ all -> 0x003d }
            int r3 = r6.length     // Catch:{ all -> 0x003d }
            android.net.Network[] r3 = new android.net.Network[r3]     // Catch:{ all -> 0x003d }
            r2.underlyingNetworks = r3     // Catch:{ all -> 0x003d }
        L_0x001a:
            int r2 = r6.length     // Catch:{ all -> 0x003d }
            if (r1 >= r2) goto L_0x003a
            r2 = r6[r1]     // Catch:{ all -> 0x003d }
            if (r2 != 0) goto L_0x0028
            com.android.internal.net.VpnConfig r2 = r5.mConfig     // Catch:{ all -> 0x003d }
            android.net.Network[] r2 = r2.underlyingNetworks     // Catch:{ all -> 0x003d }
            r2[r1] = r0     // Catch:{ all -> 0x003d }
            goto L_0x0037
        L_0x0028:
            com.android.internal.net.VpnConfig r2 = r5.mConfig     // Catch:{ all -> 0x003d }
            android.net.Network[] r2 = r2.underlyingNetworks     // Catch:{ all -> 0x003d }
            android.net.Network r3 = new android.net.Network     // Catch:{ all -> 0x003d }
            r4 = r6[r1]     // Catch:{ all -> 0x003d }
            int r4 = r4.netId     // Catch:{ all -> 0x003d }
            r3.<init>(r4)     // Catch:{ all -> 0x003d }
            r2[r1] = r3     // Catch:{ all -> 0x003d }
        L_0x0037:
            int r1 = r1 + 1
            goto L_0x001a
        L_0x003a:
            monitor-exit(r5)
            r0 = 1
            return r0
        L_0x003d:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.setUnderlyingNetworks(android.net.Network[]):boolean");
    }

    public synchronized Network[] getUnderlyingNetworks() {
        if (!isRunningLocked()) {
            return null;
        }
        return this.mConfig.underlyingNetworks;
    }

    public synchronized VpnInfo getVpnInfo() {
        if (!isRunningLocked()) {
            return null;
        }
        VpnInfo info = new VpnInfo();
        info.ownerUid = this.mOwnerUID;
        info.vpnIface = this.mInterface;
        return info;
    }

    public synchronized boolean appliesToUid(int uid) {
        if (!isRunningLocked()) {
            return false;
        }
        return this.mNetworkCapabilities.appliesToUid(uid);
    }

    public synchronized boolean isBlockingUid(int uid) {
        if (this.mNetworkInfo.isConnected()) {
            return !appliesToUid(uid);
        }
        return UidRange.containsUid(this.mBlockedUsers, uid);
    }

    private void updateAlwaysOnNotification(NetworkInfo.DetailedState networkState) {
        boolean visible = this.mAlwaysOn && networkState != NetworkInfo.DetailedState.CONNECTED;
        UserHandle user = UserHandle.of(this.mUserHandle);
        long token = Binder.clearCallingIdentity();
        try {
            NotificationManager notificationManager = NotificationManager.from(this.mContext);
            if (!visible) {
                notificationManager.cancelAsUser(TAG, 17, user);
                return;
            }
            Intent intent = new Intent();
            intent.setComponent(ComponentName.unflattenFromString(this.mContext.getString(17039715)));
            intent.putExtra("lockdown", this.mLockdown);
            intent.addFlags(268435456);
            notificationManager.notifyAsUser(TAG, 17, new Notification.Builder(this.mContext, SystemNotificationChannels.VPN).setSmallIcon(17303829).setContentTitle(this.mContext.getString(17041331)).setContentText(this.mContext.getString(17041328)).setContentIntent(this.mSystemServices.pendingIntentGetActivityAsUser(intent, 201326592, user)).setCategory("sys").setVisibility(1).setOngoing(true).setColor(this.mContext.getColor(17170460)).build(), user);
            Binder.restoreCallingIdentity(token);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @VisibleForTesting
    public static class SystemServices {
        private final Context mContext;

        public SystemServices(Context context) {
            this.mContext = context;
        }

        public PendingIntent pendingIntentGetActivityAsUser(Intent intent, int flags, UserHandle user) {
            return PendingIntent.getActivityAsUser(this.mContext, 0, intent, flags, (Bundle) null, user);
        }

        public void settingsSecurePutStringForUser(String key, String value, int userId) {
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), key, value, userId);
        }

        public void settingsSecurePutIntForUser(String key, int value, int userId) {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), key, value, userId);
        }

        public String settingsSecureGetStringForUser(String key, int userId) {
            return Settings.Secure.getStringForUser(this.mContext.getContentResolver(), key, userId);
        }

        public int settingsSecureGetIntForUser(String key, int def, int userId) {
            return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), key, def, userId);
        }
    }

    private static RouteInfo findIPv4DefaultRoute(LinkProperties prop) {
        for (RouteInfo route : prop.getAllRoutes()) {
            if (route.isDefaultRoute() && (route.getGateway() instanceof Inet4Address)) {
                return route;
            }
        }
        throw new IllegalStateException("Unable to find IPv4 default gateway");
    }

    public void startLegacyVpn(VpnProfile profile, KeyStore keyStore, LinkProperties egress) {
        enforceControlPermission();
        long token = Binder.clearCallingIdentity();
        try {
            startLegacyVpnPrivileged(profile, keyStore, egress);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void startLegacyVpnPrivileged(VpnProfile profile, KeyStore keyStore, LinkProperties egress) {
        VpnProfile vpnProfile = profile;
        KeyStore keyStore2 = keyStore;
        UserManager mgr = UserManager.get(this.mContext);
        if (mgr.getUserInfo(this.mUserHandle).isRestricted() || mgr.hasUserRestriction("no_config_vpn", new UserHandle(this.mUserHandle))) {
            throw new SecurityException("Restricted users cannot establish VPNs");
        }
        RouteInfo ipv4DefaultRoute = findIPv4DefaultRoute(egress);
        String gateway = ipv4DefaultRoute.getGateway().getHostAddress();
        String iface = ipv4DefaultRoute.getInterface();
        String privateKey = "";
        String userCert = "";
        String caCert = "";
        String serverCert = "";
        String str = null;
        if (!vpnProfile.ipsecUserCert.isEmpty()) {
            privateKey = "USRPKEY_" + vpnProfile.ipsecUserCert;
            byte[] value = keyStore2.get("USRCERT_" + vpnProfile.ipsecUserCert);
            userCert = value == null ? null : new String(value, StandardCharsets.UTF_8);
        }
        if (!vpnProfile.ipsecCaCert.isEmpty()) {
            byte[] value2 = keyStore2.get("CACERT_" + vpnProfile.ipsecCaCert);
            caCert = value2 == null ? null : new String(value2, StandardCharsets.UTF_8);
        }
        if (!vpnProfile.ipsecServerCert.isEmpty()) {
            byte[] value3 = keyStore2.get("USRCERT_" + vpnProfile.ipsecServerCert);
            if (value3 != null) {
                str = new String(value3, StandardCharsets.UTF_8);
            }
            serverCert = str;
        }
        if (privateKey == null || userCert == null || caCert == null || serverCert == null) {
            throw new IllegalStateException("Cannot load credentials");
        }
        String[] racoon = null;
        int i = vpnProfile.type;
        if (i == 1) {
            racoon = new String[]{iface, vpnProfile.server, "udppsk", vpnProfile.ipsecIdentifier, vpnProfile.ipsecSecret, "1701"};
        } else if (i == 2) {
            racoon = new String[]{iface, vpnProfile.server, "udprsa", privateKey, userCert, caCert, serverCert, "1701"};
        } else if (i == 3) {
            racoon = new String[]{iface, vpnProfile.server, "xauthpsk", vpnProfile.ipsecIdentifier, vpnProfile.ipsecSecret, vpnProfile.username, vpnProfile.password, "", gateway};
        } else if (i == 4) {
            racoon = new String[]{iface, vpnProfile.server, "xauthrsa", privateKey, userCert, caCert, serverCert, vpnProfile.username, vpnProfile.password, "", gateway};
        } else if (i == 5) {
            racoon = new String[]{iface, vpnProfile.server, "hybridrsa", caCert, serverCert, vpnProfile.username, vpnProfile.password, "", gateway};
        }
        String[] mtpd = null;
        int i2 = vpnProfile.type;
        if (i2 == 0) {
            String[] strArr = new String[20];
            strArr[0] = iface;
            strArr[1] = "pptp";
            strArr[2] = vpnProfile.server;
            strArr[3] = "1723";
            strArr[4] = com.android.server.pm.Settings.ATTR_NAME;
            strArr[5] = vpnProfile.username;
            strArr[6] = "password";
            strArr[7] = vpnProfile.password;
            strArr[8] = "linkname";
            strArr[9] = "vpn";
            strArr[10] = "refuse-eap";
            strArr[11] = "nodefaultroute";
            strArr[12] = "usepeerdns";
            strArr[13] = "idle";
            strArr[14] = "1800";
            strArr[15] = "mtu";
            strArr[16] = "1400";
            strArr[17] = "mru";
            strArr[18] = "1400";
            strArr[19] = vpnProfile.mppe ? "+mppe" : "nomppe";
            mtpd = strArr;
        } else if (i2 == 1 || i2 == 2) {
            mtpd = new String[]{iface, "l2tp", vpnProfile.server, "1701", vpnProfile.l2tpSecret, com.android.server.pm.Settings.ATTR_NAME, vpnProfile.username, "password", vpnProfile.password, "linkname", "vpn", "refuse-eap", "nodefaultroute", "usepeerdns", "idle", "1800", "mtu", "1400", "mru", "1400"};
        }
        VpnConfig config = new VpnConfig();
        config.legacy = true;
        config.user = vpnProfile.key;
        config.interfaze = iface;
        config.session = vpnProfile.name;
        config.isMetered = false;
        config.proxyInfo = vpnProfile.proxy;
        config.addLegacyRoutes(vpnProfile.routes);
        if (!vpnProfile.dnsServers.isEmpty()) {
            config.dnsServers = Arrays.asList(vpnProfile.dnsServers.split(" +"));
        }
        if (!vpnProfile.searchDomains.isEmpty()) {
            config.searchDomains = Arrays.asList(vpnProfile.searchDomains.split(" +"));
        }
        startLegacyVpn(config, racoon, mtpd, vpnProfile);
    }

    private synchronized void startLegacyVpn(VpnConfig config, String[] racoon, String[] mtpd, VpnProfile profile) {
        stopLegacyVpnPrivileged();
        prepareInternal("[Legacy VPN]");
        updateState(NetworkInfo.DetailedState.CONNECTING, "startLegacyVpn");
        this.mLegacyVpnRunner = new LegacyVpnRunner(config, racoon, mtpd, profile);
        this.mLegacyVpnRunner.start();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0015, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void stopLegacyVpnPrivileged() {
        /*
            r2 = this;
            monitor-enter(r2)
            com.android.server.connectivity.Vpn$LegacyVpnRunner r0 = r2.mLegacyVpnRunner     // Catch:{ all -> 0x0019 }
            if (r0 == 0) goto L_0x0017
            com.android.server.connectivity.Vpn$LegacyVpnRunner r0 = r2.mLegacyVpnRunner     // Catch:{ all -> 0x0019 }
            r0.exit()     // Catch:{ all -> 0x0019 }
            r0 = 0
            r2.mLegacyVpnRunner = r0     // Catch:{ all -> 0x0019 }
            java.lang.String r0 = "LegacyVpnRunner"
            monitor-enter(r0)     // Catch:{ all -> 0x0019 }
            monitor-exit(r0)     // Catch:{ all -> 0x0012 }
            goto L_0x0017
        L_0x0012:
            r1 = move-exception
        L_0x0013:
            monitor-exit(r0)     // Catch:{ all -> 0x0015 }
            throw r1     // Catch:{ all -> 0x0019 }
        L_0x0015:
            r1 = move-exception
            goto L_0x0013
        L_0x0017:
            monitor-exit(r2)
            return
        L_0x0019:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.stopLegacyVpnPrivileged():void");
    }

    public synchronized LegacyVpnInfo getLegacyVpnInfo() {
        enforceControlPermission();
        return getLegacyVpnInfoPrivileged();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.android.internal.net.LegacyVpnInfo getLegacyVpnInfoPrivileged() {
        /*
            r2 = this;
            monitor-enter(r2)
            com.android.server.connectivity.Vpn$LegacyVpnRunner r0 = r2.mLegacyVpnRunner     // Catch:{ all -> 0x0029 }
            if (r0 != 0) goto L_0x0008
            r0 = 0
            monitor-exit(r2)
            return r0
        L_0x0008:
            com.android.internal.net.LegacyVpnInfo r0 = new com.android.internal.net.LegacyVpnInfo     // Catch:{ all -> 0x0029 }
            r0.<init>()     // Catch:{ all -> 0x0029 }
            com.android.internal.net.VpnConfig r1 = r2.mConfig     // Catch:{ all -> 0x0029 }
            java.lang.String r1 = r1.user     // Catch:{ all -> 0x0029 }
            r0.key = r1     // Catch:{ all -> 0x0029 }
            android.net.NetworkInfo r1 = r2.mNetworkInfo     // Catch:{ all -> 0x0029 }
            int r1 = com.android.internal.net.LegacyVpnInfo.stateFromNetworkInfo(r1)     // Catch:{ all -> 0x0029 }
            r0.state = r1     // Catch:{ all -> 0x0029 }
            android.net.NetworkInfo r1 = r2.mNetworkInfo     // Catch:{ all -> 0x0029 }
            boolean r1 = r1.isConnected()     // Catch:{ all -> 0x0029 }
            if (r1 == 0) goto L_0x0027
            android.app.PendingIntent r1 = r2.mStatusIntent     // Catch:{ all -> 0x0029 }
            r0.intent = r1     // Catch:{ all -> 0x0029 }
        L_0x0027:
            monitor-exit(r2)
            return r0
        L_0x0029:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.getLegacyVpnInfoPrivileged():com.android.internal.net.LegacyVpnInfo");
    }

    public VpnConfig getLegacyVpnConfig() {
        if (this.mLegacyVpnRunner != null) {
            return this.mConfig;
        }
        return null;
    }

    private class LegacyVpnRunner extends Thread {
        private static final String TAG = "LegacyVpnRunner";
        private final String[][] mArguments;
        private long mBringupStartTime = -1;
        private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                NetworkInfo info;
                if (Vpn.this.mEnableTeardown && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE") && intent.getIntExtra("networkType", -1) == LegacyVpnRunner.this.mOuterConnection.get() && (info = (NetworkInfo) intent.getExtra("networkInfo")) != null && !info.isConnectedOrConnecting()) {
                    try {
                        Vpn.this.mObserver.interfaceStatusChanged(LegacyVpnRunner.this.mOuterInterface, false);
                    } catch (RemoteException e) {
                    }
                }
            }
        };
        private final String[] mDaemons;
        /* access modifiers changed from: private */
        public final AtomicInteger mOuterConnection = new AtomicInteger(-1);
        /* access modifiers changed from: private */
        public final String mOuterInterface;
        private final VpnProfile mProfile;
        private final LocalSocket[] mSockets;

        LegacyVpnRunner(VpnConfig config, String[] racoon, String[] mtpd, VpnProfile profile) {
            super(TAG);
            NetworkInfo networkInfo;
            Vpn.this.mConfig = config;
            this.mDaemons = new String[]{"racoon", "mtpd"};
            this.mArguments = new String[][]{racoon, mtpd};
            this.mSockets = new LocalSocket[this.mDaemons.length];
            this.mOuterInterface = Vpn.this.mConfig.interfaze;
            this.mProfile = profile;
            if (!TextUtils.isEmpty(this.mOuterInterface)) {
                ConnectivityManager cm = ConnectivityManager.from(Vpn.this.mContext);
                for (Network network : cm.getAllNetworks()) {
                    LinkProperties lp = cm.getLinkProperties(network);
                    if (!(lp == null || !lp.getAllInterfaceNames().contains(this.mOuterInterface) || (networkInfo = cm.getNetworkInfo(network)) == null)) {
                        this.mOuterConnection.set(networkInfo.getType());
                    }
                }
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            Vpn.this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
        }

        public void check(String interfaze) {
            if (interfaze.equals(this.mOuterInterface)) {
                Log.i(TAG, "Legacy VPN is going down with " + interfaze);
                exit();
            }
        }

        public void exit() {
            interrupt();
            Vpn.this.agentDisconnect();
            try {
                Vpn.this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            } catch (IllegalArgumentException e) {
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        /*  JADX ERROR: StackOverflow in pass: MarkFinallyVisitor
            jadx.core.utils.exceptions.JadxOverflowException: 
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
            	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
            */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:16:0x0030=Splitter:B:16:0x0030, B:28:0x0051=Splitter:B:28:0x0051, B:41:0x0073=Splitter:B:41:0x0073} */
        public void run() {
            /*
                r9 = this;
                java.lang.String r0 = "LegacyVpnRunner"
                java.lang.String r1 = "Waiting"
                android.util.Log.v(r0, r1)
                java.lang.String r0 = "LegacyVpnRunner"
                monitor-enter(r0)
                java.lang.String r1 = "LegacyVpnRunner"
                java.lang.String r2 = "Executing"
                android.util.Log.v(r1, r2)     // Catch:{ all -> 0x0088 }
                r1 = 50
                r3 = 0
                r9.bringup()     // Catch:{ InterruptedException -> 0x005f, all -> 0x003d }
                r9.waitForDaemonsToStop()     // Catch:{ InterruptedException -> 0x005f, all -> 0x003d }
                interrupted()     // Catch:{ InterruptedException -> 0x005f, all -> 0x003d }
                android.net.LocalSocket[] r4 = r9.mSockets     // Catch:{ all -> 0x0088 }
                int r5 = r4.length     // Catch:{ all -> 0x0088 }
                r6 = r3
            L_0x0021:
                if (r6 >= r5) goto L_0x002b
                r7 = r4[r6]     // Catch:{ all -> 0x0088 }
                libcore.io.IoUtils.closeQuietly(r7)     // Catch:{ all -> 0x0088 }
                int r6 = r6 + 1
                goto L_0x0021
            L_0x002b:
                java.lang.Thread.sleep(r1)     // Catch:{ InterruptedException -> 0x002f }
                goto L_0x0030
            L_0x002f:
                r1 = move-exception
            L_0x0030:
                java.lang.String[] r1 = r9.mDaemons     // Catch:{ all -> 0x0088 }
                int r2 = r1.length     // Catch:{ all -> 0x0088 }
            L_0x0033:
                if (r3 >= r2) goto L_0x0080
                r4 = r1[r3]     // Catch:{ all -> 0x0088 }
                android.os.SystemService.stop(r4)     // Catch:{ all -> 0x0088 }
                int r3 = r3 + 1
                goto L_0x0033
            L_0x003d:
                r4 = move-exception
                android.net.LocalSocket[] r5 = r9.mSockets     // Catch:{ all -> 0x0088 }
                int r6 = r5.length     // Catch:{ all -> 0x0088 }
                r7 = r3
            L_0x0042:
                if (r7 >= r6) goto L_0x004c
                r8 = r5[r7]     // Catch:{ all -> 0x0088 }
                libcore.io.IoUtils.closeQuietly(r8)     // Catch:{ all -> 0x0088 }
                int r7 = r7 + 1
                goto L_0x0042
            L_0x004c:
                java.lang.Thread.sleep(r1)     // Catch:{ InterruptedException -> 0x0050 }
                goto L_0x0051
            L_0x0050:
                r1 = move-exception
            L_0x0051:
                java.lang.String[] r1 = r9.mDaemons     // Catch:{ all -> 0x0088 }
                int r2 = r1.length     // Catch:{ all -> 0x0088 }
            L_0x0054:
                if (r3 >= r2) goto L_0x005e
                r5 = r1[r3]     // Catch:{ all -> 0x0088 }
                android.os.SystemService.stop(r5)     // Catch:{ all -> 0x0088 }
                int r3 = r3 + 1
                goto L_0x0054
            L_0x005e:
                throw r4     // Catch:{ all -> 0x0088 }
            L_0x005f:
                r4 = move-exception
                android.net.LocalSocket[] r4 = r9.mSockets     // Catch:{ all -> 0x0088 }
                int r5 = r4.length     // Catch:{ all -> 0x0088 }
                r6 = r3
            L_0x0064:
                if (r6 >= r5) goto L_0x006e
                r7 = r4[r6]     // Catch:{ all -> 0x0088 }
                libcore.io.IoUtils.closeQuietly(r7)     // Catch:{ all -> 0x0088 }
                int r6 = r6 + 1
                goto L_0x0064
            L_0x006e:
                java.lang.Thread.sleep(r1)     // Catch:{ InterruptedException -> 0x0072 }
                goto L_0x0073
            L_0x0072:
                r1 = move-exception
            L_0x0073:
                java.lang.String[] r1 = r9.mDaemons     // Catch:{ all -> 0x0088 }
                int r2 = r1.length     // Catch:{ all -> 0x0088 }
            L_0x0076:
                if (r3 >= r2) goto L_0x0080
                r4 = r1[r3]     // Catch:{ all -> 0x0088 }
                android.os.SystemService.stop(r4)     // Catch:{ all -> 0x0088 }
                int r3 = r3 + 1
                goto L_0x0076
            L_0x0080:
                com.android.server.connectivity.Vpn r1 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x0088 }
                r1.agentDisconnect()     // Catch:{ all -> 0x0088 }
                monitor-exit(r0)     // Catch:{ all -> 0x0088 }
                return
            L_0x0088:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0088 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.LegacyVpnRunner.run():void");
        }

        private void checkInterruptAndDelay(boolean sleepLonger) throws InterruptedException {
            if (SystemClock.elapsedRealtime() - this.mBringupStartTime <= 60000) {
                Thread.sleep(sleepLonger ? 200 : 1);
            } else {
                Vpn.this.updateState(NetworkInfo.DetailedState.FAILED, "checkpoint");
                throw new IllegalStateException("VPN bringup took too long");
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 16 */
        /* JADX WARNING: Can't wrap try/catch for region: R(6:54|55|56|49|48|50) */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void bringup() {
            /*
                r16 = this;
                r1 = r16
                r2 = 0
                long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x02e3 }
                r1.mBringupStartTime = r3     // Catch:{ Exception -> 0x02e3 }
                java.lang.String[] r0 = r1.mDaemons     // Catch:{ Exception -> 0x02e3 }
                int r3 = r0.length     // Catch:{ Exception -> 0x02e3 }
                r4 = 0
                r5 = r4
            L_0x000e:
                r6 = 1
                if (r5 >= r3) goto L_0x0020
                r7 = r0[r5]     // Catch:{ Exception -> 0x02e3 }
            L_0x0013:
                boolean r8 = android.os.SystemService.isStopped(r7)     // Catch:{ Exception -> 0x02e3 }
                if (r8 != 0) goto L_0x001d
                r1.checkInterruptAndDelay(r6)     // Catch:{ Exception -> 0x02e3 }
                goto L_0x0013
            L_0x001d:
                int r5 = r5 + 1
                goto L_0x000e
            L_0x0020:
                java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r3 = "/data/misc/vpn/state"
                r0.<init>(r3)     // Catch:{ Exception -> 0x02e3 }
                r3 = r0
                r3.delete()     // Catch:{ Exception -> 0x02e3 }
                boolean r0 = r3.exists()     // Catch:{ Exception -> 0x02e3 }
                if (r0 != 0) goto L_0x02db
                java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r5 = "/data/misc/vpn/abort"
                r0.<init>(r5)     // Catch:{ Exception -> 0x02e3 }
                r0.delete()     // Catch:{ Exception -> 0x02e3 }
                r2 = 1
                r0 = 0
                java.lang.String[][] r5 = r1.mArguments     // Catch:{ Exception -> 0x02e3 }
                int r7 = r5.length     // Catch:{ Exception -> 0x02e3 }
                r8 = r0
                r0 = r4
            L_0x0042:
                if (r0 >= r7) goto L_0x0052
                r9 = r5[r0]     // Catch:{ Exception -> 0x02e3 }
                if (r8 != 0) goto L_0x004d
                if (r9 == 0) goto L_0x004b
                goto L_0x004d
            L_0x004b:
                r10 = r4
                goto L_0x004e
            L_0x004d:
                r10 = r6
            L_0x004e:
                r8 = r10
                int r0 = r0 + 1
                goto L_0x0042
            L_0x0052:
                if (r8 != 0) goto L_0x005a
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                r0.agentDisconnect()     // Catch:{ Exception -> 0x02e3 }
                return
            L_0x005a:
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                android.net.NetworkInfo$DetailedState r5 = android.net.NetworkInfo.DetailedState.CONNECTING     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r7 = "execute"
                r0.updateState(r5, r7)     // Catch:{ Exception -> 0x02e3 }
                r0 = r4
                r5 = r0
            L_0x0065:
                java.lang.String[] r0 = r1.mDaemons     // Catch:{ Exception -> 0x02e3 }
                int r0 = r0.length     // Catch:{ Exception -> 0x02e3 }
                if (r5 >= r0) goto L_0x010d
                java.lang.String[][] r0 = r1.mArguments     // Catch:{ Exception -> 0x02e3 }
                r0 = r0[r5]     // Catch:{ Exception -> 0x02e3 }
                r9 = r0
                if (r9 != 0) goto L_0x0073
                goto L_0x00fa
            L_0x0073:
                java.lang.String[] r0 = r1.mDaemons     // Catch:{ Exception -> 0x02e3 }
                r0 = r0[r5]     // Catch:{ Exception -> 0x02e3 }
                r10 = r0
                android.os.SystemService.start(r10)     // Catch:{ Exception -> 0x02e3 }
            L_0x007b:
                boolean r0 = android.os.SystemService.isRunning(r10)     // Catch:{ Exception -> 0x02e3 }
                if (r0 != 0) goto L_0x0085
                r1.checkInterruptAndDelay(r6)     // Catch:{ Exception -> 0x02e3 }
                goto L_0x007b
            L_0x0085:
                android.net.LocalSocket[] r0 = r1.mSockets     // Catch:{ Exception -> 0x02e3 }
                android.net.LocalSocket r11 = new android.net.LocalSocket     // Catch:{ Exception -> 0x02e3 }
                r11.<init>()     // Catch:{ Exception -> 0x02e3 }
                r0[r5] = r11     // Catch:{ Exception -> 0x02e3 }
                android.net.LocalSocketAddress r0 = new android.net.LocalSocketAddress     // Catch:{ Exception -> 0x02e3 }
                android.net.LocalSocketAddress$Namespace r11 = android.net.LocalSocketAddress.Namespace.RESERVED     // Catch:{ Exception -> 0x02e3 }
                r0.<init>(r10, r11)     // Catch:{ Exception -> 0x02e3 }
                r11 = r0
            L_0x0096:
                android.net.LocalSocket[] r0 = r1.mSockets     // Catch:{ Exception -> 0x0106 }
                r0 = r0[r5]     // Catch:{ Exception -> 0x0106 }
                r0.connect(r11)     // Catch:{ Exception -> 0x0106 }
                android.net.LocalSocket[] r0 = r1.mSockets     // Catch:{ Exception -> 0x02e3 }
                r0 = r0[r5]     // Catch:{ Exception -> 0x02e3 }
                r12 = 500(0x1f4, float:7.0E-43)
                r0.setSoTimeout(r12)     // Catch:{ Exception -> 0x02e3 }
                android.net.LocalSocket[] r0 = r1.mSockets     // Catch:{ Exception -> 0x02e3 }
                r0 = r0[r5]     // Catch:{ Exception -> 0x02e3 }
                java.io.OutputStream r0 = r0.getOutputStream()     // Catch:{ Exception -> 0x02e3 }
                r12 = r0
                int r0 = r9.length     // Catch:{ Exception -> 0x02e3 }
                r13 = r4
            L_0x00b2:
                if (r13 >= r0) goto L_0x00de
                r14 = r9[r13]     // Catch:{ Exception -> 0x02e3 }
                java.nio.charset.Charset r15 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ Exception -> 0x02e3 }
                byte[] r15 = r14.getBytes(r15)     // Catch:{ Exception -> 0x02e3 }
                int r6 = r15.length     // Catch:{ Exception -> 0x02e3 }
                r7 = 65535(0xffff, float:9.1834E-41)
                if (r6 >= r7) goto L_0x00d6
                int r6 = r15.length     // Catch:{ Exception -> 0x02e3 }
                int r6 = r6 >> 8
                r12.write(r6)     // Catch:{ Exception -> 0x02e3 }
                int r6 = r15.length     // Catch:{ Exception -> 0x02e3 }
                r12.write(r6)     // Catch:{ Exception -> 0x02e3 }
                r12.write(r15)     // Catch:{ Exception -> 0x02e3 }
                r1.checkInterruptAndDelay(r4)     // Catch:{ Exception -> 0x02e3 }
                int r13 = r13 + 1
                r6 = 1
                goto L_0x00b2
            L_0x00d6:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r4 = "Argument is too large"
                r0.<init>(r4)     // Catch:{ Exception -> 0x02e3 }
                throw r0     // Catch:{ Exception -> 0x02e3 }
            L_0x00de:
                r0 = 255(0xff, float:3.57E-43)
                r12.write(r0)     // Catch:{ Exception -> 0x02e3 }
                r12.write(r0)     // Catch:{ Exception -> 0x02e3 }
                r12.flush()     // Catch:{ Exception -> 0x02e3 }
                android.net.LocalSocket[] r0 = r1.mSockets     // Catch:{ Exception -> 0x02e3 }
                r0 = r0[r5]     // Catch:{ Exception -> 0x02e3 }
                java.io.InputStream r0 = r0.getInputStream()     // Catch:{ Exception -> 0x02e3 }
                r6 = r0
            L_0x00f2:
                int r0 = r6.read()     // Catch:{ Exception -> 0x0100 }
                r7 = -1
                if (r0 != r7) goto L_0x00ff
            L_0x00fa:
                int r5 = r5 + 1
                r6 = 1
                goto L_0x0065
            L_0x00ff:
                goto L_0x0101
            L_0x0100:
                r0 = move-exception
            L_0x0101:
                r7 = 1
                r1.checkInterruptAndDelay(r7)     // Catch:{ Exception -> 0x02e3 }
                goto L_0x00f2
            L_0x0106:
                r0 = move-exception
                r6 = 1
                r1.checkInterruptAndDelay(r6)     // Catch:{ Exception -> 0x02e3 }
                r6 = 1
                goto L_0x0096
            L_0x010d:
                boolean r0 = r3.exists()     // Catch:{ Exception -> 0x02e3 }
                if (r0 != 0) goto L_0x0149
                r0 = r4
            L_0x0114:
                java.lang.String[] r5 = r1.mDaemons     // Catch:{ Exception -> 0x02e3 }
                int r5 = r5.length     // Catch:{ Exception -> 0x02e3 }
                if (r0 >= r5) goto L_0x0144
                java.lang.String[] r5 = r1.mDaemons     // Catch:{ Exception -> 0x02e3 }
                r5 = r5[r0]     // Catch:{ Exception -> 0x02e3 }
                java.lang.String[][] r6 = r1.mArguments     // Catch:{ Exception -> 0x02e3 }
                r6 = r6[r0]     // Catch:{ Exception -> 0x02e3 }
                if (r6 == 0) goto L_0x0141
                boolean r6 = android.os.SystemService.isRunning(r5)     // Catch:{ Exception -> 0x02e3 }
                if (r6 == 0) goto L_0x012a
                goto L_0x0141
            L_0x012a:
                java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch:{ Exception -> 0x02e3 }
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02e3 }
                r6.<init>()     // Catch:{ Exception -> 0x02e3 }
                r6.append(r5)     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r7 = " is dead"
                r6.append(r7)     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x02e3 }
                r4.<init>(r6)     // Catch:{ Exception -> 0x02e3 }
                throw r4     // Catch:{ Exception -> 0x02e3 }
            L_0x0141:
                int r0 = r0 + 1
                goto L_0x0114
            L_0x0144:
                r5 = 1
                r1.checkInterruptAndDelay(r5)     // Catch:{ Exception -> 0x02e3 }
                goto L_0x010d
            L_0x0149:
                r0 = 0
                java.lang.String r0 = android.os.FileUtils.readTextFile(r3, r4, r0)     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r5 = "\n"
                r6 = -1
                java.lang.String[] r0 = r0.split(r5, r6)     // Catch:{ Exception -> 0x02e3 }
                r5 = r0
                int r0 = r5.length     // Catch:{ Exception -> 0x02e3 }
                r6 = 7
                if (r0 != r6) goto L_0x02d3
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                r6 = r5[r4]     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r6 = r6.trim()     // Catch:{ Exception -> 0x02e3 }
                r0.interfaze = r6     // Catch:{ Exception -> 0x02e3 }
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                r6 = 1
                r6 = r5[r6]     // Catch:{ Exception -> 0x02e3 }
                r0.addLegacyAddresses(r6)     // Catch:{ Exception -> 0x02e3 }
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                java.util.List r0 = r0.routes     // Catch:{ Exception -> 0x02e3 }
                if (r0 == 0) goto L_0x0184
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                java.util.List r0 = r0.routes     // Catch:{ Exception -> 0x02e3 }
                boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x02e3 }
                if (r0 == 0) goto L_0x018e
            L_0x0184:
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                r6 = 2
                r6 = r5[r6]     // Catch:{ Exception -> 0x02e3 }
                r0.addLegacyRoutes(r6)     // Catch:{ Exception -> 0x02e3 }
            L_0x018e:
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                java.util.List r0 = r0.dnsServers     // Catch:{ Exception -> 0x02e3 }
                if (r0 == 0) goto L_0x01a2
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                java.util.List r0 = r0.dnsServers     // Catch:{ Exception -> 0x02e3 }
                int r0 = r0.size()     // Catch:{ Exception -> 0x02e3 }
                if (r0 != 0) goto L_0x01bf
            L_0x01a2:
                r0 = 3
                r0 = r5[r0]     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r0 = r0.trim()     // Catch:{ Exception -> 0x02e3 }
                boolean r6 = r0.isEmpty()     // Catch:{ Exception -> 0x02e3 }
                if (r6 != 0) goto L_0x01bf
                com.android.server.connectivity.Vpn r6 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r6 = r6.mConfig     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r7 = " "
                java.lang.String[] r7 = r0.split(r7)     // Catch:{ Exception -> 0x02e3 }
                java.util.List r7 = java.util.Arrays.asList(r7)     // Catch:{ Exception -> 0x02e3 }
                r6.dnsServers = r7     // Catch:{ Exception -> 0x02e3 }
            L_0x01bf:
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                java.util.List r0 = r0.searchDomains     // Catch:{ Exception -> 0x02e3 }
                if (r0 == 0) goto L_0x01d3
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ Exception -> 0x02e3 }
                java.util.List r0 = r0.searchDomains     // Catch:{ Exception -> 0x02e3 }
                int r0 = r0.size()     // Catch:{ Exception -> 0x02e3 }
                if (r0 != 0) goto L_0x01f0
            L_0x01d3:
                r0 = 4
                r0 = r5[r0]     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r0 = r0.trim()     // Catch:{ Exception -> 0x02e3 }
                boolean r6 = r0.isEmpty()     // Catch:{ Exception -> 0x02e3 }
                if (r6 != 0) goto L_0x01f0
                com.android.server.connectivity.Vpn r6 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                com.android.internal.net.VpnConfig r6 = r6.mConfig     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r7 = " "
                java.lang.String[] r7 = r0.split(r7)     // Catch:{ Exception -> 0x02e3 }
                java.util.List r7 = java.util.Arrays.asList(r7)     // Catch:{ Exception -> 0x02e3 }
                r6.searchDomains = r7     // Catch:{ Exception -> 0x02e3 }
            L_0x01f0:
                r0 = 5
                r6 = r5[r0]     // Catch:{ Exception -> 0x02e3 }
                boolean r6 = r6.isEmpty()     // Catch:{ Exception -> 0x02e3 }
                if (r6 == 0) goto L_0x01fe
                com.android.internal.net.VpnProfile r0 = r1.mProfile     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r0 = r0.server     // Catch:{ Exception -> 0x02e3 }
                goto L_0x0200
            L_0x01fe:
                r0 = r5[r0]     // Catch:{ Exception -> 0x02e3 }
            L_0x0200:
                r6 = r0
                boolean r0 = r6.isEmpty()     // Catch:{ Exception -> 0x02e3 }
                if (r0 != 0) goto L_0x0277
                java.net.InetAddress r0 = java.net.InetAddress.parseNumericAddress(r6)     // Catch:{ IllegalArgumentException -> 0x0258 }
                boolean r7 = r0 instanceof java.net.Inet4Address     // Catch:{ IllegalArgumentException -> 0x0258 }
                r9 = 9
                if (r7 == 0) goto L_0x0227
                com.android.server.connectivity.Vpn r7 = com.android.server.connectivity.Vpn.this     // Catch:{ IllegalArgumentException -> 0x0258 }
                com.android.internal.net.VpnConfig r7 = r7.mConfig     // Catch:{ IllegalArgumentException -> 0x0258 }
                java.util.List r7 = r7.routes     // Catch:{ IllegalArgumentException -> 0x0258 }
                android.net.RouteInfo r10 = new android.net.RouteInfo     // Catch:{ IllegalArgumentException -> 0x0258 }
                android.net.IpPrefix r11 = new android.net.IpPrefix     // Catch:{ IllegalArgumentException -> 0x0258 }
                r12 = 32
                r11.<init>(r0, r12)     // Catch:{ IllegalArgumentException -> 0x0258 }
                r10.<init>(r11, r9)     // Catch:{ IllegalArgumentException -> 0x0258 }
                r7.add(r10)     // Catch:{ IllegalArgumentException -> 0x0258 }
                goto L_0x0257
            L_0x0227:
                boolean r7 = r0 instanceof java.net.Inet6Address     // Catch:{ IllegalArgumentException -> 0x0258 }
                if (r7 == 0) goto L_0x0241
                com.android.server.connectivity.Vpn r7 = com.android.server.connectivity.Vpn.this     // Catch:{ IllegalArgumentException -> 0x0258 }
                com.android.internal.net.VpnConfig r7 = r7.mConfig     // Catch:{ IllegalArgumentException -> 0x0258 }
                java.util.List r7 = r7.routes     // Catch:{ IllegalArgumentException -> 0x0258 }
                android.net.RouteInfo r10 = new android.net.RouteInfo     // Catch:{ IllegalArgumentException -> 0x0258 }
                android.net.IpPrefix r11 = new android.net.IpPrefix     // Catch:{ IllegalArgumentException -> 0x0258 }
                r12 = 128(0x80, float:1.794E-43)
                r11.<init>(r0, r12)     // Catch:{ IllegalArgumentException -> 0x0258 }
                r10.<init>(r11, r9)     // Catch:{ IllegalArgumentException -> 0x0258 }
                r7.add(r10)     // Catch:{ IllegalArgumentException -> 0x0258 }
                goto L_0x0257
            L_0x0241:
                java.lang.String r7 = "LegacyVpnRunner"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0258 }
                r9.<init>()     // Catch:{ IllegalArgumentException -> 0x0258 }
                java.lang.String r10 = "Unknown IP address family for VPN endpoint: "
                r9.append(r10)     // Catch:{ IllegalArgumentException -> 0x0258 }
                r9.append(r6)     // Catch:{ IllegalArgumentException -> 0x0258 }
                java.lang.String r9 = r9.toString()     // Catch:{ IllegalArgumentException -> 0x0258 }
                android.util.Log.e(r7, r9)     // Catch:{ IllegalArgumentException -> 0x0258 }
            L_0x0257:
                goto L_0x0277
            L_0x0258:
                r0 = move-exception
                java.lang.String r7 = "LegacyVpnRunner"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02e3 }
                r9.<init>()     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r10 = "Exception constructing throw route to "
                r9.append(r10)     // Catch:{ Exception -> 0x02e3 }
                r9.append(r6)     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r10 = ": "
                r9.append(r10)     // Catch:{ Exception -> 0x02e3 }
                r9.append(r0)     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x02e3 }
                android.util.Log.e(r7, r9)     // Catch:{ Exception -> 0x02e3 }
            L_0x0277:
                com.android.server.connectivity.Vpn r7 = com.android.server.connectivity.Vpn.this     // Catch:{ Exception -> 0x02e3 }
                monitor-enter(r7)     // Catch:{ Exception -> 0x02e3 }
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x02d0 }
                com.android.internal.net.VpnConfig r0 = r0.mConfig     // Catch:{ all -> 0x02d0 }
                long r9 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x02d0 }
                r0.startTime = r9     // Catch:{ all -> 0x02d0 }
                r1.checkInterruptAndDelay(r4)     // Catch:{ all -> 0x02d0 }
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x02d0 }
                com.android.server.connectivity.Vpn r4 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x02d0 }
                com.android.internal.net.VpnConfig r4 = r4.mConfig     // Catch:{ all -> 0x02d0 }
                java.lang.String r4 = r4.interfaze     // Catch:{ all -> 0x02d0 }
                int r0 = r0.jniCheck(r4)     // Catch:{ all -> 0x02d0 }
                if (r0 == 0) goto L_0x02b3
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x02d0 }
                com.android.server.connectivity.Vpn r4 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x02d0 }
                com.android.internal.net.VpnConfig r4 = r4.mConfig     // Catch:{ all -> 0x02d0 }
                java.lang.String r4 = r4.interfaze     // Catch:{ all -> 0x02d0 }
                java.lang.String unused = r0.mInterface = r4     // Catch:{ all -> 0x02d0 }
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x02d0 }
                r0.prepareStatusIntent()     // Catch:{ all -> 0x02d0 }
                com.android.server.connectivity.Vpn r0 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x02d0 }
                r0.agentConnect()     // Catch:{ all -> 0x02d0 }
                java.lang.String r0 = "LegacyVpnRunner"
                java.lang.String r4 = "Connected!"
                android.util.Log.i(r0, r4)     // Catch:{ all -> 0x02d0 }
                monitor-exit(r7)     // Catch:{ all -> 0x02d0 }
                goto L_0x02f9
            L_0x02b3:
                java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x02d0 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x02d0 }
                r4.<init>()     // Catch:{ all -> 0x02d0 }
                com.android.server.connectivity.Vpn r9 = com.android.server.connectivity.Vpn.this     // Catch:{ all -> 0x02d0 }
                com.android.internal.net.VpnConfig r9 = r9.mConfig     // Catch:{ all -> 0x02d0 }
                java.lang.String r9 = r9.interfaze     // Catch:{ all -> 0x02d0 }
                r4.append(r9)     // Catch:{ all -> 0x02d0 }
                java.lang.String r9 = " is gone"
                r4.append(r9)     // Catch:{ all -> 0x02d0 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x02d0 }
                r0.<init>(r4)     // Catch:{ all -> 0x02d0 }
                throw r0     // Catch:{ all -> 0x02d0 }
            L_0x02d0:
                r0 = move-exception
                monitor-exit(r7)     // Catch:{ all -> 0x02d0 }
                throw r0     // Catch:{ Exception -> 0x02e3 }
            L_0x02d3:
                java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r4 = "Cannot parse the state"
                r0.<init>(r4)     // Catch:{ Exception -> 0x02e3 }
                throw r0     // Catch:{ Exception -> 0x02e3 }
            L_0x02db:
                java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ Exception -> 0x02e3 }
                java.lang.String r4 = "Cannot delete the state"
                r0.<init>(r4)     // Catch:{ Exception -> 0x02e3 }
                throw r0     // Catch:{ Exception -> 0x02e3 }
            L_0x02e3:
                r0 = move-exception
                java.lang.String r3 = "LegacyVpnRunner"
                java.lang.String r4 = "Aborting"
                android.util.Log.i(r3, r4, r0)
                com.android.server.connectivity.Vpn r3 = com.android.server.connectivity.Vpn.this
                android.net.NetworkInfo$DetailedState r4 = android.net.NetworkInfo.DetailedState.FAILED
                java.lang.String r5 = r0.getMessage()
                r3.updateState(r4, r5)
                r16.exit()
            L_0x02f9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.LegacyVpnRunner.bringup():void");
        }

        private void waitForDaemonsToStop() throws InterruptedException {
            if (!Vpn.this.mNetworkInfo.isConnected()) {
                return;
            }
            while (true) {
                Thread.sleep(2000);
                int i = 0;
                while (true) {
                    String[] strArr = this.mDaemons;
                    if (i < strArr.length) {
                        if (this.mArguments[i] == null || !SystemService.isStopped(strArr[i])) {
                            i++;
                        } else {
                            return;
                        }
                    }
                }
            }
        }
    }
}
