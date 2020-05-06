package com.miui.support.provider;

import android.content.ContentResolver;
import android.provider.Settings;
import b.b.o.g.e;

public class g {
    public static int a(ContentResolver contentResolver, String str, int i, int i2) {
        Class cls = Integer.TYPE;
        Class[] clsArr = {ContentResolver.class, String.class, cls, cls};
        try {
            return ((Integer) e.a((Class<?>) Settings.System.class, "getIntForUser", (Class<?>[]) clsArr, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    public static boolean b(ContentResolver contentResolver, String str, int i, int i2) {
        Class cls = Integer.TYPE;
        Class[] clsArr = {ContentResolver.class, String.class, cls, cls};
        try {
            return ((Boolean) e.a((Class<?>) Settings.System.class, "putIntForUser", (Class<?>[]) clsArr, contentResolver, str, Integer.valueOf(i), Integer.valueOf(i2))).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
