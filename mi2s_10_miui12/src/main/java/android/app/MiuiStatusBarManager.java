package android.app;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.util.Log;
import java.lang.reflect.Method;

public class MiuiStatusBarManager {
    public static final String ACTION_ENTER_DRIVE_MODE = "com.miui.app.ExtraStatusBarManager.action_enter_drive_mode";
    public static final String ACTION_ENTER_RECORDER = "com.miui.app.ExtraStatusBarManager.action_enter_recorder";
    public static final String ACTION_EXIT_FULLSCREEN = "com.miui.app.ExtraStatusBarManager.EXIT_FULLSCREEN";
    public static final String ACTION_EXPAND_NOTIFICATIONS_TAB = "com.miui.app.ExtraStatusBarManager.EXPAND_NOTIFICATIONS_TAB";
    public static final String ACTION_EXPAND_TOGGLES_TAB = "com.miui.app.ExtraStatusBarManager.EXPAND_TOGGLE_TAB";
    public static final String ACTION_LEAVE_DRIVE_MODE = "com.miui.app.ExtraStatusBarManager.action_leave_drive_mode";
    public static final String ACTION_LEAVE_RECORDER = "com.miui.app.ExtraStatusBarManager.action_leave_recorder";
    public static final String ACTION_PICK_TOGGLE_INTENT = "com.miui.app.ExtraStatusBarManager.action_PICK_TOGGLE_INTENT";
    public static final String ACTION_REFRESH_NOTIFICATION = "com.miui.app.ExtraStatusBarManager.action_refresh_notification";
    public static final String ACTION_REMOVE_KEYGUARD_NOTIFICATION = "com.miui.app.ExtraStatusBarManager.action_remove_keyguard_notification";
    public static final String ACTION_REQUEST_RESTART = "com.miui.app.ExtraStatusBarManager.REQUEST_RESTART";
    public static final String ACTION_SHOW_APP_NOTIFICATION_PANEL = "show_app_notification_panel";
    public static final String ACTION_STATUS_BAR_COLOR = "com.miui.app.ExtraStatusBarManager.action_status_bar_color";
    public static final String ACTION_STATUS_QUIET_MODE = "com.miui.app.ExtraStatusBarManager.action_status_quiet_mode";
    public static final String ACTION_STATUS_RECORDER = "com.miui.app.ExtraStatusBarManager.action_status_recorder";
    public static final String ACTION_STATUS_SAFEPAY = "com.miui.app.ExtraStatusBarManager.action_status_safepay";
    public static final String ACTION_STATUS_SOS = "com.miui.app.ExtraStatusBarManager.action_status_sos";
    public static final String ACTION_TRIGGER_CAMERA_KEY = "com.miui.app.ExtraStatusBarManager.TRIGGER_CAMERA_KEY";
    public static final String ACTION_TRIGGER_TOGGLE = "com.miui.app.ExtraStatusBarManager.action_TRIGGER_TOGGLE";
    public static final String ACTION_TRIGGER_TOGGLE_LOCK = "com.miui.app.ExtraStatusBarManager.TRIGGER_TOGGLE_LOCK";
    public static final String ACTION_TRIGGER_TOGGLE_SCREEN_BUTTONS = "com.miui.app.ExtraStatusBarManager.TRIGGER_TOGGLE_SCREEN_BUTTONS";
    public static final int DISABLE_FLAG_DARK_MODE = 64;
    public static final int DISABLE_FLAG_FLOAT_NOTIFICATION = 1024;
    public static final int DISABLE_FLAG_FOD_ICON = 2048;
    public static final int DISABLE_FLAG_FULLSCREEN = Integer.MIN_VALUE;
    public static final int DISABLE_FLAG_HIDE = 128;
    public static final int DISABLE_FLAG_HIDE_IN_FULLSCREEN = 256;
    public static final int DISABLE_FLAG_KEYGUARD_MODE = 16;
    public static final int DISABLE_FLAG_LOW_PROFILE = 2;
    public static final int DISABLE_FLAG_NAVIGATION_DARK_MODE = 512;
    public static final int DISABLE_FLAG_SIMPLE_MODE = 32;
    public static final int DISABLE_FLAG_TRANSPARENT = 1;
    public static final String EXTRA_APP_PACKAGENAME = "app_packageName";
    public static final String EXTRA_FORBID_NOTIFICATION = "com.miui.app.ExtraStatusBarManager.extra_forbid_notification";
    public static final String EXTRA_NOTIFICATION_CLICK = "com.miui.app.ExtraStatusBarManager.extra_notification_click";
    public static final String EXTRA_NOTIFICATION_KEY = "com.miui.app.ExtraStatusBarManager.extra_notification_key";
    public static final String EXTRA_RECORDER_DURATION = "com.miui.app.ExtraStatusBarManager.extra_recorder_duration";
    public static final String EXTRA_RECORDER_ENTER_SILENT_MODE = "com.miui.app.ExtraStatusBarManager.extra_recorder_enter_silent_mode";
    public static final String EXTRA_RECORDER_SILENT_MODE_CHANGED_BY_USER = "com.miui.app.ExtraStatusBarManager.extra_recorder_silent_mode_changed_by_user";
    public static final String EXTRA_RECORDER_TIMER_ON_OFF = "com.miui.app.ExtraStatusBarManager.extra_recorder_timer_on_off";
    public static final String EXTRA_RECORDER_TITLE = "com.miui.app.ExtraStatusBarManager.extra_recorder_title";
    public static final String EXTRA_TOGGLE_ID = "com.miui.app.ExtraStatusBarManager.extra_TOGGLE_ID";
    public static final int NAVIGATION_HINT_BACK_ALT = 8;
    public static final int NAVIGATION_HINT_BACK_NOP = 1;
    public static final int NAVIGATION_HINT_HOME_NOP = 2;
    public static final int NAVIGATION_HINT_RECENT_NOP = 4;
    public static final String NOTIFICATION_PANEL_ACTIVITY_NAME = "NotiPanelActivity";
    public static final String PARAM_BASE_TIME = "base_time";
    public static final String SAFEPAY_SCAN_PROGRESS = "safepay_scan_progress";
    public static final String SAFEPAY_SCAN_PROGRESS_CURRENT = "safepay_scan_progress_current";
    public static final String SAFEPAY_SCAN_PROGRESS_MAX = "safepay_scan_progress_max";
    public static final int STATUS_BAR_COLOR_RESTORE = -1;
    public static final int STATUS_BAR_QUIET_ICON_ACTIVE = 1;
    public static final int STATUS_BAR_QUIET_ICON_INACTIVE = 0;
    public static final int STATUS_BAR_RECORDER_DEAD = 3;
    public static final int STATUS_BAR_RECORDER_HIDE = 0;
    public static final int STATUS_BAR_RECORDER_SHOW = 1;
    public static final int STATUS_BAR_RECORDER_SILENCE = 2;
    public static final int STATUS_BAR_SAFEPAY_HIDE = 0;
    public static final int STATUS_BAR_SAFEPAY_RISK = 3;
    public static final int STATUS_BAR_SAFEPAY_SAFE = 2;
    public static final int STATUS_BAR_SAFEPAY_SCANNING = 1;
    public static final int STATUS_BAR_SAFEPAY_UNKNOWN = 4;
    public static final int STATUS_BAR_SAFEPAY_UPDATE_PROGRESS = 5;
    private static final String STATUS_BAR_SERVICE = "statusbar";
    public static final int STATUS_BAR_SOS_HIDE = 0;
    public static final int STATUS_BAR_SOS_SHOW = 1;
    private static final int STATUS_BAR_TRANSLUCENT = 1073741824;
    private static final String TAG = "MiuiStatusBarManager";
    public static final int TOGGLE_ACCESS_CONTROL = 16;
    public static final int TOGGLE_ADVANCED_SYNC = 19;
    public static final int TOGGLE_AUTO_BRIGHTNESS = 22;
    public static final int TOGGLE_BATTERY_SAVER = 30;
    public static final int TOGGLE_BLUETOOTH = 2;
    public static final int TOGGLE_BRIGHTNESS = 4;
    public static final int TOGGLE_CAST = 28;
    public static final int TOGGLE_COUNT = 32;
    public static final int TOGGLE_DATA = 1;
    public static final int TOGGLE_DIVIDER = 0;
    public static final int TOGGLE_DRIVE_MODE = 21;
    public static final int TOGGLE_EDIT = 29;
    public static final int TOGGLE_FLIGHT_MODE = 9;
    public static final int TOGGLE_GPS = 7;
    public static final int TOGGLE_LOCK = 10;
    public static final int TOGGLE_MIDROP = 27;
    public static final int TOGGLE_NETWORK_TYPE = 17;
    public static final int TOGGLE_NFC = 31;
    public static final int TOGGLE_PAPER_MODE = 26;
    public static final int TOGGLE_POWER_MODE = 23;
    public static final int TOGGLE_PRIVACY_MODE = 14;
    public static final int TOGGLE_QUIET_MODE = 25;
    public static final int TOGGLE_REBOOT = 12;
    public static final int TOGGLE_RINGER = 5;
    public static final int TOGGLE_ROTATE = 3;
    public static final int TOGGLE_SCREENSHOT = 18;
    public static final int TOGGLE_SCREEN_BUTTON = 20;
    public static final int TOGGLE_SHUTDOWN = 13;
    public static final int TOGGLE_SYNC = 8;
    public static final int TOGGLE_TORCH = 11;
    public static final int TOGGLE_VIBRATE = 6;
    public static final int TOGGLE_WIFI = 15;
    public static final int TOGGLE_WIFI_AP = 24;
    private static final Method sSetStatusMethod;

    public static int getDisabledFlags(int windowExtraFlags) {
        int flags = 0;
        if ((windowExtraFlags & 32) != 0) {
            flags = 0 | 128;
        }
        if ((windowExtraFlags & 16) != 0) {
            flags |= 64;
        }
        if ((windowExtraFlags & 8) != 0) {
            flags |= 32;
        }
        if ((windowExtraFlags & 64) != 0) {
            return flags | 512;
        }
        return flags;
    }

    public static int getSystemUIVisibilityFlags(int windowExtraFlags) {
        int flags = 0;
        if ((windowExtraFlags & 2) != 0) {
            flags = 0 | 1;
        }
        if ((windowExtraFlags & 1) != 0) {
            return flags | STATUS_BAR_TRANSLUCENT;
        }
        return flags;
    }

    static {
        Method method = null;
        try {
            method = Class.forName("android.app.StatusBarManager").getMethod("setStatus", new Class[]{Integer.TYPE, String.class, Bundle.class});
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        sSetStatusMethod = method;
    }

    public static void enableScreenshotNotification(Context context, boolean enable) {
        enableScreenshotNotificationForUser(context, enable, UserHandle.myUserId());
    }

    public static void enableScreenshotNotificationForUser(Context context, boolean enable, int userHandle) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "screenshot_notification_enabled", enable, userHandle);
    }

    public static boolean isScreenshotNotificationEnabled(Context context) {
        return isScreenshotNotificationEnabledForUser(context, UserHandle.myUserId());
    }

    public static boolean isScreenshotNotificationEnabledForUser(Context context, int userHandle) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "screenshot_notification_enabled", true, userHandle);
    }

    public static void setCollapseAfterClicked(Context context, boolean enable) {
        setCollapseAfterClickedForUser(context, enable, UserHandle.myUserId());
    }

    public static void setCollapseAfterClickedForUser(Context context, boolean enable, int userHandle) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_collapse_after_clicked", enable, userHandle);
    }

    public static boolean isCollapseAfterClicked(Context context) {
        return isCollapseAfterClickedForUser(context, UserHandle.myUserId());
    }

    public static boolean isCollapseAfterClickedForUser(Context context, int userHandle) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_collapse_after_clicked", false, userHandle);
    }

    public static void setExpandableUnderKeyguard(Context context, boolean enable) {
        setExpandableUnderKeyguardForUser(context, enable, UserHandle.myUserId());
    }

    public static void setExpandableUnderKeyguardForUser(Context context, boolean enable, int userHandle) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_expandable_under_keyguard", enable, userHandle);
    }

    public static boolean isExpandableUnderKeyguard(Context context) {
        return isExpandableUnderKeyguardForUser(context, UserHandle.myUserId());
    }

    public static boolean isExpandableUnderKeyguardForUser(Context context, int userHandle) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_expandable_under_keyguard", true, userHandle);
    }

    public static void setExpandableUnderFullscreen(Context context, boolean enable) {
        setExpandableUnderFullscreenForUser(context, enable, UserHandle.myUserId());
    }

    public static void setExpandableUnderFullscreenForUser(Context context, boolean enable, int userHandle) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_expandable_under_fullscreen", enable, userHandle);
    }

    public static boolean isExpandableUnderFullscreen(Context context) {
        return isExpandableUnderFullscreenForUser(context, UserHandle.myUserId());
    }

    public static boolean isExpandableUnderFullscreenForUser(Context context, int userHandle) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_expandable_under_fullscreen", true, userHandle);
    }

    public static void setShowNetworkSpeed(Context context, boolean enable) {
        setShowNetworkSpeedForUser(context, enable, UserHandle.myUserId());
    }

    public static void setShowNetworkSpeedForUser(Context context, boolean enable, int userHandle) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_show_network_speed", enable, userHandle);
    }

    public static boolean isShowNetworkSpeed(Context context) {
        return isShowNetworkSpeedForUser(context, UserHandle.myUserId());
    }

    public static boolean isShowNetworkSpeedForUser(Context context, int userHandle) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_show_network_speed", false, userHandle);
    }

    public static void setShowNotificationIcon(Context context, boolean enable) {
        setShowNotificationIconForUser(context, enable, UserHandle.myUserId());
    }

    public static void setShowNotificationIconForUser(Context context, boolean enable, int userHandle) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_show_notification_icon", enable, userHandle);
    }

    public static boolean isShowNotificationIcon(Context context) {
        return isShowNotificationIconForUser(context, UserHandle.myUserId());
    }

    public static boolean isShowNotificationIconForUser(Context context, int userHandle) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_show_notification_icon", true, userHandle);
    }

    public static void setShowFlowInfo(Context context, boolean enable) {
        setShowFlowInfoForUser(context, enable, UserHandle.myUserId());
    }

    public static void setShowFlowInfoForUser(Context context, boolean enable, int userHandle) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_show_network_assistant", enable, userHandle);
    }

    public static boolean isShowFlowInfo(Context context) {
        return isShowFlowInfoForUser(context, UserHandle.myUserId());
    }

    public static boolean isShowFlowInfoForUser(Context context, int userHandle) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_show_network_assistant", false, userHandle);
    }

    public static void setShowCarrier(Context context, boolean enable) {
        setShowCarrierForUser(context, enable, UserHandle.myUserId());
    }

    public static void setShowCarrierForUser(Context context, boolean enable, int userHandle) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_show_custom_carrier", enable, userHandle);
    }

    public static boolean isShowCarrier(Context context) {
        return isShowCarrierForUser(context, UserHandle.myUserId());
    }

    public static boolean isShowCarrierForUser(Context context, int userHandle) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_show_custom_carrier", MiuiSettings.System.getShowCustomCarrierDefault() != 0, userHandle);
    }

    public static boolean applyState(Context context, MiuiStatusBarState state) {
        if (sSetStatusMethod == null) {
            return false;
        }
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method method = sSetStatusMethod;
            method.invoke(statusBarManager, new Object[]{1, "action_set_status_bar_state." + state.getTag(context), state.toBundle(context)});
            return true;
        } catch (Exception e) {
            Log.e(TAG, "applyState exception:" + e.toString());
            return false;
        }
    }

    public static boolean clearState(Context context, MiuiStatusBarState state) {
        return clearState(context, state.toBundle(context).getString(MiuiStatusBarState.KEY_STATUS_BAR_TAG));
    }

    public static boolean clearState(Context context, String tag) {
        if (sSetStatusMethod == null) {
            return false;
        }
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Bundle bundle = new Bundle();
            bundle.putString(MiuiStatusBarState.KEY_STATUS_BAR_TAG, tag);
            bundle.putString(MiuiStatusBarState.KEY_STATUS_BAR_PACKAGE_NAME, context.getPackageName());
            sSetStatusMethod.invoke(statusBarManager, new Object[]{0, MiuiStatusBarState.ACTION_CLEAR_STATUS_BAR_STATE, bundle});
            return true;
        } catch (Exception e) {
            Log.e(TAG, "clearState exception:" + e.toString());
            return false;
        }
    }
}
