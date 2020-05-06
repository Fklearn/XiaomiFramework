package com.miui.networkassistant.config;

import com.miui.networkassistant.model.FirewallRule;

public final class SimUserConstants {

    public static final class DEFAULT {
        public static final long BILL_PACKAGE_REMAINED_DEFAULT = Long.MIN_VALUE;
        public static final long BILL_PACKAGE_TOTAL_DEFAULT = -1;
        public static final String BILL_SMS_DETAIL_DEFAULT = "";
        public static final int BILL_TC_RESULT_CODE_DEFAULT = 0;
        public static final String BILL_TC_RESULT_DEFAULT = "";
        public static final long CALL_TIME_PACKAGE_REMAINED_DEFAULT = -1;
        public static final long CALL_TIME_PACKAGE_TOTAL_DEFAULT = -1;
        public static final int COMMON_INT_DEFAULT = -1;
        public static final String COMMON_STRING_DEFAULT = "";
        public static final long CORRECTION_SOURCE_UPDATE_TIME = 0;
        public static final long CUSTOMIZE_DAILY_LIMIT_WARNING_DEFAULT = 0;
        public static final long DAILY_CARD_DATA_UPDATE_TIME_DEFAULT = 0;
        public static final boolean DAILY_CARD_SETTING_GUIDE_ENABLE_DEFAULT = true;
        public static final int DAILY_CARD_STOP_NETWORK_COUNT_DEFAULT = 0;
        public static final long DAILY_CARD_STOP_NETWORK_TIME_DEFAULT = 0;
        public static final boolean DAILY_CARD_STOP_NETWORK_TYPE_DEFAULT = false;
        public static final boolean DAILY_LIMIT_AVAILABLE_DEFAULT = false;
        public static final int DAILY_LIMIT_VALUE_DEFAULT = 3;
        public static final int DAILY_LIMIT_WARNING_TYPE_DEFAULT = 1;
        public static final long DAILY_USED_CARD_PACKAGE_DEFAULT = 0;
        public static final boolean DATA_ROAMING_STOP_CHANGED_DEFAULT = false;
        public static final long DATA_ROAMING_STOP_UPDATE_TIME_DEFAULT = 0;
        public static final long DATA_USAGE_AUTO_CORRECTED_TIME_DEFAULT = 0;
        public static final boolean DATA_USAGE_AUTO_CORRECTION_ON_DEFAULT = true;
        public static final long DATA_USAGE_CORRECTED_TIME_DEFAULT = 0;
        public static final long DATA_USAGE_CORRECTED_VALUE_DEFAULT = 0;
        public static final long DATA_USAGE_NOT_LIMITED_OVERLIMIT_WARNING_TIME_DEFAULT = 0;
        public static final long DATA_USAGE_OVERLAY_PACKAGE_DEFAULT = 0;
        public static final long DATA_USAGE_OVERLAY_PACKAGE_TIME_DEFAULT = 0;
        public static final boolean DATA_USAGE_OVERLIMIT_STOP_NETWORK_DEFAULT = true;
        public static final long DATA_USAGE_OVERLIMIT_STOP_NETWORK_TIME_DEFAULT = 0;
        public static final long DATA_USAGE_OVERLIMIT_STOP_NETWORK_WARNING_TIME_DEFAULT = 0;
        public static final long DATA_USAGE_OVER_ROAMING_DAILY_LIMIT_TIME_DEFAULT = 0;
        public static final long DATA_USAGE_TOTAL_DEFAULT = -1;
        public static final boolean DATA_USAGE_TOTAL_NOT_SET_NOTIFIED_DEFAULT = false;
        public static final float DATA_USAGE_WARNING_DEFAULT = 0.8f;
        public static final int FIREWALL_MOBILE_PRECONFIG_DEFAULT = FirewallRule.Allow.value();
        public static final long HALF_YEAR_PACKAGE_BEGIN_TIME_DEFAULT = 0;
        public static final boolean HALF_YEAR_PACKAGE_DEFAULT = false;
        public static final long HALF_YEAR_PACKAGE_VALUE_DEFAULT = 0;
        public static final long LEISURE_DATA_USAGE_CORRECTED_TIME_DEFAULT = 0;
        public static final long LEISURE_DATA_USAGE_CORRECTED_VALUE_DEFAULT = 0;
        public static final long LEISURE_DATA_USAGE_FROM_TIME_DEFAULT = 82800000;
        public static final boolean LEISURE_DATA_USAGE_ON_DEFAULT = false;
        public static final boolean LEISURE_DATA_USAGE_OVERLIMIT_WARNING_DEFAULT = true;
        public static final long LEISURE_DATA_USAGE_OVERLIMIT_WARNING_TIME_DEFAULT = 0;
        public static final long LEISURE_DATA_USAGE_TOTAL_DEFAULT = 0;
        public static final long LEISURE_DATA_USAGE_TO_TIME_DEFAULT = 25200000;
        public static final long LEISURE_OVERLIMIT_STOP_NETWORK_TIME_DEFAULT = 0;
        public static final boolean LOCK_SCREEN_TRAFFIC_ENABLE_DEFAULT = false;
        public static final int LOCK_SCREEN_WARNING_LEVEL_DEFAULT = -1;
        public static final boolean MI_MOBILE_OPERATOR_MODIFY_DEFAULT = false;
        public static final boolean MOBILE_POLICY_ENABLE_DEFALUT = false;
        public static final int MONTH_START_DEFAULT = 1;
        public static final String NA_TRAFFIC_PURCHASE_ORDER_TIPS_DEFAULT = "";
        public static final long NOT_LIMITED_OVERLIMIT_DEFAULT = 42949672960L;
        public static final int OVER_DATA_USAGE_STOP_NETWORK_TYPE_DEFAULT = 0;
        public static final long PACKAGE_CHANGE_UPDATE_TIME_DEFAULT = 0;
        public static final String PURCHASE_ACTIVITY_ID_DEFAULT = "NOACTIVITY";
        public static final boolean PURCHASE_TIPS_ENABLE_DEFAULT = false;
        public static final long ROAMING_BEGIN_TIME_DEFAULT = 0;
        public static final boolean ROAMING_DAILY_LIMIT_ENABLED_DEFAULT = false;
        public static final long ROAMING_DAILY_LIMIT_TRAFFIC_DEFAULT = 0;
        public static final boolean ROAMING_NETWORK_STATE_DEFAULT = false;
        public static final int ROAMING_OVER_LIMIT_OPT_TYPE_DEFAULT = 1;
        public static final boolean SIM_LOCATION_ALERT_IGNORE = false;
        public static final String TC_SMS_REPORT_CACHE_DEFAULT = "";
        public static final boolean TRAFFIC_CORRECTION_ALERT_IGNORE_DEFAULT = true;
        public static final long TRAFFIC_CORRECTION_ENGINE_UPDATE_TIME_DEFAULT = 0;
        public static final boolean TRAFFIC_MANAGE_CONTROL_DEFAULT = true;
        public static final long TRAFFIC_OVER_DAILY_LIMIT_TIME_DEFAULT = 0;
        public static final long TRAFFIC_PROTECTED_STOP_NET_TIME_DEFAULT = 0;
        public static final long TRAFFIC_PURCHASE_AVALIABLE_NA_UPDATE_TIME_DEFAULT = 0;
        public static final boolean TRAFFIC_PURCHASE_NA_AVALIABLE_DEFAULT = false;
        public static final boolean TRAFFIC_PURCHASE_STATUS_DEFAULT = false;
        public static final long TRAFFIC_SETTING_DAILY_LIMIT_NOTIFY_UPDATE_TIME_DEAFULT = 0;
        public static final boolean TRAFFIC_SETTING_DAILY_NOTIFY_UPDATE_TIME_DEFAULT = true;
        public static final long TRAFFIC_SETTING_MONTHLY_NOTIFY_UPDATE_TIME_DEFAULT = 0;
        public static final String TRAFFIC_SMS_DETAIL_DEFAULT = "";
        public static final int TRAFFIC_TC_RESULT_CODE_DEFAULT = 0;
        public static final String TRAFFIC_TC_RESULT_DEFAULT = "";
        public static final long TRAFFIC_ZERO = 0;
        public static final boolean USER_CUSTOMIZED_SMS_DEFAULT = false;
        public static final long WEB_CORRECTION_STATUS_REFRESH_TIME_DEFAULT = 0;
        public static final boolean WEB_CORRECTION_SUPPORTED_DEFAULT = false;
        public static final long WEB_SHOULD_CORRECTION_DEFAULT = 0;

