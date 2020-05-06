package com.miui.appmanager.a;

import android.content.Context;
import b.b.c.j.x;
import com.miui.analytics.AnalyticsUtil;
import com.miui.appmanager.AppManageUtils;
import com.miui.appmanager.i;
import com.xiaomi.stat.MiStat;
import java.util.HashMap;
import java.util.Map;

public class a {
    public static void a(Context context) {
        if (x.h(context, "com.miui.thirdappassistant")) {
            AnalyticsUtil.trackEvent("event_settings_switch_state", "group_settings", "switch_state", new i(context).c() ? 1 : 0);
        }
    }

    public static void a(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("enter_way", str);
        a("enter_am_homepage_way", (Map<String, String>) hashMap);
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordNumericEvent("appmanager", str, j);
    }

    public static void a(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put(str, str2);
        a("app_manager_ad", (Map<String, String>) hashMap);
    }

    private static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("appmanager", str, map);
    }

    public static void b(Context context) {
        AnalyticsUtil.trackEvent("default_browser", "packagename", AppManageUtils.a(context));
    }

    public static void b(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("module_click", str);
        a("application_manage_click", (Map<String, String>) hashMap);
    }

    public static void b(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put(str, str2);
        a("app_manager_uninstall", (Map<String, String>) hashMap);
    }

    public static void c(Context context) {
        a("toggle_safe_keyboard", AppManageUtils.b(context) ? 1 : 0);
    }

    public static void c(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("enter_way", str);
        a("enter_apps_details_page_way", (Map<String, String>) hashMap);
    }

    public static void d(Context context) {
        a("toggle_apps_update", new i(context).e() ? 1 : 0);
    }

    public static void d(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put(MiStat.Event.CLICK, str);
        a("apps_details_page_click", (Map<String, String>) hashMap);
    }

    public static void e(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("module_click", str);
        a("select_order", (Map<String, String>) hashMap);
    }

    public static void f(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("module_click", str);
        a("app_clear_data_click", (Map<String, String>) hashMap);
    }
}
