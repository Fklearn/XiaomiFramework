package com.miui.permcenter.a;

import android.content.Context;
import android.provider.Settings;
import com.miui.analytics.AnalyticsUtil;
import com.miui.permcenter.n;
import java.util.HashMap;

public class a {
    public static void a(Context context) {
        int i = Settings.Secure.getInt(context.getContentResolver(), "PERMISSION_USE_WARNING", 0);
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", String.valueOf(i));
        AnalyticsUtil.recordCountEvent("permcenter", "privacy_protect_state", hashMap);
    }

    public static void a(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("pkg", str);
        AnalyticsUtil.recordCountEvent("permcenter", "install_close_block", hashMap);
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordNumericEvent("permcenter", str, j);
    }

    public static void a(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put("allow_call", str + "_" + str2);
        AnalyticsUtil.recordCountEvent("permcenter", "wakepath_confirm_start_activity", hashMap);
    }

    public static void a(String str, boolean z, boolean z2) {
        HashMap hashMap = new HashMap();
        hashMap.put("pkg", str);
        hashMap.put(z ? "reject_pkg" : "allowed_pkg", str);
        if (z2) {
            hashMap.put("remember_pkg", str);
        }
        AnalyticsUtil.recordCountEvent("permcenter", "install_prompt", hashMap);
    }

    public static void b(Context context) {
        boolean z;
        try {
            z = n.b(context.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
            z = false;
        }
        a("toggle_app_permission_monitor", z ? 1 : 0);
    }

    public static void b(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("pkg", str);
        AnalyticsUtil.recordCountEvent("permcenter", "install_reject", hashMap);
    }

    public static void b(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put("reject_call", str + "_" + str2);
        AnalyticsUtil.recordCountEvent("permcenter", "wakepath_confirm_start_activity", hashMap);
    }

    public static void c(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("pkg", str);
        AnalyticsUtil.recordCountEvent("permcenter", "reject_lock", hashMap);
    }

    public static void d(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        AnalyticsUtil.recordCountEvent("permcenter", "privacy_protect_page", hashMap);
    }

    public static void e(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        AnalyticsUtil.recordCountEvent("permcenter", "privacy_protect_item_click", hashMap);
    }

    public static void f(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        AnalyticsUtil.recordCountEvent("permcenter", "privacy_use_toggle", hashMap);
    }
}
