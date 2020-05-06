package com.miui.securitycenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import b.b.c.c.d;
import b.b.c.j.y;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0381l;
import com.miui.gamebooster.m.C0384o;
import com.miui.securitycenter.service.NotificationService;
import miui.os.Build;
import miui.provider.ExtraSettings;

public class h {
    public static long a() {
        return b.a("key_cmcc_last_notify_time", -1);
    }

    public static long a(Context context) {
        return C0381l.a(context) ? ExtraSettings.Secure.getLong(context.getContentResolver(), "key_notificaiton_general_clean_size", 0) : ExtraSettings.System.getLong(context.getContentResolver(), "key_notificaiton_general_clean_size", 0);
    }

    public static void a(int i) {
        b.b("key_cmcc_system_check_day", i);
    }

    public static void a(long j) {
        b.b("key_cmcc_last_notify_time", j);
    }

    public static void a(ContentResolver contentResolver, boolean z) {
        try {
            e.a(Class.forName("android.provider.MiuiSettings$System"), "putBoolean", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, contentResolver, "extra_show_security_notification", Boolean.valueOf(z));
        } catch (Exception e) {
            Log.i("Preferences", e.toString());
        }
    }

    public static void a(Context context, int i) {
        ExtraSettings.Secure.putInt(context.getContentResolver(), "key_score_in_security", i);
    }

    public static void a(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_app_lock_state_data_migrated", z ? 1 : 0);
    }

