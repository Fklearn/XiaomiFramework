package com.android.server.wifi;

import android.content.Context;
import android.hardware.wifi.supplicant.V1_0.ISupplicantStaIfaceCallback;
import android.net.wifi.IOnWifiUsabilityStatsListener;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiUsabilityStatsEntry;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.wifi.ScoringParams;
import com.android.server.wifi.aware.WifiAwareMetrics;
import com.android.server.wifi.hotspot2.NetworkDetail;
import com.android.server.wifi.hotspot2.PasspointManager;
import com.android.server.wifi.hotspot2.PasspointProvider;
import com.android.server.wifi.nano.WifiMetricsProto;
import com.android.server.wifi.p2p.WifiP2pMetrics;
import com.android.server.wifi.rtt.RttMetrics;
import com.android.server.wifi.scanner.ChannelHelper;
import com.android.server.wifi.util.ExternalCallbackTracker;
import com.android.server.wifi.util.IntCounter;
import com.android.server.wifi.util.IntHistogram;
import com.android.server.wifi.util.MetricsUtils;
import com.android.server.wifi.util.ObjectCounter;
import com.android.server.wifi.util.ScanResultUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import miui.telephony.phonenumber.Prefix;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WifiMetrics {
    public static final String CLEAN_DUMP_ARG = "clean";
    private static final int CONNECT_TO_NETWORK_NOTIFICATION_ACTION_KEY_MULTIPLIER = 1000;
    private static final boolean DBG = false;
    private static final int[] LINK_PROBE_ELAPSED_TIME_MS_HISTOGRAM_BUCKETS = {5, 10, 15, 20, 25, 50, 100, ChannelHelper.SCAN_PERIOD_PER_CHANNEL_MS, 400, 800};
    private static final int[] LINK_PROBE_TIME_SINCE_LAST_TX_SUCCESS_SECONDS_HISTOGRAM_BUCKETS = {5, 15, 45, 135};
    @VisibleForTesting
    static final int LOW_WIFI_SCORE = 50;
    @VisibleForTesting
    static final int LOW_WIFI_USABILITY_SCORE = 50;
    public static final int MAX_CONNECTABLE_BSSID_NETWORK_BUCKET = 50;
    public static final int MAX_CONNECTABLE_SSID_NETWORK_BUCKET = 20;
    private static final int MAX_CONNECTION_EVENTS = 256;
    @VisibleForTesting
    static final int MAX_LINK_PROBE_STA_EVENTS = 192;
    private static final int MAX_NUM_SOFT_AP_EVENTS = 256;
    public static final int MAX_PASSPOINT_APS_PER_UNIQUE_ESS_BUCKET = 50;
    public static final int MAX_RSSI_DELTA = 127;
    private static final int MAX_RSSI_POLL = 0;
    public static final int MAX_STA_EVENTS = 768;
    public static final int MAX_TOTAL_80211MC_APS_BUCKET = 20;
    public static final int MAX_TOTAL_PASSPOINT_APS_BUCKET = 50;
    public static final int MAX_TOTAL_PASSPOINT_UNIQUE_ESS_BUCKET = 20;
    public static final int MAX_TOTAL_SCAN_RESULTS_BUCKET = 250;
    public static final int MAX_TOTAL_SCAN_RESULT_SSIDS_BUCKET = 100;
    public static final int MAX_UNUSABLE_EVENTS = 20;
    private static final int MAX_WIFI_SCORE = 60;
    private static final int MAX_WIFI_USABILITY_SCORE = 100;
    public static final int MAX_WIFI_USABILITY_STATS_ENTRIES_LIST_SIZE = 40;
    public static final int MAX_WIFI_USABILITY_STATS_LIST_SIZE_PER_TYPE = 10;
    public static final int MAX_WIFI_USABILITY_STATS_PER_TYPE_TO_UPLOAD = 2;
    public static final int MIN_DATA_STALL_WAIT_MS = 120000;
    public static final int MIN_LINK_SPEED_MBPS = 0;
    public static final int MIN_RSSI_DELTA = -127;
    private static final int MIN_RSSI_POLL = -127;
    public static final int MIN_SCORE_BREACH_TO_GOOD_STATS_WAIT_TIME_MS = 60000;
    public static final int MIN_WIFI_GOOD_USABILITY_STATS_PERIOD_MS = 3600000;
    private static final int MIN_WIFI_SCORE = 0;
    private static final int MIN_WIFI_USABILITY_SCORE = 0;
    private static final int[] NETWORK_REQUEST_API_MATCH_SIZE_HISTOGRAM_BUCKETS = {0, 1, 5, 10};
    private static final int[] NETWORK_SUGGESTION_API_LIST_SIZE_HISTOGRAM_BUCKETS = {5, 20, 50, 100, 500};
    public static final int NUM_WIFI_USABILITY_STATS_ENTRIES_PER_WIFI_GOOD = 100;
    public static final String PROTO_DUMP_ARG = "wifiMetricsProto";
    private static final int SCREEN_OFF = 0;
    private static final int SCREEN_ON = 1;
    private static final String TAG = "WifiMetrics";
    public static final long TIMEOUT_RSSI_DELTA_MILLIS = 3000;
    public static final int VALIDITY_PERIOD_OF_SCORE_BREACH_LOW_MS = 90000;
    private static final int[] WIFI_CONFIG_STORE_IO_DURATION_BUCKET_RANGES_MS = {50, 100, 150, ChannelHelper.SCAN_PERIOD_PER_CHANNEL_MS, 300};
    private static final int WIFI_IS_UNUSABLE_EVENT_METRICS_ENABLED_DEFAULT = 1;
    private static final int WIFI_LINK_SPEED_METRICS_ENABLED_DEFAULT = 1;
    private static final int[] WIFI_LOCK_SESSION_DURATION_HISTOGRAM_BUCKETS = {1, 10, 60, 600, 3600};
    private final SparseIntArray mAvailableOpenBssidsInScanHistogram = new SparseIntArray();
    private final SparseIntArray mAvailableOpenOrSavedBssidsInScanHistogram = new SparseIntArray();
    private final SparseIntArray mAvailableOpenOrSavedSsidsInScanHistogram = new SparseIntArray();
    private final SparseIntArray mAvailableOpenSsidsInScanHistogram = new SparseIntArray();
    private final SparseIntArray mAvailableSavedBssidsInScanHistogram = new SparseIntArray();
    private final SparseIntArray mAvailableSavedPasspointProviderBssidsInScanHistogram = new SparseIntArray();
    private final SparseIntArray mAvailableSavedPasspointProviderProfilesInScanHistogram = new SparseIntArray();
    private final SparseIntArray mAvailableSavedSsidsInScanHistogram = new SparseIntArray();
    private final CellularLinkLayerStatsCollector mCellularLinkLayerStatsCollector;
    private Clock mClock;
    private final SparseIntArray mConnectToNetworkNotificationActionCount = new SparseIntArray();
    private final SparseIntArray mConnectToNetworkNotificationCount = new SparseIntArray();
    private final List<ConnectionEvent> mConnectionEventList = new ArrayList();
    private Context mContext;
    /* access modifiers changed from: private */
    public ConnectionEvent mCurrentConnectionEvent;
    private int mCurrentDeviceMobilityState;
    private long mCurrentDeviceMobilityStatePnoScanStartMs;
    private long mCurrentDeviceMobilityStateStartMs;
    private final DppMetrics mDppMetrics;
    private final WifiMetricsProto.ExperimentValues mExperimentValues = new WifiMetricsProto.ExperimentValues();
    private FrameworkFacade mFacade;
    private Handler mHandler;
    private final IntCounter mInstalledPasspointProfileTypeForR1 = new IntCounter();
    private final IntCounter mInstalledPasspointProfileTypeForR2 = new IntCounter();
    private boolean mIsMacRandomizationOn = false;
    private boolean mIsWifiNetworksAvailableNotificationOn = false;
    private String mLastBssid;
    private long mLastDataStallTime = Long.MIN_VALUE;
    private int mLastFrequency = -1;
    private WifiLinkLayerStats mLastLinkLayerStats;
    private int mLastPollFreq = -1;
    private int mLastPollLinkSpeed = -1;
    private int mLastPollRssi = -127;
    private int mLastPredictionHorizonSec = -1;
    private int mLastPredictionHorizonSecNoReset = -1;
    private int mLastScore = -1;
    private int mLastScoreNoReset = -1;
    private int mLastWifiUsabilityScore = -1;
    private int mLastWifiUsabilityScoreNoReset = -1;
    private final ObjectCounter<String> mLinkProbeExperimentProbeCounts = new ObjectCounter<>();
    private final IntCounter mLinkProbeFailureLinkSpeedCounts = new IntCounter();
    private final IntCounter mLinkProbeFailureReasonCounts = new IntCounter();
    private final IntCounter mLinkProbeFailureRssiCounts = new IntCounter(-85, -65);
    private final IntHistogram mLinkProbeFailureSecondsSinceLastTxSuccessHistogram = new IntHistogram(LINK_PROBE_TIME_SINCE_LAST_TX_SUCCESS_SECONDS_HISTOGRAM_BUCKETS);
    private int mLinkProbeStaEventCount = 0;
    private final IntHistogram mLinkProbeSuccessElapsedTimeMsHistogram = new IntHistogram(LINK_PROBE_ELAPSED_TIME_MS_HISTOGRAM_BUCKETS);
    private final IntCounter mLinkProbeSuccessLinkSpeedCounts = new IntCounter();
    private final IntCounter mLinkProbeSuccessRssiCounts = new IntCounter(-85, -65);
    private final IntHistogram mLinkProbeSuccessSecondsSinceLastTxSuccessHistogram = new IntHistogram(LINK_PROBE_TIME_SINCE_LAST_TX_SUCCESS_SECONDS_HISTOGRAM_BUCKETS);
    private final SparseArray<WifiMetricsProto.LinkSpeedCount> mLinkSpeedCounts = new SparseArray<>();
    private boolean mLinkSpeedCountsLogging = true;
    private long mLlStatsLastUpdateTime = 0;
    private long mLlStatsUpdateTimeDelta = 0;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final SparseArray<WifiMetricsProto.DeviceMobilityStatePnoScanStats> mMobilityStatePnoStatsMap = new SparseArray<>();
    private final SparseIntArray mNetworkIdToNominatorId = new SparseIntArray();
    private Map<Pair<Integer, Integer>, NetworkSelectionExperimentResults> mNetworkSelectionExperimentPairNumChoicesCounts = new ArrayMap();
    private int mNetworkSelectorExperimentId;
    private int mNumOpenNetworkConnectMessageFailedToSend = 0;
    private int mNumOpenNetworkRecommendationUpdates = 0;
    private int mNumProvisionSuccess = 0;
    private final SparseIntArray mObserved80211mcApInScanHistogram = new SparseIntArray();
    private final SparseIntArray mObservedHotspotR1ApInScanHistogram = new SparseIntArray();
    private final SparseIntArray mObservedHotspotR1ApsPerEssInScanHistogram = new SparseIntArray();
    private final SparseIntArray mObservedHotspotR1EssInScanHistogram = new SparseIntArray();
    private final SparseIntArray mObservedHotspotR2ApInScanHistogram = new SparseIntArray();
    private final SparseIntArray mObservedHotspotR2ApsPerEssInScanHistogram = new SparseIntArray();
    private final SparseIntArray mObservedHotspotR2EssInScanHistogram = new SparseIntArray();
    private final ExternalCallbackTracker<IOnWifiUsabilityStatsListener> mOnWifiUsabilityListeners;
    private int mOpenNetworkRecommenderBlacklistSize = 0;
    private PasspointManager mPasspointManager;
    private final IntCounter mPasspointProvisionFailureCounts = new IntCounter();
    private final WifiMetricsProto.PnoScanMetrics mPnoScanMetrics = new WifiMetricsProto.PnoScanMetrics();
    private int mProbeElapsedTimeSinceLastUpdateMs = -1;
    private int mProbeMcsRateSinceLastUpdate = -1;
    private int mProbeStatusSinceLastUpdate = 1;
    private final Random mRand = new Random();
    private long mRecordStartTimeSec;
    private final SparseIntArray mRssiDeltaCounts = new SparseIntArray();
    private final Map<Integer, SparseIntArray> mRssiPollCountsMap = new HashMap();
    private RttMetrics mRttMetrics;
    private long mRxSuccessDelta = 0;
    private int mScanResultRssi = 0;
    private long mScanResultRssiTimestampMillis = -1;
    private final SparseIntArray mScanReturnEntries = new SparseIntArray();
    private long mScoreBreachLowTimeMillis = -1;
    private ScoringParams mScoringParams;
    private boolean mScreenOn;
    private int mSeqNumInsideFramework = 0;
    private int mSeqNumToFramework = -1;
    private final List<WifiMetricsProto.SoftApConnectedClientsEvent> mSoftApEventListLocalOnly = new ArrayList();
    private final List<WifiMetricsProto.SoftApConnectedClientsEvent> mSoftApEventListTethered = new ArrayList();
    private final SparseIntArray mSoftApManagerReturnCodeCounts = new SparseIntArray();
    private LinkedList<StaEventWithTime> mStaEventList = new LinkedList<>();
    private int mSupplicantStateChangeBitmask = 0;
    private final SparseIntArray mTotalBssidsInScanHistogram = new SparseIntArray();
    private final SparseIntArray mTotalSsidsInScanHistogram = new SparseIntArray();
    private long mTxBadDelta = 0;
    private long mTxRetriesDelta = 0;
    private long mTxScucessDelta = 0;
    private boolean mUnusableEventLogging = false;
    private final SparseIntArray mWifiAlertReasonCounts = new SparseIntArray();
    private WifiAwareMetrics mWifiAwareMetrics;
    private WifiConfigManager mWifiConfigManager;
    private SparseIntArray mWifiConfigStoreReadDurationHistogram = new SparseIntArray();
    private SparseIntArray mWifiConfigStoreWriteDurationHistogram = new SparseIntArray();
    private WifiDataStall mWifiDataStall;
    private LinkedList<WifiIsUnusableWithTime> mWifiIsUnusableList = new LinkedList<>();
    private final WifiMetricsProto.WifiLinkLayerUsageStats mWifiLinkLayerUsageStats = new WifiMetricsProto.WifiLinkLayerUsageStats();
    private final IntHistogram mWifiLockHighPerfAcqDurationSecHistogram = new IntHistogram(WIFI_LOCK_SESSION_DURATION_HISTOGRAM_BUCKETS);
    private final IntHistogram mWifiLockHighPerfActiveSessionDurationSecHistogram = new IntHistogram(WIFI_LOCK_SESSION_DURATION_HISTOGRAM_BUCKETS);
    private final IntHistogram mWifiLockLowLatencyAcqDurationSecHistogram = new IntHistogram(WIFI_LOCK_SESSION_DURATION_HISTOGRAM_BUCKETS);
    private final IntHistogram mWifiLockLowLatencyActiveSessionDurationSecHistogram = new IntHistogram(WIFI_LOCK_SESSION_DURATION_HISTOGRAM_BUCKETS);
    private final WifiMetricsProto.WifiLockStats mWifiLockStats = new WifiMetricsProto.WifiLockStats();
    private final WifiMetricsProto.WifiLog mWifiLogProto = new WifiMetricsProto.WifiLog();
    private final WifiMetricsProto.WifiNetworkRequestApiLog mWifiNetworkRequestApiLog = new WifiMetricsProto.WifiNetworkRequestApiLog();
    private final IntHistogram mWifiNetworkRequestApiMatchSizeHistogram = new IntHistogram(NETWORK_REQUEST_API_MATCH_SIZE_HISTOGRAM_BUCKETS);
    private WifiNetworkSelector mWifiNetworkSelector;
    private final IntHistogram mWifiNetworkSuggestionApiListSizeHistogram = new IntHistogram(NETWORK_SUGGESTION_API_LIST_SIZE_HISTOGRAM_BUCKETS);
    private final WifiMetricsProto.WifiNetworkSuggestionApiLog mWifiNetworkSuggestionApiLog = new WifiMetricsProto.WifiNetworkSuggestionApiLog();
    private final WifiP2pMetrics mWifiP2pMetrics;
    private WifiPowerMetrics mWifiPowerMetrics;
    private final SparseIntArray mWifiScoreCounts = new SparseIntArray();
    private int mWifiState;
    private final SparseIntArray mWifiSystemStateEntries = new SparseIntArray();
    private final WifiMetricsProto.WifiToggleStats mWifiToggleStats = new WifiMetricsProto.WifiToggleStats();
    private final SparseIntArray mWifiUsabilityScoreCounts = new SparseIntArray();
    private int mWifiUsabilityStatsCounter = 0;
    private final LinkedList<WifiMetricsProto.WifiUsabilityStatsEntry> mWifiUsabilityStatsEntriesList = new LinkedList<>();
    private final LinkedList<WifiMetricsProto.WifiUsabilityStats> mWifiUsabilityStatsListBad = new LinkedList<>();
    private final LinkedList<WifiMetricsProto.WifiUsabilityStats> mWifiUsabilityStatsListGood = new LinkedList<>();
    private final WifiWakeMetrics mWifiWakeMetrics = new WifiWakeMetrics();
    private boolean mWifiWins = false;
    private boolean mWifiWinsUsabilityScore = false;
    private final WifiMetricsProto.WpsMetrics mWpsMetrics = new WifiMetricsProto.WpsMetrics();

    @VisibleForTesting
    static class NetworkSelectionExperimentResults {
        public static final int MAX_CHOICES = 10;
        public IntCounter differentSelectionNumChoicesCounter = new IntCounter(0, 10);
        public IntCounter sameSelectionNumChoicesCounter = new IntCounter(0, 10);

        NetworkSelectionExperimentResults() {
        }

        public String toString() {
            return "NetworkSelectionExperimentResults{sameSelectionNumChoicesCounter=" + this.sameSelectionNumChoicesCounter + ", differentSelectionNumChoicesCounter=" + this.differentSelectionNumChoicesCounter + '}';
        }
    }

    class RouterFingerPrint {
        /* access modifiers changed from: private */
        public WifiMetricsProto.RouterFingerPrint mRouterFingerPrintProto = new WifiMetricsProto.RouterFingerPrint();

        RouterFingerPrint() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            synchronized (WifiMetrics.this.mLock) {
                sb.append("mConnectionEvent.roamType=" + this.mRouterFingerPrintProto.roamType);
                sb.append(", mChannelInfo=" + this.mRouterFingerPrintProto.channelInfo);
                sb.append(", mDtim=" + this.mRouterFingerPrintProto.dtim);
                sb.append(", mAuthentication=" + this.mRouterFingerPrintProto.authentication);
                sb.append(", mHidden=" + this.mRouterFingerPrintProto.hidden);
                sb.append(", mRouterTechnology=" + this.mRouterFingerPrintProto.routerTechnology);
                sb.append(", mSupportsIpv6=" + this.mRouterFingerPrintProto.supportsIpv6);
            }
            return sb.toString();
        }

        public void updateFromWifiConfiguration(WifiConfiguration config) {
            synchronized (WifiMetrics.this.mLock) {
                if (config != null) {
                    this.mRouterFingerPrintProto.hidden = config.hiddenSSID;
                    if (config.dtimInterval > 0) {
                        this.mRouterFingerPrintProto.dtim = config.dtimInterval;
                    }
                    String unused = WifiMetrics.this.mCurrentConnectionEvent.mConfigSsid = config.SSID;
                    if (config.allowedKeyManagement != null && config.allowedKeyManagement.get(0)) {
                        WifiMetrics.this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 1;
                    } else if (config.isEnterprise()) {
                        WifiMetrics.this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 3;
                    } else {
                        WifiMetrics.this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 2;
                    }
                    WifiMetrics.this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.passpoint = config.isPasspoint();
                    ScanResult candidate = config.getNetworkSelectionStatus().getCandidate();
                    if (candidate != null) {
                        WifiMetrics.this.updateMetricsFromScanResult(candidate);
                    }
                }
            }
        }
    }

    class ConnectionEvent {
        public static final int FAILURE_ASSOCIATION_REJECTION = 2;
        public static final int FAILURE_ASSOCIATION_TIMED_OUT = 11;
        public static final int FAILURE_AUTHENTICATION_FAILURE = 3;
        public static final int FAILURE_CONNECT_NETWORK_FAILED = 5;
        public static final int FAILURE_DHCP = 10;
        public static final int FAILURE_NETWORK_DISCONNECTION = 6;
        public static final int FAILURE_NEW_CONNECTION_ATTEMPT = 7;
        public static final int FAILURE_NONE = 1;
        public static final int FAILURE_REDUNDANT_CONNECTION_ATTEMPT = 8;
        public static final int FAILURE_ROAM_TIMEOUT = 9;
        public static final int FAILURE_SSID_TEMP_DISABLED = 4;
        public static final int FAILURE_UNKNOWN = 0;
        /* access modifiers changed from: private */
        public String mConfigBssid;
        /* access modifiers changed from: private */
        public String mConfigSsid;
        WifiMetricsProto.ConnectionEvent mConnectionEvent;
        /* access modifiers changed from: private */
        public long mRealEndTime;
        /* access modifiers changed from: private */
        public long mRealStartTime;
        RouterFingerPrint mRouterFingerPrint;
        /* access modifiers changed from: private */
        public boolean mScreenOn;
        /* access modifiers changed from: private */
        public int mWifiState;

        private ConnectionEvent() {
            this.mConnectionEvent = new WifiMetricsProto.ConnectionEvent();
            this.mRealEndTime = 0;
            this.mRealStartTime = 0;
            this.mRouterFingerPrint = new RouterFingerPrint();
            this.mConnectionEvent.routerFingerprint = this.mRouterFingerPrint.mRouterFingerPrintProto;
            this.mConfigSsid = "<NULL>";
            this.mConfigBssid = "<NULL>";
            this.mWifiState = 0;
            this.mScreenOn = false;
        }

        public String toString() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("startTime=");
            Calendar c = Calendar.getInstance();
            synchronized (WifiMetrics.this.mLock) {
                c.setTimeInMillis(this.mConnectionEvent.startTimeMillis);
                if (this.mConnectionEvent.startTimeMillis == 0) {
                    str = "            <null>";
                } else {
                    str = String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c, c, c, c, c, c});
                }
                sb.append(str);
                sb.append(", SSID=");
                sb.append(this.mConfigSsid);
                sb.append(", BSSID=");
                sb.append(this.mConfigBssid);
                sb.append(", durationMillis=");
                sb.append(this.mConnectionEvent.durationTakenToConnectMillis);
                sb.append(", roamType=");
                int i = this.mConnectionEvent.roamType;
                if (i == 1) {
                    sb.append("ROAM_NONE");
                } else if (i == 2) {
                    sb.append("ROAM_DBDC");
                } else if (i == 3) {
                    sb.append("ROAM_ENTERPRISE");
                } else if (i == 4) {
                    sb.append("ROAM_USER_SELECTED");
                } else if (i != 5) {
                    sb.append("ROAM_UNKNOWN");
                } else {
                    sb.append("ROAM_UNRELATED");
                }
                sb.append(", connectionResult=");
                sb.append(this.mConnectionEvent.connectionResult);
                sb.append(", level2FailureCode=");
                switch (this.mConnectionEvent.level2FailureCode) {
                    case 1:
                        sb.append("NONE");
                        break;
                    case 2:
                        sb.append("ASSOCIATION_REJECTION");
                        break;
                    case 3:
                        sb.append("AUTHENTICATION_FAILURE");
                        break;
                    case 4:
                        sb.append("SSID_TEMP_DISABLED");
                        break;
                    case 5:
                        sb.append("CONNECT_NETWORK_FAILED");
                        break;
                    case 6:
                        sb.append("NETWORK_DISCONNECTION");
                        break;
                    case 7:
                        sb.append("NEW_CONNECTION_ATTEMPT");
                        break;
                    case 8:
                        sb.append("REDUNDANT_CONNECTION_ATTEMPT");
                        break;
                    case 9:
                        sb.append("ROAM_TIMEOUT");
                        break;
                    case 10:
                        sb.append("DHCP");
                        break;
                    case 11:
                        sb.append("ASSOCIATION_TIMED_OUT");
                        break;
                    default:
                        sb.append("UNKNOWN");
                        break;
                }
                sb.append(", connectivityLevelFailureCode=");
                int i2 = this.mConnectionEvent.connectivityLevelFailureCode;
                if (i2 == 1) {
                    sb.append("NONE");
                } else if (i2 == 2) {
                    sb.append("DHCP");
                } else if (i2 == 3) {
                    sb.append("NO_INTERNET");
                } else if (i2 != 4) {
                    sb.append("UNKNOWN");
                } else {
                    sb.append("UNWANTED");
                }
                sb.append(", signalStrength=");
                sb.append(this.mConnectionEvent.signalStrength);
                sb.append(", wifiState=");
                int i3 = this.mWifiState;
                if (i3 == 1) {
                    sb.append("WIFI_DISABLED");
                } else if (i3 == 2) {
                    sb.append("WIFI_DISCONNECTED");
                } else if (i3 != 3) {
                    sb.append("WIFI_UNKNOWN");
                } else {
                    sb.append("WIFI_ASSOCIATED");
                }
                sb.append(", screenOn=");
                sb.append(this.mScreenOn);
                sb.append(", mRouterFingerprint=");
                sb.append(this.mRouterFingerPrint.toString());
                sb.append(", useRandomizedMac=");
                sb.append(this.mConnectionEvent.useRandomizedMac);
                sb.append(", connectionNominator=");
                switch (this.mConnectionEvent.connectionNominator) {
                    case 0:
                        sb.append("NOMINATOR_UNKNOWN");
                        break;
                    case 1:
                        sb.append("NOMINATOR_MANUAL");
                        break;
                    case 2:
                        sb.append("NOMINATOR_SAVED");
                        break;
                    case 3:
                        sb.append("NOMINATOR_SUGGESTION");
                        break;
                    case 4:
                        sb.append("NOMINATOR_PASSPOINT");
                        break;
                    case 5:
                        sb.append("NOMINATOR_CARRIER");
                        break;
                    case 6:
                        sb.append("NOMINATOR_EXTERNAL_SCORED");
                        break;
                    case 7:
                        sb.append("NOMINATOR_SPECIFIER");
                        break;
                    case 8:
                        sb.append("NOMINATOR_SAVED_USER_CONNECT_CHOICE");
                        break;
                    case 9:
                        sb.append("NOMINATOR_OPEN_NETWORK_AVAILABLE");
                        break;
                    default:
                        sb.append(String.format("UnrecognizedNominator(%d)", new Object[]{Integer.valueOf(this.mConnectionEvent.connectionNominator)}));
                        break;
                }
                sb.append(", networkSelectorExperimentId=");
                sb.append(this.mConnectionEvent.networkSelectorExperimentId);
                sb.append(", level2FailureReason=");
                int i4 = this.mConnectionEvent.level2FailureReason;
                if (i4 == 1) {
                    sb.append("AUTH_FAILURE_NONE");
                } else if (i4 == 2) {
                    sb.append("AUTH_FAILURE_TIMEOUT");
                } else if (i4 == 3) {
                    sb.append("AUTH_FAILURE_WRONG_PSWD");
                } else if (i4 != 4) {
                    sb.append("FAILURE_REASON_UNKNOWN");
                } else {
                    sb.append("AUTH_FAILURE_EAP_FAILURE");
                }
            }
            return sb.toString();
        }
    }

    public WifiMetrics(Context context, FrameworkFacade facade, Clock clock, Looper looper, WifiAwareMetrics awareMetrics, RttMetrics rttMetrics, WifiPowerMetrics wifiPowerMetrics, WifiP2pMetrics wifiP2pMetrics, DppMetrics dppMetrics, CellularLinkLayerStatsCollector cellularLinkLayerStatsCollector) {
        this.mContext = context;
        this.mFacade = facade;
        this.mClock = clock;
        this.mCurrentConnectionEvent = null;
        this.mScreenOn = true;
        this.mWifiState = 1;
        this.mRecordStartTimeSec = this.mClock.getElapsedSinceBootMillis() / 1000;
        this.mWifiAwareMetrics = awareMetrics;
        this.mRttMetrics = rttMetrics;
        this.mWifiPowerMetrics = wifiPowerMetrics;
        this.mWifiP2pMetrics = wifiP2pMetrics;
        this.mDppMetrics = dppMetrics;
        this.mCellularLinkLayerStatsCollector = cellularLinkLayerStatsCollector;
        loadSettings();
        this.mHandler = new Handler(looper) {
            public void handleMessage(Message msg) {
                synchronized (WifiMetrics.this.mLock) {
                    WifiMetrics.this.processMessage(msg);
                }
            }
        };
        this.mCurrentDeviceMobilityState = 0;
        getOrCreateDeviceMobilityStatePnoScanStats(this.mCurrentDeviceMobilityState).numTimesEnteredState++;
        this.mCurrentDeviceMobilityStateStartMs = this.mClock.getElapsedSinceBootMillis();
        this.mCurrentDeviceMobilityStatePnoScanStartMs = -1;
        this.mOnWifiUsabilityListeners = new ExternalCallbackTracker<>(this.mHandler);
    }

    @VisibleForTesting
    public void loadSettings() {
        boolean z = false;
        this.mUnusableEventLogging = this.mFacade.getIntegerSetting(this.mContext, "wifi_is_unusable_event_metrics_enabled", 1) == 1;
        setWifiIsUnusableLoggingEnabled(this.mUnusableEventLogging);
        if (this.mFacade.getIntegerSetting(this.mContext, "wifi_link_speed_metrics_enabled", 1) == 1) {
            z = true;
        }
        this.mLinkSpeedCountsLogging = z;
        setLinkSpeedCountsLoggingEnabled(this.mLinkSpeedCountsLogging);
        WifiDataStall wifiDataStall = this.mWifiDataStall;
        if (wifiDataStall != null) {
            wifiDataStall.loadSettings();
        }
    }

    public void setScoringParams(ScoringParams scoringParams) {
        this.mScoringParams = scoringParams;
    }

    public void setWifiConfigManager(WifiConfigManager wifiConfigManager) {
        this.mWifiConfigManager = wifiConfigManager;
    }

    public void setWifiNetworkSelector(WifiNetworkSelector wifiNetworkSelector) {
        this.mWifiNetworkSelector = wifiNetworkSelector;
    }

    public void setPasspointManager(PasspointManager passpointManager) {
        this.mPasspointManager = passpointManager;
    }

    public void setWifiDataStall(WifiDataStall wifiDataStall) {
        this.mWifiDataStall = wifiDataStall;
    }

    public void incrementWifiLinkLayerUsageStats(WifiLinkLayerStats newStats) {
        if (newStats != null) {
            WifiLinkLayerStats wifiLinkLayerStats = this.mLastLinkLayerStats;
            if (wifiLinkLayerStats == null) {
                this.mLastLinkLayerStats = newStats;
            } else if (!newLinkLayerStatsIsValid(wifiLinkLayerStats, newStats)) {
                this.mLastLinkLayerStats = null;
            } else {
                this.mWifiLinkLayerUsageStats.loggingDurationMs += newStats.timeStampInMs - this.mLastLinkLayerStats.timeStampInMs;
                this.mWifiLinkLayerUsageStats.radioOnTimeMs += (long) (newStats.on_time - this.mLastLinkLayerStats.on_time);
                this.mWifiLinkLayerUsageStats.radioTxTimeMs += (long) (newStats.tx_time - this.mLastLinkLayerStats.tx_time);
                this.mWifiLinkLayerUsageStats.radioRxTimeMs += (long) (newStats.rx_time - this.mLastLinkLayerStats.rx_time);
                this.mWifiLinkLayerUsageStats.radioScanTimeMs += (long) (newStats.on_time_scan - this.mLastLinkLayerStats.on_time_scan);
                this.mWifiLinkLayerUsageStats.radioNanScanTimeMs += (long) (newStats.on_time_nan_scan - this.mLastLinkLayerStats.on_time_nan_scan);
                this.mWifiLinkLayerUsageStats.radioBackgroundScanTimeMs += (long) (newStats.on_time_background_scan - this.mLastLinkLayerStats.on_time_background_scan);
                this.mWifiLinkLayerUsageStats.radioRoamScanTimeMs += (long) (newStats.on_time_roam_scan - this.mLastLinkLayerStats.on_time_roam_scan);
                this.mWifiLinkLayerUsageStats.radioPnoScanTimeMs += (long) (newStats.on_time_pno_scan - this.mLastLinkLayerStats.on_time_pno_scan);
                this.mWifiLinkLayerUsageStats.radioHs20ScanTimeMs += (long) (newStats.on_time_hs20_scan - this.mLastLinkLayerStats.on_time_hs20_scan);
                this.mLastLinkLayerStats = newStats;
            }
        }
    }

    private boolean newLinkLayerStatsIsValid(WifiLinkLayerStats oldStats, WifiLinkLayerStats newStats) {
        if (newStats.on_time < oldStats.on_time || newStats.tx_time < oldStats.tx_time || newStats.rx_time < oldStats.rx_time || newStats.on_time_scan < oldStats.on_time_scan) {
            return false;
        }
        return true;
    }

    public void incrementPnoScanStartAttempCount() {
        synchronized (this.mLock) {
            this.mPnoScanMetrics.numPnoScanAttempts++;
        }
    }

    public void incrementPnoScanFailedCount() {
        synchronized (this.mLock) {
            this.mPnoScanMetrics.numPnoScanFailed++;
        }
    }

    public void incrementPnoScanStartedOverOffloadCount() {
        synchronized (this.mLock) {
            this.mPnoScanMetrics.numPnoScanStartedOverOffload++;
        }
    }

    public void incrementPnoScanFailedOverOffloadCount() {
        synchronized (this.mLock) {
            this.mPnoScanMetrics.numPnoScanFailedOverOffload++;
        }
    }

    public void incrementPnoFoundNetworkEventCount() {
        synchronized (this.mLock) {
            this.mPnoScanMetrics.numPnoFoundNetworkEvents++;
        }
    }

    public void incrementWpsAttemptCount() {
        synchronized (this.mLock) {
            this.mWpsMetrics.numWpsAttempts++;
        }
    }

    public void incrementWpsSuccessCount() {
        synchronized (this.mLock) {
            this.mWpsMetrics.numWpsSuccess++;
        }
    }

    public void incrementWpsStartFailureCount() {
        synchronized (this.mLock) {
            this.mWpsMetrics.numWpsStartFailure++;
        }
    }

    public void incrementWpsOverlapFailureCount() {
        synchronized (this.mLock) {
            this.mWpsMetrics.numWpsOverlapFailure++;
        }
    }

    public void incrementWpsTimeoutFailureCount() {
        synchronized (this.mLock) {
            this.mWpsMetrics.numWpsTimeoutFailure++;
        }
    }

    public void incrementWpsOtherConnectionFailureCount() {
        synchronized (this.mLock) {
            this.mWpsMetrics.numWpsOtherConnectionFailure++;
        }
    }

    public void incrementWpsSupplicantFailureCount() {
        synchronized (this.mLock) {
            this.mWpsMetrics.numWpsSupplicantFailure++;
        }
    }

    public void incrementWpsCancellationCount() {
        synchronized (this.mLock) {
            this.mWpsMetrics.numWpsCancellation++;
        }
    }

    public void startConnectionEvent(WifiConfiguration config, String targetBSSID, int roamType) {
        synchronized (this.mLock) {
            boolean z = true;
            if (this.mCurrentConnectionEvent != null) {
                if (this.mCurrentConnectionEvent.mConfigSsid == null || this.mCurrentConnectionEvent.mConfigBssid == null || config == null || !this.mCurrentConnectionEvent.mConfigSsid.equals(config.SSID) || (!this.mCurrentConnectionEvent.mConfigBssid.equals("any") && !this.mCurrentConnectionEvent.mConfigBssid.equals(targetBSSID))) {
                    endConnectionEvent(7, 1, 0);
                } else {
                    String unused = this.mCurrentConnectionEvent.mConfigBssid = targetBSSID;
                    endConnectionEvent(8, 1, 0);
                }
            }
            while (this.mConnectionEventList.size() >= 256) {
                this.mConnectionEventList.remove(0);
            }
            this.mCurrentConnectionEvent = new ConnectionEvent();
            this.mCurrentConnectionEvent.mConnectionEvent.startTimeMillis = this.mClock.getWallClockMillis();
            String unused2 = this.mCurrentConnectionEvent.mConfigBssid = targetBSSID;
            this.mCurrentConnectionEvent.mConnectionEvent.roamType = roamType;
            this.mCurrentConnectionEvent.mConnectionEvent.networkSelectorExperimentId = this.mNetworkSelectorExperimentId;
            this.mCurrentConnectionEvent.mRouterFingerPrint.updateFromWifiConfiguration(config);
            String unused3 = this.mCurrentConnectionEvent.mConfigBssid = "any";
            long unused4 = this.mCurrentConnectionEvent.mRealStartTime = this.mClock.getElapsedSinceBootMillis();
            int unused5 = this.mCurrentConnectionEvent.mWifiState = this.mWifiState;
            boolean unused6 = this.mCurrentConnectionEvent.mScreenOn = this.mScreenOn;
            this.mConnectionEventList.add(this.mCurrentConnectionEvent);
            this.mScanResultRssiTimestampMillis = -1;
            if (config != null) {
                WifiMetricsProto.ConnectionEvent connectionEvent = this.mCurrentConnectionEvent.mConnectionEvent;
                if (config.macRandomizationSetting != 1) {
                    z = false;
                }
                connectionEvent.useRandomizedMac = z;
                this.mCurrentConnectionEvent.mConnectionEvent.connectionNominator = this.mNetworkIdToNominatorId.get(config.networkId, 0);
                ScanResult candidate = config.getNetworkSelectionStatus().getCandidate();
                if (candidate != null) {
                    this.mScanResultRssi = candidate.level;
                    this.mScanResultRssiTimestampMillis = this.mClock.getElapsedSinceBootMillis();
                }
            }
        }
    }

    public void setConnectionEventRoamType(int roamType) {
        synchronized (this.mLock) {
            if (this.mCurrentConnectionEvent != null) {
                this.mCurrentConnectionEvent.mConnectionEvent.roamType = roamType;
            }
        }
    }

    public void setConnectionScanDetail(ScanDetail scanDetail) {
        synchronized (this.mLock) {
            if (!(this.mCurrentConnectionEvent == null || scanDetail == null)) {
                NetworkDetail networkDetail = scanDetail.getNetworkDetail();
                ScanResult scanResult = scanDetail.getScanResult();
                if (!(networkDetail == null || scanResult == null || this.mCurrentConnectionEvent.mConfigSsid == null)) {
                    String access$200 = this.mCurrentConnectionEvent.mConfigSsid;
                    if (access$200.equals("\"" + networkDetail.getSSID() + "\"")) {
                        updateMetricsFromNetworkDetail(networkDetail);
                        updateMetricsFromScanResult(scanResult);
                    }
                }
            }
        }
    }

    public void endConnectionEvent(int level2FailureCode, int connectivityFailureCode, int level2FailureReason) {
        synchronized (this.mLock) {
            if (this.mCurrentConnectionEvent != null) {
                int i = 0;
                boolean result = level2FailureCode == 1 && connectivityFailureCode == 1;
                WifiMetricsProto.ConnectionEvent connectionEvent = this.mCurrentConnectionEvent.mConnectionEvent;
                if (result) {
                    i = 1;
                }
                connectionEvent.connectionResult = i;
                long unused = this.mCurrentConnectionEvent.mRealEndTime = this.mClock.getElapsedSinceBootMillis();
                this.mCurrentConnectionEvent.mConnectionEvent.durationTakenToConnectMillis = (int) (this.mCurrentConnectionEvent.mRealEndTime - this.mCurrentConnectionEvent.mRealStartTime);
                this.mCurrentConnectionEvent.mConnectionEvent.level2FailureCode = level2FailureCode;
                this.mCurrentConnectionEvent.mConnectionEvent.connectivityLevelFailureCode = connectivityFailureCode;
                this.mCurrentConnectionEvent.mConnectionEvent.level2FailureReason = level2FailureReason;
                this.mCurrentConnectionEvent = null;
                if (!result) {
                    this.mScanResultRssiTimestampMillis = -1;
                }
            }
        }
    }

    private void updateMetricsFromNetworkDetail(NetworkDetail networkDetail) {
        int connectionWifiMode;
        int dtimInterval = networkDetail.getDtimInterval();
        if (dtimInterval > 0) {
            this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.dtim = dtimInterval;
        }
        int wifiMode = networkDetail.getWifiMode();
        if (wifiMode == 0) {
            connectionWifiMode = 0;
        } else if (wifiMode == 1) {
            connectionWifiMode = 1;
        } else if (wifiMode == 2) {
            connectionWifiMode = 2;
        } else if (wifiMode == 3) {
            connectionWifiMode = 3;
        } else if (wifiMode == 4) {
            connectionWifiMode = 4;
        } else if (wifiMode != 5) {
            connectionWifiMode = 6;
        } else {
            connectionWifiMode = 5;
        }
        this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.routerTechnology = connectionWifiMode;
    }

    /* access modifiers changed from: private */
    public void updateMetricsFromScanResult(ScanResult scanResult) {
        this.mCurrentConnectionEvent.mConnectionEvent.signalStrength = scanResult.level;
        this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 1;
        String unused = this.mCurrentConnectionEvent.mConfigBssid = scanResult.BSSID;
        if (scanResult.capabilities != null) {
            if (ScanResultUtil.isScanResultForWepNetwork(scanResult)) {
                this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 2;
            } else if (ScanResultUtil.isScanResultForPskNetwork(scanResult) || ScanResultUtil.isScanResultForSaeNetwork(scanResult)) {
                this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 2;
            } else if (ScanResultUtil.isScanResultForWapiPskNetwork(scanResult)) {
                this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 2;
            } else if (ScanResultUtil.isScanResultForWapiCertNetwork(scanResult)) {
                this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 2;
            } else if (ScanResultUtil.isScanResultForEapNetwork(scanResult) || ScanResultUtil.isScanResultForEapSuiteBNetwork(scanResult)) {
                this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.authentication = 3;
            }
        }
        this.mCurrentConnectionEvent.mRouterFingerPrint.mRouterFingerPrintProto.channelInfo = scanResult.frequency;
    }

    /* access modifiers changed from: package-private */
    public void setIsLocationEnabled(boolean enabled) {
        synchronized (this.mLock) {
            this.mWifiLogProto.isLocationEnabled = enabled;
        }
    }

    /* access modifiers changed from: package-private */
    public void setIsScanningAlwaysEnabled(boolean enabled) {
        synchronized (this.mLock) {
            this.mWifiLogProto.isScanningAlwaysEnabled = enabled;
        }
    }

    public void incrementNonEmptyScanResultCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numNonEmptyScanResults++;
        }
    }

    public void incrementEmptyScanResultCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numEmptyScanResults++;
        }
    }

    public void incrementBackgroundScanCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numBackgroundScans++;
        }
    }

    public int getBackgroundScanCount() {
        int i;
        synchronized (this.mLock) {
            i = this.mWifiLogProto.numBackgroundScans;
        }
        return i;
    }

    public void incrementOneshotScanCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numOneshotScans++;
        }
        incrementWifiSystemScanStateCount(this.mWifiState, this.mScreenOn);
    }

    public void incrementOneshotScanWithDfsCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numOneshotHasDfsChannelScans++;
        }
    }

    public void incrementConnectivityOneshotScanCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numConnectivityOneshotScans++;
        }
    }

    public int getOneshotScanCount() {
        int i;
        synchronized (this.mLock) {
            i = this.mWifiLogProto.numOneshotScans;
        }
        return i;
    }

    public int getConnectivityOneshotScanCount() {
        int i;
        synchronized (this.mLock) {
            i = this.mWifiLogProto.numConnectivityOneshotScans;
        }
        return i;
    }

    public int getOneshotScanWithDfsCount() {
        int i;
        synchronized (this.mLock) {
            i = this.mWifiLogProto.numOneshotHasDfsChannelScans;
        }
        return i;
    }

    public void incrementExternalAppOneshotScanRequestsCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numExternalAppOneshotScanRequests++;
        }
    }

    public void incrementExternalForegroundAppOneshotScanRequestsThrottledCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numExternalForegroundAppOneshotScanRequestsThrottled++;
        }
    }

    public void incrementExternalBackgroundAppOneshotScanRequestsThrottledCount() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numExternalBackgroundAppOneshotScanRequestsThrottled++;
        }
    }

    private String returnCodeToString(int scanReturnCode) {
        if (scanReturnCode == 0) {
            return "SCAN_UNKNOWN";
        }
        if (scanReturnCode == 1) {
            return "SCAN_SUCCESS";
        }
        if (scanReturnCode == 2) {
            return "SCAN_FAILURE_INTERRUPTED";
        }
        if (scanReturnCode == 3) {
            return "SCAN_FAILURE_INVALID_CONFIGURATION";
        }
        if (scanReturnCode != 4) {
            return "<UNKNOWN>";
        }
        return "FAILURE_WIFI_DISABLED";
    }

    public void incrementScanReturnEntry(int scanReturnCode, int countToAdd) {
        synchronized (this.mLock) {
            this.mScanReturnEntries.put(scanReturnCode, this.mScanReturnEntries.get(scanReturnCode) + countToAdd);
        }
    }

    public int getScanReturnEntry(int scanReturnCode) {
        int i;
        synchronized (this.mLock) {
            i = this.mScanReturnEntries.get(scanReturnCode);
        }
        return i;
    }

    private String wifiSystemStateToString(int state) {
        if (state == 0) {
            return "WIFI_UNKNOWN";
        }
        if (state == 1) {
            return "WIFI_DISABLED";
        }
        if (state == 2) {
            return "WIFI_DISCONNECTED";
        }
        if (state != 3) {
            return "default";
        }
        return "WIFI_ASSOCIATED";
    }

    public void incrementWifiSystemScanStateCount(int state, boolean screenOn) {
        synchronized (this.mLock) {
            int index = (state * 2) + (screenOn ? 1 : 0);
            this.mWifiSystemStateEntries.put(index, this.mWifiSystemStateEntries.get(index) + 1);
        }
    }

    public int getSystemStateCount(int state, boolean screenOn) {
        int i;
        synchronized (this.mLock) {
            i = this.mWifiSystemStateEntries.get((state * 2) + (screenOn ? 1 : 0));
        }
        return i;
    }

    public void incrementNumLastResortWatchdogTriggers() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogTriggers++;
        }
    }

    public void addCountToNumLastResortWatchdogBadAssociationNetworksTotal(int count) {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogBadAssociationNetworksTotal += count;
        }
    }

    public void addCountToNumLastResortWatchdogBadAuthenticationNetworksTotal(int count) {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogBadAuthenticationNetworksTotal += count;
        }
    }

    public void addCountToNumLastResortWatchdogBadDhcpNetworksTotal(int count) {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogBadDhcpNetworksTotal += count;
        }
    }

    public void addCountToNumLastResortWatchdogBadOtherNetworksTotal(int count) {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogBadOtherNetworksTotal += count;
        }
    }

    public void addCountToNumLastResortWatchdogAvailableNetworksTotal(int count) {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogAvailableNetworksTotal += count;
        }
    }

    public void incrementNumLastResortWatchdogTriggersWithBadAssociation() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogTriggersWithBadAssociation++;
        }
    }

    public void incrementNumLastResortWatchdogTriggersWithBadAuthentication() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogTriggersWithBadAuthentication++;
        }
    }

    public void incrementNumLastResortWatchdogTriggersWithBadDhcp() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogTriggersWithBadDhcp++;
        }
    }

    public void incrementNumLastResortWatchdogTriggersWithBadOther() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogTriggersWithBadOther++;
        }
    }

    public void incrementNumConnectivityWatchdogPnoGood() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numConnectivityWatchdogPnoGood++;
        }
    }

    public void incrementNumConnectivityWatchdogPnoBad() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numConnectivityWatchdogPnoBad++;
        }
    }

    public void incrementNumConnectivityWatchdogBackgroundGood() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numConnectivityWatchdogBackgroundGood++;
        }
    }

    public void incrementNumConnectivityWatchdogBackgroundBad() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numConnectivityWatchdogBackgroundBad++;
        }
    }

    public void handlePollResult(WifiInfo wifiInfo) {
        this.mLastPollRssi = wifiInfo.getRssi();
        this.mLastPollLinkSpeed = wifiInfo.getLinkSpeed();
        this.mLastPollFreq = wifiInfo.getFrequency();
        incrementRssiPollRssiCount(this.mLastPollFreq, this.mLastPollRssi);
        incrementLinkSpeedCount(this.mLastPollLinkSpeed, this.mLastPollRssi);
    }

    @VisibleForTesting
    public void incrementRssiPollRssiCount(int frequency, int rssi) {
        if (rssi >= -127 && rssi <= 0) {
            synchronized (this.mLock) {
                if (!this.mRssiPollCountsMap.containsKey(Integer.valueOf(frequency))) {
                    this.mRssiPollCountsMap.put(Integer.valueOf(frequency), new SparseIntArray());
                }
                SparseIntArray sparseIntArray = this.mRssiPollCountsMap.get(Integer.valueOf(frequency));
                sparseIntArray.put(rssi, sparseIntArray.get(rssi) + 1);
                maybeIncrementRssiDeltaCount(rssi - this.mScanResultRssi);
            }
        }
    }

    private void maybeIncrementRssiDeltaCount(int rssi) {
        if (this.mScanResultRssiTimestampMillis >= 0) {
            if (this.mClock.getElapsedSinceBootMillis() - this.mScanResultRssiTimestampMillis <= TIMEOUT_RSSI_DELTA_MILLIS && rssi >= -127 && rssi <= 127) {
                this.mRssiDeltaCounts.put(rssi, this.mRssiDeltaCounts.get(rssi) + 1);
            }
            this.mScanResultRssiTimestampMillis = -1;
        }
    }

    @VisibleForTesting
    public void incrementLinkSpeedCount(int linkSpeed, int rssi) {
        if (this.mLinkSpeedCountsLogging && linkSpeed >= 0 && rssi >= -127 && rssi <= 0) {
            synchronized (this.mLock) {
                WifiMetricsProto.LinkSpeedCount linkSpeedCount = this.mLinkSpeedCounts.get(linkSpeed);
                if (linkSpeedCount == null) {
                    linkSpeedCount = new WifiMetricsProto.LinkSpeedCount();
                    linkSpeedCount.linkSpeedMbps = linkSpeed;
                    this.mLinkSpeedCounts.put(linkSpeed, linkSpeedCount);
                }
                linkSpeedCount.count++;
                linkSpeedCount.rssiSumDbm += Math.abs(rssi);
                linkSpeedCount.rssiSumOfSquaresDbmSq += (long) (rssi * rssi);
            }
        }
    }

    public void incrementNumLastResortWatchdogSuccesses() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numLastResortWatchdogSuccesses++;
        }
    }

    public void incrementWatchdogTotalConnectionFailureCountAfterTrigger() {
        synchronized (this.mLock) {
            this.mWifiLogProto.watchdogTotalConnectionFailureCountAfterTrigger++;
        }
    }

    public void setWatchdogSuccessTimeDurationMs(long ms) {
        synchronized (this.mLock) {
            this.mWifiLogProto.watchdogTriggerToConnectionSuccessDurationMs = ms;
        }
    }

    private void incrementAlertReasonCount(int reason) {
        if (reason > 1024 || reason < 0) {
            reason = 0;
        }
        synchronized (this.mLock) {
            this.mWifiAlertReasonCounts.put(reason, this.mWifiAlertReasonCounts.get(reason) + 1);
        }
    }

    public void countScanResults(List<ScanDetail> scanDetails) {
        if (scanDetails != null) {
            int enterpriseNetworks = 0;
            int hiddenNetworks = 0;
            int hotspot2r1Networks = 0;
            int hotspot2r2Networks = 0;
            int wpa3PersonalNetworks = 0;
            int wpa3EnterpriseNetworks = 0;
            int enhacedOpenNetworks = 0;
            int personalNetworks = 0;
            int openNetworks = 0;
            int totalResults = 0;
            for (ScanDetail scanDetail : scanDetails) {
                NetworkDetail networkDetail = scanDetail.getNetworkDetail();
                ScanResult scanResult = scanDetail.getScanResult();
                totalResults++;
                if (networkDetail != null) {
                    if (networkDetail.isHiddenBeaconFrame()) {
                        hiddenNetworks++;
                    }
                    if (networkDetail.getHSRelease() != null) {
                        ScanDetail scanDetail2 = scanDetail;
                        if (networkDetail.getHSRelease() == NetworkDetail.HSRelease.R1) {
                            hotspot2r1Networks++;
                        } else if (networkDetail.getHSRelease() == NetworkDetail.HSRelease.R2) {
                            hotspot2r2Networks++;
                        }
                    }
                }
                if (!(scanResult == null || scanResult.capabilities == null)) {
                    if (ScanResultUtil.isScanResultForEapSuiteBNetwork(scanResult)) {
                        wpa3EnterpriseNetworks++;
                    } else if (ScanResultUtil.isScanResultForEapNetwork(scanResult)) {
                        enterpriseNetworks++;
                    } else if (ScanResultUtil.isScanResultForSaeNetwork(scanResult)) {
                        wpa3PersonalNetworks++;
                    } else if (ScanResultUtil.isScanResultForPskNetwork(scanResult) || ScanResultUtil.isScanResultForWapiPskNetwork(scanResult) || ScanResultUtil.isScanResultForWapiCertNetwork(scanResult) || ScanResultUtil.isScanResultForWepNetwork(scanResult)) {
                        personalNetworks++;
                    } else if (ScanResultUtil.isScanResultForOweNetwork(scanResult)) {
                        enhacedOpenNetworks++;
                    } else {
                        openNetworks++;
                    }
                }
            }
            synchronized (this.mLock) {
                this.mWifiLogProto.numTotalScanResults += totalResults;
                this.mWifiLogProto.numOpenNetworkScanResults += openNetworks;
                this.mWifiLogProto.numLegacyPersonalNetworkScanResults += personalNetworks;
                this.mWifiLogProto.numLegacyEnterpriseNetworkScanResults += enterpriseNetworks;
                this.mWifiLogProto.numEnhancedOpenNetworkScanResults += enhacedOpenNetworks;
                this.mWifiLogProto.numWpa3PersonalNetworkScanResults += wpa3PersonalNetworks;
                this.mWifiLogProto.numWpa3EnterpriseNetworkScanResults += wpa3EnterpriseNetworks;
                this.mWifiLogProto.numHiddenNetworkScanResults += hiddenNetworks;
                this.mWifiLogProto.numHotspot2R1NetworkScanResults += hotspot2r1Networks;
                this.mWifiLogProto.numHotspot2R2NetworkScanResults += hotspot2r2Networks;
                this.mWifiLogProto.numScans++;
            }
        }
    }

    public void incrementWifiScoreCount(int score) {
        if (score >= 0 && score <= 60) {
            synchronized (this.mLock) {
                this.mWifiScoreCounts.put(score, this.mWifiScoreCounts.get(score) + 1);
                boolean wifiWins = this.mWifiWins;
                if (this.mWifiWins && score < 50) {
                    wifiWins = false;
                } else if (!this.mWifiWins && score > 50) {
                    wifiWins = true;
                }
                this.mLastScore = score;
                this.mLastScoreNoReset = score;
                if (wifiWins != this.mWifiWins) {
                    this.mWifiWins = wifiWins;
                    WifiMetricsProto.StaEvent event = new WifiMetricsProto.StaEvent();
                    event.type = 16;
                    addStaEvent(event);
                    if (!wifiWins && this.mScoreBreachLowTimeMillis == -1) {
                        this.mScoreBreachLowTimeMillis = this.mClock.getElapsedSinceBootMillis();
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0037, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void incrementSoftApStartResult(boolean r6, int r7) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            r1 = 1
            if (r6 == 0) goto L_0x0017
            android.util.SparseIntArray r2 = r5.mSoftApManagerReturnCodeCounts     // Catch:{ all -> 0x0015 }
            int r2 = r2.get(r1)     // Catch:{ all -> 0x0015 }
            android.util.SparseIntArray r3 = r5.mSoftApManagerReturnCodeCounts     // Catch:{ all -> 0x0015 }
            int r4 = r2 + 1
            r3.put(r1, r4)     // Catch:{ all -> 0x0015 }
            monitor-exit(r0)     // Catch:{ all -> 0x0015 }
            return
        L_0x0015:
            r1 = move-exception
            goto L_0x0038
        L_0x0017:
            if (r7 != r1) goto L_0x0028
            android.util.SparseIntArray r1 = r5.mSoftApManagerReturnCodeCounts     // Catch:{ all -> 0x0015 }
            r2 = 3
            int r1 = r1.get(r2)     // Catch:{ all -> 0x0015 }
            android.util.SparseIntArray r3 = r5.mSoftApManagerReturnCodeCounts     // Catch:{ all -> 0x0015 }
            int r4 = r1 + 1
            r3.put(r2, r4)     // Catch:{ all -> 0x0015 }
            goto L_0x0036
        L_0x0028:
            android.util.SparseIntArray r1 = r5.mSoftApManagerReturnCodeCounts     // Catch:{ all -> 0x0015 }
            r2 = 2
            int r1 = r1.get(r2)     // Catch:{ all -> 0x0015 }
            android.util.SparseIntArray r3 = r5.mSoftApManagerReturnCodeCounts     // Catch:{ all -> 0x0015 }
            int r4 = r1 + 1
            r3.put(r2, r4)     // Catch:{ all -> 0x0015 }
        L_0x0036:
            monitor-exit(r0)     // Catch:{ all -> 0x0015 }
            return
        L_0x0038:
            monitor-exit(r0)     // Catch:{ all -> 0x0015 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiMetrics.incrementSoftApStartResult(boolean, int):void");
    }

    public void addSoftApUpChangedEvent(boolean isUp, int mode) {
        int i;
        WifiMetricsProto.SoftApConnectedClientsEvent event = new WifiMetricsProto.SoftApConnectedClientsEvent();
        if (isUp) {
            i = 0;
        } else {
            i = 1;
        }
        event.eventType = i;
        event.numConnectedClients = 0;
        addSoftApConnectedClientsEvent(event, mode);
    }

    public void addSoftApNumAssociatedStationsChangedEvent(int numStations, int mode) {
        WifiMetricsProto.SoftApConnectedClientsEvent event = new WifiMetricsProto.SoftApConnectedClientsEvent();
        event.eventType = 2;
        event.numConnectedClients = numStations;
        addSoftApConnectedClientsEvent(event, mode);
    }

    private void addSoftApConnectedClientsEvent(WifiMetricsProto.SoftApConnectedClientsEvent event, int mode) {
        List<WifiMetricsProto.SoftApConnectedClientsEvent> softApEventList;
        synchronized (this.mLock) {
            if (mode == 1) {
                softApEventList = this.mSoftApEventListTethered;
            } else if (mode != 2) {
                try {
                    return;
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                softApEventList = this.mSoftApEventListLocalOnly;
            }
            if (softApEventList.size() <= 256) {
                event.timeStampMillis = this.mClock.getElapsedSinceBootMillis();
                softApEventList.add(event);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addSoftApChannelSwitchedEvent(int r6, int r7, int r8) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            r1 = 1
            if (r8 == r1) goto L_0x0010
            r2 = 2
            if (r8 == r2) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x000b }
            return
        L_0x000b:
            r1 = move-exception
            goto L_0x0030
        L_0x000d:
            java.util.List<com.android.server.wifi.nano.WifiMetricsProto$SoftApConnectedClientsEvent> r2 = r5.mSoftApEventListLocalOnly     // Catch:{ all -> 0x000b }
            goto L_0x0013
        L_0x0010:
            java.util.List<com.android.server.wifi.nano.WifiMetricsProto$SoftApConnectedClientsEvent> r2 = r5.mSoftApEventListTethered     // Catch:{ all -> 0x000b }
        L_0x0013:
            int r3 = r2.size()     // Catch:{ all -> 0x000b }
            int r3 = r3 - r1
        L_0x0018:
            if (r3 < 0) goto L_0x002e
            java.lang.Object r1 = r2.get(r3)     // Catch:{ all -> 0x000b }
            com.android.server.wifi.nano.WifiMetricsProto$SoftApConnectedClientsEvent r1 = (com.android.server.wifi.nano.WifiMetricsProto.SoftApConnectedClientsEvent) r1     // Catch:{ all -> 0x000b }
            if (r1 == 0) goto L_0x002b
            int r4 = r1.eventType     // Catch:{ all -> 0x000b }
            if (r4 != 0) goto L_0x002b
            r1.channelFrequency = r6     // Catch:{ all -> 0x000b }
            r1.channelBandwidth = r7     // Catch:{ all -> 0x000b }
            goto L_0x002e
        L_0x002b:
            int r3 = r3 + -1
            goto L_0x0018
        L_0x002e:
            monitor-exit(r0)     // Catch:{ all -> 0x000b }
            return
        L_0x0030:
            monitor-exit(r0)     // Catch:{ all -> 0x000b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiMetrics.addSoftApChannelSwitchedEvent(int, int, int):void");
    }

    public void incrementNumHalCrashes() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numHalCrashes++;
        }
    }

    public void incrementNumWificondCrashes() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numWificondCrashes++;
        }
    }

    public void incrementNumSupplicantCrashes() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSupplicantCrashes++;
        }
    }

    public void incrementNumHostapdCrashes() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numHostapdCrashes++;
        }
    }

    public void incrementNumSetupClientInterfaceFailureDueToHal() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSetupClientInterfaceFailureDueToHal++;
        }
    }

    public void incrementNumSetupClientInterfaceFailureDueToWificond() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSetupClientInterfaceFailureDueToWificond++;
        }
    }

    public void incrementNumSetupClientInterfaceFailureDueToSupplicant() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSetupClientInterfaceFailureDueToSupplicant++;
        }
    }

    public void incrementNumSetupSoftApInterfaceFailureDueToHal() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSetupSoftApInterfaceFailureDueToHal++;
        }
    }

    public void incrementNumSetupSoftApInterfaceFailureDueToWificond() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSetupSoftApInterfaceFailureDueToWificond++;
        }
    }

    public void incrementNumSetupSoftApInterfaceFailureDueToHostapd() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSetupSoftApInterfaceFailureDueToHostapd++;
        }
    }

    public void incrementNumClientInterfaceDown() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numClientInterfaceDown++;
        }
    }

    public void incrementNumSoftApInterfaceDown() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSoftApInterfaceDown++;
        }
    }

    public void incrementNumPasspointProviderInstallation() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numPasspointProviderInstallation++;
        }
    }

    public void incrementNumPasspointProviderInstallSuccess() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numPasspointProviderInstallSuccess++;
        }
    }

    public void incrementNumPasspointProviderUninstallation() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numPasspointProviderUninstallation++;
        }
    }

    public void incrementNumPasspointProviderUninstallSuccess() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numPasspointProviderUninstallSuccess++;
        }
    }

    public void incrementNumRadioModeChangeToMcc() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numRadioModeChangeToMcc++;
        }
    }

    public void incrementNumRadioModeChangeToScc() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numRadioModeChangeToScc++;
        }
    }

    public void incrementNumRadioModeChangeToSbs() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numRadioModeChangeToSbs++;
        }
    }

    public void incrementNumRadioModeChangeToDbs() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numRadioModeChangeToDbs++;
        }
    }

    public void incrementNumSoftApUserBandPreferenceUnsatisfied() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSoftApUserBandPreferenceUnsatisfied++;
        }
    }

    public void incrementNumSarSensorRegistrationFailures() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSarSensorRegistrationFailures++;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x029b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void incrementAvailableNetworksHistograms(java.util.List<com.android.server.wifi.ScanDetail> r39, boolean r40) {
        /*
            r38 = this;
            r1 = r38
            java.lang.Object r2 = r1.mLock
            monitor-enter(r2)
            com.android.server.wifi.WifiConfigManager r0 = r1.mWifiConfigManager     // Catch:{ all -> 0x029c }
            if (r0 == 0) goto L_0x029a
            com.android.server.wifi.WifiNetworkSelector r0 = r1.mWifiNetworkSelector     // Catch:{ all -> 0x029c }
            if (r0 == 0) goto L_0x029a
            com.android.server.wifi.hotspot2.PasspointManager r0 = r1.mPasspointManager     // Catch:{ all -> 0x029c }
            if (r0 != 0) goto L_0x0013
            goto L_0x029a
        L_0x0013:
            r3 = 1
            if (r40 != 0) goto L_0x001f
            com.android.server.wifi.nano.WifiMetricsProto$WifiLog r0 = r1.mWifiLogProto     // Catch:{ all -> 0x029c }
            int r4 = r0.partialAllSingleScanListenerResults     // Catch:{ all -> 0x029c }
            int r4 = r4 + r3
            r0.partialAllSingleScanListenerResults = r4     // Catch:{ all -> 0x029c }
            monitor-exit(r2)     // Catch:{ all -> 0x029c }
            return
        L_0x001f:
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ all -> 0x029c }
            r0.<init>()     // Catch:{ all -> 0x029c }
            r4 = r0
            r0 = 0
            java.util.HashSet r5 = new java.util.HashSet     // Catch:{ all -> 0x029c }
            r5.<init>()     // Catch:{ all -> 0x029c }
            r6 = 0
            java.util.HashSet r7 = new java.util.HashSet     // Catch:{ all -> 0x029c }
            r7.<init>()     // Catch:{ all -> 0x029c }
            r8 = 0
            r9 = 0
            java.util.HashSet r10 = new java.util.HashSet     // Catch:{ all -> 0x029c }
            r10.<init>()     // Catch:{ all -> 0x029c }
            r11 = 0
            r12 = 0
            r13 = 0
            java.util.HashMap r14 = new java.util.HashMap     // Catch:{ all -> 0x029c }
            r14.<init>()     // Catch:{ all -> 0x029c }
            java.util.HashMap r15 = new java.util.HashMap     // Catch:{ all -> 0x029c }
            r15.<init>()     // Catch:{ all -> 0x029c }
            r16 = 0
            java.util.Iterator r17 = r39.iterator()     // Catch:{ all -> 0x029c }
            r3 = r11
            r11 = r9
            r9 = r8
            r8 = r6
            r6 = r0
            r0 = r16
        L_0x0052:
            boolean r18 = r17.hasNext()     // Catch:{ all -> 0x029c }
            if (r18 == 0) goto L_0x01cf
            java.lang.Object r18 = r17.next()     // Catch:{ all -> 0x029c }
            com.android.server.wifi.ScanDetail r18 = (com.android.server.wifi.ScanDetail) r18     // Catch:{ all -> 0x029c }
            r19 = r18
            com.android.server.wifi.hotspot2.NetworkDetail r18 = r19.getNetworkDetail()     // Catch:{ all -> 0x029c }
            android.net.wifi.ScanResult r20 = r19.getScanResult()     // Catch:{ all -> 0x029c }
            r21 = r20
            boolean r20 = r18.is80211McResponderSupport()     // Catch:{ all -> 0x029c }
            if (r20 == 0) goto L_0x0075
            int r0 = r0 + 1
            r20 = r0
            goto L_0x0077
        L_0x0075:
            r20 = r0
        L_0x0077:
            com.android.server.wifi.ScanResultMatchInfo r0 = com.android.server.wifi.ScanResultMatchInfo.fromScanResult(r21)     // Catch:{ all -> 0x029c }
            r22 = r0
            r0 = 0
            r23 = 0
            boolean r24 = r18.isInterworking()     // Catch:{ all -> 0x029c }
            r25 = 0
            if (r24 == 0) goto L_0x0150
            r24 = r0
            com.android.server.wifi.hotspot2.PasspointManager r0 = r1.mPasspointManager     // Catch:{ all -> 0x029c }
            r26 = r3
            r3 = r21
            android.util.Pair r0 = r0.matchProvider(r3)     // Catch:{ all -> 0x029c }
            r21 = r0
            r27 = r10
            r10 = r21
            if (r10 == 0) goto L_0x00a1
            java.lang.Object r0 = r10.first     // Catch:{ all -> 0x029c }
            com.android.server.wifi.hotspot2.PasspointProvider r0 = (com.android.server.wifi.hotspot2.PasspointProvider) r0     // Catch:{ all -> 0x029c }
            goto L_0x00a2
        L_0x00a1:
            r0 = 0
        L_0x00a2:
            r23 = r0
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r0 = r18.getHSRelease()     // Catch:{ all -> 0x029c }
            r21 = r10
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r10 = com.android.server.wifi.hotspot2.NetworkDetail.HSRelease.R1     // Catch:{ all -> 0x029c }
            if (r0 != r10) goto L_0x00b1
            int r12 = r12 + 1
            goto L_0x00bb
        L_0x00b1:
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r0 = r18.getHSRelease()     // Catch:{ all -> 0x029c }
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r10 = com.android.server.wifi.hotspot2.NetworkDetail.HSRelease.R2     // Catch:{ all -> 0x029c }
            if (r0 != r10) goto L_0x00bb
            int r13 = r13 + 1
        L_0x00bb:
            r28 = 0
            r10 = 0
            java.lang.String r0 = r3.BSSID     // Catch:{ IllegalArgumentException -> 0x00cc }
            long r30 = com.android.server.wifi.hotspot2.Utils.parseMac(r0)     // Catch:{ IllegalArgumentException -> 0x00cc }
            r28 = r30
            r10 = 1
            r30 = r10
            r31 = r12
            goto L_0x00eb
        L_0x00cc:
            r0 = move-exception
            r24 = r0
            java.lang.String r0 = "WifiMetrics"
            r30 = r10
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x029c }
            r10.<init>()     // Catch:{ all -> 0x029c }
            r31 = r12
            java.lang.String r12 = "Invalid BSSID provided in the scan result: "
            r10.append(r12)     // Catch:{ all -> 0x029c }
            java.lang.String r12 = r3.BSSID     // Catch:{ all -> 0x029c }
            r10.append(r12)     // Catch:{ all -> 0x029c }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x029c }
            android.util.Log.e(r0, r10)     // Catch:{ all -> 0x029c }
        L_0x00eb:
            if (r30 == 0) goto L_0x0147
            java.lang.String r0 = r3.SSID     // Catch:{ all -> 0x029c }
            r10 = r13
            long r12 = r3.hessid     // Catch:{ all -> 0x029c }
            int r37 = r18.getAnqpDomainID()     // Catch:{ all -> 0x029c }
            r32 = r0
            r33 = r28
            r35 = r12
            com.android.server.wifi.hotspot2.ANQPNetworkKey r0 = com.android.server.wifi.hotspot2.ANQPNetworkKey.buildKey(r32, r33, r35, r37)     // Catch:{ all -> 0x029c }
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r12 = r18.getHSRelease()     // Catch:{ all -> 0x029c }
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r13 = com.android.server.wifi.hotspot2.NetworkDetail.HSRelease.R1     // Catch:{ all -> 0x029c }
            if (r12 != r13) goto L_0x0124
            java.lang.Object r12 = r14.get(r0)     // Catch:{ all -> 0x029c }
            java.lang.Integer r12 = (java.lang.Integer) r12     // Catch:{ all -> 0x029c }
            if (r12 != 0) goto L_0x0113
            r13 = r25
            goto L_0x0117
        L_0x0113:
            int r13 = r12.intValue()     // Catch:{ all -> 0x029c }
        L_0x0117:
            int r24 = r13 + 1
            r32 = r10
            java.lang.Integer r10 = java.lang.Integer.valueOf(r24)     // Catch:{ all -> 0x029c }
            r14.put(r0, r10)     // Catch:{ all -> 0x029c }
            goto L_0x0149
        L_0x0124:
            r32 = r10
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r10 = r18.getHSRelease()     // Catch:{ all -> 0x029c }
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r12 = com.android.server.wifi.hotspot2.NetworkDetail.HSRelease.R2     // Catch:{ all -> 0x029c }
            if (r10 != r12) goto L_0x0149
            java.lang.Object r10 = r15.get(r0)     // Catch:{ all -> 0x029c }
            java.lang.Integer r10 = (java.lang.Integer) r10     // Catch:{ all -> 0x029c }
            if (r10 != 0) goto L_0x0139
            r12 = r25
            goto L_0x013d
        L_0x0139:
            int r12 = r10.intValue()     // Catch:{ all -> 0x029c }
        L_0x013d:
            int r13 = r12 + 1
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ all -> 0x029c }
            r15.put(r0, r13)     // Catch:{ all -> 0x029c }
            goto L_0x0149
        L_0x0147:
            r32 = r13
        L_0x0149:
            r0 = r23
            r12 = r31
            r13 = r32
            goto L_0x015c
        L_0x0150:
            r24 = r0
            r26 = r3
            r27 = r10
            r3 = r21
            r0 = r23
            r21 = r24
        L_0x015c:
            com.android.server.wifi.WifiNetworkSelector r10 = r1.mWifiNetworkSelector     // Catch:{ all -> 0x029c }
            boolean r10 = r10.isSignalTooWeak(r3)     // Catch:{ all -> 0x029c }
            if (r10 == 0) goto L_0x016c
            r0 = r20
            r3 = r26
            r10 = r27
            goto L_0x0052
        L_0x016c:
            r10 = r22
            r4.add(r10)     // Catch:{ all -> 0x029c }
            int r6 = r6 + 1
            r22 = r3
            int r3 = r10.networkType     // Catch:{ all -> 0x029c }
            if (r3 != 0) goto L_0x017b
            r3 = 1
            goto L_0x017d
        L_0x017b:
            r3 = r25
        L_0x017d:
            r23 = r6
            com.android.server.wifi.WifiConfigManager r6 = r1.mWifiConfigManager     // Catch:{ all -> 0x029c }
            r24 = r12
            r12 = r19
            android.net.wifi.WifiConfiguration r6 = r6.getConfiguredNetworkForScanDetail(r12)     // Catch:{ all -> 0x029c }
            if (r6 == 0) goto L_0x019a
            boolean r19 = r6.isEphemeral()     // Catch:{ all -> 0x029c }
            if (r19 != 0) goto L_0x019a
            boolean r19 = r6.isPasspoint()     // Catch:{ all -> 0x029c }
            if (r19 != 0) goto L_0x019a
            r19 = 1
            goto L_0x019c
        L_0x019a:
            r19 = r25
        L_0x019c:
            if (r0 == 0) goto L_0x01a0
            r25 = 1
        L_0x01a0:
            if (r3 == 0) goto L_0x01a7
            r5.add(r10)     // Catch:{ all -> 0x029c }
            int r8 = r8 + 1
        L_0x01a7:
            if (r19 == 0) goto L_0x01ae
            r7.add(r10)     // Catch:{ all -> 0x029c }
            int r9 = r9 + 1
        L_0x01ae:
            if (r3 != 0) goto L_0x01b2
            if (r19 == 0) goto L_0x01b4
        L_0x01b2:
            int r11 = r11 + 1
        L_0x01b4:
            if (r25 == 0) goto L_0x01c0
            r28 = r3
            r3 = r27
            r3.add(r0)     // Catch:{ all -> 0x029c }
            int r26 = r26 + 1
            goto L_0x01c4
        L_0x01c0:
            r28 = r3
            r3 = r27
        L_0x01c4:
            r10 = r3
            r0 = r20
            r6 = r23
            r12 = r24
            r3 = r26
            goto L_0x0052
        L_0x01cf:
            r26 = r3
            r3 = r10
            com.android.server.wifi.nano.WifiMetricsProto$WifiLog r10 = r1.mWifiLogProto     // Catch:{ all -> 0x029c }
            r17 = r0
            int r0 = r10.fullBandAllSingleScanListenerResults     // Catch:{ all -> 0x029c }
            r16 = 1
            int r0 = r0 + 1
            r10.fullBandAllSingleScanListenerResults = r0     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mTotalSsidsInScanHistogram     // Catch:{ all -> 0x029c }
            int r10 = r4.size()     // Catch:{ all -> 0x029c }
            r1.incrementTotalScanSsids(r0, r10)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mTotalBssidsInScanHistogram     // Catch:{ all -> 0x029c }
            r1.incrementTotalScanResults(r0, r6)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mAvailableOpenSsidsInScanHistogram     // Catch:{ all -> 0x029c }
            int r10 = r5.size()     // Catch:{ all -> 0x029c }
            r1.incrementSsid(r0, r10)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mAvailableOpenBssidsInScanHistogram     // Catch:{ all -> 0x029c }
            r1.incrementBssid(r0, r8)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mAvailableSavedSsidsInScanHistogram     // Catch:{ all -> 0x029c }
            int r10 = r7.size()     // Catch:{ all -> 0x029c }
            r1.incrementSsid(r0, r10)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mAvailableSavedBssidsInScanHistogram     // Catch:{ all -> 0x029c }
            r1.incrementBssid(r0, r9)     // Catch:{ all -> 0x029c }
            r5.addAll(r7)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mAvailableOpenOrSavedSsidsInScanHistogram     // Catch:{ all -> 0x029c }
            int r10 = r5.size()     // Catch:{ all -> 0x029c }
            r1.incrementSsid(r0, r10)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mAvailableOpenOrSavedBssidsInScanHistogram     // Catch:{ all -> 0x029c }
            r1.incrementBssid(r0, r11)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mAvailableSavedPasspointProviderProfilesInScanHistogram     // Catch:{ all -> 0x029c }
            int r10 = r3.size()     // Catch:{ all -> 0x029c }
            r1.incrementSsid(r0, r10)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mAvailableSavedPasspointProviderBssidsInScanHistogram     // Catch:{ all -> 0x029c }
            r10 = r26
            r1.incrementBssid(r0, r10)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mObservedHotspotR1ApInScanHistogram     // Catch:{ all -> 0x029c }
            r1.incrementTotalPasspointAps(r0, r12)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mObservedHotspotR2ApInScanHistogram     // Catch:{ all -> 0x029c }
            r1.incrementTotalPasspointAps(r0, r13)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mObservedHotspotR1EssInScanHistogram     // Catch:{ all -> 0x029c }
            r27 = r3
            int r3 = r14.size()     // Catch:{ all -> 0x029c }
            r1.incrementTotalUniquePasspointEss(r0, r3)     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r0 = r1.mObservedHotspotR2EssInScanHistogram     // Catch:{ all -> 0x029c }
            int r3 = r15.size()     // Catch:{ all -> 0x029c }
            r1.incrementTotalUniquePasspointEss(r0, r3)     // Catch:{ all -> 0x029c }
            java.util.Collection r0 = r14.values()     // Catch:{ all -> 0x029c }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x029c }
        L_0x024f:
            boolean r3 = r0.hasNext()     // Catch:{ all -> 0x029c }
            if (r3 == 0) goto L_0x026d
            java.lang.Object r3 = r0.next()     // Catch:{ all -> 0x029c }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x029c }
            r16 = r0
            android.util.SparseIntArray r0 = r1.mObservedHotspotR1ApsPerEssInScanHistogram     // Catch:{ all -> 0x029c }
            r18 = r4
            int r4 = r3.intValue()     // Catch:{ all -> 0x029c }
            r1.incrementPasspointPerUniqueEss(r0, r4)     // Catch:{ all -> 0x029c }
            r0 = r16
            r4 = r18
            goto L_0x024f
        L_0x026d:
            r18 = r4
            java.util.Collection r0 = r15.values()     // Catch:{ all -> 0x029c }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x029c }
        L_0x0277:
            boolean r3 = r0.hasNext()     // Catch:{ all -> 0x029c }
            if (r3 == 0) goto L_0x0291
            java.lang.Object r3 = r0.next()     // Catch:{ all -> 0x029c }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x029c }
            android.util.SparseIntArray r4 = r1.mObservedHotspotR2ApsPerEssInScanHistogram     // Catch:{ all -> 0x029c }
            r16 = r0
            int r0 = r3.intValue()     // Catch:{ all -> 0x029c }
            r1.incrementPasspointPerUniqueEss(r4, r0)     // Catch:{ all -> 0x029c }
            r0 = r16
            goto L_0x0277
        L_0x0291:
            android.util.SparseIntArray r0 = r1.mObserved80211mcApInScanHistogram     // Catch:{ all -> 0x029c }
            r3 = r17
            r1.increment80211mcAps(r0, r3)     // Catch:{ all -> 0x029c }
            monitor-exit(r2)     // Catch:{ all -> 0x029c }
            return
        L_0x029a:
            monitor-exit(r2)     // Catch:{ all -> 0x029c }
            return
        L_0x029c:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x029c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiMetrics.incrementAvailableNetworksHistograms(java.util.List, boolean):void");
    }

    public void incrementConnectToNetworkNotification(String notifierTag, int notificationType) {
        synchronized (this.mLock) {
            this.mConnectToNetworkNotificationCount.put(notificationType, this.mConnectToNetworkNotificationCount.get(notificationType) + 1);
        }
    }

    public void incrementConnectToNetworkNotificationAction(String notifierTag, int notificationType, int actionType) {
        synchronized (this.mLock) {
            int key = (notificationType * 1000) + actionType;
            this.mConnectToNetworkNotificationActionCount.put(key, this.mConnectToNetworkNotificationActionCount.get(key) + 1);
        }
    }

    public void setNetworkRecommenderBlacklistSize(String notifierTag, int size) {
        synchronized (this.mLock) {
            this.mOpenNetworkRecommenderBlacklistSize = size;
        }
    }

    public void setIsWifiNetworksAvailableNotificationEnabled(String notifierTag, boolean enabled) {
        synchronized (this.mLock) {
            this.mIsWifiNetworksAvailableNotificationOn = enabled;
        }
    }

    public void incrementNumNetworkRecommendationUpdates(String notifierTag) {
        synchronized (this.mLock) {
            this.mNumOpenNetworkRecommendationUpdates++;
        }
    }

    public void incrementNumNetworkConnectMessageFailedToSend(String notifierTag) {
        synchronized (this.mLock) {
            this.mNumOpenNetworkConnectMessageFailedToSend++;
        }
    }

    public void setIsMacRandomizationOn(boolean enabled) {
        synchronized (this.mLock) {
            this.mIsMacRandomizationOn = enabled;
        }
    }

    public void logFirmwareAlert(int errorCode) {
        incrementAlertReasonCount(errorCode);
        logWifiIsUnusableEvent(4, errorCode);
        addToWifiUsabilityStatsList(2, 4, errorCode);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        String eventLine;
        FileDescriptor fileDescriptor = fd;
        PrintWriter printWriter = pw;
        String[] strArr = args;
        synchronized (this.mLock) {
            try {
                consolidateScoringParams();
                if (strArr == null || strArr.length <= 0 || !PROTO_DUMP_ARG.equals(strArr[0])) {
                    printWriter.println("WifiMetrics:");
                    printWriter.println("mConnectionEvents:");
                    for (ConnectionEvent event : this.mConnectionEventList) {
                        String eventLine2 = event.toString();
                        if (event == this.mCurrentConnectionEvent) {
                            e = eventLine2 + "CURRENTLY OPEN EVENT";
                        }
                        printWriter.println(eventLine2);
                    }
                    printWriter.println("mWifiLogProto.numSavedNetworks=" + this.mWifiLogProto.numSavedNetworks);
                    printWriter.println("mWifiLogProto.numSavedNetworksWithMacRandomization=" + this.mWifiLogProto.numSavedNetworksWithMacRandomization);
                    printWriter.println("mWifiLogProto.numOpenNetworks=" + this.mWifiLogProto.numOpenNetworks);
                    printWriter.println("mWifiLogProto.numLegacyPersonalNetworks=" + this.mWifiLogProto.numLegacyPersonalNetworks);
                    printWriter.println("mWifiLogProto.numLegacyEnterpriseNetworks=" + this.mWifiLogProto.numLegacyEnterpriseNetworks);
                    printWriter.println("mWifiLogProto.numEnhancedOpenNetworks=" + this.mWifiLogProto.numEnhancedOpenNetworks);
                    printWriter.println("mWifiLogProto.numWpa3PersonalNetworks=" + this.mWifiLogProto.numWpa3PersonalNetworks);
                    printWriter.println("mWifiLogProto.numWpa3EnterpriseNetworks=" + this.mWifiLogProto.numWpa3EnterpriseNetworks);
                    printWriter.println("mWifiLogProto.numHiddenNetworks=" + this.mWifiLogProto.numHiddenNetworks);
                    printWriter.println("mWifiLogProto.numPasspointNetworks=" + this.mWifiLogProto.numPasspointNetworks);
                    printWriter.println("mWifiLogProto.isLocationEnabled=" + this.mWifiLogProto.isLocationEnabled);
                    printWriter.println("mWifiLogProto.isScanningAlwaysEnabled=" + this.mWifiLogProto.isScanningAlwaysEnabled);
                    printWriter.println("mWifiLogProto.numNetworksAddedByUser=" + this.mWifiLogProto.numNetworksAddedByUser);
                    printWriter.println("mWifiLogProto.numNetworksAddedByApps=" + this.mWifiLogProto.numNetworksAddedByApps);
                    printWriter.println("mWifiLogProto.numNonEmptyScanResults=" + this.mWifiLogProto.numNonEmptyScanResults);
                    printWriter.println("mWifiLogProto.numEmptyScanResults=" + this.mWifiLogProto.numEmptyScanResults);
                    printWriter.println("mWifiLogProto.numConnecitvityOneshotScans=" + this.mWifiLogProto.numConnectivityOneshotScans);
                    printWriter.println("mWifiLogProto.numOneshotScans=" + this.mWifiLogProto.numOneshotScans);
                    printWriter.println("mWifiLogProto.numOneshotHasDfsChannelScans=" + this.mWifiLogProto.numOneshotHasDfsChannelScans);
                    printWriter.println("mWifiLogProto.numBackgroundScans=" + this.mWifiLogProto.numBackgroundScans);
                    printWriter.println("mWifiLogProto.numExternalAppOneshotScanRequests=" + this.mWifiLogProto.numExternalAppOneshotScanRequests);
                    printWriter.println("mWifiLogProto.numExternalForegroundAppOneshotScanRequestsThrottled=" + this.mWifiLogProto.numExternalForegroundAppOneshotScanRequestsThrottled);
                    printWriter.println("mWifiLogProto.numExternalBackgroundAppOneshotScanRequestsThrottled=" + this.mWifiLogProto.numExternalBackgroundAppOneshotScanRequestsThrottled);
                    printWriter.println("mScanReturnEntries:");
                    printWriter.println("  SCAN_UNKNOWN: " + getScanReturnEntry(0));
                    printWriter.println("  SCAN_SUCCESS: " + getScanReturnEntry(1));
                    printWriter.println("  SCAN_FAILURE_INTERRUPTED: " + getScanReturnEntry(2));
                    printWriter.println("  SCAN_FAILURE_INVALID_CONFIGURATION: " + getScanReturnEntry(3));
                    printWriter.println("  FAILURE_WIFI_DISABLED: " + getScanReturnEntry(4));
                    printWriter.println("mSystemStateEntries: <state><screenOn> : <scansInitiated>");
                    printWriter.println("  WIFI_UNKNOWN       ON: " + getSystemStateCount(0, true));
                    printWriter.println("  WIFI_DISABLED      ON: " + getSystemStateCount(1, true));
                    printWriter.println("  WIFI_DISCONNECTED  ON: " + getSystemStateCount(2, true));
                    printWriter.println("  WIFI_ASSOCIATED    ON: " + getSystemStateCount(3, true));
                    printWriter.println("  WIFI_UNKNOWN      OFF: " + getSystemStateCount(0, false));
                    printWriter.println("  WIFI_DISABLED     OFF: " + getSystemStateCount(1, false));
                    printWriter.println("  WIFI_DISCONNECTED OFF: " + getSystemStateCount(2, false));
                    printWriter.println("  WIFI_ASSOCIATED   OFF: " + getSystemStateCount(3, false));
                    printWriter.println("mWifiLogProto.numConnectivityWatchdogPnoGood=" + this.mWifiLogProto.numConnectivityWatchdogPnoGood);
                    printWriter.println("mWifiLogProto.numConnectivityWatchdogPnoBad=" + this.mWifiLogProto.numConnectivityWatchdogPnoBad);
                    printWriter.println("mWifiLogProto.numConnectivityWatchdogBackgroundGood=" + this.mWifiLogProto.numConnectivityWatchdogBackgroundGood);
                    printWriter.println("mWifiLogProto.numConnectivityWatchdogBackgroundBad=" + this.mWifiLogProto.numConnectivityWatchdogBackgroundBad);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogTriggers=" + this.mWifiLogProto.numLastResortWatchdogTriggers);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogBadAssociationNetworksTotal=" + this.mWifiLogProto.numLastResortWatchdogBadAssociationNetworksTotal);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogBadAuthenticationNetworksTotal=" + this.mWifiLogProto.numLastResortWatchdogBadAuthenticationNetworksTotal);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogBadDhcpNetworksTotal=" + this.mWifiLogProto.numLastResortWatchdogBadDhcpNetworksTotal);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogBadOtherNetworksTotal=" + this.mWifiLogProto.numLastResortWatchdogBadOtherNetworksTotal);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogAvailableNetworksTotal=" + this.mWifiLogProto.numLastResortWatchdogAvailableNetworksTotal);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogTriggersWithBadAssociation=" + this.mWifiLogProto.numLastResortWatchdogTriggersWithBadAssociation);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogTriggersWithBadAuthentication=" + this.mWifiLogProto.numLastResortWatchdogTriggersWithBadAuthentication);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogTriggersWithBadDhcp=" + this.mWifiLogProto.numLastResortWatchdogTriggersWithBadDhcp);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogTriggersWithBadOther=" + this.mWifiLogProto.numLastResortWatchdogTriggersWithBadOther);
                    printWriter.println("mWifiLogProto.numLastResortWatchdogSuccesses=" + this.mWifiLogProto.numLastResortWatchdogSuccesses);
                    printWriter.println("mWifiLogProto.watchdogTotalConnectionFailureCountAfterTrigger=" + this.mWifiLogProto.watchdogTotalConnectionFailureCountAfterTrigger);
                    printWriter.println("mWifiLogProto.watchdogTriggerToConnectionSuccessDurationMs=" + this.mWifiLogProto.watchdogTriggerToConnectionSuccessDurationMs);
                    printWriter.println("mWifiLogProto.recordDurationSec=" + ((this.mClock.getElapsedSinceBootMillis() / 1000) - this.mRecordStartTimeSec));
                    JSONObject rssiMap = new JSONObject();
                    for (Map.Entry<Integer, SparseIntArray> entry : this.mRssiPollCountsMap.entrySet()) {
                        int frequency = entry.getKey().intValue();
                        SparseIntArray histogram = entry.getValue();
                        JSONArray histogramElements = new JSONArray();
                        for (int i = -127; i <= 0; i++) {
                            int count = histogram.get(i);
                            if (count != 0) {
                                JSONObject histogramElement = new JSONObject();
                                histogramElement.put(Integer.toString(i), count);
                                histogramElements.put(histogramElement);
                            }
                        }
                        rssiMap.put(Integer.toString(frequency), histogramElements);
                    }
                    printWriter.println("mWifiLogProto.rssiPollCount: " + rssiMap.toString());
                    printWriter.println("mWifiLogProto.rssiPollDeltaCount: Printing counts for [-127, 127]");
                    StringBuilder sb = new StringBuilder();
                    for (int i2 = -127; i2 <= 127; i2++) {
                        sb.append(this.mRssiDeltaCounts.get(i2) + " ");
                    }
                    printWriter.println("  " + sb.toString());
                    printWriter.println("mWifiLogProto.linkSpeedCounts: ");
                    sb.setLength(0);
                    for (int i3 = 0; i3 < this.mLinkSpeedCounts.size(); i3++) {
                        WifiMetricsProto.LinkSpeedCount linkSpeedCount = this.mLinkSpeedCounts.valueAt(i3);
                        sb.append(linkSpeedCount.linkSpeedMbps);
                        sb.append(":{");
                        sb.append(linkSpeedCount.count);
                        sb.append(", ");
                        sb.append(linkSpeedCount.rssiSumDbm);
                        sb.append(", ");
                        sb.append(linkSpeedCount.rssiSumOfSquaresDbmSq);
                        sb.append("} ");
                    }
                    if (sb.length() > 0) {
                        printWriter.println(sb.toString());
                    }
                    printWriter.print("mWifiLogProto.alertReasonCounts=");
                    sb.setLength(0);
                    for (int i4 = 0; i4 <= 1024; i4++) {
                        int count2 = this.mWifiAlertReasonCounts.get(i4);
                        if (count2 > 0) {
                            sb.append("(" + i4 + "," + count2 + "),");
                        }
                    }
                    if (sb.length() > 1) {
                        sb.setLength(sb.length() - 1);
                        printWriter.println(sb.toString());
                    } else {
                        printWriter.println("()");
                    }
                    printWriter.println("mWifiLogProto.numTotalScanResults=" + this.mWifiLogProto.numTotalScanResults);
                    printWriter.println("mWifiLogProto.numOpenNetworkScanResults=" + this.mWifiLogProto.numOpenNetworkScanResults);
                    printWriter.println("mWifiLogProto.numLegacyPersonalNetworkScanResults=" + this.mWifiLogProto.numLegacyPersonalNetworkScanResults);
                    printWriter.println("mWifiLogProto.numLegacyEnterpriseNetworkScanResults=" + this.mWifiLogProto.numLegacyEnterpriseNetworkScanResults);
                    printWriter.println("mWifiLogProto.numEnhancedOpenNetworkScanResults=" + this.mWifiLogProto.numEnhancedOpenNetworkScanResults);
                    printWriter.println("mWifiLogProto.numWpa3PersonalNetworkScanResults=" + this.mWifiLogProto.numWpa3PersonalNetworkScanResults);
                    printWriter.println("mWifiLogProto.numWpa3EnterpriseNetworkScanResults=" + this.mWifiLogProto.numWpa3EnterpriseNetworkScanResults);
                    printWriter.println("mWifiLogProto.numHiddenNetworkScanResults=" + this.mWifiLogProto.numHiddenNetworkScanResults);
                    printWriter.println("mWifiLogProto.numHotspot2R1NetworkScanResults=" + this.mWifiLogProto.numHotspot2R1NetworkScanResults);
                    printWriter.println("mWifiLogProto.numHotspot2R2NetworkScanResults=" + this.mWifiLogProto.numHotspot2R2NetworkScanResults);
                    printWriter.println("mWifiLogProto.numScans=" + this.mWifiLogProto.numScans);
                    printWriter.println("mWifiLogProto.WifiScoreCount: [0, 60]");
                    for (int i5 = 0; i5 <= 60; i5++) {
                        printWriter.print(this.mWifiScoreCounts.get(i5) + " ");
                    }
                    pw.println();
                    printWriter.println("mWifiLogProto.WifiUsabilityScoreCount: [0, 100]");
                    for (int i6 = 0; i6 <= 100; i6++) {
                        printWriter.print(this.mWifiUsabilityScoreCounts.get(i6) + " ");
                    }
                    pw.println();
                    printWriter.println("mWifiLogProto.SoftApManagerReturnCodeCounts:");
                    printWriter.println("  SUCCESS: " + this.mSoftApManagerReturnCodeCounts.get(1));
                    printWriter.println("  FAILED_GENERAL_ERROR: " + this.mSoftApManagerReturnCodeCounts.get(2));
                    printWriter.println("  FAILED_NO_CHANNEL: " + this.mSoftApManagerReturnCodeCounts.get(3));
                    printWriter.print("\n");
                    printWriter.println("mWifiLogProto.numHalCrashes=" + this.mWifiLogProto.numHalCrashes);
                    printWriter.println("mWifiLogProto.numWificondCrashes=" + this.mWifiLogProto.numWificondCrashes);
                    printWriter.println("mWifiLogProto.numSupplicantCrashes=" + this.mWifiLogProto.numSupplicantCrashes);
                    printWriter.println("mWifiLogProto.numHostapdCrashes=" + this.mWifiLogProto.numHostapdCrashes);
                    printWriter.println("mWifiLogProto.numSetupClientInterfaceFailureDueToHal=" + this.mWifiLogProto.numSetupClientInterfaceFailureDueToHal);
                    printWriter.println("mWifiLogProto.numSetupClientInterfaceFailureDueToWificond=" + this.mWifiLogProto.numSetupClientInterfaceFailureDueToWificond);
                    printWriter.println("mWifiLogProto.numSetupClientInterfaceFailureDueToSupplicant=" + this.mWifiLogProto.numSetupClientInterfaceFailureDueToSupplicant);
                    printWriter.println("mWifiLogProto.numSetupSoftApInterfaceFailureDueToHal=" + this.mWifiLogProto.numSetupSoftApInterfaceFailureDueToHal);
                    printWriter.println("mWifiLogProto.numSetupSoftApInterfaceFailureDueToWificond=" + this.mWifiLogProto.numSetupSoftApInterfaceFailureDueToWificond);
                    printWriter.println("mWifiLogProto.numSetupSoftApInterfaceFailureDueToHostapd=" + this.mWifiLogProto.numSetupSoftApInterfaceFailureDueToHostapd);
                    printWriter.println("mWifiLogProto.numSarSensorRegistrationFailures=" + this.mWifiLogProto.numSarSensorRegistrationFailures);
                    printWriter.println("StaEventList:");
                    Iterator it = this.mStaEventList.iterator();
                    while (it.hasNext()) {
                        printWriter.println((StaEventWithTime) it.next());
                    }
                    printWriter.println("mWifiLogProto.numPasspointProviders=" + this.mWifiLogProto.numPasspointProviders);
                    printWriter.println("mWifiLogProto.numPasspointProviderInstallation=" + this.mWifiLogProto.numPasspointProviderInstallation);
                    printWriter.println("mWifiLogProto.numPasspointProviderInstallSuccess=" + this.mWifiLogProto.numPasspointProviderInstallSuccess);
                    printWriter.println("mWifiLogProto.numPasspointProviderUninstallation=" + this.mWifiLogProto.numPasspointProviderUninstallation);
                    printWriter.println("mWifiLogProto.numPasspointProviderUninstallSuccess=" + this.mWifiLogProto.numPasspointProviderUninstallSuccess);
                    printWriter.println("mWifiLogProto.numPasspointProvidersSuccessfullyConnected=" + this.mWifiLogProto.numPasspointProvidersSuccessfullyConnected);
                    printWriter.println("mWifiLogProto.installedPasspointProfileTypeForR1:" + this.mInstalledPasspointProfileTypeForR1);
                    printWriter.println("mWifiLogProto.installedPasspointProfileTypeForR2:" + this.mInstalledPasspointProfileTypeForR2);
                    printWriter.println("mWifiLogProto.passpointProvisionStats.numProvisionSuccess=" + this.mNumProvisionSuccess);
                    printWriter.println("mWifiLogProto.passpointProvisionStats.provisionFailureCount:" + this.mPasspointProvisionFailureCounts);
                    printWriter.println("mWifiLogProto.numRadioModeChangeToMcc=" + this.mWifiLogProto.numRadioModeChangeToMcc);
                    printWriter.println("mWifiLogProto.numRadioModeChangeToScc=" + this.mWifiLogProto.numRadioModeChangeToScc);
                    printWriter.println("mWifiLogProto.numRadioModeChangeToSbs=" + this.mWifiLogProto.numRadioModeChangeToSbs);
                    printWriter.println("mWifiLogProto.numRadioModeChangeToDbs=" + this.mWifiLogProto.numRadioModeChangeToDbs);
                    printWriter.println("mWifiLogProto.numSoftApUserBandPreferenceUnsatisfied=" + this.mWifiLogProto.numSoftApUserBandPreferenceUnsatisfied);
                    printWriter.println("mTotalSsidsInScanHistogram:" + this.mTotalSsidsInScanHistogram.toString());
                    printWriter.println("mTotalBssidsInScanHistogram:" + this.mTotalBssidsInScanHistogram.toString());
                    printWriter.println("mAvailableOpenSsidsInScanHistogram:" + this.mAvailableOpenSsidsInScanHistogram.toString());
                    printWriter.println("mAvailableOpenBssidsInScanHistogram:" + this.mAvailableOpenBssidsInScanHistogram.toString());
                    printWriter.println("mAvailableSavedSsidsInScanHistogram:" + this.mAvailableSavedSsidsInScanHistogram.toString());
                    printWriter.println("mAvailableSavedBssidsInScanHistogram:" + this.mAvailableSavedBssidsInScanHistogram.toString());
                    printWriter.println("mAvailableOpenOrSavedSsidsInScanHistogram:" + this.mAvailableOpenOrSavedSsidsInScanHistogram.toString());
                    printWriter.println("mAvailableOpenOrSavedBssidsInScanHistogram:" + this.mAvailableOpenOrSavedBssidsInScanHistogram.toString());
                    printWriter.println("mAvailableSavedPasspointProviderProfilesInScanHistogram:" + this.mAvailableSavedPasspointProviderProfilesInScanHistogram.toString());
                    printWriter.println("mAvailableSavedPasspointProviderBssidsInScanHistogram:" + this.mAvailableSavedPasspointProviderBssidsInScanHistogram.toString());
                    printWriter.println("mWifiLogProto.partialAllSingleScanListenerResults=" + this.mWifiLogProto.partialAllSingleScanListenerResults);
                    printWriter.println("mWifiLogProto.fullBandAllSingleScanListenerResults=" + this.mWifiLogProto.fullBandAllSingleScanListenerResults);
                    printWriter.println("mWifiAwareMetrics:");
                    this.mWifiAwareMetrics.dump(fileDescriptor, printWriter, strArr);
                    printWriter.println("mRttMetrics:");
                    this.mRttMetrics.dump(fileDescriptor, printWriter, strArr);
                    printWriter.println("mPnoScanMetrics.numPnoScanAttempts=" + this.mPnoScanMetrics.numPnoScanAttempts);
                    printWriter.println("mPnoScanMetrics.numPnoScanFailed=" + this.mPnoScanMetrics.numPnoScanFailed);
                    printWriter.println("mPnoScanMetrics.numPnoScanStartedOverOffload=" + this.mPnoScanMetrics.numPnoScanStartedOverOffload);
                    printWriter.println("mPnoScanMetrics.numPnoScanFailedOverOffload=" + this.mPnoScanMetrics.numPnoScanFailedOverOffload);
                    printWriter.println("mPnoScanMetrics.numPnoFoundNetworkEvents=" + this.mPnoScanMetrics.numPnoFoundNetworkEvents);
                    printWriter.println("mWifiLinkLayerUsageStats.loggingDurationMs=" + this.mWifiLinkLayerUsageStats.loggingDurationMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioOnTimeMs=" + this.mWifiLinkLayerUsageStats.radioOnTimeMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioTxTimeMs=" + this.mWifiLinkLayerUsageStats.radioTxTimeMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioRxTimeMs=" + this.mWifiLinkLayerUsageStats.radioRxTimeMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioScanTimeMs=" + this.mWifiLinkLayerUsageStats.radioScanTimeMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioNanScanTimeMs=" + this.mWifiLinkLayerUsageStats.radioNanScanTimeMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioBackgroundScanTimeMs=" + this.mWifiLinkLayerUsageStats.radioBackgroundScanTimeMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioRoamScanTimeMs=" + this.mWifiLinkLayerUsageStats.radioRoamScanTimeMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioPnoScanTimeMs=" + this.mWifiLinkLayerUsageStats.radioPnoScanTimeMs);
                    printWriter.println("mWifiLinkLayerUsageStats.radioHs20ScanTimeMs=" + this.mWifiLinkLayerUsageStats.radioHs20ScanTimeMs);
                    printWriter.println("mWifiLogProto.connectToNetworkNotificationCount=" + this.mConnectToNetworkNotificationCount.toString());
                    printWriter.println("mWifiLogProto.connectToNetworkNotificationActionCount=" + this.mConnectToNetworkNotificationActionCount.toString());
                    printWriter.println("mWifiLogProto.openNetworkRecommenderBlacklistSize=" + this.mOpenNetworkRecommenderBlacklistSize);
                    printWriter.println("mWifiLogProto.isWifiNetworksAvailableNotificationOn=" + this.mIsWifiNetworksAvailableNotificationOn);
                    printWriter.println("mWifiLogProto.numOpenNetworkRecommendationUpdates=" + this.mNumOpenNetworkRecommendationUpdates);
                    printWriter.println("mWifiLogProto.numOpenNetworkConnectMessageFailedToSend=" + this.mNumOpenNetworkConnectMessageFailedToSend);
                    printWriter.println("mWifiLogProto.observedHotspotR1ApInScanHistogram=" + this.mObservedHotspotR1ApInScanHistogram);
                    printWriter.println("mWifiLogProto.observedHotspotR2ApInScanHistogram=" + this.mObservedHotspotR2ApInScanHistogram);
                    printWriter.println("mWifiLogProto.observedHotspotR1EssInScanHistogram=" + this.mObservedHotspotR1EssInScanHistogram);
                    printWriter.println("mWifiLogProto.observedHotspotR2EssInScanHistogram=" + this.mObservedHotspotR2EssInScanHistogram);
                    printWriter.println("mWifiLogProto.observedHotspotR1ApsPerEssInScanHistogram=" + this.mObservedHotspotR1ApsPerEssInScanHistogram);
                    printWriter.println("mWifiLogProto.observedHotspotR2ApsPerEssInScanHistogram=" + this.mObservedHotspotR2ApsPerEssInScanHistogram);
                    printWriter.println("mWifiLogProto.observed80211mcSupportingApsInScanHistogram" + this.mObserved80211mcApInScanHistogram);
                    printWriter.println("mSoftApTetheredEvents:");
                    for (WifiMetricsProto.SoftApConnectedClientsEvent event2 : this.mSoftApEventListTethered) {
                        StringBuilder eventLine3 = new StringBuilder();
                        eventLine3.append("event_type=" + event2.eventType);
                        eventLine3.append(",time_stamp_millis=" + event2.timeStampMillis);
                        eventLine3.append(",num_connected_clients=" + event2.numConnectedClients);
                        eventLine3.append(",channel_frequency=" + event2.channelFrequency);
                        eventLine3.append(",channel_bandwidth=" + event2.channelBandwidth);
                        printWriter.println(eventLine3.toString());
                    }
                    printWriter.println("mSoftApLocalOnlyEvents:");
                    for (WifiMetricsProto.SoftApConnectedClientsEvent event3 : this.mSoftApEventListLocalOnly) {
                        StringBuilder eventLine4 = new StringBuilder();
                        eventLine4.append("event_type=" + event3.eventType);
                        eventLine4.append(",time_stamp_millis=" + event3.timeStampMillis);
                        eventLine4.append(",num_connected_clients=" + event3.numConnectedClients);
                        eventLine4.append(",channel_frequency=" + event3.channelFrequency);
                        eventLine4.append(",channel_bandwidth=" + event3.channelBandwidth);
                        printWriter.println(eventLine4.toString());
                    }
                    printWriter.println("mWpsMetrics.numWpsAttempts=" + this.mWpsMetrics.numWpsAttempts);
                    printWriter.println("mWpsMetrics.numWpsSuccess=" + this.mWpsMetrics.numWpsSuccess);
                    printWriter.println("mWpsMetrics.numWpsStartFailure=" + this.mWpsMetrics.numWpsStartFailure);
                    printWriter.println("mWpsMetrics.numWpsOverlapFailure=" + this.mWpsMetrics.numWpsOverlapFailure);
                    printWriter.println("mWpsMetrics.numWpsTimeoutFailure=" + this.mWpsMetrics.numWpsTimeoutFailure);
                    printWriter.println("mWpsMetrics.numWpsOtherConnectionFailure=" + this.mWpsMetrics.numWpsOtherConnectionFailure);
                    printWriter.println("mWpsMetrics.numWpsSupplicantFailure=" + this.mWpsMetrics.numWpsSupplicantFailure);
                    printWriter.println("mWpsMetrics.numWpsCancellation=" + this.mWpsMetrics.numWpsCancellation);
                    this.mWifiPowerMetrics.dump(printWriter);
                    this.mWifiWakeMetrics.dump(printWriter);
                    printWriter.println("mWifiLogProto.isMacRandomizationOn=" + this.mIsMacRandomizationOn);
                    printWriter.println("mWifiLogProto.scoreExperimentId=" + this.mWifiLogProto.scoreExperimentId);
                    printWriter.println("mExperimentValues.wifiIsUnusableLoggingEnabled=" + this.mExperimentValues.wifiIsUnusableLoggingEnabled);
                    printWriter.println("mExperimentValues.wifiDataStallMinTxBad=" + this.mExperimentValues.wifiDataStallMinTxBad);
                    printWriter.println("mExperimentValues.wifiDataStallMinTxSuccessWithoutRx=" + this.mExperimentValues.wifiDataStallMinTxSuccessWithoutRx);
                    printWriter.println("mExperimentValues.linkSpeedCountsLoggingEnabled=" + this.mExperimentValues.linkSpeedCountsLoggingEnabled);
                    printWriter.println("WifiIsUnusableEventList: ");
                    Iterator it2 = this.mWifiIsUnusableList.iterator();
                    while (it2.hasNext()) {
                        printWriter.println((WifiIsUnusableWithTime) it2.next());
                    }
                    printWriter.println("Hardware Version: " + SystemProperties.get("ro.boot.revision", Prefix.EMPTY));
                    printWriter.println("mWifiUsabilityStatsEntriesList:");
                    Iterator it3 = this.mWifiUsabilityStatsEntriesList.iterator();
                    while (it3.hasNext()) {
                        printWifiUsabilityStatsEntry(printWriter, (WifiMetricsProto.WifiUsabilityStatsEntry) it3.next());
                    }
                    printWriter.println("mWifiUsabilityStatsList:");
                    Iterator it4 = this.mWifiUsabilityStatsListGood.iterator();
                    while (it4.hasNext()) {
                        WifiMetricsProto.WifiUsabilityStats stats = (WifiMetricsProto.WifiUsabilityStats) it4.next();
                        printWriter.println("\nlabel=" + stats.label);
                        printWriter.println("\ntrigger_type=" + stats.triggerType);
                        printWriter.println("\ntime_stamp_ms=" + stats.timeStampMs);
                        for (WifiMetricsProto.WifiUsabilityStatsEntry entry2 : stats.stats) {
                            printWifiUsabilityStatsEntry(printWriter, entry2);
                        }
                    }
                    Iterator it5 = this.mWifiUsabilityStatsListBad.iterator();
                    while (it5.hasNext()) {
                        WifiMetricsProto.WifiUsabilityStats stats2 = (WifiMetricsProto.WifiUsabilityStats) it5.next();
                        printWriter.println("\nlabel=" + stats2.label);
                        printWriter.println("\ntrigger_type=" + stats2.triggerType);
                        printWriter.println("\ntime_stamp_ms=" + stats2.timeStampMs);
                        for (WifiMetricsProto.WifiUsabilityStatsEntry entry3 : stats2.stats) {
                            printWifiUsabilityStatsEntry(printWriter, entry3);
                        }
                    }
                    printWriter.println("mMobilityStatePnoStatsMap:");
                    for (int i7 = 0; i7 < this.mMobilityStatePnoStatsMap.size(); i7++) {
                        printDeviceMobilityStatePnoScanStats(printWriter, this.mMobilityStatePnoStatsMap.valueAt(i7));
                    }
                    this.mWifiP2pMetrics.dump(printWriter);
                    printWriter.println("mDppMetrics:");
                    this.mDppMetrics.dump(printWriter);
                    printWriter.println("mWifiConfigStoreReadDurationHistogram:" + this.mWifiConfigStoreReadDurationHistogram.toString());
                    printWriter.println("mWifiConfigStoreWriteDurationHistogram:" + this.mWifiConfigStoreWriteDurationHistogram.toString());
                    printWriter.println("mLinkProbeSuccessRssiCounts:" + this.mLinkProbeSuccessRssiCounts);
                    printWriter.println("mLinkProbeFailureRssiCounts:" + this.mLinkProbeFailureRssiCounts);
                    printWriter.println("mLinkProbeSuccessLinkSpeedCounts:" + this.mLinkProbeSuccessLinkSpeedCounts);
                    printWriter.println("mLinkProbeFailureLinkSpeedCounts:" + this.mLinkProbeFailureLinkSpeedCounts);
                    printWriter.println("mLinkProbeSuccessSecondsSinceLastTxSuccessHistogram:" + this.mLinkProbeSuccessSecondsSinceLastTxSuccessHistogram);
                    printWriter.println("mLinkProbeFailureSecondsSinceLastTxSuccessHistogram:" + this.mLinkProbeFailureSecondsSinceLastTxSuccessHistogram);
                    printWriter.println("mLinkProbeSuccessElapsedTimeMsHistogram:" + this.mLinkProbeSuccessElapsedTimeMsHistogram);
                    printWriter.println("mLinkProbeFailureReasonCounts:" + this.mLinkProbeFailureReasonCounts);
                    printWriter.println("mLinkProbeExperimentProbeCounts:" + this.mLinkProbeExperimentProbeCounts);
                    printWriter.println("mNetworkSelectionExperimentPairNumChoicesCounts:" + this.mNetworkSelectionExperimentPairNumChoicesCounts);
                    printWriter.println("mLinkProbeStaEventCount:" + this.mLinkProbeStaEventCount);
                    printWriter.println("mWifiNetworkRequestApiLog:\n" + this.mWifiNetworkRequestApiLog);
                    printWriter.println("mWifiNetworkRequestApiMatchSizeHistogram:\n" + this.mWifiNetworkRequestApiMatchSizeHistogram);
                    printWriter.println("mWifiNetworkSuggestionApiLog:\n" + this.mWifiNetworkSuggestionApiLog);
                    printWriter.println("mWifiNetworkSuggestionApiMatchSizeHistogram:\n" + this.mWifiNetworkRequestApiMatchSizeHistogram);
                    printWriter.println("mNetworkIdToNominatorId:\n" + this.mNetworkIdToNominatorId);
                    printWriter.println("mWifiLockStats:\n" + this.mWifiLockStats);
                    printWriter.println("mWifiLockHighPerfAcqDurationSecHistogram:\n" + this.mWifiLockHighPerfAcqDurationSecHistogram);
                    printWriter.println("mWifiLockLowLatencyAcqDurationSecHistogram:\n" + this.mWifiLockLowLatencyAcqDurationSecHistogram);
                    printWriter.println("mWifiLockHighPerfActiveSessionDurationSecHistogram:\n" + this.mWifiLockHighPerfActiveSessionDurationSecHistogram);
                    printWriter.println("mWifiLockLowLatencyActiveSessionDurationSecHistogram:\n" + this.mWifiLockLowLatencyActiveSessionDurationSecHistogram);
                    printWriter.println("mWifiToggleStats:\n" + this.mWifiToggleStats);
                    printWriter.println("mWifiLogProto.numAddOrUpdateNetworkCalls=" + this.mWifiLogProto.numAddOrUpdateNetworkCalls);
                    printWriter.println("mWifiLogProto.numEnableNetworkCalls=" + this.mWifiLogProto.numEnableNetworkCalls);
                } else {
                    consolidateProto();
                    String metricsProtoDump = Base64.encodeToString(WifiMetricsProto.WifiLog.toByteArray(this.mWifiLogProto), 0);
                    if (strArr.length <= 1 || !CLEAN_DUMP_ARG.equals(strArr[1])) {
                        printWriter.println("WifiMetrics:");
                        printWriter.println(metricsProtoDump);
                        printWriter.println("EndWifiMetrics");
                    } else {
                        printWriter.print(metricsProtoDump);
                    }
                    clear();
                }
            } catch (JSONException e) {
                printWriter.println("JSONException occurred: " + eventLine.getMessage());
            } finally {
                eventLine = e;
            }
        }
    }

    private void printWifiUsabilityStatsEntry(PrintWriter pw, WifiMetricsProto.WifiUsabilityStatsEntry entry) {
        StringBuilder line = new StringBuilder();
        line.append("timestamp_ms=" + entry.timeStampMs);
        line.append(",rssi=" + entry.rssi);
        line.append(",link_speed_mbps=" + entry.linkSpeedMbps);
        line.append(",total_tx_success=" + entry.totalTxSuccess);
        line.append(",total_tx_retries=" + entry.totalTxRetries);
        line.append(",total_tx_bad=" + entry.totalTxBad);
        line.append(",total_rx_success=" + entry.totalRxSuccess);
        line.append(",total_radio_on_time_ms=" + entry.totalRadioOnTimeMs);
        line.append(",total_radio_tx_time_ms=" + entry.totalRadioTxTimeMs);
        line.append(",total_radio_rx_time_ms=" + entry.totalRadioRxTimeMs);
        line.append(",total_scan_time_ms=" + entry.totalScanTimeMs);
        line.append(",total_nan_scan_time_ms=" + entry.totalNanScanTimeMs);
        line.append(",total_background_scan_time_ms=" + entry.totalBackgroundScanTimeMs);
        line.append(",total_roam_scan_time_ms=" + entry.totalRoamScanTimeMs);
        line.append(",total_pno_scan_time_ms=" + entry.totalPnoScanTimeMs);
        line.append(",total_hotspot_2_scan_time_ms=" + entry.totalHotspot2ScanTimeMs);
        line.append(",wifi_score=" + entry.wifiScore);
        line.append(",wifi_usability_score=" + entry.wifiUsabilityScore);
        line.append(",seq_num_to_framework=" + entry.seqNumToFramework);
        line.append(",prediction_horizon_sec=" + entry.predictionHorizonSec);
        line.append(",total_cca_busy_freq_time_ms=" + entry.totalCcaBusyFreqTimeMs);
        line.append(",total_radio_on_freq_time_ms=" + entry.totalRadioOnFreqTimeMs);
        line.append(",total_beacon_rx=" + entry.totalBeaconRx);
        line.append(",probe_status_since_last_update=" + entry.probeStatusSinceLastUpdate);
        line.append(",probe_elapsed_time_ms_since_last_update=" + entry.probeElapsedTimeSinceLastUpdateMs);
        line.append(",probe_mcs_rate_since_last_update=" + entry.probeMcsRateSinceLastUpdate);
        line.append(",rx_link_speed_mbps=" + entry.rxLinkSpeedMbps);
        line.append(",seq_num_inside_framework=" + entry.seqNumInsideFramework);
        line.append(",is_same_bssid_and_freq=" + entry.isSameBssidAndFreq);
        line.append(",cellular_data_network_type=" + entry.cellularDataNetworkType);
        line.append(",cellular_signal_strength_dbm=" + entry.cellularSignalStrengthDbm);
        line.append(",cellular_signal_strength_db=" + entry.cellularSignalStrengthDb);
        line.append(",is_same_registered_cell=" + entry.isSameRegisteredCell);
        line.append(",device_mobility_state=" + entry.deviceMobilityState);
        pw.println(line.toString());
    }

    private void printDeviceMobilityStatePnoScanStats(PrintWriter pw, WifiMetricsProto.DeviceMobilityStatePnoScanStats stats) {
        StringBuilder line = new StringBuilder();
        line.append("device_mobility_state=" + stats.deviceMobilityState);
        line.append(",num_times_entered_state=" + stats.numTimesEnteredState);
        line.append(",total_duration_ms=" + stats.totalDurationMs);
        line.append(",pno_duration_ms=" + stats.pnoDurationMs);
        pw.println(line.toString());
    }

    public void updateSavedNetworks(List<WifiConfiguration> networks) {
        synchronized (this.mLock) {
            this.mWifiLogProto.numSavedNetworks = networks.size();
            this.mWifiLogProto.numOpenNetworks = 0;
            this.mWifiLogProto.numLegacyPersonalNetworks = 0;
            this.mWifiLogProto.numLegacyEnterpriseNetworks = 0;
            this.mWifiLogProto.numEnhancedOpenNetworks = 0;
            this.mWifiLogProto.numWpa3PersonalNetworks = 0;
            this.mWifiLogProto.numWpa3EnterpriseNetworks = 0;
            this.mWifiLogProto.numNetworksAddedByUser = 0;
            this.mWifiLogProto.numNetworksAddedByApps = 0;
            this.mWifiLogProto.numHiddenNetworks = 0;
            this.mWifiLogProto.numPasspointNetworks = 0;
            for (WifiConfiguration config : networks) {
                if (config.allowedKeyManagement.get(0)) {
                    this.mWifiLogProto.numOpenNetworks++;
                } else if (config.allowedKeyManagement.get(9)) {
                    this.mWifiLogProto.numEnhancedOpenNetworks++;
                } else if (config.isEnterprise()) {
                    if (config.allowedKeyManagement.get(10)) {
                        this.mWifiLogProto.numWpa3EnterpriseNetworks++;
                    } else {
                        this.mWifiLogProto.numLegacyEnterpriseNetworks++;
                    }
                } else if (config.allowedKeyManagement.get(8)) {
                    this.mWifiLogProto.numWpa3PersonalNetworks++;
                } else {
                    this.mWifiLogProto.numLegacyPersonalNetworks++;
                }
                if (config.selfAdded) {
                    this.mWifiLogProto.numNetworksAddedByUser++;
                } else {
                    this.mWifiLogProto.numNetworksAddedByApps++;
                }
                if (config.hiddenSSID) {
                    this.mWifiLogProto.numHiddenNetworks++;
                }
                if (config.isPasspoint()) {
                    this.mWifiLogProto.numPasspointNetworks++;
                }
                if (config.macRandomizationSetting == 1) {
                    this.mWifiLogProto.numSavedNetworksWithMacRandomization++;
                }
            }
        }
    }

    public void updateSavedPasspointProfiles(int numSavedProfiles, int numConnectedProfiles) {
        synchronized (this.mLock) {
            this.mWifiLogProto.numPasspointProviders = numSavedProfiles;
            this.mWifiLogProto.numPasspointProvidersSuccessfullyConnected = numConnectedProfiles;
        }
    }

    public void updateSavedPasspointProfilesInfo(Map<String, PasspointProvider> providers) {
        int eapType;
        int passpointType;
        synchronized (this.mLock) {
            this.mInstalledPasspointProfileTypeForR1.clear();
            this.mInstalledPasspointProfileTypeForR2.clear();
            for (Map.Entry<String, PasspointProvider> entry : providers.entrySet()) {
                PasspointConfiguration config = entry.getValue().getConfig();
                if (config.getCredential().getUserCredential() != null) {
                    eapType = 21;
                } else if (config.getCredential().getCertCredential() != null) {
                    eapType = 13;
                } else if (config.getCredential().getSimCredential() != null) {
                    eapType = config.getCredential().getSimCredential().getEapType();
                } else {
                    eapType = -1;
                }
                if (eapType == 13) {
                    passpointType = 1;
                } else if (eapType == 18) {
                    passpointType = 3;
                } else if (eapType == 21) {
                    passpointType = 2;
                } else if (eapType == 23) {
                    passpointType = 4;
                } else if (eapType != 50) {
                    passpointType = 0;
                } else {
                    passpointType = 5;
                }
                if (config.validateForR2()) {
                    this.mInstalledPasspointProfileTypeForR2.increment(passpointType);
                } else {
                    this.mInstalledPasspointProfileTypeForR1.increment(passpointType);
                }
            }
        }
    }

    private void consolidateProto() {
        List<WifiMetricsProto.RssiPollCount> rssis = new ArrayList<>();
        synchronized (this.mLock) {
            try {
                int connectionEventCount = this.mConnectionEventList.size();
                if (this.mCurrentConnectionEvent != null) {
                    connectionEventCount--;
                }
                this.mWifiLogProto.connectionEvent = new WifiMetricsProto.ConnectionEvent[connectionEventCount];
                for (int i = 0; i < connectionEventCount; i++) {
                    this.mWifiLogProto.connectionEvent[i] = this.mConnectionEventList.get(i).mConnectionEvent;
                }
                this.mWifiLogProto.scanReturnEntries = new WifiMetricsProto.WifiLog.ScanReturnEntry[this.mScanReturnEntries.size()];
                th = false;
                for (int i2 = th; i2 < this.mScanReturnEntries.size(); i2++) {
                    this.mWifiLogProto.scanReturnEntries[i2] = new WifiMetricsProto.WifiLog.ScanReturnEntry();
                    this.mWifiLogProto.scanReturnEntries[i2].scanReturnCode = this.mScanReturnEntries.keyAt(i2);
                    this.mWifiLogProto.scanReturnEntries[i2].scanResultsCount = this.mScanReturnEntries.valueAt(i2);
                }
                this.mWifiLogProto.wifiSystemStateEntries = new WifiMetricsProto.WifiLog.WifiSystemStateEntry[this.mWifiSystemStateEntries.size()];
                int i3 = th;
                while (true) {
                    boolean z = true;
                    if (i3 >= this.mWifiSystemStateEntries.size()) {
                        break;
                    }
                    this.mWifiLogProto.wifiSystemStateEntries[i3] = new WifiMetricsProto.WifiLog.WifiSystemStateEntry();
                    this.mWifiLogProto.wifiSystemStateEntries[i3].wifiState = this.mWifiSystemStateEntries.keyAt(i3) / 2;
                    this.mWifiLogProto.wifiSystemStateEntries[i3].wifiStateCount = this.mWifiSystemStateEntries.valueAt(i3);
                    WifiMetricsProto.WifiLog.WifiSystemStateEntry wifiSystemStateEntry = this.mWifiLogProto.wifiSystemStateEntries[i3];
                    if (this.mWifiSystemStateEntries.keyAt(i3) % 2 > 0) {
                    }
                    wifiSystemStateEntry.isScreenOn = z;
                    i3++;
                }
                this.mWifiLogProto.recordDurationSec = (int) ((this.mClock.getElapsedSinceBootMillis() / 1000) - this.mRecordStartTimeSec);
                for (Map.Entry<Integer, SparseIntArray> entry : this.mRssiPollCountsMap.entrySet()) {
                    int frequency = entry.getKey().intValue();
                    SparseIntArray histogram = entry.getValue();
                    for (int i4 = th; i4 < histogram.size(); i4++) {
                        WifiMetricsProto.RssiPollCount keyVal = new WifiMetricsProto.RssiPollCount();
                        keyVal.rssi = histogram.keyAt(i4);
                        keyVal.count = histogram.valueAt(i4);
                        keyVal.frequency = frequency;
                        rssis.add(keyVal);
                    }
                }
                this.mWifiLogProto.rssiPollRssiCount = (WifiMetricsProto.RssiPollCount[]) rssis.toArray(this.mWifiLogProto.rssiPollRssiCount);
                this.mWifiLogProto.rssiPollDeltaCount = new WifiMetricsProto.RssiPollCount[this.mRssiDeltaCounts.size()];
                for (int i5 = th; i5 < this.mRssiDeltaCounts.size(); i5++) {
                    this.mWifiLogProto.rssiPollDeltaCount[i5] = new WifiMetricsProto.RssiPollCount();
                    this.mWifiLogProto.rssiPollDeltaCount[i5].rssi = this.mRssiDeltaCounts.keyAt(i5);
                    this.mWifiLogProto.rssiPollDeltaCount[i5].count = this.mRssiDeltaCounts.valueAt(i5);
                }
                this.mWifiLogProto.linkSpeedCounts = new WifiMetricsProto.LinkSpeedCount[this.mLinkSpeedCounts.size()];
                for (int i6 = th; i6 < this.mLinkSpeedCounts.size(); i6++) {
                    this.mWifiLogProto.linkSpeedCounts[i6] = this.mLinkSpeedCounts.valueAt(i6);
                }
                this.mWifiLogProto.alertReasonCount = new WifiMetricsProto.AlertReasonCount[this.mWifiAlertReasonCounts.size()];
                for (int i7 = th; i7 < this.mWifiAlertReasonCounts.size(); i7++) {
                    this.mWifiLogProto.alertReasonCount[i7] = new WifiMetricsProto.AlertReasonCount();
                    this.mWifiLogProto.alertReasonCount[i7].reason = this.mWifiAlertReasonCounts.keyAt(i7);
                    this.mWifiLogProto.alertReasonCount[i7].count = this.mWifiAlertReasonCounts.valueAt(i7);
                }
                this.mWifiLogProto.wifiScoreCount = new WifiMetricsProto.WifiScoreCount[this.mWifiScoreCounts.size()];
                for (int score = th; score < this.mWifiScoreCounts.size(); score++) {
                    this.mWifiLogProto.wifiScoreCount[score] = new WifiMetricsProto.WifiScoreCount();
                    this.mWifiLogProto.wifiScoreCount[score].score = this.mWifiScoreCounts.keyAt(score);
                    this.mWifiLogProto.wifiScoreCount[score].count = this.mWifiScoreCounts.valueAt(score);
                }
                this.mWifiLogProto.wifiUsabilityScoreCount = new WifiMetricsProto.WifiUsabilityScoreCount[this.mWifiUsabilityScoreCounts.size()];
                for (int scoreIdx = th; scoreIdx < this.mWifiUsabilityScoreCounts.size(); scoreIdx++) {
                    this.mWifiLogProto.wifiUsabilityScoreCount[scoreIdx] = new WifiMetricsProto.WifiUsabilityScoreCount();
                    this.mWifiLogProto.wifiUsabilityScoreCount[scoreIdx].score = this.mWifiUsabilityScoreCounts.keyAt(scoreIdx);
                    this.mWifiLogProto.wifiUsabilityScoreCount[scoreIdx].count = this.mWifiUsabilityScoreCounts.valueAt(scoreIdx);
                }
                int codeCounts = this.mSoftApManagerReturnCodeCounts.size();
                this.mWifiLogProto.softApReturnCode = new WifiMetricsProto.SoftApReturnCodeCount[codeCounts];
                for (int sapCode = 0; sapCode < codeCounts; sapCode++) {
                    this.mWifiLogProto.softApReturnCode[sapCode] = new WifiMetricsProto.SoftApReturnCodeCount();
                    this.mWifiLogProto.softApReturnCode[sapCode].startResult = this.mSoftApManagerReturnCodeCounts.keyAt(sapCode);
                    this.mWifiLogProto.softApReturnCode[sapCode].count = this.mSoftApManagerReturnCodeCounts.valueAt(sapCode);
                }
                this.mWifiLogProto.staEventList = new WifiMetricsProto.StaEvent[this.mStaEventList.size()];
                for (int i8 = th; i8 < this.mStaEventList.size(); i8++) {
                    this.mWifiLogProto.staEventList[i8] = this.mStaEventList.get(i8).staEvent;
                }
                this.mWifiLogProto.totalSsidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mTotalSsidsInScanHistogram);
                this.mWifiLogProto.totalBssidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mTotalBssidsInScanHistogram);
                this.mWifiLogProto.availableOpenSsidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mAvailableOpenSsidsInScanHistogram);
                this.mWifiLogProto.availableOpenBssidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mAvailableOpenBssidsInScanHistogram);
                this.mWifiLogProto.availableSavedSsidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mAvailableSavedSsidsInScanHistogram);
                this.mWifiLogProto.availableSavedBssidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mAvailableSavedBssidsInScanHistogram);
                this.mWifiLogProto.availableOpenOrSavedSsidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mAvailableOpenOrSavedSsidsInScanHistogram);
                this.mWifiLogProto.availableOpenOrSavedBssidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mAvailableOpenOrSavedBssidsInScanHistogram);
                this.mWifiLogProto.availableSavedPasspointProviderProfilesInScanHistogram = makeNumConnectableNetworksBucketArray(this.mAvailableSavedPasspointProviderProfilesInScanHistogram);
                this.mWifiLogProto.availableSavedPasspointProviderBssidsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mAvailableSavedPasspointProviderBssidsInScanHistogram);
                this.mWifiLogProto.wifiAwareLog = this.mWifiAwareMetrics.consolidateProto();
                this.mWifiLogProto.wifiRttLog = this.mRttMetrics.consolidateProto();
                this.mWifiLogProto.pnoScanMetrics = this.mPnoScanMetrics;
                this.mWifiLogProto.wifiLinkLayerUsageStats = this.mWifiLinkLayerUsageStats;
                WifiMetricsProto.ConnectToNetworkNotificationAndActionCount[] notificationCountArray = new WifiMetricsProto.ConnectToNetworkNotificationAndActionCount[this.mConnectToNetworkNotificationCount.size()];
                for (int i9 = th; i9 < this.mConnectToNetworkNotificationCount.size(); i9++) {
                    WifiMetricsProto.ConnectToNetworkNotificationAndActionCount keyVal2 = new WifiMetricsProto.ConnectToNetworkNotificationAndActionCount();
                    keyVal2.notification = this.mConnectToNetworkNotificationCount.keyAt(i9);
                    keyVal2.recommender = 1;
                    keyVal2.count = this.mConnectToNetworkNotificationCount.valueAt(i9);
                    notificationCountArray[i9] = keyVal2;
                }
                this.mWifiLogProto.connectToNetworkNotificationCount = notificationCountArray;
                WifiMetricsProto.ConnectToNetworkNotificationAndActionCount[] notificationActionCountArray = new WifiMetricsProto.ConnectToNetworkNotificationAndActionCount[this.mConnectToNetworkNotificationActionCount.size()];
                for (int i10 = th; i10 < this.mConnectToNetworkNotificationActionCount.size(); i10++) {
                    WifiMetricsProto.ConnectToNetworkNotificationAndActionCount keyVal3 = new WifiMetricsProto.ConnectToNetworkNotificationAndActionCount();
                    int key = this.mConnectToNetworkNotificationActionCount.keyAt(i10);
                    keyVal3.notification = key / 1000;
                    keyVal3.action = key % 1000;
                    keyVal3.recommender = 1;
                    keyVal3.count = this.mConnectToNetworkNotificationActionCount.valueAt(i10);
                    notificationActionCountArray[i10] = keyVal3;
                }
                this.mWifiLogProto.installedPasspointProfileTypeForR1 = convertPasspointProfilesToProto(this.mInstalledPasspointProfileTypeForR1);
                this.mWifiLogProto.installedPasspointProfileTypeForR2 = convertPasspointProfilesToProto(this.mInstalledPasspointProfileTypeForR2);
                this.mWifiLogProto.connectToNetworkNotificationActionCount = notificationActionCountArray;
                this.mWifiLogProto.openNetworkRecommenderBlacklistSize = this.mOpenNetworkRecommenderBlacklistSize;
                this.mWifiLogProto.isWifiNetworksAvailableNotificationOn = this.mIsWifiNetworksAvailableNotificationOn;
                this.mWifiLogProto.numOpenNetworkRecommendationUpdates = this.mNumOpenNetworkRecommendationUpdates;
                this.mWifiLogProto.numOpenNetworkConnectMessageFailedToSend = this.mNumOpenNetworkConnectMessageFailedToSend;
                this.mWifiLogProto.observedHotspotR1ApsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mObservedHotspotR1ApInScanHistogram);
                this.mWifiLogProto.observedHotspotR2ApsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mObservedHotspotR2ApInScanHistogram);
                this.mWifiLogProto.observedHotspotR1EssInScanHistogram = makeNumConnectableNetworksBucketArray(this.mObservedHotspotR1EssInScanHistogram);
                this.mWifiLogProto.observedHotspotR2EssInScanHistogram = makeNumConnectableNetworksBucketArray(this.mObservedHotspotR2EssInScanHistogram);
                this.mWifiLogProto.observedHotspotR1ApsPerEssInScanHistogram = makeNumConnectableNetworksBucketArray(this.mObservedHotspotR1ApsPerEssInScanHistogram);
                this.mWifiLogProto.observedHotspotR2ApsPerEssInScanHistogram = makeNumConnectableNetworksBucketArray(this.mObservedHotspotR2ApsPerEssInScanHistogram);
                this.mWifiLogProto.observed80211McSupportingApsInScanHistogram = makeNumConnectableNetworksBucketArray(this.mObserved80211mcApInScanHistogram);
                if (this.mSoftApEventListTethered.size() > 0) {
                    this.mWifiLogProto.softApConnectedClientsEventsTethered = (WifiMetricsProto.SoftApConnectedClientsEvent[]) this.mSoftApEventListTethered.toArray(this.mWifiLogProto.softApConnectedClientsEventsTethered);
                }
                if (this.mSoftApEventListLocalOnly.size() > 0) {
                    this.mWifiLogProto.softApConnectedClientsEventsLocalOnly = (WifiMetricsProto.SoftApConnectedClientsEvent[]) this.mSoftApEventListLocalOnly.toArray(this.mWifiLogProto.softApConnectedClientsEventsLocalOnly);
                }
                this.mWifiLogProto.wpsMetrics = this.mWpsMetrics;
                this.mWifiLogProto.wifiPowerStats = this.mWifiPowerMetrics.buildProto();
                this.mWifiLogProto.wifiRadioUsage = this.mWifiPowerMetrics.buildWifiRadioUsageProto();
                this.mWifiLogProto.wifiWakeStats = this.mWifiWakeMetrics.buildProto();
                this.mWifiLogProto.isMacRandomizationOn = this.mIsMacRandomizationOn;
                this.mWifiLogProto.experimentValues = this.mExperimentValues;
                this.mWifiLogProto.wifiIsUnusableEventList = new WifiMetricsProto.WifiIsUnusableEvent[this.mWifiIsUnusableList.size()];
                for (int i11 = th; i11 < this.mWifiIsUnusableList.size(); i11++) {
                    this.mWifiLogProto.wifiIsUnusableEventList[i11] = this.mWifiIsUnusableList.get(i11).event;
                }
                this.mWifiLogProto.hardwareRevision = SystemProperties.get("ro.boot.revision", Prefix.EMPTY);
                int numUsabilityStats = Math.min(Math.min(this.mWifiUsabilityStatsListBad.size(), this.mWifiUsabilityStatsListGood.size()), 2);
                LinkedList<WifiMetricsProto.WifiUsabilityStats> usabilityStatsGoodCopy = new LinkedList<>(this.mWifiUsabilityStatsListGood);
                LinkedList<WifiMetricsProto.WifiUsabilityStats> usabilityStatsBadCopy = new LinkedList<>(this.mWifiUsabilityStatsListBad);
                this.mWifiLogProto.wifiUsabilityStatsList = new WifiMetricsProto.WifiUsabilityStats[(numUsabilityStats * 2)];
                for (int i12 = 0; i12 < numUsabilityStats; i12++) {
                    this.mWifiLogProto.wifiUsabilityStatsList[i12 * 2] = usabilityStatsGoodCopy.remove(this.mRand.nextInt(usabilityStatsGoodCopy.size()));
                    this.mWifiLogProto.wifiUsabilityStatsList[(i12 * 2) + 1] = usabilityStatsBadCopy.remove(this.mRand.nextInt(usabilityStatsBadCopy.size()));
                }
                this.mWifiLogProto.mobilityStatePnoStatsList = new WifiMetricsProto.DeviceMobilityStatePnoScanStats[this.mMobilityStatePnoStatsMap.size()];
                for (int i13 = 0; i13 < this.mMobilityStatePnoStatsMap.size(); i13++) {
                    this.mWifiLogProto.mobilityStatePnoStatsList[i13] = this.mMobilityStatePnoStatsMap.valueAt(i13);
                }
                this.mWifiLogProto.wifiP2PStats = this.mWifiP2pMetrics.consolidateProto();
                this.mWifiLogProto.wifiDppLog = this.mDppMetrics.consolidateProto();
                this.mWifiLogProto.wifiConfigStoreIo = new WifiMetricsProto.WifiConfigStoreIO();
                this.mWifiLogProto.wifiConfigStoreIo.readDurations = makeWifiConfigStoreIODurationBucketArray(this.mWifiConfigStoreReadDurationHistogram);
                this.mWifiLogProto.wifiConfigStoreIo.writeDurations = makeWifiConfigStoreIODurationBucketArray(this.mWifiConfigStoreWriteDurationHistogram);
                WifiMetricsProto.LinkProbeStats linkProbeStats = new WifiMetricsProto.LinkProbeStats();
                linkProbeStats.successRssiCounts = this.mLinkProbeSuccessRssiCounts.toProto();
                linkProbeStats.failureRssiCounts = this.mLinkProbeFailureRssiCounts.toProto();
                linkProbeStats.successLinkSpeedCounts = this.mLinkProbeSuccessLinkSpeedCounts.toProto();
                linkProbeStats.failureLinkSpeedCounts = this.mLinkProbeFailureLinkSpeedCounts.toProto();
                linkProbeStats.successSecondsSinceLastTxSuccessHistogram = this.mLinkProbeSuccessSecondsSinceLastTxSuccessHistogram.toProto();
                linkProbeStats.failureSecondsSinceLastTxSuccessHistogram = this.mLinkProbeFailureSecondsSinceLastTxSuccessHistogram.toProto();
                linkProbeStats.successElapsedTimeMsHistogram = this.mLinkProbeSuccessElapsedTimeMsHistogram.toProto();
                linkProbeStats.failureReasonCounts = (WifiMetricsProto.LinkProbeStats.LinkProbeFailureReasonCount[]) this.mLinkProbeFailureReasonCounts.toProto(WifiMetricsProto.LinkProbeStats.LinkProbeFailureReasonCount.class, $$Lambda$WifiMetrics$2FAdjVJtGK2Wuyu18kKp1OyQsI4.INSTANCE);
                linkProbeStats.experimentProbeCounts = (WifiMetricsProto.LinkProbeStats.ExperimentProbeCounts[]) this.mLinkProbeExperimentProbeCounts.toProto(WifiMetricsProto.LinkProbeStats.ExperimentProbeCounts.class, $$Lambda$WifiMetrics$VFNq46goenmxhiBbqFQb2w_bbQ.INSTANCE);
                this.mWifiLogProto.linkProbeStats = linkProbeStats;
                this.mWifiLogProto.networkSelectionExperimentDecisionsList = makeNetworkSelectionExperimentDecisionsList();
                this.mWifiNetworkRequestApiLog.networkMatchSizeHistogram = this.mWifiNetworkRequestApiMatchSizeHistogram.toProto();
                this.mWifiLogProto.wifiNetworkRequestApiLog = this.mWifiNetworkRequestApiLog;
                this.mWifiNetworkSuggestionApiLog.networkListSizeHistogram = this.mWifiNetworkSuggestionApiListSizeHistogram.toProto();
                this.mWifiLogProto.wifiNetworkSuggestionApiLog = this.mWifiNetworkSuggestionApiLog;
                this.mWifiLockStats.highPerfLockAcqDurationSecHistogram = this.mWifiLockHighPerfAcqDurationSecHistogram.toProto();
                this.mWifiLockStats.lowLatencyLockAcqDurationSecHistogram = this.mWifiLockLowLatencyAcqDurationSecHistogram.toProto();
                this.mWifiLockStats.highPerfActiveSessionDurationSecHistogram = this.mWifiLockHighPerfActiveSessionDurationSecHistogram.toProto();
                this.mWifiLockStats.lowLatencyActiveSessionDurationSecHistogram = this.mWifiLockLowLatencyActiveSessionDurationSecHistogram.toProto();
                this.mWifiLogProto.wifiLockStats = this.mWifiLockStats;
                this.mWifiLogProto.wifiToggleStats = this.mWifiToggleStats;
                this.mWifiLogProto.passpointProvisionStats = new WifiMetricsProto.PasspointProvisionStats();
                this.mWifiLogProto.passpointProvisionStats.numProvisionSuccess = this.mNumProvisionSuccess;
                this.mWifiLogProto.passpointProvisionStats.provisionFailureCount = (WifiMetricsProto.PasspointProvisionStats.ProvisionFailureCount[]) this.mPasspointProvisionFailureCounts.toProto(WifiMetricsProto.PasspointProvisionStats.ProvisionFailureCount.class, $$Lambda$WifiMetrics$tunke9EhqDWQH_nd_0DQvxGJZmI.INSTANCE);
            } finally {
                boolean z2 = th;
            }
        }
    }

    static /* synthetic */ WifiMetricsProto.LinkProbeStats.LinkProbeFailureReasonCount lambda$consolidateProto$0(int reason, int count) {
        WifiMetricsProto.LinkProbeStats.LinkProbeFailureReasonCount c = new WifiMetricsProto.LinkProbeStats.LinkProbeFailureReasonCount();
        c.failureReason = linkProbeFailureReasonToProto(reason);
        c.count = count;
        return c;
    }

    static /* synthetic */ WifiMetricsProto.LinkProbeStats.ExperimentProbeCounts lambda$consolidateProto$1(String experimentId, int probeCount) {
        WifiMetricsProto.LinkProbeStats.ExperimentProbeCounts c = new WifiMetricsProto.LinkProbeStats.ExperimentProbeCounts();
        c.experimentId = experimentId;
        c.probeCount = probeCount;
        return c;
    }

    static /* synthetic */ WifiMetricsProto.PasspointProvisionStats.ProvisionFailureCount lambda$consolidateProto$2(int key, int count) {
        WifiMetricsProto.PasspointProvisionStats.ProvisionFailureCount entry = new WifiMetricsProto.PasspointProvisionStats.ProvisionFailureCount();
        entry.failureCode = key;
        entry.count = count;
        return entry;
    }

    private static int linkProbeFailureReasonToProto(int reason) {
        if (reason == 2) {
            return 1;
        }
        if (reason == 3) {
            return 2;
        }
        if (reason == 4) {
            return 3;
        }
        if (reason != 5) {
            return 0;
        }
        return 4;
    }

    private WifiMetricsProto.NetworkSelectionExperimentDecisions[] makeNetworkSelectionExperimentDecisionsList() {
        WifiMetricsProto.NetworkSelectionExperimentDecisions[] results = new WifiMetricsProto.NetworkSelectionExperimentDecisions[this.mNetworkSelectionExperimentPairNumChoicesCounts.size()];
        int i = 0;
        for (Map.Entry<Pair<Integer, Integer>, NetworkSelectionExperimentResults> entry : this.mNetworkSelectionExperimentPairNumChoicesCounts.entrySet()) {
            WifiMetricsProto.NetworkSelectionExperimentDecisions result = new WifiMetricsProto.NetworkSelectionExperimentDecisions();
            result.experiment1Id = ((Integer) entry.getKey().first).intValue();
            result.experiment2Id = ((Integer) entry.getKey().second).intValue();
            result.sameSelectionNumChoicesCounter = entry.getValue().sameSelectionNumChoicesCounter.toProto();
            result.differentSelectionNumChoicesCounter = entry.getValue().differentSelectionNumChoicesCounter.toProto();
            results[i] = result;
            i++;
        }
        return results;
    }

    private void consolidateScoringParams() {
        synchronized (this.mLock) {
            if (this.mScoringParams != null) {
                int experimentIdentifier = this.mScoringParams.getExperimentIdentifier();
                if (experimentIdentifier == 0) {
                    this.mWifiLogProto.scoreExperimentId = Prefix.EMPTY;
                } else {
                    WifiMetricsProto.WifiLog wifiLog = this.mWifiLogProto;
                    wifiLog.scoreExperimentId = "x" + experimentIdentifier;
                }
            }
        }
    }

    private WifiMetricsProto.NumConnectableNetworksBucket[] makeNumConnectableNetworksBucketArray(SparseIntArray sia) {
        WifiMetricsProto.NumConnectableNetworksBucket[] array = new WifiMetricsProto.NumConnectableNetworksBucket[sia.size()];
        for (int i = 0; i < sia.size(); i++) {
            WifiMetricsProto.NumConnectableNetworksBucket keyVal = new WifiMetricsProto.NumConnectableNetworksBucket();
            keyVal.numConnectableNetworks = sia.keyAt(i);
            keyVal.count = sia.valueAt(i);
            array[i] = keyVal;
        }
        return array;
    }

    private WifiMetricsProto.WifiConfigStoreIO.DurationBucket[] makeWifiConfigStoreIODurationBucketArray(SparseIntArray sia) {
        MetricsUtils.GenericBucket[] genericBuckets = MetricsUtils.linearHistogramToGenericBuckets(sia, WIFI_CONFIG_STORE_IO_DURATION_BUCKET_RANGES_MS);
        WifiMetricsProto.WifiConfigStoreIO.DurationBucket[] array = new WifiMetricsProto.WifiConfigStoreIO.DurationBucket[genericBuckets.length];
        int i = 0;
        while (i < genericBuckets.length) {
            try {
                array[i] = new WifiMetricsProto.WifiConfigStoreIO.DurationBucket();
                array[i].rangeStartMs = StrictMath.toIntExact(genericBuckets[i].start);
                array[i].rangeEndMs = StrictMath.toIntExact(genericBuckets[i].end);
                array[i].count = genericBuckets[i].count;
                i++;
            } catch (ArithmeticException e) {
                return new WifiMetricsProto.WifiConfigStoreIO.DurationBucket[0];
            }
        }
        return array;
    }

    private void clear() {
        synchronized (this.mLock) {
            loadSettings();
            this.mConnectionEventList.clear();
            if (this.mCurrentConnectionEvent != null) {
                this.mConnectionEventList.add(this.mCurrentConnectionEvent);
            }
            this.mScanReturnEntries.clear();
            this.mWifiSystemStateEntries.clear();
            this.mRecordStartTimeSec = this.mClock.getElapsedSinceBootMillis() / 1000;
            this.mRssiPollCountsMap.clear();
            this.mRssiDeltaCounts.clear();
            this.mLinkSpeedCounts.clear();
            this.mWifiAlertReasonCounts.clear();
            this.mWifiScoreCounts.clear();
            this.mWifiUsabilityScoreCounts.clear();
            this.mWifiLogProto.clear();
            this.mScanResultRssiTimestampMillis = -1;
            this.mSoftApManagerReturnCodeCounts.clear();
            this.mStaEventList.clear();
            this.mWifiAwareMetrics.clear();
            this.mRttMetrics.clear();
            this.mTotalSsidsInScanHistogram.clear();
            this.mTotalBssidsInScanHistogram.clear();
            this.mAvailableOpenSsidsInScanHistogram.clear();
            this.mAvailableOpenBssidsInScanHistogram.clear();
            this.mAvailableSavedSsidsInScanHistogram.clear();
            this.mAvailableSavedBssidsInScanHistogram.clear();
            this.mAvailableOpenOrSavedSsidsInScanHistogram.clear();
            this.mAvailableOpenOrSavedBssidsInScanHistogram.clear();
            this.mAvailableSavedPasspointProviderProfilesInScanHistogram.clear();
            this.mAvailableSavedPasspointProviderBssidsInScanHistogram.clear();
            this.mPnoScanMetrics.clear();
            this.mWifiLinkLayerUsageStats.clear();
            this.mConnectToNetworkNotificationCount.clear();
            this.mConnectToNetworkNotificationActionCount.clear();
            this.mNumOpenNetworkRecommendationUpdates = 0;
            this.mNumOpenNetworkConnectMessageFailedToSend = 0;
            this.mObservedHotspotR1ApInScanHistogram.clear();
            this.mObservedHotspotR2ApInScanHistogram.clear();
            this.mObservedHotspotR1EssInScanHistogram.clear();
            this.mObservedHotspotR2EssInScanHistogram.clear();
            this.mObservedHotspotR1ApsPerEssInScanHistogram.clear();
            this.mObservedHotspotR2ApsPerEssInScanHistogram.clear();
            this.mSoftApEventListTethered.clear();
            this.mSoftApEventListLocalOnly.clear();
            this.mWpsMetrics.clear();
            this.mWifiWakeMetrics.clear();
            this.mObserved80211mcApInScanHistogram.clear();
            this.mWifiIsUnusableList.clear();
            this.mInstalledPasspointProfileTypeForR1.clear();
            this.mInstalledPasspointProfileTypeForR2.clear();
            this.mWifiUsabilityStatsListGood.clear();
            this.mWifiUsabilityStatsListBad.clear();
            this.mWifiUsabilityStatsEntriesList.clear();
            this.mMobilityStatePnoStatsMap.clear();
            this.mWifiP2pMetrics.clear();
            this.mDppMetrics.clear();
            this.mWifiUsabilityStatsCounter = 0;
            this.mLastBssid = null;
            this.mLastFrequency = -1;
            this.mSeqNumInsideFramework = 0;
            this.mLastWifiUsabilityScore = -1;
            this.mLastWifiUsabilityScoreNoReset = -1;
            this.mLastPredictionHorizonSec = -1;
            this.mLastPredictionHorizonSecNoReset = -1;
            this.mSeqNumToFramework = -1;
            this.mProbeStatusSinceLastUpdate = 1;
            this.mProbeElapsedTimeSinceLastUpdateMs = -1;
            this.mProbeMcsRateSinceLastUpdate = -1;
            this.mScoreBreachLowTimeMillis = -1;
            this.mWifiConfigStoreReadDurationHistogram.clear();
            this.mWifiConfigStoreWriteDurationHistogram.clear();
            this.mLinkProbeSuccessRssiCounts.clear();
            this.mLinkProbeFailureRssiCounts.clear();
            this.mLinkProbeSuccessLinkSpeedCounts.clear();
            this.mLinkProbeFailureLinkSpeedCounts.clear();
            this.mLinkProbeSuccessSecondsSinceLastTxSuccessHistogram.clear();
            this.mLinkProbeFailureSecondsSinceLastTxSuccessHistogram.clear();
            this.mLinkProbeSuccessElapsedTimeMsHistogram.clear();
            this.mLinkProbeFailureReasonCounts.clear();
            this.mLinkProbeExperimentProbeCounts.clear();
            this.mLinkProbeStaEventCount = 0;
            this.mNetworkSelectionExperimentPairNumChoicesCounts.clear();
            this.mWifiNetworkSuggestionApiLog.clear();
            this.mWifiNetworkSuggestionApiLog.clear();
            this.mWifiNetworkRequestApiMatchSizeHistogram.clear();
            this.mWifiNetworkSuggestionApiListSizeHistogram.clear();
            this.mWifiLockHighPerfAcqDurationSecHistogram.clear();
            this.mWifiLockLowLatencyAcqDurationSecHistogram.clear();
            this.mWifiLockHighPerfActiveSessionDurationSecHistogram.clear();
            this.mWifiLockLowLatencyActiveSessionDurationSecHistogram.clear();
            this.mWifiLockStats.clear();
            this.mWifiToggleStats.clear();
            this.mPasspointProvisionFailureCounts.clear();
            this.mNumProvisionSuccess = 0;
        }
    }

    public void setScreenState(boolean screenOn) {
        synchronized (this.mLock) {
            this.mScreenOn = screenOn;
        }
    }

    public void setWifiState(int wifiState) {
        synchronized (this.mLock) {
            this.mWifiState = wifiState;
            boolean z = true;
            this.mWifiWins = wifiState == 3;
            if (wifiState != 3) {
                z = false;
            }
            this.mWifiWinsUsabilityScore = z;
        }
    }

    /* access modifiers changed from: private */
    public void processMessage(Message msg) {
        WifiMetricsProto.StaEvent event = new WifiMetricsProto.StaEvent();
        boolean logEvent = true;
        boolean z = false;
        switch (msg.what) {
            case 131213:
                event.type = 10;
                break;
            case 131219:
                event.type = 6;
                break;
            case WifiMonitor.NETWORK_CONNECTION_EVENT:
                event.type = 3;
                break;
            case WifiMonitor.NETWORK_DISCONNECTION_EVENT:
                event.type = 4;
                event.reason = msg.arg2;
                if (msg.arg1 != 0) {
                    z = true;
                }
                event.localGen = z;
                break;
            case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT:
                logEvent = false;
                this.mSupplicantStateChangeBitmask |= supplicantStateToBit(((StateChangeResult) msg.obj).state);
                break;
            case WifiMonitor.AUTHENTICATION_FAILURE_EVENT:
                event.type = 2;
                int i = msg.arg1;
                if (i != 0) {
                    if (i != 1) {
                        if (i != 2) {
                            if (i == 3) {
                                event.authFailureReason = 4;
                                break;
                            }
                        } else {
                            event.authFailureReason = 3;
                            break;
                        }
                    } else {
                        event.authFailureReason = 2;
                        break;
                    }
                } else {
                    event.authFailureReason = 1;
                    break;
                }
                break;
            case WifiMonitor.ASSOCIATION_REJECTION_EVENT:
                event.type = 1;
                if (msg.arg1 > 0) {
                    z = true;
                }
                event.associationTimedOut = z;
                event.status = msg.arg2;
                break;
            default:
                return;
        }
        if (logEvent) {
            addStaEvent(event);
        }
    }

    public void logStaEvent(int type) {
        logStaEvent(type, 0, (WifiConfiguration) null);
    }

    public void logStaEvent(int type, WifiConfiguration config) {
        logStaEvent(type, 0, config);
    }

    public void logStaEvent(int type, int frameworkDisconnectReason) {
        logStaEvent(type, frameworkDisconnectReason, (WifiConfiguration) null);
    }

    public void logStaEvent(int type, int frameworkDisconnectReason, WifiConfiguration config) {
        switch (type) {
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                WifiMetricsProto.StaEvent event = new WifiMetricsProto.StaEvent();
                event.type = type;
                if (frameworkDisconnectReason != 0) {
                    event.frameworkDisconnectReason = frameworkDisconnectReason;
                }
                event.configInfo = createConfigInfo(config);
                addStaEvent(event);
                return;
            default:
                Log.e(TAG, "Unknown StaEvent:" + type);
                return;
        }
    }

    private void addStaEvent(WifiMetricsProto.StaEvent staEvent) {
        staEvent.startTimeMillis = this.mClock.getElapsedSinceBootMillis();
        staEvent.lastRssi = this.mLastPollRssi;
        staEvent.lastFreq = this.mLastPollFreq;
        staEvent.lastLinkSpeed = this.mLastPollLinkSpeed;
        staEvent.supplicantStateChangesBitmask = this.mSupplicantStateChangeBitmask;
        staEvent.lastScore = this.mLastScore;
        staEvent.lastWifiUsabilityScore = this.mLastWifiUsabilityScore;
        staEvent.lastPredictionHorizonSec = this.mLastPredictionHorizonSec;
        this.mSupplicantStateChangeBitmask = 0;
        this.mLastPollRssi = -127;
        this.mLastPollFreq = -1;
        this.mLastPollLinkSpeed = -1;
        this.mLastScore = -1;
        this.mLastWifiUsabilityScore = -1;
        this.mLastPredictionHorizonSec = -1;
        this.mStaEventList.add(new StaEventWithTime(staEvent, this.mClock.getWallClockMillis()));
        if (this.mStaEventList.size() > 768) {
            this.mStaEventList.remove();
        }
    }

    private WifiMetricsProto.StaEvent.ConfigInfo createConfigInfo(WifiConfiguration config) {
        if (config == null) {
            return null;
        }
        WifiMetricsProto.StaEvent.ConfigInfo info = new WifiMetricsProto.StaEvent.ConfigInfo();
        info.allowedKeyManagement = bitSetToInt(config.allowedKeyManagement);
        info.allowedProtocols = bitSetToInt(config.allowedProtocols);
        info.allowedAuthAlgorithms = bitSetToInt(config.allowedAuthAlgorithms);
        info.allowedPairwiseCiphers = bitSetToInt(config.allowedPairwiseCiphers);
        info.allowedGroupCiphers = bitSetToInt(config.allowedGroupCiphers);
        info.hiddenSsid = config.hiddenSSID;
        info.isPasspoint = config.isPasspoint();
        info.isEphemeral = config.isEphemeral();
        info.hasEverConnected = config.getNetworkSelectionStatus().getHasEverConnected();
        ScanResult candidate = config.getNetworkSelectionStatus().getCandidate();
        if (candidate != null) {
            info.scanRssi = candidate.level;
            info.scanFreq = candidate.frequency;
        }
        return info;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public WifiAwareMetrics getWifiAwareMetrics() {
        return this.mWifiAwareMetrics;
    }

    public WifiWakeMetrics getWakeupMetrics() {
        return this.mWifiWakeMetrics;
    }

    public RttMetrics getRttMetrics() {
        return this.mRttMetrics;
    }

    /* renamed from: com.android.server.wifi.WifiMetrics$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$net$wifi$SupplicantState = new int[SupplicantState.values().length];

        static {
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.DISCONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.INTERFACE_DISABLED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.INACTIVE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.SCANNING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.AUTHENTICATING.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.ASSOCIATING.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.ASSOCIATED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.FOUR_WAY_HANDSHAKE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.GROUP_HANDSHAKE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.COMPLETED.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.DORMANT.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.UNINITIALIZED.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.INVALID.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    public static int supplicantStateToBit(SupplicantState state) {
        switch (AnonymousClass2.$SwitchMap$android$net$wifi$SupplicantState[state.ordinal()]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
                return 8;
            case 5:
                return 16;
            case 6:
                return 32;
            case 7:
                return 64;
            case 8:
                return 128;
            case 9:
                return 256;
            case 10:
                return 512;
            case 11:
                return 1024;
            case 12:
                return 2048;
            case 13:
                return 4096;
            default:
                Log.wtf(TAG, "Got unknown supplicant state: " + state.ordinal());
                return 0;
        }
    }

    private static String supplicantStateChangesBitmaskToString(int mask) {
        StringBuilder sb = new StringBuilder();
        sb.append("supplicantStateChangeEvents: {");
        if ((mask & 1) > 0) {
            sb.append(" DISCONNECTED");
        }
        if ((mask & 2) > 0) {
            sb.append(" INTERFACE_DISABLED");
        }
        if ((mask & 4) > 0) {
            sb.append(" INACTIVE");
        }
        if ((mask & 8) > 0) {
            sb.append(" SCANNING");
        }
        if ((mask & 16) > 0) {
            sb.append(" AUTHENTICATING");
        }
        if ((mask & 32) > 0) {
            sb.append(" ASSOCIATING");
        }
        if ((mask & 64) > 0) {
            sb.append(" ASSOCIATED");
        }
        if ((mask & 128) > 0) {
            sb.append(" FOUR_WAY_HANDSHAKE");
        }
        if ((mask & 256) > 0) {
            sb.append(" GROUP_HANDSHAKE");
        }
        if ((mask & 512) > 0) {
            sb.append(" COMPLETED");
        }
        if ((mask & 1024) > 0) {
            sb.append(" DORMANT");
        }
        if ((mask & 2048) > 0) {
            sb.append(" UNINITIALIZED");
        }
        if ((mask & 4096) > 0) {
            sb.append(" INVALID");
        }
        sb.append(" }");
        return sb.toString();
    }

    public static String staEventToString(WifiMetricsProto.StaEvent event) {
        if (event == null) {
            return "<NULL>";
        }
        StringBuilder sb = new StringBuilder();
        switch (event.type) {
            case 1:
                sb.append("ASSOCIATION_REJECTION_EVENT");
                sb.append(" timedOut=");
                sb.append(event.associationTimedOut);
                sb.append(" status=");
                sb.append(event.status);
                sb.append(":");
                sb.append(ISupplicantStaIfaceCallback.StatusCode.toString(event.status));
                break;
            case 2:
                sb.append("AUTHENTICATION_FAILURE_EVENT reason=");
                sb.append(event.authFailureReason);
                sb.append(":");
                sb.append(authFailureReasonToString(event.authFailureReason));
                break;
            case 3:
                sb.append("NETWORK_CONNECTION_EVENT");
                break;
            case 4:
                sb.append("NETWORK_DISCONNECTION_EVENT");
                sb.append(" local_gen=");
                sb.append(event.localGen);
                sb.append(" reason=");
                sb.append(event.reason);
                sb.append(":");
                sb.append(ISupplicantStaIfaceCallback.ReasonCode.toString(event.reason >= 0 ? event.reason : event.reason * -1));
                break;
            case 6:
                sb.append("CMD_ASSOCIATED_BSSID");
                break;
            case 7:
                sb.append("CMD_IP_CONFIGURATION_SUCCESSFUL");
                break;
            case 8:
                sb.append("CMD_IP_CONFIGURATION_LOST");
                break;
            case 9:
                sb.append("CMD_IP_REACHABILITY_LOST");
                break;
            case 10:
                sb.append("CMD_TARGET_BSSID");
                break;
            case 11:
                sb.append("CMD_START_CONNECT");
                break;
            case 12:
                sb.append("CMD_START_ROAM");
                break;
            case 13:
                sb.append("CONNECT_NETWORK");
                break;
            case 14:
                sb.append("NETWORK_AGENT_VALID_NETWORK");
                break;
            case 15:
                sb.append("FRAMEWORK_DISCONNECT");
                sb.append(" reason=");
                sb.append(frameworkDisconnectReasonToString(event.frameworkDisconnectReason));
                break;
            case 16:
                sb.append("SCORE_BREACH");
                break;
            case 17:
                sb.append("MAC_CHANGE");
                break;
            case 18:
                sb.append("WIFI_ENABLED");
                break;
            case 19:
                sb.append("WIFI_DISABLED");
                break;
            case 20:
                sb.append("WIFI_USABILITY_SCORE_BREACH");
                break;
            case 21:
                sb.append("LINK_PROBE");
                sb.append(" linkProbeWasSuccess=");
                sb.append(event.linkProbeWasSuccess);
                if (!event.linkProbeWasSuccess) {
                    sb.append(" linkProbeFailureReason=");
                    sb.append(event.linkProbeFailureReason);
                    break;
                } else {
                    sb.append(" linkProbeSuccessElapsedTimeMs=");
                    sb.append(event.linkProbeSuccessElapsedTimeMs);
                    break;
                }
            default:
                sb.append("UNKNOWN " + event.type + ":");
                break;
        }
        if (event.lastRssi != -127) {
            sb.append(" lastRssi=");
            sb.append(event.lastRssi);
        }
        if (event.lastFreq != -1) {
            sb.append(" lastFreq=");
            sb.append(event.lastFreq);
        }
        if (event.lastLinkSpeed != -1) {
            sb.append(" lastLinkSpeed=");
            sb.append(event.lastLinkSpeed);
        }
        if (event.lastScore != -1) {
            sb.append(" lastScore=");
            sb.append(event.lastScore);
        }
        if (event.lastWifiUsabilityScore != -1) {
            sb.append(" lastWifiUsabilityScore=");
            sb.append(event.lastWifiUsabilityScore);
            sb.append(" lastPredictionHorizonSec=");
            sb.append(event.lastPredictionHorizonSec);
        }
        if (event.supplicantStateChangesBitmask != 0) {
            sb.append(", ");
            sb.append(supplicantStateChangesBitmaskToString(event.supplicantStateChangesBitmask));
        }
        if (event.configInfo != null) {
            sb.append(", ");
            sb.append(configInfoToString(event.configInfo));
        }
        return sb.toString();
    }

    private static String authFailureReasonToString(int authFailureReason) {
        if (authFailureReason == 1) {
            return "ERROR_AUTH_FAILURE_NONE";
        }
        if (authFailureReason == 2) {
            return "ERROR_AUTH_FAILURE_TIMEOUT";
        }
        if (authFailureReason == 3) {
            return "ERROR_AUTH_FAILURE_WRONG_PSWD";
        }
        if (authFailureReason != 4) {
            return Prefix.EMPTY;
        }
        return "ERROR_AUTH_FAILURE_EAP_FAILURE";
    }

    private static String frameworkDisconnectReasonToString(int frameworkDisconnectReason) {
        switch (frameworkDisconnectReason) {
            case 1:
                return "DISCONNECT_API";
            case 2:
                return "DISCONNECT_GENERIC";
            case 3:
                return "DISCONNECT_UNWANTED";
            case 4:
                return "DISCONNECT_ROAM_WATCHDOG_TIMER";
            case 5:
                return "DISCONNECT_P2P_DISCONNECT_WIFI_REQUEST";
            case 6:
                return "DISCONNECT_RESET_SIM_NETWORKS";
            default:
                return "DISCONNECT_UNKNOWN=" + frameworkDisconnectReason;
        }
    }

    private static String configInfoToString(WifiMetricsProto.StaEvent.ConfigInfo info) {
        return "ConfigInfo:" + " allowed_key_management=" + info.allowedKeyManagement + " allowed_protocols=" + info.allowedProtocols + " allowed_auth_algorithms=" + info.allowedAuthAlgorithms + " allowed_pairwise_ciphers=" + info.allowedPairwiseCiphers + " allowed_group_ciphers=" + info.allowedGroupCiphers + " hidden_ssid=" + info.hiddenSsid + " is_passpoint=" + info.isPasspoint + " is_ephemeral=" + info.isEphemeral + " has_ever_connected=" + info.hasEverConnected + " scan_rssi=" + info.scanRssi + " scan_freq=" + info.scanFreq;
    }

    private static int bitSetToInt(BitSet bits) {
        int value = 0;
        int i = 31;
        if (bits.length() < 31) {
            i = bits.length();
        }
        int nBits = i;
        for (int i2 = 0; i2 < nBits; i2++) {
            value += bits.get(i2) ? 1 << i2 : 0;
        }
        return value;
    }

    private void incrementSsid(SparseIntArray sia, int element) {
        increment(sia, Math.min(element, 20));
    }

    private void incrementBssid(SparseIntArray sia, int element) {
        increment(sia, Math.min(element, 50));
    }

    private void incrementTotalScanResults(SparseIntArray sia, int element) {
        increment(sia, Math.min(element, MAX_TOTAL_SCAN_RESULTS_BUCKET));
    }

    private void incrementTotalScanSsids(SparseIntArray sia, int element) {
        increment(sia, Math.min(element, 100));
    }

    private void incrementTotalPasspointAps(SparseIntArray sia, int element) {
        increment(sia, Math.min(element, 50));
    }

    private void incrementTotalUniquePasspointEss(SparseIntArray sia, int element) {
        increment(sia, Math.min(element, 20));
    }

    private void incrementPasspointPerUniqueEss(SparseIntArray sia, int element) {
        increment(sia, Math.min(element, 50));
    }

    private void increment80211mcAps(SparseIntArray sia, int element) {
        increment(sia, Math.min(element, 20));
    }

    private void increment(SparseIntArray sia, int element) {
        sia.put(element, sia.get(element) + 1);
    }

    private static class StaEventWithTime {
        public WifiMetricsProto.StaEvent staEvent;
        public long wallClockMillis;

        StaEventWithTime(WifiMetricsProto.StaEvent event, long wallClockMillis2) {
            this.staEvent = event;
            this.wallClockMillis = wallClockMillis2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(this.wallClockMillis);
            if (this.wallClockMillis != 0) {
                sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c, c, c, c, c, c}));
            } else {
                sb.append("                  ");
            }
            sb.append(" ");
            sb.append(WifiMetrics.staEventToString(this.staEvent));
            return sb.toString();
        }
    }

    private static class WifiIsUnusableWithTime {
        public WifiMetricsProto.WifiIsUnusableEvent event;
        public long wallClockMillis;

        WifiIsUnusableWithTime(WifiMetricsProto.WifiIsUnusableEvent event2, long wallClockMillis2) {
            this.event = event2;
            this.wallClockMillis = wallClockMillis2;
        }

        public String toString() {
            if (this.event == null) {
                return "<NULL>";
            }
            StringBuilder sb = new StringBuilder();
            if (this.wallClockMillis != 0) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(this.wallClockMillis);
                sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c, c, c, c, c, c}));
            } else {
                sb.append("                  ");
            }
            sb.append(" ");
            int i = this.event.type;
            if (i == 1) {
                sb.append("DATA_STALL_BAD_TX");
            } else if (i == 2) {
                sb.append("DATA_STALL_TX_WITHOUT_RX");
            } else if (i == 3) {
                sb.append("DATA_STALL_BOTH");
            } else if (i != 4) {
                sb.append("UNKNOWN " + this.event.type);
            } else {
                sb.append("FIRMWARE_ALERT");
            }
            sb.append(" lastScore=");
            sb.append(this.event.lastScore);
            sb.append(" txSuccessDelta=");
            sb.append(this.event.txSuccessDelta);
            sb.append(" txRetriesDelta=");
            sb.append(this.event.txRetriesDelta);
            sb.append(" txBadDelta=");
            sb.append(this.event.txBadDelta);
            sb.append(" rxSuccessDelta=");
            sb.append(this.event.rxSuccessDelta);
            sb.append(" packetUpdateTimeDelta=");
            sb.append(this.event.packetUpdateTimeDelta);
            sb.append("ms");
            if (this.event.firmwareAlertCode != -1) {
                sb.append(" firmwareAlertCode=");
                sb.append(this.event.firmwareAlertCode);
            }
            sb.append(" lastWifiUsabilityScore=");
            sb.append(this.event.lastWifiUsabilityScore);
            sb.append(" lastPredictionHorizonSec=");
            sb.append(this.event.lastPredictionHorizonSec);
            return sb.toString();
        }
    }

    public void updateWifiIsUnusableLinkLayerStats(long txSuccessDelta, long txRetriesDelta, long txBadDelta, long rxSuccessDelta, long updateTimeDelta) {
        this.mTxScucessDelta = txSuccessDelta;
        this.mTxRetriesDelta = txRetriesDelta;
        this.mTxBadDelta = txBadDelta;
        this.mRxSuccessDelta = rxSuccessDelta;
        this.mLlStatsUpdateTimeDelta = updateTimeDelta;
        this.mLlStatsLastUpdateTime = this.mClock.getElapsedSinceBootMillis();
    }

    public void resetWifiIsUnusableLinkLayerStats() {
        this.mTxScucessDelta = 0;
        this.mTxRetriesDelta = 0;
        this.mTxBadDelta = 0;
        this.mRxSuccessDelta = 0;
        this.mLlStatsUpdateTimeDelta = 0;
        this.mLlStatsLastUpdateTime = 0;
        this.mLastDataStallTime = Long.MIN_VALUE;
    }

    public void logWifiIsUnusableEvent(int triggerType) {
        logWifiIsUnusableEvent(triggerType, -1);
    }

    public void logWifiIsUnusableEvent(int triggerType, int firmwareAlertCode) {
        this.mScoreBreachLowTimeMillis = -1;
        if (this.mUnusableEventLogging) {
            long currentBootTime = this.mClock.getElapsedSinceBootMillis();
            if (triggerType == 1 || triggerType == 2 || triggerType == 3) {
                if (currentBootTime >= this.mLastDataStallTime + 120000) {
                    this.mLastDataStallTime = currentBootTime;
                } else {
                    return;
                }
            } else if (!(triggerType == 4 || triggerType == 5)) {
                Log.e(TAG, "Unknown WifiIsUnusableEvent: " + triggerType);
                return;
            }
            WifiMetricsProto.WifiIsUnusableEvent event = new WifiMetricsProto.WifiIsUnusableEvent();
            event.type = triggerType;
            if (triggerType == 4) {
                event.firmwareAlertCode = firmwareAlertCode;
            }
            event.startTimeMillis = currentBootTime;
            event.lastScore = this.mLastScoreNoReset;
            event.lastWifiUsabilityScore = this.mLastWifiUsabilityScoreNoReset;
            event.lastPredictionHorizonSec = this.mLastPredictionHorizonSecNoReset;
            event.txSuccessDelta = this.mTxScucessDelta;
            event.txRetriesDelta = this.mTxRetriesDelta;
            event.txBadDelta = this.mTxBadDelta;
            event.rxSuccessDelta = this.mRxSuccessDelta;
            event.packetUpdateTimeDelta = this.mLlStatsUpdateTimeDelta;
            event.lastLinkLayerStatsUpdateTime = this.mLlStatsLastUpdateTime;
            event.screenOn = this.mScreenOn;
            this.mWifiIsUnusableList.add(new WifiIsUnusableWithTime(event, this.mClock.getWallClockMillis()));
            if (this.mWifiIsUnusableList.size() > 20) {
                this.mWifiIsUnusableList.removeFirst();
            }
        }
    }

    @VisibleForTesting
    public void setWifiIsUnusableLoggingEnabled(boolean enabled) {
        synchronized (this.mLock) {
            this.mExperimentValues.wifiIsUnusableLoggingEnabled = enabled;
        }
    }

    @VisibleForTesting
    public void setLinkSpeedCountsLoggingEnabled(boolean enabled) {
        synchronized (this.mLock) {
            this.mExperimentValues.linkSpeedCountsLoggingEnabled = enabled;
        }
    }

    public void setWifiDataStallMinTxBad(int minTxBad) {
        synchronized (this.mLock) {
            this.mExperimentValues.wifiDataStallMinTxBad = minTxBad;
        }
    }

    public void setWifiDataStallMinRxWithoutTx(int minTxSuccessWithoutRx) {
        synchronized (this.mLock) {
            this.mExperimentValues.wifiDataStallMinTxSuccessWithoutRx = minTxSuccessWithoutRx;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:47:0x01a9, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00f6  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0176  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateWifiUsabilityStatsEntries(android.net.wifi.WifiInfo r17, com.android.server.wifi.WifiLinkLayerStats r18) {
        /*
            r16 = this;
            r1 = r16
            r2 = r18
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            if (r17 == 0) goto L_0x01a8
            if (r2 != 0) goto L_0x000d
            goto L_0x01a8
        L_0x000d:
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry> r0 = r1.mWifiUsabilityStatsEntriesList     // Catch:{ all -> 0x01aa }
            int r0 = r0.size()     // Catch:{ all -> 0x01aa }
            r4 = 40
            if (r0 >= r4) goto L_0x001d
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry r0 = new com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry     // Catch:{ all -> 0x01aa }
            r0.<init>()     // Catch:{ all -> 0x01aa }
            goto L_0x0025
        L_0x001d:
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry> r0 = r1.mWifiUsabilityStatsEntriesList     // Catch:{ all -> 0x01aa }
            java.lang.Object r0 = r0.remove()     // Catch:{ all -> 0x01aa }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry r0 = (com.android.server.wifi.nano.WifiMetricsProto.WifiUsabilityStatsEntry) r0     // Catch:{ all -> 0x01aa }
        L_0x0025:
            long r4 = r2.timeStampInMs     // Catch:{ all -> 0x01aa }
            r0.timeStampMs = r4     // Catch:{ all -> 0x01aa }
            long r4 = r2.txmpdu_be     // Catch:{ all -> 0x01aa }
            long r6 = r2.txmpdu_bk     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            long r6 = r2.txmpdu_vi     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            long r6 = r2.txmpdu_vo     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            r0.totalTxSuccess = r4     // Catch:{ all -> 0x01aa }
            long r4 = r2.retries_be     // Catch:{ all -> 0x01aa }
            long r6 = r2.retries_bk     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            long r6 = r2.retries_vi     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            long r6 = r2.retries_vo     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            r0.totalTxRetries = r4     // Catch:{ all -> 0x01aa }
            long r4 = r2.lostmpdu_be     // Catch:{ all -> 0x01aa }
            long r6 = r2.lostmpdu_bk     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            long r6 = r2.lostmpdu_vi     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            long r6 = r2.lostmpdu_vo     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            r0.totalTxBad = r4     // Catch:{ all -> 0x01aa }
            long r4 = r2.rxmpdu_be     // Catch:{ all -> 0x01aa }
            long r6 = r2.rxmpdu_bk     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            long r6 = r2.rxmpdu_vi     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            long r6 = r2.rxmpdu_vo     // Catch:{ all -> 0x01aa }
            long r4 = r4 + r6
            r0.totalRxSuccess = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.on_time     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalRadioOnTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.tx_time     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalRadioTxTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.rx_time     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalRadioRxTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.on_time_scan     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalScanTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.on_time_nan_scan     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalNanScanTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.on_time_background_scan     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalBackgroundScanTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.on_time_roam_scan     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalRoamScanTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.on_time_pno_scan     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalPnoScanTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r2.on_time_hs20_scan     // Catch:{ all -> 0x01aa }
            long r4 = (long) r4     // Catch:{ all -> 0x01aa }
            r0.totalHotspot2ScanTimeMs = r4     // Catch:{ all -> 0x01aa }
            int r4 = r17.getRssi()     // Catch:{ all -> 0x01aa }
            r0.rssi = r4     // Catch:{ all -> 0x01aa }
            int r4 = r17.getLinkSpeed()     // Catch:{ all -> 0x01aa }
            r0.linkSpeedMbps = r4     // Catch:{ all -> 0x01aa }
            android.util.SparseArray<com.android.server.wifi.WifiLinkLayerStats$ChannelStats> r4 = r2.channelStatsMap     // Catch:{ all -> 0x01aa }
            int r5 = r17.getFrequency()     // Catch:{ all -> 0x01aa }
            java.lang.Object r4 = r4.get(r5)     // Catch:{ all -> 0x01aa }
            com.android.server.wifi.WifiLinkLayerStats$ChannelStats r4 = (com.android.server.wifi.WifiLinkLayerStats.ChannelStats) r4     // Catch:{ all -> 0x01aa }
            if (r4 == 0) goto L_0x00ae
            int r5 = r4.radioOnTimeMs     // Catch:{ all -> 0x01aa }
            long r5 = (long) r5     // Catch:{ all -> 0x01aa }
            r0.totalRadioOnFreqTimeMs = r5     // Catch:{ all -> 0x01aa }
            int r5 = r4.ccaBusyTimeMs     // Catch:{ all -> 0x01aa }
            long r5 = (long) r5     // Catch:{ all -> 0x01aa }
            r0.totalCcaBusyFreqTimeMs = r5     // Catch:{ all -> 0x01aa }
        L_0x00ae:
            int r5 = r2.beacon_rx     // Catch:{ all -> 0x01aa }
            long r5 = (long) r5     // Catch:{ all -> 0x01aa }
            r0.totalBeaconRx = r5     // Catch:{ all -> 0x01aa }
            java.lang.String r5 = r1.mLastBssid     // Catch:{ all -> 0x01aa }
            r6 = 0
            r7 = -1
            r8 = 1
            if (r5 == 0) goto L_0x00d5
            int r5 = r1.mLastFrequency     // Catch:{ all -> 0x01aa }
            if (r5 == r7) goto L_0x00d5
            java.lang.String r5 = r1.mLastBssid     // Catch:{ all -> 0x01aa }
            java.lang.String r9 = r17.getBSSID()     // Catch:{ all -> 0x01aa }
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x01aa }
            if (r5 == 0) goto L_0x00d3
            int r5 = r1.mLastFrequency     // Catch:{ all -> 0x01aa }
            int r9 = r17.getFrequency()     // Catch:{ all -> 0x01aa }
            if (r5 != r9) goto L_0x00d3
            goto L_0x00d5
        L_0x00d3:
            r5 = r6
            goto L_0x00d6
        L_0x00d5:
            r5 = r8
        L_0x00d6:
            java.lang.String r9 = r17.getBSSID()     // Catch:{ all -> 0x01aa }
            r1.mLastBssid = r9     // Catch:{ all -> 0x01aa }
            int r9 = r17.getFrequency()     // Catch:{ all -> 0x01aa }
            r1.mLastFrequency = r9     // Catch:{ all -> 0x01aa }
            int r9 = r1.mLastScoreNoReset     // Catch:{ all -> 0x01aa }
            r0.wifiScore = r9     // Catch:{ all -> 0x01aa }
            int r9 = r1.mLastWifiUsabilityScoreNoReset     // Catch:{ all -> 0x01aa }
            r0.wifiUsabilityScore = r9     // Catch:{ all -> 0x01aa }
            int r9 = r1.mSeqNumToFramework     // Catch:{ all -> 0x01aa }
            r0.seqNumToFramework = r9     // Catch:{ all -> 0x01aa }
            int r9 = r1.mLastPredictionHorizonSecNoReset     // Catch:{ all -> 0x01aa }
            r0.predictionHorizonSec = r9     // Catch:{ all -> 0x01aa }
            int r9 = r1.mProbeStatusSinceLastUpdate     // Catch:{ all -> 0x01aa }
            if (r9 == r8) goto L_0x011d
            r10 = 2
            if (r9 == r10) goto L_0x011a
            r10 = 3
            if (r9 == r10) goto L_0x0117
            r0.probeStatusSinceLastUpdate = r6     // Catch:{ all -> 0x01aa }
            java.lang.String r9 = "WifiMetrics"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x01aa }
            r10.<init>()     // Catch:{ all -> 0x01aa }
            java.lang.String r11 = "Unknown link probe status: "
            r10.append(r11)     // Catch:{ all -> 0x01aa }
            int r11 = r1.mProbeStatusSinceLastUpdate     // Catch:{ all -> 0x01aa }
            r10.append(r11)     // Catch:{ all -> 0x01aa }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x01aa }
            android.util.Log.e(r9, r10)     // Catch:{ all -> 0x01aa }
            goto L_0x0120
        L_0x0117:
            r0.probeStatusSinceLastUpdate = r10     // Catch:{ all -> 0x01aa }
            goto L_0x0120
        L_0x011a:
            r0.probeStatusSinceLastUpdate = r10     // Catch:{ all -> 0x01aa }
            goto L_0x0120
        L_0x011d:
            r0.probeStatusSinceLastUpdate = r8     // Catch:{ all -> 0x01aa }
        L_0x0120:
            int r9 = r1.mProbeElapsedTimeSinceLastUpdateMs     // Catch:{ all -> 0x01aa }
            r0.probeElapsedTimeSinceLastUpdateMs = r9     // Catch:{ all -> 0x01aa }
            int r9 = r1.mProbeMcsRateSinceLastUpdate     // Catch:{ all -> 0x01aa }
            r0.probeMcsRateSinceLastUpdate = r9     // Catch:{ all -> 0x01aa }
            int r9 = r17.getRxLinkSpeedMbps()     // Catch:{ all -> 0x01aa }
            r0.rxLinkSpeedMbps = r9     // Catch:{ all -> 0x01aa }
            r0.isSameBssidAndFreq = r5     // Catch:{ all -> 0x01aa }
            int r9 = r1.mSeqNumInsideFramework     // Catch:{ all -> 0x01aa }
            r0.seqNumInsideFramework = r9     // Catch:{ all -> 0x01aa }
            int r9 = r1.mCurrentDeviceMobilityState     // Catch:{ all -> 0x01aa }
            r0.deviceMobilityState = r9     // Catch:{ all -> 0x01aa }
            com.android.server.wifi.CellularLinkLayerStatsCollector r9 = r1.mCellularLinkLayerStatsCollector     // Catch:{ all -> 0x01aa }
            com.android.server.wifi.CellularLinkLayerStats r9 = r9.update()     // Catch:{ all -> 0x01aa }
            int r10 = r9.getDataNetworkType()     // Catch:{ all -> 0x01aa }
            int r10 = r1.parseDataNetworkTypeToProto(r10)     // Catch:{ all -> 0x01aa }
            r0.cellularDataNetworkType = r10     // Catch:{ all -> 0x01aa }
            int r10 = r9.getSignalStrengthDbm()     // Catch:{ all -> 0x01aa }
            r0.cellularSignalStrengthDbm = r10     // Catch:{ all -> 0x01aa }
            int r10 = r9.getSignalStrengthDb()     // Catch:{ all -> 0x01aa }
            r0.cellularSignalStrengthDb = r10     // Catch:{ all -> 0x01aa }
            boolean r10 = r9.getIsSameRegisteredCell()     // Catch:{ all -> 0x01aa }
            r0.isSameRegisteredCell = r10     // Catch:{ all -> 0x01aa }
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry> r10 = r1.mWifiUsabilityStatsEntriesList     // Catch:{ all -> 0x01aa }
            r10.add(r0)     // Catch:{ all -> 0x01aa }
            int r10 = r1.mWifiUsabilityStatsCounter     // Catch:{ all -> 0x01aa }
            int r10 = r10 + r8
            r1.mWifiUsabilityStatsCounter = r10     // Catch:{ all -> 0x01aa }
            int r10 = r1.mWifiUsabilityStatsCounter     // Catch:{ all -> 0x01aa }
            r11 = 100
            if (r10 < r11) goto L_0x016e
            r1.addToWifiUsabilityStatsList(r8, r6, r7)     // Catch:{ all -> 0x01aa }
        L_0x016e:
            long r10 = r1.mScoreBreachLowTimeMillis     // Catch:{ all -> 0x01aa }
            r12 = -1
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 == 0) goto L_0x0192
            com.android.server.wifi.Clock r10 = r1.mClock     // Catch:{ all -> 0x01aa }
            long r10 = r10.getElapsedSinceBootMillis()     // Catch:{ all -> 0x01aa }
            long r14 = r1.mScoreBreachLowTimeMillis     // Catch:{ all -> 0x01aa }
            long r10 = r10 - r14
            r14 = 60000(0xea60, double:2.9644E-319)
            int r14 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r14 < 0) goto L_0x0192
            r1.mScoreBreachLowTimeMillis = r12     // Catch:{ all -> 0x01aa }
            r12 = 90000(0x15f90, double:4.4466E-319)
            int r12 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r12 > 0) goto L_0x0192
            r1.addToWifiUsabilityStatsList(r8, r6, r7)     // Catch:{ all -> 0x01aa }
        L_0x0192:
            int r6 = r1.mSeqNumInsideFramework     // Catch:{ all -> 0x01aa }
            android.net.wifi.WifiUsabilityStatsEntry r10 = r1.createNewWifiUsabilityStatsEntryParcelable(r0)     // Catch:{ all -> 0x01aa }
            r1.sendWifiUsabilityStats(r6, r5, r10)     // Catch:{ all -> 0x01aa }
            int r6 = r1.mSeqNumInsideFramework     // Catch:{ all -> 0x01aa }
            int r6 = r6 + r8
            r1.mSeqNumInsideFramework = r6     // Catch:{ all -> 0x01aa }
            r1.mProbeStatusSinceLastUpdate = r8     // Catch:{ all -> 0x01aa }
            r1.mProbeElapsedTimeSinceLastUpdateMs = r7     // Catch:{ all -> 0x01aa }
            r1.mProbeMcsRateSinceLastUpdate = r7     // Catch:{ all -> 0x01aa }
            monitor-exit(r3)     // Catch:{ all -> 0x01aa }
            return
        L_0x01a8:
            monitor-exit(r3)     // Catch:{ all -> 0x01aa }
            return
        L_0x01aa:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x01aa }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiMetrics.updateWifiUsabilityStatsEntries(android.net.wifi.WifiInfo, com.android.server.wifi.WifiLinkLayerStats):void");
    }

    private int parseDataNetworkTypeToProto(int cellularDataNetworkType) {
        if (cellularDataNetworkType == 0) {
            return 0;
        }
        if (cellularDataNetworkType == 13) {
            return 6;
        }
        if (cellularDataNetworkType == 20) {
            return 7;
        }
        if (cellularDataNetworkType == 3) {
            return 4;
        }
        if (cellularDataNetworkType == 4) {
            return 2;
        }
        if (cellularDataNetworkType == 5) {
            return 3;
        }
        if (cellularDataNetworkType == 16) {
            return 1;
        }
        if (cellularDataNetworkType == 17) {
            return 5;
        }
        Log.e(TAG, "Unknown data network type : " + cellularDataNetworkType);
        return 0;
    }

    private int parseDataNetworkTypeFromProto(int cellularDataNetworkType) {
        switch (cellularDataNetworkType) {
            case 0:
                return 0;
            case 1:
                return 16;
            case 2:
                return 4;
            case 3:
                return 5;
            case 4:
                return 3;
            case 5:
                return 17;
            case 6:
                return 13;
            case 7:
                return 20;
            default:
                Log.e(TAG, "Unknown data network type : " + cellularDataNetworkType);
                return 0;
        }
    }

    private void sendWifiUsabilityStats(int seqNum, boolean isSameBssidAndFreq, WifiUsabilityStatsEntry statsEntry) {
        for (IOnWifiUsabilityStatsListener listener : this.mOnWifiUsabilityListeners.getCallbacks()) {
            try {
                listener.onWifiUsabilityStats(seqNum, isSameBssidAndFreq, statsEntry);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to invoke Wifi usability stats entry listener " + listener, e);
            }
        }
    }

    private WifiUsabilityStatsEntry createNewWifiUsabilityStatsEntryParcelable(WifiMetricsProto.WifiUsabilityStatsEntry s) {
        int probeStatus;
        WifiMetricsProto.WifiUsabilityStatsEntry wifiUsabilityStatsEntry = s;
        int i = wifiUsabilityStatsEntry.probeStatusSinceLastUpdate;
        if (i == 1) {
            probeStatus = 1;
        } else if (i == 2) {
            probeStatus = 2;
        } else if (i != 3) {
            probeStatus = 0;
            Log.e(TAG, "Unknown link probe status: " + wifiUsabilityStatsEntry.probeStatusSinceLastUpdate);
        } else {
            probeStatus = 3;
        }
        long j = wifiUsabilityStatsEntry.timeStampMs;
        return new WifiUsabilityStatsEntry(j, wifiUsabilityStatsEntry.rssi, wifiUsabilityStatsEntry.linkSpeedMbps, wifiUsabilityStatsEntry.totalTxSuccess, wifiUsabilityStatsEntry.totalTxRetries, wifiUsabilityStatsEntry.totalTxBad, wifiUsabilityStatsEntry.totalRxSuccess, wifiUsabilityStatsEntry.totalRadioOnTimeMs, wifiUsabilityStatsEntry.totalRadioTxTimeMs, wifiUsabilityStatsEntry.totalRadioRxTimeMs, wifiUsabilityStatsEntry.totalScanTimeMs, wifiUsabilityStatsEntry.totalNanScanTimeMs, wifiUsabilityStatsEntry.totalBackgroundScanTimeMs, wifiUsabilityStatsEntry.totalRoamScanTimeMs, wifiUsabilityStatsEntry.totalPnoScanTimeMs, wifiUsabilityStatsEntry.totalHotspot2ScanTimeMs, wifiUsabilityStatsEntry.totalCcaBusyFreqTimeMs, wifiUsabilityStatsEntry.totalRadioOnFreqTimeMs, wifiUsabilityStatsEntry.totalBeaconRx, probeStatus, wifiUsabilityStatsEntry.probeElapsedTimeSinceLastUpdateMs, wifiUsabilityStatsEntry.probeMcsRateSinceLastUpdate, wifiUsabilityStatsEntry.rxLinkSpeedMbps, parseDataNetworkTypeFromProto(wifiUsabilityStatsEntry.cellularDataNetworkType), wifiUsabilityStatsEntry.cellularSignalStrengthDbm, wifiUsabilityStatsEntry.cellularSignalStrengthDb, wifiUsabilityStatsEntry.isSameRegisteredCell);
    }

    private WifiMetricsProto.WifiUsabilityStatsEntry createNewWifiUsabilityStatsEntry(WifiMetricsProto.WifiUsabilityStatsEntry s) {
        WifiMetricsProto.WifiUsabilityStatsEntry out = new WifiMetricsProto.WifiUsabilityStatsEntry();
        out.timeStampMs = s.timeStampMs;
        out.totalTxSuccess = s.totalTxSuccess;
        out.totalTxRetries = s.totalTxRetries;
        out.totalTxBad = s.totalTxBad;
        out.totalRxSuccess = s.totalRxSuccess;
        out.totalRadioOnTimeMs = s.totalRadioOnTimeMs;
        out.totalRadioTxTimeMs = s.totalRadioTxTimeMs;
        out.totalRadioRxTimeMs = s.totalRadioRxTimeMs;
        out.totalScanTimeMs = s.totalScanTimeMs;
        out.totalNanScanTimeMs = s.totalNanScanTimeMs;
        out.totalBackgroundScanTimeMs = s.totalBackgroundScanTimeMs;
        out.totalRoamScanTimeMs = s.totalRoamScanTimeMs;
        out.totalPnoScanTimeMs = s.totalPnoScanTimeMs;
        out.totalHotspot2ScanTimeMs = s.totalHotspot2ScanTimeMs;
        out.rssi = s.rssi;
        out.linkSpeedMbps = s.linkSpeedMbps;
        out.totalCcaBusyFreqTimeMs = s.totalCcaBusyFreqTimeMs;
        out.totalRadioOnFreqTimeMs = s.totalRadioOnFreqTimeMs;
        out.totalBeaconRx = s.totalBeaconRx;
        out.wifiScore = s.wifiScore;
        out.wifiUsabilityScore = s.wifiUsabilityScore;
        out.seqNumToFramework = s.seqNumToFramework;
        out.predictionHorizonSec = s.predictionHorizonSec;
        out.probeStatusSinceLastUpdate = s.probeStatusSinceLastUpdate;
        out.probeElapsedTimeSinceLastUpdateMs = s.probeElapsedTimeSinceLastUpdateMs;
        out.probeMcsRateSinceLastUpdate = s.probeMcsRateSinceLastUpdate;
        out.rxLinkSpeedMbps = s.rxLinkSpeedMbps;
        out.isSameBssidAndFreq = s.isSameBssidAndFreq;
        out.seqNumInsideFramework = s.seqNumInsideFramework;
        out.cellularDataNetworkType = s.cellularDataNetworkType;
        out.cellularSignalStrengthDbm = s.cellularSignalStrengthDbm;
        out.cellularSignalStrengthDb = s.cellularSignalStrengthDb;
        out.isSameRegisteredCell = s.isSameRegisteredCell;
        out.deviceMobilityState = s.deviceMobilityState;
        return out;
    }

    private WifiMetricsProto.WifiUsabilityStats createWifiUsabilityStatsWithLabel(int label, int triggerType, int firmwareAlertCode) {
        WifiMetricsProto.WifiUsabilityStats wifiUsabilityStats = new WifiMetricsProto.WifiUsabilityStats();
        wifiUsabilityStats.label = label;
        wifiUsabilityStats.triggerType = triggerType;
        wifiUsabilityStats.firmwareAlertCode = firmwareAlertCode;
        wifiUsabilityStats.timeStampMs = this.mClock.getElapsedSinceBootMillis();
        wifiUsabilityStats.stats = new WifiMetricsProto.WifiUsabilityStatsEntry[this.mWifiUsabilityStatsEntriesList.size()];
        for (int i = 0; i < this.mWifiUsabilityStatsEntriesList.size(); i++) {
            wifiUsabilityStats.stats[i] = createNewWifiUsabilityStatsEntry(this.mWifiUsabilityStatsEntriesList.get(i));
        }
        return wifiUsabilityStats;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00cf, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addToWifiUsabilityStatsList(int r7, int r8, int r9) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry> r1 = r6.mWifiUsabilityStatsEntriesList     // Catch:{ all -> 0x00d0 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x00d0 }
            if (r1 != 0) goto L_0x00ce
            boolean r1 = r6.mScreenOn     // Catch:{ all -> 0x00d0 }
            if (r1 != 0) goto L_0x0011
            goto L_0x00ce
        L_0x0011:
            r1 = 10
            r2 = 1
            if (r7 != r2) goto L_0x006e
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r3 = r6.mWifiUsabilityStatsListGood     // Catch:{ all -> 0x00d0 }
            boolean r3 = r3.isEmpty()     // Catch:{ all -> 0x00d0 }
            if (r3 != 0) goto L_0x004a
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r3 = r6.mWifiUsabilityStatsListGood     // Catch:{ all -> 0x00d0 }
            java.lang.Object r3 = r3.getLast()     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats r3 = (com.android.server.wifi.nano.WifiMetricsProto.WifiUsabilityStats) r3     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry[] r3 = r3.stats     // Catch:{ all -> 0x00d0 }
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r4 = r6.mWifiUsabilityStatsListGood     // Catch:{ all -> 0x00d0 }
            java.lang.Object r4 = r4.getLast()     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats r4 = (com.android.server.wifi.nano.WifiMetricsProto.WifiUsabilityStats) r4     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry[] r4 = r4.stats     // Catch:{ all -> 0x00d0 }
            int r4 = r4.length     // Catch:{ all -> 0x00d0 }
            int r4 = r4 - r2
            r2 = r3[r4]     // Catch:{ all -> 0x00d0 }
            long r2 = r2.timeStampMs     // Catch:{ all -> 0x00d0 }
            r4 = 3600000(0x36ee80, double:1.7786363E-317)
            long r2 = r2 + r4
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry> r4 = r6.mWifiUsabilityStatsEntriesList     // Catch:{ all -> 0x00d0 }
            java.lang.Object r4 = r4.getLast()     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry r4 = (com.android.server.wifi.nano.WifiMetricsProto.WifiUsabilityStatsEntry) r4     // Catch:{ all -> 0x00d0 }
            long r4 = r4.timeStampMs     // Catch:{ all -> 0x00d0 }
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x00c9
        L_0x004a:
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r2 = r6.mWifiUsabilityStatsListGood     // Catch:{ all -> 0x00d0 }
            int r2 = r2.size()     // Catch:{ all -> 0x00d0 }
            if (r2 < r1) goto L_0x0064
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r2 = r6.mWifiUsabilityStatsListGood     // Catch:{ all -> 0x00d0 }
            java.util.Random r3 = r6.mRand     // Catch:{ all -> 0x00d0 }
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r4 = r6.mWifiUsabilityStatsListGood     // Catch:{ all -> 0x00d0 }
            int r4 = r4.size()     // Catch:{ all -> 0x00d0 }
            int r3 = r3.nextInt(r4)     // Catch:{ all -> 0x00d0 }
            r2.remove(r3)     // Catch:{ all -> 0x00d0 }
            goto L_0x004a
        L_0x0064:
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r1 = r6.mWifiUsabilityStatsListGood     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats r2 = r6.createWifiUsabilityStatsWithLabel(r7, r8, r9)     // Catch:{ all -> 0x00d0 }
            r1.add(r2)     // Catch:{ all -> 0x00d0 }
            goto L_0x00c9
        L_0x006e:
            r3 = -1
            r6.mScoreBreachLowTimeMillis = r3     // Catch:{ all -> 0x00d0 }
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r3 = r6.mWifiUsabilityStatsListBad     // Catch:{ all -> 0x00d0 }
            boolean r3 = r3.isEmpty()     // Catch:{ all -> 0x00d0 }
            if (r3 != 0) goto L_0x00a6
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r3 = r6.mWifiUsabilityStatsListBad     // Catch:{ all -> 0x00d0 }
            java.lang.Object r3 = r3.getLast()     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats r3 = (com.android.server.wifi.nano.WifiMetricsProto.WifiUsabilityStats) r3     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry[] r3 = r3.stats     // Catch:{ all -> 0x00d0 }
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r4 = r6.mWifiUsabilityStatsListBad     // Catch:{ all -> 0x00d0 }
            java.lang.Object r4 = r4.getLast()     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats r4 = (com.android.server.wifi.nano.WifiMetricsProto.WifiUsabilityStats) r4     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry[] r4 = r4.stats     // Catch:{ all -> 0x00d0 }
            int r4 = r4.length     // Catch:{ all -> 0x00d0 }
            int r4 = r4 - r2
            r2 = r3[r4]     // Catch:{ all -> 0x00d0 }
            long r2 = r2.timeStampMs     // Catch:{ all -> 0x00d0 }
            r4 = 120000(0x1d4c0, double:5.9288E-319)
            long r2 = r2 + r4
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry> r4 = r6.mWifiUsabilityStatsEntriesList     // Catch:{ all -> 0x00d0 }
            java.lang.Object r4 = r4.getLast()     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStatsEntry r4 = (com.android.server.wifi.nano.WifiMetricsProto.WifiUsabilityStatsEntry) r4     // Catch:{ all -> 0x00d0 }
            long r4 = r4.timeStampMs     // Catch:{ all -> 0x00d0 }
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x00c9
        L_0x00a6:
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r2 = r6.mWifiUsabilityStatsListBad     // Catch:{ all -> 0x00d0 }
            int r2 = r2.size()     // Catch:{ all -> 0x00d0 }
            if (r2 < r1) goto L_0x00c0
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r2 = r6.mWifiUsabilityStatsListBad     // Catch:{ all -> 0x00d0 }
            java.util.Random r3 = r6.mRand     // Catch:{ all -> 0x00d0 }
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r4 = r6.mWifiUsabilityStatsListBad     // Catch:{ all -> 0x00d0 }
            int r4 = r4.size()     // Catch:{ all -> 0x00d0 }
            int r3 = r3.nextInt(r4)     // Catch:{ all -> 0x00d0 }
            r2.remove(r3)     // Catch:{ all -> 0x00d0 }
            goto L_0x00a6
        L_0x00c0:
            java.util.LinkedList<com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats> r1 = r6.mWifiUsabilityStatsListBad     // Catch:{ all -> 0x00d0 }
            com.android.server.wifi.nano.WifiMetricsProto$WifiUsabilityStats r2 = r6.createWifiUsabilityStatsWithLabel(r7, r8, r9)     // Catch:{ all -> 0x00d0 }
            r1.add(r2)     // Catch:{ all -> 0x00d0 }
        L_0x00c9:
            r1 = 0
            r6.mWifiUsabilityStatsCounter = r1     // Catch:{ all -> 0x00d0 }
            monitor-exit(r0)     // Catch:{ all -> 0x00d0 }
            return
        L_0x00ce:
            monitor-exit(r0)     // Catch:{ all -> 0x00d0 }
            return
        L_0x00d0:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00d0 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiMetrics.addToWifiUsabilityStatsList(int, int, int):void");
    }

    private WifiMetricsProto.DeviceMobilityStatePnoScanStats getOrCreateDeviceMobilityStatePnoScanStats(int deviceMobilityState) {
        WifiMetricsProto.DeviceMobilityStatePnoScanStats stats = this.mMobilityStatePnoStatsMap.get(deviceMobilityState);
        if (stats != null) {
            return stats;
        }
        WifiMetricsProto.DeviceMobilityStatePnoScanStats stats2 = new WifiMetricsProto.DeviceMobilityStatePnoScanStats();
        stats2.deviceMobilityState = deviceMobilityState;
        stats2.numTimesEnteredState = 0;
        stats2.totalDurationMs = 0;
        stats2.pnoDurationMs = 0;
        this.mMobilityStatePnoStatsMap.put(deviceMobilityState, stats2);
        return stats2;
    }

    private void updateCurrentMobilityStateTotalDuration(long now) {
        getOrCreateDeviceMobilityStatePnoScanStats(this.mCurrentDeviceMobilityState).totalDurationMs += now - this.mCurrentDeviceMobilityStateStartMs;
        this.mCurrentDeviceMobilityStateStartMs = now;
    }

    private WifiMetricsProto.PasspointProfileTypeCount[] convertPasspointProfilesToProto(IntCounter passpointProfileTypes) {
        return (WifiMetricsProto.PasspointProfileTypeCount[]) passpointProfileTypes.toProto(WifiMetricsProto.PasspointProfileTypeCount.class, $$Lambda$WifiMetrics$yWvvMMEHVhWYAnW5_JvWYcXUo.INSTANCE);
    }

    static /* synthetic */ WifiMetricsProto.PasspointProfileTypeCount lambda$convertPasspointProfilesToProto$3(int key, int count) {
        WifiMetricsProto.PasspointProfileTypeCount entry = new WifiMetricsProto.PasspointProfileTypeCount();
        entry.eapMethodType = key;
        entry.count = count;
        return entry;
    }

    public void enterDeviceMobilityState(int newState) {
        synchronized (this.mLock) {
            updateCurrentMobilityStateTotalDuration(this.mClock.getElapsedSinceBootMillis());
            if (newState != this.mCurrentDeviceMobilityState) {
                this.mCurrentDeviceMobilityState = newState;
                getOrCreateDeviceMobilityStatePnoScanStats(this.mCurrentDeviceMobilityState).numTimesEnteredState++;
            }
        }
    }

    public void logPnoScanStart() {
        synchronized (this.mLock) {
            long now = this.mClock.getElapsedSinceBootMillis();
            this.mCurrentDeviceMobilityStatePnoScanStartMs = now;
            updateCurrentMobilityStateTotalDuration(now);
        }
    }

    public void logPnoScanStop() {
        synchronized (this.mLock) {
            if (this.mCurrentDeviceMobilityStatePnoScanStartMs < 0) {
                Log.e(TAG, "Called WifiMetrics#logPNoScanStop() without calling WifiMetrics#logPnoScanStart() first!");
                return;
            }
            WifiMetricsProto.DeviceMobilityStatePnoScanStats stats = getOrCreateDeviceMobilityStatePnoScanStats(this.mCurrentDeviceMobilityState);
            long now = this.mClock.getElapsedSinceBootMillis();
            stats.pnoDurationMs += now - this.mCurrentDeviceMobilityStatePnoScanStartMs;
            this.mCurrentDeviceMobilityStatePnoScanStartMs = -1;
            updateCurrentMobilityStateTotalDuration(now);
        }
    }

    public void addOnWifiUsabilityListener(IBinder binder, IOnWifiUsabilityStatsListener listener, int listenerIdentifier) {
        if (!this.mOnWifiUsabilityListeners.add(binder, listener, listenerIdentifier)) {
            Log.e(TAG, "Failed to add listener");
        }
    }

    public void removeOnWifiUsabilityListener(int listenerIdentifier) {
        this.mOnWifiUsabilityListeners.remove(listenerIdentifier);
    }

    public void incrementWifiUsabilityScoreCount(int seqNum, int score, int predictionHorizonSec) {
        if (score >= 0 && score <= 100) {
            synchronized (this.mLock) {
                this.mSeqNumToFramework = seqNum;
                this.mLastWifiUsabilityScore = score;
                this.mLastWifiUsabilityScoreNoReset = score;
                this.mWifiUsabilityScoreCounts.put(score, this.mWifiUsabilityScoreCounts.get(score) + 1);
                this.mLastPredictionHorizonSec = predictionHorizonSec;
                this.mLastPredictionHorizonSecNoReset = predictionHorizonSec;
                boolean wifiWins = this.mWifiWinsUsabilityScore;
                if (score > 50) {
                    wifiWins = true;
                } else if (score < 50) {
                    wifiWins = false;
                }
                if (wifiWins != this.mWifiWinsUsabilityScore) {
                    this.mWifiWinsUsabilityScore = wifiWins;
                    WifiMetricsProto.StaEvent event = new WifiMetricsProto.StaEvent();
                    event.type = 20;
                    addStaEvent(event);
                    if (!wifiWins && this.mScoreBreachLowTimeMillis == -1) {
                        this.mScoreBreachLowTimeMillis = this.mClock.getElapsedSinceBootMillis();
                    }
                }
            }
        }
    }

    public void logLinkProbeSuccess(long timeSinceLastTxSuccessMs, int rssi, int linkSpeed, int elapsedTimeMs) {
        synchronized (this.mLock) {
            this.mProbeStatusSinceLastUpdate = 2;
            this.mProbeElapsedTimeSinceLastUpdateMs = elapsedTimeMs;
            this.mLinkProbeSuccessSecondsSinceLastTxSuccessHistogram.increment((int) (timeSinceLastTxSuccessMs / 1000));
            this.mLinkProbeSuccessRssiCounts.increment(rssi);
            this.mLinkProbeSuccessLinkSpeedCounts.increment(linkSpeed);
            this.mLinkProbeSuccessElapsedTimeMsHistogram.increment(elapsedTimeMs);
            if (this.mLinkProbeStaEventCount < 192) {
                WifiMetricsProto.StaEvent event = new WifiMetricsProto.StaEvent();
                event.type = 21;
                event.linkProbeWasSuccess = true;
                event.linkProbeSuccessElapsedTimeMs = elapsedTimeMs;
                addStaEvent(event);
            }
            this.mLinkProbeStaEventCount++;
        }
    }

    public void logLinkProbeFailure(long timeSinceLastTxSuccessMs, int rssi, int linkSpeed, int reason) {
        synchronized (this.mLock) {
            this.mProbeStatusSinceLastUpdate = 3;
            this.mProbeElapsedTimeSinceLastUpdateMs = ScoringParams.Values.MAX_EXPID;
            this.mLinkProbeFailureSecondsSinceLastTxSuccessHistogram.increment((int) (timeSinceLastTxSuccessMs / 1000));
            this.mLinkProbeFailureRssiCounts.increment(rssi);
            this.mLinkProbeFailureLinkSpeedCounts.increment(linkSpeed);
            this.mLinkProbeFailureReasonCounts.increment(reason);
            if (this.mLinkProbeStaEventCount < 192) {
                WifiMetricsProto.StaEvent event = new WifiMetricsProto.StaEvent();
                event.type = 21;
                event.linkProbeWasSuccess = false;
                event.linkProbeFailureReason = linkProbeFailureReasonToProto(reason);
                addStaEvent(event);
            }
            this.mLinkProbeStaEventCount++;
        }
    }

    public void incrementLinkProbeExperimentProbeCount(String experimentId) {
        synchronized (this.mLock) {
            this.mLinkProbeExperimentProbeCounts.increment(experimentId);
        }
    }

    public void noteWifiConfigStoreReadDuration(int timeMs) {
        synchronized (this.mLock) {
            MetricsUtils.addValueToLinearHistogram(timeMs, this.mWifiConfigStoreReadDurationHistogram, WIFI_CONFIG_STORE_IO_DURATION_BUCKET_RANGES_MS);
        }
    }

    public void noteWifiConfigStoreWriteDuration(int timeMs) {
        synchronized (this.mLock) {
            MetricsUtils.addValueToLinearHistogram(timeMs, this.mWifiConfigStoreWriteDurationHistogram, WIFI_CONFIG_STORE_IO_DURATION_BUCKET_RANGES_MS);
        }
    }

    public void logNetworkSelectionDecision(int experiment1Id, int experiment2Id, boolean isSameDecision, int numNetworkChoices) {
        IntCounter counter;
        if (numNetworkChoices < 0) {
            Log.e(TAG, "numNetworkChoices cannot be negative!");
        } else if (experiment1Id == experiment2Id) {
            Log.e(TAG, "comparing the same experiment id: " + experiment1Id);
        } else {
            Pair<Integer, Integer> key = new Pair<>(Integer.valueOf(experiment1Id), Integer.valueOf(experiment2Id));
            synchronized (this.mLock) {
                NetworkSelectionExperimentResults results = this.mNetworkSelectionExperimentPairNumChoicesCounts.computeIfAbsent(key, $$Lambda$WifiMetrics$4QnnAs9Tryn1ajhBSqNWkgS3hXI.INSTANCE);
                if (isSameDecision) {
                    counter = results.sameSelectionNumChoicesCounter;
                } else {
                    counter = results.differentSelectionNumChoicesCounter;
                }
                counter.increment(numNetworkChoices);
            }
        }
    }

    static /* synthetic */ NetworkSelectionExperimentResults lambda$logNetworkSelectionDecision$4(Pair k) {
        return new NetworkSelectionExperimentResults();
    }

    public void incrementNetworkRequestApiNumRequest() {
        synchronized (this.mLock) {
            this.mWifiNetworkRequestApiLog.numRequest++;
        }
    }

    public void incrementNetworkRequestApiMatchSizeHistogram(int matchSize) {
        synchronized (this.mLock) {
            this.mWifiNetworkRequestApiMatchSizeHistogram.increment(matchSize);
        }
    }

    public void incrementNetworkRequestApiNumConnectSuccess() {
        synchronized (this.mLock) {
            this.mWifiNetworkRequestApiLog.numConnectSuccess++;
        }
    }

    public void incrementNetworkRequestApiNumUserApprovalBypass() {
        synchronized (this.mLock) {
            this.mWifiNetworkRequestApiLog.numUserApprovalBypass++;
        }
    }

    public void incrementNetworkRequestApiNumUserReject() {
        synchronized (this.mLock) {
            this.mWifiNetworkRequestApiLog.numUserReject++;
        }
    }

    public void incrementNetworkRequestApiNumApps() {
        synchronized (this.mLock) {
            this.mWifiNetworkRequestApiLog.numApps++;
        }
    }

    public void incrementNetworkSuggestionApiNumModification() {
        synchronized (this.mLock) {
            this.mWifiNetworkSuggestionApiLog.numModification++;
        }
    }

    public void incrementNetworkSuggestionApiNumConnectSuccess() {
        synchronized (this.mLock) {
            this.mWifiNetworkSuggestionApiLog.numConnectSuccess++;
        }
    }

    public void incrementNetworkSuggestionApiNumConnectFailure() {
        synchronized (this.mLock) {
            this.mWifiNetworkSuggestionApiLog.numConnectFailure++;
        }
    }

    public void noteNetworkSuggestionApiListSizeHistogram(List<Integer> listSizes) {
        synchronized (this.mLock) {
            this.mWifiNetworkSuggestionApiListSizeHistogram.clear();
            for (Integer listSize : listSizes) {
                this.mWifiNetworkSuggestionApiListSizeHistogram.increment(listSize.intValue());
            }
        }
    }

    public void setNominatorForNetwork(int networkId, int nominatorId) {
        synchronized (this.mLock) {
            if (networkId != -1) {
                this.mNetworkIdToNominatorId.put(networkId, nominatorId);
            }
        }
    }

    public void setNetworkSelectorExperimentId(int expId) {
        synchronized (this.mLock) {
            this.mNetworkSelectorExperimentId = expId;
        }
    }

    public void addWifiLockAcqSession(int lockType, long duration) {
        if (lockType == 3) {
            this.mWifiLockHighPerfAcqDurationSecHistogram.increment((int) (duration / 1000));
        } else if (lockType != 4) {
            Log.e(TAG, "addWifiLockAcqSession: Invalid lock type: " + lockType);
        } else {
            this.mWifiLockLowLatencyAcqDurationSecHistogram.increment((int) (duration / 1000));
        }
    }

    public void addWifiLockActiveSession(int lockType, long duration) {
        if (lockType == 3) {
            this.mWifiLockStats.highPerfActiveTimeMs += duration;
            this.mWifiLockHighPerfActiveSessionDurationSecHistogram.increment((int) (duration / 1000));
        } else if (lockType != 4) {
            Log.e(TAG, "addWifiLockActiveSession: Invalid lock type: " + lockType);
        } else {
            this.mWifiLockStats.lowLatencyActiveTimeMs += duration;
            this.mWifiLockLowLatencyActiveSessionDurationSecHistogram.increment((int) (duration / 1000));
        }
    }

    public void incrementNumAddOrUpdateNetworkCalls() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numAddOrUpdateNetworkCalls++;
        }
    }

    public void incrementNumEnableNetworkCalls() {
        synchronized (this.mLock) {
            this.mWifiLogProto.numEnableNetworkCalls++;
        }
    }

    public void incrementNumWifiToggles(boolean isPrivileged, boolean enable) {
        synchronized (this.mLock) {
            if (isPrivileged && enable) {
                try {
                    this.mWifiToggleStats.numToggleOnPrivileged++;
                } catch (Throwable th) {
                    throw th;
                }
            } else if (isPrivileged && !enable) {
                this.mWifiToggleStats.numToggleOffPrivileged++;
            } else if (isPrivileged || !enable) {
                this.mWifiToggleStats.numToggleOffNormal++;
            } else {
                this.mWifiToggleStats.numToggleOnNormal++;
            }
        }
    }

    public void incrementPasspointProvisionFailure(int failureCode) {
        int provisionFailureCode;
        synchronized (this.mLock) {
            switch (failureCode) {
                case 1:
                    provisionFailureCode = 1;
                    break;
                case 2:
                    provisionFailureCode = 2;
                    break;
                case 3:
                    provisionFailureCode = 3;
                    break;
                case 4:
                    provisionFailureCode = 4;
                    break;
                case 5:
                    provisionFailureCode = 5;
                    break;
                case 6:
                    provisionFailureCode = 6;
                    break;
                case 7:
                    provisionFailureCode = 7;
                    break;
                case 8:
                    provisionFailureCode = 8;
                    break;
                case 9:
                    provisionFailureCode = 9;
                    break;
                case 10:
                    provisionFailureCode = 10;
                    break;
                case 11:
                    provisionFailureCode = 11;
                    break;
                case 12:
                    provisionFailureCode = 12;
                    break;
                case 13:
                    provisionFailureCode = 13;
                    break;
                case 14:
                    provisionFailureCode = 14;
                    break;
                case 15:
                    provisionFailureCode = 15;
                    break;
                case 16:
                    provisionFailureCode = 16;
                    break;
                case 17:
                    provisionFailureCode = 17;
                    break;
                case 18:
                    provisionFailureCode = 18;
                    break;
                case 19:
                    provisionFailureCode = 19;
                    break;
                case 20:
                    provisionFailureCode = 20;
                    break;
                case 21:
                    provisionFailureCode = 21;
                    break;
                case 22:
                    provisionFailureCode = 22;
                    break;
                case 23:
                    provisionFailureCode = 23;
                    break;
                default:
                    provisionFailureCode = 0;
                    break;
            }
            this.mPasspointProvisionFailureCounts.increment(provisionFailureCode);
        }
    }

    public void incrementPasspointProvisionSuccess() {
        synchronized (this.mLock) {
            this.mNumProvisionSuccess++;
        }
    }
}
