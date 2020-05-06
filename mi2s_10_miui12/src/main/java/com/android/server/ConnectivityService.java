package com.android.server;

import android.app.BroadcastOptions;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.CaptivePortal;
import android.net.ConnectionInfo;
import android.net.ConnectivityManager;
import android.net.ICaptivePortal;
import android.net.IConnectivityManager;
import android.net.IDnsResolver;
import android.net.IIpConnectivityMetrics;
import android.net.INetd;
import android.net.INetdEventCallback;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkMonitor;
import android.net.INetworkMonitorCallbacks;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyManager;
import android.net.INetworkStatsService;
import android.net.ISocketKeepaliveCallback;
import android.net.ITetheringEventCallback;
import android.net.InetAddresses;
import android.net.IpMemoryStore;
import android.net.IpPrefix;
import android.net.LinkProperties;
import android.net.MatchAllNetworkSpecifier;
import android.net.Network;
import android.net.NetworkAgent;
import android.net.NetworkCapabilities;
import android.net.NetworkConfig;
import android.net.NetworkFactory;
import android.net.NetworkInfo;
import android.net.NetworkMisc;
import android.net.NetworkMonitorManager;
import android.net.NetworkPolicyManager;
import android.net.NetworkQuotaInfo;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.NetworkStackClient;
import android.net.NetworkState;
import android.net.NetworkUtils;
import android.net.NetworkWatchlistManager;
import android.net.PrivateDnsConfigParcel;
import android.net.ProxyInfo;
import android.net.RouteInfo;
import android.net.StringNetworkSpecifier;
import android.net.UidRange;
import android.net.Uri;
import android.net.metrics.IpConnectivityLog;
import android.net.metrics.NetworkEvent;
import android.net.netlink.InetDiagMessage;
import android.net.shared.NetworkMonitorUtils;
import android.net.shared.PrivateDnsConfig;
import android.net.util.MultinetworkPolicyTracker;
import android.net.util.NetdService;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.security.KeyStore;
import android.server.am.SplitScreenReporter;
import android.system.OsConstants;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnInfo;
import com.android.internal.net.VpnProfile;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.MessageUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.WakeupMessage;
import com.android.internal.util.XmlUtils;
import com.android.server.ConnectivityService;
import com.android.server.accounts.AccountManagerServiceInjector;
import com.android.server.am.ActivityManagerServiceInjector;
import com.android.server.am.BatteryStatsService;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.connectivity.AutodestructReference;
import com.android.server.connectivity.CaptivePortalInjector;
import com.android.server.connectivity.DataConnectionStats;
import com.android.server.connectivity.DnsManager;
import com.android.server.connectivity.IpConnectivityMetrics;
import com.android.server.connectivity.KeepaliveTracker;
import com.android.server.connectivity.LingerMonitor;
import com.android.server.connectivity.MockableSystemProperties;
import com.android.server.connectivity.MultipathPolicyTracker;
import com.android.server.connectivity.NetworkAgentInfo;
import com.android.server.connectivity.NetworkDiagnostics;
import com.android.server.connectivity.NetworkNotificationManager;
import com.android.server.connectivity.PermissionMonitor;
import com.android.server.connectivity.ProxyTracker;
import com.android.server.connectivity.Tethering;
import com.android.server.connectivity.Vpn;
import com.android.server.connectivity.tethering.TetheringDependencies;
import com.android.server.net.BaseNetdEventCallback;
import com.android.server.net.BaseNetworkObserver;
import com.android.server.net.LockdownVpnTracker;
import com.android.server.net.NetworkPolicyManagerInternal;
import com.android.server.pm.DumpState;
import com.android.server.policy.PhoneWindowManager;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.utils.PriorityDump;
import com.google.android.collect.Lists;
import com.miui.enterprise.RestrictionsHelper;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ConnectivityService extends IConnectivityManager.Stub implements PendingIntent.OnFinished {
    private static final String ATTR_MCC = "mcc";
    private static final String ATTR_MNC = "mnc";
    private static final boolean DBG = true;
    private static final boolean DDBG = Log.isLoggable(TAG, 3);
    private static final String DEFAULT_CAPTIVE_PORTAL_HTTP_URL = "http://connectivitycheck.gstatic.com/generate_204";
    private static final int DEFAULT_LINGER_DELAY_MS = 30000;
    @VisibleForTesting
    protected static final String DEFAULT_TCP_BUFFER_SIZES = "4096,87380,110208,4096,16384,110208";
    private static final String DEFAULT_TCP_RWND_KEY = "net.tcp.default_init_rwnd";
    private static final String DIAG_ARG = "--diag";
    private static final int EVENT_APPLY_GLOBAL_HTTP_PROXY = 9;
    private static final int EVENT_CLEAR_NET_TRANSITION_WAKELOCK = 8;
    private static final int EVENT_CONFIGURE_ALWAYS_ON_NETWORKS = 30;
    private static final int EVENT_DATA_SAVER_CHANGED = 40;
    private static final int EVENT_EXPIRE_NET_TRANSITION_WAKELOCK = 24;
    private static final int EVENT_IFACE_CONFIG_LOST = 128;
    public static final int EVENT_NETWORK_TESTED = 41;
    public static final int EVENT_PRIVATE_DNS_CONFIG_RESOLVED = 42;
    private static final int EVENT_PRIVATE_DNS_SETTINGS_CHANGED = 37;
    private static final int EVENT_PRIVATE_DNS_VALIDATION_UPDATE = 38;
    private static final int EVENT_PROMPT_UNVALIDATED = 29;
    public static final int EVENT_PROVISIONING_NOTIFICATION = 43;
    private static final int EVENT_PROXY_HAS_CHANGED = 16;
    private static final int EVENT_REGISTER_NETWORK_AGENT = 18;
    private static final int EVENT_REGISTER_NETWORK_FACTORY = 17;
    private static final int EVENT_REGISTER_NETWORK_LISTENER = 21;
    private static final int EVENT_REGISTER_NETWORK_LISTENER_WITH_INTENT = 31;
    private static final int EVENT_REGISTER_NETWORK_REQUEST = 19;
    private static final int EVENT_REGISTER_NETWORK_REQUEST_WITH_INTENT = 26;
    private static final int EVENT_RELEASE_NETWORK_REQUEST = 22;
    private static final int EVENT_RELEASE_NETWORK_REQUEST_WITH_INTENT = 27;
    private static final int EVENT_REVALIDATE_NETWORK = 36;
    private static final int EVENT_SET_ACCEPT_PARTIAL_CONNECTIVITY = 45;
    private static final int EVENT_SET_ACCEPT_UNVALIDATED = 28;
    private static final int EVENT_SET_AVOID_UNVALIDATED = 35;
    private static final int EVENT_SYSTEM_READY = 25;
    private static final int EVENT_TIMEOUT_NETWORK_REQUEST = 20;
    public static final int EVENT_TIMEOUT_NOTIFICATION = 44;
    private static final int EVENT_UID_RULES_CHANGED = 39;
    private static final int EVENT_UNREGISTER_NETWORK_FACTORY = 23;
    private static final int EVENT_UPDATE_ACTIVE_DATA_SUBID = 161;
    private static final int EVENT_UPDATE_TCP_BUFFER_FOR_5G = 160;
    private static final String LINGER_DELAY_PROPERTY = "persist.netmon.linger";
    private static final boolean LOGD_BLOCKED_NETWORKINFO = true;
    private static final int MAX_NETWORK_INFO_LOGS = 40;
    private static final int MAX_NETWORK_REQUESTS_PER_UID = 100;
    private static final int MAX_NETWORK_REQUEST_LOGS = 20;
    private static final int MAX_NET_ID = 64511;
    private static final int MAX_WAKELOCK_LOGS = 20;
    private static final int MIN_NET_ID = 100;
    private static final String NETWORK_ARG = "networks";
    private static final String NETWORK_RESTORE_DELAY_PROP_NAME = "android.telephony.apn-restore";
    private static final int PROMPT_UNVALIDATED_DELAY_MS = 8000;
    public static final int PROVISIONING_NOTIFICATION_HIDE = 0;
    public static final int PROVISIONING_NOTIFICATION_SHOW = 1;
    private static final String PROVISIONING_URL_PATH = "/data/misc/radio/provisioning_urls.xml";
    private static final String REQUEST_ARG = "requests";
    private static final int RESTORE_DEFAULT_NETWORK_DELAY = 60000;
    public static final String SHORT_ARG = "--short";
    /* access modifiers changed from: private */
    public static final String TAG = ConnectivityService.class.getSimpleName();
    private static final String TAG_PROVISIONING_URL = "provisioningUrl";
    private static final String TAG_PROVISIONING_URLS = "provisioningUrls";
    private static final String TETHERING_ARG = "tethering";
    private static final int TIMEOUT_NOTIFICATION_DELAY_MS = 20000;
    /* access modifiers changed from: private */
    public static final boolean VDBG = Log.isLoggable(TAG, 2);
    private static final SparseArray<String> sMagicDecoderRing = MessageUtils.findMessageNames(new Class[]{AsyncChannel.class, ConnectivityService.class, NetworkAgent.class, NetworkAgentInfo.class});
    @GuardedBy({"mBandwidthRequests"})
    private final SparseArray<Integer> mBandwidthRequests;
    @GuardedBy({"mBlockedAppUids"})
    private final HashSet<Integer> mBlockedAppUids;
    /* access modifiers changed from: private */
    public final Context mContext;
    private String mCurrentTcpBufferSizes;
    private INetworkManagementEventObserver mDataActivityObserver;
    private int mDefaultInetConditionPublished;
    private final NetworkRequest mDefaultMobileDataRequest;
    /* access modifiers changed from: private */
    public final NetworkRequest mDefaultRequest;
    private final NetworkRequest mDefaultWifiRequest;
    private final DnsManager mDnsManager;
    @VisibleForTesting
    protected IDnsResolver mDnsResolver;
    /* access modifiers changed from: private */
    public final InternalHandler mHandler;
    @VisibleForTesting
    protected final HandlerThread mHandlerThread;
    private Intent mInitialBroadcast;
    private BroadcastReceiver mIntentReceiver;
    /* access modifiers changed from: private */
    public KeepaliveTracker mKeepaliveTracker;
    private KeyStore mKeyStore;
    private long mLastWakeLockAcquireTimestamp;
    private final LegacyTypeTracker mLegacyTypeTracker;
    @VisibleForTesting
    protected int mLingerDelayMs;
    private LingerMonitor mLingerMonitor;
    @GuardedBy({"mVpns"})
    private boolean mLockdownEnabled;
    @GuardedBy({"mVpns"})
    private LockdownVpnTracker mLockdownTracker;
    private long mMaxWakelockDurationMs;
    private final IpConnectivityLog mMetricsLog;
    @VisibleForTesting
    final MultinetworkPolicyTracker mMultinetworkPolicyTracker;
    @VisibleForTesting
    final MultipathPolicyTracker mMultipathPolicyTracker;
    private INetworkManagementService mNMS;
    private NetworkConfig[] mNetConfigs;
    @GuardedBy({"mNetworkForNetId"})
    private final SparseBooleanArray mNetIdInUse;
    private PowerManager.WakeLock mNetTransitionWakeLock;
    private int mNetTransitionWakeLockTimeout;
    @VisibleForTesting
    protected INetd mNetd;
    @VisibleForTesting
    protected final INetdEventCallback mNetdEventCallback;
    /* access modifiers changed from: private */
    public final HashMap<Messenger, NetworkAgentInfo> mNetworkAgentInfos;
    private final HashMap<Messenger, NetworkFactoryInfo> mNetworkFactoryInfos;
    @GuardedBy({"mNetworkForNetId"})
    private final SparseArray<NetworkAgentInfo> mNetworkForNetId;
    @GuardedBy({"mNetworkForRequestId"})
    private final SparseArray<NetworkAgentInfo> mNetworkForRequestId;
    private final LocalLog mNetworkInfoBlockingLogs;
    private final LocalLog mNetworkRequestInfoLogs;
    private final HashMap<NetworkRequest, NetworkRequestInfo> mNetworkRequests;
    private int mNetworksDefined;
    private int mNextNetId;
    private int mNextNetworkRequestId;
    protected int mNonDefaultSubscriptionLingerDelayMs;
    /* access modifiers changed from: private */
    public NetworkNotificationManager mNotifier;
    private final PowerManager.WakeLock mPendingIntentWakeLock;
    @VisibleForTesting
    protected final PermissionMonitor mPermissionMonitor;
    private PhoneStateListener mPhoneStateListener;
    private final INetworkPolicyListener mPolicyListener;
    private INetworkPolicyManager mPolicyManager;
    private NetworkPolicyManagerInternal mPolicyManagerInternal;
    /* access modifiers changed from: private */
    public int mPreferredSubId;
    private final PriorityDump.PriorityDumper mPriorityDumper;
    private List mProtectedNetworks;
    private final File mProvisioningUrlFile;
    @VisibleForTesting
    protected final ProxyTracker mProxyTracker;
    private final int mReleasePendingIntentDelayMs;
    private boolean mRestrictBackground;
    private final SettingsObserver mSettingsObserver;
    private INetworkStatsService mStatsService;
    private SubscriptionManager mSubscriptionManager;
    private MockableSystemProperties mSystemProperties;
    private boolean mSystemReady;
    @GuardedBy({"mTNSLock"})
    private TestNetworkService mTNS;
    private final Object mTNSLock;
    private TelephonyManager mTelephonyManager;
    /* access modifiers changed from: private */
    public Tethering mTethering;
    private int mTotalWakelockAcquisitions;
    private long mTotalWakelockDurationMs;
    private int mTotalWakelockReleases;
    /* access modifiers changed from: private */
    public final NetworkStateTrackerHandler mTrackerHandler;
    private SparseIntArray mUidRules;
    /* access modifiers changed from: private */
    @GuardedBy({"mUidToNetworkRequestCount"})
    public final SparseIntArray mUidToNetworkRequestCount;
    private UserManager mUserManager;
    private BroadcastReceiver mUserPresentReceiver;
    @GuardedBy({"mVpns"})
    @VisibleForTesting
    protected final SparseArray<Vpn> mVpns;
    private final LocalLog mWakelockLogs;

    private enum ReapUnvalidatedNetworks {
        REAP,
        DONT_REAP
    }

    private enum UnneededFor {
        LINGER,
        TEARDOWN
    }

    /* access modifiers changed from: private */
    public static String eventName(int what) {
        return sMagicDecoderRing.get(what, Integer.toString(what));
    }

    private static IDnsResolver getDnsResolver() {
        return IDnsResolver.Stub.asInterface(ServiceManager.getService("dnsresolver"));
    }

    @VisibleForTesting
    static class LegacyTypeTracker {
        private static final boolean DBG = true;
        private static final boolean VDBG = false;
        private final ConnectivityService mService;
        private final ArrayList<NetworkAgentInfo>[] mTypeLists = new ArrayList[19];

        LegacyTypeTracker(ConnectivityService service) {
            this.mService = service;
        }

        public void addSupportedType(int type) {
            ArrayList<NetworkAgentInfo>[] arrayListArr = this.mTypeLists;
            if (arrayListArr[type] == null) {
                arrayListArr[type] = new ArrayList<>();
                return;
            }
            throw new IllegalStateException("legacy list for type " + type + "already initialized");
        }

        public boolean isTypeSupported(int type) {
            return ConnectivityManager.isNetworkTypeValid(type) && this.mTypeLists[type] != null;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
            return null;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.android.server.connectivity.NetworkAgentInfo getNetworkForType(int r4) {
            /*
                r3 = this;
                java.util.ArrayList<com.android.server.connectivity.NetworkAgentInfo>[] r0 = r3.mTypeLists
                monitor-enter(r0)
                boolean r1 = r3.isTypeSupported(r4)     // Catch:{ all -> 0x0023 }
                if (r1 == 0) goto L_0x0020
                java.util.ArrayList<com.android.server.connectivity.NetworkAgentInfo>[] r1 = r3.mTypeLists     // Catch:{ all -> 0x0023 }
                r1 = r1[r4]     // Catch:{ all -> 0x0023 }
                boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0023 }
                if (r1 != 0) goto L_0x0020
                java.util.ArrayList<com.android.server.connectivity.NetworkAgentInfo>[] r1 = r3.mTypeLists     // Catch:{ all -> 0x0023 }
                r1 = r1[r4]     // Catch:{ all -> 0x0023 }
                r2 = 0
                java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0023 }
                com.android.server.connectivity.NetworkAgentInfo r1 = (com.android.server.connectivity.NetworkAgentInfo) r1     // Catch:{ all -> 0x0023 }
                monitor-exit(r0)     // Catch:{ all -> 0x0023 }
                return r1
            L_0x0020:
                monitor-exit(r0)     // Catch:{ all -> 0x0023 }
                r0 = 0
                return r0
            L_0x0023:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0023 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.LegacyTypeTracker.getNetworkForType(int):com.android.server.connectivity.NetworkAgentInfo");
        }

        private void maybeLogBroadcast(NetworkAgentInfo nai, NetworkInfo.DetailedState state, int type, boolean isDefaultNetwork) {
            ConnectivityService.log("Sending " + state + " broadcast for type " + type + " " + nai.name() + " isDefaultNetwork=" + isDefaultNetwork);
        }

        public void add(int type, NetworkAgentInfo nai) {
            if (isTypeSupported(type)) {
                ArrayList<NetworkAgentInfo> list = this.mTypeLists[type];
                if (!list.contains(nai)) {
                    synchronized (this.mTypeLists) {
                        list.add(nai);
                    }
                    boolean isDefaultNetwork = this.mService.isDefaultNetwork(nai);
                    if (list.size() == 1 || isDefaultNetwork) {
                        maybeLogBroadcast(nai, NetworkInfo.DetailedState.CONNECTED, type, isDefaultNetwork);
                        this.mService.sendLegacyNetworkBroadcast(nai, NetworkInfo.DetailedState.CONNECTED, type);
                    }
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0024, code lost:
            if (r2 != false) goto L_0x0028;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
            if (r8 == false) goto L_0x0034;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
            maybeLogBroadcast(r7, android.net.NetworkInfo.DetailedState.DISCONNECTED, r6, r8);
            r5.mService.sendLegacyNetworkBroadcast(r7, android.net.NetworkInfo.DetailedState.DISCONNECTED, r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0038, code lost:
            if (r0.isEmpty() != false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x003a, code lost:
            if (r2 == false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003c, code lost:
            com.android.server.ConnectivityService.access$200("Other network available for type " + r6 + ", sending connected broadcast");
            r1 = r0.get(0);
            maybeLogBroadcast(r1, android.net.NetworkInfo.DetailedState.CONNECTED, r6, r5.mService.isDefaultNetwork(r1));
            r5.mService.sendLegacyNetworkBroadcast(r1, android.net.NetworkInfo.DetailedState.CONNECTED, r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void remove(int r6, com.android.server.connectivity.NetworkAgentInfo r7, boolean r8) {
            /*
                r5 = this;
                java.util.ArrayList<com.android.server.connectivity.NetworkAgentInfo>[] r0 = r5.mTypeLists
                r0 = r0[r6]
                if (r0 == 0) goto L_0x0071
                boolean r1 = r0.isEmpty()
                if (r1 == 0) goto L_0x000d
                goto L_0x0071
            L_0x000d:
                r1 = 0
                java.lang.Object r2 = r0.get(r1)
                com.android.server.connectivity.NetworkAgentInfo r2 = (com.android.server.connectivity.NetworkAgentInfo) r2
                boolean r2 = r2.equals(r7)
                java.util.ArrayList<com.android.server.connectivity.NetworkAgentInfo>[] r3 = r5.mTypeLists
                monitor-enter(r3)
                boolean r4 = r0.remove(r7)     // Catch:{ all -> 0x006e }
                if (r4 != 0) goto L_0x0023
                monitor-exit(r3)     // Catch:{ all -> 0x006e }
                return
            L_0x0023:
                monitor-exit(r3)     // Catch:{ all -> 0x006e }
                if (r2 != 0) goto L_0x0028
                if (r8 == 0) goto L_0x0034
            L_0x0028:
                android.net.NetworkInfo$DetailedState r3 = android.net.NetworkInfo.DetailedState.DISCONNECTED
                r5.maybeLogBroadcast(r7, r3, r6, r8)
                com.android.server.ConnectivityService r3 = r5.mService
                android.net.NetworkInfo$DetailedState r4 = android.net.NetworkInfo.DetailedState.DISCONNECTED
                r3.sendLegacyNetworkBroadcast(r7, r4, r6)
            L_0x0034:
                boolean r3 = r0.isEmpty()
                if (r3 != 0) goto L_0x006d
                if (r2 == 0) goto L_0x006d
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Other network available for type "
                r3.append(r4)
                r3.append(r6)
                java.lang.String r4 = ", sending connected broadcast"
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                com.android.server.ConnectivityService.log(r3)
                java.lang.Object r1 = r0.get(r1)
                com.android.server.connectivity.NetworkAgentInfo r1 = (com.android.server.connectivity.NetworkAgentInfo) r1
                android.net.NetworkInfo$DetailedState r3 = android.net.NetworkInfo.DetailedState.CONNECTED
                com.android.server.ConnectivityService r4 = r5.mService
                boolean r4 = r4.isDefaultNetwork(r1)
                r5.maybeLogBroadcast(r1, r3, r6, r4)
                com.android.server.ConnectivityService r3 = r5.mService
                android.net.NetworkInfo$DetailedState r4 = android.net.NetworkInfo.DetailedState.CONNECTED
                r3.sendLegacyNetworkBroadcast(r1, r4, r6)
            L_0x006d:
                return
            L_0x006e:
                r1 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x006e }
                throw r1
            L_0x0071:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.LegacyTypeTracker.remove(int, com.android.server.connectivity.NetworkAgentInfo, boolean):void");
        }

        public void remove(NetworkAgentInfo nai, boolean wasDefault) {
            for (int type = 0; type < this.mTypeLists.length; type++) {
                remove(type, nai, wasDefault);
            }
        }

        public void update(NetworkAgentInfo nai) {
            boolean isDefault = this.mService.isDefaultNetwork(nai);
            NetworkInfo.DetailedState state = nai.networkInfo.getDetailedState();
            int type = 0;
            while (true) {
                ArrayList<NetworkAgentInfo>[] arrayListArr = this.mTypeLists;
                if (type < arrayListArr.length) {
                    ArrayList<NetworkAgentInfo> list = arrayListArr[type];
                    boolean isFirst = true;
                    boolean contains = list != null && list.contains(nai);
                    if (!contains || nai != list.get(0)) {
                        isFirst = false;
                    }
                    if (isFirst || (contains && isDefault)) {
                        maybeLogBroadcast(nai, state, type, isDefault);
                        this.mService.sendLegacyNetworkBroadcast(nai, state, type);
                    }
                    type++;
                } else {
                    return;
                }
            }
        }

        private String naiToString(NetworkAgentInfo nai) {
            String state;
            String name = nai.name();
            if (nai.networkInfo != null) {
                state = nai.networkInfo.getState() + SliceClientPermissions.SliceAuthority.DELIMITER + nai.networkInfo.getDetailedState();
            } else {
                state = "???/???";
            }
            return name + " " + state;
        }

        public void dump(IndentingPrintWriter pw) {
            pw.println("mLegacyTypeTracker:");
            pw.increaseIndent();
            pw.print("Supported types:");
            int type = 0;
            while (true) {
                ArrayList<NetworkAgentInfo>[] arrayListArr = this.mTypeLists;
                if (type >= arrayListArr.length) {
                    break;
                }
                if (arrayListArr[type] != null) {
                    pw.print(" " + type);
                }
                type++;
            }
            pw.println();
            pw.println("Current state:");
            pw.increaseIndent();
            synchronized (this.mTypeLists) {
                for (int type2 = 0; type2 < this.mTypeLists.length; type2++) {
                    if (this.mTypeLists[type2] != null) {
                        if (!this.mTypeLists[type2].isEmpty()) {
                            Iterator<NetworkAgentInfo> it = this.mTypeLists[type2].iterator();
                            while (it.hasNext()) {
                                pw.println(type2 + " " + naiToString(it.next()));
                            }
                        }
                    }
                }
            }
            pw.decreaseIndent();
            pw.decreaseIndent();
            pw.println();
        }
    }

    public ConnectivityService(Context context, INetworkManagementService netManager, INetworkStatsService statsService, INetworkPolicyManager policyManager) {
        this(context, netManager, statsService, policyManager, getDnsResolver(), new IpConnectivityLog(), NetdService.getInstance());
    }

    @VisibleForTesting
    protected ConnectivityService(Context context, INetworkManagementService netManager, INetworkStatsService statsService, INetworkPolicyManager policyManager, IDnsResolver dnsresolver, IpConnectivityLog logger, INetd netd) {
        NetworkRequestInfo defaultNRI;
        Context context2 = context;
        this.mVpns = new SparseArray<>();
        this.mUidRules = new SparseIntArray();
        int i = 0;
        this.mDefaultInetConditionPublished = 0;
        this.mTNSLock = new Object();
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onActiveDataSubscriptionIdChanged(int subId) {
                if (subId != ConnectivityService.this.mPreferredSubId) {
                    ConnectivityService.this.mHandler.sendMessage(ConnectivityService.this.mHandler.obtainMessage(ConnectivityService.EVENT_UPDATE_ACTIVE_DATA_SUBID, subId, 0));
                }
            }
        };
        this.mPreferredSubId = -1;
        this.mNextNetId = 100;
        this.mNextNetworkRequestId = 1;
        this.mNetworkRequestInfoLogs = new LocalLog(20);
        this.mNetworkInfoBlockingLogs = new LocalLog(40);
        this.mWakelockLogs = new LocalLog(20);
        this.mTotalWakelockAcquisitions = 0;
        this.mTotalWakelockReleases = 0;
        this.mTotalWakelockDurationMs = 0;
        this.mMaxWakelockDurationMs = 0;
        this.mLastWakeLockAcquireTimestamp = 0;
        this.mBandwidthRequests = new SparseArray<>(10);
        this.mLegacyTypeTracker = new LegacyTypeTracker(this);
        this.mPriorityDumper = new PriorityDump.PriorityDumper() {
            public void dumpHigh(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
                ConnectivityService.this.doDump(fd, pw, new String[]{ConnectivityService.DIAG_ARG}, asProto);
                ConnectivityService.this.doDump(fd, pw, new String[]{ConnectivityService.SHORT_ARG}, asProto);
            }

            public void dumpNormal(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
                ConnectivityService.this.doDump(fd, pw, args, asProto);
            }

            public void dump(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
                ConnectivityService.this.doDump(fd, pw, args, asProto);
            }
        };
        this.mDataActivityObserver = new BaseNetworkObserver() {
            public void interfaceClassDataActivityChanged(String label, boolean active, long tsNanos) {
                ConnectivityService.this.sendDataActivityBroadcast(Integer.parseInt(label), active, tsNanos);
            }

            public void interfaceConfigurationLost() {
                ConnectivityService.loge("Interface configuration was lost, reset connected network!");
                ConnectivityService.this.mHandler.sendMessage(ConnectivityService.this.mHandler.obtainMessage(128));
            }
        };
        this.mNetdEventCallback = new BaseNetdEventCallback() {
            public void onPrivateDnsValidationEvent(int netId, String ipAddress, String hostname, boolean validated) {
                try {
                    ConnectivityService.this.mHandler.sendMessage(ConnectivityService.this.mHandler.obtainMessage(38, new DnsManager.PrivateDnsValidationUpdate(netId, InetAddress.parseNumericAddress(ipAddress), hostname, validated)));
                } catch (IllegalArgumentException e) {
                    ConnectivityService.loge("Error parsing ip address in validation event");
                }
            }

            public void onDnsEvent(int netId, int eventType, int returnCode, String hostname, String[] ipAddresses, int ipAddressesCount, long timestamp, int uid) {
                NetworkAgentInfo nai = ConnectivityService.this.getNetworkAgentInfoForNetId(netId);
                if (nai != null && nai.satisfies(ConnectivityService.this.mDefaultRequest)) {
                    nai.networkMonitor().notifyDnsResponse(returnCode);
                }
            }

            public /* synthetic */ void lambda$onNat64PrefixEvent$0$ConnectivityService$5(int netId, boolean added, String prefixString, int prefixLength) {
                ConnectivityService.this.handleNat64PrefixEvent(netId, added, prefixString, prefixLength);
            }

            public void onNat64PrefixEvent(int netId, boolean added, String prefixString, int prefixLength) {
                ConnectivityService.this.mHandler.post(new Runnable(netId, added, prefixString, prefixLength) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ String f$3;
                    private final /* synthetic */ int f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        ConnectivityService.AnonymousClass5.this.lambda$onNat64PrefixEvent$0$ConnectivityService$5(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }
        };
        this.mPolicyListener = new NetworkPolicyManager.Listener() {
            public void onUidRulesChanged(int uid, int uidRules) {
                ConnectivityService.this.mHandler.sendMessage(ConnectivityService.this.mHandler.obtainMessage(39, uid, uidRules));
            }

            public void onRestrictBackgroundChanged(boolean restrictBackground) {
                ConnectivityService.log("onRestrictBackgroundChanged(restrictBackground=" + restrictBackground + ")");
                ConnectivityService.this.mHandler.sendMessage(ConnectivityService.this.mHandler.obtainMessage(40, restrictBackground, 0));
                if (restrictBackground) {
                    ConnectivityService.log("onRestrictBackgroundChanged(true): disabling tethering");
                    ConnectivityService.this.mTethering.untetherAll();
                }
            }
        };
        this.mProvisioningUrlFile = new File(PROVISIONING_URL_PATH);
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ConnectivityService.this.ensureRunningOnConnectivityServiceThread();
                String action = intent.getAction();
                int userId = intent.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
                int uid = intent.getIntExtra("android.intent.extra.UID", -1);
                Uri packageData = intent.getData();
                String packageName = packageData != null ? packageData.getSchemeSpecificPart() : null;
                if (userId != -10000) {
                    if ("android.intent.action.USER_STARTED".equals(action)) {
                        ConnectivityService.this.onUserStart(userId);
                    } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                        ConnectivityService.this.onUserStop(userId);
                    } else if ("android.intent.action.USER_ADDED".equals(action)) {
                        ConnectivityService.this.onUserAdded(userId);
                    } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                        ConnectivityService.this.onUserRemoved(userId);
                    } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                        ConnectivityService.this.onUserUnlocked(userId);
                    } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                        ConnectivityService.this.onPackageAdded(packageName, uid);
                    } else if ("android.intent.action.PACKAGE_REPLACED".equals(action)) {
                        ConnectivityService.this.onPackageReplaced(packageName, uid);
                    } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                        ConnectivityService.this.onPackageRemoved(packageName, uid, intent.getBooleanExtra("android.intent.extra.REPLACING", false));
                    }
                }
            }
        };
        this.mUserPresentReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ConnectivityService.this.updateLockdownVpn();
                ConnectivityService.this.mContext.unregisterReceiver(this);
            }
        };
        this.mNetworkFactoryInfos = new HashMap<>();
        this.mNetworkRequests = new HashMap<>();
        this.mUidToNetworkRequestCount = new SparseIntArray();
        this.mNetworkForRequestId = new SparseArray<>();
        this.mNetworkForNetId = new SparseArray<>();
        this.mNetIdInUse = new SparseBooleanArray();
        this.mNetworkAgentInfos = new HashMap<>();
        this.mBlockedAppUids = new HashSet<>();
        log("ConnectivityService starting up");
        this.mSystemProperties = getSystemProperties();
        this.mMetricsLog = logger;
        this.mDefaultRequest = createDefaultInternetRequestForTransport(-1, NetworkRequest.Type.REQUEST);
        NetworkRequestInfo defaultNRI2 = new NetworkRequestInfo((Messenger) null, this.mDefaultRequest, new Binder());
        this.mNetworkRequests.put(this.mDefaultRequest, defaultNRI2);
        this.mNetworkRequestInfoLogs.log("REGISTER " + defaultNRI2);
        this.mDefaultMobileDataRequest = createDefaultInternetRequestForTransport(0, NetworkRequest.Type.BACKGROUND_REQUEST);
        this.mDefaultWifiRequest = createDefaultInternetRequestForTransport(1, NetworkRequest.Type.BACKGROUND_REQUEST);
        this.mHandlerThread = new HandlerThread("ConnectivityServiceThread");
        this.mHandlerThread.start();
        this.mHandler = new InternalHandler(this.mHandlerThread.getLooper());
        this.mTrackerHandler = new NetworkStateTrackerHandler(this.mHandlerThread.getLooper());
        MiuiSettings.System.setNetHostName(context);
        this.mReleasePendingIntentDelayMs = Settings.Secure.getInt(context.getContentResolver(), "connectivity_release_pending_intent_delay_ms", 5000);
        this.mLingerDelayMs = this.mSystemProperties.getInt(LINGER_DELAY_PROPERTY, DEFAULT_LINGER_DELAY_MS);
        this.mNonDefaultSubscriptionLingerDelayMs = 5000;
        this.mContext = (Context) Preconditions.checkNotNull(context2, "missing Context");
        this.mNMS = (INetworkManagementService) Preconditions.checkNotNull(netManager, "missing INetworkManagementService");
        this.mStatsService = (INetworkStatsService) Preconditions.checkNotNull(statsService, "missing INetworkStatsService");
        this.mPolicyManager = (INetworkPolicyManager) Preconditions.checkNotNull(policyManager, "missing INetworkPolicyManager");
        this.mPolicyManagerInternal = (NetworkPolicyManagerInternal) Preconditions.checkNotNull((NetworkPolicyManagerInternal) LocalServices.getService(NetworkPolicyManagerInternal.class), "missing NetworkPolicyManagerInternal");
        this.mDnsResolver = (IDnsResolver) Preconditions.checkNotNull(dnsresolver, "missing IDnsResolver");
        this.mProxyTracker = makeProxyTracker();
        this.mNetd = netd;
        this.mKeyStore = KeyStore.getInstance();
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
        this.mTelephonyManager.listen(this.mPhoneStateListener, DumpState.DUMP_CHANGES);
        try {
            this.mPolicyManager.registerListener(this.mPolicyListener);
        } catch (RemoteException e) {
            loge("unable to register INetworkPolicyListener" + e);
        }
        PowerManager powerManager = (PowerManager) context2.getSystemService("power");
        this.mNetTransitionWakeLock = powerManager.newWakeLock(1, TAG);
        this.mNetTransitionWakeLockTimeout = this.mContext.getResources().getInteger(17694857);
        this.mPendingIntentWakeLock = powerManager.newWakeLock(1, TAG);
        this.mNetConfigs = new NetworkConfig[19];
        boolean wifiOnly = this.mSystemProperties.getBoolean("ro.radio.noril", false);
        log("wifiOnly=" + wifiOnly);
        String[] naStrings = context.getResources().getStringArray(17236103);
        int length = naStrings.length;
        while (i < length) {
            String naString = naStrings[i];
            try {
                NetworkConfig n = new NetworkConfig(naString);
                if (VDBG) {
                    StringBuilder sb = new StringBuilder();
                    defaultNRI = defaultNRI2;
                    try {
                        sb.append("naString=");
                        sb.append(naString);
                        sb.append(" config=");
                        sb.append(n);
                        log(sb.toString());
                    } catch (Exception e2) {
                    }
                } else {
                    defaultNRI = defaultNRI2;
                }
                if (n.type > 18) {
                    loge("Error in networkAttributes - ignoring attempt to define type " + n.type);
                } else if (wifiOnly && ConnectivityManager.isNetworkTypeMobile(n.type)) {
                    log("networkAttributes - ignoring mobile as this dev is wifiOnly " + n.type);
                } else if (this.mNetConfigs[n.type] != null) {
                    loge("Error in networkAttributes - ignoring attempt to redefine type " + n.type);
                } else {
                    this.mLegacyTypeTracker.addSupportedType(n.type);
                    this.mNetConfigs[n.type] = n;
                    this.mNetworksDefined++;
                }
            } catch (Exception e3) {
                defaultNRI = defaultNRI2;
            }
            i++;
            IpConnectivityLog ipConnectivityLog = logger;
            defaultNRI2 = defaultNRI;
        }
        if (this.mNetConfigs[17] == null) {
            this.mLegacyTypeTracker.addSupportedType(17);
            this.mNetworksDefined++;
        }
        if (this.mNetConfigs[9] == null && hasService("ethernet")) {
            this.mLegacyTypeTracker.addSupportedType(9);
            this.mNetworksDefined++;
        }
        if (VDBG) {
            log("mNetworksDefined=" + this.mNetworksDefined);
        }
        this.mProtectedNetworks = new ArrayList();
        int[] protectedNetworks = context.getResources().getIntArray(17236054);
        for (int p : protectedNetworks) {
            if (this.mNetConfigs[p] == null || this.mProtectedNetworks.contains(Integer.valueOf(p))) {
                loge("Ignoring protectedNetwork " + p);
            } else {
                this.mProtectedNetworks.add(Integer.valueOf(p));
            }
        }
        this.mTethering = makeTethering();
        this.mPermissionMonitor = new PermissionMonitor(this.mContext, this.mNetd);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_STARTED");
        intentFilter.addAction("android.intent.action.USER_STOPPED");
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        this.mContext.registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, intentFilter, (String) null, this.mHandler);
        this.mContext.registerReceiverAsUser(this.mUserPresentReceiver, UserHandle.SYSTEM, new IntentFilter("android.intent.action.USER_PRESENT"), (String) null, (Handler) null);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter2.addDataScheme(com.android.server.pm.Settings.ATTR_PACKAGE);
        this.mContext.registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, intentFilter2, (String) null, this.mHandler);
        try {
            this.mNMS.registerObserver(this.mTethering);
            this.mNMS.registerObserver(this.mDataActivityObserver);
        } catch (RemoteException e4) {
            loge("Error registering observer :" + e4);
        }
        this.mSettingsObserver = new SettingsObserver(this.mContext, this.mHandler);
        registerSettingsCallbacks();
        DataConnectionStats dataConnectionStats = new DataConnectionStats(this.mContext);
        dataConnectionStats.startMonitoring();
        this.mUserManager = (UserManager) context2.getSystemService("user");
        this.mKeepaliveTracker = new KeepaliveTracker(this.mContext, this.mHandler);
        Context context3 = this.mContext;
        DataConnectionStats dataConnectionStats2 = dataConnectionStats;
        this.mNotifier = new NetworkNotificationManager(context3, this.mTelephonyManager, (NotificationManager) context3.getSystemService(NotificationManager.class));
        int dailyLimit = Settings.Global.getInt(this.mContext.getContentResolver(), "network_switch_notification_daily_limit", 3);
        int[] iArr = protectedNetworks;
        IntentFilter intentFilter3 = intentFilter2;
        this.mLingerMonitor = new LingerMonitor(this.mContext, this.mNotifier, dailyLimit, Settings.Global.getLong(this.mContext.getContentResolver(), "network_switch_notification_rate_limit_millis", 60000));
        this.mMultinetworkPolicyTracker = createMultinetworkPolicyTracker(this.mContext, this.mHandler, new Runnable() {
            public final void run() {
                ConnectivityService.this.lambda$new$0$ConnectivityService();
            }
        });
        this.mMultinetworkPolicyTracker.start();
        this.mMultipathPolicyTracker = new MultipathPolicyTracker(this.mContext, this.mHandler);
        int i2 = dailyLimit;
        this.mDnsManager = new DnsManager(this.mContext, this.mDnsResolver, this.mSystemProperties);
        registerPrivateDnsSettingsCallbacks();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public Tethering makeTethering() {
        return new Tethering(this.mContext, this.mNMS, this.mStatsService, this.mPolicyManager, IoThread.get().getLooper(), new MockableSystemProperties(), new TetheringDependencies() {
            public boolean isTetheringSupported() {
                return ConnectivityService.this.isTetheringSupported();
            }

            public NetworkRequest getDefaultNetworkRequest() {
                return ConnectivityService.this.mDefaultRequest;
            }
        });
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public ProxyTracker makeProxyTracker() {
        return new ProxyTracker(this.mContext, this.mHandler, 16);
    }

    private static NetworkCapabilities createDefaultNetworkCapabilitiesForUid(int uid) {
        NetworkCapabilities netCap = new NetworkCapabilities();
        netCap.addCapability(12);
        netCap.addCapability(13);
        netCap.removeCapability(15);
        netCap.setSingleUid(uid);
        return netCap;
    }

    private NetworkRequest createDefaultInternetRequestForTransport(int transportType, NetworkRequest.Type type) {
        NetworkCapabilities netCap = new NetworkCapabilities();
        netCap.addCapability(12);
        netCap.addCapability(13);
        if (transportType > -1) {
            netCap.addTransportType(transportType);
        }
        return new NetworkRequest(netCap, -1, nextNetworkRequestId(), type);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateAlwaysOnNetworks() {
        this.mHandler.sendEmptyMessage(30);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updatePrivateDnsSettings() {
        this.mHandler.sendEmptyMessage(37);
    }

    private void handleAlwaysOnNetworkRequest(NetworkRequest networkRequest, String settingName, boolean defaultValue) {
        boolean enable = toBool(Settings.Global.getInt(this.mContext.getContentResolver(), settingName, encodeBool(defaultValue)));
        if (enable != (this.mNetworkRequests.get(networkRequest) != null)) {
            if (enable) {
                handleRegisterNetworkRequest(new NetworkRequestInfo((Messenger) null, networkRequest, new Binder()));
            } else {
                handleReleaseNetworkRequest(networkRequest, 1000, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleConfigureAlwaysOnNetworks() {
        handleAlwaysOnNetworkRequest(this.mDefaultMobileDataRequest, "mobile_data_always_on", false);
        handleAlwaysOnNetworkRequest(this.mDefaultWifiRequest, "wifi_always_requested", false);
    }

    private void registerSettingsCallbacks() {
        this.mSettingsObserver.observe(Settings.Global.getUriFor("http_proxy"), 9);
        this.mSettingsObserver.observe(Settings.Global.getUriFor("mobile_data_always_on"), 30);
        this.mSettingsObserver.observe(Settings.Global.getUriFor("wifi_always_requested"), 30);
    }

    private void registerPrivateDnsSettingsCallbacks() {
        for (Uri uri : DnsManager.getPrivateDnsSettingsUris()) {
            this.mSettingsObserver.observe(uri, 37);
        }
    }

    private synchronized int nextNetworkRequestId() {
        int i;
        i = this.mNextNetworkRequestId;
        this.mNextNetworkRequestId = i + 1;
        return i;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public int reserveNetId() {
        synchronized (this.mNetworkForNetId) {
            for (int i = 100; i <= MAX_NET_ID; i++) {
                int netId = this.mNextNetId;
                int i2 = this.mNextNetId + 1;
                this.mNextNetId = i2;
                if (i2 > MAX_NET_ID) {
                    this.mNextNetId = 100;
                }
                if (!this.mNetIdInUse.get(netId)) {
                    this.mNetIdInUse.put(netId, true);
                    return netId;
                }
            }
            throw new IllegalStateException("No free netIds");
        }
    }

    private NetworkState getFilteredNetworkState(int networkType, int uid) {
        NetworkState state;
        if (!this.mLegacyTypeTracker.isTypeSupported(networkType)) {
            return NetworkState.EMPTY;
        }
        NetworkAgentInfo nai = this.mLegacyTypeTracker.getNetworkForType(networkType);
        if (nai != null) {
            state = nai.getNetworkState();
            state.networkInfo.setType(networkType);
        } else {
            NetworkInfo info = new NetworkInfo(networkType, 0, ConnectivityManager.getNetworkTypeName(networkType), "");
            info.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, (String) null, (String) null);
            info.setIsAvailable(true);
            NetworkCapabilities capabilities = new NetworkCapabilities();
            capabilities.setCapability(18, true ^ info.isRoaming());
            state = new NetworkState(info, new LinkProperties(), capabilities, (Network) null, (String) null, (String) null);
        }
        filterNetworkStateForUid(state, uid, false);
        return state;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public NetworkAgentInfo getNetworkAgentInfoForNetwork(Network network) {
        if (network == null) {
            return null;
        }
        return getNetworkAgentInfoForNetId(network.netId);
    }

    /* access modifiers changed from: private */
    public NetworkAgentInfo getNetworkAgentInfoForNetId(int netId) {
        NetworkAgentInfo networkAgentInfo;
        synchronized (this.mNetworkForNetId) {
            networkAgentInfo = this.mNetworkForNetId.get(netId);
        }
        return networkAgentInfo;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0022, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.net.Network[] getVpnUnderlyingNetworks(int r5) {
        /*
            r4 = this;
            android.util.SparseArray<com.android.server.connectivity.Vpn> r0 = r4.mVpns
            monitor-enter(r0)
            boolean r1 = r4.mLockdownEnabled     // Catch:{ all -> 0x0024 }
            if (r1 != 0) goto L_0x0021
            int r1 = android.os.UserHandle.getUserId(r5)     // Catch:{ all -> 0x0024 }
            android.util.SparseArray<com.android.server.connectivity.Vpn> r2 = r4.mVpns     // Catch:{ all -> 0x0024 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ all -> 0x0024 }
            com.android.server.connectivity.Vpn r2 = (com.android.server.connectivity.Vpn) r2     // Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x0021
            boolean r3 = r2.appliesToUid(r5)     // Catch:{ all -> 0x0024 }
            if (r3 == 0) goto L_0x0021
            android.net.Network[] r3 = r2.getUnderlyingNetworks()     // Catch:{ all -> 0x0024 }
            monitor-exit(r0)     // Catch:{ all -> 0x0024 }
            return r3
        L_0x0021:
            monitor-exit(r0)     // Catch:{ all -> 0x0024 }
            r0 = 0
            return r0
        L_0x0024:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0024 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.getVpnUnderlyingNetworks(int):android.net.Network[]");
    }

    private NetworkState getUnfilteredActiveNetworkState(int uid) {
        NetworkAgentInfo nai = getDefaultNetwork();
        Network[] networks = getVpnUnderlyingNetworks(uid);
        if (networks != null) {
            if (networks.length > 0) {
                nai = getNetworkAgentInfoForNetwork(networks[0]);
            } else {
                nai = null;
            }
        }
        if (nai != null) {
            return nai.getNetworkState();
        }
        return NetworkState.EMPTY;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
        if (r4 != null) goto L_0x002a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
        r0 = "";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002a, code lost:
        r0 = r4.getInterfaceName();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0034, code lost:
        return r3.mPolicyManagerInternal.isUidNetworkingBlocked(r5, r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isNetworkWithLinkPropertiesBlocked(android.net.LinkProperties r4, int r5, boolean r6) {
        /*
            r3 = this;
            if (r6 == 0) goto L_0x0004
            r0 = 0
            return r0
        L_0x0004:
            android.util.SparseArray<com.android.server.connectivity.Vpn> r0 = r3.mVpns
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.connectivity.Vpn> r1 = r3.mVpns     // Catch:{ all -> 0x0035 }
            int r2 = android.os.UserHandle.getUserId(r5)     // Catch:{ all -> 0x0035 }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0035 }
            com.android.server.connectivity.Vpn r1 = (com.android.server.connectivity.Vpn) r1     // Catch:{ all -> 0x0035 }
            if (r1 == 0) goto L_0x0024
            boolean r2 = r1.getLockdown()     // Catch:{ all -> 0x0035 }
            if (r2 == 0) goto L_0x0024
            boolean r2 = r1.isBlockingUid(r5)     // Catch:{ all -> 0x0035 }
            if (r2 == 0) goto L_0x0024
            r2 = 1
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            return r2
        L_0x0024:
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            if (r4 != 0) goto L_0x002a
            java.lang.String r0 = ""
            goto L_0x002e
        L_0x002a:
            java.lang.String r0 = r4.getInterfaceName()
        L_0x002e:
            com.android.server.net.NetworkPolicyManagerInternal r1 = r3.mPolicyManagerInternal
            boolean r1 = r1.isUidNetworkingBlocked(r5, r0)
            return r1
        L_0x0035:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.isNetworkWithLinkPropertiesBlocked(android.net.LinkProperties, int, boolean):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002f, code lost:
        if (r1 == false) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0031, code lost:
        r0 = "BLOCKED";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
        r0 = "UNBLOCKED";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0036, code lost:
        log(java.lang.String.format("Returning %s NetworkInfo to uid=%d", new java.lang.Object[]{r0, java.lang.Integer.valueOf(r7)}));
        r2 = r5.mNetworkInfoBlockingLogs;
        r2.log(r0 + " " + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0065, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void maybeLogBlockedNetworkInfo(android.net.NetworkInfo r6, int r7) {
        /*
            r5 = this;
            if (r6 == 0) goto L_0x006b
            java.util.HashSet<java.lang.Integer> r0 = r5.mBlockedAppUids
            monitor-enter(r0)
            android.net.NetworkInfo$DetailedState r1 = r6.getDetailedState()     // Catch:{ all -> 0x0068 }
            android.net.NetworkInfo$DetailedState r2 = android.net.NetworkInfo.DetailedState.BLOCKED     // Catch:{ all -> 0x0068 }
            if (r1 != r2) goto L_0x001b
            java.util.HashSet<java.lang.Integer> r1 = r5.mBlockedAppUids     // Catch:{ all -> 0x0068 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x0068 }
            boolean r1 = r1.add(r2)     // Catch:{ all -> 0x0068 }
            if (r1 == 0) goto L_0x001b
            r1 = 1
            goto L_0x002e
        L_0x001b:
            boolean r1 = r6.isConnected()     // Catch:{ all -> 0x0068 }
            if (r1 == 0) goto L_0x0066
            java.util.HashSet<java.lang.Integer> r1 = r5.mBlockedAppUids     // Catch:{ all -> 0x0068 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x0068 }
            boolean r1 = r1.remove(r2)     // Catch:{ all -> 0x0068 }
            if (r1 == 0) goto L_0x0066
            r1 = 0
        L_0x002e:
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            if (r1 == 0) goto L_0x0034
            java.lang.String r0 = "BLOCKED"
            goto L_0x0036
        L_0x0034:
            java.lang.String r0 = "UNBLOCKED"
        L_0x0036:
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r0
            r3 = 1
            java.lang.Integer r4 = java.lang.Integer.valueOf(r7)
            r2[r3] = r4
            java.lang.String r3 = "Returning %s NetworkInfo to uid=%d"
            java.lang.String r2 = java.lang.String.format(r3, r2)
            log(r2)
            android.util.LocalLog r2 = r5.mNetworkInfoBlockingLogs
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            java.lang.String r4 = " "
            r3.append(r4)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.log(r3)
            return
        L_0x0066:
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            return
        L_0x0068:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            throw r1
        L_0x006b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.maybeLogBlockedNetworkInfo(android.net.NetworkInfo, int):void");
    }

    private void maybeLogBlockedStatusChanged(NetworkRequestInfo nri, Network net, boolean blocked) {
        if (nri != null && net != null) {
            String action = blocked ? "BLOCKED" : "UNBLOCKED";
            log(String.format("Blocked status changed to %s for %d(%d) on netId %d", new Object[]{Boolean.valueOf(blocked), Integer.valueOf(nri.mUid), Integer.valueOf(nri.request.requestId), Integer.valueOf(net.netId)}));
            LocalLog localLog = this.mNetworkInfoBlockingLogs;
            localLog.log(action + " " + nri.mUid);
        }
    }

    private void filterNetworkStateForUid(NetworkState state, int uid, boolean ignoreBlocked) {
        if (state != null && state.networkInfo != null && state.linkProperties != null) {
            if (isNetworkWithLinkPropertiesBlocked(state.linkProperties, uid, ignoreBlocked)) {
                state.networkInfo.setDetailedState(NetworkInfo.DetailedState.BLOCKED, (String) null, (String) null);
            }
            synchronized (this.mVpns) {
                if (this.mLockdownTracker != null) {
                    this.mLockdownTracker.augmentNetworkInfo(state.networkInfo);
                }
            }
        }
    }

    public NetworkInfo getActiveNetworkInfo() {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        NetworkState state = getUnfilteredActiveNetworkState(uid);
        filterNetworkStateForUid(state, uid, false);
        maybeLogBlockedNetworkInfo(state.networkInfo, uid);
        return state.networkInfo;
    }

    public Network getActiveNetwork() {
        enforceAccessPermission();
        return getActiveNetworkForUidInternal(Binder.getCallingUid(), false);
    }

    public Network getActiveNetworkForUid(int uid, boolean ignoreBlocked) {
        enforceConnectivityInternalPermission();
        return getActiveNetworkForUidInternal(uid, ignoreBlocked);
    }

    private Network getActiveNetworkForUidInternal(int uid, boolean ignoreBlocked) {
        NetworkAgentInfo nai;
        int user = UserHandle.getUserId(uid);
        int vpnNetId = 0;
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(user);
            if (vpn != null && vpn.appliesToUid(uid)) {
                vpnNetId = vpn.getNetId();
            }
        }
        if (vpnNetId != 0 && (nai = getNetworkAgentInfoForNetId(vpnNetId)) != null && createDefaultNetworkCapabilitiesForUid(uid).satisfiedByNetworkCapabilities(nai.networkCapabilities)) {
            return nai.network;
        }
        NetworkAgentInfo nai2 = getDefaultNetwork();
        if (nai2 != null && isNetworkWithLinkPropertiesBlocked(nai2.linkProperties, uid, ignoreBlocked)) {
            nai2 = null;
        }
        if (nai2 != null) {
            return nai2.network;
        }
        return null;
    }

    public NetworkInfo getActiveNetworkInfoUnfiltered() {
        enforceAccessPermission();
        return getUnfilteredActiveNetworkState(Binder.getCallingUid()).networkInfo;
    }

    public NetworkInfo getActiveNetworkInfoForUid(int uid, boolean ignoreBlocked) {
        enforceConnectivityInternalPermission();
        NetworkState state = getUnfilteredActiveNetworkState(uid);
        filterNetworkStateForUid(state, uid, ignoreBlocked);
        return state.networkInfo;
    }

    public NetworkInfo getNetworkInfo(int networkType) {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        if (getVpnUnderlyingNetworks(uid) != null) {
            NetworkState state = getUnfilteredActiveNetworkState(uid);
            if (state.networkInfo != null && state.networkInfo.getType() == networkType) {
                filterNetworkStateForUid(state, uid, false);
                return state.networkInfo;
            }
        }
        return getFilteredNetworkState(networkType, uid).networkInfo;
    }

    public NetworkInfo getNetworkInfoForUid(Network network, int uid, boolean ignoreBlocked) {
        enforceAccessPermission();
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        if (nai == null) {
            return null;
        }
        NetworkState state = nai.getNetworkState();
        filterNetworkStateForUid(state, uid, ignoreBlocked);
        return state.networkInfo;
    }

    public NetworkInfo[] getAllNetworkInfo() {
        enforceAccessPermission();
        ArrayList<NetworkInfo> result = Lists.newArrayList();
        for (int networkType = 0; networkType <= 18; networkType++) {
            NetworkInfo info = getNetworkInfo(networkType);
            if (info != null) {
                result.add(info);
            }
        }
        return (NetworkInfo[]) result.toArray(new NetworkInfo[result.size()]);
    }

    public Network getNetworkForType(int networkType) {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        NetworkState state = getFilteredNetworkState(networkType, uid);
        if (!isNetworkWithLinkPropertiesBlocked(state.linkProperties, uid, false)) {
            return state.network;
        }
        return null;
    }

    public Network[] getAllNetworks() {
        Network[] result;
        enforceAccessPermission();
        synchronized (this.mNetworkForNetId) {
            result = new Network[this.mNetworkForNetId.size()];
            for (int i = 0; i < this.mNetworkForNetId.size(); i++) {
                result[i] = this.mNetworkForNetId.valueAt(i).network;
            }
        }
        return result;
    }

    public NetworkCapabilities[] getDefaultNetworkCapabilitiesForUser(int userId) {
        Vpn vpn;
        Network[] networks;
        enforceAccessPermission();
        HashMap<Network, NetworkCapabilities> result = new HashMap<>();
        NetworkAgentInfo nai = getDefaultNetwork();
        NetworkCapabilities nc = getNetworkCapabilitiesInternal(nai);
        if (nc != null) {
            result.put(nai.network, nc);
        }
        synchronized (this.mVpns) {
            if (!(this.mLockdownEnabled || (vpn = this.mVpns.get(userId)) == null || (networks = vpn.getUnderlyingNetworks()) == null)) {
                for (Network network : networks) {
                    NetworkCapabilities nc2 = getNetworkCapabilitiesInternal(getNetworkAgentInfoForNetwork(network));
                    if (nc2 != null) {
                        result.put(network, nc2);
                    }
                }
            }
        }
        return (NetworkCapabilities[]) result.values().toArray(new NetworkCapabilities[result.size()]);
    }

    public boolean isNetworkSupported(int networkType) {
        enforceAccessPermission();
        return this.mLegacyTypeTracker.isTypeSupported(networkType);
    }

    public LinkProperties getActiveLinkProperties() {
        enforceAccessPermission();
        return getUnfilteredActiveNetworkState(Binder.getCallingUid()).linkProperties;
    }

    public LinkProperties getLinkPropertiesForType(int networkType) {
        LinkProperties linkProperties;
        enforceAccessPermission();
        NetworkAgentInfo nai = this.mLegacyTypeTracker.getNetworkForType(networkType);
        if (nai == null) {
            return null;
        }
        synchronized (nai) {
            linkProperties = new LinkProperties(nai.linkProperties);
        }
        return linkProperties;
    }

    public LinkProperties getLinkProperties(Network network) {
        enforceAccessPermission();
        return getLinkProperties(getNetworkAgentInfoForNetwork(network));
    }

    private LinkProperties getLinkProperties(NetworkAgentInfo nai) {
        LinkProperties linkProperties;
        if (nai == null) {
            return null;
        }
        synchronized (nai) {
            linkProperties = new LinkProperties(nai.linkProperties);
        }
        return linkProperties;
    }

    private NetworkCapabilities getNetworkCapabilitiesInternal(NetworkAgentInfo nai) {
        if (nai == null) {
            return null;
        }
        synchronized (nai) {
            if (nai.networkCapabilities == null) {
                return null;
            }
            NetworkCapabilities networkCapabilitiesRestrictedForCallerPermissions = networkCapabilitiesRestrictedForCallerPermissions(nai.networkCapabilities, Binder.getCallingPid(), Binder.getCallingUid());
            return networkCapabilitiesRestrictedForCallerPermissions;
        }
    }

    public NetworkCapabilities getNetworkCapabilities(Network network) {
        enforceAccessPermission();
        return getNetworkCapabilitiesInternal(getNetworkAgentInfoForNetwork(network));
    }

    private NetworkCapabilities networkCapabilitiesRestrictedForCallerPermissions(NetworkCapabilities nc, int callerPid, int callerUid) {
        NetworkCapabilities newNc = new NetworkCapabilities(nc);
        if (!checkSettingsPermission(callerPid, callerUid)) {
            newNc.setUids((Set) null);
            newNc.setSSID((String) null);
        }
        if (newNc.getNetworkSpecifier() != null) {
            newNc.setNetworkSpecifier(newNc.getNetworkSpecifier().redact());
        }
        return newNc;
    }

    private void restrictRequestUidsForCaller(NetworkCapabilities nc) {
        if (!checkSettingsPermission()) {
            nc.setSingleUid(Binder.getCallingUid());
        }
    }

    private void restrictBackgroundRequestForCaller(NetworkCapabilities nc) {
        if (!this.mPermissionMonitor.hasUseBackgroundNetworksPermission(Binder.getCallingUid())) {
            nc.addCapability(19);
        }
    }

    public NetworkState[] getAllNetworkState() {
        enforceConnectivityInternalPermission();
        ArrayList<NetworkState> result = Lists.newArrayList();
        for (Network network : getAllNetworks()) {
            NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
            if (nai != null) {
                result.add(nai.getNetworkState());
            }
        }
        return (NetworkState[]) result.toArray(new NetworkState[result.size()]);
    }

    @Deprecated
    public NetworkQuotaInfo getActiveNetworkQuotaInfo() {
        String str = TAG;
        Log.w(str, "Shame on UID " + Binder.getCallingUid() + " for calling the hidden API getNetworkQuotaInfo(). Shame!");
        return new NetworkQuotaInfo();
    }

    public boolean isActiveNetworkMetered() {
        enforceAccessPermission();
        NetworkCapabilities caps = getNetworkCapabilities(getActiveNetwork());
        if (caps != null) {
            return true ^ caps.hasCapability(11);
        }
        return true;
    }

    private boolean disallowedBecauseSystemCaller() {
        if (!isSystem(Binder.getCallingUid()) || SystemProperties.getInt("ro.product.first_api_level", 0) <= 28) {
            return false;
        }
        log("This method exists only for app backwards compatibility and must not be called by system services.");
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public boolean requestRouteToHostAddress(int networkType, byte[] hostAddress) {
        NetworkInfo.DetailedState netState;
        LinkProperties lp;
        int netId;
        if (disallowedBecauseSystemCaller()) {
            return false;
        }
        enforceChangePermission();
        if (this.mProtectedNetworks.contains(Integer.valueOf(networkType))) {
            enforceConnectivityInternalPermission();
        }
        try {
            InetAddress addr = InetAddress.getByAddress(hostAddress);
            if (!ConnectivityManager.isNetworkTypeValid(networkType)) {
                log("requestRouteToHostAddress on invalid network: " + networkType);
                return false;
            }
            NetworkAgentInfo nai = this.mLegacyTypeTracker.getNetworkForType(networkType);
            if (nai == null) {
                if (!this.mLegacyTypeTracker.isTypeSupported(networkType)) {
                    log("requestRouteToHostAddress on unsupported network: " + networkType);
                } else {
                    log("requestRouteToHostAddress on down network: " + networkType);
                }
                return false;
            }
            synchronized (nai) {
                netState = nai.networkInfo.getDetailedState();
            }
            if (netState == NetworkInfo.DetailedState.CONNECTED || netState == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK) {
                int uid = Binder.getCallingUid();
                long token = Binder.clearCallingIdentity();
                try {
                    synchronized (nai) {
                        lp = nai.linkProperties;
                        netId = nai.network.netId;
                    }
                    boolean ok = addLegacyRouteToHost(lp, addr, netId, uid);
                    log("requestRouteToHostAddress ok=" + ok);
                    Binder.restoreCallingIdentity(token);
                    return ok;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(token);
                    throw th;
                }
            } else {
                if (VDBG) {
                    log("requestRouteToHostAddress on down network (" + networkType + ") - dropped netState=" + netState);
                }
                return false;
            }
        } catch (UnknownHostException e) {
            log("requestRouteToHostAddress got " + e.toString());
            return false;
        }
    }

    private boolean addLegacyRouteToHost(LinkProperties lp, InetAddress addr, int netId, int uid) {
        RouteInfo bestRoute;
        RouteInfo bestRoute2 = RouteInfo.selectBestRoute(lp.getAllRoutes(), addr);
        if (bestRoute2 == null) {
            bestRoute = RouteInfo.makeHostRoute(addr, lp.getInterfaceName());
        } else {
            String iface = bestRoute2.getInterface();
            if (bestRoute2.getGateway().equals(addr)) {
                bestRoute = RouteInfo.makeHostRoute(addr, iface);
            } else {
                bestRoute = RouteInfo.makeHostRoute(addr, bestRoute2.getGateway(), iface);
            }
        }
        log("Adding legacy route " + bestRoute + " for UID/PID " + uid + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid());
        try {
            this.mNMS.addLegacyRouteForNetId(netId, bestRoute, uid);
            return true;
        } catch (Exception e) {
            loge("Exception trying to add a route: " + e);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void registerNetdEventCallback() {
        IIpConnectivityMetrics ipConnectivityMetrics = IIpConnectivityMetrics.Stub.asInterface(ServiceManager.getService("connmetrics"));
        if (ipConnectivityMetrics == null) {
            Slog.wtf(TAG, "Missing IIpConnectivityMetrics");
            return;
        }
        try {
            ipConnectivityMetrics.addNetdEventCallback(0, this.mNetdEventCallback);
        } catch (Exception e) {
            loge("Error registering netd callback: " + e);
        }
    }

    /* access modifiers changed from: package-private */
    public void handleUidRulesChanged(int uid, int newRules) {
        if (this.mUidRules.get(uid, 0) != newRules) {
            maybeNotifyNetworkBlockedForNewUidRules(uid, newRules);
            if (newRules == 0) {
                this.mUidRules.delete(uid);
            } else {
                this.mUidRules.put(uid, newRules);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void handleRestrictBackgroundChanged(boolean restrictBackground) {
        if (this.mRestrictBackground != restrictBackground) {
            for (NetworkAgentInfo nai : this.mNetworkAgentInfos.values()) {
                boolean curMetered = nai.networkCapabilities.isMetered();
                maybeNotifyNetworkBlocked(nai, curMetered, curMetered, this.mRestrictBackground, restrictBackground);
            }
            this.mRestrictBackground = restrictBackground;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0021, code lost:
        r0 = r3.mPolicyManagerInternal;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0027, code lost:
        return com.android.server.net.NetworkPolicyManagerInternal.isUidNetworkingBlocked(r4, r5, r6, r7);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isUidNetworkingWithVpnBlocked(int r4, int r5, boolean r6, boolean r7) {
        /*
            r3 = this;
            android.util.SparseArray<com.android.server.connectivity.Vpn> r0 = r3.mVpns
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.connectivity.Vpn> r1 = r3.mVpns     // Catch:{ all -> 0x0028 }
            int r2 = android.os.UserHandle.getUserId(r4)     // Catch:{ all -> 0x0028 }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0028 }
            com.android.server.connectivity.Vpn r1 = (com.android.server.connectivity.Vpn) r1     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0020
            boolean r2 = r1.getLockdown()     // Catch:{ all -> 0x0028 }
            if (r2 == 0) goto L_0x0020
            boolean r2 = r1.isBlockingUid(r4)     // Catch:{ all -> 0x0028 }
            if (r2 == 0) goto L_0x0020
            r2 = 1
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return r2
        L_0x0020:
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            com.android.server.net.NetworkPolicyManagerInternal r0 = r3.mPolicyManagerInternal
            boolean r0 = com.android.server.net.NetworkPolicyManagerInternal.isUidNetworkingBlocked(r4, r5, r6, r7)
            return r0
        L_0x0028:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.isUidNetworkingWithVpnBlocked(int, int, boolean, boolean):boolean");
    }

    private void enforceCrossUserPermission(int userId) {
        if (userId != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "ConnectivityService");
        }
    }

    private boolean checkAnyPermissionOf(String... permissions) {
        for (String permission : permissions) {
            if (this.mContext.checkCallingOrSelfPermission(permission) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean checkAnyPermissionOf(int pid, int uid, String... permissions) {
        for (String permission : permissions) {
            if (this.mContext.checkPermission(permission, pid, uid) == 0) {
                return true;
            }
        }
        return false;
    }

    private void enforceAnyPermissionOf(String... permissions) {
        if (!checkAnyPermissionOf(permissions)) {
            throw new SecurityException("Requires one of the following permissions: " + String.join(", ", permissions) + ".");
        }
    }

    private void enforceInternetPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.INTERNET", "ConnectivityService");
    }

    private void enforceAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE", "ConnectivityService");
    }

    private void enforceChangePermission() {
        ConnectivityManager.enforceChangePermission(this.mContext);
    }

    /* access modifiers changed from: private */
    public void enforceSettingsPermission() {
        enforceAnyPermissionOf("android.permission.NETWORK_SETTINGS", "android.permission.MAINLINE_NETWORK_STACK");
    }

    private boolean checkSettingsPermission() {
        return checkAnyPermissionOf("android.permission.NETWORK_SETTINGS", "android.permission.MAINLINE_NETWORK_STACK");
    }

    private boolean checkSettingsPermission(int pid, int uid) {
        return this.mContext.checkPermission("android.permission.NETWORK_SETTINGS", pid, uid) == 0 || this.mContext.checkPermission("android.permission.MAINLINE_NETWORK_STACK", pid, uid) == 0;
    }

    private void enforceTetherAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE", "ConnectivityService");
    }

    private void enforceConnectivityInternalPermission() {
        enforceAnyPermissionOf("android.permission.CONNECTIVITY_INTERNAL", "android.permission.MAINLINE_NETWORK_STACK");
    }

    private void enforceControlAlwaysOnVpnPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONTROL_ALWAYS_ON_VPN", "ConnectivityService");
    }

    private void enforceNetworkStackSettingsOrSetup() {
        enforceAnyPermissionOf("android.permission.NETWORK_SETTINGS", "android.permission.NETWORK_SETUP_WIZARD", "android.permission.NETWORK_STACK", "android.permission.MAINLINE_NETWORK_STACK");
    }

    private boolean checkNetworkStackPermission() {
        return checkAnyPermissionOf("android.permission.NETWORK_STACK", "android.permission.MAINLINE_NETWORK_STACK");
    }

    private boolean checkNetworkSignalStrengthWakeupPermission(int pid, int uid) {
        return checkAnyPermissionOf(pid, uid, "android.permission.NETWORK_SIGNAL_STRENGTH_WAKEUP", "android.permission.MAINLINE_NETWORK_STACK");
    }

    private void enforceConnectivityRestrictedNetworksPermission() {
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS", "ConnectivityService");
        } catch (SecurityException e) {
            enforceConnectivityInternalPermission();
        }
    }

    private void enforceKeepalivePermission() {
        this.mContext.enforceCallingOrSelfPermission(KeepaliveTracker.PERMISSION, "ConnectivityService");
    }

    public void sendConnectedBroadcast(NetworkInfo info) {
        enforceConnectivityInternalPermission();
        sendGeneralBroadcast(info, "android.net.conn.CONNECTIVITY_CHANGE");
    }

    private void sendInetConditionBroadcast(NetworkInfo info) {
        sendGeneralBroadcast(info, "android.net.conn.INET_CONDITION_ACTION");
    }

    private Intent makeGeneralIntent(NetworkInfo info, String bcastType) {
        synchronized (this.mVpns) {
            if (this.mLockdownTracker != null) {
                info = new NetworkInfo(info);
                this.mLockdownTracker.augmentNetworkInfo(info);
            }
        }
        Intent intent = new Intent(bcastType);
        intent.putExtra("networkInfo", new NetworkInfo(info));
        intent.putExtra("networkType", info.getType());
        if (info.isFailover()) {
            intent.putExtra("isFailover", true);
            info.setFailover(false);
        }
        if (info.getReason() != null) {
            intent.putExtra(PhoneWindowManager.SYSTEM_DIALOG_REASON_KEY, info.getReason());
        }
        if (info.getExtraInfo() != null) {
            intent.putExtra("extraInfo", info.getExtraInfo());
        }
        intent.putExtra("inetCondition", this.mDefaultInetConditionPublished);
        return intent;
    }

    private void sendGeneralBroadcast(NetworkInfo info, String bcastType) {
        sendStickyBroadcast(makeGeneralIntent(info, bcastType));
    }

    /* access modifiers changed from: private */
    public void sendDataActivityBroadcast(int deviceType, boolean active, long tsNanos) {
        Intent intent = new Intent("android.net.conn.DATA_ACTIVITY_CHANGE");
        intent.putExtra("deviceType", deviceType);
        intent.putExtra("isActive", active);
        intent.putExtra("tsNanos", tsNanos);
        long ident = Binder.clearCallingIdentity();
        try {
            this.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.ALL, "android.permission.RECEIVE_DATA_ACTIVITY_CHANGE", (BroadcastReceiver) null, (Handler) null, 0, (String) null, (Bundle) null);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    private void sendStickyBroadcast(Intent intent) {
        synchronized (this) {
            if (!this.mSystemReady && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                this.mInitialBroadcast = new Intent(intent);
            }
            intent.addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
            if (VDBG) {
                log("sendStickyBroadcast: action=" + intent.getAction());
            }
            Bundle options = null;
            long ident = Binder.clearCallingIdentity();
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                NetworkInfo ni = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                if (ni.getType() == 3) {
                    intent.setAction("android.net.conn.CONNECTIVITY_CHANGE_SUPL");
                    intent.addFlags(1073741824);
                } else {
                    BroadcastOptions opts = BroadcastOptions.makeBasic();
                    opts.setMaxManifestReceiverApiLevel(23);
                    options = opts.toBundle();
                }
                try {
                    BatteryStatsService.getService().noteConnectivityChanged(intent.getIntExtra("networkType", -1), ni.getState().toString());
                } catch (RemoteException e) {
                }
                intent.addFlags(DumpState.DUMP_COMPILER_STATS);
            }
            try {
                this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL, options);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void systemReady() {
        this.mProxyTracker.loadGlobalProxy();
        registerNetdEventCallback();
        this.mTethering.systemReady();
        synchronized (this) {
            this.mSystemReady = true;
            if (this.mInitialBroadcast != null) {
                this.mContext.sendStickyBroadcastAsUser(this.mInitialBroadcast, UserHandle.ALL);
                this.mInitialBroadcast = null;
            }
        }
        updateLockdownVpn();
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(30));
        InternalHandler internalHandler2 = this.mHandler;
        internalHandler2.sendMessage(internalHandler2.obtainMessage(25));
        this.mPermissionMonitor.startMonitoring();
        WifiAssistant.make(this.mContext);
    }

    private void setupDataActivityTracking(NetworkAgentInfo networkAgent) {
        int timeout;
        String iface = networkAgent.linkProperties.getInterfaceName();
        int type = -1;
        if (networkAgent.networkCapabilities.hasTransport(0)) {
            timeout = Settings.Global.getInt(this.mContext.getContentResolver(), "data_activity_timeout_mobile", 10);
            type = 0;
        } else if (networkAgent.networkCapabilities.hasTransport(1)) {
            timeout = Settings.Global.getInt(this.mContext.getContentResolver(), "data_activity_timeout_wifi", 15);
            type = 1;
        } else {
            timeout = 0;
        }
        if (timeout > 0 && iface != null && type != -1) {
            try {
                this.mNMS.addIdleTimer(iface, timeout, type);
            } catch (Exception e) {
                loge("Exception in setupDataActivityTracking " + e);
            }
        }
    }

    private void removeDataActivityTracking(NetworkAgentInfo networkAgent) {
        String iface = networkAgent.linkProperties.getInterfaceName();
        NetworkCapabilities caps = networkAgent.networkCapabilities;
        if (iface == null) {
            return;
        }
        if (caps.hasTransport(0) || caps.hasTransport(1)) {
            try {
                this.mNMS.removeIdleTimer(iface);
            } catch (Exception e) {
                loge("Exception in removeDataActivityTracking " + e);
            }
        }
    }

    private void updateDataActivityTracking(NetworkAgentInfo newNetwork, NetworkAgentInfo oldNetwork) {
        if (newNetwork != null) {
            setupDataActivityTracking(newNetwork);
        }
        if (oldNetwork != null) {
            removeDataActivityTracking(oldNetwork);
        }
    }

    private void updateMtu(LinkProperties newLp, LinkProperties oldLp) {
        String iface = newLp.getInterfaceName();
        int mtu = newLp.getMtu();
        if (oldLp != null || mtu != 0) {
            if (oldLp == null || !newLp.isIdenticalMtu(oldLp)) {
                if (!LinkProperties.isValidMtu(mtu, newLp.hasGlobalIpv6Address())) {
                    if (mtu != 0) {
                        loge("Unexpected mtu value: " + mtu + ", " + iface);
                    }
                } else if (TextUtils.isEmpty(iface)) {
                    loge("Setting MTU size with null iface.");
                } else {
                    try {
                        if (VDBG || DDBG) {
                            log("Setting MTU size: " + iface + ", " + mtu);
                        }
                        this.mNMS.setMtu(iface, mtu);
                    } catch (Exception e) {
                        String str = TAG;
                        Slog.e(str, "exception in setMtu()" + e);
                    }
                }
            } else if (VDBG) {
                log("identical MTU - not setting");
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public MockableSystemProperties getSystemProperties() {
        return new MockableSystemProperties();
    }

    private void updateTcpBufferSizes(NetworkAgentInfo nai) {
        if (isDefaultNetwork(nai)) {
            String tcpBufferSizes = nai.linkProperties.getTcpBufferSizes();
            if (nai.networkCapabilities.hasTransport(0)) {
                tcpBufferSizes = NetPluginDelegate.get5GTcpBuffers(tcpBufferSizes, nai.networkCapabilities.getNetworkSpecifier());
            }
            updateTcpBufferSizes(tcpBufferSizes);
        }
    }

    private void updateTcpBufferSizes(String tcpBufferSizes) {
        String[] values = null;
        if (tcpBufferSizes != null) {
            values = tcpBufferSizes.split(",");
        }
        if (values == null || values.length != 6) {
            log("Invalid tcpBufferSizes string: " + tcpBufferSizes + ", using defaults");
            tcpBufferSizes = DEFAULT_TCP_BUFFER_SIZES;
            values = tcpBufferSizes.split(",");
        }
        if (!tcpBufferSizes.equals(this.mCurrentTcpBufferSizes)) {
            try {
                if (VDBG || DDBG) {
                    String str = TAG;
                    Slog.d(str, "Setting tx/rx TCP buffers to " + tcpBufferSizes);
                }
                this.mNetd.setTcpRWmemorySize(String.join(" ", new CharSequence[]{values[0], values[1], values[2]}), String.join(" ", new CharSequence[]{values[3], values[4], values[5]}));
                this.mCurrentTcpBufferSizes = tcpBufferSizes;
            } catch (RemoteException | ServiceSpecificException e) {
                loge("Can't set TCP buffer sizes:" + e);
            }
            Integer rwndValue = Integer.valueOf(Settings.Global.getInt(this.mContext.getContentResolver(), "tcp_default_init_rwnd", this.mSystemProperties.getInt(DEFAULT_TCP_RWND_KEY, 0)));
            if (rwndValue.intValue() != 0) {
                this.mSystemProperties.set("sys.sysctl.tcp_def_init_rwnd", rwndValue.toString());
            }
        }
    }

    public int getRestoreDefaultNetworkDelay(int networkType) {
        String restoreDefaultNetworkDelayStr = this.mSystemProperties.get(NETWORK_RESTORE_DELAY_PROP_NAME);
        if (!(restoreDefaultNetworkDelayStr == null || restoreDefaultNetworkDelayStr.length() == 0)) {
            try {
                return Integer.parseInt(restoreDefaultNetworkDelayStr);
            } catch (NumberFormatException e) {
            }
        }
        if (networkType > 18) {
            return RESTORE_DEFAULT_NETWORK_DELAY;
        }
        NetworkConfig[] networkConfigArr = this.mNetConfigs;
        if (networkConfigArr[networkType] != null) {
            return networkConfigArr[networkType].restoreTime;
        }
        return RESTORE_DEFAULT_NETWORK_DELAY;
    }

    private void dumpNetworkDiagnostics(IndentingPrintWriter pw) {
        List<NetworkDiagnostics> netDiags = new ArrayList<>();
        for (NetworkAgentInfo nai : networksSortedById()) {
            netDiags.add(new NetworkDiagnostics(nai.network, new LinkProperties(nai.linkProperties), 5000));
        }
        for (NetworkDiagnostics netDiag : netDiags) {
            pw.println();
            netDiag.waitForMeasurements();
            netDiag.dump(pw);
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        PriorityDump.dump(this.mPriorityDumper, fd, writer, args);
    }

    /* access modifiers changed from: private */
    public void doDump(FileDescriptor fd, PrintWriter writer, String[] args, boolean asProto) {
        int i;
        IndentingPrintWriter pw = new IndentingPrintWriter(writer, "  ");
        if (!DumpUtils.checkDumpPermission(this.mContext, TAG, pw) || asProto) {
            return;
        }
        if (ArrayUtils.contains(args, DIAG_ARG)) {
            dumpNetworkDiagnostics(pw);
        } else if (ArrayUtils.contains(args, TETHERING_ARG)) {
            this.mTethering.dump(fd, pw, args);
        } else if (ArrayUtils.contains(args, NETWORK_ARG)) {
            dumpNetworks(pw);
        } else if (ArrayUtils.contains(args, REQUEST_ARG)) {
            dumpNetworkRequests(pw);
        } else {
            pw.print("NetworkFactories for:");
            for (NetworkFactoryInfo nfi : this.mNetworkFactoryInfos.values()) {
                pw.print(" " + nfi.name);
            }
            pw.println();
            pw.println();
            NetworkAgentInfo defaultNai = getDefaultNetwork();
            pw.print("Active default network: ");
            if (defaultNai == null) {
                pw.println("none");
            } else {
                pw.println(defaultNai.network.netId);
            }
            pw.println();
            pw.println("Current Networks:");
            pw.increaseIndent();
            dumpNetworks(pw);
            pw.decreaseIndent();
            pw.println();
            pw.print("Restrict background: ");
            pw.println(this.mRestrictBackground);
            pw.println();
            pw.println("Status for known UIDs:");
            pw.increaseIndent();
            int size = this.mUidRules.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                }
                try {
                    int uid = this.mUidRules.keyAt(i2);
                    int uidRules = this.mUidRules.get(uid, 0);
                    pw.println("UID=" + uid + " rules=" + NetworkPolicyManager.uidRulesToString(uidRules));
                } catch (ArrayIndexOutOfBoundsException e) {
                    pw.println("  ArrayIndexOutOfBoundsException");
                } catch (ConcurrentModificationException e2) {
                    pw.println("  ConcurrentModificationException");
                }
                i2++;
            }
            pw.println();
            pw.decreaseIndent();
            pw.println("Network Requests:");
            pw.increaseIndent();
            dumpNetworkRequests(pw);
            pw.decreaseIndent();
            pw.println();
            this.mLegacyTypeTracker.dump(pw);
            pw.println();
            this.mTethering.dump(fd, pw, args);
            pw.println();
            this.mKeepaliveTracker.dump(pw);
            pw.println();
            dumpAvoidBadWifiSettings(pw);
            pw.println();
            this.mMultipathPolicyTracker.dump(pw);
            if (!ArrayUtils.contains(args, SHORT_ARG)) {
                pw.println();
                pw.println("mNetworkRequestInfoLogs (most recent first):");
                pw.increaseIndent();
                this.mNetworkRequestInfoLogs.reverseDump(fd, pw, args);
                pw.decreaseIndent();
                pw.println();
                pw.println("mNetworkInfoBlockingLogs (most recent first):");
                pw.increaseIndent();
                this.mNetworkInfoBlockingLogs.reverseDump(fd, pw, args);
                pw.decreaseIndent();
                pw.println();
                pw.println("NetTransition WakeLock activity (most recent first):");
                pw.increaseIndent();
                pw.println("total acquisitions: " + this.mTotalWakelockAcquisitions);
                pw.println("total releases: " + this.mTotalWakelockReleases);
                pw.println("cumulative duration: " + (this.mTotalWakelockDurationMs / 1000) + "s");
                pw.println("longest duration: " + (this.mMaxWakelockDurationMs / 1000) + "s");
                if (this.mTotalWakelockAcquisitions > this.mTotalWakelockReleases) {
                    pw.println("currently holding WakeLock for: " + ((SystemClock.elapsedRealtime() - this.mLastWakeLockAcquireTimestamp) / 1000) + "s");
                }
                this.mWakelockLogs.reverseDump(fd, pw, args);
                pw.println();
                pw.println("bandwidth update requests (by uid):");
                pw.increaseIndent();
                synchronized (this.mBandwidthRequests) {
                    for (i = 0; i < this.mBandwidthRequests.size(); i++) {
                        pw.println("[" + this.mBandwidthRequests.keyAt(i) + "]: " + this.mBandwidthRequests.valueAt(i));
                    }
                }
                pw.decreaseIndent();
                pw.decreaseIndent();
            }
            pw.println();
            pw.println("NetworkStackClient logs:");
            pw.increaseIndent();
            NetworkStackClient.getInstance().dump(pw);
            pw.decreaseIndent();
            pw.println();
            pw.println("Permission Monitor:");
            pw.increaseIndent();
            this.mPermissionMonitor.dump(pw);
            pw.decreaseIndent();
            WifiAssistant.get().dump(fd, pw, args);
        }
    }

    private void dumpNetworks(IndentingPrintWriter pw) {
        for (NetworkAgentInfo nai : networksSortedById()) {
            pw.println(nai.toString());
            pw.increaseIndent();
            pw.println(String.format("Requests: REQUEST:%d LISTEN:%d BACKGROUND_REQUEST:%d total:%d", new Object[]{Integer.valueOf(nai.numForegroundNetworkRequests()), Integer.valueOf(nai.numNetworkRequests() - nai.numRequestNetworkRequests()), Integer.valueOf(nai.numBackgroundNetworkRequests()), Integer.valueOf(nai.numNetworkRequests())}));
            pw.increaseIndent();
            for (int i = 0; i < nai.numNetworkRequests(); i++) {
                pw.println(nai.requestAt(i).toString());
            }
            pw.decreaseIndent();
            pw.println("Lingered:");
            pw.increaseIndent();
            nai.dumpLingerTimers(pw);
            pw.decreaseIndent();
            pw.decreaseIndent();
        }
    }

    private void dumpNetworkRequests(IndentingPrintWriter pw) {
        for (NetworkRequestInfo nri : requestsSortedById()) {
            pw.println(nri.toString());
        }
    }

    private NetworkAgentInfo[] networksSortedById() {
        NetworkAgentInfo[] networks = (NetworkAgentInfo[]) this.mNetworkAgentInfos.values().toArray(new NetworkAgentInfo[0]);
        Arrays.sort(networks, Comparator.comparingInt($$Lambda$ConnectivityService$_NU7EIcPVSuF_gWH_NWN_gBL4w.INSTANCE));
        return networks;
    }

    private NetworkRequestInfo[] requestsSortedById() {
        NetworkRequestInfo[] requests = (NetworkRequestInfo[]) this.mNetworkRequests.values().toArray(new NetworkRequestInfo[0]);
        Arrays.sort(requests, Comparator.comparingInt($$Lambda$ConnectivityService$iOdlQdHoQM14teTSEPRHRRL3k.INSTANCE));
        return requests;
    }

    /* access modifiers changed from: private */
    public boolean isLiveNetworkAgent(NetworkAgentInfo nai, int what) {
        if (nai.network == null) {
            return false;
        }
        NetworkAgentInfo officialNai = getNetworkAgentInfoForNetwork(nai.network);
        if (officialNai != null && officialNai.equals(nai)) {
            return true;
        }
        if (officialNai != null || VDBG) {
            loge(eventName(what) + " - isLiveNetworkAgent found mismatched netId: " + officialNai + " - " + nai);
        }
        return false;
    }

    private class NetworkStateTrackerHandler extends Handler {
        public NetworkStateTrackerHandler(Looper looper) {
            super(looper);
        }

        private boolean maybeHandleAsyncChannelMessage(Message msg) {
            switch (msg.what) {
                case 69632:
                    ConnectivityService.this.handleAsyncChannelHalfConnect(msg);
                    return true;
                case 69635:
                    NetworkAgentInfo nai = (NetworkAgentInfo) ConnectivityService.this.mNetworkAgentInfos.get(msg.replyTo);
                    if (nai == null) {
                        return true;
                    }
                    nai.asyncChannel.disconnect();
                    return true;
                case 69636:
                    ConnectivityService.this.handleAsyncChannelDisconnected(msg);
                    return true;
                default:
                    return false;
            }
        }

        private void maybeHandleNetworkAgentMessage(Message msg) {
            NetworkAgentInfo nai = (NetworkAgentInfo) ConnectivityService.this.mNetworkAgentInfos.get(msg.replyTo);
            boolean z = false;
            if (nai != null) {
                int i = msg.what;
                if (i == 528392) {
                    if (nai.everConnected) {
                        ConnectivityService.loge("ERROR: cannot call explicitlySelected on already-connected network");
                    }
                    nai.networkMisc.explicitlySelected = msg.arg1 == 1;
                    CaptivePortalInjector.NetworkAgentInfoInner.setExplicitlySelected(msg.arg1 == 1);
                    nai.networkMisc.acceptUnvalidated = msg.arg1 == 1 && msg.arg2 == 1;
                    NetworkMisc networkMisc = nai.networkMisc;
                    if (msg.arg2 == 1) {
                        z = true;
                    }
                    networkMisc.acceptPartialConnectivity = z;
                } else if (i != 528397) {
                    switch (i) {
                        case 528385:
                            ConnectivityService.this.updateNetworkInfo(nai, (NetworkInfo) msg.obj);
                            return;
                        case 528386:
                            NetworkCapabilities networkCapabilities = (NetworkCapabilities) msg.obj;
                            if (networkCapabilities.hasConnectivityManagedCapability()) {
                                Slog.wtf(ConnectivityService.TAG, "BUG: " + nai + " has CS-managed capability.");
                            }
                            ConnectivityService.this.updateCapabilities(nai.getCurrentScore(), nai, networkCapabilities);
                            return;
                        case 528387:
                            ConnectivityService.this.handleUpdateLinkProperties(nai, (LinkProperties) msg.obj);
                            return;
                        case 528388:
                            ConnectivityService.this.updateNetworkScore(nai, msg.arg1);
                            return;
                        default:
                            return;
                    }
                } else {
                    ConnectivityService.this.mKeepaliveTracker.handleEventSocketKeepalive(nai, msg);
                }
            } else if (ConnectivityService.VDBG) {
                ConnectivityService.log(String.format("%s from unknown NetworkAgent", new Object[]{ConnectivityService.eventName(msg.what)}));
            }
        }

        private boolean maybeHandleNetworkMonitorMessage(Message msg) {
            int i = 2;
            switch (msg.what) {
                case 41:
                    NetworkAgentInfo nai = ConnectivityService.this.getNetworkAgentInfoForNetId(msg.arg2);
                    if (nai != null) {
                        boolean wasPartial = nai.partialConnectivity;
                        nai.partialConnectivity = (msg.arg1 & 2) != 0;
                        boolean partialConnectivityChanged = wasPartial != nai.partialConnectivity;
                        boolean valid = WifiAssistant.get().handleNetworkValidationResult(nai, (msg.arg1 & 1) != 0);
                        boolean wasValidated = nai.lastValidated;
                        boolean wasDefault = ConnectivityService.this.isDefaultNetwork(nai);
                        if (nai.captivePortalValidationPending && valid) {
                            nai.captivePortalValidationPending = false;
                            ConnectivityService.this.showNetworkNotification(nai, NetworkNotificationManager.NotificationType.LOGGED_IN);
                        }
                        String logMsg = "";
                        String redirectUrl = msg.obj instanceof String ? (String) msg.obj : logMsg;
                        if (!TextUtils.isEmpty(redirectUrl)) {
                            logMsg = " with redirect to " + redirectUrl;
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append(nai.name());
                        sb.append(" validation ");
                        sb.append(valid ? "passed" : "failed");
                        sb.append(logMsg);
                        ConnectivityService.log(sb.toString());
                        if (valid != nai.lastValidated) {
                            if (wasDefault) {
                                ConnectivityService.this.metricsLogger().defaultNetworkMetrics().logDefaultNetworkValidity(SystemClock.elapsedRealtime(), valid);
                            }
                            int oldScore = nai.getCurrentScore();
                            nai.lastValidated = valid;
                            nai.everValidated |= valid;
                            ConnectivityService.this.updateCapabilities(oldScore, nai, nai.networkCapabilities);
                            if (oldScore != nai.getCurrentScore()) {
                                ConnectivityService.this.sendUpdatedScoreToFactories(nai);
                            }
                            if (valid) {
                                ConnectivityService.this.handleFreshlyValidatedNetwork(nai);
                                ConnectivityService.this.mNotifier.clearNotification(nai.network.netId, NetworkNotificationManager.NotificationType.NO_INTERNET);
                                ConnectivityService.this.mNotifier.clearNotification(nai.network.netId, NetworkNotificationManager.NotificationType.LOST_INTERNET);
                                ConnectivityService.this.mNotifier.clearNotification(nai.network.netId, NetworkNotificationManager.NotificationType.PARTIAL_CONNECTIVITY);
                            }
                        } else if (partialConnectivityChanged) {
                            ConnectivityService.this.updateCapabilities(nai.getCurrentScore(), nai, nai.networkCapabilities);
                        }
                        ConnectivityService.this.updateInetCondition(nai);
                        Bundle redirectUrlBundle = new Bundle();
                        redirectUrlBundle.putString(NetworkAgent.REDIRECT_URL_KEY, redirectUrl);
                        AsyncChannel asyncChannel = nai.asyncChannel;
                        if (valid) {
                            i = 1;
                        }
                        asyncChannel.sendMessage(528391, i, 0, redirectUrlBundle);
                        if (!wasPartial && nai.partialConnectivity) {
                            ConnectivityService.this.mHandler.removeMessages(29, nai.network);
                            ConnectivityService.this.handlePromptUnvalidated(nai.network);
                        }
                        if (wasValidated && !nai.lastValidated) {
                            ConnectivityService.this.handleNetworkUnvalidated(nai);
                            break;
                        }
                    }
                    break;
                case 42:
                    NetworkAgentInfo nai2 = ConnectivityService.this.getNetworkAgentInfoForNetId(msg.arg2);
                    if (nai2 != null) {
                        ConnectivityService.this.updatePrivateDns(nai2, (PrivateDnsConfig) msg.obj);
                        break;
                    }
                    break;
                case 43:
                    int netId = msg.arg2;
                    boolean visible = ConnectivityService.toBool(msg.arg1);
                    NetworkAgentInfo nai3 = ConnectivityService.this.getNetworkAgentInfoForNetId(netId);
                    if (!(nai3 == null || visible == nai3.lastCaptivePortalDetected)) {
                        int oldScore2 = nai3.getCurrentScore();
                        nai3.lastCaptivePortalDetected = visible;
                        nai3.everCaptivePortalDetected |= visible;
                        if (nai3.lastCaptivePortalDetected && 2 == getCaptivePortalMode()) {
                            ConnectivityService.log("Avoiding captive portal network: " + nai3.name());
                            nai3.asyncChannel.sendMessage(528399);
                            ConnectivityService.this.teardownUnneededNetwork(nai3);
                            break;
                        } else {
                            ConnectivityService.this.updateCapabilities(oldScore2, nai3, nai3.networkCapabilities);
                        }
                    }
                    if (visible) {
                        if (nai3 != null) {
                            if (!nai3.networkMisc.provisioningNotificationDisabled) {
                                ConnectivityService.this.mNotifier.showNotification(netId, NetworkNotificationManager.NotificationType.SIGN_IN, nai3, (NetworkAgentInfo) null, (PendingIntent) msg.obj, nai3.networkMisc.explicitlySelected);
                                break;
                            }
                        } else {
                            ConnectivityService.loge("EVENT_PROVISIONING_NOTIFICATION from unknown NetworkMonitor");
                            break;
                        }
                    } else {
                        ConnectivityService.this.mNotifier.clearNotification(netId, NetworkNotificationManager.NotificationType.SIGN_IN);
                        ConnectivityService.this.mNotifier.clearNotification(netId, NetworkNotificationManager.NotificationType.NETWORK_SWITCH);
                        break;
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }

        private int getCaptivePortalMode() {
            return Settings.Global.getInt(ConnectivityService.this.mContext.getContentResolver(), "captive_portal_mode", 1);
        }

        private boolean maybeHandleNetworkAgentInfoMessage(Message msg) {
            if (msg.what != 1001) {
                return false;
            }
            NetworkAgentInfo nai = (NetworkAgentInfo) msg.obj;
            if (nai == null || !ConnectivityService.this.isLiveNetworkAgent(nai, msg.what)) {
                return true;
            }
            ConnectivityService.this.handleLingerComplete(nai);
            return true;
        }

        private boolean maybeHandleNetworkFactoryMessage(Message msg) {
            if (msg.what != 536580) {
                return false;
            }
            ConnectivityService.this.handleReleaseNetworkRequest((NetworkRequest) msg.obj, msg.sendingUid, true);
            return true;
        }

        public void handleMessage(Message msg) {
            if (!maybeHandleAsyncChannelMessage(msg) && !maybeHandleNetworkMonitorMessage(msg) && !maybeHandleNetworkAgentInfoMessage(msg) && !maybeHandleNetworkFactoryMessage(msg)) {
                maybeHandleNetworkAgentMessage(msg);
            }
        }
    }

    private class NetworkMonitorCallbacks extends INetworkMonitorCallbacks.Stub {
        private final AutodestructReference<NetworkAgentInfo> mNai;
        CaptivePortalInjector.NetworkAgentInfoInner mNaii;
        private final int mNetId;

        private NetworkMonitorCallbacks(NetworkAgentInfo nai) {
            this.mNetId = nai.network.netId;
            this.mNai = new AutodestructReference<>(nai);
            this.mNaii = new CaptivePortalInjector.NetworkAgentInfoInner(nai);
        }

        public void onNetworkMonitorCreated(INetworkMonitor networkMonitor) {
            ConnectivityService.this.mHandler.sendMessage(ConnectivityService.this.mHandler.obtainMessage(18, new Pair(this.mNai.getAndDestroy(), networkMonitor)));
        }

        public void notifyNetworkTested(int testResult, String redirectUrl) {
            ConnectivityService.this.mTrackerHandler.sendMessage(ConnectivityService.this.mTrackerHandler.obtainMessage(41, testResult, this.mNetId, redirectUrl));
        }

        public void notifyPrivateDnsConfigResolved(PrivateDnsConfigParcel config) {
            ConnectivityService.this.mTrackerHandler.sendMessage(ConnectivityService.this.mTrackerHandler.obtainMessage(42, 0, this.mNetId, PrivateDnsConfig.fromParcel(config)));
        }

        /* JADX INFO: finally extract failed */
        public void showProvisioningNotification(String action, String packageName) {
            Intent intent = new Intent(action);
            intent.setPackage(packageName);
            long token = Binder.clearCallingIdentity();
            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ConnectivityService.this.mContext, 0, intent, 0);
                Binder.restoreCallingIdentity(token);
                ConnectivityService.this.mTrackerHandler.sendMessage(ConnectivityService.this.mTrackerHandler.obtainMessage(43, 1, this.mNetId, CaptivePortalInjector.getCaptivePortalPendingIntent(ConnectivityService.this.mContext, pendingIntent, this.mNaii)));
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        public void hideProvisioningNotification() {
            ConnectivityService.this.mTrackerHandler.sendMessage(ConnectivityService.this.mTrackerHandler.obtainMessage(43, 0, this.mNetId));
        }

        public int getInterfaceVersion() {
            return 3;
        }
    }

    private boolean networkRequiresPrivateDnsValidation(NetworkAgentInfo nai) {
        return NetworkMonitorUtils.isPrivateDnsValidationRequired(nai.networkCapabilities);
    }

    /* access modifiers changed from: private */
    public void handleFreshlyValidatedNetwork(NetworkAgentInfo nai) {
        if (nai != null) {
            PrivateDnsConfig cfg = this.mDnsManager.getPrivateDnsConfig();
            if (cfg.useTls && TextUtils.isEmpty(cfg.hostname)) {
                updateDnses(nai.linkProperties, (LinkProperties) null, nai.network.netId);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handlePrivateDnsSettingsChanged() {
        PrivateDnsConfig cfg = this.mDnsManager.getPrivateDnsConfig();
        for (NetworkAgentInfo nai : this.mNetworkAgentInfos.values()) {
            handlePerNetworkPrivateDnsConfig(nai, cfg);
            if (networkRequiresPrivateDnsValidation(nai)) {
                handleUpdateLinkProperties(nai, new LinkProperties(nai.linkProperties));
            }
        }
    }

    private void handlePerNetworkPrivateDnsConfig(NetworkAgentInfo nai, PrivateDnsConfig cfg) {
        if (networkRequiresPrivateDnsValidation(nai)) {
            nai.networkMonitor().notifyPrivateDnsChanged(cfg.toParcel());
            updatePrivateDns(nai, cfg);
        }
    }

    /* access modifiers changed from: private */
    public void updatePrivateDns(NetworkAgentInfo nai, PrivateDnsConfig newCfg) {
        this.mDnsManager.updatePrivateDns(nai.network, newCfg);
        updateDnses(nai.linkProperties, (LinkProperties) null, nai.network.netId);
    }

    /* access modifiers changed from: private */
    public void handlePrivateDnsValidationUpdate(DnsManager.PrivateDnsValidationUpdate update) {
        NetworkAgentInfo nai = getNetworkAgentInfoForNetId(update.netId);
        if (nai != null) {
            this.mDnsManager.updatePrivateDnsValidation(update);
            handleUpdateLinkProperties(nai, new LinkProperties(nai.linkProperties));
        }
    }

    /* access modifiers changed from: private */
    public void handleNat64PrefixEvent(int netId, boolean added, String prefixString, int prefixLength) {
        NetworkAgentInfo nai = this.mNetworkForNetId.get(netId);
        if (nai != null) {
            Object[] objArr = new Object[4];
            objArr[0] = added ? AccountManagerServiceInjector.ACCOUNT_CHANGED_ACTION_ADDED : AccountManagerServiceInjector.ACCOUNT_CHANGED_ACTION_REMOVED;
            objArr[1] = Integer.valueOf(netId);
            objArr[2] = prefixString;
            objArr[3] = Integer.valueOf(prefixLength);
            log(String.format("NAT64 prefix %s on netId %d: %s/%d", objArr));
            IpPrefix prefix = null;
            if (added) {
                try {
                    prefix = new IpPrefix(InetAddresses.parseNumericAddress(prefixString), prefixLength);
                } catch (IllegalArgumentException e) {
                    loge("Invalid NAT64 prefix " + prefixString + SliceClientPermissions.SliceAuthority.DELIMITER + prefixLength);
                    return;
                }
            }
            nai.clatd.setNat64Prefix(prefix);
            handleUpdateLinkProperties(nai, new LinkProperties(nai.linkProperties));
        }
    }

    private void updateLingerState(NetworkAgentInfo nai, long now) {
        nai.updateLingerTimer();
        if (nai.isLingering() && nai.numForegroundNetworkRequests() > 0) {
            log("Unlingering " + nai.name());
            nai.unlinger();
            logNetworkEvent(nai, 6);
        } else if (unneeded(nai, UnneededFor.LINGER) && nai.getLingerExpiry() > 0) {
            int lingerTime = (int) (nai.getLingerExpiry() - now);
            log("Lingering " + nai.name() + " for " + lingerTime + "ms");
            nai.linger();
            logNetworkEvent(nai, 5);
            notifyNetworkCallbacks(nai, 524291, lingerTime);
        }
    }

    /* access modifiers changed from: private */
    public void handleAsyncChannelHalfConnect(Message msg) {
        int serial;
        int score;
        AsyncChannel ac = (AsyncChannel) msg.obj;
        if (this.mNetworkFactoryInfos.containsKey(msg.replyTo)) {
            if (msg.arg1 == 0) {
                if (VDBG) {
                    log("NetworkFactory connected");
                }
                this.mNetworkFactoryInfos.get(msg.replyTo).asyncChannel.sendMessage(69633);
                for (NetworkRequestInfo nri : this.mNetworkRequests.values()) {
                    if (!nri.request.isListen()) {
                        NetworkAgentInfo nai = getNetworkForRequest(nri.request.requestId);
                        if (nai != null) {
                            score = nai.getCurrentScore();
                            serial = nai.factorySerialNumber;
                        } else {
                            score = 0;
                            serial = -1;
                        }
                        ac.sendMessage(536576, score, serial, nri.request);
                    }
                }
                return;
            }
            loge("Error connecting NetworkFactory");
            this.mNetworkFactoryInfos.remove(msg.obj);
        } else if (!this.mNetworkAgentInfos.containsKey(msg.replyTo)) {
        } else {
            if (msg.arg1 == 0) {
                if (VDBG) {
                    log("NetworkAgent connected");
                }
                this.mNetworkAgentInfos.get(msg.replyTo).asyncChannel.sendMessage(69633);
                return;
            }
            loge("Error connecting NetworkAgent");
            NetworkAgentInfo nai2 = this.mNetworkAgentInfos.remove(msg.replyTo);
            if (nai2 != null) {
                boolean wasDefault = isDefaultNetwork(nai2);
                synchronized (this.mNetworkForNetId) {
                    this.mNetworkForNetId.remove(nai2.network.netId);
                    this.mNetIdInUse.delete(nai2.network.netId);
                }
                this.mLegacyTypeTracker.remove(nai2, wasDefault);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleAsyncChannelDisconnected(Message msg) {
        NetworkAgentInfo nai = this.mNetworkAgentInfos.get(msg.replyTo);
        if (nai != null) {
            disconnectAndDestroyNetwork(nai);
            return;
        }
        NetworkFactoryInfo nfi = this.mNetworkFactoryInfos.remove(msg.replyTo);
        if (nfi != null) {
            log("unregisterNetworkFactory for " + nfi.name);
        }
    }

    private void disconnectAndDestroyNetwork(NetworkAgentInfo nai) {
        log(nai.name() + " got DISCONNECTED, was satisfying " + nai.numNetworkRequests());
        this.mNotifier.clearNotification(nai.network.netId);
        if (nai.networkInfo.isConnected()) {
            nai.networkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, (String) null, (String) null);
        }
        boolean wasDefault = isDefaultNetwork(nai);
        if (wasDefault) {
            this.mDefaultInetConditionPublished = 0;
            metricsLogger().defaultNetworkMetrics().logDefaultNetworkEvent(SystemClock.elapsedRealtime(), (NetworkAgentInfo) null, nai);
        }
        notifyIfacesChangedForNetworkStats();
        notifyNetworkCallbacks(nai, 524292);
        this.mKeepaliveTracker.handleStopAllKeepalives(nai, -20);
        for (String iface : nai.linkProperties.getAllInterfaceNames()) {
            wakeupModifyInterface(iface, nai.networkCapabilities, false);
        }
        nai.networkMonitor().notifyNetworkDisconnected();
        this.mNetworkAgentInfos.remove(nai.messenger);
        nai.clatd.update();
        synchronized (this.mNetworkForNetId) {
            this.mNetworkForNetId.remove(nai.network.netId);
        }
        for (int i = 0; i < nai.numNetworkRequests(); i++) {
            NetworkRequest request = nai.requestAt(i);
            NetworkAgentInfo currentNetwork = getNetworkForRequest(request.requestId);
            if (currentNetwork != null && currentNetwork.network.netId == nai.network.netId) {
                clearNetworkForRequest(request.requestId);
                sendUpdatedScoreToFactories(request, (NetworkAgentInfo) null);
            }
        }
        nai.clearLingerState();
        if (nai.isSatisfyingRequest(this.mDefaultRequest.requestId)) {
            updateDataActivityTracking((NetworkAgentInfo) null, nai);
            notifyLockdownVpn(nai);
            ensureNetworkTransitionWakelock(nai.name());
        }
        this.mLegacyTypeTracker.remove(nai, wasDefault);
        if (!nai.networkCapabilities.hasTransport(4)) {
            updateAllVpnsCapabilities();
        }
        rematchAllNetworksAndRequests((NetworkAgentInfo) null, 0);
        this.mLingerMonitor.noteDisconnect(nai);
        WifiAssistant.get().maybeClearNotification(nai.network.netId);
        if (nai.created) {
            destroyNativeNetwork(nai);
            this.mDnsManager.removeNetwork(nai.network);
        }
        synchronized (this.mNetworkForNetId) {
            this.mNetIdInUse.delete(nai.network.netId);
        }
    }

    private boolean createNativeNetwork(NetworkAgentInfo networkAgent) {
        boolean z;
        try {
            if (networkAgent.isVPN()) {
                INetd iNetd = this.mNetd;
                int i = networkAgent.network.netId;
                if (networkAgent.networkMisc != null) {
                    if (networkAgent.networkMisc.allowBypass) {
                        z = false;
                        iNetd.networkCreateVpn(i, z);
                    }
                }
                z = true;
                iNetd.networkCreateVpn(i, z);
            } else {
                this.mNetd.networkCreatePhysical(networkAgent.network.netId, getNetworkPermission(networkAgent.networkCapabilities));
            }
            this.mDnsResolver.createNetworkCache(networkAgent.network.netId);
            return true;
        } catch (RemoteException | ServiceSpecificException e) {
            loge("Error creating network " + networkAgent.network.netId + ": " + e.getMessage());
            return false;
        }
    }

    private void destroyNativeNetwork(NetworkAgentInfo networkAgent) {
        try {
            this.mNetd.networkDestroy(networkAgent.network.netId);
            this.mDnsResolver.destroyNetworkCache(networkAgent.network.netId);
        } catch (RemoteException | ServiceSpecificException e) {
            loge("Exception destroying network: " + e);
        }
    }

    private NetworkRequestInfo findExistingNetworkRequestInfo(PendingIntent pendingIntent) {
        Intent intent = pendingIntent.getIntent();
        for (Map.Entry<NetworkRequest, NetworkRequestInfo> entry : this.mNetworkRequests.entrySet()) {
            PendingIntent existingPendingIntent = entry.getValue().mPendingIntent;
            if (existingPendingIntent != null && existingPendingIntent.getIntent().filterEquals(intent)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void handleRegisterNetworkRequestWithIntent(Message msg) {
        NetworkRequestInfo nri = (NetworkRequestInfo) msg.obj;
        NetworkRequestInfo existingRequest = findExistingNetworkRequestInfo(nri.mPendingIntent);
        if (existingRequest != null) {
            log("Replacing " + existingRequest.request + " with " + nri.request + " because their intents matched.");
            handleReleaseNetworkRequest(existingRequest.request, getCallingUid(), false);
        }
        handleRegisterNetworkRequest(nri);
    }

    /* access modifiers changed from: private */
    public void handleRegisterNetworkRequest(NetworkRequestInfo nri) {
        this.mNetworkRequests.put(nri.request, nri);
        LocalLog localLog = this.mNetworkRequestInfoLogs;
        localLog.log("REGISTER " + nri);
        if (nri.request.isListen()) {
            for (NetworkAgentInfo network : this.mNetworkAgentInfos.values()) {
                if (nri.request.networkCapabilities.hasSignalStrength() && network.satisfiesImmutableCapabilitiesOf(nri.request)) {
                    updateSignalStrengthThresholds(network, "REGISTER", nri.request);
                }
            }
        }
        rematchAllNetworksAndRequests((NetworkAgentInfo) null, 0);
        if (nri.request.isRequest() && getNetworkForRequest(nri.request.requestId) == null) {
            sendUpdatedScoreToFactories(nri.request, (NetworkAgentInfo) null);
        }
    }

    /* access modifiers changed from: private */
    public void handleReleaseNetworkRequestWithIntent(PendingIntent pendingIntent, int callingUid) {
        NetworkRequestInfo nri = findExistingNetworkRequestInfo(pendingIntent);
        if (nri != null) {
            handleReleaseNetworkRequest(nri.request, callingUid, false);
        }
    }

    private boolean unneeded(NetworkAgentInfo nai, UnneededFor reason) {
        int numRequests;
        int i = AnonymousClass9.$SwitchMap$com$android$server$ConnectivityService$UnneededFor[reason.ordinal()];
        if (i == 1) {
            numRequests = nai.numRequestNetworkRequests();
        } else if (i != 2) {
            Slog.wtf(TAG, "Invalid reason. Cannot happen.");
            return true;
        } else {
            numRequests = nai.numForegroundNetworkRequests();
        }
        if (!nai.everConnected || nai.isVPN() || nai.isLingering() || numRequests > 0) {
            return false;
        }
        for (NetworkRequestInfo nri : this.mNetworkRequests.values()) {
            if ((reason != UnneededFor.LINGER || !nri.request.isBackgroundRequest()) && nri.request.isRequest() && nai.satisfies(nri.request) && satisfiesMobileMultiNetworkDataCheck(nai.networkCapabilities, nri.request.networkCapabilities)) {
                if (nai.isSatisfyingRequest(nri.request.requestId) || (getNetworkForRequest(nri.request.requestId) != null && getNetworkForRequest(nri.request.requestId).getCurrentScore() < nai.getCurrentScoreAsValidated())) {
                    return false;
                }
            }
        }
        return true;
    }

    private NetworkRequestInfo getNriForAppRequest(NetworkRequest request, int callingUid, String requestedOperation) {
        NetworkRequestInfo nri = this.mNetworkRequests.get(request);
        if (nri == null || 1000 == callingUid || nri.mUid == callingUid) {
            return nri;
        }
        log(String.format("UID %d attempted to %s for unowned request %s", new Object[]{Integer.valueOf(callingUid), requestedOperation, nri}));
        return null;
    }

    /* access modifiers changed from: private */
    public void handleTimedOutNetworkRequest(NetworkRequestInfo nri) {
        if (this.mNetworkRequests.get(nri.request) != null && getNetworkForRequest(nri.request.requestId) == null) {
            if (VDBG || nri.request.isRequest()) {
                log("releasing " + nri.request + " (timeout)");
            }
            handleRemoveNetworkRequest(nri);
            callCallbackForRequest(nri, (NetworkAgentInfo) null, 524293, 0);
        }
    }

    /* access modifiers changed from: private */
    public void handleReleaseNetworkRequest(NetworkRequest request, int callingUid, boolean callOnUnavailable) {
        NetworkRequestInfo nri = getNriForAppRequest(request, callingUid, "release NetworkRequest");
        if (nri != null) {
            if (VDBG || nri.request.isRequest()) {
                log("releasing " + nri.request + " (release request)");
            }
            handleRemoveNetworkRequest(nri);
            if (callOnUnavailable) {
                callCallbackForRequest(nri, (NetworkAgentInfo) null, 524293, 0);
            }
        }
    }

    private void handleRemoveNetworkRequest(NetworkRequestInfo nri) {
        nri.unlinkDeathRecipient();
        this.mNetworkRequests.remove(nri.request);
        synchronized (this.mUidToNetworkRequestCount) {
            int requests = this.mUidToNetworkRequestCount.get(nri.mUid, 0);
            if (requests < 1) {
                String str = TAG;
                Slog.wtf(str, "BUG: too small request count " + requests + " for UID " + nri.mUid);
            } else if (requests == 1) {
                this.mUidToNetworkRequestCount.removeAt(this.mUidToNetworkRequestCount.indexOfKey(nri.mUid));
            } else {
                this.mUidToNetworkRequestCount.put(nri.mUid, requests - 1);
            }
        }
        LocalLog localLog = this.mNetworkRequestInfoLogs;
        localLog.log("RELEASE " + nri);
        if (nri.request.isRequest()) {
            boolean wasKept = false;
            NetworkAgentInfo nai = getNetworkForRequest(nri.request.requestId);
            if (nai != null) {
                boolean wasBackgroundNetwork = nai.isBackgroundNetwork();
                nai.removeRequest(nri.request.requestId);
                if (VDBG || DDBG) {
                    log(" Removing from current network " + nai.name() + ", leaving " + nai.numNetworkRequests() + " requests.");
                }
                updateLingerState(nai, SystemClock.elapsedRealtime());
                if (unneeded(nai, UnneededFor.TEARDOWN)) {
                    log("no live requests for " + nai.name() + "; disconnecting");
                    teardownUnneededNetwork(nai);
                } else {
                    wasKept = true;
                }
                clearNetworkForRequest(nri.request.requestId);
                if (!wasBackgroundNetwork && nai.isBackgroundNetwork()) {
                    updateCapabilities(nai.getCurrentScore(), nai, nai.networkCapabilities);
                }
            }
            if (!(nri.request.legacyType == -1 || nai == null)) {
                boolean doRemove = true;
                if (wasKept) {
                    for (int i = 0; i < nai.numNetworkRequests(); i++) {
                        NetworkRequest otherRequest = nai.requestAt(i);
                        if (otherRequest.legacyType == nri.request.legacyType && otherRequest.isRequest()) {
                            log(" still have other legacy request - leaving");
                            doRemove = false;
                        }
                    }
                }
                if (doRemove) {
                    this.mLegacyTypeTracker.remove(nri.request.legacyType, nai, false);
                }
            }
            for (NetworkFactoryInfo nfi : this.mNetworkFactoryInfos.values()) {
                nfi.asyncChannel.sendMessage(536577, nri.request);
            }
            return;
        }
        for (NetworkAgentInfo nai2 : this.mNetworkAgentInfos.values()) {
            nai2.removeRequest(nri.request.requestId);
            if (nri.request.networkCapabilities.hasSignalStrength() && nai2.satisfiesImmutableCapabilitiesOf(nri.request)) {
                updateSignalStrengthThresholds(nai2, "RELEASE", nri.request);
            }
        }
    }

    public void setAcceptUnvalidated(Network network, boolean accept, boolean always) {
        enforceNetworkStackSettingsOrSetup();
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(28, encodeBool(accept), encodeBool(always), network));
    }

    public void setAcceptPartialConnectivity(Network network, boolean accept, boolean always) {
        enforceNetworkStackSettingsOrSetup();
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(45, encodeBool(accept), encodeBool(always), network));
    }

    public void setAvoidUnvalidated(Network network) {
        enforceNetworkStackSettingsOrSetup();
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(35, network));
    }

    /* access modifiers changed from: private */
    public void handleSetAcceptUnvalidated(Network network, boolean accept, boolean always) {
        log("handleSetAcceptUnvalidated network=" + network + " accept=" + accept + " always=" + always);
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        if (nai != null) {
            if (accept && nai.avoidUnvalidated) {
                nai.avoidUnvalidated = false;
            }
            if (!nai.networkMisc.explicitlySelected) {
                Slog.wtf(TAG, "BUG: setAcceptUnvalidated non non-explicitly selected network");
            }
            if (accept != nai.networkMisc.acceptUnvalidated) {
                int oldScore = nai.getCurrentScore();
                nai.networkMisc.acceptUnvalidated = accept;
                nai.networkMisc.keepScore = accept;
                nai.networkMisc.acceptPartialConnectivity = accept;
                rematchAllNetworksAndRequests(nai, oldScore);
                sendUpdatedScoreToFactories(nai);
            }
            if (always) {
                nai.asyncChannel.sendMessage(528393, encodeBool(accept));
            }
            if (!accept) {
                nai.asyncChannel.sendMessage(528399);
                teardownUnneededNetwork(nai);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleSetAcceptPartialConnectivity(Network network, boolean accept, boolean always) {
        log("handleSetAcceptPartialConnectivity network=" + network + " accept=" + accept + " always=" + always);
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        if (nai != null && !nai.lastValidated) {
            if (accept != nai.networkMisc.acceptPartialConnectivity) {
                nai.networkMisc.acceptPartialConnectivity = accept;
            }
            if (always) {
                nai.asyncChannel.sendMessage(528393, encodeBool(accept));
            }
            if (!accept) {
                nai.asyncChannel.sendMessage(528399);
                teardownUnneededNetwork(nai);
                return;
            }
            nai.networkMonitor().setAcceptPartialConnectivity();
        }
    }

    /* access modifiers changed from: private */
    public void handleSetAvoidUnvalidated(Network network) {
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        if (nai != null && !nai.lastValidated) {
            if (nai.networkMisc.acceptUnvalidated) {
                nai.networkMisc.acceptUnvalidated = false;
            }
            nai.networkMisc.keepScore = false;
            if (!nai.avoidUnvalidated) {
                int oldScore = nai.getCurrentScore();
                nai.avoidUnvalidated = true;
                rematchAllNetworksAndRequests(nai, oldScore);
                sendUpdatedScoreToFactories(nai);
            }
        }
    }

    private void scheduleUnvalidatedPrompt(NetworkAgentInfo nai) {
        if (VDBG) {
            log("scheduleUnvalidatedPrompt " + nai.network);
        }
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessageDelayed(internalHandler.obtainMessage(29, nai.network), 8000);
    }

    public void startCaptivePortalApp(Network network) {
        enforceConnectivityInternalPermission();
        this.mHandler.post(new Runnable(network) {
            private final /* synthetic */ Network f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ConnectivityService.this.lambda$startCaptivePortalApp$3$ConnectivityService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$startCaptivePortalApp$3$ConnectivityService(Network network) {
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        if (nai != null && nai.networkCapabilities.hasCapability(17)) {
            nai.networkMonitor().launchCaptivePortalApp();
        }
    }

    public void startCaptivePortalAppInternal(Network network, Bundle appExtras) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MAINLINE_NETWORK_STACK", "ConnectivityService");
        Intent appIntent = new Intent("android.net.conn.CAPTIVE_PORTAL");
        appIntent.putExtras(appExtras);
        appIntent.putExtra("android.net.extra.CAPTIVE_PORTAL", new CaptivePortal(new CaptivePortalImpl(network).asBinder()));
        appIntent.setFlags(272629760);
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        if (nai != null) {
            nai.captivePortalValidationPending = true;
        }
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable(appIntent) {
            private final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void runOrThrow() {
                ConnectivityService.this.lambda$startCaptivePortalAppInternal$4$ConnectivityService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$startCaptivePortalAppInternal$4$ConnectivityService(Intent appIntent) throws Exception {
        this.mContext.startActivityAsUser(appIntent, UserHandle.CURRENT);
    }

    private class CaptivePortalImpl extends ICaptivePortal.Stub {
        private final Network mNetwork;

        private CaptivePortalImpl(Network network) {
            this.mNetwork = network;
        }

        public void appResponse(int response) {
            NetworkMonitorManager nm;
            if (response == 2) {
                ConnectivityService.this.enforceSettingsPermission();
            }
            NetworkAgentInfo nai = ConnectivityService.this.getNetworkAgentInfoForNetwork(this.mNetwork);
            if (nai != null && (nm = nai.networkMonitor()) != null) {
                nm.notifyCaptivePortalAppFinished(response);
            }
        }

        public void logEvent(int eventId, String packageName) {
            ConnectivityService.this.enforceSettingsPermission();
            new MetricsLogger().action(eventId, packageName);
        }
    }

    public boolean avoidBadWifi() {
        return this.mMultinetworkPolicyTracker.getAvoidBadWifi();
    }

    public boolean shouldAvoidBadWifi() {
        if (checkNetworkStackPermission()) {
            return avoidBadWifi();
        }
        throw new SecurityException("avoidBadWifi requires NETWORK_STACK permission");
    }

    /* access modifiers changed from: private */
    /* renamed from: rematchForAvoidBadWifiUpdate */
    public void lambda$new$0$ConnectivityService() {
        rematchAllNetworksAndRequests((NetworkAgentInfo) null, 0);
        for (NetworkAgentInfo nai : this.mNetworkAgentInfos.values()) {
            if (nai.networkCapabilities.hasTransport(1)) {
                sendUpdatedScoreToFactories(nai);
            }
        }
    }

    private void dumpAvoidBadWifiSettings(IndentingPrintWriter pw) {
        String description;
        boolean configRestrict = this.mMultinetworkPolicyTracker.configRestrictsAvoidBadWifi();
        if (!configRestrict) {
            pw.println("Bad Wi-Fi avoidance: unrestricted");
            return;
        }
        pw.println("Bad Wi-Fi avoidance: " + avoidBadWifi());
        pw.increaseIndent();
        pw.println("Config restrict:   " + configRestrict);
        String value = this.mMultinetworkPolicyTracker.getAvoidBadWifiSetting();
        if ("0".equals(value)) {
            description = "get stuck";
        } else if (value == null) {
            description = "prompt";
        } else if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(value)) {
            description = "avoid";
        } else {
            description = value + " (?)";
        }
        pw.println("User setting:      " + description);
        pw.println("Network overrides:");
        pw.increaseIndent();
        for (NetworkAgentInfo nai : networksSortedById()) {
            if (nai.avoidUnvalidated) {
                pw.println(nai.name());
            }
        }
        pw.decreaseIndent();
        pw.decreaseIndent();
    }

    /* renamed from: com.android.server.ConnectivityService$9  reason: invalid class name */
    static /* synthetic */ class AnonymousClass9 {
        static final /* synthetic */ int[] $SwitchMap$com$android$server$ConnectivityService$UnneededFor = new int[UnneededFor.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$android$server$connectivity$NetworkNotificationManager$NotificationType = new int[NetworkNotificationManager.NotificationType.values().length];

        static {
            try {
                $SwitchMap$com$android$server$connectivity$NetworkNotificationManager$NotificationType[NetworkNotificationManager.NotificationType.LOGGED_IN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$server$connectivity$NetworkNotificationManager$NotificationType[NetworkNotificationManager.NotificationType.NO_INTERNET.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$server$connectivity$NetworkNotificationManager$NotificationType[NetworkNotificationManager.NotificationType.LOST_INTERNET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$server$connectivity$NetworkNotificationManager$NotificationType[NetworkNotificationManager.NotificationType.PARTIAL_CONNECTIVITY.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$android$server$ConnectivityService$UnneededFor[UnneededFor.TEARDOWN.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$android$server$ConnectivityService$UnneededFor[UnneededFor.LINGER.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void showNetworkNotification(NetworkAgentInfo nai, NetworkNotificationManager.NotificationType type) {
        boolean highPriority;
        String action;
        int i = AnonymousClass9.$SwitchMap$com$android$server$connectivity$NetworkNotificationManager$NotificationType[type.ordinal()];
        if (i == 1) {
            action = "android.settings.WIFI_SETTINGS";
            this.mHandler.removeMessages(44);
            InternalHandler internalHandler = this.mHandler;
            internalHandler.sendMessageDelayed(internalHandler.obtainMessage(44, nai.network.netId, 0), ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION);
            highPriority = true;
        } else if (i == 2) {
            action = "android.net.conn.PROMPT_UNVALIDATED";
            highPriority = true;
        } else if (i == 3) {
            action = "android.net.conn.PROMPT_LOST_VALIDATION";
            highPriority = true;
        } else if (i != 4) {
            Slog.wtf(TAG, "Unknown notification type " + type);
            return;
        } else {
            action = "android.net.conn.PROMPT_PARTIAL_CONNECTIVITY";
            highPriority = nai.networkMisc.explicitlySelected;
        }
        Intent intent = new Intent(action);
        if (type != NetworkNotificationManager.NotificationType.LOGGED_IN) {
            intent.setData(Uri.fromParts("netId", Integer.toString(nai.network.netId), (String) null));
            intent.addFlags(268435456);
            intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiNoInternetDialog");
        }
        this.mNotifier.showNotification(nai.network.netId, type, nai, (NetworkAgentInfo) null, PendingIntent.getActivityAsUser(this.mContext, 0, intent, 268435456, (Bundle) null, UserHandle.CURRENT), highPriority);
    }

    private boolean shouldPromptUnvalidated(NetworkAgentInfo nai) {
        if (nai.everValidated || nai.everCaptivePortalDetected || WifiAssistant.get().handleNetworkNoInternet(nai)) {
            return false;
        }
        if (nai.partialConnectivity && !nai.networkMisc.acceptPartialConnectivity) {
            return true;
        }
        if (!nai.networkMisc.explicitlySelected || nai.networkMisc.acceptUnvalidated) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void handlePromptUnvalidated(Network network) {
        if (VDBG || DDBG) {
            log("handlePromptUnvalidated " + network);
        }
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        if (nai != null && shouldPromptUnvalidated(nai)) {
            nai.asyncChannel.sendMessage(528399);
            if (nai.partialConnectivity) {
                showNetworkNotification(nai, NetworkNotificationManager.NotificationType.PARTIAL_CONNECTIVITY);
            } else {
                showNetworkNotification(nai, NetworkNotificationManager.NotificationType.NO_INTERNET);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleNetworkUnvalidated(NetworkAgentInfo nai) {
        NetworkCapabilities nc = nai.networkCapabilities;
        log("handleNetworkUnvalidated " + nai.name() + " cap=" + nc);
        if (nc.hasTransport(1) && this.mMultinetworkPolicyTracker.shouldNotifyWifiUnvalidated()) {
            showNetworkNotification(nai, NetworkNotificationManager.NotificationType.LOST_INTERNET);
        }
    }

    public int getMultipathPreference(Network network) {
        enforceAccessPermission();
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        if (nai != null && nai.networkCapabilities.hasCapability(11)) {
            return 7;
        }
        Integer networkPreference = this.mMultipathPolicyTracker.getMultipathPreference(network);
        if (networkPreference != null) {
            return networkPreference.intValue();
        }
        return this.mMultinetworkPolicyTracker.getMeteredMultipathPreference();
    }

    public NetworkRequest getDefaultRequest() {
        return this.mDefaultRequest;
    }

    private class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 8) {
                if (i == 9) {
                    ConnectivityService.this.mProxyTracker.loadDeprecatedGlobalHttpProxy();
                    return;
                } else if (i == 44) {
                    ConnectivityService.this.mNotifier.clearNotification(msg.arg1, NetworkNotificationManager.NotificationType.LOGGED_IN);
                    return;
                } else if (i == 45) {
                    ConnectivityService.this.handleSetAcceptPartialConnectivity((Network) msg.obj, ConnectivityService.toBool(msg.arg1), ConnectivityService.toBool(msg.arg2));
                    return;
                } else if (i == 128) {
                    for (NetworkAgentInfo nai : ConnectivityService.this.mNetworkAgentInfos.values()) {
                        ConnectivityService.this.teardownUnneededNetwork(nai);
                    }
                    return;
                } else if (i == 160) {
                    ConnectivityService.this.handleUpdateTCPBuffersfor5G();
                    return;
                } else if (i != ConnectivityService.EVENT_UPDATE_ACTIVE_DATA_SUBID) {
                    switch (i) {
                        case 16:
                            ConnectivityService.this.handleApplyDefaultProxy((ProxyInfo) msg.obj);
                            return;
                        case 17:
                            ConnectivityService.this.handleRegisterNetworkFactory((NetworkFactoryInfo) msg.obj);
                            return;
                        case 18:
                            Pair<NetworkAgentInfo, INetworkMonitor> arg = (Pair) msg.obj;
                            ConnectivityService.this.handleRegisterNetworkAgent((NetworkAgentInfo) arg.first, (INetworkMonitor) arg.second);
                            return;
                        case 19:
                        case 21:
                            ConnectivityService.this.handleRegisterNetworkRequest((NetworkRequestInfo) msg.obj);
                            return;
                        case 20:
                            ConnectivityService.this.handleTimedOutNetworkRequest((NetworkRequestInfo) msg.obj);
                            return;
                        case 22:
                            ConnectivityService.this.handleReleaseNetworkRequest((NetworkRequest) msg.obj, msg.arg1, false);
                            return;
                        case 23:
                            ConnectivityService.this.handleUnregisterNetworkFactory((Messenger) msg.obj);
                            return;
                        case 24:
                            break;
                        case 25:
                            ConnectivityService.this.mMultipathPolicyTracker.start();
                            NetPluginDelegate.registerHandler(ConnectivityService.this.mHandler);
                            return;
                        case ConnectivityService.EVENT_REGISTER_NETWORK_REQUEST_WITH_INTENT /*26*/:
                        case 31:
                            ConnectivityService.this.handleRegisterNetworkRequestWithIntent(msg);
                            return;
                        case ConnectivityService.EVENT_RELEASE_NETWORK_REQUEST_WITH_INTENT /*27*/:
                            ConnectivityService.this.handleReleaseNetworkRequestWithIntent((PendingIntent) msg.obj, msg.arg1);
                            return;
                        case 28:
                            ConnectivityService.this.handleSetAcceptUnvalidated((Network) msg.obj, ConnectivityService.toBool(msg.arg1), ConnectivityService.toBool(msg.arg2));
                            return;
                        case 29:
                            ConnectivityService.this.handlePromptUnvalidated((Network) msg.obj);
                            return;
                        case 30:
                            ConnectivityService.this.handleConfigureAlwaysOnNetworks();
                            return;
                        default:
                            switch (i) {
                                case 35:
                                    ConnectivityService.this.handleSetAvoidUnvalidated((Network) msg.obj);
                                    return;
                                case 36:
                                    ConnectivityService.this.handleReportNetworkConnectivity((Network) msg.obj, msg.arg1, ConnectivityService.toBool(msg.arg2));
                                    return;
                                case 37:
                                    ConnectivityService.this.handlePrivateDnsSettingsChanged();
                                    return;
                                case 38:
                                    ConnectivityService.this.handlePrivateDnsValidationUpdate((DnsManager.PrivateDnsValidationUpdate) msg.obj);
                                    return;
                                case 39:
                                    ConnectivityService.this.handleUidRulesChanged(msg.arg1, msg.arg2);
                                    return;
                                case 40:
                                    ConnectivityService.this.handleRestrictBackgroundChanged(ConnectivityService.toBool(msg.arg1));
                                    return;
                                default:
                                    switch (i) {
                                        case 528395:
                                            ConnectivityService.this.mKeepaliveTracker.handleStartKeepalive(msg);
                                            return;
                                        case 528396:
                                            ConnectivityService.this.mKeepaliveTracker.handleStopKeepalive(ConnectivityService.this.getNetworkAgentInfoForNetwork((Network) msg.obj), msg.arg1, msg.arg2);
                                            return;
                                        default:
                                            return;
                                    }
                            }
                    }
                } else {
                    ConnectivityService.this.handleUpdateActiveDataSubId(msg.arg1);
                    return;
                }
            }
            ConnectivityService.this.handleReleaseNetworkTransitionWakelock(msg.what);
        }
    }

    public int tether(String iface, String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        if (isTetheringSupported()) {
            return this.mTethering.tether(iface);
        }
        return 3;
    }

    public int untether(String iface, String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        if (isTetheringSupported()) {
            return this.mTethering.untether(iface);
        }
        return 3;
    }

    public int getLastTetherError(String iface) {
        enforceTetherAccessPermission();
        if (isTetheringSupported()) {
            return this.mTethering.getLastTetherError(iface);
        }
        return 3;
    }

    public String[] getTetherableUsbRegexs() {
        enforceTetherAccessPermission();
        if (isTetheringSupported()) {
            return this.mTethering.getTetherableUsbRegexs();
        }
        return new String[0];
    }

    public String[] getTetherableWifiRegexs() {
        enforceTetherAccessPermission();
        if (isTetheringSupported()) {
            return this.mTethering.getTetherableWifiRegexs();
        }
        return new String[0];
    }

    public String[] getTetherableBluetoothRegexs() {
        enforceTetherAccessPermission();
        if (isTetheringSupported()) {
            return this.mTethering.getTetherableBluetoothRegexs();
        }
        return new String[0];
    }

    public int setUsbTethering(boolean enable, String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        if (isTetheringSupported()) {
            return this.mTethering.setUsbTethering(enable);
        }
        return 3;
    }

    public String[] getTetherableIfaces() {
        enforceTetherAccessPermission();
        return this.mTethering.getTetherableIfaces();
    }

    public String[] getTetheredIfaces() {
        enforceTetherAccessPermission();
        return this.mTethering.getTetheredIfaces();
    }

    public String[] getTetheringErroredIfaces() {
        enforceTetherAccessPermission();
        return this.mTethering.getErroredIfaces();
    }

    public String[] getTetheredDhcpRanges() {
        enforceConnectivityInternalPermission();
        return this.mTethering.getTetheredDhcpRanges();
    }

    public boolean isTetheringSupported(String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        return isTetheringSupported();
    }

    /* access modifiers changed from: private */
    public boolean isTetheringSupported() {
        boolean tetherEnabledInSettings = toBool(Settings.Global.getInt(this.mContext.getContentResolver(), "tether_supported", encodeBool(this.mSystemProperties.get("ro.tether.denied").equals("true") ^ true))) && !this.mUserManager.hasUserRestriction("no_config_tethering");
        long token = Binder.clearCallingIdentity();
        try {
            boolean adminUser = this.mUserManager.isAdminUser();
            if (!tetherEnabledInSettings || !adminUser || !this.mTethering.hasTetherableConfiguration()) {
                return false;
            }
            return true;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void startTethering(int type, ResultReceiver receiver, boolean showProvisioningUi, String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        if (!isTetheringSupported()) {
            receiver.send(3, (Bundle) null);
        } else {
            this.mTethering.startTethering(type, receiver, showProvisioningUi);
        }
    }

    public void stopTethering(int type, String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        this.mTethering.stopTethering(type);
    }

    public void getLatestTetheringEntitlementResult(int type, ResultReceiver receiver, boolean showEntitlementUi, String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        this.mTethering.getLatestTetheringEntitlementResult(type, receiver, showEntitlementUi);
    }

    public void registerTetheringEventCallback(ITetheringEventCallback callback, String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        this.mTethering.registerTetheringEventCallback(callback);
    }

    public void unregisterTetheringEventCallback(ITetheringEventCallback callback, String callerPkg) {
        ConnectivityManager.enforceTetherChangePermission(this.mContext, callerPkg);
        this.mTethering.unregisterTetheringEventCallback(callback);
    }

    private void ensureNetworkTransitionWakelock(String forWhom) {
        synchronized (this) {
            if (!this.mNetTransitionWakeLock.isHeld()) {
                this.mNetTransitionWakeLock.acquire();
                this.mLastWakeLockAcquireTimestamp = SystemClock.elapsedRealtime();
                this.mTotalWakelockAcquisitions++;
                this.mWakelockLogs.log("ACQUIRE for " + forWhom);
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(24), (long) this.mNetTransitionWakeLockTimeout);
            }
        }
    }

    private void scheduleReleaseNetworkTransitionWakelock() {
        synchronized (this) {
            if (this.mNetTransitionWakeLock.isHeld()) {
                this.mHandler.removeMessages(24);
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(8), 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleReleaseNetworkTransitionWakelock(int eventId) {
        String event = eventName(eventId);
        synchronized (this) {
            if (!this.mNetTransitionWakeLock.isHeld()) {
                this.mWakelockLogs.log(String.format("RELEASE: already released (%s)", new Object[]{event}));
                Slog.w(TAG, "expected Net Transition WakeLock to be held");
                return;
            }
            this.mNetTransitionWakeLock.release();
            long lockDuration = SystemClock.elapsedRealtime() - this.mLastWakeLockAcquireTimestamp;
            this.mTotalWakelockDurationMs += lockDuration;
            this.mMaxWakelockDurationMs = Math.max(this.mMaxWakelockDurationMs, lockDuration);
            this.mTotalWakelockReleases++;
            this.mWakelockLogs.log(String.format("RELEASE (%s)", new Object[]{event}));
        }
    }

    public void reportInetCondition(int networkType, int percentage) {
        NetworkAgentInfo nai = this.mLegacyTypeTracker.getNetworkForType(networkType);
        if (nai != null) {
            reportNetworkConnectivity(nai.network, percentage > 50);
        }
    }

    public void reportNetworkConnectivity(Network network, boolean hasConnectivity) {
        enforceAccessPermission();
        enforceInternetPermission();
        int uid = Binder.getCallingUid();
        int connectivityInfo = encodeBool(hasConnectivity);
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(36, uid, connectivityInfo, network));
    }

    /* access modifiers changed from: private */
    public void handleReportNetworkConnectivity(Network network, int uid, boolean hasConnectivity) {
        NetworkAgentInfo nai;
        if (network == null) {
            nai = getDefaultNetwork();
        } else {
            nai = getNetworkAgentInfoForNetwork(network);
        }
        if (nai != null && nai.networkInfo.getState() != NetworkInfo.State.DISCONNECTING && nai.networkInfo.getState() != NetworkInfo.State.DISCONNECTED && hasConnectivity != nai.lastValidated) {
            int netid = nai.network.netId;
            log("reportNetworkConnectivity(" + netid + ", " + hasConnectivity + ") by " + uid);
            if (nai.everConnected != 0 && !isNetworkWithLinkPropertiesBlocked(getLinkProperties(nai), uid, false)) {
                nai.networkMonitor().forceReevaluation(uid);
            }
        }
    }

    public ProxyInfo getProxyForNetwork(Network network) {
        ProxyInfo globalProxy = this.mProxyTracker.getGlobalProxy();
        if (globalProxy != null) {
            return globalProxy;
        }
        if (network == null) {
            Network activeNetwork = getActiveNetworkForUidInternal(Binder.getCallingUid(), true);
            if (activeNetwork == null) {
                return null;
            }
            return getLinkPropertiesProxyInfo(activeNetwork);
        } else if (queryUserAccess(Binder.getCallingUid(), network.netId)) {
            return getLinkPropertiesProxyInfo(network);
        } else {
            return null;
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean queryUserAccess(int uid, int netId) {
        return NetworkUtils.queryUserAccess(uid, netId);
    }

    private ProxyInfo getLinkPropertiesProxyInfo(Network network) {
        NetworkAgentInfo nai = getNetworkAgentInfoForNetwork(network);
        ProxyInfo proxyInfo = null;
        if (nai == null) {
            return null;
        }
        synchronized (nai) {
            ProxyInfo linkHttpProxy = nai.linkProperties.getHttpProxy();
            if (linkHttpProxy != null) {
                proxyInfo = new ProxyInfo(linkHttpProxy);
            }
        }
        return proxyInfo;
    }

    public void setGlobalProxy(ProxyInfo proxyProperties) {
        enforceConnectivityInternalPermission();
        this.mProxyTracker.setGlobalProxy(proxyProperties);
    }

    public ProxyInfo getGlobalProxy() {
        return this.mProxyTracker.getGlobalProxy();
    }

    /* access modifiers changed from: private */
    public void handleApplyDefaultProxy(ProxyInfo proxy) {
        if (proxy != null && TextUtils.isEmpty(proxy.getHost()) && Uri.EMPTY.equals(proxy.getPacFileUrl())) {
            proxy = null;
        }
        this.mProxyTracker.setDefaultProxy(proxy);
    }

    private void updateProxy(LinkProperties newLp, LinkProperties oldLp) {
        ProxyInfo oldProxyInfo = null;
        ProxyInfo newProxyInfo = newLp == null ? null : newLp.getHttpProxy();
        if (oldLp != null) {
            oldProxyInfo = oldLp.getHttpProxy();
        }
        if (!ProxyTracker.proxyInfoEqual(newProxyInfo, oldProxyInfo)) {
            this.mProxyTracker.sendProxyBroadcast();
        }
    }

    private static class SettingsObserver extends ContentObserver {
        private final Context mContext;
        private final Handler mHandler;
        private final HashMap<Uri, Integer> mUriEventMap = new HashMap<>();

        SettingsObserver(Context context, Handler handler) {
            super((Handler) null);
            this.mContext = context;
            this.mHandler = handler;
        }

        /* access modifiers changed from: package-private */
        public void observe(Uri uri, int what) {
            this.mUriEventMap.put(uri, Integer.valueOf(what));
            this.mContext.getContentResolver().registerContentObserver(uri, false, this);
        }

        public void onChange(boolean selfChange) {
            Slog.wtf(ConnectivityService.TAG, "Should never be reached.");
        }

        public void onChange(boolean selfChange, Uri uri) {
            Integer what = this.mUriEventMap.get(uri);
            if (what != null) {
                this.mHandler.obtainMessage(what.intValue()).sendToTarget();
                return;
            }
            ConnectivityService.loge("No matching event to send for URI=" + uri);
        }
    }

    /* access modifiers changed from: private */
    public static void log(String s) {
        Slog.d(TAG, s);
    }

    /* access modifiers changed from: private */
    public static void loge(String s) {
        Slog.e(TAG, s);
    }

    private static void loge(String s, Throwable t) {
        Slog.e(TAG, s, t);
    }

    public boolean prepareVpn(String oldPackage, String newPackage, int userId) {
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                return false;
            }
            boolean prepare = vpn.prepare(oldPackage, newPackage);
            return prepare;
        }
    }

    public void setVpnPackageAuthorization(String packageName, int userId, boolean authorized) {
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn != null) {
                vpn.setPackageAuthorization(packageName, authorized);
            }
        }
    }

    public ParcelFileDescriptor establishVpn(VpnConfig config) {
        ParcelFileDescriptor establish;
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            establish = this.mVpns.get(user).establish(config);
        }
        return establish;
    }

    public void startLegacyVpn(VpnProfile profile) {
        int user = UserHandle.getUserId(Binder.getCallingUid());
        LinkProperties egress = getActiveLinkProperties();
        if (egress != null) {
            synchronized (this.mVpns) {
                throwIfLockdownEnabled();
                this.mVpns.get(user).startLegacyVpn(profile, this.mKeyStore, egress);
            }
            return;
        }
        throw new IllegalStateException("Missing active network connection");
    }

    public LegacyVpnInfo getLegacyVpnInfo(int userId) {
        LegacyVpnInfo legacyVpnInfo;
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            legacyVpnInfo = this.mVpns.get(userId).getLegacyVpnInfo();
        }
        return legacyVpnInfo;
    }

    private VpnInfo[] getAllVpnInfo() {
        ensureRunningOnConnectivityServiceThread();
        synchronized (this.mVpns) {
            if (this.mLockdownEnabled) {
                VpnInfo[] vpnInfoArr = new VpnInfo[0];
                return vpnInfoArr;
            }
            List<VpnInfo> infoList = new ArrayList<>();
            for (int i = 0; i < this.mVpns.size(); i++) {
                VpnInfo info = createVpnInfo(this.mVpns.valueAt(i));
                if (info != null) {
                    infoList.add(info);
                }
            }
            VpnInfo[] vpnInfoArr2 = (VpnInfo[]) infoList.toArray(new VpnInfo[infoList.size()]);
            return vpnInfoArr2;
        }
    }

    private VpnInfo createVpnInfo(Vpn vpn) {
        LinkProperties linkProperties;
        VpnInfo info = vpn.getVpnInfo();
        if (info == null) {
            return null;
        }
        Network[] underlyingNetworks = vpn.getUnderlyingNetworks();
        if (underlyingNetworks == null) {
            NetworkAgentInfo defaultNetwork = getDefaultNetwork();
            if (!(defaultNetwork == null || defaultNetwork.linkProperties == null)) {
                info.primaryUnderlyingIface = getDefaultNetwork().linkProperties.getInterfaceName();
            }
        } else if (underlyingNetworks.length > 0 && (linkProperties = getLinkProperties(underlyingNetworks[0])) != null) {
            info.primaryUnderlyingIface = linkProperties.getInterfaceName();
        }
        if (info.primaryUnderlyingIface == null) {
            return null;
        }
        return info;
    }

    public VpnConfig getVpnConfig(int userId) {
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                return null;
            }
            VpnConfig vpnConfig = vpn.getVpnConfig();
            return vpnConfig;
        }
    }

    private void updateAllVpnsCapabilities() {
        Network defaultNetwork = getNetwork(getDefaultNetwork());
        synchronized (this.mVpns) {
            for (int i = 0; i < this.mVpns.size(); i++) {
                Vpn vpn = this.mVpns.valueAt(i);
                updateVpnCapabilities(vpn, vpn.updateCapabilities(defaultNetwork));
            }
        }
    }

    private void updateVpnCapabilities(Vpn vpn, NetworkCapabilities nc) {
        ensureRunningOnConnectivityServiceThread();
        NetworkAgentInfo vpnNai = getNetworkAgentInfoForNetId(vpn.getNetId());
        if (vpnNai != null && nc != null) {
            updateCapabilities(vpnNai.getCurrentScore(), vpnNai, nc);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b5, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean updateLockdownVpn() {
        /*
            r14 = this;
            int r0 = android.os.Binder.getCallingUid()
            r1 = 0
            r2 = 1000(0x3e8, float:1.401E-42)
            if (r0 == r2) goto L_0x0011
            java.lang.String r0 = TAG
            java.lang.String r2 = "Lockdown VPN only available to AID_SYSTEM"
            android.util.Slog.w(r0, r2)
            return r1
        L_0x0011:
            android.util.SparseArray<com.android.server.connectivity.Vpn> r0 = r14.mVpns
            monitor-enter(r0)
            boolean r2 = com.android.server.net.LockdownVpnTracker.isEnabled()     // Catch:{ all -> 0x00b6 }
            r14.mLockdownEnabled = r2     // Catch:{ all -> 0x00b6 }
            boolean r2 = r14.mLockdownEnabled     // Catch:{ all -> 0x00b6 }
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L_0x00b1
            android.security.KeyStore r2 = r14.mKeyStore     // Catch:{ all -> 0x00b6 }
            java.lang.String r5 = "LOCKDOWN_VPN"
            byte[] r2 = r2.get(r5)     // Catch:{ all -> 0x00b6 }
            if (r2 != 0) goto L_0x0033
            java.lang.String r3 = TAG     // Catch:{ all -> 0x00b6 }
            java.lang.String r4 = "Lockdown VPN configured but cannot be read from keystore"
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r0)     // Catch:{ all -> 0x00b6 }
            return r1
        L_0x0033:
            java.lang.String r5 = new java.lang.String     // Catch:{ all -> 0x00b6 }
            r5.<init>(r2)     // Catch:{ all -> 0x00b6 }
            android.security.KeyStore r6 = r14.mKeyStore     // Catch:{ all -> 0x00b6 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b6 }
            r7.<init>()     // Catch:{ all -> 0x00b6 }
            java.lang.String r8 = "VPN_"
            r7.append(r8)     // Catch:{ all -> 0x00b6 }
            r7.append(r5)     // Catch:{ all -> 0x00b6 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00b6 }
            byte[] r6 = r6.get(r7)     // Catch:{ all -> 0x00b6 }
            com.android.internal.net.VpnProfile r6 = com.android.internal.net.VpnProfile.decode(r5, r6)     // Catch:{ all -> 0x00b6 }
            if (r6 != 0) goto L_0x0070
            java.lang.String r1 = TAG     // Catch:{ all -> 0x00b6 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b6 }
            r7.<init>()     // Catch:{ all -> 0x00b6 }
            java.lang.String r8 = "Lockdown VPN configured invalid profile "
            r7.append(r8)     // Catch:{ all -> 0x00b6 }
            r7.append(r5)     // Catch:{ all -> 0x00b6 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00b6 }
            android.util.Slog.e(r1, r7)     // Catch:{ all -> 0x00b6 }
            r14.setLockdownTracker(r4)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r0)     // Catch:{ all -> 0x00b6 }
            return r3
        L_0x0070:
            int r4 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00b6 }
            int r4 = android.os.UserHandle.getUserId(r4)     // Catch:{ all -> 0x00b6 }
            android.util.SparseArray<com.android.server.connectivity.Vpn> r7 = r14.mVpns     // Catch:{ all -> 0x00b6 }
            java.lang.Object r7 = r7.get(r4)     // Catch:{ all -> 0x00b6 }
            com.android.server.connectivity.Vpn r7 = (com.android.server.connectivity.Vpn) r7     // Catch:{ all -> 0x00b6 }
            r13 = r7
            if (r13 != 0) goto L_0x00a0
            java.lang.String r3 = TAG     // Catch:{ all -> 0x00b6 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b6 }
            r7.<init>()     // Catch:{ all -> 0x00b6 }
            java.lang.String r8 = "VPN for user "
            r7.append(r8)     // Catch:{ all -> 0x00b6 }
            r7.append(r4)     // Catch:{ all -> 0x00b6 }
            java.lang.String r8 = " not ready yet. Skipping lockdown"
            r7.append(r8)     // Catch:{ all -> 0x00b6 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00b6 }
            android.util.Slog.w(r3, r7)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r0)     // Catch:{ all -> 0x00b6 }
            return r1
        L_0x00a0:
            com.android.server.net.LockdownVpnTracker r1 = new com.android.server.net.LockdownVpnTracker     // Catch:{ all -> 0x00b6 }
            android.content.Context r8 = r14.mContext     // Catch:{ all -> 0x00b6 }
            android.os.INetworkManagementService r9 = r14.mNMS     // Catch:{ all -> 0x00b6 }
            r7 = r1
            r10 = r14
            r11 = r13
            r12 = r6
            r7.<init>(r8, r9, r10, r11, r12)     // Catch:{ all -> 0x00b6 }
            r14.setLockdownTracker(r1)     // Catch:{ all -> 0x00b6 }
            goto L_0x00b4
        L_0x00b1:
            r14.setLockdownTracker(r4)     // Catch:{ all -> 0x00b6 }
        L_0x00b4:
            monitor-exit(r0)     // Catch:{ all -> 0x00b6 }
            return r3
        L_0x00b6:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00b6 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.updateLockdownVpn():boolean");
    }

    @GuardedBy({"mVpns"})
    private void setLockdownTracker(LockdownVpnTracker tracker) {
        LockdownVpnTracker existing = this.mLockdownTracker;
        this.mLockdownTracker = null;
        if (existing != null) {
            existing.shutdown();
        }
        if (tracker != null) {
            this.mLockdownTracker = tracker;
            this.mLockdownTracker.init();
        }
    }

    @GuardedBy({"mVpns"})
    private void throwIfLockdownEnabled() {
        if (this.mLockdownEnabled) {
            throw new IllegalStateException("Unavailable in lockdown mode");
        }
    }

    private boolean startAlwaysOnVpn(int userId) {
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                String str = TAG;
                Slog.wtf(str, "User " + userId + " has no Vpn configuration");
                return false;
            }
            boolean startAlwaysOnVpn = vpn.startAlwaysOnVpn();
            return startAlwaysOnVpn;
        }
    }

    public boolean isAlwaysOnVpnPackageSupported(int userId, String packageName) {
        enforceSettingsPermission();
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                String str = TAG;
                Slog.w(str, "User " + userId + " has no Vpn configuration");
                return false;
            }
            boolean isAlwaysOnPackageSupported = vpn.isAlwaysOnPackageSupported(packageName);
            return isAlwaysOnPackageSupported;
        }
    }

    public boolean setAlwaysOnVpnPackage(int userId, String packageName, boolean lockdown, List<String> lockdownWhitelist) {
        enforceControlAlwaysOnVpnPermission();
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            if (LockdownVpnTracker.isEnabled()) {
                return false;
            }
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                String str = TAG;
                Slog.w(str, "User " + userId + " has no Vpn configuration");
                return false;
            } else if (!vpn.setAlwaysOnPackage(packageName, lockdown, lockdownWhitelist)) {
                return false;
            } else {
                if (startAlwaysOnVpn(userId)) {
                    return true;
                }
                vpn.setAlwaysOnPackage((String) null, false, (List<String>) null);
                return false;
            }
        }
    }

    public String getAlwaysOnVpnPackage(int userId) {
        enforceControlAlwaysOnVpnPermission();
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                String str = TAG;
                Slog.w(str, "User " + userId + " has no Vpn configuration");
                return null;
            }
            String alwaysOnPackage = vpn.getAlwaysOnPackage();
            return alwaysOnPackage;
        }
    }

    public boolean isVpnLockdownEnabled(int userId) {
        enforceControlAlwaysOnVpnPermission();
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                String str = TAG;
                Slog.w(str, "User " + userId + " has no Vpn configuration");
                return false;
            }
            boolean lockdown = vpn.getLockdown();
            return lockdown;
        }
    }

    public List<String> getVpnLockdownWhitelist(int userId) {
        enforceControlAlwaysOnVpnPermission();
        enforceCrossUserPermission(userId);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                String str = TAG;
                Slog.w(str, "User " + userId + " has no Vpn configuration");
                return null;
            }
            List<String> lockdownWhitelist = vpn.getLockdownWhitelist();
            return lockdownWhitelist;
        }
    }

    public int checkMobileProvisioning(int suggestedTimeOutMs) {
        return -1;
    }

    private String getProvisioningUrlBaseFromFile() {
        String mcc;
        String mnc;
        FileReader fileReader = null;
        Configuration config = this.mContext.getResources().getConfiguration();
        try {
            fileReader = new FileReader(this.mProvisioningUrlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fileReader);
            XmlUtils.beginDocument(parser, TAG_PROVISIONING_URLS);
            while (true) {
                XmlUtils.nextElement(parser);
                String element = parser.getName();
                if (element == null) {
                    try {
                        fileReader.close();
                    } catch (IOException e) {
                    }
                    return null;
                } else if (element.equals(TAG_PROVISIONING_URL) && (mcc = parser.getAttributeValue((String) null, ATTR_MCC)) != null) {
                    try {
                        if (Integer.parseInt(mcc) == config.mcc && (mnc = parser.getAttributeValue((String) null, ATTR_MNC)) != null && Integer.parseInt(mnc) == config.mnc) {
                            parser.next();
                            if (parser.getEventType() == 4) {
                                String text = parser.getText();
                                try {
                                    fileReader.close();
                                } catch (IOException e2) {
                                }
                                return text;
                            }
                        }
                    } catch (NumberFormatException e3) {
                        loge("NumberFormatException in getProvisioningUrlBaseFromFile: " + e3);
                    }
                }
            }
        } catch (FileNotFoundException e4) {
            loge("Carrier Provisioning Urls file not found");
            if (fileReader != null) {
                fileReader.close();
            }
            return null;
        } catch (XmlPullParserException e5) {
            loge("Xml parser exception reading Carrier Provisioning Urls file: " + e5);
            if (fileReader != null) {
                fileReader.close();
            }
            return null;
        } catch (IOException e6) {
            loge("I/O exception reading Carrier Provisioning Urls file: " + e6);
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e7) {
                }
            }
            return null;
        } catch (Throwable th) {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e8) {
                }
            }
            throw th;
        }
    }

    public String getMobileProvisioningUrl() {
        enforceConnectivityInternalPermission();
        String url = getProvisioningUrlBaseFromFile();
        if (TextUtils.isEmpty(url)) {
            url = this.mContext.getResources().getString(17040512);
            log("getMobileProvisioningUrl: mobile_provisioining_url from resource =" + url);
        } else {
            log("getMobileProvisioningUrl: mobile_provisioning_url from File =" + url);
        }
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String phoneNumber = this.mTelephonyManager.getLine1Number();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumber = "0000000000";
        }
        return String.format(url, new Object[]{this.mTelephonyManager.getSimSerialNumber(), this.mTelephonyManager.getDeviceId(), phoneNumber});
    }

    public void setProvisioningNotificationVisible(boolean visible, int networkType, String action) {
        enforceConnectivityInternalPermission();
        if (ConnectivityManager.isNetworkTypeValid(networkType)) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mNotifier.setProvNotificationVisible(visible, networkType + 1 + 64512, action);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    public void setAirplaneMode(boolean enable) {
        enforceNetworkStackSettingsOrSetup();
        long ident = Binder.clearCallingIdentity();
        boolean enable2 = RestrictionsHelper.handleAirplaneChange(this.mContext, enable);
        try {
            Settings.Global.putInt(this.mContext.getContentResolver(), "airplane_mode_on", encodeBool(enable2));
            Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
            intent.putExtra("state", enable2);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onUserStart(int r7) {
        /*
            r6 = this;
            android.util.SparseArray<com.android.server.connectivity.Vpn> r0 = r6.mVpns
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.connectivity.Vpn> r1 = r6.mVpns     // Catch:{ all -> 0x0040 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ all -> 0x0040 }
            com.android.server.connectivity.Vpn r1 = (com.android.server.connectivity.Vpn) r1     // Catch:{ all -> 0x0040 }
            if (r1 == 0) goto L_0x0014
            java.lang.String r2 = "Starting user already has a VPN"
            loge(r2)     // Catch:{ all -> 0x0040 }
            monitor-exit(r0)     // Catch:{ all -> 0x0040 }
            return
        L_0x0014:
            com.android.server.connectivity.Vpn r2 = new com.android.server.connectivity.Vpn     // Catch:{ all -> 0x0040 }
            com.android.server.ConnectivityService$InternalHandler r3 = r6.mHandler     // Catch:{ all -> 0x0040 }
            android.os.Looper r3 = r3.getLooper()     // Catch:{ all -> 0x0040 }
            android.content.Context r4 = r6.mContext     // Catch:{ all -> 0x0040 }
            android.os.INetworkManagementService r5 = r6.mNMS     // Catch:{ all -> 0x0040 }
            r2.<init>(r3, r4, r5, r7)     // Catch:{ all -> 0x0040 }
            r1 = r2
            android.util.SparseArray<com.android.server.connectivity.Vpn> r2 = r6.mVpns     // Catch:{ all -> 0x0040 }
            r2.put(r7, r1)     // Catch:{ all -> 0x0040 }
            android.os.UserManager r2 = r6.mUserManager     // Catch:{ all -> 0x0040 }
            android.content.pm.UserInfo r2 = r2.getUserInfo(r7)     // Catch:{ all -> 0x0040 }
            boolean r2 = r2.isPrimary()     // Catch:{ all -> 0x0040 }
            if (r2 == 0) goto L_0x003e
            boolean r2 = com.android.server.net.LockdownVpnTracker.isEnabled()     // Catch:{ all -> 0x0040 }
            if (r2 == 0) goto L_0x003e
            r6.updateLockdownVpn()     // Catch:{ all -> 0x0040 }
        L_0x003e:
            monitor-exit(r0)     // Catch:{ all -> 0x0040 }
            return
        L_0x0040:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0040 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.onUserStart(int):void");
    }

    /* access modifiers changed from: private */
    public void onUserStop(int userId) {
        synchronized (this.mVpns) {
            Vpn userVpn = this.mVpns.get(userId);
            if (userVpn == null) {
                loge("Stopped user has no VPN");
                return;
            }
            userVpn.onUserStopped();
            this.mVpns.delete(userId);
        }
    }

    /* access modifiers changed from: private */
    public void onUserAdded(int userId) {
        this.mPermissionMonitor.onUserAdded(userId);
        Network defaultNetwork = getNetwork(getDefaultNetwork());
        synchronized (this.mVpns) {
            int vpnsSize = this.mVpns.size();
            for (int i = 0; i < vpnsSize; i++) {
                Vpn vpn = this.mVpns.valueAt(i);
                vpn.onUserAdded(userId);
                updateVpnCapabilities(vpn, vpn.updateCapabilities(defaultNetwork));
            }
        }
    }

    /* access modifiers changed from: private */
    public void onUserRemoved(int userId) {
        this.mPermissionMonitor.onUserRemoved(userId);
        Network defaultNetwork = getNetwork(getDefaultNetwork());
        synchronized (this.mVpns) {
            int vpnsSize = this.mVpns.size();
            for (int i = 0; i < vpnsSize; i++) {
                Vpn vpn = this.mVpns.valueAt(i);
                vpn.onUserRemoved(userId);
                updateVpnCapabilities(vpn, vpn.updateCapabilities(defaultNetwork));
            }
        }
    }

    /* access modifiers changed from: private */
    public void onPackageAdded(String packageName, int uid) {
        if (TextUtils.isEmpty(packageName) || uid < 0) {
            String str = TAG;
            Slog.wtf(str, "Invalid package in onPackageAdded: " + packageName + " | " + uid);
            return;
        }
        this.mPermissionMonitor.onPackageAdded(packageName, uid);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0048, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPackageReplaced(java.lang.String r7, int r8) {
        /*
            r6 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            if (r0 != 0) goto L_0x004c
            if (r8 >= 0) goto L_0x0009
            goto L_0x004c
        L_0x0009:
            int r0 = android.os.UserHandle.getUserId(r8)
            android.util.SparseArray<com.android.server.connectivity.Vpn> r1 = r6.mVpns
            monitor-enter(r1)
            android.util.SparseArray<com.android.server.connectivity.Vpn> r2 = r6.mVpns     // Catch:{ all -> 0x0049 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x0049 }
            com.android.server.connectivity.Vpn r2 = (com.android.server.connectivity.Vpn) r2     // Catch:{ all -> 0x0049 }
            if (r2 != 0) goto L_0x001c
            monitor-exit(r1)     // Catch:{ all -> 0x0049 }
            return
        L_0x001c:
            java.lang.String r3 = r2.getAlwaysOnPackage()     // Catch:{ all -> 0x0049 }
            boolean r3 = android.text.TextUtils.equals(r3, r7)     // Catch:{ all -> 0x0049 }
            if (r3 == 0) goto L_0x0047
            java.lang.String r3 = TAG     // Catch:{ all -> 0x0049 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0049 }
            r4.<init>()     // Catch:{ all -> 0x0049 }
            java.lang.String r5 = "Restarting always-on VPN package "
            r4.append(r5)     // Catch:{ all -> 0x0049 }
            r4.append(r7)     // Catch:{ all -> 0x0049 }
            java.lang.String r5 = " for user "
            r4.append(r5)     // Catch:{ all -> 0x0049 }
            r4.append(r0)     // Catch:{ all -> 0x0049 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0049 }
            android.util.Slog.d(r3, r4)     // Catch:{ all -> 0x0049 }
            r2.startAlwaysOnVpn()     // Catch:{ all -> 0x0049 }
        L_0x0047:
            monitor-exit(r1)     // Catch:{ all -> 0x0049 }
            return
        L_0x0049:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0049 }
            throw r2
        L_0x004c:
            java.lang.String r0 = TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid package in onPackageReplaced: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = " | "
            r1.append(r2)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            android.util.Slog.wtf(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.onPackageReplaced(java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0051, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPackageRemoved(java.lang.String r7, int r8, boolean r9) {
        /*
            r6 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            if (r0 != 0) goto L_0x0055
            if (r8 >= 0) goto L_0x0009
            goto L_0x0055
        L_0x0009:
            com.android.server.connectivity.PermissionMonitor r0 = r6.mPermissionMonitor
            r0.onPackageRemoved(r8)
            int r0 = android.os.UserHandle.getUserId(r8)
            android.util.SparseArray<com.android.server.connectivity.Vpn> r1 = r6.mVpns
            monitor-enter(r1)
            android.util.SparseArray<com.android.server.connectivity.Vpn> r2 = r6.mVpns     // Catch:{ all -> 0x0052 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x0052 }
            com.android.server.connectivity.Vpn r2 = (com.android.server.connectivity.Vpn) r2     // Catch:{ all -> 0x0052 }
            if (r2 != 0) goto L_0x0021
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            return
        L_0x0021:
            java.lang.String r3 = r2.getAlwaysOnPackage()     // Catch:{ all -> 0x0052 }
            boolean r3 = android.text.TextUtils.equals(r3, r7)     // Catch:{ all -> 0x0052 }
            if (r3 == 0) goto L_0x0050
            if (r9 != 0) goto L_0x0050
            java.lang.String r3 = TAG     // Catch:{ all -> 0x0052 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0052 }
            r4.<init>()     // Catch:{ all -> 0x0052 }
            java.lang.String r5 = "Removing always-on VPN package "
            r4.append(r5)     // Catch:{ all -> 0x0052 }
            r4.append(r7)     // Catch:{ all -> 0x0052 }
            java.lang.String r5 = " for user "
            r4.append(r5)     // Catch:{ all -> 0x0052 }
            r4.append(r0)     // Catch:{ all -> 0x0052 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0052 }
            android.util.Slog.d(r3, r4)     // Catch:{ all -> 0x0052 }
            r3 = 0
            r4 = 0
            r2.setAlwaysOnPackage(r4, r3, r4)     // Catch:{ all -> 0x0052 }
        L_0x0050:
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            return
        L_0x0052:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            throw r2
        L_0x0055:
            java.lang.String r0 = TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid package in onPackageRemoved: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = " | "
            r1.append(r2)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            android.util.Slog.wtf(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.onPackageRemoved(java.lang.String, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public void onUserUnlocked(int userId) {
        synchronized (this.mVpns) {
            if (!this.mUserManager.getUserInfo(userId).isPrimary() || !LockdownVpnTracker.isEnabled()) {
                startAlwaysOnVpn(userId);
            } else {
                updateLockdownVpn();
            }
        }
    }

    private static class NetworkFactoryInfo {
        public final AsyncChannel asyncChannel;
        public final int factorySerialNumber;
        public final Messenger messenger;
        public final String name;

        NetworkFactoryInfo(String name2, Messenger messenger2, AsyncChannel asyncChannel2, int factorySerialNumber2) {
            this.name = name2;
            this.messenger = messenger2;
            this.asyncChannel = asyncChannel2;
            this.factorySerialNumber = factorySerialNumber2;
        }
    }

    /* access modifiers changed from: private */
    public void ensureNetworkRequestHasType(NetworkRequest request) {
        if (request.type == NetworkRequest.Type.NONE) {
            throw new IllegalArgumentException("All NetworkRequests in ConnectivityService must have a type");
        }
    }

    private class NetworkRequestInfo implements IBinder.DeathRecipient {
        private final IBinder mBinder;
        final PendingIntent mPendingIntent;
        boolean mPendingIntentSent;
        final int mPid;
        final int mUid;
        final Messenger messenger;
        final NetworkRequest request;

        NetworkRequestInfo(NetworkRequest r, PendingIntent pi) {
            this.request = r;
            ConnectivityService.this.ensureNetworkRequestHasType(this.request);
            this.mPendingIntent = pi;
            this.messenger = null;
            this.mBinder = null;
            this.mPid = Binder.getCallingPid();
            this.mUid = Binder.getCallingUid();
            enforceRequestCountLimit();
        }

        NetworkRequestInfo(Messenger m, NetworkRequest r, IBinder binder) {
            this.messenger = m;
            this.request = r;
            ConnectivityService.this.ensureNetworkRequestHasType(this.request);
            this.mBinder = binder;
            this.mPid = Binder.getCallingPid();
            this.mUid = Binder.getCallingUid();
            this.mPendingIntent = null;
            enforceRequestCountLimit();
            try {
                this.mBinder.linkToDeath(this, 0);
            } catch (RemoteException e) {
                binderDied();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        private void enforceRequestCountLimit() {
            synchronized (ConnectivityService.this.mUidToNetworkRequestCount) {
                int networkRequests = ConnectivityService.this.mUidToNetworkRequestCount.get(this.mUid, 0) + 1;
                if (networkRequests < 100) {
                    ConnectivityService.this.mUidToNetworkRequestCount.put(this.mUid, networkRequests);
                } else {
                    throw new ServiceSpecificException(1);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void unlinkDeathRecipient() {
            IBinder iBinder = this.mBinder;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
            }
        }

        public void binderDied() {
            ConnectivityService.log("ConnectivityService NetworkRequestInfo binderDied(" + this.request + ", " + this.mBinder + ")");
            ConnectivityService.this.releaseNetworkRequest(this.request);
        }

        public String toString() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("uid/pid:");
            sb.append(this.mUid);
            sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
            sb.append(this.mPid);
            sb.append(" ");
            sb.append(this.request);
            if (this.mPendingIntent == null) {
                str = "";
            } else {
                str = " to trigger " + this.mPendingIntent;
            }
            sb.append(str);
            return sb.toString();
        }
    }

    private void ensureRequestableCapabilities(NetworkCapabilities networkCapabilities) {
        String badCapability = networkCapabilities.describeFirstNonRequestableCapability();
        if (badCapability != null) {
            throw new IllegalArgumentException("Cannot request network with " + badCapability);
        }
    }

    private void ensureSufficientPermissionsForRequest(NetworkCapabilities nc, int callerPid, int callerUid) {
        if (nc.getSSID() != null && !checkSettingsPermission(callerPid, callerUid)) {
            throw new SecurityException("Insufficient permissions to request a specific SSID");
        } else if (nc.hasSignalStrength() && !checkNetworkSignalStrengthWakeupPermission(callerPid, callerUid)) {
            throw new SecurityException("Insufficient permissions to request a specific signal strength");
        }
    }

    private ArrayList<Integer> getSignalStrengthThresholds(NetworkAgentInfo nai) {
        SortedSet<Integer> thresholds = new TreeSet<>();
        synchronized (nai) {
            for (NetworkRequestInfo nri : this.mNetworkRequests.values()) {
                if (nri.request.networkCapabilities.hasSignalStrength() && nai.satisfiesImmutableCapabilitiesOf(nri.request)) {
                    thresholds.add(Integer.valueOf(nri.request.networkCapabilities.getSignalStrength()));
                }
            }
        }
        return new ArrayList<>(thresholds);
    }

    private void updateSignalStrengthThresholds(NetworkAgentInfo nai, String reason, NetworkRequest request) {
        String detail;
        ArrayList<Integer> thresholdsArray = getSignalStrengthThresholds(nai);
        Bundle thresholds = new Bundle();
        thresholds.putIntegerArrayList("thresholds", thresholdsArray);
        if (VDBG || !"CONNECT".equals(reason)) {
            if (request == null || !request.networkCapabilities.hasSignalStrength()) {
                detail = reason;
            } else {
                detail = reason + " " + request.networkCapabilities.getSignalStrength();
            }
            log(String.format("updateSignalStrengthThresholds: %s, sending %s to %s", new Object[]{detail, Arrays.toString(thresholdsArray.toArray()), nai.name()}));
        }
        nai.asyncChannel.sendMessage(528398, 0, 0, thresholds);
    }

    private void ensureValidNetworkSpecifier(NetworkCapabilities nc) {
        NetworkSpecifier ns;
        if (nc != null && (ns = nc.getNetworkSpecifier()) != null) {
            MatchAllNetworkSpecifier.checkNotMatchAllNetworkSpecifier(ns);
            ns.assertValidFromUid(Binder.getCallingUid());
        }
    }

    public NetworkRequest requestNetwork(NetworkCapabilities networkCapabilities, Messenger messenger, int timeoutMs, IBinder binder, int legacyType) {
        NetworkRequest.Type type;
        NetworkCapabilities networkCapabilities2;
        if (networkCapabilities == null) {
            type = NetworkRequest.Type.TRACK_DEFAULT;
        } else {
            type = NetworkRequest.Type.REQUEST;
        }
        if (type == NetworkRequest.Type.TRACK_DEFAULT) {
            networkCapabilities2 = createDefaultNetworkCapabilitiesForUid(Binder.getCallingUid());
            enforceAccessPermission();
        } else {
            networkCapabilities2 = new NetworkCapabilities(networkCapabilities);
            enforceNetworkRequestPermissions(networkCapabilities2);
            enforceMeteredApnPolicy(networkCapabilities2);
        }
        ensureRequestableCapabilities(networkCapabilities2);
        ensureSufficientPermissionsForRequest(networkCapabilities2, Binder.getCallingPid(), Binder.getCallingUid());
        restrictRequestUidsForCaller(networkCapabilities2);
        if (timeoutMs >= 0) {
            ensureValidNetworkSpecifier(networkCapabilities2);
            NetworkRequest networkRequest = new NetworkRequest(networkCapabilities2, legacyType, nextNetworkRequestId(), type);
            NetworkRequestInfo nri = new NetworkRequestInfo(messenger, networkRequest, binder);
            log("requestNetwork for " + nri);
            InternalHandler internalHandler = this.mHandler;
            internalHandler.sendMessage(internalHandler.obtainMessage(19, nri));
            if (timeoutMs > 0) {
                InternalHandler internalHandler2 = this.mHandler;
                internalHandler2.sendMessageDelayed(internalHandler2.obtainMessage(20, nri), (long) timeoutMs);
            }
            return networkRequest;
        }
        throw new IllegalArgumentException("Bad timeout specified");
    }

    private void enforceNetworkRequestPermissions(NetworkCapabilities networkCapabilities) {
        if (!networkCapabilities.hasCapability(13)) {
            enforceConnectivityRestrictedNetworksPermission();
        } else {
            enforceChangePermission();
        }
    }

    public boolean requestBandwidthUpdate(Network network) {
        NetworkAgentInfo nai;
        enforceAccessPermission();
        if (network == null) {
            return false;
        }
        synchronized (this.mNetworkForNetId) {
            nai = this.mNetworkForNetId.get(network.netId);
        }
        if (nai == null) {
            return false;
        }
        nai.asyncChannel.sendMessage(528394);
        synchronized (this.mBandwidthRequests) {
            int uid = Binder.getCallingUid();
            Integer uidReqs = this.mBandwidthRequests.get(uid);
            if (uidReqs == null) {
                uidReqs = new Integer(0);
            }
            SparseArray<Integer> sparseArray = this.mBandwidthRequests;
            Integer valueOf = Integer.valueOf(uidReqs.intValue() + 1);
            Integer uidReqs2 = valueOf;
            sparseArray.put(uid, valueOf);
        }
        return true;
    }

    private boolean isSystem(int uid) {
        return uid < 10000;
    }

    private void enforceMeteredApnPolicy(NetworkCapabilities networkCapabilities) {
        int uid = Binder.getCallingUid();
        if (!isSystem(uid) && !networkCapabilities.hasCapability(11) && this.mPolicyManagerInternal.isUidRestrictedOnMeteredNetworks(uid)) {
            networkCapabilities.addCapability(11);
        }
    }

    public NetworkRequest pendingRequestForNetwork(NetworkCapabilities networkCapabilities, PendingIntent operation) {
        Preconditions.checkNotNull(operation, "PendingIntent cannot be null.");
        NetworkCapabilities networkCapabilities2 = new NetworkCapabilities(networkCapabilities);
        enforceNetworkRequestPermissions(networkCapabilities2);
        enforceMeteredApnPolicy(networkCapabilities2);
        ensureRequestableCapabilities(networkCapabilities2);
        ensureSufficientPermissionsForRequest(networkCapabilities2, Binder.getCallingPid(), Binder.getCallingUid());
        ensureValidNetworkSpecifier(networkCapabilities2);
        restrictRequestUidsForCaller(networkCapabilities2);
        NetworkRequest networkRequest = new NetworkRequest(networkCapabilities2, -1, nextNetworkRequestId(), NetworkRequest.Type.REQUEST);
        NetworkRequestInfo nri = new NetworkRequestInfo(networkRequest, operation);
        log("pendingRequest for " + nri);
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(EVENT_REGISTER_NETWORK_REQUEST_WITH_INTENT, nri));
        return networkRequest;
    }

    private void releasePendingNetworkRequestWithDelay(PendingIntent operation) {
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessageDelayed(internalHandler.obtainMessage(EVENT_RELEASE_NETWORK_REQUEST_WITH_INTENT, getCallingUid(), 0, operation), (long) this.mReleasePendingIntentDelayMs);
    }

    public void releasePendingNetworkRequest(PendingIntent operation) {
        Preconditions.checkNotNull(operation, "PendingIntent cannot be null.");
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(EVENT_RELEASE_NETWORK_REQUEST_WITH_INTENT, getCallingUid(), 0, operation));
    }

    private boolean hasWifiNetworkListenPermission(NetworkCapabilities nc) {
        if (nc == null) {
            return false;
        }
        int[] transportTypes = nc.getTransportTypes();
        if (transportTypes.length != 1 || transportTypes[0] != 1) {
            return false;
        }
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE", "ConnectivityService");
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    public NetworkRequest listenForNetwork(NetworkCapabilities networkCapabilities, Messenger messenger, IBinder binder) {
        if (!hasWifiNetworkListenPermission(networkCapabilities)) {
            enforceAccessPermission();
        }
        NetworkCapabilities nc = new NetworkCapabilities(networkCapabilities);
        ensureSufficientPermissionsForRequest(networkCapabilities, Binder.getCallingPid(), Binder.getCallingUid());
        restrictRequestUidsForCaller(nc);
        restrictBackgroundRequestForCaller(nc);
        ensureValidNetworkSpecifier(nc);
        NetworkRequest networkRequest = new NetworkRequest(nc, -1, nextNetworkRequestId(), NetworkRequest.Type.LISTEN);
        NetworkRequestInfo nri = new NetworkRequestInfo(messenger, networkRequest, binder);
        if (VDBG) {
            log("listenForNetwork for " + nri);
        }
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(21, nri));
        return networkRequest;
    }

    public void pendingListenForNetwork(NetworkCapabilities networkCapabilities, PendingIntent operation) {
        Preconditions.checkNotNull(operation, "PendingIntent cannot be null.");
        if (!hasWifiNetworkListenPermission(networkCapabilities)) {
            enforceAccessPermission();
        }
        ensureValidNetworkSpecifier(networkCapabilities);
        ensureSufficientPermissionsForRequest(networkCapabilities, Binder.getCallingPid(), Binder.getCallingUid());
        NetworkCapabilities nc = new NetworkCapabilities(networkCapabilities);
        restrictRequestUidsForCaller(nc);
        NetworkRequestInfo nri = new NetworkRequestInfo(new NetworkRequest(nc, -1, nextNetworkRequestId(), NetworkRequest.Type.LISTEN), operation);
        if (VDBG) {
            log("pendingListenForNetwork for " + nri);
        }
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(21, nri));
    }

    public void releaseNetworkRequest(NetworkRequest networkRequest) {
        ensureNetworkRequestHasType(networkRequest);
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(22, getCallingUid(), 0, networkRequest));
    }

    public int registerNetworkFactory(Messenger messenger, String name) {
        enforceConnectivityInternalPermission();
        NetworkFactoryInfo nfi = new NetworkFactoryInfo(name, messenger, new AsyncChannel(), NetworkFactory.SerialNumber.nextSerialNumber());
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(17, nfi));
        return nfi.factorySerialNumber;
    }

    /* access modifiers changed from: private */
    public void handleRegisterNetworkFactory(NetworkFactoryInfo nfi) {
        log("Got NetworkFactory Messenger for " + nfi.name);
        this.mNetworkFactoryInfos.put(nfi.messenger, nfi);
        nfi.asyncChannel.connect(this.mContext, this.mTrackerHandler, nfi.messenger);
    }

    public void unregisterNetworkFactory(Messenger messenger) {
        enforceConnectivityInternalPermission();
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(23, messenger));
    }

    /* access modifiers changed from: private */
    public void handleUnregisterNetworkFactory(Messenger messenger) {
        NetworkFactoryInfo nfi = this.mNetworkFactoryInfos.remove(messenger);
        if (nfi == null) {
            loge("Failed to find Messenger in unregisterNetworkFactory");
            return;
        }
        log("unregisterNetworkFactory for " + nfi.name);
    }

    private NetworkAgentInfo getNetworkForRequest(int requestId) {
        NetworkAgentInfo networkAgentInfo;
        synchronized (this.mNetworkForRequestId) {
            networkAgentInfo = this.mNetworkForRequestId.get(requestId);
        }
        return networkAgentInfo;
    }

    private void clearNetworkForRequest(int requestId) {
        synchronized (this.mNetworkForRequestId) {
            this.mNetworkForRequestId.remove(requestId);
        }
    }

    private void setNetworkForRequest(int requestId, NetworkAgentInfo nai) {
        synchronized (this.mNetworkForRequestId) {
            this.mNetworkForRequestId.put(requestId, nai);
        }
    }

    private NetworkAgentInfo getDefaultNetwork() {
        return getNetworkForRequest(this.mDefaultRequest.requestId);
    }

    private Network getNetwork(NetworkAgentInfo nai) {
        if (nai != null) {
            return nai.network;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void ensureRunningOnConnectivityServiceThread() {
        if (this.mHandler.getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Not running on ConnectivityService thread: " + Thread.currentThread().getName());
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isDefaultNetwork(NetworkAgentInfo nai) {
        return nai == getDefaultNetwork();
    }

    private boolean isDefaultRequest(NetworkRequestInfo nri) {
        return nri.request.requestId == this.mDefaultRequest.requestId;
    }

    public int registerNetworkAgent(Messenger messenger, NetworkInfo networkInfo, LinkProperties linkProperties, NetworkCapabilities networkCapabilities, int currentScore, NetworkMisc networkMisc) {
        return registerNetworkAgent(messenger, networkInfo, linkProperties, networkCapabilities, currentScore, networkMisc, -1);
    }

    /* JADX INFO: finally extract failed */
    public int registerNetworkAgent(Messenger messenger, NetworkInfo networkInfo, LinkProperties linkProperties, NetworkCapabilities networkCapabilities, int currentScore, NetworkMisc networkMisc, int factorySerialNumber) {
        enforceConnectivityInternalPermission();
        LinkProperties lp = new LinkProperties(linkProperties);
        lp.ensureDirectlyConnectedRoutes();
        NetworkCapabilities nc = new NetworkCapabilities(networkCapabilities);
        AsyncChannel asyncChannel = new AsyncChannel();
        Network network = new Network(reserveNetId());
        NetworkInfo networkInfo2 = new NetworkInfo(networkInfo);
        Context context = this.mContext;
        NetworkStateTrackerHandler networkStateTrackerHandler = this.mTrackerHandler;
        NetworkMisc networkMisc2 = new NetworkMisc(networkMisc);
        NetworkMisc networkMisc3 = networkMisc2;
        LinkProperties lp2 = lp;
        NetworkAgentInfo nai = new NetworkAgentInfo(messenger, asyncChannel, network, networkInfo2, lp2, nc, currentScore, context, networkStateTrackerHandler, networkMisc3, this, this.mNetd, this.mDnsResolver, this.mNMS, factorySerialNumber);
        nai.setNetworkCapabilities(mixInCapabilities(nai, nc));
        String extraInfo = networkInfo.getExtraInfo();
        String name = TextUtils.isEmpty(extraInfo) ? nai.networkCapabilities.getSSID() : extraInfo;
        log("registerNetworkAgent " + nai);
        long token = Binder.clearCallingIdentity();
        try {
            getNetworkStack().makeNetworkMonitor(nai.network, name, new NetworkMonitorCallbacks(nai));
            Binder.restoreCallingIdentity(token);
            return nai.network.netId;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public NetworkStackClient getNetworkStack() {
        return NetworkStackClient.getInstance();
    }

    /* access modifiers changed from: private */
    public void handleRegisterNetworkAgent(NetworkAgentInfo nai, INetworkMonitor networkMonitor) {
        nai.onNetworkMonitorCreated(networkMonitor);
        if (VDBG) {
            log("Got NetworkAgent Messenger");
        }
        this.mNetworkAgentInfos.put(nai.messenger, nai);
        synchronized (this.mNetworkForNetId) {
            this.mNetworkForNetId.put(nai.network.netId, nai);
        }
        try {
            networkMonitor.start();
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
        nai.asyncChannel.connect(this.mContext, this.mTrackerHandler, nai.messenger);
        NetworkInfo networkInfo = nai.networkInfo;
        nai.networkInfo = null;
        updateNetworkInfo(nai, networkInfo);
        updateUids(nai, (NetworkCapabilities) null, nai.networkCapabilities);
        CaptivePortalInjector.NetworkAgentInfoInner.setNetworkMonitor(nai.networkMonitor());
    }

    private void updateLinkProperties(NetworkAgentInfo networkAgent, LinkProperties newLp, LinkProperties oldLp) {
        int netId = networkAgent.network.netId;
        networkAgent.clatd.fixupLinkProperties(oldLp, newLp);
        updateInterfaces(newLp, oldLp, netId, networkAgent.networkCapabilities);
        updateVpnFiltering(newLp, oldLp, networkAgent);
        updateMtu(newLp, oldLp);
        if (isDefaultNetwork(networkAgent)) {
            updateTcpBufferSizes(newLp.getTcpBufferSizes());
        }
        updateRoutes(newLp, oldLp, netId);
        updateDnses(newLp, oldLp, netId);
        this.mDnsManager.updatePrivateDnsStatus(netId, newLp);
        if (isDefaultNetwork(networkAgent)) {
            handleApplyDefaultProxy(newLp.getHttpProxy());
        } else {
            updateProxy(newLp, oldLp);
        }
        if (!Objects.equals(newLp, oldLp)) {
            synchronized (networkAgent) {
                networkAgent.linkProperties = newLp;
            }
            networkAgent.clatd.update();
            notifyIfacesChangedForNetworkStats();
            networkAgent.networkMonitor().notifyLinkPropertiesChanged(newLp);
            if (networkAgent.everConnected) {
                notifyNetworkCallbacks(networkAgent, 524295);
            }
        }
        this.mKeepaliveTracker.handleCheckKeepalivesStillValid(networkAgent);
    }

    private void wakeupModifyInterface(String iface, NetworkCapabilities caps, boolean add) {
        if (caps.hasTransport(1)) {
            int mark = this.mContext.getResources().getInteger(17694858);
            int mask = this.mContext.getResources().getInteger(17694859);
            if (mark != 0 && mask != 0) {
                String prefix = "iface:" + iface;
                if (add) {
                    try {
                        this.mNetd.wakeupAddInterface(iface, prefix, mark, mask);
                    } catch (Exception e) {
                        loge("Exception modifying wakeup packet monitoring: " + e);
                    }
                } else {
                    this.mNetd.wakeupDelInterface(iface, prefix, mark, mask);
                }
            }
        }
    }

    private void updateInterfaces(LinkProperties newLp, LinkProperties oldLp, int netId, NetworkCapabilities caps) {
        List list = null;
        List allInterfaceNames = oldLp != null ? oldLp.getAllInterfaceNames() : null;
        if (newLp != null) {
            list = newLp.getAllInterfaceNames();
        }
        LinkProperties.CompareResult<String> interfaceDiff = new LinkProperties.CompareResult<>(allInterfaceNames, list);
        for (String iface : interfaceDiff.added) {
            try {
                log("Adding iface " + iface + " to network " + netId);
                this.mNMS.addInterfaceToNetwork(iface, netId);
                wakeupModifyInterface(iface, caps, true);
            } catch (Exception e) {
                loge("Exception adding interface: " + e);
            }
        }
        for (String iface2 : interfaceDiff.removed) {
            try {
                log("Removing iface " + iface2 + " from network " + netId);
                wakeupModifyInterface(iface2, caps, false);
                this.mNMS.removeInterfaceFromNetwork(iface2, netId);
            } catch (Exception e2) {
                loge("Exception removing interface: " + e2);
            }
        }
    }

    private boolean updateRoutes(LinkProperties newLp, LinkProperties oldLp, int netId) {
        List list = null;
        List allRoutes = oldLp != null ? oldLp.getAllRoutes() : null;
        if (newLp != null) {
            list = newLp.getAllRoutes();
        }
        LinkProperties.CompareResult<RouteInfo> routeDiff = new LinkProperties.CompareResult<>(allRoutes, list);
        for (RouteInfo route : routeDiff.added) {
            if (!route.hasGateway()) {
                if (VDBG || DDBG) {
                    log("Adding Route [" + route + "] to network " + netId);
                }
                try {
                    this.mNMS.addRoute(netId, route);
                } catch (Exception e) {
                    if ((route.getDestination().getAddress() instanceof Inet4Address) || VDBG) {
                        loge("Exception in addRoute for non-gateway: " + e);
                    }
                }
            }
        }
        for (RouteInfo route2 : routeDiff.added) {
            if (route2.hasGateway()) {
                if (VDBG || DDBG) {
                    log("Adding Route [" + route2 + "] to network " + netId);
                }
                try {
                    this.mNMS.addRoute(netId, route2);
                } catch (Exception e2) {
                    if ((route2.getGateway() instanceof Inet4Address) || VDBG) {
                        loge("Exception in addRoute for gateway: " + e2);
                    }
                }
            }
        }
        for (RouteInfo route3 : routeDiff.removed) {
            if (VDBG || DDBG) {
                log("Removing Route [" + route3 + "] from network " + netId);
            }
            try {
                this.mNMS.removeRoute(netId, route3);
            } catch (Exception e3) {
                loge("Exception in removeRoute: " + e3);
            }
        }
        return !routeDiff.added.isEmpty() || !routeDiff.removed.isEmpty();
    }

    private void updateDnses(LinkProperties newLp, LinkProperties oldLp, int netId) {
        if (oldLp == null || !newLp.isIdenticalDnses(oldLp)) {
            NetworkAgentInfo defaultNai = getDefaultNetwork();
            boolean isDefaultNetwork = defaultNai != null && defaultNai.network.netId == netId;
            Collection<InetAddress> dnses = ConnectivityServiceInjector.addDnsIfNeeded(newLp.getDnsServers());
            LinkProperties miuiLp = new LinkProperties(newLp);
            miuiLp.setDnsServers(dnses);
            log("Setting DNS servers for network " + netId + " to " + dnses);
            try {
                this.mDnsManager.setDnsConfigurationForNetwork(netId, miuiLp, isDefaultNetwork);
            } catch (Exception e) {
                loge("Exception in setDnsConfigurationForNetwork: " + e);
            }
        }
    }

    private void updateVpnFiltering(LinkProperties newLp, LinkProperties oldLp, NetworkAgentInfo nai) {
        String newIface = null;
        String oldIface = oldLp != null ? oldLp.getInterfaceName() : null;
        if (newLp != null) {
            newIface = newLp.getInterfaceName();
        }
        boolean wasFiltering = requiresVpnIsolation(nai, nai.networkCapabilities, oldLp);
        boolean needsFiltering = requiresVpnIsolation(nai, nai.networkCapabilities, newLp);
        if (!wasFiltering && !needsFiltering) {
            return;
        }
        if (!Objects.equals(oldIface, newIface) || wasFiltering != needsFiltering) {
            Set<UidRange> ranges = nai.networkCapabilities.getUids();
            int vpnAppUid = nai.networkCapabilities.getEstablishingVpnAppUid();
            if (wasFiltering) {
                this.mPermissionMonitor.onVpnUidRangesRemoved(oldIface, ranges, vpnAppUid);
            }
            if (needsFiltering) {
                this.mPermissionMonitor.onVpnUidRangesAdded(newIface, ranges, vpnAppUid);
            }
        }
    }

    private int getNetworkPermission(NetworkCapabilities nc) {
        if (!nc.hasCapability(13)) {
            return 2;
        }
        if (!nc.hasCapability(19)) {
            return 1;
        }
        return 0;
    }

    private NetworkCapabilities mixInCapabilities(NetworkAgentInfo nai, NetworkCapabilities nc) {
        if (nai.everConnected && !nai.isVPN() && !nai.networkCapabilities.satisfiedByImmutableNetworkCapabilities(nc)) {
            String diff = nai.networkCapabilities.describeImmutableDifferences(nc);
            if (!TextUtils.isEmpty(diff)) {
                String str = TAG;
                Slog.wtf(str, "BUG: " + nai + " lost immutable capabilities:" + diff);
            }
        }
        NetworkCapabilities newNc = new NetworkCapabilities(nc);
        if (nai.lastValidated) {
            newNc.addCapability(16);
        } else {
            newNc.removeCapability(16);
        }
        if (nai.lastCaptivePortalDetected) {
            newNc.addCapability(17);
        } else {
            newNc.removeCapability(17);
        }
        if (nai.isBackgroundNetwork()) {
            newNc.removeCapability(19);
        } else {
            newNc.addCapability(19);
        }
        if (nai.isSuspended()) {
            newNc.removeCapability(21);
        } else {
            newNc.addCapability(21);
        }
        if (nai.partialConnectivity) {
            newNc.addCapability(24);
        } else {
            newNc.removeCapability(24);
        }
        return newNc;
    }

    /* access modifiers changed from: private */
    public void updateCapabilities(int oldScore, NetworkAgentInfo nai, NetworkCapabilities nc) {
        NetworkCapabilities prevNc;
        int i = oldScore;
        NetworkAgentInfo networkAgentInfo = nai;
        NetworkCapabilities newNc = mixInCapabilities(networkAgentInfo, nc);
        if (!Objects.equals(networkAgentInfo.networkCapabilities, newNc)) {
            int oldPermission = getNetworkPermission(networkAgentInfo.networkCapabilities);
            int newPermission = getNetworkPermission(newNc);
            if (oldPermission != newPermission && networkAgentInfo.created && !nai.isVPN()) {
                try {
                    this.mNMS.setNetworkPermission(networkAgentInfo.network.netId, newPermission);
                } catch (RemoteException e) {
                    loge("Exception in setNetworkPermission: " + e);
                }
            }
            synchronized (nai) {
                prevNc = networkAgentInfo.networkCapabilities;
                networkAgentInfo.setNetworkCapabilities(newNc);
            }
            updateUids(networkAgentInfo, prevNc, newNc);
            boolean z = true;
            if (nai.getCurrentScore() != i || !newNc.equalRequestableCapabilities(prevNc)) {
                rematchAllNetworksAndRequests(networkAgentInfo, i);
                notifyNetworkCallbacks(networkAgentInfo, 524294);
            } else {
                processListenRequests(networkAgentInfo, true);
            }
            if (prevNc != null) {
                boolean oldMetered = prevNc.isMetered();
                boolean newMetered = newNc.isMetered();
                boolean meteredChanged = oldMetered != newMetered;
                if (meteredChanged) {
                    boolean newMetered2 = this.mRestrictBackground;
                    boolean z2 = newMetered;
                    maybeNotifyNetworkBlocked(nai, oldMetered, newMetered, newMetered2, newMetered2);
                }
                if (prevNc.hasCapability(18) == newNc.hasCapability(18)) {
                    z = false;
                }
                boolean roamingChanged = z;
                if (meteredChanged || roamingChanged) {
                    notifyIfacesChangedForNetworkStats();
                }
            }
            if (!newNc.hasTransport(4)) {
                updateAllVpnsCapabilities();
            }
        }
    }

    private boolean requiresVpnIsolation(NetworkAgentInfo nai, NetworkCapabilities nc, LinkProperties lp) {
        if (nc == null || lp == null || !nai.isVPN() || nai.networkMisc.allowBypass || nc.getEstablishingVpnAppUid() == 1000 || lp.getInterfaceName() == null) {
            return false;
        }
        if (lp.hasIPv4DefaultRoute() || lp.hasIPv6DefaultRoute()) {
            return true;
        }
        return false;
    }

    private void updateUids(NetworkAgentInfo nai, NetworkCapabilities prevNc, NetworkCapabilities newNc) {
        Set<UidRange> newRanges = null;
        Set<UidRange> prevRanges = prevNc == null ? null : prevNc.getUids();
        if (newNc != null) {
            newRanges = newNc.getUids();
        }
        if (prevRanges == null) {
            prevRanges = new ArraySet<>();
        }
        if (newRanges == null) {
            newRanges = new ArraySet<>();
        }
        Set<UidRange> prevRangesCopy = new ArraySet<>(prevRanges);
        prevRanges.removeAll(newRanges);
        newRanges.removeAll(prevRangesCopy);
        try {
            if (!newRanges.isEmpty()) {
                UidRange[] addedRangesArray = new UidRange[newRanges.size()];
                newRanges.toArray(addedRangesArray);
                this.mNMS.addVpnUidRanges(nai.network.netId, addedRangesArray);
            }
            if (!prevRanges.isEmpty()) {
                UidRange[] removedRangesArray = new UidRange[prevRanges.size()];
                prevRanges.toArray(removedRangesArray);
                this.mNMS.removeVpnUidRanges(nai.network.netId, removedRangesArray);
            }
            boolean wasFiltering = requiresVpnIsolation(nai, prevNc, nai.linkProperties);
            boolean shouldFilter = requiresVpnIsolation(nai, newNc, nai.linkProperties);
            String iface = nai.linkProperties.getInterfaceName();
            if (wasFiltering && !prevRanges.isEmpty()) {
                this.mPermissionMonitor.onVpnUidRangesRemoved(iface, prevRanges, prevNc.getEstablishingVpnAppUid());
            }
            if (shouldFilter && !newRanges.isEmpty()) {
                this.mPermissionMonitor.onVpnUidRangesAdded(iface, newRanges, newNc.getEstablishingVpnAppUid());
            }
        } catch (Exception e) {
            loge("Exception in updateUids: ", e);
        }
    }

    public void handleUpdateLinkProperties(NetworkAgentInfo nai, LinkProperties newLp) {
        ensureRunningOnConnectivityServiceThread();
        if (getNetworkAgentInfoForNetId(nai.network.netId) == nai) {
            newLp.ensureDirectlyConnectedRoutes();
            if (VDBG || DDBG) {
                log("Update of LinkProperties for " + nai.name() + "; created=" + nai.created + "; everConnected=" + nai.everConnected);
            }
            updateLinkProperties(nai, newLp, new LinkProperties(nai.linkProperties));
        }
    }

    /* access modifiers changed from: private */
    public void sendUpdatedScoreToFactories(NetworkAgentInfo nai) {
        for (int i = 0; i < nai.numNetworkRequests(); i++) {
            NetworkRequest nr = nai.requestAt(i);
            if (!nr.isListen()) {
                sendUpdatedScoreToFactories(nr, nai);
            }
        }
    }

    private void sendUpdatedScoreToFactories(NetworkRequest networkRequest, NetworkAgentInfo nai) {
        int score = 0;
        int serial = 0;
        if (nai != null) {
            score = nai.getCurrentScore();
            serial = nai.factorySerialNumber;
        }
        if (VDBG || DDBG) {
            log("sending new Min Network Score(" + score + "): " + networkRequest.toString());
        }
        for (NetworkFactoryInfo nfi : this.mNetworkFactoryInfos.values()) {
            nfi.asyncChannel.sendMessage(536576, score, serial, networkRequest);
        }
    }

    private void sendPendingIntentForRequest(NetworkRequestInfo nri, NetworkAgentInfo networkAgent, int notificationType) {
        if (notificationType == 524290 && !nri.mPendingIntentSent) {
            Intent intent = new Intent();
            intent.putExtra("android.net.extra.NETWORK", networkAgent.network);
            intent.putExtra("android.net.extra.NETWORK_REQUEST", nri.request);
            nri.mPendingIntentSent = true;
            sendIntent(nri.mPendingIntent, intent);
        }
    }

    private void sendIntent(PendingIntent pendingIntent, Intent intent) {
        this.mPendingIntentWakeLock.acquire();
        try {
            log("Sending " + pendingIntent);
            pendingIntent.send(this.mContext, 0, intent, this, (Handler) null);
        } catch (PendingIntent.CanceledException e) {
            log(pendingIntent + " was not sent, it had been canceled.");
            this.mPendingIntentWakeLock.release();
            releasePendingNetworkRequest(pendingIntent);
        }
    }

    public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
        log("Finished sending " + pendingIntent);
        this.mPendingIntentWakeLock.release();
        releasePendingNetworkRequestWithDelay(pendingIntent);
    }

    private void callCallbackForRequest(NetworkRequestInfo nri, NetworkAgentInfo networkAgent, int notificationType, int arg1) {
        if (nri.messenger != null) {
            Bundle bundle = new Bundle();
            putParcelable(bundle, new NetworkRequest(nri.request));
            Message msg = Message.obtain();
            if (notificationType != 524293) {
                putParcelable(bundle, networkAgent.network);
            }
            switch (notificationType) {
                case 524290:
                    putParcelable(bundle, networkCapabilitiesRestrictedForCallerPermissions(networkAgent.networkCapabilities, nri.mPid, nri.mUid));
                    putParcelable(bundle, new LinkProperties(networkAgent.linkProperties));
                    msg.arg1 = arg1;
                    break;
                case 524291:
                    msg.arg1 = arg1;
                    break;
                case 524294:
                    putParcelable(bundle, networkCapabilitiesRestrictedForCallerPermissions(networkAgent.networkCapabilities, nri.mPid, nri.mUid));
                    break;
                case 524295:
                    putParcelable(bundle, new LinkProperties(networkAgent.linkProperties));
                    break;
                case 524299:
                    maybeLogBlockedStatusChanged(nri, networkAgent.network, arg1 != 0);
                    msg.arg1 = arg1;
                    break;
            }
            msg.what = notificationType;
            msg.setData(bundle);
            try {
                if (VDBG) {
                    String notification = ConnectivityManager.getCallbackName(notificationType);
                    log("sending notification " + notification + " for " + nri.request);
                }
                nri.messenger.send(msg);
            } catch (RemoteException e) {
                loge("RemoteException caught trying to send a callback msg for " + nri.request);
            }
        }
    }

    private static <T extends Parcelable> void putParcelable(Bundle bundle, T t) {
        bundle.putParcelable(t.getClass().getSimpleName(), t);
    }

    /* access modifiers changed from: private */
    public void teardownUnneededNetwork(NetworkAgentInfo nai) {
        if (nai.numRequestNetworkRequests() != 0) {
            int i = 0;
            while (true) {
                if (i >= nai.numNetworkRequests()) {
                    break;
                }
                NetworkRequest nr = nai.requestAt(i);
                if (!nr.isListen()) {
                    loge("Dead network still had at least " + nr);
                    break;
                }
                i++;
            }
        }
        nai.asyncChannel.disconnect();
    }

    /* access modifiers changed from: private */
    public void handleLingerComplete(NetworkAgentInfo oldNetwork) {
        if (oldNetwork == null) {
            loge("Unknown NetworkAgentInfo in handleLingerComplete");
            return;
        }
        log("handleLingerComplete for " + oldNetwork.name());
        oldNetwork.clearLingerState();
        if (unneeded(oldNetwork, UnneededFor.TEARDOWN)) {
            teardownUnneededNetwork(oldNetwork);
        } else {
            updateCapabilities(oldNetwork.getCurrentScore(), oldNetwork, oldNetwork.networkCapabilities);
        }
    }

    private void makeDefault(NetworkAgentInfo newNetwork) {
        log("Switching to new default network: " + newNetwork);
        try {
            this.mNMS.setDefaultNetId(newNetwork.network.netId);
        } catch (Exception e) {
            loge("Exception setting default network :" + e);
        }
        notifyLockdownVpn(newNetwork);
        handleApplyDefaultProxy(newNetwork.linkProperties.getHttpProxy());
        updateTcpBufferSizes(newNetwork.linkProperties.getTcpBufferSizes());
        this.mDnsManager.setDefaultDnsSystemProperties(ConnectivityServiceInjector.addDnsIfNeeded(newNetwork.linkProperties.getDnsServers()));
        notifyIfacesChangedForNetworkStats();
        updateAllVpnsCapabilities();
    }

    private void processListenRequests(NetworkAgentInfo nai, boolean capabilitiesChanged) {
        for (NetworkRequestInfo nri : this.mNetworkRequests.values()) {
            NetworkRequest nr = nri.request;
            if (nr.isListen() && nai.isSatisfyingRequest(nr.requestId) && !nai.satisfies(nr)) {
                nai.removeRequest(nri.request.requestId);
                callCallbackForRequest(nri, nai, 524292, 0);
            }
        }
        if (capabilitiesChanged) {
            notifyNetworkCallbacks(nai, 524294);
        }
        for (NetworkRequestInfo nri2 : this.mNetworkRequests.values()) {
            NetworkRequest nr2 = nri2.request;
            if (nr2.isListen() && nai.satisfies(nr2) && !nai.isSatisfyingRequest(nr2.requestId)) {
                nai.addRequest(nr2);
                notifyNetworkAvailable(nai, nri2);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0145, code lost:
        if (r23.getCurrentScore() >= r12) goto L_0x02c1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void rematchNetworkAndRequests(com.android.server.connectivity.NetworkAgentInfo r26, com.android.server.ConnectivityService.ReapUnvalidatedNetworks r27, long r28) {
        /*
            r25 = this;
            r7 = r25
            r8 = r26
            r9 = r28
            boolean r0 = r8.everConnected
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            boolean r0 = r26.isVPN()
            r1 = 0
            r2 = 0
            boolean r11 = r26.isBackgroundNetwork()
            int r12 = r26.getCurrentScore()
            boolean r3 = VDBG
            if (r3 != 0) goto L_0x0021
            boolean r3 = DDBG
            if (r3 == 0) goto L_0x003a
        L_0x0021:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "rematching "
            r3.append(r4)
            java.lang.String r4 = r26.name()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            log(r3)
        L_0x003a:
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r13 = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r14 = r3
            android.net.NetworkCapabilities r15 = r8.networkCapabilities
            boolean r3 = VDBG
            if (r3 == 0) goto L_0x0060
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = " network has: "
            r3.append(r4)
            r3.append(r15)
            java.lang.String r3 = r3.toString()
            log(r3)
        L_0x0060:
            java.util.HashMap<android.net.NetworkRequest, com.android.server.ConnectivityService$NetworkRequestInfo> r3 = r7.mNetworkRequests
            java.util.Collection r3 = r3.values()
            java.util.Iterator r16 = r3.iterator()
            r18 = r0
            r17 = r1
            r6 = r2
        L_0x006f:
            boolean r0 = r16.hasNext()
            r1 = 0
            if (r0 == 0) goto L_0x02c6
            java.lang.Object r0 = r16.next()
            com.android.server.ConnectivityService$NetworkRequestInfo r0 = (com.android.server.ConnectivityService.NetworkRequestInfo) r0
            android.net.NetworkRequest r2 = r0.request
            boolean r2 = r2.isListen()
            if (r2 == 0) goto L_0x0085
            goto L_0x006f
        L_0x0085:
            android.net.NetworkRequest r2 = r0.request
            int r2 = r2.requestId
            com.android.server.connectivity.NetworkAgentInfo r5 = r7.getNetworkForRequest(r2)
            android.net.NetworkRequest r2 = r0.request
            boolean r19 = r8.satisfies(r2)
            r2 = 0
            if (r19 == 0) goto L_0x00a3
            android.net.NetworkCapabilities r3 = r8.networkCapabilities
            android.net.NetworkRequest r4 = r0.request
            android.net.NetworkCapabilities r4 = r4.networkCapabilities
            boolean r2 = r7.satisfiesMobileMultiNetworkDataCheck(r3, r4)
            r20 = r2
            goto L_0x00a5
        L_0x00a3:
            r20 = r2
        L_0x00a5:
            java.lang.String r2 = "Network "
            if (r8 != r5) goto L_0x00d9
            if (r20 == 0) goto L_0x00d9
            boolean r1 = VDBG
            if (r1 == 0) goto L_0x00d6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r2)
            java.lang.String r2 = r26.name()
            r1.append(r2)
            java.lang.String r2 = " was already satisfying request "
            r1.append(r2)
            android.net.NetworkRequest r2 = r0.request
            int r2 = r2.requestId
            r1.append(r2)
            java.lang.String r2 = ". No change."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            log(r1)
        L_0x00d6:
            r18 = 1
            goto L_0x006f
        L_0x00d9:
            boolean r3 = VDBG
            if (r3 == 0) goto L_0x00f3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "  checking if request is satisfied: "
            r3.append(r4)
            android.net.NetworkRequest r4 = r0.request
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            log(r3)
        L_0x00f3:
            if (r20 == 0) goto L_0x022c
            boolean r2 = VDBG
            if (r2 != 0) goto L_0x00fd
            boolean r2 = DDBG
            if (r2 == 0) goto L_0x011f
        L_0x00fd:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "currentScore = "
            r2.append(r3)
            if (r5 == 0) goto L_0x010d
            int r1 = r5.getCurrentScore()
        L_0x010d:
            r2.append(r1)
            java.lang.String r1 = ", newScore = "
            r2.append(r1)
            r2.append(r12)
            java.lang.String r1 = r2.toString()
            log(r1)
        L_0x011f:
            if (r5 == 0) goto L_0x0148
            android.net.NetworkCapabilities r3 = r5.networkCapabilities
            android.net.NetworkCapabilities r4 = r8.networkCapabilities
            android.net.NetworkRequest r1 = r0.request
            android.net.NetworkCapabilities r2 = r1.networkCapabilities
            r1 = r25
            r21 = r2
            r2 = r5
            r22 = r4
            r4 = r26
            r23 = r5
            r5 = r22
            r22 = r11
            r11 = r6
            r6 = r21
            boolean r1 = r1.isBestMobileMultiNetwork(r2, r3, r4, r5, r6)
            if (r1 != 0) goto L_0x014d
            int r1 = r23.getCurrentScore()
            if (r1 >= r12) goto L_0x02c1
            goto L_0x014d
        L_0x0148:
            r23 = r5
            r22 = r11
            r11 = r6
        L_0x014d:
            boolean r1 = VDBG
            if (r1 == 0) goto L_0x016a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "rematch for "
            r1.append(r2)
            java.lang.String r2 = r26.name()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            log(r1)
        L_0x016a:
            r5 = r23
            if (r5 == 0) goto L_0x01c3
            boolean r1 = VDBG
            if (r1 != 0) goto L_0x0176
            boolean r1 = DDBG
            if (r1 == 0) goto L_0x018e
        L_0x0176:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "   accepting network in place of "
            r1.append(r2)
            java.lang.String r2 = r5.name()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            log(r1)
        L_0x018e:
            android.net.NetworkRequest r1 = r0.request
            int r1 = r1.requestId
            r5.removeRequest(r1)
            android.net.NetworkCapabilities r1 = r5.networkCapabilities
            boolean r1 = r7.satisfiesMobileNetworkDataCheck(r1)
            if (r1 == 0) goto L_0x01af
            android.net.NetworkRequest r2 = r0.request
            int r1 = r7.mLingerDelayMs
            long r3 = (long) r1
            r1 = r5
            r23 = r3
            r3 = r28
            r21 = r5
            r5 = r23
            r1.lingerRequest(r2, r3, r5)
            goto L_0x01bd
        L_0x01af:
            r21 = r5
            android.net.NetworkRequest r2 = r0.request
            int r1 = r7.mNonDefaultSubscriptionLingerDelayMs
            long r5 = (long) r1
            r1 = r21
            r3 = r28
            r1.lingerRequest(r2, r3, r5)
        L_0x01bd:
            r5 = r21
            r13.add(r5)
            goto L_0x01d0
        L_0x01c3:
            boolean r1 = VDBG
            if (r1 != 0) goto L_0x01cb
            boolean r1 = DDBG
            if (r1 == 0) goto L_0x01d0
        L_0x01cb:
            java.lang.String r1 = "   accepting network in place of null"
            log(r1)
        L_0x01d0:
            android.net.NetworkRequest r1 = r0.request
            r8.unlingerRequest(r1)
            android.net.NetworkRequest r1 = r0.request
            int r1 = r1.requestId
            r7.setNetworkForRequest(r1, r8)
            android.net.NetworkRequest r1 = r0.request
            boolean r1 = r8.addRequest(r1)
            if (r1 != 0) goto L_0x0208
            java.lang.String r1 = TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "BUG: "
            r2.append(r3)
            java.lang.String r3 = r26.name()
            r2.append(r3)
            java.lang.String r3 = " already has "
            r2.append(r3)
            android.net.NetworkRequest r3 = r0.request
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Slog.wtf(r1, r2)
        L_0x0208:
            r14.add(r0)
            r1 = 1
            android.net.NetworkRequest r2 = r0.request
            r7.sendUpdatedScoreToFactories(r2, r8)
            boolean r2 = r7.isDefaultRequest(r0)
            if (r2 == 0) goto L_0x0227
            r2 = 1
            r3 = r5
            if (r5 == 0) goto L_0x0220
            com.android.server.connectivity.LingerMonitor r4 = r7.mLingerMonitor
            r4.noteLingerDefaultNetwork(r5, r8)
        L_0x0220:
            r18 = r1
            r17 = r2
            r6 = r3
            goto L_0x02c2
        L_0x0227:
            r18 = r1
            r6 = r11
            goto L_0x02c2
        L_0x022c:
            r22 = r11
            r11 = r6
            android.net.NetworkRequest r3 = r0.request
            int r3 = r3.requestId
            boolean r3 = r8.isSatisfyingRequest(r3)
            if (r3 == 0) goto L_0x02bf
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            java.lang.String r2 = r26.name()
            r3.append(r2)
            java.lang.String r2 = " stopped satisfying request "
            r3.append(r2)
            android.net.NetworkRequest r2 = r0.request
            int r2 = r2.requestId
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            log(r2)
            android.net.NetworkRequest r2 = r0.request
            int r2 = r2.requestId
            r8.removeRequest(r2)
            if (r5 != r8) goto L_0x0272
            android.net.NetworkRequest r2 = r0.request
            int r2 = r2.requestId
            r7.clearNetworkForRequest(r2)
            android.net.NetworkRequest r2 = r0.request
            r3 = 0
            r7.sendUpdatedScoreToFactories(r2, r3)
            goto L_0x029d
        L_0x0272:
            java.lang.String r2 = TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "BUG: Removing request "
            r3.append(r4)
            android.net.NetworkRequest r4 = r0.request
            int r4 = r4.requestId
            r3.append(r4)
            java.lang.String r4 = " from "
            r3.append(r4)
            java.lang.String r4 = r26.name()
            r3.append(r4)
            java.lang.String r4 = " without updating mNetworkForRequestId or factories!"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Slog.wtf(r2, r3)
        L_0x029d:
            android.net.NetworkCapabilities r2 = r8.networkCapabilities
            boolean r2 = r7.satisfiesMobileNetworkDataCheck(r2)
            if (r2 == 0) goto L_0x02ac
            r2 = 524292(0x80004, float:7.3469E-40)
            r7.callCallbackForRequest(r0, r8, r2, r1)
            goto L_0x02c1
        L_0x02ac:
            android.net.NetworkRequest r2 = r0.request
            int r1 = r7.mNonDefaultSubscriptionLingerDelayMs
            long r3 = (long) r1
            r1 = r26
            r23 = r3
            r3 = r28
            r21 = r5
            r5 = r23
            r1.lingerRequest(r2, r3, r5)
            goto L_0x02c1
        L_0x02bf:
            r21 = r5
        L_0x02c1:
            r6 = r11
        L_0x02c2:
            r11 = r22
            goto L_0x006f
        L_0x02c6:
            r22 = r11
            r11 = r6
            java.lang.String r2 = "Exception in setNetworkPermission: "
            if (r17 == 0) goto L_0x030a
            boolean r0 = r26.isVPN()
            if (r0 != 0) goto L_0x02f6
            android.os.INetworkManagementService r0 = r7.mNMS     // Catch:{ RemoteException -> 0x02e3 }
            android.net.Network r3 = r8.network     // Catch:{ RemoteException -> 0x02e3 }
            int r3 = r3.netId     // Catch:{ RemoteException -> 0x02e3 }
            android.net.NetworkCapabilities r4 = r8.networkCapabilities     // Catch:{ RemoteException -> 0x02e3 }
            int r4 = r7.getNetworkPermission(r4)     // Catch:{ RemoteException -> 0x02e3 }
            r0.setNetworkPermission(r3, r4)     // Catch:{ RemoteException -> 0x02e3 }
            goto L_0x02f6
        L_0x02e3:
            r0 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            loge(r3)
        L_0x02f6:
            r7.updateDataActivityTracking(r8, r11)
            r25.makeDefault(r26)
            com.android.server.connectivity.IpConnectivityMetrics$Logger r0 = r25.metricsLogger()
            com.android.server.connectivity.DefaultNetworkMetrics r0 = r0.defaultNetworkMetrics()
            r0.logDefaultNetworkEvent(r9, r8, r11)
            r25.scheduleReleaseNetworkTransitionWakelock()
        L_0x030a:
            android.net.NetworkCapabilities r0 = r8.networkCapabilities
            boolean r0 = r0.equalRequestableCapabilities(r15)
            r3 = 2
            r4 = 3
            r5 = 1
            if (r0 != 0) goto L_0x032e
            java.lang.String r0 = TAG
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r16 = r26.name()
            r6[r1] = r16
            r6[r5] = r15
            android.net.NetworkCapabilities r5 = r8.networkCapabilities
            r6[r3] = r5
            java.lang.String r5 = "BUG: %s changed requestable capabilities during rematch: %s -> %s"
            java.lang.String r5 = java.lang.String.format(r5, r6)
            android.util.Slog.wtf(r0, r5)
        L_0x032e:
            int r0 = r26.getCurrentScore()
            if (r0 == r12) goto L_0x0358
            java.lang.String r0 = TAG
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r5 = r26.name()
            r4[r1] = r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r12)
            r6 = 1
            r4[r6] = r5
            int r5 = r26.getCurrentScore()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r3] = r5
            java.lang.String r3 = "BUG: %s changed score during rematch: %d -> %d"
            java.lang.String r3 = java.lang.String.format(r3, r4)
            android.util.Slog.wtf(r0, r3)
        L_0x0358:
            boolean r0 = r26.isBackgroundNetwork()
            r3 = r22
            if (r3 == r0) goto L_0x0366
            android.net.NetworkCapabilities r0 = r8.networkCapabilities
            r7.updateCapabilities(r12, r8, r0)
            goto L_0x0369
        L_0x0366:
            r7.processListenRequests(r8, r1)
        L_0x0369:
            android.net.NetworkCapabilities r0 = r8.networkCapabilities
            boolean r0 = r7.satisfiesMobileNetworkDataCheck(r0)
            if (r0 != 0) goto L_0x0395
            boolean r0 = r26.isVPN()
            if (r0 != 0) goto L_0x0395
            android.os.INetworkManagementService r0 = r7.mNMS     // Catch:{ RemoteException -> 0x0382 }
            android.net.Network r4 = r8.network     // Catch:{ RemoteException -> 0x0382 }
            int r4 = r4.netId     // Catch:{ RemoteException -> 0x0382 }
            r5 = 1
            r0.setNetworkPermission(r4, r5)     // Catch:{ RemoteException -> 0x0382 }
            goto L_0x0395
        L_0x0382:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r4.append(r0)
            java.lang.String r2 = r4.toString()
            loge(r2)
        L_0x0395:
            java.util.Iterator r0 = r14.iterator()
        L_0x0399:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x03a9
            java.lang.Object r2 = r0.next()
            com.android.server.ConnectivityService$NetworkRequestInfo r2 = (com.android.server.ConnectivityService.NetworkRequestInfo) r2
            r7.notifyNetworkAvailable(r8, r2)
            goto L_0x0399
        L_0x03a9:
            java.util.Iterator r0 = r13.iterator()
        L_0x03ad:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x03bd
            java.lang.Object r2 = r0.next()
            com.android.server.connectivity.NetworkAgentInfo r2 = (com.android.server.connectivity.NetworkAgentInfo) r2
            r7.updateLingerState(r2, r9)
            goto L_0x03ad
        L_0x03bd:
            r7.updateLingerState(r8, r9)
            if (r17 == 0) goto L_0x03e6
            if (r11 == 0) goto L_0x03d0
            com.android.server.ConnectivityService$LegacyTypeTracker r0 = r7.mLegacyTypeTracker
            android.net.NetworkInfo r2 = r11.networkInfo
            int r2 = r2.getType()
            r4 = 1
            r0.remove(r2, r11, r4)
        L_0x03d0:
            boolean r0 = r8.lastValidated
            if (r0 == 0) goto L_0x03d6
            r1 = 100
        L_0x03d6:
            r7.mDefaultInetConditionPublished = r1
            com.android.server.ConnectivityService$LegacyTypeTracker r0 = r7.mLegacyTypeTracker
            android.net.NetworkInfo r1 = r8.networkInfo
            int r1 = r1.getType()
            r0.add(r1, r8)
            r25.notifyLockdownVpn(r26)
        L_0x03e6:
            if (r18 == 0) goto L_0x0448
            com.android.internal.app.IBatteryStats r0 = com.android.server.am.BatteryStatsService.getService()     // Catch:{ RemoteException -> 0x041a }
            android.net.NetworkInfo r1 = r8.networkInfo     // Catch:{ RemoteException -> 0x041a }
            int r1 = r1.getType()     // Catch:{ RemoteException -> 0x041a }
            android.net.LinkProperties r2 = r8.linkProperties     // Catch:{ RemoteException -> 0x041a }
            java.lang.String r2 = r2.getInterfaceName()     // Catch:{ RemoteException -> 0x041a }
            r0.noteNetworkInterfaceType(r2, r1)     // Catch:{ RemoteException -> 0x041a }
            android.net.LinkProperties r4 = r8.linkProperties     // Catch:{ RemoteException -> 0x041a }
            java.util.List r4 = r4.getStackedLinks()     // Catch:{ RemoteException -> 0x041a }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ RemoteException -> 0x041a }
        L_0x0405:
            boolean r5 = r4.hasNext()     // Catch:{ RemoteException -> 0x041a }
            if (r5 == 0) goto L_0x0419
            java.lang.Object r5 = r4.next()     // Catch:{ RemoteException -> 0x041a }
            android.net.LinkProperties r5 = (android.net.LinkProperties) r5     // Catch:{ RemoteException -> 0x041a }
            java.lang.String r6 = r5.getInterfaceName()     // Catch:{ RemoteException -> 0x041a }
            r0.noteNetworkInterfaceType(r6, r1)     // Catch:{ RemoteException -> 0x041a }
            goto L_0x0405
        L_0x0419:
            goto L_0x041b
        L_0x041a:
            r0 = move-exception
        L_0x041b:
            r0 = 0
        L_0x041c:
            int r1 = r26.numNetworkRequests()
            if (r0 >= r1) goto L_0x043b
            android.net.NetworkRequest r1 = r8.requestAt(r0)
            int r2 = r1.legacyType
            r4 = -1
            if (r2 == r4) goto L_0x0438
            boolean r2 = r1.isRequest()
            if (r2 == 0) goto L_0x0438
            com.android.server.ConnectivityService$LegacyTypeTracker r2 = r7.mLegacyTypeTracker
            int r4 = r1.legacyType
            r2.add(r4, r8)
        L_0x0438:
            int r0 = r0 + 1
            goto L_0x041c
        L_0x043b:
            boolean r0 = r26.isVPN()
            if (r0 == 0) goto L_0x0448
            com.android.server.ConnectivityService$LegacyTypeTracker r0 = r7.mLegacyTypeTracker
            r1 = 17
            r0.add(r1, r8)
        L_0x0448:
            com.android.server.ConnectivityService$ReapUnvalidatedNetworks r0 = com.android.server.ConnectivityService.ReapUnvalidatedNetworks.REAP
            r1 = r27
            if (r1 != r0) goto L_0x0496
            java.util.HashMap<android.os.Messenger, com.android.server.connectivity.NetworkAgentInfo> r0 = r7.mNetworkAgentInfos
            java.util.Collection r0 = r0.values()
            java.util.Iterator r0 = r0.iterator()
        L_0x0458:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x0496
            java.lang.Object r2 = r0.next()
            com.android.server.connectivity.NetworkAgentInfo r2 = (com.android.server.connectivity.NetworkAgentInfo) r2
            com.android.server.ConnectivityService$UnneededFor r4 = com.android.server.ConnectivityService.UnneededFor.TEARDOWN
            boolean r4 = r7.unneeded(r2, r4)
            if (r4 == 0) goto L_0x0495
            long r4 = r2.getLingerExpiry()
            r19 = 0
            int r4 = (r4 > r19 ? 1 : (r4 == r19 ? 0 : -1))
            if (r4 <= 0) goto L_0x047a
            r7.updateLingerState(r2, r9)
            goto L_0x0495
        L_0x047a:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Reaping "
            r4.append(r5)
            java.lang.String r5 = r2.name()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            log(r4)
            r7.teardownUnneededNetwork(r2)
        L_0x0495:
            goto L_0x0458
        L_0x0496:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.rematchNetworkAndRequests(com.android.server.connectivity.NetworkAgentInfo, com.android.server.ConnectivityService$ReapUnvalidatedNetworks, long):void");
    }

    private void rematchAllNetworksAndRequests(NetworkAgentInfo changed, int oldScore) {
        ReapUnvalidatedNetworks reapUnvalidatedNetworks;
        long now = SystemClock.elapsedRealtime();
        if (changed == null || oldScore >= changed.getCurrentScore()) {
            NetworkAgentInfo[] nais = (NetworkAgentInfo[]) this.mNetworkAgentInfos.values().toArray(new NetworkAgentInfo[this.mNetworkAgentInfos.size()]);
            Arrays.sort(nais);
            for (NetworkAgentInfo nai : nais) {
                if (nai != nais[nais.length - 1]) {
                    reapUnvalidatedNetworks = ReapUnvalidatedNetworks.DONT_REAP;
                } else {
                    reapUnvalidatedNetworks = ReapUnvalidatedNetworks.REAP;
                }
                rematchNetworkAndRequests(nai, reapUnvalidatedNetworks, now);
            }
            return;
        }
        rematchNetworkAndRequests(changed, ReapUnvalidatedNetworks.REAP, now);
    }

    /* access modifiers changed from: private */
    public void updateInetCondition(NetworkAgentInfo nai) {
        if (nai.everValidated && isDefaultNetwork(nai)) {
            int newInetCondition = nai.lastValidated ? 100 : 0;
            if (newInetCondition != this.mDefaultInetConditionPublished) {
                this.mDefaultInetConditionPublished = newInetCondition;
                sendInetConditionBroadcast(nai.networkInfo);
            }
        }
    }

    private void notifyLockdownVpn(NetworkAgentInfo nai) {
        synchronized (this.mVpns) {
            if (this.mLockdownTracker != null) {
                if (nai == null || !nai.isVPN()) {
                    this.mLockdownTracker.onNetworkInfoChanged();
                } else {
                    this.mLockdownTracker.onVpnStateChanged(nai.networkInfo);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateNetworkInfo(NetworkAgentInfo networkAgent, NetworkInfo newInfo) {
        NetworkInfo oldInfo;
        int i;
        NetworkInfo.State state = newInfo.getState();
        int oldScore = networkAgent.getCurrentScore();
        synchronized (networkAgent) {
            oldInfo = networkAgent.networkInfo;
            networkAgent.networkInfo = newInfo;
        }
        notifyLockdownVpn(networkAgent);
        StringBuilder sb = new StringBuilder();
        sb.append(networkAgent.name());
        sb.append(" EVENT_NETWORK_INFO_CHANGED, going from ");
        sb.append(oldInfo == null ? "null" : oldInfo.getState());
        sb.append(" to ");
        sb.append(state);
        log(sb.toString());
        if (!networkAgent.created && (state == NetworkInfo.State.CONNECTED || (state == NetworkInfo.State.CONNECTING && networkAgent.isVPN()))) {
            networkAgent.networkCapabilities.addCapability(19);
            if (createNativeNetwork(networkAgent)) {
                networkAgent.created = true;
            } else {
                return;
            }
        }
        if (!networkAgent.everConnected && state == NetworkInfo.State.CONNECTED) {
            networkAgent.everConnected = true;
            if (networkAgent.linkProperties == null) {
                String str = TAG;
                Slog.wtf(str, networkAgent.name() + " connected with null LinkProperties");
            }
            synchronized (networkAgent) {
                networkAgent.setNetworkCapabilities(networkAgent.networkCapabilities);
            }
            handlePerNetworkPrivateDnsConfig(networkAgent, this.mDnsManager.getPrivateDnsConfig());
            updateLinkProperties(networkAgent, new LinkProperties(networkAgent.linkProperties), (LinkProperties) null);
            if (networkAgent.networkMisc.acceptPartialConnectivity) {
                networkAgent.networkMonitor().setAcceptPartialConnectivity();
            }
            networkAgent.networkMonitor().notifyNetworkConnected(networkAgent.linkProperties, networkAgent.networkCapabilities);
            scheduleUnvalidatedPrompt(networkAgent);
            updateSignalStrengthThresholds(networkAgent, "CONNECT", (NetworkRequest) null);
            if (networkAgent.isVPN()) {
                updateAllVpnsCapabilities();
            }
            rematchNetworkAndRequests(networkAgent, ReapUnvalidatedNetworks.REAP, SystemClock.elapsedRealtime());
            notifyNetworkCallbacks(networkAgent, 524289);
        } else if (state == NetworkInfo.State.DISCONNECTED) {
            networkAgent.asyncChannel.disconnect();
            if (networkAgent.isVPN()) {
                updateUids(networkAgent, networkAgent.networkCapabilities, (NetworkCapabilities) null);
            }
            disconnectAndDestroyNetwork(networkAgent);
            if (networkAgent.isVPN()) {
                this.mProxyTracker.sendProxyBroadcast();
            }
        } else if ((oldInfo != null && oldInfo.getState() == NetworkInfo.State.SUSPENDED) || state == NetworkInfo.State.SUSPENDED) {
            if (networkAgent.getCurrentScore() != oldScore) {
                rematchAllNetworksAndRequests(networkAgent, oldScore);
            }
            updateCapabilities(networkAgent.getCurrentScore(), networkAgent, networkAgent.networkCapabilities);
            if (state == NetworkInfo.State.SUSPENDED) {
                i = 524297;
            } else {
                i = 524298;
            }
            notifyNetworkCallbacks(networkAgent, i);
            this.mLegacyTypeTracker.update(networkAgent);
        }
    }

    /* access modifiers changed from: private */
    public void updateNetworkScore(NetworkAgentInfo nai, int score) {
        if (VDBG || DDBG) {
            log("updateNetworkScore for " + nai.name() + " to " + score);
        }
        if (score < 0) {
            loge("updateNetworkScore for " + nai.name() + " got a negative score (" + score + ").  Bumping score to min of 0");
            score = 0;
        }
        int oldScore = nai.getCurrentScore();
        nai.setCurrentScore(score);
        rematchAllNetworksAndRequests(nai, oldScore);
        sendUpdatedScoreToFactories(nai);
    }

    /* access modifiers changed from: protected */
    public void notifyNetworkAvailable(NetworkAgentInfo nai, NetworkRequestInfo nri) {
        this.mHandler.removeMessages(20, nri);
        if (nri.mPendingIntent != null) {
            sendPendingIntentForRequest(nri, nai, 524290);
            return;
        }
        callCallbackForRequest(nri, nai, 524290, isUidNetworkingWithVpnBlocked(nri.mUid, this.mUidRules.get(nri.mUid), nai.networkCapabilities.isMetered(), this.mRestrictBackground));
    }

    private void maybeNotifyNetworkBlocked(NetworkAgentInfo nai, boolean oldMetered, boolean newMetered, boolean oldRestrictBackground, boolean newRestrictBackground) {
        boolean oldBlocked;
        boolean newBlocked;
        for (int i = 0; i < nai.numNetworkRequests(); i++) {
            NetworkRequestInfo nri = this.mNetworkRequests.get(nai.requestAt(i));
            int uidRules = this.mUidRules.get(nri.mUid);
            synchronized (this.mVpns) {
                oldBlocked = isUidNetworkingWithVpnBlocked(nri.mUid, uidRules, oldMetered, oldRestrictBackground);
                newBlocked = isUidNetworkingWithVpnBlocked(nri.mUid, uidRules, newMetered, newRestrictBackground);
            }
            if (oldBlocked != newBlocked) {
                callCallbackForRequest(nri, nai, 524299, encodeBool(newBlocked));
            }
        }
    }

    private void maybeNotifyNetworkBlockedForNewUidRules(int uid, int newRules) {
        boolean oldBlocked;
        boolean newBlocked;
        for (NetworkAgentInfo nai : this.mNetworkAgentInfos.values()) {
            boolean metered = nai.networkCapabilities.isMetered();
            synchronized (this.mVpns) {
                oldBlocked = isUidNetworkingWithVpnBlocked(uid, this.mUidRules.get(uid), metered, this.mRestrictBackground);
                newBlocked = isUidNetworkingWithVpnBlocked(uid, newRules, metered, this.mRestrictBackground);
            }
            if (oldBlocked != newBlocked) {
                int arg = encodeBool(newBlocked);
                for (int i = 0; i < nai.numNetworkRequests(); i++) {
                    NetworkRequestInfo nri = this.mNetworkRequests.get(nai.requestAt(i));
                    if (nri != null && nri.mUid == uid) {
                        callCallbackForRequest(nri, nai, 524299, arg);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void sendLegacyNetworkBroadcast(NetworkAgentInfo nai, NetworkInfo.DetailedState state, int type) {
        NetworkInfo info = new NetworkInfo(nai.networkInfo);
        info.setType(type);
        if (state != NetworkInfo.DetailedState.DISCONNECTED) {
            info.setDetailedState(state, (String) null, info.getExtraInfo());
            sendConnectedBroadcast(info);
            return;
        }
        info.setDetailedState(state, info.getReason(), info.getExtraInfo());
        Intent intent = new Intent("android.net.conn.CONNECTIVITY_CHANGE");
        intent.putExtra("networkInfo", info);
        intent.putExtra("networkType", info.getType());
        if (info.isFailover()) {
            intent.putExtra("isFailover", true);
            nai.networkInfo.setFailover(false);
        }
        if (info.getReason() != null) {
            intent.putExtra(PhoneWindowManager.SYSTEM_DIALOG_REASON_KEY, info.getReason());
        }
        if (info.getExtraInfo() != null) {
            intent.putExtra("extraInfo", info.getExtraInfo());
        }
        NetworkAgentInfo newDefaultAgent = null;
        if (nai.isSatisfyingRequest(this.mDefaultRequest.requestId)) {
            newDefaultAgent = getDefaultNetwork();
            if (newDefaultAgent != null) {
                intent.putExtra("otherNetwork", newDefaultAgent.networkInfo);
            } else {
                intent.putExtra("noConnectivity", true);
            }
        }
        intent.putExtra("inetCondition", this.mDefaultInetConditionPublished);
        sendStickyBroadcast(intent);
        if (newDefaultAgent != null) {
            sendConnectedBroadcast(newDefaultAgent.networkInfo);
        }
    }

    /* access modifiers changed from: protected */
    public void notifyNetworkCallbacks(NetworkAgentInfo networkAgent, int notifyType, int arg1) {
        if (VDBG || DDBG) {
            String notification = ConnectivityManager.getCallbackName(notifyType);
            log("notifyType " + notification + " for " + networkAgent.name());
        }
        for (int i = 0; i < networkAgent.numNetworkRequests(); i++) {
            NetworkRequest nr = networkAgent.requestAt(i);
            NetworkRequestInfo nri = this.mNetworkRequests.get(nr);
            if (VDBG) {
                log(" sending notification for " + nr);
            }
            if (nri.mPendingIntent == null) {
                callCallbackForRequest(nri, networkAgent, notifyType, arg1);
            } else {
                sendPendingIntentForRequest(nri, networkAgent, notifyType);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void notifyNetworkCallbacks(NetworkAgentInfo networkAgent, int notifyType) {
        notifyNetworkCallbacks(networkAgent, notifyType, 0);
    }

    private Network[] getDefaultNetworks() {
        ensureRunningOnConnectivityServiceThread();
        ArrayList<Network> defaultNetworks = new ArrayList<>();
        NetworkAgentInfo defaultNetwork = getDefaultNetwork();
        for (NetworkAgentInfo nai : this.mNetworkAgentInfos.values()) {
            if (nai.everConnected && (nai == defaultNetwork || nai.isVPN())) {
                defaultNetworks.add(nai.network);
            }
        }
        return (Network[]) defaultNetworks.toArray(new Network[0]);
    }

    private void notifyIfacesChangedForNetworkStats() {
        ensureRunningOnConnectivityServiceThread();
        String activeIface = null;
        LinkProperties activeLinkProperties = getActiveLinkProperties();
        if (activeLinkProperties != null) {
            activeIface = activeLinkProperties.getInterfaceName();
        }
        try {
            this.mStatsService.forceUpdateIfaces(getDefaultNetworks(), getAllVpnInfo(), getAllNetworkState(), activeIface);
        } catch (Exception e) {
        }
    }

    public boolean addVpnAddress(String address, int prefixLength) {
        boolean addAddress;
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            addAddress = this.mVpns.get(user).addAddress(address, prefixLength);
        }
        return addAddress;
    }

    public boolean removeVpnAddress(String address, int prefixLength) {
        boolean removeAddress;
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            removeAddress = this.mVpns.get(user).removeAddress(address, prefixLength);
        }
        return removeAddress;
    }

    public boolean setUnderlyingNetworksForVpn(Network[] networks) {
        boolean success;
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            success = this.mVpns.get(user).setUnderlyingNetworks(networks);
        }
        if (success) {
            this.mHandler.post(new Runnable() {
                public final void run() {
                    ConnectivityService.this.lambda$setUnderlyingNetworksForVpn$5$ConnectivityService();
                }
            });
        }
        return success;
    }

    public /* synthetic */ void lambda$setUnderlyingNetworksForVpn$5$ConnectivityService() {
        updateAllVpnsCapabilities();
        notifyIfacesChangedForNetworkStats();
    }

    public String getCaptivePortalServerUrl() {
        enforceConnectivityInternalPermission();
        String settingUrl = this.mContext.getResources().getString(17039782);
        if (!TextUtils.isEmpty(settingUrl)) {
            return settingUrl;
        }
        String settingUrl2 = Settings.Global.getString(this.mContext.getContentResolver(), "captive_portal_http_url");
        if (!TextUtils.isEmpty(settingUrl2)) {
            return settingUrl2;
        }
        return DEFAULT_CAPTIVE_PORTAL_HTTP_URL;
    }

    public void startNattKeepalive(Network network, int intervalSeconds, ISocketKeepaliveCallback cb, String srcAddr, int srcPort, String dstAddr) {
        enforceKeepalivePermission();
        this.mKeepaliveTracker.startNattKeepalive(getNetworkAgentInfoForNetwork(network), (FileDescriptor) null, intervalSeconds, cb, srcAddr, srcPort, dstAddr, 4500);
    }

    public void startNattKeepaliveWithFd(Network network, FileDescriptor fd, int resourceId, int intervalSeconds, ISocketKeepaliveCallback cb, String srcAddr, String dstAddr) {
        this.mKeepaliveTracker.startNattKeepalive(getNetworkAgentInfoForNetwork(network), fd, resourceId, intervalSeconds, cb, srcAddr, dstAddr, 4500);
    }

    public void startTcpKeepalive(Network network, FileDescriptor fd, int intervalSeconds, ISocketKeepaliveCallback cb) {
        enforceKeepalivePermission();
        this.mKeepaliveTracker.startTcpKeepalive(getNetworkAgentInfoForNetwork(network), fd, intervalSeconds, cb);
    }

    public void stopKeepalive(Network network, int slot) {
        InternalHandler internalHandler = this.mHandler;
        internalHandler.sendMessage(internalHandler.obtainMessage(528396, slot, 0, network));
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX INFO: finally extract failed */
    public void factoryReset() {
        enforceConnectivityInternalPermission();
        if (!this.mUserManager.hasUserRestriction("no_network_reset")) {
            int userId = UserHandle.getCallingUserId();
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() {
                public final void runOrThrow() {
                    ConnectivityService.this.lambda$factoryReset$6$ConnectivityService();
                }
            });
            setAirplaneMode(false);
            if (!this.mUserManager.hasUserRestriction("no_config_tethering")) {
                String pkgName = this.mContext.getOpPackageName();
                for (String tether : getTetheredIfaces()) {
                    untether(tether, pkgName);
                }
            }
            if (!this.mUserManager.hasUserRestriction("no_config_vpn")) {
                synchronized (this.mVpns) {
                    String alwaysOnPackage = getAlwaysOnVpnPackage(userId);
                    if (alwaysOnPackage != null) {
                        setAlwaysOnVpnPackage(userId, (String) null, false, (List<String>) null);
                        setVpnPackageAuthorization(alwaysOnPackage, userId, false);
                    }
                    if (this.mLockdownEnabled && userId == 0) {
                        long ident = Binder.clearCallingIdentity();
                        try {
                            this.mKeyStore.delete("LOCKDOWN_VPN");
                            this.mLockdownEnabled = false;
                            setLockdownTracker((LockdownVpnTracker) null);
                            Binder.restoreCallingIdentity(ident);
                        } catch (Throwable th) {
                            Binder.restoreCallingIdentity(ident);
                            throw th;
                        }
                    }
                    VpnConfig vpnConfig = getVpnConfig(userId);
                    if (vpnConfig != null) {
                        if (vpnConfig.legacy) {
                            prepareVpn("[Legacy VPN]", "[Legacy VPN]", userId);
                        } else {
                            setVpnPackageAuthorization(vpnConfig.user, userId, false);
                            prepareVpn((String) null, "[Legacy VPN]", userId);
                        }
                    }
                }
            }
            if (!this.mUserManager.hasUserRestriction("disallow_config_private_dns")) {
                Settings.Global.putString(this.mContext.getContentResolver(), "private_dns_mode", "opportunistic");
            }
            Settings.Global.putString(this.mContext.getContentResolver(), "network_avoid_bad_wifi", (String) null);
        }
    }

    public /* synthetic */ void lambda$factoryReset$6$ConnectivityService() throws Exception {
        IpMemoryStore.getMemoryStore(this.mContext).factoryReset();
    }

    public byte[] getNetworkWatchlistConfigHash() {
        NetworkWatchlistManager nwm = (NetworkWatchlistManager) this.mContext.getSystemService(NetworkWatchlistManager.class);
        if (nwm != null) {
            return nwm.getWatchlistConfigHash();
        }
        loge("Unable to get NetworkWatchlistManager");
        return null;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public MultinetworkPolicyTracker createMultinetworkPolicyTracker(Context c, Handler h, Runnable r) {
        return new MultinetworkPolicyTracker(c, h, r);
    }

    @VisibleForTesting
    public WakeupMessage makeWakeupMessage(Context c, Handler h, String s, int cmd, Object obj) {
        return new WakeupMessage(c, h, s, cmd, 0, 0, obj);
    }

    @VisibleForTesting
    public boolean hasService(String name) {
        return ServiceManager.checkService(name) != null;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public IpConnectivityMetrics.Logger metricsLogger() {
        return (IpConnectivityMetrics.Logger) Preconditions.checkNotNull((IpConnectivityMetrics.Logger) LocalServices.getService(IpConnectivityMetrics.Logger.class), "no IpConnectivityMetrics service");
    }

    private void logNetworkEvent(NetworkAgentInfo nai, int evtype) {
        this.mMetricsLog.log(nai.network.netId, nai.networkCapabilities.getTransportTypes(), new NetworkEvent(evtype));
    }

    /* access modifiers changed from: private */
    public static boolean toBool(int encodedBoolean) {
        return encodedBoolean != 0;
    }

    private static int encodeBool(boolean b) {
        return b;
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.ConnectivityService$ShellCmd r0 = new com.android.server.ConnectivityService$ShellCmd
            r1 = 0
            r0.<init>()
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    private class ShellCmd extends ShellCommand {
        private ShellCmd() {
        }

        public int onCommand(String cmd) {
            if (cmd == null) {
                return handleDefaultCommands(cmd);
            }
            PrintWriter pw = getOutPrintWriter();
            try {
                if ((cmd.hashCode() == 144736062 && cmd.equals("airplane-mode")) ? false : true) {
                    return handleDefaultCommands(cmd);
                }
                String action = getNextArg();
                if ("enable".equals(action)) {
                    ConnectivityService.this.setAirplaneMode(true);
                    return 0;
                } else if ("disable".equals(action)) {
                    ConnectivityService.this.setAirplaneMode(false);
                    return 0;
                } else if (action == null) {
                    pw.println(Settings.Global.getInt(ConnectivityService.this.mContext.getContentResolver(), "airplane_mode_on") == 0 ? "disabled" : "enabled");
                    return 0;
                } else {
                    onHelp();
                    return -1;
                }
            } catch (Exception e) {
                pw.println(e);
                return -1;
            }
        }

        public void onHelp() {
            PrintWriter pw = getOutPrintWriter();
            pw.println("Connectivity service commands:");
            pw.println("  help");
            pw.println("    Print this help text.");
            pw.println("  airplane-mode [enable|disable]");
            pw.println("    Turn airplane mode on or off.");
            pw.println("  airplane-mode");
            pw.println("    Get airplane mode.");
        }
    }

    @GuardedBy({"mVpns"})
    private Vpn getVpnIfOwner() {
        VpnInfo info;
        int uid = Binder.getCallingUid();
        Vpn vpn = this.mVpns.get(UserHandle.getUserId(uid));
        if (vpn == null || (info = vpn.getVpnInfo()) == null || info.ownerUid != uid) {
            return null;
        }
        return vpn;
    }

    private Vpn enforceActiveVpnOrNetworkStackPermission() {
        if (checkNetworkStackPermission()) {
            return null;
        }
        synchronized (this.mVpns) {
            Vpn vpn = getVpnIfOwner();
            if (vpn != null) {
                return vpn;
            }
            throw new SecurityException("App must either be an active VPN or have the NETWORK_STACK permission");
        }
    }

    public int getConnectionOwnerUid(ConnectionInfo connectionInfo) {
        Vpn vpn = enforceActiveVpnOrNetworkStackPermission();
        if (connectionInfo.protocol == OsConstants.IPPROTO_TCP || connectionInfo.protocol == OsConstants.IPPROTO_UDP) {
            int uid = InetDiagMessage.getConnectionOwnerUid(connectionInfo.protocol, connectionInfo.local, connectionInfo.remote);
            if (vpn == null || vpn.appliesToUid(uid)) {
                return uid;
            }
            return -1;
        }
        throw new IllegalArgumentException("Unsupported protocol " + connectionInfo.protocol);
    }

    public boolean isCallerCurrentAlwaysOnVpnApp() {
        boolean z;
        synchronized (this.mVpns) {
            Vpn vpn = getVpnIfOwner();
            z = vpn != null && vpn.getAlwaysOn();
        }
        return z;
    }

    public boolean isCallerCurrentAlwaysOnVpnLockdownApp() {
        boolean z;
        synchronized (this.mVpns) {
            Vpn vpn = getVpnIfOwner();
            z = vpn != null && vpn.getLockdown();
        }
        return z;
    }

    /* JADX WARNING: type inference failed for: r1v3, types: [com.android.server.TestNetworkService, android.os.IBinder] */
    public IBinder startOrGetTestNetworkService() {
        ? r1;
        synchronized (this.mTNSLock) {
            TestNetworkService.enforceTestNetworkPermissions(this.mContext);
            if (this.mTNS == null) {
                this.mTNS = new TestNetworkService(this.mContext, this.mNMS);
            }
            r1 = this.mTNS;
        }
        return r1;
    }

    private boolean isMobileNetwork(NetworkAgentInfo nai) {
        if (nai == null || nai.networkCapabilities == null || !nai.networkCapabilities.hasTransport(0)) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0018, code lost:
        r2 = r4.mSubscriptionManager;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean satisfiesMobileNetworkDataCheck(android.net.NetworkCapabilities r5) {
        /*
            r4 = this;
            r0 = 1
            if (r5 == 0) goto L_0x003c
            r1 = 0
            boolean r2 = r5.hasTransport(r1)
            if (r2 == 0) goto L_0x003c
            int r2 = r4.mPreferredSubId
            r3 = -1
            if (r2 != r3) goto L_0x0010
            return r0
        L_0x0010:
            r2 = 10
            boolean r2 = r5.hasCapability(r2)
            if (r2 == 0) goto L_0x002e
            android.telephony.SubscriptionManager r2 = r4.mSubscriptionManager
            if (r2 == 0) goto L_0x002e
            java.util.List r2 = r2.getActiveSubscriptionInfoList()
            if (r2 == 0) goto L_0x003a
            android.telephony.SubscriptionManager r2 = r4.mSubscriptionManager
            java.util.List r2 = r2.getActiveSubscriptionInfoList()
            int r2 = r2.size()
            if (r2 == 0) goto L_0x003a
        L_0x002e:
            android.net.NetworkSpecifier r2 = r5.getNetworkSpecifier()
            int r2 = r4.getIntSpecifier(r2)
            int r3 = r4.mPreferredSubId
            if (r2 != r3) goto L_0x003b
        L_0x003a:
            return r0
        L_0x003b:
            return r1
        L_0x003c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.satisfiesMobileNetworkDataCheck(android.net.NetworkCapabilities):boolean");
    }

    private boolean satisfiesMobileMultiNetworkDataCheck(NetworkCapabilities agentNc, NetworkCapabilities requestNc) {
        if (requestNc == null || getIntSpecifier(requestNc.getNetworkSpecifier()) >= 0) {
            return true;
        }
        return satisfiesMobileNetworkDataCheck(agentNc);
    }

    private int getIntSpecifier(NetworkSpecifier networkSpecifierObj) {
        String specifierStr = null;
        if (networkSpecifierObj != null && (networkSpecifierObj instanceof StringNetworkSpecifier)) {
            specifierStr = ((StringNetworkSpecifier) networkSpecifierObj).specifier;
        }
        if (specifierStr == null || specifierStr.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(specifierStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isBestMobileMultiNetwork(NetworkAgentInfo currentNetwork, NetworkCapabilities currentRequestNc, NetworkAgentInfo newNetwork, NetworkCapabilities newRequestNc, NetworkCapabilities requestNc) {
        if (!isMobileNetwork(currentNetwork) || !isMobileNetwork(newNetwork) || !satisfiesMobileMultiNetworkDataCheck(newRequestNc, requestNc) || satisfiesMobileMultiNetworkDataCheck(currentRequestNc, requestNc)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void handleUpdateTCPBuffersfor5G() {
        NetworkAgentInfo ntwAgent = getNetworkAgentInfoForNetwork(getActiveNetwork());
        log("handleUpdateTCPBuffersfor5G nai " + ntwAgent);
        if (ntwAgent != null) {
            updateTcpBufferSizes(ntwAgent);
        }
    }

    /* access modifiers changed from: private */
    public void handleUpdateActiveDataSubId(int subId) {
        log("Setting mPreferredSubId to " + subId);
        this.mPreferredSubId = subId;
        rematchAllNetworksAndRequests((NetworkAgentInfo) null, 0);
    }
}
