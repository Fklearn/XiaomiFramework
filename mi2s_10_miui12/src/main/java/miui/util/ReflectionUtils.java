package miui.util;

import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.miui.commons.lang3.ClassUtils;
import org.apache.miui.commons.lang3.reflect.MemberUtils;

public class ReflectionUtils
{
  public static final ClassLoader BOOTCLASSLOADER = ;
  private static final String TAG = "ReflectionUtils";
  private static final HashMap<String, Constructor<?>> constructorCache = new HashMap();
  private static final HashMap<String, Field> fieldCache = new HashMap();
  private static final HashMap<String, Method> methodCache = new HashMap();
  
  public static <T> T callMethod(Object paramObject, String paramString, Class<T> paramClass, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    return (T)checkMethodReturnValue(findMethodBestMatch(paramObject.getClass(), paramString, paramVarArgs).invoke(paramObject, paramVarArgs), paramClass);
  }
  
  public static <T> T callStaticMethod(Class<?> paramClass, String paramString, Class<T> paramClass1, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    paramClass = findMethodBestMatch(paramClass, paramString, paramVarArgs);
    try
    {
      paramClass = checkMethodReturnValue(paramClass.invoke(null, paramVarArgs), paramClass1);
      return paramClass;
    }
    catch (NullPointerException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      throw new IllegalArgumentException(paramClass);
    }
  }
  
  private static Object checkFieldValue(Object paramObject, Class<?> paramClass)
    throws IllegalArgumentException
  {
    if (paramClass != Void.class)
    {
      if (paramObject == null) {
        return null;
      }
      if (paramClass == null) {
        return paramObject;
      }
      if (ClassUtils.isAssignable(paramObject.getClass(), paramClass, true)) {
        return paramObject;
      }
      throw new IllegalArgumentException("fieldClazz");
    }
    throw new IllegalArgumentException("fieldClazz");
  }
  
  private static Object checkMethodReturnValue(Object paramObject, Class<?> paramClass)
    throws IllegalArgumentException
  {
    if (paramObject == null) {
      return null;
    }
    if (paramClass == null) {
      return paramObject;
    }
    if (paramClass == Void.class) {
      return null;
    }
    if (ClassUtils.isAssignable(paramObject.getClass(), paramClass, true)) {
      return paramObject;
    }
    throw new IllegalArgumentException("returnValueClazz");
  }
  
