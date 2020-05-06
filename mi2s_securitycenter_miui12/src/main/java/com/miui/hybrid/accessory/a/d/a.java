package com.miui.hybrid.accessory.a.d;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final Map<Class<?>, Class<?>> f5488a = new HashMap();

    /* renamed from: com.miui.hybrid.accessory.a.d.a$a  reason: collision with other inner class name */
    public static class C0051a<T> {

        /* renamed from: a  reason: collision with root package name */
        public final Class<? extends T> f5489a;

        /* renamed from: b  reason: collision with root package name */
        public final T f5490b;
    }

    static {
        f5488a.put(Boolean.class, Boolean.TYPE);
        f5488a.put(Byte.class, Byte.TYPE);
        f5488a.put(Character.class, Character.TYPE);
        f5488a.put(Short.class, Short.TYPE);
        f5488a.put(Integer.class, Integer.TYPE);
        f5488a.put(Float.class, Float.TYPE);
        f5488a.put(Long.class, Long.TYPE);
        f5488a.put(Double.class, Double.TYPE);
        Map<Class<?>, Class<?>> map = f5488a;
        Class cls = Boolean.TYPE;
        map.put(cls, cls);
        Map<Class<?>, Class<?>> map2 = f5488a;
        Class cls2 = Byte.TYPE;
        map2.put(cls2, cls2);
        Map<Class<?>, Class<?>> map3 = f5488a;
        Class cls3 = Character.TYPE;
        map3.put(cls3, cls3);
        Map<Class<?>, Class<?>> map4 = f5488a;
        Class cls4 = Short.TYPE;
        map4.put(cls4, cls4);
        Map<Class<?>, Class<?>> map5 = f5488a;
        Class cls5 = Integer.TYPE;
        map5.put(cls5, cls5);
        Map<Class<?>, Class<?>> map6 = f5488a;
        Class cls6 = Float.TYPE;
        map6.put(cls6, cls6);
        Map<Class<?>, Class<?>> map7 = f5488a;
        Class cls7 = Long.TYPE;
        map7.put(cls7, cls7);
        Map<Class<?>, Class<?>> map8 = f5488a;
        Class cls8 = Double.TYPE;
        map8.put(cls8, cls8);
    }

    public static <T> T a(Class<?> cls, String str, Object... objArr) {
        return a(cls, str, (Class<?>[]) a(objArr)).invoke((Object) null, b(objArr));
    }

    public static <T> T a(Object obj, String str, Object... objArr) {
        try {
            return b(obj, str, objArr);
        } catch (Exception e) {
            com.miui.hybrid.accessory.a.b.a.a("JavaCalls", "Meet exception when call Method '" + str + "' in " + obj, e);
            return null;
        }
    }

    public static <T> T a(String str, String str2, Object... objArr) {
        try {
            return a(Class.forName(str), str2, objArr);
        } catch (Exception e) {
            com.miui.hybrid.accessory.a.b.a.a("JavaCalls", "Meet exception when call Method '" + str2 + "' in " + str, e);
            return null;
        }
    }

    private static Method a(Class<?> cls, String str, Class<?>... clsArr) {
        Method a2 = a(cls.getDeclaredMethods(), str, clsArr);
        if (a2 != null) {
            a2.setAccessible(true);
            return a2;
        } else if (cls.getSuperclass() != null) {
            return a((Class<?>) cls.getSuperclass(), str, clsArr);
        } else {
            throw new NoSuchMethodException();
        }
    }

    private static Method a(Method[] methodArr, String str, Class<?>[] clsArr) {
        if (str != null) {
            for (Method method : methodArr) {
                if (method.getName().equals(str) && a(method.getParameterTypes(), clsArr)) {
                    return method;
                }
            }
            return null;
        }
        throw new NullPointerException("Method name must not be null.");
    }

    private static boolean a(Class<?>[] clsArr, Class<?>[] clsArr2) {
        if (clsArr == null) {
            return clsArr2 == null || clsArr2.length == 0;
        }
        if (clsArr2 == null) {
            return clsArr.length == 0;
        }
        if (clsArr.length != clsArr2.length) {
            return false;
        }
        for (int i = 0; i < clsArr.length; i++) {
            if (!clsArr[i].isAssignableFrom(clsArr2[i]) && (!f5488a.containsKey(clsArr[i]) || !f5488a.get(clsArr[i]).equals(f5488a.get(clsArr2[i])))) {
                return false;
            }
        }
        return true;
    }

    private static Class<?>[] a(Object... objArr) {
        if (objArr == null || objArr.length <= 0) {
            return null;
        }
        Class[] clsArr = new Class[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            C0051a aVar = objArr[i];
            if (aVar == null || !(aVar instanceof C0051a)) {
                clsArr[i] = aVar == null ? null : aVar.getClass();
            } else {
                clsArr[i] = aVar.f5489a;
            }
        }
        return clsArr;
    }

    public static <T> T b(Object obj, String str, Object... objArr) {
        return a(obj.getClass(), str, (Class<?>[]) a(objArr)).invoke(obj, b(objArr));
    }

    private static Object[] b(Object... objArr) {
        if (objArr == null || objArr.length <= 0) {
            return null;
        }
        Object[] objArr2 = new Object[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            C0051a aVar = objArr[i];
            if (aVar == null || !(aVar instanceof C0051a)) {
                objArr2[i] = aVar;
            } else {
                objArr2[i] = aVar.f5490b;
            }
        }
        return objArr2;
    }
}
