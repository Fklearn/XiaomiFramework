package com.xiaomi.stat.d;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.xiaomi.stat.ak;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f8506a;

    /* renamed from: b  reason: collision with root package name */
    private static int f8507b;

    /* renamed from: c  reason: collision with root package name */
    private static String f8508c;

    public static int a() {
        if (!f8506a) {
            c();
        }
        return f8507b;
    }

    public static String b() {
        if (!f8506a) {
            c();
        }
        return f8508c;
    }

    private static void c() {
        if (!f8506a) {
            f8506a = true;
            Context a2 = ak.a();
            try {
                PackageInfo packageInfo = a2.getPackageManager().getPackageInfo(a2.getPackageName(), 0);
                f8507b = packageInfo.versionCode;
                f8508c = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
    }
}