        private DEFAULT() {
        }
    }

    public static final class KEY {
        public static final String BILL_PACKAGE_REMAINED = "bill_package_remained";
        public static final String BILL_PACKAGE_TOTAL = "bill_package_total";
        public static final String BILL_SMS_DETAIL = "bill_sms_detail";
        public static final String BILL_TC_RESULT = "bill_tc_result";
        public static final String BILL_TC_RESULT_CODE = "bill_tc_result_code";
        public static final String CALL_TIME_PACKAGE_REMAINED = "call_time_package_remained";
        public static final String CALL_TIME_PACKAGE_TOTAL = "call_time_package_total";
        public static final String CUSTOMIZE_DAILY_LIMIT_WARNING = "customize_daily_limit_warning";
        public static final String DAILY_CARD_DATA_UPDATE_TIME = "daily_card_data_update_time";
        public static final String DAILY_CARD_SETTING_GUIDE_ENABLE = "daily_card_setting_guide_enable";
        public static final String DAILY_CARD_STOP_NETWORK_COUNT = "daily_card_stop_network_count";
        public static final String DAILY_CARD_STOP_NETWORK_TIME = "daily_card_stop_network_time";
        public static final String DAILY_CARD_STOP_NETWORK_TYPE = "daily_card_stop_network_type";
        public static final String DAILY_LIMIT_AVAILABLE = "daily_limit_available";
        public static final String DAILY_LIMIT_VALUE = "daily_limit_value";
        public static final String DAILY_LIMIT_WARNING_TYPE = "daily_limit_warning_type";
        public static final String DAILY_USED_CARD_BRAND = "daily_used_card_brand";
        public static final String DAILY_USED_CARD_PACKAGE = "daily_used_card_package";
        public static final String DATA_ROAMING_STOP_CHANGED = "data_roaming_stop_changed";
        public static final String DATA_ROAMING_STOP_UPDATE_TIME = "data_roaming_stop_update_time";
        public static final String DATA_USAGE_AUTO_CORRECTED_TIME = "data_usage_auto_corrected_time";
        public static final String DATA_USAGE_AUTO_CORRECTION_ON = "data_usage_auto_correction_on";
        public static final String DATA_USAGE_CORRECTED_TIME = "data_usage_correct_time";
        public static final String DATA_USAGE_CORRECTED_VALUE = "data_usage_correct_value";
        public static final String DATA_USAGE_OVERLAY_PACKAGE = "data_usage_overlay_package";
        public static final String DATA_USAGE_OVERLAY_PACKAGE_TIME = "data_usage_overlay_package_time";
        public static final String DATA_USAGE_OVERLIMIT_STOP_NETWORK = "data_usage_overlimit_stop_network";
        public static final String DATA_USAGE_OVERLIMIT_STOP_NETWORK_TIME = "data_usage_overlimit_stop_network_time";
        public static final String DATA_USAGE_OVERLIMIT_STOP_NETWORK_WARNING_TIME = "data_usage_overlimit_stop_network_warning_time";
        public static final String DATA_USAGE_OVER_ROAMING_DAILY_LIMIT_TIME = "data_usage_over_roaming_daily_limit_time";
        public static final String DATA_USAGE_TOTAL = "data_usage_total";
        public static final String DATA_USAGE_TOTAL_NOT_SET_NOTIFIED = "data_usage_total_not_set_notified";
        public static final String DATA_USAGE_WARNING = "data_usage_warning";
        public static final String FIREWALL_MOBILE_PRECONFIG = "firewall_mobile_preconfig";
        public static final String HALF_YEAR_PACKAGE = "half_year_package";
        public static final String HALF_YEAR_PACKAGE_BEGIN_TIME = "half_year_package_begin_time";
        public static final String HALF_YEAR_PACKAGE_VALUE = "half_year_package_value";
        public static final String LAST_BILL_NOTIFY_TIME = "last_bill_notify_time";
        public static final String LAST_BILL_TC_DIRECTION = "last_bill_tc_direction";
        public static final String LAST_TRAFFIC_TC_DIRECTION = "last_traffic_tc_direction";
        public static final String LEISURE_DATA_USAGE_CORRECTED_TIME = "leisure_data_usage_correct_time";
        public static final String LEISURE_DATA_USAGE_CORRECTED_VALUE = "leisure_data_usage_corrected_value";
        public static final String LEISURE_DATA_USAGE_FROM_TIME = "leisure_data_usage_from_time";
        public static final String LEISURE_DATA_USAGE_ON = "leisure_data_usage_on";
        public static final String LEISURE_DATA_USAGE_OVERLIMIT_WARNING = "leisure_data_usage_overlimit_warning";
        public static final String LEISURE_DATA_USAGE_OVERLIMIT_WARNING_TIME = "leisure_data_usage_overlimit_warning_time";
        public static final String LEISURE_DATA_USAGE_TOTAL = "leisure_data_usage_total";
        public static final String LEISURE_DATA_USAGE_TO_TIME = "leisure_data_usage_to_time";
        public static final String LEISURE_OVERLIMIT_STOP_NETWORK_TIME = "leisure_overlimit_stop_network_time";
        public static final String LOCK_SCREEN_TRAFFIC_ENABLE = "lock_screen_traffic_enable";
        public static final String LOCK_SCREEN_WARNING_LEVEL = "lock_screen_warning_level";
        public static final String MI_MOBILE_OPERATOR_MODIFY_AGAIN = "mi_mobile_operator_modify_again";
        public static final String MOBILE_POLICY_ENABLE = "mobile_policy_enable";
        public static final String MONTH_START = "month_start";
        public static final String NA_TRAFFIC_PURCHASE_ORDER_TIPS = "na_traffic_purchase_order_tips";
        public static final String NA_TRAFFIC_PURCHASE_TYPE = "na_traffic_purchase_type";
        public static final String NOT_LIMITED_CARD_PACKAGE = "not_limited_card_package";
        public static final String NOT_LIMITED_OVERLIMIT_WARNING_TIME = "not_limited_overlimit_warning_time";
        public static final String OVER_DATA_USAGE_STOP_NETWORK_TYPE = "over_data_usage_stop_network_type";
        public static final String PACKAGE_CHANGE_UPDATE_TIME = "package_change_update_time";
        public static final String PURCHASE_ACTIVITY_ID = "purchase_activity_id";
        public static final String PURCHASE_TIPS_ENBALE = "purchase_tips_enbale";
        public static final String ROAMING_BEGIN_TIME = "roaming_begin_time";
        public static final String ROAMING_DAILY_LIMIT_ENABLED = "roaming_daily_limit_enabled";
        public static final String ROAMING_DAILY_LIMIT_TRAFFIC = "roaming_daily_limit_traffic";
        public static final String ROAMING_NETWORK_STATE = "roaming_network_state";
        public static final String ROAMING_OVER_LIMIT_OPT_TYPE = "roaming_over_limit_opt_type";
        public static final String SIM_LOCATION_ALERT_IGNORE = "sim_location_alert_ignore";
        public static final String TC_SMS_REPORT_CACHE = "tc_sms_report_cache";
        public static final String TRAFFIC_CORRECTION_ALERT_IGNORE = "traffic_correction_alert_ignore";
        public static final String TRAFFIC_CORRECTION_ENGINE_UPDATE_TIME = "traffic_correction_engine_update_time";
        public static final String TRAFFIC_LAST_TC_REMAIN = "traffic_last_tc_remain";
        public static final String TRAFFIC_LAST_TC_USED = "traffic_last_tc_used";
        public static final String TRAFFIC_MANAGE_CONTROL = "traffic_manage_control";
        public static final String TRAFFIC_OVER_DAILY_LIMIT_TIME = "traffic_over_daily_limit_time";
        public static final String TRAFFIC_PROTECTED_STOP_NET_TIME = "traffic_protected_stop_net_time";
        public static final String TRAFFIC_PURCHASE_NA_AVALIABLE = "traffic_purchase_avaliable_na";
        public static final String TRAFFIC_PURCHASE_NA_AVALIABLE_UPDATE_TIME = "traffic_purchase_avaliable_update_time_na";
        public static final String TRAFFIC_PURCHASE_STATUS = "traffic_purchase_status";
        public static final String TRAFFIC_SETTING_DAILY_LIMIT_NOTIFY_UPDATE_TIME = "traffic_setting_daily_limit_notify_update_time";
        public static final String TRAFFIC_SETTING_DAILY_NOTIFY_UPDATE_TIME = "traffic_setting_daily_notify_update_time1";
        public static final String TRAFFIC_SETTING_MONTHLY_NOTIFY_UPDATE_TIME = "traffic_setting_monthly_notify_update_time1";
        public static final String TRAFFIC_SMS_DETAIL = "traffic_sms_detail";
        public static final String TRAFFIC_TC_RESULT = "traffic_tc_result";
        public static final String TRAFFIC_TC_RESULT_CODE = "traffic_tc_result_code";
        public static final String USER_BRAND = "user_brand1";
        public static final String USER_CITY = "user_city";
        public static final String USER_CUSTOMIZED_SMS = "user_customized_sms";
        public static final String USER_CUSTOMIZED_SMS_CONTENT = "user_customized_sms_content";
        public static final String USER_CUSTOMIZED_SMS_NUM = "user_customized_sms_num";
        public static final String USER_OPERATOR = "user_operator";
        public static final String USER_PROVINCE = "user_province";
        public static final String WEB_CORRECTION_STATUS_REFRESH_TIME = "web_correction_status_refresh_time";
        public static final String WEB_CORRECTION_SUPPORTED = "web_correction_supported";
        public static final String WEB_SHOULD_CORRECTION = "web_should_correction";

