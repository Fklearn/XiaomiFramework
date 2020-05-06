package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.provider.Settings;
import b.b.c.h.f;
import com.miui.analytics.AnalyticsUtil;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.DataUsageIgnoreAppListConfig;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.traffic.purchase.CooperationManager;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import java.util.ArrayList;

public class TrackAnalyticsManager {
    private static final int MEGA = 1048576;
    private CommonConfig mCommonConfig;
    private Context mContext;
    private long mDailyLastTrackTime = -1;
    private TrafficSimManager[] mSimManager;
    private long mWeeklyLastTrackTime = -1;

    public TrackAnalyticsManager(Context context, TrafficSimManager[] trafficSimManagerArr) {
        this.mContext = context.getApplicationContext();
        this.mSimManager = trafficSimManagerArr;
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
    }

    private boolean getDataUsageIgnoreEnable(String str) {
        ArrayList<String> ignoreList = DataUsageIgnoreAppListConfig.getInstance(this.mContext, str).getIgnoreList();
        return ignoreList != null && !ignoreList.isEmpty();
    }

    private String getRoamingSettingState() {
        return TelephonyUtil.getDataRoamingEnabled(this.mContext) ? this.mCommonConfig.getDataRoamingWhiteListEnable() ? AnalyticsHelper.TRACK_PARAM_WHITE_LIST_ALLOW : AnalyticsHelper.TRACK_PARAM_ALL_APP_ALLOW : AnalyticsHelper.TRACK_PARAM_ALL_APP_BAN;
    }

    private boolean isPackageEffective(long j) {
        return j > 0 && j < 107374182400L;
    }

