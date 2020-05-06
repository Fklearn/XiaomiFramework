package com.miui.gamebooster.m;

import android.app.MiuiNotification;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import b.b.o.g.c;
import b.b.o.g.e;

/* renamed from: com.miui.gamebooster.m.o  reason: case insensitive filesystem */
public class C0384o {
    public static int a(ContentResolver contentResolver, String str, int i, int i2) {
        try {
            return ((Integer) e.a(Class.forName("android.provider.Settings$Secure"), Integer.TYPE, "getIntForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE}, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return 0;
        }
    }

    public static MiuiNotification a(Notification notification) {
        try {
            return (MiuiNotification) e.a((Object) notification, "extraNotification", MiuiNotification.class);
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return null;
        }
    }

    public static Object a(String str, String str2, ContentResolver contentResolver, String str3) {
        try {
            return e.a(Class.forName(str), str2, (Class<?>[]) new Class[]{ContentResolver.class, String.class}, contentResolver, str3);
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return null;
        }
    }

    public static String a(String str) {
        try {
            return (String) e.a(Class.forName("android.content.Intent"), str, String.class);
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return "android.intent.action.USER_SWITCHED";
        }
    }

    public static String a(String str, String str2) {
        try {
            return (String) e.a(Class.forName(str), str2);
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return null;
        }
    }

    public static boolean a() {
        try {
            c.a a2 = c.a.a("miui.telephony.TelephonyManagerEx");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("isMultiSimEnabled", (Class<?>[]) null, new Object[0]);
            return ((Boolean) a2.d()).booleanValue();
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return false;
        }
    }

    public static boolean a(Context context, String str) {
        try {
            return ((Boolean) e.a(Class.forName("android.provider.MiuiSettings$Privacy"), Boolean.TYPE, "isEnabled", (Class<?>[]) new Class[]{Context.class, String.class}, context, str)).booleanValue();
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return false;
        }
    }

    public static Object b(String str, String str2) {
        try {
            return e.a(Class.forName(str), str2);
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return null;
        }
    }

    public static void b(ContentResolver contentResolver, String str, int i, int i2) {
        try {
            e.a(Class.forName("android.provider.Settings$Secure"), "putIntForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE}, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2));
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
        }
    }

    public static int c(ContentResolver contentResolver, String str, int i, int i2) {
        try {
            return ((Integer) e.a(Class.forName("android.provider.Settings$System"), Integer.TYPE, "getIntForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE}, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            return 0;
        }
    }

    public static void d(ContentResolver contentResolver, String str, int i, int i2) {
        try {
            e.a(Class.forName("android.provider.Settings$System"), "putIntForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE}, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2));
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
        }
    }
}
