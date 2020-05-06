package com.xiaomi.analytics.a.a;

import android.util.Log;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static boolean f8282a = false;

    public static String a(String str) {
        return "Analytics-Api-" + str;
    }

    public static void a(String str, String str2) {
        if (f8282a) {
            Log.d(a(str), str2);
        }
    }

    public static void a(String str, String str2, Throwable th) {
        if (f8282a) {
            Log.d(a(str), str2, th);
        }
    }

    public static void b(String str, String str2) {
        if (f8282a) {
            Log.i(a(str), str2);
        }
    }

    public static void b(String str, String str2, Throwable th) {
        if (f8282a) {
            Log.e(a(str), str2, th);
        }
    }

    public static void c(String str, String str2) {
        if (f8282a) {
            Log.w(a(str), str2);
        }
    }

    public static void c(String str, String str2, Throwable th) {
        if (f8282a) {
            Log.w(a(str), str2, th);
        }
    }
}
