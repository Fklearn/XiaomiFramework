package com.miui.securityscan;

import android.preference.PreferenceManager;
import com.miui.common.persistence.b;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;

public class M {
    public static long a(long j) {
        return b.a("latest_consume_power_optimize_date", j);
    }

    public static void a(int i) {
        b.b("need_update_app_count", i);
    }

    public static void a(String str) {
        b.b("key_mifi_insurance_url", str);
    }

    public static void a(ArrayList<String> arrayList) {
        b.b("need_update_app_pkgName", arrayList);
    }

    public static void a(boolean z) {
        b.b("key_first_update_screen_ad_state", z);
    }

    public static boolean a() {
        return b.a("key_first_update_screen_ad_state", true);
    }

    public static long b() {
        return b.a("key_last_update_scan_item_time", 0);
    }

    public static long b(long j) {
        return b.a("latest_optimize_date", j);
    }

    public static void b(int i) {
        b.b("key_third_desktop_type", i);
    }

    public static void b(String str) {
        b.b("key_set_newest_miui_version", str);
    }

    public static void b(boolean z) {
        b.b("manual_optimize_item_jumped", z);
    }

    public static String c() {
        return b.a("key_mifi_insurance_url", "");
    }

    public static void c(long j) {
        b.b("key_last_update_scan_item_time", j);
    }

    public static void c(boolean z) {
        b.b("key_sc_setting_news_only_wlan", z);
    }

    public static int d() {
        return b.a("need_update_app_count", 0);
    }

    public static void d(long j) {
        b.b("latest_optimize_date", j);
    }

    public static void d(boolean z) {
        b.b("key_sc_setting_news_recommend", z);
    }

    public static ArrayList<String> e() {
        return b.a("need_update_app_pkgName", (ArrayList<String>) new ArrayList());
    }

    public static void e(long j) {
        b.b("key_no_kill_pkg_version", j);
    }

    public static void e(boolean z) {
        b.b("key_scan_item_use_flag", z);
    }

    public static String f() {
        return b.a("key_set_newest_miui_version", "");
    }

    public static void f(long j) {
        b.b("key_third_desktop_version", j);
    }

    public static long g() {
        return b.a("key_no_kill_pkg_version", 0);
    }

    public static boolean h() {
        return b.a("key_scan_item_use_flag", false);
    }

    public static int i() {
        return b.a("key_third_desktop_type", 0);
    }

    public static long j() {
        return b.a("key_third_desktop_version", -1);
    }

    public static boolean k() {
        return b.a("manual_optimize_item_jumped", false);
    }

    public static boolean l() {
        Application d2 = Application.d();
        return b.a("key_sc_setting_news_only_wlan", PreferenceManager.getDefaultSharedPreferences(d2).getBoolean(d2.getString(R.string.preference_key_information_setting_wlan), false));
    }

    public static boolean m() {
        Application d2 = Application.d();
        return b.a("key_sc_setting_news_recommend", PreferenceManager.getDefaultSharedPreferences(d2).getBoolean(d2.getString(R.string.preference_key_information_setting_close), true));
    }
}
