package miui.cloud;

@Deprecated
public class Constants {
    public static final String CLOUDSERVICE_PACKAGE_NAME = "com.miui.cloudservice";
    public static final boolean ENABLE_ANALYTICS = true;
    public static final int SYNC_WITHOUT_ACTIVATE_SIM_INDEX = -1;
    public static final String XIAOMI_ACCOUNT_TYPE = "com.xiaomi";
    public static final String XMSF_PACKAGE_NAME = "com.xiaomi.xmsf";

    @Deprecated
    public static class Analytics {
        public static final String ACTION = "com.xiaomi.action.MICLOUD_STAT";
        public static final String EVENT_ID_MICLOUD_ACTIVATE_SOURCE = "event_id_micloud_activate_source";
        public static final String EVENT_ID_MICLOUD_LOGIN_ENABLE = "event_id_micloud_login_enable";
        public static final String EVENT_KEY_MICLOUD_ACTIVATE_AUTHORITY = "event_key_micloud_activate_authority";
        public static final String EVENT_KEY_MICLOUD_ACTIVATE_INIT = "event_key_micloud_activate_init";
        public static final String EVENT_KEY_MICLOUD_ACTIVATE_PACKAGE_NAME = "event_key_micloud_activate_package_name";
        public static final String EVENT_KEY_MICLOUD_ACTIVATE_RESULT = "event_key_micloud_activate_result";
        public static final String EVENT_KEY_MICLOUD_ACTIVATE_SOURCE = "event_key_micloud_activate_source";
        public static final String EVENT_KEY_MICLOUD_FIND_DEVICE_ENABLE = "event_key_micloud_find_device_enable";
        public static final String EVENT_KEY_MICLOUD_MX_ENABLE = "event_key_micloud_mx_enable";
        public static final String EVENT_KEY_MICLOUD_SETUP_GUIDE = "event_key_micloud_setup_guide";
        public static final String EVENT_KEY_MICLOUD_SYNC_ENABLE = "event_key_micloud_sync_enable";
        public static final String EVENT_VALUE_MICLOUD_ACTIVATE_SOURCE_ACCOUNT = "event_value_micloud_activate_source_account";
        public static final String EVENT_VALUE_MICLOUD_ACTIVATE_SOURCE_ALERT = "event_value_micloud_activate_source_alert";
        public static final String EVENT_VALUE_MICLOUD_ACTIVATE_SOURCE_ALERT_APP = "event_value_micloud_activate_source_alert_app";
        public static final String EVENT_VALUE_MICLOUD_ACTIVATE_SOURCE_CALENDAR = "event_value_micloud_activate_source_calendar";
        public static final String EVENT_VALUE_MICLOUD_ACTIVATE_SOURCE_CHECKBOX = "event_value_micloud_activate_source_checkbox";
        public static final String EVENT_VALUE_MICLOUD_ACTIVATE_SOURCE_GALLERY = "event_value_micloud_activate_source_gallery";
        public static final String EVENT_VALUE_MICLOUD_ACTIVATE_SOURCE_NOTES = "event_value_micloud_activate_source_notes";
        public static final String EXTRA_ANALYTICS_EVENT_ID = "extra_analytics_event_id";
        public static final String EXTRA_ANALYTICS_EVENT_PARAMETERS = "extra_analytics_event_parameters";
    }

    @Deprecated
    public static class Intents {
        public static final String ACTION_FIND_DEVICE_GUIDE = "com.xiaomi.action.MICLOUD_FIND_DEVICE_GUIDE";
        public static final String ACTION_MICLOUD_INFO_SETTINGS = "com.xiaomi.action.MICLOUD_INFO_SETTINGS";
        public static final String ACTION_UPLOAD_PHONE_LIST = "com.miui.cloudservice.mms.UPLOAD_PHONE_LIST";
        public static final String ACTION_VIEW_CLOUD = "com.xiaomi.action.MICLOUD_MAIN";
        public static final String ACTION_WARN_INVALID_DEVICE_ID = "com.xiaomi.action.WARN_INVALID_DEVICE_ID";
        public static final String EXTRA_DEVICE_ID = "device_id";
    }

    @Deprecated
    public static class UserData {
        public static final String EXTRA_MICLOUD_STATUS_INFO_QUOTA = "extra_micloud_status_info_quota";
        public static final String EXTRA_MICLOUD_VIP_AVAILIABLE = "extra_micloud_vip_availiable";
        public static final String KEY_FIND_DEVICE_ENABLED = "extra_find_device_enabled";
        public static final String KEY_FIND_DEVICE_TOKEN = "extra_find_my_device_token";
    }
}
