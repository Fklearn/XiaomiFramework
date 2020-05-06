package com.android.server.wifi;

import android.content.Context;
import android.content.Intent;
import android.content.MiuiIntent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiSsid;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;

public final class SupplicantStateTrackerInjector {
    public static final String TAG = "SupplicantStateTrackerInjector";

    public static int handleNetworkConnectionFailure(Context context, WifiConfigManager wifiConfigManager, int netid, int authenticationFailuresCount) {
        WifiConfiguration network;
        if (!isConformAuthFailure(netid, authenticationFailuresCount)) {
            return 0;
        }
        MiuiSupplicantStateTracker.clear();
        if (wifiConfigManager == null || (network = wifiConfigManager.getConfiguredNetwork(netid)) == null) {
            return 0;
        }
        if (network.allowedKeyManagement.get(0) && network.wepKeys[0] == null) {
            return 0;
        }
        WifiConfiguration.NetworkSelectionStatus networkStatus = network.getNetworkSelectionStatus();
        if (networkStatus.isNetworkEnabled()) {
            networkStatus.setNetworkSelectionStatus(1);
            networkStatus.setDisableTime(SystemClock.elapsedRealtime());
            networkStatus.setNetworkSelectionDisableReason(3);
        }
        sendBroadcast(context, network);
        return 0;
    }

    public static int handleNetworkConnectionFailure(Context context, WifiConfigManager wifiConfigManager, WifiSsid wifiSsid, int authenticationFailuresCount) {
        return handleNetworkConnectionFailure(context, wifiConfigManager, wifiSsid, authenticationFailuresCount, false);
    }

    public static int handleNetworkConnectionFailure(Context context, WifiConfigManager wifiConfigManager, WifiSsid wifiSsid, int authenticationFailuresCount, boolean isSlave) {
        int netId;
        if (!isSlave) {
            netId = wifiConfigManager.getLastSelectedNetwork();
        } else {
            netId = getSlaveLastSelectedNetwork(wifiConfigManager);
        }
        WifiConfiguration network = wifiConfigManager.getConfiguredNetwork(netId);
        if (!isConformAuthFailure(netId, authenticationFailuresCount) || network == null || !TextUtils.equals(Utils.removeDoubleQuotes(network.SSID), wifiSsid.toString())) {
            return 0;
        }
        if (network.allowedKeyManagement.get(0) && network.wepKeys[0] == null) {
            return 0;
        }
        MiuiSupplicantStateTracker.clear();
        wifiConfigManager.updateNetworkSelectionStatus(netId, 13);
        sendBroadcast(context, network, isSlave);
        return 0;
    }

    public static void handleConnectNetwork(int netid) {
        MiuiSupplicantStateTracker.handleConnectNetwork(netid);
    }

    private static int getSlaveLastSelectedNetwork(WifiConfigManager wifiConfigManager) {
        try {
            Class<?> clazz = Class.forName("com.android.server.wifi.WifiConfigManager");
            try {
                return ((Integer) clazz.getDeclaredMethod("getLastSelectedNetwork", new Class[]{Boolean.TYPE}).invoke(wifiConfigManager, new Object[]{true})).intValue();
            } catch (Exception e) {
                Log.e(TAG, "cannot find getSlaveLastSelectedNetwork, return :" + e);
                return -1;
            }
        } catch (Exception e2) {
            Log.e(TAG, "forName WifiConfigManager catch exception, return :" + e2);
            return -1;
        }
    }

    public static void handleNetworkConnectionComplete() {
        MiuiSupplicantStateTracker.handleNetworkConnectionComplete();
    }

    public static boolean isConformAuthFailure(int netid, int authenticationFailuresCount) {
        return MiuiSupplicantStateTracker.isConformAuthFailure(netid, authenticationFailuresCount);
    }

    private static void sendBroadcast(Context context, WifiConfiguration network) {
        sendBroadcast(context, network, false);
    }

    private static void sendBroadcast(Context context, WifiConfiguration network, boolean isSlave) {
        Intent intent = new Intent(MiuiIntent.ACTION_WIFI_CONNECTION_FAILURE);
        intent.addFlags(352321536);
        intent.putExtra("wifiConfiguration", network);
        intent.putExtra("isSlave", isSlave);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }
}
