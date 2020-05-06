package com.xiaomi.stat.b;

import android.content.Context;
import com.xiaomi.stat.d.k;
import java.lang.reflect.Method;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8443a = "IdentifierManager";

    /* renamed from: b  reason: collision with root package name */
    private static Object f8444b;

    /* renamed from: c  reason: collision with root package name */
    private static Class<?> f8445c;

    /* renamed from: d  reason: collision with root package name */
    private static Method f8446d;
    private static Method e;
    private static Method f;
    private static Method g;

    static {
        try {
            f8445c = Class.forName("com.android.id.impl.IdProviderImpl");
            f8444b = f8445c.newInstance();
            f8446d = f8445c.getMethod("getUDID", new Class[]{Context.class});
            e = f8445c.getMethod("getOAID", new Class[]{Context.class});
            f = f8445c.getMethod("getVAID", new Class[]{Context.class});
            g = f8445c.getMethod("getAAID", new Class[]{Context.class});
        } catch (Exception e2) {
            k.d(f8443a, "reflect exception!", e2);
        }
    }

    public static String a(Context context) {
        return a(context, f8446d);
    }

    private static String a(Context context, Method method) {
        Object obj = f8444b;
        if (obj == null || method == null) {
            return "";
        }
        try {
            Object invoke = method.invoke(obj, new Object[]{context});
            return invoke != null ? (String) invoke : "";
        } catch (Exception e2) {
            k.d(f8443a, "invoke exception!", e2);
            return "";
        }
    }

    public static boolean a() {
        return (f8445c == null || f8444b == null) ? false : true;
    }

    public static String b(Context context) {
        return a(context, e);
    }

    public static String c(Context context) {
        return a(context, f);
    }

    public static String d(Context context) {
        return a(context, g);
    }
}
