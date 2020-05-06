package com.miui.activityutil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class q {
    private static Object a(Class cls, String str) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get((Object) null);
    }

    public static Object a(Class cls, String str, Class[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke((Object) null, objArr);
    }

    private static Object a(Object obj, Class cls, String str) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    private static Object a(Object obj, String str) {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    private static Object a(Object obj, String str, Class cls, Class[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static Object a(Object obj, String str, Class[] clsArr, Object... objArr) {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    private static Object a(String str, String str2) {
        Field declaredField = Class.forName(str).getDeclaredField(str2);
        declaredField.setAccessible(true);
        return declaredField.get((Object) null);
    }

    public static Object a(String str, String str2, Class[] clsArr, Object... objArr) {
        return b((Class) Class.forName(str), str2, clsArr, objArr);
    }

    public static Object a(String str, Object[] objArr) {
        Class<?> cls = Class.forName(str);
        if (objArr == null) {
            return cls.newInstance();
        }
        Class[] clsArr = new Class[objArr.length];
        int length = objArr.length;
        for (int i = 0; i < length; i++) {
            clsArr[i] = objArr[i].getClass();
        }
        return cls.getConstructor(clsArr).newInstance(objArr);
    }

    private static void a(Class cls, String str, Object obj) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        declaredField.set((Object) null, obj);
    }

    private static void a(Object obj, Class cls, String str, Object obj2) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        declaredField.set(obj, obj2);
    }

    private static void a(Object obj, String str, Object obj2) {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        declaredField.set(obj, obj2);
    }

    private static Object b(Class cls, String str) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get((Object) null);
    }

    public static Object b(Class cls, String str, Class[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke((Object) null, objArr);
    }

    private static Object b(Object obj, String str) {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    public static Object b(Object obj, String str, Class[] clsArr, Object... objArr) {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    private static Object b(String str, String str2, Class[] clsArr, Object... objArr) {
        Object a2 = a(str, (Object[]) null);
        Method declaredMethod = a2.getClass().getDeclaredMethod(str2, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(a2, objArr);
    }

    private static Object b(String str, Object[] objArr) {
        return a(str, objArr);
    }

    private static Object c(String str, String str2, Class[] clsArr, Object... objArr) {
        Object a2 = a(str, (Object[]) null);
        Method declaredMethod = a2.getClass().getDeclaredMethod(str2, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(a2, objArr);
    }
}