        private KEY() {
        }
    }

    public static final class Value {
        public static final int BRAND_DAILY_CARD = 1;
        public static final int BRAND_NORMAL = 0;
        public static final int BRAND_NOT_LIMITED = 2;
        public static final int BRAND_NOT_SET = -1;
        public static final int DAILY_LIMIT_CUSTOMIZE = 0;
        public static final int DAILY_LIMIT_TOTAL_PER_FIVE = 5;
        public static final int DAILY_LIMIT_TOTAL_PER_TEN = 10;
        public static final int DAILY_LIMIT_TOTAL_PER_THREE = 3;
        public static final int OVER_LIMIT_TYPE_DAILY = 1;
        public static final int OVER_LIMIT_TYPE_DAILY_ROAMING = 2;
        public static final int OVER_LIMIT_TYPE_DAILY_USED_CARD = 4;
        public static final int OVER_LIMIT_TYPE_LEISURE = 3;
        public static final int OVER_LIMIT_TYPE_NORMAL = 0;
        public static final int OVER_LIMIT_TYPE_PROTECTED = 5;
        public static final int OVER_LIMIT_TYPE_ROAMING = 6;
        public static final int TOTAL_DATA_USAGE_NOT_SET = -1;
        public static final int TOTAL_DATA_USAGE_NO_DATA = -2;

        private Value() {
        }
    }

    private SimUserConstants() {
    }
}
