package com.miui.powercenter.d;

import android.content.Context;
import android.provider.Settings;
import com.miui.powercenter.y;

public class g implements d {
    public void a(Context context) {
        synchronized (g.class) {
            int i = Settings.System.getInt(context.getContentResolver(), "power_center_wakeup_pickup", -1);
            int i2 = Settings.System.getInt(context.getContentResolver(), "power_center_wakeup_double_click", -1);
            int i3 = Settings.System.getInt(context.getContentResolver(), "power_center_wakeup_notification", -1);
            if (!(i == -1 || i == Settings.System.getInt(context.getContentResolver(), "pick_up_gesture_wakeup_mode", 0))) {
                Settings.System.putInt(context.getContentResolver(), "pick_up_gesture_wakeup_mode", i);
            }
            if (!(i2 == -1 || i2 == Settings.System.getInt(context.getContentResolver(), "gesture_wakeup", 0))) {
                Settings.System.putInt(context.getContentResolver(), "gesture_wakeup", i2);
            }
            if (!(i3 == -1 || i3 == Settings.System.getInt(context.getContentResolver(), "wakeup_for_keyguard_notification", 0))) {
                Settings.System.putInt(context.getContentResolver(), "wakeup_for_keyguard_notification", i3);
            }
            Settings.System.putInt(context.getContentResolver(), "power_center_wakeup_pickup", -1);
            Settings.System.putInt(context.getContentResolver(), "power_center_wakeup_double_click", -1);
            Settings.System.putInt(context.getContentResolver(), "power_center_wakeup_notification", -1);
        }
    }

    public void b(Context context) {
        boolean h = y.h();
        synchronized (g.class) {
            int i = Settings.System.getInt(context.getContentResolver(), "power_center_wakeup_pickup", -1);
            int i2 = Settings.System.getInt(context.getContentResolver(), "power_center_wakeup_notification", -1);
            int i3 = Settings.System.getInt(context.getContentResolver(), "pick_up_gesture_wakeup_mode", 0);
            Settings.System.getInt(context.getContentResolver(), "gesture_wakeup", 0);
            int i4 = Settings.System.getInt(context.getContentResolver(), "wakeup_for_keyguard_notification", 0);
            if (i == -1) {
                Settings.System.putInt(context.getContentResolver(), "power_center_wakeup_pickup", i3);
            }
            if (i2 == -1 && h) {
                Settings.System.putInt(context.getContentResolver(), "power_center_wakeup_notification", i4);
            }
            if (i3 != 0) {
                Settings.System.putInt(context.getContentResolver(), "pick_up_gesture_wakeup_mode", 0);
            }
            if (i4 != 0 && h) {
                Settings.System.putInt(context.getContentResolver(), "wakeup_for_keyguard_notification", 0);
            }
        }
    }
}
