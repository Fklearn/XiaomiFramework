package com.miui.networkassistant.config;

import android.content.Context;
import android.text.TextUtils;
import com.miui.networkassistant.config.CommonPerConstants;

public class CommonConfig {
    private static final String COMMON_CONFIG_FILE_NAME = "common";
    private static CommonConfig sInstance;
    private Context mContext;
    private SharedPreferenceHelper mSpHelper = SharedPreferenceHelper.getInstance(this.mContext, COMMON_CONFIG_FILE_NAME);

    private CommonConfig(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static synchronized CommonConfig getInstance(Context context) {
        CommonConfig commonConfig;
        synchronized (CommonConfig.class) {
            if (sInstance == null) {
                sInstance = new CommonConfig(context);
            }
            commonConfig = sInstance;
        }
        return commonConfig;
    }

    public long getCommonAnalyticsUpdateTime() {
        return this.mSpHelper.load(CommonPerConstants.KEY.COMMON_ANALYTICS_UPDATE_TIME, 0);
    }

    public boolean getDataRoamingWhiteListEnable() {
        return this.mSpHelper.load(CommonPerConstants.KEY.FIREWALL_ROAMING_WHITELIST_PRECONFIG, false);
    }

    public long getDataUsageDailyAnalyticsUpdateTime() {
        return this.mSpHelper.load(CommonPerConstants.KEY.DATA_USAGE_DAILY_ANALYTICS_UPDATE_TIME, Long.MAX_VALUE);
    }

    public int getFirewallMobilePreConfig() {
        return this.mSpHelper.load("firewall_mobile_preconfig", CommonPerConstants.DEFAULT.FIREWALL_MOBILE_PRECONFIG_DEFAULT);
    }

    public int getFirewallWifiPreConfig() {
        return this.mSpHelper.load(CommonPerConstants.KEY.FIREWALL_WIFI_PRECONFIG, CommonPerConstants.DEFAULT.FIREWALL_WIFI_PRECONFIG_DEFAULT);
    }

    public boolean getFirstEnterTrafficPurchaseDeclare() {
        return this.mSpHelper.load(CommonPerConstants.KEY.FIRST_ENTER_TRAFFIC_PURCHASE_DECLARE, true);
    }

    public int getLockScreenTrafficGuideNotifyCount() {
        return this.mSpHelper.load(CommonPerConstants.KEY.LOCK_SCREEN_TRAFFIC_GUIDE_NOTIFY_COUNT, 0);
    }

    public String getMiSimCloudData() {
        return this.mSpHelper.load(CommonPerConstants.KEY.MI_SIM_CLOUD_DATA, "");
    }

    public String getMiuiVpnInfos() {
        return this.mSpHelper.load(CommonPerConstants.KEY.MIUI_VPN_INFOS, "");
    }

    public long getMobileDailyConnectedTime() {
        return this.mSpHelper.load(CommonPerConstants.KEY.MOBILE_DAILY_TURN_ON_TIME, 0);
    }

    public long getNetworkExceptionUpdateTime() {
        return this.mSpHelper.load(CommonPerConstants.KEY.NETWORK_EXCEPTION_UPDATE_TIME, 0);
    }

    public String getPurchaseSmsNumber() {
        return this.mSpHelper.load(CommonPerConstants.KEY.PURCHASE_SMS_NUMBER, CommonPerConstants.DEFAULT.PURCHASE_SMS_NUMBER_DEFAULT);
    }

    public long getPurchaseSmsNumberUpdateTime() {
        return this.mSpHelper.load(CommonPerConstants.KEY.PURCHASE_SMS_NUMBER_UPDATE_TIME, 0);
    }

    public long getSmsNumberReceiverUpdateTime() {
        return this.mSpHelper.load(CommonPerConstants.KEY.SMS_NUMBER_RECEIVER_UPDATE_TIME, 0);
    }

    public boolean getTetheringDataUsageOverLimit() {
        return this.mSpHelper.load(CommonPerConstants.KEY.TETHERING_DATA_USAGE_OVER_LIMIT, false);
    }

    public boolean getTetheringLimitEnabled() {
        return this.mSpHelper.load("tethering_limit_enabled", false);
    }

    public long getTetheringLimitTraffic() {
        return this.mSpHelper.load(CommonPerConstants.KEY.TETHERING_LIMIT_TRAFFIC, (long) CommonPerConstants.DEFAULT.TETHERING_LIMIT_TRAFFIC_DEFAULT);
    }

    public int getTetheringOverLimitOptType() {
        return this.mSpHelper.load(CommonPerConstants.KEY.TETHERING_OVER_LIMIT_OPT_TYPE, 0);
    }

    public long getUploadMonthReportUpdateTime() {
        return this.mSpHelper.load(CommonPerConstants.KEY.UPLOAD_MONTH_REPORT_UPDATE_TIME, 0);
    }

    public int getVpnState(String str, String str2) {
        if (!TextUtils.equals(getVpnUserId(str), str2)) {
            setVpnUserId(str, str2);
            setVpnState(str, str2, 0);
        }
        SharedPreferenceHelper sharedPreferenceHelper = this.mSpHelper;
        return sharedPreferenceHelper.load("miui_vpn_state_" + str, 0);
    }

    public String getVpnUserId(String str) {
        SharedPreferenceHelper sharedPreferenceHelper = this.mSpHelper;
        return sharedPreferenceHelper.load("miui_vpn_userid_" + str, (String) null);
    }

    public long getWifiDailyConnectedTime() {
        return this.mSpHelper.load(CommonPerConstants.KEY.WIFI_DAILY_TURN_ON_TIME, 0);
    }

    public boolean isCmccWebCorrectAvailable() {
        return this.mSpHelper.load(CommonPerConstants.KEY.CMCC_WEB_CORRECT_AVAILABLE, true);
    }

    public boolean isFloatNotificationEnabled() {
        return this.mSpHelper.load(CommonPerConstants.KEY.FLOAT_NOTIFICATION_ENABLED, false);
    }

    public boolean isLockScreenTrafficMonitorEnable() {
        return this.mSpHelper.load(CommonPerConstants.KEY.LOCK_SCREEN_TRAFFIC_MONITOR, false);
    }

    public boolean isLockScreenTrafficOpened() {
        return this.mSpHelper.load(CommonPerConstants.KEY.LOCK_SCREEN_TRAFFIC_OPENED, false);
    }

    public boolean isNetworkDiagnosticsFloatNotificationEnabled() {
        return this.mSpHelper.load(CommonPerConstants.KEY.NETWORK_DIAGNOSTICS_FLOAT_NOTIFICATION_ENABLED, true);
    }

    public boolean isNoMoreAskRoaming() {
        return this.mSpHelper.load(CommonPerConstants.KEY.NO_MORE_ASK_ROAMING, false);
    }

    public boolean isRoamingAppWhiteListDefault() {
        return this.mSpHelper.load(CommonPerConstants.KEY.ROAMING_APP_WHITE_LIST_INIT, true);
    }

    public boolean isRoamingWhiteListNotifyEnable() {
        return this.mSpHelper.load(CommonPerConstants.KEY.ROAMING_WHITE_LIST_NOTIFY_ENABLE, true);
    }

    public boolean isStatusBarShowTrafficUpdate() {
        return this.mSpHelper.load(CommonPerConstants.KEY.STATUS_BAR_SHOW_TRAFFIC, false);
    }

    public boolean setCmccWebCorrectAvailable(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.CMCC_WEB_CORRECT_AVAILABLE, z);
    }

