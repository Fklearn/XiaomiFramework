package com.android.server.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.telephony.TelephonyManager;
import android.util.LocalLog;
import com.android.server.wifi.WifiNetworkSelector;
import com.android.server.wifi.util.ScanResultUtil;
import com.android.server.wifi.util.TelephonyUtil;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class CarrierNetworkEvaluator implements WifiNetworkSelector.NetworkEvaluator {
    private static final String TAG = "CarrierNetworkEvaluator";
    private final CarrierNetworkConfig mCarrierNetworkConfig;
    private final LocalLog mLocalLog;
    private TelephonyManager mTelephonyManager;
    private final WifiConfigManager mWifiConfigManager;
    private final WifiInjector mWifiInjector;

    public CarrierNetworkEvaluator(WifiConfigManager wifiConfigManager, CarrierNetworkConfig carrierNetworkConfig, LocalLog localLog, WifiInjector wifiInjector) {
        this.mWifiConfigManager = wifiConfigManager;
        this.mCarrierNetworkConfig = carrierNetworkConfig;
        this.mLocalLog = localLog;
        this.mWifiInjector = wifiInjector;
    }

    private TelephonyManager getTelephonyManager() {
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = this.mWifiInjector.makeTelephonyManager();
        }
        return this.mTelephonyManager;
    }

    public int getId() {
        return 3;
    }

    public String getName() {
        return TAG;
    }

    public void update(List<ScanDetail> list) {
    }

    public WifiConfiguration evaluateNetworks(List<ScanDetail> scanDetails, WifiConfiguration currentNetwork, String currentBssid, boolean connected, boolean untrustedNetworkAllowed, WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener) {
        if (!this.mCarrierNetworkConfig.isCarrierEncryptionInfoAvailable()) {
            return null;
        }
        int currentMaxRssi = Integer.MIN_VALUE;
        WifiConfiguration configWithMaxRssi = null;
        for (ScanDetail scanDetail : scanDetails) {
            ScanResult scanResult = scanDetail.getScanResult();
            if (!ScanResultUtil.isScanResultForEapNetwork(scanResult)) {
                WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener2 = onConnectableListener;
            } else if (!this.mCarrierNetworkConfig.isCarrierNetwork(scanResult.SSID)) {
                WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener3 = onConnectableListener;
            } else {
                int eapType = this.mCarrierNetworkConfig.getNetworkEapType(scanResult.SSID);
                if (!TelephonyUtil.isSimEapMethod(eapType)) {
                    LocalLog localLog = this.mLocalLog;
                    localLog.log("CarrierNetworkEvaluator: eapType is not a carrier eap method: " + eapType);
                    WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener4 = onConnectableListener;
                } else if (this.mWifiConfigManager.wasEphemeralNetworkDeleted(ScanResultUtil.createQuotedSSID(scanResult.SSID))) {
                    LocalLog localLog2 = this.mLocalLog;
                    localLog2.log("CarrierNetworkEvaluator: Ignoring disabled ephemeral SSID: " + WifiNetworkSelector.toScanId(scanResult));
                    WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener5 = onConnectableListener;
                } else {
                    WifiConfiguration config = ScanResultUtil.createNetworkFromScanResult(scanResult);
                    config.ephemeral = true;
                    if (config.enterpriseConfig == null) {
                        config.enterpriseConfig = new WifiEnterpriseConfig();
                    }
                    config.enterpriseConfig.setEapMethod(eapType);
                    WifiConfiguration existingNetwork = this.mWifiConfigManager.getConfiguredNetwork(config.configKey());
                    if (existingNetwork == null || existingNetwork.getNetworkSelectionStatus().isNetworkEnabled() || this.mWifiConfigManager.tryEnableNetwork(existingNetwork.networkId)) {
                        NetworkUpdateResult result = this.mWifiConfigManager.addOrUpdateNetwork(config, 1010);
                        if (!result.isSuccess()) {
                            LocalLog localLog3 = this.mLocalLog;
                            localLog3.log("CarrierNetworkEvaluator: Failed to add carrier network: " + config);
                            WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener6 = onConnectableListener;
                        } else if (!this.mWifiConfigManager.enableNetwork(result.getNetworkId(), false, 1010)) {
                            LocalLog localLog4 = this.mLocalLog;
                            localLog4.log("CarrierNetworkEvaluator: Failed to enable carrier network: " + config);
                            WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener7 = onConnectableListener;
                        } else if (!this.mWifiConfigManager.setNetworkCandidateScanResult(result.getNetworkId(), scanResult, 0)) {
                            LocalLog localLog5 = this.mLocalLog;
                            localLog5.log("CarrierNetworkEvaluator: Failed to set network candidate for carrier network: " + config);
                            WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener8 = onConnectableListener;
                        } else {
                            WifiConfiguration config2 = this.mWifiConfigManager.getConfiguredNetwork(result.getNetworkId());
                            WifiConfiguration.NetworkSelectionStatus nss = null;
                            if (config2 != null) {
                                nss = config2.getNetworkSelectionStatus();
                            }
                            if (nss == null) {
                                LocalLog localLog6 = this.mLocalLog;
                                localLog6.log("CarrierNetworkEvaluator: null network selection status for: " + config2);
                                WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener9 = onConnectableListener;
                            } else {
                                if (nss.getCandidate() != null && nss.getCandidate().level < scanResult.level) {
                                    this.mWifiConfigManager.updateScanDetailForNetwork(result.getNetworkId(), scanDetail);
                                }
                                onConnectableListener.onConnectable(scanDetail, config2, 0);
                                if (scanResult.level > currentMaxRssi) {
                                    configWithMaxRssi = config2;
                                    currentMaxRssi = scanResult.level;
                                }
                            }
                        }
                    } else {
                        LocalLog localLog7 = this.mLocalLog;
                        localLog7.log("CarrierNetworkEvaluator: Ignoring blacklisted network: " + WifiNetworkSelector.toNetworkString(existingNetwork));
                        WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener10 = onConnectableListener;
                    }
                }
            }
        }
        WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener11 = onConnectableListener;
        return configWithMaxRssi;
    }
}
