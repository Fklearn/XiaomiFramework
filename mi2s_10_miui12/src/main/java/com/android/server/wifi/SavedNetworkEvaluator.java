package com.android.server.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.telephony.SubscriptionManager;
import android.util.LocalLog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.wifi.WifiNetworkSelector;
import com.android.server.wifi.util.TelephonyUtil;
import java.util.List;

public class SavedNetworkEvaluator implements WifiNetworkSelector.NetworkEvaluator {
    @VisibleForTesting
    public static final int LAST_SELECTION_AWARD_DECAY_MSEC = 60000;
    private static final String NAME = "SavedNetworkEvaluator";
    private final int mBand5GHzAward;
    private final Clock mClock;
    private final WifiConnectivityHelper mConnectivityHelper;
    private final int mLastSelectionAward;
    private final LocalLog mLocalLog;
    private final int mRssiScoreOffset;
    private final int mRssiScoreSlope;
    private final int mSameBssidAward;
    private final int mSameNetworkAward;
    private final ScoringParams mScoringParams;
    private final int mSecurityAward;
    private final SubscriptionManager mSubscriptionManager;
    private final WifiConfigManager mWifiConfigManager;

    SavedNetworkEvaluator(Context context, ScoringParams scoringParams, WifiConfigManager configManager, Clock clock, LocalLog localLog, WifiConnectivityHelper connectivityHelper, SubscriptionManager subscriptionManager) {
        this.mScoringParams = scoringParams;
        this.mWifiConfigManager = configManager;
        this.mClock = clock;
        this.mLocalLog = localLog;
        this.mConnectivityHelper = connectivityHelper;
        this.mSubscriptionManager = subscriptionManager;
        this.mRssiScoreSlope = context.getResources().getInteger(17694922);
        this.mRssiScoreOffset = context.getResources().getInteger(17694921);
        this.mSameBssidAward = context.getResources().getInteger(17694923);
        this.mSameNetworkAward = context.getResources().getInteger(17694933);
        this.mLastSelectionAward = context.getResources().getInteger(17694919);
        this.mSecurityAward = context.getResources().getInteger(17694924);
        this.mBand5GHzAward = context.getResources().getInteger(17694916);
    }

    private void localLog(String log) {
        this.mLocalLog.log(log);
    }

    public int getId() {
        return 0;
    }

    public String getName() {
        return NAME;
    }

    public void update(List<ScanDetail> list) {
    }

    private int calculateBssidScore(ScanResult scanResult, WifiConfiguration network, WifiConfiguration currentNetwork, String currentBssid, StringBuffer sbuf) {
        ScanResult scanResult2 = scanResult;
        WifiConfiguration wifiConfiguration = network;
        WifiConfiguration wifiConfiguration2 = currentNetwork;
        String str = currentBssid;
        StringBuffer stringBuffer = sbuf;
        boolean is5GHz = scanResult.is5GHz();
        stringBuffer.append("[ ");
        stringBuffer.append(scanResult2.SSID);
        stringBuffer.append(" ");
        stringBuffer.append(scanResult2.BSSID);
        stringBuffer.append(" RSSI:");
        stringBuffer.append(scanResult2.level);
        stringBuffer.append(" ] ");
        int score = 0 + ((this.mRssiScoreOffset + Math.min(scanResult2.level, this.mScoringParams.getGoodRssi(scanResult2.frequency))) * this.mRssiScoreSlope);
        stringBuffer.append(" RSSI score: ");
        stringBuffer.append(score);
        stringBuffer.append(",");
        if (is5GHz) {
            score += this.mBand5GHzAward;
            stringBuffer.append(" 5GHz bonus: ");
            stringBuffer.append(this.mBand5GHzAward);
            stringBuffer.append(",");
        }
        int lastUserSelectedNetworkId = this.mWifiConfigManager.getLastSelectedNetwork();
        if (lastUserSelectedNetworkId == -1 || lastUserSelectedNetworkId != wifiConfiguration.networkId) {
        } else {
            long timeDifference = this.mClock.getElapsedSinceBootMillis() - this.mWifiConfigManager.getLastSelectedTimeStamp();
            if (timeDifference > 0) {
                boolean z = is5GHz;
                int bonus = Math.max(this.mLastSelectionAward - ((int) (timeDifference / 60000)), 0);
                score += bonus;
                stringBuffer.append(" User selection ");
                stringBuffer.append(timeDifference);
                stringBuffer.append(" ms ago, bonus: ");
                stringBuffer.append(bonus);
                stringBuffer.append(",");
            }
        }
        if (wifiConfiguration2 != null && wifiConfiguration.networkId == wifiConfiguration2.networkId) {
            score += this.mSameNetworkAward;
            stringBuffer.append(" Same network bonus: ");
            stringBuffer.append(this.mSameNetworkAward);
            stringBuffer.append(",");
            if (this.mConnectivityHelper.isFirmwareRoamingSupported() && str != null && !str.equals(scanResult2.BSSID)) {
                score += this.mSameBssidAward;
                stringBuffer.append(" Equivalent BSSID bonus: ");
                stringBuffer.append(this.mSameBssidAward);
                stringBuffer.append(",");
            }
        }
        if (str != null && str.equals(scanResult2.BSSID)) {
            score += this.mSameBssidAward;
            stringBuffer.append(" Same BSSID bonus: ");
            stringBuffer.append(this.mSameBssidAward);
            stringBuffer.append(",");
        }
        if (!WifiConfigurationUtil.isConfigForOpenNetwork(network)) {
            score += this.mSecurityAward;
            stringBuffer.append(" Secure network bonus: ");
            stringBuffer.append(this.mSecurityAward);
            stringBuffer.append(",");
        }
        stringBuffer.append(" ## Total score: ");
        stringBuffer.append(score);
        stringBuffer.append("\n");
        return score;
    }

