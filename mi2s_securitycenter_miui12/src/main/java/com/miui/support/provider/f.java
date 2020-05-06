package com.miui.support.provider;

import android.content.ContentResolver;
import android.provider.Settings;
import b.b.c.j.B;
import b.b.o.g.e;

public class f {
    public static int a(ContentResolver contentResolver, String str, int i) {
        if (!B.i()) {
            return Settings.Secure.getInt(contentResolver, str, i);
        }
        Class cls = Integer.TYPE;
        Class[] clsArr = {ContentResolver.class, String.class, cls, cls};
        try {
            return ((Integer) e.a((Class<?>) Settings.Secure.class, "getIntForUser", (Class<?>[]) clsArr, contentResolver, str, Integer.valueOf(i), 0)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    public static int a(ContentResolver contentResolver, String str, int i, int i2) {
        if (!B.i()) {
            return Settings.Secure.getInt(contentResolver, str, i);
        }
        Class cls = Integer.TYPE;
        Class[] clsArr = {ContentResolver.class, String.class, cls, cls};
        try {
            return ((Integer) e.a((Class<?>) Settings.Secure.class, "getIntForUser", (Class<?>[]) clsArr, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    public static long a(ContentResolver contentResolver, String str, long j) {
        if (!B.i()) {
            return Settings.Secure.getLong(contentResolver, str, j);
        }
        try {
            return ((Long) e.a((Class<?>) Settings.Secure.class, "getLongForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Long.TYPE, Integer.TYPE}, contentResolver, str, Long.valueOf(j), 0)).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return j;
        }
    }

    public static String a(ContentResolver contentResolver, String str) {
        if (!B.i()) {
            return Settings.Secure.getString(contentResolver, str);
        }
        try {
            return (String) e.a((Class<?>) Settings.Secure.class, "getStringForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE}, contentResolver, str, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void b(ContentResolver contentResolver, String str, int i, int i2) {
        if (B.i()) {
            Class cls = Integer.TYPE;
            Class[] clsArr = {ContentResolver.class, String.class, cls, cls};
            Class<Settings.Secure> cls2 = Settings.Secure.class;
            try {
                e.a((Class<?>) cls2, "putIntForUser", (Class<?>[]) clsArr, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Settings.Secure.putInt(contentResolver, str, i);
        }
    }

    public static boolean b(ContentResolver contentResolver, String str, int i) {
        if (!B.i()) {
            return Settings.Secure.putInt(contentResolver, str, i);
        }
        Class cls = Integer.TYPE;
        Class[] clsArr = {ContentResolver.class, String.class, cls, cls};
        try {
            return ((Boolean) e.a((Class<?>) Settings.Secure.class, "putIntForUser", (Class<?>[]) clsArr, contentResolver, str, Integer.valueOf(i), 0)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean b(ContentResolver contentResolver, String str, long j) {
        if (!B.i()) {
            return Settings.Secure.putLong(contentResolver, str, j);
        }
        try {
            return ((Boolean) e.a((Class<?>) Settings.Secure.class, "putLongForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Long.TYPE, Integer.TYPE}, contentResolver, str, Long.valueOf(j), 0)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
