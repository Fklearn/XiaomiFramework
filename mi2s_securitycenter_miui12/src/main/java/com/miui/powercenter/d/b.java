package com.miui.powercenter.d;

import android.content.Context;
import android.provider.Settings;
import com.miui.powercenter.utils.g;

public class b implements d {
    public void a(Context context) {
        if (g.b() && g.a()) {
            synchronized (b.class) {
                int i = Settings.System.getInt(context.getContentResolver(), "power_center_is_5g_mode_enable", -1);
                if (-1 != i) {
                    boolean z = true;
                    if (i != 1) {
                        z = false;
                    }
                    if (z != g.c(context)) {
                        g.a(z);
                    }
                    Settings.System.putInt(context.getContentResolver(), "power_center_is_5g_mode_enable", -1);
                }
            }
        }
    }

    public void b(Context context) {
        if (g.b() && g.a()) {
            synchronized (b.class) {
                int i = Settings.System.getInt(context.getContentResolver(), "power_center_is_5g_mode_enable", -1);
                boolean c2 = g.c(context);
                if (-1 == i) {
                    Settings.System.putInt(context.getContentResolver(), "power_center_is_5g_mode_enable", c2 ? 1 : 0);
                }
                if (c2) {
                    g.a(false);
                }
            }
        }
    }
}
