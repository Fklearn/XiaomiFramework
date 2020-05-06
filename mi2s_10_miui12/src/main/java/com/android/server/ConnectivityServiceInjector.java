package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkUtils;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Slog;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

class ConnectivityServiceInjector {
    private static final String DEFAULT_DNSV6_ABROAD = "2001:4860:4860::8888";
    private static final String DEFAULT_DNSV6_DOMESTIC = "240c::6666";
    private static final String DEFAULT_DNS_ABROAD = "8.8.8.8";
    private static final String DEFAULT_DNS_DOMESTIC = "114.114.114.114";
    private static final String TAG = "ConnectivityServiceInjector";

    ConnectivityServiceInjector() {
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
        context.sendBroadcast(loginIntent);
        Intent cacheIntent = new Intent(loginIntent);
        cacheIntent.setAction("miui.intent.CACHE_OPENWIFI");
        context.sendStickyBroadcast(cacheIntent);
    }

    static final boolean enableDataAndWifiRoam(Context context) {
        return MiuiConfigCaptivePortal.enableDataAndWifiRoam(context);
    }

    static final Collection<InetAddress> addDnsIfNeeded(Collection<InetAddress> dnses) {
        if (dnses != null && dnses.size() <= 2) {
            dnses = new ArrayList<>(dnses);
            String dns = MiuiSettings.System.isInCnRegion() ? DEFAULT_DNS_DOMESTIC : DEFAULT_DNS_ABROAD;
            String dnsv6 = MiuiSettings.System.isInCnRegion() ? DEFAULT_DNSV6_DOMESTIC : DEFAULT_DNSV6_ABROAD;
            boolean hasIpV6 = false;
            boolean hasIpV4 = false;
            for (InetAddress ia : dnses) {
                if (ia instanceof Inet4Address) {
                    hasIpV4 = true;
                } else if (ia instanceof Inet6Address) {
                    hasIpV6 = true;
                }
            }
            if (hasIpV6) {
                try {
                    dnses.add(NetworkUtils.numericToInetAddress(dnsv6));
                } catch (IllegalArgumentException e) {
                    Slog.e(TAG, "Error setting dns using " + dns + " or " + dnsv6);
                }
            }
            if (hasIpV4) {
                dnses.add(NetworkUtils.numericToInetAddress(dns));
            }
        }
        return dnses;
    }
}
