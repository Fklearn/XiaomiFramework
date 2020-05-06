package com.miui.networkassistant.config;

import com.miui.networkassistant.utils.DeviceUtil;

public final class Constants {

    public static final class App {
        public static final String ACTION_BROADCAST_ALLOW_APP_FIREWALL = "action_broadcast_allow_app_firewall";
        public static final String ACTION_BROADCAST_CANCEL_NOTIFICATION = "action_broadcast_cancel_notification";
        public static final String ACTION_BROADCAST_TC_SMS_REPORT_STATUS = "action_broadcast_tc_sms_report_status";
        public static final String ACTION_DATA_USAGE_AUTO_CORRECTION = "com.miui.action.DATA_USAGE_AUTO_CORRECTION";
        public static final String ACTION_NETWORK_ASSISTANT_APP_DETAIL = "miui.intent.action.NETWORKASSISTANT_APP_DETAIL";
        public static final String ACTION_NETWORK_ASSISTANT_AUTO_TRAFFIC_CORRECTION_SETTING = "miui.intent.action.NETWORKASSISTANT_AUTO_TRAFFIC_CORRECTION_SETTING";
        public static final String ACTION_NETWORK_ASSISTANT_BG_NETWORK = "miui.intent.action.BG_NETWORK_CONTROL";
        public static final String ACTION_NETWORK_ASSISTANT_ENTRANCE = "miui.intent.action.NETWORKASSISTANT_ENTRANCE";
        public static final String ACTION_NETWORK_ASSISTANT_LIMIT_SETTING = "miui.intent.action.NETWORKASSISTANT_LIMIT_SETTING";
        public static final String ACTION_NETWORK_ASSISTANT_MONTH_PACKAGE_SETTING = "miui.intent.action.NETWORKASSISTANT_MONTH_PACKAGE_SETTING";
        public static final String ACTION_NETWORK_ASSISTANT_OPERATOR_SETTING = "miui.intent.action.NETWORKASSISTANT_OPERATOR_SETTING";
        public static final String ACTION_NETWORK_ASSISTANT_SETTING = "miui.intent.action.NETWORKASSISTANT_SETTINGS";
        public static final String ACTION_NETWORK_ASSISTANT_SMS_REPORT = "miui.intent.action.NETWORKASSISTANT_SMS_REPORT";
        public static final String ACTION_NETWORK_ASSISTANT_STATUS_BAR_SETTING = "miui.intent.action.NETWORKASSISTANT_STATUS_BAR_SETTING";
        public static final String ACTION_NETWORK_ASSISTANT_TC_DIAGNOSTIC = "miui.intent.action.NETWORKASSISTANT_TC_DIAGNOSTIC";
        public static final String ACTION_NETWORK_ASSISTANT_TRAFFIC_PURCHASE = "miui.intent.action.NETWORKASSISTANT_TRAFFIC_PURCHASE";
        public static final String ACTION_NETWORK_POLICY_UPDATE = "com.miui.action.NETWORK_POLICY_UPDATE";
        public static final String ACTION_PURCHASE_SUCCESS = "miui.intent.action.PURCHASE_SUCCESS";
        public static final String ACTION_REFRESH_DATA_USAGE_DAILY = "com.miui.action.REFRESH_DATA_USAGE_DAILY";
        public static final String ACTION_VIEW_DATA_USAGE = "miui.intent.action.NETWORKASSISTANT_ENTRANCE";
        public static final String PERMISSION_EXTRA_NETWORK = "miui.permission.EXTRA_NETWORK";

        private App() {
        }
    }

    public static final class Default {
        public static final String MANAGED_PROFILE_PACKAGE_SPLIT = "&@";
        public static final String PACKAGE_MANAGED_PROFILE = "magaged_profile_package";
        public static final int STATUS_BAR_SHOW_NETWORK_ASSISTANT_DEFAULT = 0;
        public static final int STATUS_BAR_SHOW_NETWORK_SPEED_DEFAULT = 0;
        public static final String XSPACE_PACKAGE_SPLIT = "&#";

        private Default() {
        }
    }

    public static final class External {
        public static final String MISIM_MAIN_URL = "misim://router?launchfrom=netasistant";

        private External() {
        }
    }

    public static final class System {
        public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
        public static final String ACTION_CALL_PRIVILEGED = "android.intent.action.CALL_PRIVILEGED";
        public static final String ACTION_DEFAULT_DATA_SLOT_CHANGED = "miui.intent.action.ACTION_DEFAULT_DATA_SLOT_CHANGED";
        public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
        public static final String ACTION_LOCALE_CHANGED = "android.intent.action.LOCALE_CHANGED";
        public static final String ACTION_NETWORK_BLOCKED = "miui.intent.action.NETWORK_BLOCKED";
        public static final String ACTION_NETWORK_CONNECTED = "miui.intent.action.NETWORK_CONNECTED";
        public static final String ACTION_NETWORK_STATS_UPDATED = "com.android.server.action.NETWORK_STATS_UPDATED";
        public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
        public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
        public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
        public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
        public static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
        public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
        public static final String ACTION_TETHER_STATE_CHANGED = "android.net.conn.TETHER_STATE_CHANGED";
        public static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT";
        public static final String ACTION_VIEW_SCAN_BARCODE = "android.intent.action.scanbarcode";
        public static final String ACTION_VIEW_SCAN_BARCODE_NEW = "miui.intent.action.scanbarcode";
        public static final String ANDROID_PACKAGE_NAME = "android";
        public static final String CATEGORY_DEFALUT = "android.intent.category.DEFAULT";
        public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
        public static final String CONNECTIVITY_ACTION_IMMEDIATE = (DeviceUtil.IS_L_OR_LATER ? "android.net.conn.CONNECTIVITY_CHANGE" : "android.net.conn.CONNECTIVITY_CHANGE_IMMEDIATE");
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        public static final String EXTRA_MIUI_STARTING_WINDOW_LABEL = ":miui:starting_window_label";
        public static final String EXTRA_SETTINGS_TITLE = "extra_settings_title";
        public static final String EXTRA_USER_HANDLE = "android.intent.extra.user_handle";
        public static final String MI_STATS_PACKAGE_NAM = "com.xiaomi.mistatistic";
        public static final String MOBILE_POLICY = "mobile_policy";
        public static final String PACKAGE_NAME_PHONE = (DeviceUtil.IS_L_OR_LATER ? "com.android.server.telecom" : "com.android.phone");
        public static final String PERMISSION_BROADCAST_SMS = "android.permission.BROADCAST_SMS";
        public static final String PERMISSION_READ_NETWORK_USAGE_HISTORY = "android.permission.READ_NETWORK_USAGE_HISTORY";
        public static final String SMS_RECEIVER_ACTION = "sms_receiver_action";
        public static final String STATUS_BAR_SHOW_NETWORK_ASSISTANT = "status_bar_show_network_assistant";
        public static final String STATUS_BAR_SHOW_NETWORK_SPEED = "status_bar_show_network_speed";
        public static final String UPLOAD_LOG = "upload_log_pref";
        public static final String XMSF_PACKAGE_NAM = "com.xiaomi.xmsf";

        private System() {
        }
    }

    private Constants() {
    }
}
