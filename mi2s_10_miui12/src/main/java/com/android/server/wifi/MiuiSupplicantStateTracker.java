package com.android.server.wifi;

import android.content.Context;
import android.content.Intent;
import android.content.MiuiIntent;
import android.net.wifi.WifiConfiguration;
import android.os.UserHandle;

public final class MiuiSupplicantStateTracker {
    private static int sNetid = -1;

    public static void handleNetworkConnectionComplete() {
        clear();
    }

    public static void handleConnectNetwork(int netid) {
        setNetId(netid);
    }

    public static boolean isConformAuthFailure(int netid, int authenticationFailuresCount) {
        return authenticationFailuresCount > 0 && match(netid);
    }

    public static void setNetId(int netid) {
        sNetid = netid;
    }

    public static boolean match(int netid) {
        return netid != -1 && sNetid == netid;
    }

    public static void clear() {
        sNetid = -1;
    }

    public static int getNetId() {
        return sNetid;
    }

    public static void sendBroadcast(Context context, WifiConfiguration network) {
        Intent intent = new Intent(MiuiIntent.ACTION_WIFI_CONNECTION_FAILURE);
        intent.addFlags(335544320);
        intent.putExtra("wifiConfiguration", network);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }
}
