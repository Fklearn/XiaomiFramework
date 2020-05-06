package com.android.server.notification;

import android.app.Notification;
import android.content.Context;
import android.miui.R;
import android.os.Binder;
import android.provider.Settings;
import com.android.server.notification.NotificationRecord;
import java.util.Iterator;

public class NotificationLightController {
    public static NotificationRecord.Light customizeNotificationLight(NotificationManagerService service, NotificationRecord ledNotification) {
        customizeNotificationLight(service, ledNotification.getNotification(), service.getContext().getResources().getColor(17170726));
        int ledARGB = ledNotification.getNotification().ledARGB;
        if (ledARGB == 0) {
            return ledNotification.getLight();
        }
        return new NotificationRecord.Light(ledARGB, ledNotification.getNotification().ledOnMS, ledNotification.getNotification().ledOffMS);
    }

    public static void customizeNotificationLight(NotificationManagerService service, Notification notification, int defaultNotificationColor) {
        long identify = Binder.clearCallingIdentity();
        boolean customized = false;
        Iterator<String> it = service.mLights.iterator();
        while (it.hasNext()) {
            String light = it.next();
            if (light.contains("com.android.phone") || light.contains("com.android.server.telecom")) {
                customizeNotificationLight(service.getContext(), notification, "call_breathing_light_color", "call_breathing_light_freq", defaultNotificationColor);
                return;
            } else if (light.contains("com.android.mms")) {
                customizeNotificationLight(service.getContext(), notification, "mms_breathing_light_color", "mms_breathing_light_freq", defaultNotificationColor);
                customized = true;
            }
        }
        if (!customized) {
            if ((notification.defaults & 4) != 0) {
                customizeNotificationLight(service.getContext(), notification, "breathing_light_color", "breathing_light_freq", defaultNotificationColor);
            }
            Binder.restoreCallingIdentity(identify);
        }
    }

    private static void customizeNotificationLight(Context context, Notification notification, String colorKey, String freqKey, int defaultNotificationColor) {
        int defaultFreq = context.getResources().getInteger(R.integer.config_defaultNotificationLedFreq);
        notification.ledARGB = Settings.System.getIntForUser(context.getContentResolver(), colorKey, defaultNotificationColor, -2);
        int freq = Settings.System.getIntForUser(context.getContentResolver(), freqKey, defaultFreq, -2);
        int[] offOn = getLedPwmOffOn(freq < 0 ? defaultFreq : freq);
        notification.ledOnMS = offOn[1];
        notification.ledOffMS = offOn[0];
    }

    public static int[] getLedPwmOffOn(int totalLength) {
        int[] values = new int[2];
        values[0] = (totalLength / 4) * 3;
        values[1] = totalLength - values[0];
        return values;
    }
}
