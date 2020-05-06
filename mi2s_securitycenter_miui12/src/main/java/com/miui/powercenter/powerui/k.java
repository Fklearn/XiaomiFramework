package com.miui.powercenter.powerui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.miui.securitycenter.R;

public class k {

    /* renamed from: a  reason: collision with root package name */
    public static final int[] f7160a = {20, 10, -1};

    public static int a(int i) {
        int[] iArr = f7160a;
        return i < iArr[1] ? iArr[1] : iArr[0];
    }

    public static long a(long j) {
        return j / 3600000;
    }

    public static void a(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
            if (notificationManager.getNotificationChannel("battery") != null) {
                notificationManager.deleteNotificationChannel("battery");
            }
            NotificationChannel notificationChannel = new NotificationChannel("batteryNotice", context.getString(R.string.notification_channel_battery), 4);
            String string = Settings.Global.getString(context.getContentResolver(), "low_battery_sound");
            notificationChannel.setSound(Uri.parse("file://" + string), new AudioAttributes.Builder().setContentType(4).setUsage(10).build());
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static void a(Context context, int i) {
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, "batteryNotice") : new Notification.Builder(context);
        Intent intent = new Intent("miui.intent.action.POWER_MANAGER");
        int i2 = i <= 9 ? R.drawable.icon_9_percent : R.drawable.icon_19_percent;
        builder.setSmallIcon(R.drawable.powercenter_small_icon).setContentTitle(context.getString(R.string.notification_low_battery_title, new Object[]{a(i) + "%"})).setContentIntent(PendingIntent.getActivity(context, 0, intent, 0, (Bundle) null)).setAutoCancel(true).setShowWhen(true).setPriority(1);
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), i2));
        }
        if (d(context) || e(context) || miui.os.Build.IS_TABLET) {
            builder.setContentText(context.getString(R.string.notification_low_battery_need_charge));
        } else {
            String string = context.getString(R.string.notification_low_battery_action_btn);
            Intent intent2 = new Intent(context, PowerReceiver.class);
            intent2.setAction("com.android.systemui.OPEN_SAVE_MODE");
            Notification.Action action = new Notification.Action(0, string, PendingIntent.getBroadcast(context, 0, intent2, 0));
            builder.addAction(action.icon, action.title, action.actionIntent);
            builder.setContentText(context.getString(R.string.notification_low_battery_open_save_mode));
            Bundle bundle = new Bundle();
            bundle.putBoolean("miui.showAction", !miui.os.Build.IS_INTERNATIONAL_BUILD);
            builder.setExtras(bundle);
        }
        Notification build = builder.build();
        build.flags |= 1;
        build.defaults |= 4;
        build.sound = null;
        ((NotificationManager) context.getSystemService("notification")).notify((String) null, R.string.notification_low_battery_title, build);
    }

    public static void a(Context context, Uri uri, int i) {
        l.a(new j(context, uri, i));
    }

    public static void a(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("EXTREME_POWER_SAVE_MODE_OPEN", true);
        bundle.putBoolean("IS_NOTIFY", true);
        bundle.putString("SOURCE", str);
        try {
            context.getContentResolver().call(Uri.parse("content://com.miui.powerkeeper.configure"), "changeExtremePowerMode", (String) null, bundle);
        } catch (IllegalArgumentException e) {
            Log.e("PowerNoticeUtils", "enableExtremeSaveMode error", e);
        }
    }

    public static long b(long j) {
        return (j % 3600000) / 60000;
    }

    public static void b(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("POWER_SAVE_MODE_OPEN", true);
        bundle.putBoolean("LOW_BATTERY_DIALOG", true);
        try {
            context.getContentResolver().call(Uri.parse("content://com.miui.powercenter.powersaver"), "changePowerMode", (String) null, bundle);
        } catch (IllegalArgumentException e) {
            Log.e("PowerNoticeUtils", "enableSaveMode error", e);
        }
    }

    public static void c(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel((String) null, R.string.notification_low_battery_title);
    }

    public static boolean d(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "EXTREME_POWER_MODE_ENABLE", 0) == 1;
    }

    public static boolean e(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "POWER_SAVE_MODE_OPEN", 0) != 0;
    }

    public static boolean f(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "POWER_SAVE_GUIDE_ENABLE", 1) == 1;
    }

    public static void g(Context context) {
        Settings.Secure.putInt(context.getContentResolver(), "POWER_SAVE_GUIDE_ENABLE", 0);
    }

    public static void h(Context context) {
        try {
            context.getContentResolver().call(Uri.parse("content://com.miui.powercenter.powersaver"), "showLowBatteryDialog", (String) null, (Bundle) null);
        } catch (IllegalArgumentException e) {
            Log.e("PowerNoticeUtils", "showLowBatteryDialog error", e);
        }
    }
}
