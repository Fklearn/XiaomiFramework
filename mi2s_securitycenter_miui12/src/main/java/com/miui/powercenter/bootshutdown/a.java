package com.miui.powercenter.bootshutdown;

import android.content.Context;
import android.content.Intent;
import com.miui.powercenter.y;

public class a {
    public static void a(Context context) {
        Intent intent = new Intent(context, BootAlarmIntentService.class);
        intent.setAction("com.miui.powercenter.RESET_BOOT_TIME");
        context.startService(intent);
    }

    public static boolean a() {
        return y.n() != 0 || y.p() > System.currentTimeMillis() + 120000;
    }

    public static void b(Context context) {
        Intent intent = new Intent(context, ShutdownAlarmIntentService.class);
        intent.setAction("com.miui.powercenter.RESET_SHUTDOWNTIME");
        context.startService(intent);
    }

    public static void c(Context context) {
        Intent intent = new Intent(context, BootAlarmIntentService.class);
        intent.setAction("com.miui.powercenter.SET_BOOT_TIME");
        context.startService(intent);
    }

    public static void d(Context context) {
        Intent intent = new Intent(context, ShutdownAlarmIntentService.class);
        intent.setAction("com.miui.powercenter.SET_SHUTDOWN_ALARM");
        context.startService(intent);
    }
}
