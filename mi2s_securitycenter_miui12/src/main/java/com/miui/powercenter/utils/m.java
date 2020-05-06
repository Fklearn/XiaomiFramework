package com.miui.powercenter.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import b.b.c.j.v;
import b.b.o.a.a;
import com.miui.powercenter.PowerCenter;
import com.miui.securitycenter.R;
import miui.os.Build;

public class m {
    private static Notification.Action a(Context context, String str, int i) {
        return new Notification.Action(0, str, a(context, i));
    }

    private static Notification a(Context context, String str, String str2, String str3, int i) {
        Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
        Notification.Action a3 = a(context, str3, i);
        a2.setSmallIcon(R.drawable.powercenter_small_icon).setLargeIcon(l.a(context)).setContentTitle(str).setContentText(str2).setContentIntent(a(context, i)).addAction(a3.icon, a3.title, a3.actionIntent).setAutoCancel(true);
        Bundle bundle = new Bundle();
        bundle.putBoolean("miui.showAction", !Build.IS_INTERNATIONAL_BUILD);
        a2.setExtras(bundle);
        Notification build = a2.build();
        a.a(build, true);
        return build;
    }

    private static PendingIntent a(Context context, int i) {
        Intent intent = new Intent(context, PowerCenter.class);
        intent.putExtra("requestCode", i);
        return PendingIntent.getActivity(context, 1, intent, 134217728);
    }

    public static void a(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.cancel(R.string.notification_battery_consume_abnormal_title);
    }

    public static void a(Context context, String str) {
        Notification a2 = a(context, context.getString(R.string.notification_battery_consume_abnormal_title), str, context.getString(R.string.btn_text_optimize_now), 2);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.notify(R.string.notification_battery_consume_abnormal_title, a2);
        com.miui.powercenter.a.a.k();
    }
}
