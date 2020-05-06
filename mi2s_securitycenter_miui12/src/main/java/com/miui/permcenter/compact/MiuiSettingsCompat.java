package com.miui.permcenter.compact;

import android.content.ContentResolver;
import android.content.Context;
import java.util.List;

public class MiuiSettingsCompat {
    public static final String TAG = "MiuiSettingsCompat";

    public static boolean getCloudDataBoolean(ContentResolver contentResolver, String str, String str2, boolean z) {
        try {
            Class<?> cls = Class.forName("android.provider.MiuiSettings$SettingsCloudData");
            Class[] clsArr = {ContentResolver.class, String.class, String.class, Boolean.TYPE};
            return ((Boolean) ReflectUtilHelper.callStaticObjectMethod(TAG, cls, Boolean.TYPE, "getCloudDataBoolean", clsArr, contentResolver, str, str2, Boolean.valueOf(z))).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getCloudDataInt(ContentResolver contentResolver, String str, String str2, int i) {
        try {
            Class<?> cls = Class.forName("android.provider.MiuiSettings$SettingsCloudData");
            Class[] clsArr = {ContentResolver.class, String.class, String.class, Integer.TYPE};
            return ((Integer) ReflectUtilHelper.callStaticObjectMethod(TAG, cls, Integer.TYPE, "getCloudDataInt", clsArr, contentResolver, str, str2, Integer.valueOf(i))).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static List<Object> getCloudDataList(ContentResolver contentResolver, String str) {
        try {
            return (List) ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("android.provider.MiuiSettings$SettingsCloudData"), List.class, "getCloudDataList", new Class[]{ContentResolver.class, String.class}, contentResolver, str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCloudDataString(ContentResolver contentResolver, String str, String str2, String str3) {
        try {
            return (String) ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("android.provider.MiuiSettings$SettingsCloudData"), String.class, "getCloudDataString", new Class[]{ContentResolver.class, String.class, String.class, String.class}, contentResolver, str, str2, str3);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isInstallMonitorEnabled(Context context) {
        try {
            Class[] clsArr = {Context.class};
            return ((Boolean) ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("android.provider.MiuiSettings$AntiVirus"), Boolean.TYPE, "isInstallMonitorEnabled", clsArr, context)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isNavigationBarFullScreen(Context context, String str) {
        try {
            Class[] clsArr = {ContentResolver.class, String.class};
            return ((Boolean) ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("android.provider.MiuiSettings$Global"), Boolean.TYPE, "getBoolean", clsArr, context.getContentResolver(), str)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setInstallMonitorEnabled(Context context, boolean z) {
        try {
            ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("android.provider.MiuiSettings$AntiVirus"), "setInstallMonitorEnabled", new Class[]{Context.class, Boolean.TYPE}, context, Boolean.valueOf(z));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
