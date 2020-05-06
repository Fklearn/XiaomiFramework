package com.miui.networkassistant.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.networkassistant.config.SimUserConstants;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.MiSimUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import java.io.File;
import java.util.HashMap;
import miui.provider.ExtraSettings;

public class SimUserInfo {
    public static final String DEFAULT_NULL_IMSI = "default";
    private static final String SP_FILE_PATH = "/data/data/com.miui.securitycenter/shared_prefs/";
    private static final String SP_SUFFIX = ".xml";
    private static final String TAG = "SimUserInfo";
    private static HashMap<String, SimUserInfo> sInstanceMap;
    private Context mContext;
    private String mIccid;
    private String mImsi;
    private boolean mIsOversea;
    private boolean mIsSimInserted;
    private String mPhoneNumber;
    private long mSimId;
    private String mSimName;
    private int mSlotNum;
    private SharedPreferenceHelper mSpHelper;

    private SimUserInfo(Context context, String str, int i) {
        Log.i(TAG, "mina create SimUserInfo");
        initData(context, str, i);
    }

    public static SimUserInfo getInstance(Context context, int i) {
        return getInstance(context, SimCardHelper.getInstance(context).getSimImsi(i), i);
    }

    public static SimUserInfo getInstance(Context context, String str) {
        return getInstance(context, str, SimCardHelper.getInstance(context).getSlotNumByImsi(str));
    }

    public static synchronized SimUserInfo getInstance(Context context, String str, int i) {
        SimUserInfo simUserInfo;
        synchronized (SimUserInfo.class) {
            if (sInstanceMap == null) {
                sInstanceMap = new HashMap<>();
            }
            simUserInfo = sInstanceMap.get(str);
            if (simUserInfo == null) {
                simUserInfo = new SimUserInfo(context, str, i);
                sInstanceMap.put(str, simUserInfo);
            } else {
                simUserInfo.initData(context, str, i);
            }
        }
        return simUserInfo;
    }

    private void initData(Context context, String str, int i) {
        this.mContext = context.getApplicationContext();
        this.mImsi = str;
        this.mSlotNum = i;
        String md5 = DeviceUtil.getMd5(str);
        renameSimInfoFile(str, md5);
        this.mSpHelper = SharedPreferenceHelper.getInstance(this.mContext, md5);
        this.mIsOversea = !TelephonyUtil.isChinaOperator(this.mSlotNum) || DeviceUtil.IS_INTERNATIONAL_BUILD;
    }

    private boolean renameSimInfoFile(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        File file = new File(SP_FILE_PATH + str + SP_SUFFIX);
        if (!file.exists()) {
            return false;
        }
        File file2 = new File(SP_FILE_PATH + str2 + SP_SUFFIX);
        return !file2.exists() && file.renameTo(file2);
    }

    public long getBillPackageRemained() {
        return MiSimUtil.isMiSimEnable(this.mContext, this.mSlotNum) ? ExtraSettings.System.getLong(this.mContext.getContentResolver(), "mm_account_balance", 0) : this.mSpHelper.load(SimUserConstants.KEY.BILL_PACKAGE_REMAINED, Long.MIN_VALUE);
    }

    public long getBillPackageTotal() {
        return this.mSpHelper.load(SimUserConstants.KEY.BILL_PACKAGE_TOTAL, -1);
    }

    public String getBillSmsDetail() {
        return this.mSpHelper.load(SimUserConstants.KEY.BILL_SMS_DETAIL, "");
    }

    public String getBillTcResult() {
        return this.mSpHelper.load(SimUserConstants.KEY.BILL_TC_RESULT, "");
    }

    public int getBillTcResultCode() {
        return this.mSpHelper.load(SimUserConstants.KEY.BILL_TC_RESULT_CODE, 0);
    }

    public int getBrand() {
        if (DeviceUtil.IS_INTERNATIONAL_BUILD) {
            return 0;
        }
        return this.mSpHelper.load(SimUserConstants.KEY.USER_BRAND, -1);
    }

