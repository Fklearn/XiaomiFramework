package org.apache.miui.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.miui.commons.lang3.ArrayUtils;
import org.apache.miui.commons.lang3.tuple.Pair;

public class EqualsBuilder
  implements Builder<Boolean>
{
  private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = new ThreadLocal();
  private boolean isEquals = true;
  
  static Pair<IDKey, IDKey> getRegisterPair(Object paramObject1, Object paramObject2)
  {
    return Pair.of(new IDKey(paramObject1), new IDKey(paramObject2));
  }
  
  static Set<Pair<IDKey, IDKey>> getRegistry()
  {
    return (Set)REGISTRY.get();
  }
  
  static boolean isRegistered(Object paramObject1, Object paramObject2)
  {
    Set localSet = getRegistry();
    paramObject1 = getRegisterPair(paramObject1, paramObject2);
    paramObject2 = Pair.of((IDKey)((Pair)paramObject1).getLeft(), (IDKey)((Pair)paramObject1).getRight());
    boolean bool;
    if ((localSet != null) && ((localSet.contains(paramObject1)) || (localSet.contains(paramObject2)))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private static void reflectionAppend(Object paramObject1, Object paramObject2, Class<?> paramClass, EqualsBuilder paramEqualsBuilder, boolean paramBoolean, String[] paramArrayOfString)
  {
    if (isRegistered(paramObject1, paramObject2)) {
      return;
    }
    try
    {
      register(paramObject1, paramObject2);
      paramClass = paramClass.getDeclaredFields();
      AccessibleObject.setAccessible(paramClass, true);
      for (int i = 0; (i < paramClass.length) && (paramEqualsBuilder.isEquals); i++)
      {
        Object localObject = paramClass[i];
        if ((!ArrayUtils.contains(paramArrayOfString, ((Field)localObject).getName())) && (((Field)localObject).getName().indexOf('$') == -1) && ((paramBoolean) || (!Modifier.isTransient(((Field)localObject).getModifiers()))))
        {
          boolean bool = Modifier.isStatic(((Field)localObject).getModifiers());
          if (!bool) {
            try
            {
              paramEqualsBuilder.append(((Field)localObject).get(paramObject1), ((Field)localObject).get(paramObject2));
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
      unregister(paramObject1, paramObject2);
    }
  }
  
  public static boolean reflectionEquals(Object paramObject1, Object paramObject2, Collection<String> paramCollection)
  {
    return reflectionEquals(paramObject1, paramObject2, ReflectionToStringBuilder.toNoNullStringArray(paramCollection));
  }
  
  public static boolean reflectionEquals(Object paramObject1, Object paramObject2, boolean paramBoolean)
  {
    return reflectionEquals(paramObject1, paramObject2, paramBoolean, null, new String[0]);
  }
  
  public static boolean reflectionEquals(Object paramObject1, Object paramObject2, boolean paramBoolean, Class<?> paramClass, String... paramVarArgs)
  {
    if (paramObject1 == paramObject2) {
      return true;
    }
    if ((paramObject1 != null) && (paramObject2 != null))
    {
      Object localObject1 = paramObject1.getClass();
      Object localObject2 = paramObject2.getClass();
      if (((Class)localObject1).isInstance(paramObject2))
      {
        if (!((Class)localObject2).isInstance(paramObject1)) {
          localObject1 = localObject2;
        }
      }
      else
      {
        if (!((Class)localObject2).isInstance(paramObject1)) {
          break label157;
        }
        if (((Class)localObject1).isInstance(paramObject2)) {
          localObject1 = localObject2;
        }
      }
      localObject2 = new EqualsBuilder();
      try
      {
        reflectionAppend(paramObject1, paramObject2, (Class)localObject1, (EqualsBuilder)localObject2, paramBoolean, paramVarArgs);
        for (;;)
        {
          Class localClass = ((Class)localObject1).getSuperclass();
          if ((localClass != null) && (localObject1 != paramClass)) {
            try
            {
              localObject1 = ((Class)localObject1).getSuperclass();
              reflectionAppend(paramObject1, paramObject2, (Class)localObject1, (EqualsBuilder)localObject2, paramBoolean, paramVarArgs);
            }
            catch (IllegalArgumentException paramObject1)
            {
              break label155;
            }
          }
        }
        return ((EqualsBuilder)localObject2).isEquals();
      }
      catch (IllegalArgumentException paramObject1) {}
      label155:
      return false;
      label157:
      return false;
    }
    return false;
  }
  
  public static boolean reflectionEquals(Object paramObject1, Object paramObject2, String... paramVarArgs)
  {
    return reflectionEquals(paramObject1, paramObject2, false, null, paramVarArgs);
  }
  
  static void register(Object paramObject1, Object paramObject2)
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
      getRegistry().add(getRegisterPair(paramObject1, paramObject2));
      return;
    }
    finally {}
  }
  
  static void unregister(Object paramObject1, Object paramObject2)
  {
    Set localSet = getRegistry();
    if (localSet != null)
    {
      localSet.remove(getRegisterPair(paramObject1, paramObject2));
      try
      {
        paramObject1 = getRegistry();
        if ((paramObject1 != null) && (((Set)paramObject1).isEmpty())) {
          REGISTRY.remove();
        }
      }
      finally {}
    }
  }
  
  public EqualsBuilder append(byte paramByte1, byte paramByte2)
  {
    if (!this.isEquals) {
      return this;
    }
    boolean bool;
    if (paramByte1 == paramByte2) {
      bool = true;
    } else {
      bool = false;
    }
    this.isEquals = bool;
    return this;
  }
  
  public EqualsBuilder append(char paramChar1, char paramChar2)
  {
    if (!this.isEquals) {
      return this;
    }
    boolean bool;
    if (paramChar1 == paramChar2) {
      bool = true;
    } else {
      bool = false;
    }
    this.isEquals = bool;
    return this;
  }
  
  public EqualsBuilder append(double paramDouble1, double paramDouble2)
  {
    if (!this.isEquals) {
      return this;
    }
    return append(Double.doubleToLongBits(paramDouble1), Double.doubleToLongBits(paramDouble2));
  }
  
  public EqualsBuilder append(float paramFloat1, float paramFloat2)
  {
    if (!this.isEquals) {
      return this;
    }
    return append(Float.floatToIntBits(paramFloat1), Float.floatToIntBits(paramFloat2));
  }
  
  public EqualsBuilder append(int paramInt1, int paramInt2)
  {
    if (!this.isEquals) {
      return this;
    }
    boolean bool;
    if (paramInt1 == paramInt2) {
      bool = true;
    } else {
      bool = false;
    }
    this.isEquals = bool;
    return this;
  }
  
  public EqualsBuilder append(long paramLong1, long paramLong2)
  {
    if (!this.isEquals) {
      return this;
    }
    boolean bool;
    if (paramLong1 == paramLong2) {
      bool = true;
    } else {
      bool = false;
    }
    this.isEquals = bool;
    return this;
  }
  
  public EqualsBuilder append(Object paramObject1, Object paramObject2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramObject1 == paramObject2) {
      return this;
    }
    if ((paramObject1 != null) && (paramObject2 != null))
    {
      if (!paramObject1.getClass().isArray()) {
        this.isEquals = paramObject1.equals(paramObject2);
      } else if (paramObject1.getClass() != paramObject2.getClass()) {
        setEquals(false);
      } else if ((paramObject1 instanceof long[])) {
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
        append((Object[])paramObject1, (Object[])paramObject2);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(short paramShort1, short paramShort2)
  {
    if (!this.isEquals) {
      return this;
    }
    boolean bool;
    if (paramShort1 == paramShort2) {
      bool = true;
    } else {
      bool = false;
    }
    this.isEquals = bool;
    return this;
  }
  
  public EqualsBuilder append(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramBoolean1 == paramBoolean2) {
      paramBoolean1 = true;
    } else {
      paramBoolean1 = false;
    }
    this.isEquals = paramBoolean1;
    return this;
  }
  
  public EqualsBuilder append(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfByte1 == paramArrayOfByte2) {
      return this;
    }
    if ((paramArrayOfByte1 != null) && (paramArrayOfByte2 != null))
    {
      if (paramArrayOfByte1.length != paramArrayOfByte2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfByte1.length) && (this.isEquals); i++) {
        append(paramArrayOfByte1[i], paramArrayOfByte2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(char[] paramArrayOfChar1, char[] paramArrayOfChar2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfChar1 == paramArrayOfChar2) {
      return this;
    }
    if ((paramArrayOfChar1 != null) && (paramArrayOfChar2 != null))
    {
      if (paramArrayOfChar1.length != paramArrayOfChar2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfChar1.length) && (this.isEquals); i++) {
        append(paramArrayOfChar1[i], paramArrayOfChar2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfDouble1 == paramArrayOfDouble2) {
      return this;
    }
    if ((paramArrayOfDouble1 != null) && (paramArrayOfDouble2 != null))
    {
      if (paramArrayOfDouble1.length != paramArrayOfDouble2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfDouble1.length) && (this.isEquals); i++) {
        append(paramArrayOfDouble1[i], paramArrayOfDouble2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfFloat1 == paramArrayOfFloat2) {
      return this;
    }
    if ((paramArrayOfFloat1 != null) && (paramArrayOfFloat2 != null))
    {
      if (paramArrayOfFloat1.length != paramArrayOfFloat2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfFloat1.length) && (this.isEquals); i++) {
        append(paramArrayOfFloat1[i], paramArrayOfFloat2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfInt1 == paramArrayOfInt2) {
      return this;
    }
    if ((paramArrayOfInt1 != null) && (paramArrayOfInt2 != null))
    {
      if (paramArrayOfInt1.length != paramArrayOfInt2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfInt1.length) && (this.isEquals); i++) {
        append(paramArrayOfInt1[i], paramArrayOfInt2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfLong1 == paramArrayOfLong2) {
      return this;
    }
    if ((paramArrayOfLong1 != null) && (paramArrayOfLong2 != null))
    {
      if (paramArrayOfLong1.length != paramArrayOfLong2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfLong1.length) && (this.isEquals); i++) {
        append(paramArrayOfLong1[i], paramArrayOfLong2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfObject1 == paramArrayOfObject2) {
      return this;
    }
    if ((paramArrayOfObject1 != null) && (paramArrayOfObject2 != null))
    {
      if (paramArrayOfObject1.length != paramArrayOfObject2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfObject1.length) && (this.isEquals); i++) {
        append(paramArrayOfObject1[i], paramArrayOfObject2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfShort1 == paramArrayOfShort2) {
      return this;
    }
    if ((paramArrayOfShort1 != null) && (paramArrayOfShort2 != null))
    {
      if (paramArrayOfShort1.length != paramArrayOfShort2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfShort1.length) && (this.isEquals); i++) {
        append(paramArrayOfShort1[i], paramArrayOfShort2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder append(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    if (!this.isEquals) {
      return this;
    }
    if (paramArrayOfBoolean1 == paramArrayOfBoolean2) {
      return this;
    }
    if ((paramArrayOfBoolean1 != null) && (paramArrayOfBoolean2 != null))
    {
      if (paramArrayOfBoolean1.length != paramArrayOfBoolean2.length)
      {
        setEquals(false);
        return this;
      }
      for (int i = 0; (i < paramArrayOfBoolean1.length) && (this.isEquals); i++) {
        append(paramArrayOfBoolean1[i], paramArrayOfBoolean2[i]);
      }
      return this;
    }
    setEquals(false);
    return this;
  }
  
  public EqualsBuilder appendSuper(boolean paramBoolean)
  {
    if (!this.isEquals) {
      return this;
    }
    this.isEquals = paramBoolean;
    return this;
  }
  
  public Boolean build()
  {
    return Boolean.valueOf(isEquals());
  }
  
  public boolean isEquals()
  {
    return this.isEquals;
  }
  
  public void reset()
  {
    this.isEquals = true;
  }
  
  protected void setEquals(boolean paramBoolean)
  {
    this.isEquals = paramBoolean;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/builder/EqualsBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */