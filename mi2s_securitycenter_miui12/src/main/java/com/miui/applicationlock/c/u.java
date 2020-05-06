package com.miui.applicationlock.c;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import b.b.o.g.e;

public class u {
    public static boolean a(Context context) {
        try {
            return a(context, "AppLockSettings", "SupportMiniCard", false);
        } catch (Exception e) {
            Log.e("CloudControlHelper", "loadPicOptApps failed : " + e.toString());
            return true;
        }
    }

    public static boolean a(Context context, String str, String str2, boolean z) {
        Class<?> cls = Class.forName("android.provider.MiuiSettings$SettingsCloudData");
        Class cls2 = Boolean.TYPE;
        return ((Boolean) e.a(cls, cls2, "getCloudDataBoolean", (Class<?>[]) new Class[]{ContentResolver.class, String.class, String.class, cls2}, context.getContentResolver(), str, str2, Boolean.valueOf(z))).booleanValue();
    }
}
