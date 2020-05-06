package com.miui.powercenter;

import com.miui.common.persistence.b;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;

public class y {
    public static void a(int i) {
        b.b("key_battery_last_drain_percent", i);
    }

    public static void a(long j) {
        b.b("key_battery_last_charge_time", j);
    }

    public static void a(boolean z) {
        b.b("key_auto_exit_power_save_mode_enabled", z);
    }

    public static boolean a() {
        return b.a("key_auto_exit_power_save_mode_enabled", true);
    }

    public static long b() {
        return b.a("key_battery_last_charge_time", 0);
    }

    public static void b(int i) {
        b.b("key_battery_over_heat_value", i);
    }

    public static void b(long j) {
        b.b("key_battery_last_drain_time_duration", j);
    }

    public static void b(boolean z) {
        b.b("key_close_wakeup_for_notification", z);
    }

    public static int c() {
        return b.a("key_battery_last_drain_percent", 0);
    }

    public static void c(int i) {
        b.b("key_disable_mobile_data_time", i);
    }

    public static void c(long j) {
        b.b("key_battery_info_charge_full_time_ac", j);
    }

    public static void c(boolean z) {
        b.b("key_close_xiaoai_voice", z);
    }

    public static long d() {
        return b.a("key_battery_last_drain_time_duration", 0);
    }

    public static void d(int i) {
        b.b("key_memory_clean_time", i);
    }

    public static void d(long j) {
        b.b("key_battery_info_charge_full_time_usb", j);
    }

    public static void d(boolean z) {
        b.b("on_time_boot_enabled", z);
    }

    public static int e() {
        return b.a("key_battery_over_heat_value", 0);
    }

    public static void e(int i) {
        b.b("on_time_boot_repeat", i);
    }

    public static void e(long j) {
        b.b("key_last_show_over_heat_time", j);
    }

    public static void e(boolean z) {
        b.b("shutdown_on_time_enabled", z);
    }

    public static long f() {
        return b.a("key_battery_info_charge_full_time_ac", 0);
    }

    public static void f(int i) {
        b.b("on_time_boot_time", i);
    }

    public static void f(long j) {
        b.b("bootTimeKey", j);
    }

    public static void f(boolean z) {
        b.b("key_power_save_alarm_enabled", z);
    }

    public static long g() {
        return b.a("key_battery_info_charge_full_time_usb", 0);
    }

    public static void g(int i) {
        b.b("on_time_shutdown_repeat", i);
    }

    public static void g(long j) {
        b.b("saved_shutdown_time", j);
    }

    public static void g(boolean z) {
        b.b("key_show_battery_consume_abnormal", z);
    }

    public static void h(int i) {
        b.b("on_time_shutdown_time", i);
    }

    public static boolean h() {
        return b.a("key_close_wakeup_for_notification", true);
    }

    public static int i() {
        int a2 = b.a("key_disable_mobile_data_time", 0);
        if (a2 != 60) {
            return a2;
        }
        int[] intArray = Application.d().getResources().getIntArray(R.array.pc_disconnect_data_time_choice_items);
        if (intArray.length <= 0) {
            return a2;
        }
        int i = intArray[0] * 60;
        c(i);
        return i;
    }

    public static void i(int i) {
        b.b("key_power_save_close_time", i);
    }

    public static long j() {
        return b.a("key_last_show_over_heat_time", 0);
    }

    public static void j(int i) {
        b.b("key_power_save_open_time", i);
    }

    public static boolean k() {
        return false;
    }

    public static int l() {
        return b.a("key_memory_clean_time", 0);
    }

    public static boolean m() {
        return b.a("on_time_boot_enabled", false);
    }

    public static int n() {
        return b.a("on_time_boot_repeat", 127);
    }

    public static int o() {
        return b.a("on_time_boot_time", 420);
    }

    public static long p() {
        return b.a("bootTimeKey", 0);
    }

    public static long q() {
        return b.a("saved_shutdown_time", 0);
    }

    public static boolean r() {
        return b.a("shutdown_on_time_enabled", false);
    }

    public static int s() {
        return b.a("on_time_shutdown_repeat", 127);
    }

    public static int t() {
        return b.a("on_time_shutdown_time", 1410);
    }

    public static boolean u() {
        return b.a("key_power_save_alarm_enabled", false);
    }

    public static int v() {
        return b.a("key_power_save_close_time", 420);
    }

    public static int w() {
        return b.a("key_power_save_open_time", 1380);
    }

    public static boolean x() {
        return b.a("key_show_battery_consume_abnormal", true);
    }
}
