package com.miui.powercenter.a;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import b.b.o.g.e;
import com.miui.activityutil.o;
import com.miui.analytics.AnalyticsUtil;
import com.miui.luckymoney.config.Constants;
import com.miui.powercenter.utils.g;
import com.miui.powercenter.y;
import java.util.HashMap;
import java.util.Map;

public class a {
    public static void a() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("5g_save_close_dialog", "show");
        a("5g_close_dialog", (Map<String, String>) hashMap);
    }

    public static void a(int i) {
        a("charge_quantity", (long) i);
    }

    public static void a(long j) {
        a("optimize_save_time", j);
    }

    public static void a(Context context) {
        try {
            a("toggle_energy_status_style", b(context));
            a("toggle_lockscreen_cut_off_data", n());
            a("toggle_lockscreen_clean_ram", m());
            long j = 1;
            b("toggle_unusual_expend_noti", y.x() ? 1 : 0);
            a("toggle_high_temperature_noti", l());
            a("toggle_timing_power_on_off", o());
            if (!y.u()) {
                j = 0;
            }
            b("toggle_timing_saving_mode", j);
            if (g.b()) {
                b("toggle_5g_saving_mode", (long) g.b(context));
            }
            b.a(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(String str) {
        j(str);
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordCalculateEvent("powercenter", str, j, (Map<String, String>) null);
    }

    private static void a(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("powercenter", str, str2);
    }

    private static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("powercenter", str, map);
    }

    public static void a(boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", z ? "5g_save_mode_on" : "5g_save_mode_off");
        a("5g_save_mode_page_on_off", (Map<String, String>) hashMap);
    }

    private static String b(Context context) {
        String str;
        try {
            str = (String) e.a(Class.forName("android.provider.MiuiSettings$System"), "BATTERY_INDICATOR_STYLE", String.class);
        } catch (Exception e) {
            Log.d("AnalyticHelper", "getBatteryStyleValue exception:", e);
            str = "";
        }
        int i = Settings.System.getInt(context.getContentResolver(), str, 0);
        return i != 0 ? i != 1 ? i != 2 ? "" : "top" : "number" : "pattern";
    }

    public static void b() {
        j("5g_device_number");
    }

    public static void b(int i) {
        a("energy_when_save_mode_on", (long) i);
    }

    public static void b(long j) {
        a("scan_time", j);
    }

    public static void b(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        a("5g_close_later_action", (Map<String, String>) hashMap);
    }

    private static void b(String str, long j) {
        AnalyticsUtil.recordNumericEvent("powercenter", str, j);
    }

    public static void b(boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", z ? "battery_statics_rank" : "battery_statics_usage");
        a("battery_statics_change", (Map<String, String>) hashMap);
    }

    public static void c() {
        k("auto_task");
    }

    public static void c(int i) {
        a("energy_when_save_mode_on_1", (long) i);
    }

    public static void c(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("scan_page_show", str);
        a("scan_page", (Map<String, String>) hashMap);
    }

    public static void c(boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", z ? "close_wakeup_for_notification_on" : "close_wakeup_for_notification_off");
        a("close_wakeup_for_notification", (Map<String, String>) hashMap);
    }

    public static void d() {
        k("battery_statics");
    }

    public static void d(int i) {
        a("scan_problem_quantity", (long) i);
    }

    public static void d(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        a("optimize_result_action", (Map<String, String>) hashMap);
    }

    public static void d(boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", z ? "close_xiaoai_voice_wakeup_on" : "close_xiaoai_voice_wakeup_off");
        a("close_xiaoai_voice_wakeup", (Map<String, String>) hashMap);
    }

    public static void e() {
        k("extreme_save_mode");
    }

    public static void e(int i) {
        a("solve_problem_quantity", (long) i);
    }

    public static void e(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        a("optimize_result_action", (Map<String, String>) hashMap);
    }

    public static void e(boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", z ? "save_mode_when_charge_on" : "save_mode_when_charge_off");
        a("save_mode_off_when_charge", (Map<String, String>) hashMap);
    }

    public static void f() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("19_percent", "save_mode_on");
        a("low_energy_popup", (Map<String, String>) hashMap);
    }

    public static void f(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("package_name", str);
        a("scan_problem_app", (Map<String, String>) hashMap);
    }

    public static void f(boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", z ? o.f2310b : o.f2309a);
        a("save_mode_on_off_plan", (Map<String, String>) hashMap);
    }

    public static void g() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("9_percent", "save_mode_on");
        a("low_energy_popup", (Map<String, String>) hashMap);
    }

    public static void g(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("scan_page_click", str);
        a("scan_page", (Map<String, String>) hashMap);
    }

    public static void g(boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", z ? "save_mode_on" : "save_mode_off");
        a("save_mode_page_on_off", (Map<String, String>) hashMap);
    }

    public static void h() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("19_percent", "show");
        a("low_energy_popup", (Map<String, String>) hashMap);
    }

    public static void h(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        a("scan_result_action", (Map<String, String>) hashMap);
    }

    public static void i() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("5_percent", "show");
        a("low_energy_popup", (Map<String, String>) hashMap);
    }

    public static void i(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_select_optimize", str);
        a("scan_result_action", (Map<String, String>) hashMap);
    }

    public static void j() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("9_percent", "show");
        a("low_energy_popup", (Map<String, String>) hashMap);
    }

    private static void j(String str) {
        AnalyticsUtil.recordCountEvent("powercenter", str, (Map<String, String>) null);
    }

    public static void k() {
        HashMap hashMap = new HashMap(1);
        hashMap.put(Constants.JSON_KEY_MODULE, "show");
        a("unusual_expend_noti", (Map<String, String>) hashMap);
    }

    private static void k(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        a("homepage_button_click", (Map<String, String>) hashMap);
    }

    private static String l() {
        int e = y.e();
        return e == 0 ? "off" : String.valueOf(e);
    }

    private static String m() {
        int l = y.l() / 60;
        return l != 0 ? l != 1 ? l != 5 ? l != 10 ? l != 30 ? "" : "30_min" : "10_min" : "5_min" : "1_min" : "none";
    }

    private static String n() {
        int i = y.i() / 60;
        return i != 0 ? i != 1 ? i != 5 ? i != 10 ? i != 30 ? "" : "30_min" : "10_min" : "5_min" : "1_min" : "none";
    }

    private static String o() {
        boolean m = y.m();
        boolean r = y.r();
        return (!m || !r) ? m ? "power_on_only" : r ? "power_off_only" : "neither" : "both";
    }
}
