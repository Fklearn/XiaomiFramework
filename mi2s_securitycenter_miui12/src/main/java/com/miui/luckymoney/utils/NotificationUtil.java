package com.miui.luckymoney.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.v;
import b.b.o.a.a;
import b.b.o.g.c;
import com.miui.securitycenter.R;

public class NotificationUtil {
    private static String NOTIFY_CHANNEL_ID = "luckymoney_notify_channel_high";
    private static int currentUniqueNotificationId = 0;
    private static final int[] prefDefinedNotificationIds = {R.raw.hongbao_arrived};

    public static void cancelNotification(Context context, int i) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        notificationManager.cancel(i);
        g.a(notificationManager, i);
    }

    public static void cancelNotificationDelay(final Context context, final int i, long j) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                NotificationUtil.cancelNotification(context, i);
            }
        }, j);
    }

    private static Uri getResourceUri(Context context, int i) {
        return Uri.parse("android.resource://" + context.getPackageName() + "/" + i);
    }

    public static synchronized int getUniqueNotificationId() {
        boolean z;
        int i;
        synchronized (NotificationUtil.class) {
            do {
                z = true;
                currentUniqueNotificationId++;
                if (currentUniqueNotificationId <= 0) {
                    currentUniqueNotificationId = 1;
                }
                int[] iArr = prefDefinedNotificationIds;
                int length = iArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        z = false;
                        continue;
                        break;
                    }
                    if (currentUniqueNotificationId == iArr[i2]) {
                        continue;
                        break;
                    }
                    i2++;
                }
            } while (z);
            i = currentUniqueNotificationId;
        }
        return i;
    }

    public static void notifyAsUser(NotificationManager notificationManager, String str, int i, Notification notification, UserHandle userHandle) {
        try {
            c.a((Object) notificationManager, "notifyAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Notification.class, UserHandle.class}, str, Integer.valueOf(i), notification, userHandle);
        } catch (Exception e) {
            Log.i("NotificationUtil", "notifyAsUser exception!!!", e);
        }
    }

    public static synchronized void playNotification(Context context, int i) {
        Notification notification;
        synchronized (NotificationUtil.class) {
            if (!SettingsUtil.isQuietModeEnable(context)) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                v.a(notificationManager, NOTIFY_CHANNEL_ID, context.getString(R.string.hongbao_name), 2);
                Notification.Builder a2 = v.a(context, NOTIFY_CHANNEL_ID);
                if (Build.VERSION.SDK_INT < 21) {
                    notification = a2.build();
                } else {
                    notification = a2.setSmallIcon(R.drawable.hongbao_launcher_small).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.hongbao_launcher)).setContentTitle(context.getResources().getString(R.string.hongbao_name)).setContentText(context.getResources().getString(R.string.float_window_tips)).setPriority(2).build();
                    a.a(notification, true);
                }
                playNotificationSound(context, i);
                notifyAsUser(notificationManager, (String) null, i, notification, B.e(B.j()));
            }
        }
    }

    public static void playNotificationSound(Context context, int i) {
        RingtoneManager.getRingtone(context, getResourceUri(context, i)).play();
    }

    public static void setAudioAttr(Notification notification) {
        try {
            c.a a2 = c.a.a("android.media.AudioAttributes$Builder");
            a2.a((Class<?>[]) null, new Object[0]);
            a2.a("setUsage", new Class[]{Integer.TYPE}, 1);
            a2.e();
            a2.a("setContentType", new Class[]{Integer.TYPE}, 2);
            a2.e();
            a2.a("build", (Class<?>[]) null, new Object[0]);
            c.a((Object) notification, "audioAttributes", a2.d());
        } catch (Exception e) {
            Log.i("NotificationUtil", "setAudioAttr exception!!!", e);
        }
    }

    public static void showFloatNotification(Context context, int i, CharSequence charSequence, CharSequence charSequence2, PendingIntent pendingIntent, int i2, boolean z, boolean z2) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, NOTIFY_CHANNEL_ID, context.getString(R.string.hongbao_name), 4);
        Notification.Builder a2 = v.a(context, NOTIFY_CHANNEL_ID);
        a2.setDefaults(32).setWhen(System.currentTimeMillis()).setPriority(2).setAutoCancel(true).setOngoing(false).setSmallIcon(R.drawable.hongbao_launcher_small).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.hongbao_launcher)).setContentIntent(pendingIntent).setContentTitle(charSequence).setContentText(charSequence2);
        Notification build = a2.build();
        if (z) {
            playNotificationSound(context, i2);
        }
        a.b(build, true);
        a.a(build, true);
        a.a(build, 0);
        if (z2) {
            cancelNotificationDelay(context, i, 6000);
        }
        g.a(notificationManager, i, build);
    }

    public static void showPushNotification(Context context, int i, PendingIntent pendingIntent, String str, String str2) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, NOTIFY_CHANNEL_ID, context.getResources().getString(R.string.hongbao_name), 4);
        Notification.Builder a2 = v.a(context, NOTIFY_CHANNEL_ID);
        a2.setWhen(System.currentTimeMillis()).setPriority(2).setDefaults(1).setAutoCancel(true).setOngoing(false).setSmallIcon(R.drawable.hongbao_launcher_small).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.hongbao_launcher)).setContentIntent(pendingIntent).setContentTitle(str).setContentText(str2);
        Notification build = a2.build();
        a.b(build, true);
        a.a(build, true);
        a.a(build, 0);
        g.a(notificationManager, i, build);
    }

    public static synchronized void stopNotification(Context context, int i) {
        synchronized (NotificationUtil.class) {
            ((NotificationManager) context.getSystemService("notification")).cancel(i);
        }
    }
}
