package com.miui.securityadd.input;

import android.content.Context;
import android.util.Log;
import b.b.m.a;

public class b {
    public static void a() {
        try {
            a.a("language", g.b());
        } catch (Exception e) {
            Log.e("InputAnalyticsHelper", "trackSystemLanguage", e);
        }
    }

    public static void a(Context context) {
        try {
            a.a("current_input_method_name_version", g.e(context));
        } catch (Exception e) {
            Log.e("InputAnalyticsHelper", "trackCurrentInputMethod", e);
        }
    }

    public static void b(Context context) {
        try {
            a.a("is_miui_bottom_enable", g.i(context) ? 1 : 0);
        } catch (Exception e) {
            Log.e("InputAnalyticsHelper", "trackMiuiBottomEnable", e);
        }
    }

    public static void c(Context context) {
        try {
            a.a("left_function", g.g(context));
        } catch (Exception e) {
            Log.e("InputAnalyticsHelper", "trackMiuiBottomEnable", e);
        }
    }

    public static void d(Context context) {
        try {
            a.a("right_function", g.h(context));
        } catch (Exception e) {
            Log.e("InputAnalyticsHelper", "trackMiuiBottomEnable", e);
        }
    }

    public static void e(Context context) {
        try {
            a.a("security_keyboard_switch", g.k(context) ? 1 : 0);
        } catch (Exception e) {
            Log.e("InputAnalyticsHelper", "trackSecurityKeyboardSwitch", e);
        }
    }
}
