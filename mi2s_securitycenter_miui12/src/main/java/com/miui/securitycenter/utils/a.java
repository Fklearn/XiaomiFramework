package com.miui.securitycenter.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.service.SpecificDeviceSystemCheckService;
import java.util.Calendar;
import miui.security.SecurityManager;

public class a {
    public static void a() {
        Application d2 = Application.d();
        ((AlarmManager) d2.getSystemService("alarm")).cancel(b(d2));
    }

    public static boolean a(Context context) {
        return a(context, false);
    }

    private static boolean a(Context context, boolean z) {
        return ((SecurityManager) context.getSystemService("security")).setAppHide(z);
    }

    private static PendingIntent b(Context context) {
        return PendingIntent.getService(context, 10002, new Intent(context, SpecificDeviceSystemCheckService.class), 0);
    }

    public static void b() {
        Application d2 = Application.d();
        AlarmManager alarmManager = (AlarmManager) d2.getSystemService("alarm");
        if (e() && c()) {
            Calendar instance = Calendar.getInstance();
            instance.set(11, (int) (Math.random() * 23.0d));
            instance.set(12, (int) (Math.random() * 60.0d));
            alarmManager.setRepeating(1, instance.getTimeInMillis(), 86400000, PendingIntent.getService(d2, 10002, new Intent(d2, SpecificDeviceSystemCheckService.class), 0));
        }
    }

    public static boolean c() {
        return ((SecurityManager) Application.d().getSystemService("security")).isAppHide();
    }

    public static boolean d() {
        return ((SecurityManager) Application.d().getSystemService("security")).isFunctionOpen();
    }

    public static boolean e() {
        return ((SecurityManager) Application.d().getSystemService("security")).isValidDevice();
    }
}
