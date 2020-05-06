package com.miui.powercenter.autotask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import b.b.c.j.v;
import b.b.o.a.a;
import com.miui.powercenter.provider.PowerSaveService;
import com.miui.securitycenter.R;
import miui.os.Build;

/* renamed from: com.miui.powercenter.autotask.y  reason: case insensitive filesystem */
public class C0495y {
    private static Notification a(Context context, String str, String str2, String str3, PendingIntent pendingIntent, boolean z) {
        Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
        a2.setSmallIcon(R.drawable.powercenter_small_icon).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_auto_task)).setContentTitle(str).setContentText(str2).setAutoCancel(z).setPriority(2).setSound(Uri.EMPTY, (AudioAttributes) null);
        a2.setContentIntent(b(context));
        if (!TextUtils.isEmpty(str3)) {
            a2.addAction(0, str3, pendingIntent);
            Bundle bundle = new Bundle();
            bundle.putBoolean("miui.showAction", !Build.IS_INTERNATIONAL_BUILD);
            a2.setExtras(bundle);
        }
        Notification build = a2.build();
        a.b(build, true);
        a.a(build, true);
        return build;
    }

    public static void a(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.powercenter", context.getResources().getString(R.string.menu_item_notification_power_text), 5);
        notificationManager.cancel(2017061901);
    }

    public static void a(Context context, long j) {
        a(context, j, (int) R.string.auto_task_notify_task_canceled_summary);
    }

    public static void a(Context context, long j, int i) {
        Notification a2 = a(context, context.getString(R.string.auto_task_notify_task_canceled_title), context.getString(i), context.getString(R.string.auto_task_notify_task_continue_do), f(context, j), false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.powercenter", context.getResources().getString(R.string.menu_item_notification_power_text), 5);
        notificationManager.notify(2017061901, a2);
    }

    public static void a(Context context, String str) {
        Notification a2 = a(context, context.getString(R.string.auto_task_notify_exit_task_title), str, (String) null, (PendingIntent) null, true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.notify(2017061901, a2);
    }

    public static void a(Context context, String str, long j) {
        Notification a2 = a(context, context.getString(R.string.auto_task_notify_do_task_title), str, context.getString(R.string.auto_task_notify_task_exit_task), g(context, j), false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.notify(2017061901, a2);
    }

    public static void a(Context context, String str, long j, boolean z) {
        Notification a2 = a(context, context.getString(!z ? R.string.auto_task_notify_task_count_down : R.string.auto_task_notify_task_cancel_count_down), str, context.getString(R.string.auto_task_notify_task_cancel_do), e(context, j), false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.powercenter", context.getResources().getString(R.string.menu_item_notification_power_text), 5);
        notificationManager.notify(2017061901, a2);
    }

    private static PendingIntent b(Context context) {
        return PendingIntent.getActivity(context, 0, new Intent(context, AutoTaskManageActivity.class), 134217728);
    }

    public static void b(Context context, long j) {
        a(context, j, (int) R.string.auto_task_notify_task_canceled_summary3);
    }

    public static void c(Context context, long j) {
        a(context, j, (int) R.string.auto_task_notify_task_canceled_summary4);
    }

    public static void d(Context context, long j) {
        a(context, j, (int) R.string.auto_task_notify_task_canceled_summary2);
    }

    private static PendingIntent e(Context context, long j) {
        Intent intent = new Intent(context, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.CANCEL_APPLY_AUTO_TASK_ALARM");
        intent.putExtra("task_id", j);
        return PendingIntent.getService(context, 0, intent, 134217728);
    }

    private static PendingIntent f(Context context, long j) {
        Intent intent = new Intent(context, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.APPLY_AUTO_TASK_NOW");
        intent.putExtra("task_id", j);
        return PendingIntent.getService(context, 0, intent, 134217728);
    }

    private static PendingIntent g(Context context, long j) {
        Intent intent = new Intent(context, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.APPLY_AUTO_TASK_ALARM");
        intent.putExtra("task_id", j);
        intent.putExtra("task_restore", true);
        intent.putExtra("hide_notification", true);
        return PendingIntent.getService(context, 0, intent, 134217728);
    }
}
