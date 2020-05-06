package com.miui.luckymoney.utils;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import b.b.o.g.c;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.powercenter.utils.o;

public class SettingsUtil {
    private static final int DISABLE = 0;
    public static final int ENABLE = 1;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String QUIET_MODE_ENABLE = "quiet_mode_enable";
    private static final String TAG = "com.miui.luckymoney.utils.SettingsUtil";
    private static final String ZEN_MODE = "zen_mode";
    private static final int ZEN_MODE_IMPORTANT_INTERRUPTIONS = 1;
    private static final int ZEN_MODE_OFF = 0;

    public static void closeAccessibility(Context context, Class<?> cls) {
        String str;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String string = Settings.Secure.getString(contentResolver, "enabled_accessibility_services");
            String str2 = context.getPackageName() + "/" + cls.getCanonicalName();
            if (string != null && string.contains(str2)) {
                if (str2.equals(string)) {
                    str = string.replace(str2, "");
                } else {
                    str = string.replace(":" + str2, "").replace(str2 + ":", "");
                }
                Settings.Secure.putString(contentResolver, "enabled_accessibility_services", str);
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to enable accessibility", e);
        }
    }

    public static void closeNotificationListener(Context context, Class<?> cls) {
        String str;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String string = Settings.Secure.getString(contentResolver, ENABLED_NOTIFICATION_LISTENERS);
            String str2 = context.getPackageName() + "/" + cls.getCanonicalName();
            if (CommonConfig.getInstance(context).getXiaomiLuckyMoneyEnable()) {
                return;
            }
            if (!o.m(context)) {
                if (string != null && string.contains(str2)) {
                    if (str2.equals(string)) {
                        str = string.replace(str2, "");
                    } else {
                        str = string.replace(":" + str2, "").replace(str2 + ":", "");
                    }
                    Settings.Secure.putString(contentResolver, ENABLED_NOTIFICATION_LISTENERS, str);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to enable notification listeners", e);
        }
    }

    public static void enableAccessibility(Context context, Class<?> cls) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String string = Settings.Secure.getString(contentResolver, "enabled_accessibility_services");
            String str = context.getPackageName() + "/" + cls.getCanonicalName();
            if (string == null || !string.contains(str)) {
                if (string != null) {
                    str = string + ":" + str;
                }
                Settings.Secure.putString(contentResolver, "enabled_accessibility_services", str);
            }
            Settings.Secure.putInt(contentResolver, "accessibility_enabled", 1);
        } catch (Exception e) {
            Log.e(TAG, "failed to enable accessibility", e);
        }
    }

    public static void enableNotificationListener(Context context, Class<?> cls) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String string = Settings.Secure.getString(contentResolver, ENABLED_NOTIFICATION_LISTENERS);
            String str = context.getPackageName() + "/" + cls.getCanonicalName();
            if (string == null || !string.contains(str)) {
                if (string != null) {
                    str = string + ":" + str;
                }
                Settings.Secure.putString(contentResolver, ENABLED_NOTIFICATION_LISTENERS, str);
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to enable notification listeners", e);
        }
    }

    private static int getZenMode(NotificationManager notificationManager) {
        c.a a2 = c.a.a((Object) notificationManager);
        a2.a("getZenMode", (Class<?>[]) null, new Object[0]);
        return a2.c();
    }

    public static boolean isQuietModeEnable(Context context) {
        if (((AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).getRingerMode() != 2) {
            return true;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        int i = Build.VERSION.SDK_INT;
        return i < 21 ? Settings.System.getInt(context.getContentResolver(), QUIET_MODE_ENABLE, 0) == 1 : i < 23 ? Settings.Global.getInt(context.getContentResolver(), ZEN_MODE, 0) == 1 : getZenMode(notificationManager) == 1;
    }
}
