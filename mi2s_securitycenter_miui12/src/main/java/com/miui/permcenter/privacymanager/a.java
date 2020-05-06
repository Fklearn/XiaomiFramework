package com.miui.permcenter.privacymanager;

import android.text.TextUtils;
import com.miui.analytics.AnalyticsUtil;
import java.util.HashMap;
import java.util.Map;

public class a {
    public static void a(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("menuClick", str);
        a("MenuClick", (Map<String, String>) hashMap);
    }

    public static void a(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            HashMap hashMap = new HashMap();
            hashMap.put("entranceFrom", str2);
            a(str, (Map<String, String>) hashMap);
        }
    }

    private static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("BehaviorRecord", str, map);
    }
}
