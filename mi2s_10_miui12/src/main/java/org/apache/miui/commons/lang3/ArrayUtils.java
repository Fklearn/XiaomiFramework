package org.apache.miui.commons.lang3;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.miui.commons.lang3.builder.EqualsBuilder;
import org.apache.miui.commons.lang3.builder.HashCodeBuilder;
import org.apache.miui.commons.lang3.builder.ToStringBuilder;
import org.apache.miui.commons.lang3.builder.ToStringStyle;
import org.apache.miui.commons.lang3.mutable.MutableInt;

public class ArrayUtils
{
  public static final boolean[] EMPTY_BOOLEAN_ARRAY;
  public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY;
  public static final byte[] EMPTY_BYTE_ARRAY;
  public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY;
  public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
  public static final char[] EMPTY_CHAR_ARRAY;
  public static final Class<?>[] EMPTY_CLASS_ARRAY;
  public static final double[] EMPTY_DOUBLE_ARRAY;
  public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY;
  public static final float[] EMPTY_FLOAT_ARRAY;
  public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY;
  public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY;
  public static final int[] EMPTY_INT_ARRAY;
  public static final long[] EMPTY_LONG_ARRAY;
  public static final Long[] EMPTY_LONG_OBJECT_ARRAY;
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  public static final short[] EMPTY_SHORT_ARRAY;
  public static final Short[] EMPTY_SHORT_OBJECT_ARRAY;
  public static final String[] EMPTY_STRING_ARRAY;
  public static final int INDEX_NOT_FOUND = -1;
  
  static
  {
    EMPTY_CLASS_ARRAY = new Class[0];
    EMPTY_STRING_ARRAY = new String[0];
    EMPTY_LONG_ARRAY = new long[0];
    EMPTY_LONG_OBJECT_ARRAY = new Long[0];
    EMPTY_INT_ARRAY = new int[0];
    EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
    EMPTY_SHORT_ARRAY = new short[0];
    EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
    EMPTY_BYTE_ARRAY = new byte[0];
    EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
    EMPTY_DOUBLE_ARRAY = new double[0];
    EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
    EMPTY_FLOAT_ARRAY = new float[0];
    EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
    EMPTY_BOOLEAN_ARRAY = new boolean[0];
    EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
    EMPTY_CHAR_ARRAY = new char[0];
  }
  
  private static Object add(Object paramObject1, int paramInt, Object paramObject2, Class<?> paramClass)
  {
    if (paramObject1 == null)
    {
      if (paramInt == 0)
      {
        paramObject1 = Array.newInstance(paramClass, 1);
        Array.set(paramObject1, 0, paramObject2);
        return paramObject1;
      }
      paramObject1 = new StringBuilder();
      ((StringBuilder)paramObject1).append("Index: ");
      ((StringBuilder)paramObject1).append(paramInt);
      ((StringBuilder)paramObject1).append(", Length: 0");
      throw new IndexOutOfBoundsException(((StringBuilder)paramObject1).toString());
    }
    int i = Array.getLength(paramObject1);
    if ((paramInt <= i) && (paramInt >= 0))
    {
      paramClass = Array.newInstance(paramClass, i + 1);
      System.arraycopy(paramObject1, 0, paramClass, 0, paramInt);
      Array.set(paramClass, paramInt, paramObject2);
      if (paramInt < i) {
        System.arraycopy(paramObject1, paramInt, paramClass, paramInt + 1, i - paramInt);
      }
      return paramClass;
    }
    paramObject1 = new StringBuilder();
    ((StringBuilder)paramObject1).append("Index: ");
    ((StringBuilder)paramObject1).append(paramInt);
    ((StringBuilder)paramObject1).append(", Length: ");
    ((StringBuilder)paramObject1).append(i);
    throw new IndexOutOfBoundsException(((StringBuilder)paramObject1).toString());
  }
  
  public static byte[] add(byte[] paramArrayOfByte, byte paramByte)
  {
    paramArrayOfByte = (byte[])copyArrayGrow1(paramArrayOfByte, Byte.TYPE);
    paramArrayOfByte[(paramArrayOfByte.length - 1)] = ((byte)paramByte);
    return paramArrayOfByte;
  }
  
  public static byte[] add(byte[] paramArrayOfByte, int paramInt, byte paramByte)
  {
    return (byte[])add(paramArrayOfByte, paramInt, Byte.valueOf(paramByte), Byte.TYPE);
  }
  
  public static char[] add(char[] paramArrayOfChar, char paramChar)
  {
    paramArrayOfChar = (char[])copyArrayGrow1(paramArrayOfChar, Character.TYPE);
    paramArrayOfChar[(paramArrayOfChar.length - 1)] = ((char)paramChar);
    return paramArrayOfChar;
  }
  
  public static char[] add(char[] paramArrayOfChar, int paramInt, char paramChar)
  {
    return (char[])add(paramArrayOfChar, paramInt, Character.valueOf(paramChar), Character.TYPE);
  }
  
  public static double[] add(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble = (double[])copyArrayGrow1(paramArrayOfDouble, Double.TYPE);
    paramArrayOfDouble[(paramArrayOfDouble.length - 1)] = paramDouble;
    return paramArrayOfDouble;
  }
  
  public static double[] add(double[] paramArrayOfDouble, int paramInt, double paramDouble)
  {
    return (double[])add(paramArrayOfDouble, paramInt, Double.valueOf(paramDouble), Double.TYPE);
  }
  
  public static float[] add(float[] paramArrayOfFloat, float paramFloat)
  {
    paramArrayOfFloat = (float[])copyArrayGrow1(paramArrayOfFloat, Float.TYPE);
    paramArrayOfFloat[(paramArrayOfFloat.length - 1)] = paramFloat;
    return paramArrayOfFloat;
  }
  
  public static float[] add(float[] paramArrayOfFloat, int paramInt, float paramFloat)
  {
    return (float[])add(paramArrayOfFloat, paramInt, Float.valueOf(paramFloat), Float.TYPE);
  }
  
  public static int[] add(int[] paramArrayOfInt, int paramInt)
  {
    paramArrayOfInt = (int[])copyArrayGrow1(paramArrayOfInt, Integer.TYPE);
    paramArrayOfInt[(paramArrayOfInt.length - 1)] = paramInt;
    return paramArrayOfInt;
  }
  
  public static int[] add(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    return (int[])add(paramArrayOfInt, paramInt1, Integer.valueOf(paramInt2), Integer.TYPE);
  }
  
  public static long[] add(long[] paramArrayOfLong, int paramInt, long paramLong)
  {
    return (long[])add(paramArrayOfLong, paramInt, Long.valueOf(paramLong), Long.TYPE);
  }
  
  public static long[] add(long[] paramArrayOfLong, long paramLong)
  {
    paramArrayOfLong = (long[])copyArrayGrow1(paramArrayOfLong, Long.TYPE);
    paramArrayOfLong[(paramArrayOfLong.length - 1)] = paramLong;
    return paramArrayOfLong;
  }
  
  public static <T> T[] add(T[] paramArrayOfT, int paramInt, T paramT)
  {
    Class localClass;
    if (paramArrayOfT != null)
    {
      localClass = paramArrayOfT.getClass().getComponentType();
    }
    else
    {
      if (paramT == null) {
        break label35;
      }
      localClass = paramT.getClass();
    }
    return (Object[])add(paramArrayOfT, paramInt, paramT, localClass);
    label35:
    throw new IllegalArgumentException("Array and element cannot both be null");
  }
  
  public static <T> T[] add(T[] paramArrayOfT, T paramT)
  {
    Class localClass;
    if (paramArrayOfT != null)
    {
      localClass = paramArrayOfT.getClass();
    }
    else
    {
      if (paramT == null) {
        break label39;
      }
      localClass = paramT.getClass();
    }
    paramArrayOfT = (Object[])copyArrayGrow1(paramArrayOfT, localClass);
    paramArrayOfT[(paramArrayOfT.length - 1)] = paramT;
    return paramArrayOfT;
    label39:
    throw new IllegalArgumentException("Arguments cannot both be null");
  }
  
  public static short[] add(short[] paramArrayOfShort, int paramInt, short paramShort)
  {
    return (short[])add(paramArrayOfShort, paramInt, Short.valueOf(paramShort), Short.TYPE);
  }
  
  public static short[] add(short[] paramArrayOfShort, short paramShort)
  {
    paramArrayOfShort = (short[])copyArrayGrow1(paramArrayOfShort, Short.TYPE);
    paramArrayOfShort[(paramArrayOfShort.length - 1)] = ((short)paramShort);
    return paramArrayOfShort;
  }
  
  public static boolean[] add(boolean[] paramArrayOfBoolean, int paramInt, boolean paramBoolean)
  {
    return (boolean[])add(paramArrayOfBoolean, paramInt, Boolean.valueOf(paramBoolean), Boolean.TYPE);
  }
  
  public static boolean[] add(boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    paramArrayOfBoolean = (boolean[])copyArrayGrow1(paramArrayOfBoolean, Boolean.TYPE);
    paramArrayOfBoolean[(paramArrayOfBoolean.length - 1)] = paramBoolean;
    return paramArrayOfBoolean;
  }
  
  public static byte[] addAll(byte[] paramArrayOfByte1, byte... paramVarArgs)
  {
    if (paramArrayOfByte1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfByte1);
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramVarArgs.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
    System.arraycopy(paramVarArgs, 0, arrayOfByte, paramArrayOfByte1.length, paramVarArgs.length);
    return arrayOfByte;
  }
  
