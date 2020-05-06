package com.miui.powercenter.utils;

import android.content.Context;
import android.os.Build;
import b.b.c.j.B;
import com.miui.activityutil.o;
import com.miui.common.persistence.b;
import com.miui.securitycenter.R;
import java.util.Locale;

public class u {

    /* renamed from: a  reason: collision with root package name */
    public static final boolean f7322a = "hammerhead".equals(Build.DEVICE);

    public static String a(int i) {
        return String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)});
    }

    public static String a(Context context, int i) {
        return context.getResources().getString(R.string.percent_formatted_text, new Object[]{String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)})});
    }

    public static boolean a() {
        return b.a("game_IsAntiMsg", false);
    }

    public static boolean a(String str) {
        return "fa".equals(str) || "ar".equals(str) || "ur".equals(str) || "ug_CN".equals(str);
    }

    public static String b(int i) {
        return a(i / 60) + ":" + c(i % 60);
    }

    public static boolean b() {
        boolean a2 = b.a("key_pc_main_not_first_track_5g", true);
        if (a2) {
            b.b("key_pc_main_not_first_track_5g", false);
        }
        return a2;
    }

    private static String c(int i) {
        String a2 = a(i);
        String a3 = a(0);
        if (!o.f2309a.equals(a3) || i >= 10) {
            return a2;
        }
        return a3 + a2;
    }

    public static boolean c() {
        boolean a2 = b.a("key_pc_main_not_first_click_power_save", false);
        if (!a2) {
            b.b("key_pc_main_not_first_click_power_save", true);
        }
        return !a2;
    }

    public static boolean d() {
        return !miui.os.Build.IS_INTERNATIONAL_BUILD;
    }

    public static boolean e() {
        return B.f() && !f7322a;
    }
}
