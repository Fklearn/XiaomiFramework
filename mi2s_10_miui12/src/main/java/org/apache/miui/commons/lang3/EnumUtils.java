package org.apache.miui.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnumUtils
{
  private static <E extends Enum<E>> Class<E> checkBitVectorable(Class<E> paramClass)
  {
    Validate.notNull(paramClass, "EnumClass must be defined.", new Object[0]);
    Enum[] arrayOfEnum = (Enum[])paramClass.getEnumConstants();
    boolean bool;
    if (arrayOfEnum != null) {
      bool = true;
    } else {
      bool = false;
    }
    Validate.isTrue(bool, "%s does not seem to be an Enum type", new Object[] { paramClass });
    if (arrayOfEnum.length <= 64) {
      bool = true;
    } else {
      bool = false;
    }
    Validate.isTrue(bool, "Cannot store %s %s values in %s bits", new Object[] { Integer.valueOf(arrayOfEnum.length), paramClass.getSimpleName(), Integer.valueOf(64) });
    return paramClass;
  }
  
  public static <E extends Enum<E>> long generateBitVector(Class<E> paramClass, Iterable<E> paramIterable)
  {
    checkBitVectorable(paramClass);
    Validate.notNull(paramIterable);
    long l = 0L;
    paramClass = paramIterable.iterator();
    while (paramClass.hasNext()) {
      l |= 1 << ((Enum)paramClass.next()).ordinal();
    }
    return l;
  }
  
  public static <E extends Enum<E>> long generateBitVector(Class<E> paramClass, E... paramVarArgs)
  {
    Validate.noNullElements(paramVarArgs);
    return generateBitVector(paramClass, Arrays.asList(paramVarArgs));
  }
  
  public static <E extends Enum<E>> E getEnum(Class<E> paramClass, String paramString)
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      paramClass = Enum.valueOf(paramClass, paramString);
      return paramClass;
    }
    catch (IllegalArgumentException paramClass) {}
    return null;
  }
  
  public static <E extends Enum<E>> List<E> getEnumList(Class<E> paramClass)
  {
    return new ArrayList(Arrays.asList((Enum[])paramClass.getEnumConstants()));
  }
  
  public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> paramClass)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    for (paramClass : (Enum[])paramClass.getEnumConstants()) {
      localLinkedHashMap.put(paramClass.name(), paramClass);
    }
    return localLinkedHashMap;
  }
  
  public static <E extends Enum<E>> boolean isValidEnum(Class<E> paramClass, String paramString)
  {
    if (paramString == null) {
      return false;
    }
    try
    {
      Enum.valueOf(paramClass, paramString);
      return true;
    }
    catch (IllegalArgumentException paramClass) {}
    return false;
  }
  
  public static <E extends Enum<E>> EnumSet<E> processBitVector(Class<E> paramClass, long paramLong)
  {
    Enum[] arrayOfEnum = (Enum[])checkBitVectorable(paramClass).getEnumConstants();
    EnumSet localEnumSet = EnumSet.noneOf(paramClass);
    int i = arrayOfEnum.length;
    for (int j = 0; j < i; j++)
    {
      paramClass = arrayOfEnum[j];
      if ((1 << paramClass.ordinal() & paramLong) != 0L) {
        localEnumSet.add(paramClass);
      }
    }
    return localEnumSet;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/EnumUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */