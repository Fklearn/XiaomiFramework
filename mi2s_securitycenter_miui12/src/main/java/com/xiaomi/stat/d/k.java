package com.xiaomi.stat.d;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.stat.ak;

public class k {

    /* renamed from: a  reason: collision with root package name */
    public static final String f8533a = "http://test.data.mistat.xiaomi.srv/get_all_config";

    /* renamed from: b  reason: collision with root package name */
    public static final String f8534b = "http://test-idservice.data.mistat.xiaomi.com/deviceid_get";

    /* renamed from: c  reason: collision with root package name */
    public static final String f8535c = "http://test.data.mistat.xiaomi.srv/mistats/v3";

    /* renamed from: d  reason: collision with root package name */
    private static final String f8536d = "MI_STAT";
    private static final String e = "com.xiaomi.stat.demo";
    private static boolean f = false;
    private static final int g = 4000;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public static final int f8537a = 0;

        /* renamed from: b  reason: collision with root package name */
        public static final int f8538b = 1;

        /* renamed from: c  reason: collision with root package name */
        public static final int f8539c = 2;

        /* renamed from: d  reason: collision with root package name */
        public static final int f8540d = 3;
        public static final int e = 4;
    }

    public static String a(Throwable th) {
        return Log.getStackTraceString(th);
    }

    private static void a(int i, String str, String str2, Throwable th) {
        boolean isEmpty = TextUtils.isEmpty(str);
        String str3 = f8536d;
        if (!isEmpty && !TextUtils.equals(str, str3)) {
            str3 = str3.concat("_").concat(str);
        }
        if (i == 0) {
            Log.v(str3, str2, th);
        } else if (i == 1) {
            Log.i(str3, str2, th);
        } else if (i == 2) {
            Log.d(str3, str2, th);
        } else if (i == 3) {
            Log.w(str3, str2, th);
        } else if (i == 4) {
            Log.e(str3, str2, th);
        }
    }

    public static void a(String str) {
        a(f8536d, str);
    }

    public static void a(String str, String str2) {
        if (f) {
            b(0, str, str2, (Throwable) null);
        }
    }

    public static void a(String str, String str2, String str3) {
        if (f) {
            b(str, str2 + " " + str3);
        }
    }

    public static void a(String str, String str2, Throwable th) {
        if (f) {
            b(0, str, str2, th);
        }
    }

    public static void a(String str, Throwable th) {
        if (f) {
            b(3, str, (String) null, th);
        }
    }

    public static void a(boolean z) {
        f = z;
    }

    public static boolean a() {
        return f;
    }

    private static void b(int i, String str, String str2, Throwable th) {
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        if (str2.length() > g) {
            a(i, str, str2.substring(0, g), (Throwable) null);
            b(i, str, str2.substring(g, str2.length()), (Throwable) null);
            return;
        }
        a(i, str, str2, th);
    }

    public static void b(String str) {
        b(f8536d, str);
    }

    public static void b(String str, String str2) {
        if (f) {
            b(2, str, str2, (Throwable) null);
        }
    }

    public static void b(String str, String str2, Throwable th) {
        if (f) {
            b(2, str, str2, th);
        }
    }

    public static boolean b() {
        Context a2;
        try {
            if (!a() || (a2 = ak.a()) == null || !TextUtils.equals(e, a2.getPackageName())) {
                return false;
            }
            return a2.getSharedPreferences("demo_config", 0).getBoolean("mistat_test_url", false);
        } catch (Throwable unused) {
            return false;
        }
    }

    public static void c(String str) {
        c(f8536d, str);
    }

    public static void c(String str, String str2) {
        if (f) {
            b(1, str, str2, (Throwable) null);
        }
    }

    public static void c(String str, String str2, Throwable th) {
        if (f) {
            b(1, str, str2, th);
        }
    }

    public static void d(String str) {
        d(f8536d, str);
    }

    public static void d(String str, String str2) {
        if (f) {
            b(3, str, str2, (Throwable) null);
        }
    }

    public static void d(String str, String str2, Throwable th) {
        if (f) {
            b(4, str, str2, th);
        }
    }

    public static void e(String str) {
        e(f8536d, str);
    }

    public static void e(String str, String str2) {
        if (f) {
            b(4, str, str2, (Throwable) null);
        }
    }
}
