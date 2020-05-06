package com.android.server.wifi;

import android.content.Context;

public class WifiDataStall {
    public static final long MAX_MS_DELTA_FOR_DATA_STALL = 60000;
    public static final int MIN_TX_BAD_DEFAULT = 1;
    public static final int MIN_TX_SUCCESS_WITHOUT_RX_DEFAULT = 50;
    private final Context mContext;
    private final FrameworkFacade mFacade;
    private int mMinTxBad;
    private int mMinTxSuccessWithoutRx;
    private final WifiMetrics mWifiMetrics;

    public WifiDataStall(Context context, FrameworkFacade facade, WifiMetrics wifiMetrics) {
        this.mContext = context;
        this.mFacade = facade;
        this.mWifiMetrics = wifiMetrics;
        loadSettings();
    }

    public void loadSettings() {
        this.mMinTxBad = this.mFacade.getIntegerSetting(this.mContext, "wifi_data_stall_min_tx_bad", 1);
        this.mMinTxSuccessWithoutRx = this.mFacade.getIntegerSetting(this.mContext, "wifi_data_stall_min_tx_success_without_rx", 50);
        this.mWifiMetrics.setWifiDataStallMinTxBad(this.mMinTxBad);
        this.mWifiMetrics.setWifiDataStallMinRxWithoutTx(this.mMinTxSuccessWithoutRx);
    }

    public int checkForDataStall(WifiLinkLayerStats oldStats, WifiLinkLayerStats newStats) {
        WifiLinkLayerStats wifiLinkLayerStats = oldStats;
        WifiLinkLayerStats wifiLinkLayerStats2 = newStats;
        if (wifiLinkLayerStats == null || wifiLinkLayerStats2 == null) {
            this.mWifiMetrics.resetWifiIsUnusableLinkLayerStats();
            return 0;
        }
        long txSuccessDelta = (((wifiLinkLayerStats2.txmpdu_be + wifiLinkLayerStats2.txmpdu_bk) + wifiLinkLayerStats2.txmpdu_vi) + wifiLinkLayerStats2.txmpdu_vo) - (((wifiLinkLayerStats.txmpdu_be + wifiLinkLayerStats.txmpdu_bk) + wifiLinkLayerStats.txmpdu_vi) + wifiLinkLayerStats.txmpdu_vo);
        long txRetriesDelta = (((wifiLinkLayerStats2.retries_be + wifiLinkLayerStats2.retries_bk) + wifiLinkLayerStats2.retries_vi) + wifiLinkLayerStats2.retries_vo) - (((wifiLinkLayerStats.retries_be + wifiLinkLayerStats.retries_bk) + wifiLinkLayerStats.retries_vi) + wifiLinkLayerStats.retries_vo);
        long txBadDelta = (((wifiLinkLayerStats2.lostmpdu_be + wifiLinkLayerStats2.lostmpdu_bk) + wifiLinkLayerStats2.lostmpdu_vi) + wifiLinkLayerStats2.lostmpdu_vo) - (((wifiLinkLayerStats.lostmpdu_be + wifiLinkLayerStats.lostmpdu_bk) + wifiLinkLayerStats.lostmpdu_vi) + wifiLinkLayerStats.lostmpdu_vo);
        long rxSuccessDelta = (((wifiLinkLayerStats2.rxmpdu_be + wifiLinkLayerStats2.rxmpdu_bk) + wifiLinkLayerStats2.rxmpdu_vi) + wifiLinkLayerStats2.rxmpdu_vo) - (((wifiLinkLayerStats.rxmpdu_be + wifiLinkLayerStats.rxmpdu_bk) + wifiLinkLayerStats.rxmpdu_vi) + wifiLinkLayerStats.rxmpdu_vo);
        long timeMsDelta = wifiLinkLayerStats2.timeStampInMs - wifiLinkLayerStats.timeStampInMs;
        if (timeMsDelta < 0 || txSuccessDelta < 0 || txRetriesDelta < 0 || txBadDelta < 0 || rxSuccessDelta < 0) {
            this.mWifiMetrics.resetWifiIsUnusableLinkLayerStats();
            return 0;
        }
        this.mWifiMetrics.updateWifiIsUnusableLinkLayerStats(txSuccessDelta, txRetriesDelta, txBadDelta, rxSuccessDelta, timeMsDelta);
        if (timeMsDelta < 60000) {
            boolean dataStallBadTx = txBadDelta >= ((long) this.mMinTxBad);
            boolean dataStallTxSuccessWithoutRx = rxSuccessDelta == 0 && txSuccessDelta >= ((long) this.mMinTxSuccessWithoutRx);
            if (dataStallBadTx && dataStallTxSuccessWithoutRx) {
                this.mWifiMetrics.logWifiIsUnusableEvent(3);
                return 3;
            } else if (dataStallBadTx) {
                this.mWifiMetrics.logWifiIsUnusableEvent(1);
                return 1;
            } else if (dataStallTxSuccessWithoutRx) {
                this.mWifiMetrics.logWifiIsUnusableEvent(2);
                return 2;
            }
        }
        return 0;
    }
}
