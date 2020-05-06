package com.miui.networkassistant.config;

import com.miui.networkassistant.model.FirewallRule;

public final class CommonPerConstants {

    public static final class DEFAULT {
        public static final boolean CMCC_WEB_CORRECT_AVAILABLE_DEFAULT = true;
        public static final long COMMON_ANALYTICS_UPDATE_TIME_DEFAULT = 0;
        public static final long DATA_USAGE_DAILY_ANALYTICS_UPDATE_TIME_DEFAULT = Long.MAX_VALUE;
        public static final int FIREWALL_MOBILE_PRECONFIG_DEFAULT = FirewallRule.Allow.value();
        public static final boolean FIREWALL_ROAMING_WHITELIST_PRECONFIG_DEFAULT = false;
        public static final int FIREWALL_WIFI_PRECONFIG_DEFAULT = FirewallRule.Allow.value();
        public static final boolean FIRST_ENTER_TRAFFIC_PURCHASE_DECLARE_DEFAULT = true;
        public static final boolean FLOAT_NOTIFICATION_ENABLED_DEFAULT = false;
        public static final int LOCK_SCREEN_TRAFFIC_GUIDE_NOTIFY_COUNT_DEFAULT = 0;
        public static final boolean LOCK_SCREEN_TRAFFIC_MONITOR_DEFAULT = false;
        public static final boolean LOCK_SCREEN_TRAFFIC_OPENED_DEFAULT = false;
        public static final int MIUI_VPN_STATE_DEFAULT = 0;
        public static final String MI_SIM_CLOUD_DATA_DEFAULT = "";
        public static final boolean NETWORK_DIAGNOSTICS_FLOAT_NOTIFICATION_ENABLED_DEFAULT = true;
        public static final long NETWORK_EXCEPTION_UPDATE_TIME_DEFAULT = 0;
        public static final boolean NO_MORE_ASK_ROAMING_DEFAULT = false;
        public static final String PURCHASE_SMS_NUMBER_DEFAULT = "{\"maxVersion\":1,\"total\":14,\"items\":[{\"data\":\"106555062\"},{\"data\":\"10086\"},{\"data\":\"106581784\"},{\"data\":\"106903780000\"},{\"data\":\"106555604\"},{\"data\":\"10655123\"},{\"data\":\"10659800\"},{\"data\":\"106555064\"},{\"data\":\"10690529\"},{\"data\":\"10690570\"},{\"data\":\"10690233\"},{\"data\":\"10690030\"},{\"data\":\"10690689390721\"},{\"data\":\"10690094613533\"}],\"code\":0}";
        public static final long PURCHASE_SMS_NUMBER_UPDATE_TIME_DEFAULT = 0;
        public static final boolean ROAMING_APP_WHITE_LIST_INIT_DEFAULT = true;
        public static final boolean ROAMING_WHITE_LIST_NOTIFY_ENABLE_DEFAULT = true;
        public static final long SMS_NUMBER_RECEIVER_UPDATE_TIME_DEFAULT = 0;
        public static final boolean STATUS_BAR_SHOW_TRAFFIC_DEFAULT = false;
        public static final boolean TETHERING_DATA_USAGE_OVER_LIMIT_DEFAULT = false;
        public static final boolean TETHERING_LIMIT_ENABLED_DEFAULT = false;
        public static final long TETHERING_LIMIT_TRAFFIC_DEFAULT = 52428800;
        public static final int TETHERING_OVER_LIMIT_OPT_TYPE_DEFAULT = 0;
        public static final long TURN_ON_TIME_DEFAULT = 0;
        public static final long UPLOAD_MONTH_REPORT_UPDATE_TIME_DEFAULT = 0;

        private DEFAULT() {
        }
    }

    public static final class KEY {
        public static final String CMCC_WEB_CORRECT_AVAILABLE = "cmcc_web_correct_available";
        public static final String COMMON_ANALYTICS_UPDATE_TIME = "common_analytics_update_time";
        public static final String DATA_USAGE_DAILY_ANALYTICS_UPDATE_TIME = "data_usage_daily_analytics_update_time";
        public static final String FIREWALL_MOBILE_PRECONFIG = "firewall_mobile_preconfig";
        public static final String FIREWALL_ROAMING_WHITELIST_PRECONFIG = "firewall_roaming_whitelist_preconfig";
        public static final String FIREWALL_WIFI_PRECONFIG = "firewall_wifi_preconfig";
        public static final String FIRST_ENTER_TRAFFIC_PURCHASE_DECLARE = "first_enter_traffic_purchase_declare";
        public static final String FLOAT_NOTIFICATION_ENABLED = "float_notification_enabled";
        public static final String LOCK_SCREEN_TRAFFIC_GUIDE_NOTIFY_COUNT = "lock_screen_traffic_guide_notify_count";
        public static final String LOCK_SCREEN_TRAFFIC_MONITOR = "lock_screen_traffic_monitor";
        public static final String LOCK_SCREEN_TRAFFIC_OPENED = "lock_screen_traffic_opened";
        public static final String MIUI_VPN_INFOS = "miui_vpn_infos";
        public static final String MIUI_VPN_STATE = "miui_vpn_state";
        public static final String MIUI_VPN_USERID = "miui_vpn_userid";
        public static final String MI_SIM_CLOUD_DATA = "mi_sim_cloud_data";
        public static final String MOBILE_DAILY_TURN_ON_TIME = "mobile_daily_turn_on_time";
        public static final String NETWORK_DIAGNOSTICS_FLOAT_NOTIFICATION_ENABLED = "netowrk_diagnostics_float_notification_enabled";
        public static final String NETWORK_EXCEPTION_UPDATE_TIME = "network_exception_update_time";
        public static final String NO_MORE_ASK_ROAMING = "no_more_ask_roaming";
        public static final String PURCHASE_SMS_NUMBER = "purchase_sms_number";
        public static final String PURCHASE_SMS_NUMBER_UPDATE_TIME = "purchase_sms_number_update_time";
        public static final String ROAMING_APP_WHITE_LIST_INIT = "roaming_app_white_list_init";
        public static final String ROAMING_WHITE_LIST_NOTIFY_ENABLE = "roaming_white_list_notify_enable";
        public static final String SMS_NUMBER_RECEIVER_UPDATE_TIME = "sms_number_receiver_update_time";
        public static final String STATUS_BAR_SHOW_TRAFFIC = "status_bar_show_traffic";
        public static final String TETHERING_DATA_USAGE_OVER_LIMIT = "tethering_data_usage_over_limit";
        public static final String TETHERING_LIMIT_ENABLED = "tethering_limit_enabled";
        public static final String TETHERING_LIMIT_TRAFFIC = "tethering_limit_traffic";
        public static final String TETHERING_OVER_LIMIT_OPT_TYPE = "tethering_over_limit_opt_type";
        public static final String UPLOAD_MONTH_REPORT_UPDATE_TIME = "upload_month_report_update_time";
        public static final String WIFI_DAILY_TURN_ON_TIME = "wifi_daily_turn_on_time";

        private KEY() {
        }
    }

    private CommonPerConstants() {
    }
}
