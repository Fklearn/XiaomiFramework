package com.android.server.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MiuiWifiDisableInjector {
    private static MiuiWifiDisableInjector INSTANCE = new MiuiWifiDisableInjector();
    private final int DISABLED_ASSOCIATION_REJECTION = 2;
    private final int RSSI_INCREASE_THRESHOLD = 10;
    private final String TAG = "MiuiWifiDisableInjector";
    private HashMap<String, Boolean> mBlackListUpdated = new HashMap<>();
    private HashMap<Integer, Integer> mCurrentRssis = new HashMap<>();
    private HashMap<String, Integer> mSavedNetworkIds = new HashMap<>();
    private Map<Integer, TemporarilyDisabledNetwork> mTemporarilyDisabledNetworks = new HashMap();

    private class TemporarilyDisabledNetwork {
        public int currentRssi;
        public int disableReason;
        public int disableRssi;
        public boolean isDisabled;
        public boolean needUpdated;

        private TemporarilyDisabledNetwork() {
        }
    }

    private MiuiWifiDisableInjector() {
    }

    public static MiuiWifiDisableInjector getInstance() {
        return INSTANCE;
    }

    public void refreshNetworkRssi(List<ScanDetail> scanDetails, List<WifiConfiguration> savedNetworks) {
        if (savedNetworks.size() == 0) {
            Log.d("MiuiWifiDisableInjector", "No saved networks.");
            return;
        }
        for (WifiConfiguration network : savedNetworks) {
            this.mSavedNetworkIds.put(network.SSID, Integer.valueOf(network.networkId));
        }
        for (int i = 0; i < scanDetails.size(); i++) {
            ScanResult scanResult = scanDetails.get(i).getScanResult();
            String result = "\"" + scanResult.SSID + "\"";
            if (this.mSavedNetworkIds.get(result) != null) {
                int savedNetworkId = this.mSavedNetworkIds.get(result).intValue();
                this.mCurrentRssis.put(Integer.valueOf(savedNetworkId), Integer.valueOf(scanResult.level));
                TemporarilyDisabledNetwork disabledNetwork = this.mTemporarilyDisabledNetworks.get(Integer.valueOf(savedNetworkId));
                if (disabledNetwork != null) {
                    disabledNetwork.currentRssi = scanResult.level;
                    if (disabledNetwork.isDisabled && disabledNetwork.currentRssi - disabledNetwork.disableRssi > 10 && disabledNetwork.disableReason == 2) {
                        disabledNetwork.isDisabled = false;
                        disabledNetwork.needUpdated = true;
                        this.mBlackListUpdated.put(scanResult.BSSID, true);
                    }
                }
            }
        }
    }

    public void setDisableRssi(int networkId, int rssi, int reason) {
        TemporarilyDisabledNetwork disabledNetwork = this.mTemporarilyDisabledNetworks.get(Integer.valueOf(networkId));
        if (disabledNetwork == null) {
            disabledNetwork = new TemporarilyDisabledNetwork();
            this.mTemporarilyDisabledNetworks.put(Integer.valueOf(networkId), disabledNetwork);
        }
        disabledNetwork.disableRssi = rssi;
        disabledNetwork.disableReason = reason;
        disabledNetwork.isDisabled = true;
    }

    public int getCurrentRssi(int networkId) {
        if (this.mCurrentRssis.get(Integer.valueOf(networkId)) == null) {
            return 0;
        }
        return this.mCurrentRssis.get(Integer.valueOf(networkId)).intValue();
    }

    public boolean isNeedEnable(int networkId) {
        if (this.mTemporarilyDisabledNetworks.get(Integer.valueOf(networkId)) == null) {
            return false;
        }
        return this.mTemporarilyDisabledNetworks.get(Integer.valueOf(networkId)).needUpdated;
    }

    public boolean isNeedRemove(String BSSID) {
        if (this.mBlackListUpdated.get(BSSID) == null) {
            return false;
        }
        return this.mBlackListUpdated.get(BSSID).booleanValue();
    }

    public void updateNetwork(int networkId, boolean updated) {
        TemporarilyDisabledNetwork disabledNetwork = this.mTemporarilyDisabledNetworks.get(Integer.valueOf(networkId));
        if (disabledNetwork != null) {
            disabledNetwork.needUpdated = updated;
            Log.d("MiuiWifiDisableInjector", "Network update: " + updated + " networkId: " + networkId);
        }
    }

    public void updateBlacklist(String BSSID, boolean updated) {
        this.mBlackListUpdated.put(BSSID, Boolean.valueOf(updated));
    }
}
