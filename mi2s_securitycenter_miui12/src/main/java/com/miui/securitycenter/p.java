package com.miui.securitycenter;

import android.content.pm.PackageInfo;
import android.util.Log;
import com.miui.gamebooster.c.b;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private static int f7500a = -1;

    /* renamed from: b  reason: collision with root package name */
    private static Object f7501b = new Object();

    public static int a() {
        int i = f7500a;
        if (i > 0) {
            return i;
        }
        synchronized (f7501b) {
            if (f7500a == -1) {
                try {
                    PackageInfo packageInfo = Application.d().getPackageManager().getPackageInfo("com.miui.securitycore", 0);
                    if (packageInfo != null) {
                        f7500a = packageInfo.versionCode;
                    }
                } catch (Exception unused) {
                    Log.w("Version", " CoreVersion error ");
                    f7500a = -2;
                }
            }
        }
        return f7500a;
    }

    public static int a(String str) {
        int i = -1;
        try {
            PackageInfo packageInfo = Application.d().getPackageManager().getPackageInfo(str, 0);
            if (packageInfo != null) {
                i = packageInfo.versionCode;
            }
        } catch (Exception unused) {
            Log.w("Version", str + " error ");
        }
        if (b.f4100a) {
            Log.i("Version", str + "Version:" + i);
        }
        return i;
    }
}
