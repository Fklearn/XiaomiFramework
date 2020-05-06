package com.miui.maml.util;

import android.text.TextUtils;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReflectionHelper {
    static HashMap<String, Class<?>> PRIMITIVE_TYPE = new HashMap<>();
    private static Map<String, Constructor> sConstructorCache = new HashMap();
    private static Map<String, Field> sFieldCache = new HashMap();
    private static Method sForNameMethod = null;
    private static Method sGetDeclaredConstructorMethod = null;
    private static Method sGetDeclaredFieldMethod = null;
    private static Method sGetDeclaredMethodMethod = null;
    private static Method sGetMethod = null;
    private static Method sInvokeMethod = null;
    private static Map<String, Method> sMethodCache = new HashMap();
    private static Method sNewInstanceMethod = null;
    private static Method sSetAccessibleMethod = null;
    private static Method sSetMethod = null;

    static {
        PRIMITIVE_TYPE.put("byte", Byte.TYPE);
        PRIMITIVE_TYPE.put("short", Short.TYPE);
        PRIMITIVE_TYPE.put("int", Integer.TYPE);
        PRIMITIVE_TYPE.put("long", Long.TYPE);
        PRIMITIVE_TYPE.put("char", Character.TYPE);
        PRIMITIVE_TYPE.put("boolean", Boolean.TYPE);
        PRIMITIVE_TYPE.put("float", Float.TYPE);
        PRIMITIVE_TYPE.put("double", Double.TYPE);
        PRIMITIVE_TYPE.put("byte[]", byte[].class);
        PRIMITIVE_TYPE.put("short[]", short[].class);
        PRIMITIVE_TYPE.put("int[]", int[].class);
        PRIMITIVE_TYPE.put("long[]", long[].class);
        PRIMITIVE_TYPE.put("char[]", char[].class);
        PRIMITIVE_TYPE.put("boolean[]", boolean[].class);
        PRIMITIVE_TYPE.put("float[]", float[].class);
        PRIMITIVE_TYPE.put("double[]", double[].class);
    }

    private static Class forNameInternal(String str) {
        if (sForNameMethod == null) {
            sForNameMethod = Class.class.getMethod("forName", new Class[]{String.class});
        }
        return (Class) sForNameMethod.invoke((Object) null, new Object[]{str});
    }

    private static String generateConstructorCacheKey(Class<?> cls, Class<?>... clsArr) {
        return cls.toString() + "/" + Arrays.toString(clsArr);
    }

    private static String generateFieldCacheKey(Class<?> cls, String str) {
        return cls.toString() + "/" + str;
    }

    private static String generateMethodCacheKey(Class<?> cls, String str, Class<?>[] clsArr) {
        return cls.toString() + "/" + str + "/" + Arrays.toString(clsArr);
    }

    public static Class<?> getClass(String str) {
        return forNameInternal(str);
    }

    public static Constructor getConstructor(Class<?> cls, Class<?>... clsArr) {
        String generateConstructorCacheKey = generateConstructorCacheKey(cls, clsArr);
        Constructor constructor = sConstructorCache.get(generateConstructorCacheKey);
        if (constructor != null) {
            return constructor;
        }
        Constructor declaredConstructorInternal = getDeclaredConstructorInternal(cls, clsArr);
        setAccessibleInternal(declaredConstructorInternal, true);
        sConstructorCache.put(generateConstructorCacheKey, declaredConstructorInternal);
        return declaredConstructorInternal;
    }

    public static <T> T getConstructorInstance(Class<?> cls, Class<?>[] clsArr, Object... objArr) {
        Constructor constructor = getConstructor(cls, clsArr);
        if (constructor == null) {
            return null;
        }
        return newInstanceInternal(constructor, objArr);
    }

    private static Constructor getDeclaredConstructorInternal(Object obj, Class<?>... clsArr) {
        if (sGetDeclaredConstructorMethod == null) {
            sGetDeclaredConstructorMethod = Class.class.getMethod("getDeclaredConstructor", new Class[]{Class[].class});
        }
        return (Constructor) sGetDeclaredConstructorMethod.invoke(obj, new Object[]{clsArr});
    }

    private static Field getDeclaredFieldInternal(Object obj, String str) {
        if (sGetDeclaredFieldMethod == null) {
            sGetDeclaredFieldMethod = Class.class.getMethod("getDeclaredField", new Class[]{String.class});
        }
        return (Field) sGetDeclaredFieldMethod.invoke(obj, new Object[]{str});
    }

    private static Method getDeclaredMethodInternal(Object obj, String str, Class<?>... clsArr) {
        if (sGetDeclaredMethodMethod == null) {
            sGetDeclaredMethodMethod = Class.class.getMethod("getDeclaredMethod", new Class[]{String.class, Class[].class});
        }
        return (Method) sGetDeclaredMethodMethod.invoke(obj, new Object[]{str, clsArr});
    }

    public static Object getEnumConstant(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                return Enum.valueOf(Class.forName(str), str2);
            } catch (ClassCastException | ClassNotFoundException | IllegalArgumentException unused) {
            }
        }
        return null;
    }

    public static Field getField(Class<?> cls, String str) {
        String generateFieldCacheKey = generateFieldCacheKey(cls, str);
        Field field = sFieldCache.get(generateFieldCacheKey);
        if (field != null) {
            return field;
        }
        Field declaredFieldInternal = getDeclaredFieldInternal(cls, str);
        setAccessibleInternal(declaredFieldInternal, true);
        sFieldCache.put(generateFieldCacheKey, declaredFieldInternal);
        return declaredFieldInternal;
    }

    public static <T> T getFieldValue(Class<?> cls, Object obj, String str) {
        Field field = getField(cls, str);
        if (field == null) {
            return null;
        }
        return getInternal(field, obj);
    }

    private static Object getInternal(Object obj, Object obj2) {
        if (sGetMethod == null) {
            sGetMethod = Field.class.getMethod("get", new Class[]{Object.class});
        }
        return sGetMethod.invoke(obj, new Object[]{obj2});
    }

    public static Method getMethod(Class<?> cls, String str, Class<?>... clsArr) {
        String generateMethodCacheKey = generateMethodCacheKey(cls, str, clsArr);
        Method method = sMethodCache.get(generateMethodCacheKey);
        if (method != null) {
            return method;
        }
        Method declaredMethodInternal = getDeclaredMethodInternal(cls, str, clsArr);
        setAccessibleInternal(declaredMethodInternal, true);
        sMethodCache.put(generateMethodCacheKey, declaredMethodInternal);
        return declaredMethodInternal;
    }

    public static void invoke(Class<?> cls, Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        Method method = getMethod(cls, str, clsArr);
        if (method != null) {
            invokeInternal(method, obj, objArr);
        }
    }

    private static Object invokeInternal(Object obj, Object... objArr) {
        if (sInvokeMethod == null) {
            sInvokeMethod = Method.class.getMethod("invoke", new Class[]{Object.class, Object[].class});
        }
        return sInvokeMethod.invoke(obj, objArr);
    }

    public static <T> T invokeObject(Class<?> cls, Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        Method method = getMethod(cls, str, clsArr);
        if (method == null) {
            return null;
        }
        return invokeInternal(method, obj, objArr);
    }

    private static <T> T newInstanceInternal(Object obj, Object... objArr) {
        if (sNewInstanceMethod == null) {
            sNewInstanceMethod = Constructor.class.getMethod("newInstance", new Class[]{Object[].class});
        }
        return sNewInstanceMethod.invoke(obj, new Object[]{objArr});
    }

    private static void setAccessibleInternal(Object obj, boolean z) {
        if (sSetAccessibleMethod == null) {
            sSetAccessibleMethod = AccessibleObject.class.getMethod("setAccessible", new Class[]{Boolean.TYPE});
        }
        sSetAccessibleMethod.invoke(obj, new Object[]{Boolean.valueOf(z)});
    }

    public static void setFieldValue(Class<?> cls, Object obj, String str, Object obj2) {
        Field field = getField(cls, str);
        if (field != null) {
            setInternal(field, obj, obj2);
        }
    }

    private static void setInternal(Object obj, Object obj2, Object obj3) {
        if (sSetMethod == null) {
            sSetMethod = Field.class.getMethod("set", new Class[]{Object.class, Object.class});
        }
        sSetMethod.invoke(obj, new Object[]{obj2, obj3});
    }

    public static Class<?> strTypeToClass(String str) {
        try {
            return strTypeToClassThrows(str);
        } catch (ClassNotFoundException unused) {
            return null;
        }
    }

    private static Class<?> strTypeToClassThrows(String str) {
        if (PRIMITIVE_TYPE.containsKey(str)) {
            return PRIMITIVE_TYPE.get(str);
        }
        if (!str.contains(".")) {
            str = "java.lang." + str;
        }
        return Class.forName(str);
    }

    public static Class<?>[] strTypesToClass(String[] strArr) {
        if (strArr == null) {
            return null;
        }
        Class<?>[] clsArr = new Class[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            clsArr[i] = strTypeToClassThrows(strArr[i]);
        }
        return clsArr;
    }
}
