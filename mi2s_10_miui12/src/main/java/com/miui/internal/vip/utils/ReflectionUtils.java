package com.miui.internal.vip.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {
    private static final Map<Class<?>, Class<?>[]> sCompatibleMap = new HashMap();
    private static final Map<Class<?>, Class<?>[]> sPrimToBoxMap = new HashMap();

    static {
        sPrimToBoxMap.put(Integer.TYPE, new Class[]{Integer.class});
        sPrimToBoxMap.put(Short.TYPE, new Class[]{Short.class});
        sPrimToBoxMap.put(Long.TYPE, new Class[]{Integer.TYPE, Integer.class, Long.class});
        sPrimToBoxMap.put(Float.TYPE, new Class[]{Float.class});
        sPrimToBoxMap.put(Double.TYPE, new Class[]{Float.TYPE, Float.class, Double.class});
        sCompatibleMap.put(Long.class, new Class[]{Integer.TYPE, Integer.class, Long.class});
        sCompatibleMap.put(Double.class, new Class[]{Float.TYPE, Float.class, Double.class});
    }

    private ReflectionUtils() {
    }

    public static <T> T createInstance(Class<T> cls) {
        try {
            return cls.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            Utils.logW("createInstance for %s, %s", cls, e);
            return null;
        }
    }

    public static <T> T getField(Object obj, Class cls, String fieldName) {
        try {
            return cls.getField(fieldName).get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T invokeMethod(Object obj, Class cls, String funcName, Class[] argsType, Object... args) {
        try {
            Method method = cls.getMethod(funcName, argsType);
            if (method != null) {
                return method.invoke(obj, args);
            }
            return null;
        } catch (InvocationTargetException e) {
            Utils.logW("ReflectionUtils.invokeMethod failed, funcName = %s, args = %s, %s", funcName, Arrays.toString(args), e.getCause());
            return null;
        } catch (Exception e2) {
            Utils.logW("ReflectionUtils.invokeMethod failed, funcName = %s, args = %s, %s", funcName, Arrays.toString(args), e2);
            return null;
        }
    }

    public static boolean isInnerClass(Class<?> clazz) {
        return clazz.getDeclaringClass() != null && !Modifier.isStatic(clazz.getModifiers());
    }

    public static <T> T[] createArray(Class cls, int length) {
        return (Object[]) Array.newInstance(getWrapperClass(cls), length);
    }

    public static Object[] toObjectArray(Object array) {
        if (array == null) {
            return null;
        }
        Class ct = array.getClass().getComponentType();
        if (ct.isPrimitive()) {
            if (ct == Integer.TYPE) {
                return toObjectArray((int[]) array);
            }
            if (ct == Long.TYPE) {
                return toObjectArray((long[]) array);
            }
            if (ct == Float.TYPE) {
                return toObjectArray((float[]) array);
            }
            if (ct == Double.TYPE) {
                return toObjectArray((double[]) array);
            }
            if (ct == Short.TYPE) {
                return toObjectArray((short[]) array);
            }
            if (ct == Byte.TYPE) {
                return toObjectArray((byte[]) array);
            }
            if (ct == Character.TYPE) {
                return toObjectArray((char[]) array);
            }
        }
        return (Object[]) array;
    }

    public static Object convertArray(Object[] array, Class clz) {
        if (array == null) {
            return null;
        }
        if (clz.isPrimitive()) {
            if (clz == Integer.TYPE) {
                return toIntArray(array);
            }
            if (clz == Long.TYPE) {
                return toLongArray(array);
            }
            if (clz == Float.TYPE) {
                return toFloatArray(array);
            }
            if (clz == Double.TYPE) {
                return toDoubleArray(array);
            }
            if (clz == Short.TYPE) {
                return toShorteArray(array);
            }
            if (clz == Byte.TYPE) {
                return toByteArray(array);
            }
            if (clz == Character.TYPE) {
                return toCharArray(array);
            }
        }
        return array;
    }

    private static Object[] toObjectArray(int[] array) {
        Object[] objArray = createArray(Integer.class, array.length);
        for (int i = 0; i < array.length; i++) {
            objArray[i] = Integer.valueOf(array[i]);
        }
        return objArray;
    }

    private static Object[] toObjectArray(long[] array) {
        Object[] objArray = createArray(Long.class, array.length);
        for (int i = 0; i < array.length; i++) {
            objArray[i] = Long.valueOf(array[i]);
        }
        return objArray;
    }

    private static Object[] toObjectArray(float[] array) {
        Object[] objArray = createArray(Float.class, array.length);
        for (int i = 0; i < array.length; i++) {
            objArray[i] = Float.valueOf(array[i]);
        }
        return objArray;
    }

    private static Object[] toObjectArray(double[] array) {
        Object[] objArray = createArray(Double.class, array.length);
        for (int i = 0; i < array.length; i++) {
            objArray[i] = Double.valueOf(array[i]);
        }
        return objArray;
    }

    private static Object[] toObjectArray(short[] array) {
        Object[] objArray = createArray(Short.class, array.length);
        for (int i = 0; i < array.length; i++) {
            objArray[i] = Short.valueOf(array[i]);
        }
        return objArray;
    }

    private static Object[] toObjectArray(byte[] array) {
        Object[] objArray = createArray(Byte.class, array.length);
        for (int i = 0; i < array.length; i++) {
            objArray[i] = Byte.valueOf(array[i]);
        }
        return objArray;
    }

    private static Object[] toObjectArray(char[] array) {
        Object[] objArray = createArray(Character.class, array.length);
        for (int i = 0; i < array.length; i++) {
            objArray[i] = Character.valueOf(array[i]);
        }
        return objArray;
    }

    private static int[] toIntArray(Object[] array) {
        int[] ret = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].intValue();
        }
        return ret;
    }

    private static long[] toLongArray(Object[] array) {
        long[] ret = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].longValue();
        }
        return ret;
    }

    private static float[] toFloatArray(Object[] array) {
        float[] ret = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].floatValue();
        }
        return ret;
    }

    private static short[] toShorteArray(Object[] array) {
        short[] ret = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].shortValue();
        }
        return ret;
    }

    private static double[] toDoubleArray(Object[] array) {
        double[] ret = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].doubleValue();
        }
        return ret;
    }

    private static byte[] toByteArray(Object[] array) {
        byte[] ret = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].byteValue();
        }
        return ret;
    }

    private static char[] toCharArray(Object[] array) {
        char[] ret = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].charValue();
        }
        return ret;
    }

    private static Class getUnboxedClass(Class clz) {
        if (clz == Integer.class) {
            return Integer.TYPE;
        }
        if (clz == Long.class) {
            return Long.TYPE;
        }
        if (clz == Float.class) {
            return Float.TYPE;
        }
        if (clz == Double.class) {
            return Double.TYPE;
        }
        if (clz == Short.class) {
            return Short.TYPE;
        }
        if (clz == Byte.class) {
            return Byte.TYPE;
        }
        if (clz == Character.class) {
            return Character.TYPE;
        }
        return clz;
    }

    private static Class getWrapperClass(Class clz) {
        if (clz.isPrimitive()) {
            if (clz == Integer.TYPE) {
                return Integer.class;
            }
            if (clz == Long.TYPE) {
                return Long.class;
            }
            if (clz == Float.TYPE) {
                return Float.class;
            }
            if (clz == Double.TYPE) {
                return Double.class;
            }
            if (clz == Short.TYPE) {
                return Short.class;
            }
            if (clz == Byte.TYPE) {
                return Byte.class;
            }
            if (clz == Character.TYPE) {
                return Character.class;
            }
        }
        return clz;
    }

    public static boolean isTypeMatched(Class<?> paramCls, Class<?> reqCls) {
        Class<?> value;
        Class<?> key;
        if (paramCls == reqCls || paramCls.isAssignableFrom(reqCls) || reqCls.isAssignableFrom(paramCls) || isCompatibleClass(paramCls, reqCls)) {
            return true;
        }
        if (!paramCls.isPrimitive() && !reqCls.isPrimitive()) {
            return false;
        }
        if (paramCls.isPrimitive()) {
            key = paramCls;
            value = reqCls;
        } else {
            key = reqCls;
            value = paramCls;
        }
        return Utils.contains((Class[]) sPrimToBoxMap.get(key), value);
    }

    public static boolean isCompatibleClass(Class paramCls, Class reqCls) {
        return Utils.contains((Class[]) sCompatibleMap.get(paramCls), reqCls);
    }

    public static <T> T convert(Object value, Class cls) {
        if (cls == value.getClass() || !isBuiltIn(cls)) {
            return cls.cast(value);
        }
        return invokeMethod((Object) null, cls, "valueOf", new Class[]{String.class}, String.valueOf(value));
    }

    public static boolean arrayEquals(Object[] array1, Object[] array2) {
        if (array1 == array2) {
            return true;
        }
        if (array1 == null || array2 == null || array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            Object a1 = array1[i];
            Object a2 = array2[i];
            Class c1 = a1.getClass();
            Class c2 = a2.getClass();
            boolean equal = false;
            if (c1.isArray() && c2.isArray()) {
                equal = arrayEquals(toObjectArray(a1), toObjectArray(a2));
            } else if (c1 == c2 || isTypeMatched(c1, c2)) {
                try {
                    equal = a1.equals(convert(a2, c1));
                } catch (Exception e) {
                    Utils.log("arrayEquals, convert failed, a2 = %s, c1 = %s, c2 = %s, %s", a2, c1, c2, e);
                    equal = false;
                }
            } else {
                Utils.log("arrayEquals, class not matched, c1 = %s, c2 = %s", c1, c2);
            }
            if (!equal) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean isBuiltIn(Class<T> cls) {
        return cls.equals(String.class) || cls.equals(Integer.class) || cls.equals(Integer.TYPE) || cls.equals(Long.class) || cls.equals(Long.TYPE) || cls.equals(Boolean.class) || cls.equals(Boolean.TYPE) || cls.equals(Short.class) || cls.equals(Short.TYPE) || cls.equals(Float.class) || cls.equals(Float.TYPE) || cls.equals(Double.class) || cls.equals(Double.TYPE);
    }
}
