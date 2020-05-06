package com.market.sdk.utils;

import android.util.Log;
import java.util.Map;
import miui.reflect.Field;
import miui.reflect.Method;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static Map<String, Method> f2246a = b.a();

    /* renamed from: b  reason: collision with root package name */
    private static Map<String, Field> f2247b = b.a();

    /* renamed from: c  reason: collision with root package name */
    private static Map<String, Class> f2248c = b.a();

    public static Class<?> a(String str) {
        Class<?> cls = f2248c.get(str);
        if (cls != null) {
            return cls;
        }
        try {
            cls = Class.forName(str);
            f2248c.put(str, cls);
            return cls;
        } catch (Exception e) {
            Log.e("ReflectUtils", e.toString(), e);
            return cls;
        }
    }

    public static <T> T a(Class<?> cls, Object obj, String str, String str2, Object... objArr) {
        try {
            Method a2 = a(cls, str, str2);
            if (a2 != null) {
                return a2.invokeObject(cls, obj, objArr);
            }
            return null;
        } catch (Throwable th) {
            Log.e("ReflectUtils", "Exception: " + th);
            return null;
        }
    }

    public static Method a(Class<?> cls, String str, String str2) {
        try {
            String b2 = b(cls, str, str2);
            Method method = f2246a.get(b2);
            if (method != null) {
                return method;
            }
            Method of = Method.of(cls, str, str2);
            f2246a.put(b2, of);
            return of;
        } catch (Throwable th) {
            Log.e("ReflectUtils", "Exception e: " + th);
            return null;
        }
    }

    private static String b(Class<?> cls, String str, String str2) {
        return cls.toString() + "/" + str + "/" + str2;
    }
}
