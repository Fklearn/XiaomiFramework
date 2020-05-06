package com.android.server.wifi;

import android.app.MiuiNotification;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import miui.app.constants.ThemeManagerConstants;

class WifiApNotificationManager {
    private final String DISABLE_TETHERING_ACTION = "DisableTetheringAction";
    private String HOTSPOT_NOTIFICATION = "HOTSPOT_NOTIFICATION";
    private String HOTSPOT_NOTIFICATION_NAME = "Hotspot Notification";
    private final String TAG = "WifiApNotificationManager";
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
            if (cm != null) {
                cm.stopTethering(0);
            }
        }
    };
    private final Context mContext;
    private final Handler mHandler;
    private Notification.Builder softApNotificationBuilder;

    public WifiApNotificationManager(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void showSoftApClientsNotification(int num) {
        CharSequence message;
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        if (notificationManager != null) {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.Settings$TetherSettingsActivity");
            PendingIntent pi = PendingIntent.getActivityAsUser(this.mContext, 0, intent, 0, (Bundle) null, UserHandle.CURRENT);
            Resources r = Resources.getSystem();
            CharSequence title = r.getText(17041227);
            int size = num;
            if (size == 0) {
                message = r.getText(17041225);
            } else if (size == 1) {
                message = String.format(r.getText(17041226).toString(), new Object[]{Integer.valueOf(size)});
            } else {
                message = String.format(r.getText(17041224).toString(), new Object[]{Integer.valueOf(size)});
            }
            if (this.softApNotificationBuilder == null) {
                NotificationChannel channel = new NotificationChannel(this.HOTSPOT_NOTIFICATION, this.HOTSPOT_NOTIFICATION_NAME, 3);
                channel.setSound((Uri) null, (AudioAttributes) null);
                notificationManager.createNotificationChannel(channel);
                PendingIntent actionPi = PendingIntent.getBroadcast(this.mContext, 0, new Intent("DisableTetheringAction"), 0);
                Notification.Action action = new Notification.Action(285671519, this.mContext.getString(286130549), actionPi);
                Bundle bundle = new Bundle();
                bundle.putBoolean(MiuiNotification.EXTRA_SHOW_ACTION, true);
                this.softApNotificationBuilder = new Notification.Builder(this.mContext, this.HOTSPOT_NOTIFICATION);
                PendingIntent pendingIntent = actionPi;
                this.softApNotificationBuilder.setWhen(0).setOngoing(true).setColor(this.mContext.getColor(17170460)).setVisibility(1).setCategory("status").setExtras(bundle).setActions(new Notification.Action[]{action});
                Intent intent2 = intent;
                this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("DisableTetheringAction"), (String) null, this.mHandler);
            }
            this.softApNotificationBuilder.setSmallIcon(285671623).setContentTitle(title).setContentText(message).setContentIntent(pi).setPriority(2);
            notificationManager.notify(this.HOTSPOT_NOTIFICATION, 17303636, this.softApNotificationBuilder.build());
        }
    }

    public void clearSoftApClientsNotification() {
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        if (notificationManager != null && this.softApNotificationBuilder != null) {
            notificationManager.cancel(this.HOTSPOT_NOTIFICATION, 17303636);
            this.softApNotificationBuilder = null;
            this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        }
    }
}
