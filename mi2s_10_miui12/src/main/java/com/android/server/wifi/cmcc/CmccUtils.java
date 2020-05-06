package com.android.server.wifi.cmcc;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.wifi.WifiMetrics;
import java.util.List;

public class CmccUtils {
    private static final int SECURITY_EAP = 3;
    private static final int SECURITY_NONE = 0;
    private static final int SECURITY_PSK = 1;
    private static final int SECURITY_WAPI_CERT = 11;
    private static final int SECURITY_WAPI_PSK = 10;
    private static final int SECURITY_WEP = 2;
    private static final String TAG = "CmccUtils";

    public static boolean isWifiEnabled(Context context) {
        boolean ret = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (wifiManager != null) {
            ret = wifiManager.isWifiEnabled();
        }
        Log.d(TAG, "isWifiEnabled[" + ret + "]");
        return ret;
    }

    public static int getIntForUser(Context context, String key) {
        int value = -1;
        if (key != null) {
            value = Settings.System.getIntForUser(context.getContentResolver(), key, 0, -2);
        }
        Log.d(TAG, "getIntForUser[" + value + "]");
        return value;
    }

    public static boolean isWifiAutoConnect(Context context) {
        return getIntForUser(context, "wifi_connect_type") == 0;
    }

    public static boolean isWifiAutoConnectAsk(Context context) {
        return getIntForUser(context, "wifi_connect_type") == 2;
    }

    public static boolean isWifiSsidAutoSelect(Context context) {
        return getIntForUser(context, "wifi_select_ssid_type") == 0;
    }

    public static boolean isWifiSsidAutoSelectAsk(Context context) {
        return getIntForUser(context, "wifi_select_ssid_type") == 2;
    }

    public static void enableBestNetwork(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService("wifi");
        List<WifiConfiguration> configs = wm.getConfiguredNetworks();
        List<ScanResult> results = wm.getScanResults();
        int highestRssi = WifiMetrics.MIN_RSSI_DELTA;
        int highestPriorityNetworkId = -1;
        if (!(configs == null || results == null)) {
            for (WifiConfiguration config : configs) {
                for (ScanResult result : results) {
                    if (isScanResultMatchNetwork(result, config) && result.level > highestRssi) {
                        highestRssi = result.level;
                        highestPriorityNetworkId = config.networkId;
                    }
                }
            }
        }
        if (highestPriorityNetworkId >= 0) {
            wm.enableNetwork(highestPriorityNetworkId, false);
        }
    }

    public static boolean isScanResultMatchNetwork(ScanResult scanResult, WifiConfiguration config) {
        return TextUtils.equals(convertToQuotedString(scanResult.SSID), config.SSID) && getSecurity(scanResult) == getSecurity(config);
    }

    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
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
        if (config.allowedKeyManagement.get(190)) {
            return 10;
        }
        if (config.allowedKeyManagement.get(191)) {
            return 11;
        }
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
