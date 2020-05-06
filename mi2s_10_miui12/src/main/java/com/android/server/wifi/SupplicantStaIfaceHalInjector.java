package com.android.server.wifi;

import android.net.wifi.WifiConfiguration;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.server.wifi.WifiBackupRestore;
import java.util.HashMap;

public class SupplicantStaIfaceHalInjector {
    private static final String TAG = "SupplicantStaIfaceHalInjector";

    public static void addExtendEapMethods(WifiConfiguration network, SupplicantStaIfaceHal staIfaceHal, SupplicantStaNetworkHal networkHal) {
        if (network != null && network.enterpriseConfig != null && network.enterpriseConfig.getEapMethod() == 0 && network.enterpriseConfig.getPhase2Method() == 0 && network.enterpriseConfig.getCaCertificateAliases() == null && TextUtils.isEmpty(network.enterpriseConfig.getCaPath()) && TextUtils.isEmpty(network.enterpriseConfig.getDomainSuffixMatch()) && TextUtils.isEmpty(network.enterpriseConfig.getClientCertificateAlias())) {
            WifiConfiguration internalNetwork = new WifiConfiguration();
            boolean loadSuccess = false;
            try {
                loadSuccess = networkHal.loadWifiConfiguration(internalNetwork, new HashMap<>());
            } catch (IllegalArgumentException e) {
                Log.wtf(TAG, "Exception while loading network params: " + internalNetwork, e);
            }
            if (loadSuccess) {
                Log.d(TAG, "Success loading network for adding other eap methods: " + internalNetwork.networkId);
                Pair<Boolean, String> result = staIfaceHal.doSupplicantCommand("SET_NETWORK " + internalNetwork.networkId + " " + WifiBackupRestore.SupplicantBackupMigration.SUPPLICANT_KEY_EAP + " PEAP TTLS PWD");
                if (result == null || !((Boolean) result.first).booleanValue()) {
                    Log.e(TAG, "Can not add other eap methods");
                }
            }
        }
    }

    public static void setPowerSave(boolean enabled) {
        WifiInjector.getInstance().getSupplicantStaIfaceHal().setPowerSave(SystemProperties.get("wifi.interface", "wlan0"), enabled);
    }
}