  public static char[] addAll(char[] paramArrayOfChar1, char... paramVarArgs)
  {
    if (paramArrayOfChar1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfChar1);
    }
    char[] arrayOfChar = new char[paramArrayOfChar1.length + paramVarArgs.length];
    System.arraycopy(paramArrayOfChar1, 0, arrayOfChar, 0, paramArrayOfChar1.length);
    System.arraycopy(paramVarArgs, 0, arrayOfChar, paramArrayOfChar1.length, paramVarArgs.length);
    return arrayOfChar;
  }
  
  public static double[] addAll(double[] paramArrayOfDouble1, double... paramVarArgs)
  {
    if (paramArrayOfDouble1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfDouble1);
    }
    double[] arrayOfDouble = new double[paramArrayOfDouble1.length + paramVarArgs.length];
    System.arraycopy(paramArrayOfDouble1, 0, arrayOfDouble, 0, paramArrayOfDouble1.length);
    System.arraycopy(paramVarArgs, 0, arrayOfDouble, paramArrayOfDouble1.length, paramVarArgs.length);
    return arrayOfDouble;
  }
  
  public static float[] addAll(float[] paramArrayOfFloat1, float... paramVarArgs)
  {
    if (paramArrayOfFloat1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfFloat1);
    }
    float[] arrayOfFloat = new float[paramArrayOfFloat1.length + paramVarArgs.length];
    System.arraycopy(paramArrayOfFloat1, 0, arrayOfFloat, 0, paramArrayOfFloat1.length);
    System.arraycopy(paramVarArgs, 0, arrayOfFloat, paramArrayOfFloat1.length, paramVarArgs.length);
    return arrayOfFloat;
  }
  
  public static int[] addAll(int[] paramArrayOfInt1, int... paramVarArgs)
  {
    if (paramArrayOfInt1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfInt1);
    }
    int[] arrayOfInt = new int[paramArrayOfInt1.length + paramVarArgs.length];
    System.arraycopy(paramArrayOfInt1, 0, arrayOfInt, 0, paramArrayOfInt1.length);
    System.arraycopy(paramVarArgs, 0, arrayOfInt, paramArrayOfInt1.length, paramVarArgs.length);
    return arrayOfInt;
  }
  
  public static long[] addAll(long[] paramArrayOfLong1, long... paramVarArgs)
  {
    if (paramArrayOfLong1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfLong1);
    }
    long[] arrayOfLong = new long[paramArrayOfLong1.length + paramVarArgs.length];
    System.arraycopy(paramArrayOfLong1, 0, arrayOfLong, 0, paramArrayOfLong1.length);
    System.arraycopy(paramVarArgs, 0, arrayOfLong, paramArrayOfLong1.length, paramVarArgs.length);
    return arrayOfLong;
  }
  
  public static <T> T[] addAll(T[] paramArrayOfT1, T... paramVarArgs)
  {
    if (paramArrayOfT1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfT1);
    }
    Class localClass = paramArrayOfT1.getClass().getComponentType();
    Object localObject = (Object[])Array.newInstance(localClass, paramArrayOfT1.length + paramVarArgs.length);
    System.arraycopy(paramArrayOfT1, 0, localObject, 0, paramArrayOfT1.length);
    try
    {
      System.arraycopy(paramVarArgs, 0, localObject, paramArrayOfT1.length, paramVarArgs.length);
      return (T[])localObject;
    }
    catch (ArrayStoreException paramArrayOfT1)
    {
      localObject = paramVarArgs.getClass().getComponentType();
      if (!localClass.isAssignableFrom((Class)localObject))
      {
        paramVarArgs = new StringBuilder();
        paramVarArgs.append("Cannot store ");
        paramVarArgs.append(((Class)localObject).getName());
        paramVarArgs.append(" in an array of ");
        paramVarArgs.append(localClass.getName());
        throw new IllegalArgumentException(paramVarArgs.toString(), paramArrayOfT1);
      }
      throw paramArrayOfT1;
    }
  }
  
  public static short[] addAll(short[] paramArrayOfShort1, short... paramVarArgs)
  {
    if (paramArrayOfShort1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfShort1);
    }
    short[] arrayOfShort = new short[paramArrayOfShort1.length + paramVarArgs.length];
    System.arraycopy(paramArrayOfShort1, 0, arrayOfShort, 0, paramArrayOfShort1.length);
    System.arraycopy(paramVarArgs, 0, arrayOfShort, paramArrayOfShort1.length, paramVarArgs.length);
    return arrayOfShort;
  }
  
  public static boolean[] addAll(boolean[] paramArrayOfBoolean1, boolean... paramVarArgs)
  {
    if (paramArrayOfBoolean1 == null) {
      return clone(paramVarArgs);
    }
    if (paramVarArgs == null) {
      return clone(paramArrayOfBoolean1);
    }
    boolean[] arrayOfBoolean = new boolean[paramArrayOfBoolean1.length + paramVarArgs.length];
    System.arraycopy(paramArrayOfBoolean1, 0, arrayOfBoolean, 0, paramArrayOfBoolean1.length);
    System.arraycopy(paramVarArgs, 0, arrayOfBoolean, paramArrayOfBoolean1.length, paramVarArgs.length);
    return arrayOfBoolean;
  }
  
  public static byte[] clone(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    return (byte[])paramArrayOfByte.clone();
  }
  
  public static char[] clone(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar == null) {
      return null;
    }
    return (char[])paramArrayOfChar.clone();
  }
  
  public static double[] clone(double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble == null) {
      return null;
    }
    return (double[])paramArrayOfDouble.clone();
  }
  
  public static float[] clone(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      return null;
    }
    return (float[])paramArrayOfFloat.clone();
  }
  
  public static int[] clone(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    return (int[])paramArrayOfInt.clone();
  }
  
  public static long[] clone(long[] paramArrayOfLong)
  {
    if (paramArrayOfLong == null) {
      return null;
    }
    return (long[])paramArrayOfLong.clone();
  }
  
  public static <T> T[] clone(T[] paramArrayOfT)
  {
    if (paramArrayOfT == null) {
      return null;
    }
    return (Object[])paramArrayOfT.clone();
  }
  
  public static short[] clone(short[] paramArrayOfShort)
  {
    if (paramArrayOfShort == null) {
      return null;
    }
    return (short[])paramArrayOfShort.clone();
  }
  
  public static boolean[] clone(boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfBoolean == null) {
      return null;
    }
    return (boolean[])paramArrayOfBoolean.clone();
  }
  
  public static boolean contains(byte[] paramArrayOfByte, byte paramByte)
  {
    boolean bool;
    if (indexOf(paramArrayOfByte, paramByte) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(char[] paramArrayOfChar, char paramChar)
  {
    boolean bool;
    if (indexOf(paramArrayOfChar, paramChar) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(double[] paramArrayOfDouble, double paramDouble)
  {
    boolean bool;
    if (indexOf(paramArrayOfDouble, paramDouble) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2)
  {
    boolean bool;
    if (indexOf(paramArrayOfDouble, paramDouble1, 0, paramDouble2) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(float[] paramArrayOfFloat, float paramFloat)
  {
    boolean bool;
    if (indexOf(paramArrayOfFloat, paramFloat) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(int[] paramArrayOfInt, int paramInt)
  {
    boolean bool;
    if (indexOf(paramArrayOfInt, paramInt) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(long[] paramArrayOfLong, long paramLong)
  {
    boolean bool;
    if (indexOf(paramArrayOfLong, paramLong) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(Object[] paramArrayOfObject, Object paramObject)
  {
    boolean bool;
    if (indexOf(paramArrayOfObject, paramObject) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(short[] paramArrayOfShort, short paramShort)
  {
    boolean bool;
    if (indexOf(paramArrayOfShort, paramShort) != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean contains(boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    if (indexOf(paramArrayOfBoolean, paramBoolean) != -1) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    }
    return paramBoolean;
  }
  
  private static Object copyArrayGrow1(Object paramObject, Class<?> paramClass)
  {
    if (paramObject != null)
    {
      int i = Array.getLength(paramObject);
      paramClass = Array.newInstance(paramObject.getClass().getComponentType(), i + 1);
      System.arraycopy(paramObject, 0, paramClass, 0, i);
      return paramClass;
    }
    return Array.newInstance(paramClass, 1);
  }
  
  private static int[] extractIndices(HashSet<Integer> paramHashSet)
  {
    int[] arrayOfInt = new int[paramHashSet.size()];
    int i = 0;
    paramHashSet = paramHashSet.iterator();
    while (paramHashSet.hasNext())
    {
      arrayOfInt[i] = ((Integer)paramHashSet.next()).intValue();
      i++;
    }
    return arrayOfInt;
  }
  
  public static int getLength(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    return Array.getLength(paramObject);
  }
  
  public static int hashCode(Object paramObject)
  {
    return new HashCodeBuilder().append(paramObject).toHashCode();
  }
  
  public static int indexOf(byte[] paramArrayOfByte, byte paramByte)
  {
    return indexOf(paramArrayOfByte, paramByte, 0);
  }
  
  public static int indexOf(byte[] paramArrayOfByte, byte paramByte, int paramInt)
  {
    if (paramArrayOfByte == null) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    for (paramInt = i; paramInt < paramArrayOfByte.length; paramInt++) {
      if (paramByte == paramArrayOfByte[paramInt]) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static int indexOf(char[] paramArrayOfChar, char paramChar)
  {
    return indexOf(paramArrayOfChar, paramChar, 0);
  }
  
  public static int indexOf(char[] paramArrayOfChar, char paramChar, int paramInt)
  {
    if (paramArrayOfChar == null) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {}
    for (i = 0; i < paramArrayOfChar.length; i++) {
      if (paramChar == paramArrayOfChar[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(double[] paramArrayOfDouble, double paramDouble)
  {
    return indexOf(paramArrayOfDouble, paramDouble, 0);
  }
  
  public static int indexOf(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2)
  {
    return indexOf(paramArrayOfDouble, paramDouble1, 0, paramDouble2);
  }
  
  public static int indexOf(double[] paramArrayOfDouble, double paramDouble, int paramInt)
  {
    if (isEmpty(paramArrayOfDouble)) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    for (paramInt = i; paramInt < paramArrayOfDouble.length; paramInt++) {
      if (paramDouble == paramArrayOfDouble[paramInt]) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static int indexOf(double[] paramArrayOfDouble, double paramDouble1, int paramInt, double paramDouble2)
  {
    if (isEmpty(paramArrayOfDouble)) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    for (paramInt = i; paramInt < paramArrayOfDouble.length; paramInt++) {
      if ((paramArrayOfDouble[paramInt] >= paramDouble1 - paramDouble2) && (paramArrayOfDouble[paramInt] <= paramDouble1 + paramDouble2)) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static int indexOf(float[] paramArrayOfFloat, float paramFloat)
  {
    return indexOf(paramArrayOfFloat, paramFloat, 0);
  }
  
  public static int indexOf(float[] paramArrayOfFloat, float paramFloat, int paramInt)
  {
    if (isEmpty(paramArrayOfFloat)) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    for (paramInt = i; paramInt < paramArrayOfFloat.length; paramInt++) {
      if (paramFloat == paramArrayOfFloat[paramInt]) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static int indexOf(int[] paramArrayOfInt, int paramInt)
  {
    return indexOf(paramArrayOfInt, paramInt, 0);
  }
  
  public static int indexOf(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramArrayOfInt == null) {
      return -1;
    }
    int i = paramInt2;
    if (paramInt2 < 0) {}
    for (i = 0; i < paramArrayOfInt.length; i++) {
      if (paramInt1 == paramArrayOfInt[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(long[] paramArrayOfLong, long paramLong)
  {
    return indexOf(paramArrayOfLong, paramLong, 0);
  }
  
  public static int indexOf(long[] paramArrayOfLong, long paramLong, int paramInt)
  {
    if (paramArrayOfLong == null) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {}
    for (i = 0; i < paramArrayOfLong.length; i++) {
      if (paramLong == paramArrayOfLong[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(Object[] paramArrayOfObject, Object paramObject)
  {
    return indexOf(paramArrayOfObject, paramObject, 0);
  }
  
  public static int indexOf(Object[] paramArrayOfObject, Object paramObject, int paramInt)
  {
    if (paramArrayOfObject == null) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    if (paramObject == null) {
      for (paramInt = i; paramInt < paramArrayOfObject.length; paramInt++) {
        if (paramArrayOfObject[paramInt] == null) {
          return paramInt;
        }
      }
    } else if (paramArrayOfObject.getClass().getComponentType().isInstance(paramObject)) {
      for (paramInt = i; paramInt < paramArrayOfObject.length; paramInt++) {
        if (paramObject.equals(paramArrayOfObject[paramInt])) {
          return paramInt;
        }
      }
    }
    return -1;
  }
  
  public static int indexOf(short[] paramArrayOfShort, short paramShort)
  {
    return indexOf(paramArrayOfShort, paramShort, 0);
  }
  
  public static int indexOf(short[] paramArrayOfShort, short paramShort, int paramInt)
  {
    if (paramArrayOfShort == null) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {}
    for (i = 0; i < paramArrayOfShort.length; i++) {
      if (paramShort == paramArrayOfShort[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    return indexOf(paramArrayOfBoolean, paramBoolean, 0);
  }
  
  public static int indexOf(boolean[] paramArrayOfBoolean, boolean paramBoolean, int paramInt)
  {
    if (isEmpty(paramArrayOfBoolean)) {
      return -1;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    for (paramInt = i; paramInt < paramArrayOfBoolean.length; paramInt++) {
      if (paramBoolean == paramArrayOfBoolean[paramInt]) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static boolean isEmpty(byte[] paramArrayOfByte)
  {
    boolean bool;
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmpty(char[] paramArrayOfChar)
  {
    boolean bool;
    if ((paramArrayOfChar != null) && (paramArrayOfChar.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmpty(double[] paramArrayOfDouble)
  {
    boolean bool;
    if ((paramArrayOfDouble != null) && (paramArrayOfDouble.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmpty(float[] paramArrayOfFloat)
  {
    boolean bool;
    if ((paramArrayOfFloat != null) && (paramArrayOfFloat.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmpty(int[] paramArrayOfInt)
  {
    boolean bool;
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmpty(long[] paramArrayOfLong)
  {
    boolean bool;
    if ((paramArrayOfLong != null) && (paramArrayOfLong.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmpty(Object[] paramArrayOfObject)
  {
    boolean bool;
    if ((paramArrayOfObject != null) && (paramArrayOfObject.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmpty(short[] paramArrayOfShort)
  {
    boolean bool;
    if ((paramArrayOfShort != null) && (paramArrayOfShort.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEmpty(boolean[] paramArrayOfBoolean)
  {
    boolean bool;
    if ((paramArrayOfBoolean != null) && (paramArrayOfBoolean.length != 0)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isEquals(Object paramObject1, Object paramObject2)
  {
    return new EqualsBuilder().append(paramObject1, paramObject2).isEquals();
  }
  
  public static boolean isNotEmpty(byte[] paramArrayOfByte)
  {
    boolean bool;
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNotEmpty(char[] paramArrayOfChar)
  {
    boolean bool;
    if ((paramArrayOfChar != null) && (paramArrayOfChar.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNotEmpty(double[] paramArrayOfDouble)
  {
    boolean bool;
    if ((paramArrayOfDouble != null) && (paramArrayOfDouble.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNotEmpty(float[] paramArrayOfFloat)
  {
    boolean bool;
    if ((paramArrayOfFloat != null) && (paramArrayOfFloat.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNotEmpty(int[] paramArrayOfInt)
  {
    boolean bool;
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNotEmpty(long[] paramArrayOfLong)
  {
    boolean bool;
    if ((paramArrayOfLong != null) && (paramArrayOfLong.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static <T> boolean isNotEmpty(T[] paramArrayOfT)
  {
    boolean bool;
    if ((paramArrayOfT != null) && (paramArrayOfT.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNotEmpty(short[] paramArrayOfShort)
  {
    boolean bool;
    if ((paramArrayOfShort != null) && (paramArrayOfShort.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNotEmpty(boolean[] paramArrayOfBoolean)
  {
    boolean bool;
    if ((paramArrayOfBoolean != null) && (paramArrayOfBoolean.length != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isSameLength(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    return ((paramArrayOfByte1 != null) || (paramArrayOfByte2 == null) || (paramArrayOfByte2.length <= 0)) && ((paramArrayOfByte2 != null) || (paramArrayOfByte1 == null) || (paramArrayOfByte1.length <= 0)) && ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null) || (paramArrayOfByte1.length == paramArrayOfByte2.length));
  }
  
  public static boolean isSameLength(char[] paramArrayOfChar1, char[] paramArrayOfChar2)
  {
    return ((paramArrayOfChar1 != null) || (paramArrayOfChar2 == null) || (paramArrayOfChar2.length <= 0)) && ((paramArrayOfChar2 != null) || (paramArrayOfChar1 == null) || (paramArrayOfChar1.length <= 0)) && ((paramArrayOfChar1 == null) || (paramArrayOfChar2 == null) || (paramArrayOfChar1.length == paramArrayOfChar2.length));
  }
  
  public static boolean isSameLength(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    return ((paramArrayOfDouble1 != null) || (paramArrayOfDouble2 == null) || (paramArrayOfDouble2.length <= 0)) && ((paramArrayOfDouble2 != null) || (paramArrayOfDouble1 == null) || (paramArrayOfDouble1.length <= 0)) && ((paramArrayOfDouble1 == null) || (paramArrayOfDouble2 == null) || (paramArrayOfDouble1.length == paramArrayOfDouble2.length));
  }
  
  public static boolean isSameLength(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    return ((paramArrayOfFloat1 != null) || (paramArrayOfFloat2 == null) || (paramArrayOfFloat2.length <= 0)) && ((paramArrayOfFloat2 != null) || (paramArrayOfFloat1 == null) || (paramArrayOfFloat1.length <= 0)) && ((paramArrayOfFloat1 == null) || (paramArrayOfFloat2 == null) || (paramArrayOfFloat1.length == paramArrayOfFloat2.length));
  }
  
  public static boolean isSameLength(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return ((paramArrayOfInt1 != null) || (paramArrayOfInt2 == null) || (paramArrayOfInt2.length <= 0)) && ((paramArrayOfInt2 != null) || (paramArrayOfInt1 == null) || (paramArrayOfInt1.length <= 0)) && ((paramArrayOfInt1 == null) || (paramArrayOfInt2 == null) || (paramArrayOfInt1.length == paramArrayOfInt2.length));
  }
  
  public static boolean isSameLength(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    return ((paramArrayOfLong1 != null) || (paramArrayOfLong2 == null) || (paramArrayOfLong2.length <= 0)) && ((paramArrayOfLong2 != null) || (paramArrayOfLong1 == null) || (paramArrayOfLong1.length <= 0)) && ((paramArrayOfLong1 == null) || (paramArrayOfLong2 == null) || (paramArrayOfLong1.length == paramArrayOfLong2.length));
  }
  
  public static boolean isSameLength(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
  {
    return ((paramArrayOfObject1 != null) || (paramArrayOfObject2 == null) || (paramArrayOfObject2.length <= 0)) && ((paramArrayOfObject2 != null) || (paramArrayOfObject1 == null) || (paramArrayOfObject1.length <= 0)) && ((paramArrayOfObject1 == null) || (paramArrayOfObject2 == null) || (paramArrayOfObject1.length == paramArrayOfObject2.length));
  }
  
  public static boolean isSameLength(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    return ((paramArrayOfShort1 != null) || (paramArrayOfShort2 == null) || (paramArrayOfShort2.length <= 0)) && ((paramArrayOfShort2 != null) || (paramArrayOfShort1 == null) || (paramArrayOfShort1.length <= 0)) && ((paramArrayOfShort1 == null) || (paramArrayOfShort2 == null) || (paramArrayOfShort1.length == paramArrayOfShort2.length));
  }
  
  public static boolean isSameLength(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    return ((paramArrayOfBoolean1 != null) || (paramArrayOfBoolean2 == null) || (paramArrayOfBoolean2.length <= 0)) && ((paramArrayOfBoolean2 != null) || (paramArrayOfBoolean1 == null) || (paramArrayOfBoolean1.length <= 0)) && ((paramArrayOfBoolean1 == null) || (paramArrayOfBoolean2 == null) || (paramArrayOfBoolean1.length == paramArrayOfBoolean2.length));
  }
  
  public static boolean isSameType(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 != null) && (paramObject2 != null)) {
      return paramObject1.getClass().getName().equals(paramObject2.getClass().getName());
    }
    throw new IllegalArgumentException("The Array must not be null");
  }
  
  public static int lastIndexOf(byte[] paramArrayOfByte, byte paramByte)
  {
    return lastIndexOf(paramArrayOfByte, paramByte, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(byte[] paramArrayOfByte, byte paramByte, int paramInt)
  {
    if (paramArrayOfByte == null) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfByte.length) {
      i = paramArrayOfByte.length - 1;
    }
    for (paramInt = i; paramInt >= 0; paramInt--) {
      if (paramByte == paramArrayOfByte[paramInt]) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(char[] paramArrayOfChar, char paramChar)
  {
    return lastIndexOf(paramArrayOfChar, paramChar, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(char[] paramArrayOfChar, char paramChar, int paramInt)
  {
    if (paramArrayOfChar == null) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfChar.length) {}
    for (i = paramArrayOfChar.length - 1; i >= 0; i--) {
      if (paramChar == paramArrayOfChar[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(double[] paramArrayOfDouble, double paramDouble)
  {
    return lastIndexOf(paramArrayOfDouble, paramDouble, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2)
  {
    return lastIndexOf(paramArrayOfDouble, paramDouble1, Integer.MAX_VALUE, paramDouble2);
  }
  
  public static int lastIndexOf(double[] paramArrayOfDouble, double paramDouble, int paramInt)
  {
    if (isEmpty(paramArrayOfDouble)) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfDouble.length) {
      i = paramArrayOfDouble.length - 1;
    }
    for (paramInt = i; paramInt >= 0; paramInt--) {
      if (paramDouble == paramArrayOfDouble[paramInt]) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(double[] paramArrayOfDouble, double paramDouble1, int paramInt, double paramDouble2)
  {
    if (isEmpty(paramArrayOfDouble)) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfDouble.length) {
      i = paramArrayOfDouble.length - 1;
    }
    for (paramInt = i; paramInt >= 0; paramInt--) {
      if ((paramArrayOfDouble[paramInt] >= paramDouble1 - paramDouble2) && (paramArrayOfDouble[paramInt] <= paramDouble1 + paramDouble2)) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(float[] paramArrayOfFloat, float paramFloat)
  {
    return lastIndexOf(paramArrayOfFloat, paramFloat, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(float[] paramArrayOfFloat, float paramFloat, int paramInt)
  {
    if (isEmpty(paramArrayOfFloat)) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfFloat.length) {}
    for (i = paramArrayOfFloat.length - 1; i >= 0; i--) {
      if (paramFloat == paramArrayOfFloat[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(int[] paramArrayOfInt, int paramInt)
  {
    return lastIndexOf(paramArrayOfInt, paramInt, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramArrayOfInt == null) {
      return -1;
    }
    if (paramInt2 < 0) {
      return -1;
    }
    int i = paramInt2;
    if (paramInt2 >= paramArrayOfInt.length) {}
    for (i = paramArrayOfInt.length - 1; i >= 0; i--) {
      if (paramInt1 == paramArrayOfInt[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(long[] paramArrayOfLong, long paramLong)
  {
    return lastIndexOf(paramArrayOfLong, paramLong, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(long[] paramArrayOfLong, long paramLong, int paramInt)
  {
    if (paramArrayOfLong == null) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfLong.length) {}
    for (i = paramArrayOfLong.length - 1; i >= 0; i--) {
      if (paramLong == paramArrayOfLong[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(Object[] paramArrayOfObject, Object paramObject)
  {
    return lastIndexOf(paramArrayOfObject, paramObject, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(Object[] paramArrayOfObject, Object paramObject, int paramInt)
  {
    if (paramArrayOfObject == null) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfObject.length) {
      i = paramArrayOfObject.length - 1;
    }
    if (paramObject == null) {
      while (i >= 0)
      {
        if (paramArrayOfObject[i] == null) {
          return i;
        }
        i--;
      }
    } else if (paramArrayOfObject.getClass().getComponentType().isInstance(paramObject)) {
      while (i >= 0)
      {
        if (paramObject.equals(paramArrayOfObject[i])) {
          return i;
        }
        i--;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(short[] paramArrayOfShort, short paramShort)
  {
    return lastIndexOf(paramArrayOfShort, paramShort, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(short[] paramArrayOfShort, short paramShort, int paramInt)
  {
    if (paramArrayOfShort == null) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfShort.length) {
      i = paramArrayOfShort.length - 1;
    }
    for (paramInt = i; paramInt >= 0; paramInt--) {
      if (paramShort == paramArrayOfShort[paramInt]) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static int lastIndexOf(boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    return lastIndexOf(paramArrayOfBoolean, paramBoolean, Integer.MAX_VALUE);
  }
  
  public static int lastIndexOf(boolean[] paramArrayOfBoolean, boolean paramBoolean, int paramInt)
  {
    if (isEmpty(paramArrayOfBoolean)) {
      return -1;
    }
    if (paramInt < 0) {
      return -1;
    }
    int i = paramInt;
    if (paramInt >= paramArrayOfBoolean.length) {
      i = paramArrayOfBoolean.length - 1;
    }
    for (paramInt = i; paramInt >= 0; paramInt--) {
      if (paramBoolean == paramArrayOfBoolean[paramInt]) {
        return paramInt;
      }
    }
    return -1;
  }
  
  public static byte[] nullToEmpty(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0)) {
      return paramArrayOfByte;
    }
    return EMPTY_BYTE_ARRAY;
  }
  
  public static char[] nullToEmpty(char[] paramArrayOfChar)
  {
    if ((paramArrayOfChar != null) && (paramArrayOfChar.length != 0)) {
      return paramArrayOfChar;
    }
    return EMPTY_CHAR_ARRAY;
  }
  
  public static double[] nullToEmpty(double[] paramArrayOfDouble)
  {
    if ((paramArrayOfDouble != null) && (paramArrayOfDouble.length != 0)) {
      return paramArrayOfDouble;
    }
    return EMPTY_DOUBLE_ARRAY;
  }
  
  public static float[] nullToEmpty(float[] paramArrayOfFloat)
  {
    if ((paramArrayOfFloat != null) && (paramArrayOfFloat.length != 0)) {
      return paramArrayOfFloat;
    }
    return EMPTY_FLOAT_ARRAY;
  }
  
  public static int[] nullToEmpty(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length != 0)) {
      return paramArrayOfInt;
    }
    return EMPTY_INT_ARRAY;
  }
  
  public static long[] nullToEmpty(long[] paramArrayOfLong)
  {
    if ((paramArrayOfLong != null) && (paramArrayOfLong.length != 0)) {
      return paramArrayOfLong;
    }
    return EMPTY_LONG_ARRAY;
  }
  
  public static Boolean[] nullToEmpty(Boolean[] paramArrayOfBoolean)
  {
    if ((paramArrayOfBoolean != null) && (paramArrayOfBoolean.length != 0)) {
      return paramArrayOfBoolean;
    }
    return EMPTY_BOOLEAN_OBJECT_ARRAY;
  }
  
  public static Byte[] nullToEmpty(Byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0)) {
      return paramArrayOfByte;
    }
    return EMPTY_BYTE_OBJECT_ARRAY;
  }
  
  public static Character[] nullToEmpty(Character[] paramArrayOfCharacter)
  {
    if ((paramArrayOfCharacter != null) && (paramArrayOfCharacter.length != 0)) {
      return paramArrayOfCharacter;
    }
    return EMPTY_CHARACTER_OBJECT_ARRAY;
  }
  
  public static Double[] nullToEmpty(Double[] paramArrayOfDouble)
  {
    if ((paramArrayOfDouble != null) && (paramArrayOfDouble.length != 0)) {
      return paramArrayOfDouble;
    }
    return EMPTY_DOUBLE_OBJECT_ARRAY;
  }
  
  public static Float[] nullToEmpty(Float[] paramArrayOfFloat)
  {
    if ((paramArrayOfFloat != null) && (paramArrayOfFloat.length != 0)) {
      return paramArrayOfFloat;
    }
    return EMPTY_FLOAT_OBJECT_ARRAY;
  }
  
  public static Integer[] nullToEmpty(Integer[] paramArrayOfInteger)
  {
    if ((paramArrayOfInteger != null) && (paramArrayOfInteger.length != 0)) {
      return paramArrayOfInteger;
    }
    return EMPTY_INTEGER_OBJECT_ARRAY;
  }
  
  public static Long[] nullToEmpty(Long[] paramArrayOfLong)
  {
    if ((paramArrayOfLong != null) && (paramArrayOfLong.length != 0)) {
      return paramArrayOfLong;
    }
    return EMPTY_LONG_OBJECT_ARRAY;
  }
  
  public static Object[] nullToEmpty(Object[] paramArrayOfObject)
  {
    if ((paramArrayOfObject != null) && (paramArrayOfObject.length != 0)) {
      return paramArrayOfObject;
    }
    return EMPTY_OBJECT_ARRAY;
  }
  
  public static Short[] nullToEmpty(Short[] paramArrayOfShort)
  {
    if ((paramArrayOfShort != null) && (paramArrayOfShort.length != 0)) {
      return paramArrayOfShort;
    }
    return EMPTY_SHORT_OBJECT_ARRAY;
  }
  
  public static String[] nullToEmpty(String[] paramArrayOfString)
  {
    if ((paramArrayOfString != null) && (paramArrayOfString.length != 0)) {
      return paramArrayOfString;
    }
    return EMPTY_STRING_ARRAY;
  }
  
  public static short[] nullToEmpty(short[] paramArrayOfShort)
  {
    if ((paramArrayOfShort != null) && (paramArrayOfShort.length != 0)) {
      return paramArrayOfShort;
    }
    return EMPTY_SHORT_ARRAY;
  }
  
  public static boolean[] nullToEmpty(boolean[] paramArrayOfBoolean)
  {
    if ((paramArrayOfBoolean != null) && (paramArrayOfBoolean.length != 0)) {
      return paramArrayOfBoolean;
    }
    return EMPTY_BOOLEAN_ARRAY;
  }
  
  private static Object remove(Object paramObject, int paramInt)
  {
    int i = getLength(paramObject);
    if ((paramInt >= 0) && (paramInt < i))
    {
      Object localObject = Array.newInstance(paramObject.getClass().getComponentType(), i - 1);
      System.arraycopy(paramObject, 0, localObject, 0, paramInt);
      if (paramInt < i - 1) {
        System.arraycopy(paramObject, paramInt + 1, localObject, paramInt, i - paramInt - 1);
      }
      return localObject;
    }
    paramObject = new StringBuilder();
    ((StringBuilder)paramObject).append("Index: ");
    ((StringBuilder)paramObject).append(paramInt);
    ((StringBuilder)paramObject).append(", Length: ");
    ((StringBuilder)paramObject).append(i);
    throw new IndexOutOfBoundsException(((StringBuilder)paramObject).toString());
  }
  
  public static byte[] remove(byte[] paramArrayOfByte, int paramInt)
  {
    return (byte[])remove(paramArrayOfByte, paramInt);
  }
  
  public static char[] remove(char[] paramArrayOfChar, int paramInt)
  {
    return (char[])remove(paramArrayOfChar, paramInt);
  }
  
  public static double[] remove(double[] paramArrayOfDouble, int paramInt)
  {
    return (double[])remove(paramArrayOfDouble, paramInt);
  }
  
  public static float[] remove(float[] paramArrayOfFloat, int paramInt)
  {
    return (float[])remove(paramArrayOfFloat, paramInt);
  }
  
  public static int[] remove(int[] paramArrayOfInt, int paramInt)
  {
    return (int[])remove(paramArrayOfInt, paramInt);
  }
  
  public static long[] remove(long[] paramArrayOfLong, int paramInt)
  {
    return (long[])remove(paramArrayOfLong, paramInt);
  }
  
  public static <T> T[] remove(T[] paramArrayOfT, int paramInt)
  {
    return (Object[])remove(paramArrayOfT, paramInt);
  }
  
  public static short[] remove(short[] paramArrayOfShort, int paramInt)
  {
    return (short[])remove(paramArrayOfShort, paramInt);
  }
  
  public static boolean[] remove(boolean[] paramArrayOfBoolean, int paramInt)
  {
    return (boolean[])remove(paramArrayOfBoolean, paramInt);
  }
  
  private static Object removeAll(Object paramObject, int... paramVarArgs)
  {
    int i = getLength(paramObject);
    int j = 0;
    int k = 0;
    int m;
    int n;
    if (isNotEmpty(paramVarArgs))
    {
      Arrays.sort(paramVarArgs);
      j = paramVarArgs.length;
      m = i;
      for (;;)
      {
        n = j - 1;
        j = k;
        if (n < 0) {
          break label124;
        }
        j = paramVarArgs[n];
        if ((j < 0) || (j >= i)) {
          break;
        }
        if (j >= m)
        {
          j = n;
        }
        else
        {
          k++;
          m = j;
          j = n;
        }
      }
      paramObject = new StringBuilder();
      ((StringBuilder)paramObject).append("Index: ");
      ((StringBuilder)paramObject).append(j);
      ((StringBuilder)paramObject).append(", Length: ");
      ((StringBuilder)paramObject).append(i);
      throw new IndexOutOfBoundsException(((StringBuilder)paramObject).toString());
    }
    label124:
    Object localObject = Array.newInstance(paramObject.getClass().getComponentType(), i - j);
    if (j < i)
    {
      m = i;
      i -= j;
      k = paramVarArgs.length - 1;
      j = m;
      while (k >= 0)
      {
        n = paramVarArgs[k];
        m = i;
        if (j - n > 1)
        {
          j = j - n - 1;
          m = i - j;
          System.arraycopy(paramObject, n + 1, localObject, m, j);
        }
        j = n;
        k--;
        i = m;
      }
      if (j > 0) {
        System.arraycopy(paramObject, 0, localObject, 0, j);
      }
    }
    return localObject;
  }
  
  public static byte[] removeAll(byte[] paramArrayOfByte, int... paramVarArgs)
  {
    return (byte[])removeAll(paramArrayOfByte, clone(paramVarArgs));
  }
  
  public static char[] removeAll(char[] paramArrayOfChar, int... paramVarArgs)
  {
    return (char[])removeAll(paramArrayOfChar, clone(paramVarArgs));
  }
  
  public static double[] removeAll(double[] paramArrayOfDouble, int... paramVarArgs)
  {
    return (double[])removeAll(paramArrayOfDouble, clone(paramVarArgs));
  }
  
  public static float[] removeAll(float[] paramArrayOfFloat, int... paramVarArgs)
  {
    return (float[])removeAll(paramArrayOfFloat, clone(paramVarArgs));
  }
  
  public static int[] removeAll(int[] paramArrayOfInt1, int... paramVarArgs)
  {
    return (int[])removeAll(paramArrayOfInt1, clone(paramVarArgs));
  }
  
  public static long[] removeAll(long[] paramArrayOfLong, int... paramVarArgs)
  {
    return (long[])removeAll(paramArrayOfLong, clone(paramVarArgs));
  }
  
  public static <T> T[] removeAll(T[] paramArrayOfT, int... paramVarArgs)
  {
    return (Object[])removeAll(paramArrayOfT, clone(paramVarArgs));
  }
  
  public static short[] removeAll(short[] paramArrayOfShort, int... paramVarArgs)
  {
    return (short[])removeAll(paramArrayOfShort, clone(paramVarArgs));
  }
  
  public static boolean[] removeAll(boolean[] paramArrayOfBoolean, int... paramVarArgs)
  {
    return (boolean[])removeAll(paramArrayOfBoolean, clone(paramVarArgs));
  }
  
  public static byte[] removeElement(byte[] paramArrayOfByte, byte paramByte)
  {
    int i = indexOf(paramArrayOfByte, paramByte);
    if (i == -1) {
      return clone(paramArrayOfByte);
    }
    return remove(paramArrayOfByte, i);
  }
  
  public static char[] removeElement(char[] paramArrayOfChar, char paramChar)
  {
    int i = indexOf(paramArrayOfChar, paramChar);
    if (i == -1) {
      return clone(paramArrayOfChar);
    }
    return remove(paramArrayOfChar, i);
  }
  
  public static double[] removeElement(double[] paramArrayOfDouble, double paramDouble)
  {
    int i = indexOf(paramArrayOfDouble, paramDouble);
    if (i == -1) {
      return clone(paramArrayOfDouble);
    }
    return remove(paramArrayOfDouble, i);
  }
  
  public static float[] removeElement(float[] paramArrayOfFloat, float paramFloat)
  {
    int i = indexOf(paramArrayOfFloat, paramFloat);
    if (i == -1) {
      return clone(paramArrayOfFloat);
    }
    return remove(paramArrayOfFloat, i);
  }
  
  public static int[] removeElement(int[] paramArrayOfInt, int paramInt)
  {
    paramInt = indexOf(paramArrayOfInt, paramInt);
    if (paramInt == -1) {
      return clone(paramArrayOfInt);
    }
    return remove(paramArrayOfInt, paramInt);
  }
  
  public static long[] removeElement(long[] paramArrayOfLong, long paramLong)
  {
    int i = indexOf(paramArrayOfLong, paramLong);
    if (i == -1) {
      return clone(paramArrayOfLong);
    }
    return remove(paramArrayOfLong, i);
  }
  
  public static <T> T[] removeElement(T[] paramArrayOfT, Object paramObject)
  {
    int i = indexOf(paramArrayOfT, paramObject);
    if (i == -1) {
      return clone(paramArrayOfT);
    }
    return remove(paramArrayOfT, i);
  }
  
  public static short[] removeElement(short[] paramArrayOfShort, short paramShort)
  {
    int i = indexOf(paramArrayOfShort, paramShort);
    if (i == -1) {
      return clone(paramArrayOfShort);
    }
    return remove(paramArrayOfShort, i);
  }
  
  public static boolean[] removeElement(boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    int i = indexOf(paramArrayOfBoolean, paramBoolean);
    if (i == -1) {
      return clone(paramArrayOfBoolean);
    }
    return remove(paramArrayOfBoolean, i);
  }
  
  public static byte[] removeElements(byte[] paramArrayOfByte1, byte... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfByte1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Byte localByte;
      for (int j = 0; j < i; j++)
      {
        localByte = Byte.valueOf(paramVarArgs[j]);
        localObject2 = (MutableInt)((HashMap)localObject1).get(localByte);
        if (localObject2 == null) {
          ((HashMap)localObject1).put(localByte, new MutableInt(1));
        } else {
          ((MutableInt)localObject2).increment();
        }
      }
      paramVarArgs = new HashSet();
      Object localObject2 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject1 = (Map.Entry)((Iterator)localObject2).next();
        localByte = (Byte)((Map.Entry)localObject1).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject1).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfByte1, localByte.byteValue(), i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfByte1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfByte1);
  }
  
  public static char[] removeElements(char[] paramArrayOfChar1, char... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfChar1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Object localObject3;
      for (int j = 0; j < i; j++)
      {
        localObject2 = Character.valueOf(paramVarArgs[j]);
        localObject3 = (MutableInt)((HashMap)localObject1).get(localObject2);
        if (localObject3 == null) {
          ((HashMap)localObject1).put(localObject2, new MutableInt(1));
        } else {
          ((MutableInt)localObject3).increment();
        }
      }
      paramVarArgs = new HashSet();
      Object localObject2 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        localObject1 = (Character)((Map.Entry)localObject3).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject3).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfChar1, ((Character)localObject1).charValue(), i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfChar1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfChar1);
  }
  
  public static double[] removeElements(double[] paramArrayOfDouble1, double... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfDouble1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Object localObject3;
      for (int j = 0; j < i; j++)
      {
        localObject2 = Double.valueOf(paramVarArgs[j]);
        localObject3 = (MutableInt)((HashMap)localObject1).get(localObject2);
        if (localObject3 == null) {
          ((HashMap)localObject1).put(localObject2, new MutableInt(1));
        } else {
          ((MutableInt)localObject3).increment();
        }
      }
      paramVarArgs = new HashSet();
      Object localObject2 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject1 = (Map.Entry)((Iterator)localObject2).next();
        localObject3 = (Double)((Map.Entry)localObject1).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject1).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfDouble1, ((Double)localObject3).doubleValue(), i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfDouble1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfDouble1);
  }
  
  public static float[] removeElements(float[] paramArrayOfFloat1, float... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfFloat1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Object localObject2;
      for (int j = 0; j < i; j++)
      {
        localObject2 = Float.valueOf(paramVarArgs[j]);
        localObject3 = (MutableInt)((HashMap)localObject1).get(localObject2);
        if (localObject3 == null) {
          ((HashMap)localObject1).put(localObject2, new MutableInt(1));
        } else {
          ((MutableInt)localObject3).increment();
        }
      }
      paramVarArgs = new HashSet();
      Object localObject3 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject3).next();
        localObject1 = (Float)((Map.Entry)localObject2).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject2).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfFloat1, ((Float)localObject1).floatValue(), i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfFloat1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfFloat1);
  }
  
  public static int[] removeElements(int[] paramArrayOfInt1, int... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfInt1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Integer localInteger;
      Object localObject2;
      for (int j = 0; j < i; j++)
      {
        localInteger = Integer.valueOf(paramVarArgs[j]);
        localObject2 = (MutableInt)((HashMap)localObject1).get(localInteger);
        if (localObject2 == null) {
          ((HashMap)localObject1).put(localInteger, new MutableInt(1));
        } else {
          ((MutableInt)localObject2).increment();
        }
      }
      paramVarArgs = new HashSet();
      localObject1 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        localInteger = (Integer)((Map.Entry)localObject2).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject2).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfInt1, localInteger.intValue(), i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfInt1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfInt1);
  }
  
  public static long[] removeElements(long[] paramArrayOfLong1, long... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfLong1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Object localObject3;
      for (int j = 0; j < i; j++)
      {
        localObject2 = Long.valueOf(paramVarArgs[j]);
        localObject3 = (MutableInt)((HashMap)localObject1).get(localObject2);
        if (localObject3 == null) {
          ((HashMap)localObject1).put(localObject2, new MutableInt(1));
        } else {
          ((MutableInt)localObject3).increment();
        }
      }
      paramVarArgs = new HashSet();
      Object localObject2 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        localObject1 = (Long)((Map.Entry)localObject3).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject3).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfLong1, ((Long)localObject1).longValue(), i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfLong1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfLong1);
  }
  
  public static <T> T[] removeElements(T[] paramArrayOfT1, T... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfT1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Object localObject3;
      for (int j = 0; j < i; j++)
      {
        localObject2 = paramVarArgs[j];
        localObject3 = (MutableInt)((HashMap)localObject1).get(localObject2);
        if (localObject3 == null) {
          ((HashMap)localObject1).put(localObject2, new MutableInt(1));
        } else {
          ((MutableInt)localObject3).increment();
        }
      }
      paramVarArgs = new HashSet();
      Object localObject2 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject1 = (Map.Entry)((Iterator)localObject2).next();
        localObject3 = ((Map.Entry)localObject1).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject1).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfT1, localObject3, i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfT1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfT1);
  }
  
  public static short[] removeElements(short[] paramArrayOfShort1, short... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfShort1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Short localShort;
      for (int j = 0; j < i; j++)
      {
        localShort = Short.valueOf(paramVarArgs[j]);
        localObject2 = (MutableInt)((HashMap)localObject1).get(localShort);
        if (localObject2 == null) {
          ((HashMap)localObject1).put(localShort, new MutableInt(1));
        } else {
          ((MutableInt)localObject2).increment();
        }
      }
      paramVarArgs = new HashSet();
      Object localObject2 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject1 = (Map.Entry)((Iterator)localObject2).next();
        localShort = (Short)((Map.Entry)localObject1).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject1).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfShort1, localShort.shortValue(), i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfShort1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfShort1);
  }
  
  public static boolean[] removeElements(boolean[] paramArrayOfBoolean1, boolean... paramVarArgs)
  {
    if ((!isEmpty(paramArrayOfBoolean1)) && (!isEmpty(paramVarArgs)))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Boolean localBoolean;
      for (int j = 0; j < i; j++)
      {
        localBoolean = Boolean.valueOf(paramVarArgs[j]);
        localObject2 = (MutableInt)((HashMap)localObject1).get(localBoolean);
        if (localObject2 == null) {
          ((HashMap)localObject1).put(localBoolean, new MutableInt(1));
        } else {
          ((MutableInt)localObject2).increment();
        }
      }
      paramVarArgs = new HashSet();
      Object localObject2 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject1 = (Map.Entry)((Iterator)localObject2).next();
        localBoolean = (Boolean)((Map.Entry)localObject1).getKey();
        i = 0;
        j = 0;
        int k = ((MutableInt)((Map.Entry)localObject1).getValue()).intValue();
        while (j < k)
        {
          i = indexOf(paramArrayOfBoolean1, localBoolean.booleanValue(), i);
          if (i < 0) {
            break;
          }
          paramVarArgs.add(Integer.valueOf(i));
          j++;
          i++;
        }
      }
      return removeAll(paramArrayOfBoolean1, extractIndices(paramVarArgs));
    }
    return clone(paramArrayOfBoolean1);
  }
  
  public static void reverse(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfByte.length - 1;
    while (j > i)
    {
      int k = paramArrayOfByte[j];
      paramArrayOfByte[j] = ((byte)paramArrayOfByte[i]);
      paramArrayOfByte[i] = ((byte)k);
      j--;
      i++;
    }
  }
  
  public static void reverse(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfChar.length - 1;
    while (j > i)
    {
      int k = paramArrayOfChar[j];
      paramArrayOfChar[j] = ((char)paramArrayOfChar[i]);
      paramArrayOfChar[i] = ((char)k);
      j--;
      i++;
    }
  }
  
  public static void reverse(double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfDouble.length - 1;
    while (j > i)
    {
      double d = paramArrayOfDouble[j];
      paramArrayOfDouble[j] = paramArrayOfDouble[i];
      paramArrayOfDouble[i] = d;
      j--;
      i++;
    }
  }
  
  public static void reverse(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfFloat.length - 1;
    while (j > i)
    {
      float f = paramArrayOfFloat[j];
      paramArrayOfFloat[j] = paramArrayOfFloat[i];
      paramArrayOfFloat[i] = f;
      j--;
      i++;
    }
  }
  
  public static void reverse(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfInt.length - 1;
    while (j > i)
    {
      int k = paramArrayOfInt[j];
      paramArrayOfInt[j] = paramArrayOfInt[i];
      paramArrayOfInt[i] = k;
      j--;
      i++;
    }
  }
  
  public static void reverse(long[] paramArrayOfLong)
  {
    if (paramArrayOfLong == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfLong.length - 1;
    while (j > i)
    {
      long l = paramArrayOfLong[j];
      paramArrayOfLong[j] = paramArrayOfLong[i];
      paramArrayOfLong[i] = l;
      j--;
      i++;
    }
  }
  
  public static void reverse(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfObject.length - 1;
    while (j > i)
    {
      Object localObject = paramArrayOfObject[j];
      paramArrayOfObject[j] = paramArrayOfObject[i];
      paramArrayOfObject[i] = localObject;
      j--;
      i++;
    }
  }
  
  public static void reverse(short[] paramArrayOfShort)
  {
    if (paramArrayOfShort == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfShort.length - 1;
    while (j > i)
    {
      int k = paramArrayOfShort[j];
      paramArrayOfShort[j] = ((short)paramArrayOfShort[i]);
      paramArrayOfShort[i] = ((short)k);
      j--;
      i++;
    }
  }
  
  public static void reverse(boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfBoolean == null) {
      return;
    }
    int i = 0;
    int j = paramArrayOfBoolean.length - 1;
    while (j > i)
    {
      int k = paramArrayOfBoolean[j];
      paramArrayOfBoolean[j] = paramArrayOfBoolean[i];
      paramArrayOfBoolean[i] = k;
      j--;
      i++;
    }
  }
  
  public static byte[] subarray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfByte.length) {
      paramInt1 = paramArrayOfByte.length;
    }
    paramInt1 -= i;
    if (paramInt1 <= 0) {
      return EMPTY_BYTE_ARRAY;
    }
    byte[] arrayOfByte = new byte[paramInt1];
    System.arraycopy(paramArrayOfByte, i, arrayOfByte, 0, paramInt1);
    return arrayOfByte;
  }
  
  public static char[] subarray(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfChar.length) {
      paramInt1 = paramArrayOfChar.length;
    }
    paramInt1 -= i;
    if (paramInt1 <= 0) {
      return EMPTY_CHAR_ARRAY;
    }
    char[] arrayOfChar = new char[paramInt1];
    System.arraycopy(paramArrayOfChar, i, arrayOfChar, 0, paramInt1);
    return arrayOfChar;
  }
  
  public static double[] subarray(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    if (paramArrayOfDouble == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfDouble.length) {
      paramInt1 = paramArrayOfDouble.length;
    }
    paramInt1 -= i;
    if (paramInt1 <= 0) {
      return EMPTY_DOUBLE_ARRAY;
    }
    double[] arrayOfDouble = new double[paramInt1];
    System.arraycopy(paramArrayOfDouble, i, arrayOfDouble, 0, paramInt1);
    return arrayOfDouble;
  }
  
  public static float[] subarray(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramArrayOfFloat == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfFloat.length) {
      paramInt1 = paramArrayOfFloat.length;
    }
    paramInt1 -= i;
    if (paramInt1 <= 0) {
      return EMPTY_FLOAT_ARRAY;
    }
    float[] arrayOfFloat = new float[paramInt1];
    System.arraycopy(paramArrayOfFloat, i, arrayOfFloat, 0, paramInt1);
    return arrayOfFloat;
  }
  
  public static int[] subarray(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfInt.length) {
      paramInt1 = paramArrayOfInt.length;
    }
    paramInt1 -= i;
    if (paramInt1 <= 0) {
      return EMPTY_INT_ARRAY;
    }
    int[] arrayOfInt = new int[paramInt1];
    System.arraycopy(paramArrayOfInt, i, arrayOfInt, 0, paramInt1);
    return arrayOfInt;
  }
  
  public static long[] subarray(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    if (paramArrayOfLong == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfLong.length) {
      paramInt1 = paramArrayOfLong.length;
    }
    paramInt1 -= i;
    if (paramInt1 <= 0) {
      return EMPTY_LONG_ARRAY;
    }
    long[] arrayOfLong = new long[paramInt1];
    System.arraycopy(paramArrayOfLong, i, arrayOfLong, 0, paramInt1);
    return arrayOfLong;
  }
  
  public static <T> T[] subarray(T[] paramArrayOfT, int paramInt1, int paramInt2)
  {
    if (paramArrayOfT == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfT.length) {
      paramInt1 = paramArrayOfT.length;
    }
    paramInt1 -= i;
    Object localObject = paramArrayOfT.getClass().getComponentType();
    if (paramInt1 <= 0) {
      return (Object[])Array.newInstance((Class)localObject, 0);
    }
    localObject = (Object[])Array.newInstance((Class)localObject, paramInt1);
    System.arraycopy(paramArrayOfT, i, localObject, 0, paramInt1);
    return (T[])localObject;
  }
  
  public static short[] subarray(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    if (paramArrayOfShort == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfShort.length) {
      paramInt1 = paramArrayOfShort.length;
    }
    paramInt1 -= i;
    if (paramInt1 <= 0) {
      return EMPTY_SHORT_ARRAY;
    }
    short[] arrayOfShort = new short[paramInt1];
    System.arraycopy(paramArrayOfShort, i, arrayOfShort, 0, paramInt1);
    return arrayOfShort;
  }
  
  public static boolean[] subarray(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    if (paramArrayOfBoolean == null) {
      return null;
    }
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = 0;
    }
    paramInt1 = paramInt2;
    if (paramInt2 > paramArrayOfBoolean.length) {
      paramInt1 = paramArrayOfBoolean.length;
    }
    paramInt1 -= i;
    if (paramInt1 <= 0) {
      return EMPTY_BOOLEAN_ARRAY;
    }
    boolean[] arrayOfBoolean = new boolean[paramInt1];
    System.arraycopy(paramArrayOfBoolean, i, arrayOfBoolean, 0, paramInt1);
    return arrayOfBoolean;
  }
  
  public static <T> T[] toArray(T... paramVarArgs)
  {
    return paramVarArgs;
  }
  
  public static Map<Object, Object> toMap(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    HashMap localHashMap = new HashMap((int)(paramArrayOfObject.length * 1.5D));
    int i = 0;
    while (i < paramArrayOfObject.length)
    {
      Object localObject = paramArrayOfObject[i];
      if ((localObject instanceof Map.Entry))
      {
        localObject = (Map.Entry)localObject;
        localHashMap.put(((Map.Entry)localObject).getKey(), ((Map.Entry)localObject).getValue());
      }
      else
      {
        if (!(localObject instanceof Object[])) {
          break label165;
        }
        Object[] arrayOfObject = (Object[])localObject;
        if (arrayOfObject.length < 2) {
          break label109;
        }
        localHashMap.put(arrayOfObject[0], arrayOfObject[1]);
      }
      i++;
      continue;
      label109:
      paramArrayOfObject = new StringBuilder();
      paramArrayOfObject.append("Array element ");
      paramArrayOfObject.append(i);
      paramArrayOfObject.append(", '");
      paramArrayOfObject.append(localObject);
      paramArrayOfObject.append("', has a length less than 2");
      throw new IllegalArgumentException(paramArrayOfObject.toString());
      label165:
      paramArrayOfObject = new StringBuilder();
      paramArrayOfObject.append("Array element ");
      paramArrayOfObject.append(i);
      paramArrayOfObject.append(", '");
      paramArrayOfObject.append(localObject);
      paramArrayOfObject.append("', is neither of type Map.Entry nor an Array");
      throw new IllegalArgumentException(paramArrayOfObject.toString());
    }
    return localHashMap;
  }
  
  public static Boolean[] toObject(boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfBoolean == null) {
      return null;
    }
    if (paramArrayOfBoolean.length == 0) {
      return EMPTY_BOOLEAN_OBJECT_ARRAY;
    }
    Boolean[] arrayOfBoolean = new Boolean[paramArrayOfBoolean.length];
    for (int i = 0; i < paramArrayOfBoolean.length; i++)
    {
      Boolean localBoolean;
      if (paramArrayOfBoolean[i] != 0) {
        localBoolean = Boolean.TRUE;
      } else {
        localBoolean = Boolean.FALSE;
      }
      arrayOfBoolean[i] = localBoolean;
    }
    return arrayOfBoolean;
  }
  
  public static Byte[] toObject(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    if (paramArrayOfByte.length == 0) {
      return EMPTY_BYTE_OBJECT_ARRAY;
    }
    Byte[] arrayOfByte = new Byte[paramArrayOfByte.length];
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      arrayOfByte[i] = Byte.valueOf(paramArrayOfByte[i]);
    }
    return arrayOfByte;
  }
  
  public static Character[] toObject(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar == null) {
      return null;
    }
    if (paramArrayOfChar.length == 0) {
      return EMPTY_CHARACTER_OBJECT_ARRAY;
    }
    Character[] arrayOfCharacter = new Character[paramArrayOfChar.length];
    for (int i = 0; i < paramArrayOfChar.length; i++) {
      arrayOfCharacter[i] = Character.valueOf(paramArrayOfChar[i]);
    }
    return arrayOfCharacter;
  }
  
  public static Double[] toObject(double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble == null) {
      return null;
    }
    if (paramArrayOfDouble.length == 0) {
      return EMPTY_DOUBLE_OBJECT_ARRAY;
    }
    Double[] arrayOfDouble = new Double[paramArrayOfDouble.length];
    for (int i = 0; i < paramArrayOfDouble.length; i++) {
      arrayOfDouble[i] = Double.valueOf(paramArrayOfDouble[i]);
    }
    return arrayOfDouble;
  }
  
  public static Float[] toObject(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      return null;
    }
    if (paramArrayOfFloat.length == 0) {
      return EMPTY_FLOAT_OBJECT_ARRAY;
    }
    Float[] arrayOfFloat = new Float[paramArrayOfFloat.length];
    for (int i = 0; i < paramArrayOfFloat.length; i++) {
      arrayOfFloat[i] = Float.valueOf(paramArrayOfFloat[i]);
    }
    return arrayOfFloat;
  }
  
  public static Integer[] toObject(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    if (paramArrayOfInt.length == 0) {
      return EMPTY_INTEGER_OBJECT_ARRAY;
    }
    Integer[] arrayOfInteger = new Integer[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      arrayOfInteger[i] = Integer.valueOf(paramArrayOfInt[i]);
    }
    return arrayOfInteger;
  }
  
  public static Long[] toObject(long[] paramArrayOfLong)
  {
    if (paramArrayOfLong == null) {
      return null;
    }
    if (paramArrayOfLong.length == 0) {
      return EMPTY_LONG_OBJECT_ARRAY;
    }
    Long[] arrayOfLong = new Long[paramArrayOfLong.length];
    for (int i = 0; i < paramArrayOfLong.length; i++) {
      arrayOfLong[i] = Long.valueOf(paramArrayOfLong[i]);
    }
    return arrayOfLong;
  }
  
  public static Short[] toObject(short[] paramArrayOfShort)
  {
    if (paramArrayOfShort == null) {
      return null;
    }
    if (paramArrayOfShort.length == 0) {
      return EMPTY_SHORT_OBJECT_ARRAY;
    }
    Short[] arrayOfShort = new Short[paramArrayOfShort.length];
    for (int i = 0; i < paramArrayOfShort.length; i++) {
      arrayOfShort[i] = Short.valueOf(paramArrayOfShort[i]);
    }
    return arrayOfShort;
  }
  
  public static byte[] toPrimitive(Byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    if (paramArrayOfByte.length == 0) {
      return EMPTY_BYTE_ARRAY;
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      arrayOfByte[i] = paramArrayOfByte[i].byteValue();
    }
    return arrayOfByte;
  }
  
  public static byte[] toPrimitive(Byte[] paramArrayOfByte, byte paramByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    if (paramArrayOfByte.length == 0) {
      return EMPTY_BYTE_ARRAY;
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      Byte localByte = paramArrayOfByte[i];
      int j;
      if (localByte == null) {
        j = paramByte;
      } else {
        j = localByte.byteValue();
      }
      arrayOfByte[i] = ((byte)j);
    }
    return arrayOfByte;
  }
  
  public static char[] toPrimitive(Character[] paramArrayOfCharacter)
  {
    if (paramArrayOfCharacter == null) {
      return null;
    }
    if (paramArrayOfCharacter.length == 0) {
      return EMPTY_CHAR_ARRAY;
    }
    char[] arrayOfChar = new char[paramArrayOfCharacter.length];
    for (int i = 0; i < paramArrayOfCharacter.length; i++) {
      arrayOfChar[i] = paramArrayOfCharacter[i].charValue();
    }
    return arrayOfChar;
  }
  
  public static char[] toPrimitive(Character[] paramArrayOfCharacter, char paramChar)
  {
    if (paramArrayOfCharacter == null) {
      return null;
    }
    if (paramArrayOfCharacter.length == 0) {
      return EMPTY_CHAR_ARRAY;
    }
    char[] arrayOfChar = new char[paramArrayOfCharacter.length];
    for (int i = 0; i < paramArrayOfCharacter.length; i++)
    {
      Character localCharacter = paramArrayOfCharacter[i];
      int j;
      if (localCharacter == null) {
        j = paramChar;
      } else {
        j = localCharacter.charValue();
      }
      arrayOfChar[i] = ((char)j);
    }
    return arrayOfChar;
  }
  
  public static double[] toPrimitive(Double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble == null) {
      return null;
    }
    if (paramArrayOfDouble.length == 0) {
      return EMPTY_DOUBLE_ARRAY;
    }
    double[] arrayOfDouble = new double[paramArrayOfDouble.length];
    for (int i = 0; i < paramArrayOfDouble.length; i++) {
      arrayOfDouble[i] = paramArrayOfDouble[i].doubleValue();
    }
    return arrayOfDouble;
  }
  
  public static double[] toPrimitive(Double[] paramArrayOfDouble, double paramDouble)
  {
    if (paramArrayOfDouble == null) {
      return null;
    }
    if (paramArrayOfDouble.length == 0) {
      return EMPTY_DOUBLE_ARRAY;
    }
    double[] arrayOfDouble = new double[paramArrayOfDouble.length];
    for (int i = 0; i < paramArrayOfDouble.length; i++)
    {
      Double localDouble = paramArrayOfDouble[i];
      double d;
      if (localDouble == null) {
        d = paramDouble;
      } else {
        d = localDouble.doubleValue();
      }
      arrayOfDouble[i] = d;
    }
    return arrayOfDouble;
  }
  
  public static float[] toPrimitive(Float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      return null;
    }
    if (paramArrayOfFloat.length == 0) {
      return EMPTY_FLOAT_ARRAY;
    }
    float[] arrayOfFloat = new float[paramArrayOfFloat.length];
    for (int i = 0; i < paramArrayOfFloat.length; i++) {
      arrayOfFloat[i] = paramArrayOfFloat[i].floatValue();
    }
    return arrayOfFloat;
  }
  
  public static float[] toPrimitive(Float[] paramArrayOfFloat, float paramFloat)
  {
    if (paramArrayOfFloat == null) {
      return null;
    }
    if (paramArrayOfFloat.length == 0) {
      return EMPTY_FLOAT_ARRAY;
    }
    float[] arrayOfFloat = new float[paramArrayOfFloat.length];
    for (int i = 0; i < paramArrayOfFloat.length; i++)
    {
      Float localFloat = paramArrayOfFloat[i];
      float f;
      if (localFloat == null) {
        f = paramFloat;
      } else {
        f = localFloat.floatValue();
      }
      arrayOfFloat[i] = f;
    }
    return arrayOfFloat;
  }
  
  public static int[] toPrimitive(Integer[] paramArrayOfInteger)
  {
    if (paramArrayOfInteger == null) {
      return null;
    }
    if (paramArrayOfInteger.length == 0) {
      return EMPTY_INT_ARRAY;
    }
    int[] arrayOfInt = new int[paramArrayOfInteger.length];
    for (int i = 0; i < paramArrayOfInteger.length; i++) {
      arrayOfInt[i] = paramArrayOfInteger[i].intValue();
    }
    return arrayOfInt;
  }
  
  public static int[] toPrimitive(Integer[] paramArrayOfInteger, int paramInt)
  {
    if (paramArrayOfInteger == null) {
      return null;
    }
    if (paramArrayOfInteger.length == 0) {
      return EMPTY_INT_ARRAY;
    }
    int[] arrayOfInt = new int[paramArrayOfInteger.length];
    for (int i = 0; i < paramArrayOfInteger.length; i++)
    {
      Integer localInteger = paramArrayOfInteger[i];
      int j;
      if (localInteger == null) {
        j = paramInt;
      } else {
        j = localInteger.intValue();
      }
      arrayOfInt[i] = j;
    }
    return arrayOfInt;
  }
  
  public static long[] toPrimitive(Long[] paramArrayOfLong)
  {
    if (paramArrayOfLong == null) {
      return null;
    }
    if (paramArrayOfLong.length == 0) {
      return EMPTY_LONG_ARRAY;
    }
    long[] arrayOfLong = new long[paramArrayOfLong.length];
    for (int i = 0; i < paramArrayOfLong.length; i++) {
      arrayOfLong[i] = paramArrayOfLong[i].longValue();
    }
    return arrayOfLong;
  }
  
  public static long[] toPrimitive(Long[] paramArrayOfLong, long paramLong)
  {
    if (paramArrayOfLong == null) {
      return null;
    }
    if (paramArrayOfLong.length == 0) {
      return EMPTY_LONG_ARRAY;
    }
    long[] arrayOfLong = new long[paramArrayOfLong.length];
    for (int i = 0; i < paramArrayOfLong.length; i++)
    {
      Long localLong = paramArrayOfLong[i];
      long l;
      if (localLong == null) {
        l = paramLong;
      } else {
        l = localLong.longValue();
      }
      arrayOfLong[i] = l;
    }
    return arrayOfLong;
  }
  
  public static short[] toPrimitive(Short[] paramArrayOfShort)
  {
    if (paramArrayOfShort == null) {
      return null;
    }
    if (paramArrayOfShort.length == 0) {
      return EMPTY_SHORT_ARRAY;
    }
    short[] arrayOfShort = new short[paramArrayOfShort.length];
    for (int i = 0; i < paramArrayOfShort.length; i++) {
      arrayOfShort[i] = paramArrayOfShort[i].shortValue();
    }
    return arrayOfShort;
  }
  
  public static short[] toPrimitive(Short[] paramArrayOfShort, short paramShort)
  {
    if (paramArrayOfShort == null) {
      return null;
    }
    if (paramArrayOfShort.length == 0) {
      return EMPTY_SHORT_ARRAY;
    }
    short[] arrayOfShort = new short[paramArrayOfShort.length];
    for (int i = 0; i < paramArrayOfShort.length; i++)
    {
      Short localShort = paramArrayOfShort[i];
      int j;
      if (localShort == null) {
        j = paramShort;
      } else {
        j = localShort.shortValue();
      }
      arrayOfShort[i] = ((short)j);
    }
    return arrayOfShort;
  }
  
  public static boolean[] toPrimitive(Boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfBoolean == null) {
      return null;
    }
    if (paramArrayOfBoolean.length == 0) {
      return EMPTY_BOOLEAN_ARRAY;
    }
    boolean[] arrayOfBoolean = new boolean[paramArrayOfBoolean.length];
    for (int i = 0; i < paramArrayOfBoolean.length; i++) {
      arrayOfBoolean[i] = paramArrayOfBoolean[i].booleanValue();
    }
    return arrayOfBoolean;
  }
  
  public static boolean[] toPrimitive(Boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    if (paramArrayOfBoolean == null) {
      return null;
    }
    if (paramArrayOfBoolean.length == 0) {
      return EMPTY_BOOLEAN_ARRAY;
    }
    boolean[] arrayOfBoolean = new boolean[paramArrayOfBoolean.length];
    for (int i = 0; i < paramArrayOfBoolean.length; i++)
    {
      Boolean localBoolean = paramArrayOfBoolean[i];
      boolean bool;
      if (localBoolean == null) {
        bool = paramBoolean;
      } else {
        bool = localBoolean.booleanValue();
      }
      arrayOfBoolean[i] = bool;
    }
    return arrayOfBoolean;
  }
  
  public static String toString(Object paramObject)
  {
    return toString(paramObject, "{}");
  }
  
  public static String toString(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      return paramString;
    }
    return new ToStringBuilder(paramObject, ToStringStyle.SIMPLE_STYLE).append(paramObject).toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/ArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */