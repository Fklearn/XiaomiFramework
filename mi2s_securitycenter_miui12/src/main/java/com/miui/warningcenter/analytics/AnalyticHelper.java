package com.miui.warningcenter.analytics;

import android.util.Log;
import com.miui.analytics.AnalyticsUtil;
import com.miui.warningcenter.mijia.MijiaUtils;
import java.util.HashMap;
import java.util.Map;

public class AnalyticHelper {
    private static final String CATEGORY_NAME = "com.miui.warningcenter";
    public static final String KEY_TOGGLE_MIJIA_WARNING = "toggle_mijia_warning";
    public static final String MAIN_ITEM_EARTHQUAKE = "main_item_earthquake";
    public static final String MAIN_ITEM_MIJIA = "main_item_mijia";
    public static final String MIJIA_ALERT_CLOSE = "mijia_alert_close";
    public static final String MIJIA_ALERT_JUMP_MIJIA = "mijia_alert_jump_mijia";
    public static final String MIJIA_ALERT_RECEIVE = "mijia_alert_receive";
    public static final String MIJIA_FIRST_REGISTER = "mijia_first_register";
    public static final String MIJIA_REGISTER = "mijia_register";
    public static final String MIJIA_REGISTER_FAILED = "mijia_register_failed";
    public static final String MIJIA_TOGGLE_CLOSE = "mijia_toggle_close";
    public static final String MIJIA_TOGGLE_OPEN = "mijia_toggle_open";
    public static final String MIJIA_UNREGISTER = "mijia_unregister";
    private static final String TRACK_KEY_MAIN_RESULT_ACTION = "main_result_action";
    private static final String TRACK_KEY_MIJIA_CLICK_ACTION = "mijia_click_action";
    private static final String TRACK_KEY_MIJIA_RESULT_ACTION = "mijia_result_action";
    private static final String TRACK_KEY_PARAMS_MODULE_CLICK = "module_click";
    private static final String TRACK_KEY_PARAMS_MODULE_SHOW = "module_show";
    public static final String WARNINGCENTER_MAIN = "warningcenter_main";
    public static final String WARNINGCENTER_MIJIA = "warningcenter_mijia";

    private static void recordCountEvent(String str, Map<String, String> map) {
        try {
            AnalyticsUtil.recordCountEvent(CATEGORY_NAME, str, map);
        } catch (Exception e) {
            Log.e("WarningCenterAnalytic", "Record count event error", e);
        }
    }

    private static void recordNumericEvent(String str, long j) {
        AnalyticsUtil.recordNumericEvent(CATEGORY_NAME, str, j);
    }

    public static void trackMainModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_MAIN_RESULT_ACTION, hashMap);
    }

    public static void trackMainModuleShow(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_SHOW, str);
        recordCountEvent(TRACK_KEY_MAIN_RESULT_ACTION, hashMap);
    }

    public static void trackMijiaModuleClick(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_CLICK, str);
        recordCountEvent(TRACK_KEY_MIJIA_CLICK_ACTION, hashMap);
    }

    public static void trackMijiaResultAction(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(TRACK_KEY_PARAMS_MODULE_SHOW, str);
        recordCountEvent(TRACK_KEY_MIJIA_RESULT_ACTION, hashMap);
    }

    public static void trackUpdateToggleStat() {
        recordNumericEvent(KEY_TOGGLE_MIJIA_WARNING, MijiaUtils.isMijiaWarningOpen() ? 1 : 0);
    }
}
