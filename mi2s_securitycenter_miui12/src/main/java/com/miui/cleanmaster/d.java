package com.miui.cleanmaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import miui.provider.ExtraSettings;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static d f3743a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f3744b;

    /* renamed from: c  reason: collision with root package name */
    private Context f3745c;

    /* renamed from: d  reason: collision with root package name */
    private SharedPreferences f3746d = this.f3745c.getSharedPreferences("cm_notification_settings", 0);

    private d(Context context) {
        this.f3745c = context.getApplicationContext();
        this.f3744b = a(context, context.getPackageName());
    }

    public static d a(Context context) {
        if (f3743a == null) {
            f3743a = new d(context);
        }
        return f3743a;
    }

    public static boolean a(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                return (packageManager.getPackageInfo(str, 0).applicationInfo.flags & 1) != 0;
            }
        } catch (Exception unused) {
        }
        return false;
    }

    public int a() {
        return this.f3746d.getInt("notification_priority_size_cn", 32);
    }

    public long a(String str) {
        return this.f3746d.getLong(str, System.currentTimeMillis());
    }

    public void a(int i) {
        this.f3746d.edit().putInt("notification_priority_size_cn", i).apply();
    }

    public void a(String str, int i) {
        this.f3746d.edit().putInt(str, i).apply();
    }

    public void a(String str, long j) {
        this.f3746d.edit().putLong(str, j).apply();
    }

    public int b() {
        return this.f3746d.getInt("notification_priority_size_global", 0);
    }

    public int b(String str) {
        return this.f3746d.getInt(str, 0);
    }

    public void b(int i) {
        this.f3746d.edit().putInt("notification_priority_size_global", i).apply();
    }

    public int c() {
        return !this.f3744b ? ExtraSettings.System.getInt(this.f3745c.getContentResolver(), "invalidCleanAlertNotificationCount", 0) : ExtraSettings.Secure.getInt(this.f3745c.getContentResolver(), "invalidCleanAlertNotificationCount", 0);
    }

    public void c(int i) {
        if (!this.f3744b) {
            ExtraSettings.System.putInt(this.f3745c.getContentResolver(), "invalidCleanAlertNotificationCount", i);
        } else {
            ExtraSettings.Secure.putInt(this.f3745c.getContentResolver(), "invalidCleanAlertNotificationCount", i);
        }
    }
}