    public long getCallTimePackageRemained() {
        return this.mSpHelper.load(SimUserConstants.KEY.CALL_TIME_PACKAGE_REMAINED, -1);
    }

    public long getCallTimePackageTotal() {
        return this.mSpHelper.load(SimUserConstants.KEY.CALL_TIME_PACKAGE_TOTAL, -1);
    }

    public int getCity() {
        return this.mSpHelper.load(SimUserConstants.KEY.USER_CITY, -1);
    }

    public long getCorrectedOffsetValue() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_CORRECTED_VALUE, 0);
    }

    public long getCorrectionSourceUpdateTime(String str) {
        return this.mSpHelper.load(str, 0);
    }

    public long getCustomizeDailyLimitWarning() {
        return this.mSpHelper.load(SimUserConstants.KEY.CUSTOMIZE_DAILY_LIMIT_WARNING, 0);
    }

    public String getCustomizedSmsContent() {
        return this.mSpHelper.load(SimUserConstants.KEY.USER_CUSTOMIZED_SMS_CONTENT, "");
    }

    public String getCustomizedSmsNum() {
        return this.mSpHelper.load(SimUserConstants.KEY.USER_CUSTOMIZED_SMS_NUM, "");
    }

    public boolean getDailyLimitEnabled() {
        return isTrafficManageControlEnable() && this.mSpHelper.load(SimUserConstants.KEY.DAILY_LIMIT_AVAILABLE, false);
    }

    public int getDailyLimitWarningType() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_LIMIT_WARNING_TYPE, 1);
    }

    public String getDailyUsedCardBrand() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_USED_CARD_BRAND, DailyCardBrandConfig.getInstance(this.mContext).getBrandNameList().get(0));
    }

    public long getDailyUsedCardDataUpdateTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_CARD_DATA_UPDATE_TIME, 0);
    }

    public long getDailyUsedCardPackage() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_USED_CARD_PACKAGE, 0);
    }

    public int getDailyUsedCardStopNetworkCount() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_CARD_STOP_NETWORK_COUNT, 0);
    }

    public boolean getDailyUsedCardStopNetworkOn() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_CARD_STOP_NETWORK_TYPE, false);
    }

    public long getDailyUsedCardStopNetworkTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_CARD_STOP_NETWORK_TIME, 0);
    }

    public boolean getDataRoamingStopChanged() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_ROAMING_STOP_CHANGED, false);
    }

    public long getDataRoamingStopUpdateTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_ROAMING_STOP_UPDATE_TIME, 0);
    }

    public long getDataUsageAutoCorrectedTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_AUTO_CORRECTED_TIME, 0);
    }

    public long getDataUsageCorrectedTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_CORRECTED_TIME, 0);
    }

    public long getDataUsageOverDailyLimitTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_OVER_DAILY_LIMIT_TIME, 0);
    }

    public long getDataUsageOverLimitStopNetworkTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_OVERLIMIT_STOP_NETWORK_TIME, 0);
    }

    public long getDataUsageOverLimitStopNetworkWarningTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_OVERLIMIT_STOP_NETWORK_WARNING_TIME, 0);
    }

    public long getDataUsageOverRoamingDailyLimitTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_OVER_ROAMING_DAILY_LIMIT_TIME, 0);
    }

    public long getDataUsageOverlayPackage() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_OVERLAY_PACKAGE, 0);
    }

    public long getDataUsageOverlayPackageTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_OVERLAY_PACKAGE_TIME, 0);
    }

    public long getDataUsageTotal() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_TOTAL, -1);
    }

    public float getDataUsageWarning() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_WARNING, 0.8f);
    }

    public int getFirewallMobilePreConfig() {
        return this.mSpHelper.load("firewall_mobile_preconfig", SimUserConstants.DEFAULT.FIREWALL_MOBILE_PRECONFIG_DEFAULT);
    }

    public long getHalfYearPackageBeginTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.HALF_YEAR_PACKAGE_BEGIN_TIME, 0);
    }

    public long getHalfYearPackageValue() {
        return this.mSpHelper.load(SimUserConstants.KEY.HALF_YEAR_PACKAGE_VALUE, 0);
    }

    public String getIccid() {
        return this.mIccid;
    }

    public String getImsi() {
        return this.mImsi;
    }

    public long getLastBillNotifyTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.LAST_BILL_NOTIFY_TIME, 0);
    }

    public String getLastBillTcDirection() {
        return this.mSpHelper.load(SimUserConstants.KEY.LAST_BILL_TC_DIRECTION, (String) null);
    }

    public long getLastTcRemain() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_LAST_TC_REMAIN, 0);
    }

    public long getLastTcUsed() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_LAST_TC_USED, 0);
    }

    public String getLastTrafficTcDirection() {
        return this.mSpHelper.load(SimUserConstants.KEY.LAST_TRAFFIC_TC_DIRECTION, (String) null);
    }

    public long getLeisureDataUsageCorrectedTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_DATA_USAGE_CORRECTED_TIME, 0);
    }

    public long getLeisureDataUsageCorrectedValue() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_DATA_USAGE_CORRECTED_VALUE, 0);
    }

    public long getLeisureDataUsageFromTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_DATA_USAGE_FROM_TIME, (long) SimUserConstants.DEFAULT.LEISURE_DATA_USAGE_FROM_TIME_DEFAULT);
    }

    public long getLeisureDataUsageOverLimitWarningTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_DATA_USAGE_OVERLIMIT_WARNING_TIME, 0);
    }

    public long getLeisureDataUsageToTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_DATA_USAGE_TO_TIME, 25200000);
    }

    public long getLeisureDataUsageTotal() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_DATA_USAGE_TOTAL, 0);
    }

    public long getLeisureOverLimitStopNetworkTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_OVERLIMIT_STOP_NETWORK_TIME, 0);
    }

    public int getLockScreenWarningLevel() {
        return this.mSpHelper.load(SimUserConstants.KEY.LOCK_SCREEN_WARNING_LEVEL, -1);
    }

    public int getMonthStart() {
        return this.mSpHelper.load(SimUserConstants.KEY.MONTH_START, 1);
    }

    public long getNATrafficPurchaseAvailableUpdateTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_PURCHASE_NA_AVALIABLE_UPDATE_TIME, 0);
    }

    public String getNATrafficPurchaseOrderTips() {
        return this.mSpHelper.load(SimUserConstants.KEY.NA_TRAFFIC_PURCHASE_ORDER_TIPS, "");
    }

    public long getNotLimitedCardPackage() {
        return this.mSpHelper.load(SimUserConstants.KEY.NOT_LIMITED_CARD_PACKAGE, (long) SimUserConstants.DEFAULT.NOT_LIMITED_OVERLIMIT_DEFAULT);
    }

    public long getNotLimitedDataUsageOverLimitStopNetworkTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.NOT_LIMITED_OVERLIMIT_WARNING_TIME, 0);
    }

    public String getOperator() {
        return this.mSpHelper.load(SimUserConstants.KEY.USER_OPERATOR, "");
    }

    public int getOverDataUsageStopNetworkType() {
        return this.mSpHelper.load(SimUserConstants.KEY.OVER_DATA_USAGE_STOP_NETWORK_TYPE, 0);
    }

    public long getPackageChangeUpdateTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.PACKAGE_CHANGE_UPDATE_TIME, 0);
    }

    public String getPhoneNumber() {
        return this.mPhoneNumber;
    }

    public int getProvince() {
        return this.mSpHelper.load(SimUserConstants.KEY.USER_PROVINCE, -1);
    }

    public String getPurchaseActivityId() {
        return this.mSpHelper.load(SimUserConstants.KEY.PURCHASE_ACTIVITY_ID, SimUserConstants.DEFAULT.PURCHASE_ACTIVITY_ID_DEFAULT);
    }

    public long getRoamingBeginTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.ROAMING_BEGIN_TIME, 0);
    }

    public boolean getRoamingDailyLimitEnabled() {
        return this.mSpHelper.load(SimUserConstants.KEY.ROAMING_DAILY_LIMIT_ENABLED, false);
    }

    public long getRoamingDailyLimitTraffic() {
        return this.mSpHelper.load(SimUserConstants.KEY.ROAMING_DAILY_LIMIT_TRAFFIC, 0);
    }

    public boolean getRoamingNetworkState() {
        return this.mSpHelper.load(SimUserConstants.KEY.ROAMING_NETWORK_STATE, false);
    }

    public int getRoamingOverLimitOptType() {
        return this.mSpHelper.load(SimUserConstants.KEY.ROAMING_OVER_LIMIT_OPT_TYPE, 1);
    }

    public long getShouldWebCorrection() {
        return this.mSpHelper.load(SimUserConstants.KEY.WEB_SHOULD_CORRECTION, 0);
    }

    public long getSimId() {
        return this.mSimId;
    }

    public String getSimName() {
        return this.mSimName;
    }

    public int getSlotNum() {
        return this.mSlotNum;
    }

    public String getTcSmsReportCache() {
        return this.mSpHelper.load(SimUserConstants.KEY.TC_SMS_REPORT_CACHE, "");
    }

    public long getTrafficCorrectionEngineUpdateTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_CORRECTION_ENGINE_UPDATE_TIME, 0);
    }

    public int getTrafficLimitValue() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_LIMIT_VALUE, 3);
    }

    public long getTrafficProtectedStopNetTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_PROTECTED_STOP_NET_TIME, 0);
    }

    public boolean getTrafficPurchaseStatus() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_PURCHASE_STATUS, false);
    }

    public long getTrafficSettingDailyLimitNotifyUpdateTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_SETTING_DAILY_LIMIT_NOTIFY_UPDATE_TIME, 0);
    }

    public boolean getTrafficSettingDailyNotifyUpdateTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_SETTING_DAILY_NOTIFY_UPDATE_TIME, true);
    }

    public long getTrafficSettingMonthlyNotifyUpdateTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_SETTING_MONTHLY_NOTIFY_UPDATE_TIME, 0);
    }

    public String getTrafficSmsDetail() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_SMS_DETAIL, "");
    }

    public String getTrafficTcResult() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_TC_RESULT, "");
    }

    public int getTrafficTcResultCode() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_TC_RESULT_CODE, 0);
    }

    public long getWebCorrectionStatusRefreshTime() {
        return this.mSpHelper.load(SimUserConstants.KEY.WEB_CORRECTION_STATUS_REFRESH_TIME, 0);
    }

    public boolean hasImsi() {
        String str = this.mImsi;
        return str != "default" && !TextUtils.isEmpty(str);
    }

    public boolean isBillPackageEffective() {
        return getBillPackageRemained() != Long.MIN_VALUE;
    }

    public boolean isBrandSetted() {
        return getBrand() != -1;
    }

    public boolean isCorrectionEffective() {
        return isSimInserted() && isOperatorSetted() && isTrafficManageControlEnable() && !isDataRoaming();
    }

    public boolean isCustomizedSms() {
        return this.mSpHelper.load(SimUserConstants.KEY.USER_CUSTOMIZED_SMS, false);
    }

    public boolean isDailyCardSettingGuideEnable() {
        return this.mSpHelper.load(SimUserConstants.KEY.DAILY_CARD_SETTING_GUIDE_ENABLE, true);
    }

    public boolean isDailyUsedCardEffective() {
        return isDailyUsedCardEnable() && getDailyUsedCardPackage() > 0;
    }

    public boolean isDailyUsedCardEnable() {
        return getBrand() == 1;
    }

    public boolean isDataRoaming() {
        return TelephonyUtil.isNetworkRoaming(this.mContext, this.mSlotNum);
    }

    public boolean isDataUsageAutoCorrectionEffective() {
        return isDataUsageAutoCorrectionOn() && isSupportCorrection();
    }

    public boolean isDataUsageAutoCorrectionOn() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_AUTO_CORRECTION_ON, true);
    }

    public boolean isDataUsageOverLimitStopNetwork() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_OVERLIMIT_STOP_NETWORK, true);
    }

    public boolean isDataUsageTotalNotSetNotified() {
        return this.mSpHelper.load(SimUserConstants.KEY.DATA_USAGE_TOTAL_NOT_SET_NOTIFIED, false);
    }

    public boolean isExistTotalDataUsage() {
        return getDataUsageTotal() >= 0;
    }

    public boolean isHalfYearPackageEnable() {
        return this.mSpHelper.load(SimUserConstants.KEY.HALF_YEAR_PACKAGE, false);
    }

    public boolean isLeisureDataUsageEffective() {
        return isLeisureDataUsageOn() && getLeisureDataUsageTotal() > 0 && getLeisureDataUsageFromTime() != getLeisureDataUsageToTime();
    }

    public boolean isLeisureDataUsageOn() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_DATA_USAGE_ON, false);
    }

    public boolean isLeisureDataUsageOverLimitWarning() {
        return this.mSpHelper.load(SimUserConstants.KEY.LEISURE_DATA_USAGE_OVERLIMIT_WARNING, true);
    }

    public boolean isLockScreenTrafficEnable() {
        return isTrafficManageControlEnable() && this.mSpHelper.load(SimUserConstants.KEY.LOCK_SCREEN_TRAFFIC_ENABLE, false);
    }

    public boolean isMiMobileOperatorModify() {
        return this.mSpHelper.load(SimUserConstants.KEY.MI_MOBILE_OPERATOR_MODIFY_AGAIN, false);
    }

    public boolean isMobilePolicyEnable() {
        return this.mSpHelper.load(SimUserConstants.KEY.MOBILE_POLICY_ENABLE, false);
    }

    public boolean isNATipsEnable() {
        return this.mSpHelper.load(SimUserConstants.KEY.PURCHASE_TIPS_ENBALE, false);
    }

    public boolean isNATrafficPurchaseAvailable() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_PURCHASE_NA_AVALIABLE, false);
    }

    public boolean isNormalCardEnable() {
        return getBrand() == 0;
    }

    public boolean isNotLimitCardEnable() {
        return getBrand() == 2;
    }

    public boolean isOperatorSetted() {
        return (getProvince() == -1 || getCity() == -1 || getOperator() == "" || getBrand() == -1) ? false : true;
    }

    public boolean isOversea() {
        return this.mIsOversea;
    }

    public boolean isSimInserted() {
        return this.mIsSimInserted;
    }

    public boolean isSimLocationAlertIgnore() {
        return this.mSpHelper.load(SimUserConstants.KEY.SIM_LOCATION_ALERT_IGNORE, false);
    }

    public boolean isSmsAvailable() {
        return this.mIsSimInserted && !isDataRoaming();
    }

    public boolean isSupportCmccWebCorrection() {
        return false;
    }

    public boolean isSupportCorrection() {
        return getOperator() != "" && !TextUtils.equals(getOperator(), TelephonyUtil.VIRTUALOPT);
    }

    public boolean isSupportCorrection(String str) {
        return !TextUtils.isEmpty(str) && !TextUtils.equals(str, TelephonyUtil.VIRTUALOPT);
    }

    public boolean isTotalDataUsageSetted() {
        return isNotLimitCardEnable() || isDailyUsedCardEnable() || getDataUsageTotal() != -1;
    }

    public boolean isTrafficCorrectionAutoModify() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_CORRECTION_ALERT_IGNORE, true);
    }

    public boolean isTrafficManageControlEnable() {
        return this.mSpHelper.load(SimUserConstants.KEY.TRAFFIC_MANAGE_CONTROL, true);
    }

    public boolean isWebCorrectionSupported() {
        return this.mSpHelper.load(SimUserConstants.KEY.WEB_CORRECTION_SUPPORTED, false);
    }

    public boolean saveBrand(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.USER_BRAND, i);
    }

    public boolean saveCity(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.USER_CITY, i);
    }

    public boolean saveCorrectedOffsetValue(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_CORRECTED_VALUE, j);
    }

    public boolean saveCustomizedSmsContent(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.USER_CUSTOMIZED_SMS_CONTENT, str);
    }

    public boolean saveCustomizedSmsNum(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.USER_CUSTOMIZED_SMS_NUM, str);
    }

    public boolean saveDataUsageAutoCorrectedTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_AUTO_CORRECTED_TIME, j);
    }

    public boolean saveDataUsageCorrectedTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_CORRECTED_TIME, j);
    }

    public boolean saveDataUsageOverLimitStopNetworkTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_OVERLIMIT_STOP_NETWORK_TIME, j);
    }

    public boolean saveDataUsageOverLimitStopNetworkWarningTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_OVERLIMIT_STOP_NETWORK_WARNING_TIME, j);
    }

    public boolean saveDataUsageOverlayPackage(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_OVERLAY_PACKAGE, j);
    }

    public boolean saveDataUsageOverlayPackageTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_OVERLAY_PACKAGE_TIME, j);
    }

    public boolean saveDataUsageTotal(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_TOTAL, j);
    }

    public boolean saveDataUsageWarning(float f) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_WARNING, f);
    }

    public boolean saveLastTcRemain(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_LAST_TC_REMAIN, j);
    }

    public boolean saveLastTcUsed(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_LAST_TC_USED, j);
    }

    public boolean saveLeisureDataUsageCorrectedTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_DATA_USAGE_CORRECTED_TIME, j);
    }

    public boolean saveLeisureDataUsageCorrectedValue(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_DATA_USAGE_CORRECTED_VALUE, j);
    }

    public boolean saveLeisureDataUsageFromTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_DATA_USAGE_FROM_TIME, j);
    }

    public boolean saveLeisureDataUsageOverLimitWarningTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_DATA_USAGE_OVERLIMIT_WARNING_TIME, j);
    }

    public boolean saveLeisureDataUsageToTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_DATA_USAGE_TO_TIME, j);
    }

    public boolean saveLeisureDataUsageTotal(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_DATA_USAGE_TOTAL, j);
    }

    public boolean saveLeisureOverLimitStopNetworkTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_OVERLIMIT_STOP_NETWORK_TIME, j);
    }

    public boolean saveMonthStart(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.MONTH_START, i);
    }

    public boolean saveNATrafficPurchaseAvailable(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_PURCHASE_NA_AVALIABLE, z);
    }

    public boolean saveNATrafficPurchaseAvailableUpdateTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_PURCHASE_NA_AVALIABLE_UPDATE_TIME, j);
    }

    public boolean saveNATrafficPurchaseOrderTips(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.NA_TRAFFIC_PURCHASE_ORDER_TIPS, str);
    }

    public boolean saveNATrafficPurchaseType(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.NA_TRAFFIC_PURCHASE_TYPE, i);
    }

    public boolean saveNotLimitedDataUsageOverLimitStopNetworkTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.NOT_LIMITED_OVERLIMIT_WARNING_TIME, j);
    }

    public boolean saveOperator(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.USER_OPERATOR, str);
    }

    public boolean saveProvince(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.USER_PROVINCE, i);
    }

    public boolean savePurchaseActivityId(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.PURCHASE_ACTIVITY_ID, str);
    }

    public boolean saveTrafficCorrectionAutoModify(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_CORRECTION_ALERT_IGNORE, z);
    }

    public boolean saveWebCorrectionStatusRefreshTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.WEB_CORRECTION_STATUS_REFRESH_TIME, j);
    }

    public boolean saveWebCorrectionSupported(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.WEB_CORRECTION_SUPPORTED, z);
    }

    public boolean setBillPackageRemained(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.BILL_PACKAGE_REMAINED, j);
    }

    public boolean setBillPackageTotal(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.BILL_PACKAGE_TOTAL, j);
    }

    public boolean setBillSmsDetail(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.BILL_SMS_DETAIL, str);
    }

    public boolean setBillTcResult(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.BILL_TC_RESULT, str);
    }

    public boolean setBillTcResultCode(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.BILL_TC_RESULT_CODE, i);
    }

    public boolean setCallTimePackageRemained(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.CALL_TIME_PACKAGE_REMAINED, j);
    }

    public boolean setCallTimePackageTotal(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.CALL_TIME_PACKAGE_TOTAL, j);
    }

    public boolean setCorrectionSourceUpdateTime(String str, long j) {
        return this.mSpHelper.save(str, j);
    }

    public boolean setCustomizeDailyLimitWarning(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.CUSTOMIZE_DAILY_LIMIT_WARNING, j);
    }

    public boolean setDailyCardSettingGuideEnable(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_CARD_SETTING_GUIDE_ENABLE, z);
    }

    public boolean setDailyLimitEnabled(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_LIMIT_AVAILABLE, z);
    }

    public boolean setDailyLimitWarningType(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_LIMIT_WARNING_TYPE, i);
    }

    public boolean setDailyUsedCardBrand(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_USED_CARD_BRAND, str);
    }

    public boolean setDailyUsedCardDataUpdateTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_CARD_DATA_UPDATE_TIME, j);
    }

    public boolean setDailyUsedCardPackage(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_USED_CARD_PACKAGE, j);
    }

    public boolean setDailyUsedCardStopNetworkCount(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_CARD_STOP_NETWORK_COUNT, i);
    }

    public boolean setDailyUsedCardStopNetworkOn(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_CARD_STOP_NETWORK_TYPE, z);
    }

    public boolean setDailyUsedCardStopNetworkTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_CARD_STOP_NETWORK_TIME, j);
    }

    public boolean setDataRoamingStopChanged(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_ROAMING_STOP_CHANGED, z);
    }

    public boolean setDataRoamingStopUpdateTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_ROAMING_STOP_UPDATE_TIME, j);
    }

    public boolean setDataUsageOverDailyLimitTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_OVER_DAILY_LIMIT_TIME, j);
    }

    public boolean setDataUsageOverRoamingDailyLimitTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_OVER_ROAMING_DAILY_LIMIT_TIME, j);
    }

    public boolean setDataUsageTotalNotSetNotified(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_TOTAL_NOT_SET_NOTIFIED, z);
    }

    public boolean setFirewallMobilePreConfig(int i) {
        return this.mSpHelper.save("firewall_mobile_preconfig", i);
    }

    public boolean setHalfYearPackageBeginTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.HALF_YEAR_PACKAGE_BEGIN_TIME, j);
    }

    public boolean setHalfYearPackageEnable(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.HALF_YEAR_PACKAGE, z);
    }

    public boolean setHalfYearPackageValue(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.HALF_YEAR_PACKAGE_VALUE, j);
    }

    public void setIccid(String str) {
        this.mIccid = str;
    }

    public boolean setLastBillNotifyTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.LAST_BILL_NOTIFY_TIME, j);
    }

    public boolean setLastBillTcDirection(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.LAST_BILL_TC_DIRECTION, str);
    }

    public boolean setLastTrafficTcDirection(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.LAST_TRAFFIC_TC_DIRECTION, str);
    }

    public boolean setLockScreenTrafficEnable(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.LOCK_SCREEN_TRAFFIC_ENABLE, z);
    }

    public boolean setLockScreenWarningLevel(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.LOCK_SCREEN_WARNING_LEVEL, i);
    }

    public boolean setMiMobileOperatorModify(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.MI_MOBILE_OPERATOR_MODIFY_AGAIN, z);
    }

    public boolean setMobilePolicyEnable(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.MOBILE_POLICY_ENABLE, z);
    }

    public boolean setNATipsEnable(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.PURCHASE_TIPS_ENBALE, z);
    }

    public boolean setNotLimitedCardPackage(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.NOT_LIMITED_CARD_PACKAGE, j);
    }

    public boolean setOverDataUsageStopNetworkType(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.OVER_DATA_USAGE_STOP_NETWORK_TYPE, i);
    }

    public boolean setPackageChangeUpdateTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.PACKAGE_CHANGE_UPDATE_TIME, j);
    }

    public void setPhoneNumber(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mPhoneNumber = str;
        }
    }

    public boolean setRoamingBeginTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.ROAMING_BEGIN_TIME, j);
    }

    public boolean setRoamingDailyLimitEnabled(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.ROAMING_DAILY_LIMIT_ENABLED, z);
    }

    public boolean setRoamingDailyLimitTraffic(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.ROAMING_DAILY_LIMIT_TRAFFIC, j);
    }

    public boolean setRoamingNetworkState(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.ROAMING_NETWORK_STATE, z);
    }

    public boolean setRoamingOverLimitOptType(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.ROAMING_OVER_LIMIT_OPT_TYPE, i);
    }

    public boolean setShouldWebCorrection(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.WEB_SHOULD_CORRECTION, j);
    }

    public void setSimId(long j) {
        this.mSimId = j;
    }

    public void setSimInserted(boolean z) {
        this.mIsSimInserted = z;
    }

    public boolean setSimLocationAlertIgnore(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.SIM_LOCATION_ALERT_IGNORE, z);
    }

    public void setSimName(String str) {
        this.mSimName = str;
    }

    public void setSlotNum(int i) {
        this.mSlotNum = i;
    }

    public boolean setTcSmsReportCache(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.TC_SMS_REPORT_CACHE, str);
    }

    public boolean setTrafficCorrectionEngineUpdateTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_CORRECTION_ENGINE_UPDATE_TIME, j);
    }

    public boolean setTrafficLimitValue(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.DAILY_LIMIT_VALUE, i);
    }

    public boolean setTrafficManageControlEnable(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_MANAGE_CONTROL, z);
    }

    public boolean setTrafficProtectedStopNetTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_PROTECTED_STOP_NET_TIME, j);
    }

    public boolean setTrafficPurchaseStatus(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_PURCHASE_STATUS, z);
    }

    public boolean setTrafficSettingDailyLimitNotifyUpdateTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_SETTING_DAILY_LIMIT_NOTIFY_UPDATE_TIME, j);
    }

    public boolean setTrafficSettingDailyNotifyUpdateTime(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_SETTING_DAILY_NOTIFY_UPDATE_TIME, z);
    }

    public boolean setTrafficSettingMonthlyNotifyUpdateTime(long j) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_SETTING_MONTHLY_NOTIFY_UPDATE_TIME, j);
    }

    public boolean setTrafficSmsDetail(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_SMS_DETAIL, str);
    }

    public boolean setTrafficTcResult(String str) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_TC_RESULT, str);
    }

    public boolean setTrafficTcResultCode(int i) {
        return this.mSpHelper.save(SimUserConstants.KEY.TRAFFIC_TC_RESULT_CODE, i);
    }

    public String toString() {
        Object obj;
        StringBuilder sb = new StringBuilder("SimInfo: ");
        sb.append("imsi:");
        String str = "null";
        if (TextUtils.isEmpty(this.mImsi)) {
            obj = str;
        } else {
            String str2 = this.mImsi;
            obj = Character.valueOf(str2.charAt(str2.length() - 1));
        }
        sb.append(obj);
        sb.append(", simName:");
        sb.append(this.mSimName);
        sb.append(", simId:");
        sb.append(this.mSimId);
        sb.append(", iccId:");
        String str3 = this.mIccid;
        if (str3 != null) {
            str = str3.replaceFirst(".{4}$", "XXXX");
        }
        sb.append(str);
        sb.append(", slotNum:");
        sb.append(this.mSlotNum);
        sb.append(", oversea:");
        sb.append(this.mIsOversea);
        sb.append(", inserted:");
        sb.append(this.mIsSimInserted);
        sb.append(", dataRoaming:");
        sb.append(isDataRoaming());
        return sb.toString();
    }

    public boolean toggleCustomizedSms(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.USER_CUSTOMIZED_SMS, z);
    }

    public boolean toggleDataUsageAutoCorrection(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_AUTO_CORRECTION_ON, z);
    }

    public boolean toggleDataUsageOverLimitStopNetwork(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.DATA_USAGE_OVERLIMIT_STOP_NETWORK, z);
    }

    public boolean toggleLeisureDataUsageOn(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_DATA_USAGE_ON, z);
    }

    public boolean toggleLeisureDataUsageOverLimitWarning(boolean z) {
        return this.mSpHelper.save(SimUserConstants.KEY.LEISURE_DATA_USAGE_OVERLIMIT_WARNING, z);
    }
}
