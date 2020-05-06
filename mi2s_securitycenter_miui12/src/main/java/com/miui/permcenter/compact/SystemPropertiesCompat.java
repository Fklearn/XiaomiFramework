package com.miui.permcenter.compact;

import android.util.Log;

public class SystemPropertiesCompat {
    private static final String TAG = "SystemPropertiesCompat";

    public static boolean getBoolean(String str, boolean z) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Class[] clsArr = {String.class, Boolean.TYPE};
            return ((Boolean) ReflectUtilHelper.callStaticObjectMethod(TAG, cls, Boolean.TYPE, "getBoolean", clsArr, str, Boolean.valueOf(z))).booleanValue();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return false;
        }
    }

    public static int getInt(String str, int i) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Class[] clsArr = {String.class, Integer.TYPE};
            return ((Integer) ReflectUtilHelper.callStaticObjectMethod(TAG, cls, Integer.TYPE, "getInt", clsArr, str, Integer.valueOf(i))).intValue();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return 0;
        }
    }

    public static String getString(String str, String str2) {
        try {
            return (String) ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("android.os.SystemProperties"), String.class, "get", new Class[]{String.class, String.class}, str, str2);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    public static int set(String str, String str2) {
        try {
            Class[] clsArr = {String.class, String.class};
            return ((Integer) ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("android.os.SystemProperties"), Integer.TYPE, "set", clsArr, str, str2)).intValue();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return 0;
        }
    }
}
