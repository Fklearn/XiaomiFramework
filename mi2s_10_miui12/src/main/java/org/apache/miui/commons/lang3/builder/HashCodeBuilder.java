package org.apache.miui.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.miui.commons.lang3.ArrayUtils;

public class HashCodeBuilder
  implements Builder<Integer>
{
  private static final ThreadLocal<Set<IDKey>> REGISTRY = new ThreadLocal();
  private final int iConstant;
  private int iTotal = 0;
  
  public HashCodeBuilder()
  {
    this.iConstant = 37;
    this.iTotal = 17;
  }
  
  public HashCodeBuilder(int paramInt1, int paramInt2)
  {
    if (paramInt1 != 0)
    {
      if (paramInt1 % 2 != 0)
      {
        if (paramInt2 != 0)
        {
          if (paramInt2 % 2 != 0)
          {
            this.iConstant = paramInt2;
            this.iTotal = paramInt1;
            return;
          }
          throw new IllegalArgumentException("HashCodeBuilder requires an odd multiplier");
        }
        throw new IllegalArgumentException("HashCodeBuilder requires a non zero multiplier");
      }
      throw new IllegalArgumentException("HashCodeBuilder requires an odd initial value");
    }
    throw new IllegalArgumentException("HashCodeBuilder requires a non zero initial value");
  }
  
  static Set<IDKey> getRegistry()
  {
    return (Set)REGISTRY.get();
  }
  
  static boolean isRegistered(Object paramObject)
  {
    Set localSet = getRegistry();
    boolean bool;
    if ((localSet != null) && (localSet.contains(new IDKey(paramObject)))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private static void reflectionAppend(Object paramObject, Class<?> paramClass, HashCodeBuilder paramHashCodeBuilder, boolean paramBoolean, String[] paramArrayOfString)
  {
    if (isRegistered(paramObject)) {
      return;
    }
    try
    {
      register(paramObject);
      Field[] arrayOfField = paramClass.getDeclaredFields();
      AccessibleObject.setAccessible(arrayOfField, true);
      int i = arrayOfField.length;
      for (int j = 0; j < i; j++)
      {
        paramClass = arrayOfField[j];
        if ((!ArrayUtils.contains(paramArrayOfString, paramClass.getName())) && (paramClass.getName().indexOf('$') == -1) && ((paramBoolean) || (!Modifier.isTransient(paramClass.getModifiers()))))
        {
          boolean bool = Modifier.isStatic(paramClass.getModifiers());
          if (!bool) {
            try
            {
              paramHashCodeBuilder.append(paramClass.get(paramObject));
            }
            catch (IllegalAccessException paramClass)
            {
              paramClass = new java/lang/InternalError;
              paramClass.<init>("Unexpected IllegalAccessException");
              throw paramClass;
            }
          }
        }
      }
      return;
    }
    finally
    {
      unregister(paramObject);
    }
  }
  
  public static int reflectionHashCode(int paramInt1, int paramInt2, Object paramObject)
  {
    return reflectionHashCode(paramInt1, paramInt2, paramObject, false, null, new String[0]);
  }
  
  public static int reflectionHashCode(int paramInt1, int paramInt2, Object paramObject, boolean paramBoolean)
  {
    return reflectionHashCode(paramInt1, paramInt2, paramObject, paramBoolean, null, new String[0]);
  }
  
  public static <T> int reflectionHashCode(int paramInt1, int paramInt2, T paramT, boolean paramBoolean, Class<? super T> paramClass, String... paramVarArgs)
  {
    if (paramT != null)
    {
      HashCodeBuilder localHashCodeBuilder = new HashCodeBuilder(paramInt1, paramInt2);
      Class localClass = paramT.getClass();
      reflectionAppend(paramT, localClass, localHashCodeBuilder, paramBoolean, paramVarArgs);
      while ((localClass.getSuperclass() != null) && (localClass != paramClass))
      {
        localClass = localClass.getSuperclass();
        reflectionAppend(paramT, localClass, localHashCodeBuilder, paramBoolean, paramVarArgs);
      }
      return localHashCodeBuilder.toHashCode();
    }
    throw new IllegalArgumentException("The object to build a hash code for must not be null");
  }
  
  public static int reflectionHashCode(Object paramObject, Collection<String> paramCollection)
  {
    return reflectionHashCode(paramObject, ReflectionToStringBuilder.toNoNullStringArray(paramCollection));
  }
  
  public static int reflectionHashCode(Object paramObject, boolean paramBoolean)
  {
    return reflectionHashCode(17, 37, paramObject, paramBoolean, null, new String[0]);
  }
  
  public static int reflectionHashCode(Object paramObject, String... paramVarArgs)
  {
    return reflectionHashCode(17, 37, paramObject, false, null, paramVarArgs);
  }
  
  static void register(Object paramObject)
  {
    try
    {
      if (getRegistry() == null)
      {
        ThreadLocal localThreadLocal = REGISTRY;
        HashSet localHashSet = new java/util/HashSet;
        localHashSet.<init>();
        localThreadLocal.set(localHashSet);
      }
      getRegistry().add(new IDKey(paramObject));
      return;
    }
    finally {}
  }
  
  static void unregister(Object paramObject)
  {
    Set localSet = getRegistry();
    if (localSet != null)
    {
      localSet.remove(new IDKey(paramObject));
      try
      {
        paramObject = getRegistry();
        if ((paramObject != null) && (((Set)paramObject).isEmpty())) {
          REGISTRY.remove();
        }
      }
      finally {}
    }
  }
  
  public HashCodeBuilder append(byte paramByte)
  {
    this.iTotal = (this.iTotal * this.iConstant + paramByte);
    return this;
  }
  
  public HashCodeBuilder append(char paramChar)
  {
    this.iTotal = (this.iTotal * this.iConstant + paramChar);
    return this;
  }
  
  public HashCodeBuilder append(double paramDouble)
  {
    return append(Double.doubleToLongBits(paramDouble));
  }
  
  public HashCodeBuilder append(float paramFloat)
  {
    this.iTotal = (this.iTotal * this.iConstant + Float.floatToIntBits(paramFloat));
    return this;
  }
  
  public HashCodeBuilder append(int paramInt)
  {
    this.iTotal = (this.iTotal * this.iConstant + paramInt);
    return this;
  }
  
  public HashCodeBuilder append(long paramLong)
  {
    this.iTotal = (this.iTotal * this.iConstant + (int)(paramLong >> 32 ^ paramLong));
    return this;
  }
  
  public HashCodeBuilder append(Object paramObject)
  {
    if (paramObject == null) {
      this.iTotal *= this.iConstant;
    } else if (paramObject.getClass().isArray())
    {
      if ((paramObject instanceof long[])) {
        append((long[])paramObject);
      } else if ((paramObject instanceof int[])) {
        append((int[])paramObject);
      } else if ((paramObject instanceof short[])) {
        append((short[])paramObject);
      } else if ((paramObject instanceof char[])) {
        append((char[])paramObject);
      } else if ((paramObject instanceof byte[])) {
        append((byte[])paramObject);
      } else if ((paramObject instanceof double[])) {
        append((double[])paramObject);
      } else if ((paramObject instanceof float[])) {
        append((float[])paramObject);
      } else if ((paramObject instanceof boolean[])) {
        append((boolean[])paramObject);
      } else {
        append((Object[])paramObject);
      }
    }
    else {
      this.iTotal = (this.iTotal * this.iConstant + paramObject.hashCode());
    }
    return this;
  }
  
  public HashCodeBuilder append(short paramShort)
  {
    this.iTotal = (this.iTotal * this.iConstant + paramShort);
    return this;
  }
  
  public HashCodeBuilder append(boolean paramBoolean)
  {
    this.iTotal = (this.iTotal * this.iConstant + (paramBoolean ^ true));
    return this;
  }
  
  public HashCodeBuilder append(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfByte.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfByte[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder append(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfChar.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfChar[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder append(double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfDouble.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfDouble[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder append(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfFloat.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfFloat[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder append(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfInt.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfInt[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder append(long[] paramArrayOfLong)
  {
    if (paramArrayOfLong == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfLong.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfLong[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder append(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfObject.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfObject[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder append(short[] paramArrayOfShort)
  {
    if (paramArrayOfShort == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfShort.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfShort[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder append(boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfBoolean == null)
    {
      this.iTotal *= this.iConstant;
    }
    else
    {
      int i = paramArrayOfBoolean.length;
      for (int j = 0; j < i; j++) {
        append(paramArrayOfBoolean[j]);
      }
    }
    return this;
  }
  
  public HashCodeBuilder appendSuper(int paramInt)
  {
    this.iTotal = (this.iTotal * this.iConstant + paramInt);
    return this;
  }
  
  public Integer build()
  {
    return Integer.valueOf(toHashCode());
  }
  
  public int hashCode()
  {
    return toHashCode();
  }
  
  public int toHashCode()
  {
    return this.iTotal;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/builder/HashCodeBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */