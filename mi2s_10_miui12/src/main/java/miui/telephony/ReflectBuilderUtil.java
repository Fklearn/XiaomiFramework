package miui.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Pair;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectBuilderUtil
{
  public static Object callObjectMethod(Class paramClass, Object paramObject, String paramString, Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    paramClass = paramClass.getDeclaredMethod(paramString, paramArrayOfClass);
    paramClass.setAccessible(true);
    return paramClass.invoke(paramObject, paramVarArgs);
  }
  
  public static <T> T callObjectMethod(Object paramObject, Class<T> paramClass, String paramString, Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    paramClass = paramObject.getClass().getDeclaredMethod(paramString, paramArrayOfClass);
    paramClass.setAccessible(true);
    return (T)paramClass.invoke(paramObject, paramVarArgs);
  }
  
  public static Object callObjectMethod(Object paramObject, String paramString, Class<?> paramClass, Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    paramString = paramClass.getDeclaredMethod(paramString, paramArrayOfClass);
    paramString.setAccessible(true);
    return paramString.invoke(paramObject, paramVarArgs);
  }
  
  public static Object callObjectMethod(Object paramObject, String paramString, Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    paramString = paramObject.getClass().getDeclaredMethod(paramString, paramArrayOfClass);
    paramString.setAccessible(true);
    return paramString.invoke(paramObject, paramVarArgs);
  }
  
  public static Object callStaticObjectMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    paramClass = paramClass.getDeclaredMethod(paramString, paramArrayOfClass);
    paramClass.setAccessible(true);
    return paramClass.invoke(null, paramVarArgs);
  }
  
  public static Object getObjectField(Object paramObject, String paramString)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
  {
    paramString = paramObject.getClass().getDeclaredField(paramString);
    paramString.setAccessible(true);
    return paramString.get(paramObject);
  }
  
  public static <T> T getObjectField(Object paramObject, String paramString, Class<T> paramClass)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
  {
    paramString = paramObject.getClass().getDeclaredField(paramString);
    paramString.setAccessible(true);
    return (T)paramString.get(paramObject);
  }
  
  public static Object getStaticObjectField(Class<?> paramClass, String paramString)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
  {
    paramClass = paramClass.getDeclaredField(paramString);
    paramClass.setAccessible(true);
    return paramClass.get(null);
  }
  
  public static <T> T getStaticObjectField(Class<?> paramClass, String paramString, Class<T> paramClass1)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
  {
    paramClass = paramClass.getDeclaredField(paramString);
    paramClass.setAccessible(true);
    return (T)paramClass.get(null);
  }
  
  public static Object newObject(Class<?> paramClass, Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    if (paramClass != null) {
      return paramClass.getConstructor(paramArrayOfClass).newInstance(paramVarArgs);
    }
    return null;
  }
  
  public static void setObjectField(Object paramObject1, String paramString, Object paramObject2)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
  {
    paramString = paramObject1.getClass().getDeclaredField(paramString);
    paramString.setAccessible(true);
    paramString.set(paramObject1, paramObject2);
  }
  
  public static void setStaticObjectField(Class<?> paramClass, String paramString, Object paramObject)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
  {
    paramClass = paramClass.getDeclaredField(paramString);
    paramClass.setAccessible(true);
    paramClass.set(null, paramObject);
  }
  
  public static class ReflAgent
  {
    private Class mClass;
    private Object mObject;
    private Object mResult;
    
    public static ReflAgent getClass(Context paramContext, String paramString1, String paramString2)
    {
      ReflAgent localReflAgent = new ReflAgent();
      try
      {
        localReflAgent.mClass = paramContext.createPackageContext(paramString1, 3).getClassLoader().loadClass(paramString2);
      }
      catch (ClassNotFoundException paramContext)
      {
        paramContext.printStackTrace();
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        paramContext.printStackTrace();
      }
      return localReflAgent;
    }
    
    public static ReflAgent getClass(String paramString)
    {
      ReflAgent localReflAgent = new ReflAgent();
      try
      {
        localReflAgent.mClass = Class.forName(paramString);
      }
      catch (ClassNotFoundException paramString)
      {
        paramString.printStackTrace();
      }
      return localReflAgent;
    }
    
    public static ReflAgent getFiveGManagerClass(Context paramContext)
    {
      return getClass(paramContext, "com.android.phone", "com.android.phone.FiveGManager");
    }
    
    public static ReflAgent getObject(Object paramObject)
    {
      ReflAgent localReflAgent = new ReflAgent();
      if (paramObject != null)
      {
        localReflAgent.mObject = paramObject;
        localReflAgent.mClass = paramObject.getClass();
      }
      return localReflAgent;
    }
    
    public boolean booleanResult()
    {
      Object localObject = this.mResult;
      if (localObject == null) {
        return false;
      }
      return ((Boolean)localObject).booleanValue();
    }
    
    public ReflAgent call(String paramString, Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    {
      Object localObject = this.mObject;
      if (localObject != null) {
        try
        {
          this.mResult = ReflectBuilderUtil.callObjectMethod(localObject, paramString, paramArrayOfClass, paramVarArgs);
        }
        catch (InvocationTargetException paramString)
        {
          paramString.printStackTrace();
        }
        catch (IllegalAccessException paramString)
        {
          paramString.printStackTrace();
        }
        catch (NoSuchMethodException paramString)
        {
          paramString.printStackTrace();
        }
      }
      return this;
    }
    
    public ReflAgent callStatic(String paramString, Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    {
      Class localClass = this.mClass;
      if (localClass != null) {
        try
        {
          this.mResult = ReflectBuilderUtil.callStaticObjectMethod(localClass, paramString, paramArrayOfClass, paramVarArgs);
        }
        catch (InvocationTargetException paramString)
        {
          paramString.printStackTrace();
        }
        catch (IllegalAccessException paramString)
        {
          paramString.printStackTrace();
        }
        catch (NoSuchMethodException paramString)
        {
          paramString.printStackTrace();
        }
      }
      return this;
    }
    
    public Object getObject()
    {
      return this.mObject;
    }
    
    public ReflAgent getObjectFiled(String paramString)
    {
      Object localObject = this.mObject;
      if (localObject != null) {
        try
        {
          this.mResult = ReflectBuilderUtil.getObjectField(localObject, paramString);
        }
        catch (IllegalAccessException paramString)
        {
          paramString.printStackTrace();
        }
        catch (NoSuchFieldException paramString)
        {
          paramString.printStackTrace();
        }
      }
      return this;
    }
    
    public ReflAgent getStaticFiled(String paramString)
    {
      Class localClass = this.mClass;
      if (localClass != null) {
        try
        {
          this.mResult = ReflectBuilderUtil.getStaticObjectField(localClass, paramString);
        }
        catch (IllegalAccessException paramString)
        {
          paramString.printStackTrace();
        }
        catch (NoSuchFieldException paramString)
        {
          paramString.printStackTrace();
        }
      }
      return this;
    }
    
    public int intResult()
    {
      Object localObject = this.mResult;
      if (localObject == null) {
        return 0;
      }
      return ((Integer)localObject).intValue();
    }
    
    public Intent intentResult()
    {
      Object localObject = this.mResult;
      if (localObject == null) {
        return null;
      }
      return (Intent)localObject;
    }
    
    public long longResult()
    {
      Object localObject = this.mResult;
      if (localObject == null) {
        return 0L;
      }
      return ((Long)localObject).longValue();
    }
    
    public ReflAgent newObject(Class<?>[] paramArrayOfClass, Object... paramVarArgs)
    {
      Class localClass = this.mClass;
      if (localClass != null) {
        try
        {
          this.mObject = localClass.getConstructor(paramArrayOfClass).newInstance(paramVarArgs);
        }
        catch (InstantiationException paramArrayOfClass)
        {
          paramArrayOfClass.printStackTrace();
        }
        catch (InvocationTargetException paramArrayOfClass)
        {
          paramArrayOfClass.printStackTrace();
        }
        catch (IllegalAccessException paramArrayOfClass)
        {
          paramArrayOfClass.printStackTrace();
        }
        catch (NoSuchMethodException paramArrayOfClass)
        {
          paramArrayOfClass.printStackTrace();
        }
      }
      return this;
    }
    
    public Pair pairResult()
    {
      Object localObject = this.mResult;
      if (localObject == null) {
        return null;
      }
      return (Pair)localObject;
    }
    
    public Object resultObject()
    {
      Object localObject = this.mResult;
      if (localObject == null) {
        return null;
      }
      return localObject;
    }
    
    public ReflAgent setResultToSelf()
    {
      this.mObject = this.mResult;
      this.mResult = null;
      return this;
    }
    
    public String stringResult()
    {
      Object localObject = this.mResult;
      if (localObject == null) {
        return null;
      }
      return localObject.toString();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/ReflectBuilderUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */