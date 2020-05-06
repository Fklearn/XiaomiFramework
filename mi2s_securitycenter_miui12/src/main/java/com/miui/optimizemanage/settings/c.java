package com.miui.optimizemanage.settings;

import com.miui.common.persistence.b;
import com.miui.luckymoney.model.message.Impl.QQMessage;

public class c {
    public static int a() {
        return b.a("animation_time", (int) QQMessage.TYPE_DISCUSS_GROUP);
    }

    public static void a(int i) {
        b.b("animation_time", i * 1000);
    }

    public static void a(long j) {
        b.b("cloud_sync_time", j);
    }

    public static void a(boolean z) {
        b.b("key_show_cpu_overload_notification_enabled", z);
    }

    public static long b() {
        return b.a("key_last_clean_memory_time", 0);
    }

    public static void b(int i) {
        b.b("key_optimize_locked_app_num", i);
    }

    public static void b(long j) {
        b.b("key_last_clean_memory_time", j);
    }

    public static long c() {
        return b.a("last_deep_clean_time", 0);
    }

    public static void c(int i) {
        b.b("key_memory_occupy_notify_percent", i);
    }

    public static void c(long j) {
        b.b("last_deep_clean_time", j);
    }

    public static long d() {
        return b.a("key_show_media_scan_timeout_time", 0);
    }

    public static long e() {
        return b.a("key_last_show_memory_occupy_notify_time", 0);
    }

    public static int f() {
        return b.a("key_optimize_locked_app_num", 0);
    }

    public static int g() {
        return b.a("key_memory_occupy_notify_percent", 80);
    }

    public static boolean h() {
        return b.a("key_optimize_usage_tips_shown", false);
    }

    public static boolean i() {
        return b.a("key_show_cpu_overload_notification_enabled", true);
    }

    public static boolean j() {
        return false;
    }
}
