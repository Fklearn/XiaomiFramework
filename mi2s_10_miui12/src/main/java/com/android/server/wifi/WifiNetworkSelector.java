package com.android.server.wifi;

import android.content.Context;
import android.content.res.MiuiConfiguration;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.wifi.WifiCandidates;
import com.android.server.wifi.WifiNetworkSelector;
import com.android.server.wifi.rtt.RttServiceImpl;
import com.android.server.wifi.util.ScanResultUtil;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WifiNetworkSelector {
    private static final int ID_PREFIX = 42;
    private static final int ID_SUFFIX_MOD = 1000000;
    private static final long INVALID_TIME_STAMP = Long.MIN_VALUE;
    @VisibleForTesting
    public static final int LAST_USER_SELECTION_DECAY_TO_ZERO_MS = 28800000;
    @VisibleForTesting
    public static final int LAST_USER_SELECTION_SUFFICIENT_MS = 30000;
    public static final int LEGACY_CANDIDATE_SCORER_EXP_ID = 0;
    @VisibleForTesting
    public static final int MINIMUM_NETWORK_SELECTION_INTERVAL_MS = 10000;
    private static final int MIN_SCORER_EXP_ID = 42000000;
    public static final String PRESET_CANDIDATE_SCORER_NAME = "CompatibilityScorer";
    private static final String TAG = "WifiNetworkSelector";
    @VisibleForTesting
    public static final int WIFI_POOR_SCORE = 40;
    private final Map<String, WifiCandidates.CandidateScorer> mCandidateScorers = new ArrayMap();
    private final Clock mClock;
    private final List<Pair<ScanDetail, WifiConfiguration>> mConnectableNetworks = new ArrayList();
    private final boolean mEnableAutoJoinWhenAssociated;
    private final List<NetworkEvaluator> mEvaluators = new ArrayList(3);
    private List<ScanDetail> mFilteredNetworks = new ArrayList();
    private boolean mIsEnhancedOpenSupported;
    private boolean mIsEnhancedOpenSupportedInitialized = false;
    private long mLastNetworkSelectionTimeStamp = INVALID_TIME_STAMP;
    private final LocalLog mLocalLog;
    private final ScoringParams mScoringParams;
    private final int mStayOnNetworkMinimumRxRate;
    private final int mStayOnNetworkMinimumTxRate;
    private final WifiConfigManager mWifiConfigManager;
    private final WifiMetrics mWifiMetrics;
    private final WifiNative mWifiNative;
    private final WifiScoreCard mWifiScoreCard;

    public interface NetworkEvaluator {
        public static final int EVALUATOR_ID_CARRIER = 3;
        public static final int EVALUATOR_ID_PASSPOINT = 2;
        public static final int EVALUATOR_ID_SAVED = 0;
        public static final int EVALUATOR_ID_SCORED = 4;
        public static final int EVALUATOR_ID_SUGGESTION = 1;

        @Retention(RetentionPolicy.SOURCE)
        public @interface EvaluatorId {
        }

        public interface OnConnectableListener {
            void onConnectable(ScanDetail scanDetail, WifiConfiguration wifiConfiguration, int i);
        }

        WifiConfiguration evaluateNetworks(List<ScanDetail> list, WifiConfiguration wifiConfiguration, String str, boolean z, boolean z2, OnConnectableListener onConnectableListener);

        int getId();

        String getName();

        void update(List<ScanDetail> list);
    }

    private void localLog(String log) {
        this.mLocalLog.log(log);
    }

    private boolean isCurrentNetworkSufficient(WifiInfo wifiInfo, List<ScanDetail> list) {
        if (wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
            localLog("No current connected network.");
            return false;
        }
        localLog("Current connected network: " + wifiInfo.getSSID() + " , ID: " + wifiInfo.getNetworkId());
        int currentRssi = wifiInfo.getRssi();
        boolean hasQualifiedRssi = currentRssi > this.mScoringParams.getSufficientRssi(wifiInfo.getFrequency());
        boolean hasActiveStream = wifiInfo.txSuccessRate > ((double) this.mStayOnNetworkMinimumTxRate) || wifiInfo.rxSuccessRate > ((double) this.mStayOnNetworkMinimumRxRate);
        if (!hasQualifiedRssi || !hasActiveStream) {
            WifiConfiguration network = this.mWifiConfigManager.getConfiguredNetwork(wifiInfo.getNetworkId());
            if (network == null) {
                localLog("Current network was removed.");
                return false;
            } else if (this.mWifiConfigManager.getLastSelectedNetwork() == network.networkId && this.mClock.getElapsedSinceBootMillis() - this.mWifiConfigManager.getLastSelectedTimeStamp() <= 30000) {
                localLog("Current network is recently user-selected.");
                return true;
            } else if (network.osu) {
                return true;
            } else {
                if (wifiInfo.isEphemeral()) {
                    localLog("Current network is an ephemeral one.");
                    return false;
                } else if (!hasQualifiedRssi) {
                    localLog("Current network RSSI[" + currentRssi + "]-acceptable but not qualified.");
                    return false;
                } else if (WifiConfigurationUtil.isConfigForOpenNetwork(network)) {
                    localLog("Current network is a open one.");
                    return false;
                } else if (network.numNoInternetAccessReports <= 0 || network.noInternetAccessExpected) {
                    return true;
                } else {
                    localLog("Current network has [" + network.numNoInternetAccessReports + "] no-internet access reports.");
                    return false;
                }
            }
        } else {
            localLog("Stay on current network because of good RSSI and ongoing traffic");
            return true;
        }
    }

    private boolean is5GHzNetworkAvailable(List<ScanDetail> scanDetails) {
        for (ScanDetail detail : scanDetails) {
            if (detail.getScanResult().is5GHz()) {
                return true;
            }
        }
        return false;
    }

    private boolean isNetworkSelectionNeeded(List<ScanDetail> scanDetails, WifiInfo wifiInfo, boolean connected, boolean disconnected) {
        if (scanDetails.size() == 0) {
            localLog("Empty connectivity scan results. Skip network selection.");
            return false;
        } else if (connected) {
            if (!this.mEnableAutoJoinWhenAssociated) {
                localLog("Switching networks in connected state is not allowed. Skip network selection.");
                return false;
            }
            if (this.mLastNetworkSelectionTimeStamp != INVALID_TIME_STAMP) {
                long gap = this.mClock.getElapsedSinceBootMillis() - this.mLastNetworkSelectionTimeStamp;
                if (gap < RttServiceImpl.HAL_AWARE_RANGING_TIMEOUT_MS) {
                    localLog("Too short since last network selection: " + gap + " ms. Skip network selection.");
                    return false;
                }
            }
            if (isCurrentNetworkSufficient(wifiInfo, scanDetails)) {
                localLog("Current connected network already sufficient. Skip network selection.");
                return false;
            }
            localLog("Current connected network is not sufficient.");
            return true;
        } else if (disconnected) {
            return true;
        } else {
            localLog("ClientModeImpl is in neither CONNECTED nor DISCONNECTED state. Skip network selection.");
            return false;
        }
    }

    public static String toScanId(ScanResult scanResult) {
        if (scanResult == null) {
            return "NULL";
        }
        return String.format("%s:%s", new Object[]{scanResult.SSID, scanResult.BSSID});
    }

    public static String toNetworkString(WifiConfiguration network) {
        if (network == null) {
            return null;
        }
        return network.SSID + ":" + network.networkId;
    }

    public boolean isSignalTooWeak(ScanResult scanResult) {
        return scanResult.level < this.mScoringParams.getEntryRssi(scanResult.frequency);
    }

    private List<ScanDetail> filterScanResults(List<ScanDetail> scanDetails, HashSet<String> bssidBlacklist, boolean isConnected, String currentBssid) {
        String str = currentBssid;
        new ArrayList();
        List<ScanDetail> validScanDetails = new ArrayList<>();
        StringBuffer noValidSsid = new StringBuffer();
        StringBuffer blacklistedBssid = new StringBuffer();
        StringBuffer lowRssi = new StringBuffer();
        boolean scanResultsHaveCurrentBssid = false;
        for (ScanDetail scanDetail : scanDetails) {
            ScanResult scanResult = scanDetail.getScanResult();
            if (TextUtils.isEmpty(scanResult.SSID)) {
                noValidSsid.append(scanResult.BSSID);
                noValidSsid.append(" / ");
            } else if (!WifiInjector.getInstance().getWifiAutoConnController().isDisableByUser(scanResult.SSID)) {
                if (scanResult.BSSID.equals(str)) {
                    scanResultsHaveCurrentBssid = true;
                }
                String scanId = toScanId(scanResult);
                if (bssidBlacklist.contains(scanResult.BSSID)) {
                    blacklistedBssid.append(scanId);
                    blacklistedBssid.append(" / ");
                } else if (isSignalTooWeak(scanResult)) {
                    lowRssi.append(scanId);
                    lowRssi.append("(");
                    lowRssi.append(scanResult.is24GHz() ? "2.4GHz" : "5GHz");
                    lowRssi.append(")");
                    lowRssi.append(scanResult.level);
                    lowRssi.append(" / ");
                } else {
                    validScanDetails.add(scanDetail);
                }
            }
        }
        HashSet<String> hashSet = bssidBlacklist;
        if (!isConnected || scanResultsHaveCurrentBssid) {
            if (noValidSsid.length() != 0) {
                localLog("Networks filtered out due to invalid SSID: " + noValidSsid);
            }
            if (blacklistedBssid.length() != 0) {
                localLog("Networks filtered out due to blacklist: " + blacklistedBssid);
            }
            if (lowRssi.length() != 0) {
                localLog("Networks filtered out due to low signal strength: " + lowRssi);
            }
            return validScanDetails;
        }
        localLog("Current connected BSSID " + str + " is not in the scan results. Skip network selection.");
        validScanDetails.clear();
        return validScanDetails;
    }

    private boolean isEnhancedOpenSupported() {
        if (this.mIsEnhancedOpenSupportedInitialized) {
            return this.mIsEnhancedOpenSupported;
        }
        boolean z = true;
        this.mIsEnhancedOpenSupportedInitialized = true;
        WifiNative wifiNative = this.mWifiNative;
        if ((wifiNative.getSupportedFeatureSet(wifiNative.getClientInterfaceName()) & MiuiConfiguration.THEME_FLAG_VAR_FONT) == 0) {
            z = false;
        }
        this.mIsEnhancedOpenSupported = z;
        return this.mIsEnhancedOpenSupported;
    }

    public List<ScanDetail> getFilteredScanDetailsForOpenUnsavedNetworks() {
        List<ScanDetail> openUnsavedNetworks = new ArrayList<>();
        boolean enhancedOpenSupported = isEnhancedOpenSupported();
        for (ScanDetail scanDetail : this.mFilteredNetworks) {
            ScanResult scanResult = scanDetail.getScanResult();
            if (ScanResultUtil.isScanResultForOpenNetwork(scanResult) && ((!ScanResultUtil.isScanResultForOweNetwork(scanResult) || enhancedOpenSupported) && this.mWifiConfigManager.getConfiguredNetworkForScanDetailAndCache(scanDetail) == null)) {
                openUnsavedNetworks.add(scanDetail);
            }
        }
        return openUnsavedNetworks;
    }

    public List<ScanDetail> getFilteredScanDetailsForCarrierUnsavedNetworks(CarrierNetworkConfig carrierConfig) {
        List<ScanDetail> carrierUnsavedNetworks = new ArrayList<>();
        for (ScanDetail scanDetail : this.mFilteredNetworks) {
            ScanResult scanResult = scanDetail.getScanResult();
            if (ScanResultUtil.isScanResultForEapNetwork(scanResult) && carrierConfig.isCarrierNetwork(scanResult.SSID) && this.mWifiConfigManager.getConfiguredNetworkForScanDetailAndCache(scanDetail) == null) {
                carrierUnsavedNetworks.add(scanDetail);
            }
        }
        return carrierUnsavedNetworks;
    }

    public List<Pair<ScanDetail, WifiConfiguration>> getConnectableScanDetails() {
        return this.mConnectableNetworks;
    }

    public boolean setUserConnectChoice(int netId) {
        localLog("userSelectNetwork: network ID=" + netId);
        WifiConfiguration selected = this.mWifiConfigManager.getConfiguredNetwork(netId);
        if (selected == null || selected.SSID == null) {
            localLog("userSelectNetwork: Invalid configuration with nid=" + netId);
            return false;
        }
        if (!selected.getNetworkSelectionStatus().isNetworkEnabled()) {
            this.mWifiConfigManager.updateNetworkSelectionStatus(netId, 0);
        }
        return setLegacyUserConnectChoice(selected);
    }

    private boolean setLegacyUserConnectChoice(WifiConfiguration selected) {
        boolean change = false;
        String key = selected.configKey();
        long currentTime = this.mClock.getWallClockMillis();
        for (WifiConfiguration network : this.mWifiConfigManager.getConfiguredNetworks()) {
            WifiConfiguration.NetworkSelectionStatus status = network.getNetworkSelectionStatus();
            if (network.configKey().equals(selected.configKey())) {
                if (status.getConnectChoice() != null) {
                    localLog("Remove user selection preference of " + status.getConnectChoice() + " Set Time: " + status.getConnectChoiceTimestamp() + " from " + network.SSID + " : " + network.networkId);
                    this.mWifiConfigManager.clearNetworkConnectChoice(network.networkId);
                    change = true;
                }
            } else if (status.getSeenInLastQualifiedNetworkSelection() && !key.equals(status.getConnectChoice())) {
                localLog("Add key: " + key + " Set Time: " + currentTime + " to " + toNetworkString(network));
                this.mWifiConfigManager.setNetworkConnectChoice(network.networkId, key, currentTime);
                change = true;
            }
        }
        return change;
    }

    private void updateConfiguredNetworks() {
        List<WifiConfiguration> configuredNetworks = this.mWifiConfigManager.getConfiguredNetworks();
        if (configuredNetworks.size() == 0) {
            localLog("No configured networks.");
            return;
        }
        StringBuffer sbuf = new StringBuffer();
        for (WifiConfiguration network : configuredNetworks) {
            this.mWifiConfigManager.tryEnableNetwork(network.networkId);
            this.mWifiConfigManager.clearNetworkCandidateScanResult(network.networkId);
            WifiConfiguration.NetworkSelectionStatus status = network.getNetworkSelectionStatus();
            if (!status.isNetworkEnabled()) {
                sbuf.append("  ");
                sbuf.append(toNetworkString(network));
                sbuf.append(" ");
                for (int index = 1; index < 15; index++) {
                    int count = status.getDisableReasonCounter(index);
                    if (count > 0) {
                        sbuf.append("reason=");
                        sbuf.append(WifiConfiguration.NetworkSelectionStatus.getNetworkDisableReasonString(index));
                        sbuf.append(", count=");
                        sbuf.append(count);
                        sbuf.append("; ");
                    }
                }
                sbuf.append("\n");
            }
        }
        if (sbuf.length() > 0) {
            localLog("Disabled configured networks:");
            localLog(sbuf.toString());
        }
    }

    private WifiConfiguration overrideCandidateWithUserConnectChoice(WifiConfiguration candidate) {
        WifiConfiguration tempConfig = (WifiConfiguration) Preconditions.checkNotNull(candidate);
        WifiConfiguration originalCandidate = candidate;
        ScanResult scanResultCandidate = candidate.getNetworkSelectionStatus().getCandidate();
        while (true) {
            if (tempConfig.getNetworkSelectionStatus().getConnectChoice() == null) {
                break;
            }
            String key = tempConfig.getNetworkSelectionStatus().getConnectChoice();
            if (!key.equals(tempConfig.configKey())) {
                tempConfig = this.mWifiConfigManager.getConfiguredNetwork(key);
                if (tempConfig == null) {
                    localLog("Connect choice: " + key + " has no corresponding saved config.");
                    break;
                }
                WifiConfiguration.NetworkSelectionStatus tempStatus = tempConfig.getNetworkSelectionStatus();
                if (tempStatus.getCandidate() != null && tempStatus.isNetworkEnabled()) {
                    scanResultCandidate = tempStatus.getCandidate();
                    candidate = tempConfig;
                }
            } else {
                localLog("Connect choice: " + key + " is itself: " + tempConfig.configKey());
                break;
            }
        }
        if (candidate != originalCandidate) {
            localLog("After user selection adjustment, the final candidate is:" + toNetworkString(candidate) + " : " + scanResultCandidate.BSSID);
            this.mWifiMetrics.setNominatorForNetwork(candidate.networkId, 8);
        }
        return candidate;
    }

    public WifiConfiguration selectNetwork(List<ScanDetail> scanDetails, HashSet<String> bssidBlacklist, WifiInfo wifiInfo, boolean connected, boolean disconnected, boolean untrustedNetworkAllowed) {
        int selectedNetworkId;
        int activeExperimentId;
        int networkId;
        ScanDetail scanDetail;
        List<ScanDetail> list = scanDetails;
        WifiInfo wifiInfo2 = wifiInfo;
        boolean z = connected;
        this.mFilteredNetworks.clear();
        this.mConnectableNetworks.clear();
        if (scanDetails.size() == 0) {
            localLog("Empty connectivity scan result");
            return null;
        }
        WifiConfiguration currentNetwork = this.mWifiConfigManager.getConfiguredNetwork(wifiInfo.getNetworkId());
        String currentBssid = wifiInfo.getBSSID();
        if (!isNetworkSelectionNeeded(list, wifiInfo2, z, disconnected)) {
            return null;
        }
        updateConfiguredNetworks();
        for (NetworkEvaluator registeredEvaluator : this.mEvaluators) {
            registeredEvaluator.update(list);
        }
        this.mFilteredNetworks = filterScanResults(list, bssidBlacklist, z && wifiInfo2.score >= 40, currentBssid);
        if (this.mFilteredNetworks.size() == 0) {
            return null;
        }
        int lastUserSelectedNetworkId = this.mWifiConfigManager.getLastSelectedNetwork();
        double lastSelectionWeight = calculateLastSelectionWeight();
        ArraySet<Integer> mNetworkIds = new ArraySet<>();
        WifiCandidates wifiCandidates = new WifiCandidates(this.mWifiScoreCard);
        if (currentNetwork != null) {
            wifiCandidates.setCurrent(currentNetwork.networkId, currentBssid);
        }
        WifiConfiguration selectedNetwork = null;
        for (NetworkEvaluator registeredEvaluator2 : this.mEvaluators) {
            localLog("About to run " + registeredEvaluator2.getName() + " :");
            ArrayList arrayList = new ArrayList(this.mFilteredNetworks);
            String str = TAG;
            WifiConfiguration selectedNetwork2 = selectedNetwork;
            WifiCandidates wifiCandidates2 = wifiCandidates;
            ArraySet<Integer> mNetworkIds2 = mNetworkIds;
            WifiConfiguration choice = registeredEvaluator2.evaluateNetworks(arrayList, currentNetwork, currentBssid, connected, untrustedNetworkAllowed, new NetworkEvaluator.OnConnectableListener(mNetworkIds, lastUserSelectedNetworkId, wifiCandidates, registeredEvaluator2, lastSelectionWeight) {
                private final /* synthetic */ ArraySet f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ WifiCandidates f$3;
                private final /* synthetic */ WifiNetworkSelector.NetworkEvaluator f$4;
                private final /* synthetic */ double f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void onConnectable(ScanDetail scanDetail, WifiConfiguration wifiConfiguration, int i) {
                    WifiNetworkSelector.this.lambda$selectNetwork$0$WifiNetworkSelector(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, scanDetail, wifiConfiguration, i);
                }
            });
            if (choice != null && !mNetworkIds2.contains(Integer.valueOf(choice.networkId))) {
                Log.wtf(str, registeredEvaluator2.getName() + " failed to report choice with noConnectibleListener");
            }
            if (selectedNetwork2 != null || choice == null) {
                selectedNetwork = selectedNetwork2;
            } else {
                WifiConfiguration selectedNetwork3 = choice;
                localLog(registeredEvaluator2.getName() + " selects " + toNetworkString(selectedNetwork3));
                selectedNetwork = selectedNetwork3;
            }
            List<ScanDetail> list2 = scanDetails;
            HashSet<String> hashSet = bssidBlacklist;
            WifiInfo wifiInfo3 = wifiInfo;
            mNetworkIds = mNetworkIds2;
            wifiCandidates = wifiCandidates2;
            boolean z2 = connected;
        }
        String str2 = TAG;
        WifiConfiguration selectedNetwork4 = selectedNetwork;
        WifiCandidates wifiCandidates3 = wifiCandidates;
        ArraySet<Integer> mNetworkIds3 = mNetworkIds;
        if (this.mConnectableNetworks.size() != wifiCandidates3.size()) {
            localLog("Connectable: " + this.mConnectableNetworks.size() + " Candidates: " + wifiCandidates3.size());
        }
        Collection<Collection<WifiCandidates.Candidate>> groupedCandidates = wifiCandidates3.getGroupedCandidates();
        for (Collection<WifiCandidates.Candidate> group : groupedCandidates) {
            WifiCandidates.Candidate best = null;
            for (WifiCandidates.Candidate candidate : group) {
                if (best == null || candidate.getEvaluatorId() < best.getEvaluatorId() || (candidate.getEvaluatorId() == best.getEvaluatorId() && candidate.getEvaluatorScore() > best.getEvaluatorScore())) {
                    best = candidate;
                }
            }
            if (!(best == null || (scanDetail = best.getScanDetail()) == null)) {
                this.mWifiConfigManager.setNetworkCandidateScanResult(best.getNetworkConfigId(), scanDetail.getScanResult(), best.getEvaluatorScore());
            }
        }
        ArrayMap<Integer, Integer> experimentNetworkSelections = new ArrayMap<>();
        if (selectedNetwork4 == null) {
            selectedNetworkId = -1;
        } else {
            selectedNetworkId = selectedNetwork4.networkId;
        }
        int legacySelectedNetworkId = selectedNetworkId;
        boolean legacyOverrideWanted = true;
        WifiCandidates.CandidateScorer activeScorer = getActiveCandidateScorer();
        Iterator<WifiCandidates.CandidateScorer> it = this.mCandidateScorers.values().iterator();
        int selectedNetworkId2 = selectedNetworkId;
        while (it.hasNext() != 0) {
            WifiCandidates.CandidateScorer candidateScorer = it.next();
            Iterator<WifiCandidates.CandidateScorer> it2 = it;
            WifiCandidates wifiCandidates4 = wifiCandidates3;
            try {
                WifiCandidates.ScoredCandidate choice2 = wifiCandidates4.choose(candidateScorer);
                wifiCandidates3 = wifiCandidates4;
                if (choice2.candidateKey == null) {
                    networkId = -1;
                } else {
                    networkId = choice2.candidateKey.networkId;
                }
                String chooses = " would choose ";
                if (candidateScorer == activeScorer) {
                    chooses = " chooses ";
                    legacyOverrideWanted = candidateScorer.userConnectChoiceOverrideWanted();
                    selectedNetworkId2 = networkId;
                }
                int selectedNetworkId3 = selectedNetworkId2;
                boolean legacyOverrideWanted2 = legacyOverrideWanted;
                String chooses2 = chooses;
                boolean legacyOverrideWanted3 = legacyOverrideWanted2;
                String id = candidateScorer.getIdentifier();
                WifiCandidates.CandidateScorer candidateScorer2 = candidateScorer;
                int expid = experimentIdFromIdentifier(id);
                WifiConfiguration selectedNetwork5 = selectedNetwork4;
                StringBuilder sb = new StringBuilder();
                sb.append(id);
                sb.append(chooses2);
                sb.append(networkId);
                String str3 = chooses2;
                sb.append(" score ");
                sb.append(choice2.value);
                sb.append("+/-");
                sb.append(choice2.err);
                sb.append(" expid ");
                sb.append(expid);
                localLog(sb.toString());
                experimentNetworkSelections.put(Integer.valueOf(expid), Integer.valueOf(networkId));
                it = it2;
                selectedNetworkId2 = selectedNetworkId3;
                legacyOverrideWanted = legacyOverrideWanted3;
                selectedNetwork4 = selectedNetwork5;
                currentNetwork = currentNetwork;
                mNetworkIds3 = mNetworkIds3;
            } catch (RuntimeException e) {
                WifiCandidates.CandidateScorer candidateScorer3 = candidateScorer;
                wifiCandidates3 = wifiCandidates4;
                Log.wtf(str2, "Exception running a CandidateScorer", e);
                it = it2;
                selectedNetwork4 = selectedNetwork4;
                currentNetwork = currentNetwork;
                mNetworkIds3 = mNetworkIds3;
            }
        }
        ArraySet<Integer> arraySet = mNetworkIds3;
        WifiConfiguration wifiConfiguration = currentNetwork;
        if (activeScorer == null) {
            activeExperimentId = 0;
        } else {
            activeExperimentId = experimentIdFromIdentifier(activeScorer.getIdentifier());
        }
        experimentNetworkSelections.put(0, Integer.valueOf(legacySelectedNetworkId));
        for (Map.Entry<Integer, Integer> entry : experimentNetworkSelections.entrySet()) {
            int experimentId = entry.getKey().intValue();
            if (experimentId != activeExperimentId) {
                this.mWifiMetrics.logNetworkSelectionDecision(experimentId, activeExperimentId, selectedNetworkId2 == entry.getValue().intValue(), groupedCandidates.size());
                experimentNetworkSelections = experimentNetworkSelections;
            }
        }
        WifiConfiguration selectedNetwork6 = this.mWifiConfigManager.getConfiguredNetwork(selectedNetworkId2);
        if (selectedNetwork6 == null || !legacyOverrideWanted) {
            return selectedNetwork6;
        }
        WifiConfiguration selectedNetwork7 = overrideCandidateWithUserConnectChoice(selectedNetwork6);
        this.mLastNetworkSelectionTimeStamp = this.mClock.getElapsedSinceBootMillis();
        return selectedNetwork7;
    }

    public /* synthetic */ void lambda$selectNetwork$0$WifiNetworkSelector(ArraySet mNetworkIds, int lastUserSelectedNetworkId, WifiCandidates wifiCandidates, NetworkEvaluator registeredEvaluator, double lastSelectionWeight, ScanDetail scanDetail, WifiConfiguration config, int score) {
        WifiConfiguration wifiConfiguration = config;
        if (wifiConfiguration != null) {
            this.mConnectableNetworks.add(Pair.create(scanDetail, config));
            ArraySet arraySet = mNetworkIds;
            mNetworkIds.add(Integer.valueOf(wifiConfiguration.networkId));
            if (wifiConfiguration.networkId == lastUserSelectedNetworkId) {
                wifiCandidates.add(scanDetail, config, registeredEvaluator.getId(), score, lastSelectionWeight);
                WifiCandidates wifiCandidates2 = wifiCandidates;
                ScanDetail scanDetail2 = scanDetail;
                int i = score;
            } else {
                WifiCandidates wifiCandidates3 = wifiCandidates;
                wifiCandidates.add(scanDetail, wifiConfiguration, registeredEvaluator.getId(), score);
            }
            this.mWifiMetrics.setNominatorForNetwork(wifiConfiguration.networkId, evaluatorIdToNominatorId(registeredEvaluator.getId()));
            return;
        }
        ArraySet arraySet2 = mNetworkIds;
        int i2 = lastUserSelectedNetworkId;
        WifiCandidates wifiCandidates4 = wifiCandidates;
        ScanDetail scanDetail3 = scanDetail;
        int i3 = score;
    }

    private static int evaluatorIdToNominatorId(int evaluatorId) {
        if (evaluatorId == 0) {
            return 2;
        }
        if (evaluatorId == 1) {
            return 3;
        }
        if (evaluatorId == 2) {
            return 4;
        }
        if (evaluatorId == 3) {
            return 5;
        }
        if (evaluatorId == 4) {
            return 6;
        }
        Log.e(TAG, "UnrecognizedEvaluatorId" + evaluatorId);
        return 0;
    }

    private double calculateLastSelectionWeight() {
        if (this.mWifiConfigManager.getLastSelectedNetwork() != -1) {
            return Math.min(Math.max(1.0d - (((double) (this.mClock.getElapsedSinceBootMillis() - this.mWifiConfigManager.getLastSelectedTimeStamp())) / 2.88E7d), 0.0d), 1.0d);
        }
        return 0.0d;
    }

    private WifiCandidates.CandidateScorer getActiveCandidateScorer() {
        int i;
        WifiCandidates.CandidateScorer ans = this.mCandidateScorers.get(PRESET_CANDIDATE_SCORER_NAME);
        int overrideExperimentId = this.mScoringParams.getExperimentIdentifier();
        if (overrideExperimentId >= MIN_SCORER_EXP_ID) {
            Iterator<WifiCandidates.CandidateScorer> it = this.mCandidateScorers.values().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                WifiCandidates.CandidateScorer candidateScorer = it.next();
                if (experimentIdFromIdentifier(candidateScorer.getIdentifier()) == overrideExperimentId) {
                    ans = candidateScorer;
                    break;
                }
            }
        }
        if (ans == null) {
            Log.wtf(TAG, "CompatibilityScorer is not registered!");
        }
        WifiMetrics wifiMetrics = this.mWifiMetrics;
        if (ans == null) {
            i = 0;
        } else {
            i = experimentIdFromIdentifier(ans.getIdentifier());
        }
        wifiMetrics.setNetworkSelectorExperimentId(i);
        return ans;
    }

    public void registerNetworkEvaluator(NetworkEvaluator evaluator) {
        this.mEvaluators.add((NetworkEvaluator) Preconditions.checkNotNull(evaluator));
    }

    public void registerCandidateScorer(WifiCandidates.CandidateScorer candidateScorer) {
        String name = ((WifiCandidates.CandidateScorer) Preconditions.checkNotNull(candidateScorer)).getIdentifier();
        if (name != null) {
            this.mCandidateScorers.put(name, candidateScorer);
        }
    }

    public void unregisterCandidateScorer(WifiCandidates.CandidateScorer candidateScorer) {
        String name = ((WifiCandidates.CandidateScorer) Preconditions.checkNotNull(candidateScorer)).getIdentifier();
        if (name != null) {
            this.mCandidateScorers.remove(name);
        }
    }

    public static int experimentIdFromIdentifier(String id) {
        return MIN_SCORER_EXP_ID + (((int) (((long) id.hashCode()) & 2147483647L)) % ID_SUFFIX_MOD);
    }

    WifiNetworkSelector(Context context, WifiScoreCard wifiScoreCard, ScoringParams scoringParams, WifiConfigManager configManager, Clock clock, LocalLog localLog, WifiMetrics wifiMetrics, WifiNative wifiNative) {
        this.mWifiConfigManager = configManager;
        this.mClock = clock;
        this.mWifiScoreCard = wifiScoreCard;
        this.mScoringParams = scoringParams;
        this.mLocalLog = localLog;
        this.mWifiMetrics = wifiMetrics;
        this.mWifiNative = wifiNative;
        this.mEnableAutoJoinWhenAssociated = context.getResources().getBoolean(17891591);
        this.mStayOnNetworkMinimumTxRate = context.getResources().getInteger(17694938);
        this.mStayOnNetworkMinimumRxRate = context.getResources().getInteger(17694937);
    }
}
