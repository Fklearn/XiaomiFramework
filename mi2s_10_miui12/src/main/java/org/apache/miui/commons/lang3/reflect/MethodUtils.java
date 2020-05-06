package org.apache.miui.commons.lang3.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.miui.commons.lang3.ArrayUtils;
import org.apache.miui.commons.lang3.ClassUtils;

public class MethodUtils
{
  public static Method getAccessibleMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
  {
    try
    {
      paramClass = getAccessibleMethod(paramClass.getMethod(paramString, paramVarArgs));
      return paramClass;
    }
    catch (NoSuchMethodException paramClass) {}
    return null;
  }
  
  public static Method getAccessibleMethod(Method paramMethod)
  {
    if (!MemberUtils.isAccessible(paramMethod)) {
      return null;
    }
    Class localClass = paramMethod.getDeclaringClass();
    if (Modifier.isPublic(localClass.getModifiers())) {
      return paramMethod;
    }
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Method localMethod = getAccessibleMethodFromInterfaceNest(localClass, str, arrayOfClass);
    paramMethod = localMethod;
    if (localMethod == null) {
      paramMethod = getAccessibleMethodFromSuperclass(localClass, str, arrayOfClass);
    }
    return paramMethod;
  }
  
  private static Method getAccessibleMethodFromInterfaceNest(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
  {
    Object localObject1 = null;
    Object localObject3 = paramClass;
    Object localObject2;
    for (paramClass = (Class<?>)localObject1; localObject3 != null; paramClass = (Class<?>)localObject2)
    {
      Class[] arrayOfClass = ((Class)localObject3).getInterfaces();
      for (int i = 0;; i++)
      {
        localObject1 = paramClass;
        if (i >= arrayOfClass.length) {
          break;
        }
        if (Modifier.isPublic(arrayOfClass[i].getModifiers()))
        {
          try
          {
            localObject1 = arrayOfClass[i].getDeclaredMethod(paramString, paramVarArgs);
            paramClass = (Class<?>)localObject1;
          }
          catch (NoSuchMethodException localNoSuchMethodException) {}
          if (paramClass != null)
          {
            localObject2 = paramClass;
          }
          else
          {
            localObject2 = getAccessibleMethodFromInterfaceNest(arrayOfClass[i], paramString, paramVarArgs);
            paramClass = (Class<?>)localObject2;
            if (localObject2 != null) {
              break;
            }
          }
        }
      }
      localObject3 = ((Class)localObject3).getSuperclass();
    }
    return paramClass;
  }
  
  private static Method getAccessibleMethodFromSuperclass(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
  {
    for (paramClass = paramClass.getSuperclass(); paramClass != null; paramClass = paramClass.getSuperclass()) {
      if (Modifier.isPublic(paramClass.getModifiers())) {
        try
        {
          paramClass = paramClass.getMethod(paramString, paramVarArgs);
          return paramClass;
        }
        catch (NoSuchMethodException paramClass)
        {
          return null;
        }
      }
    }
    return null;
  }
  
  public static Method getMatchingAccessibleMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
  {
    try
    {
      Method localMethod1 = paramClass.getMethod(paramString, paramVarArgs);
      MemberUtils.setAccessibleWorkaround(localMethod1);
      return localMethod1;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Object localObject = null;
      Method[] arrayOfMethod = paramClass.getMethods();
      int i = arrayOfMethod.length;
      int j = 0;
      for (paramClass = (Class<?>)localObject; j < i; paramClass = (Class<?>)localObject)
      {
        Method localMethod2 = arrayOfMethod[j];
        localObject = paramClass;
        if (localMethod2.getName().equals(paramString))
        {
          localObject = paramClass;
          if (ClassUtils.isAssignable(paramVarArgs, localMethod2.getParameterTypes(), true))
          {
            localMethod2 = getAccessibleMethod(localMethod2);
            localObject = paramClass;
            if (localMethod2 != null) {
              if (paramClass != null)
              {
                localObject = paramClass;
                if (MemberUtils.compareParameterTypes(localMethod2.getParameterTypes(), paramClass.getParameterTypes(), paramVarArgs) >= 0) {}
              }
              else
              {
                localObject = localMethod2;
              }
            }
          }
        }
        j++;
      }
      if (paramClass != null) {
        MemberUtils.setAccessibleWorkaround(paramClass);
      }
    }
    return paramClass;
  }
  
  public static Object invokeExactMethod(Object paramObject, String paramString, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Object[] arrayOfObject = paramVarArgs;
    if (paramVarArgs == null) {
      arrayOfObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    int i = arrayOfObject.length;
    paramVarArgs = new Class[i];
    for (int j = 0; j < i; j++) {
      paramVarArgs[j] = arrayOfObject[j].getClass();
    }
    return invokeExactMethod(paramObject, paramString, arrayOfObject, paramVarArgs);
  }
  
  public static Object invokeExactMethod(Object paramObject, String paramString, Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Object[] arrayOfObject = paramArrayOfObject;
    if (paramArrayOfObject == null) {
      arrayOfObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    paramArrayOfObject = paramArrayOfClass;
    if (paramArrayOfClass == null) {
      paramArrayOfObject = ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    paramArrayOfObject = getAccessibleMethod(paramObject.getClass(), paramString, paramArrayOfObject);
    if (paramArrayOfObject != null) {
      return paramArrayOfObject.invoke(paramObject, arrayOfObject);
    }
    paramArrayOfObject = new StringBuilder();
    paramArrayOfObject.append("No such accessible method: ");
    paramArrayOfObject.append(paramString);
    paramArrayOfObject.append("() on object: ");
    paramArrayOfObject.append(paramObject.getClass().getName());
    throw new NoSuchMethodException(paramArrayOfObject.toString());
  }
  
  public static Object invokeExactStaticMethod(Class<?> paramClass, String paramString, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Object[] arrayOfObject = paramVarArgs;
    if (paramVarArgs == null) {
      arrayOfObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    int i = arrayOfObject.length;
    paramVarArgs = new Class[i];
    for (int j = 0; j < i; j++) {
      paramVarArgs[j] = arrayOfObject[j].getClass();
    }
    return invokeExactStaticMethod(paramClass, paramString, arrayOfObject, paramVarArgs);
  }
  
  public static Object invokeExactStaticMethod(Class<?> paramClass, String paramString, Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Object[] arrayOfObject = paramArrayOfObject;
    if (paramArrayOfObject == null) {
      arrayOfObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    paramArrayOfObject = paramArrayOfClass;
    if (paramArrayOfClass == null) {
      paramArrayOfObject = ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    paramArrayOfObject = getAccessibleMethod(paramClass, paramString, paramArrayOfObject);
    if (paramArrayOfObject != null) {
      return paramArrayOfObject.invoke(null, arrayOfObject);
    }
    paramArrayOfObject = new StringBuilder();
    paramArrayOfObject.append("No such accessible method: ");
    paramArrayOfObject.append(paramString);
    paramArrayOfObject.append("() on class: ");
    paramArrayOfObject.append(paramClass.getName());
    throw new NoSuchMethodException(paramArrayOfObject.toString());
  }
  
  public static Object invokeMethod(Object paramObject, String paramString, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Object[] arrayOfObject = paramVarArgs;
    if (paramVarArgs == null) {
      arrayOfObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    int i = arrayOfObject.length;
    paramVarArgs = new Class[i];
    for (int j = 0; j < i; j++) {
      paramVarArgs[j] = arrayOfObject[j].getClass();
    }
    return invokeMethod(paramObject, paramString, arrayOfObject, paramVarArgs);
  }
  
  public static Object invokeMethod(Object paramObject, String paramString, Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Object localObject = paramArrayOfClass;
    if (paramArrayOfClass == null) {
      localObject = ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    paramArrayOfClass = paramArrayOfObject;
    if (paramArrayOfObject == null) {
      paramArrayOfClass = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    paramArrayOfObject = getMatchingAccessibleMethod(paramObject.getClass(), paramString, (Class[])localObject);
    if (paramArrayOfObject != null) {
      return paramArrayOfObject.invoke(paramObject, paramArrayOfClass);
    }
    paramArrayOfObject = new StringBuilder();
    paramArrayOfObject.append("No such accessible method: ");
    paramArrayOfObject.append(paramString);
    paramArrayOfObject.append("() on object: ");
    paramArrayOfObject.append(paramObject.getClass().getName());
    throw new NoSuchMethodException(paramArrayOfObject.toString());
  }
  
  public static Object invokeStaticMethod(Class<?> paramClass, String paramString, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Object[] arrayOfObject = paramVarArgs;
    if (paramVarArgs == null) {
      arrayOfObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    int i = arrayOfObject.length;
    paramVarArgs = new Class[i];
    for (int j = 0; j < i; j++) {
      paramVarArgs[j] = arrayOfObject[j].getClass();
    }
    return invokeStaticMethod(paramClass, paramString, arrayOfObject, paramVarArgs);
  }
  
  public static Object invokeStaticMethod(Class<?> paramClass, String paramString, Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Object localObject = paramArrayOfClass;
    if (paramArrayOfClass == null) {
      localObject = ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    paramArrayOfClass = paramArrayOfObject;
    if (paramArrayOfObject == null) {
      paramArrayOfClass = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    paramArrayOfObject = getMatchingAccessibleMethod(paramClass, paramString, (Class[])localObject);
    if (paramArrayOfObject != null) {
      return paramArrayOfObject.invoke(null, paramArrayOfClass);
    }
    paramArrayOfObject = new StringBuilder();
    paramArrayOfObject.append("No such accessible method: ");
    paramArrayOfObject.append(paramString);
    paramArrayOfObject.append("() on class: ");
    paramArrayOfObject.append(paramClass.getName());
    throw new NoSuchMethodException(paramArrayOfObject.toString());
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/reflect/MethodUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */