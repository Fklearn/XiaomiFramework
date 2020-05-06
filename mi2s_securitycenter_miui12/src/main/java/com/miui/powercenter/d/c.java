package com.miui.powercenter.d;

import android.content.Context;
import android.provider.Settings;
import miui.os.SystemProperties;

public class c implements d {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f6988a;

    static {
        String str = SystemProperties.get("sys.haptic.motor", "");
        f6988a = str.equals("linear") || str.equals("zlinear");
    }

    public static boolean a() {
        return f6988a;
    }

    public void a(Context context) {
        synchronized (c.class) {
            int i = Settings.System.getInt(context.getContentResolver(), "power_center_haptic_feed_back_mode", -1);
            if (i != -1) {
                if (i != Settings.System.getInt(context.getContentResolver(), "haptic_feedback_enabled", 0)) {
                    Settings.System.putInt(context.getContentResolver(), "haptic_feedback_enabled", i);
                }
                Settings.System.putInt(context.getContentResolver(), "power_center_haptic_feed_back_mode", -1);
            }
        }
    }

    public void b(Context context) {
        if (!f6988a) {
            synchronized (c.class) {
                int i = Settings.System.getInt(context.getContentResolver(), "power_center_haptic_feed_back_mode", -1);
                int i2 = Settings.System.getInt(context.getContentResolver(), "haptic_feedback_enabled", 1);
                if (i == -1) {
                    Settings.System.putInt(context.getContentResolver(), "power_center_haptic_feed_back_mode", i2);
                }
                if (i2 != 0) {
                    Settings.System.putInt(context.getContentResolver(), "haptic_feedback_enabled", 0);
                }
            }
        }
    }
}
