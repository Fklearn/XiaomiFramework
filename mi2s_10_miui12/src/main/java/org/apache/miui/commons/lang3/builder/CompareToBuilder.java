package org.apache.miui.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import org.apache.miui.commons.lang3.ArrayUtils;

public class CompareToBuilder
  implements Builder<Integer>
{
  private int comparison = 0;
  
  private static void reflectionAppend(Object paramObject1, Object paramObject2, Class<?> paramClass, CompareToBuilder paramCompareToBuilder, boolean paramBoolean, String[] paramArrayOfString)
  {
    paramClass = paramClass.getDeclaredFields();
    AccessibleObject.setAccessible(paramClass, true);
    for (int i = 0; (i < paramClass.length) && (paramCompareToBuilder.comparison == 0); i++)
    {
      Object localObject = paramClass[i];
      if ((!ArrayUtils.contains(paramArrayOfString, ((Field)localObject).getName())) && (((Field)localObject).getName().indexOf('$') == -1) && ((paramBoolean) || (!Modifier.isTransient(((Field)localObject).getModifiers()))) && (!Modifier.isStatic(((Field)localObject).getModifiers()))) {
        try
        {
          paramCompareToBuilder.append(((Field)localObject).get(paramObject1), ((Field)localObject).get(paramObject2));
        }
        catch (IllegalAccessException paramObject1)
        {
          throw new InternalError("Unexpected IllegalAccessException");
        }
      }
    }
  }
  
  public static int reflectionCompare(Object paramObject1, Object paramObject2)
  {
    return reflectionCompare(paramObject1, paramObject2, false, null, new String[0]);
  }
  
  public static int reflectionCompare(Object paramObject1, Object paramObject2, Collection<String> paramCollection)
  {
    return reflectionCompare(paramObject1, paramObject2, ReflectionToStringBuilder.toNoNullStringArray(paramCollection));
  }
  
  public static int reflectionCompare(Object paramObject1, Object paramObject2, boolean paramBoolean)
  {
    return reflectionCompare(paramObject1, paramObject2, paramBoolean, null, new String[0]);
  }
  
  public static int reflectionCompare(Object paramObject1, Object paramObject2, boolean paramBoolean, Class<?> paramClass, String... paramVarArgs)
  {
    if (paramObject1 == paramObject2) {
      return 0;
    }
    if ((paramObject1 != null) && (paramObject2 != null))
    {
      Class localClass = paramObject1.getClass();
      if (localClass.isInstance(paramObject2))
      {
        CompareToBuilder localCompareToBuilder = new CompareToBuilder();
        reflectionAppend(paramObject1, paramObject2, localClass, localCompareToBuilder, paramBoolean, paramVarArgs);
        while ((localClass.getSuperclass() != null) && (localClass != paramClass))
        {
          localClass = localClass.getSuperclass();
          reflectionAppend(paramObject1, paramObject2, localClass, localCompareToBuilder, paramBoolean, paramVarArgs);
        }
        return localCompareToBuilder.toComparison();
      }
      throw new ClassCastException();
    }
    throw new NullPointerException();
  }
  
  public static int reflectionCompare(Object paramObject1, Object paramObject2, String... paramVarArgs)
  {
    return reflectionCompare(paramObject1, paramObject2, false, null, paramVarArgs);
  }
  
  public CompareToBuilder append(byte paramByte1, byte paramByte2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramByte1 < paramByte2) {
      paramByte1 = -1;
    } else if (paramByte1 > paramByte2) {
      paramByte1 = 1;
    } else {
      paramByte1 = 0;
    }
    this.comparison = paramByte1;
    return this;
  }
  
  public CompareToBuilder append(char paramChar1, char paramChar2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramChar1 < paramChar2) {
      paramChar1 = '￿';
    } else if (paramChar1 > paramChar2) {
      paramChar1 = '\001';
    } else {
      paramChar1 = '\000';
    }
    this.comparison = paramChar1;
    return this;
  }
  
  public CompareToBuilder append(double paramDouble1, double paramDouble2)
  {
    if (this.comparison != 0) {
      return this;
    }
    this.comparison = Double.compare(paramDouble1, paramDouble2);
    return this;
  }
  
  public CompareToBuilder append(float paramFloat1, float paramFloat2)
  {
    if (this.comparison != 0) {
      return this;
    }
    this.comparison = Float.compare(paramFloat1, paramFloat2);
    return this;
  }
  
  public CompareToBuilder append(int paramInt1, int paramInt2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramInt1 < paramInt2) {
      paramInt1 = -1;
    } else if (paramInt1 > paramInt2) {
      paramInt1 = 1;
    } else {
      paramInt1 = 0;
    }
    this.comparison = paramInt1;
    return this;
  }
  
  public CompareToBuilder append(long paramLong1, long paramLong2)
  {
    if (this.comparison != 0) {
      return this;
    }
    int i;
    if (paramLong1 < paramLong2) {
      i = -1;
    } else if (paramLong1 > paramLong2) {
      i = 1;
    } else {
      i = 0;
    }
    this.comparison = i;
    return this;
  }
  
  public CompareToBuilder append(Object paramObject1, Object paramObject2)
  {
    return append(paramObject1, paramObject2, null);
  }
  
  public CompareToBuilder append(Object paramObject1, Object paramObject2, Comparator<?> paramComparator)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramObject1 == paramObject2) {
      return this;
    }
    if (paramObject1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramObject2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramObject1.getClass().isArray())
    {
      if ((paramObject1 instanceof long[])) {
        append((long[])paramObject1, (long[])paramObject2);
      } else if ((paramObject1 instanceof int[])) {
        append((int[])paramObject1, (int[])paramObject2);
      } else if ((paramObject1 instanceof short[])) {
        append((short[])paramObject1, (short[])paramObject2);
      } else if ((paramObject1 instanceof char[])) {
        append((char[])paramObject1, (char[])paramObject2);
      } else if ((paramObject1 instanceof byte[])) {
        append((byte[])paramObject1, (byte[])paramObject2);
      } else if ((paramObject1 instanceof double[])) {
        append((double[])paramObject1, (double[])paramObject2);
      } else if ((paramObject1 instanceof float[])) {
        append((float[])paramObject1, (float[])paramObject2);
      } else if ((paramObject1 instanceof boolean[])) {
        append((boolean[])paramObject1, (boolean[])paramObject2);
      } else {
        append((Object[])paramObject1, (Object[])paramObject2, paramComparator);
      }
    }
    else if (paramComparator == null) {
      this.comparison = ((Comparable)paramObject1).compareTo(paramObject2);
    } else {
      this.comparison = paramComparator.compare(paramObject1, paramObject2);
    }
    return this;
  }
  
  public CompareToBuilder append(short paramShort1, short paramShort2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramShort1 < paramShort2) {
      paramShort1 = -1;
    } else if (paramShort1 > paramShort2) {
      paramShort1 = 1;
    } else {
      paramShort1 = 0;
    }
    this.comparison = paramShort1;
    return this;
  }
  
  public CompareToBuilder append(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramBoolean1 == paramBoolean2) {
      return this;
    }
    if (!paramBoolean1) {
      this.comparison = -1;
    } else {
      this.comparison = 1;
    }
    return this;
  }
  
  public CompareToBuilder append(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfByte1 == paramArrayOfByte2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfByte1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfByte2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfByte1.length != paramArrayOfByte2.length)
    {
      if (paramArrayOfByte1.length >= paramArrayOfByte2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfByte1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfByte1[i], paramArrayOfByte2[i]);
    }
    return this;
  }
  
  public CompareToBuilder append(char[] paramArrayOfChar1, char[] paramArrayOfChar2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfChar1 == paramArrayOfChar2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfChar1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfChar2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfChar1.length != paramArrayOfChar2.length)
    {
      if (paramArrayOfChar1.length >= paramArrayOfChar2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfChar1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfChar1[i], paramArrayOfChar2[i]);
    }
    return this;
  }
  
  public CompareToBuilder append(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfDouble1 == paramArrayOfDouble2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfDouble1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfDouble2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfDouble1.length != paramArrayOfDouble2.length)
    {
      if (paramArrayOfDouble1.length >= paramArrayOfDouble2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfDouble1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfDouble1[i], paramArrayOfDouble2[i]);
    }
    return this;
  }
  
  public CompareToBuilder append(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfFloat1 == paramArrayOfFloat2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfFloat1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfFloat2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfFloat1.length != paramArrayOfFloat2.length)
    {
      if (paramArrayOfFloat1.length >= paramArrayOfFloat2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfFloat1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfFloat1[i], paramArrayOfFloat2[i]);
    }
    return this;
  }
  
  public CompareToBuilder append(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfInt1 == paramArrayOfInt2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfInt1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfInt2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfInt1.length != paramArrayOfInt2.length)
    {
      if (paramArrayOfInt1.length >= paramArrayOfInt2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfInt1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfInt1[i], paramArrayOfInt2[i]);
    }
    return this;
  }
  
  public CompareToBuilder append(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfLong1 == paramArrayOfLong2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfLong1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfLong2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfLong1.length != paramArrayOfLong2.length)
    {
      if (paramArrayOfLong1.length >= paramArrayOfLong2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfLong1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfLong1[i], paramArrayOfLong2[i]);
    }
    return this;
  }
  
  public CompareToBuilder append(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
  {
    return append(paramArrayOfObject1, paramArrayOfObject2, null);
  }
  
  public CompareToBuilder append(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, Comparator<?> paramComparator)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfObject1 == paramArrayOfObject2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfObject1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfObject2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfObject1.length != paramArrayOfObject2.length)
    {
      if (paramArrayOfObject1.length >= paramArrayOfObject2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfObject1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfObject1[i], paramArrayOfObject2[i], paramComparator);
    }
    return this;
  }
  
  public CompareToBuilder append(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfShort1 == paramArrayOfShort2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfShort1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfShort2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfShort1.length != paramArrayOfShort2.length)
    {
      if (paramArrayOfShort1.length >= paramArrayOfShort2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfShort1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfShort1[i], paramArrayOfShort2[i]);
    }
    return this;
  }
  
  public CompareToBuilder append(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    if (this.comparison != 0) {
      return this;
    }
    if (paramArrayOfBoolean1 == paramArrayOfBoolean2) {
      return this;
    }
    int i = -1;
    if (paramArrayOfBoolean1 == null)
    {
      this.comparison = -1;
      return this;
    }
    if (paramArrayOfBoolean2 == null)
    {
      this.comparison = 1;
      return this;
    }
    if (paramArrayOfBoolean1.length != paramArrayOfBoolean2.length)
    {
      if (paramArrayOfBoolean1.length >= paramArrayOfBoolean2.length) {
        i = 1;
      }
      this.comparison = i;
      return this;
    }
    for (i = 0; (i < paramArrayOfBoolean1.length) && (this.comparison == 0); i++) {
      append(paramArrayOfBoolean1[i], paramArrayOfBoolean2[i]);
    }
    return this;
  }
  
  public CompareToBuilder appendSuper(int paramInt)
  {
    if (this.comparison != 0) {
      return this;
    }
    this.comparison = paramInt;
    return this;
  }
  
  public Integer build()
  {
    return Integer.valueOf(toComparison());
  }
  
  public int toComparison()
  {
    return this.comparison;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/builder/CompareToBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */