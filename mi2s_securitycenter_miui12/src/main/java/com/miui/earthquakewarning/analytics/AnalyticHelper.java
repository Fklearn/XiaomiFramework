package com.miui.earthquakewarning.analytics;

import android.util.Log;
import com.miui.analytics.AnalyticsUtil;
import com.miui.earthquakewarning.utils.Utils;
import java.util.HashMap;
import java.util.Map;

public class AnalyticHelper {
    public static final String ALERT_CALL = "alert_call";
    public static final String ALERT_CLOSE = "alert_close";
    public static final String ALERT_EMERGENCY = "alert_emergency";
    public static final String ALERT_SAFE_PLACE = "alert_safe_place";
    public static final String ALERT_SHOW = "alert_show";
    static final String CATEGORY_NAME = "com.miui.earthquakewarning";
    public static final String GUIDE_CLICK_BACK = "guide_click_back";
    public static final String GUIDE_CLICK_DISAGREE = "guide_click_disagree";
    public static final String GUIDE_CLICK_LISTEN = "guide_click_listen";
    public static final String GUIDE_CLICK_NEXT = "guide_click_next";
    public static final String KEY_TOGGLE_EARTHQUAKE_WARNING = "toggle_earthquake_warning";
    public static final String MAIN_ADD_CONTACT = "main_add_contact";
    public static final String MAIN_EMERGENCY = "main_emergency";
    public static final String MAIN_GUIDE = "main_guide";
    public static final String MAIN_SAFE_PLACE = "main_safe_place";
    public static final String MAIN_SWITCH_OFF = "main_switch_off";
    public static final String MAIN_SWITCH_ON = "main_switch_on";
    public static final String PUSH_ERROR_ILLGAL_TYPE = "push_error_illgal_type";
    public static final String PUSH_ERROR_INTENSITY_LOW = "push_error_intensity_low";
    public static final String PUSH_ERROR_LOCATION_FAILED = "push_error_location_failed";
    public static final String PUSH_ERROR_NOT_OPEN = "push_error_not_open";
    public static final String PUSH_ERROR_NO_SIGN_AREA = "push_error_no_sign_area";
    public static final String PUSH_ERROR_PARSE_SIGNATURE = "push_error_parse_signature";
    public static final String PUSH_ERROR_TIME_LONG = "push_error_time_long";
    public static final String PUSH_RECEIVE = "push_receive";
    static final String TRACK_KEY_ALERT_RESULT_ACTION = "alert_result_action";
    static final String TRACK_KEY_ALERT_TIMES_ACTION = "alert_result_times_action";
    static final String TRACK_KEY_GUIDE_1_RESULT_ACTION = "guide1_result_action";
    static final String TRACK_KEY_GUIDE_2_RESULT_ACTION = "guide2_result_action";
    static final String TRACK_KEY_GUIDE_3_RESULT_ACTION = "guide3_result_action";
    static final String TRACK_KEY_GUIDE_4_RESULT_ACTION = "guide4_result_action";
    static final String TRACK_KEY_MAIN_RESULT_ACTION = "main_result_action";
    static final String TRACK_KEY_PARAMS_MODULE_CLICK = "module_click";
    static final String TRACK_KEY_PUSH_ACTION = "push_action";

    private static void recordCountEvent(String str, Map<String, String> map) {
        try {
            AnalyticsUtil.recordCountEvent(CATEGORY_NAME, str, map);
        } catch (Exception e) {
            Log.e("EWAnalyticHelper", "trackWarningTime", e);
        }
    }

    private static void recordNumericEvent(String str, long j) {
        AnalyticsUtil.recordNumericEvent(CATEGORY_NAME, str, j);
    }

    public static void trackAlertResultActionModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_ALERT_RESULT_ACTION, hashMap);
    }

    public static void trackGuide1ActionModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_GUIDE_1_RESULT_ACTION, hashMap);
    }

    public static void trackGuide2ActionModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_GUIDE_2_RESULT_ACTION, hashMap);
    }

    public static void trackGuide3ActionModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_GUIDE_3_RESULT_ACTION, hashMap);
    }

    public static void trackGuide4ActionModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_GUIDE_4_RESULT_ACTION, hashMap);
    }

    public static void trackMainResultActionModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_MAIN_RESULT_ACTION, hashMap);
    }

    public static void trackPushActionModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_PUSH_ACTION, hashMap);
    }

    public static void trackUpdateToggleStat() {
        recordNumericEvent(KEY_TOGGLE_EARTHQUAKE_WARNING, Utils.isEarthquakeWarningOpen() ? 1 : 0);
    }

    public static void trackWarningTime(int i) {
        try {
            AnalyticsUtil.recordCalculateEvent(CATEGORY_NAME, TRACK_KEY_ALERT_TIMES_ACTION, (long) i, (Map<String, String>) null);
        } catch (Exception e) {
            Log.e("EWAnalyticHelper", "trackWarningTime", e);
        }
    }
}
