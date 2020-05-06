package com.miui.powercenter.bootshutdown;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import b.b.c.j.v;
import b.b.o.a.a;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.powercenter.utils.l;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import miui.os.Build;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private static Handler f6968a = new o(Looper.getMainLooper());

    public static boolean a(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getCallState() == 0;
    }

    public static void b(Context context) {
        f6968a.removeMessages(123);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.cancel(2017061301);
    }

    /* access modifiers changed from: private */
    public static void b(Context context, int i) {
        Message message = new Message();
        message.what = 123;
        message.obj = context;
        message.arg1 = i - 1;
        f6968a.sendMessageDelayed(message, 1000);
    }

    /* access modifiers changed from: private */
    public static void b(Context context, int i, int i2) {
        String format;
        int i3;
        f6968a.removeMessages(123);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        PendingIntent activity = PendingIntent.getActivity(context, 0, new Intent(context, PowerShutdownOnTime.class), 134217728);
        Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
        a2.setSmallIcon(R.drawable.powercenter_small_icon).setLargeIcon(l.a(context)).setContentIntent(activity).setShowWhen(false).setPriority(2).setSound(Uri.EMPTY, (AudioAttributes) null);
        if (i == 1) {
            a2.setContentTitle(String.format(context.getString(R.string.power_20s_shutdown), new Object[]{Integer.valueOf(i2)}));
            a2.setContentText(g(context));
            Notification.Action f = f(context);
            a2.addAction(f.icon, f.title, f.actionIntent);
            a2.setAutoCancel(false);
            Bundle bundle = new Bundle();
            bundle.putBoolean("miui.showAction", !Build.IS_INTERNATIONAL_BUILD);
            a2.setExtras(bundle);
        } else {
            if (i == 2) {
                format = String.format(context.getString(R.string.shutdown_cancel_reminder), new Object[0]);
                i3 = R.string.shutdown_cancel_reminder_detail;
            } else if (i == 3) {
                format = String.format(context.getString(R.string.shutdown_cancel_reminder), new Object[0]);
                i3 = R.string.shutdown_cancel_reminder_detail2;
            } else {
                return;
            }
            String format2 = String.format(context.getString(i3), new Object[0]);
            a2.setContentTitle(format);
            a2.setContentText(format2);
        }
        Notification build = a2.build();
        a.b(build, true);
        a.a(build, (long) DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        a.a(build, true);
        notificationManager.notify(2017061301, build);
    }

    public static void c(Context context) {
        b(context, 2, 0);
    }

    public static void d(Context context) {
        b(context, 3, 0);
    }

    public static void e(Context context) {
        b(context, 1, 20);
        b(context, 20);
    }

    private static Notification.Action f(Context context) {
        Intent intent = new Intent(context, ShutdownAlarmIntentService.class);
        intent.setAction("com.miui.powercenter.CANCEL_SHUTDOWN");
        return new Notification.Action(0, context.getResources().getString(17039360), PendingIntent.getService(context, 0, intent, 134217728));
    }

    private static String g(Context context) {
        String str;
        Object[] objArr;
        if (!y.m()) {
            return String.format(context.getString(R.string.boot_reminder), new Object[0]);
        }
        n a2 = m.a();
        long a3 = (long) a2.a();
        String b2 = a2.b();
        int i = (a3 > 1 ? 1 : (a3 == 1 ? 0 : -1));
        if (i > 0) {
            return String.format(context.getString(R.string.boot_time_day), new Object[]{Long.valueOf(a3), b2});
        }
        if (i == 0) {
            str = context.getString(R.string.boot_time_day_tomorrow);
            objArr = new Object[]{b2};
        } else {
            str = context.getString(R.string.boot_time);
            objArr = new Object[]{b2};
        }
        return String.format(str, objArr);
    }
}
