package com.miui.gamebooster.m;

import android.content.Context;
import android.provider.Settings;
import com.miui.common.persistence.b;

public class Y {
    public static int a(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_brightness_mode", 0);
    }

    public static void a(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "screen_brightness_mode", i);
    }

    public static void a(Context context, boolean z) {
        if (z) {
            int a2 = a(context);
            b.b("gb_function_user_auto_bright", a2);
            if (a2 == 1) {
                a(context, 0);
            }
        } else if (a(context) == 0) {
            a(context, b.a("gb_function_user_auto_bright", 0));
        }
    }
}
