package com.android.server.wifi;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.DhcpResults;
import android.net.IpConfiguration;
import android.net.KeepalivePacketData;
import android.net.LinkProperties;
import android.net.MacAddress;
import android.net.MatchAllNetworkSpecifier;
import android.net.NattKeepalivePacketData;
import android.net.Network;
import android.net.NetworkAgent;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkMisc;
import android.net.NetworkSpecifier;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.SocketKeepalive;
import android.net.TcpKeepalivePacketData;
import android.net.ip.IIpClient;
import android.net.ip.IpClientCallbacks;
import android.net.ip.IpClientManager;
import android.net.shared.ProvisioningConfiguration;
import android.net.wifi.INetworkRequestMatchCallback;
import android.net.wifi.RssiPacketCountInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiDppConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkAgentSpecifier;
import android.net.wifi.WifiSsid;
import android.net.wifi.hotspot2.IProvisioningCallback;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.p2p.IWifiP2pManager;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings;
import android.system.OsConstants;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.StatsLog;
import android.view.MiuiWindowManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.MessageUtils;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.wifi.ClientModeManager;
import com.android.server.wifi.WifiBackupRestore;
import com.android.server.wifi.WifiMulticastLockManager;
import com.android.server.wifi.WifiNative;
import com.android.server.wifi.hotspot2.NetworkDetail;
import com.android.server.wifi.hotspot2.PasspointManager;
import com.android.server.wifi.hotspot2.anqp.Constants;
import com.android.server.wifi.p2p.WifiP2pServiceImpl;
import com.android.server.wifi.rtt.RttServiceImpl;
import com.android.server.wifi.scanner.ChannelHelper;
import com.android.server.wifi.util.NativeUtil;
import com.android.server.wifi.util.TelephonyUtil;
import com.android.server.wifi.util.WifiPermissionsUtil;
import com.android.server.wifi.util.WifiPermissionsWrapper;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import miui.provider.ExtraTelephony;
import miui.telephony.phonenumber.Prefix;
import miui.yellowpage.YellowPageContract;

public class ClientModeImpl extends StateMachine {
    static final int BASE = 131072;
    static final int CMD_ACCEPT_UNVALIDATED = 131225;
    static final int CMD_ADD_KEEPALIVE_PACKET_FILTER_TO_APF = 131281;
    static final int CMD_ADD_OR_UPDATE_NETWORK = 131124;
    static final int CMD_ADD_OR_UPDATE_PASSPOINT_CONFIG = 131178;
    static final int CMD_ASSOCIATED_BSSID = 131219;
    static final int CMD_BLUETOOTH_ADAPTER_STATE_CHANGE = 131103;
    static final int CMD_BOOT_COMPLETED = 131206;
    static final int CMD_CONFIG_ND_OFFLOAD = 131276;
    static final int CMD_DIAGS_CONNECT_TIMEOUT = 131324;
    static final int CMD_DISABLE_EPHEMERAL_NETWORK = 131170;
    static final int CMD_DISCONNECT = 131145;
    static final int CMD_DISCONNECTING_WATCHDOG_TIMER = 131168;
    public static final int CMD_DPP_ADD_BOOTSTRAP_QRCODE = 131374;
    public static final int CMD_DPP_AUTH_INIT = 131381;
    public static final int CMD_DPP_CONFIGURATOR_GET_KEY = 131382;
    public static final int CMD_DPP_CONF_ADD = 131379;
    public static final int CMD_DPP_CONF_REMOVE = 131380;
    public static final int CMD_DPP_GENERATE_BOOTSTRAP = 131373;
    public static final int CMD_DPP_GET_URI = 131376;
    public static final int CMD_DPP_LISTEN_START = 131377;
    public static final int CMD_DPP_LISTEN_STOP = 131378;
    public static final int CMD_DPP_REMOVE_BOOTSTRAP = 131375;
    static final int CMD_ENABLE_NETWORK = 131126;
    static final int CMD_ENABLE_RSSI_POLL = 131154;
    static final int CMD_ENABLE_TDLS = 131164;
    static final int CMD_ENABLE_WIFI_CONNECTIVITY_MANAGER = 131238;
    static final int CMD_GET_ALL_MATCHING_FQDNS_FOR_SCAN_RESULTS = 131240;
    static final int CMD_GET_CONFIGURED_NETWORKS = 131131;
    static final int CMD_GET_LINK_LAYER_STATS = 131135;
    static final int CMD_GET_MATCHING_OSU_PROVIDERS = 131181;
    static final int CMD_GET_MATCHING_PASSPOINT_CONFIGS_FOR_OSU_PROVIDERS = 131182;
    static final int CMD_GET_PASSPOINT_CONFIGS = 131180;
    static final int CMD_GET_PRIVILEGED_CONFIGURED_NETWORKS = 131134;
    static final int CMD_GET_SUPPORTED_FEATURES = 131133;
    static final int CMD_GET_WIFI_CONFIGS_FOR_PASSPOINT_PROFILES = 131184;
    static final int CMD_INITIALIZE = 131207;
    static final int CMD_INSTALL_PACKET_FILTER = 131274;
    static final int CMD_IPV4_PROVISIONING_FAILURE = 131273;
    static final int CMD_IPV4_PROVISIONING_SUCCESS = 131272;
    static final int CMD_IP_CONFIGURATION_LOST = 131211;
    static final int CMD_IP_CONFIGURATION_SUCCESSFUL = 131210;
    static final int CMD_IP_REACHABILITY_LOST = 131221;
    private static final int CMD_IP_REACHABILITY_SESSION_END = 131383;
    static final int CMD_MATCH_PROVIDER_NETWORK = 131177;
    static final int CMD_NETWORK_STATUS = 131220;
    static final int CMD_ONESHOT_RSSI_POLL = 131156;
    private static final int CMD_POST_DHCP_ACTION = 131329;
    @VisibleForTesting
    static final int CMD_PRE_DHCP_ACTION = 131327;
    private static final int CMD_PRE_DHCP_ACTION_COMPLETE = 131328;
    static final int CMD_QUERY_OSU_ICON = 131176;
    static final int CMD_READ_PACKET_FILTER = 131280;
    static final int CMD_REASSOCIATE = 131147;
    static final int CMD_RECONNECT = 131146;
    static final int CMD_REMOVE_APP_CONFIGURATIONS = 131169;
    static final int CMD_REMOVE_KEEPALIVE_PACKET_FILTER_FROM_APF = 131282;
    static final int CMD_REMOVE_NETWORK = 131125;
    static final int CMD_REMOVE_PASSPOINT_CONFIG = 131179;
    static final int CMD_REMOVE_USER_CONFIGURATIONS = 131224;
    static final int CMD_RESET_SIM_NETWORKS = 131173;
    static final int CMD_RESET_SUPPLICANT_STATE = 131183;
    static final int CMD_ROAM_WATCHDOG_TIMER = 131166;
    static final int CMD_RSSI_POLL = 131155;
    static final int CMD_RSSI_THRESHOLD_BREACHED = 131236;
    static final int CMD_SCREEN_STATE_CHANGED = 131167;
    static final int CMD_SET_FALLBACK_PACKET_FILTERING = 131275;
    static final int CMD_SET_HIGH_PERF_MODE = 131149;
    static final int CMD_SET_OPERATIONAL_MODE = 131144;
    static final int CMD_SET_SUSPEND_OPT_ENABLED = 131158;
    static final int CMD_START_CONNECT = 131215;
    static final int CMD_START_IP_PACKET_OFFLOAD = 131232;
    static final int CMD_START_ROAM = 131217;
    static final int CMD_START_RSSI_MONITORING_OFFLOAD = 131234;
    private static final int CMD_START_SUBSCRIPTION_PROVISIONING = 131326;
    static final int CMD_STOP_IP_PACKET_OFFLOAD = 131233;
    static final int CMD_STOP_RSSI_MONITORING_OFFLOAD = 131235;
    static final int CMD_TARGET_BSSID = 131213;
    static final int CMD_UNWANTED_NETWORK = 131216;
    static final int CMD_UPDATE_LINKPROPERTIES = 131212;
    static final int CMD_USER_STOP = 131279;
    static final int CMD_USER_SWITCH = 131277;
    static final int CMD_USER_UNLOCK = 131278;
    private static final int CMD_WAIT_IPCLIENT_OBTAINED = 131384;
    public static final int CONNECT_MODE = 1;
    private static final int DEFAULT_POLL_RSSI_INTERVAL_MSECS = 3000;
    @VisibleForTesting
    public static final long DIAGS_CONNECT_TIMEOUT_MILLIS = 60000;
    public static final int DISABLED_MODE = 4;
    static final int DISCONNECTING_GUARD_TIMER_MSEC = 5000;
    private static final String EXTRA_OSU_ICON_QUERY_BSSID = "BSSID";
    private static final String EXTRA_OSU_ICON_QUERY_FILENAME = "FILENAME";
    private static final String EXTRA_OSU_PROVIDER = "OsuProvider";
    private static final String EXTRA_PACKAGE_NAME = "PackageName";
    private static final String EXTRA_PASSPOINT_CONFIGURATION = "PasspointConfiguration";
    private static final String EXTRA_UID = "uid";
    private static final int FAILURE = -1;
    private static final String GOOGLE_OUI = "DA-A1-19";
    private static final int IPCLIENT_TIMEOUT_MS = 10000;
    @VisibleForTesting
    public static final int LAST_SELECTED_NETWORK_EXPIRATION_AGE_MILLIS = 30000;
    private static final int LINK_FLAPPING_DEBOUNCE_MSEC = 4000;
    private static final String LOGD_LEVEL_DEBUG = "D";
    private static final String LOGD_LEVEL_VERBOSE = "V";
    private static final int MESSAGE_HANDLING_STATUS_DEFERRED = -4;
    private static final int MESSAGE_HANDLING_STATUS_DISCARD = -5;
    private static final int MESSAGE_HANDLING_STATUS_FAIL = -2;
    private static final int MESSAGE_HANDLING_STATUS_HANDLING_ERROR = -7;
    private static final int MESSAGE_HANDLING_STATUS_LOOPED = -6;
    private static final int MESSAGE_HANDLING_STATUS_OBSOLETE = -3;
    private static final int MESSAGE_HANDLING_STATUS_OK = 1;
    private static final int MESSAGE_HANDLING_STATUS_PROCESSED = 2;
    private static final int MESSAGE_HANDLING_STATUS_REFUSED = -1;
    private static final int MESSAGE_HANDLING_STATUS_UNKNOWN = 0;
    private static final String NETWORKTYPE = "WIFI";
    private static final int NETWORK_STATUS_UNWANTED_DISABLE_AUTOJOIN = 2;
    private static final int NETWORK_STATUS_UNWANTED_DISCONNECT = 0;
    private static final int NETWORK_STATUS_UNWANTED_VALIDATION_FAILED = 1;
    @VisibleForTesting
    public static final short NUM_LOG_RECS_NORMAL = 100;
    @VisibleForTesting
    public static final short NUM_LOG_RECS_VERBOSE = 1000;
    @VisibleForTesting
    public static final short NUM_LOG_RECS_VERBOSE_LOW_MEMORY = 200;
    private static final int ONE_HOUR_MILLI = 3600000;
    static final int ROAM_GUARD_TIMER_MSEC = 15000;
    public static final int SCAN_ONLY_MODE = 2;
    public static final int SCAN_ONLY_WITH_WIFI_OFF_MODE = 3;
    private static final int SUCCESS = 1;
    public static final String SUPPLICANT_BSSID_ANY = "any";
    private static final int SUPPLICANT_RESTART_INTERVAL_MSECS = 5000;
    private static final int SUPPLICANT_RESTART_TRIES = 5;
    private static final int SUSPEND_DUE_TO_DHCP = 1;
    private static final int SUSPEND_DUE_TO_HIGH_PERF = 2;
    private static final int SUSPEND_DUE_TO_SCREEN = 4;
    private static final String SYSTEM_PROPERTY_LOG_CONTROL_WIFIHAL = "log.tag.WifiHAL";
    private static final String TAG = "WifiClientModeImpl";
    private static final int WAIT_IPCLIENT_OBTAINED_INTERVAL_MS = 4000;
    public static final WorkSource WIFI_WORK_SOURCE = new WorkSource(1010);
    private static final SparseArray<String> sGetWhatToString = MessageUtils.findMessageNames(sMessageClasses);
    private static final Class[] sMessageClasses = {AsyncChannel.class, ClientModeImpl.class};
    private static int sScanAlarmIntentCount = 0;
    private final BackupManagerProxy mBackupManagerProxy;
    private final IBatteryStats mBatteryStats;
    /* access modifiers changed from: private */
    public boolean mBluetoothConnectionActive = false;
    private final BuildProperties mBuildProperties;
    private ClientModeManager.Listener mClientModeCallback = null;
    /* access modifiers changed from: private */
    public final Clock mClock;
    private ConnectivityManager mCm;
    private State mConnectModeState = new ConnectModeState();
    /* access modifiers changed from: private */
    public boolean mConnectedMacRandomzationSupported;
    /* access modifiers changed from: private */
    public State mConnectedState = new ConnectedState();
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public final WifiCountryCode mCountryCode;
    private String mDataInterfaceName;
    private State mDefaultState = new DefaultState();
    private DhcpResults mDhcpResults;
    private final Object mDhcpResultsLock = new Object();
    /* access modifiers changed from: private */
    public boolean mDidBlackListBSSID = false;
    /* access modifiers changed from: private */
    public boolean mDisconnectOnlyOnInitialIpReachability = true;
    /* access modifiers changed from: private */
    public State mDisconnectedState = new DisconnectedState();
    /* access modifiers changed from: private */
    public State mDisconnectingState = new DisconnectingState();
    int mDisconnectingWatchdogCount = 0;
    /* access modifiers changed from: private */
    public boolean mEnableRssiPolling = false;
    /* access modifiers changed from: private */
    public FrameworkFacade mFacade;
    /* access modifiers changed from: private */
    public WifiConfiguration mFilsConfig;
    /* access modifiers changed from: private */
    public State mFilsState = new FilsState();
    /* access modifiers changed from: private */
    public String mInterfaceName;
    /* access modifiers changed from: private */
    public volatile IpClientManager mIpClient;
    private IpClientCallbacksImpl mIpClientCallbacks;
    /* access modifiers changed from: private */
    public boolean mIpReachabilityDisconnectEnabled = true;
    /* access modifiers changed from: private */
    public boolean mIpReachabilityMonitorActive = true;
    /* access modifiers changed from: private */
    public boolean mIsAutoRoaming = false;
    /* access modifiers changed from: private */
    public boolean mIsFilsConnection = false;
    /* access modifiers changed from: private */
    public boolean mIsIpClientStarted = false;
    private boolean mIsRunning = false;
    private State mL2ConnectedState = new L2ConnectedState();
    /* access modifiers changed from: private */
    public String mLastBssid;
    /* access modifiers changed from: private */
    public long mLastConnectAttemptTimestamp = 0;
    /* access modifiers changed from: private */
    public long mLastDriverRoamAttempt = 0;
    private Pair<String, String> mLastL2KeyAndGroupHint = null;
    /* access modifiers changed from: private */
    public WifiLinkLayerStats mLastLinkLayerStats;
    private long mLastLinkLayerStatsUpdate = 0;
    /* access modifiers changed from: private */
    public int mLastNetworkId;
    private long mLastOntimeReportTimeStamp = 0;
    private final WorkSource mLastRunningWifiUids = new WorkSource();
    private long mLastScreenStateChangeTimeStamp = 0;
    /* access modifiers changed from: private */
    public int mLastSignalLevel = -1;
    /* access modifiers changed from: private */
    public final LinkProbeManager mLinkProbeManager;
    /* access modifiers changed from: private */
    public LinkProperties mLinkProperties;
    private final McastLockManagerFilterController mMcastLockManagerFilterController;
    /* access modifiers changed from: private */
    public int mMessageHandlingStatus = 0;
    private boolean mModeChange = false;
    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkAgentLock"})
    public WifiNetworkAgent mNetworkAgent;
    /* access modifiers changed from: private */
    public final Object mNetworkAgentLock = new Object();
    private final NetworkCapabilities mNetworkCapabilitiesFilter = new NetworkCapabilities();
    /* access modifiers changed from: private */
    public WifiNetworkFactory mNetworkFactory;
    /* access modifiers changed from: private */
    public NetworkInfo mNetworkInfo;
    /* access modifiers changed from: private */
    public final NetworkMisc mNetworkMisc = new NetworkMisc();
    private AtomicInteger mNullMessageCounter = new AtomicInteger(0);
    /* access modifiers changed from: private */
    public State mObtainingIpState = new ObtainingIpState();
    private int mOnTime = 0;
    private int mOnTimeLastReport = 0;
    private int mOnTimeScreenStateChange = 0;
    /* access modifiers changed from: private */
    public int mOperationalMode = 4;
    /* access modifiers changed from: private */
    public final AtomicBoolean mP2pConnected = new AtomicBoolean(false);
    private final boolean mP2pSupported;
    /* access modifiers changed from: private */
    public final PasspointManager mPasspointManager;
    private int mPeriodicScanToken = 0;
    /* access modifiers changed from: private */
    public volatile int mPollRssiIntervalMsecs = DEFAULT_POLL_RSSI_INTERVAL_MSECS;
    private final PropertyService mPropertyService;
    private AsyncChannel mReplyChannel = new AsyncChannel();
    private boolean mReportedRunning = false;
    private int mRoamFailCount = 0;
    int mRoamWatchdogCount = 0;
    /* access modifiers changed from: private */
    public State mRoamingState = new RoamingState();
    /* access modifiers changed from: private */
    public int mRssiPollToken = 0;
    /* access modifiers changed from: private */
    public byte[] mRssiRanges;
    int mRunningBeaconCount = 0;
    private final WorkSource mRunningWifiUids = new WorkSource();
    private int mRxTime = 0;
    private int mRxTimeLastReport = 0;
    /* access modifiers changed from: private */
    public final SarManager mSarManager;
    /* access modifiers changed from: private */
    public boolean mScreenOn = false;
    private long mSupplicantScanIntervalMs;
    /* access modifiers changed from: private */
    public SupplicantStateTracker mSupplicantStateTracker;
    private int mSuspendOptNeedsDisabled = 0;
    /* access modifiers changed from: private */
    public PowerManager.WakeLock mSuspendWakeLock;
    /* access modifiers changed from: private */
    public int mTargetNetworkId = -1;
    /* access modifiers changed from: private */
    public String mTargetRoamBSSID = "any";
    /* access modifiers changed from: private */
    public WifiConfiguration mTargetWifiConfiguration = null;
    /* access modifiers changed from: private */
    public final String mTcpBufferSizes;
    private TelephonyManager mTelephonyManager;
    /* access modifiers changed from: private */
    public boolean mTemporarilyDisconnectWifi = false;
    private WifiTrafficPoller mTrafficPoller;
    private int mTxTime = 0;
    private int mTxTimeLastReport = 0;
    private UntrustedWifiNetworkFactory mUntrustedNetworkFactory;
    /* access modifiers changed from: private */
    public AtomicBoolean mUserWantsSuspendOpt = new AtomicBoolean(true);
    /* access modifiers changed from: private */
    public boolean mVerboseLoggingEnabled = false;
    private PowerManager.WakeLock mWakeLock;
    /* access modifiers changed from: private */
    public final WifiConfigManager mWifiConfigManager;
    /* access modifiers changed from: private */
    public final WifiConnectivityManager mWifiConnectivityManager;
    /* access modifiers changed from: private */
    public final WifiDataStall mWifiDataStall;
    /* access modifiers changed from: private */
    public BaseWifiDiagnostics mWifiDiagnostics;
    /* access modifiers changed from: private */
    public final ExtendedWifiInfo mWifiInfo;
    /* access modifiers changed from: private */
    public final WifiInjector mWifiInjector;
    /* access modifiers changed from: private */
    public final WifiMetrics mWifiMetrics;
    private final WifiMonitor mWifiMonitor;
    /* access modifiers changed from: private */
    public final WifiNative mWifiNative;
    private WifiNetworkSuggestionsManager mWifiNetworkSuggestionsManager;
    /* access modifiers changed from: private */
    public AsyncChannel mWifiP2pChannel;
    /* access modifiers changed from: private */
    public final WifiPermissionsUtil mWifiPermissionsUtil;
    private final WifiPermissionsWrapper mWifiPermissionsWrapper;
    /* access modifiers changed from: private */
    public final WifiScoreCard mWifiScoreCard;
    /* access modifiers changed from: private */
    public final WifiScoreReport mWifiScoreReport;
    private final AtomicInteger mWifiState = new AtomicInteger(1);
    /* access modifiers changed from: private */
    public WifiStateTracker mWifiStateTracker;
    /* access modifiers changed from: private */
    public final WifiTrafficPoller mWifiTrafficPoller;
    /* access modifiers changed from: private */
    public final WrongPasswordNotifier mWrongPasswordNotifier;
    private WifiP2pServiceImpl wifiP2pServiceImpl;

    static /* synthetic */ int access$12308(ClientModeImpl x0) {
        int i = x0.mRoamFailCount;
        x0.mRoamFailCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$8708(ClientModeImpl x0) {
        int i = x0.mRssiPollToken;
        x0.mRssiPollToken = i + 1;
        return i;
    }

    /* access modifiers changed from: protected */
    public void loge(String s) {
        Log.e(getName(), s);
    }

    /* access modifiers changed from: protected */
    public void logd(String s) {
        Log.d(getName(), s);
    }

    /* access modifiers changed from: protected */
    public void log(String s) {
        Log.d(getName(), s);
    }

    public WifiScoreReport getWifiScoreReport() {
        return this.mWifiScoreReport;
    }

    /* access modifiers changed from: private */
    public void processRssiThreshold(byte curRssi, int reason, WifiNative.WifiRssiEventHandler rssiHandler) {
        if (curRssi == Byte.MAX_VALUE || curRssi == Byte.MIN_VALUE) {
            Log.wtf(TAG, "processRssiThreshold: Invalid rssi " + curRssi);
            return;
        }
        int i = 0;
        while (true) {
            byte[] bArr = this.mRssiRanges;
            if (i >= bArr.length) {
                return;
            }
            if (curRssi < bArr[i]) {
                byte maxRssi = bArr[i];
                byte minRssi = bArr[i - 1];
                this.mWifiInfo.setRssi(curRssi);
                updateCapabilities();
                int ret = startRssiMonitoringOffload(maxRssi, minRssi, rssiHandler);
                Log.d(TAG, "Re-program RSSI thresholds for " + getWhatToString(reason) + ": [" + minRssi + ", " + maxRssi + "], curRssi=" + curRssi + " ret=" + ret);
                return;
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    public int getPollRssiIntervalMsecs() {
        return this.mPollRssiIntervalMsecs;
    }

    /* access modifiers changed from: package-private */
    public void setPollRssiIntervalMsecs(int newPollIntervalMsecs) {
        this.mPollRssiIntervalMsecs = newPollIntervalMsecs;
    }

    public boolean clearTargetBssid(String dbg) {
        WifiConfiguration config = this.mWifiConfigManager.getConfiguredNetwork(this.mTargetNetworkId);
        if (config == null) {
            return false;
        }
        String bssid = "any";
        if (config.BSSID != null) {
            bssid = config.BSSID;
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "force BSSID to " + bssid + "due to config");
            }
        }
        if (this.mVerboseLoggingEnabled) {
            logd(dbg + " clearTargetBssid " + bssid + " key=" + config.configKey());
        }
        this.mTargetRoamBSSID = bssid;
        return this.mWifiNative.setConfiguredNetworkBSSID(this.mInterfaceName, bssid);
    }

    /* access modifiers changed from: private */
    public boolean setTargetBssid(WifiConfiguration config, String bssid) {
        if (config == null || bssid == null) {
            return false;
        }
        if (config.BSSID != null) {
            bssid = config.BSSID;
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "force BSSID to " + bssid + "due to config");
            }
        }
        if (this.mVerboseLoggingEnabled) {
            Log.d(TAG, "setTargetBssid set to " + bssid + " key=" + config.configKey());
        }
        this.mTargetRoamBSSID = bssid;
        config.getNetworkSelectionStatus().setNetworkSelectionBSSID(bssid);
        return true;
    }

