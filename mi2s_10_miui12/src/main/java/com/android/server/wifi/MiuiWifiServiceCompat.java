package com.android.server.wifi;

import android.util.Log;
import android.util.Pair;

public class MiuiWifiServiceCompat {
    private static final String TAG = "MiuiWifiServiceCompat";

    public static void setSARLimit(int set) {
        SupplicantStaIfaceHal supplicantStaIfaceHal = WifiInjector.getInstance().getSupplicantStaIfaceHal();
        Pair<Boolean, String> result = supplicantStaIfaceHal.doSupplicantCommand("SET_SAR_LIMIT " + set);
        Log.e(TAG, "setSARLimit enter");
        if (result == null || !((Boolean) result.first).booleanValue()) {
            Log.e(TAG, "Can not set SAR limitation");
            HostapdHal hostadpHal = WifiInjector.getInstance().getHostadpHal();
            Pair<Boolean, String> result2 = hostadpHal.doHostapdCommand("SET_SAR_LIMIT " + set);
            if (result2 == null || !((Boolean) result2.first).booleanValue()) {
                Log.e(TAG, "Can not set SAR limitation-HostAPH");
            }
        }
    }

    public static void setLatencyLevel(int level) {
        SupplicantStaIfaceHal supplicantStaIfaceHal = WifiInjector.getInstance().getSupplicantStaIfaceHal();
        Pair<Boolean, String> result = supplicantStaIfaceHal.doSupplicantCommand("SET_LATENCY_LEVEL " + level);
        if (result == null || !((Boolean) result.first).booleanValue()) {
            Log.e(TAG, "Can not set WiFi latency level");
        }
    }
}