    public static void a(Context context, boolean z, long j) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), "key_notificaiton_general_clean_need", z);
        ExtraSettings.Secure.putLong(context.getContentResolver(), "key_notificaiton_general_clean_size", j);
        ExtraSettings.System.putBoolean(context.getContentResolver(), "key_notificaiton_general_clean_need", z);
        ExtraSettings.System.putLong(context.getContentResolver(), "key_notificaiton_general_clean_size", j);
    }

    public static void a(boolean z) {
        b.b("key_cmcc_system_check_done", z);
    }

    public static boolean a(ContentResolver contentResolver) {
        return MiuiSettings.System.getBoolean(contentResolver, "extra_show_security_notification", false);
    }

    public static int b() {
        return b.a("key_cmcc_system_check_day", 7);
    }

    public static long b(Context context) {
        return C0381l.a(context) ? ExtraSettings.Secure.getLong(context.getContentResolver(), "key_garbage_danger_in_size", 0) : ExtraSettings.System.getLong(context.getContentResolver(), "key_garbage_danger_in_size", 0);
    }

    public static void b(int i) {
        b.b("key_cmcc_system_check_score", i);
    }

    public static void b(long j) {
        b.b("key_last_get_incompatible_app_time", j);
    }

    public static void b(Context context, boolean z) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), "key_cleanup_db_auto_update_enabled", z);
    }

    public static void b(Context context, boolean z, long j) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), "key_notification_wechat_size_need", z);
        ExtraSettings.Secure.putLong(context.getContentResolver(), "key_notification_wechat_size", j);
        ExtraSettings.System.putBoolean(context.getContentResolver(), "key_notification_wechat_size_need", z);
        ExtraSettings.System.putLong(context.getContentResolver(), "key_notification_wechat_size", j);
    }

    public static void b(boolean z) {
        String str;
        String str2;
        if (z) {
            str2 = (String) C0384o.b("android.provider.MiuiSettings$System", "KEY_SECURITY_CENTER_ALLOW_CONNECT_NETWORK");
            str = "true";
        } else {
            str2 = (String) C0384o.b("android.provider.MiuiSettings$System", "KEY_SECURITY_CENTER_ALLOW_CONNECT_NETWORK");
            str = "false";
        }
        y.b(str2, str);
        c(z);
    }

    public static long c(Context context) {
        return C0381l.a(context) ? ExtraSettings.Secure.getLong(context.getContentResolver(), "key_notification_wechat_size", 0) : ExtraSettings.System.getLong(context.getContentResolver(), "key_notification_wechat_size", 0);
    }

    public static void c(long j) {
        b.b("key_last_upload_switch_analytics_time", j);
    }

    public static void c(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_homelist_cache_deleted", z ? 1 : 0);
    }

    public static void c(Context context, boolean z, long j) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), "key_notification_whatsapp_clean_need", z);
        ExtraSettings.Secure.putLong(context.getContentResolver(), "key_notificaiton_whatsapp_clean_size", j);
        ExtraSettings.System.putBoolean(context.getContentResolver(), "key_notification_whatsapp_clean_need", z);
        ExtraSettings.System.putLong(context.getContentResolver(), "key_notificaiton_whatsapp_clean_size", j);
    }

    private static void c(boolean z) {
        Context a2 = d.a();
        Intent intent = new Intent("action_update_sc_network_allow");
        intent.putExtra("extra_network_status", z);
        a2.sendBroadcast(intent);
    }

    public static boolean c() {
        return b.a("key_cmcc_system_check_done", true);
    }

    public static long d() {
        return b.a("key_last_get_incompatible_app_time", 0);
    }

    public static long d(Context context) {
        return C0381l.a(context) ? ExtraSettings.Secure.getLong(context.getContentResolver(), "key_notificaiton_whatsapp_clean_size", 0) : ExtraSettings.System.getLong(context.getContentResolver(), "key_notificaiton_whatsapp_clean_size", 0);
    }

    public static void d(long j) {
        b.b("key_start_device_time", j);
    }

    public static void d(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_launcher_loading_finished", z ? 1 : 0);
    }

    public static long e() {
        return b.a("key_last_upload_switch_analytics_time", 0);
    }

    public static void e(long j) {
        b.b("key_start_device_time_for_gms", j);
    }

    public static void e(Context context, boolean z) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), "optimizer_scan_cloud", z);
    }

    public static boolean e(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_app_lock_state_data_migrated", 0) == 1;
    }

    public static long f() {
        return b.a("key_start_device_time", -1);
    }

    public static void f(long j) {
        b.b("key_trigger_clean_master_auto_clean_time", j);
    }

    public static void f(Context context, boolean z) {
        a(context.getContentResolver(), z);
        Intent intent = new Intent(context, NotificationService.class);
        intent.setPackage(context.getPackageName());
        if (z) {
            context.startService(intent);
        } else {
            context.stopService(intent);
        }
    }

    public static boolean f(Context context) {
        return ExtraSettings.Secure.getBoolean(context.getContentResolver(), "key_cleanup_db_auto_update_enabled", true);
    }

    public static long g() {
        return b.a("key_start_device_time_for_gms", -1);
    }

    public static boolean g(Context context) {
        return C0381l.a(context) ? ExtraSettings.Secure.getBoolean(context.getContentResolver(), "key_notificaiton_general_clean_need", false) : ExtraSettings.System.getBoolean(context.getContentResolver(), "key_notificaiton_general_clean_need", false);
    }

    public static long h() {
        return b.a("key_trigger_clean_master_auto_clean_time", 0);
    }

    public static boolean h(Context context) {
        return C0381l.a(context) ? ExtraSettings.Secure.getBoolean(context.getContentResolver(), "key_garbage_danger_in_flag", false) : ExtraSettings.System.getBoolean(context.getContentResolver(), "key_garbage_danger_in_flag", false);
    }

    public static boolean i() {
        return Build.IS_INTERNATIONAL_BUILD ? C0384o.a(d.a(), "com.miui.securitycenter") && y.a((String) C0384o.b("android.provider.MiuiSettings$System", "KEY_SECURITY_CENTER_ALLOW_CONNECT_NETWORK"), false) : y.a((String) C0384o.b("android.provider.MiuiSettings$System", "KEY_SECURITY_CENTER_ALLOW_CONNECT_NETWORK"), false);
    }

    public static boolean i(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_homelist_cache_deleted", 0) == 1;
    }

    public static boolean j(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_launcher_loading_finished", 0) == 1;
    }

    public static boolean k(Context context) {
        return ExtraSettings.Secure.getBoolean(context.getContentResolver(), "optimizer_scan_cloud", false);
    }

    public static boolean l(Context context) {
        return C0381l.a(context) ? ExtraSettings.Secure.getBoolean(context.getContentResolver(), "key_notification_wechat_size_need", false) : ExtraSettings.System.getBoolean(context.getContentResolver(), "key_notification_wechat_size_need", false);
    }

    public static boolean m(Context context) {
        return C0381l.a(context) ? ExtraSettings.Secure.getBoolean(context.getContentResolver(), "key_notification_whatsapp_clean_need", false) : ExtraSettings.System.getBoolean(context.getContentResolver(), "key_notification_whatsapp_clean_need", false);
    }
}