    public WifiConfiguration evaluateNetworks(List<ScanDetail> scanDetails, WifiConfiguration currentNetwork, String currentBssid, boolean connected, boolean untrustedNetworkAllowed, WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener) {
        StringBuffer scoreHistory = new StringBuffer();
        int highestScore = Integer.MIN_VALUE;
        ScanResult scanResultCandidate = null;
        WifiConfiguration candidate = null;
        for (ScanDetail scanDetail : scanDetails) {
            ScanResult scanResult = scanDetail.getScanResult();
            WifiConfiguration network = this.mWifiConfigManager.getConfiguredNetworkForScanDetailAndCache(scanDetail);
            if (network == null) {
                WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener2 = onConnectableListener;
            } else if (network.isPasspoint()) {
                WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener3 = onConnectableListener;
            } else if (network.isEphemeral()) {
                WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener4 = onConnectableListener;
            } else {
                WifiConfiguration.NetworkSelectionStatus status = network.getNetworkSelectionStatus();
                status.setSeenInLastQualifiedNetworkSelection(true);
                if (!status.isNetworkEnabled()) {
                    WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener5 = onConnectableListener;
                } else if (status.getHasEverConnected() && network.isNoInternetAccessExpected()) {
                    localLog("Network " + WifiNetworkSelector.toNetworkString(network) + " is expected to have no Internet.");
                    WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener6 = onConnectableListener;
                } else if (network.BSSID != null && !network.BSSID.equals("any") && !network.BSSID.equals(scanResult.BSSID)) {
                    localLog("Network " + WifiNetworkSelector.toNetworkString(network) + " has specified BSSID " + network.BSSID + ". Skip " + scanResult.BSSID);
                    WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener7 = onConnectableListener;
                } else if (!TelephonyUtil.isSimConfig(network) || TelephonyUtil.isSimPresent(this.mSubscriptionManager)) {
                    int score = calculateBssidScore(scanResult, network, currentNetwork, currentBssid, scoreHistory);
                    if (score > status.getCandidateScore() || (score == status.getCandidateScore() && status.getCandidate() != null && scanResult.level > status.getCandidate().level)) {
                        this.mWifiConfigManager.setNetworkCandidateScanResult(network.networkId, scanResult, score);
                    }
                    if (network.useExternalScores) {
                        localLog("Network " + WifiNetworkSelector.toNetworkString(network) + " has external score.");
                        WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener8 = onConnectableListener;
                    } else {
                        onConnectableListener.onConnectable(scanDetail, this.mWifiConfigManager.getConfiguredNetwork(network.networkId), score);
                        if (score > highestScore || (score == highestScore && scanResultCandidate != null && scanResult.level > scanResultCandidate.level)) {
                            int highestScore2 = score;
                            ScanResult scanResultCandidate2 = scanResult;
                            this.mWifiConfigManager.setNetworkCandidateScanResult(network.networkId, scanResultCandidate2, highestScore2);
                            highestScore = highestScore2;
                            scanResultCandidate = scanResultCandidate2;
                            candidate = this.mWifiConfigManager.getConfiguredNetwork(network.networkId);
                        }
                    }
                } else {
                    WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener9 = onConnectableListener;
                }
            }
        }
        WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener10 = onConnectableListener;
        if (scoreHistory.length() > 0) {
            localLog("\n" + scoreHistory.toString());
        }
        if (scanResultCandidate == null) {
            localLog("did not see any good candidates.");
        }
        return candidate;
    }
}
