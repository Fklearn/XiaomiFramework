package com.miui.appcompatibility;

import com.miui.analytics.AnalyticsUtil;
import java.util.HashMap;
import java.util.Map;

public class a {
    public static void a(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put(str, str2);
        a("incompatible_app_dialog_alert", (Map<String, String>) hashMap);
    }

    private static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("appcompatibility", str, map);
    }
}
