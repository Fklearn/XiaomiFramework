package com.miui.hybrid.accessory.a.a;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class a {
    public static int a(Context context) {
        return a(context, context.getPackageName());
    }

    public static int a(Context context, String str) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 16384);
        } catch (Exception e) {
            com.miui.hybrid.accessory.a.b.a.b("AppUtils", "Fail to getPackageInfo by " + str, e);
            packageInfo = null;
        }
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public static int a(Context context, String str, String str2, int i) {
        try {
            Bundle bundle = context.getApplicationContext().getPackageManager().getApplicationInfo(str, 128).metaData;
            return bundle != null ? bundle.getInt(str2) : i;
        } catch (PackageManager.NameNotFoundException e) {
            com.miui.hybrid.accessory.a.b.a.b("AppUtils", "Fail to getApplicationInfo by " + str, e);
            return -2;
        }
    }

    public static boolean b(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 128) != null;
        } catch (PackageManager.NameNotFoundException e) {
            com.miui.hybrid.accessory.a.b.a.b("AppUtils", "Fail to getPackageInfo by " + str, e);
            return false;
        }
    }
}
