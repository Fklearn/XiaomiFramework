package com.miui.networkassistant.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.miui.analytics.AnalyticsUtil;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsHelper {
    private static final String CATEGORY_NAME = "networkassistant";
    private static final String CATEGORY_NAME_NETWORK_DIAGNOSTICS = "NetworkDiagnostics";
    private static final String NETWORK_ASSISTANT_ACTIVE = "networkassistant_active";
    private static final String TRACK_ID_EMPTY_SMS_REPORT = "empty_sms_report";
    private static final String TRACK_ID_SMS_REPORT = "sms_report";
    private static final String TRACK_ID_TC_SMS_DETAIL_REPORT = "tc_sms_detail_report";
    private static final String TRACK_ID_TC_SMS_DETAIL_SHOW = "tc_sms_detail_show";
    private static final String TRACK_ID_TC_SMS_REPORT = "tc_sms_report";
    private static final String TRACK_ID_TC_SMS_REPORT_SHOW = "tc_sms_report_show";
    public static final String TRACK_ITEM_FIREWALL = "net_control";
    public static final String TRACK_ITEM_LONG_CORRECTION_SETTING = "flow_correction_hold";
    public static final String TRACK_ITEM_PACKAGE_SETTING = "flow_set";
    public static final String TRACK_ITEM_SETTING = "settings";
    public static final String TRACK_ITEM_TRAFFIC_CORRECTION = "flow_correction";
    public static final String TRACK_ITEM_TRAFFIC_PURCHASE = "flow_buy";
    public static final String TRACK_ITEM_TRAFFIC_SORTED = "flow_list";
    private static final String TRACK_KEY_ADDR_FAMILY_CN = "addrFamily_CN";
    private static final String TRACK_KEY_ADDR_FAMILY_GLOBAL = "addrFamily_CN";
    private static final String TRACK_KEY_ALLOW = "allow";
    public static final String TRACK_KEY_AUTO_CORRECTION = "toggle_auto_correction";
    public static final String TRACK_KEY_AUTO_MODIFY_PACKAGE = "toggle_autochange_flow";
    private static final String TRACK_KEY_CHANGE_MOBILE_FIREWALL = "change_mobile_firewall";
    private static final String TRACK_KEY_CHANGE_WLAN_FIREWALL = "change_wlan_firewall";
    private static final String TRACK_KEY_CMCC_WEB_CORRECT = "cmcc_web_correct";
    private static final String TRACK_KEY_CMCC_WEB_CORRECT_STATS = "cmcc_web_correct_stats";
    private static final String TRACK_KEY_CORRECTION_SMS = "flow_correction_sms";
    private static final String TRACK_KEY_CORRECTION_SMS_BILL = "flow_correction_sms_bill";
    private static final String TRACK_KEY_CORRECTION_SMS_CALLTIME = "flow_correction_sms_calltime";
    private static final String TRACK_KEY_CORRECTION_WEB = "flow_correction_net";
    private static final String TRACK_KEY_CUSTOMIZED_SMS = "change_correction_order";
    public static final String TRACK_KEY_DAILY_BRAND = "daily_brand";
    public static final String TRACK_KEY_DAILY_CARD = "daily_card";
    public static final String TRACK_KEY_DAILY_FLOW_USE = "daily_flow_use";
    public static final String TRACK_KEY_DAILY_LIMIT = "toggle_dailyflow_limit";
    public static final String TRACK_KEY_DATA_ROAMING_DAILY_LIMIT_SETTING = "toggle_overseas_daily_limit";
    public static final String TRACK_KEY_DATA_ROAMING_SETTING = "toggle_overseas_control";
    public static final String TRACK_KEY_DATA_USAGE_APP_IGNORE = "flow_except_app";
    private static final String TRACK_KEY_DNSASSIGMENT = "dnsAssignment";
    public static final String TRACK_KEY_DOUBLE_SIM = "toggle_double_sim";
    public static final String TRACK_KEY_DOUBLE_SIM1_ENABLE = "toggle_dailyflow_sim1";
    public static final String TRACK_KEY_DOUBLE_SIM1_VALUE = "daily_flow_double_sim1";
    public static final String TRACK_KEY_DOUBLE_SIM2_ENABLE = "toggle_dailyflow_sim2";
    public static final String TRACK_KEY_DOUBLE_SIM2_VALUE = "daily_flow_double_sim2";
    public static final String TRACK_KEY_DOUBLE_SIM_TOTAL = "daily_flow_double_sim1+2";
    public static final String TRACK_KEY_EXTRA_PACKAGE_SETTING = "toggle_add_flow";
    public static final String TRACK_KEY_EXTRA_PACKAGE_SIZE = "add_flow_size1";
    public static final String TRACK_KEY_LEISURE_PACKAGE_SETTING = "toggle_idler_flow";
    public static final String TRACK_KEY_LEISURE_PACKAGE_SIZE = "idler_flow_size1";
    public static final String TRACK_KEY_LOCK_SCREEN_SETTING = "toggle_lockscreen_flow";
    private static final String TRACK_KEY_MAIN_BUTTON_CLICK = "net_homepage_click";
    private static final String TRACK_KEY_NA_TC_FROM = "na_tc_from";
    private static final String TRACK_KEY_NA_TC_TYPE = "na_tc_type";
    public static final String TRACK_KEY_NEW_INSTALL_APP_FIREWALL_MOBILE = "toggle_newapp_data_allow";
    public static final String TRACK_KEY_NEW_INSTALL_APP_FIREWALL_WIFI = "toggle_newapp_wlan_allow";
    private static final String TRACK_KEY_NOTIFICATION_SHOW = "notification_show";
    public static final String TRACK_KEY_PACKAGE_SELECT = "package_select";
    public static final String TRACK_KEY_PACKAGE_START_DATE = "daily_flow_startdate";
    public static final String TRACK_KEY_PACKAGE_STATUS_ENOUGH = "enough";
    public static final String TRACK_KEY_PACKAGE_STATUS_EXCEED = "exceed";
    public static final String TRACK_KEY_PACKAGE_STATUS_REACH = "reach";
    public static final String TRACK_KEY_PACKAGE_WARNING_VALUE = "daily_warning_value";
    private static final String TRACK_KEY_PARAM_CORRECTION = "correction";
    private static final String TRACK_KEY_PARAM_CORRECTION_SMS = "change";
    private static final String TRACK_KEY_PARAM_MODULE_NAME = "module_click";
    private static final String TRACK_KEY_PING_RES_DIFF_SOCKET = "pingResDiffSocket";
    private static final String TRACK_KEY_RESTRICT = "restrict";
    private static final String TRACK_KEY_SHOW_CMCC_WEBSITE = "show_cmcc_website";
    public static final String TRACK_KEY_SHOW_SPEED_STATUS_BAR = "toggle_statusbar_netspeed";
    public static final String TRACK_KEY_SHOW_TRAFFIC_STATUS_BAR = "toggle_notification_bar";
    public static final String TRACK_KEY_SINGLE_SIM = "toggle_single_sim";
    public static final String TRACK_KEY_SINGLE_SIM_ENABLE = "toggle_dailyflow_sim";
    public static final String TRACK_KEY_SINGLE_SIM_VALUE = "daily_flow_single";
    private static final String TRACK_KEY_START = "start";
    private static final String TRACK_KEY_STEP = "step";
    private static final String TRACK_KEY_TRAFFIC_CORRECTION_RESULT = "flow_correction";
    public static final String TRACK_KEY_TRAFFIC_OVER_LIMIT = "toggle_daily_exceed_cutoff";
    public static final String TRACK_KEY_TRAFFIC_PURCHASE_SHOW = "toggle_flowbuy_display";
    private static final String TRACK_KEY_TYPE = "type";
    public static final String TRACK_PARAM_ALL_APP_ALLOW = "all_allow";
    public static final String TRACK_PARAM_ALL_APP_BAN = "all_ban";
    private static final String TRACK_PARAM_CORRECTION_CODE_ERROR = "code_error";
    private static final String TRACK_PARAM_CORRECTION_GET_SMS_INSTRUCTION_FAILURE = "get_sms_instruction_failure";
    private static final String TRACK_PARAM_CORRECTION_INVALID_SMS = "invalid_sms";
    private static final String TRACK_PARAM_CORRECTION_PARSE_FAILURE = "parse_failure";
    private static final String TRACK_PARAM_CORRECTION_SEND_FAILURE = "send_failure";
    private static final String TRACK_PARAM_CORRECTION_SUCCESS = "success";
    private static final String TRACK_PARAM_CORRECTION_TIMEOUT = "timeout";
    private static final String TRACK_PARAM_CORRECTION_WEB_FAILED = "web_failure";
    public static final String TRACK_PARAM_WHITE_LIST_ALLOW = "white_list_allow";

    private AnalyticsHelper() {
    }

    private static String getCorrectionParamValue(int i) {
        if (i == 10) {
            return TRACK_PARAM_CORRECTION_CODE_ERROR;
        }
        switch (i) {
            case 0:
                return TRACK_PARAM_CORRECTION_SUCCESS;
            case 1:
                return TRACK_PARAM_CORRECTION_SEND_FAILURE;
            case 2:
                return TRACK_PARAM_CORRECTION_INVALID_SMS;
            case 3:
                return TRACK_PARAM_CORRECTION_TIMEOUT;
            case 4:
                return TRACK_PARAM_CORRECTION_PARSE_FAILURE;
            case 5:
                return TRACK_PARAM_CORRECTION_GET_SMS_INSTRUCTION_FAILURE;
            case 6:
                return TRACK_PARAM_CORRECTION_WEB_FAILED;
            default:
                return "";
        }
    }

    public static void recordCalculateEvent(String str, long j) {
        AnalyticsUtil.recordCalculateEvent(CATEGORY_NAME, str, j, (Map<String, String>) null);
    }

    public static void recordCalculateEvent(String str, long j, Map<String, String> map) {
        AnalyticsUtil.recordCalculateEvent(CATEGORY_NAME, str, j, map);
    }

    public static void recordCountEvent(String str) {
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME, str, (Map<String, String>) null);
    }

    public static void recordCountEvent(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME, str, map);
    }

    private static void recordException(String str) {
        Log.e("recordException", str);
    }

    public static void recordNumericEvent(String str, long j) {
        AnalyticsUtil.recordNumericEvent(CATEGORY_NAME, str, j);
    }

    public static void recordStringPropertyEvent(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent(CATEGORY_NAME, str, str2);
    }

    public static void recordThrowable(Throwable th) {
        recordException(String.format("Version: %s \nID: %s\n%s", new Object[]{Build.VERSION.INCREMENTAL, DeviceUtil.getAndroidId("recordThrowable"), th.getMessage()}));
    }

    public static void recordThrowable(Throwable th, String str) {
        recordException(String.format("Version: %s \nID: %s\nExtraMsg: %s\n%s", new Object[]{Build.VERSION.INCREMENTAL, DeviceUtil.getAndroidId("recordThrowable"), str, th.getMessage()}));
    }

    public static void trackActiveNetworkAssistant(Context context) {
        recordCountEvent(NETWORK_ASSISTANT_ACTIVE);
    }

    public static void trackCmccWebCorrect(int i) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_CMCC_WEB_CORRECT_STATS, i + "");
        recordCountEvent(TRACK_KEY_CMCC_WEB_CORRECT, hashMap);
    }

    public static void trackCustomizedSms(ITrafficCorrection.TrafficConfig trafficConfig, String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAM_CORRECTION_SMS, trafficConfig.getBrandId() + "_" + trafficConfig.getProvinceId() + "_" + trafficConfig.getCityId() + "_" + str + "_" + str2);
        recordCountEvent(TRACK_KEY_CUSTOMIZED_SMS, hashMap);
    }

    public static void trackDailyBrandSelect(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_DAILY_BRAND, str);
        recordCountEvent(TRACK_KEY_DAILY_BRAND, hashMap);
    }

    public static void trackEmptySmsReport() {
        recordCountEvent(TRACK_ID_EMPTY_SMS_REPORT);
    }

    public static void trackMainButtonClickCountEvent(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAM_MODULE_NAME, str);
        recordCountEvent(TRACK_KEY_MAIN_BUTTON_CLICK, hashMap);
    }

    public static void trackNetworkDiagnosticsCnAddrFamily(Map<String, String> map) {
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME_NETWORK_DIAGNOSTICS, "addrFamily_CN", map);
    }

    public static void trackNetworkDiagnosticsDnsAssigment(Map<String, String> map) {
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME_NETWORK_DIAGNOSTICS, TRACK_KEY_DNSASSIGMENT, map);
    }

    public static void trackNetworkDiagnosticsGlobalAddrFamily(Map<String, String> map) {
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME_NETWORK_DIAGNOSTICS, "addrFamily_CN", map);
    }

    public static void trackNetworkDiagnosticsNotificationShow(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("type", str);
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME_NETWORK_DIAGNOSTICS, TRACK_KEY_NOTIFICATION_SHOW, hashMap);
    }

    public static void trackNetworkDiagnosticsPingResultDiffSocket(Map<String, String> map) {
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME_NETWORK_DIAGNOSTICS, TRACK_KEY_PING_RES_DIFF_SOCKET, map);
    }

    public static void trackNetworkDiagnosticsStart() {
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME_NETWORK_DIAGNOSTICS, "start", (Map<String, String>) null);
    }

    public static void trackNetworkDiagnosticsStep(Map<String, String> map) {
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME_NETWORK_DIAGNOSTICS, TRACK_KEY_STEP, map);
    }

    public static void trackPackageSelect(int i) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PACKAGE_SELECT, String.valueOf(i));
        recordCountEvent(TRACK_KEY_PACKAGE_SELECT, hashMap);
    }

    public static void trackSetMobileFirewallRule(String str, boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put(z ? TRACK_KEY_RESTRICT : TRACK_KEY_ALLOW, str);
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME, TRACK_KEY_CHANGE_MOBILE_FIREWALL, hashMap);
    }

    public static void trackSetWlanFirewallRule(String str, boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put(z ? TRACK_KEY_RESTRICT : TRACK_KEY_ALLOW, str);
        AnalyticsUtil.recordCountEvent(CATEGORY_NAME, TRACK_KEY_CHANGE_WLAN_FIREWALL, hashMap);
    }

    public static void trackShowCmccWebsite() {
        recordCountEvent(TRACK_KEY_SHOW_CMCC_WEBSITE);
    }

    public static void trackSmsReport() {
        recordCountEvent(TRACK_ID_SMS_REPORT);
    }

    public static void trackTcSmsDetailReport(int i) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_NA_TC_TYPE, String.valueOf(i));
        recordCountEvent(TRACK_ID_TC_SMS_DETAIL_REPORT, hashMap);
    }

    public static void trackTcSmsDetailShow(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_NA_TC_FROM, str);
        recordCountEvent(TRACK_ID_TC_SMS_DETAIL_SHOW, hashMap);
    }

    public static void trackTcSmsReport(int i) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_NA_TC_TYPE, String.valueOf(i));
        recordCountEvent(TRACK_ID_TC_SMS_REPORT, hashMap);
    }

    public static void trackTcSmsShow(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_NA_TC_FROM, str);
        recordCountEvent(TRACK_ID_TC_SMS_REPORT_SHOW, hashMap);
    }

    public static void trackTrafficCorrectionResult(TrafficUsedStatus trafficUsedStatus) {
        String correctionParamValue = getCorrectionParamValue(trafficUsedStatus.getReturnCode());
        if (!TextUtils.isEmpty(correctionParamValue)) {
            HashMap hashMap = new HashMap(1);
            hashMap.put(TRACK_KEY_PARAM_CORRECTION, correctionParamValue);
            recordCountEvent("flow_correction", hashMap);
        }
    }

    public static void trackTrafficSmsCorrection(String str, String str2, int i, int i2) {
        String correctionParamValue = getCorrectionParamValue(i);
        if (!TextUtils.isEmpty(correctionParamValue)) {
            HashMap hashMap = new HashMap(1);
            hashMap.put(str, str2 + "_" + correctionParamValue);
            if ((i2 & 1) != 0) {
                recordCountEvent(TRACK_KEY_CORRECTION_SMS, hashMap);
            }
            if ((i2 & 2) != 0) {
                recordCountEvent(TRACK_KEY_CORRECTION_SMS_BILL, hashMap);
            }
            if ((i2 & 4) != 0) {
                recordCountEvent(TRACK_KEY_CORRECTION_SMS_CALLTIME, hashMap);
            }
        }
    }

    public static void trackTrafficWebCorrection(String str, String str2, boolean z) {
        HashMap hashMap = new HashMap(1);
        StringBuilder sb = new StringBuilder(str2);
        sb.append("_");
        sb.append(z ? TRACK_PARAM_CORRECTION_SUCCESS : "failure");
        hashMap.put(str, sb.toString());
        recordCountEvent(TRACK_KEY_CORRECTION_WEB, hashMap);
    }
}
