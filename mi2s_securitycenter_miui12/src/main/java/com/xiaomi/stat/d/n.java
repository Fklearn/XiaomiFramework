package com.xiaomi.stat.d;

import android.text.TextUtils;
import java.util.regex.Pattern;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static final int f8549a = 64;

    /* renamed from: b  reason: collision with root package name */
    private static final int f8550b = 256;

    /* renamed from: c  reason: collision with root package name */
    private static final int f8551c = 10240;

    /* renamed from: d  reason: collision with root package name */
    private static final String f8552d = "mistat_";
    private static final String e = "mi_";
    private static final String f = "abtest_";
    private static final String g = "null";
    private static Pattern h = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");

    public static void a() {
        k.e("parameter number exceed limits");
    }

    public static boolean a(String str) {
        if (TextUtils.isEmpty(str) || str.length() > 64 || str.startsWith(f8552d) || str.startsWith(e) || str.startsWith(f)) {
            return false;
        }
        return h.matcher(str).matches();
    }

    public static boolean b(String str) {
        return str == null || str.length() <= f8550b;
    }

    public static String c(String str) {
        return str == null ? g : str;
    }

    public static boolean d(String str) {
        return str == null || str.length() <= f8551c;
    }

    public static void e(String str) {
        k.e("invalid parameter name: " + str);
    }

    public static void f(String str) {
        k.e("parameter value is too long: " + str);
    }
}
