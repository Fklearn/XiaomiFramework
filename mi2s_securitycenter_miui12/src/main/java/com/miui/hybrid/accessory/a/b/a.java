package com.miui.hybrid.accessory.a.b;

import android.util.Log;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static int f5478a = 4;

    public static void a(String str, String str2) {
        if (f5478a <= 2) {
            Log.v(str, str2);
        }
    }

    public static void a(String str, String str2, Throwable th) {
        if (f5478a <= 5) {
            Log.w(str, str2, th);
        }
    }

    public static void b(String str, String str2) {
        if (f5478a <= 4) {
            Log.i(str, str2);
        }
    }

    public static void b(String str, String str2, Throwable th) {
        if (f5478a <= 6) {
            Log.e(str, str2, th);
        }
    }

    public static void c(String str, String str2) {
        if (f5478a <= 3) {
            Log.d(str, str2);
        }
    }

    public static void d(String str, String str2) {
        if (f5478a <= 5) {
            Log.w(str, str2);
        }
    }

    public static void e(String str, String str2) {
        if (f5478a <= 6) {
            Log.e(str, str2);
        }
    }
}