    public boolean setCommonAnalyticUpdateTime(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.COMMON_ANALYTICS_UPDATE_TIME, j);
    }

    public boolean setDataRoamingWhiteListEnable(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.FIREWALL_ROAMING_WHITELIST_PRECONFIG, z);
    }

    public boolean setDataUsageDailyAnalyticUpdateTime(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.DATA_USAGE_DAILY_ANALYTICS_UPDATE_TIME, j);
    }

    public boolean setFirewallWifiPreConfig(int i) {
        return this.mSpHelper.save(CommonPerConstants.KEY.FIREWALL_WIFI_PRECONFIG, i);
    }

    public boolean setFirstEnterTrafficPurchaseDeclare(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.FIRST_ENTER_TRAFFIC_PURCHASE_DECLARE, z);
    }

    public boolean setFloatNotificationEnabled(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.FLOAT_NOTIFICATION_ENABLED, z);
    }

    public boolean setLockScreenTrafficGuideNotifyCount(int i) {
        return this.mSpHelper.save(CommonPerConstants.KEY.LOCK_SCREEN_TRAFFIC_GUIDE_NOTIFY_COUNT, i);
    }

    public boolean setLockScreenTrafficMonitorEnable(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.LOCK_SCREEN_TRAFFIC_MONITOR, z);
    }

    public boolean setLockScreenTrafficOpened(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.LOCK_SCREEN_TRAFFIC_OPENED, z);
    }

    public boolean setMiSimCloudData(String str) {
        return this.mSpHelper.save(CommonPerConstants.KEY.MI_SIM_CLOUD_DATA, str);
    }

    public boolean setMiuiVpnInfos(String str) {
        return this.mSpHelper.save(CommonPerConstants.KEY.MIUI_VPN_INFOS, str);
    }

    public boolean setMobileDailyConnectedTime(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.MOBILE_DAILY_TURN_ON_TIME, j);
    }

    public boolean setNetworkDiagnosticsFloatNotificationEnabled(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.NETWORK_DIAGNOSTICS_FLOAT_NOTIFICATION_ENABLED, z);
    }

    public boolean setNetworkExceptionUpdateTime(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.NETWORK_EXCEPTION_UPDATE_TIME, j);
    }

    public boolean setNoMoreAskRoaming(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.NO_MORE_ASK_ROAMING, z);
    }

    public boolean setPurchaseSmsNumber(String str) {
        return this.mSpHelper.save(CommonPerConstants.KEY.PURCHASE_SMS_NUMBER, str);
    }

    public boolean setPurchaseSmsNumberUpdateTime(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.PURCHASE_SMS_NUMBER_UPDATE_TIME, j);
    }

    public boolean setRoamingAppWhiteListDefault(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.ROAMING_APP_WHITE_LIST_INIT, z);
    }

    public boolean setRoamingWhiteListNotifyEnable(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.ROAMING_WHITE_LIST_NOTIFY_ENABLE, z);
    }

    public boolean setSmsNumberReceiverUpdateTime(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.SMS_NUMBER_RECEIVER_UPDATE_TIME, j);
    }

    public boolean setStatusBarShowTrafficUpdate(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.STATUS_BAR_SHOW_TRAFFIC, z);
    }

    public boolean setTetheringDataUsageOverLimit(boolean z) {
        return this.mSpHelper.save(CommonPerConstants.KEY.TETHERING_DATA_USAGE_OVER_LIMIT, z);
    }

    public boolean setTetheringLimitEnabled(boolean z) {
        return this.mSpHelper.save("tethering_limit_enabled", z);
    }

    public boolean setTetheringLimitTraffic(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.TETHERING_LIMIT_TRAFFIC, j);
    }

    public boolean setTetheringOverLimitOptType(int i) {
        return this.mSpHelper.save(CommonPerConstants.KEY.TETHERING_OVER_LIMIT_OPT_TYPE, i);
    }

    public boolean setUploadMonthReportUpdateTime(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.UPLOAD_MONTH_REPORT_UPDATE_TIME, j);
    }

    public boolean setVpnState(String str, String str2, int i) {
        if (!TextUtils.equals(getVpnUserId(str), str2)) {
            setVpnUserId(str, str2);
        }
        SharedPreferenceHelper sharedPreferenceHelper = this.mSpHelper;
        return sharedPreferenceHelper.save("miui_vpn_state_" + str, i);
    }

    public boolean setVpnUserId(String str, String str2) {
        SharedPreferenceHelper sharedPreferenceHelper = this.mSpHelper;
        return sharedPreferenceHelper.save("miui_vpn_userid_" + str, str2);
    }

    public boolean setWifiDailyConnectedTime(long j) {
        return this.mSpHelper.save(CommonPerConstants.KEY.WIFI_DAILY_TURN_ON_TIME, j);
    }
}
