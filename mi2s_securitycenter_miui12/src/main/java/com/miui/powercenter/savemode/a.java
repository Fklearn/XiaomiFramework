package com.miui.powercenter.savemode;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.powercenter.provider.PowerSaveService;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.y;
import java.util.Calendar;

public class a {
    private static long a(int i, boolean z) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        instance.set(11, i / 60);
        instance.set(12, i % 60);
        instance.set(13, 0);
        instance.set(14, 0);
        if (z && instance.getTimeInMillis() <= System.currentTimeMillis()) {
            instance.add(7, 1);
        }
        return instance.getTimeInMillis();
    }

    private static PendingIntent a(Context context, boolean z) {
        Intent intent = new Intent(context, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.CHANGE_POWER_MODE_ALARM");
        intent.putExtra("extra_key_power_mode_open", z);
        return PendingIntent.getService(context, 0, intent, 134217728);
    }

    public static void a(Context context) {
        c(context);
    }

    public static void a(Context context, long j) {
        String str;
        String str2;
        if (y.u()) {
            c(context);
            boolean l = o.l(context);
            long currentTimeMillis = System.currentTimeMillis();
            if (l) {
                if (a()) {
                    currentTimeMillis = a(y.v(), true);
                    str2 = "Close save mode in future";
                } else {
                    str2 = "Close save mode now";
                }
                Log.i("PowerSaveAlarmHelper", str2);
                a(context, false, currentTimeMillis + j);
                return;
            }
            if (a()) {
                str = "Open save mode now";
            } else {
                currentTimeMillis = a(y.w(), true);
                str = "Open save mode in future";
            }
            Log.i("PowerSaveAlarmHelper", str);
            a(context, true, currentTimeMillis + j);
        }
    }

    private static void a(Context context, boolean z, long j) {
        ((AlarmManager) context.getSystemService("alarm")).setExact(0, j, a(context, z));
    }

    public static boolean a() {
        long a2 = a(y.w(), false);
        long a3 = a(y.v(), false);
        if (a3 < a2) {
            a3 = a(y.v(), true);
        }
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis >= a2 && currentTimeMillis < a3;
    }

    public static void b(Context context) {
        a(context, 0);
    }

    private static void c(Context context) {
        ((AlarmManager) context.getSystemService("alarm")).cancel(a(context, false));
    }
}
