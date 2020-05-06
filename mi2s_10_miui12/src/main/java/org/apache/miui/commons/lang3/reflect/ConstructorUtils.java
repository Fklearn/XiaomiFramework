package org.apache.miui.commons.lang3.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.miui.commons.lang3.ArrayUtils;
import org.apache.miui.commons.lang3.ClassUtils;

public class ConstructorUtils
{
  public static <T> Constructor<T> getAccessibleConstructor(Class<T> paramClass, Class<?>... paramVarArgs)
  {
    try
    {
      paramClass = getAccessibleConstructor(paramClass.getConstructor(paramVarArgs));
      return paramClass;
    }
    catch (NoSuchMethodException paramClass) {}
    return null;
  }
  
  public static <T> Constructor<T> getAccessibleConstructor(Constructor<T> paramConstructor)
  {
    if ((!MemberUtils.isAccessible(paramConstructor)) || (!Modifier.isPublic(paramConstructor.getDeclaringClass().getModifiers()))) {
      paramConstructor = null;
    }
    return paramConstructor;
  }
  
  public static <T> Constructor<T> getMatchingAccessibleConstructor(Class<T> paramClass, Class<?>... paramVarArgs)
  {
    try
    {
      Constructor localConstructor1 = paramClass.getConstructor(paramVarArgs);
      MemberUtils.setAccessibleWorkaround(localConstructor1);
      return localConstructor1;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Class<T> localClass = null;
      Constructor[] arrayOfConstructor = paramClass.getConstructors();
      int i = arrayOfConstructor.length;
      int j = 0;
      while (j < i)
      {
        Constructor localConstructor2 = arrayOfConstructor[j];
        paramClass = localClass;
        if (ClassUtils.isAssignable(paramVarArgs, localConstructor2.getParameterTypes(), true))
        {
          localConstructor2 = getAccessibleConstructor(localConstructor2);
          paramClass = localClass;
          if (localConstructor2 != null)
          {
            MemberUtils.setAccessibleWorkaround(localConstructor2);
            if (localClass != null)
            {
              paramClass = localClass;
              if (MemberUtils.compareParameterTypes(localConstructor2.getParameterTypes(), localClass.getParameterTypes(), paramVarArgs) >= 0) {}
            }
            else
            {
              paramClass = localConstructor2;
            }
          }
        }
        j++;
        localClass = paramClass;
      }
      return localClass;
    }
  }
  
  public static <T> T invokeConstructor(Class<T> paramClass, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    Object[] arrayOfObject = paramVarArgs;
    if (paramVarArgs == null) {
      arrayOfObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    paramVarArgs = new Class[arrayOfObject.length];
    for (int i = 0; i < arrayOfObject.length; i++) {
      paramVarArgs[i] = arrayOfObject[i].getClass();
    }
    return (T)invokeConstructor(paramClass, arrayOfObject, paramVarArgs);
  }
  
  public static <T> T invokeConstructor(Class<T> paramClass, Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    Object localObject = paramArrayOfClass;
    if (paramArrayOfClass == null) {
      localObject = ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    paramArrayOfClass = paramArrayOfObject;
    if (paramArrayOfObject == null) {
      paramArrayOfClass = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    paramArrayOfObject = getMatchingAccessibleConstructor(paramClass, (Class[])localObject);
    if (paramArrayOfObject != null) {
      return (T)paramArrayOfObject.newInstance(paramArrayOfClass);
    }
    paramArrayOfObject = new StringBuilder();
    paramArrayOfObject.append("No such accessible constructor on object: ");
    paramArrayOfObject.append(paramClass.getName());
    throw new NoSuchMethodException(paramArrayOfObject.toString());
  }
  
  public static <T> T invokeExactConstructor(Class<T> paramClass, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
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
    return (T)invokeExactConstructor(paramClass, arrayOfObject, paramVarArgs);
  }
  
  public static <T> T invokeExactConstructor(Class<T> paramClass, Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    Object[] arrayOfObject = paramArrayOfObject;
    if (paramArrayOfObject == null) {
      arrayOfObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    paramArrayOfObject = paramArrayOfClass;
    if (paramArrayOfClass == null) {
      paramArrayOfObject = ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    paramArrayOfObject = getAccessibleConstructor(paramClass, paramArrayOfObject);
    if (paramArrayOfObject != null) {
      return (T)paramArrayOfObject.newInstance(arrayOfObject);
    }
    paramArrayOfObject = new StringBuilder();
    paramArrayOfObject.append("No such accessible constructor on object: ");
    paramArrayOfObject.append(paramClass.getName());
    throw new NoSuchMethodException(paramArrayOfObject.toString());
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/reflect/ConstructorUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */