package com.android.server;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.INetd;
import android.net.INetdUnsolicitedEventListener;
import android.net.INetworkManagementEventObserver;
import android.net.ITetheringStatsProvider;
import android.net.InetAddresses;
import android.net.InterfaceConfiguration;
import android.net.InterfaceConfigurationParcel;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.Network;
import android.net.NetworkStats;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.TetherStatsParcel;
import android.net.UidRange;
import android.net.UidRangeParcel;
import android.net.util.NetdService;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.INetworkActivityListener;
import android.os.INetworkManagementService;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.StatsLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.HexDump;
import com.android.internal.util.Preconditions;
import com.android.server.NetworkManagementService;
import com.android.server.net.MiuiNetworkManager;
import com.android.server.net.NetworkStatsFactory;
import com.google.android.collect.Maps;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import libcore.util.EmptyArray;

public class NetworkManagementService extends INetworkManagementService.Stub {
    static final int DAEMON_MSG_MOBILE_CONN_REAL_TIME_INFO = 1;
    private static final boolean DBG = Log.isLoggable(TAG, 3);
    public static final String LIMIT_GLOBAL_ALERT = "globalAlert";
    private static final int MAX_UID_RANGES_PER_COMMAND = 10;
    static final boolean MODIFY_OPERATION_ADD = true;
    static final boolean MODIFY_OPERATION_REMOVE = false;
    private static final String TAG = "NetworkManagement";
    @GuardedBy({"mQuotaLock"})
    private HashMap<String, Long> mActiveAlerts;
    private HashMap<String, IdleTimerParams> mActiveIdleTimers;
    @GuardedBy({"mQuotaLock"})
    private HashMap<String, Long> mActiveQuotas;
    private IBatteryStats mBatteryStats;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final Handler mDaemonHandler;
    /* access modifiers changed from: private */
    @GuardedBy({"mQuotaLock"})
    public volatile boolean mDataSaverMode;
    @GuardedBy({"mRulesLock"})
    final SparseBooleanArray mFirewallChainStates;
    private volatile boolean mFirewallEnabled;
    private final Object mIdleTimerLock;
    private int mLastPowerStateFromRadio;
    private int mLastPowerStateFromWifi;
    private MiuiNetworkManagementService mMiNMS;
    private boolean mMobileActivityFromRadio;
    /* access modifiers changed from: private */
    public INetd mNetdService;
    private final NetdUnsolicitedEventListener mNetdUnsolicitedEventListener;
    private boolean mNetworkActive;
    private final RemoteCallbackList<INetworkActivityListener> mNetworkActivityListeners;
    private final RemoteCallbackList<INetworkManagementEventObserver> mObservers;
    private final Object mQuotaLock;
    /* access modifiers changed from: private */
    public final Object mRulesLock;
    private final SystemServices mServices;
    private final NetworkStatsFactory mStatsFactory;
    private volatile boolean mStrictEnabled;
    @GuardedBy({"mTetheringStatsProviders"})
    private final HashMap<ITetheringStatsProvider, String> mTetheringStatsProviders;
    /* access modifiers changed from: private */
    @GuardedBy({"mRulesLock"})
    public SparseBooleanArray mUidAllowOnMetered;
    @GuardedBy({"mQuotaLock"})
    private SparseIntArray mUidCleartextPolicy;
    @GuardedBy({"mRulesLock"})
    private SparseIntArray mUidFirewallDozableRules;
    @GuardedBy({"mRulesLock"})
    private SparseIntArray mUidFirewallPowerSaveRules;
    @GuardedBy({"mRulesLock"})
    private SparseIntArray mUidFirewallRules;
    @GuardedBy({"mRulesLock"})
    private SparseIntArray mUidFirewallStandbyRules;
    /* access modifiers changed from: private */
    @GuardedBy({"mRulesLock"})
    public SparseBooleanArray mUidRejectOnMetered;

    @FunctionalInterface
    private interface NetworkManagementEventCallback {
        void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) throws RemoteException;
    }

    static class SystemServices {
        SystemServices() {
        }

        public IBinder getService(String name) {
            return ServiceManager.getService(name);
        }

        public void registerLocalService(NetworkManagementInternal nmi) {
            LocalServices.addService(NetworkManagementInternal.class, nmi);
        }

        public INetd getNetd() {
            return NetdService.get();
        }
    }

    private static class IdleTimerParams {
        public int networkCount = 1;
        public final int timeout;
        public final int type;

        IdleTimerParams(int timeout2, int type2) {
            this.timeout = timeout2;
            this.type = type2;
        }
    }

    private NetworkManagementService(Context context, SystemServices services2) {
        this.mObservers = new RemoteCallbackList<>();
        this.mStatsFactory = new NetworkStatsFactory();
        this.mTetheringStatsProviders = Maps.newHashMap();
        this.mQuotaLock = new Object();
        this.mRulesLock = new Object();
        this.mActiveQuotas = Maps.newHashMap();
        this.mActiveAlerts = Maps.newHashMap();
        this.mUidRejectOnMetered = new SparseBooleanArray();
        this.mUidAllowOnMetered = new SparseBooleanArray();
        this.mUidCleartextPolicy = new SparseIntArray();
        this.mUidFirewallRules = new SparseIntArray();
        this.mUidFirewallStandbyRules = new SparseIntArray();
        this.mUidFirewallDozableRules = new SparseIntArray();
        this.mUidFirewallPowerSaveRules = new SparseIntArray();
        this.mFirewallChainStates = new SparseBooleanArray();
        this.mIdleTimerLock = new Object();
        this.mActiveIdleTimers = Maps.newHashMap();
        this.mMobileActivityFromRadio = false;
        this.mLastPowerStateFromRadio = 1;
        this.mLastPowerStateFromWifi = 1;
        this.mNetworkActivityListeners = new RemoteCallbackList<>();
        this.mContext = context;
        this.mServices = services2;
        this.mDaemonHandler = new Handler(FgThread.get().getLooper());
        this.mNetdUnsolicitedEventListener = new NetdUnsolicitedEventListener();
        this.mServices.registerLocalService(new LocalService());
        synchronized (this.mTetheringStatsProviders) {
            this.mTetheringStatsProviders.put(new NetdTetheringStatsProvider(), "netd");
        }
        this.mMiNMS = MiuiNetworkManagementService.Init(context);
    }

    @VisibleForTesting
    NetworkManagementService() {
        this.mObservers = new RemoteCallbackList<>();
        this.mStatsFactory = new NetworkStatsFactory();
        this.mTetheringStatsProviders = Maps.newHashMap();
        this.mQuotaLock = new Object();
        this.mRulesLock = new Object();
        this.mActiveQuotas = Maps.newHashMap();
        this.mActiveAlerts = Maps.newHashMap();
        this.mUidRejectOnMetered = new SparseBooleanArray();
        this.mUidAllowOnMetered = new SparseBooleanArray();
        this.mUidCleartextPolicy = new SparseIntArray();
        this.mUidFirewallRules = new SparseIntArray();
        this.mUidFirewallStandbyRules = new SparseIntArray();
        this.mUidFirewallDozableRules = new SparseIntArray();
        this.mUidFirewallPowerSaveRules = new SparseIntArray();
        this.mFirewallChainStates = new SparseBooleanArray();
        this.mIdleTimerLock = new Object();
        this.mActiveIdleTimers = Maps.newHashMap();
        this.mMobileActivityFromRadio = false;
        this.mLastPowerStateFromRadio = 1;
        this.mLastPowerStateFromWifi = 1;
        this.mNetworkActivityListeners = new RemoteCallbackList<>();
        this.mContext = null;
        this.mDaemonHandler = null;
        this.mServices = null;
        this.mNetdUnsolicitedEventListener = null;
    }

    static NetworkManagementService create(Context context, SystemServices services2) throws InterruptedException {
        NetworkManagementService service = new NetworkManagementService(context, services2);
        if (DBG) {
            Slog.d(TAG, "Creating NetworkManagementService");
        }
        if (DBG) {
            Slog.d(TAG, "Connecting native netd service");
        }
        service.connectNativeNetdService();
        if (DBG) {
            Slog.d(TAG, "Connected");
        }
        return service;
    }

    public static NetworkManagementService create(Context context) throws InterruptedException {
        return create(context, new SystemServices());
    }

    public void systemReady() {
        if (DBG) {
            long start = System.currentTimeMillis();
            prepareNativeDaemon();
            Slog.d(TAG, "Prepared in " + (System.currentTimeMillis() - start) + "ms");
            return;
        }
        prepareNativeDaemon();
    }

    private IBatteryStats getBatteryStats() {
        synchronized (this) {
            if (this.mBatteryStats != null) {
                IBatteryStats iBatteryStats = this.mBatteryStats;
                return iBatteryStats;
            }
            this.mBatteryStats = IBatteryStats.Stub.asInterface(this.mServices.getService("batterystats"));
            IBatteryStats iBatteryStats2 = this.mBatteryStats;
            return iBatteryStats2;
        }
    }

    public void registerObserver(INetworkManagementEventObserver observer) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        this.mObservers.register(observer);
    }

    public void unregisterObserver(INetworkManagementEventObserver observer) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        this.mObservers.unregister(observer);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0018, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r3.mObservers.finishBroadcast();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        throw r1;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0018 A[ExcHandler: all (r1v4 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:5:0x000a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void invokeForAllObservers(com.android.server.NetworkManagementService.NetworkManagementEventCallback r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            android.os.RemoteCallbackList<android.net.INetworkManagementEventObserver> r0 = r3.mObservers     // Catch:{ all -> 0x002b }
            int r0 = r0.beginBroadcast()     // Catch:{ all -> 0x002b }
            r1 = 0
        L_0x0008:
            if (r1 >= r0) goto L_0x0023
            android.os.RemoteCallbackList<android.net.INetworkManagementEventObserver> r2 = r3.mObservers     // Catch:{ RemoteException | RuntimeException -> 0x001f, all -> 0x0018 }
            android.os.IInterface r2 = r2.getBroadcastItem(r1)     // Catch:{ RemoteException | RuntimeException -> 0x0016, all -> 0x0018 }
            android.net.INetworkManagementEventObserver r2 = (android.net.INetworkManagementEventObserver) r2     // Catch:{ RemoteException | RuntimeException -> 0x0016, all -> 0x0018 }
            r4.sendCallback(r2)     // Catch:{ RemoteException | RuntimeException -> 0x0016, all -> 0x0018 }
            goto L_0x0020
        L_0x0016:
            r2 = move-exception
            goto L_0x0020
        L_0x0018:
            r1 = move-exception
            android.os.RemoteCallbackList<android.net.INetworkManagementEventObserver> r2 = r3.mObservers     // Catch:{ all -> 0x002b }
            r2.finishBroadcast()     // Catch:{ all -> 0x002b }
            throw r1     // Catch:{ all -> 0x002b }
        L_0x001f:
            r2 = move-exception
        L_0x0020:
            int r1 = r1 + 1
            goto L_0x0008
        L_0x0023:
            android.os.RemoteCallbackList<android.net.INetworkManagementEventObserver> r1 = r3.mObservers     // Catch:{ all -> 0x002b }
            r1.finishBroadcast()     // Catch:{ all -> 0x002b }
            monitor-exit(r3)
            return
        L_0x002b:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetworkManagementService.invokeForAllObservers(com.android.server.NetworkManagementService$NetworkManagementEventCallback):void");
    }

    /* access modifiers changed from: private */
    public void notifyInterfaceStatusChanged(String iface, boolean up) {
        invokeForAllObservers(new NetworkManagementEventCallback(iface, up) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ boolean f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceStatusChanged(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyInterfaceLinkStateChanged(String iface, boolean up) {
        invokeForAllObservers(new NetworkManagementEventCallback(iface, up) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ boolean f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceLinkStateChanged(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyInterfaceAdded(String iface) {
        invokeForAllObservers(new NetworkManagementEventCallback(iface) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceAdded(this.f$0);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyInterfaceRemoved(String iface) {
        this.mActiveAlerts.remove(iface);
        this.mActiveQuotas.remove(iface);
        invokeForAllObservers(new NetworkManagementEventCallback(iface) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceRemoved(this.f$0);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyLimitReached(String limitName, String iface) {
        invokeForAllObservers(new NetworkManagementEventCallback(limitName, iface) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ String f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.limitReached(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyInterfaceClassActivity(int type, boolean isActive, long tsNanos, int uid, boolean fromRadio) {
        int powerState;
        this.mMiNMS.miuiNotifyInterfaceClassActivity(type, isActive, tsNanos, uid, fromRadio);
        boolean isMobile = ConnectivityManager.isNetworkTypeMobile(type);
        if (isActive) {
            powerState = 3;
        } else {
            powerState = 1;
        }
        if (isMobile) {
            if (fromRadio) {
                this.mMobileActivityFromRadio = true;
            } else if (this.mMobileActivityFromRadio) {
                powerState = this.mLastPowerStateFromRadio;
            }
            if (this.mLastPowerStateFromRadio != powerState) {
                this.mLastPowerStateFromRadio = powerState;
                try {
                    getBatteryStats().noteMobileRadioPowerState(powerState, tsNanos, uid);
                } catch (RemoteException e) {
                }
                StatsLog.write_non_chained(12, uid, (String) null, powerState);
            }
        }
        if (ConnectivityManager.isNetworkTypeWifi(type) && this.mLastPowerStateFromWifi != powerState) {
            this.mLastPowerStateFromWifi = powerState;
            try {
                getBatteryStats().noteWifiRadioPowerState(powerState, tsNanos, uid);
            } catch (RemoteException e2) {
            }
            StatsLog.write_non_chained(13, uid, (String) null, powerState);
        }
        if (!isMobile || fromRadio || !this.mMobileActivityFromRadio) {
            invokeForAllObservers(new NetworkManagementEventCallback(type, isActive, tsNanos) {
                private final /* synthetic */ int f$0;
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ long f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                    iNetworkManagementEventObserver.interfaceClassDataActivityChanged(Integer.toString(this.f$0), this.f$1, this.f$2);
                }
            });
        }
        boolean report = false;
        synchronized (this.mIdleTimerLock) {
            if (this.mActiveIdleTimers.isEmpty()) {
                isActive = true;
            }
            if (this.mNetworkActive != isActive) {
                this.mNetworkActive = isActive;
                report = isActive;
            }
        }
        if (report) {
            reportNetworkActive();
        }
    }

    public void registerTetheringStatsProvider(ITetheringStatsProvider provider, String name) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_STACK", TAG);
        Preconditions.checkNotNull(provider);
        synchronized (this.mTetheringStatsProviders) {
            this.mTetheringStatsProviders.put(provider, name);
        }
    }

    public void unregisterTetheringStatsProvider(ITetheringStatsProvider provider) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_STACK", TAG);
        synchronized (this.mTetheringStatsProviders) {
            this.mTetheringStatsProviders.remove(provider);
        }
    }

    public void tetherLimitReached(ITetheringStatsProvider provider) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_STACK", TAG);
        synchronized (this.mTetheringStatsProviders) {
            if (this.mTetheringStatsProviders.containsKey(provider)) {
                this.mDaemonHandler.post(new Runnable() {
                    public final void run() {
                        NetworkManagementService.this.lambda$tetherLimitReached$6$NetworkManagementService();
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$tetherLimitReached$6$NetworkManagementService() {
        notifyLimitReached(LIMIT_GLOBAL_ALERT, (String) null);
    }

    private void syncFirewallChainLocked(int chain, String name) {
        SparseIntArray rules;
        synchronized (this.mRulesLock) {
            SparseIntArray uidFirewallRules = getUidFirewallRulesLR(chain);
            rules = uidFirewallRules.clone();
            uidFirewallRules.clear();
        }
        if (rules.size() > 0) {
            if (DBG) {
                Slog.d(TAG, "Pushing " + rules.size() + " active firewall " + name + "UID rules");
            }
            for (int i = 0; i < rules.size(); i++) {
                setFirewallUidRuleLocked(chain, rules.keyAt(i), rules.valueAt(i));
            }
        }
    }

    private void connectNativeNetdService() {
        this.mNetdService = this.mServices.getNetd();
        try {
            this.mNetdService.registerUnsolicitedEventListener(this.mNetdUnsolicitedEventListener);
            if (DBG) {
                Slog.d(TAG, "Register unsolicited event listener");
            }
        } catch (RemoteException | ServiceSpecificException e) {
            Slog.e(TAG, "Failed to set Netd unsolicited event listener " + e);
        }
        try {
            this.mMiNMS.setOemNetd(this.mNetdService.getOemNetd());
        } catch (RemoteException e2) {
            Slog.e(TAG, "### setOemNetd failed ###");
            e2.printStackTrace();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    private void prepareNativeDaemon() {
        synchronized (this.mQuotaLock) {
            SystemProperties.set("net.qtaguid_enabled", SplitScreenReporter.ACTION_ENTER_SPLIT);
            this.mStrictEnabled = true;
            setDataSaverModeEnabled(this.mDataSaverMode);
            int size = this.mActiveQuotas.size();
            if (size > 0) {
                if (DBG) {
                    Slog.d(TAG, "Pushing " + size + " active quota rules");
                }
                HashMap<String, Long> activeQuotas = this.mActiveQuotas;
                this.mActiveQuotas = Maps.newHashMap();
                for (Map.Entry<String, Long> entry : activeQuotas.entrySet()) {
                    setInterfaceQuota(entry.getKey(), entry.getValue().longValue());
                }
            }
            int size2 = this.mActiveAlerts.size();
            if (size2 > 0) {
                if (DBG) {
                    Slog.d(TAG, "Pushing " + size2 + " active alert rules");
                }
                HashMap<String, Long> activeAlerts = this.mActiveAlerts;
                this.mActiveAlerts = Maps.newHashMap();
                for (Map.Entry<String, Long> entry2 : activeAlerts.entrySet()) {
                    setInterfaceAlert(entry2.getKey(), entry2.getValue().longValue());
                }
            }
            SparseBooleanArray uidRejectOnQuota = null;
            SparseBooleanArray uidAcceptOnQuota = null;
            synchronized (this.mRulesLock) {
                int size3 = this.mUidRejectOnMetered.size();
                if (size3 > 0) {
                    if (DBG) {
                        Slog.d(TAG, "Pushing " + size3 + " UIDs to metered blacklist rules");
                    }
                    uidRejectOnQuota = this.mUidRejectOnMetered;
                    this.mUidRejectOnMetered = new SparseBooleanArray();
                }
                int size4 = this.mUidAllowOnMetered.size();
                if (size4 > 0) {
                    if (DBG) {
                        Slog.d(TAG, "Pushing " + size4 + " UIDs to metered whitelist rules");
                    }
                    uidAcceptOnQuota = this.mUidAllowOnMetered;
                    this.mUidAllowOnMetered = new SparseBooleanArray();
                }
            }
            if (uidRejectOnQuota != null) {
                for (int i = 0; i < uidRejectOnQuota.size(); i++) {
                    setUidMeteredNetworkBlacklist(uidRejectOnQuota.keyAt(i), uidRejectOnQuota.valueAt(i));
                }
            }
            if (uidAcceptOnQuota != null) {
                for (int i2 = 0; i2 < uidAcceptOnQuota.size(); i2++) {
                    setUidMeteredNetworkWhitelist(uidAcceptOnQuota.keyAt(i2), uidAcceptOnQuota.valueAt(i2));
                }
            }
            int size5 = this.mUidCleartextPolicy.size();
            if (size5 > 0) {
                if (DBG) {
                    Slog.d(TAG, "Pushing " + size5 + " active UID cleartext policies");
                }
                SparseIntArray local = this.mUidCleartextPolicy;
                this.mUidCleartextPolicy = new SparseIntArray();
                for (int i3 = 0; i3 < local.size(); i3++) {
                    setUidCleartextNetworkPolicy(local.keyAt(i3), local.valueAt(i3));
                }
            }
            setFirewallEnabled(this.mFirewallEnabled);
            syncFirewallChainLocked(0, "");
            syncFirewallChainLocked(2, "standby ");
            syncFirewallChainLocked(1, "dozable ");
            syncFirewallChainLocked(3, "powersave ");
            for (int chain : new int[]{2, 1, 3}) {
                if (getFirewallChainState(chain)) {
                    setFirewallChainEnabled(chain, true);
                }
            }
        }
        try {
            getBatteryStats().noteNetworkStatsEnabled();
        } catch (RemoteException e) {
        }
    }

    private void notifyInterfaceConfigurationLost() {
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).interfaceConfigurationLost();
            } catch (RemoteException | RuntimeException e) {
            } catch (Throwable th) {
                this.mObservers.finishBroadcast();
                throw th;
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* access modifiers changed from: private */
    public void notifyAddressUpdated(String iface, LinkAddress address) {
        invokeForAllObservers(new NetworkManagementEventCallback(iface, address) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ LinkAddress f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.addressUpdated(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyAddressRemoved(String iface, LinkAddress address) {
        invokeForAllObservers(new NetworkManagementEventCallback(iface, address) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ LinkAddress f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.addressRemoved(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyInterfaceDnsServerInfo(String iface, long lifetime, String[] addresses) {
        invokeForAllObservers(new NetworkManagementEventCallback(iface, lifetime, addresses) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ long f$1;
            private final /* synthetic */ String[] f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceDnsServerInfo(this.f$0, this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyRouteChange(boolean updated, RouteInfo route) {
        if (updated) {
            invokeForAllObservers(new NetworkManagementEventCallback(route) {
                private final /* synthetic */ RouteInfo f$0;

                {
                    this.f$0 = r1;
                }

                public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                    iNetworkManagementEventObserver.routeUpdated(this.f$0);
                }
            });
        } else {
            invokeForAllObservers(new NetworkManagementEventCallback(route) {
                private final /* synthetic */ RouteInfo f$0;

                {
                    this.f$0 = r1;
                }

                public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                    iNetworkManagementEventObserver.routeRemoved(this.f$0);
                }
            });
        }
    }

    private class NetdUnsolicitedEventListener extends INetdUnsolicitedEventListener.Stub {
        private NetdUnsolicitedEventListener() {
        }

        public void onInterfaceClassActivityChanged(boolean isActive, int label, long timestamp, int uid) throws RemoteException {
            long timestampNanos;
            if (timestamp <= 0) {
                timestampNanos = SystemClock.elapsedRealtimeNanos();
            } else {
                timestampNanos = timestamp;
            }
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(label, isActive, timestampNanos, uid) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceClassActivityChanged$0$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$onInterfaceClassActivityChanged$0$NetworkManagementService$NetdUnsolicitedEventListener(int label, boolean isActive, long timestampNanos, int uid) {
            NetworkManagementService.this.notifyInterfaceClassActivity(label, isActive, timestampNanos, uid, false);
        }

        public /* synthetic */ void lambda$onQuotaLimitReached$1$NetworkManagementService$NetdUnsolicitedEventListener(String alertName, String ifName) {
            NetworkManagementService.this.notifyLimitReached(alertName, ifName);
        }

        public void onQuotaLimitReached(String alertName, String ifName) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(alertName, ifName) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onQuotaLimitReached$1$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$onInterfaceDnsServerInfo$2$NetworkManagementService$NetdUnsolicitedEventListener(String ifName, long lifetime, String[] servers) {
            NetworkManagementService.this.notifyInterfaceDnsServerInfo(ifName, lifetime, servers);
        }

        public void onInterfaceDnsServerInfo(String ifName, long lifetime, String[] servers) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(ifName, lifetime, servers) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ String[] f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceDnsServerInfo$2$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public void onInterfaceAddressUpdated(String addr, String ifName, int flags, int scope) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(ifName, new LinkAddress(addr, flags, scope)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ LinkAddress f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceAddressUpdated$3$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$onInterfaceAddressUpdated$3$NetworkManagementService$NetdUnsolicitedEventListener(String ifName, LinkAddress address) {
            NetworkManagementService.this.notifyAddressUpdated(ifName, address);
        }

        public void onInterfaceAddressRemoved(String addr, String ifName, int flags, int scope) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(ifName, new LinkAddress(addr, flags, scope)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ LinkAddress f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceAddressRemoved$4$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$onInterfaceAddressRemoved$4$NetworkManagementService$NetdUnsolicitedEventListener(String ifName, LinkAddress address) {
            NetworkManagementService.this.notifyAddressRemoved(ifName, address);
        }

        public /* synthetic */ void lambda$onInterfaceAdded$5$NetworkManagementService$NetdUnsolicitedEventListener(String ifName) {
            NetworkManagementService.this.notifyInterfaceAdded(ifName);
        }

        public void onInterfaceAdded(String ifName) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(ifName) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceAdded$5$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onInterfaceRemoved$6$NetworkManagementService$NetdUnsolicitedEventListener(String ifName) {
            NetworkManagementService.this.notifyInterfaceRemoved(ifName);
        }

        public void onInterfaceRemoved(String ifName) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(ifName) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceRemoved$6$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onInterfaceChanged$7$NetworkManagementService$NetdUnsolicitedEventListener(String ifName, boolean up) {
            NetworkManagementService.this.notifyInterfaceStatusChanged(ifName, up);
        }

        public void onInterfaceChanged(String ifName, boolean up) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(ifName, up) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceChanged$7$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$onInterfaceLinkStateChanged$8$NetworkManagementService$NetdUnsolicitedEventListener(String ifName, boolean up) {
            NetworkManagementService.this.notifyInterfaceLinkStateChanged(ifName, up);
        }

        public void onInterfaceLinkStateChanged(String ifName, boolean up) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(ifName, up) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceLinkStateChanged$8$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1, this.f$2);
                }
            });
        }

        public void onRouteChanged(boolean updated, String route, String gateway, String ifName) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable(updated, new RouteInfo(new IpPrefix(route), "".equals(gateway) ? null : InetAddresses.parseNumericAddress(gateway), ifName)) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ RouteInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onRouteChanged$9$NetworkManagementService$NetdUnsolicitedEventListener(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$onRouteChanged$9$NetworkManagementService$NetdUnsolicitedEventListener(boolean updated, RouteInfo processRoute) {
            NetworkManagementService.this.notifyRouteChange(updated, processRoute);
        }

        public void onStrictCleartextDetected(int uid, String hex) throws RemoteException {
            ActivityManager.getService().notifyCleartextNetwork(uid, HexDump.hexStringToByteArray(hex));
        }

        public int getInterfaceVersion() {
            return 2;
        }
    }

    public String[] listInterfaces() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetdService.interfaceGetList();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    private static InterfaceConfigurationParcel toStableParcel(InterfaceConfiguration cfg, String iface) {
        InterfaceConfigurationParcel cfgParcel = new InterfaceConfigurationParcel();
        cfgParcel.ifName = iface;
        String hwAddr = cfg.getHardwareAddress();
        if (!TextUtils.isEmpty(hwAddr)) {
            cfgParcel.hwAddr = hwAddr;
        } else {
            cfgParcel.hwAddr = "";
        }
        cfgParcel.ipv4Addr = cfg.getLinkAddress().getAddress().getHostAddress();
        cfgParcel.prefixLength = cfg.getLinkAddress().getPrefixLength();
        ArrayList<String> flags = new ArrayList<>();
        for (String flag : cfg.getFlags()) {
            flags.add(flag);
        }
        cfgParcel.flags = (String[]) flags.toArray(new String[0]);
        return cfgParcel;
    }

    public static InterfaceConfiguration fromStableParcel(InterfaceConfigurationParcel p) {
        InterfaceConfiguration cfg = new InterfaceConfiguration();
        cfg.setHardwareAddress(p.hwAddr);
        cfg.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress(p.ipv4Addr), p.prefixLength));
        for (String flag : p.flags) {
            cfg.setFlag(flag);
        }
        return cfg;
    }

    public InterfaceConfiguration getInterfaceConfig(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            try {
                return fromStableParcel(this.mNetdService.interfaceGetCfg(iface));
            } catch (IllegalArgumentException iae) {
                throw new IllegalStateException("Invalid InterfaceConfigurationParcel", iae);
            }
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setInterfaceConfig(String iface, InterfaceConfiguration cfg) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        LinkAddress linkAddr = cfg.getLinkAddress();
        if (linkAddr == null || linkAddr.getAddress() == null) {
            throw new IllegalStateException("Null LinkAddress given");
        }
        try {
            this.mNetdService.interfaceSetCfg(toStableParcel(cfg, iface));
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setInterfaceDown(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        InterfaceConfiguration ifcg = getInterfaceConfig(iface);
        ifcg.setInterfaceDown();
        setInterfaceConfig(iface, ifcg);
    }

    public void setInterfaceUp(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        InterfaceConfiguration ifcg = getInterfaceConfig(iface);
        ifcg.setInterfaceUp();
        setInterfaceConfig(iface, ifcg);
    }

    public void setInterfaceIpv6PrivacyExtensions(String iface, boolean enable) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.interfaceSetIPv6PrivacyExtensions(iface, enable);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void clearInterfaceAddresses(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.interfaceClearAddrs(iface);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void enableIpv6(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.interfaceSetEnableIPv6(iface, true);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setIPv6AddrGenMode(String iface, int mode) throws ServiceSpecificException {
        try {
            this.mNetdService.setIPv6AddrGenMode(iface, mode);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public void disableIpv6(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.interfaceSetEnableIPv6(iface, false);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void addRoute(int netId, RouteInfo route) {
        modifyRoute(true, netId, route);
    }

    public void removeRoute(int netId, RouteInfo route) {
        modifyRoute(false, netId, route);
    }

    private void modifyRoute(boolean add, int netId, RouteInfo route) {
        String nextHop;
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        String ifName = route.getInterface();
        String dst = route.getDestination().toString();
        int type = route.getType();
        if (type != 1) {
            if (type == 7) {
                nextHop = INetd.NEXTHOP_UNREACHABLE;
            } else if (type != 9) {
                nextHop = "";
            } else {
                nextHop = INetd.NEXTHOP_THROW;
            }
        } else if (route.hasGateway()) {
            nextHop = route.getGateway().getHostAddress();
        } else {
            nextHop = "";
        }
        if (add) {
            try {
                this.mNetdService.networkAddRoute(netId, ifName, dst, nextHop);
            } catch (RemoteException | ServiceSpecificException e) {
                throw new IllegalStateException(e);
            }
        } else {
            this.mNetdService.networkRemoveRoute(netId, ifName, dst, nextHop);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.ArrayList<java.lang.String> readRouteList(java.lang.String r7) {
        /*
            r6 = this;
            r0 = 0
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            r2.<init>(r7)     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            r0 = r2
            java.io.DataInputStream r2 = new java.io.DataInputStream     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            r2.<init>(r0)     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            java.io.InputStreamReader r4 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            r4.<init>(r2)     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            r3.<init>(r4)     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
        L_0x001b:
            java.lang.String r4 = r3.readLine()     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            r5 = r4
            if (r4 == 0) goto L_0x002c
            int r4 = r5.length()     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            if (r4 == 0) goto L_0x002c
            r1.add(r5)     // Catch:{ IOException -> 0x003d, all -> 0x0033 }
            goto L_0x001b
        L_0x002c:
            r0.close()     // Catch:{ IOException -> 0x0031 }
        L_0x0030:
            goto L_0x0044
        L_0x0031:
            r2 = move-exception
            goto L_0x0030
        L_0x0033:
            r2 = move-exception
            if (r0 == 0) goto L_0x003c
            r0.close()     // Catch:{ IOException -> 0x003a }
        L_0x0039:
            goto L_0x003c
        L_0x003a:
            r3 = move-exception
            goto L_0x0039
        L_0x003c:
            throw r2
        L_0x003d:
            r2 = move-exception
            if (r0 == 0) goto L_0x0044
            r0.close()     // Catch:{ IOException -> 0x0031 }
            goto L_0x0030
        L_0x0044:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetworkManagementService.readRouteList(java.lang.String):java.util.ArrayList");
    }

    public void setMtu(String iface, int mtu) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.interfaceSetMtu(iface, mtu);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void shutdown() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.SHUTDOWN", TAG);
        Slog.i(TAG, "Shutting down");
    }

    public boolean getIpForwardingEnabled() throws IllegalStateException {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetdService.ipfwdEnabled();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setIpForwardingEnabled(boolean enable) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        if (enable) {
            try {
                this.mNetdService.ipfwdEnableForwarding("tethering");
            } catch (RemoteException | ServiceSpecificException e) {
                throw new IllegalStateException(e);
            }
        } else {
            this.mNetdService.ipfwdDisableForwarding("tethering");
        }
    }

    public void startTethering(String[] dhcpRange) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.tetherStart(dhcpRange);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void stopTethering() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.tetherStop();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isTetheringStarted() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetdService.tetherIsEnabled();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void tetherInterface(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.tetherInterfaceAdd(iface);
            List<RouteInfo> routes = new ArrayList<>();
            routes.add(new RouteInfo(getInterfaceConfig(iface).getLinkAddress(), (InetAddress) null, iface));
            addInterfaceToLocalNetwork(iface, routes);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void untetherInterface(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.tetherInterfaceRemove(iface);
            removeInterfaceFromLocalNetwork(iface);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        } catch (Throwable th) {
            removeInterfaceFromLocalNetwork(iface);
            throw th;
        }
    }

    public String[] listTetheredInterfaces() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetdService.tetherInterfaceList();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setDnsForwarders(Network network, String[] dns) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.tetherDnsSet(network != null ? network.netId : 0, dns);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public String[] getDnsForwarders() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetdService.tetherDnsList();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<InterfaceAddress> excludeLinkLocal(List<InterfaceAddress> addresses) {
        ArrayList<InterfaceAddress> filtered = new ArrayList<>(addresses.size());
        for (InterfaceAddress ia : addresses) {
            if (!ia.getAddress().isLinkLocalAddress()) {
                filtered.add(ia);
            }
        }
        return filtered;
    }

    private void modifyInterfaceForward(boolean add, String fromIface, String toIface) {
        if (add) {
            try {
                this.mNetdService.ipfwdAddInterfaceForward(fromIface, toIface);
            } catch (RemoteException | ServiceSpecificException e) {
                throw new IllegalStateException(e);
            }
        } else {
            this.mNetdService.ipfwdRemoveInterfaceForward(fromIface, toIface);
        }
    }

    public void startInterfaceForwarding(String fromIface, String toIface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        modifyInterfaceForward(true, fromIface, toIface);
    }

    public void stopInterfaceForwarding(String fromIface, String toIface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        modifyInterfaceForward(false, fromIface, toIface);
    }

    public void enableNat(String internalInterface, String externalInterface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.tetherAddForward(internalInterface, externalInterface);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void disableNat(String internalInterface, String externalInterface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.tetherRemoveForward(internalInterface, externalInterface);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void addIdleTimer(String iface, int timeout, int type) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        if (DBG) {
            Slog.d(TAG, "Adding idletimer");
        }
        synchronized (this.mIdleTimerLock) {
            IdleTimerParams params = this.mActiveIdleTimers.get(iface);
            if (params != null) {
                params.networkCount++;
                return;
            }
            try {
                this.mNetdService.idletimerAddInterface(iface, timeout, Integer.toString(type));
                this.mActiveIdleTimers.put(iface, new IdleTimerParams(timeout, type));
                if (ConnectivityManager.isNetworkTypeMobile(type)) {
                    this.mNetworkActive = false;
                }
                this.mDaemonHandler.post(new Runnable(type) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NetworkManagementService.this.lambda$addIdleTimer$12$NetworkManagementService(this.f$1);
                    }
                });
            } catch (RemoteException | ServiceSpecificException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public /* synthetic */ void lambda$addIdleTimer$12$NetworkManagementService(int type) {
        notifyInterfaceClassActivity(type, true, SystemClock.elapsedRealtimeNanos(), -1, false);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0052, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeIdleTimer(java.lang.String r6) {
        /*
            r5 = this;
            android.content.Context r0 = r5.mContext
            java.lang.String r1 = "android.permission.CONNECTIVITY_INTERNAL"
            java.lang.String r2 = "NetworkManagement"
            r0.enforceCallingOrSelfPermission(r1, r2)
            boolean r0 = DBG
            if (r0 == 0) goto L_0x0014
            java.lang.String r0 = "NetworkManagement"
            java.lang.String r1 = "Removing idletimer"
            android.util.Slog.d(r0, r1)
        L_0x0014:
            java.lang.Object r0 = r5.mIdleTimerLock
            monitor-enter(r0)
            java.util.HashMap<java.lang.String, com.android.server.NetworkManagementService$IdleTimerParams> r1 = r5.mActiveIdleTimers     // Catch:{ all -> 0x0053 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0053 }
            com.android.server.NetworkManagementService$IdleTimerParams r1 = (com.android.server.NetworkManagementService.IdleTimerParams) r1     // Catch:{ all -> 0x0053 }
            if (r1 == 0) goto L_0x0051
            int r2 = r1.networkCount     // Catch:{ all -> 0x0053 }
            int r2 = r2 + -1
            r1.networkCount = r2     // Catch:{ all -> 0x0053 }
            if (r2 <= 0) goto L_0x002a
            goto L_0x0051
        L_0x002a:
            android.net.INetd r2 = r5.mNetdService     // Catch:{ RemoteException | ServiceSpecificException -> 0x004a }
            int r3 = r1.timeout     // Catch:{ RemoteException | ServiceSpecificException -> 0x004a }
            int r4 = r1.type     // Catch:{ RemoteException | ServiceSpecificException -> 0x004a }
            java.lang.String r4 = java.lang.Integer.toString(r4)     // Catch:{ RemoteException | ServiceSpecificException -> 0x004a }
            r2.idletimerRemoveInterface(r6, r3, r4)     // Catch:{ RemoteException | ServiceSpecificException -> 0x004a }
            java.util.HashMap<java.lang.String, com.android.server.NetworkManagementService$IdleTimerParams> r2 = r5.mActiveIdleTimers     // Catch:{ all -> 0x0053 }
            r2.remove(r6)     // Catch:{ all -> 0x0053 }
            android.os.Handler r2 = r5.mDaemonHandler     // Catch:{ all -> 0x0053 }
            com.android.server.-$$Lambda$NetworkManagementService$15DusjG2gzn5UASV-lMS3BUUn9c r3 = new com.android.server.-$$Lambda$NetworkManagementService$15DusjG2gzn5UASV-lMS3BUUn9c     // Catch:{ all -> 0x0053 }
            r3.<init>(r1)     // Catch:{ all -> 0x0053 }
            r2.post(r3)     // Catch:{ all -> 0x0053 }
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return
        L_0x004a:
            r2 = move-exception
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0053 }
            r3.<init>(r2)     // Catch:{ all -> 0x0053 }
            throw r3     // Catch:{ all -> 0x0053 }
        L_0x0051:
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return
        L_0x0053:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetworkManagementService.removeIdleTimer(java.lang.String):void");
    }

    public /* synthetic */ void lambda$removeIdleTimer$13$NetworkManagementService(IdleTimerParams params) {
        notifyInterfaceClassActivity(params.type, false, SystemClock.elapsedRealtimeNanos(), -1, false);
    }

    public NetworkStats getNetworkStatsSummaryDev() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mStatsFactory.readNetworkStatsSummaryDev();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public NetworkStats getNetworkStatsSummaryXt() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mStatsFactory.readNetworkStatsSummaryXt();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public NetworkStats getNetworkStatsDetail() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mStatsFactory.readNetworkStatsDetail(-1, (String[]) null, -1, (NetworkStats) null);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void setInterfaceQuota(String iface, long quotaBytes) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        synchronized (this.mQuotaLock) {
            if (!this.mActiveQuotas.containsKey(iface)) {
                try {
                    this.mNetdService.bandwidthSetInterfaceQuota(iface, quotaBytes);
                    this.mActiveQuotas.put(iface, Long.valueOf(quotaBytes));
                    synchronized (this.mTetheringStatsProviders) {
                        for (ITetheringStatsProvider provider : this.mTetheringStatsProviders.keySet()) {
                            try {
                                provider.setInterfaceQuota(iface, quotaBytes);
                            } catch (RemoteException e) {
                                Log.e(TAG, "Problem setting tethering data limit on provider " + this.mTetheringStatsProviders.get(provider) + ": " + e);
                            }
                        }
                    }
                } catch (RemoteException | ServiceSpecificException e2) {
                    throw new IllegalStateException(e2);
                }
            } else {
                throw new IllegalStateException("iface " + iface + " already has quota");
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void removeInterfaceQuota(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        synchronized (this.mQuotaLock) {
            if (this.mActiveQuotas.containsKey(iface)) {
                this.mActiveQuotas.remove(iface);
                this.mActiveAlerts.remove(iface);
                try {
                    this.mNetdService.bandwidthRemoveInterfaceQuota(iface);
                    synchronized (this.mTetheringStatsProviders) {
                        for (ITetheringStatsProvider provider : this.mTetheringStatsProviders.keySet()) {
                            try {
                                provider.setInterfaceQuota(iface, -1);
                            } catch (RemoteException e) {
                                Log.e(TAG, "Problem removing tethering data limit on provider " + this.mTetheringStatsProviders.get(provider) + ": " + e);
                            }
                        }
                    }
                } catch (RemoteException | ServiceSpecificException e2) {
                    throw new IllegalStateException(e2);
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void setInterfaceAlert(String iface, long alertBytes) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        if (this.mActiveQuotas.containsKey(iface)) {
            synchronized (this.mQuotaLock) {
                if (!this.mActiveAlerts.containsKey(iface)) {
                    try {
                        this.mNetdService.bandwidthSetInterfaceAlert(iface, alertBytes);
                        this.mActiveAlerts.put(iface, Long.valueOf(alertBytes));
                    } catch (RemoteException | ServiceSpecificException e) {
                        throw new IllegalStateException(e);
                    }
                } else {
                    throw new IllegalStateException("iface " + iface + " already has alert");
                }
            }
            return;
        }
        throw new IllegalStateException("setting alert requires existing quota on iface");
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void removeInterfaceAlert(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        synchronized (this.mQuotaLock) {
            if (this.mActiveAlerts.containsKey(iface)) {
                try {
                    this.mNetdService.bandwidthRemoveInterfaceAlert(iface);
                    this.mActiveAlerts.remove(iface);
                } catch (RemoteException | ServiceSpecificException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    public void setGlobalAlert(long alertBytes) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.bandwidthSetGlobalAlert(alertBytes);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    private void setUidOnMeteredNetworkList(int uid, boolean blacklist, boolean enable) {
        SparseBooleanArray quotaList;
        boolean oldEnable;
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        synchronized (this.mQuotaLock) {
            synchronized (this.mRulesLock) {
                quotaList = blacklist ? this.mUidRejectOnMetered : this.mUidAllowOnMetered;
                oldEnable = quotaList.get(uid, false);
            }
            if (oldEnable != enable) {
                Trace.traceBegin(2097152, "inetd bandwidth");
                if (blacklist) {
                    if (enable) {
                        try {
                            this.mNetdService.bandwidthAddNaughtyApp(uid);
                        } catch (RemoteException | ServiceSpecificException e) {
                            try {
                                throw new IllegalStateException(e);
                            } catch (Throwable th) {
                                Trace.traceEnd(2097152);
                                throw th;
                            }
                        }
                    } else {
                        this.mNetdService.bandwidthRemoveNaughtyApp(uid);
                    }
                } else if (enable) {
                    this.mNetdService.bandwidthAddNiceApp(uid);
                } else {
                    this.mNetdService.bandwidthRemoveNiceApp(uid);
                }
                synchronized (this.mRulesLock) {
                    if (enable) {
                        quotaList.put(uid, true);
                    } else {
                        quotaList.delete(uid);
                    }
                }
                Trace.traceEnd(2097152);
            }
        }
    }

    public void setUidMeteredNetworkBlacklist(int uid, boolean enable) {
        setUidOnMeteredNetworkList(uid, true, enable);
    }

    public void setUidMeteredNetworkWhitelist(int uid, boolean enable) {
        setUidOnMeteredNetworkList(uid, false, enable);
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public boolean setDataSaverModeEnabled(boolean enable) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_SETTINGS", TAG);
        if (DBG) {
            Log.d(TAG, "setDataSaverMode: " + enable);
        }
        synchronized (this.mQuotaLock) {
            if (this.mDataSaverMode == enable) {
                Log.w(TAG, "setDataSaverMode(): already " + this.mDataSaverMode);
                return true;
            }
            Trace.traceBegin(2097152, "bandwidthEnableDataSaver");
            try {
                boolean changed = this.mNetdService.bandwidthEnableDataSaver(enable);
                if (changed) {
                    this.mDataSaverMode = enable;
                } else {
                    Log.w(TAG, "setDataSaverMode(" + enable + "): netd command silently failed");
                }
                Trace.traceEnd(2097152);
                return changed;
            } catch (RemoteException e) {
                try {
                    Log.w(TAG, "setDataSaverMode(" + enable + "): netd command failed", e);
                    return false;
                } finally {
                    Trace.traceEnd(2097152);
                }
            }
        }
    }

    private static UidRangeParcel makeUidRangeParcel(int start, int stop) {
        UidRangeParcel range = new UidRangeParcel();
        range.start = start;
        range.stop = stop;
        return range;
    }

    private static UidRangeParcel[] toStableParcels(UidRange[] ranges) {
        UidRangeParcel[] stableRanges = new UidRangeParcel[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            stableRanges[i] = makeUidRangeParcel(ranges[i].start, ranges[i].stop);
        }
        return stableRanges;
    }

    public void setAllowOnlyVpnForUids(boolean add, UidRange[] uidRanges) throws ServiceSpecificException {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_STACK", TAG);
        try {
            this.mNetdService.networkRejectNonSecureVpn(add, toStableParcels(uidRanges));
        } catch (ServiceSpecificException e) {
            Log.w(TAG, "setAllowOnlyVpnForUids(" + add + ", " + Arrays.toString(uidRanges) + "): netd command failed", e);
            throw e;
        } catch (RemoteException e2) {
            Log.w(TAG, "setAllowOnlyVpnForUids(" + add + ", " + Arrays.toString(uidRanges) + "): netd command failed", e2);
            throw e2.rethrowAsRuntimeException();
        }
    }

    private void applyUidCleartextNetworkPolicy(int uid, int policy) {
        int policyValue;
        if (policy == 0) {
            policyValue = 1;
        } else if (policy == 1) {
            policyValue = 2;
        } else if (policy == 2) {
            policyValue = 3;
        } else {
            throw new IllegalArgumentException("Unknown policy " + policy);
        }
        try {
            this.mNetdService.strictUidCleartextPenalty(uid, policyValue);
            this.mUidCleartextPolicy.put(uid, policy);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setUidCleartextNetworkPolicy(int uid, int policy) {
        if (Binder.getCallingUid() != uid) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        }
        synchronized (this.mQuotaLock) {
            int oldPolicy = this.mUidCleartextPolicy.get(uid, 0);
            if (oldPolicy != policy) {
                if (!this.mStrictEnabled) {
                    this.mUidCleartextPolicy.put(uid, policy);
                    return;
                }
                if (!(oldPolicy == 0 || policy == 0)) {
                    applyUidCleartextNetworkPolicy(uid, 0);
                }
                applyUidCleartextNetworkPolicy(uid, policy);
            }
        }
    }

    public boolean isBandwidthControlEnabled() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        return true;
    }

    public NetworkStats getNetworkStatsUidDetail(int uid, String[] ifaces) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mStatsFactory.readNetworkStatsDetail(uid, ifaces, -1, (NetworkStats) null);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private class NetdTetheringStatsProvider extends ITetheringStatsProvider.Stub {
        private NetdTetheringStatsProvider() {
        }

        public NetworkStats getTetherStats(int how) {
            if (how != 1) {
                return new NetworkStats(SystemClock.elapsedRealtime(), 0);
            }
            try {
                TetherStatsParcel[] tetherStatsVec = NetworkManagementService.this.mNetdService.tetherGetStats();
                NetworkStats stats = new NetworkStats(SystemClock.elapsedRealtime(), tetherStatsVec.length);
                NetworkStats.Entry entry = new NetworkStats.Entry();
                int length = tetherStatsVec.length;
                int i = 0;
                while (i < length) {
                    TetherStatsParcel tetherStats = tetherStatsVec[i];
                    try {
                        entry.iface = tetherStats.iface;
                        entry.uid = -5;
                        entry.set = 0;
                        entry.tag = 0;
                        entry.rxBytes = tetherStats.rxBytes;
                        entry.rxPackets = tetherStats.rxPackets;
                        entry.txBytes = tetherStats.txBytes;
                        entry.txPackets = tetherStats.txPackets;
                        stats.combineValues(entry);
                        i++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new IllegalStateException("invalid tethering stats " + e);
                    }
                }
                return stats;
            } catch (RemoteException | ServiceSpecificException e2) {
                throw new IllegalStateException("problem parsing tethering stats: ", e2);
            }
        }

        public void setInterfaceQuota(String iface, long quotaBytes) {
        }
    }

    public NetworkStats getNetworkStatsTethering(int how) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        NetworkStats stats = new NetworkStats(SystemClock.elapsedRealtime(), 1);
        synchronized (this.mTetheringStatsProviders) {
            for (ITetheringStatsProvider provider : this.mTetheringStatsProviders.keySet()) {
                try {
                    stats.combineAllValues(provider.getTetherStats(how));
                } catch (RemoteException e) {
                    Log.e(TAG, "Problem reading tethering stats from " + this.mTetheringStatsProviders.get(provider) + ": " + e);
                }
            }
        }
        return stats;
    }

    public void addVpnUidRanges(int netId, UidRange[] ranges) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.networkAddUidRanges(netId, toStableParcels(ranges));
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void removeVpnUidRanges(int netId, UidRange[] ranges) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.networkRemoveUidRanges(netId, toStableParcels(ranges));
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setFirewallEnabled(boolean enabled) {
        enforceSystemUid();
        try {
            this.mNetdService.firewallSetFirewallType(enabled ? 0 : 1);
            this.mFirewallEnabled = enabled;
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isFirewallEnabled() {
        enforceSystemUid();
        return this.mFirewallEnabled;
    }

    public void setFirewallInterfaceRule(String iface, boolean allow) {
        enforceSystemUid();
        Preconditions.checkState(this.mFirewallEnabled);
        try {
            this.mNetdService.firewallSetInterfaceRule(iface, allow ? 1 : 2);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    private void closeSocketsForFirewallChainLocked(int chain, String chainName) {
        int[] exemptUids;
        UidRangeParcel[] ranges;
        UidRangeParcel[] ranges2;
        int numUids = 0;
        if (DBG) {
            Slog.d(TAG, "Closing sockets after enabling chain " + chainName);
        }
        if (getFirewallType(chain) == 0) {
            ranges = new UidRangeParcel[]{makeUidRangeParcel(10000, Integer.MAX_VALUE)};
            synchronized (this.mRulesLock) {
                SparseIntArray rules = getUidFirewallRulesLR(chain);
                exemptUids = new int[rules.size()];
                for (int i = 0; i < exemptUids.length; i++) {
                    if (rules.valueAt(i) == 1) {
                        exemptUids[numUids] = rules.keyAt(i);
                        numUids++;
                    }
                }
            }
            if (numUids != exemptUids.length) {
                exemptUids = Arrays.copyOf(exemptUids, numUids);
            }
        } else {
            synchronized (this.mRulesLock) {
                SparseIntArray rules2 = getUidFirewallRulesLR(chain);
                ranges2 = new UidRangeParcel[rules2.size()];
                for (int i2 = 0; i2 < ranges2.length; i2++) {
                    if (rules2.valueAt(i2) == 2) {
                        int uid = rules2.keyAt(i2);
                        ranges2[numUids] = makeUidRangeParcel(uid, uid);
                        numUids++;
                    }
                }
            }
            if (numUids != ranges2.length) {
                ranges = (UidRangeParcel[]) Arrays.copyOf(ranges2, numUids);
            } else {
                ranges = ranges2;
            }
            exemptUids = new int[0];
        }
        try {
            this.mNetdService.socketDestroy(ranges, exemptUids);
        } catch (RemoteException | ServiceSpecificException e) {
            Slog.e(TAG, "Error closing sockets after enabling chain " + chainName + ": " + e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r1 = getFirewallChainName(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001a, code lost:
        if (r6 == 0) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r5.mNetdService.firewallEnableChildChain(r6, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0022, code lost:
        if (r7 == false) goto L_0x0027;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        closeSocketsForFirewallChainLocked(r6, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0028, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0029, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x002f, code lost:
        throw new java.lang.IllegalStateException(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0046, code lost:
        throw new java.lang.IllegalArgumentException("Bad child chain: " + r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setFirewallChainEnabled(int r6, boolean r7) {
        /*
            r5 = this;
            enforceSystemUid()
            java.lang.Object r0 = r5.mQuotaLock
            monitor-enter(r0)
            java.lang.Object r1 = r5.mRulesLock     // Catch:{ all -> 0x004a }
            monitor-enter(r1)     // Catch:{ all -> 0x004a }
            boolean r2 = r5.getFirewallChainState(r6)     // Catch:{ all -> 0x0047 }
            if (r2 != r7) goto L_0x0012
            monitor-exit(r1)     // Catch:{ all -> 0x0047 }
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            return
        L_0x0012:
            r5.setFirewallChainState(r6, r7)     // Catch:{ all -> 0x0047 }
            monitor-exit(r1)     // Catch:{ all -> 0x0047 }
            java.lang.String r1 = r5.getFirewallChainName(r6)     // Catch:{ all -> 0x004a }
            if (r6 == 0) goto L_0x0030
            android.net.INetd r2 = r5.mNetdService     // Catch:{ RemoteException | ServiceSpecificException -> 0x0029 }
            r2.firewallEnableChildChain(r6, r7)     // Catch:{ RemoteException | ServiceSpecificException -> 0x0029 }
            if (r7 == 0) goto L_0x0027
            r5.closeSocketsForFirewallChainLocked(r6, r1)     // Catch:{ all -> 0x004a }
        L_0x0027:
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            return
        L_0x0029:
            r2 = move-exception
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ all -> 0x004a }
            r3.<init>(r2)     // Catch:{ all -> 0x004a }
            throw r3     // Catch:{ all -> 0x004a }
        L_0x0030:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x004a }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x004a }
            r3.<init>()     // Catch:{ all -> 0x004a }
            java.lang.String r4 = "Bad child chain: "
            r3.append(r4)     // Catch:{ all -> 0x004a }
            r3.append(r1)     // Catch:{ all -> 0x004a }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x004a }
            r2.<init>(r3)     // Catch:{ all -> 0x004a }
            throw r2     // Catch:{ all -> 0x004a }
        L_0x0047:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0047 }
            throw r2     // Catch:{ all -> 0x004a }
        L_0x004a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetworkManagementService.setFirewallChainEnabled(int, boolean):void");
    }

    private String getFirewallChainName(int chain) {
        if (chain == 1) {
            return "dozable";
        }
        if (chain == 2) {
            return "standby";
        }
        if (chain == 3) {
            return "powersave";
        }
        throw new IllegalArgumentException("Bad child chain: " + chain);
    }

    private int getFirewallType(int chain) {
        if (chain == 1) {
            return 0;
        }
        if (chain == 2) {
            return 1;
        }
        if (chain != 3) {
            return isFirewallEnabled() ^ true ? 1 : 0;
        }
        return 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void setFirewallUidRules(int chain, int[] uids, int[] rules) {
        int[] uids2;
        enforceSystemUid();
        synchronized (this.mQuotaLock) {
            synchronized (this.mRulesLock) {
                SparseIntArray uidFirewallRules = getUidFirewallRulesLR(chain);
                SparseIntArray newRules = new SparseIntArray();
                for (int index = uids.length - 1; index >= 0; index--) {
                    int uid = uids[index];
                    int rule = rules[index];
                    updateFirewallUidRuleLocked(chain, uid, rule);
                    if (rule != 0) {
                        newRules.put(uid, rule);
                    }
                }
                SparseIntArray rulesToRemove = new SparseIntArray();
                for (int index2 = uidFirewallRules.size() - 1; index2 >= 0; index2--) {
                    int uid2 = uidFirewallRules.keyAt(index2);
                    if (newRules.indexOfKey(uid2) < 0) {
                        rulesToRemove.put(uid2, 0);
                    }
                }
                for (int index3 = rulesToRemove.size() - 1; index3 >= 0; index3--) {
                    updateFirewallUidRuleLocked(chain, rulesToRemove.keyAt(index3), 0);
                }
                uids2 = newRules.size() > 0 ? newRules.copyKeys() : EmptyArray.INT;
            }
            if (chain == 1) {
                this.mNetdService.firewallReplaceUidChain("fw_dozable", true, uids2);
            } else if (chain == 2) {
                this.mNetdService.firewallReplaceUidChain("fw_standby", false, uids2);
            } else if (chain != 3) {
                try {
                    Slog.d(TAG, "setFirewallUidRules() called on invalid chain: " + chain);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Error flushing firewall chain " + chain, e);
                }
            } else {
                this.mNetdService.firewallReplaceUidChain("fw_powersave", true, uids2);
            }
        }
    }

    public void setFirewallUidRule(int chain, int uid, int rule) {
        enforceSystemUid();
        synchronized (this.mQuotaLock) {
            setFirewallUidRuleLocked(chain, uid, rule);
        }
    }

    private void setFirewallUidRuleLocked(int chain, int uid, int rule) {
        if (updateFirewallUidRuleLocked(chain, uid, rule)) {
            try {
                this.mNetdService.firewallSetUidRule(chain, uid, getFirewallRuleType(chain, rule));
            } catch (RemoteException | ServiceSpecificException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x004d, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0067, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean updateFirewallUidRuleLocked(int r8, int r9, int r10) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mRulesLock
            monitor-enter(r0)
            android.util.SparseIntArray r1 = r7.getUidFirewallRulesLR(r8)     // Catch:{ all -> 0x0068 }
            r2 = 0
            int r3 = r1.get(r9, r2)     // Catch:{ all -> 0x0068 }
            boolean r4 = DBG     // Catch:{ all -> 0x0068 }
            if (r4 == 0) goto L_0x003f
            java.lang.String r4 = "NetworkManagement"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0068 }
            r5.<init>()     // Catch:{ all -> 0x0068 }
            java.lang.String r6 = "oldRule = "
            r5.append(r6)     // Catch:{ all -> 0x0068 }
            r5.append(r3)     // Catch:{ all -> 0x0068 }
            java.lang.String r6 = ", newRule="
            r5.append(r6)     // Catch:{ all -> 0x0068 }
            r5.append(r10)     // Catch:{ all -> 0x0068 }
            java.lang.String r6 = " for uid="
            r5.append(r6)     // Catch:{ all -> 0x0068 }
            r5.append(r9)     // Catch:{ all -> 0x0068 }
            java.lang.String r6 = " on chain "
            r5.append(r6)     // Catch:{ all -> 0x0068 }
            r5.append(r8)     // Catch:{ all -> 0x0068 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0068 }
            android.util.Slog.d(r4, r5)     // Catch:{ all -> 0x0068 }
        L_0x003f:
            if (r3 != r10) goto L_0x004e
            boolean r4 = DBG     // Catch:{ all -> 0x0068 }
            if (r4 == 0) goto L_0x004c
            java.lang.String r4 = "NetworkManagement"
            java.lang.String r5 = "!!!!! Skipping change"
            android.util.Slog.d(r4, r5)     // Catch:{ all -> 0x0068 }
        L_0x004c:
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            return r2
        L_0x004e:
            java.lang.String r4 = r7.getFirewallRuleName(r8, r10)     // Catch:{ all -> 0x0068 }
            java.lang.String r5 = r7.getFirewallRuleName(r8, r3)     // Catch:{ all -> 0x0068 }
            if (r10 != 0) goto L_0x005c
            r1.delete(r9)     // Catch:{ all -> 0x0068 }
            goto L_0x005f
        L_0x005c:
            r1.put(r9, r10)     // Catch:{ all -> 0x0068 }
        L_0x005f:
            boolean r6 = r4.equals(r5)     // Catch:{ all -> 0x0068 }
            if (r6 != 0) goto L_0x0066
            r2 = 1
        L_0x0066:
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            return r2
        L_0x0068:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetworkManagementService.updateFirewallUidRuleLocked(int, int, int):boolean");
    }

    private String getFirewallRuleName(int chain, int rule) {
        if (getFirewallType(chain) == 0) {
            if (rule == 1) {
                return "allow";
            }
            return "deny";
        } else if (rule == 2) {
            return "deny";
        } else {
            return "allow";
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mRulesLock"})
    public SparseIntArray getUidFirewallRulesLR(int chain) {
        if (chain == 0) {
            return this.mUidFirewallRules;
        }
        if (chain == 1) {
            return this.mUidFirewallDozableRules;
        }
        if (chain == 2) {
            return this.mUidFirewallStandbyRules;
        }
        if (chain == 3) {
            return this.mUidFirewallPowerSaveRules;
        }
        throw new IllegalArgumentException("Unknown chain:" + chain);
    }

    private int getFirewallRuleType(int chain, int rule) {
        if (rule == 0) {
            return getFirewallType(chain) == 0 ? 2 : 1;
        }
        return rule;
    }

    private static void enforceSystemUid() {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Only available to AID_SYSTEM");
        }
    }

    public void registerNetworkActivityListener(INetworkActivityListener listener) {
        this.mNetworkActivityListeners.register(listener);
    }

    public void unregisterNetworkActivityListener(INetworkActivityListener listener) {
        this.mNetworkActivityListeners.unregister(listener);
    }

    public boolean isNetworkActive() {
        boolean z;
        synchronized (this.mNetworkActivityListeners) {
            if (!this.mNetworkActive) {
                if (!this.mActiveIdleTimers.isEmpty()) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    private void reportNetworkActive() {
        int length = this.mNetworkActivityListeners.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mNetworkActivityListeners.getBroadcastItem(i).onNetworkActive();
            } catch (RemoteException | RuntimeException e) {
            } catch (Throwable th) {
                this.mNetworkActivityListeners.finishBroadcast();
                throw th;
            }
        }
        this.mNetworkActivityListeners.finishBroadcast();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            pw.print("mMobileActivityFromRadio=");
            pw.print(this.mMobileActivityFromRadio);
            pw.print(" mLastPowerStateFromRadio=");
            pw.println(this.mLastPowerStateFromRadio);
            pw.print("mNetworkActive=");
            pw.println(this.mNetworkActive);
            synchronized (this.mQuotaLock) {
                pw.print("Active quota ifaces: ");
                pw.println(this.mActiveQuotas.toString());
                pw.print("Active alert ifaces: ");
                pw.println(this.mActiveAlerts.toString());
                pw.print("Data saver mode: ");
                pw.println(this.mDataSaverMode);
                synchronized (this.mRulesLock) {
                    dumpUidRuleOnQuotaLocked(pw, "blacklist", this.mUidRejectOnMetered);
                    dumpUidRuleOnQuotaLocked(pw, "whitelist", this.mUidAllowOnMetered);
                }
            }
            synchronized (this.mRulesLock) {
                dumpUidFirewallRule(pw, "", this.mUidFirewallRules);
                pw.print("UID firewall standby chain enabled: ");
                pw.println(getFirewallChainState(2));
                dumpUidFirewallRule(pw, "standby", this.mUidFirewallStandbyRules);
                pw.print("UID firewall dozable chain enabled: ");
                pw.println(getFirewallChainState(1));
                dumpUidFirewallRule(pw, "dozable", this.mUidFirewallDozableRules);
                pw.println("UID firewall powersave chain enabled: " + getFirewallChainState(3));
                dumpUidFirewallRule(pw, "powersave", this.mUidFirewallPowerSaveRules);
            }
            synchronized (this.mIdleTimerLock) {
                pw.println("Idle timers:");
                for (Map.Entry<String, IdleTimerParams> ent : this.mActiveIdleTimers.entrySet()) {
                    pw.print("  ");
                    pw.print(ent.getKey());
                    pw.println(":");
                    IdleTimerParams params = ent.getValue();
                    pw.print("    timeout=");
                    pw.print(params.timeout);
                    pw.print(" type=");
                    pw.print(params.type);
                    pw.print(" networkCount=");
                    pw.println(params.networkCount);
                }
            }
            pw.print("Firewall enabled: ");
            pw.println(this.mFirewallEnabled);
            pw.print("Netd service status: ");
            INetd iNetd = this.mNetdService;
            if (iNetd == null) {
                pw.println("disconnected");
                return;
            }
            try {
                pw.println(iNetd.isAlive() ? "alive" : "dead");
            } catch (RemoteException e) {
                pw.println(INetd.NEXTHOP_UNREACHABLE);
            }
        }
    }

    private void dumpUidRuleOnQuotaLocked(PrintWriter pw, String name, SparseBooleanArray list) {
        pw.print("UID bandwith control ");
        pw.print(name);
        pw.print(" rule: [");
        int size = list.size();
        for (int i = 0; i < size; i++) {
            pw.print(list.keyAt(i));
            if (i < size - 1) {
                pw.print(",");
            }
        }
        pw.println("]");
    }

    private void dumpUidFirewallRule(PrintWriter pw, String name, SparseIntArray rules) {
        pw.print("UID firewall ");
        pw.print(name);
        pw.print(" rule: [");
        int size = rules.size();
        for (int i = 0; i < size; i++) {
            pw.print(rules.keyAt(i));
            pw.print(":");
            pw.print(rules.valueAt(i));
            if (i < size - 1) {
                pw.print(",");
            }
        }
        pw.println("]");
    }

    public void addInterfaceToNetwork(String iface, int netId) {
        modifyInterfaceInNetwork(true, netId, iface);
    }

    public void removeInterfaceFromNetwork(String iface, int netId) {
        modifyInterfaceInNetwork(false, netId, iface);
    }

    private void modifyInterfaceInNetwork(boolean add, int netId, String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        if (add) {
            try {
                this.mNetdService.networkAddInterface(netId, iface);
            } catch (RemoteException | ServiceSpecificException e) {
                throw new IllegalStateException(e);
            }
        } else {
            this.mNetdService.networkRemoveInterface(netId, iface);
        }
    }

    public void addLegacyRouteForNetId(int netId, RouteInfo routeInfo, int uid) {
        String nextHop;
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        LinkAddress la = routeInfo.getDestinationLinkAddress();
        String ifName = routeInfo.getInterface();
        String dst = la.toString();
        if (routeInfo.hasGateway()) {
            nextHop = routeInfo.getGateway().getHostAddress();
        } else {
            nextHop = "";
        }
        try {
            this.mNetdService.networkAddLegacyRoute(netId, ifName, dst, nextHop, uid);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setDefaultNetId(int netId) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.networkSetDefault(netId);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void clearDefaultNetId() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.networkClearDefault();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setNetworkPermission(int netId, int permission) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.networkSetPermissionForNetwork(netId, permission);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void allowProtect(int uid) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.networkSetProtectAllow(uid);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void denyProtect(int uid) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetdService.networkSetProtectDeny(uid);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void addInterfaceToLocalNetwork(String iface, List<RouteInfo> routes) {
        modifyInterfaceInNetwork(true, 99, iface);
        if (iface != null) {
            try {
                if (NetworkInterface.getByName(iface) == null) {
                    Log.e(TAG, "network interface:" + iface + " is down !!!");
                    return;
                }
            } catch (SocketException e) {
                Log.e(TAG, "Error looking up NetworkInterfaces: " + e);
                return;
            }
        }
        for (RouteInfo route : routes) {
            if (!route.isDefaultRoute()) {
                modifyRoute(true, 99, route);
            }
        }
        modifyRoute(true, 99, new RouteInfo(new IpPrefix("fe80::/64"), (InetAddress) null, iface));
    }

    public void removeInterfaceFromLocalNetwork(String iface) {
        modifyInterfaceInNetwork(false, 99, iface);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.net.MiuiNetworkManager, android.os.IBinder] */
    public IBinder getMiuiNetworkManager() {
        return MiuiNetworkManager.get();
    }

    public int removeRoutesFromLocalNetwork(List<RouteInfo> routes) {
        int failures = 0;
        for (RouteInfo route : routes) {
            try {
                modifyRoute(false, 99, route);
            } catch (IllegalStateException e) {
                failures++;
            }
        }
        return failures;
    }

    public boolean isNetworkRestricted(int uid) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        return isNetworkRestrictedInternal(uid);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0033, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0062, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0092, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00bb, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00e8, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isNetworkRestrictedInternal(int r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mRulesLock
            monitor-enter(r0)
            r1 = 2
            boolean r2 = r5.getFirewallChainState(r1)     // Catch:{ all -> 0x00ec }
            r3 = 1
            if (r2 == 0) goto L_0x0034
            android.util.SparseIntArray r2 = r5.mUidFirewallStandbyRules     // Catch:{ all -> 0x00ec }
            int r2 = r2.get(r6)     // Catch:{ all -> 0x00ec }
            if (r2 != r1) goto L_0x0034
            boolean r1 = DBG     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x0032
            java.lang.String r1 = "NetworkManagement"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ec }
            r2.<init>()     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = "Uid "
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            r2.append(r6)     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = " restricted because of app standby mode"
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ec }
            android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x00ec }
        L_0x0032:
            monitor-exit(r0)     // Catch:{ all -> 0x00ec }
            return r3
        L_0x0034:
            boolean r1 = r5.getFirewallChainState(r3)     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x0063
            android.util.SparseIntArray r1 = r5.mUidFirewallDozableRules     // Catch:{ all -> 0x00ec }
            int r1 = r1.get(r6)     // Catch:{ all -> 0x00ec }
            if (r1 == r3) goto L_0x0063
            boolean r1 = DBG     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x0061
            java.lang.String r1 = "NetworkManagement"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ec }
            r2.<init>()     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = "Uid "
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            r2.append(r6)     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = " restricted because of device idle mode"
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ec }
            android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x00ec }
        L_0x0061:
            monitor-exit(r0)     // Catch:{ all -> 0x00ec }
            return r3
        L_0x0063:
            r1 = 3
            boolean r1 = r5.getFirewallChainState(r1)     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x0093
            android.util.SparseIntArray r1 = r5.mUidFirewallPowerSaveRules     // Catch:{ all -> 0x00ec }
            int r1 = r1.get(r6)     // Catch:{ all -> 0x00ec }
            if (r1 == r3) goto L_0x0093
            boolean r1 = DBG     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x0091
            java.lang.String r1 = "NetworkManagement"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ec }
            r2.<init>()     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = "Uid "
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            r2.append(r6)     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = " restricted because of power saver mode"
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ec }
            android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x00ec }
        L_0x0091:
            monitor-exit(r0)     // Catch:{ all -> 0x00ec }
            return r3
        L_0x0093:
            android.util.SparseBooleanArray r1 = r5.mUidRejectOnMetered     // Catch:{ all -> 0x00ec }
            boolean r1 = r1.get(r6)     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x00bc
            boolean r1 = DBG     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x00ba
            java.lang.String r1 = "NetworkManagement"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ec }
            r2.<init>()     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = "Uid "
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            r2.append(r6)     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = " restricted because of no metered data in the background"
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ec }
            android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x00ec }
        L_0x00ba:
            monitor-exit(r0)     // Catch:{ all -> 0x00ec }
            return r3
        L_0x00bc:
            boolean r1 = r5.mDataSaverMode     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x00e9
            android.util.SparseBooleanArray r1 = r5.mUidAllowOnMetered     // Catch:{ all -> 0x00ec }
            boolean r1 = r1.get(r6)     // Catch:{ all -> 0x00ec }
            if (r1 != 0) goto L_0x00e9
            boolean r1 = DBG     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x00e7
            java.lang.String r1 = "NetworkManagement"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ec }
            r2.<init>()     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = "Uid "
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            r2.append(r6)     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = " restricted because of data saver mode"
            r2.append(r4)     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ec }
            android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x00ec }
        L_0x00e7:
            monitor-exit(r0)     // Catch:{ all -> 0x00ec }
            return r3
        L_0x00e9:
            r1 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x00ec }
            return r1
        L_0x00ec:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00ec }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetworkManagementService.isNetworkRestrictedInternal(int):boolean");
    }

    /* access modifiers changed from: private */
    public void setFirewallChainState(int chain, boolean state) {
        synchronized (this.mRulesLock) {
            this.mFirewallChainStates.put(chain, state);
        }
    }

    private boolean getFirewallChainState(int chain) {
        boolean z;
        synchronized (this.mRulesLock) {
            z = this.mFirewallChainStates.get(chain);
        }
        return z;
    }

    @VisibleForTesting
    class LocalService extends NetworkManagementInternal {
        LocalService() {
        }

        public boolean isNetworkRestrictedForUid(int uid) {
            return NetworkManagementService.this.isNetworkRestrictedInternal(uid);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Injector getInjector() {
        return new Injector();
    }

    @VisibleForTesting
    class Injector {
        Injector() {
        }

        /* access modifiers changed from: package-private */
        public void setDataSaverMode(boolean dataSaverMode) {
            boolean unused = NetworkManagementService.this.mDataSaverMode = dataSaverMode;
        }

        /* access modifiers changed from: package-private */
        public void setFirewallChainState(int chain, boolean state) {
            NetworkManagementService.this.setFirewallChainState(chain, state);
        }

        /* access modifiers changed from: package-private */
        public void setFirewallRule(int chain, int uid, int rule) {
            synchronized (NetworkManagementService.this.mRulesLock) {
                NetworkManagementService.this.getUidFirewallRulesLR(chain).put(uid, rule);
            }
        }

        /* access modifiers changed from: package-private */
        public void setUidOnMeteredNetworkList(boolean blacklist, int uid, boolean enable) {
            synchronized (NetworkManagementService.this.mRulesLock) {
                if (blacklist) {
                    NetworkManagementService.this.mUidRejectOnMetered.put(uid, enable);
                } else {
                    NetworkManagementService.this.mUidAllowOnMetered.put(uid, enable);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            synchronized (NetworkManagementService.this.mRulesLock) {
                setDataSaverMode(false);
                for (int chain : new int[]{1, 2, 3}) {
                    setFirewallChainState(chain, false);
                    NetworkManagementService.this.getUidFirewallRulesLR(chain).clear();
                }
                NetworkManagementService.this.mUidAllowOnMetered.clear();
                NetworkManagementService.this.mUidRejectOnMetered.clear();
            }
        }
    }
}
