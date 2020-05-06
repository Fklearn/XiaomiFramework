package com.android.server.connectivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.server.MiuiConfigCaptivePortal;
import com.android.server.pm.DumpState;

class NetworkNotificationManagerInjector {
    NetworkNotificationManagerInjector() {
    }

    static void showLogin(Context context, Intent intent, String ssid) {
        WifiInfo wifiInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        Intent loginIntent = new Intent();
        loginIntent.setAction("com.miui.action.OPEN_WIFI_LOGIN");
        loginIntent.putExtra("miui.intent.extra.OPEN_WIFI_SSID", ssid);
        loginIntent.setData(Uri.parse(MiuiConfigCaptivePortal.getCaptivePortalServer(context, intent.getDataString()).toString()));
        if (wifiInfo != null && TextUtils.equals(ssid, wifiInfo.getSSID())) {
            loginIntent.putExtra("miui.intent.extra.BSSID", wifiInfo.getBSSID());
        }
        loginIntent.putExtra("miui.intent.extra.CAPTIVE_PORTAL", intent.getIBinderExtra("miui.intent.extra.CAPTIVE_PORTAL"));
        loginIntent.putExtra("miui.intent.extra.NETWORK", intent.getParcelableExtra("miui.intent.extra.NETWORK"));
        loginIntent.putExtra("miui.intent.extra.EXPLICIT_SELECTED", Boolean.valueOf(intent.getBooleanExtra("miui.intent.extra.EXPLICIT_SELECTED", false)));
        boolean isSlave = Boolean.valueOf(intent.getBooleanExtra("miui.intent.extra.IS_SLAVE", false)).booleanValue();
        loginIntent.putExtra("miui.intent.extra.IS_SLAVE", isSlave);
        loginIntent.addFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
        context.sendBroadcastAsUser(loginIntent, UserHandle.ALL);
        Intent cacheIntent = new Intent(loginIntent);
        if (isSlave) {
            cacheIntent.setAction("miui.intent.DUAL_WIFI.CACHE_OPENWIFI");
        } else {
            cacheIntent.setAction("miui.intent.CACHE_OPENWIFI");
        }
        context.sendStickyBroadcastAsUser(cacheIntent, UserHandle.ALL);
    }
}
