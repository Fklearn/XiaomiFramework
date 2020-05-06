package com.android.server.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerCompat;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import com.android.internal.telephony.PhoneConstants;
import com.android.server.BatteryService;
import com.android.server.MiuiNetworkManagementService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.Settings;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import miui.util.FeatureParser;

class MiuiNetworkPolicyManagerService {
    private static final String ACTION_NETWORK_CONDITIONS_MEASURED = "android.net.conn.NETWORK_CONDITIONS_MEASURED";
    private static final byte[] AVOID_CHANNEL_IE = {0, -96, -58, 0};
    private static final long BG_MIN_BANDWIDTH = 100000;
    private static final String CLOUD_LOW_LATENCY_APPLIST_FOR_MOBILE = "cloud_block_scan_applist_for_mobile";
    private static final String CLOUD_LOW_LATENCY_WHITELIST = "cloud_lowlatency_whitelist";
    private static final String CLOUD_NETWORK_PRIORITY_ENABLED = "cloud_network_priority_enabled";
    private static final String CLOUD_RESTRICT_WIFI_POWERSAVE_APPLIST = "cloud_block_scan_applist";
    private static final String CLOUD_WMMER_ENABLED = "cloud_wmmer_enabled";
    private static final boolean DEBUG = true;
    private static final int DISABLE_LIMIT_TIMEOUT = 5000;
    private static final int DISABLE_POWER_SAVE_TIMEOUT = 5000;
    private static final int ENABLE_LIMIT_TIMEOUT = 25000;
    private static final String EXTRA_IS_CAPTIVE_PORTAL = "extra_is_captive_portal";
    private static final long FG_MAX_BANDWIDTH = 500000;
    private static final long HISTORY_BANDWIDTH_MIN = 200000;
    private static final int HISTORY_BANDWIDTH_SIZE = 20;
    private static final boolean IS_QCOM = "qcom".equals(FeatureParser.getString("vendor"));
    private static final String LATENCY_ACTION_CHANGE_LEVEL = "com.android.phone.intent.action.CHANGE_LEVEL";
    private static final int LATENCY_DEFAULT = -1;
    private static final String LATENCY_KEY_LEVEL_DL = "Level_DL";
    private static final String LATENCY_KEY_LEVEL_UL = "Level_UL";
    private static final String LATENCY_KEY_RAT_TYPE = "Rat_type";
    private static final int LATENCY_OFF = 0;
    private static final int LATENCY_ON = 1;
    private static final long LATENCY_VALUE_L1 = 1;
    private static final long LATENCY_VALUE_L2 = 2;
    private static final long LATENCY_VALUE_L3 = 3;
    private static final long LATENCY_VALUE_L4 = 4;
    private static final long LATENCY_VALUE_WLAN = 1;
    private static final long LATENCY_VALUE_WWAN = 0;
    private static final String[] LOCAL_NETWORK_PRIORITY_WHITELIST = {"com.tencent.mm", "com.tencent.mobileqq", "com.xiaomi.xmsf", "com.google.android.gms", "com.whatsapp", "com.miui.vpnsdkmanager"};
    private static final long MAX_ROUTER_DETECT_TIME = 3000000;
    private static final int MSG_BANDWIDTH_POLL = 6;
    private static final int MSG_CHECK_ROUTER_MTK = 11;
    private static final int MSG_DISABLE_LIMIT_TIMEOUT = 5;
    private static final int MSG_DISABLE_POWER_SAVE_TIMEOUT = 8;
    private static final int MSG_ENABLE_LIMIT_TIMEOUT = 4;
    private static final int MSG_MOBILE_LATENCY_CHANGED = 9;
    private static final int MSG_SET_RPS_STATS = 10;
    private static final int MSG_SET_TRAFFIC_POLICY = 7;
    private static final int MSG_UID_DATA_ACTIVITY_CHANGED = 3;
    public static final int MSG_UID_STATE_CHANGED = 1;
    public static final int MSG_UID_STATE_GONED = 2;
    private static final byte[] MTK_OUI1 = {0, 12, -25};
    private static final byte[] MTK_OUI2 = {0, 10, 0};
    private static final byte[] MTK_OUI3 = {0, 12, 67};
    private static final byte[] MTK_OUI4 = {0, 23, -91};
    private static final int NETWORK_PRIORITY_MODE_CLOSED = 255;
    private static final int NETWORK_PRIORITY_MODE_FAST = 2;
    private static final int NETWORK_PRIORITY_MODE_NORMAL = 1;
    private static final int NETWORK_PRIORITY_MODE_WMM = 0;
    private static final String NETWORK_PRIORITY_WHITELIST = "cloud_network_priority_whitelist";
    private static final String NOTIFACATION_RECEIVER_PACKAGE = "com.android.phone";
    private static final int POLL_BANDWIDTH_INTERVAL_SECS = 3;
    private static final int POWER_SAVE_IDLETIMER_LABEL = 118;
    private static final int POWER_SAVE_IDLETIMER_TIMEOUT = 2;
    private static final String TAG = "MiuiNetworkPolicy";
    private static final int WMM_AC_BE = 0;
    private static final int WMM_AC_VI = 1;
    private static final int WMM_AC_VO = 2;
    private static final byte[] WPA2_AKM_PSK_IE = {0, 80, -14, 4};
    private static final byte[] WPA_AKM_EAP_IE = {0, 80, -14, 1};
    private static final byte[] WPA_AKM_PSK_IE = {0, 80, -14, 2};
    /* access modifiers changed from: private */
    public static int mMobileLatencyState = -1;
    private static MiuiNetworkPolicyManagerService sSelf;
    private long ROUTER_DETECT_TIME = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
    /* access modifiers changed from: private */
    public MiuiNetworkPolicyAppBuckets mAppBuckets;
    /* access modifiers changed from: private */
    public boolean mCloudWmmerEnable;
    /* access modifiers changed from: private */
    public PhoneConstants.DataState mConnectState = PhoneConstants.DataState.DISCONNECTED;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            boolean z = false;
            switch (msg.what) {
                case 1:
                    MiuiNetworkPolicyManagerService.this.updateUidState(msg.arg1, msg.arg2);
                    break;
                case 2:
                    MiuiNetworkPolicyManagerService.this.removeUidState(msg.arg1);
                    break;
                case 3:
                    String label = (String) msg.obj;
                    int uid = msg.arg1;
                    boolean isActive = msg.arg2 == 1;
                    if (TextUtils.equals(MiuiNetworkPolicyManagerService.this.mInterfaceName, label)) {
                        if (isActive && !MiuiNetworkPolicyManagerService.this.mLimitEnabled) {
                            MiuiNetworkPolicyManagerService.this.mHandler.removeMessages(5);
                            MiuiNetworkPolicyManagerService.this.mHandler.sendEmptyMessageDelayed(4, 25000);
                            MiuiNetworkPolicyManagerService.this.updateLimit(true);
                        } else if (!isActive) {
                            MiuiNetworkPolicyManagerService.this.mHandler.removeMessages(5);
                            MiuiNetworkPolicyManagerService.this.mHandler.removeMessages(4);
                            if (MiuiNetworkPolicyManagerService.this.mLimitEnabled) {
                                MiuiNetworkPolicyManagerService.this.updateLimit(false);
                            }
                        }
                    } else if (TextUtils.equals(String.valueOf(118), label)) {
                        if (isActive && MiuiNetworkPolicyManagerService.this.mPowerSaveEnabled) {
                            MiuiNetworkPolicyManagerService.this.mHandler.removeMessages(8);
                            MiuiNetworkPolicyManagerService.this.mHandler.sendMessageDelayed(MiuiNetworkPolicyManagerService.this.mHandler.obtainMessage(8, uid, msg.arg2), 5000);
                        } else if (!isActive) {
                            MiuiNetworkPolicyManagerService.this.mHandler.removeMessages(8);
                            if (!MiuiNetworkPolicyManagerService.this.mPowerSaveEnabled) {
                                MiuiNetworkPolicyManagerService.this.updatePowerSaveForUidDataActivityChanged(uid, isActive);
                            }
                        }
                    }
                    return true;
                case 4:
                    if (MiuiNetworkPolicyManagerService.this.mLimitEnabled) {
                        MiuiNetworkPolicyManagerService.this.updateLimit(false);
                        MiuiNetworkPolicyManagerService.this.mHandler.sendEmptyMessageDelayed(5, 5000);
                    }
                    return true;
                case 5:
                    if (!MiuiNetworkPolicyManagerService.this.mLimitEnabled) {
                        MiuiNetworkPolicyManagerService.this.updateLimit(true);
                        MiuiNetworkPolicyManagerService.this.mHandler.sendEmptyMessageDelayed(4, 25000);
                    }
                    return true;
                case 6:
                    MiuiNetworkPolicyManagerService.this.calculateBandWidth();
                    MiuiNetworkPolicyManagerService.this.mHandler.sendEmptyMessageDelayed(6, 3000);
                    return true;
                case 7:
                    int unused = MiuiNetworkPolicyManagerService.this.mTrafficPolicyMode = msg.arg1;
                    int networkPriority = MiuiNetworkPolicyManagerService.this.networkPriorityMode();
                    Log.i(MiuiNetworkPolicyManagerService.TAG, "networkPriorityMode = " + networkPriority + " mNetworkPriorityMode =" + MiuiNetworkPolicyManagerService.this.mNetworkPriorityMode + " mWifiConnected=" + MiuiNetworkPolicyManagerService.this.mWifiConnected);
                    if (MiuiNetworkPolicyManagerService.this.mWifiConnected && networkPriority != MiuiNetworkPolicyManagerService.this.mNetworkPriorityMode) {
                        MiuiNetworkPolicyManagerService.this.enableNetworkPriority(networkPriority);
                    }
                    return true;
                case 8:
                    MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService = MiuiNetworkPolicyManagerService.this;
                    int i = msg.arg1;
                    if (msg.arg2 == 1) {
                        z = true;
                    }
                    miuiNetworkPolicyManagerService.updatePowerSaveForUidDataActivityChanged(i, z);
                    return true;
                case 9:
                    MiuiNetworkPolicyManagerService.this.updateMobileLatency();
                    return true;
                case 10:
                    if (msg.obj instanceof Boolean) {
                        boolean unused2 = MiuiNetworkPolicyManagerService.this.mRpsEnabled = ((Boolean) msg.obj).booleanValue();
                        MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService2 = MiuiNetworkPolicyManagerService.this;
                        miuiNetworkPolicyManagerService2.enableRps(miuiNetworkPolicyManagerService2.mRpsEnabled);
                    }
                    return true;
                case 11:
                    if (MiuiNetworkPolicyManagerService.this.mWifiConnected) {
                        MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService3 = MiuiNetworkPolicyManagerService.this;
                        boolean unused3 = miuiNetworkPolicyManagerService3.mIsMtkRouter = miuiNetworkPolicyManagerService3.checkRouterMTK();
                        Log.d(MiuiNetworkPolicyManagerService.TAG, "detect again, router is mtk:" + MiuiNetworkPolicyManagerService.this.mIsMtkRouter);
                        MiuiNetworkPolicyManagerService.this.enableWmmer();
                    }
                    return true;
            }
            return false;
        }
    };
    private Deque<Long> mHistoryBandWidth;
    /* access modifiers changed from: private */
    public String mInterfaceName;
    /* access modifiers changed from: private */
    public boolean mIsCaptivePortal;
    /* access modifiers changed from: private */
    public boolean mIsMtkRouter;
    private long mLastRxBytes;
    /* access modifiers changed from: private */
    public boolean mLimitEnabled;
    /* access modifiers changed from: private */
    public Set<Integer> mLowLatencyApps;
    /* access modifiers changed from: private */
    public Set<String> mLowLatencyAppsPN;
    /* access modifiers changed from: private */
    public Set<Integer> mMobileLowLatencyApps;
    /* access modifiers changed from: private */
    public Set<String> mMobileLowLatencyAppsPN;
    private final BroadcastReceiver mMobileNwReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            PhoneConstants.DataState state;
            if (BatteryService.HealthServiceWrapper.INSTANCE_VENDOR.equals(intent.getStringExtra("apnType")) && MiuiNetworkPolicyManagerService.this.mConnectState != (state = Enum.valueOf(PhoneConstants.DataState.class, intent.getStringExtra("state")))) {
                PhoneConstants.DataState unused = MiuiNetworkPolicyManagerService.this.mConnectState = state;
                Log.i(MiuiNetworkPolicyManagerService.TAG, "mMobileNwReceiver mConnectState = " + MiuiNetworkPolicyManagerService.this.mConnectState);
                if ((state == PhoneConstants.DataState.DISCONNECTED && MiuiNetworkPolicyManagerService.mMobileLatencyState == 1) || (state == PhoneConstants.DataState.CONNECTED && MiuiNetworkPolicyManagerService.mMobileLatencyState == 0)) {
                    MiuiNetworkPolicyManagerService.this.mHandler.sendEmptyMessage(9);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public Set<Integer> mNeedRestrictPowerSaveApps;
    /* access modifiers changed from: private */
    public Set<String> mNeedRestrictPowerSaveAppsPN;
    /* access modifiers changed from: private */
    public MiuiNetworkManagementService mNetworkManager;
    /* access modifiers changed from: private */
    public int mNetworkPriorityMode;
    private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int uid;
            String action = intent.getAction();
            Uri data = intent.getData();
            if (data != null) {
                String packageName = data.getSchemeSpecificPart();
                if (TextUtils.isEmpty(packageName) || (uid = intent.getIntExtra("android.intent.extra.UID", -1)) == -1) {
                    return;
                }
                if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                    Log.i(MiuiNetworkPolicyManagerService.TAG, "ACTION_PACKAGE_ADDED uid = " + uid);
                    if (MiuiNetworkPolicyManagerService.this.mUnRestrictAppsPN.contains(packageName)) {
                        MiuiNetworkPolicyManagerService.this.mUnRestrictApps.add(Integer.valueOf(uid));
                    }
                    if (MiuiNetworkPolicyManagerService.this.mLowLatencyAppsPN.contains(packageName)) {
                        MiuiNetworkPolicyManagerService.this.mLowLatencyApps.add(Integer.valueOf(uid));
                    }
                    if (MiuiNetworkPolicyManagerService.this.mNeedRestrictPowerSaveAppsPN.contains(packageName)) {
                        MiuiNetworkPolicyManagerService.this.mNeedRestrictPowerSaveApps.add(Integer.valueOf(uid));
                    }
                    if (MiuiNetworkPolicyManagerService.isMobileLatencyAllowed() && MiuiNetworkPolicyManagerService.this.mMobileLowLatencyAppsPN != null && MiuiNetworkPolicyManagerService.this.mMobileLowLatencyAppsPN.contains(packageName)) {
                        MiuiNetworkPolicyManagerService.this.mMobileLowLatencyApps.add(Integer.valueOf(uid));
                    }
                    if (MiuiNetworkPolicyManagerService.this.mQosUtils != null) {
                        MiuiNetworkPolicyManagerService.this.mQosUtils.updateAppPN(packageName, uid, true);
                    }
                    if (MiuiNetworkPolicyManagerService.this.mAppBuckets != null) {
                        MiuiNetworkPolicyManagerService.this.mAppBuckets.updateAppPN(packageName, uid, true);
                    }
                } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    Log.i(MiuiNetworkPolicyManagerService.TAG, "ACTION_PACKAGE_REMOVED uid = " + uid);
                    if (MiuiNetworkPolicyManagerService.this.mUnRestrictAppsPN.contains(packageName)) {
                        MiuiNetworkPolicyManagerService.this.mUnRestrictApps.remove(Integer.valueOf(uid));
                    }
                    if (MiuiNetworkPolicyManagerService.this.mLowLatencyAppsPN.contains(packageName)) {
                        MiuiNetworkPolicyManagerService.this.mLowLatencyApps.remove(Integer.valueOf(uid));
                    }
                    if (MiuiNetworkPolicyManagerService.this.mNeedRestrictPowerSaveAppsPN.contains(packageName)) {
                        MiuiNetworkPolicyManagerService.this.mNeedRestrictPowerSaveApps.remove(Integer.valueOf(uid));
                    }
                    if (MiuiNetworkPolicyManagerService.isMobileLatencyAllowed() && MiuiNetworkPolicyManagerService.this.mMobileLowLatencyAppsPN != null && MiuiNetworkPolicyManagerService.this.mMobileLowLatencyAppsPN.contains(packageName)) {
                        MiuiNetworkPolicyManagerService.this.mMobileLowLatencyApps.remove(Integer.valueOf(uid));
                    }
                    if (MiuiNetworkPolicyManagerService.this.mQosUtils != null) {
                        MiuiNetworkPolicyManagerService.this.mQosUtils.updateAppPN(packageName, uid, false);
                    }
                    if (MiuiNetworkPolicyManagerService.this.mAppBuckets != null) {
                        MiuiNetworkPolicyManagerService.this.mAppBuckets.updateAppPN(packageName, uid, false);
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mPowerSaveEnabled = true;
    /* access modifiers changed from: private */
    public MiuiNetworkPolicyQosUtils mQosUtils;
    /* access modifiers changed from: private */
    public boolean mRpsEnabled = false;
    /* access modifiers changed from: private */
    public MiuiNetworkPolicyServiceSupport mSupport;
    /* access modifiers changed from: private */
    public int mTrafficPolicyMode;
    private MiuiNetworkManagementService.NetworkEventObserver mUidDataActivityObserver = new MiuiNetworkManagementService.NetworkEventObserver() {
        public void uidDataActivityChanged(String label, int uid, boolean active, long tsNanos) {
            Log.i(MiuiNetworkPolicyManagerService.TAG, "label " + label + ", uid " + uid + ", active " + active + ", tsNanos " + tsNanos);
            MiuiNetworkPolicyManagerService.this.mHandler.sendMessage(MiuiNetworkPolicyManagerService.this.mHandler.obtainMessage(3, uid, active, label));
        }
    };
    final SparseIntArray mUidState = new SparseIntArray();
    /* access modifiers changed from: private */
    public Set<Integer> mUnRestrictApps;
    /* access modifiers changed from: private */
    public Set<String> mUnRestrictAppsPN;
    /* access modifiers changed from: private */
    public boolean mWifiConnected;
    private final BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(MiuiNetworkPolicyManagerService.TAG, "mWifiStateReceiver onReceive: " + action);
            boolean z = false;
            if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                NetworkInfo netInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                boolean wasConnected = MiuiNetworkPolicyManagerService.this.mWifiConnected;
                MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService = MiuiNetworkPolicyManagerService.this;
                if (netInfo != null && netInfo.isConnected()) {
                    z = true;
                }
                boolean unused = miuiNetworkPolicyManagerService.mWifiConnected = z;
                Log.i(MiuiNetworkPolicyManagerService.TAG, "wasConnected = " + wasConnected + " mWifiConnected = " + MiuiNetworkPolicyManagerService.this.mWifiConnected + " mNetworkPriorityMode =" + MiuiNetworkPolicyManagerService.this.mNetworkPriorityMode);
                if (MiuiNetworkPolicyManagerService.this.mWifiConnected != wasConnected) {
                    MiuiNetworkPolicyManagerService.this.enablePowerSave(true);
                    if (MiuiNetworkPolicyManagerService.this.mWifiConnected) {
                        MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService2 = MiuiNetworkPolicyManagerService.this;
                        String unused2 = miuiNetworkPolicyManagerService2.mInterfaceName = miuiNetworkPolicyManagerService2.mSupport.updateIface(MiuiNetworkPolicyManagerService.this.mInterfaceName);
                        MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService3 = MiuiNetworkPolicyManagerService.this;
                        boolean unused3 = miuiNetworkPolicyManagerService3.mIsMtkRouter = miuiNetworkPolicyManagerService3.checkRouterMTK();
                        Log.d(MiuiNetworkPolicyManagerService.TAG, "router is mtk: " + MiuiNetworkPolicyManagerService.this.mIsMtkRouter);
                    }
                    MiuiNetworkPolicyManagerService.this.enableWmmer();
                    int networkPriority = MiuiNetworkPolicyManagerService.this.networkPriorityMode();
                    if (MiuiNetworkPolicyManagerService.this.isLimitterEnabled(networkPriority)) {
                        MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService4 = MiuiNetworkPolicyManagerService.this;
                        miuiNetworkPolicyManagerService4.enableNetworkPriority(miuiNetworkPolicyManagerService4.mWifiConnected ? networkPriority : 255);
                    }
                }
            } else if ("android.net.conn.NETWORK_CONDITIONS_MEASURED".equals(action)) {
                boolean wasCaptivePortal = MiuiNetworkPolicyManagerService.this.mIsCaptivePortal;
                boolean unused4 = MiuiNetworkPolicyManagerService.this.mIsCaptivePortal = intent.getBooleanExtra("extra_is_captive_portal", false);
                Log.i(MiuiNetworkPolicyManagerService.TAG, "network was: " + wasCaptivePortal + " captive portal, and now is " + MiuiNetworkPolicyManagerService.this.mIsCaptivePortal);
                if (wasCaptivePortal != MiuiNetworkPolicyManagerService.this.mIsCaptivePortal && MiuiNetworkPolicyManagerService.this.mWmmerEnable) {
                    MiuiNetworkPolicyManagerService.this.updateRuleGlobal();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mWmmerEnable;

    static MiuiNetworkPolicyManagerService make(Context context) {
        sSelf = new MiuiNetworkPolicyManagerService(context);
        return sSelf;
    }

    public static MiuiNetworkPolicyManagerService get() {
        MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService = sSelf;
        if (miuiNetworkPolicyManagerService != null) {
            return miuiNetworkPolicyManagerService;
        }
        throw new RuntimeException("MiuiNetworkPolicyManagerService has not been initialized ");
    }

    private MiuiNetworkPolicyManagerService(Context context) {
        this.mContext = context;
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        this.mHandler = new Handler(thread.getLooper(), this.mHandlerCallback);
        this.mUnRestrictApps = new HashSet();
        this.mLowLatencyApps = new HashSet();
        this.mNeedRestrictPowerSaveApps = new HashSet();
        this.mMobileLowLatencyApps = new HashSet();
        this.mHistoryBandWidth = new LinkedList();
        this.mNetworkPriorityMode = 255;
        this.mTrafficPolicyMode = 0;
        this.mSupport = new MiuiNetworkPolicyServiceSupport(this.mContext, this.mHandler);
        if (isQosFeatureAllowed()) {
            this.mQosUtils = new MiuiNetworkPolicyQosUtils(this.mContext, this.mHandler);
        }
        this.mAppBuckets = new MiuiNetworkPolicyAppBuckets(this.mContext);
    }

    /* access modifiers changed from: protected */
    public void systemReady() {
        this.mInterfaceName = SystemProperties.get("wifi.interface", "wlan0");
        this.mNetworkManager = MiuiNetworkManagementService.getInstance();
        this.mNetworkManager.setNetworkEventObserver(this.mUidDataActivityObserver);
        this.mNetworkManager.enableWmmer(false);
        this.mNetworkManager.enableLimitter(false);
        IntentFilter wifiStateFilter = new IntentFilter("android.net.wifi.STATE_CHANGE");
        wifiStateFilter.addAction("android.net.conn.NETWORK_CONDITIONS_MEASURED");
        this.mContext.registerReceiver(this.mWifiStateReceiver, wifiStateFilter, (String) null, this.mHandler);
        registerWmmerEnableChangedObserver();
        registerNetworkProrityModeChangedObserver();
        networkPriorityCloudControl();
        registerUnRestirctAppsChangedObserver();
        registerLowLatencyAppsChangedObserver();
        registerRestrictPowerSaveAppsChangedObserver();
        if (Build.VERSION.SDK_INT < 26) {
            registerMiuiOptimizationChangedObserver();
        }
        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
        packageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        packageFilter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiver(this.mPackageReceiver, packageFilter, (String) null, this.mHandler);
        this.mSupport.registerUidObserver();
        if (isMobileLatencyAllowed()) {
            registerMobileLatencyAppsChangedObserver();
            this.mContext.registerReceiver(this.mMobileNwReceiver, new IntentFilter("android.intent.action.ANY_DATA_STATE"), (String) null, this.mHandler);
        }
        MiuiNetworkPolicyQosUtils miuiNetworkPolicyQosUtils = this.mQosUtils;
        if (miuiNetworkPolicyQosUtils != null) {
            miuiNetworkPolicyQosUtils.systemReady(this.mNetworkManager);
        }
        MiuiNetworkPolicyAppBuckets miuiNetworkPolicyAppBuckets = this.mAppBuckets;
        if (miuiNetworkPolicyAppBuckets != null) {
            miuiNetworkPolicyAppBuckets.systemReady();
        }
    }

    private int getWmmForUidState(int uid, int state) {
        if (!isStateWmmed(state)) {
            return 0;
        }
        if (!this.mLowLatencyApps.contains(Integer.valueOf(uid)) || this.mIsCaptivePortal) {
            return 1;
        }
        return 2;
    }

    private boolean isStateWmmed(int state) {
        return state >= 0 && state <= 2;
    }

    private boolean isStateUnRestrictedForUid(int uid, int state) {
        return state >= 0 && (state <= 5 || (state < 20 && this.mUnRestrictApps.contains(Integer.valueOf(uid))));
    }

    private static boolean isUidValidForRules(int uid) {
        if (uid == 1013 || uid == 1019 || UserHandle.isApp(uid)) {
            return true;
        }
        return false;
    }

    private void updateWmmForUidState(int uid, int state) {
        if (this.mWmmerEnable) {
            Log.i(TAG, "updateWmmForUidState uid: " + uid + " state: " + state + " wmm: " + getWmmForUidState(uid, state));
            this.mNetworkManager.updateWmm(uid, getWmmForUidState(uid, state));
        }
    }

    private void updateLimitterForUidState(int uid, int state) {
        if (isLimitterEnabled()) {
            this.mNetworkManager.whiteListUid(uid, isStateUnRestrictedForUid(uid, state));
        }
    }

    private void updateRulesForUidStateChange(int uid, int oldUidState, int newUidState) {
        if (isUidValidForRules(uid)) {
            if (getWmmForUidState(uid, oldUidState) != getWmmForUidState(uid, newUidState)) {
                updateWmmForUidState(uid, newUidState);
            }
            if (isStateUnRestrictedForUid(uid, oldUidState) != isStateUnRestrictedForUid(uid, newUidState)) {
                updateLimitterForUidState(uid, newUidState);
            }
            if (isPowerSaveRestrictedForUid(uid, oldUidState) != isPowerSaveRestrictedForUid(uid, newUidState)) {
                updatePowerSaveStateForUidState(uid, newUidState);
            }
        }
    }

    private boolean isPowerSaveRestrictedForUid(int uid, int state) {
        return state == 2 && this.mWifiConnected && this.mNeedRestrictPowerSaveApps.contains(Integer.valueOf(uid));
    }

    /* access modifiers changed from: private */
    public void updatePowerSaveForUidDataActivityChanged(int uid, boolean active) {
        int state = this.mUidState.get(uid, 20);
        boolean restrict = isPowerSaveRestrictedForUid(uid, state);
        Log.i(TAG, "update ps for data activity, uid = " + uid + ", state= " + state + ", restrict = " + restrict + ", active = " + active + ", mPS = " + this.mPowerSaveEnabled);
        if (active && restrict && this.mPowerSaveEnabled) {
            enablePowerSave(false);
        } else if (!active && !this.mPowerSaveEnabled) {
            enablePowerSave(true);
        }
    }

    private void updatePowerSaveStateForUidState(int uid, int state) {
        boolean restrict = isPowerSaveRestrictedForUid(uid, state);
        this.mNetworkManager.listenUidDataActivity(OsConstants.IPPROTO_UDP, uid, 118, 2, restrict);
        enablePowerSave(!restrict);
    }

    /* access modifiers changed from: private */
    public void enablePowerSave(boolean enable) {
        Log.i(TAG, "enable ps, mPS = " + this.mPowerSaveEnabled + ", enable = " + enable);
        if (this.mPowerSaveEnabled != enable) {
            this.mPowerSaveEnabled = enable;
            this.mSupport.enablePowerSave(enable);
        }
    }

    /* access modifiers changed from: private */
    public void updateRuleGlobal() {
        int size = this.mUidState.size();
        for (int i = 0; i < size; i++) {
            int uid = this.mUidState.keyAt(i);
            int state = this.mUidState.get(uid, 20);
            Log.i(TAG, "updateRuleGlobal uid = " + uid + ", state = " + state);
            updateRulesForUidStateChange(uid, 20, state);
        }
    }

    /* access modifiers changed from: private */
    public void calculateBandWidth() {
        long rxBytes = TrafficStats.getRxBytes(this.mInterfaceName);
        if (rxBytes < 0 || this.mLastRxBytes > rxBytes) {
            Log.i(TAG, "rxByte: " + rxBytes + ", mLastRxBytes: " + this.mLastRxBytes);
            this.mLastRxBytes = 0;
        }
        if (this.mLastRxBytes != 0 || rxBytes < 0) {
            long bwBps = (rxBytes - this.mLastRxBytes) / 3;
            if (bwBps >= HISTORY_BANDWIDTH_MIN) {
                addHistoryBandWidth(bwBps);
            }
            Log.i(TAG, "bandwidth: " + (bwBps / 1000) + " KB/s, Max bandwidth: " + (((Long) Collections.max(this.mHistoryBandWidth)).longValue() / 1000) + " KB/s");
            this.mLastRxBytes = rxBytes;
            return;
        }
        this.mLastRxBytes = rxBytes;
    }

    private void addHistoryBandWidth(long bwBps) {
        if (this.mHistoryBandWidth.size() >= 20) {
            this.mHistoryBandWidth.removeLast();
        }
        this.mHistoryBandWidth.addFirst(Long.valueOf(bwBps));
    }

    private void enableBandwidthPoll(boolean enabled) {
        this.mHandler.removeMessages(6);
        this.mHistoryBandWidth.clear();
        if (enabled) {
            this.mHandler.sendEmptyMessage(6);
            addHistoryBandWidth(HISTORY_BANDWIDTH_MIN);
        }
    }

    /* access modifiers changed from: private */
    public void networkPriorityCloudControl() {
        String cvalue = Settings.System.getStringForUser(this.mContext.getContentResolver(), CLOUD_NETWORK_PRIORITY_ENABLED, -2);
        if ("mediatek".equals(FeatureParser.getString("vendor"))) {
            cvalue = "off";
        }
        try {
            SystemProperties.set("sys.net.support.netprio", TextUtils.equals("off", cvalue) ? "false" : "true");
        } catch (Exception e) {
            Log.e(TAG, "Failed to set network priority support config", e);
        }
    }

    /* access modifiers changed from: private */
    public int networkPriorityMode() {
        String cvalue = Settings.System.getStringForUser(this.mContext.getContentResolver(), CLOUD_NETWORK_PRIORITY_ENABLED, -2);
        if ("mediatek".equals(FeatureParser.getString("vendor"))) {
            cvalue = "off";
        }
        if (!(!"off".equals(cvalue))) {
            return 255;
        }
        int i = this.mTrafficPolicyMode;
        if (i != 0) {
            return i;
        }
        int def = 0;
        if ("on".equals(cvalue)) {
            def = 1;
        } else if ("fast".equals(cvalue)) {
            def = 2;
        }
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), "user_network_priority_enabled", def, -2);
    }

    private boolean isLimitterEnabled() {
        return isLimitterEnabled(this.mNetworkPriorityMode);
    }

    /* access modifiers changed from: private */
    public boolean isLimitterEnabled(int mode) {
        return mode == 1 || mode == 2;
    }

    private boolean isWmmerEnabled() {
        return this.mCloudWmmerEnable && this.mWifiConnected && !this.mIsMtkRouter;
    }

    private boolean validatePriorityMode(int mode) {
        return mode == 0 || mode == 1 || mode == 2 || mode == 255;
    }

    public boolean setNetworkTrafficPolicy(int mode) {
        if (!validatePriorityMode(mode)) {
            return false;
        }
        this.mHandler.removeMessages(7);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(7, mode, 0));
        return true;
    }

    public boolean setRpsStatus(boolean enable) {
        Log.d(TAG, "setRpsStatus/in [" + enable + "]");
        if (this.mRpsEnabled != enable) {
            this.mHandler.removeMessages(10);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(10, Boolean.valueOf(enable)));
            Log.d(TAG, "setRpsStatus/out [ true ]");
            return true;
        }
        Log.d(TAG, "setRpsStatus/out [ false]");
        return false;
    }

    /* access modifiers changed from: private */
    public void enableNetworkPriority(int mode) {
        boolean isNeedUpdate = false;
        boolean wasLimitterEnabled = isLimitterEnabled();
        boolean isLimitterEnabled = isLimitterEnabled(mode);
        if (wasLimitterEnabled && !isLimitterEnabled) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(3, 0, 0));
            this.mNetworkManager.enableLimitter(false);
        } else if (!wasLimitterEnabled && isLimitterEnabled) {
            this.mNetworkManager.enableLimitter(true);
            isNeedUpdate = true;
        }
        if (mode == 1) {
            enableBandwidthPoll(true);
        } else {
            enableBandwidthPoll(false);
        }
        this.mNetworkPriorityMode = mode;
        if (isNeedUpdate) {
            updateRuleGlobal();
        }
    }

    /* access modifiers changed from: private */
    public void enableWmmer() {
        enableWmmer(isWmmerEnabled());
    }

    /* access modifiers changed from: private */
    public void enableWmmer(boolean enable) {
        if (this.mWmmerEnable != enable) {
            this.mNetworkManager.enableWmmer(enable);
            if (enable) {
                updateRuleGlobal();
            }
            this.mWmmerEnable = enable;
        }
    }

    private void registerWmmerEnableChangedObserver() {
        final ContentObserver observer = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                String value = Settings.System.getStringForUser(MiuiNetworkPolicyManagerService.this.mContext.getContentResolver(), MiuiNetworkPolicyManagerService.CLOUD_WMMER_ENABLED, -2);
                boolean z = false;
                if ("mediatek".equals(FeatureParser.getString("vendor"))) {
                    boolean unused = MiuiNetworkPolicyManagerService.this.mCloudWmmerEnable = false;
                } else {
                    MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService = MiuiNetworkPolicyManagerService.this;
                    if (value != null && "on".equals(value)) {
                        z = true;
                    }
                    boolean unused2 = miuiNetworkPolicyManagerService.mCloudWmmerEnable = z;
                }
                Log.d(MiuiNetworkPolicyManagerService.TAG, " wmmer value:" + value + " mCloudWmmerEnable:" + MiuiNetworkPolicyManagerService.this.mCloudWmmerEnable);
                MiuiNetworkPolicyManagerService.this.enableWmmer();
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_WMMER_ENABLED), false, observer, -2);
        this.mHandler.post(new Runnable() {
            public void run() {
                observer.onChange(false);
            }
        });
    }

    private void registerNetworkProrityModeChangedObserver() {
        final ContentObserver observer = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                if (uri != null && uri.equals(Settings.System.getUriFor(MiuiNetworkPolicyManagerService.CLOUD_NETWORK_PRIORITY_ENABLED))) {
                    MiuiNetworkPolicyManagerService.this.networkPriorityCloudControl();
                }
                int networkPriority = MiuiNetworkPolicyManagerService.this.networkPriorityMode();
                Log.i(MiuiNetworkPolicyManagerService.TAG, "networkPriorityMode = " + networkPriority + " mNetworkPriorityMode =" + MiuiNetworkPolicyManagerService.this.mNetworkPriorityMode + " mWifiConnected=" + MiuiNetworkPolicyManagerService.this.mWifiConnected);
                if (networkPriority != MiuiNetworkPolicyManagerService.this.mNetworkPriorityMode && MiuiNetworkPolicyManagerService.this.mWifiConnected) {
                    MiuiNetworkPolicyManagerService.this.enableNetworkPriority(networkPriority);
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_NETWORK_PRIORITY_ENABLED), false, observer, -2);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("user_network_priority_enabled"), false, observer, -2);
        this.mHandler.post(new Runnable() {
            public void run() {
                observer.onChange(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean checkRouterMTK() {
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        String bssid = wifiManager.getConnectionInfo().getBSSID();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        if (bssid == null || scanResults.isEmpty()) {
            long j = this.ROUTER_DETECT_TIME;
            if (j < MAX_ROUTER_DETECT_TIME) {
                this.mHandler.sendEmptyMessageDelayed(11, j);
                this.ROUTER_DETECT_TIME *= 2;
                return true;
            }
        }
        this.ROUTER_DETECT_TIME = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        for (ScanResult scanResult : scanResults) {
            if (TextUtils.equals(bssid, scanResult.BSSID) && checkIEMTK(scanResult.informationElements)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIEMTK(ScanResult.InformationElement[] infoElements) {
        if (infoElements == null || infoElements.length == 0) {
            return true;
        }
        for (ScanResult.InformationElement ie : infoElements) {
            if (ie.id == 221) {
                byte[] value = Arrays.copyOf(ie.bytes, 4);
                if (!Arrays.equals(value, AVOID_CHANNEL_IE) && !Arrays.equals(value, WPA_AKM_PSK_IE) && !Arrays.equals(value, WPA_AKM_EAP_IE) && !Arrays.equals(value, WPA2_AKM_PSK_IE)) {
                    byte[] oui = Arrays.copyOf(ie.bytes, 3);
                    if (Arrays.equals(oui, MTK_OUI1) || Arrays.equals(oui, MTK_OUI2) || Arrays.equals(oui, MTK_OUI3) || Arrays.equals(oui, MTK_OUI4)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateUidState(int uid, int uidState) {
        Log.i(TAG, "updateUidState uid = " + uid + ", uidState = " + uidState);
        int oldUidState = this.mUidState.get(uid, 20);
        if (oldUidState != uidState) {
            this.mUidState.put(uid, uidState);
            updateRulesForUidStateChange(uid, oldUidState, uidState);
            if (isMobileLatencyAllowed()) {
                updateMobileLatencyForUidStateChange(uid, oldUidState, uidState);
            }
            MiuiNetworkPolicyQosUtils miuiNetworkPolicyQosUtils = this.mQosUtils;
            if (miuiNetworkPolicyQosUtils != null) {
                miuiNetworkPolicyQosUtils.updateQosForUidStateChange(uid, oldUidState, uidState);
            }
            MiuiNetworkPolicyAppBuckets miuiNetworkPolicyAppBuckets = this.mAppBuckets;
            if (miuiNetworkPolicyAppBuckets != null) {
                miuiNetworkPolicyAppBuckets.updateAppBucketsForUidStateChange(uid, oldUidState, uidState);
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeUidState(int uid) {
        Log.i(TAG, "removeUidState uid = " + uid);
        int index = this.mUidState.indexOfKey(uid);
        if (index >= 0) {
            int oldUidState = this.mUidState.valueAt(index);
            this.mUidState.removeAt(index);
            if (oldUidState != 20) {
                updateRulesForUidStateChange(uid, oldUidState, 20);
                if (isMobileLatencyAllowed()) {
                    updateMobileLatencyForUidStateChange(uid, oldUidState, 20);
                }
                MiuiNetworkPolicyQosUtils miuiNetworkPolicyQosUtils = this.mQosUtils;
                if (miuiNetworkPolicyQosUtils != null) {
                    miuiNetworkPolicyQosUtils.updateQosForUidStateChange(uid, oldUidState, 20);
                }
                MiuiNetworkPolicyAppBuckets miuiNetworkPolicyAppBuckets = this.mAppBuckets;
                if (miuiNetworkPolicyAppBuckets != null) {
                    miuiNetworkPolicyAppBuckets.updateAppBucketsForUidStateChange(uid, oldUidState, 20);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public Set<String> getUnRestrictedApps(Context context) {
        String whiteString = Settings.System.getStringForUser(context.getContentResolver(), NETWORK_PRIORITY_WHITELIST, -2);
        Set<String> whiteList = new HashSet<>();
        if (TextUtils.isEmpty(whiteString)) {
            int i = 0;
            while (true) {
                String[] strArr = LOCAL_NETWORK_PRIORITY_WHITELIST;
                if (i >= strArr.length) {
                    break;
                }
                whiteList.add(strArr[i]);
                i++;
            }
        } else {
            String[] packages = whiteString.split(",");
            if (packages != null) {
                for (String add : packages) {
                    whiteList.add(add);
                }
            }
        }
        return whiteList;
    }

    private void registerRestrictPowerSaveAppsChangedObserver() {
        final ContentObserver observer = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                PackageManager pm = MiuiNetworkPolicyManagerService.this.mContext.getPackageManager();
                List<UserInfo> users = ((UserManager) MiuiNetworkPolicyManagerService.this.mContext.getSystemService("user")).getUsers();
                MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService = MiuiNetworkPolicyManagerService.this;
                Set unused = miuiNetworkPolicyManagerService.mNeedRestrictPowerSaveAppsPN = miuiNetworkPolicyManagerService.getNeedRestrictPowerSaveApps(miuiNetworkPolicyManagerService.mContext);
                MiuiNetworkPolicyManagerService.this.mNeedRestrictPowerSaveApps.clear();
                if (!MiuiNetworkPolicyManagerService.this.mNeedRestrictPowerSaveAppsPN.isEmpty()) {
                    for (UserInfo user : users) {
                        for (PackageInfo app : PackageManagerCompat.getInstalledPackagesAsUser(pm, 0, user.id)) {
                            if (!(app.packageName == null || app.applicationInfo == null || !MiuiNetworkPolicyManagerService.this.mNeedRestrictPowerSaveAppsPN.contains(app.packageName))) {
                                MiuiNetworkPolicyManagerService.this.mNeedRestrictPowerSaveApps.add(Integer.valueOf(UserHandle.getUid(user.id, app.applicationInfo.uid)));
                            }
                        }
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_RESTRICT_WIFI_POWERSAVE_APPLIST), false, observer, -2);
        this.mHandler.post(new Runnable() {
            public void run() {
                observer.onChange(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public Set<String> getNeedRestrictPowerSaveApps(Context context) {
        String[] packages;
        String appString = Settings.System.getStringForUser(context.getContentResolver(), CLOUD_RESTRICT_WIFI_POWERSAVE_APPLIST, -2);
        Set<String> appList = new HashSet<>();
        if (!TextUtils.isEmpty(appString) && (packages = appString.split(",")) != null) {
            for (String add : packages) {
                appList.add(add);
            }
        }
        return appList;
    }

    private void registerUnRestirctAppsChangedObserver() {
        final ContentObserver observer = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                PackageManager pm = MiuiNetworkPolicyManagerService.this.mContext.getPackageManager();
                List<UserInfo> users = ((UserManager) MiuiNetworkPolicyManagerService.this.mContext.getSystemService("user")).getUsers();
                MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService = MiuiNetworkPolicyManagerService.this;
                Set unused = miuiNetworkPolicyManagerService.mUnRestrictAppsPN = miuiNetworkPolicyManagerService.getUnRestrictedApps(miuiNetworkPolicyManagerService.mContext);
                MiuiNetworkPolicyManagerService.this.mUnRestrictApps.clear();
                if (!MiuiNetworkPolicyManagerService.this.mUnRestrictAppsPN.isEmpty()) {
                    for (UserInfo user : users) {
                        for (PackageInfo app : PackageManagerCompat.getInstalledPackagesAsUser(pm, 0, user.id)) {
                            if (!(app.packageName == null || app.applicationInfo == null || !MiuiNetworkPolicyManagerService.this.mUnRestrictAppsPN.contains(app.packageName))) {
                                MiuiNetworkPolicyManagerService.this.mUnRestrictApps.add(Integer.valueOf(UserHandle.getUid(user.id, app.applicationInfo.uid)));
                            }
                        }
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(NETWORK_PRIORITY_WHITELIST), false, observer, -2);
        this.mHandler.post(new Runnable() {
            public void run() {
                observer.onChange(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public Set<String> getLowLatencyApps(Context context) {
        String[] packages;
        String whiteString = Settings.System.getStringForUser(context.getContentResolver(), CLOUD_LOW_LATENCY_WHITELIST, -2);
        Set<String> whiteList = new HashSet<>();
        if (!TextUtils.isEmpty(whiteString) && (packages = whiteString.split(",")) != null) {
            for (String add : packages) {
                whiteList.add(add);
            }
        }
        return whiteList;
    }

    private void registerLowLatencyAppsChangedObserver() {
        final ContentObserver observer = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                PackageManager pm = MiuiNetworkPolicyManagerService.this.mContext.getPackageManager();
                List<UserInfo> users = ((UserManager) MiuiNetworkPolicyManagerService.this.mContext.getSystemService("user")).getUsers();
                MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService = MiuiNetworkPolicyManagerService.this;
                Set unused = miuiNetworkPolicyManagerService.mLowLatencyAppsPN = miuiNetworkPolicyManagerService.getLowLatencyApps(miuiNetworkPolicyManagerService.mContext);
                MiuiNetworkPolicyManagerService.this.mLowLatencyApps.clear();
                if (!MiuiNetworkPolicyManagerService.this.mLowLatencyAppsPN.isEmpty()) {
                    for (UserInfo user : users) {
                        for (PackageInfo app : PackageManagerCompat.getInstalledPackagesAsUser(pm, 0, user.id)) {
                            if (!(app.packageName == null || app.applicationInfo == null || !MiuiNetworkPolicyManagerService.this.mLowLatencyAppsPN.contains(app.packageName))) {
                                MiuiNetworkPolicyManagerService.this.mLowLatencyApps.add(Integer.valueOf(UserHandle.getUid(user.id, app.applicationInfo.uid)));
                            }
                        }
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_LOW_LATENCY_WHITELIST), false, observer, -2);
        this.mHandler.post(new Runnable() {
            public void run() {
                observer.onChange(false);
            }
        });
    }

    private void registerMiuiOptimizationChangedObserver() {
        final ContentObserver observer = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                boolean isCtsMode = !SystemProperties.getBoolean("persist.sys.miui_optimization", !SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.cts")));
                int networkPriority = MiuiNetworkPolicyManagerService.this.networkPriorityMode();
                Log.i(MiuiNetworkPolicyManagerService.TAG, "miui optimization mode changed: " + isCtsMode + ", current network priority: " + networkPriority);
                if (isCtsMode) {
                    MiuiNetworkPolicyManagerService.this.enableNetworkPriority(255);
                    MiuiNetworkPolicyManagerService.this.enableWmmer(false);
                    MiuiNetworkPolicyManagerService.this.mNetworkManager.enableIptablesRestore(false);
                    return;
                }
                MiuiNetworkPolicyManagerService.this.mNetworkManager.enableIptablesRestore(true);
                MiuiNetworkPolicyManagerService.this.enableWmmer();
                if (MiuiNetworkPolicyManagerService.this.mWifiConnected && networkPriority != 255) {
                    MiuiNetworkPolicyManagerService.this.enableNetworkPriority(networkPriority);
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(MiuiSettings.Secure.MIUI_OPTIMIZATION), false, observer, -2);
        this.mHandler.post(new Runnable() {
            public void run() {
                observer.onChange(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateLimit(boolean enabled) {
        if (this.mLimitEnabled != enabled) {
            long bwBps = 0;
            if (enabled && this.mNetworkPriorityMode == 1) {
                long bwBps2 = HISTORY_BANDWIDTH_MIN;
                if (this.mHistoryBandWidth.size() > 0) {
                    bwBps2 = ((Long) Collections.max(this.mHistoryBandWidth)).longValue();
                }
                bwBps = Math.max(bwBps2 - Math.min((80 * bwBps2) / 100, FG_MAX_BANDWIDTH), BG_MIN_BANDWIDTH);
            }
            this.mNetworkManager.setLimit(enabled, bwBps);
            this.mLimitEnabled = enabled;
        }
    }

    private void registerMobileLatencyAppsChangedObserver() {
        final ContentObserver observer = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                PackageManager pm = MiuiNetworkPolicyManagerService.this.mContext.getPackageManager();
                List<UserInfo> users = ((UserManager) MiuiNetworkPolicyManagerService.this.mContext.getSystemService("user")).getUsers();
                MiuiNetworkPolicyManagerService miuiNetworkPolicyManagerService = MiuiNetworkPolicyManagerService.this;
                Set unused = miuiNetworkPolicyManagerService.mMobileLowLatencyAppsPN = miuiNetworkPolicyManagerService.getMobileLowLatencyApps(miuiNetworkPolicyManagerService.mContext);
                MiuiNetworkPolicyManagerService.this.mMobileLowLatencyApps.clear();
                if (!MiuiNetworkPolicyManagerService.this.mMobileLowLatencyAppsPN.isEmpty()) {
                    for (UserInfo user : users) {
                        for (PackageInfo app : PackageManagerCompat.getInstalledPackagesAsUser(pm, 0, user.id)) {
                            if (!(app.packageName == null || app.applicationInfo == null || !MiuiNetworkPolicyManagerService.this.mMobileLowLatencyAppsPN.contains(app.packageName))) {
                                MiuiNetworkPolicyManagerService.this.mMobileLowLatencyApps.add(Integer.valueOf(UserHandle.getUid(user.id, app.applicationInfo.uid)));
                            }
                        }
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_LOW_LATENCY_APPLIST_FOR_MOBILE), false, observer, -2);
        this.mHandler.post(new Runnable() {
            public void run() {
                observer.onChange(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public Set<String> getMobileLowLatencyApps(Context context) {
        String[] packages;
        String appString = Settings.System.getStringForUser(context.getContentResolver(), CLOUD_LOW_LATENCY_APPLIST_FOR_MOBILE, -2);
        Log.i(TAG, "getMobileLowLatencyApps appString=" + appString);
        Set<String> appList = new HashSet<>();
        if (!TextUtils.isEmpty(appString) && (packages = appString.split(",")) != null) {
            for (String add : packages) {
                appList.add(add);
            }
        }
        return appList;
    }

    private boolean isMobileLatencyEnabledForUid(int uid, int state) {
        Log.i(TAG, "isMLEnabled state:" + state + ",uid:" + uid + ",connect:" + this.mConnectState);
        return state == 2 && this.mConnectState == PhoneConstants.DataState.CONNECTED && this.mMobileLowLatencyApps.contains(Integer.valueOf(uid));
    }

    private void updateMobileLatencyStateForUidState(int uid, int state) {
        boolean enabled = isMobileLatencyEnabledForUid(uid, state);
        Log.i(TAG, "updateMobileLowLatencyStateForUidState enabled = " + enabled);
        enableMobileLowLatency(enabled);
    }

    private void enableMobileLowLatency(boolean enable) {
        Log.i(TAG, "enableMobileLowLatency enable = " + enable + ",mMobileLatencyState=" + mMobileLatencyState);
        int mobileState = enable;
        if (mMobileLatencyState != mobileState) {
            mMobileLatencyState = (int) mobileState;
            Intent intent = new Intent();
            intent.setAction(LATENCY_ACTION_CHANGE_LEVEL);
            intent.setPackage(NOTIFACATION_RECEIVER_PACKAGE);
            intent.putExtra(LATENCY_KEY_RAT_TYPE, 0);
            long j = 4;
            intent.putExtra(LATENCY_KEY_LEVEL_UL, enable ? 4 : 1);
            if (!enable) {
                j = 1;
            }
            intent.putExtra(LATENCY_KEY_LEVEL_DL, j);
            this.mContext.sendBroadcast(intent);
        }
    }

    /* access modifiers changed from: private */
    public void updateMobileLatency() {
        int size = this.mUidState.size();
        for (int i = 0; i < size; i++) {
            int uid = this.mUidState.keyAt(i);
            int state = this.mUidState.get(uid, 20);
            Log.i(TAG, "updateMobileLatency uid = " + uid + ", state = " + state);
            if (isUidValidForRules(uid) && state == 2 && this.mMobileLowLatencyApps.contains(Integer.valueOf(uid))) {
                updateMobileLatencyStateForUidState(uid, state);
            }
        }
    }

    private void updateMobileLatencyForUidStateChange(int uid, int oldUidState, int newUidState) {
        Log.i(TAG, "updateMLUid uid:" + uid + ",old:" + oldUidState + ",new:" + newUidState);
        if (isUidValidForRules(uid) && isMobileLatencyEnabledForUid(uid, oldUidState) != isMobileLatencyEnabledForUid(uid, newUidState)) {
            updateMobileLatencyStateForUidState(uid, newUidState);
        }
    }

    /* access modifiers changed from: private */
    public void enableRps(boolean enable) {
        String iface = this.mSupport.updateIface(this.mInterfaceName);
        Log.d(TAG, "enableRps interface = " + iface + "enable = " + enable);
        if (iface != null) {
            this.mNetworkManager.enableRps(this.mInterfaceName, enable);
        }
    }

    /* access modifiers changed from: private */
    public static boolean isMobileLatencyAllowed() {
        return (IS_QCOM && Build.VERSION.SDK_INT < 26) ? false : false;
    }

    private static boolean isQosFeatureAllowed() {
        return "cepheus".equals(Build.DEVICE) || "raphael".equals(Build.DEVICE) || "davinci".equals(Build.DEVICE) || "crux".equals(Build.DEVICE) || "tucana".equals(Build.DEVICE) || "cmi".equals(Build.DEVICE) || "umi".equals(Build.DEVICE) || "picasso".equals(Build.DEVICE) || "phoenix".equals(Build.DEVICE) || "lmi".equals(Build.DEVICE) || "lmipro".equals(Build.DEVICE);
    }
}
