package com.miui.gamebooster.m;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

/* renamed from: com.miui.gamebooster.m.b  reason: case insensitive filesystem */
public class C0371b {
    public static void a(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.miui.gamebooster.action.SIGN_NOTIFICATION");
        ((AlarmManager) context.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(context, 0, intent, 134217728));
    }

    public static void b(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.miui.gamebooster.action.SIGN_NOTIFICATION");
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, 134217728);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        instance.set(11, 20);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        ((AlarmManager) context.getSystemService("alarm")).setRepeating(1, instance.getTimeInMillis(), 86400000, broadcast);
    }
}
