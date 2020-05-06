package com.miui.permcenter.compact;

import b.b.o.g.e;
import miui.util.Log;

public class ReflectUtilHelper {
    public static final String TAG = "ReflectUtilHelper";

    public static <T> T callObjectMethod(String str, Object obj, Class<T> cls, String str2, Class<?>[] clsArr, Object... objArr) {
        try {
            return e.a(obj, cls, str2, clsArr, objArr);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static Object callObjectMethod(String str, Object obj, String str2, Class<?> cls, Class<?>[] clsArr, Object... objArr) {
        try {
            return e.a(obj, str2, cls, clsArr, objArr);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static Object callObjectMethod(String str, Object obj, String str2, Class<?>[] clsArr, Object... objArr) {
        try {
            return e.a(obj, str2, clsArr, objArr);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static <T> T callStaticObjectMethod(String str, Class<?> cls, Class<T> cls2, String str2, Class<?>[] clsArr, Object... objArr) {
        try {
            return e.a(cls, cls2, str2, clsArr, objArr);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static Object callStaticObjectMethod(String str, Class<?> cls, String str2, Class<?>[] clsArr, Object... objArr) {
        try {
            return e.a(cls, str2, clsArr, objArr);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static Object getObjectField(String str, Object obj, String str2) {
        try {
            return e.a(obj, str2);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static <T> T getObjectField(String str, Object obj, String str2, Class<T> cls) {
        try {
            return e.a(obj, str2, cls);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static Object getStaticObjectField(String str, Class<?> cls, String str2) {
        try {
            return e.a(cls, str2);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static <T> T getStaticObjectField(String str, Class<?> cls, String str2, Class<T> cls2) {
        try {
            return e.a(cls, str2, cls2);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
            return null;
        }
    }

    public static void setObjectField(String str, Object obj, Class<?> cls, String str2, Object obj2) {
        try {
            e.a(obj, cls, str2, obj2);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
        }
    }

    public static void setObjectField(String str, Object obj, String str2, Object obj2) {
        try {
            e.a(obj, str2, obj2);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
        }
    }

    public static void setStaticObjectField(String str, Class<?> cls, String str2, Object obj) {
        try {
            e.a(cls, str2, obj);
        } catch (Exception e) {
            Log.e(TAG, str + ":" + e.toString());
        }
    }
}
