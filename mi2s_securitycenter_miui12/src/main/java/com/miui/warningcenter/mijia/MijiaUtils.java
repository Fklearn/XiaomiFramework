package com.miui.warningcenter.mijia;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import b.b.c.j.v;
import b.b.o.a.a;
import com.miui.common.persistence.b;
import com.miui.securitycenter.R;

public class MijiaUtils {
    private static Bitmap getBitmap(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.warningcenter_icon_mijia, options).copy(Bitmap.Config.ARGB_8888, true);
    }

    public static String getPreviousAccount() {
        return b.a(MijiaConstants.PREFERENCE_KEY_PREVIOUS_ACCOUNT, "");
    }

    public static String getPreviousServer() {
        return b.a(MijiaConstants.PREFERENCE_KEY_PREVIOUS_SERVER, "");
    }

    public static boolean isFirstUseMijiaWarning() {
        return b.a(MijiaConstants.PREFERENCE_KEY_FIRST_USE_MIJIA_WARNING, true);
    }

    public static boolean isMijiaWarningOpen() {
        return b.a(MijiaConstants.PREFERENCE_KEY_MIJIA_WARNING_OPEN, true);
    }

    public static void setFirstUseMijiaWarning(boolean z) {
        b.b(MijiaConstants.PREFERENCE_KEY_FIRST_USE_MIJIA_WARNING, z);
    }

    public static void setMijiaWarningOpen(boolean z) {
        b.b(MijiaConstants.PREFERENCE_KEY_MIJIA_WARNING_OPEN, z);
    }

    public static void setPreviousAccount(String str) {
        b.b(MijiaConstants.PREFERENCE_KEY_PREVIOUS_ACCOUNT, str);
    }

    public static void setPreviousServer(String str) {
        b.b(MijiaConstants.PREFERENCE_KEY_PREVIOUS_SERVER, str);
    }

    public static void showFirstUseMijiaNoti(Context context) {
        Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
        a2.setSmallIcon(R.drawable.warningcenter_icon_mijia);
        a2.setContentTitle(context.getResources().getString(R.string.warningcenter_mijia_notification_title));
        a2.setContentText(context.getResources().getString(R.string.warningcenter_mijia_notification_summary));
        a2.setShowWhen(false);
        a2.setAutoCancel(true);
        a2.setLargeIcon(getBitmap(context));
        if (Build.VERSION.SDK_INT < 26) {
            a2.setPriority(2);
            a2.setDefaults(-1);
        }
        Intent intent = new Intent(context, WarningCenterMijiaActivity.class);
        intent.setFlags(268435456);
        a2.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
        Notification build = a2.build();
        a.a(build, true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (Build.VERSION.SDK_INT < 26) {
            build.defaults = -1;
        }
        v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.notify(R.string.ew_push_title, build);
    }
}
