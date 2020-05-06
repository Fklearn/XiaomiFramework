package com.android.server.wifi;

import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import java.util.List;

final class WifiStateMachineInjectorProxy {
    WifiStateMachineInjectorProxy() {
    }

    static void handleNetworkStateChange(WifiInfo wifiInfo, NetworkInfo networkInfo, List<ScanDetail> scanResults) {
        if (networkInfo != null && networkInfo.isConnected() && wifiInfo != null && !wifiInfo.getMeteredHint() && scanResults != null) {
            String bssid = wifiInfo.getBSSID();
            if (!TextUtils.isEmpty(bssid)) {
                for (ScanDetail result : scanResults) {
                    if (bssid.equals(result.getScanResult().BSSID)) {
                        wifiInfo.setMeteredHint(Utils.isMeteredHint(result.getScanResult().informationElements));
                        return;
                    }
                }
            }
        }
    }

    static void handleNetworkStateChange(WifiInfo wifiInfo, NetworkInfo networkInfo, ScanResult scanResult) {
        if (networkInfo != null && networkInfo.isConnected() && wifiInfo != null && !wifiInfo.getMeteredHint() && scanResult != null) {
            wifiInfo.setMeteredHint(Utils.isMeteredHint(scanResult.informationElements));
        }
    }
}
