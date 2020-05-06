package com.android.server.wifi;

import android.util.Log;
import android.util.Pair;

public class MiuiWifiP2pServiceCompat {
    private static final String TAG = "MiuiWifiiP2pServiceCompat";

    public static void discoverPeersOnTheFixedFreq(int freq) {
        SupplicantStaIfaceHal supplicantStaIfaceHal = WifiInjector.getInstance().getSupplicantStaIfaceHal();
        Pair<Boolean, String> result = supplicantStaIfaceHal.doSupplicantCommand("FIXED_FREQ_DISCOVER " + freq);
        if (result == null || !((Boolean) result.first).booleanValue()) {
            Log.e(TAG, "Can not Discover Peers on a Fixed Freq");
        }
    }

    public static boolean setP2pConfig(String config) {
        if (config == null) {
            return false;
        }
        String[] configs = config.split(";");
        if (configs.length != 3) {
            return false;
        }
        SupplicantStaIfaceHal supplicantStaIfaceHal = WifiInjector.getInstance().getSupplicantStaIfaceHal();
        Pair<Boolean, String> result = supplicantStaIfaceHal.doSupplicantCommand("SET_P2P_CONFIG ssid:\"" + configs[0] + "\"");
        SupplicantStaIfaceHal supplicantStaIfaceHal2 = WifiInjector.getInstance().getSupplicantStaIfaceHal();
        supplicantStaIfaceHal2.doSupplicantCommand("SET_P2P_CONFIG passphrase:" + configs[1]);
        SupplicantStaIfaceHal supplicantStaIfaceHal3 = WifiInjector.getInstance().getSupplicantStaIfaceHal();
        supplicantStaIfaceHal3.doSupplicantCommand("SET_P2P_CONFIG freq:" + configs[2]);
        if (result != null && ((Boolean) result.first).booleanValue()) {
            return true;
        }
        Log.e(TAG, "setP2pConfig Failed");
        return false;
    }
}
