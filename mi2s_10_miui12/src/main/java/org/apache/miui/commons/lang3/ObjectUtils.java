package org.apache.miui.commons.lang3;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.apache.miui.commons.lang3.exception.CloneFailedException;
import org.apache.miui.commons.lang3.mutable.MutableInt;

public class ObjectUtils
{
  public static final Null NULL = new Null();
  
  public static <T> T clone(T paramT)
  {
    if ((paramT instanceof Cloneable))
    {
      Object localObject;
      if (paramT.getClass().isArray())
      {
        localObject = paramT.getClass().getComponentType();
        if (!((Class)localObject).isPrimitive())
        {
          paramT = ((Object[])paramT).clone();
        }
        else
        {
          int i = Array.getLength(paramT);
          localObject = Array.newInstance((Class)localObject, i);
          for (;;)
          {
            int j = i - 1;
            if (i <= 0) {
              break;
            }
            Array.set(localObject, j, Array.get(paramT, j));
            i = j;
          }
          paramT = (T)localObject;
        }
      }
      try
      {
        localObject = paramT.getClass().getMethod("clone", new Class[0]).invoke(paramT, new Object[0]);
        paramT = (T)localObject;
        return paramT;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Exception cloning Cloneable type ");
        ((StringBuilder)localObject).append(paramT.getClass().getName());
        throw new CloneFailedException(((StringBuilder)localObject).toString(), localInvocationTargetException.getCause());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Cannot clone Cloneable type ");
        localStringBuilder.append(paramT.getClass().getName());
        throw new CloneFailedException(localStringBuilder.toString(), localIllegalAccessException);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Cloneable type ");
        localStringBuilder.append(paramT.getClass().getName());
        localStringBuilder.append(" has no clone method");
        throw new CloneFailedException(localStringBuilder.toString(), localNoSuchMethodException);
      }
    }
    return null;
  }
  
  public static <T> T cloneIfPossible(T paramT)
  {
    Object localObject = clone(paramT);
    if (localObject != null) {
      paramT = (T)localObject;
    }
    return paramT;
  }
  
  public static <T extends Comparable<? super T>> int compare(T paramT1, T paramT2)
  {
    return compare(paramT1, paramT2, false);
  }
  
  public static <T extends Comparable<? super T>> int compare(T paramT1, T paramT2, boolean paramBoolean)
  {
    if (paramT1 == paramT2) {
      return 0;
    }
    int i = 1;
    int j = 1;
    if (paramT1 == null)
    {
      if (!paramBoolean) {
        j = -1;
      }
      return j;
    }
    if (paramT2 == null)
    {
      j = i;
      if (paramBoolean) {
        j = -1;
      }
      return j;
    }
    return paramT1.compareTo(paramT2);
  }
  
  public static <T> T defaultIfNull(T paramT1, T paramT2)
  {
    if (paramT1 == null) {
      paramT1 = paramT2;
    }
    return paramT1;
  }
  