    private void trackActiveCardPackageState(int i) {
        SimUserInfo simUserInfo = this.mSimManager[i].mSimUser;
        if (isPackageEffective(simUserInfo.getDataUsageTotal())) {
            AnalyticsHelper.recordCalculateEvent(AnalyticsHelper.TRACK_KEY_PACKAGE_WARNING_VALUE, (long) (simUserInfo.getDataUsageWarning() * 100.0f));
            AnalyticsHelper.recordCalculateEvent(AnalyticsHelper.TRACK_KEY_PACKAGE_START_DATE, (long) simUserInfo.getMonthStart());
            long j = 1;
            if (simUserInfo.isLeisureDataUsageEffective()) {
                long leisureDataUsageTotal = simUserInfo.getLeisureDataUsageTotal();
                AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_LEISURE_PACKAGE_SETTING, leisureDataUsageTotal > 0 ? 1 : 0);
                AnalyticsHelper.recordCalculateEvent(AnalyticsHelper.TRACK_KEY_LEISURE_PACKAGE_SIZE, leisureDataUsageTotal / 1048576);
            }
            long currentMonthExtraPackage = this.mSimManager[i].getCurrentMonthExtraPackage();
            int i2 = (currentMonthExtraPackage > 0 ? 1 : (currentMonthExtraPackage == 0 ? 0 : -1));
            if (i2 <= 0) {
                j = 0;
            }
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_EXTRA_PACKAGE_SETTING, j);
            if (i2 > 0) {
                AnalyticsHelper.recordCalculateEvent(AnalyticsHelper.TRACK_KEY_EXTRA_PACKAGE_SIZE, currentMonthExtraPackage / 1048576);
            }
        }
    }

    private void trackDataUsageState(int i) {
        long currentMonthTotalPackage = this.mSimManager[i].getCurrentMonthTotalPackage();
        SimUserInfo simUserInfo = this.mSimManager[i].mSimUser;
        if (isPackageEffective(currentMonthTotalPackage)) {
            long dataUsageWarning = (long) (((float) currentMonthTotalPackage) * simUserInfo.getDataUsageWarning());
            long j = this.mSimManager[i].getCorrectedNormalAndLeisureMonthTotalUsed()[0];
            AnalyticsHelper.recordStringPropertyEvent(AnalyticsHelper.TRACK_KEY_DAILY_FLOW_USE, j >= currentMonthTotalPackage ? AnalyticsHelper.TRACK_KEY_PACKAGE_STATUS_EXCEED : j >= dataUsageWarning ? AnalyticsHelper.TRACK_KEY_PACKAGE_STATUS_REACH : AnalyticsHelper.TRACK_KEY_PACKAGE_STATUS_ENOUGH);
        }
    }

    private void trackDualCardPackageState() {
        long j;
        int i = 0;
        long j2 = 1;
        if (DeviceUtil.IS_DUAL_CARD) {
            SimUserInfo simUserInfo = this.mSimManager[0].mSimUser;
            if (simUserInfo == null || !simUserInfo.isSimInserted()) {
                j = 0;
            } else {
                long dataUsageTotal = simUserInfo.getDataUsageTotal();
                j = dataUsageTotal + 0;
                AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_DOUBLE_SIM1_ENABLE, dataUsageTotal > 0 ? 1 : 0);
                trackPackageSize(AnalyticsHelper.TRACK_KEY_DOUBLE_SIM1_VALUE, dataUsageTotal);
                i = 1;
            }
            SimUserInfo simUserInfo2 = this.mSimManager[1].mSimUser;
            if (simUserInfo2 != null && simUserInfo2.isSimInserted()) {
                i++;
                long dataUsageTotal2 = simUserInfo2.getDataUsageTotal();
                j += dataUsageTotal2;
                if (dataUsageTotal2 <= 0) {
                    j2 = 0;
                }
                AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_DOUBLE_SIM2_ENABLE, j2);
                trackPackageSize(AnalyticsHelper.TRACK_KEY_DOUBLE_SIM2_VALUE, dataUsageTotal2);
            }
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_DOUBLE_SIM, (long) i);
            trackPackageSize(AnalyticsHelper.TRACK_KEY_DOUBLE_SIM_TOTAL, j);
            return;
        }
        SimUserInfo simUserInfo3 = this.mSimManager[0].mSimUser;
        AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_SINGLE_SIM, simUserInfo3.isSimInserted() ? 1 : 0);
        if (simUserInfo3.isSimInserted()) {
            long dataUsageTotal3 = simUserInfo3.getDataUsageTotal();
            if (dataUsageTotal3 <= 0) {
                j2 = 0;
            }
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_SINGLE_SIM_ENABLE, j2);
            trackPackageSize(AnalyticsHelper.TRACK_KEY_SINGLE_SIM_VALUE, dataUsageTotal3);
        }
    }

    private void trackPackageSize(String str, long j) {
        if (isPackageEffective(j)) {
            AnalyticsHelper.recordCalculateEvent(str, j / 1048576);
        }
    }

    private void trackSettingButtonState(int i) {
        SimUserInfo simUserInfo = this.mSimManager[i].mSimUser;
        long j = 1;
        if (simUserInfo.isSimInserted()) {
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_DATA_USAGE_APP_IGNORE, getDataUsageIgnoreEnable(simUserInfo.getImsi()) ? 1 : 0);
            AnalyticsHelper.recordStringPropertyEvent(AnalyticsHelper.TRACK_KEY_DATA_ROAMING_SETTING, getRoamingSettingState());
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_DATA_ROAMING_DAILY_LIMIT_SETTING, simUserInfo.getRoamingDailyLimitEnabled() ? 1 : 0);
        }
        if (simUserInfo.isCorrectionEffective()) {
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_AUTO_CORRECTION, simUserInfo.isDataUsageAutoCorrectionEffective() ? 1 : 0);
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_AUTO_MODIFY_PACKAGE, simUserInfo.isTrafficCorrectionAutoModify() ? 1 : 0);
        }
        if (this.mSimManager[i].getCurrentMonthTotalPackage() > 0) {
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_DAILY_LIMIT, simUserInfo.getDailyLimitEnabled() ? 1 : 0);
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_LOCK_SCREEN_SETTING, simUserInfo.isLockScreenTrafficEnable() ? 1 : 0);
            AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_TRAFFIC_OVER_LIMIT, simUserInfo.isDataUsageOverLimitStopNetwork() ? 1 : 0);
        }
        AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_SHOW_TRAFFIC_STATUS_BAR, (long) Settings.System.getInt(this.mContext.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, 0));
        AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_SHOW_SPEED_STATUS_BAR, (long) Settings.System.getInt(this.mContext.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, 0));
        AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_NEW_INSTALL_APP_FIREWALL_MOBILE, FirewallRule.Allow.value() == this.mCommonConfig.getFirewallMobilePreConfig() ? 1 : 0);
        AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_NEW_INSTALL_APP_FIREWALL_WIFI, FirewallRule.Allow.value() == this.mCommonConfig.getFirewallWifiPreConfig() ? 1 : 0);
        AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_TRAFFIC_PURCHASE_SHOW, CooperationManager.isTrafficPurchaseAvailable(this.mContext, simUserInfo, false) ? 1 : 0);
        if (!simUserInfo.isDailyUsedCardEffective()) {
            j = 0;
        }
        AnalyticsHelper.recordNumericEvent(AnalyticsHelper.TRACK_KEY_DAILY_CARD, j);
    }

    /* access modifiers changed from: package-private */
    public void trackAnalyticDaily(int i) {
        if (this.mDailyLastTrackTime == -1) {
            this.mDailyLastTrackTime = this.mCommonConfig.getDataUsageDailyAnalyticsUpdateTime();
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > this.mDailyLastTrackTime + 86400000) {
            this.mDailyLastTrackTime = currentTimeMillis;
            trackDataUsageState(i);
            this.mCommonConfig.setDataUsageDailyAnalyticUpdateTime(currentTimeMillis);
        }
    }

    /* access modifiers changed from: package-private */
    public void trackAnalyticsWeekly(int i) {
        if (this.mWeeklyLastTrackTime == -1) {
            this.mWeeklyLastTrackTime = this.mCommonConfig.getCommonAnalyticsUpdateTime();
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > this.mWeeklyLastTrackTime + 604800000 && f.l(this.mContext)) {
            this.mWeeklyLastTrackTime = currentTimeMillis;
            trackActiveCardPackageState(i);
            trackSettingButtonState(i);
            trackDualCardPackageState();
            AnalyticsUtil.triggerUpload();
            this.mCommonConfig.setCommonAnalyticUpdateTime(currentTimeMillis);
        }
    }
}
