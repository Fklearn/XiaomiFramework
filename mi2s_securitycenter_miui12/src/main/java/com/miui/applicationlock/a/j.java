package com.miui.applicationlock.a;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import b.b.o.g.c;
import com.miui.analytics.AnalyticsUtil;
import com.miui.applicationlock.c.E;
import com.miui.applicationlock.c.K;

public class j {

    /* renamed from: a  reason: collision with root package name */
    private static String f3252a = "PrivacyAnalyticHelper";

    public static void a(Context context) {
        e(context);
        if (c(context)) {
            g(context);
            d(context);
            if (E.a(context).d()) {
                f(context);
            }
        }
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordNumericEvent("applicationlock", str, j);
    }

    private static void a(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("applicationlock", str, str2);
    }

    private static boolean b(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String string = Settings.Secure.getString(contentResolver, "privacy_pwd_bind_xiaomi_account");
        if (string != null) {
            Settings.Secure.putString(contentResolver, "privacy_add_account_md5", b.b.c.j.j.d(string.getBytes()));
            Settings.Secure.putString(contentResolver, "privacy_pwd_bind_xiaomi_account", (String) null);
        }
        return Settings.Secure.getString(contentResolver, "privacy_add_account_md5") != null;
    }

    private static boolean c(Context context) {
        try {
            c.a a2 = c.a.a("android.security.ChooseLockSettingsHelper");
            a2.a(new Class[]{Context.class}, context);
            a2.a("isACLockEnabled", (Class<?>[]) null, new Object[0]);
            return ((Boolean) a2.d()).booleanValue();
        } catch (Exception e) {
            Log.e(f3252a, "isPrivacyPasswordEnable exception: ", e);
            return false;
        }
    }

    private static void d(Context context) {
        a("toggle_private_binding", b(context) ? "on" : K.c(context) ? "off_logged_in" : "off_not_logged");
    }

    private static void e(Context context) {
        a("toggle_private_main", c(context) ? 1 : 0);
    }

    private static void f(Context context) {
        boolean z = true;
        if (Settings.Secure.getInt(context.getContentResolver(), "fingerprint_apply_to_privacy_password", 1) != 2) {
            z = false;
        }
        a("toggle_private_finger_mark", z ? 1 : 0);
    }

    private static void g(Context context) {
        boolean z = true;
        if (Settings.Secure.getInt(context.getContentResolver(), "privacy_password_is_visible_pattern", 1) != 1) {
            z = false;
        }
        a("toggle_private_showdrawing", z ? 1 : 0);
    }
}