  public static Class<?> findClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    ClassLoader localClassLoader = paramClassLoader;
    if (paramClassLoader == null) {
      localClassLoader = BOOTCLASSLOADER;
    }
    return ClassUtils.getClass(localClassLoader, paramString, false);
  }
  
  public static Constructor<?> findConstructorBestMatch(Class<?> arg0, Class<?>... arg1)
    throws NoSuchMethodException
  {
    ??? = new StringBuilder(???.getName());
    ((StringBuilder)???).append(getParametersString(???));
    ((StringBuilder)???).append("#bestmatch");
    str = ((StringBuilder)???).toString();
    synchronized (constructorCache)
    {
      if (constructorCache.containsKey(str))
      {
        ??? = (Constructor)constructorCache.get(str);
        if (??? != null) {
          return (Constructor<?>)???;
        }
        ??? = new java/lang/NoSuchMethodException;
        ???.<init>(str);
        throw ???;
      }
      try
      {
        Constructor localConstructor = findConstructorExact(???, ???);
        synchronized (constructorCache)
        {
          constructorCache.put(str, localConstructor);
          return localConstructor;
        }
        Object localObject2;
        synchronized (constructorCache)
        {
          Constructor[] arrayOfConstructor;
          int i;
          int j;
          Object localObject4;
          constructorCache.put(str, null);
          throw ???;
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        arrayOfConstructor = ???.getDeclaredConstructors();
        i = arrayOfConstructor.length;
        j = 0;
        for (??? = null; j < i; ??? = (Class<?>)localObject2)
        {
          localObject4 = arrayOfConstructor[j];
          localObject2 = ???;
          if (ClassUtils.isAssignable(???, ((Constructor)localObject4).getParameterTypes(), true)) {
            if (??? != null)
            {
              localObject2 = ???;
              if (MemberUtils.compareParameterTypes(((Constructor)localObject4).getParameterTypes(), ???.getParameterTypes(), ???) >= 0) {}
            }
            else
            {
              localObject2 = localObject4;
            }
          }
          j++;
        }
        if (??? != null)
        {
          ???.setAccessible(true);
          synchronized (constructorCache)
          {
            constructorCache.put(str, ???);
            return (Constructor<?>)???;
          }
        }
        ??? = new NoSuchMethodException(str);
      }
    }
  }
  
  public static Constructor<?> findConstructorBestMatch(Class<?> paramClass, Object... paramVarArgs)
    throws NoSuchMethodException
  {
    return findConstructorBestMatch(paramClass, getParameterTypes(paramVarArgs));
  }
  
  public static Constructor<?> findConstructorExact(Class<?> arg0, Class<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    localObject = new StringBuilder(???.getName());
    ((StringBuilder)localObject).append(getParametersString(paramVarArgs));
    ((StringBuilder)localObject).append("#exact");
    localObject = ((StringBuilder)localObject).toString();
    synchronized (constructorCache)
    {
      if (constructorCache.containsKey(localObject))
      {
        ??? = (Constructor)constructorCache.get(localObject);
        if (??? != null) {
          return (Constructor<?>)???;
        }
        ??? = new java/lang/NoSuchMethodException;
        ???.<init>((String)localObject);
        throw ???;
      }
      try
      {
        paramVarArgs = ???.getDeclaredConstructor(paramVarArgs);
        paramVarArgs.setAccessible(true);
        synchronized (constructorCache)
        {
          constructorCache.put(localObject, paramVarArgs);
          return paramVarArgs;
        }
        synchronized (constructorCache)
        {
          constructorCache.put(localObject, null);
          throw paramVarArgs;
        }
      }
      catch (NoSuchMethodException paramVarArgs) {}
    }
  }
  
  public static Field findField(Class<?> arg0, String paramString)
    throws NoSuchFieldException
  {
    localObject = new StringBuilder(???.getName());
    ((StringBuilder)localObject).append('#');
    ((StringBuilder)localObject).append(paramString);
    localObject = ((StringBuilder)localObject).toString();
    synchronized (fieldCache)
    {
      if (fieldCache.containsKey(localObject))
      {
        ??? = (Field)fieldCache.get(localObject);
        if (??? != null) {
          return (Field)???;
        }
        ??? = new java/lang/NoSuchFieldException;
        ???.<init>((String)localObject);
        throw ???;
      }
      try
      {
        paramString = findFieldRecursiveImpl(???, paramString);
        paramString.setAccessible(true);
        synchronized (fieldCache)
        {
          fieldCache.put(localObject, paramString);
          return paramString;
        }
        synchronized (fieldCache)
        {
          fieldCache.put(localObject, null);
          throw paramString;
        }
      }
      catch (NoSuchFieldException paramString) {}
    }
  }
  
  private static Field findFieldRecursiveImpl(Class<?> paramClass, String paramString)
    throws NoSuchFieldException
  {
    try
    {
      Field localField1 = paramClass.getDeclaredField(paramString);
      return localField1;
    }
    catch (NoSuchFieldException localNoSuchFieldException1)
    {
      for (;;)
      {
        paramClass = paramClass.getSuperclass();
        if ((paramClass == null) || (paramClass.equals(Object.class))) {
          break;
        }
        try
        {
          Field localField2 = paramClass.getDeclaredField(paramString);
          return localField2;
        }
        catch (NoSuchFieldException localNoSuchFieldException2) {}
      }
      throw localNoSuchFieldException1;
    }
  }
  
  public static Method findMethodBestMatch(Class<?> arg0, String arg1, Class<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    ??? = new StringBuilder(???.getName());
    ((StringBuilder)???).append('#');
    ((StringBuilder)???).append(???);
    ((StringBuilder)???).append(getParametersString(paramVarArgs));
    ((StringBuilder)???).append("#bestmatch");
    str = ((StringBuilder)???).toString();
    synchronized (methodCache)
    {
      if (methodCache.containsKey(str))
      {
        ??? = (Method)methodCache.get(str);
        if (??? != null) {
          return (Method)???;
        }
        ??? = new java/lang/NoSuchMethodException;
        ???.<init>(str);
        throw ???;
      }
      try
      {
        Method localMethod = findMethodExact(???, ???, paramVarArgs);
        synchronized (methodCache)
        {
          methodCache.put(str, localMethod);
          return localMethod;
        }
        Object localObject2;
        synchronized (methodCache)
        {
          Method[] arrayOfMethod;
          int i;
          int j;
          Object localObject4;
          methodCache.put(str, null);
          throw ???;
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        arrayOfMethod = ???.getDeclaredMethods();
        i = arrayOfMethod.length;
        j = 0;
        for (??? = null; j < i; ??? = (Class<?>)localObject2)
        {
          localObject4 = arrayOfMethod[j];
          localObject2 = ???;
          if (((Method)localObject4).getName().equals(???))
          {
            localObject2 = ???;
            if (ClassUtils.isAssignable(paramVarArgs, ((Method)localObject4).getParameterTypes(), true)) {
              if (??? != null)
              {
                localObject2 = ???;
                if (MemberUtils.compareParameterTypes(((Method)localObject4).getParameterTypes(), ???.getParameterTypes(), paramVarArgs) >= 0) {}
              }
              else
              {
                localObject2 = localObject4;
              }
            }
          }
          j++;
        }
        if (??? != null)
        {
          ???.setAccessible(true);
          synchronized (methodCache)
          {
            methodCache.put(str, ???);
            return (Method)???;
          }
        }
        ??? = new NoSuchMethodException(str);
      }
    }
  }
  
  public static Method findMethodBestMatch(Class<?> paramClass, String paramString, Object... paramVarArgs)
    throws NoSuchMethodException
  {
    return findMethodBestMatch(paramClass, paramString, getParameterTypes(paramVarArgs));
  }
  
  public static Method findMethodExact(Class<?> arg0, String paramString, Class<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    localObject = new StringBuilder(???.getName());
    ((StringBuilder)localObject).append('#');
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(getParametersString(paramVarArgs));
    ((StringBuilder)localObject).append("#exact");
    localObject = ((StringBuilder)localObject).toString();
    synchronized (methodCache)
    {
      if (methodCache.containsKey(localObject))
      {
        ??? = (Method)methodCache.get(localObject);
        if (??? != null) {
          return (Method)???;
        }
        ??? = new java/lang/NoSuchMethodException;
        ???.<init>((String)localObject);
        throw ???;
      }
      try
      {
        paramString = ???.getDeclaredMethod(paramString, paramVarArgs);
        paramString.setAccessible(true);
        synchronized (methodCache)
        {
          methodCache.put(localObject, paramString);
          return paramString;
        }
        synchronized (methodCache)
        {
          methodCache.put(localObject, null);
          throw paramString;
        }
      }
      catch (NoSuchMethodException paramString) {}
    }
  }
  
  public static Method findMethodExact(Class<?> paramClass, String paramString, Object... paramVarArgs)
    throws ClassNotFoundException, NoSuchMethodException
  {
    Object localObject1 = null;
    int i = paramVarArgs.length - 1;
    while (i >= 0)
    {
      Object localObject2 = paramVarArgs[i];
      if (localObject2 != null)
      {
        Object localObject3 = localObject1;
        if (localObject1 == null) {
          localObject3 = new Class[i + 1];
        }
        if ((localObject2 instanceof Class))
        {
          localObject3[i] = ((Class)localObject2);
        }
        else
        {
          if (!(localObject2 instanceof String)) {
            break label95;
          }
          localObject3[i] = findClass((String)localObject2, paramClass.getClassLoader());
        }
        i--;
        localObject1 = localObject3;
        continue;
        label95:
        throw new IllegalArgumentException("parameter type must either be specified as Class or String", null);
      }
      else
      {
        throw new NullPointerException("parameter type must not be null");
      }
    }
    paramVarArgs = (Object[])localObject1;
    if (localObject1 == null) {
      paramVarArgs = new Class[0];
    }
    return findMethodExact(paramClass, paramString, paramVarArgs);
  }
  
  public static Class<?>[] getClassesAsArray(Class<?>... paramVarArgs)
  {
    return paramVarArgs;
  }
  
  public static <T> T getObjectField(Object paramObject, String paramString, Class<T> paramClass)
    throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
  {
    return (T)checkFieldValue(findField(paramObject.getClass(), paramString).get(paramObject), paramClass);
  }
  
  public static Class<?>[] getParameterTypes(Object... paramVarArgs)
  {
    Class[] arrayOfClass = new Class[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++)
    {
      Class localClass;
      if (paramVarArgs[i] != null) {
        localClass = paramVarArgs[i].getClass();
      } else {
        localClass = null;
      }
      arrayOfClass[i] = localClass;
    }
    return arrayOfClass;
  }
  
  private static String getParametersString(Class<?>... paramVarArgs)
  {
    StringBuilder localStringBuilder = new StringBuilder("(");
    int i = 1;
    int j = paramVarArgs.length;
    for (int k = 0; k < j; k++)
    {
      Class<?> localClass = paramVarArgs[k];
      if (i != 0) {
        i = 0;
      } else {
        localStringBuilder.append(",");
      }
      if (localClass != null) {
        localStringBuilder.append(localClass.getCanonicalName());
      } else {
        localStringBuilder.append("null");
      }
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public static <T> T getStaticObjectField(Class<?> paramClass, String paramString, Class<T> paramClass1)
    throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
  {
    paramClass = findField(paramClass, paramString);
    try
    {
      paramClass = checkFieldValue(paramClass.get(null), paramClass1);
      return paramClass;
    }
    catch (NullPointerException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      throw new IllegalArgumentException(paramClass);
    }
  }
  
  public static <T> T getSurroundingThis(Object paramObject, Class<T> paramClass)
    throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
  {
    return (T)getObjectField(paramObject, "this$0", paramClass);
  }
  
  public static Object newInstance(Class<?> paramClass, Object... paramVarArgs)
    throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    return findConstructorBestMatch(paramClass, paramVarArgs).newInstance(paramVarArgs);
  }
  
  public static void setObjectField(Object paramObject1, String paramString, Object paramObject2)
    throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
  {
    findField(paramObject1.getClass(), paramString).set(paramObject1, paramObject2);
  }
  
  public static void setStaticObjectField(Class<?> paramClass, String paramString, Object paramObject)
    throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
  {
    paramClass = findField(paramClass, paramString);
    try
    {
      paramClass.set(null, paramObject);
      return;
    }
    catch (NullPointerException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      throw new IllegalArgumentException(paramClass);
    }
  }
  
  public static <T> ObjectReference<T> tryCallMethod(Object paramObject, String paramString, Class<T> paramClass, Object... paramVarArgs)
  {
    try
    {
      paramObject = new ObjectReference(callMethod(paramObject, paramString, paramClass, paramVarArgs));
      return (ObjectReference<T>)paramObject;
    }
    catch (InvocationTargetException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
      return null;
    }
    catch (IllegalArgumentException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
      return null;
    }
    catch (IllegalAccessException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
      return null;
    }
    catch (NoSuchMethodException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
    }
    return null;
  }
  
  public static <T> ObjectReference<T> tryCallStaticMethod(Class<?> paramClass, String paramString, Class<T> paramClass1, Object... paramVarArgs)
  {
    try
    {
      paramClass = new ObjectReference(callStaticMethod(paramClass, paramString, paramClass1, paramVarArgs));
      return paramClass;
    }
    catch (InvocationTargetException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (IllegalArgumentException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (IllegalAccessException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static Class<?> tryFindClass(String paramString, ClassLoader paramClassLoader)
  {
    try
    {
      paramString = findClass(paramString, paramClassLoader);
      return paramString;
    }
    catch (ClassNotFoundException paramString)
    {
      Log.w("ReflectionUtils", "", paramString);
    }
    return null;
  }
  
  public static Constructor<?> tryFindConstructorBestMatch(Class<?> paramClass, Class<?>... paramVarArgs)
  {
    try
    {
      paramClass = findConstructorBestMatch(paramClass, paramVarArgs);
      return paramClass;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static Constructor<?> tryFindConstructorBestMatch(Class<?> paramClass, Object... paramVarArgs)
  {
    try
    {
      paramClass = findConstructorBestMatch(paramClass, paramVarArgs);
      return paramClass;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static Constructor<?> tryFindConstructorExact(Class<?> paramClass, Class<?>... paramVarArgs)
  {
    try
    {
      paramClass = findConstructorExact(paramClass, paramVarArgs);
      return paramClass;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static Field tryFindField(Class<?> paramClass, String paramString)
  {
    try
    {
      paramClass = findField(paramClass, paramString);
      return paramClass;
    }
    catch (NoSuchFieldException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static Method tryFindMethodBestMatch(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
  {
    try
    {
      paramClass = findMethodBestMatch(paramClass, paramString, paramVarArgs);
      return paramClass;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static Method tryFindMethodBestMatch(Class<?> paramClass, String paramString, Object... paramVarArgs)
  {
    try
    {
      paramClass = findMethodBestMatch(paramClass, paramString, paramVarArgs);
      return paramClass;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static Method tryFindMethodExact(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
  {
    try
    {
      paramClass = findMethodExact(paramClass, paramString, paramVarArgs);
      return paramClass;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static Method tryFindMethodExact(Class<?> paramClass, String paramString, Object... paramVarArgs)
  {
    try
    {
      paramClass = findMethodExact(paramClass, paramString, paramVarArgs);
      return paramClass;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (ClassNotFoundException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static <T> ObjectReference<T> tryGetObjectField(Object paramObject, String paramString, Class<T> paramClass)
  {
    try
    {
      paramObject = new ObjectReference(getObjectField(paramObject, paramString, paramClass));
      return (ObjectReference<T>)paramObject;
    }
    catch (IllegalArgumentException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
      return null;
    }
    catch (IllegalAccessException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
      return null;
    }
    catch (NoSuchFieldException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
    }
    return null;
  }
  
  public static <T> ObjectReference<T> tryGetStaticObjectField(Class<?> paramClass, String paramString, Class<T> paramClass1)
  {
    try
    {
      paramClass = new ObjectReference(getStaticObjectField(paramClass, paramString, paramClass1));
      return paramClass;
    }
    catch (IllegalArgumentException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (IllegalAccessException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (NoSuchFieldException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static <T> ObjectReference<T> tryGetSurroundingThis(Object paramObject, Class<T> paramClass)
  {
    try
    {
      paramObject = new ObjectReference(getSurroundingThis(paramObject, paramClass));
      return (ObjectReference<T>)paramObject;
    }
    catch (IllegalArgumentException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
      return null;
    }
    catch (IllegalAccessException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
      return null;
    }
    catch (NoSuchFieldException paramObject)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject);
    }
    return null;
  }
  
  public static Object tryNewInstance(Class<?> paramClass, Object... paramVarArgs)
  {
    try
    {
      paramClass = newInstance(paramClass, paramVarArgs);
      return paramClass;
    }
    catch (InvocationTargetException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (IllegalArgumentException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (IllegalAccessException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (InstantiationException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
      return null;
    }
    catch (NoSuchMethodException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    return null;
  }
  
  public static void trySetObjectField(Object paramObject1, String paramString, Object paramObject2)
  {
    try
    {
      setObjectField(paramObject1, paramString, paramObject2);
    }
    catch (IllegalArgumentException paramObject1)
    {
      Log.w("ReflectionUtils", "", (Throwable)paramObject1);
    }
    catch (IllegalAccessException paramObject1)
    {
      for (;;)
      {
        Log.w("ReflectionUtils", "", (Throwable)paramObject1);
      }
    }
    catch (NoSuchFieldException paramObject1)
    {
      for (;;)
      {
        Log.w("ReflectionUtils", "", (Throwable)paramObject1);
      }
    }
  }
  
  public static void trySetStaticObjectField(Class<?> paramClass, String paramString, Object paramObject)
  {
    try
    {
      setStaticObjectField(paramClass, paramString, paramObject);
    }
    catch (IllegalArgumentException paramClass)
    {
      Log.w("ReflectionUtils", "", paramClass);
    }
    catch (IllegalAccessException paramClass)
    {
      for (;;)
      {
        Log.w("ReflectionUtils", "", paramClass);
      }
    }
    catch (NoSuchFieldException paramClass)
    {
      for (;;)
      {
        Log.w("ReflectionUtils", "", paramClass);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/ReflectionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */