package com.miui.permcenter.compact;

import android.app.MiuiNotification;
import android.util.Log;

public class MiuiNotificationCompat {
    public static final String TAG = "MiuiNotificationCompat";

    public static void setCustomizedIcon(boolean z) {
        try {
            ((MiuiNotification) ReflectUtilHelper.getObjectField(TAG, Class.forName("android.app.Notification"), "extraNotification", MiuiNotification.class)).setCustomizedIcon(z);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public static void setEnableFloat(boolean z) {
        try {
            ((MiuiNotification) ReflectUtilHelper.getObjectField(TAG, Class.forName("android.app.Notification"), "extraNotification", MiuiNotification.class)).setEnableFloat(z);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public static void setEnableKeyguard(boolean z) {
        try {
            ((MiuiNotification) ReflectUtilHelper.getObjectField(TAG, Class.forName("android.app.Notification"), "extraNotification", MiuiNotification.class)).setEnableKeyguard(z);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public static void setFloatTime(int i) {
        try {
            ((MiuiNotification) ReflectUtilHelper.getObjectField(TAG, Class.forName("android.app.Notification"), "extraNotification", MiuiNotification.class)).setFloatTime(i);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
}