  public static boolean equals(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == paramObject2) {
      return true;
    }
    if ((paramObject1 != null) && (paramObject2 != null)) {
      return paramObject1.equals(paramObject2);
    }
    return false;
  }
  
  public static <T> T firstNonNull(T... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      int i = paramVarArgs.length;
      for (int j = 0; j < i; j++)
      {
        T ? = paramVarArgs[j];
        if (? != null) {
          return ?;
        }
      }
    }
    return null;
  }
  
  public static int hashCode(Object paramObject)
  {
    int i;
    if (paramObject == null) {
      i = 0;
    } else {
      i = paramObject.hashCode();
    }
    return i;
  }
  
  public static int hashCodeMulti(Object... paramVarArgs)
  {
    int i = 1;
    int j = 1;
    if (paramVarArgs != null)
    {
      int k = paramVarArgs.length;
      for (int m = 0;; m++)
      {
        i = j;
        if (m >= k) {
          break;
        }
        j = j * 31 + hashCode(paramVarArgs[m]);
      }
    }
    return i;
  }
  
  public static String identityToString(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    identityToString(localStringBuffer, paramObject);
    return localStringBuffer.toString();
  }
  
  public static void identityToString(StringBuffer paramStringBuffer, Object paramObject)
  {
    if (paramObject != null)
    {
      paramStringBuffer.append(paramObject.getClass().getName());
      paramStringBuffer.append('@');
      paramStringBuffer.append(Integer.toHexString(System.identityHashCode(paramObject)));
      return;
    }
    throw new NullPointerException("Cannot get the toString of a null identity");
  }
  
  public static <T extends Comparable<? super T>> T max(T... paramVarArgs)
  {
    Object localObject1 = null;
    if (paramVarArgs != null)
    {
      int i = paramVarArgs.length;
      localObject1 = null;
      int j = 0;
      while (j < i)
      {
        T ? = paramVarArgs[j];
        Object localObject2 = localObject1;
        if (compare(?, (Comparable)localObject1, false) > 0) {
          localObject2 = ?;
        }
        j++;
        localObject1 = localObject2;
      }
    }
    return (T)localObject1;
  }
  
  public static <T extends Comparable<? super T>> T median(T... paramVarArgs)
  {
    Validate.notEmpty(paramVarArgs);
    Validate.noNullElements(paramVarArgs);
    TreeSet localTreeSet = new TreeSet();
    Collections.addAll(localTreeSet, paramVarArgs);
    return (Comparable)localTreeSet.toArray()[((localTreeSet.size() - 1) / 2)];
  }
  
  public static <T> T median(Comparator<T> paramComparator, T... paramVarArgs)
  {
    Validate.notEmpty(paramVarArgs, "null/empty items", new Object[0]);
    Validate.noNullElements(paramVarArgs);
    Validate.notNull(paramComparator, "null comparator", new Object[0]);
    paramComparator = new TreeSet(paramComparator);
    Collections.addAll(paramComparator, paramVarArgs);
    return (T)paramComparator.toArray()[((paramComparator.size() - 1) / 2)];
  }
  
  public static <T extends Comparable<? super T>> T min(T... paramVarArgs)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    if (paramVarArgs != null)
    {
      int i = paramVarArgs.length;
      int j = 0;
      for (;;)
      {
        localObject1 = localObject2;
        if (j >= i) {
          break;
        }
        T ? = paramVarArgs[j];
        localObject1 = localObject2;
        if (compare(?, (Comparable)localObject2, true) < 0) {
          localObject1 = ?;
        }
        j++;
        localObject2 = localObject1;
      }
    }
    return (T)localObject1;
  }
  
  public static <T> T mode(T... paramVarArgs)
  {
    if (ArrayUtils.isNotEmpty(paramVarArgs))
    {
      Object localObject1 = new HashMap(paramVarArgs.length);
      int i = paramVarArgs.length;
      Object localObject2;
      for (int j = 0; j < i; j++)
      {
        T ? = paramVarArgs[j];
        localObject2 = (MutableInt)((HashMap)localObject1).get(?);
        if (localObject2 == null) {
          ((HashMap)localObject1).put(?, new MutableInt(1));
        } else {
          ((MutableInt)localObject2).increment();
        }
      }
      paramVarArgs = null;
      i = 0;
      localObject1 = ((HashMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        int k = ((MutableInt)((Map.Entry)localObject2).getValue()).intValue();
        if (k == i)
        {
          paramVarArgs = null;
          j = i;
        }
        else
        {
          j = i;
          if (k > i)
          {
            j = k;
            paramVarArgs = ((Map.Entry)localObject2).getKey();
          }
        }
        i = j;
      }
      return paramVarArgs;
    }
    return null;
  }
  
  public static boolean notEqual(Object paramObject1, Object paramObject2)
  {
    return equals(paramObject1, paramObject2) ^ true;
  }
  
  public static String toString(Object paramObject)
  {
    if (paramObject == null) {
      paramObject = "";
    } else {
      paramObject = paramObject.toString();
    }
    return (String)paramObject;
  }
  
  public static String toString(Object paramObject, String paramString)
  {
    if (paramObject != null) {
      paramString = paramObject.toString();
    }
    return paramString;
  }
  
  public static class Null
    implements Serializable
  {
    private static final long serialVersionUID = 7092611880189329093L;
    
    private Object readResolve()
    {
      return ObjectUtils.NULL;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/ObjectUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */