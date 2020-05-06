package miui.content;

import android.content.ComponentName;
import android.content.Intent;

public class ExtraIntent {
    public static final String ACTION_ACTIVATE_ALL_MI_SERVICES = "com.xiaomi.action.ACTIVATE_ALL_MI_SERVICES";
    public static final String ACTION_BIND_XIAOMI_ACCOUNT_SERVICE = "android.intent.action.BIND_XIAOMI_ACCOUNT_SERVICE";
    public static final String ACTION_DISCARD_FIND_DEVICE_TOKEN = "miui.intent.action.DISCARD_FIND_DEVICE_TOKEN";
    public static final String ACTION_LOCK_DEVICE = "miui.intent.action.LOCK_DEVICE";
    public static final String ACTION_MICLOUD_STATUS_INFO_CHANGED = "com.xiaomi.action.MICLOUD_STATUS_INFO_CHANGED";
    public static final String ACTION_MIUI_DISCLAIMER = "android.intent.action.MIUI_DISCLAIMER";
    public static final String ACTION_MIUI_LICENSE = "android.intent.action.MIUI_LICENSE";
    public static final String ACTION_NOISE = "miui.intent.action.NOISE";
    public static final String ACTION_PRIVACY_MODE_CHANGED = "android.intent.action.PRIVACY_MODE_CHANGED";
    public static final String ACTION_REQUEST_LOCATION = "miui.intent.action.REQUEST_LOCATION";
    public static final String ACTION_SHOW_APP_NOTIFICATION_PANEL = "miui.intent.action.NOTIFICATION_PANEL";
    public static final String ACTION_SIM_ACTIVATED = "com.xiaomi.action.SIM_ACTIVATED";
    public static final String ACTION_VIEW_LICENSE = "android.intent.action.VIEW_LICENSE";
    public static final String ACTION_VIEW_MIUI_LICENSE = "miui.intent.action.VIEW_LICENSE";
    public static final String ACTION_WIPE_DATA = "miui.intent.action.WIPE_DATA";
    public static final String ACTION_XIAOMI_ACCOUNT_BIND_NICKNAME = "android.settings.XIAOMI_ACCOUNT_BIND_NICKNAME";
    public static final String ACTION_XIAOMI_ACCOUNT_EXPIRED = "android.intent.account.expired";
    public static final String ACTION_XIAOMI_ACCOUNT_SETTING = "android.settings.XIAOMI_ACCOUNT_SYNC_SETTINGS";
    public static final String ACTION_XIAOMI_SYNC_ADVANCED_SETTINGS = "com.xiaomi.SYNC_ADVANCED_SETTINGS";
    public static final String ACTION_XIAOMI_USER_INFO_CHANGED = "com.xiaomi.action.XIAOMI_USER_INFO_CHANGED";
    public static final String EXTRA_ACCOUNT = "account";
    public static final String EXTRA_AUTHORITY = "authority";
    public static final String EXTRA_BIND_TYPE = "extra_bind_type";
    public static final String EXTRA_DEVICE_AUTO = "android.intent.extra.device_auto";
    public static final String EXTRA_DEVICE_CMD = "android.intent.extra.device_cmd";
    public static final String EXTRA_DEVICE_DIGEST = "android.intent.extra.device_digest";
    public static final String EXTRA_DEVICE_ID = "extra_deviceId";
    public static final String EXTRA_DEVICE_MSGID = "android.intent.extra.device_msgId";
    public static final String EXTRA_DEVICE_TIME = "android.intent.extra.device_time";
    public static final String EXTRA_DEVICE_TOKEN = "android.intent.extra.device_token";
    public static final String EXTRA_FIND_DEVICE_INTERCEPT_SMS_INTENT = "android.intent.extra.finddevice.intercept_sms_intent";
    public static final String EXTRA_FIND_DEVICE_V2_COMMAND = "android.intent.extra.finddevice.command";
    public static final String EXTRA_FIND_DEVICE_V2_COMMAND_TYPE = "android.intent.extra.finddevice.command_type";
    public static final String EXTRA_FIND_DEVICE_V2_COMMAND_TYPE_PUSH = "push";
    public static final String EXTRA_FIND_DEVICE_V2_COMMAND_TYPE_SMS = "sms";
    public static final String EXTRA_FROM_ADDRESS = "android.intent.extra.from_address";
    public static final String EXTRA_FROM_SLOT_ID = "android.intent.extra.from_slotId";
    public static final String EXTRA_FROM_SUB_ID = "android.intent.extra.from_subId";
    public static final String EXTRA_IME_PRIVACY_POLICY_URL = "extra_ime_privacy_policy_url";
    public static final String EXTRA_LICENSE_TYPE = "android.intent.extra.LICENSE_TYPE";
    public static final String EXTRA_LOCK_DEVICE_PASSWORD = "android.intent.extra.lock_password";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_MICLOUD_STATUS_INFO_WARN = "extra_micloud_status_info_warn";
    public static final String EXTRA_PWD = "extra_pwd";
    public static final String EXTRA_REG_TYPE_EMAIL = "reg_type_email";
    public static final String EXTRA_REG_TYPE_SMS = "reg_type_sms";
    public static final String EXTRA_SHOW_ON_FINDDEVICE_KEYGUARD = "extra_show_on_finddevice_keyguard";
    public static final String EXTRA_UPLOAD_OPTION = "extra_upload_opt";
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_XIAOMI_ACCOUNT = "account";
    public static final String EXTRA_XIAOMI_ACCOUNT_NAME = "user_name";
    public static final String EXTRA_XIAOMI_ACCOUNT_REG_TYPE = "reg_type";
    public static final String EXTRA_XIAOMI_ACCOUNT_USER_ID = "user_id";
    public static final String FIND_DEVICE_V2_RECEIVE_COMMAND_PERMISSION = "miui.cloud.finddevice.RECEIVE_COMMAND";
    public static final Intent INTENT_FIND_DEVICE_V2_CMD_RECEIVER = new Intent();
    public static final int LICENSE_CONTENT_PRIVACY_POLICY = 13;
    public static final int LICENSE_COPYRIGHT = 0;
    public static final int LICENSE_GAME_PRIVACY_POLICY = 15;
    public static final int LICENSE_IME_PRIVACY_POLICY = 10;
    public static final int LICENSE_INSTRUCTION = 6;
    public static final int LICENSE_INVALID = -1;
    public static final int LICENSE_LOCATION_PRIVACY_POLICY = 12;
    public static final int LICENSE_LOCK_SCREEN_PRIVACY_POLICY = 11;
    public static final int LICENSE_MIBI = 4;
    public static final int LICENSE_MICLOUD_USERAGREEMENT = 5;
    public static final int LICENSE_MIUI_PRIVACY_STATION = 18;
    public static final int LICENSE_POCO_LAUNCHER_PRIVACY_POLICY = 17;
    public static final int LICENSE_POCO_LAUNCHER_USER_AGREEMENT = 16;
    public static final int LICENSE_POCO_PRIVACY_POLICY = 9;
    public static final int LICENSE_PRIVACY_POLICY = 1;
    public static final int LICENSE_SAR = 7;
    public static final int LICENSE_SECURITY_PRIVACY_POLICY = 14;
    public static final int LICENSE_USER_AGREEMENT = 2;
    public static final int LICENSE_USER_MANUAL = 3;
    public static final String SYNC_SETTINGS_ACTION_APPENDER = ".SYNC_SETTINGS";
    public static final int UPLOAD_OPTION_DISABLED = 2;
    public static final int UPLOAD_OPTION_ENABLED = 1;
    public static final int UPLOAD_OPTION_UNSPECIFIED = 3;
    public static final int USER_EXPERIENCE_PROGRAM = 8;
    public static final String XIAOMI_ACCOUNT_TYPE = "com.xiaomi";
    public static final String XIAOMI_ACCOUNT_TYPE_UNACTIVATED = "com.xiaomi.unactivated";
    public static final String XIAOMI_KEY_ACCOUNT_TYPE = "com.miui.auth";
    public static final String XIAOMI_KEY_AUTHTOKEN = "token";
    public static final String XIAOMI_KEY_SERVICE_URL = "service_url";

    protected ExtraIntent() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    static {
        INTENT_FIND_DEVICE_V2_CMD_RECEIVER.setComponent(new ComponentName("com.xiaomi.finddevice", "com.xiaomi.finddevice.v2.command.CommandReceiver"));
    }
}
