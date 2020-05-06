package com.miui.optimizemanage.a;

import com.miui.analytics.AnalyticsUtil;
import com.miui.optimizemanage.settings.c;
import com.miui.powercenter.y;
import java.util.HashMap;
import java.util.Map;

public class a {
    public static void a() {
        HashMap hashMap = new HashMap();
        hashMap.put("slidedown", "");
        a("speedboost_results", (Map<String, String>) hashMap);
    }

    public static void a(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("enter_way", str);
        a("speedboost_enter_way", (Map<String, String>) hashMap);
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordCalculateEvent("optimizemanage", str, j, (Map<String, String>) null);
    }

    private static void a(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("optimizemanage", str, str2);
    }

    private static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("optimizemanage", str, map);
    }

    public static void b() {
        a("toggle_lock_apps_num", (long) c.f());
        a("toggle_clean_ram_lockscreen", c());
        a("toggle_optimize_ram_noti", d());
        b("toggle_optimize_cpu_noti", c.i() ? 1 : 0);
    }

    public static void b(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("module_click", str);
        a("speedboost_results", (Map<String, String>) hashMap);
    }

    private static void b(String str, long j) {
        AnalyticsUtil.recordNumericEvent("optimizemanage", str, j);
    }

    private static String c() {
        long l = (long) y.l();
        if (l == 0) {
            return "never";
        }
        return l + "min";
    }

    public static void c(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("module_show", str);
        a("speedboost_results", (Map<String, String>) hashMap);
    }

    private static String d() {
        int g = c.g();
        if (g == 0) {
            return "never";
        }
        return g + "%";
    }

    public static void d(String str) {
        e(str);
    }

    private static void e(String str) {
        AnalyticsUtil.recordCountEvent("optimizemanage", str, (Map<String, String>) null);
    }
}
