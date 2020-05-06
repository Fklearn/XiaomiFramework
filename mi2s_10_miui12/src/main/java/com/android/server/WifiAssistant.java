package com.android.server;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.connectivity.NetworkAgentInfo;
import java.io.FileDescriptor;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WifiAssistant {
    protected static final String ACTION_NO_INTERNET_DETAIL = "com.android.server.WIFI_ASSISTANT_NO_INTERNET";
    protected static final boolean DEBUG = true;
    protected static final String EXTRA_NETWORK_ID = "EXTRA_NETWORK_ID";
    public static boolean IS_CTS_MODE = false;
    private static final int MAX_LOCAL_LOG_LINES = 500;
    private static final int MIN_RSSI_THRESHOLD = -67;
    private static final String TAG = "WifiAssistant";
    protected static final boolean VDEBUG = false;
    private static WifiAssistant sSelf;
    private ConnectivityManager mConnManager;
    private Context mContext;
    private LocalLog mLocalLog;
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            WifiAssistant wifiAssistant = WifiAssistant.this;
            wifiAssistant.logd("Received: " + action);
            if (TextUtils.equals("miui.intent.action.NETWORK_BLOCKED", action)) {
                WifiAssistant.this.handleNetworkBlocked();
            } else if (TextUtils.equals(WifiAssistant.ACTION_NO_INTERNET_DETAIL, action)) {
                WifiAssistant.this.handleClickNotification(intent.getIntExtra(WifiAssistant.EXTRA_NETWORK_ID, -1));
            }
        }
    };
    private SparseIntArray mNotificationMap;
    private TelephonyManager mTelephonyManager;
    private SparseBooleanArray mValidateHandledMap;
    private WifiManager mWifiManager;

    private enum NetworkCandidate {
        NONE(0),
        SELF(1),
        WIFI(2),
        DATA(3);
        
        public final int eventId;

        private NetworkCandidate(int eventId2) {
            this.eventId = eventId2;
        }
    }

    private WifiAssistant(Context context) {
        this.mContext = context;
        this.mConnManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        this.mNotificationMap = new SparseIntArray();
        this.mValidateHandledMap = new SparseBooleanArray();
        this.mLocalLog = new LocalLog(500);
        registerNetworkReceiver();
        if (Build.VERSION.SDK_INT >= 28) {
            registerMiuiOptimizationObserver();
        }
    }

    public static void make(Context context) {
        sSelf = new WifiAssistant(context);
    }

    public static WifiAssistant get() {
        WifiAssistant wifiAssistant = sSelf;
        if (wifiAssistant != null) {
            return wifiAssistant;
        }
        throw new RuntimeException("WifiAssistant not initialized");
    }

    public void dump(FileDescriptor fd, IndentingPrintWriter pw, String[] args) {
        pw.println();
        pw.println();
        pw.println("WifiAssistant: ");
        pw.increaseIndent();
        this.mLocalLog.dump(fd, pw, args);
        pw.decreaseIndent();
        pw.println();
    }

    /* access modifiers changed from: private */
    public void logd(String msg) {
        Log.d(TAG, msg);
        this.mLocalLog.log(msg);
    }

    private void logv(String msg) {
        this.mLocalLog.log(msg);
    }

    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NO_INTERNET_DETAIL);
        this.mContext.registerReceiver(this.mNetworkReceiver, filter);
    }

    private void registerMiuiOptimizationObserver() {
        ContentObserver observer = new ContentObserver((Handler) null) {
            public void onChange(boolean selfChange) {
                WifiAssistant.IS_CTS_MODE = !SystemProperties.getBoolean("persist.sys.miui_optimization", !SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.cts")));
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(MiuiSettings.Secure.MIUI_OPTIMIZATION), false, observer, -2);
        observer.onChange(false);
    }

    public void enableWifiAssistant(boolean enable) {
        Settings.System.putInt(this.mContext.getContentResolver(), "wifi_assistant", enable);
    }

    public boolean isWifiAssistantEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "wifi_assistant", 1) == 1;
    }

    public void enableDataWifiRoamWarning(boolean enable) {
        Settings.System.putInt(this.mContext.getContentResolver(), "wifi_assistant_data_prompt", enable);
    }

    public boolean isDataWifiRoamWarningEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "wifi_assistant_data_prompt", 1) == 1;
    }

    private void showNotValidatedDialog(int currentNetwork, boolean explicitlySelected, int candidateType) {
        logd("showNotValidatedDialog: " + currentNetwork + " | " + explicitlySelected + " | " + candidateType);
        Intent intent = new Intent("android.net.conn.PROMPT_UNVALIDATED");
        intent.putExtra("netId", currentNetwork);
        intent.putExtra("candidate", candidateType);
        intent.putExtra("explicitlySelected", explicitlySelected);
        intent.addFlags(268435456);
        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiAssistantDialog");
        try {
            this.mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "showNotValidatedDialog failed: ", e);
        }
    }

    private void showValidationNotification(int netId, int eventId, boolean alert) {
        logd("showValidationNotification: " + netId + " | " + eventId + " | " + alert);
        this.mNotificationMap.put(netId, eventId);
        WifiAssistantCompat.showValidationNotification(this.mContext, notificationTagFor(netId), netId, eventId, alert);
    }

    public void maybeClearNotification(int netId) {
        logd("maybeClearNotification: " + netId);
        int eventId = this.mNotificationMap.get(netId, -1);
        if (eventId == -1) {
            logd("maybeClearNotification: no eventId " + netId);
            return;
        }
        try {
            ((NotificationManager) this.mContext.getSystemService("notification")).cancelAsUser(notificationTagFor(netId), eventId, UserHandle.ALL);
        } catch (Exception e) {
            Log.e(TAG, "maybeClearNotification failed", e);
        }
        this.mNotificationMap.delete(netId);
    }

    private String notificationTagFor(int id) {
        return String.format("WifiAssistant:%d", new Object[]{Integer.valueOf(id)});
    }

    private void maybeClearNoInternetAccessFeature(NetworkAgentInfo nai) {
        logd("maybeClearNoInternetAccessFeature: " + nai.network.netId);
        WifiConfiguration configuration = getAssociatedWifiConfiguration(nai);
        if (configuration != null && configuration.noInternetAccessExpected) {
            nai.asyncChannel.sendMessage(528393, 0);
        }
    }

    private boolean isNetworkNoInternetExpected(NetworkAgentInfo nai) {
        WifiConfiguration configuration = getAssociatedWifiConfiguration(nai);
        return configuration != null && configuration.noInternetAccessExpected;
    }

    private WifiConfiguration getAssociatedWifiConfiguration(NetworkAgentInfo nai) {
        List<WifiConfiguration> configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        WifiInfo wifiInfo = this.mWifiManager.getConnectionInfo();
        if (!(configuredNetworks == null && wifiInfo == null)) {
            for (WifiConfiguration network : configuredNetworks) {
                if (network.networkId == wifiInfo.getNetworkId()) {
                    logd("getAssociatedWifiConfiguration: " + nai.network.netId + " | " + network.networkId);
                    return network;
                }
            }
        }
        logd("getAssociatedWifiConfiguration: " + nai.network.netId + " | " + null);
        return null;
    }

    private boolean isAutoConnectDisabledByUser(String ssid) {
        Set<String> ssidSet = MiuiSettings.System.getDisableWifiAutoConnectSsid(this.mContext);
        return ssidSet != null && ssidSet.contains(ssid);
    }

    private boolean validateNetworkAgent(NetworkAgentInfo nai) {
        if (nai == null || nai.networkInfo == null || nai.networkInfo.getType() != 1 || !nai.networkCapabilities.hasCapability(12)) {
            return false;
        }
        return true;
    }

    private NetworkCandidate selectNetwork() {
        List<WifiConfiguration> configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        List<ScanResult> lastScanResults = this.mWifiManager.getScanResults();
        WifiInfo currentWifiInfo = this.mWifiManager.getConnectionInfo();
        if (currentWifiInfo == null || lastScanResults == null) {
            logd("Select SELF for current wi or scan result is null");
            return NetworkCandidate.SELF;
        }
        logv("Select network start for: " + maskMacAddrForPrivacy(currentWifiInfo.getBSSID()));
        for (ScanResult scanResult : lastScanResults) {
            Iterator<WifiConfiguration> it = configuredNetworks.iterator();
            while (true) {
                if (it.hasNext()) {
                    WifiConfiguration network = it.next();
                    if (WifiAssistantUtils.isScanResultMatchNetwork(scanResult, network)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("    Select network: ");
                        sb.append(network.networkId);
                        sb.append(" | ");
                        sb.append(maskMacAddrForPrivacy(scanResult.BSSID));
                        sb.append(" | ");
                        sb.append(currentWifiInfo.getNetworkId() != network.networkId);
                        sb.append(" | ");
                        sb.append(network.getNetworkSelectionStatus().isNetworkEnabled());
                        sb.append(" | ");
                        sb.append(!network.noInternetAccessExpected);
                        sb.append(" | ");
                        sb.append(!isAutoConnectDisabledByUser(network.SSID));
                        logv(sb.toString());
                        if (currentWifiInfo.getNetworkId() != network.networkId && network.getNetworkSelectionStatus().isNetworkEnabled() && !network.noInternetAccessExpected && !isAutoConnectDisabledByUser(network.SSID)) {
                            logd("Select WIFI for available: " + network.networkId);
                            return NetworkCandidate.WIFI;
                        }
                    }
                }
            }
        }
        if (!this.mConnManager.isNetworkSupported(0) || this.mTelephonyManager.getSimState() != 5 || !this.mTelephonyManager.getDataEnabled()) {
            logd("Select NONE for no available network");
            return NetworkCandidate.NONE;
        }
        logd("Select DATA for data is enabled");
        return NetworkCandidate.DATA;
    }

    private String maskMacAddrForPrivacy(String mac) {
        try {
            return "*:*:*:" + mac.substring(9);
        } catch (Exception e) {
            logd("maskMacAddrForPrivacy  Exception " + e);
            return mac;
        }
    }

    private boolean handleExplicitlySelected(Network network, NetworkCandidate candidate, boolean notify, boolean dialog) {
        int type;
        logd("handleExplicitlySelected: " + network.netId);
        if (candidate == NetworkCandidate.NONE) {
            type = -1;
        } else if (candidate == NetworkCandidate.WIFI) {
            type = 1;
        } else if (candidate != NetworkCandidate.DATA) {
            return true;
        } else {
            type = 0;
        }
        if (dialog) {
            showNotValidatedDialog(network.netId, true, type);
        }
        if (notify) {
            showValidationNotification(network.netId, candidate.eventId, false);
        }
        return true;
    }

    private boolean handleAutoConnect(NetworkAgentInfo nai, NetworkCandidate candidate, boolean notify, boolean dialog) {
        if (isWifiAssistantEnabled()) {
            logd("handleAutoConnect: auto conn and assistant enabled");
            if (candidate == NetworkCandidate.NONE) {
                if (notify) {
                    showValidationNotification(nai.network.netId, candidate.eventId, true);
                }
                return true;
            } else if (candidate == NetworkCandidate.WIFI) {
                return false;
            } else {
                if (candidate != NetworkCandidate.DATA) {
                    return true;
                }
                if (notify) {
                    showValidationNotification(nai.network.netId, candidate.eventId, false);
                }
                if (isDataWifiRoamWarningEnabled()) {
                    logd("handleAutoConnect: assistant prompt data enabled");
                    if (dialog) {
                        showNotValidatedDialog(nai.network.netId, false, 0);
                    }
                    return true;
                }
                logd("handleAutoConnect: assistant prompt data disabled");
                this.mConnManager.setAcceptUnvalidated(nai.network, false, false);
                return false;
            }
        } else {
            logd("handleAutoConnect: auto conn and assistant disabled");
            if (notify) {
                showValidationNotification(nai.network.netId, candidate.eventId, true);
            }
            return true;
        }
    }

    public boolean handleNetworkValidationResult(NetworkAgentInfo nai, boolean valid) {
        boolean expectedValid;
        logd("handleNetworkValidationResult: " + nai.network.netId + " | " + valid + " | " + nai.lastValidated + " | " + nai.everValidated);
        if (!validateNetworkAgent(nai)) {
            return valid;
        }
        if (valid) {
            maybeClearNotification(nai.network.netId);
            maybeClearNoInternetAccessFeature(nai);
            if (this.mValidateHandledMap.get(nai.network.netId, false)) {
                this.mValidateHandledMap.delete(nai.network.netId);
            }
            logd("handleNetworkValidationResult: abort for validated: " + valid);
            nai.setCurrentScore(60);
            return valid;
        } else if (nai.everCaptivePortalDetected) {
            logd("handleNetworkValidationResult: abort for portal network");
            return valid;
        } else {
            nai.setCurrentScore(51);
            if (!IS_CTS_MODE && nai.everValidated) {
                logd("handleNetworkValidationResult: abort for everValidated: " + nai.everValidated);
                return true;
            } else if (!nai.lastValidated) {
                logd("handleNetworkValidationResult: abort for lastValidated: " + nai.lastValidated);
                return valid;
            } else if (this.mValidateHandledMap.get(nai.network.netId, false)) {
                logd("handleNetworkValidationResult: abort for already handled: " + nai.network.netId + " | " + nai.lastValidated + " | " + valid);
                return valid;
            } else {
                this.mValidateHandledMap.put(nai.network.netId, true);
                if (!nai.networkMisc.explicitlySelected || !nai.networkMisc.acceptUnvalidated) {
                    List<ScanResult> lastScanResults = this.mWifiManager.getScanResults();
                    WifiInfo currentWifiInfo = this.mWifiManager.getConnectionInfo();
                    if (currentWifiInfo == null || lastScanResults == null) {
                        logd("handleNetworkValidationResult: wi or scan result is null");
                        return true;
                    }
                    Iterator<ScanResult> it = lastScanResults.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        ScanResult scanResult = it.next();
                        if (TextUtils.equals(currentWifiInfo.getBSSID(), scanResult.BSSID)) {
                            if (scanResult.level < MIN_RSSI_THRESHOLD) {
                                logd("handleNetworkValidationResult: rssi limit " + scanResult.level);
                                return true;
                            }
                        }
                    }
                    NetworkCandidate candidate = selectNetwork();
                    if (nai.networkMisc.explicitlySelected) {
                        expectedValid = handleExplicitlySelected(nai.network, candidate, true, true);
                    } else {
                        expectedValid = handleAutoConnect(nai, candidate, true, true);
                    }
                    if (expectedValid) {
                        this.mConnManager.setAcceptUnvalidated(nai.network, true, false);
                    }
                    return valid;
                }
                logd("handleNetworkValidationResult: user preferred " + nai.network.netId);
                showValidationNotification(nai.network.netId, NetworkCandidate.SELF.eventId, true);
                return valid;
            }
        }
    }

    public boolean handleNetworkNoInternet(NetworkAgentInfo nai) {
        WifiConfiguration wifiConfiguration;
        logd("handleNetworkNoInternet: " + nai.network.netId);
        if (!validateNetworkAgent(nai) || nai.everCaptivePortalDetected) {
            return false;
        }
        if (nai.networkMisc.explicitlySelected && nai.networkMisc.acceptUnvalidated && isNetworkNoInternetExpected(nai)) {
            logd("handleNetworkNoInternet: user preferred " + nai.network.netId);
            showValidationNotification(nai.network.netId, NetworkCandidate.SELF.eventId, true);
            return true;
        } else if (IS_CTS_MODE || (wifiConfiguration = getAssociatedWifiConfiguration(nai)) == null || wifiConfiguration.creatorUid == 1000) {
            NetworkCandidate candidate = selectNetwork();
            if (nai.networkMisc.explicitlySelected) {
                handleExplicitlySelected(nai.network, candidate, true, true);
            } else if (handleAutoConnect(nai, candidate, true, true)) {
                this.mConnManager.setAcceptUnvalidated(nai.network, true, false);
            }
            return true;
        } else {
            logd(wifiConfiguration.SSID + " is created by " + wifiConfiguration.creatorUid + ", bail out.");
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void handleNetworkBlocked() {
        NetworkCapabilities nc;
        logd("handleNetworkBlocked");
        Network network = this.mConnManager.getActiveNetwork();
        if (network != null && (nc = this.mConnManager.getNetworkCapabilities(network)) != null && nc.hasCapability(1)) {
            this.mConnManager.reportNetworkConnectivity(network, false);
        }
    }

    /* access modifiers changed from: private */
    public void handleClickNotification(int networkId) {
        logd("handleClickNotification: " + networkId);
        try {
            Network network = new Network(networkId);
            NetworkInfo networkInfo = this.mConnManager.getNetworkInfo(network);
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                Log.e(TAG, "handleClickNotification: network " + networkId + " is not connected");
                return;
            }
            handleExplicitlySelected(network, selectNetwork(), false, true);
        } catch (Exception e) {
            Log.e(TAG, "handleClickNotification", e);
        }
    }

    public static class WifiAssistantUtils {
        private static final int SECURITY_EAP = 3;
        private static final int SECURITY_NONE = 0;
        private static final int SECURITY_PSK = 1;
        private static final int SECURITY_WAPI_CERT = 11;
        private static final int SECURITY_WAPI_PSK = 10;
        private static final int SECURITY_WEP = 2;

        public static boolean isScanResultMatchNetwork(ScanResult scanResult, WifiConfiguration network) {
            return TextUtils.equals(createQuotedSSID(scanResult.SSID), network.SSID) && getSecurity(scanResult) == getSecurity(network);
        }

        public static String createQuotedSSID(String ssid) {
            return "\"" + ssid + "\"";
        }

        public static int getSecurity(ScanResult scanResult) {
            if (scanResult.capabilities.contains("WAPI-KEY") || scanResult.capabilities.contains("WAPI-PSK")) {
                return 10;
            }
            if (scanResult.capabilities.contains("WAPI-CERT")) {
                return 11;
            }
            if (scanResult.capabilities.contains("WEP")) {
                return 2;
            }
            if (scanResult.capabilities.contains("PSK")) {
                return 1;
            }
            if (scanResult.capabilities.contains("EAP")) {
                return 3;
            }
            return 0;
        }

        public static int getSecurity(WifiConfiguration config) {
            if (config.allowedKeyManagement.get(1)) {
                return 1;
            }
            if (config.allowedKeyManagement.get(2) || config.allowedKeyManagement.get(3)) {
                return 3;
            }
            if (config.wepKeys[0] != null) {
                return 2;
            }
            return 0;
        }
    }
}
