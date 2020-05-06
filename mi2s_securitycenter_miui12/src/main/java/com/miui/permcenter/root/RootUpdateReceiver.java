package com.miui.permcenter.root;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.util.Log;
import b.b.c.j.v;
import com.miui.permcenter.MainAcitivty;
import com.miui.permcenter.compact.MiuiNotificationCompat;
import com.miui.securitycenter.R;

public class RootUpdateReceiver extends BroadcastReceiver {
    private void a(Context context) {
        Intent intent = new Intent(context, MainAcitivty.class);
        intent.setFlags(268435456);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 1073741824);
        Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
        a2.setDefaults(4);
        a2.setContentTitle(context.getResources().getString(R.string.pm_root_success_notification_title));
        a2.setContentText(context.getResources().getString(R.string.pm_root_success_notification_infomation));
        a2.setContentIntent(activity);
        a2.setSmallIcon(R.drawable.ic_license_manage_small_icon);
        a2.setWhen(System.currentTimeMillis());
        a2.setOngoing(false);
        a2.setAutoCancel(true);
        a2.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_license_manage));
        a2.setPriority(1);
        a2.setSound(Uri.EMPTY, (AudioAttributes) null);
        Notification build = a2.build();
        MiuiNotificationCompat.setEnableKeyguard(false);
        MiuiNotificationCompat.setCustomizedIcon(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.notify(65533, build);
    }

    public void onReceive(Context context, Intent intent) {
        String action;
        Log.d("RootUpdateReceiver", "receive broadcast");
        if (intent != null && (action = intent.getAction()) != null && "com.android.updater.action.ACQUIRED_ROOT_SUCCESSED".equals(action)) {
            a(context);
        }
    }
}
