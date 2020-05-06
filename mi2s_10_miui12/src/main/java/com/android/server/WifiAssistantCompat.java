package com.android.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.miui.R;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.notification.SystemNotificationChannels;

public class WifiAssistantCompat {
    private static final String TAG = "WifiAssistantCompat";

    public static void showValidationNotification(Context context, String tag, int netId, int eventId, boolean alert) {
        Log.d(TAG, "showValidationNotification: " + netId + " | " + eventId + " | " + alert);
        Intent intent = new Intent();
        intent.setAction("com.android.server.WIFI_ASSISTANT_NO_INTERNET");
        intent.putExtra("EXTRA_NETWORK_ID", netId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, netId, intent, 0);
        Notification.Builder builder = new Notification.Builder(context, alert ? SystemNotificationChannels.NETWORK_ALERTS : SystemNotificationChannels.NETWORK_STATUS);
        builder.setSmallIcon(17303569);
        builder.setContentTitle(context.getResources().getString(R.string.wifi_assistant_wifi_no_internet));
        builder.setContentText(context.getResources().getString(R.string.wifi_assistant_wifi_no_internet_detail));
        builder.setContentIntent(pendingIntent);
        builder.setLocalOnly(true);
        builder.setOnlyAlertOnce(true);
        try {
            ((NotificationManager) context.getSystemService("notification")).notifyAsUser(tag, eventId, builder.build(), UserHandle.ALL);
        } catch (Exception e) {
            Log.e(TAG, "Exception occur when show validation notification", e);
        }
    }
}
