package android.content;

public class SystemIntent {
    @Deprecated
    public static final String ACTION_ADD_FIREWALL = "android.intent.action.ADD_FIREWALL";
    public static final String ACTION_APPLICATION_PROGRESS_QUERY = "miui.intent.action.APPLICATION_PROGRESS_QUERY";
    public static final String ACTION_APPLICATION_PROGRESS_UPDATE = "miui.intent.action.APPLICATION_PROGRESS_UPDATE";
    public static final String ACTION_BIND_WIDGET_COMPLETED = "miui.intent.action.BIND_WIDGET_COMPLETED";
    public static final String ACTION_ENTER_INCALL_SCREEN_DURING_CALL = "android.intent.action.ENTER_INCALL_SCREEN_DURING_CALL";
    public static final String ACTION_GARBAGE_DEEPCLEAN = "miui.intent.action.GARBAGE_DEEPCLEAN";
    public static final String ACTION_HEADSET_SETTINGS = "miui.intent.action.HEADSET_SETTINGS";
    public static final String ACTION_IMPORT_SINA_WEIBO = "miui.intent.action.ACTION_IMPORT_SINA_WEIBO";
    public static final String ACTION_IMPORT_SINA_WEIBO_CANCELED = "miui.intent.action.ACTION_IMPORT_SINA_WEIBO_CANCELED";
    public static final String ACTION_INPUT_METHOD_VISIBLE_HEIGHT_CHANGED = "miui.intent.action.INPUT_METHOD_VISIBLE_HEIGHT_CHANGED";
    public static final String ACTION_INSTALL_WIDGET = "com.miui.home.launcher.action.INSTALL_WIDGET";
    public static final String ACTION_KEYCODE_MUTE = "android.intent.action.KEYCODE_MUTE";
    public static final String ACTION_KEYCODE_POWER_UP = "android.intent.action.KEYCODE_POWER_UP";
    public static final String ACTION_LEAVE_INCALL_SCREEN_DURING_CALL = "android.intent.action.LEAVE_INCALL_SCREEN_DURING_CALL";
    public static final String ACTION_LOCKWALLPAPER_PROVIDER = "miui.intent.action.LOCKWALLPAPER_PROVIDER";
    public static final String ACTION_MARK_ANTISPAM = "miui.intent.action.MARK_ANTISPAM";
    public static final String ACTION_MEDIA_SCANNER_SCAN_FOLDER = "miui.intent.action.MEDIA_SCANNER_SCAN_FOLDER";
    public static final String ACTION_MIUI_REGION_CHANGED = "miui.intent.action.MIUI_REGION_CHANGED";
    public static final String ACTION_PICK_SAFE_GUARD = "miui.intent.action.safeguard";
    public static final String ACTION_SAVE_TO_CLOUD_GALLERY = "com.miui.gallery.SAVE_TO_CLOUD";
    public static final String ACTION_SET_KEYGUARD_WALLPAPER = "com.miui.keyguard.setwallpaper";
    public static final String ACTION_SET_LOCKSCREEN_WALLPAPER = "android.intent.action.SET_LOCKSCREEN_WALLPAPER";
    public static final String ACTION_SYSTEMUI_TASK_MANAGER_CLEAR = "com.android.systemui.taskmanager.Clear";
    public static final String ACTION_THEME_SERVICE = "com.miui.service.THEMESERVICE";
    @Deprecated
    public static final String ACTION_THEME_SERVICE_DEPRECATED = "com.miui.service.THEME";
    public static final String ACTION_WALLPAPER_PROGRESS_QUERY = "android.intent.action.WALLPAPER_PROGRESS_QUERY";
    public static final String ACTION_WALLPAPER_PROGRESS_UPDATE = "android.intent.action.WALLPAPER_PROGRESS_UPDATE";
    @Deprecated
    public static final String ACTIVATE_SERVICE_HOST_PACKAGE = "com.xiaomi.xmsf";
    public static final String ACTIVATE_SERVICE_HOST_PACKAGE_NEW = "com.xiaomi.simactivate.service";
    public static final String EXTRA_BASE_TIME = "base_time";
    public static final String EXTRA_BIND_WIDGET_RESULT = "miui.intent.extra.bind_widget_result";
    public static final String EXTRA_CALL_STATE = "call_state";
    public static final String EXTRA_CUSTOMIZED_ICON_SHORTCUT = "android.intent.extra.CUSTOMIZED_ICON_SHORTCUT";
    public static final String EXTRA_FILE_PATH = "extra_file_path";
    public static final String EXTRA_IMPORTED_WEIBO_COUNT = "miui.intent.extra.EXTRA_IMPORTED_WEIBO_COUNT";
    public static final String EXTRA_INCLUDE_UNKNOWN_NUMBERS = "android.intent.extra.include_unknown_numbers";
    public static final String EXTRA_INITIAL_PICKER_TAB = "android.intent.extra.initial_picker_tab";
    public static final String EXTRA_INPUT_METHOD_VISIBLE_HEIGHT = "miui.intent.extra.input_method_visible_height";
    public static final String EXTRA_LOCATE_APP = "locate_app";
    public static final String EXTRA_PROVIDER_COMPONENT_NAME = "miui.intent.extra.provider_component_name";
    public static final String EXTRA_RESTART_WEIBO_ACTIVITY = "miui.intent.extra.EXTRA_RESTART_WEIBO_ACTIVITY";
    public static final String EXTRA_RESULT_RECEIVER_COMPONENT_NAME = "miui.intent.extra.result_receiver_component_name";
    public static final String EXTRA_SINA_WEIBO_ACCESS_TOKEN = "miui.intent.action.EXTRA_SINA_WEIBO_ACCESS_TOKEN";
    public static final String EXTRA_SINA_WEIBO_BINDED_ID = "miui.intent.extra.EXTRA_SINA_WEIBO_BINDED_ID";
    public static final String EXTRA_SINA_WEIBO_CODE = "miui.intent.extra.EXTRA_SINA_WEIBO_CODE";
    public static final String EXTRA_UPDATE_APPLICATION_PROGRESS_ICON_URI = "android.intent.extra.update_application_progress_icon_uri";
    public static final String EXTRA_UPDATE_APPLICATION_PROGRESS_TITLE = "android.intent.extra.update_application_progress_title";
    public static final String EXTRA_UPDATE_PROGRESS_CHECK_CODE = "android.intent.extra.update_progress_check_code";
    public static final String EXTRA_UPDATE_PROGRESS_KEY = "android.intent.extra.update_progress_key";
    public static final String EXTRA_UPDATE_PROGRESS_STATUS = "android.intent.extra.update_progress_status";
    public static final String EXTRA_UPDATE_PROGRESS_STATUS_TITLE_MAP = "android.intent.extra.update_progress_status_title_map";
    public static final String EXTRA_YELLOWPAGE_NUMBER = "com.miui.yellowpage.extra.number";
    public static final int WEIBO_CODE_FAIL_IMPORT_CANCELED = -5;
    public static final int WEIBO_CODE_FAIL_NO_MATCHED_CONTACTS = -2;
    public static final int WEIBO_CODE_FAIL_NO_NETWORK = -3;
    public static final int WEIBO_CODE_FAIL_NO_PHONE_NUMBERS = -1;
    public static final int WEIBO_CODE_FAIL_UNKNOWN_INTERRUPTION = -6;
    public static final int WEIBO_CODE_FAIL_UNSTABLE_NETWORK = -4;
    public static final int WEIBO_CODE_SUCCESS = 0;
}
