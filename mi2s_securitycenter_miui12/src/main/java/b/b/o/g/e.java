package b.b.o.g;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class e {
    public static <T> T a(Class<?> cls, Class<T> cls2, String str, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke((Object) null, objArr);
    }

    public static Object a(Class<? extends Object> cls, Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static Object a(Class<?> cls, String str) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get((Object) null);
    }

    public static <T> T a(Class<?> cls, String str, Class<T> cls2) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get((Object) null);
    }

    public static Object a(Class<?> cls, String str, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke((Object) null, objArr);
    }

    public static Object a(Object obj, Class<?> cls, String str) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    public static <T> T a(Object obj, Class<T> cls, String str, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static Object a(Object obj, String str) {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    public static <T> T a(Object obj, String str, Class<T> cls) {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    public static Object a(Object obj, String str, Class<?> cls, Class<?>[] clsArr, Object... objArr) {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static Object a(Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        return obj.getClass().getDeclaredMethod(str, clsArr).invoke(obj, objArr);
    }

    public static void a(Class<?> cls, String str, Object obj) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        declaredField.set((Object) null, obj);
    }

    public static void a(Object obj, Class<?> cls, String str, Object obj2) {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        declaredField.set(obj, obj2);
    }

    public static void a(Object obj, String str, Object obj2) {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        declaredField.set(obj, obj2);
    }

    public static <T> T b(Object obj, Class<T> cls, String str, Class<?>[] clsArr, Object... objArr) {
        Method method = obj.getClass().getMethod(str, clsArr);
        method.setAccessible(true);
        return method.invoke(obj, objArr);
    }

    public static Object b(Object obj, String str) {
        Field field = obj.getClass().getField(str);
        field.setAccessible(true);
        return field.get(obj);
    }

    public static Object b(Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        return obj.getClass().getMethod(str, clsArr).invoke(obj, objArr);
    }
}
