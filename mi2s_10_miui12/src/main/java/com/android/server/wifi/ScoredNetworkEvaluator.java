package com.android.server.wifi;

import android.content.Context;
import android.database.ContentObserver;
import android.net.NetworkKey;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.LocalLog;
import android.util.Log;
import com.android.server.wifi.WifiNetworkSelector;
import com.android.server.wifi.util.ScanResultUtil;
import com.android.server.wifi.util.WifiPermissionsUtil;
import java.util.ArrayList;
import java.util.List;

public class ScoredNetworkEvaluator implements WifiNetworkSelector.NetworkEvaluator {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final String TAG = "ScoredNetworkEvaluator";
    private final ContentObserver mContentObserver;
    /* access modifiers changed from: private */
    public final LocalLog mLocalLog;
    /* access modifiers changed from: private */
    public boolean mNetworkRecommendationsEnabled;
    private final NetworkScoreManager mNetworkScoreManager;
    /* access modifiers changed from: private */
    public WifiNetworkScoreCache mScoreCache;
    /* access modifiers changed from: private */
    public final WifiConfigManager mWifiConfigManager;
    private final WifiPermissionsUtil mWifiPermissionsUtil;

    ScoredNetworkEvaluator(final Context context, Looper looper, final FrameworkFacade frameworkFacade, NetworkScoreManager networkScoreManager, WifiConfigManager wifiConfigManager, LocalLog localLog, WifiNetworkScoreCache wifiNetworkScoreCache, WifiPermissionsUtil wifiPermissionsUtil) {
        this.mScoreCache = wifiNetworkScoreCache;
        this.mWifiPermissionsUtil = wifiPermissionsUtil;
        this.mNetworkScoreManager = networkScoreManager;
        this.mWifiConfigManager = wifiConfigManager;
        this.mLocalLog = localLog;
        this.mContentObserver = new ContentObserver(new Handler(looper)) {
            public void onChange(boolean selfChange) {
                ScoredNetworkEvaluator scoredNetworkEvaluator = ScoredNetworkEvaluator.this;
                boolean z = true;
                if (frameworkFacade.getIntegerSetting(context, "network_recommendations_enabled", 0) != 1) {
                    z = false;
                }
                boolean unused = scoredNetworkEvaluator.mNetworkRecommendationsEnabled = z;
            }
        };
        frameworkFacade.registerContentObserver(context, Settings.Global.getUriFor("network_recommendations_enabled"), false, this.mContentObserver);
        this.mContentObserver.onChange(false);
        LocalLog localLog2 = this.mLocalLog;
        localLog2.log("ScoredNetworkEvaluator constructed. mNetworkRecommendationsEnabled: " + this.mNetworkRecommendationsEnabled);
    }

    public void update(List<ScanDetail> scanDetails) {
        if (this.mNetworkRecommendationsEnabled) {
            updateNetworkScoreCache(scanDetails);
        }
    }

    private void updateNetworkScoreCache(List<ScanDetail> scanDetails) {
        ArrayList<NetworkKey> unscoredNetworks = new ArrayList<>();
        for (int i = 0; i < scanDetails.size(); i++) {
            NetworkKey networkKey = NetworkKey.createFromScanResult(scanDetails.get(i).getScanResult());
            if (networkKey != null && this.mScoreCache.getScoredNetwork(networkKey) == null) {
                unscoredNetworks.add(networkKey);
            }
        }
        if (unscoredNetworks.isEmpty() == 0 && activeScorerAllowedtoSeeScanResults()) {
            this.mNetworkScoreManager.requestScores((NetworkKey[]) unscoredNetworks.toArray(new NetworkKey[unscoredNetworks.size()]));
        }
    }

