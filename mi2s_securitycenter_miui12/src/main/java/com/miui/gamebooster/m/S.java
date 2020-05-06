package com.miui.gamebooster.m;

import android.app.MiuiNotification;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import b.b.c.j.v;
import com.miui.securitycenter.R;
import miui.os.Build;

public class S {
    public static Notification a(Context context, String str, String str2, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, boolean z2, boolean z3, int i) {
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_booster_icon);
        Notification.Builder a2 = v.a(context, "com.miui.gamebooster");
        a2.setContentTitle(str);
        a2.setContentText(str2);
        a2.setContentIntent(pendingIntent);
        a2.setSmallIcon(R.drawable.game_booster_icon_small);
        a2.setLargeIcon(decodeResource);
        if (z3) {
            a2.setPriority(2);
        } else {
            a2.setPriority(1);
        }
        a2.setWhen(System.currentTimeMillis());
        if (z2) {
            a2.addAction(R.drawable.game_booster_icon, context.getString(R.string.booster_immediately), pendingIntent);
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("miui.showAction", !Build.IS_INTERNATIONAL_BUILD);
        a2.setExtras(bundle);
        if (z) {
            a2.setAutoCancel(true);
        } else {
            a2.setOngoing(true);
        }
        if (pendingIntent2 != null) {
            a2.setDeleteIntent(pendingIntent2);
        }
        Notification build = a2.build();
        build.tickerText = str + ":" + str2;
        build.flags = i;
        return build;
    }

    public static void a(Context context) {
        a(context, 10003);
    }

    public static void a(Context context, int i) {
        ((NotificationManager) context.getSystemService("notification")).cancel(i);
    }

    public static void a(Context context, int i, String str, String str2, PendingIntent pendingIntent, PendingIntent pendingIntent2, PendingIntent pendingIntent3, Boolean bool, Boolean bool2, int i2) {
        Context context2 = context;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.gamebooster", context.getResources().getString(R.string.game_booster), bool2.booleanValue() ? 5 : 4);
        Notification a2 = a(context, str, str2, pendingIntent, pendingIntent3, true, bool.booleanValue(), bool2.booleanValue(), i2);
        MiuiNotification a3 = C0384o.a(a2);
        if (a3 != null) {
            PendingIntent pendingIntent4 = pendingIntent2;
            a3.setExitFloatingIntent(pendingIntent2);
            a3.setCustomizedIcon(true);
        }
        int i3 = i;
        notificationManager.notify(i, a2);
    }

    public static PendingIntent b(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.miui.gamebooster.action.GAMEBOX_ALERT_ACTIVITY");
        intent.putExtra("intent_gamebox_function_type", "intent_gamebox_func_type_immersion_back");
        return PendingIntent.getActivity(context, 0, intent, 134217728);
    }

    public static void b(Context context, int i) {
        Context context2 = context;
        a(context2, 10002, context.getResources().getQuantityString(R.plurals.add_game_notification_title, i, new Object[]{Integer.valueOf(i)}), context.getString(R.string.add_game_notification_summary), c(context), (PendingIntent) null, (PendingIntent) null, (Boolean) null, false, 16);
        Log.i("NotificationHelper", "Add Notification has shown!");
        C0373d.q("noti_gameadd", "show");
    }

    public static PendingIntent c(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.miui.gamebooster.action.ACCESS_MAINACTIVITY");
        intent.putExtra("gamebooster_entrance", "noti_gameadd");
        if (C0388t.s()) {
            intent.putExtra("jump_target", "gamebox");
        }
        return PendingIntent.getActivity(context, 0, intent, 134217728);
    }

    public static void d(Context context) {
        a(context, 10003, context.getResources().getString(R.string.noti_back_diving_mode_title), (String) null, b(context), (PendingIntent) null, (PendingIntent) null, (Boolean) null, false, 2);
        Log.i("NotificationHelper", "DivingMode Notification has shown!");
        C0373d.q("noti_diving_mode", "show");
    }

    public static void e(Context context) {
        a(context, 10002, context.getResources().getString(R.string.gb_sign_notification_title), context.getResources().getString(R.string.gb_sign_notification_summary), c(context), (PendingIntent) null, (PendingIntent) null, (Boolean) null, false, 16);
        Log.i("NotificationHelper", "Sign Notification has shown!");
    }
}
