package com.miui.activityutil;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Method;

public final class g {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2285a = "IdentifierManager";

    /* renamed from: b  reason: collision with root package name */
    private static Object f2286b;

    /* renamed from: c  reason: collision with root package name */
    private static Class f2287c;

    /* renamed from: d  reason: collision with root package name */
    private static Method f2288d;
    private static Method e;
    private static Method f;
    private static Method g;

    static {
        try {
            Class<?> cls = Class.forName("com.android.id.impl.IdProviderImpl");
            f2287c = cls;
            f2286b = cls.newInstance();
            f2288d = f2287c.getMethod("getUDID", new Class[]{Context.class});
            e = f2287c.getMethod("getOAID", new Class[]{Context.class});
            f = f2287c.getMethod("getVAID", new Class[]{Context.class});
            g = f2287c.getMethod("getAAID", new Class[]{Context.class});
        } catch (Exception e2) {
            Log.e(f2285a, "reflect exception!", e2);
        }
    }

    public static String a(Context context) {
        return a(context, e);
    }

    private static String a(Context context, Method method) {
        Object obj = f2286b;
        if (obj == null || method == null) {
            return null;
        }
        try {
            Object invoke = method.invoke(obj, new Object[]{context});
            if (invoke != null) {
                return (String) invoke;
            }
            return null;
        } catch (Exception e2) {
            Log.e(f2285a, "invoke exception!", e2);
            return null;
        }
    }

    public static boolean a() {
        return (f2287c == null || f2286b == null) ? false : true;
    }

    public static String b(Context context) {
        return a(context, f);
    }

    private static String c(Context context) {
        return a(context, f2288d);
    }

    private static String d(Context context) {
        return a(context, g);
    }
}