    private boolean activeScorerAllowedtoSeeScanResults() {
        NetworkScorerAppData networkScorerAppData = this.mNetworkScoreManager.getActiveScorer();
        String packageName = this.mNetworkScoreManager.getActiveScorerPackage();
        if (networkScorerAppData == null || packageName == null) {
            return false;
        }
        try {
            this.mWifiPermissionsUtil.enforceCanAccessScanResults(packageName, networkScorerAppData.packageUid);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00ec, code lost:
        if (android.text.TextUtils.equals(r17, r7.BSSID) != false) goto L_0x00f2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.net.wifi.WifiConfiguration evaluateNetworks(java.util.List<com.android.server.wifi.ScanDetail> r15, android.net.wifi.WifiConfiguration r16, java.lang.String r17, boolean r18, boolean r19, com.android.server.wifi.WifiNetworkSelector.NetworkEvaluator.OnConnectableListener r20) {
        /*
            r14 = this;
            r0 = r14
            r1 = r16
            r2 = r20
            boolean r3 = r0.mNetworkRecommendationsEnabled
            if (r3 != 0) goto L_0x0012
            android.util.LocalLog r3 = r0.mLocalLog
            java.lang.String r4 = "Skipping evaluateNetworks; Network recommendations disabled."
            r3.log(r4)
            r3 = 0
            return r3
        L_0x0012:
            com.android.server.wifi.ScoredNetworkEvaluator$ScoreTracker r3 = new com.android.server.wifi.ScoredNetworkEvaluator$ScoreTracker
            r3.<init>()
            r4 = 0
        L_0x0018:
            int r5 = r15.size()
            if (r4 >= r5) goto L_0x0104
            r5 = r15
            java.lang.Object r6 = r15.get(r4)
            com.android.server.wifi.ScanDetail r6 = (com.android.server.wifi.ScanDetail) r6
            android.net.wifi.ScanResult r7 = r6.getScanResult()
            if (r7 != 0) goto L_0x002f
            r13 = r17
            goto L_0x0100
        L_0x002f:
            com.android.server.wifi.WifiConfigManager r8 = r0.mWifiConfigManager
            java.lang.String r9 = r7.SSID
            java.lang.String r9 = com.android.server.wifi.util.ScanResultUtil.createQuotedSSID(r9)
            boolean r8 = r8.wasEphemeralNetworkDeleted(r9)
            if (r8 == 0) goto L_0x0057
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Ignoring disabled ephemeral SSID: "
            r8.append(r9)
            java.lang.String r9 = r7.SSID
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r14.debugLog(r8)
            r13 = r17
            goto L_0x0100
        L_0x0057:
            com.android.server.wifi.WifiConfigManager r8 = r0.mWifiConfigManager
            android.net.wifi.WifiConfiguration r8 = r8.getConfiguredNetworkForScanDetailAndCache(r6)
            r9 = 0
            r10 = 1
            if (r8 == 0) goto L_0x0068
            boolean r11 = r8.trusted
            if (r11 != 0) goto L_0x0066
            goto L_0x0068
        L_0x0066:
            r11 = r9
            goto L_0x0069
        L_0x0068:
            r11 = r10
        L_0x0069:
            if (r19 != 0) goto L_0x0071
            if (r11 == 0) goto L_0x0071
            r13 = r17
            goto L_0x0100
        L_0x0071:
            if (r8 != 0) goto L_0x0084
            boolean r9 = com.android.server.wifi.util.ScanResultUtil.isScanResultForOpenNetwork(r7)
            if (r9 == 0) goto L_0x0080
            r3.trackUntrustedCandidate(r6)
            r13 = r17
            goto L_0x0100
        L_0x0080:
            r13 = r17
            goto L_0x0100
        L_0x0084:
            boolean r12 = r8.trusted
            if (r12 == 0) goto L_0x0090
            boolean r12 = r8.useExternalScores
            if (r12 != 0) goto L_0x0090
            r13 = r17
            goto L_0x0100
        L_0x0090:
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r12 = r8.getNetworkSelectionStatus()
            boolean r12 = r12.isNetworkEnabled()
            if (r12 != 0) goto L_0x00b3
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Ignoring disabled SSID: "
            r9.append(r10)
            java.lang.String r10 = r8.SSID
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r14.debugLog(r9)
            r13 = r17
            goto L_0x0100
        L_0x00b3:
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r12 = r8.getNetworkSelectionStatus()
            boolean r12 = r12.getHasEverConnected()
            if (r12 == 0) goto L_0x00dc
            boolean r12 = r8.isNoInternetAccessExpected()
            if (r12 == 0) goto L_0x00dc
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Ignoring no Internet expected SSID: "
            r9.append(r10)
            java.lang.String r10 = r8.SSID
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r14.debugLog(r9)
            r13 = r17
            goto L_0x0100
        L_0x00dc:
            if (r1 == 0) goto L_0x00ef
            int r12 = r1.networkId
            int r13 = r8.networkId
            if (r12 != r13) goto L_0x00ef
            java.lang.String r12 = r7.BSSID
            r13 = r17
            boolean r12 = android.text.TextUtils.equals(r13, r12)
            if (r12 == 0) goto L_0x00f1
            goto L_0x00f2
        L_0x00ef:
            r13 = r17
        L_0x00f1:
            r10 = r9
        L_0x00f2:
            boolean r12 = r8.trusted
            if (r12 != 0) goto L_0x00fa
            r3.trackUntrustedCandidate(r7, r8, r10)
            goto L_0x00fd
        L_0x00fa:
            r3.trackExternallyScoredCandidate(r7, r8, r10)
        L_0x00fd:
            r2.onConnectable(r6, r8, r9)
        L_0x0100:
            int r4 = r4 + 1
            goto L_0x0018
        L_0x0104:
            r5 = r15
            r13 = r17
            android.net.wifi.WifiConfiguration r4 = r3.getCandidateConfiguration(r2)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.ScoredNetworkEvaluator.evaluateNetworks(java.util.List, android.net.wifi.WifiConfiguration, java.lang.String, boolean, boolean, com.android.server.wifi.WifiNetworkSelector$NetworkEvaluator$OnConnectableListener):android.net.wifi.WifiConfiguration");
    }

    class ScoreTracker {
        private static final int EXTERNAL_SCORED_NONE = 0;
        private static final int EXTERNAL_SCORED_SAVED_NETWORK = 1;
        private static final int EXTERNAL_SCORED_UNTRUSTED_NETWORK = 2;
        private int mBestCandidateType = 0;
        private WifiConfiguration mEphemeralConfig;
        private int mHighScore = -128;
        private WifiConfiguration mSavedConfig;
        private ScanDetail mScanDetailCandidate;
        private ScanResult mScanResultCandidate;

        ScoreTracker() {
        }

        private Integer getNetworkScore(ScanResult scanResult, boolean isCurrentNetwork) {
            if (!ScoredNetworkEvaluator.this.mScoreCache.isScoredNetwork(scanResult)) {
                return null;
            }
            int score = ScoredNetworkEvaluator.this.mScoreCache.getNetworkScore(scanResult, isCurrentNetwork);
            if (ScoredNetworkEvaluator.DEBUG) {
                LocalLog access$300 = ScoredNetworkEvaluator.this.mLocalLog;
                access$300.log(WifiNetworkSelector.toScanId(scanResult) + " has score: " + score + " isCurrentNetwork network: " + isCurrentNetwork);
            }
            return Integer.valueOf(score);
        }

        /* access modifiers changed from: package-private */
        public void trackUntrustedCandidate(ScanDetail scanDetail) {
            ScanResult scanResult = scanDetail.getScanResult();
            Integer score = getNetworkScore(scanResult, false);
            if (score != null && score.intValue() > this.mHighScore) {
                this.mHighScore = score.intValue();
                this.mScanResultCandidate = scanResult;
                this.mScanDetailCandidate = scanDetail;
                this.mBestCandidateType = 2;
                ScoredNetworkEvaluator scoredNetworkEvaluator = ScoredNetworkEvaluator.this;
                scoredNetworkEvaluator.debugLog(WifiNetworkSelector.toScanId(scanResult) + " becomes the new untrusted candidate.");
            }
        }

        /* access modifiers changed from: package-private */
        public void trackUntrustedCandidate(ScanResult scanResult, WifiConfiguration config, boolean isCurrentNetwork) {
            Integer score = getNetworkScore(scanResult, isCurrentNetwork);
            if (score != null && score.intValue() > this.mHighScore) {
                this.mHighScore = score.intValue();
                this.mScanResultCandidate = scanResult;
                this.mScanDetailCandidate = null;
                this.mBestCandidateType = 2;
                this.mEphemeralConfig = config;
                ScoredNetworkEvaluator.this.mWifiConfigManager.setNetworkCandidateScanResult(config.networkId, scanResult, 0);
                ScoredNetworkEvaluator scoredNetworkEvaluator = ScoredNetworkEvaluator.this;
                scoredNetworkEvaluator.debugLog(WifiNetworkSelector.toScanId(scanResult) + " becomes the new untrusted candidate.");
            }
        }

        /* access modifiers changed from: package-private */
        public void trackExternallyScoredCandidate(ScanResult scanResult, WifiConfiguration config, boolean isCurrentNetwork) {
            Integer score = getNetworkScore(scanResult, isCurrentNetwork);
            if (score == null) {
                return;
            }
            if (score.intValue() > this.mHighScore || (this.mBestCandidateType == 2 && score.intValue() == this.mHighScore)) {
                this.mHighScore = score.intValue();
                this.mSavedConfig = config;
                this.mScanResultCandidate = scanResult;
                this.mScanDetailCandidate = null;
                this.mBestCandidateType = 1;
                ScoredNetworkEvaluator.this.mWifiConfigManager.setNetworkCandidateScanResult(config.networkId, scanResult, 0);
                ScoredNetworkEvaluator scoredNetworkEvaluator = ScoredNetworkEvaluator.this;
                scoredNetworkEvaluator.debugLog(WifiNetworkSelector.toScanId(scanResult) + " becomes the new externally scored saved network candidate.");
            }
        }

        /* access modifiers changed from: package-private */
        public WifiConfiguration getCandidateConfiguration(WifiNetworkSelector.NetworkEvaluator.OnConnectableListener onConnectableListener) {
            ScanDetail scanDetail;
            int candidateNetworkId = -1;
            int i = this.mBestCandidateType;
            if (i == 1) {
                candidateNetworkId = this.mSavedConfig.networkId;
                ScoredNetworkEvaluator.this.mLocalLog.log(String.format("new saved network candidate %s network ID:%d", new Object[]{WifiNetworkSelector.toScanId(this.mScanResultCandidate), Integer.valueOf(candidateNetworkId)}));
            } else if (i != 2) {
                ScoredNetworkEvaluator.this.mLocalLog.log("ScoredNetworkEvaluator did not see any good candidates.");
            } else {
                WifiConfiguration wifiConfiguration = this.mEphemeralConfig;
                if (wifiConfiguration != null) {
                    candidateNetworkId = wifiConfiguration.networkId;
                    ScoredNetworkEvaluator.this.mLocalLog.log(String.format("existing ephemeral candidate %s network ID:%d, meteredHint=%b", new Object[]{WifiNetworkSelector.toScanId(this.mScanResultCandidate), Integer.valueOf(candidateNetworkId), Boolean.valueOf(this.mEphemeralConfig.meteredHint)}));
                } else {
                    this.mEphemeralConfig = ScanResultUtil.createNetworkFromScanResult(this.mScanResultCandidate);
                    WifiConfiguration wifiConfiguration2 = this.mEphemeralConfig;
                    wifiConfiguration2.ephemeral = true;
                    wifiConfiguration2.trusted = false;
                    wifiConfiguration2.meteredHint = ScoredNetworkEvaluator.this.mScoreCache.getMeteredHint(this.mScanResultCandidate);
                    NetworkUpdateResult result = ScoredNetworkEvaluator.this.mWifiConfigManager.addOrUpdateNetwork(this.mEphemeralConfig, 1010);
                    if (!result.isSuccess()) {
                        ScoredNetworkEvaluator.this.mLocalLog.log("Failed to add ephemeral network");
                    } else if (!ScoredNetworkEvaluator.this.mWifiConfigManager.updateNetworkSelectionStatus(result.getNetworkId(), 0)) {
                        ScoredNetworkEvaluator.this.mLocalLog.log("Failed to make ephemeral network selectable");
                    } else {
                        candidateNetworkId = result.getNetworkId();
                        if (this.mScanDetailCandidate == null) {
                            Log.e(ScoredNetworkEvaluator.TAG, "mScanDetailCandidate is null!");
                        }
                        ScoredNetworkEvaluator.this.mWifiConfigManager.setNetworkCandidateScanResult(candidateNetworkId, this.mScanResultCandidate, 0);
                        ScoredNetworkEvaluator.this.mLocalLog.log(String.format("new ephemeral candidate %s network ID:%d, meteredHint=%b", new Object[]{WifiNetworkSelector.toScanId(this.mScanResultCandidate), Integer.valueOf(candidateNetworkId), Boolean.valueOf(this.mEphemeralConfig.meteredHint)}));
                    }
                }
            }
            WifiConfiguration ans = ScoredNetworkEvaluator.this.mWifiConfigManager.getConfiguredNetwork(candidateNetworkId);
            if (!(ans == null || (scanDetail = this.mScanDetailCandidate) == null)) {
                onConnectableListener.onConnectable(scanDetail, ans, 0);
            }
            return ans;
        }
    }

    /* access modifiers changed from: private */
    public void debugLog(String msg) {
        if (DEBUG) {
            this.mLocalLog.log(msg);
        }
    }

    public int getId() {
        return 4;
    }

    public String getName() {
        return TAG;
    }
}
