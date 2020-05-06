package com.android.server.wifi;

import android.net.wifi.WifiConfiguration;
import miui.telephony.phonenumber.Prefix;

final class ConfigUtils {
    private static final String TAG = "ConfigUtils";

    ConfigUtils() {
    }

    @Deprecated
    public static String getWifiConfigStringWithPassword(WifiConfiguration config) {
        return Prefix.EMPTY;
    }
}