    /* access modifiers changed from: private */
    public TelephonyManager getTelephonyManager() {
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = this.mWifiInjector.makeTelephonyManager();
        }
        return this.mTelephonyManager;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ClientModeImpl(Context context, FrameworkFacade facade, Looper looper, UserManager userManager, WifiInjector wifiInjector, BackupManagerProxy backupManagerProxy, WifiCountryCode countryCode, WifiNative wifiNative, WrongPasswordNotifier wrongPasswordNotifier, SarManager sarManager, WifiTrafficPoller wifiTrafficPoller, LinkProbeManager linkProbeManager) {
        super(TAG, looper);
        Context context2 = context;
        this.mWifiInjector = wifiInjector;
        this.mWifiMetrics = this.mWifiInjector.getWifiMetrics();
        this.mClock = wifiInjector.getClock();
        this.mPropertyService = wifiInjector.getPropertyService();
        this.mBuildProperties = wifiInjector.getBuildProperties();
        this.mWifiScoreCard = wifiInjector.getWifiScoreCard();
        this.mContext = context2;
        this.mFacade = facade;
        this.mWifiNative = wifiNative;
        this.mBackupManagerProxy = backupManagerProxy;
        this.mWrongPasswordNotifier = wrongPasswordNotifier;
        this.mSarManager = sarManager;
        this.mWifiTrafficPoller = wifiTrafficPoller;
        this.mLinkProbeManager = linkProbeManager;
        this.mNetworkInfo = new NetworkInfo(1, 0, "WIFI", Prefix.EMPTY);
        this.mBatteryStats = IBatteryStats.Stub.asInterface(this.mFacade.getService("batterystats"));
        this.mWifiStateTracker = wifiInjector.getWifiStateTracker();
        IBinder b = this.mFacade.getService("network_management");
        this.mP2pSupported = this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.direct");
        this.mWifiPermissionsUtil = this.mWifiInjector.getWifiPermissionsUtil();
        this.mWifiConfigManager = this.mWifiInjector.getWifiConfigManager();
        this.mPasspointManager = this.mWifiInjector.getPasspointManager();
        this.mWifiMonitor = this.mWifiInjector.getWifiMonitor();
        this.mWifiDiagnostics = this.mWifiInjector.getWifiDiagnostics();
        this.mWifiPermissionsWrapper = this.mWifiInjector.getWifiPermissionsWrapper();
        this.mWifiDataStall = this.mWifiInjector.getWifiDataStall();
        this.mWifiInfo = new ExtendedWifiInfo();
        this.mSupplicantStateTracker = this.mFacade.makeSupplicantStateTracker(context2, this.mWifiConfigManager, getHandler());
        this.mWifiConnectivityManager = this.mWifiInjector.makeWifiConnectivityManager(this);
        this.mLinkProperties = new LinkProperties();
        this.mMcastLockManagerFilterController = new McastLockManagerFilterController();
        this.mNetworkInfo.setIsAvailable(false);
        this.mLastBssid = null;
        this.mLastNetworkId = -1;
        this.mLastSignalLevel = -1;
        this.mCountryCode = countryCode;
        this.mWifiScoreReport = new WifiScoreReport(this.mWifiInjector.getScoringParams(), this.mClock);
        this.mNetworkCapabilitiesFilter.addTransportType(1);
        this.mNetworkCapabilitiesFilter.addCapability(12);
        this.mNetworkCapabilitiesFilter.addCapability(11);
        this.mNetworkCapabilitiesFilter.addCapability(18);
        this.mNetworkCapabilitiesFilter.addCapability(20);
        this.mNetworkCapabilitiesFilter.addCapability(13);
        this.mNetworkCapabilitiesFilter.setLinkUpstreamBandwidthKbps(1048576);
        this.mNetworkCapabilitiesFilter.setLinkDownstreamBandwidthKbps(1048576);
        this.mNetworkCapabilitiesFilter.setNetworkSpecifier(new MatchAllNetworkSpecifier());
        this.mNetworkFactory = this.mWifiInjector.makeWifiNetworkFactory(this.mNetworkCapabilitiesFilter, this.mWifiConnectivityManager);
        this.mUntrustedNetworkFactory = this.mWifiInjector.makeUntrustedWifiNetworkFactory(this.mNetworkCapabilitiesFilter, this.mWifiConnectivityManager);
        this.mWifiNetworkSuggestionsManager = this.mWifiInjector.getWifiNetworkSuggestionsManager();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.SCREEN_ON")) {
                    ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_SCREEN_STATE_CHANGED, 1);
                } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                    ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_SCREEN_STATE_CHANGED, 0);
                }
            }
        }, filter);
        IntentFilter intentFilter = filter;
        IBinder iBinder = b;
        this.mFacade.registerContentObserver(this.mContext, Settings.Global.getUriFor("wifi_suspend_optimizations_enabled"), false, new ContentObserver(getHandler()) {
            public void onChange(boolean selfChange) {
                AtomicBoolean access$200 = ClientModeImpl.this.mUserWantsSuspendOpt;
                boolean z = true;
                if (ClientModeImpl.this.mFacade.getIntegerSetting(ClientModeImpl.this.mContext, "wifi_suspend_optimizations_enabled", 1) != 1) {
                    z = false;
                }
                access$200.set(z);
            }
        });
        this.mUserWantsSuspendOpt.set(this.mFacade.getIntegerSetting(this.mContext, "wifi_suspend_optimizations_enabled", 1) == 1);
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mWakeLock = powerManager.newWakeLock(1, getName());
        this.mSuspendWakeLock = powerManager.newWakeLock(1, "WifiSuspend");
        this.mSuspendWakeLock.setReferenceCounted(false);
        this.mConnectedMacRandomzationSupported = this.mContext.getResources().getBoolean(17891582);
        this.mWifiInfo.setEnableConnectedMacRandomization(this.mConnectedMacRandomzationSupported);
        this.mWifiMetrics.setIsMacRandomizationOn(this.mConnectedMacRandomzationSupported);
        this.mTcpBufferSizes = this.mContext.getResources().getString(17039806);
        this.mDisconnectOnlyOnInitialIpReachability = SystemProperties.get("persist.vendor.wifi.enableIpReachabilityMonitorPeriod", "1").equals("1");
        addState(this.mDefaultState);
        addState(this.mConnectModeState, this.mDefaultState);
        addState(this.mL2ConnectedState, this.mConnectModeState);
        addState(this.mObtainingIpState, this.mL2ConnectedState);
        addState(this.mConnectedState, this.mL2ConnectedState);
        addState(this.mRoamingState, this.mL2ConnectedState);
        addState(this.mDisconnectingState, this.mConnectModeState);
        addState(this.mDisconnectedState, this.mConnectModeState);
        addState(this.mFilsState, this.mConnectModeState);
        setInitialState(this.mDefaultState);
        setLogRecSize(ActivityManager.isLowRamDeviceStatic() ? 100 : 1000);
        setLogOnlyTransitions(false);
    }

    public void start() {
        ClientModeImpl.super.start();
        handleScreenStateChanged(((PowerManager) this.mContext.getSystemService("power")).isInteractive());
    }

    private void registerForWifiMonitorEvents() {
        this.mWifiMonitor.registerHandler(this.mInterfaceName, CMD_TARGET_BSSID, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, CMD_ASSOCIATED_BSSID, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.ANQP_DONE_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.ASSOCIATION_REJECTION_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.AUTHENTICATION_FAILURE_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.GAS_QUERY_DONE_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.GAS_QUERY_START_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.HS20_REMEDIATION_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.NETWORK_CONNECTION_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.NETWORK_DISCONNECTION_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.RX_HS20_ANQP_ICON_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.SUP_REQUEST_IDENTITY, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.SUP_REQUEST_SIM_AUTH, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.ASSOCIATION_REJECTION_EVENT, this.mWifiMetrics.getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.AUTHENTICATION_FAILURE_EVENT, this.mWifiMetrics.getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.NETWORK_CONNECTION_EVENT, this.mWifiMetrics.getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.NETWORK_DISCONNECTION_EVENT, this.mWifiMetrics.getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT, this.mWifiMetrics.getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, CMD_ASSOCIATED_BSSID, this.mWifiMetrics.getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, CMD_TARGET_BSSID, this.mWifiMetrics.getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.FILS_NETWORK_CONNECTION_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiMonitor.DPP_EVENT, getHandler());
    }

    /* access modifiers changed from: private */
    public void setMulticastFilter(boolean enabled) {
        if (this.mIpClient != null) {
            this.mIpClient.setMulticastFilter(enabled);
        }
    }

    class McastLockManagerFilterController implements WifiMulticastLockManager.FilterController {
        McastLockManagerFilterController() {
        }

        public void startFilteringMulticastPackets() {
            ClientModeImpl.this.setMulticastFilter(true);
        }

        public void stopFilteringMulticastPackets() {
            ClientModeImpl.this.setMulticastFilter(false);
        }
    }

    class IpClientCallbacksImpl extends IpClientCallbacks {
        private final ConditionVariable mWaitForCreationCv = new ConditionVariable(false);
        private final ConditionVariable mWaitForStopCv = new ConditionVariable(false);

        IpClientCallbacksImpl() {
        }

        public void onIpClientCreated(IIpClient ipClient) {
            ClientModeImpl clientModeImpl = ClientModeImpl.this;
            IpClientManager unused = clientModeImpl.mIpClient = new IpClientManager(ipClient, clientModeImpl.getName());
            this.mWaitForCreationCv.open();
        }

        public void onPreDhcpAction() {
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_PRE_DHCP_ACTION);
        }

        public void onPostDhcpAction() {
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_POST_DHCP_ACTION);
        }

        public void onNewDhcpResults(DhcpResults dhcpResults) {
            if (dhcpResults != null) {
                ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_IPV4_PROVISIONING_SUCCESS, dhcpResults);
            } else {
                ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_IPV4_PROVISIONING_FAILURE);
            }
        }

        public void onProvisioningSuccess(LinkProperties newLp) {
            ClientModeImpl.this.mWifiMetrics.logStaEvent(7);
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_UPDATE_LINKPROPERTIES, newLp);
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_IP_CONFIGURATION_SUCCESSFUL);
        }

        public void onProvisioningFailure(LinkProperties newLp) {
            ClientModeImpl.this.mWifiMetrics.logStaEvent(8);
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_IP_CONFIGURATION_LOST);
        }

        public void onLinkPropertiesChange(LinkProperties newLp) {
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_UPDATE_LINKPROPERTIES, newLp);
        }

        public void onReachabilityLost(String logMsg) {
            ClientModeImpl.this.mWifiMetrics.logStaEvent(9);
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_IP_REACHABILITY_LOST, logMsg);
        }

        public void installPacketFilter(byte[] filter) {
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_INSTALL_PACKET_FILTER, filter);
        }

        public void startReadPacketFilter() {
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_READ_PACKET_FILTER);
        }

        public void setFallbackMulticastFilter(boolean enabled) {
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_SET_FALLBACK_PACKET_FILTERING, Boolean.valueOf(enabled));
        }

        public void setNeighborDiscoveryOffload(boolean enabled) {
            ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_CONFIG_ND_OFFLOAD, enabled);
        }

        public void onQuit() {
            this.mWaitForStopCv.open();
        }

        /* access modifiers changed from: package-private */
        public boolean awaitCreation() {
            return this.mWaitForCreationCv.block(RttServiceImpl.HAL_AWARE_RANGING_TIMEOUT_MS);
        }

        /* access modifiers changed from: package-private */
        public boolean awaitShutdown() {
            return this.mWaitForStopCv.block(RttServiceImpl.HAL_AWARE_RANGING_TIMEOUT_MS);
        }
    }

    /* access modifiers changed from: private */
    public void stopIpClient() {
        handlePostDhcpSetup();
        if (this.mIpClient != null) {
            this.mIpClient.stop();
            this.mIsIpClientStarted = false;
        }
    }

    public void setWifiDiagnostics(BaseWifiDiagnostics WifiDiagnostics) {
        this.mWifiDiagnostics = WifiDiagnostics;
    }

    public void setTrafficPoller(WifiTrafficPoller trafficPoller) {
        this.mTrafficPoller = trafficPoller;
        WifiTrafficPoller wifiTrafficPoller = this.mTrafficPoller;
        if (wifiTrafficPoller != null) {
            wifiTrafficPoller.setInterface(this.mDataInterfaceName);
        }
    }

    /* access modifiers changed from: package-private */
    public void setSupplicantLogLevel() {
        this.mWifiNative.setSupplicantLogLevel(this.mVerboseLoggingEnabled);
    }

    public void enableVerboseLogging(int verbose) {
        int i = 1000;
        if (verbose > 0) {
            this.mVerboseLoggingEnabled = true;
            if (ActivityManager.isLowRamDeviceStatic()) {
                i = ChannelHelper.SCAN_PERIOD_PER_CHANNEL_MS;
            }
            setLogRecSize(i);
        } else {
            this.mVerboseLoggingEnabled = false;
            if (ActivityManager.isLowRamDeviceStatic()) {
                i = 100;
            }
            setLogRecSize(i);
        }
        configureVerboseHalLogging(this.mVerboseLoggingEnabled);
        setSupplicantLogLevel();
        this.mCountryCode.enableVerboseLogging(verbose);
        this.mWifiScoreReport.enableVerboseLogging(this.mVerboseLoggingEnabled);
        this.mWifiDiagnostics.startLogging(this.mVerboseLoggingEnabled);
        WifiP2pServiceImpl wifiP2pServiceImpl2 = this.wifiP2pServiceImpl;
        if (wifiP2pServiceImpl2 != null) {
            wifiP2pServiceImpl2.enableVerboseLogging(verbose);
        }
        this.mWifiMonitor.enableVerboseLogging(verbose);
        this.mWifiNative.enableVerboseLogging(verbose);
        this.mWifiConfigManager.enableVerboseLogging(verbose);
        this.mSupplicantStateTracker.enableVerboseLogging(verbose);
        this.mPasspointManager.enableVerboseLogging(verbose);
        this.mNetworkFactory.enableVerboseLogging(verbose);
        this.mLinkProbeManager.enableVerboseLogging(this.mVerboseLoggingEnabled);
        WifiConnectivityManager wifiConnectivityManager = this.mWifiConnectivityManager;
        if (wifiConnectivityManager != null) {
            wifiConnectivityManager.enableVerboseLogging(verbose);
        }
    }

    private void configureVerboseHalLogging(boolean enableVerbose) {
        if (!this.mBuildProperties.isUserBuild()) {
            this.mPropertyService.set(SYSTEM_PROPERTY_LOG_CONTROL_WIFIHAL, enableVerbose ? LOGD_LEVEL_VERBOSE : LOGD_LEVEL_DEBUG);
        }
    }

    private void updateDataInterface() {
        String dataInterfaceName = this.mWifiNative.getFstDataInterfaceName();
        if (TextUtils.isEmpty(dataInterfaceName)) {
            dataInterfaceName = this.mInterfaceName;
        }
        this.mDataInterfaceName = dataInterfaceName;
        if (this.mIpClient != null) {
            this.mIpClient.shutdown();
            this.mIpClientCallbacks.awaitShutdown();
        }
        this.mIpClientCallbacks = new IpClientCallbacksImpl();
        this.mFacade.makeIpClient(this.mContext, this.mDataInterfaceName, this.mIpClientCallbacks);
        if (!this.mIpClientCallbacks.awaitCreation()) {
            loge("Timeout waiting for IpClient");
        }
        setMulticastFilter(true);
        WifiTrafficPoller wifiTrafficPoller = this.mTrafficPoller;
        if (wifiTrafficPoller != null) {
            wifiTrafficPoller.setInterface(this.mDataInterfaceName);
        }
    }

    private boolean setRandomMacOui() {
        String oui = this.mContext.getResources().getString(17039803);
        if (TextUtils.isEmpty(oui)) {
            oui = GOOGLE_OUI;
        }
        String[] ouiParts = oui.split("-");
        byte[] ouiBytes = {(byte) (Integer.parseInt(ouiParts[0], 16) & Constants.BYTE_MASK), (byte) (Integer.parseInt(ouiParts[1], 16) & Constants.BYTE_MASK), (byte) (Integer.parseInt(ouiParts[2], 16) & Constants.BYTE_MASK)};
        logd("Setting OUI to " + oui);
        return this.mWifiNative.setScanningMacOui(this.mInterfaceName, ouiBytes);
    }

    /* access modifiers changed from: private */
    public boolean connectToUserSelectNetwork(int netId, int uid, boolean forceReconnect) {
        logd("connectToUserSelectNetwork netId " + netId + ", uid " + uid + ", forceReconnect = " + forceReconnect);
        WifiConfiguration config = this.mWifiConfigManager.getConfiguredNetwork(netId);
        if (config == null) {
            loge("connectToUserSelectNetwork Invalid network Id=" + netId);
            return false;
        }
        if (!this.mWifiConfigManager.enableNetwork(netId, true, uid) || !this.mWifiConfigManager.updateLastConnectUid(netId, uid)) {
            logi("connectToUserSelectNetwork Allowing uid " + uid + " with insufficient permissions to connect=" + netId);
        } else if (this.mWifiPermissionsUtil.checkNetworkSettingsPermission(uid)) {
            this.mWifiConnectivityManager.setUserConnectChoice(netId);
        }
        if (forceReconnect || this.mWifiInfo.getNetworkId() != netId) {
            this.mWifiConnectivityManager.prepareForForcedConnection(netId);
            if (uid == 1000) {
                this.mWifiMetrics.setNominatorForNetwork(config.networkId, 1);
            }
            startConnectToNetwork(netId, uid, "any");
        } else {
            logi("connectToUserSelectNetwork already connecting/connected=" + netId);
        }
        return true;
    }

    public Messenger getMessenger() {
        return new Messenger(getHandler());
    }

    /* access modifiers changed from: package-private */
    public String reportOnTime() {
        long now = this.mClock.getWallClockMillis();
        StringBuilder sb = new StringBuilder();
        int i = this.mOnTime;
        int on = i - this.mOnTimeLastReport;
        this.mOnTimeLastReport = i;
        int i2 = this.mTxTime;
        int tx = i2 - this.mTxTimeLastReport;
        this.mTxTimeLastReport = i2;
        int i3 = this.mRxTime;
        int rx = i3 - this.mRxTimeLastReport;
        this.mRxTimeLastReport = i3;
        int period = (int) (now - this.mLastOntimeReportTimeStamp);
        this.mLastOntimeReportTimeStamp = now;
        sb.append(String.format("[on:%d tx:%d rx:%d period:%d]", new Object[]{Integer.valueOf(on), Integer.valueOf(tx), Integer.valueOf(rx), Integer.valueOf(period)}));
        sb.append(String.format(" from screen [on:%d period:%d]", new Object[]{Integer.valueOf(this.mOnTime - this.mOnTimeScreenStateChange), Integer.valueOf((int) (now - this.mLastScreenStateChangeTimeStamp))}));
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public WifiLinkLayerStats getWifiLinkLayerStats() {
        if (this.mInterfaceName == null) {
            loge("getWifiLinkLayerStats called without an interface");
            return null;
        }
        this.mLastLinkLayerStatsUpdate = this.mClock.getWallClockMillis();
        WifiLinkLayerStats stats = this.mWifiNative.getWifiLinkLayerStats(this.mInterfaceName);
        if (stats != null) {
            this.mOnTime = stats.on_time;
            this.mTxTime = stats.tx_time;
            this.mRxTime = stats.rx_time;
            this.mRunningBeaconCount = stats.beacon_rx;
            this.mWifiInfo.updatePacketRates(stats, this.mLastLinkLayerStatsUpdate);
        } else {
            long mTxPkts = this.mFacade.getTxPackets(this.mDataInterfaceName);
            this.mWifiInfo.updatePacketRates(mTxPkts, this.mFacade.getRxPackets(this.mDataInterfaceName), this.mLastLinkLayerStatsUpdate);
        }
        return stats;
    }

    private byte[] getDstMacForKeepalive(KeepalivePacketData packetData) throws SocketKeepalive.InvalidPacketException {
        try {
            return NativeUtil.macAddressToByteArray(macAddressFromRoute(RouteInfo.selectBestRoute(this.mLinkProperties.getRoutes(), packetData.dstAddress).getGateway().getHostAddress()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SocketKeepalive.InvalidPacketException(-21);
        }
    }

    private static int getEtherProtoForKeepalive(KeepalivePacketData packetData) throws SocketKeepalive.InvalidPacketException {
        if (packetData.dstAddress instanceof Inet4Address) {
            return OsConstants.ETH_P_IP;
        }
        if (packetData.dstAddress instanceof Inet6Address) {
            return OsConstants.ETH_P_IPV6;
        }
        throw new SocketKeepalive.InvalidPacketException(-21);
    }

    /* access modifiers changed from: private */
    public int startWifiIPPacketOffload(int slot, KeepalivePacketData packetData, int intervalSeconds) {
        SocketKeepalive.InvalidPacketException e;
        try {
            byte[] packet = packetData.getPacket();
            try {
                byte[] dstMac = getDstMacForKeepalive(packetData);
                try {
                    int ret = this.mWifiNative.startSendingOffloadedPacket(this.mInterfaceName, slot, dstMac, packet, getEtherProtoForKeepalive(packetData), intervalSeconds * 1000);
                    if (ret == 0) {
                        return 0;
                    }
                    loge("startWifiIPPacketOffload(" + slot + ", " + intervalSeconds + "): hardware error " + ret);
                    return -31;
                } catch (SocketKeepalive.InvalidPacketException e2) {
                    e = e2;
                    byte[] bArr = dstMac;
                    return e.error;
                }
            } catch (SocketKeepalive.InvalidPacketException e3) {
                e = e3;
                return e.error;
            }
        } catch (SocketKeepalive.InvalidPacketException e4) {
            e = e4;
            return e.error;
        }
    }

    /* access modifiers changed from: private */
    public int stopWifiIPPacketOffload(int slot) {
        int ret = this.mWifiNative.stopSendingOffloadedPacket(this.mInterfaceName, slot);
        if (ret == 0) {
            return 0;
        }
        loge("stopWifiIPPacketOffload(" + slot + "): hardware error " + ret);
        return -31;
    }

    private int startRssiMonitoringOffload(byte maxRssi, byte minRssi, WifiNative.WifiRssiEventHandler rssiHandler) {
        return this.mWifiNative.startRssiMonitoring(this.mInterfaceName, maxRssi, minRssi, rssiHandler);
    }

    /* access modifiers changed from: private */
    public int stopRssiMonitoringOffload() {
        return this.mWifiNative.stopRssiMonitoring(this.mInterfaceName);
    }

    public void setWifiStateForApiCalls(int newState) {
        if (newState == 0 || newState == 1 || newState == 2 || newState == 3 || newState == 4) {
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "setting wifi state to: " + newState);
            }
            this.mWifiState.set(newState);
            return;
        }
        Log.d(TAG, "attempted to set an invalid state: " + newState);
    }

    public int syncGetWifiState() {
        return this.mWifiState.get();
    }

    public String syncGetWifiStateByName() {
        int i = this.mWifiState.get();
        if (i == 0) {
            return "disabling";
        }
        if (i == 1) {
            return "disabled";
        }
        if (i == 2) {
            return "enabling";
        }
        if (i == 3) {
            return "enabled";
        }
        if (i != 4) {
            return "[invalid state]";
        }
        return "unknown state";
    }

    public boolean isConnected() {
        return getCurrentState() == this.mConnectedState;
    }

    public boolean isDisconnected() {
        return getCurrentState() == this.mDisconnectedState || getCurrentState() == this.mFilsState;
    }

    public boolean isSupplicantTransientState() {
        SupplicantState supplicantState = this.mWifiInfo.getSupplicantState();
        if (supplicantState == SupplicantState.ASSOCIATING || supplicantState == SupplicantState.AUTHENTICATING || supplicantState == SupplicantState.FOUR_WAY_HANDSHAKE || supplicantState == SupplicantState.GROUP_HANDSHAKE) {
            if (!this.mVerboseLoggingEnabled) {
                return true;
            }
            Log.d(TAG, "Supplicant is under transient state: " + supplicantState);
            return true;
        } else if (!this.mVerboseLoggingEnabled) {
            return false;
        } else {
            Log.d(TAG, "Supplicant is under steady state: " + supplicantState);
            return false;
        }
    }

    public WifiInfo syncRequestConnectionInfo() {
        return new WifiInfo(this.mWifiInfo);
    }

    public WifiInfo getWifiInfo() {
        return this.mWifiInfo;
    }

    public DhcpResults syncGetDhcpResults() {
        DhcpResults dhcpResults;
        synchronized (this.mDhcpResultsLock) {
            dhcpResults = new DhcpResults(this.mDhcpResults);
        }
        return dhcpResults;
    }

    public void handleIfaceDestroyed() {
        handleNetworkDisconnect();
    }

    public void setOperationalMode(int mode, String ifaceName) {
        if (this.mVerboseLoggingEnabled) {
            log("setting operational mode to " + String.valueOf(mode) + " for iface: " + ifaceName);
        }
        this.mModeChange = true;
        if (mode != 1) {
            transitionTo(this.mDefaultState);
        } else if (ifaceName != null) {
            this.mInterfaceName = ifaceName;
            transitionTo(this.mDisconnectedState);
        } else {
            Log.e(TAG, "supposed to enter connect mode, but iface is null -> DefaultState");
            transitionTo(this.mDefaultState);
        }
        sendMessageAtFrontOfQueue(CMD_SET_OPERATIONAL_MODE);
    }

    public void takeBugReport(String bugTitle, String bugDetail) {
        this.mWifiDiagnostics.takeBugReport(bugTitle, bugDetail);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public int getOperationalModeForTest() {
        return this.mOperationalMode;
    }

    /* access modifiers changed from: protected */
    public WifiMulticastLockManager.FilterController getMcastLockManagerFilterController() {
        return this.mMcastLockManagerFilterController;
    }

    public boolean syncQueryPasspointIcon(AsyncChannel channel, long bssid, String fileName) {
        Bundle bundle = new Bundle();
        bundle.putLong("BSSID", bssid);
        bundle.putString(EXTRA_OSU_ICON_QUERY_FILENAME, fileName);
        Message resultMsg = channel.sendMessageSynchronously(CMD_QUERY_OSU_ICON, bundle);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result == 1;
    }

    public int matchProviderWithCurrentNetwork(AsyncChannel channel, String fqdn) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_MATCH_PROVIDER_NETWORK, fqdn);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public void deauthenticateNetwork(AsyncChannel channel, long holdoff, boolean ess) {
    }

    public void disableEphemeralNetwork(String ssid) {
        if (ssid != null) {
            sendMessage(CMD_DISABLE_EPHEMERAL_NETWORK, ssid);
        }
    }

    public void disconnectCommand() {
        sendMessage(CMD_DISCONNECT);
    }

    public void disconnectCommand(int uid, int reason) {
        sendMessage(CMD_DISCONNECT, uid, reason);
    }

    public void reconnectCommand(WorkSource workSource) {
        sendMessage(CMD_RECONNECT, workSource);
    }

    public void reassociateCommand() {
        sendMessage(CMD_REASSOCIATE);
    }

    private boolean messageIsNull(Message resultMsg) {
        if (resultMsg != null) {
            return false;
        }
        if (this.mNullMessageCounter.getAndIncrement() <= 0) {
            return true;
        }
        Log.wtf(TAG, "Persistent null Message", new RuntimeException());
        return true;
    }

    public int syncAddOrUpdateNetwork(AsyncChannel channel, WifiConfiguration config) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_ADD_OR_UPDATE_NETWORK, config);
        if (messageIsNull(resultMsg)) {
            return -1;
        }
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public List<WifiConfiguration> syncGetConfiguredNetworks(int uuid, AsyncChannel channel, int targetUid) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_CONFIGURED_NETWORKS, uuid, targetUid);
        if (messageIsNull(resultMsg)) {
            return null;
        }
        List<WifiConfiguration> result = (List) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    public List<WifiConfiguration> syncGetPrivilegedConfiguredNetwork(AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_PRIVILEGED_CONFIGURED_NETWORKS);
        if (messageIsNull(resultMsg)) {
            return null;
        }
        List<WifiConfiguration> result = (List) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    /* access modifiers changed from: package-private */
    public Map<String, Map<Integer, List<ScanResult>>> syncGetAllMatchingFqdnsForScanResults(List<ScanResult> scanResults, AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_ALL_MATCHING_FQDNS_FOR_SCAN_RESULTS, scanResults);
        if (messageIsNull(resultMsg)) {
            return new HashMap();
        }
        Map<String, Map<Integer, List<ScanResult>>> configs = (Map) resultMsg.obj;
        resultMsg.recycle();
        return configs;
    }

    public Map<OsuProvider, List<ScanResult>> syncGetMatchingOsuProviders(List<ScanResult> scanResults, AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_MATCHING_OSU_PROVIDERS, scanResults);
        if (messageIsNull(resultMsg)) {
            return new HashMap();
        }
        Map<OsuProvider, List<ScanResult>> providers = (Map) resultMsg.obj;
        resultMsg.recycle();
        return providers;
    }

    public Map<OsuProvider, PasspointConfiguration> syncGetMatchingPasspointConfigsForOsuProviders(List<OsuProvider> osuProviders, AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_MATCHING_PASSPOINT_CONFIGS_FOR_OSU_PROVIDERS, osuProviders);
        if (messageIsNull(resultMsg)) {
            return new HashMap();
        }
        Map<OsuProvider, PasspointConfiguration> result = (Map) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    public List<WifiConfiguration> syncGetWifiConfigsForPasspointProfiles(List<String> fqdnList, AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_WIFI_CONFIGS_FOR_PASSPOINT_PROFILES, fqdnList);
        if (messageIsNull(resultMsg)) {
            return new ArrayList();
        }
        List<WifiConfiguration> result = (List) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    public boolean syncAddOrUpdatePasspointConfig(AsyncChannel channel, PasspointConfiguration config, int uid, String packageName) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_UID, uid);
        bundle.putString(EXTRA_PACKAGE_NAME, packageName);
        bundle.putParcelable(EXTRA_PASSPOINT_CONFIGURATION, config);
        Message resultMsg = channel.sendMessageSynchronously(CMD_ADD_OR_UPDATE_PASSPOINT_CONFIG, bundle);
        boolean z = false;
        if (messageIsNull(resultMsg)) {
            return false;
        }
        if (resultMsg.arg1 == 1) {
            z = true;
        }
        boolean result = z;
        resultMsg.recycle();
        return result;
    }

    public boolean syncRemovePasspointConfig(AsyncChannel channel, String fqdn) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_REMOVE_PASSPOINT_CONFIG, fqdn);
        boolean z = false;
        if (messageIsNull(resultMsg)) {
            return false;
        }
        if (resultMsg.arg1 == 1) {
            z = true;
        }
        boolean result = z;
        resultMsg.recycle();
        return result;
    }

    public List<PasspointConfiguration> syncGetPasspointConfigs(AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_PASSPOINT_CONFIGS);
        if (messageIsNull(resultMsg)) {
            return null;
        }
        List<PasspointConfiguration> result = (List) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    public boolean syncStartSubscriptionProvisioning(int callingUid, OsuProvider provider, IProvisioningCallback callback, AsyncChannel channel) {
        Message msg = Message.obtain();
        msg.what = CMD_START_SUBSCRIPTION_PROVISIONING;
        msg.arg1 = callingUid;
        msg.obj = callback;
        msg.getData().putParcelable(EXTRA_OSU_PROVIDER, provider);
        Message resultMsg = channel.sendMessageSynchronously(msg);
        boolean z = false;
        if (messageIsNull(resultMsg)) {
            return false;
        }
        if (resultMsg.arg1 != 0) {
            z = true;
        }
        boolean result = z;
        resultMsg.recycle();
        return result;
    }

    public long syncGetSupportedFeatures(AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_SUPPORTED_FEATURES);
        if (messageIsNull(resultMsg)) {
            return 0;
        }
        long supportedFeatureSet = ((Long) resultMsg.obj).longValue();
        resultMsg.recycle();
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.rtt")) {
            return supportedFeatureSet & -385;
        }
        return supportedFeatureSet;
    }

    public WifiLinkLayerStats syncGetLinkLayerStats(AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_LINK_LAYER_STATS);
        if (messageIsNull(resultMsg)) {
            return null;
        }
        WifiLinkLayerStats result = (WifiLinkLayerStats) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    public boolean syncRemoveNetwork(AsyncChannel channel, int networkId) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_REMOVE_NETWORK, networkId);
        boolean z = false;
        if (messageIsNull(resultMsg)) {
            return false;
        }
        if (resultMsg.arg1 != -1) {
            z = true;
        }
        boolean result = z;
        resultMsg.recycle();
        return result;
    }

    public boolean syncEnableNetwork(AsyncChannel channel, int netId, boolean disableOthers) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_ENABLE_NETWORK, netId, disableOthers);
        boolean z = false;
        if (messageIsNull(resultMsg)) {
            return false;
        }
        if (resultMsg.arg1 != -1) {
            z = true;
        }
        boolean result = z;
        resultMsg.recycle();
        return result;
    }

    public boolean syncDisableNetwork(AsyncChannel channel, int netId) {
        Message resultMsg = channel.sendMessageSynchronously(151569, netId);
        boolean result = resultMsg.what != 151570;
        if (messageIsNull(resultMsg)) {
            return false;
        }
        resultMsg.recycle();
        return result;
    }

    public void enableRssiPolling(boolean enabled) {
        sendMessage(CMD_ENABLE_RSSI_POLL, enabled, 0);
    }

    public void setHighPerfModeEnabled(boolean enable) {
        sendMessage(CMD_SET_HIGH_PERF_MODE, enable, 0);
    }

    public synchronized void resetSimAuthNetworks(boolean simPresent) {
        sendMessage(CMD_RESET_SIM_NETWORKS, simPresent ? 1 : 0);
    }

    public Network getCurrentNetwork() {
        synchronized (this.mNetworkAgentLock) {
            if (this.mNetworkAgent == null) {
                return null;
            }
            Network network = new Network(this.mNetworkAgent.netId);
            return network;
        }
    }

    public void enableTdls(String remoteMacAddress, boolean enable) {
        sendMessage(CMD_ENABLE_TDLS, (int) enable, 0, remoteMacAddress);
    }

    public void sendBluetoothAdapterStateChange(int state) {
        sendMessage(CMD_BLUETOOTH_ADAPTER_STATE_CHANGE, state, 0);
    }

    public void removeAppConfigs(String packageName, int uid) {
        ApplicationInfo ai = new ApplicationInfo();
        ai.packageName = packageName;
        ai.uid = uid;
        sendMessage(CMD_REMOVE_APP_CONFIGURATIONS, ai);
    }

    public void removeUserConfigs(int userId) {
        sendMessage(CMD_REMOVE_USER_CONFIGURATIONS, userId);
    }

    public void updateBatteryWorkSource(WorkSource newSource) {
        synchronized (this.mRunningWifiUids) {
            if (newSource != null) {
                try {
                    this.mRunningWifiUids.set(newSource);
                } catch (RemoteException e) {
                }
            }
            if (this.mIsRunning) {
                if (!this.mReportedRunning) {
                    this.mBatteryStats.noteWifiRunning(this.mRunningWifiUids);
                    this.mLastRunningWifiUids.set(this.mRunningWifiUids);
                    this.mReportedRunning = true;
                } else if (!this.mLastRunningWifiUids.equals(this.mRunningWifiUids)) {
                    this.mBatteryStats.noteWifiRunningChanged(this.mLastRunningWifiUids, this.mRunningWifiUids);
                    this.mLastRunningWifiUids.set(this.mRunningWifiUids);
                }
            } else if (this.mReportedRunning) {
                this.mBatteryStats.noteWifiStopped(this.mLastRunningWifiUids);
                this.mLastRunningWifiUids.clear();
                this.mReportedRunning = false;
            }
            this.mWakeLock.setWorkSource(newSource);
        }
    }

    public void dumpIpClient(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mIpClient != null) {
            pw.println("IpClient logs have moved to dumpsys network_stack");
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        ClientModeImpl.super.dump(fd, pw, args);
        this.mSupplicantStateTracker.dump(fd, pw, args);
        pw.println("mLinkProperties " + this.mLinkProperties);
        pw.println("mWifiInfo " + this.mWifiInfo);
        pw.println("mDhcpResults " + this.mDhcpResults);
        pw.println("mNetworkInfo " + this.mNetworkInfo);
        pw.println("mLastSignalLevel " + this.mLastSignalLevel);
        pw.println("mLastBssid " + this.mLastBssid);
        pw.println("mLastNetworkId " + this.mLastNetworkId);
        pw.println("mOperationalMode " + this.mOperationalMode);
        pw.println("mUserWantsSuspendOpt " + this.mUserWantsSuspendOpt);
        pw.println("mSuspendOptNeedsDisabled " + this.mSuspendOptNeedsDisabled);
        this.mCountryCode.dump(fd, pw, args);
        this.mNetworkFactory.dump(fd, pw, args);
        this.mUntrustedNetworkFactory.dump(fd, pw, args);
        pw.println("Wlan Wake Reasons:" + this.mWifiNative.getWlanWakeReasonCount());
        pw.println();
        this.mWifiConfigManager.dump(fd, pw, args);
        pw.println();
        this.mPasspointManager.dump(pw);
        pw.println();
        this.mWifiDiagnostics.captureBugReportData(7);
        this.mWifiDiagnostics.dump(fd, pw, args);
        dumpIpClient(fd, pw, args);
        this.mWifiConnectivityManager.dump(fd, pw, args);
        this.mWifiInjector.getWakeupController().dump(fd, pw, args);
        this.mLinkProbeManager.dump(fd, pw, args);
        this.mWifiInjector.getWifiLastResortWatchdog().dump(fd, pw, args);
    }

    public void handleBootCompleted() {
        sendMessage(CMD_BOOT_COMPLETED);
    }

    public void handleUserSwitch(int userId) {
        sendMessage(CMD_USER_SWITCH, userId);
    }

    public void handleUserUnlock(int userId) {
        sendMessage(CMD_USER_UNLOCK, userId);
    }

    public void handleUserStop(int userId) {
        sendMessage(CMD_USER_STOP, userId);
    }

    /* access modifiers changed from: private */
    public void logStateAndMessage(Message message, State state) {
        this.mMessageHandlingStatus = 0;
        if (this.mVerboseLoggingEnabled) {
            logd(" " + state.getClass().getSimpleName() + " " + getLogRecString(message));
        }
    }

    /* access modifiers changed from: protected */
    public boolean recordLogRec(Message msg) {
        return msg.what != CMD_RSSI_POLL ? true : true;
    }

    /* access modifiers changed from: protected */
    public String getLogRecString(Message msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("screen=");
        sb.append(this.mScreenOn ? "on" : "off");
        if (this.mMessageHandlingStatus != 0) {
            sb.append("(");
            sb.append(this.mMessageHandlingStatus);
            sb.append(")");
        }
        if (msg.sendingUid > 0 && msg.sendingUid != 1010) {
            sb.append(" uid=" + msg.sendingUid);
        }
        switch (msg.what) {
            case CMD_ADD_OR_UPDATE_NETWORK /*131124*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                if (msg.obj != null) {
                    WifiConfiguration config = (WifiConfiguration) msg.obj;
                    sb.append(" ");
                    sb.append(config.configKey());
                    sb.append(" prio=");
                    sb.append(config.priority);
                    sb.append(" status=");
                    sb.append(config.status);
                    if (config.BSSID != null) {
                        sb.append(" ");
                        sb.append(config.BSSID);
                    }
                    WifiConfiguration curConfig = getCurrentWifiConfiguration();
                    if (curConfig != null) {
                        if (!curConfig.configKey().equals(config.configKey())) {
                            sb.append(" current=");
                            sb.append(curConfig.configKey());
                            sb.append(" prio=");
                            sb.append(curConfig.priority);
                            sb.append(" status=");
                            sb.append(curConfig.status);
                            break;
                        } else {
                            sb.append(" is current");
                            break;
                        }
                    }
                }
                break;
            case CMD_ENABLE_NETWORK /*131126*/:
            case 151569:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                String key = this.mWifiConfigManager.getLastSelectedNetworkConfigKey();
                if (key != null) {
                    sb.append(" last=");
                    sb.append(key);
                }
                WifiConfiguration config2 = this.mWifiConfigManager.getConfiguredNetwork(msg.arg1);
                if (config2 != null && (key == null || !config2.configKey().equals(key))) {
                    sb.append(" target=");
                    sb.append(key);
                    break;
                }
            case CMD_GET_CONFIGURED_NETWORKS /*131131*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                sb.append(" num=");
                sb.append(this.mWifiConfigManager.getConfiguredNetworks().size());
                break;
            case CMD_RSSI_POLL /*131155*/:
            case CMD_ONESHOT_RSSI_POLL /*131156*/:
            case CMD_UNWANTED_NETWORK /*131216*/:
            case 151572:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                if (!(this.mWifiInfo.getSSID() == null || this.mWifiInfo.getSSID() == null)) {
                    sb.append(" ");
                    sb.append(this.mWifiInfo.getSSID());
                }
                if (this.mWifiInfo.getBSSID() != null) {
                    sb.append(" ");
                    sb.append(this.mWifiInfo.getBSSID());
                }
                sb.append(" rssi=");
                sb.append(this.mWifiInfo.getRssi());
                sb.append(" f=");
                sb.append(this.mWifiInfo.getFrequency());
                sb.append(" sc=");
                sb.append(this.mWifiInfo.score);
                sb.append(" link=");
                sb.append(this.mWifiInfo.getLinkSpeed());
                sb.append(String.format(" tx=%.1f,", new Object[]{Double.valueOf(this.mWifiInfo.txSuccessRate)}));
                sb.append(String.format(" %.1f,", new Object[]{Double.valueOf(this.mWifiInfo.txRetriesRate)}));
                sb.append(String.format(" %.1f ", new Object[]{Double.valueOf(this.mWifiInfo.txBadRate)}));
                sb.append(String.format(" rx=%.1f", new Object[]{Double.valueOf(this.mWifiInfo.rxSuccessRate)}));
                sb.append(String.format(" bcn=%d", new Object[]{Integer.valueOf(this.mRunningBeaconCount)}));
                String report = reportOnTime();
                if (report != null) {
                    sb.append(" ");
                    sb.append(report);
                }
                sb.append(String.format(" score=%d", new Object[]{Integer.valueOf(this.mWifiInfo.score)}));
                break;
            case CMD_ROAM_WATCHDOG_TIMER /*131166*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                sb.append(" cur=");
                sb.append(this.mRoamWatchdogCount);
                break;
            case CMD_DISCONNECTING_WATCHDOG_TIMER /*131168*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                sb.append(" cur=");
                sb.append(this.mDisconnectingWatchdogCount);
                break;
            case CMD_IP_CONFIGURATION_LOST /*131211*/:
                int count = -1;
                WifiConfiguration c = getCurrentWifiConfiguration();
                if (c != null) {
                    count = c.getNetworkSelectionStatus().getDisableReasonCounter(4);
                }
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                sb.append(" failures: ");
                sb.append(Integer.toString(count));
                sb.append("/");
                sb.append(Integer.toString(this.mFacade.getIntegerSetting(this.mContext, "wifi_max_dhcp_retry_count", 0)));
                if (this.mWifiInfo.getBSSID() != null) {
                    sb.append(" ");
                    sb.append(this.mWifiInfo.getBSSID());
                }
                sb.append(String.format(" bcn=%d", new Object[]{Integer.valueOf(this.mRunningBeaconCount)}));
                break;
            case CMD_UPDATE_LINKPROPERTIES /*131212*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                if (this.mLinkProperties != null) {
                    sb.append(" ");
                    sb.append(getLinkPropertiesSummary(this.mLinkProperties));
                    break;
                }
                break;
            case CMD_TARGET_BSSID /*131213*/:
            case CMD_ASSOCIATED_BSSID /*131219*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                if (msg.obj != null) {
                    sb.append(" BSSID=");
                    sb.append((String) msg.obj);
                }
                if (this.mTargetRoamBSSID != null) {
                    sb.append(" Target=");
                    sb.append(this.mTargetRoamBSSID);
                }
                sb.append(" roam=");
                sb.append(Boolean.toString(this.mIsAutoRoaming));
                break;
            case CMD_START_CONNECT /*131215*/:
            case 151553:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                WifiConfiguration config3 = this.mWifiConfigManager.getConfiguredNetwork(msg.arg1);
                if (config3 != null) {
                    sb.append(" ");
                    sb.append(config3.configKey());
                }
                if (this.mTargetRoamBSSID != null) {
                    sb.append(" ");
                    sb.append(this.mTargetRoamBSSID);
                }
                sb.append(" roam=");
                sb.append(Boolean.toString(this.mIsAutoRoaming));
                WifiConfiguration config4 = getCurrentWifiConfiguration();
                if (config4 != null) {
                    sb.append(config4.configKey());
                    break;
                }
                break;
            case CMD_START_ROAM /*131217*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                ScanResult result = (ScanResult) msg.obj;
                if (result != null) {
                    Long now = Long.valueOf(this.mClock.getWallClockMillis());
                    sb.append(" bssid=");
                    sb.append(result.BSSID);
                    sb.append(" rssi=");
                    sb.append(result.level);
                    sb.append(" freq=");
                    sb.append(result.frequency);
                    if (result.seen <= 0 || result.seen >= now.longValue()) {
                        sb.append(" !seen=");
                        sb.append(result.seen);
                    } else {
                        sb.append(" seen=");
                        sb.append(now.longValue() - result.seen);
                    }
                }
                if (this.mTargetRoamBSSID != null) {
                    sb.append(" ");
                    sb.append(this.mTargetRoamBSSID);
                }
                sb.append(" roam=");
                sb.append(Boolean.toString(this.mIsAutoRoaming));
                sb.append(" fail count=");
                sb.append(Integer.toString(this.mRoamFailCount));
                break;
            case CMD_IP_REACHABILITY_LOST /*131221*/:
                if (msg.obj != null) {
                    sb.append(" ");
                    sb.append((String) msg.obj);
                    break;
                }
                break;
            case CMD_START_RSSI_MONITORING_OFFLOAD /*131234*/:
            case CMD_STOP_RSSI_MONITORING_OFFLOAD /*131235*/:
            case CMD_RSSI_THRESHOLD_BREACHED /*131236*/:
                sb.append(" rssi=");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" thresholds=");
                sb.append(Arrays.toString(this.mRssiRanges));
                break;
            case CMD_IPV4_PROVISIONING_SUCCESS /*131272*/:
                sb.append(" ");
                sb.append(msg.obj);
                break;
            case CMD_INSTALL_PACKET_FILTER /*131274*/:
                sb.append(" len=" + ((byte[]) msg.obj).length);
                break;
            case CMD_SET_FALLBACK_PACKET_FILTERING /*131275*/:
                sb.append(" enabled=" + ((Boolean) msg.obj).booleanValue());
                break;
            case CMD_USER_SWITCH /*131277*/:
                sb.append(" userId=");
                sb.append(Integer.toString(msg.arg1));
                break;
            case CMD_PRE_DHCP_ACTION /*131327*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                sb.append(" txpkts=");
                sb.append(this.mWifiInfo.txSuccess);
                sb.append(",");
                sb.append(this.mWifiInfo.txBad);
                sb.append(",");
                sb.append(this.mWifiInfo.txRetries);
                break;
            case CMD_POST_DHCP_ACTION /*131329*/:
                if (this.mLinkProperties != null) {
                    sb.append(" ");
                    sb.append(getLinkPropertiesSummary(this.mLinkProperties));
                    break;
                }
                break;
            case CMD_IP_REACHABILITY_SESSION_END /*131383*/:
                if (msg.obj != null) {
                    sb.append(" ");
                    sb.append((String) msg.obj);
                    break;
                }
                break;
            case WifiP2pServiceImpl.P2P_CONNECTION_CHANGED /*143371*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                if (msg.obj != null) {
                    NetworkInfo info = (NetworkInfo) msg.obj;
                    NetworkInfo.State state = info.getState();
                    NetworkInfo.DetailedState detailedState = info.getDetailedState();
                    if (state != null) {
                        sb.append(" st=");
                        sb.append(state);
                    }
                    if (detailedState != null) {
                        sb.append("/");
                        sb.append(detailedState);
                        break;
                    }
                }
                break;
            case WifiMonitor.NETWORK_CONNECTION_EVENT /*147459*/:
            case WifiMonitor.FILS_NETWORK_CONNECTION_EVENT /*147519*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                sb.append(" ");
                sb.append(this.mLastBssid);
                sb.append(" nid=");
                sb.append(this.mLastNetworkId);
                WifiConfiguration config5 = getCurrentWifiConfiguration();
                if (config5 != null) {
                    sb.append(" ");
                    sb.append(config5.configKey());
                }
                String key2 = this.mWifiConfigManager.getLastSelectedNetworkConfigKey();
                if (key2 != null) {
                    sb.append(" last=");
                    sb.append(key2);
                    break;
                }
                break;
            case WifiMonitor.NETWORK_DISCONNECTION_EVENT /*147460*/:
                if (msg.obj != null) {
                    sb.append(" ");
                    sb.append((String) msg.obj);
                }
                sb.append(" nid=");
                sb.append(msg.arg1);
                sb.append(" reason=");
                sb.append(msg.arg2);
                if (this.mLastBssid != null) {
                    sb.append(" lastbssid=");
                    sb.append(this.mLastBssid);
                }
                if (this.mWifiInfo.getFrequency() != -1) {
                    sb.append(" freq=");
                    sb.append(this.mWifiInfo.getFrequency());
                    sb.append(" rssi=");
                    sb.append(this.mWifiInfo.getRssi());
                    break;
                }
                break;
            case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /*147462*/:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                StateChangeResult stateChangeResult = (StateChangeResult) msg.obj;
                if (stateChangeResult != null) {
                    sb.append(stateChangeResult.toString());
                    break;
                }
                break;
            case WifiMonitor.ASSOCIATION_REJECTION_EVENT /*147499*/:
                sb.append(" ");
                sb.append(" timedOut=" + Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                String bssid = (String) msg.obj;
                if (bssid != null && bssid.length() > 0) {
                    sb.append(" ");
                    sb.append(bssid);
                }
                sb.append(" blacklist=" + Boolean.toString(this.mDidBlackListBSSID));
                break;
            case WifiMonitor.DPP_EVENT /*147557*/:
                sb.append(" type=");
                sb.append(msg.arg1);
                break;
            case 151556:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                WifiConfiguration config6 = (WifiConfiguration) msg.obj;
                if (config6 != null) {
                    sb.append(" ");
                    sb.append(config6.configKey());
                    sb.append(" nid=");
                    sb.append(config6.networkId);
                    if (config6.hiddenSSID) {
                        sb.append(" hidden");
                    }
                    if (config6.preSharedKey != null) {
                        sb.append(" hasPSK");
                    }
                    if (config6.ephemeral) {
                        sb.append(" ephemeral");
                    }
                    if (config6.selfAdded) {
                        sb.append(" selfAdded");
                    }
                    sb.append(" cuid=");
                    sb.append(config6.creatorUid);
                    sb.append(" suid=");
                    sb.append(config6.lastUpdateUid);
                    WifiConfiguration.NetworkSelectionStatus netWorkSelectionStatus = config6.getNetworkSelectionStatus();
                    sb.append(" ajst=");
                    sb.append(netWorkSelectionStatus.getNetworkStatusString());
                    break;
                }
                break;
            case 151559:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                WifiConfiguration config7 = (WifiConfiguration) msg.obj;
                if (config7 != null) {
                    sb.append(" ");
                    sb.append(config7.configKey());
                    sb.append(" nid=");
                    sb.append(config7.networkId);
                    if (config7.hiddenSSID) {
                        sb.append(" hidden");
                    }
                    if (config7.preSharedKey != null && !config7.preSharedKey.equals("*")) {
                        sb.append(" hasPSK");
                    }
                    if (config7.ephemeral) {
                        sb.append(" ephemeral");
                    }
                    if (config7.selfAdded) {
                        sb.append(" selfAdded");
                    }
                    sb.append(" cuid=");
                    sb.append(config7.creatorUid);
                    sb.append(" suid=");
                    sb.append(config7.lastUpdateUid);
                    break;
                }
                break;
            default:
                sb.append(" ");
                sb.append(Integer.toString(msg.arg1));
                sb.append(" ");
                sb.append(Integer.toString(msg.arg2));
                break;
        }
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public String getWhatToString(int what) {
        String s = sGetWhatToString.get(what);
        if (s != null) {
            return s;
        }
        switch (what) {
            case 69632:
                return "CMD_CHANNEL_HALF_CONNECTED";
            case 69636:
                return "CMD_CHANNEL_DISCONNECTED";
            case WifiP2pServiceImpl.GROUP_CREATING_TIMED_OUT /*143361*/:
                return "GROUP_CREATING_TIMED_OUT";
            case WifiP2pServiceImpl.P2P_CONNECTION_CHANGED /*143371*/:
                return "P2P_CONNECTION_CHANGED";
            case WifiP2pServiceImpl.DISCONNECT_WIFI_REQUEST /*143372*/:
                return "DISCONNECT_WIFI_REQUEST";
            case WifiP2pServiceImpl.DISCONNECT_WIFI_RESPONSE /*143373*/:
                return "DISCONNECT_WIFI_RESPONSE";
            case WifiP2pServiceImpl.SET_MIRACAST_MODE /*143374*/:
                return "SET_MIRACAST_MODE";
            case WifiP2pServiceImpl.BLOCK_DISCOVERY /*143375*/:
                return "BLOCK_DISCOVERY";
            case WifiMonitor.NETWORK_CONNECTION_EVENT /*147459*/:
                return "NETWORK_CONNECTION_EVENT";
            case WifiMonitor.NETWORK_DISCONNECTION_EVENT /*147460*/:
                return "NETWORK_DISCONNECTION_EVENT";
            case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /*147462*/:
                return "SUPPLICANT_STATE_CHANGE_EVENT";
            case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /*147463*/:
                return "AUTHENTICATION_FAILURE_EVENT";
            case WifiMonitor.SUP_REQUEST_IDENTITY /*147471*/:
                return "SUP_REQUEST_IDENTITY";
            case WifiMonitor.ASSOCIATION_REJECTION_EVENT /*147499*/:
                return "ASSOCIATION_REJECTION_EVENT";
            case WifiMonitor.ANQP_DONE_EVENT /*147500*/:
                return "ANQP_DONE_EVENT";
            case WifiMonitor.GAS_QUERY_START_EVENT /*147507*/:
                return "GAS_QUERY_START_EVENT";
            case WifiMonitor.GAS_QUERY_DONE_EVENT /*147508*/:
                return "GAS_QUERY_DONE_EVENT";
            case WifiMonitor.RX_HS20_ANQP_ICON_EVENT /*147509*/:
                return "RX_HS20_ANQP_ICON_EVENT";
            case WifiMonitor.HS20_REMEDIATION_EVENT /*147517*/:
                return "HS20_REMEDIATION_EVENT";
            case 151553:
                return "CONNECT_NETWORK";
            case 151556:
                return "FORGET_NETWORK";
            case 151559:
                return "SAVE_NETWORK";
            case 151569:
                return "DISABLE_NETWORK";
            case 151572:
                return "RSSI_PKTCNT_FETCH";
            default:
                return "what:" + Integer.toString(what);
        }
    }

    /* access modifiers changed from: private */
    public void handleScreenStateChanged(boolean screenOn) {
        this.mScreenOn = screenOn;
        if (this.mVerboseLoggingEnabled) {
            logd(" handleScreenStateChanged Enter: screenOn=" + screenOn + " mUserWantsSuspendOpt=" + this.mUserWantsSuspendOpt + " state " + getCurrentState().getName() + " suppState:" + this.mSupplicantStateTracker.getSupplicantStateName());
        }
        enableRssiPolling(screenOn);
        if (this.mUserWantsSuspendOpt.get()) {
            int shouldReleaseWakeLock = 0;
            if (screenOn) {
                sendMessage(CMD_SET_SUSPEND_OPT_ENABLED, 0, 0);
            } else {
                if (isConnected()) {
                    this.mSuspendWakeLock.acquire(2000);
                    shouldReleaseWakeLock = 1;
                }
                sendMessage(CMD_SET_SUSPEND_OPT_ENABLED, 1, shouldReleaseWakeLock);
            }
        }
        getWifiLinkLayerStats();
        this.mOnTimeScreenStateChange = this.mOnTime;
        this.mLastScreenStateChangeTimeStamp = this.mLastLinkLayerStatsUpdate;
        this.mWifiMetrics.setScreenState(screenOn);
        this.mWifiConnectivityManager.handleScreenStateChanged(screenOn);
        this.mNetworkFactory.handleScreenStateChanged(screenOn);
        WifiLockManager wifiLockManager = this.mWifiInjector.getWifiLockManager();
        if (wifiLockManager == null) {
            Log.w(TAG, "WifiLockManager not initialized, skipping screen state notification");
        } else {
            wifiLockManager.handleScreenStateChanged(screenOn);
        }
        this.mSarManager.handleScreenStateChanged(screenOn);
        if (this.mVerboseLoggingEnabled) {
            log("handleScreenStateChanged Exit: " + screenOn);
        }
    }

    private boolean checkAndSetConnectivityInstance() {
        if (this.mCm == null) {
            this.mCm = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        }
        if (this.mCm != null) {
            return true;
        }
        Log.e(TAG, "Cannot retrieve connectivity service");
        return false;
    }

    /* access modifiers changed from: private */
    public void setSuspendOptimizationsNative(int reason, boolean enabled) {
        if (this.mVerboseLoggingEnabled) {
            log("setSuspendOptimizationsNative: " + reason + " " + enabled + " -want " + this.mUserWantsSuspendOpt.get() + " stack:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " - " + Thread.currentThread().getStackTrace()[3].getMethodName() + " - " + Thread.currentThread().getStackTrace()[4].getMethodName() + " - " + Thread.currentThread().getStackTrace()[5].getMethodName());
        }
        if (enabled) {
            this.mSuspendOptNeedsDisabled &= ~reason;
            if (this.mSuspendOptNeedsDisabled == 0 && this.mUserWantsSuspendOpt.get()) {
                if (this.mVerboseLoggingEnabled) {
                    log("setSuspendOptimizationsNative do it " + reason + " " + enabled + " stack:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " - " + Thread.currentThread().getStackTrace()[3].getMethodName() + " - " + Thread.currentThread().getStackTrace()[4].getMethodName() + " - " + Thread.currentThread().getStackTrace()[5].getMethodName());
                }
                this.mWifiNative.setSuspendOptimizations(this.mInterfaceName, true);
                return;
            }
            return;
        }
        this.mSuspendOptNeedsDisabled |= reason;
        this.mWifiNative.setSuspendOptimizations(this.mInterfaceName, false);
    }

    /* access modifiers changed from: private */
    public void setSuspendOptimizations(int reason, boolean enabled) {
        if (this.mVerboseLoggingEnabled) {
            log("setSuspendOptimizations: " + reason + " " + enabled);
        }
        if (enabled) {
            this.mSuspendOptNeedsDisabled &= ~reason;
        } else {
            this.mSuspendOptNeedsDisabled |= reason;
        }
        if (this.mVerboseLoggingEnabled) {
            log("mSuspendOptNeedsDisabled " + this.mSuspendOptNeedsDisabled);
        }
    }

    /* access modifiers changed from: private */
    public void fetchRssiLinkSpeedAndFrequencyNative() {
        WifiNative.SignalPollResult pollResult = this.mWifiNative.signalPoll(this.mInterfaceName);
        if (pollResult != null) {
            int newRssi = pollResult.currentRssi;
            int newTxLinkSpeed = pollResult.txBitrate;
            int newFrequency = pollResult.associationFrequency;
            int newRxLinkSpeed = pollResult.rxBitrate;
            if (this.mVerboseLoggingEnabled) {
                logd("fetchRssiLinkSpeedAndFrequencyNative rssi=" + newRssi + " TxLinkspeed=" + newTxLinkSpeed + " freq=" + newFrequency + " RxLinkSpeed=" + newRxLinkSpeed);
            }
            if (newRssi <= -127 || newRssi >= 200) {
                this.mWifiInfo.setRssi(WifiMetrics.MIN_RSSI_DELTA);
                updateCapabilities();
            } else {
                if (newRssi > 0) {
                    Log.wtf(TAG, "Error! +ve value RSSI: " + newRssi);
                    newRssi += -256;
                }
                this.mWifiInfo.setRssi(newRssi);
                int newSignalLevel = WifiManager.calculateSignalLevel(newRssi, 5);
                if (newSignalLevel != this.mLastSignalLevel) {
                    updateCapabilities();
                    sendRssiChangeBroadcast(newRssi);
                }
                this.mLastSignalLevel = newSignalLevel;
            }
            if (newTxLinkSpeed > 0) {
                this.mWifiInfo.setLinkSpeed(newTxLinkSpeed);
                this.mWifiInfo.setTxLinkSpeedMbps(newTxLinkSpeed);
            }
            if (newRxLinkSpeed > 0) {
                this.mWifiInfo.setRxLinkSpeedMbps(newRxLinkSpeed);
            }
            if (newFrequency > 0) {
                this.mWifiInfo.setFrequency(newFrequency);
            }
            this.mWifiConfigManager.updateScanDetailCacheFromWifiInfo(this.mWifiInfo);
            this.mWifiMetrics.handlePollResult(this.mWifiInfo);
        }
    }

    /* access modifiers changed from: private */
    public void cleanWifiScore() {
        ExtendedWifiInfo extendedWifiInfo = this.mWifiInfo;
        extendedWifiInfo.txBadRate = 0.0d;
        extendedWifiInfo.txSuccessRate = 0.0d;
        extendedWifiInfo.txRetriesRate = 0.0d;
        extendedWifiInfo.rxSuccessRate = 0.0d;
        this.mWifiScoreReport.reset();
        this.mLastLinkLayerStats = null;
    }

    /* access modifiers changed from: private */
    public void updateLinkProperties(LinkProperties newLp) {
        if (this.mVerboseLoggingEnabled) {
            log("Link configuration changed for netId: " + this.mLastNetworkId + " old: " + this.mLinkProperties + " new: " + newLp);
        }
        this.mLinkProperties = newLp;
        WifiNetworkAgent wifiNetworkAgent = this.mNetworkAgent;
        if (wifiNetworkAgent != null) {
            wifiNetworkAgent.sendLinkProperties(this.mLinkProperties);
        }
        if (getNetworkDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            sendLinkConfigurationChangedBroadcast();
        }
        if (this.mVerboseLoggingEnabled) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateLinkProperties nid: " + this.mLastNetworkId);
            sb.append(" state: " + getNetworkDetailedState());
            if (this.mLinkProperties != null) {
                sb.append(" ");
                sb.append(getLinkPropertiesSummary(this.mLinkProperties));
            }
            logd(sb.toString());
        }
    }

    private void clearLinkProperties() {
        synchronized (this.mDhcpResultsLock) {
            if (this.mDhcpResults != null) {
                this.mDhcpResults.clear();
            }
        }
        this.mLinkProperties.clear();
        WifiNetworkAgent wifiNetworkAgent = this.mNetworkAgent;
        if (wifiNetworkAgent != null) {
            wifiNetworkAgent.sendLinkProperties(this.mLinkProperties);
        }
    }

    /* access modifiers changed from: private */
    public void sendRssiChangeBroadcast(int newRssi) {
        try {
            this.mBatteryStats.noteWifiRssiChanged(newRssi);
        } catch (RemoteException e) {
        }
        StatsLog.write(38, WifiManager.calculateSignalLevel(newRssi, 5));
        Intent intent = new Intent("android.net.wifi.RSSI_CHANGED");
        intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        intent.putExtra("newRssi", newRssi);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.ACCESS_WIFI_STATE");
    }

    /* access modifiers changed from: private */
    public void sendNetworkStateChangeBroadcast(String bssid) {
        WifiStateMachineInjectorProxy.handleNetworkStateChange((WifiInfo) this.mWifiInfo, this.mNetworkInfo, getCurrentScanResult());
        Intent intent = new Intent("android.net.wifi.STATE_CHANGE");
        intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        NetworkInfo networkInfo = new NetworkInfo(this.mNetworkInfo);
        networkInfo.setExtraInfo((String) null);
        intent.putExtra("networkInfo", networkInfo);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sendLinkConfigurationChangedBroadcast() {
        Intent intent = new Intent("android.net.wifi.LINK_CONFIGURATION_CHANGED");
        intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        intent.putExtra("linkProperties", new LinkProperties(this.mLinkProperties));
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sendSupplicantConnectionChangedBroadcast(boolean connected) {
        Intent intent = new Intent("android.net.wifi.supplicant.CONNECTION_CHANGE");
        intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        intent.putExtra(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, connected);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* access modifiers changed from: private */
    public void sendDppEventBroadcast(int dppEventType, WifiDppConfig.DppResult result) {
        WifiDppConfig config = new WifiDppConfig();
        config.setDppResult(result);
        Intent intent = new Intent("com.qualcomm.qti.net.wifi.DPP_EVENT");
        intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        intent.putExtra("dppEventType", dppEventType);
        intent.putExtra("dppEventData", config);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* access modifiers changed from: private */
    public boolean setNetworkDetailedState(NetworkInfo.DetailedState state) {
        boolean hidden = false;
        if (this.mIsAutoRoaming) {
            hidden = true;
        }
        if (this.mVerboseLoggingEnabled) {
            log("setDetailed state, old =" + this.mNetworkInfo.getDetailedState() + " and new state=" + state + " hidden=" + hidden);
        }
        if (hidden || state == this.mNetworkInfo.getDetailedState()) {
            return false;
        }
        this.mNetworkInfo.setDetailedState(state, (String) null, (String) null);
        WifiNetworkAgent wifiNetworkAgent = this.mNetworkAgent;
        if (wifiNetworkAgent != null) {
            wifiNetworkAgent.sendNetworkInfo(this.mNetworkInfo);
        }
        sendNetworkStateChangeBroadcast((String) null);
        return true;
    }

    private NetworkInfo.DetailedState getNetworkDetailedState() {
        return this.mNetworkInfo.getDetailedState();
    }

    /* access modifiers changed from: private */
    public SupplicantState handleSupplicantStateChange(Message message) {
        ScanDetail scanDetail;
        StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
        SupplicantState state = stateChangeResult.state;
        this.mWifiScoreCard.noteSupplicantStateChanging(this.mWifiInfo, state);
        this.mWifiInfo.setSupplicantState(state);
        if (SupplicantState.isConnecting(state)) {
            this.mWifiInfo.setNetworkId(stateChangeResult.networkId);
            this.mWifiInfo.setBSSID(stateChangeResult.BSSID);
            this.mWifiInfo.setSSID(stateChangeResult.wifiSsid);
        } else {
            this.mWifiInfo.setNetworkId(-1);
            this.mWifiInfo.setBSSID((String) null);
            this.mWifiInfo.setSSID((WifiSsid) null);
        }
        updateL2KeyAndGroupHint();
        updateCapabilities();
        WifiConfiguration config = getCurrentWifiConfiguration();
        if (config != null) {
            this.mWifiInfo.setEphemeral(config.ephemeral);
            this.mWifiInfo.setTrusted(config.trusted);
            this.mWifiInfo.setOsuAp(config.osu);
            if (config.fromWifiNetworkSpecifier || config.fromWifiNetworkSuggestion) {
                this.mWifiInfo.setNetworkSuggestionOrSpecifierPackageName(config.creatorName);
            }
            ScanDetailCache scanDetailCache = this.mWifiConfigManager.getScanDetailCacheForNetwork(config.networkId);
            if (!(scanDetailCache == null || (scanDetail = scanDetailCache.getScanDetail(stateChangeResult.BSSID)) == null)) {
                this.mWifiInfo.setFrequency(scanDetail.getScanResult().frequency);
                NetworkDetail networkDetail = scanDetail.getNetworkDetail();
                if (networkDetail != null && networkDetail.getAnt() == NetworkDetail.Ant.ChargeablePublic) {
                    this.mWifiInfo.setMeteredHint(true);
                }
            }
        }
        this.mSupplicantStateTracker.sendMessage(Message.obtain(message));
        this.mWifiScoreCard.noteSupplicantStateChanged(this.mWifiInfo);
        return state;
    }

    public ScanResult getScanResultForBssid(String bssid) {
        Iterator<ScanDetail> it = this.mWifiNative.getScanResults(this.mInterfaceName).iterator();
        while (it.hasNext()) {
            ScanResult scanRes = it.next().getScanResult();
            Log.e(TAG, "getScanResults scanRes.BSSID = " + scanRes.BSSID);
            if (scanRes.BSSID.equals(bssid)) {
                return scanRes;
            }
        }
        return null;
    }

    private void updateL2KeyAndGroupHint() {
        if (this.mIpClient != null) {
            Pair<String, String> p = this.mWifiScoreCard.getL2KeyAndGroupHint(this.mWifiInfo);
            if (p.equals(this.mLastL2KeyAndGroupHint)) {
                return;
            }
            if (this.mIpClient.setL2KeyAndGroupHint((String) p.first, (String) p.second)) {
                this.mLastL2KeyAndGroupHint = p;
            } else {
                this.mLastL2KeyAndGroupHint = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleNetworkDisconnect() {
        handleNetworkDisconnect(false);
    }

    /* access modifiers changed from: private */
    public void handleNetworkDisconnect(boolean connectionInProgress) {
        if (this.mVerboseLoggingEnabled) {
            log("handleNetworkDisconnect: Stopping DHCP and clearing IP stack:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " - " + Thread.currentThread().getStackTrace()[3].getMethodName() + " - " + Thread.currentThread().getStackTrace()[4].getMethodName() + " - " + Thread.currentThread().getStackTrace()[5].getMethodName());
        }
        WifiConfiguration wifiConfig = getCurrentWifiConfiguration();
        if (wifiConfig != null) {
            this.mWifiInjector.getWakeupController().setLastDisconnectInfo(ScanResultMatchInfo.fromWifiConfiguration(wifiConfig));
            this.mWifiNetworkSuggestionsManager.handleDisconnect(wifiConfig, getCurrentBSSID());
        }
        stopRssiMonitoringOffload();
        clearTargetBssid("handleNetworkDisconnect");
        if (getCurrentState() != this.mFilsState || !connectionInProgress) {
            stopIpClient();
        }
        this.mWifiScoreReport.reset();
        this.mWifiInfo.reset();
        this.mIsAutoRoaming = false;
        setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
        synchronized (this.mNetworkAgentLock) {
            if (this.mNetworkAgent != null) {
                this.mNetworkAgent.sendNetworkInfo(this.mNetworkInfo);
                this.mNetworkAgent = null;
            }
        }
        clearLinkProperties();
        sendNetworkStateChangeBroadcast(this.mLastBssid);
        this.mLastBssid = null;
        this.mLastLinkLayerStats = null;
        registerDisconnected();
        this.mLastNetworkId = -1;
        this.mWifiScoreCard.resetConnectionState();
        updateL2KeyAndGroupHint();
    }

    /* access modifiers changed from: package-private */
    public void handlePreDhcpSetup() {
        this.mWifiNative.setBluetoothCoexistenceMode(this.mInterfaceName, 1);
        setSuspendOptimizationsNative(1, false);
        setPowerSave(false);
        getWifiLinkLayerStats();
        if (this.mWifiP2pChannel != null) {
            Message msg = new Message();
            msg.what = WifiP2pServiceImpl.BLOCK_DISCOVERY;
            msg.arg1 = 1;
            msg.arg2 = CMD_PRE_DHCP_ACTION_COMPLETE;
            msg.obj = this;
            this.mWifiP2pChannel.sendMessage(msg);
            return;
        }
        sendMessage(CMD_PRE_DHCP_ACTION_COMPLETE);
    }

    /* access modifiers changed from: package-private */
    public void buildDiscoverWithRapidCommitPacket() {
        ByteBuffer mDiscoverPacket = null;
        if (mDiscoverPacket != null) {
            byte[] bytes = mDiscoverPacket.array();
            StringBuilder dst = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                dst.append(String.format("%02x:", new Object[]{Byte.valueOf(bytes[i])}));
            }
            dst.append(String.format("%02x", new Object[]{Byte.valueOf(bytes[5])}));
            StringBuilder sb = new StringBuilder();
            for (int i2 = 12; i2 < mDiscoverPacket.limit(); i2++) {
                sb.append(String.format("%02x", new Object[]{Byte.valueOf(bytes[i2])}));
            }
            String mDiscoverPacketBytes = sb.toString();
            this.mWifiNative.flushAllHlp(this.mInterfaceName);
            this.mWifiNative.addHlpReq(this.mInterfaceName, dst.toString(), mDiscoverPacketBytes);
        }
    }

    /* access modifiers changed from: package-private */
    public void handlePreFilsDhcpSetup() {
        if (this.mWifiP2pChannel != null) {
            Message msg = new Message();
            msg.what = WifiP2pServiceImpl.BLOCK_DISCOVERY;
            msg.arg1 = 1;
            msg.arg2 = CMD_PRE_DHCP_ACTION_COMPLETE;
            msg.obj = this;
            this.mWifiP2pChannel.sendMessage(msg);
            return;
        }
        sendMessage(CMD_PRE_DHCP_ACTION_COMPLETE);
    }

    /* access modifiers changed from: package-private */
    public void setPowerSaveForFilsDhcp() {
        this.mWifiNative.setBluetoothCoexistenceMode(this.mInterfaceName, 1);
        setSuspendOptimizationsNative(1, false);
        this.mWifiNative.setPowerSave(this.mInterfaceName, false);
    }

    /* access modifiers changed from: package-private */
    public void handlePostDhcpSetup() {
        setSuspendOptimizationsNative(1, true);
        setPowerSave(true);
        p2pSendMessage(WifiP2pServiceImpl.BLOCK_DISCOVERY, 0);
        this.mWifiNative.setBluetoothCoexistenceMode(this.mInterfaceName, 2);
    }

    public String getCapabilities(String capaType) {
        return this.mWifiNative.getCapabilities(this.mInterfaceName, capaType);
    }

    public boolean setPowerSave(boolean ps) {
        if (this.mInterfaceName != null) {
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "Setting power save for: " + this.mInterfaceName + " to: " + ps);
            }
            this.mWifiNative.setPowerSave(this.mInterfaceName, ps);
            return true;
        }
        Log.e(TAG, "Failed to setPowerSave, interfaceName is null");
        return false;
    }

    public boolean setLowLatencyMode(boolean enabled) {
        if (this.mVerboseLoggingEnabled) {
            Log.d(TAG, "Setting low latency mode to " + enabled);
        }
        if (this.mWifiNative.setLowLatencyMode(enabled)) {
            return true;
        }
        Log.e(TAG, "Failed to setLowLatencyMode");
        return false;
    }

    private WifiNative.WifiGenerationStatus getWifiGenerationStatus() {
        String str = this.mInterfaceName;
        if (str == null) {
            return null;
        }
        return this.mWifiNative.getWifiGenerationStatus(str);
    }

    /* access modifiers changed from: private */
    public void updateWifiGenerationInfo() {
        WifiNative.WifiGenerationStatus wifiGenerationStatus = getWifiGenerationStatus();
        if (wifiGenerationStatus != null) {
            this.mWifiInfo.setWifiGeneration(wifiGenerationStatus.generation);
            this.mWifiInfo.setVhtMax8SpatialStreamsSupport(wifiGenerationStatus.vhtMax8SpatialStreamsSupport);
            this.mWifiInfo.setTwtSupport(wifiGenerationStatus.twtSupport);
            return;
        }
        this.mWifiInfo.setWifiGeneration(0);
        this.mWifiInfo.setVhtMax8SpatialStreamsSupport(false);
        this.mWifiInfo.setTwtSupport(false);
    }

    /* access modifiers changed from: private */
    public void reportConnectionAttemptStart(WifiConfiguration config, String targetBSSID, int roamType) {
        this.mWifiMetrics.startConnectionEvent(config, targetBSSID, roamType);
        this.mWifiDiagnostics.reportConnectionEvent((byte) 0);
        this.mWrongPasswordNotifier.onNewConnectionAttempt();
        removeMessages(CMD_DIAGS_CONNECT_TIMEOUT);
        sendMessageDelayed(CMD_DIAGS_CONNECT_TIMEOUT, 60000);
    }

    private void handleConnectionAttemptEndForDiagnostics(int level2FailureCode) {
        if (level2FailureCode != 1 && level2FailureCode != 5) {
            removeMessages(CMD_DIAGS_CONNECT_TIMEOUT);
            this.mWifiDiagnostics.reportConnectionEvent((byte) 2);
        }
    }

    /* access modifiers changed from: private */
    public void reportConnectionAttemptEnd(int level2FailureCode, int connectivityFailureCode, int level2FailureReason) {
        if (level2FailureCode != 1) {
            this.mWifiScoreCard.noteConnectionFailure(this.mWifiInfo, level2FailureCode, connectivityFailureCode);
        }
        WifiConfiguration configuration = getCurrentWifiConfiguration();
        if (configuration == null) {
            configuration = getTargetWifiConfiguration();
        }
        this.mWifiMetrics.endConnectionEvent(level2FailureCode, connectivityFailureCode, level2FailureReason);
        this.mWifiConnectivityManager.handleConnectionAttemptEnded(level2FailureCode);
        if (configuration != null) {
            this.mNetworkFactory.handleConnectionAttemptEnded(level2FailureCode, configuration);
            this.mWifiNetworkSuggestionsManager.handleConnectionAttemptEnded(level2FailureCode, configuration, getCurrentBSSID());
        }
        handleConnectionAttemptEndForDiagnostics(level2FailureCode);
    }

    /* access modifiers changed from: private */
    public void handleIPv4Success(DhcpResults dhcpResults) {
        Inet4Address addr;
        if (this.mVerboseLoggingEnabled) {
            logd("handleIPv4Success <" + dhcpResults.toString() + ">");
            StringBuilder sb = new StringBuilder();
            sb.append("link address ");
            sb.append(dhcpResults.ipAddress);
            logd(sb.toString());
        }
        synchronized (this.mDhcpResultsLock) {
            this.mDhcpResults = dhcpResults;
            addr = (Inet4Address) dhcpResults.ipAddress.getAddress();
        }
        if (this.mIsAutoRoaming && this.mWifiInfo.getIpAddress() != NetworkUtils.inetAddressToInt(addr)) {
            logd("handleIPv4Success, roaming and address changed" + this.mWifiInfo + " got: " + addr);
        }
        this.mWifiInfo.setInetAddress(addr);
        WifiConfiguration config = getCurrentWifiConfiguration();
        if (config != null) {
            this.mWifiInfo.setEphemeral(config.ephemeral);
            this.mWifiInfo.setTrusted(config.trusted);
        }
        if (dhcpResults.hasMeteredHint()) {
            this.mWifiInfo.setMeteredHint(true);
        }
        updateCapabilities(config);
    }

    /* access modifiers changed from: private */
    public void handleSuccessfulIpConfiguration() {
        this.mLastSignalLevel = -1;
        WifiConfiguration c = getCurrentWifiConfiguration();
        if (c != null) {
            c.getNetworkSelectionStatus().clearDisableReasonCounter(4);
            updateCapabilities(c);
        }
        this.mWifiScoreCard.noteIpConfiguration(this.mWifiInfo);
    }

    /* access modifiers changed from: private */
    public void handleIPv4Failure() {
        this.mWifiDiagnostics.captureBugReportData(4);
        if (this.mVerboseLoggingEnabled) {
            int count = -1;
            WifiConfiguration config = getCurrentWifiConfiguration();
            if (config != null) {
                count = config.getNetworkSelectionStatus().getDisableReasonCounter(4);
            }
            log("DHCP failure count=" + count);
        }
        reportConnectionAttemptEnd(10, 2, 0);
        synchronized (this.mDhcpResultsLock) {
            if (this.mDhcpResults != null) {
                this.mDhcpResults.clear();
            }
        }
        if (this.mVerboseLoggingEnabled) {
            logd("handleIPv4Failure");
        }
    }

    /* access modifiers changed from: private */
    public void handleIpConfigurationLost() {
        this.mWifiInfo.setInetAddress((InetAddress) null);
        this.mWifiInfo.setMeteredHint(false);
        this.mWifiConfigManager.updateNetworkSelectionStatus(this.mLastNetworkId, 4);
        this.mWifiNative.disconnect(this.mInterfaceName);
    }

    /* access modifiers changed from: private */
    public void handleIpReachabilityLost() {
        this.mWifiScoreCard.noteIpReachabilityLost(this.mWifiInfo);
        this.mWifiInfo.setInetAddress((InetAddress) null);
        this.mWifiInfo.setMeteredHint(false);
        this.mWifiNative.disconnect(this.mInterfaceName);
    }

    private String macAddressFromRoute(String ipAddress) {
        String macAddress = null;
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new FileReader("/proc/net/arp"));
            String readLine = reader2.readLine();
            while (true) {
                String readLine2 = reader2.readLine();
                String line = readLine2;
                if (readLine2 == null) {
                    break;
                }
                String[] tokens = line.split("[ ]+");
                if (tokens.length >= 6) {
                    String ip = tokens[0];
                    String mac = tokens[3];
                    if (ipAddress.equals(ip)) {
                        macAddress = mac;
                        break;
                    }
                }
            }
            if (macAddress == null) {
                loge("Did not find remoteAddress {" + ipAddress + "} in /proc/net/arp");
            }
            try {
                reader2.close();
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e2) {
            loge("Could not open /proc/net/arp to lookup mac address");
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e3) {
            loge("Could not read /proc/net/arp to lookup mac address");
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
        return macAddress;
    }

    /* access modifiers changed from: private */
    public boolean isPermanentWrongPasswordFailure(int networkId, int reasonCode) {
        if (reasonCode != 2) {
            return false;
        }
        WifiConfiguration network = this.mWifiConfigManager.getConfiguredNetwork(networkId);
        if (network == null || !network.getNetworkSelectionStatus().getHasEverConnected()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void registerNetworkFactory() {
        if (checkAndSetConnectivityInstance()) {
            this.mNetworkFactory.register();
            this.mUntrustedNetworkFactory.register();
        }
    }

    /* access modifiers changed from: private */
    public void getAdditionalWifiServiceInterfaces() {
        if (this.mP2pSupported) {
            this.wifiP2pServiceImpl = IWifiP2pManager.Stub.asInterface(this.mFacade.getService("wifip2p"));
            if (this.wifiP2pServiceImpl != null) {
                this.mWifiP2pChannel = new AsyncChannel();
                this.mWifiP2pChannel.connect(this.mContext, getHandler(), this.wifiP2pServiceImpl.getP2pStateMachineMessenger());
            }
        }
    }

    /* access modifiers changed from: private */
    public void configureRandomizedMacAddress(WifiConfiguration config) {
        if (config == null) {
            Log.e(TAG, "No config to change MAC address to");
            return;
        }
        try {
            MacAddress currentMac = MacAddress.fromString(this.mWifiNative.getMacAddress(this.mInterfaceName));
            MacAddress newMac = config.getOrCreateRandomizedMacAddress();
            this.mWifiConfigManager.setNetworkRandomizedMacAddress(config.networkId, newMac);
            if (!WifiConfiguration.isValidMacAddressForRandomization(newMac)) {
                Log.wtf(TAG, "Config generated an invalid MAC address");
            } else if (currentMac.equals(newMac)) {
                Log.d(TAG, "No changes in MAC address");
            } else {
                this.mWifiMetrics.logStaEvent(17, config);
                boolean setMacSuccess = this.mWifiNative.setMacAddress(this.mInterfaceName, newMac);
                Log.d(TAG, "ConnectedMacRandomization SSID(" + config.getPrintableSsid() + "). setMacAddress(" + newMac.toString() + ") from " + currentMac.toString() + " = " + setMacSuccess);
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            Log.e(TAG, "Exception in configureRandomizedMacAddress: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentMacToFactoryMac(WifiConfiguration config) {
        MacAddress factoryMac = this.mWifiNative.getFactoryMacAddress(this.mInterfaceName);
        if (factoryMac == null) {
            Log.e(TAG, "Fail to set factory MAC address. Factory MAC is null.");
        } else if (TextUtils.equals(this.mWifiNative.getMacAddress(this.mInterfaceName), factoryMac.toString())) {
        } else {
            if (this.mWifiNative.setMacAddress(this.mInterfaceName, factoryMac)) {
                this.mWifiMetrics.logStaEvent(17, config);
                return;
            }
            Log.e(TAG, "Failed to set MAC address to '" + factoryMac.toString() + "'");
        }
    }

    public boolean isConnectedMacRandomizationEnabled() {
        return this.mConnectedMacRandomzationSupported;
    }

    public void failureDetected(int reason) {
        this.mWifiInjector.getSelfRecovery().trigger(2);
    }

    class DefaultState extends State {
        DefaultState() {
        }

        public boolean processMessage(Message message) {
            int removeResult = -1;
            boolean disableOthers = false;
            switch (message.what) {
                case 0:
                    Log.wtf(ClientModeImpl.TAG, "Error! empty message encountered");
                    break;
                case 69632:
                    if (((AsyncChannel) message.obj) == ClientModeImpl.this.mWifiP2pChannel) {
                        if (message.arg1 != 0) {
                            ClientModeImpl.this.loge("WifiP2pService connection failure, error=" + message.arg1);
                            break;
                        } else {
                            boolean unused = ClientModeImpl.this.p2pSendMessage(69633);
                            break;
                        }
                    } else {
                        ClientModeImpl.this.loge("got HALF_CONNECTED for unknown channel");
                        break;
                    }
                case 69636:
                    if (((AsyncChannel) message.obj) == ClientModeImpl.this.mWifiP2pChannel) {
                        ClientModeImpl.this.loge("WifiP2pService channel lost, message.arg1 =" + message.arg1);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_BLUETOOTH_ADAPTER_STATE_CHANGE /*131103*/:
                    ClientModeImpl clientModeImpl = ClientModeImpl.this;
                    if (message.arg1 != 0) {
                        disableOthers = true;
                    }
                    boolean unused2 = clientModeImpl.mBluetoothConnectionActive = disableOthers;
                    break;
                case ClientModeImpl.CMD_ADD_OR_UPDATE_NETWORK /*131124*/:
                    NetworkUpdateResult result = ClientModeImpl.this.mWifiConfigManager.addOrUpdateNetwork((WifiConfiguration) message.obj, message.sendingUid);
                    if (!result.isSuccess()) {
                        int unused3 = ClientModeImpl.this.mMessageHandlingStatus = -2;
                    }
                    ClientModeImpl.this.replyToMessage(message, message.what, result.getNetworkId());
                    break;
                case ClientModeImpl.CMD_REMOVE_NETWORK /*131125*/:
                    boolean unused4 = ClientModeImpl.this.deleteNetworkConfigAndSendReply(message, false);
                    break;
                case ClientModeImpl.CMD_ENABLE_NETWORK /*131126*/:
                    if (message.arg2 == 1) {
                        disableOthers = true;
                    }
                    boolean ok = ClientModeImpl.this.mWifiConfigManager.enableNetwork(message.arg1, disableOthers, message.sendingUid);
                    if (!ok) {
                        int unused5 = ClientModeImpl.this.mMessageHandlingStatus = -2;
                    }
                    ClientModeImpl clientModeImpl2 = ClientModeImpl.this;
                    int i = message.what;
                    if (ok) {
                        removeResult = 1;
                    }
                    clientModeImpl2.replyToMessage(message, i, removeResult);
                    break;
                case ClientModeImpl.CMD_GET_CONFIGURED_NETWORKS /*131131*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) ClientModeImpl.this.mWifiConfigManager.getSavedNetworks(message.arg2));
                    break;
                case ClientModeImpl.CMD_GET_SUPPORTED_FEATURES /*131133*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) Long.valueOf(ClientModeImpl.this.mWifiNative.getSupportedFeatureSet(ClientModeImpl.this.mInterfaceName)));
                    break;
                case ClientModeImpl.CMD_GET_PRIVILEGED_CONFIGURED_NETWORKS /*131134*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) ClientModeImpl.this.mWifiConfigManager.getConfiguredNetworksWithPasswords());
                    break;
                case ClientModeImpl.CMD_GET_LINK_LAYER_STATS /*131135*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) null);
                    break;
                case ClientModeImpl.CMD_SET_OPERATIONAL_MODE /*131144*/:
                    break;
                case ClientModeImpl.CMD_DISCONNECT /*131145*/:
                case ClientModeImpl.CMD_RECONNECT /*131146*/:
                case ClientModeImpl.CMD_REASSOCIATE /*131147*/:
                case ClientModeImpl.CMD_RSSI_POLL /*131155*/:
                case ClientModeImpl.CMD_ONESHOT_RSSI_POLL /*131156*/:
                case ClientModeImpl.CMD_ROAM_WATCHDOG_TIMER /*131166*/:
                case ClientModeImpl.CMD_DISCONNECTING_WATCHDOG_TIMER /*131168*/:
                case ClientModeImpl.CMD_DISABLE_EPHEMERAL_NETWORK /*131170*/:
                case ClientModeImpl.CMD_TARGET_BSSID /*131213*/:
                case ClientModeImpl.CMD_START_CONNECT /*131215*/:
                case ClientModeImpl.CMD_UNWANTED_NETWORK /*131216*/:
                case ClientModeImpl.CMD_START_ROAM /*131217*/:
                case ClientModeImpl.CMD_ASSOCIATED_BSSID /*131219*/:
                case ClientModeImpl.CMD_PRE_DHCP_ACTION /*131327*/:
                case ClientModeImpl.CMD_PRE_DHCP_ACTION_COMPLETE /*131328*/:
                case ClientModeImpl.CMD_POST_DHCP_ACTION /*131329*/:
                case ClientModeImpl.CMD_IP_REACHABILITY_SESSION_END /*131383*/:
                case ClientModeImpl.CMD_WAIT_IPCLIENT_OBTAINED /*131384*/:
                case WifiMonitor.NETWORK_CONNECTION_EVENT /*147459*/:
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /*147460*/:
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /*147462*/:
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /*147463*/:
                case WifiMonitor.SUP_REQUEST_IDENTITY /*147471*/:
                case WifiMonitor.SUP_REQUEST_SIM_AUTH /*147472*/:
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /*147499*/:
                case WifiMonitor.FILS_NETWORK_CONNECTION_EVENT /*147519*/:
                case WifiMonitor.DPP_EVENT /*147557*/:
                    int unused6 = ClientModeImpl.this.mMessageHandlingStatus = -5;
                    break;
                case ClientModeImpl.CMD_SET_HIGH_PERF_MODE /*131149*/:
                    if (message.arg1 != 1) {
                        ClientModeImpl.this.setSuspendOptimizations(2, true);
                        break;
                    } else {
                        ClientModeImpl.this.setSuspendOptimizations(2, false);
                        break;
                    }
                case ClientModeImpl.CMD_ENABLE_RSSI_POLL /*131154*/:
                    ClientModeImpl clientModeImpl3 = ClientModeImpl.this;
                    if (message.arg1 == 1) {
                        disableOthers = true;
                    }
                    boolean unused7 = clientModeImpl3.mEnableRssiPolling = disableOthers;
                    break;
                case ClientModeImpl.CMD_SET_SUSPEND_OPT_ENABLED /*131158*/:
                    if (message.arg1 != 1) {
                        ClientModeImpl.this.setSuspendOptimizations(4, false);
                        break;
                    } else {
                        if (message.arg2 == 1) {
                            ClientModeImpl.this.mSuspendWakeLock.release();
                        }
                        ClientModeImpl.this.setSuspendOptimizations(4, true);
                        break;
                    }
                case ClientModeImpl.CMD_SCREEN_STATE_CHANGED /*131167*/:
                    ClientModeImpl clientModeImpl4 = ClientModeImpl.this;
                    if (message.arg1 != 0) {
                        disableOthers = true;
                    }
                    clientModeImpl4.handleScreenStateChanged(disableOthers);
                    break;
                case ClientModeImpl.CMD_REMOVE_APP_CONFIGURATIONS /*131169*/:
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case ClientModeImpl.CMD_RESET_SIM_NETWORKS /*131173*/:
                    int unused8 = ClientModeImpl.this.mMessageHandlingStatus = -4;
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case ClientModeImpl.CMD_QUERY_OSU_ICON /*131176*/:
                case ClientModeImpl.CMD_MATCH_PROVIDER_NETWORK /*131177*/:
                    ClientModeImpl.this.replyToMessage(message, message.what);
                    break;
                case ClientModeImpl.CMD_ADD_OR_UPDATE_PASSPOINT_CONFIG /*131178*/:
                    Bundle bundle = (Bundle) message.obj;
                    if (ClientModeImpl.this.mPasspointManager.addOrUpdateProvider((PasspointConfiguration) bundle.getParcelable(ClientModeImpl.EXTRA_PASSPOINT_CONFIGURATION), bundle.getInt(ClientModeImpl.EXTRA_UID), bundle.getString(ClientModeImpl.EXTRA_PACKAGE_NAME))) {
                        removeResult = 1;
                    }
                    ClientModeImpl.this.replyToMessage(message, message.what, removeResult);
                    break;
                case ClientModeImpl.CMD_REMOVE_PASSPOINT_CONFIG /*131179*/:
                    if (ClientModeImpl.this.mPasspointManager.removeProvider((String) message.obj)) {
                        removeResult = 1;
                    }
                    ClientModeImpl.this.replyToMessage(message, message.what, removeResult);
                    break;
                case ClientModeImpl.CMD_GET_PASSPOINT_CONFIGS /*131180*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) ClientModeImpl.this.mPasspointManager.getProviderConfigs());
                    break;
                case ClientModeImpl.CMD_GET_MATCHING_OSU_PROVIDERS /*131181*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) new HashMap());
                    break;
                case ClientModeImpl.CMD_GET_MATCHING_PASSPOINT_CONFIGS_FOR_OSU_PROVIDERS /*131182*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) new HashMap());
                    break;
                case ClientModeImpl.CMD_GET_WIFI_CONFIGS_FOR_PASSPOINT_PROFILES /*131184*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) new ArrayList());
                    break;
                case ClientModeImpl.CMD_BOOT_COMPLETED /*131206*/:
                    ClientModeImpl.this.getAdditionalWifiServiceInterfaces();
                    new MemoryStoreImpl(ClientModeImpl.this.mContext, ClientModeImpl.this.mWifiInjector, ClientModeImpl.this.mWifiScoreCard).start();
                    if (!ClientModeImpl.this.mWifiConfigManager.loadFromStore()) {
                        Log.e(ClientModeImpl.TAG, "Failed to load from config store");
                    }
                    ClientModeImpl.this.registerNetworkFactory();
                    break;
                case ClientModeImpl.CMD_INITIALIZE /*131207*/:
                    boolean ok2 = ClientModeImpl.this.mWifiNative.initialize();
                    ClientModeImpl.this.mPasspointManager.initializeProvisioner(ClientModeImpl.this.mWifiInjector.getWifiServiceHandlerThread().getLooper());
                    ClientModeImpl clientModeImpl5 = ClientModeImpl.this;
                    int i2 = message.what;
                    if (ok2) {
                        removeResult = 1;
                    }
                    clientModeImpl5.replyToMessage(message, i2, removeResult);
                    break;
                case ClientModeImpl.CMD_IP_CONFIGURATION_SUCCESSFUL /*131210*/:
                case ClientModeImpl.CMD_IP_CONFIGURATION_LOST /*131211*/:
                case ClientModeImpl.CMD_IP_REACHABILITY_LOST /*131221*/:
                    int unused9 = ClientModeImpl.this.mMessageHandlingStatus = -5;
                    break;
                case ClientModeImpl.CMD_UPDATE_LINKPROPERTIES /*131212*/:
                    ClientModeImpl.this.updateLinkProperties((LinkProperties) message.obj);
                    break;
                case ClientModeImpl.CMD_REMOVE_USER_CONFIGURATIONS /*131224*/:
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case ClientModeImpl.CMD_START_IP_PACKET_OFFLOAD /*131232*/:
                case ClientModeImpl.CMD_STOP_IP_PACKET_OFFLOAD /*131233*/:
                case ClientModeImpl.CMD_ADD_KEEPALIVE_PACKET_FILTER_TO_APF /*131281*/:
                case ClientModeImpl.CMD_REMOVE_KEEPALIVE_PACKET_FILTER_FROM_APF /*131282*/:
                    if (ClientModeImpl.this.mNetworkAgent != null) {
                        ClientModeImpl.this.mNetworkAgent.onSocketKeepaliveEvent(message.arg1, -20);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_START_RSSI_MONITORING_OFFLOAD /*131234*/:
                    int unused10 = ClientModeImpl.this.mMessageHandlingStatus = -5;
                    break;
                case ClientModeImpl.CMD_STOP_RSSI_MONITORING_OFFLOAD /*131235*/:
                    int unused11 = ClientModeImpl.this.mMessageHandlingStatus = -5;
                    break;
                case ClientModeImpl.CMD_GET_ALL_MATCHING_FQDNS_FOR_SCAN_RESULTS /*131240*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) new HashMap());
                    break;
                case ClientModeImpl.CMD_INSTALL_PACKET_FILTER /*131274*/:
                    ClientModeImpl.this.mWifiNative.installPacketFilter(ClientModeImpl.this.mInterfaceName, (byte[]) message.obj);
                    break;
                case ClientModeImpl.CMD_SET_FALLBACK_PACKET_FILTERING /*131275*/:
                    if (!((Boolean) message.obj).booleanValue()) {
                        ClientModeImpl.this.mWifiNative.stopFilteringMulticastV4Packets(ClientModeImpl.this.mInterfaceName);
                        break;
                    } else {
                        ClientModeImpl.this.mWifiNative.startFilteringMulticastV4Packets(ClientModeImpl.this.mInterfaceName);
                        break;
                    }
                case ClientModeImpl.CMD_USER_SWITCH /*131277*/:
                    Set<Integer> removedNetworkIds = ClientModeImpl.this.mWifiConfigManager.handleUserSwitch(message.arg1);
                    if (removedNetworkIds.contains(Integer.valueOf(ClientModeImpl.this.mTargetNetworkId)) || removedNetworkIds.contains(Integer.valueOf(ClientModeImpl.this.mLastNetworkId))) {
                        ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_DISCONNECT);
                        break;
                    }
                case ClientModeImpl.CMD_USER_UNLOCK /*131278*/:
                    ClientModeImpl.this.mWifiConfigManager.handleUserUnlock(message.arg1);
                    break;
                case ClientModeImpl.CMD_USER_STOP /*131279*/:
                    ClientModeImpl.this.mWifiConfigManager.handleUserStop(message.arg1);
                    break;
                case ClientModeImpl.CMD_READ_PACKET_FILTER /*131280*/:
                    byte[] data = ClientModeImpl.this.mWifiNative.readPacketFilter(ClientModeImpl.this.mInterfaceName);
                    if (ClientModeImpl.this.mIpClient != null) {
                        ClientModeImpl.this.mIpClient.readPacketFilterComplete(data);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_DIAGS_CONNECT_TIMEOUT /*131324*/:
                    ClientModeImpl.this.mWifiDiagnostics.reportConnectionEvent((byte) 3);
                    break;
                case ClientModeImpl.CMD_START_SUBSCRIPTION_PROVISIONING /*131326*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, 0);
                    break;
                case ClientModeImpl.CMD_DPP_GENERATE_BOOTSTRAP /*131373*/:
                case ClientModeImpl.CMD_DPP_ADD_BOOTSTRAP_QRCODE /*131374*/:
                case ClientModeImpl.CMD_DPP_REMOVE_BOOTSTRAP /*131375*/:
                case ClientModeImpl.CMD_DPP_LISTEN_START /*131377*/:
                case ClientModeImpl.CMD_DPP_CONF_ADD /*131379*/:
                case ClientModeImpl.CMD_DPP_CONF_REMOVE /*131380*/:
                case ClientModeImpl.CMD_DPP_AUTH_INIT /*131381*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, -1);
                    break;
                case ClientModeImpl.CMD_DPP_GET_URI /*131376*/:
                case ClientModeImpl.CMD_DPP_CONFIGURATOR_GET_KEY /*131382*/:
                    ClientModeImpl.this.replyToMessage(message, message.what, (Object) "Supplicant Not Started!!");
                    break;
                case ClientModeImpl.CMD_DPP_LISTEN_STOP /*131378*/:
                    int unused12 = ClientModeImpl.this.mMessageHandlingStatus = -5;
                    break;
                case WifiP2pServiceImpl.P2P_CONNECTION_CHANGED /*143371*/:
                    ClientModeImpl.this.mP2pConnected.set(((NetworkInfo) message.obj).isConnected());
                    break;
                case WifiP2pServiceImpl.DISCONNECT_WIFI_REQUEST /*143372*/:
                    ClientModeImpl clientModeImpl6 = ClientModeImpl.this;
                    if (message.arg1 == 1) {
                        disableOthers = true;
                    }
                    boolean unused13 = clientModeImpl6.mTemporarilyDisconnectWifi = disableOthers;
                    ClientModeImpl.this.replyToMessage(message, WifiP2pServiceImpl.DISCONNECT_WIFI_RESPONSE);
                    break;
                case WifiP2pServiceImpl.SET_MIRACAST_MODE /*143374*/:
                    if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                        ClientModeImpl.this.logd("SET_MIRACAST_MODE: " + message.arg1);
                    }
                    ClientModeImpl.this.mWifiConnectivityManager.saveMiracastMode(message.arg1);
                    break;
                case 151553:
                    ClientModeImpl.this.replyToMessage(message, 151554, 2);
                    break;
                case 151556:
                    boolean unused14 = ClientModeImpl.this.deleteNetworkConfigAndSendReply(message, true);
                    break;
                case 151559:
                    NetworkUpdateResult unused15 = ClientModeImpl.this.saveNetworkConfigAndSendReply(message);
                    break;
                case 151569:
                    ClientModeImpl.this.replyToMessage(message, 151570, 2);
                    break;
                case 151572:
                    ClientModeImpl.this.replyToMessage(message, 151574, 2);
                    break;
                default:
                    ClientModeImpl.this.loge("Error! unhandled message" + message);
                    break;
            }
            if (1 == 1) {
                ClientModeImpl.this.logStateAndMessage(message, this);
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void setupClientMode() {
        Log.d(TAG, "setupClientMode() ifacename = " + this.mInterfaceName);
        setHighPerfModeEnabled(false);
        this.mWifiStateTracker.updateState(0);
        updateDataInterface();
        registerForWifiMonitorEvents();
        this.mWifiInjector.getWifiLastResortWatchdog().clearAllFailureCounts();
        setSupplicantLogLevel();
        this.mSupplicantStateTracker.sendMessage(CMD_RESET_SUPPLICANT_STATE);
        this.mLastBssid = null;
        this.mLastNetworkId = -1;
        this.mLastSignalLevel = -1;
        this.mWifiInfo.setMacAddress(this.mWifiNative.getMacAddress(this.mInterfaceName));
        sendSupplicantConnectionChangedBroadcast(true);
        this.mWifiNative.setExternalSim(this.mInterfaceName, true);
        setRandomMacOui();
        this.mCountryCode.setReadyForChange(true);
        this.mWifiDiagnostics.startLogging(this.mVerboseLoggingEnabled);
        this.mIsRunning = true;
        updateBatteryWorkSource((WorkSource) null);
        this.mWifiNative.setBluetoothCoexistenceScanMode(this.mInterfaceName, this.mBluetoothConnectionActive);
        setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
        this.mWifiNative.stopFilteringMulticastV4Packets(this.mInterfaceName);
        this.mWifiNative.stopFilteringMulticastV6Packets(this.mInterfaceName);
        this.mWifiNative.setSuspendOptimizations(this.mInterfaceName, this.mSuspendOptNeedsDisabled == 0 && this.mUserWantsSuspendOpt.get());
        setPowerSave(true);
        this.mWifiNative.enableStaAutoReconnect(this.mInterfaceName, false);
        this.mWifiNative.setConcurrencyPriority(true);
    }

    /* access modifiers changed from: private */
    public void stopClientMode() {
        this.mWifiDiagnostics.stopLogging();
        this.mIsRunning = false;
        updateBatteryWorkSource((WorkSource) null);
        if (this.mIpClient != null && this.mIpClient.shutdown()) {
            this.mIpClientCallbacks.awaitShutdown();
        }
        this.mNetworkInfo.setIsAvailable(false);
        WifiNetworkAgent wifiNetworkAgent = this.mNetworkAgent;
        if (wifiNetworkAgent != null) {
            wifiNetworkAgent.sendNetworkInfo(this.mNetworkInfo);
        }
        this.mCountryCode.setReadyForChange(false);
        this.mInterfaceName = null;
        this.mDataInterfaceName = null;
        sendSupplicantConnectionChangedBroadcast(false);
        this.mWifiConfigManager.removeAllEphemeralOrPasspointConfiguredNetworks();
    }

    /* access modifiers changed from: package-private */
    public void registerConnected() {
        int i = this.mLastNetworkId;
        if (i != -1) {
            this.mWifiConfigManager.updateNetworkAfterConnect(i);
            WifiConfiguration currentNetwork = getCurrentWifiConfiguration();
            if (currentNetwork != null && currentNetwork.isPasspoint()) {
                this.mPasspointManager.onPasspointNetworkConnected(currentNetwork.FQDN);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void registerDisconnected() {
        int i = this.mLastNetworkId;
        if (i != -1) {
            this.mWifiConfigManager.updateNetworkAfterDisconnect(i);
        }
    }

    public WifiConfiguration getCurrentWifiConfiguration() {
        int i = this.mLastNetworkId;
        if (i == -1) {
            return null;
        }
        return this.mWifiConfigManager.getConfiguredNetwork(i);
    }

    private WifiConfiguration getTargetWifiConfiguration() {
        int i = this.mTargetNetworkId;
        if (i == -1) {
            return null;
        }
        return this.mWifiConfigManager.getConfiguredNetwork(i);
    }

    /* access modifiers changed from: package-private */
    public ScanResult getCurrentScanResult() {
        WifiConfiguration config = getCurrentWifiConfiguration();
        if (config == null) {
            return null;
        }
        String bssid = this.mWifiInfo.getBSSID();
        if (bssid == null) {
            bssid = this.mTargetRoamBSSID;
        }
        ScanDetailCache scanDetailCache = this.mWifiConfigManager.getScanDetailCacheForNetwork(config.networkId);
        if (scanDetailCache == null) {
            return null;
        }
        return scanDetailCache.getScanResult(bssid);
    }

    /* access modifiers changed from: package-private */
    public String getCurrentBSSID() {
        return this.mLastBssid;
    }

    class ConnectModeState extends State {
        ConnectModeState() {
        }

        public void enter() {
            Log.d(ClientModeImpl.TAG, "entering ConnectModeState: ifaceName = " + ClientModeImpl.this.mInterfaceName);
            int unused = ClientModeImpl.this.mOperationalMode = 1;
            ClientModeImpl.this.setupClientMode();
            if (!ClientModeImpl.this.mWifiNative.removeAllNetworks(ClientModeImpl.this.mInterfaceName)) {
                ClientModeImpl.this.loge("Failed to remove networks on entering connect mode");
            }
            ClientModeImpl.this.mWifiInfo.reset();
            ClientModeImpl.this.mWifiInfo.setSupplicantState(SupplicantState.DISCONNECTED);
            ClientModeImpl.this.mWifiInjector.getWakeupController().reset();
            ClientModeImpl.this.mNetworkInfo.setIsAvailable(true);
            if (ClientModeImpl.this.mNetworkAgent != null) {
                ClientModeImpl.this.mNetworkAgent.sendNetworkInfo(ClientModeImpl.this.mNetworkInfo);
            }
            boolean unused2 = ClientModeImpl.this.setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
            ClientModeImpl.this.mWifiConnectivityManager.setWifiEnabled(true);
            ClientModeImpl.this.mWifiConnectivityManager.enableVerboseLogging(ClientModeImpl.this.mVerboseLoggingEnabled ? 1 : 0);
            ClientModeImpl.this.mNetworkFactory.setWifiState(true);
            ClientModeImpl.this.mWifiMetrics.setWifiState(2);
            ClientModeImpl.this.mWifiMetrics.logStaEvent(18);
            ClientModeImpl.this.mSarManager.setClientWifiState(3);
            ClientModeImpl.this.mWifiScoreCard.noteSupplicantStateChanged(ClientModeImpl.this.mWifiInfo);
        }

        public void exit() {
            int unused = ClientModeImpl.this.mOperationalMode = 4;
            ClientModeImpl.this.mNetworkInfo.setIsAvailable(false);
            if (ClientModeImpl.this.mNetworkAgent != null) {
                ClientModeImpl.this.mNetworkAgent.sendNetworkInfo(ClientModeImpl.this.mNetworkInfo);
            }
            ClientModeImpl.this.mWifiConnectivityManager.setWifiEnabled(false);
            ClientModeImpl.this.mNetworkFactory.setWifiState(false);
            ClientModeImpl.this.mWifiMetrics.setWifiState(1);
            ClientModeImpl.this.mWifiMetrics.logStaEvent(19);
            ClientModeImpl.this.mWifiScoreCard.noteWifiDisabled(ClientModeImpl.this.mWifiInfo);
            ClientModeImpl.this.mSarManager.setClientWifiState(1);
            if (!ClientModeImpl.this.mWifiNative.removeAllNetworks(ClientModeImpl.this.mInterfaceName)) {
                ClientModeImpl.this.loge("Failed to remove networks on exiting connect mode");
            }
            ClientModeImpl.this.mWifiInfo.reset();
            ClientModeImpl.this.mWifiInfo.setSupplicantState(SupplicantState.DISCONNECTED);
            ClientModeImpl.this.mWifiScoreCard.noteSupplicantStateChanged(ClientModeImpl.this.mWifiInfo);
            ClientModeImpl.this.stopClientMode();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:273:0x0bc8, code lost:
            if (com.android.server.wifi.ClientModeImpl.access$8200(r4, com.android.server.wifi.ClientModeImpl.access$3300(r4), r3) != false) goto L_0x0bca;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:282:0x0c35, code lost:
            if (com.android.server.wifi.ClientModeImpl.access$8200(r6, com.android.server.wifi.ClientModeImpl.access$3300(r6), r5) != false) goto L_0x0c37;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean processMessage(android.os.Message r17) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = 1
                int r3 = r1.what
                r4 = 15
                r5 = 147463(0x24007, float:2.0664E-40)
                r6 = 151554(0x25002, float:2.12372E-40)
                r8 = 4
                r9 = 13
                r10 = 5
                r11 = -2
                java.lang.String r12 = "WifiClientModeImpl"
                r13 = 131145(0x20049, float:1.83773E-40)
                r14 = 2
                r15 = 0
                r7 = 1
                switch(r3) {
                    case 131103: goto L_0x0dec;
                    case 131125: goto L_0x0dc6;
                    case 131126: goto L_0x0d92;
                    case 131135: goto L_0x0d83;
                    case 131146: goto L_0x0d74;
                    case 131147: goto L_0x0d56;
                    case 131149: goto L_0x0d43;
                    case 131158: goto L_0x0d23;
                    case 131164: goto L_0x0d03;
                    case 131169: goto L_0x0cce;
                    case 131170: goto L_0x0ca3;
                    case 131173: goto L_0x0c7f;
                    case 131176: goto L_0x0c60;
                    case 131177: goto L_0x0c57;
                    case 131178: goto L_0x0bf3;
                    case 131179: goto L_0x0ba2;
                    case 131181: goto L_0x0b8b;
                    case 131182: goto L_0x0b74;
                    case 131184: goto L_0x0b5d;
                    case 131213: goto L_0x0b4e;
                    case 131215: goto L_0x0917;
                    case 131217: goto L_0x090f;
                    case 131219: goto L_0x08e7;
                    case 131224: goto L_0x08ac;
                    case 131233: goto L_0x0891;
                    case 131238: goto L_0x0881;
                    case 131240: goto L_0x086a;
                    case 131276: goto L_0x0853;
                    case 131326: goto L_0x0827;
                    case 131373: goto L_0x080a;
                    case 131374: goto L_0x07ed;
                    case 131375: goto L_0x07d2;
                    case 131376: goto L_0x07b7;
                    case 131377: goto L_0x0776;
                    case 131378: goto L_0x0765;
                    case 131379: goto L_0x0736;
                    case 131380: goto L_0x071b;
                    case 131381: goto L_0x06fe;
                    case 131382: goto L_0x06dd;
                    case 143372: goto L_0x06a4;
                    case 147459: goto L_0x0559;
                    case 147460: goto L_0x04cd;
                    case 147462: goto L_0x0478;
                    case 147463: goto L_0x03d1;
                    case 147471: goto L_0x02ff;
                    case 147472: goto L_0x02ce;
                    case 147499: goto L_0x0223;
                    case 147500: goto L_0x0214;
                    case 147509: goto L_0x0205;
                    case 147517: goto L_0x01f6;
                    case 147519: goto L_0x0559;
                    case 147557: goto L_0x01d3;
                    case 151553: goto L_0x014d;
                    case 151556: goto L_0x012a;
                    case 151559: goto L_0x0067;
                    case 151569: goto L_0x0022;
                    default: goto L_0x001f;
                }
            L_0x001f:
                r2 = 0
                goto L_0x0e0c
            L_0x0022:
                int r3 = r1.arg1
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r4 = r4.mWifiConfigManager
                int r5 = r1.sendingUid
                boolean r4 = r4.disableNetwork(r3, r5)
                if (r4 == 0) goto L_0x0051
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r5 = 151571(0x25013, float:2.12396E-40)
                r4.replyToMessage(r1, r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mTargetNetworkId
                if (r3 == r4) goto L_0x004a
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mLastNetworkId
                if (r3 != r4) goto L_0x0e0c
            L_0x004a:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.sendMessage(r13)
                goto L_0x0e0c
            L_0x0051:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r5 = "Failed to disable network"
                r4.loge(r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int unused = r4.mMessageHandlingStatus = r11
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r5 = 151570(0x25012, float:2.12395E-40)
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r15)
                goto L_0x0e0c
            L_0x0067:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.NetworkUpdateResult r3 = r3.saveNetworkConfigAndSendReply(r1)
                int r4 = r3.getNetworkId()
                boolean r5 = r3.isSuccess()
                if (r5 == 0) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ExtendedWifiInfo r5 = r5.mWifiInfo
                int r5 = r5.getNetworkId()
                if (r5 != r4) goto L_0x0e0c
                boolean r5 = r3.hasCredentialChanged()
                if (r5 == 0) goto L_0x00b7
                java.lang.Object r5 = r1.obj
                android.net.wifi.WifiConfiguration r5 = (android.net.wifi.WifiConfiguration) r5
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                java.lang.String r9 = "SAVE_NETWORK credential changed for config="
                r8.append(r9)
                java.lang.String r9 = r5.configKey()
                r8.append(r9)
                java.lang.String r9 = ", Reconnecting."
                r8.append(r9)
                java.lang.String r8 = r8.toString()
                r6.logi(r8)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r1.sendingUid
                java.lang.String r9 = "any"
                r6.startConnectToNetwork(r4, r8, r9)
                goto L_0x0e0c
            L_0x00b7:
                boolean r5 = r3.hasProxyChanged()
                if (r5 == 0) goto L_0x00e1
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                android.net.ip.IpClientManager r5 = r5.mIpClient
                if (r5 == 0) goto L_0x00e1
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = "Reconfiguring proxy on connection"
                r5.log(r6)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r5 = r5.getCurrentWifiConfiguration()
                if (r5 == 0) goto L_0x00e1
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                android.net.ip.IpClientManager r6 = r6.mIpClient
                android.net.ProxyInfo r8 = r5.getHttpProxy()
                r6.setHttpProxy(r8)
            L_0x00e1:
                boolean r5 = r3.hasIpChanged()
                if (r5 == 0) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = "Reconfiguring IP on connection"
                r5.log(r6)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r5 = r5.mWifiNative
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = r6.mInterfaceName
                r5.disconnect(r6)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.State r6 = r5.mDisconnectingState
                r5.transitionTo(r6)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r5 = r5.mWifiNative
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = r6.mInterfaceName
                boolean r5 = r5.reconnect(r6)
                if (r5 == 0) goto L_0x0121
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = "Reconnecting after IP reconfiguration"
                r5.log(r6)
                goto L_0x0e0c
            L_0x0121:
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = "Failed to reconnect after IP reconfiguration"
                r5.loge(r6)
                goto L_0x0e0c
            L_0x012a:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                boolean r3 = r3.deleteNetworkConfigAndSendReply(r1, r7)
                if (r3 != 0) goto L_0x0134
                goto L_0x0e0c
            L_0x0134:
                int r3 = r1.arg1
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mTargetNetworkId
                if (r3 == r4) goto L_0x0146
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mLastNetworkId
                if (r3 != r4) goto L_0x0e0c
            L_0x0146:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.sendMessage(r13)
                goto L_0x0e0c
            L_0x014d:
                int r3 = r1.arg1
                java.lang.Object r4 = r1.obj
                android.net.wifi.WifiConfiguration r4 = (android.net.wifi.WifiConfiguration) r4
                r5 = 0
                if (r4 == 0) goto L_0x0197
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r8 = r8.mWifiConfigManager
                int r10 = r1.sendingUid
                com.android.server.wifi.NetworkUpdateResult r8 = r8.addOrUpdateNetwork(r4, r10)
                boolean r10 = r8.isSuccess()
                if (r10 != 0) goto L_0x018f
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                java.lang.String r12 = "CONNECT_NETWORK adding/updating config="
                r10.append(r12)
                r10.append(r4)
                java.lang.String r12 = " failed"
                r10.append(r12)
                java.lang.String r10 = r10.toString()
                r9.loge(r10)
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                int unused = r9.mMessageHandlingStatus = r11
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                r9.replyToMessage((android.os.Message) r1, (int) r6, (int) r15)
                goto L_0x0e0c
            L_0x018f:
                int r3 = r8.getNetworkId()
                boolean r5 = r8.hasCredentialChanged()
            L_0x0197:
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int r10 = r1.sendingUid
                boolean r8 = r8.connectToUserSelectNetwork(r3, r10, r5)
                if (r8 != 0) goto L_0x01af
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int unused = r8.mMessageHandlingStatus = r11
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                r9 = 9
                r8.replyToMessage((android.os.Message) r1, (int) r6, (int) r9)
                goto L_0x0e0c
            L_0x01af:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.SupplicantStateTracker r6 = r6.mSupplicantStateTracker
                r8 = 151553(0x25001, float:2.12371E-40)
                r6.sendMessage(r8, r3)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiMetrics r6 = r6.mWifiMetrics
                r6.logStaEvent((int) r9, (android.net.wifi.WifiConfiguration) r4)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                r6.broadcastWifiCredentialChanged(r15, r4)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                r8 = 151555(0x25003, float:2.12374E-40)
                r6.replyToMessage(r1, r8)
                goto L_0x0e0c
            L_0x01d3:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "DPP Event received. Type = "
                r3.append(r4)
                int r4 = r1.arg1
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                android.util.Log.d(r12, r3)
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.arg1
                java.lang.Object r5 = r1.obj
                android.net.wifi.WifiDppConfig$DppResult r5 = (android.net.wifi.WifiDppConfig.DppResult) r5
                r3.sendDppEventBroadcast(r4, r5)
                goto L_0x0e0c
            L_0x01f6:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r3 = r3.mPasspointManager
                java.lang.Object r4 = r1.obj
                com.android.server.wifi.hotspot2.WnmData r4 = (com.android.server.wifi.hotspot2.WnmData) r4
                r3.receivedWnmFrame(r4)
                goto L_0x0e0c
            L_0x0205:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r3 = r3.mPasspointManager
                java.lang.Object r4 = r1.obj
                com.android.server.wifi.hotspot2.IconEvent r4 = (com.android.server.wifi.hotspot2.IconEvent) r4
                r3.notifyIconDone(r4)
                goto L_0x0e0c
            L_0x0214:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r3 = r3.mPasspointManager
                java.lang.Object r4 = r1.obj
                com.android.server.wifi.hotspot2.AnqpEvent r4 = (com.android.server.wifi.hotspot2.AnqpEvent) r4
                r3.notifyANQPDone(r4)
                goto L_0x0e0c
            L_0x0223:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.BaseWifiDiagnostics r3 = r3.mWifiDiagnostics
                r3.captureBugReportData(r7)
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                boolean unused = r3.mDidBlackListBSSID = r15
                java.lang.Object r3 = r1.obj
                java.lang.String r3 = (java.lang.String) r3
                int r4 = r1.arg1
                if (r4 <= 0) goto L_0x023b
                r4 = r7
                goto L_0x023c
            L_0x023b:
                r4 = r15
            L_0x023c:
                int r5 = r1.arg2
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r8 = "Association Rejection event: bssid="
                r6.append(r8)
                r6.append(r3)
                java.lang.String r8 = " reason code="
                r6.append(r8)
                r6.append(r5)
                java.lang.String r8 = " timedOut="
                r6.append(r8)
                java.lang.String r8 = java.lang.Boolean.toString(r4)
                r6.append(r8)
                java.lang.String r6 = r6.toString()
                android.util.Log.d(r12, r6)
                if (r3 == 0) goto L_0x026e
                boolean r6 = android.text.TextUtils.isEmpty(r3)
                if (r6 == 0) goto L_0x0274
            L_0x026e:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r3 = r6.mTargetRoamBSSID
            L_0x0274:
                if (r3 == 0) goto L_0x0283
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConnectivityManager r8 = r6.mWifiConnectivityManager
                boolean r8 = r8.trackBssid(r3, r15, r5)
                boolean unused = r6.mDidBlackListBSSID = r8
            L_0x0283:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r6 = r6.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r8.mTargetNetworkId
                r6.updateNetworkSelectionStatus((int) r8, (int) r14)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r6 = r6.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r8.mTargetNetworkId
                r6.setRecentFailureAssociationStatus(r8, r5)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.SupplicantStateTracker r6 = r6.mSupplicantStateTracker
                r8 = 147499(0x2402b, float:2.0669E-40)
                r6.sendMessage(r8)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                if (r4 == 0) goto L_0x02b4
                r14 = 11
                goto L_0x02b5
            L_0x02b4:
            L_0x02b5:
                r6.reportConnectionAttemptEnd(r14, r7, r15)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiInjector r6 = r6.mWifiInjector
                com.android.server.wifi.WifiLastResortWatchdog r6 = r6.getWifiLastResortWatchdog()
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r8 = r8.getTargetSsid()
                r6.noteConnectionFailureAndTriggerIfNeeded(r8, r3, r7)
                goto L_0x0e0c
            L_0x02ce:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = "Received SUP_REQUEST_SIM_AUTH"
                r3.logd(r4)
                java.lang.Object r3 = r1.obj
                com.android.server.wifi.util.TelephonyUtil$SimAuthRequestData r3 = (com.android.server.wifi.util.TelephonyUtil.SimAuthRequestData) r3
                if (r3 == 0) goto L_0x02f6
                int r4 = r3.protocol
                if (r4 != r8) goto L_0x02e6
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.handleGsmAuthRequest(r3)
                goto L_0x0e0c
            L_0x02e6:
                int r4 = r3.protocol
                if (r4 == r10) goto L_0x02ef
                int r4 = r3.protocol
                r5 = 6
                if (r4 != r5) goto L_0x0e0c
            L_0x02ef:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.handle3GAuthRequest(r3)
                goto L_0x0e0c
            L_0x02f6:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r5 = "Invalid SIM auth request"
                r4.loge(r5)
                goto L_0x0e0c
            L_0x02ff:
                int r3 = r1.arg2
                r6 = 0
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r8 = r8.mTargetWifiConfiguration
                if (r8 == 0) goto L_0x0377
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r8 = r8.mTargetWifiConfiguration
                int r8 = r8.networkId
                if (r8 != r3) goto L_0x0377
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r8 = r8.mTargetWifiConfiguration
                boolean r8 = com.android.server.wifi.util.TelephonyUtil.isSimConfig(r8)
                if (r8 == 0) goto L_0x0377
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                android.telephony.TelephonyManager r8 = r8.getTelephonyManager()
                com.android.server.wifi.util.TelephonyUtil r9 = new com.android.server.wifi.util.TelephonyUtil
                r9.<init>()
                com.android.server.wifi.ClientModeImpl r10 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r10 = r10.mTargetWifiConfiguration
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiInjector r11 = r11.mWifiInjector
                com.android.server.wifi.CarrierNetworkConfig r11 = r11.getCarrierNetworkConfig()
                android.util.Pair r8 = com.android.server.wifi.util.TelephonyUtil.getSimIdentity(r8, r9, r10, r11)
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = "SUP_REQUEST_IDENTITY: identityPair="
                r9.append(r10)
                r9.append(r8)
                java.lang.String r9 = r9.toString()
                android.util.Log.i(r12, r9)
                if (r8 == 0) goto L_0x0372
                java.lang.Object r9 = r8.first
                if (r9 == 0) goto L_0x0372
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r9 = r9.mWifiNative
                com.android.server.wifi.ClientModeImpl r10 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r10 = r10.mInterfaceName
                java.lang.Object r11 = r8.first
                java.lang.String r11 = (java.lang.String) r11
                java.lang.Object r12 = r8.second
                java.lang.String r12 = (java.lang.String) r12
                r9.simIdentityResponse(r10, r3, r11, r12)
                r6 = 1
                goto L_0x0377
            L_0x0372:
                java.lang.String r9 = "Unable to retrieve identity from Telephony"
                android.util.Log.e(r12, r9)
            L_0x0377:
                if (r6 != 0) goto L_0x0e0c
                java.lang.Object r8 = r1.obj
                java.lang.String r8 = (java.lang.String) r8
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r9 = r9.mTargetWifiConfiguration
                if (r9 == 0) goto L_0x03b7
                if (r8 == 0) goto L_0x03b7
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r9 = r9.mTargetWifiConfiguration
                java.lang.String r9 = r9.SSID
                if (r9 == 0) goto L_0x03b7
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r9 = r9.mTargetWifiConfiguration
                int r9 = r9.networkId
                if (r9 != r3) goto L_0x03b7
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r9 = r9.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r10 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r10 = r10.mTargetWifiConfiguration
                int r10 = r10.networkId
                r11 = 9
                r9.updateNetworkSelectionStatus((int) r10, (int) r11)
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.SupplicantStateTracker r9 = r9.mSupplicantStateTracker
                r9.sendMessage(r5)
            L_0x03b7:
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiMetrics r5 = r5.mWifiMetrics
                r5.logStaEvent((int) r4, (int) r14)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r4 = r4.mWifiNative
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r5 = r5.mInterfaceName
                r4.disconnect(r5)
                goto L_0x0e0c
            L_0x03d1:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.BaseWifiDiagnostics r3 = r3.mWifiDiagnostics
                r3.captureBugReportData(r14)
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.SupplicantStateTracker r3 = r3.mSupplicantStateTracker
                r3.sendMessage(r5)
                r3 = 3
                int r4 = r1.arg1
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                int r6 = r5.mTargetNetworkId
                boolean r5 = r5.isPermanentWrongPasswordFailure(r6, r4)
                r6 = 3
                if (r5 == 0) goto L_0x0413
                r3 = 13
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r5 = r5.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r8.mTargetNetworkId
                android.net.wifi.WifiConfiguration r5 = r5.getConfiguredNetwork((int) r8)
                if (r5 == 0) goto L_0x0412
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WrongPasswordNotifier r8 = r8.mWrongPasswordNotifier
                java.lang.String r9 = r5.SSID
                r8.onWrongPasswordError(r9)
            L_0x0412:
                goto L_0x0426
            L_0x0413:
                if (r4 != r6) goto L_0x0412
                int r5 = r1.arg2
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int r9 = r8.mTargetNetworkId
                r8.handleEapAuthFailure(r9, r5)
                r8 = 1031(0x407, float:1.445E-42)
                if (r5 != r8) goto L_0x0426
                r3 = 14
            L_0x0426:
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r5 = r5.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r8.mTargetNetworkId
                r5.updateNetworkSelectionStatus((int) r8, (int) r3)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r5 = r5.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r8.mTargetNetworkId
                r5.clearRecentFailureReason(r8)
                if (r4 == 0) goto L_0x0454
                if (r4 == r7) goto L_0x0452
                if (r4 == r14) goto L_0x0450
                if (r4 == r6) goto L_0x044e
                r5 = 0
                goto L_0x0456
            L_0x044e:
                r5 = 4
                goto L_0x0456
            L_0x0450:
                r5 = 3
                goto L_0x0456
            L_0x0452:
                r5 = 2
                goto L_0x0456
            L_0x0454:
                r5 = 1
            L_0x0456:
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                r8.reportConnectionAttemptEnd(r6, r7, r5)
                if (r4 == r14) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiInjector r6 = r6.mWifiInjector
                com.android.server.wifi.WifiLastResortWatchdog r6 = r6.getWifiLastResortWatchdog()
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r8 = r8.getTargetSsid()
                com.android.server.wifi.ClientModeImpl r9 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r9 = r9.mTargetRoamBSSID
                r6.noteConnectionFailureAndTriggerIfNeeded(r8, r9, r14)
                goto L_0x0e0c
            L_0x0478:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.SupplicantState r3 = r3.handleSupplicantStateChange(r1)
                android.net.wifi.SupplicantState r4 = android.net.wifi.SupplicantState.DISCONNECTED
                if (r3 != r4) goto L_0x04ad
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                android.net.NetworkInfo r4 = r4.mNetworkInfo
                android.net.NetworkInfo$State r4 = r4.getState()
                android.net.NetworkInfo$State r5 = android.net.NetworkInfo.State.DISCONNECTED
                if (r4 == r5) goto L_0x04ad
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                boolean r4 = r4.mVerboseLoggingEnabled
                if (r4 == 0) goto L_0x049f
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r5 = "Missed CTRL-EVENT-DISCONNECTED, disconnect"
                r4.log(r5)
            L_0x049f:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.handleNetworkDisconnect()
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.State r5 = r4.mDisconnectedState
                r4.transitionTo(r5)
            L_0x04ad:
                android.net.wifi.SupplicantState r4 = android.net.wifi.SupplicantState.COMPLETED
                if (r3 != r4) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                android.net.ip.IpClientManager r4 = r4.mIpClient
                if (r4 == 0) goto L_0x04c2
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                android.net.ip.IpClientManager r4 = r4.mIpClient
                r4.confirmConfiguration()
            L_0x04c2:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiScoreReport r4 = r4.mWifiScoreReport
                r4.noteIpCheck()
                goto L_0x0e0c
            L_0x04cd:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                boolean r3 = r3.mVerboseLoggingEnabled
                if (r3 == 0) goto L_0x04dc
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = "ConnectModeState: Network connection lost "
                r3.log(r4)
            L_0x04dc:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.arg1
                int unused = r3.mLastNetworkId = r4
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r3 = r3.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mLastNetworkId
                r3.clearRecentFailureReason(r4)
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.Object r4 = r1.obj
                java.lang.String r4 = (java.lang.String) r4
                java.lang.String unused = r3.mLastBssid = r4
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r3.mLastBssid
                android.net.wifi.ScanResult r3 = r3.getScanResultForBssid(r4)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r4 = r4.mTargetWifiConfiguration
                if (r4 == 0) goto L_0x0537
                if (r3 == 0) goto L_0x0537
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r4 = r4.mTargetWifiConfiguration
                java.lang.String r4 = r4.SSID
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "\""
                r5.append(r6)
                java.lang.String r6 = r3.SSID
                r5.append(r6)
                java.lang.String r6 = "\""
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                boolean r4 = r4.equals(r5)
                if (r4 != 0) goto L_0x0537
                r15 = r7
                goto L_0x0538
            L_0x0537:
            L_0x0538:
                r4 = r15
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                r5.handleNetworkDisconnect(r4)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.IState r5 = r5.getCurrentState()
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.State r6 = r6.mFilsState
                if (r5 != r6) goto L_0x054e
                if (r4 != 0) goto L_0x0e0c
            L_0x054e:
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.State r6 = r5.mDisconnectedState
                r5.transitionTo(r6)
                goto L_0x0e0c
            L_0x0559:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                boolean r3 = r3.mVerboseLoggingEnabled
                if (r3 == 0) goto L_0x0568
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = "Network connection established"
                r3.log(r4)
            L_0x0568:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.arg1
                int unused = r3.mLastNetworkId = r4
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r3 = r3.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mLastNetworkId
                r3.clearRecentFailureReason(r4)
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.Object r4 = r1.obj
                java.lang.String r4 = (java.lang.String) r4
                java.lang.String unused = r3.mLastBssid = r4
                int r3 = r1.arg2
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration r4 = r4.getCurrentWifiConfiguration()
                if (r4 == 0) goto L_0x067c
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ExtendedWifiInfo r5 = r5.mWifiInfo
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = r6.mLastBssid
                r5.setBSSID(r6)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ExtendedWifiInfo r5 = r5.mWifiInfo
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                int r6 = r6.mLastNetworkId
                r5.setNetworkId(r6)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ExtendedWifiInfo r5 = r5.mWifiInfo
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r6 = r6.mWifiNative
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r8 = r8.mInterfaceName
                java.lang.String r6 = r6.getMacAddress(r8)
                r5.setMacAddress(r6)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                r5.updateWifiGenerationInfo()
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r5 = r5.mWifiConfigManager
                int r6 = r4.networkId
                com.android.server.wifi.ScanDetailCache r5 = r5.getScanDetailCacheForNetwork(r6)
                if (r5 == 0) goto L_0x05fa
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = r6.mLastBssid
                if (r6 == 0) goto L_0x05fa
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = r6.mLastBssid
                android.net.wifi.ScanResult r6 = r5.getScanResult(r6)
                if (r6 == 0) goto L_0x05fa
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ExtendedWifiInfo r8 = r8.mWifiInfo
                int r9 = r6.frequency
                r8.setFrequency(r9)
            L_0x05fa:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConnectivityManager r6 = r6.mWifiConnectivityManager
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r8 = r8.mLastBssid
                r6.trackBssid(r8, r7, r3)
                android.net.wifi.WifiEnterpriseConfig r6 = r4.enterpriseConfig
                if (r6 == 0) goto L_0x0663
                android.net.wifi.WifiEnterpriseConfig r6 = r4.enterpriseConfig
                int r6 = r6.getEapMethod()
                boolean r6 = com.android.server.wifi.util.TelephonyUtil.isSimEapMethod(r6)
                if (r6 == 0) goto L_0x0663
                android.net.wifi.WifiEnterpriseConfig r6 = r4.enterpriseConfig
                java.lang.String r6 = r6.getAnonymousIdentity()
                boolean r6 = com.android.server.wifi.util.TelephonyUtil.isAnonymousAtRealmIdentity(r6)
                if (r6 != 0) goto L_0x0663
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r6 = r6.mWifiNative
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r8 = r8.mInterfaceName
                java.lang.String r6 = r6.getEapAnonymousIdentity(r8)
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                boolean r8 = r8.mVerboseLoggingEnabled
                if (r8 == 0) goto L_0x0653
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = "EAP Pseudonym: "
                r9.append(r10)
                r9.append(r6)
                java.lang.String r9 = r9.toString()
                r8.log(r9)
            L_0x0653:
                android.net.wifi.WifiEnterpriseConfig r8 = r4.enterpriseConfig
                r8.setAnonymousIdentity(r6)
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r8 = r8.mWifiConfigManager
                r9 = 1010(0x3f2, float:1.415E-42)
                r8.addOrUpdateNetwork(r4, r9)
            L_0x0663:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r8 = r6.mLastBssid
                r6.sendNetworkStateChangeBroadcast(r8)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                boolean unused = r6.mIpReachabilityMonitorActive = r7
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.State r8 = r6.mObtainingIpState
                r6.transitionTo(r8)
                goto L_0x0e0c
            L_0x067c:
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r8 = "Connected to unknown networkId "
                r6.append(r8)
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r8.mLastNetworkId
                r6.append(r8)
                java.lang.String r8 = ", disconnecting..."
                r6.append(r8)
                java.lang.String r6 = r6.toString()
                r5.logw(r6)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                r5.sendMessage(r13)
                goto L_0x0e0c
            L_0x06a4:
                int r3 = r1.arg1
                if (r3 != r7) goto L_0x06c7
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiMetrics r3 = r3.mWifiMetrics
                r3.logStaEvent((int) r4, (int) r10)
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                r3.disconnect(r4)
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                boolean unused = r3.mTemporarilyDisconnectWifi = r7
                goto L_0x0e0c
            L_0x06c7:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                r3.reconnect(r4)
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                boolean unused = r3.mTemporarilyDisconnectWifi = r15
                goto L_0x0e0c
            L_0x06dd:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                java.lang.Object r5 = r1.obj
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r5 = r5.intValue()
                java.lang.String r3 = r3.dppConfiguratorGetKey(r4, r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (java.lang.Object) r3)
                goto L_0x0e0c
            L_0x06fe:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                java.lang.Object r5 = r1.obj
                android.net.wifi.WifiDppConfig r5 = (android.net.wifi.WifiDppConfig) r5
                int r3 = r3.dppStartAuth(r4, r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r3)
                goto L_0x0e0c
            L_0x071b:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                int r5 = r1.arg1
                int r3 = r3.dppConfiguratorRemove(r4, r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r3)
                goto L_0x0e0c
            L_0x0736:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                java.lang.Object r5 = r1.obj
                android.os.Bundle r5 = (android.os.Bundle) r5
                java.lang.String r6 = "curve"
                java.lang.String r5 = r5.getString(r6)
                java.lang.Object r6 = r1.obj
                android.os.Bundle r6 = (android.os.Bundle) r6
                java.lang.String r8 = "key"
                java.lang.String r6 = r6.getString(r8)
                int r8 = r1.arg1
                int r3 = r3.dppConfiguratorAdd(r4, r5, r6, r8)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r3)
                goto L_0x0e0c
            L_0x0765:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                r3.dppStopListen(r4)
                goto L_0x0e0c
            L_0x0776:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r8 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r9 = r3.mInterfaceName
                java.lang.Object r3 = r1.obj
                android.os.Bundle r3 = (android.os.Bundle) r3
                java.lang.String r4 = "freq"
                java.lang.String r10 = r3.getString(r4)
                java.lang.Object r3 = r1.obj
                android.os.Bundle r3 = (android.os.Bundle) r3
                java.lang.String r4 = "dppRole"
                int r11 = r3.getInt(r4)
                java.lang.Object r3 = r1.obj
                android.os.Bundle r3 = (android.os.Bundle) r3
                java.lang.String r4 = "mutual"
                boolean r12 = r3.getBoolean(r4)
                java.lang.Object r3 = r1.obj
                android.os.Bundle r3 = (android.os.Bundle) r3
                java.lang.String r4 = "netRoleAp"
                boolean r13 = r3.getBoolean(r4)
                int r3 = r8.dppListen(r9, r10, r11, r12, r13)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r3)
                goto L_0x0e0c
            L_0x07b7:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                int r5 = r1.arg1
                java.lang.String r3 = r3.dppGetUri(r4, r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (java.lang.Object) r3)
                goto L_0x0e0c
            L_0x07d2:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                int r5 = r1.arg1
                int r3 = r3.dppBootstrapRemove(r4, r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r3)
                goto L_0x0e0c
            L_0x07ed:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                java.lang.Object r5 = r1.obj
                java.lang.String r5 = (java.lang.String) r5
                int r3 = r3.dppAddBootstrapQrCode(r4, r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r3)
                goto L_0x0e0c
            L_0x080a:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                java.lang.Object r5 = r1.obj
                android.net.wifi.WifiDppConfig r5 = (android.net.wifi.WifiDppConfig) r5
                int r3 = r3.dppBootstrapGenerate(r4, r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r3)
                goto L_0x0e0c
            L_0x0827:
                java.lang.Object r3 = r1.obj
                android.net.wifi.hotspot2.IProvisioningCallback r3 = (android.net.wifi.hotspot2.IProvisioningCallback) r3
                android.os.Bundle r4 = r17.getData()
                java.lang.String r5 = "OsuProvider"
                android.os.Parcelable r4 = r4.getParcelable(r5)
                android.net.wifi.hotspot2.OsuProvider r4 = (android.net.wifi.hotspot2.OsuProvider) r4
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r5 = r5.mPasspointManager
                int r6 = r1.arg1
                boolean r5 = r5.startSubscriptionProvisioning(r6, r4, r3)
                if (r5 == 0) goto L_0x0848
                r15 = r7
                goto L_0x0849
            L_0x0848:
            L_0x0849:
                r5 = r15
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r1.what
                r6.replyToMessage((android.os.Message) r1, (int) r8, (int) r5)
                goto L_0x0e0c
            L_0x0853:
                int r3 = r1.arg1
                if (r3 <= 0) goto L_0x0858
                r15 = r7
            L_0x0858:
                r3 = r15
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r4 = r4.mWifiNative
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r5 = r5.mInterfaceName
                r4.configureNeighborDiscoveryOffload(r5, r3)
                goto L_0x0e0c
            L_0x086a:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.what
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r5 = r5.mPasspointManager
                java.lang.Object r6 = r1.obj
                java.util.List r6 = (java.util.List) r6
                java.util.Map r5 = r5.getAllMatchingFqdnsForScanResults(r6)
                r3.replyToMessage((android.os.Message) r1, (int) r4, (java.lang.Object) r5)
                goto L_0x0e0c
            L_0x0881:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConnectivityManager r3 = r3.mWifiConnectivityManager
                int r4 = r1.arg1
                if (r4 != r7) goto L_0x088c
                r15 = r7
            L_0x088c:
                r3.enable(r15)
                goto L_0x0e0c
            L_0x0891:
                int r3 = r1.arg1
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.stopWifiIPPacketOffload(r3)
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ClientModeImpl$WifiNetworkAgent r5 = r5.mNetworkAgent
                if (r5 == 0) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ClientModeImpl$WifiNetworkAgent r5 = r5.mNetworkAgent
                r5.onSocketKeepaliveEvent(r3, r4)
                goto L_0x0e0c
            L_0x08ac:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r3 = r3.mWifiConfigManager
                int r4 = r1.arg1
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                int r4 = r4.intValue()
                java.util.Set r3 = r3.removeNetworksForUser(r4)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mTargetNetworkId
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                boolean r4 = r3.contains(r4)
                if (r4 != 0) goto L_0x08e0
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mLastNetworkId
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                boolean r4 = r3.contains(r4)
                if (r4 == 0) goto L_0x0e0c
            L_0x08e0:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.sendMessage(r13)
                goto L_0x0e0c
            L_0x08e7:
                java.lang.Object r3 = r1.obj
                java.lang.String r3 = (java.lang.String) r3
                if (r3 == 0) goto L_0x090c
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r4 = r4.mWifiConfigManager
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r5.mTargetNetworkId
                com.android.server.wifi.ScanDetailCache r4 = r4.getScanDetailCacheForNetwork(r5)
                if (r4 == 0) goto L_0x090c
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiMetrics r5 = r5.mWifiMetrics
                com.android.server.wifi.ScanDetail r6 = r4.getScanDetail(r3)
                r5.setConnectionScanDetail(r6)
            L_0x090c:
                r2 = 0
                goto L_0x0e0c
            L_0x090f:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                r4 = -5
                int unused = r3.mMessageHandlingStatus = r4
                goto L_0x0e0c
            L_0x0917:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                boolean unused = r3.mIsFilsConnection = r15
                int r3 = r1.arg1
                int r4 = r1.arg2
                java.lang.Object r5 = r1.obj
                java.lang.String r5 = (java.lang.String) r5
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                boolean r8 = r8.hasConnectionRequests()
                if (r8 != 0) goto L_0x0952
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ClientModeImpl$WifiNetworkAgent r8 = r8.mNetworkAgent
                if (r8 != 0) goto L_0x093d
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r8 = "CMD_START_CONNECT but no requests and not connected, bailing"
                r6.loge(r8)
                goto L_0x0e0c
            L_0x093d:
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.util.WifiPermissionsUtil r8 = r8.mWifiPermissionsUtil
                boolean r8 = r8.checkNetworkSettingsPermission(r4)
                if (r8 != 0) goto L_0x0952
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r8 = "CMD_START_CONNECT but no requests and connected, but app does not have sufficient permissions, bailing"
                r6.loge(r8)
                goto L_0x0e0c
            L_0x0952:
                com.android.server.wifi.ClientModeImpl r8 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r8 = r8.mWifiConfigManager
                android.net.wifi.WifiConfiguration r8 = r8.getConfiguredNetworkWithoutMasking(r3)
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "CMD_START_CONNECT sup state "
                r13.append(r14)
                com.android.server.wifi.ClientModeImpl r14 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.SupplicantStateTracker r14 = r14.mSupplicantStateTracker
                java.lang.String r14 = r14.getSupplicantStateName()
                r13.append(r14)
                java.lang.String r14 = " my state "
                r13.append(r14)
                com.android.server.wifi.ClientModeImpl r14 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.IState r14 = r14.getCurrentState()
                java.lang.String r14 = r14.getName()
                r13.append(r14)
                java.lang.String r14 = " nid="
                r13.append(r14)
                java.lang.String r14 = java.lang.Integer.toString(r3)
                r13.append(r14)
                java.lang.String r14 = " roam="
                r13.append(r14)
                com.android.server.wifi.ClientModeImpl r14 = com.android.server.wifi.ClientModeImpl.this
                boolean r14 = r14.mIsAutoRoaming
                java.lang.String r14 = java.lang.Boolean.toString(r14)
                r13.append(r14)
                java.lang.String r13 = r13.toString()
                r11.logd(r13)
                if (r8 != 0) goto L_0x09b7
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r9 = "CMD_START_CONNECT and no config, bail out..."
                r6.loge(r9)
                goto L_0x0e0c
            L_0x09b7:
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiScoreCard r11 = r11.mWifiScoreCard
                com.android.server.wifi.ClientModeImpl r13 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ExtendedWifiInfo r13 = r13.mWifiInfo
                r11.noteConnectionAttempt(r13)
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                int unused = r11.mTargetNetworkId = r3
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                boolean unused = r11.setTargetBssid(r8, r5)
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r13 = r11.mTargetRoamBSSID
                r11.reportConnectionAttemptStart(r8, r13, r10)
                int r11 = r8.macRandomizationSetting
                if (r11 != r7) goto L_0x09eb
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                boolean r11 = r11.mConnectedMacRandomzationSupported
                if (r11 == 0) goto L_0x09eb
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                r11.configureRandomizedMacAddress(r8)
                goto L_0x09f0
            L_0x09eb:
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                r11.setCurrentMacToFactoryMac(r8)
            L_0x09f0:
                com.android.server.wifi.ClientModeImpl r11 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r11 = r11.mWifiNative
                com.android.server.wifi.ClientModeImpl r13 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r13 = r13.mInterfaceName
                java.lang.String r11 = r11.getMacAddress(r13)
                com.android.server.wifi.ClientModeImpl r13 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.ExtendedWifiInfo r13 = r13.mWifiInfo
                r13.setMacAddress(r11)
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "Connecting with "
                r13.append(r14)
                r13.append(r11)
                java.lang.String r14 = " as the mac address"
                r13.append(r14)
                java.lang.String r13 = r13.toString()
                android.util.Log.i(r12, r13)
                java.util.BitSet r13 = r8.allowedKeyManagement
                boolean r13 = r13.get(r9)
                r14 = 14
                if (r13 != 0) goto L_0x0a34
                java.util.BitSet r13 = r8.allowedKeyManagement
                boolean r13 = r13.get(r14)
                if (r13 == 0) goto L_0x0a7e
            L_0x0a34:
                com.android.server.wifi.ClientModeImpl r13 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = "key_mgmt"
                java.lang.String r6 = r13.getCapabilities((java.lang.String) r6)
                java.lang.String r13 = "FILS-SHA256"
                boolean r13 = r6.contains(r13)
                if (r13 != 0) goto L_0x0a5d
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r7 = "FILS_SHA256 not supported, device capability: "
                r13.append(r7)
                r13.append(r6)
                java.lang.String r7 = r13.toString()
                android.util.Log.d(r12, r7)
                java.util.BitSet r7 = r8.allowedKeyManagement
                r7.clear(r9)
            L_0x0a5d:
                java.lang.String r7 = "FILS-SHA384"
                boolean r7 = r6.contains(r7)
                if (r7 != 0) goto L_0x0a7e
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.String r13 = "FILS_SHA384 not supported, device capability: "
                r7.append(r13)
                r7.append(r6)
                java.lang.String r7 = r7.toString()
                android.util.Log.d(r12, r7)
                java.util.BitSet r7 = r8.allowedKeyManagement
                r7.clear(r14)
            L_0x0a7e:
                java.util.BitSet r6 = r8.allowedKeyManagement
                boolean r6 = r6.get(r9)
                if (r6 != 0) goto L_0x0b3e
                java.util.BitSet r6 = r8.allowedKeyManagement
                boolean r6 = r6.get(r14)
                if (r6 == 0) goto L_0x0a90
                goto L_0x0b3e
            L_0x0a90:
                android.net.wifi.WifiEnterpriseConfig r6 = r8.enterpriseConfig
                if (r6 == 0) goto L_0x0acb
                android.net.wifi.WifiEnterpriseConfig r6 = r8.enterpriseConfig
                int r6 = r6.getEapMethod()
                boolean r6 = com.android.server.wifi.util.TelephonyUtil.isSimEapMethod(r6)
                if (r6 == 0) goto L_0x0acb
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiInjector r6 = r6.mWifiInjector
                com.android.server.wifi.CarrierNetworkConfig r6 = r6.getCarrierNetworkConfig()
                boolean r6 = r6.isCarrierEncryptionInfoAvailable()
                if (r6 == 0) goto L_0x0acb
                android.net.wifi.WifiEnterpriseConfig r6 = r8.enterpriseConfig
                java.lang.String r6 = r6.getAnonymousIdentity()
                boolean r6 = android.text.TextUtils.isEmpty(r6)
                if (r6 == 0) goto L_0x0acb
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                android.telephony.TelephonyManager r6 = r6.getTelephonyManager()
                java.lang.String r6 = com.android.server.wifi.util.TelephonyUtil.getAnonymousIdentityWith3GppRealm(r6)
                android.net.wifi.WifiEnterpriseConfig r7 = r8.enterpriseConfig
                r7.setAnonymousIdentity(r6)
            L_0x0acb:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r6 = r6.mWifiNative
                com.android.server.wifi.ClientModeImpl r7 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r7 = r7.mInterfaceName
                boolean r6 = r6.connectToNetwork(r7, r8)
                if (r6 == 0) goto L_0x0b18
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiMetrics r6 = r6.mWifiMetrics
                r7 = 11
                r6.logStaEvent((int) r7, (android.net.wifi.WifiConfiguration) r8)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.Clock r7 = r6.mClock
                long r9 = r7.getWallClockMillis()
                long unused = r6.mLastConnectAttemptTimestamp = r9
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration unused = r6.mTargetWifiConfiguration = r8
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                boolean unused = r6.mIsAutoRoaming = r15
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.IState r6 = r6.getCurrentState()
                com.android.server.wifi.ClientModeImpl r7 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.State r7 = r7.mDisconnectedState
                if (r6 == r7) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.State r7 = r6.mDisconnectingState
                r6.transitionTo(r7)
                goto L_0x0e0c
            L_0x0b18:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.String r9 = "CMD_START_CONNECT Failed to start connection to network "
                r7.append(r9)
                r7.append(r8)
                java.lang.String r7 = r7.toString()
                r6.loge(r7)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                r7 = 1
                r6.reportConnectionAttemptEnd(r10, r7, r15)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                r7 = 151554(0x25002, float:2.12372E-40)
                r6.replyToMessage((android.os.Message) r1, (int) r7, (int) r15)
                goto L_0x0e0c
            L_0x0b3e:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                android.net.wifi.WifiConfiguration unused = r6.mFilsConfig = r8
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                com.android.internal.util.State r7 = r6.mFilsState
                r6.transitionTo(r7)
                goto L_0x0e0c
            L_0x0b4e:
                java.lang.Object r3 = r1.obj
                if (r3 == 0) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.Object r4 = r1.obj
                java.lang.String r4 = (java.lang.String) r4
                java.lang.String unused = r3.mTargetRoamBSSID = r4
                goto L_0x0e0c
            L_0x0b5d:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.what
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r5 = r5.mPasspointManager
                java.lang.Object r6 = r1.obj
                java.util.List r6 = (java.util.List) r6
                java.util.List r5 = r5.getWifiConfigsForPasspointProfiles(r6)
                r3.replyToMessage((android.os.Message) r1, (int) r4, (java.lang.Object) r5)
                goto L_0x0e0c
            L_0x0b74:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.what
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r5 = r5.mPasspointManager
                java.lang.Object r6 = r1.obj
                java.util.List r6 = (java.util.List) r6
                java.util.Map r5 = r5.getMatchingPasspointConfigsForOsuProviders(r6)
                r3.replyToMessage((android.os.Message) r1, (int) r4, (java.lang.Object) r5)
                goto L_0x0e0c
            L_0x0b8b:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.what
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r5 = r5.mPasspointManager
                java.lang.Object r6 = r1.obj
                java.util.List r6 = (java.util.List) r6
                java.util.Map r5 = r5.getMatchingOsuProviders(r6)
                r3.replyToMessage((android.os.Message) r1, (int) r4, (java.lang.Object) r5)
                goto L_0x0e0c
            L_0x0ba2:
                java.lang.Object r3 = r1.obj
                java.lang.String r3 = (java.lang.String) r3
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r4 = r4.mPasspointManager
                boolean r4 = r4.removeProvider(r3)
                if (r4 == 0) goto L_0x0be9
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r4.mTargetNetworkId
                boolean r4 = r4.isProviderOwnedNetwork(r5, r3)
                if (r4 != 0) goto L_0x0bca
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r4.mLastNetworkId
                boolean r4 = r4.isProviderOwnedNetwork(r5, r3)
                if (r4 == 0) goto L_0x0bd6
            L_0x0bca:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r5 = "Disconnect from current network since its provider is removed"
                r4.logd(r5)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.sendMessage(r13)
            L_0x0bd6:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r4 = r4.mWifiConfigManager
                r4.removePasspointConfiguredNetwork(r3)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r6 = 1
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r6)
                goto L_0x0e0c
            L_0x0be9:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r6 = -1
                r4.replyToMessage((android.os.Message) r1, (int) r5, (int) r6)
                goto L_0x0e0c
            L_0x0bf3:
                java.lang.Object r3 = r1.obj
                android.os.Bundle r3 = (android.os.Bundle) r3
                java.lang.String r4 = "PasspointConfiguration"
                android.os.Parcelable r4 = r3.getParcelable(r4)
                android.net.wifi.hotspot2.PasspointConfiguration r4 = (android.net.wifi.hotspot2.PasspointConfiguration) r4
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r5 = r5.mPasspointManager
                java.lang.String r6 = "uid"
                int r6 = r3.getInt(r6)
                java.lang.String r7 = "PackageName"
                java.lang.String r7 = r3.getString(r7)
                boolean r5 = r5.addOrUpdateProvider(r4, r6, r7)
                if (r5 == 0) goto L_0x0c4d
                android.net.wifi.hotspot2.pps.HomeSp r5 = r4.getHomeSp()
                java.lang.String r5 = r5.getFqdn()
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                int r7 = r6.mTargetNetworkId
                boolean r6 = r6.isProviderOwnedNetwork(r7, r5)
                if (r6 != 0) goto L_0x0c37
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                int r7 = r6.mLastNetworkId
                boolean r6 = r6.isProviderOwnedNetwork(r7, r5)
                if (r6 == 0) goto L_0x0c43
            L_0x0c37:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r7 = "Disconnect from current network since its provider is updated"
                r6.logd(r7)
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                r6.sendMessage(r13)
            L_0x0c43:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                int r7 = r1.what
                r8 = 1
                r6.replyToMessage((android.os.Message) r1, (int) r7, (int) r8)
                goto L_0x0e0c
            L_0x0c4d:
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                int r6 = r1.what
                r7 = -1
                r5.replyToMessage((android.os.Message) r1, (int) r6, (int) r7)
                goto L_0x0e0c
            L_0x0c57:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.what
                r3.replyToMessage((android.os.Message) r1, (int) r4, (int) r15)
                goto L_0x0e0c
            L_0x0c60:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r3 = r3.mPasspointManager
                java.lang.Object r4 = r1.obj
                android.os.Bundle r4 = (android.os.Bundle) r4
                java.lang.String r5 = "BSSID"
                long r4 = r4.getLong(r5)
                java.lang.Object r6 = r1.obj
                android.os.Bundle r6 = (android.os.Bundle) r6
                java.lang.String r7 = "FILENAME"
                java.lang.String r6 = r6.getString(r7)
                r3.queryPasspointIcon(r4, r6)
                goto L_0x0e0c
            L_0x0c7f:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = "resetting EAP-SIM/AKA/AKA' networks since SIM was changed"
                r3.log(r4)
                int r3 = r1.arg1
                r4 = 1
                if (r3 != r4) goto L_0x0c8c
                r15 = 1
            L_0x0c8c:
                r3 = r15
                if (r3 != 0) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.hotspot2.PasspointManager r4 = r4.mPasspointManager
                r4.removeEphemeralProviders()
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r4 = r4.mWifiConfigManager
                r4.resetSimNetworks()
                goto L_0x0e0c
            L_0x0ca3:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r3 = r3.mWifiConfigManager
                java.lang.Object r4 = r1.obj
                java.lang.String r4 = (java.lang.String) r4
                android.net.wifi.WifiConfiguration r3 = r3.disableEphemeralNetwork(r4)
                if (r3 == 0) goto L_0x0e0c
                int r4 = r3.networkId
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r5.mTargetNetworkId
                if (r4 == r5) goto L_0x0cc7
                int r4 = r3.networkId
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r5.mLastNetworkId
                if (r4 != r5) goto L_0x0e0c
            L_0x0cc7:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.sendMessage(r13)
                goto L_0x0e0c
            L_0x0cce:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r3 = r3.mWifiConfigManager
                java.lang.Object r4 = r1.obj
                android.content.pm.ApplicationInfo r4 = (android.content.pm.ApplicationInfo) r4
                java.util.Set r3 = r3.removeNetworksForApp(r4)
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mTargetNetworkId
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                boolean r4 = r3.contains(r4)
                if (r4 != 0) goto L_0x0cfc
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mLastNetworkId
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                boolean r4 = r3.contains(r4)
                if (r4 == 0) goto L_0x0e0c
            L_0x0cfc:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.sendMessage(r13)
                goto L_0x0e0c
            L_0x0d03:
                java.lang.Object r3 = r1.obj
                if (r3 == 0) goto L_0x0e0c
                java.lang.Object r3 = r1.obj
                java.lang.String r3 = (java.lang.String) r3
                int r4 = r1.arg1
                r5 = 1
                if (r4 != r5) goto L_0x0d11
                r15 = 1
            L_0x0d11:
                r4 = r15
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r5 = r5.mWifiNative
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r6 = r6.mInterfaceName
                r5.startTdls(r6, r3, r4)
                goto L_0x0e0c
            L_0x0d23:
                int r3 = r1.arg1
                r4 = 1
                if (r3 != r4) goto L_0x0d3c
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                r3.setSuspendOptimizationsNative(r8, r4)
                int r3 = r1.arg2
                if (r3 != r4) goto L_0x0e0c
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                android.os.PowerManager$WakeLock r3 = r3.mSuspendWakeLock
                r3.release()
                goto L_0x0e0c
            L_0x0d3c:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                r3.setSuspendOptimizationsNative(r8, r15)
                goto L_0x0e0c
            L_0x0d43:
                int r3 = r1.arg1
                r4 = 1
                if (r3 != r4) goto L_0x0d4f
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                r3.setSuspendOptimizationsNative(r14, r15)
                goto L_0x0e0c
            L_0x0d4f:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                r3.setSuspendOptimizationsNative(r14, r4)
                goto L_0x0e0c
            L_0x0d56:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.Clock r4 = r3.mClock
                long r4 = r4.getWallClockMillis()
                long unused = r3.mLastConnectAttemptTimestamp = r4
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                r3.reassociate(r4)
                goto L_0x0e0c
            L_0x0d74:
                java.lang.Object r3 = r1.obj
                android.os.WorkSource r3 = (android.os.WorkSource) r3
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConnectivityManager r4 = r4.mWifiConnectivityManager
                r4.forceConnectivityScan(r3)
                goto L_0x0e0c
            L_0x0d83:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiLinkLayerStats r3 = r3.getWifiLinkLayerStats()
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r5 = r1.what
                r4.replyToMessage((android.os.Message) r1, (int) r5, (java.lang.Object) r3)
                goto L_0x0e0c
            L_0x0d92:
                r7 = -1
                int r3 = r1.arg2
                r4 = 1
                if (r3 != r4) goto L_0x0d9a
                r3 = 1
                goto L_0x0d9b
            L_0x0d9a:
                r3 = r15
            L_0x0d9b:
                int r4 = r1.arg1
                if (r3 == 0) goto L_0x0da8
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                int r6 = r1.sendingUid
                boolean r5 = r5.connectToUserSelectNetwork(r4, r6, r15)
                goto L_0x0db4
            L_0x0da8:
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiConfigManager r5 = r5.mWifiConfigManager
                int r6 = r1.sendingUid
                boolean r5 = r5.enableNetwork(r4, r15, r6)
            L_0x0db4:
                if (r5 != 0) goto L_0x0dbb
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                int unused = r6.mMessageHandlingStatus = r11
            L_0x0dbb:
                com.android.server.wifi.ClientModeImpl r6 = com.android.server.wifi.ClientModeImpl.this
                int r8 = r1.what
                if (r5 == 0) goto L_0x0dc2
                r7 = 1
            L_0x0dc2:
                r6.replyToMessage((android.os.Message) r1, (int) r8, (int) r7)
                goto L_0x0e0c
            L_0x0dc6:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                boolean r3 = r3.deleteNetworkConfigAndSendReply(r1, r15)
                if (r3 != 0) goto L_0x0dd4
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int unused = r3.mMessageHandlingStatus = r11
                goto L_0x0e0c
            L_0x0dd4:
                int r3 = r1.arg1
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mTargetNetworkId
                if (r3 == r4) goto L_0x0de6
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r4.mLastNetworkId
                if (r3 != r4) goto L_0x0e0c
            L_0x0de6:
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                r4.sendMessage(r13)
                goto L_0x0e0c
            L_0x0dec:
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                int r4 = r1.arg1
                if (r4 == 0) goto L_0x0df3
                r15 = 1
            L_0x0df3:
                boolean unused = r3.mBluetoothConnectionActive = r15
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                com.android.server.wifi.WifiNative r3 = r3.mWifiNative
                com.android.server.wifi.ClientModeImpl r4 = com.android.server.wifi.ClientModeImpl.this
                java.lang.String r4 = r4.mInterfaceName
                com.android.server.wifi.ClientModeImpl r5 = com.android.server.wifi.ClientModeImpl.this
                boolean r5 = r5.mBluetoothConnectionActive
                r3.setBluetoothCoexistenceScanMode(r4, r5)
            L_0x0e0c:
                r3 = 1
                if (r2 != r3) goto L_0x0e14
                com.android.server.wifi.ClientModeImpl r3 = com.android.server.wifi.ClientModeImpl.this
                r3.logStateAndMessage(r1, r0)
            L_0x0e14:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.ClientModeImpl.ConnectModeState.processMessage(android.os.Message):boolean");
        }
    }

    private WifiNetworkAgentSpecifier createNetworkAgentSpecifier(WifiConfiguration currentWifiConfiguration, String currentBssid, int specificRequestUid, String specificRequestPackageName) {
        currentWifiConfiguration.BSSID = currentBssid;
        return new WifiNetworkAgentSpecifier(currentWifiConfiguration, specificRequestUid, specificRequestPackageName);
    }

    /* access modifiers changed from: private */
    public NetworkCapabilities getCapabilities(WifiConfiguration currentWifiConfiguration) {
        NetworkCapabilities result = new NetworkCapabilities(this.mNetworkCapabilitiesFilter);
        result.setNetworkSpecifier((NetworkSpecifier) null);
        if (currentWifiConfiguration == null) {
            return result;
        }
        if (!this.mWifiInfo.isTrusted()) {
            result.removeCapability(14);
        } else {
            result.addCapability(14);
        }
        if (!WifiConfiguration.isMetered(currentWifiConfiguration, this.mWifiInfo)) {
            result.addCapability(11);
        } else {
            result.removeCapability(11);
        }
        if (this.mWifiInfo.getRssi() != -127) {
            result.setSignalStrength(this.mWifiInfo.getRssi());
        } else {
            result.setSignalStrength(Integer.MIN_VALUE);
        }
        if (currentWifiConfiguration.osu) {
            result.removeCapability(12);
        }
        if (!this.mWifiInfo.getSSID().equals("<unknown ssid>")) {
            result.setSSID(this.mWifiInfo.getSSID());
        } else {
            result.setSSID((String) null);
        }
        Pair<Integer, String> specificRequestUidAndPackageName = this.mNetworkFactory.getSpecificNetworkRequestUidAndPackageName(currentWifiConfiguration);
        if (((Integer) specificRequestUidAndPackageName.first).intValue() != -1) {
            result.removeCapability(12);
        }
        result.setNetworkSpecifier(createNetworkAgentSpecifier(currentWifiConfiguration, getCurrentBSSID(), ((Integer) specificRequestUidAndPackageName.first).intValue(), (String) specificRequestUidAndPackageName.second));
        return result;
    }

    public void updateCapabilities() {
        updateCapabilities(getCurrentWifiConfiguration());
    }

    private void updateCapabilities(WifiConfiguration currentWifiConfiguration) {
        WifiNetworkAgent wifiNetworkAgent = this.mNetworkAgent;
        if (wifiNetworkAgent != null) {
            wifiNetworkAgent.sendNetworkCapabilities(getCapabilities(currentWifiConfiguration));
        }
    }

    /* access modifiers changed from: private */
    public boolean isProviderOwnedNetwork(int networkId, String providerFqdn) {
        WifiConfiguration config;
        if (networkId == -1 || (config = this.mWifiConfigManager.getConfiguredNetwork(networkId)) == null) {
            return false;
        }
        return TextUtils.equals(config.FQDN, providerFqdn);
    }

    /* access modifiers changed from: private */
    public void handleEapAuthFailure(int networkId, int errorCode) {
        WifiConfiguration targetedNetwork = this.mWifiConfigManager.getConfiguredNetwork(this.mTargetNetworkId);
        if (targetedNetwork != null) {
            int eapMethod = targetedNetwork.enterpriseConfig.getEapMethod();
            if ((eapMethod == 4 || eapMethod == 5 || eapMethod == 6) && errorCode == 16385) {
                getTelephonyManager().createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId()).resetCarrierKeysForImsiEncryption();
            }
        }
    }

    private class WifiNetworkAgent extends NetworkAgent {
        private int mLastNetworkStatus = -1;
        final /* synthetic */ ClientModeImpl this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        WifiNetworkAgent(ClientModeImpl clientModeImpl, Looper l, Context c, String tag, NetworkInfo ni, NetworkCapabilities nc, LinkProperties lp, int score, NetworkMisc misc) {
            super(l, c, tag, ni, nc, lp, score, misc);
            this.this$0 = clientModeImpl;
        }

        /* access modifiers changed from: protected */
        public void unwanted() {
            if (this == this.this$0.mNetworkAgent) {
                if (this.this$0.mVerboseLoggingEnabled) {
                    log("WifiNetworkAgent -> Wifi unwanted score " + Integer.toString(this.this$0.mWifiInfo.score));
                }
                this.this$0.unwantedNetwork(0);
            }
        }

        /* access modifiers changed from: protected */
        public void networkStatus(int status, String redirectUrl) {
            if (this == this.this$0.mNetworkAgent && status != this.mLastNetworkStatus) {
                this.mLastNetworkStatus = status;
                if (status == 2) {
                    if (this.this$0.mVerboseLoggingEnabled) {
                        log("WifiNetworkAgent -> Wifi networkStatus invalid, score=" + Integer.toString(this.this$0.mWifiInfo.score));
                    }
                    this.this$0.unwantedNetwork(1);
                } else if (status == 1) {
                    if (this.this$0.mVerboseLoggingEnabled) {
                        log("WifiNetworkAgent -> Wifi networkStatus valid, score= " + Integer.toString(this.this$0.mWifiInfo.score));
                    }
                    this.this$0.mWifiMetrics.logStaEvent(14);
                    this.this$0.doNetworkStatus(status);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void saveAcceptUnvalidated(boolean accept) {
            if (this == this.this$0.mNetworkAgent) {
                this.this$0.sendMessage(ClientModeImpl.CMD_ACCEPT_UNVALIDATED, accept);
            }
        }

        /* access modifiers changed from: protected */
        public void startSocketKeepalive(Message msg) {
            this.this$0.sendMessage(ClientModeImpl.CMD_START_IP_PACKET_OFFLOAD, msg.arg1, msg.arg2, msg.obj);
        }

        /* access modifiers changed from: protected */
        public void stopSocketKeepalive(Message msg) {
            this.this$0.sendMessage(ClientModeImpl.CMD_STOP_IP_PACKET_OFFLOAD, msg.arg1, msg.arg2, msg.obj);
        }

        /* access modifiers changed from: protected */
        public void addKeepalivePacketFilter(Message msg) {
            this.this$0.sendMessage(ClientModeImpl.CMD_ADD_KEEPALIVE_PACKET_FILTER_TO_APF, msg.arg1, msg.arg2, msg.obj);
        }

        /* access modifiers changed from: protected */
        public void removeKeepalivePacketFilter(Message msg) {
            this.this$0.sendMessage(ClientModeImpl.CMD_REMOVE_KEEPALIVE_PACKET_FILTER_FROM_APF, msg.arg1, msg.arg2, msg.obj);
        }

        /* access modifiers changed from: protected */
        public void setSignalStrengthThresholds(int[] thresholds) {
            log("Received signal strength thresholds: " + Arrays.toString(thresholds));
            if (thresholds.length == 0) {
                ClientModeImpl clientModeImpl = this.this$0;
                clientModeImpl.sendMessage(ClientModeImpl.CMD_STOP_RSSI_MONITORING_OFFLOAD, clientModeImpl.mWifiInfo.getRssi());
                return;
            }
            int[] rssiVals = Arrays.copyOf(thresholds, thresholds.length + 2);
            rssiVals[rssiVals.length - 2] = -128;
            rssiVals[rssiVals.length - 1] = 127;
            Arrays.sort(rssiVals);
            byte[] rssiRange = new byte[rssiVals.length];
            for (int i = 0; i < rssiVals.length; i++) {
                int val = rssiVals[i];
                if (val > 127 || val < -128) {
                    Log.e(ClientModeImpl.TAG, "Illegal value " + val + " for RSSI thresholds: " + Arrays.toString(rssiVals));
                    ClientModeImpl clientModeImpl2 = this.this$0;
                    clientModeImpl2.sendMessage(ClientModeImpl.CMD_STOP_RSSI_MONITORING_OFFLOAD, clientModeImpl2.mWifiInfo.getRssi());
                    return;
                }
                rssiRange[i] = (byte) val;
            }
            byte[] unused = this.this$0.mRssiRanges = rssiRange;
            ClientModeImpl clientModeImpl3 = this.this$0;
            clientModeImpl3.sendMessage(ClientModeImpl.CMD_START_RSSI_MONITORING_OFFLOAD, clientModeImpl3.mWifiInfo.getRssi());
        }

        /* access modifiers changed from: protected */
        public void preventAutomaticReconnect() {
            if (this == this.this$0.mNetworkAgent) {
                this.this$0.unwantedNetwork(2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unwantedNetwork(int reason) {
        sendMessage(CMD_UNWANTED_NETWORK, reason);
    }

    /* access modifiers changed from: package-private */
    public void doNetworkStatus(int status) {
        sendMessage(CMD_NETWORK_STATUS, status);
    }

    private String buildIdentity(int eapMethod, String imsi, String mccMnc) {
        String prefix;
        String mnc;
        String mcc;
        if (imsi == null || imsi.isEmpty()) {
            return Prefix.EMPTY;
        }
        if (eapMethod == 4) {
            prefix = "1";
        } else if (eapMethod == 5) {
            prefix = "0";
        } else if (eapMethod != 6) {
            return Prefix.EMPTY;
        } else {
            prefix = ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK;
        }
        if (mccMnc == null || mccMnc.isEmpty()) {
            mcc = imsi.substring(0, 3);
            mnc = imsi.substring(3, 6);
        } else {
            mcc = mccMnc.substring(0, 3);
            mnc = mccMnc.substring(3);
            if (mnc.length() == 2) {
                mnc = "0" + mnc;
            }
        }
        return prefix + imsi + "@wlan.mnc" + mnc + ".mcc" + mcc + ".3gppnetwork.org";
    }

    class L2ConnectedState extends State {
        RssiEventHandler mRssiEventHandler = new RssiEventHandler();

        class RssiEventHandler implements WifiNative.WifiRssiEventHandler {
            RssiEventHandler() {
            }

            public void onRssiThresholdBreached(byte curRssi) {
                if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                    Log.e(ClientModeImpl.TAG, "onRssiThresholdBreach event. Cur Rssi = " + curRssi);
                }
                ClientModeImpl.this.sendMessage(ClientModeImpl.CMD_RSSI_THRESHOLD_BREACHED, curRssi);
            }
        }

        L2ConnectedState() {
        }

        public void enter() {
            ClientModeImpl.access$8708(ClientModeImpl.this);
            if (ClientModeImpl.this.mEnableRssiPolling) {
                ClientModeImpl.this.mLinkProbeManager.resetOnNewConnection();
                ClientModeImpl clientModeImpl = ClientModeImpl.this;
                clientModeImpl.sendMessage(ClientModeImpl.CMD_RSSI_POLL, clientModeImpl.mRssiPollToken, 0);
            }
            if (ClientModeImpl.this.mNetworkAgent != null) {
                ClientModeImpl.this.loge("Have NetworkAgent when entering L2Connected");
                boolean unused = ClientModeImpl.this.setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
            }
            boolean unused2 = ClientModeImpl.this.setNetworkDetailedState(NetworkInfo.DetailedState.CONNECTING);
            ClientModeImpl.this.mNetworkMisc.keepScore = true;
            ClientModeImpl clientModeImpl2 = ClientModeImpl.this;
            NetworkCapabilities nc = clientModeImpl2.getCapabilities(clientModeImpl2.getCurrentWifiConfiguration());
            synchronized (ClientModeImpl.this.mNetworkAgentLock) {
                WifiNetworkAgent unused3 = ClientModeImpl.this.mNetworkAgent = new WifiNetworkAgent(ClientModeImpl.this, ClientModeImpl.this.getHandler().getLooper(), ClientModeImpl.this.mContext, "WifiNetworkAgent", ClientModeImpl.this.mNetworkInfo, nc, ClientModeImpl.this.mLinkProperties, 60, ClientModeImpl.this.mNetworkMisc);
            }
            ClientModeImpl.this.clearTargetBssid("L2ConnectedState");
            ClientModeImpl.this.mCountryCode.setReadyForChange(false);
            ClientModeImpl.this.mWifiMetrics.setWifiState(3);
            ClientModeImpl.this.mWifiScoreCard.noteNetworkAgentCreated(ClientModeImpl.this.mWifiInfo, ClientModeImpl.this.mNetworkAgent.netId);
        }

        public void exit() {
            if (ClientModeImpl.this.mIpClient != null) {
                ClientModeImpl.this.mIpClient.stop();
                boolean unused = ClientModeImpl.this.mIsIpClientStarted = false;
            }
            if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                StringBuilder sb = new StringBuilder();
                sb.append("leaving L2ConnectedState state nid=" + Integer.toString(ClientModeImpl.this.mLastNetworkId));
                if (ClientModeImpl.this.mLastBssid != null) {
                    sb.append(" ");
                    sb.append(ClientModeImpl.this.mLastBssid);
                }
            }
            if (!(ClientModeImpl.this.mLastBssid == null && ClientModeImpl.this.mLastNetworkId == -1)) {
                ClientModeImpl.this.handleNetworkDisconnect();
            }
            ClientModeImpl.this.mCountryCode.setReadyForChange(true);
            ClientModeImpl.this.mWifiMetrics.setWifiState(2);
            ClientModeImpl.this.mWifiStateTracker.updateState(2);
            ClientModeImpl.this.mWifiInjector.getWifiLockManager().updateWifiClientConnected(false);
        }

        public boolean processMessage(Message message) {
            ScanDetailCache scanDetailCache;
            ScanResult scanResult;
            boolean handleStatus = true;
            switch (message.what) {
                case ClientModeImpl.CMD_DISCONNECT /*131145*/:
                    ClientModeImpl.this.mWifiMetrics.logStaEvent(15, 2);
                    ClientModeImpl.this.mWifiNative.disconnect(ClientModeImpl.this.mInterfaceName);
                    ClientModeImpl clientModeImpl = ClientModeImpl.this;
                    clientModeImpl.transitionTo(clientModeImpl.mDisconnectingState);
                    break;
                case ClientModeImpl.CMD_RECONNECT /*131146*/:
                    ClientModeImpl.this.log(" Ignore CMD_RECONNECT request because wifi is already connected");
                    break;
                case ClientModeImpl.CMD_ENABLE_RSSI_POLL /*131154*/:
                    ClientModeImpl.this.cleanWifiScore();
                    boolean unused = ClientModeImpl.this.mEnableRssiPolling = message.arg1 == 1;
                    ClientModeImpl.access$8708(ClientModeImpl.this);
                    if (ClientModeImpl.this.mEnableRssiPolling) {
                        int unused2 = ClientModeImpl.this.mLastSignalLevel = -1;
                        ClientModeImpl.this.mLinkProbeManager.resetOnScreenTurnedOn();
                        ClientModeImpl.this.fetchRssiLinkSpeedAndFrequencyNative();
                        ClientModeImpl clientModeImpl2 = ClientModeImpl.this;
                        clientModeImpl2.sendMessageDelayed(clientModeImpl2.obtainMessage(ClientModeImpl.CMD_RSSI_POLL, clientModeImpl2.mRssiPollToken, 0), (long) ClientModeImpl.this.mPollRssiIntervalMsecs);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_RSSI_POLL /*131155*/:
                    if (message.arg1 == ClientModeImpl.this.mRssiPollToken) {
                        WifiLinkLayerStats stats = updateLinkLayerStatsRssiAndScoreReportInternal();
                        ClientModeImpl.this.mWifiMetrics.updateWifiUsabilityStatsEntries(ClientModeImpl.this.mWifiInfo, stats);
                        if (ClientModeImpl.this.mWifiScoreReport.shouldCheckIpLayer()) {
                            if (ClientModeImpl.this.mIpClient != null) {
                                ClientModeImpl.this.mIpClient.confirmConfiguration();
                            }
                            ClientModeImpl.this.mWifiScoreReport.noteIpCheck();
                        }
                        int statusDataStall = ClientModeImpl.this.mWifiDataStall.checkForDataStall(ClientModeImpl.this.mLastLinkLayerStats, stats);
                        if (statusDataStall != 0) {
                            ClientModeImpl.this.mWifiMetrics.addToWifiUsabilityStatsList(2, ClientModeImpl.convertToUsabilityStatsTriggerType(statusDataStall), -1);
                        }
                        ClientModeImpl.this.mWifiMetrics.incrementWifiLinkLayerUsageStats(stats);
                        WifiLinkLayerStats unused3 = ClientModeImpl.this.mLastLinkLayerStats = stats;
                        ClientModeImpl.this.mWifiScoreCard.noteSignalPoll(ClientModeImpl.this.mWifiInfo);
                        ClientModeImpl.this.mLinkProbeManager.updateConnectionStats(ClientModeImpl.this.mWifiInfo, ClientModeImpl.this.mInterfaceName);
                        ClientModeImpl clientModeImpl3 = ClientModeImpl.this;
                        clientModeImpl3.sendMessageDelayed(clientModeImpl3.obtainMessage(ClientModeImpl.CMD_RSSI_POLL, clientModeImpl3.mRssiPollToken, 0), (long) ClientModeImpl.this.mPollRssiIntervalMsecs);
                        if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                            ClientModeImpl clientModeImpl4 = ClientModeImpl.this;
                            clientModeImpl4.sendRssiChangeBroadcast(clientModeImpl4.mWifiInfo.getRssi());
                        }
                        ClientModeImpl.this.mWifiTrafficPoller.notifyOnDataActivity(ClientModeImpl.this.mWifiInfo.txSuccess, ClientModeImpl.this.mWifiInfo.rxSuccess);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_ONESHOT_RSSI_POLL /*131156*/:
                    if (!ClientModeImpl.this.mEnableRssiPolling) {
                        updateLinkLayerStatsRssiAndScoreReportInternal();
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_RESET_SIM_NETWORKS /*131173*/:
                    if (message.arg1 == 0 && ClientModeImpl.this.mLastNetworkId != -1 && TelephonyUtil.isSimConfig(ClientModeImpl.this.mWifiConfigManager.getConfiguredNetwork(ClientModeImpl.this.mLastNetworkId))) {
                        ClientModeImpl.this.mWifiMetrics.logStaEvent(15, 6);
                        ClientModeImpl.this.mWifiNative.disconnect(ClientModeImpl.this.mInterfaceName);
                        ClientModeImpl clientModeImpl5 = ClientModeImpl.this;
                        clientModeImpl5.transitionTo(clientModeImpl5.mDisconnectingState);
                    }
                    handleStatus = false;
                    break;
                case ClientModeImpl.CMD_IP_CONFIGURATION_SUCCESSFUL /*131210*/:
                    if (ClientModeImpl.this.getCurrentWifiConfiguration() != null) {
                        ClientModeImpl.this.handleSuccessfulIpConfiguration();
                        ClientModeImpl.this.sendConnectedState();
                        ClientModeImpl clientModeImpl6 = ClientModeImpl.this;
                        clientModeImpl6.transitionTo(clientModeImpl6.mConnectedState);
                        break;
                    } else {
                        ClientModeImpl.this.reportConnectionAttemptEnd(6, 1, 0);
                        ClientModeImpl.this.mWifiNative.disconnect(ClientModeImpl.this.mInterfaceName);
                        ClientModeImpl clientModeImpl7 = ClientModeImpl.this;
                        clientModeImpl7.transitionTo(clientModeImpl7.mDisconnectingState);
                        break;
                    }
                case ClientModeImpl.CMD_IP_CONFIGURATION_LOST /*131211*/:
                    ClientModeImpl.this.getWifiLinkLayerStats();
                    ClientModeImpl.this.handleIpConfigurationLost();
                    ClientModeImpl.this.reportConnectionAttemptEnd(10, 1, 0);
                    ClientModeImpl.this.mWifiInjector.getWifiLastResortWatchdog().noteConnectionFailureAndTriggerIfNeeded(ClientModeImpl.this.getTargetSsid(), ClientModeImpl.this.mTargetRoamBSSID, 3);
                    ClientModeImpl clientModeImpl8 = ClientModeImpl.this;
                    clientModeImpl8.transitionTo(clientModeImpl8.mDisconnectingState);
                    break;
                case ClientModeImpl.CMD_ASSOCIATED_BSSID /*131219*/:
                    if (((String) message.obj) != null) {
                        String unused4 = ClientModeImpl.this.mLastBssid = (String) message.obj;
                        if (ClientModeImpl.this.mLastBssid != null && (ClientModeImpl.this.mWifiInfo.getBSSID() == null || !ClientModeImpl.this.mLastBssid.equals(ClientModeImpl.this.mWifiInfo.getBSSID()))) {
                            ClientModeImpl.this.mWifiInfo.setBSSID(ClientModeImpl.this.mLastBssid);
                            ClientModeImpl.this.updateWifiGenerationInfo();
                            WifiConfiguration config = ClientModeImpl.this.getCurrentWifiConfiguration();
                            if (!(config == null || (scanDetailCache = ClientModeImpl.this.mWifiConfigManager.getScanDetailCacheForNetwork(config.networkId)) == null || (scanResult = scanDetailCache.getScanResult(ClientModeImpl.this.mLastBssid)) == null)) {
                                ClientModeImpl.this.mWifiInfo.setFrequency(scanResult.frequency);
                            }
                            ClientModeImpl clientModeImpl9 = ClientModeImpl.this;
                            clientModeImpl9.sendNetworkStateChangeBroadcast(clientModeImpl9.mLastBssid);
                            break;
                        }
                    } else {
                        ClientModeImpl.this.logw("Associated command w/o BSSID");
                        break;
                    }
                case ClientModeImpl.CMD_IP_REACHABILITY_LOST /*131221*/:
                    if (ClientModeImpl.this.mVerboseLoggingEnabled && message.obj != null) {
                        ClientModeImpl.this.log((String) message.obj);
                    }
                    ClientModeImpl.this.mWifiDiagnostics.captureBugReportData(9);
                    ClientModeImpl.this.mWifiMetrics.logWifiIsUnusableEvent(5);
                    ClientModeImpl.this.mWifiMetrics.addToWifiUsabilityStatsList(2, 5, -1);
                    if (ClientModeImpl.this.mIpReachabilityDisconnectEnabled) {
                        if (ClientModeImpl.this.mDisconnectOnlyOnInitialIpReachability && !ClientModeImpl.this.mIpReachabilityMonitorActive) {
                            ClientModeImpl.this.logd("CMD_IP_REACHABILITY_LOST Connect session is over, skip ip reachability lost indication.");
                            break;
                        } else {
                            ClientModeImpl.this.handleIpReachabilityLost();
                            ClientModeImpl.this.mWifiDiagnostics.captureBugReportData(11);
                            ClientModeImpl clientModeImpl10 = ClientModeImpl.this;
                            clientModeImpl10.transitionTo(clientModeImpl10.mDisconnectingState);
                            break;
                        }
                    } else {
                        ClientModeImpl.this.logd("CMD_IP_REACHABILITY_LOST but disconnect disabled -- ignore");
                        break;
                    }
                case ClientModeImpl.CMD_START_IP_PACKET_OFFLOAD /*131232*/:
                    int slot = message.arg1;
                    int result = ClientModeImpl.this.startWifiIPPacketOffload(slot, (KeepalivePacketData) message.obj, message.arg2);
                    if (ClientModeImpl.this.mNetworkAgent != null) {
                        ClientModeImpl.this.mNetworkAgent.onSocketKeepaliveEvent(slot, result);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_START_RSSI_MONITORING_OFFLOAD /*131234*/:
                case ClientModeImpl.CMD_RSSI_THRESHOLD_BREACHED /*131236*/:
                    ClientModeImpl.this.processRssiThreshold((byte) message.arg1, message.what, this.mRssiEventHandler);
                    break;
                case ClientModeImpl.CMD_STOP_RSSI_MONITORING_OFFLOAD /*131235*/:
                    int unused5 = ClientModeImpl.this.stopRssiMonitoringOffload();
                    break;
                case ClientModeImpl.CMD_IPV4_PROVISIONING_SUCCESS /*131272*/:
                    ClientModeImpl.this.handleIPv4Success((DhcpResults) message.obj);
                    ClientModeImpl clientModeImpl11 = ClientModeImpl.this;
                    clientModeImpl11.sendNetworkStateChangeBroadcast(clientModeImpl11.mLastBssid);
                    break;
                case ClientModeImpl.CMD_IPV4_PROVISIONING_FAILURE /*131273*/:
                    ClientModeImpl.this.handleIPv4Failure();
                    ClientModeImpl.this.mWifiInjector.getWifiLastResortWatchdog().noteConnectionFailureAndTriggerIfNeeded(ClientModeImpl.this.getTargetSsid(), ClientModeImpl.this.mTargetRoamBSSID, 3);
                    break;
                case ClientModeImpl.CMD_ADD_KEEPALIVE_PACKET_FILTER_TO_APF /*131281*/:
                    if (ClientModeImpl.this.mIpClient != null) {
                        int slot2 = message.arg1;
                        if (!(message.obj instanceof NattKeepalivePacketData)) {
                            if (message.obj instanceof TcpKeepalivePacketData) {
                                ClientModeImpl.this.mIpClient.addKeepalivePacketFilter(slot2, (TcpKeepalivePacketData) message.obj);
                                break;
                            }
                        } else {
                            ClientModeImpl.this.mIpClient.addKeepalivePacketFilter(slot2, (NattKeepalivePacketData) message.obj);
                            break;
                        }
                    }
                    break;
                case ClientModeImpl.CMD_REMOVE_KEEPALIVE_PACKET_FILTER_FROM_APF /*131282*/:
                    if (ClientModeImpl.this.mIpClient != null) {
                        ClientModeImpl.this.mIpClient.removeKeepalivePacketFilter(message.arg1);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_PRE_DHCP_ACTION /*131327*/:
                    ClientModeImpl.this.handlePreDhcpSetup();
                    break;
                case ClientModeImpl.CMD_PRE_DHCP_ACTION_COMPLETE /*131328*/:
                    if (ClientModeImpl.this.mIpClient != null) {
                        ClientModeImpl.this.mIpClient.completedPreDhcpAction();
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_POST_DHCP_ACTION /*131329*/:
                    ClientModeImpl.this.handlePostDhcpSetup();
                    break;
                case WifiP2pServiceImpl.DISCONNECT_WIFI_REQUEST /*143372*/:
                    if (message.arg1 == 1) {
                        ClientModeImpl.this.mWifiMetrics.logStaEvent(15, 5);
                        ClientModeImpl.this.mWifiNative.disconnect(ClientModeImpl.this.mInterfaceName);
                        boolean unused6 = ClientModeImpl.this.mTemporarilyDisconnectWifi = true;
                        ClientModeImpl clientModeImpl12 = ClientModeImpl.this;
                        clientModeImpl12.transitionTo(clientModeImpl12.mDisconnectingState);
                        break;
                    }
                    break;
                case WifiMonitor.NETWORK_CONNECTION_EVENT /*147459*/:
                case WifiMonitor.FILS_NETWORK_CONNECTION_EVENT /*147519*/:
                    ClientModeImpl.this.mWifiInfo.setBSSID((String) message.obj);
                    int unused7 = ClientModeImpl.this.mLastNetworkId = message.arg1;
                    ClientModeImpl.this.mWifiInfo.setNetworkId(ClientModeImpl.this.mLastNetworkId);
                    ClientModeImpl.this.mWifiInfo.setMacAddress(ClientModeImpl.this.mWifiNative.getMacAddress(ClientModeImpl.this.mInterfaceName));
                    if (!TextUtils.equals(ClientModeImpl.this.mLastBssid, (String) message.obj)) {
                        ClientModeImpl.this.updateWifiGenerationInfo();
                        String unused8 = ClientModeImpl.this.mLastBssid = (String) message.obj;
                        ClientModeImpl clientModeImpl13 = ClientModeImpl.this;
                        clientModeImpl13.sendNetworkStateChangeBroadcast(clientModeImpl13.mLastBssid);
                    }
                    boolean unused9 = ClientModeImpl.this.mIpReachabilityMonitorActive = true;
                    ClientModeImpl clientModeImpl14 = ClientModeImpl.this;
                    clientModeImpl14.sendMessageDelayed(clientModeImpl14.obtainMessage(ClientModeImpl.CMD_IP_REACHABILITY_SESSION_END, 0, 0), RttServiceImpl.HAL_AWARE_RANGING_TIMEOUT_MS);
                    break;
                case 151553:
                    if (ClientModeImpl.this.mWifiInfo.getNetworkId() != message.arg1) {
                        handleStatus = false;
                        break;
                    } else {
                        ClientModeImpl.this.replyToMessage(message, 151555);
                        break;
                    }
                case 151572:
                    RssiPacketCountInfo info = new RssiPacketCountInfo();
                    ClientModeImpl.this.fetchRssiLinkSpeedAndFrequencyNative();
                    info.rssi = ClientModeImpl.this.mWifiInfo.getRssi();
                    WifiNative.TxPacketCounters counters = ClientModeImpl.this.mWifiNative.getTxPacketCounters(ClientModeImpl.this.mInterfaceName);
                    if (counters == null) {
                        ClientModeImpl.this.replyToMessage(message, 151574, 0);
                        break;
                    } else {
                        info.txgood = counters.txSucceeded;
                        info.txbad = counters.txFailed;
                        ClientModeImpl.this.replyToMessage(message, 151573, (Object) info);
                        break;
                    }
                default:
                    handleStatus = false;
                    break;
            }
            if (handleStatus) {
                ClientModeImpl.this.logStateAndMessage(message, this);
            }
            return handleStatus;
        }

        private WifiLinkLayerStats updateLinkLayerStatsRssiAndScoreReportInternal() {
            WifiLinkLayerStats stats = ClientModeImpl.this.getWifiLinkLayerStats();
            ClientModeImpl.this.fetchRssiLinkSpeedAndFrequencyNative();
            ClientModeImpl.this.mWifiScoreReport.calculateAndReportScore(ClientModeImpl.this.mWifiInfo, ClientModeImpl.this.mNetworkAgent, ClientModeImpl.this.mWifiMetrics);
            return stats;
        }
    }

    public void updateLinkLayerStatsRssiAndScoreReport() {
        sendMessage(CMD_ONESHOT_RSSI_POLL);
    }

    /* access modifiers changed from: private */
    public static int convertToUsabilityStatsTriggerType(int unusableEventTriggerType) {
        if (unusableEventTriggerType == 1) {
            return 1;
        }
        if (unusableEventTriggerType == 2) {
            return 2;
        }
        if (unusableEventTriggerType == 3) {
            return 3;
        }
        if (unusableEventTriggerType == 4) {
            return 4;
        }
        if (unusableEventTriggerType == 5) {
            return 5;
        }
        Log.e(TAG, "Unknown WifiIsUnusableEvent: " + unusableEventTriggerType);
        return 0;
    }

    class ObtainingIpState extends State {
        ProvisioningConfiguration mProv;

        ObtainingIpState() {
        }

        public void enter() {
            WifiConfiguration currentConfig = ClientModeImpl.this.getCurrentWifiConfiguration();
            boolean isUsingStaticIp = currentConfig.getIpAssignment() == IpConfiguration.IpAssignment.STATIC;
            if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                String key = currentConfig.configKey();
                ClientModeImpl clientModeImpl = ClientModeImpl.this;
                clientModeImpl.log("enter ObtainingIpState netId=" + Integer.toString(ClientModeImpl.this.mLastNetworkId) + " " + key + "  roam=" + ClientModeImpl.this.mIsAutoRoaming + " static=" + isUsingStaticIp);
            }
            boolean unused = ClientModeImpl.this.setNetworkDetailedState(NetworkInfo.DetailedState.OBTAINING_IPADDR);
            ClientModeImpl.this.clearTargetBssid("ObtainingIpAddress");
            ClientModeImpl.this.mWifiNative.setPowerSave(ClientModeImpl.this.mInterfaceName, false);
            if (!ClientModeImpl.this.mIsFilsConnection) {
                ClientModeImpl.this.stopIpClient();
            }
            if (ClientModeImpl.this.mIpClient != null) {
                ClientModeImpl.this.mIpClient.setHttpProxy(currentConfig.getHttpProxy());
                if (!TextUtils.isEmpty(ClientModeImpl.this.mTcpBufferSizes)) {
                    ClientModeImpl.this.mIpClient.setTcpBufferSizes(ClientModeImpl.this.mTcpBufferSizes);
                }
            }
            if (!ClientModeImpl.this.mIsFilsConnection || !ClientModeImpl.this.mIsIpClientStarted) {
                if (!isUsingStaticIp) {
                    this.mProv = new ProvisioningConfiguration.Builder().withPreDhcpAction().withApfCapabilities(ClientModeImpl.this.mWifiNative.getApfCapabilities(ClientModeImpl.this.mInterfaceName)).withNetwork(ClientModeImpl.this.getCurrentNetwork()).withDisplayName(currentConfig.SSID).withRandomMacAddress().build();
                } else {
                    this.mProv = new ProvisioningConfiguration.Builder().withStaticConfiguration(currentConfig.getStaticIpConfiguration()).withApfCapabilities(ClientModeImpl.this.mWifiNative.getApfCapabilities(ClientModeImpl.this.mInterfaceName)).withNetwork(ClientModeImpl.this.getCurrentNetwork()).withDisplayName(currentConfig.SSID).build();
                }
                if (ClientModeImpl.this.mIpClient != null) {
                    ClientModeImpl.this.mIpClient.startProvisioning(this.mProv);
                    boolean unused2 = ClientModeImpl.this.mIsIpClientStarted = true;
                } else {
                    ClientModeImpl.this.sendMessageDelayed(ClientModeImpl.CMD_WAIT_IPCLIENT_OBTAINED, 4000);
                }
            } else {
                ClientModeImpl.this.setPowerSaveForFilsDhcp();
            }
            ClientModeImpl.this.getWifiLinkLayerStats();
            boolean unused3 = ClientModeImpl.this.mIsFilsConnection = false;
        }

        public boolean processMessage(Message message) {
            boolean handleStatus = true;
            switch (message.what) {
                case ClientModeImpl.CMD_SET_HIGH_PERF_MODE /*131149*/:
                    int unused = ClientModeImpl.this.mMessageHandlingStatus = -4;
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case ClientModeImpl.CMD_START_CONNECT /*131215*/:
                case ClientModeImpl.CMD_START_ROAM /*131217*/:
                    int unused2 = ClientModeImpl.this.mMessageHandlingStatus = -5;
                    break;
                case ClientModeImpl.CMD_WAIT_IPCLIENT_OBTAINED /*131384*/:
                    Log.d(ClientModeImpl.TAG, "CMD_WAIT_IPCLIENT_OBTAINED ");
                    if (ClientModeImpl.this.mIpClient == null) {
                        ClientModeImpl.this.sendMessageDelayed(ClientModeImpl.CMD_WAIT_IPCLIENT_OBTAINED, 4000);
                        break;
                    } else {
                        if (ClientModeImpl.this.hasMessages(ClientModeImpl.CMD_WAIT_IPCLIENT_OBTAINED)) {
                            ClientModeImpl.this.removeMessages(ClientModeImpl.CMD_WAIT_IPCLIENT_OBTAINED);
                        }
                        ClientModeImpl.this.mIpClient.startProvisioning(this.mProv);
                        boolean unused3 = ClientModeImpl.this.mIsIpClientStarted = true;
                        break;
                    }
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /*147460*/:
                    ClientModeImpl.this.reportConnectionAttemptEnd(6, 1, 0);
                    handleStatus = false;
                    break;
                case 151553:
                    int unused4 = ClientModeImpl.this.mMessageHandlingStatus = -4;
                    ClientModeImpl.this.mWifiNative.disconnect(ClientModeImpl.this.mInterfaceName);
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case 151559:
                    int unused5 = ClientModeImpl.this.mMessageHandlingStatus = -4;
                    ClientModeImpl.this.deferMessage(message);
                    break;
                default:
                    handleStatus = false;
                    break;
            }
            if (handleStatus) {
                ClientModeImpl.this.logStateAndMessage(message, this);
            }
            return handleStatus;
        }
    }

    @VisibleForTesting
    public boolean shouldEvaluateWhetherToSendExplicitlySelected(WifiConfiguration currentConfig) {
        if (currentConfig == null) {
            Log.wtf(TAG, "Current WifiConfiguration is null, but IP provisioning just succeeded");
            return false;
        }
        long currentTimeMillis = this.mClock.getElapsedSinceBootMillis();
        if (this.mWifiConfigManager.getLastSelectedNetwork() != currentConfig.networkId || currentTimeMillis - this.mWifiConfigManager.getLastSelectedTimeStamp() >= 30000) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void sendConnectedState() {
        WifiConfiguration config = getCurrentWifiConfiguration();
        boolean explicitlySelected = false;
        if (shouldEvaluateWhetherToSendExplicitlySelected(config)) {
            explicitlySelected = this.mWifiPermissionsUtil.checkNetworkSettingsPermission(config.lastConnectUid);
            if (this.mVerboseLoggingEnabled) {
                log("Network selected by UID " + config.lastConnectUid + " explicitlySelected=" + explicitlySelected);
            }
            if (explicitlySelected) {
                if (this.mVerboseLoggingEnabled) {
                    log("explictlySelected acceptUnvalidated=" + config.noInternetAccessExpected);
                }
                WifiNetworkAgent wifiNetworkAgent = this.mNetworkAgent;
                if (wifiNetworkAgent != null) {
                    wifiNetworkAgent.explicitlySelected(config.noInternetAccessExpected);
                }
            }
        }
        if (this.mVerboseLoggingEnabled) {
            log("explictlySelected=" + explicitlySelected + " acceptUnvalidated=" + config.noInternetAccessExpected);
        }
        WifiNetworkAgent wifiNetworkAgent2 = this.mNetworkAgent;
        if (wifiNetworkAgent2 != null) {
            wifiNetworkAgent2.explicitlySelected(explicitlySelected, config.noInternetAccessExpected);
        }
        setNetworkDetailedState(NetworkInfo.DetailedState.CONNECTED);
        sendNetworkStateChangeBroadcast(this.mLastBssid);
    }

    class RoamingState extends State {
        boolean mAssociated;

        RoamingState() {
        }

        public void enter() {
            if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                ClientModeImpl.this.log("RoamingState Enter mScreenOn=" + ClientModeImpl.this.mScreenOn);
            }
            ClientModeImpl.this.mRoamWatchdogCount++;
            ClientModeImpl.this.logd("Start Roam Watchdog " + ClientModeImpl.this.mRoamWatchdogCount);
            ClientModeImpl clientModeImpl = ClientModeImpl.this;
            clientModeImpl.sendMessageDelayed(clientModeImpl.obtainMessage(ClientModeImpl.CMD_ROAM_WATCHDOG_TIMER, clientModeImpl.mRoamWatchdogCount, 0), 15000);
            this.mAssociated = false;
        }

        public boolean processMessage(Message message) {
            boolean handleStatus = true;
            switch (message.what) {
                case ClientModeImpl.CMD_ROAM_WATCHDOG_TIMER /*131166*/:
                    if (ClientModeImpl.this.mRoamWatchdogCount == message.arg1) {
                        if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                            ClientModeImpl.this.log("roaming watchdog! -> disconnect");
                        }
                        ClientModeImpl.this.mWifiMetrics.endConnectionEvent(9, 1, 0);
                        ClientModeImpl.access$12308(ClientModeImpl.this);
                        ClientModeImpl.this.handleNetworkDisconnect();
                        ClientModeImpl.this.mWifiMetrics.logStaEvent(15, 4);
                        ClientModeImpl.this.mWifiNative.disconnect(ClientModeImpl.this.mInterfaceName);
                        ClientModeImpl clientModeImpl = ClientModeImpl.this;
                        clientModeImpl.transitionTo(clientModeImpl.mDisconnectedState);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_IP_CONFIGURATION_LOST /*131211*/:
                    if (ClientModeImpl.this.getCurrentWifiConfiguration() != null) {
                        ClientModeImpl.this.mWifiDiagnostics.captureBugReportData(3);
                    }
                    handleStatus = false;
                    break;
                case ClientModeImpl.CMD_UNWANTED_NETWORK /*131216*/:
                    if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                        ClientModeImpl.this.log("Roaming and CS doesn't want the network -> ignore");
                        break;
                    }
                    break;
                case WifiMonitor.NETWORK_CONNECTION_EVENT /*147459*/:
                    if (!this.mAssociated) {
                        int unused = ClientModeImpl.this.mMessageHandlingStatus = -5;
                        break;
                    } else {
                        if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                            ClientModeImpl.this.log("roaming and Network connection established");
                        }
                        int unused2 = ClientModeImpl.this.mLastNetworkId = message.arg1;
                        String unused3 = ClientModeImpl.this.mLastBssid = (String) message.obj;
                        ClientModeImpl.this.mWifiInfo.setBSSID(ClientModeImpl.this.mLastBssid);
                        ClientModeImpl.this.mWifiInfo.setNetworkId(ClientModeImpl.this.mLastNetworkId);
                        ClientModeImpl.this.updateWifiGenerationInfo();
                        ClientModeImpl.this.mWifiConnectivityManager.trackBssid(ClientModeImpl.this.mLastBssid, true, message.arg2);
                        ClientModeImpl clientModeImpl2 = ClientModeImpl.this;
                        clientModeImpl2.sendNetworkStateChangeBroadcast(clientModeImpl2.mLastBssid);
                        ClientModeImpl.this.reportConnectionAttemptEnd(1, 1, 0);
                        ClientModeImpl.this.clearTargetBssid("RoamingCompleted");
                        boolean unused4 = ClientModeImpl.this.mIpReachabilityMonitorActive = true;
                        ClientModeImpl clientModeImpl3 = ClientModeImpl.this;
                        clientModeImpl3.transitionTo(clientModeImpl3.mConnectedState);
                        break;
                    }
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /*147460*/:
                    String bssid = (String) message.obj;
                    String target = Prefix.EMPTY;
                    if (ClientModeImpl.this.mTargetRoamBSSID != null) {
                        target = ClientModeImpl.this.mTargetRoamBSSID;
                    }
                    ClientModeImpl clientModeImpl4 = ClientModeImpl.this;
                    clientModeImpl4.log("NETWORK_DISCONNECTION_EVENT in roaming state BSSID=" + bssid + " target=" + target);
                    if (bssid != null && bssid.equals(ClientModeImpl.this.mTargetRoamBSSID)) {
                        ClientModeImpl.this.handleNetworkDisconnect();
                        ClientModeImpl clientModeImpl5 = ClientModeImpl.this;
                        clientModeImpl5.transitionTo(clientModeImpl5.mDisconnectedState);
                        break;
                    }
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /*147462*/:
                    StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
                    if (stateChangeResult.state == SupplicantState.DISCONNECTED || stateChangeResult.state == SupplicantState.INACTIVE || stateChangeResult.state == SupplicantState.INTERFACE_DISABLED) {
                        if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                            ClientModeImpl clientModeImpl6 = ClientModeImpl.this;
                            clientModeImpl6.log("STATE_CHANGE_EVENT in roaming state " + stateChangeResult.toString());
                        }
                        if (stateChangeResult.BSSID != null && stateChangeResult.BSSID.equals(ClientModeImpl.this.mTargetRoamBSSID)) {
                            ClientModeImpl.this.handleNetworkDisconnect();
                            ClientModeImpl clientModeImpl7 = ClientModeImpl.this;
                            clientModeImpl7.transitionTo(clientModeImpl7.mDisconnectedState);
                        }
                    }
                    if (stateChangeResult.state == SupplicantState.ASSOCIATED) {
                        this.mAssociated = true;
                        if (stateChangeResult.BSSID != null) {
                            String unused5 = ClientModeImpl.this.mTargetRoamBSSID = stateChangeResult.BSSID;
                        }
                        if (stateChangeResult.wifiSsid != null && !stateChangeResult.wifiSsid.toString().isEmpty()) {
                            ClientModeImpl.this.mWifiInfo.setSSID(stateChangeResult.wifiSsid);
                            break;
                        }
                    }
                    break;
                default:
                    handleStatus = false;
                    break;
            }
            if (handleStatus) {
                ClientModeImpl.this.logStateAndMessage(message, this);
            }
            return handleStatus;
        }

        public void exit() {
            ClientModeImpl.this.logd("ClientModeImpl: Leaving Roaming state");
        }
    }

    class ConnectedState extends State {
        ConnectedState() {
        }

        public void enter() {
            if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                ClientModeImpl clientModeImpl = ClientModeImpl.this;
                clientModeImpl.log("Enter ConnectedState  mScreenOn=" + ClientModeImpl.this.mScreenOn);
            }
            ClientModeImpl.this.reportConnectionAttemptEnd(1, 1, 0);
            ClientModeImpl.this.mWifiConnectivityManager.handleConnectionStateChanged(1);
            if (ClientModeImpl.this.mIpReachabilityMonitorActive) {
                ClientModeImpl clientModeImpl2 = ClientModeImpl.this;
                clientModeImpl2.sendMessageDelayed(clientModeImpl2.obtainMessage(ClientModeImpl.CMD_IP_REACHABILITY_SESSION_END, 0, 0), RttServiceImpl.HAL_AWARE_RANGING_TIMEOUT_MS);
            }
            ClientModeImpl.this.registerConnected();
            long unused = ClientModeImpl.this.mLastConnectAttemptTimestamp = 0;
            WifiConfiguration unused2 = ClientModeImpl.this.mTargetWifiConfiguration = null;
            ClientModeImpl.this.mWifiScoreReport.reset();
            int unused3 = ClientModeImpl.this.mLastSignalLevel = -1;
            boolean unused4 = ClientModeImpl.this.mIsAutoRoaming = false;
            long unused5 = ClientModeImpl.this.mLastDriverRoamAttempt = 0;
            int unused6 = ClientModeImpl.this.mTargetNetworkId = -1;
            ClientModeImpl.this.mWifiInjector.getWifiLastResortWatchdog().connectedStateTransition(true);
            ClientModeImpl.this.mWifiStateTracker.updateState(3);
            ClientModeImpl.this.mWifiInjector.getWifiLockManager().updateWifiClientConnected(true);
        }

        public boolean processMessage(Message message) {
            String str;
            String str2;
            Message message2 = message;
            boolean handleStatus = true;
            boolean accept = false;
            switch (message2.what) {
                case ClientModeImpl.CMD_UNWANTED_NETWORK /*131216*/:
                    if (message2.arg1 != 0) {
                        if (message2.arg1 == 2 || message2.arg1 == 1) {
                            if (message2.arg1 == 2) {
                                str = "NETWORK_STATUS_UNWANTED_DISABLE_AUTOJOIN";
                            } else {
                                str = "NETWORK_STATUS_UNWANTED_VALIDATION_FAILED";
                            }
                            Log.d(ClientModeImpl.TAG, str);
                            WifiConfiguration config = ClientModeImpl.this.getCurrentWifiConfiguration();
                            if (config != null) {
                                if (message2.arg1 != 2) {
                                    ClientModeImpl.this.removeMessages(ClientModeImpl.CMD_DIAGS_CONNECT_TIMEOUT);
                                    ClientModeImpl.this.mWifiDiagnostics.reportConnectionEvent((byte) 2);
                                    ClientModeImpl.this.mWifiConfigManager.incrementNetworkNoInternetAccessReports(config.networkId);
                                    if (ClientModeImpl.this.mWifiConfigManager.getLastSelectedNetwork() != config.networkId && !config.noInternetAccessExpected) {
                                        Log.i(ClientModeImpl.TAG, "Temporarily disabling network because ofno-internet access");
                                        ClientModeImpl.this.mWifiConfigManager.updateNetworkSelectionStatus(config.networkId, 6);
                                        break;
                                    }
                                } else {
                                    ClientModeImpl.this.mWifiConfigManager.setNetworkValidatedInternetAccess(config.networkId, false);
                                    ClientModeImpl.this.mWifiConfigManager.updateNetworkSelectionStatus(config.networkId, 10);
                                    break;
                                }
                            }
                        }
                    } else {
                        ClientModeImpl.this.mWifiMetrics.logStaEvent(15, 3);
                        ClientModeImpl.this.mWifiNative.disconnect(ClientModeImpl.this.mInterfaceName);
                        ClientModeImpl clientModeImpl = ClientModeImpl.this;
                        clientModeImpl.transitionTo(clientModeImpl.mDisconnectingState);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_START_ROAM /*131217*/:
                    long unused = ClientModeImpl.this.mLastDriverRoamAttempt = 0;
                    int netId = message2.arg1;
                    ScanResult candidate = (ScanResult) message2.obj;
                    String bssid = "any";
                    if (candidate != null) {
                        bssid = candidate.BSSID;
                    }
                    WifiConfiguration config2 = ClientModeImpl.this.mWifiConfigManager.getConfiguredNetworkWithoutMasking(netId);
                    if (config2 != null) {
                        ClientModeImpl.this.mWifiScoreCard.noteConnectionAttempt(ClientModeImpl.this.mWifiInfo);
                        boolean unused2 = ClientModeImpl.this.setTargetBssid(config2, bssid);
                        int unused3 = ClientModeImpl.this.mTargetNetworkId = netId;
                        ClientModeImpl.this.logd("CMD_START_ROAM sup state " + ClientModeImpl.this.mSupplicantStateTracker.getSupplicantStateName() + " my state " + ClientModeImpl.this.getCurrentState().getName() + " nid=" + Integer.toString(netId) + " config " + config2.configKey() + " targetRoamBSSID " + ClientModeImpl.this.mTargetRoamBSSID);
                        ClientModeImpl clientModeImpl2 = ClientModeImpl.this;
                        clientModeImpl2.reportConnectionAttemptStart(config2, clientModeImpl2.mTargetRoamBSSID, 3);
                        if (!ClientModeImpl.this.mWifiNative.roamToNetwork(ClientModeImpl.this.mInterfaceName, config2)) {
                            ClientModeImpl.this.loge("CMD_START_ROAM Failed to start roaming to network " + config2);
                            ClientModeImpl.this.reportConnectionAttemptEnd(5, 1, 0);
                            ClientModeImpl.this.replyToMessage(message2, 151554, 0);
                            int unused4 = ClientModeImpl.this.mMessageHandlingStatus = -2;
                            break;
                        } else {
                            ClientModeImpl clientModeImpl3 = ClientModeImpl.this;
                            long unused5 = clientModeImpl3.mLastConnectAttemptTimestamp = clientModeImpl3.mClock.getWallClockMillis();
                            WifiConfiguration unused6 = ClientModeImpl.this.mTargetWifiConfiguration = config2;
                            boolean unused7 = ClientModeImpl.this.mIsAutoRoaming = true;
                            ClientModeImpl.this.mWifiMetrics.logStaEvent(12, config2);
                            ClientModeImpl clientModeImpl4 = ClientModeImpl.this;
                            clientModeImpl4.transitionTo(clientModeImpl4.mRoamingState);
                            break;
                        }
                    } else {
                        ClientModeImpl.this.loge("CMD_START_ROAM and no config, bail out...");
                        break;
                    }
                case ClientModeImpl.CMD_ASSOCIATED_BSSID /*131219*/:
                    ClientModeImpl clientModeImpl5 = ClientModeImpl.this;
                    long unused8 = clientModeImpl5.mLastDriverRoamAttempt = clientModeImpl5.mClock.getWallClockMillis();
                    handleStatus = false;
                    break;
                case ClientModeImpl.CMD_NETWORK_STATUS /*131220*/:
                    if (message2.arg1 == 1) {
                        ClientModeImpl.this.removeMessages(ClientModeImpl.CMD_DIAGS_CONNECT_TIMEOUT);
                        ClientModeImpl.this.mWifiDiagnostics.reportConnectionEvent((byte) 1);
                        ClientModeImpl.this.mWifiScoreCard.noteValidationSuccess(ClientModeImpl.this.mWifiInfo);
                        WifiConfiguration config3 = ClientModeImpl.this.getCurrentWifiConfiguration();
                        if (config3 != null) {
                            ClientModeImpl.this.mWifiConfigManager.updateNetworkSelectionStatus(config3.networkId, 0);
                            ClientModeImpl.this.mWifiConfigManager.setNetworkValidatedInternetAccess(config3.networkId, true);
                            break;
                        }
                    }
                    break;
                case ClientModeImpl.CMD_ACCEPT_UNVALIDATED /*131225*/:
                    if (message2.arg1 != 0) {
                        accept = true;
                    }
                    ClientModeImpl.this.mWifiConfigManager.setNetworkNoInternetAccessExpected(ClientModeImpl.this.mLastNetworkId, accept);
                    break;
                case ClientModeImpl.CMD_START_IP_PACKET_OFFLOAD /*131232*/:
                    int slot = message2.arg1;
                    int result = ClientModeImpl.this.startWifiIPPacketOffload(slot, (KeepalivePacketData) message2.obj, message2.arg2);
                    if (ClientModeImpl.this.mNetworkAgent != null) {
                        ClientModeImpl.this.mNetworkAgent.onSocketKeepaliveEvent(slot, result);
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_IP_REACHABILITY_SESSION_END /*131383*/:
                    boolean unused9 = ClientModeImpl.this.mIpReachabilityMonitorActive = false;
                    break;
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /*147460*/:
                    ClientModeImpl.this.reportConnectionAttemptEnd(6, 1, 0);
                    if (ClientModeImpl.this.mLastDriverRoamAttempt != 0) {
                        long lastRoam = ClientModeImpl.this.mClock.getWallClockMillis() - ClientModeImpl.this.mLastDriverRoamAttempt;
                        long unused10 = ClientModeImpl.this.mLastDriverRoamAttempt = 0;
                    }
                    if (ClientModeImpl.unexpectedDisconnectedReason(message2.arg2)) {
                        ClientModeImpl.this.mWifiDiagnostics.captureBugReportData(5);
                    }
                    WifiConfiguration config4 = ClientModeImpl.this.getCurrentWifiConfiguration();
                    if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                        ClientModeImpl clientModeImpl6 = ClientModeImpl.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append("NETWORK_DISCONNECTION_EVENT in connected state BSSID=");
                        sb.append(ClientModeImpl.this.mWifiInfo.getBSSID());
                        sb.append(" RSSI=");
                        sb.append(ClientModeImpl.this.mWifiInfo.getRssi());
                        sb.append(" freq=");
                        sb.append(ClientModeImpl.this.mWifiInfo.getFrequency());
                        sb.append(" reason=");
                        sb.append(message2.arg2);
                        sb.append(" Network Selection Status=");
                        if (config4 == null) {
                            str2 = "Unavailable";
                        } else {
                            str2 = config4.getNetworkSelectionStatus().getNetworkStatusString();
                        }
                        sb.append(str2);
                        clientModeImpl6.log(sb.toString());
                        break;
                    }
                    break;
                default:
                    handleStatus = false;
                    break;
            }
            if (handleStatus) {
                ClientModeImpl.this.logStateAndMessage(message2, this);
            }
            return handleStatus;
        }

        public void exit() {
            ClientModeImpl.this.logd("ClientModeImpl: Leaving Connected state");
            ClientModeImpl.this.mWifiConnectivityManager.handleConnectionStateChanged(3);
            long unused = ClientModeImpl.this.mLastDriverRoamAttempt = 0;
            ClientModeImpl.this.mWifiInjector.getWifiLastResortWatchdog().connectedStateTransition(false);
        }
    }

    class DisconnectingState extends State {
        DisconnectingState() {
        }

        public void enter() {
            if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                ClientModeImpl.this.logd(" Enter DisconnectingState State screenOn=" + ClientModeImpl.this.mScreenOn);
            }
            ClientModeImpl.this.mDisconnectingWatchdogCount++;
            ClientModeImpl.this.logd("Start Disconnecting Watchdog " + ClientModeImpl.this.mDisconnectingWatchdogCount);
            ClientModeImpl clientModeImpl = ClientModeImpl.this;
            clientModeImpl.sendMessageDelayed(clientModeImpl.obtainMessage(ClientModeImpl.CMD_DISCONNECTING_WATCHDOG_TIMER, clientModeImpl.mDisconnectingWatchdogCount, 0), RttServiceImpl.HAL_RANGING_TIMEOUT_MS);
        }

        public boolean processMessage(Message message) {
            boolean handleStatus = true;
            int i = message.what;
            if (i != ClientModeImpl.CMD_DISCONNECT) {
                if (i != ClientModeImpl.CMD_DISCONNECTING_WATCHDOG_TIMER) {
                    if (i != 147462) {
                        handleStatus = false;
                    } else {
                        ClientModeImpl.this.deferMessage(message);
                        ClientModeImpl.this.handleNetworkDisconnect();
                        ClientModeImpl clientModeImpl = ClientModeImpl.this;
                        clientModeImpl.transitionTo(clientModeImpl.mDisconnectedState);
                    }
                } else if (ClientModeImpl.this.mDisconnectingWatchdogCount == message.arg1) {
                    if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                        ClientModeImpl.this.log("disconnecting watchdog! -> disconnect");
                    }
                    ClientModeImpl.this.handleNetworkDisconnect();
                    ClientModeImpl clientModeImpl2 = ClientModeImpl.this;
                    clientModeImpl2.transitionTo(clientModeImpl2.mDisconnectedState);
                }
            } else if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                ClientModeImpl.this.log("Ignore CMD_DISCONNECT when already disconnecting.");
            }
            if (handleStatus) {
                ClientModeImpl.this.logStateAndMessage(message, this);
            }
            return handleStatus;
        }
    }

    class DisconnectedState extends State {
        DisconnectedState() {
        }

        public void enter() {
            Log.i(ClientModeImpl.TAG, "disconnectedstate enter");
            if (ClientModeImpl.this.mTemporarilyDisconnectWifi) {
                boolean unused = ClientModeImpl.this.p2pSendMessage(WifiP2pServiceImpl.DISCONNECT_WIFI_RESPONSE);
                return;
            }
            if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                ClientModeImpl clientModeImpl = ClientModeImpl.this;
                clientModeImpl.logd(" Enter DisconnectedState screenOn=" + ClientModeImpl.this.mScreenOn);
            }
            boolean unused2 = ClientModeImpl.this.mIsAutoRoaming = false;
            boolean unused3 = ClientModeImpl.this.mIpReachabilityMonitorActive = false;
            ClientModeImpl.this.removeMessages(ClientModeImpl.CMD_IP_REACHABILITY_SESSION_END);
            ClientModeImpl.this.mWifiConnectivityManager.handleConnectionStateChanged(2);
        }

        public boolean processMessage(Message message) {
            boolean handleStatus = true;
            boolean z = false;
            switch (message.what) {
                case ClientModeImpl.CMD_DISCONNECT /*131145*/:
                    ClientModeImpl.this.mWifiMetrics.logStaEvent(15, 2);
                    ClientModeImpl.this.mWifiNative.disconnect(ClientModeImpl.this.mInterfaceName);
                    break;
                case ClientModeImpl.CMD_RECONNECT /*131146*/:
                case ClientModeImpl.CMD_REASSOCIATE /*131147*/:
                    if (!ClientModeImpl.this.mTemporarilyDisconnectWifi) {
                        handleStatus = false;
                        break;
                    }
                    break;
                case ClientModeImpl.CMD_SCREEN_STATE_CHANGED /*131167*/:
                    ClientModeImpl clientModeImpl = ClientModeImpl.this;
                    if (message.arg1 != 0) {
                        z = true;
                    }
                    clientModeImpl.handleScreenStateChanged(z);
                    break;
                case WifiP2pServiceImpl.P2P_CONNECTION_CHANGED /*143371*/:
                    ClientModeImpl.this.mP2pConnected.set(((NetworkInfo) message.obj).isConnected());
                    break;
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /*147460*/:
                    if (message.arg2 == 15) {
                        ClientModeImpl.this.mWifiInjector.getWifiLastResortWatchdog().noteConnectionFailureAndTriggerIfNeeded(ClientModeImpl.this.getTargetSsid(), message.obj == null ? ClientModeImpl.this.mTargetRoamBSSID : (String) message.obj, 2);
                        break;
                    }
                    break;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /*147462*/:
                    StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
                    if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                        ClientModeImpl.this.logd("SUPPLICANT_STATE_CHANGE_EVENT state=" + stateChangeResult.state + " -> state= " + WifiInfo.getDetailedStateOf(stateChangeResult.state));
                    }
                    if (SupplicantState.isConnecting(stateChangeResult.state)) {
                        WifiConfiguration config = ClientModeImpl.this.mWifiConfigManager.getConfiguredNetwork(stateChangeResult.networkId);
                        ClientModeImpl.this.mWifiInfo.setFQDN((String) null);
                        ClientModeImpl.this.mWifiInfo.setOsuAp(false);
                        ClientModeImpl.this.mWifiInfo.setProviderFriendlyName((String) null);
                        if (config != null && (config.isPasspoint() || config.osu)) {
                            if (config.isPasspoint()) {
                                ClientModeImpl.this.mWifiInfo.setFQDN(config.FQDN);
                            } else {
                                ClientModeImpl.this.mWifiInfo.setOsuAp(true);
                            }
                            ClientModeImpl.this.mWifiInfo.setProviderFriendlyName(config.providerFriendlyName);
                        }
                    }
                    boolean unused = ClientModeImpl.this.setNetworkDetailedState(WifiInfo.getDetailedStateOf(stateChangeResult.state));
                    handleStatus = false;
                    break;
                default:
                    handleStatus = false;
                    break;
            }
            if (handleStatus) {
                ClientModeImpl.this.logStateAndMessage(message, this);
            }
            return handleStatus;
        }

        public void exit() {
            ClientModeImpl.this.mWifiConnectivityManager.handleConnectionStateChanged(3);
        }
    }

    class FilsState extends State {
        FilsState() {
        }

        public void enter() {
            if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                Log.d(ClientModeImpl.TAG, "Filsstate enter");
            }
            ProvisioningConfiguration prov = new ProvisioningConfiguration.Builder().withPreDhcpAction().withApfCapabilities(ClientModeImpl.this.mWifiNative.getApfCapabilities(ClientModeImpl.this.mInterfaceName)).build();
            prov.mRapidCommit = true;
            prov.mDiscoverSent = true;
            ClientModeImpl.this.mIpClient.startProvisioning(prov);
            boolean unused = ClientModeImpl.this.mIsIpClientStarted = true;
        }

        public boolean processMessage(Message message) {
            ClientModeImpl.this.logStateAndMessage(message, this);
            switch (message.what) {
                case ClientModeImpl.CMD_IP_CONFIGURATION_SUCCESSFUL /*131210*/:
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case ClientModeImpl.CMD_IPV4_PROVISIONING_SUCCESS /*131272*/:
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case ClientModeImpl.CMD_IPV4_PROVISIONING_FAILURE /*131273*/:
                    ClientModeImpl.this.stopIpClient();
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case ClientModeImpl.CMD_PRE_DHCP_ACTION /*131327*/:
                    ClientModeImpl.this.handlePreFilsDhcpSetup();
                    break;
                case ClientModeImpl.CMD_PRE_DHCP_ACTION_COMPLETE /*131328*/:
                    ClientModeImpl.this.mIpClient.completedPreDhcpAction();
                    ClientModeImpl.this.buildDiscoverWithRapidCommitPacket();
                    ClientModeImpl clientModeImpl = ClientModeImpl.this;
                    clientModeImpl.reportConnectionAttemptStart(clientModeImpl.mFilsConfig, ClientModeImpl.this.mTargetRoamBSSID, 5);
                    if (!ClientModeImpl.this.mWifiNative.connectToNetwork(ClientModeImpl.this.mInterfaceName, ClientModeImpl.this.mFilsConfig)) {
                        ClientModeImpl clientModeImpl2 = ClientModeImpl.this;
                        clientModeImpl2.loge("Failed to connect to FILS network " + ClientModeImpl.this.mFilsConfig);
                        ClientModeImpl.this.reportConnectionAttemptEnd(5, 1, 0);
                        ClientModeImpl.this.replyToMessage(message, 151554, 0);
                        break;
                    } else {
                        ClientModeImpl.this.mWifiMetrics.logStaEvent(11, ClientModeImpl.this.mFilsConfig);
                        ClientModeImpl clientModeImpl3 = ClientModeImpl.this;
                        long unused = clientModeImpl3.mLastConnectAttemptTimestamp = clientModeImpl3.mClock.getWallClockMillis();
                        ClientModeImpl clientModeImpl4 = ClientModeImpl.this;
                        WifiConfiguration unused2 = clientModeImpl4.mTargetWifiConfiguration = clientModeImpl4.mFilsConfig;
                        boolean unused3 = ClientModeImpl.this.mIsAutoRoaming = false;
                        break;
                    }
                case ClientModeImpl.CMD_POST_DHCP_ACTION /*131329*/:
                    ClientModeImpl.this.deferMessage(message);
                    break;
                case WifiMonitor.NETWORK_CONNECTION_EVENT /*147459*/:
                    break;
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /*147463*/:
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /*147499*/:
                    ClientModeImpl.this.stopIpClient();
                    return false;
                case WifiMonitor.FILS_NETWORK_CONNECTION_EVENT /*147519*/:
                    boolean unused4 = ClientModeImpl.this.mIsFilsConnection = true;
                    break;
                default:
                    return false;
            }
            if (ClientModeImpl.this.mVerboseLoggingEnabled) {
                ClientModeImpl clientModeImpl5 = ClientModeImpl.this;
                clientModeImpl5.log("Network connection established with FILS " + ClientModeImpl.this.mIsFilsConnection);
            }
            int unused5 = ClientModeImpl.this.mLastNetworkId = message.arg1;
            String unused6 = ClientModeImpl.this.mLastBssid = (String) message.obj;
            int reasonCode = message.arg2;
            WifiConfiguration config = ClientModeImpl.this.getCurrentWifiConfiguration();
            if (config != null) {
                ClientModeImpl.this.mWifiInfo.setBSSID(ClientModeImpl.this.mLastBssid);
                ClientModeImpl.this.mWifiInfo.setNetworkId(ClientModeImpl.this.mLastNetworkId);
                ClientModeImpl.this.updateWifiGenerationInfo();
                ClientModeImpl.this.mWifiConnectivityManager.trackBssid(ClientModeImpl.this.mLastBssid, true, reasonCode);
                if (config.enterpriseConfig != null && TelephonyUtil.isSimEapMethod(config.enterpriseConfig.getEapMethod())) {
                    String anonymousIdentity = ClientModeImpl.this.mWifiNative.getEapAnonymousIdentity(ClientModeImpl.this.mInterfaceName);
                    if (anonymousIdentity != null) {
                        config.enterpriseConfig.setAnonymousIdentity(anonymousIdentity);
                    } else {
                        Log.d(ClientModeImpl.TAG, "Failed to get updated anonymous identity from supplicant, reset it in WifiConfiguration.");
                        config.enterpriseConfig.setAnonymousIdentity((String) null);
                    }
                    ClientModeImpl.this.mWifiConfigManager.addOrUpdateNetwork(config, 1010);
                }
                ClientModeImpl clientModeImpl6 = ClientModeImpl.this;
                clientModeImpl6.sendNetworkStateChangeBroadcast(clientModeImpl6.mLastBssid);
                ClientModeImpl clientModeImpl7 = ClientModeImpl.this;
                clientModeImpl7.transitionTo(clientModeImpl7.mObtainingIpState);
            }
            return true;
        }

        public void exit() {
        }
    }

    /* access modifiers changed from: private */
    public void replyToMessage(Message msg, int what) {
        if (msg.replyTo != null) {
            this.mReplyChannel.replyToMessage(msg, obtainMessageWithWhatAndArg2(msg, what));
        }
    }

    /* access modifiers changed from: private */
    public void replyToMessage(Message msg, int what, int arg1) {
        if (msg.replyTo != null) {
            Message dstMsg = obtainMessageWithWhatAndArg2(msg, what);
            dstMsg.arg1 = arg1;
            this.mReplyChannel.replyToMessage(msg, dstMsg);
        }
    }

    /* access modifiers changed from: private */
    public void replyToMessage(Message msg, int what, Object obj) {
        if (msg.replyTo != null) {
            Message dstMsg = obtainMessageWithWhatAndArg2(msg, what);
            dstMsg.obj = obj;
            this.mReplyChannel.replyToMessage(msg, dstMsg);
        }
    }

    private Message obtainMessageWithWhatAndArg2(Message srcMsg, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg2 = srcMsg.arg2;
        return msg;
    }

    /* access modifiers changed from: private */
    public void broadcastWifiCredentialChanged(int wifiCredentialEventType, WifiConfiguration config) {
        if (config != null && config.preSharedKey != null) {
            Intent intent = new Intent("android.net.wifi.WIFI_CREDENTIAL_CHANGED");
            intent.putExtra(WifiBackupRestore.SupplicantBackupMigration.SUPPLICANT_KEY_SSID, config.SSID);
            intent.putExtra("et", wifiCredentialEventType);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT, "android.permission.RECEIVE_WIFI_CREDENTIAL_CHANGE");
        }
    }

    /* access modifiers changed from: package-private */
    public void handleGsmAuthRequest(TelephonyUtil.SimAuthRequestData requestData) {
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfiguration;
        if (wifiConfiguration == null || wifiConfiguration.networkId == requestData.networkId) {
            logd("id matches targetWifiConfiguration");
            String response = TelephonyUtil.getGsmSimAuthResponse(requestData.data, getTelephonyManager());
            if (response == null && (response = TelephonyUtil.getGsmSimpleSimAuthResponse(requestData.data, getTelephonyManager())) == null) {
                response = TelephonyUtil.getGsmSimpleSimNoLengthAuthResponse(requestData.data, getTelephonyManager());
            }
            if (response == null || response.length() == 0) {
                this.mWifiNative.simAuthFailedResponse(this.mInterfaceName, requestData.networkId);
                return;
            }
            logv("Supplicant Response -" + response);
            this.mWifiNative.simAuthResponse(this.mInterfaceName, requestData.networkId, WifiNative.SIM_AUTH_RESP_TYPE_GSM_AUTH, response);
            return;
        }
        logd("id does not match targetWifiConfiguration");
    }

    /* access modifiers changed from: package-private */
    public void handle3GAuthRequest(TelephonyUtil.SimAuthRequestData requestData) {
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfiguration;
        if (wifiConfiguration == null || wifiConfiguration.networkId == requestData.networkId) {
            logd("id matches targetWifiConfiguration");
            TelephonyUtil.SimAuthResponseData response = TelephonyUtil.get3GAuthResponse(requestData, getTelephonyManager());
            if (response != null) {
                this.mWifiNative.simAuthResponse(this.mInterfaceName, requestData.networkId, response.type, response.response);
            } else {
                this.mWifiNative.umtsAuthFailedResponse(this.mInterfaceName, requestData.networkId);
            }
        } else {
            logd("id does not match targetWifiConfiguration");
        }
    }

    public void startConnectToNetwork(int networkId, int uid, String bssid) {
        sendMessage(CMD_START_CONNECT, networkId, uid, bssid);
    }

    public void startRoamToNetwork(int networkId, ScanResult scanResult) {
        sendMessage(CMD_START_ROAM, networkId, 0, scanResult);
    }

    public void enableWifiConnectivityManager(boolean enabled) {
        sendMessage(CMD_ENABLE_WIFI_CONNECTIVITY_MANAGER, enabled);
    }

    static boolean unexpectedDisconnectedReason(int reason) {
        return reason == 2 || reason == 6 || reason == 7 || reason == 8 || reason == 9 || reason == 14 || reason == 15 || reason == 16 || reason == 18 || reason == 19 || reason == 23 || reason == 34;
    }

    public void updateWifiMetrics() {
        this.mWifiMetrics.updateSavedNetworks(this.mWifiConfigManager.getSavedNetworks(1010));
        this.mPasspointManager.updateMetrics();
    }

    /* access modifiers changed from: private */
    public boolean deleteNetworkConfigAndSendReply(Message message, boolean calledFromForget) {
        boolean success = this.mWifiConfigManager.removeNetwork(message.arg1, message.sendingUid);
        if (!success) {
            loge("Failed to remove network");
        }
        if (calledFromForget) {
            if (success) {
                replyToMessage(message, 151558);
                broadcastWifiCredentialChanged(1, (WifiConfiguration) message.obj);
                return true;
            }
            replyToMessage(message, 151557, 0);
            return false;
        } else if (success) {
            replyToMessage(message, message.what, 1);
            return true;
        } else {
            this.mMessageHandlingStatus = -2;
            replyToMessage(message, message.what, -1);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public NetworkUpdateResult saveNetworkConfigAndSendReply(Message message) {
        WifiConfiguration config = (WifiConfiguration) message.obj;
        if (config == null) {
            loge("SAVE_NETWORK with null configuration " + this.mSupplicantStateTracker.getSupplicantStateName() + " my state " + getCurrentState().getName());
            this.mMessageHandlingStatus = -2;
            replyToMessage(message, 151560, 0);
            return new NetworkUpdateResult(-1);
        }
        NetworkUpdateResult result = this.mWifiConfigManager.addOrUpdateNetwork(config, message.sendingUid);
        if (!result.isSuccess()) {
            loge("SAVE_NETWORK adding/updating config=" + config + " failed");
            this.mMessageHandlingStatus = -2;
            replyToMessage(message, 151560, 0);
            return result;
        } else if (!this.mWifiConfigManager.enableNetwork(result.getNetworkId(), false, message.sendingUid)) {
            loge("SAVE_NETWORK enabling config=" + config + " failed");
            this.mMessageHandlingStatus = -2;
            replyToMessage(message, 151560, 0);
            return new NetworkUpdateResult(-1);
        } else {
            broadcastWifiCredentialChanged(0, config);
            replyToMessage(message, 151561);
            return result;
        }
    }

    private static String getLinkPropertiesSummary(LinkProperties lp) {
        List<String> attributes = new ArrayList<>(6);
        if (lp.hasIPv4Address()) {
            attributes.add("v4");
        }
        if (lp.hasIPv4DefaultRoute()) {
            attributes.add("v4r");
        }
        if (lp.hasIPv4DnsServer()) {
            attributes.add("v4dns");
        }
        if (lp.hasGlobalIPv6Address()) {
            attributes.add("v6");
        }
        if (lp.hasIPv6DefaultRoute()) {
            attributes.add("v6r");
        }
        if (lp.hasIPv6DnsServer()) {
            attributes.add("v6dns");
        }
        return TextUtils.join(" ", attributes);
    }

    /* access modifiers changed from: private */
    public String getTargetSsid() {
        WifiConfiguration currentConfig = this.mWifiConfigManager.getConfiguredNetwork(this.mTargetNetworkId);
        if (currentConfig != null) {
            return currentConfig.SSID;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public boolean p2pSendMessage(int what) {
        AsyncChannel asyncChannel = this.mWifiP2pChannel;
        if (asyncChannel == null) {
            return false;
        }
        asyncChannel.sendMessage(what);
        return true;
    }

    private boolean p2pSendMessage(int what, int arg1) {
        AsyncChannel asyncChannel = this.mWifiP2pChannel;
        if (asyncChannel == null) {
            return false;
        }
        asyncChannel.sendMessage(what, arg1);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean hasConnectionRequests() {
        return this.mNetworkFactory.hasConnectionRequests() || this.mUntrustedNetworkFactory.hasConnectionRequests();
    }

    public boolean getIpReachabilityDisconnectEnabled() {
        return this.mIpReachabilityDisconnectEnabled;
    }

    public void setIpReachabilityDisconnectEnabled(boolean enabled) {
        this.mIpReachabilityDisconnectEnabled = enabled;
    }

    public boolean syncInitialize(AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_INITIALIZE);
        boolean result = resultMsg.arg1 != -1;
        resultMsg.recycle();
        return result;
    }

    public void addNetworkRequestMatchCallback(IBinder binder, INetworkRequestMatchCallback callback, int callbackIdentifier) {
        this.mNetworkFactory.addCallback(binder, callback, callbackIdentifier);
    }

    public void removeNetworkRequestMatchCallback(int callbackIdentifier) {
        this.mNetworkFactory.removeCallback(callbackIdentifier);
    }

    public void removeNetworkRequestUserApprovedAccessPointsForApp(String packageName) {
        this.mNetworkFactory.removeUserApprovedAccessPointsForApp(packageName);
    }

    public void clearNetworkRequestUserApprovedAccessPoints() {
        this.mNetworkFactory.clear();
    }

    public int syncDppAddBootstrapQrCode(AsyncChannel channel, String uri) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_ADD_BOOTSTRAP_QRCODE, 0, 0, uri);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public int syncDppBootstrapGenerate(AsyncChannel channel, WifiDppConfig config) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_GENERATE_BOOTSTRAP, 0, 0, config);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public String syncDppGetUri(AsyncChannel channel, int bootstrap_id) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_GET_URI, bootstrap_id);
        String result = (String) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    public int syncDppBootstrapRemove(AsyncChannel channel, int bootstrap_id) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_REMOVE_BOOTSTRAP, bootstrap_id);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public int syncDppListen(AsyncChannel channel, String frequency, int dpp_role, boolean qr_mutual, boolean netrole_ap) {
        Bundle bundle = new Bundle();
        bundle.putString("freq", frequency);
        bundle.putInt("dppRole", dpp_role);
        bundle.putBoolean("mutual", qr_mutual);
        bundle.putBoolean("netRoleAp", netrole_ap);
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_LISTEN_START, 0, 0, bundle);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public void dppStopListen(AsyncChannel channel) {
        sendMessage(CMD_DPP_LISTEN_STOP);
    }

    public int syncDppConfiguratorAdd(AsyncChannel channel, String curve, String key, int expiry) {
        Bundle bundle = new Bundle();
        bundle.putString("curve", curve);
        bundle.putString("key", key);
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_CONF_ADD, expiry, 0, bundle);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public int syncDppConfiguratorRemove(AsyncChannel channel, int config_id) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_CONF_REMOVE, config_id);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public int syncDppStartAuth(AsyncChannel channel, WifiDppConfig config) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_AUTH_INIT, 0, 0, config);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public String syncDppConfiguratorGetKey(AsyncChannel channel, int id) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_DPP_CONFIGURATOR_GET_KEY, 0, 0, Integer.valueOf(id));
        String result = (String) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    public String getFactoryMacAddress() {
        MacAddress macAddress = this.mWifiNative.getFactoryMacAddress(this.mInterfaceName);
        if (macAddress != null) {
            return macAddress.toString();
        }
        if (!this.mConnectedMacRandomzationSupported) {
            return this.mWifiNative.getMacAddress(this.mInterfaceName);
        }
        return null;
    }

    public void setDeviceMobilityState(int state) {
        this.mWifiConnectivityManager.setDeviceMobilityState(state);
    }

    public void updateWifiUsabilityScore(int seqNum, int score, int predictionHorizonSec) {
        this.mWifiMetrics.incrementWifiUsabilityScoreCount(seqNum, score, predictionHorizonSec);
    }

    @VisibleForTesting
    public void probeLink(WifiNative.SendMgmtFrameCallback callback, int mcs) {
        this.mWifiNative.probeLink(this.mInterfaceName, MacAddress.fromString(this.mWifiInfo.getBSSID()), callback, mcs);
    }
}
