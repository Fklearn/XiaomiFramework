package org.apache.miui.commons.lang3.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import org.apache.miui.commons.lang3.ClassUtils;

public class FieldUtils
{
  public static Field getDeclaredField(Class<?> paramClass, String paramString)
  {
    return getDeclaredField(paramClass, paramString, false);
  }
  
  public static Field getDeclaredField(Class<?> paramClass, String paramString, boolean paramBoolean)
  {
    if (paramClass != null)
    {
      if (paramString != null) {
        try
        {
          paramClass = paramClass.getDeclaredField(paramString);
          if (!MemberUtils.isAccessible(paramClass)) {
            if (paramBoolean) {
              paramClass.setAccessible(true);
            } else {
              return null;
            }
          }
          return paramClass;
        }
        catch (NoSuchFieldException paramClass)
        {
          return null;
        }
      }
      throw new IllegalArgumentException("The field name must not be null");
    }
    throw new IllegalArgumentException("The class must not be null");
  }
  
  public static Field getField(Class<?> paramClass, String paramString)
  {
    paramClass = getField(paramClass, paramString, false);
    MemberUtils.setAccessibleWorkaround(paramClass);
    return paramClass;
  }
  
  public static Field getField(Class<?> paramClass, String paramString, boolean paramBoolean)
  {
    if (paramClass != null)
    {
      if (paramString != null)
      {
        Object localObject1 = paramClass;
        while (localObject1 != null) {
          try
          {
            Field localField = ((Class)localObject1).getDeclaredField(paramString);
            if (!Modifier.isPublic(localField.getModifiers())) {
              if (paramBoolean) {
                localField.setAccessible(true);
              } else {
                break label53;
              }
            }
            return localField;
          }
          catch (NoSuchFieldException localNoSuchFieldException1)
          {
            label53:
            localObject1 = ((Class)localObject1).getSuperclass();
          }
        }
        localObject1 = null;
        Iterator localIterator = ClassUtils.getAllInterfaces(paramClass).iterator();
        while (localIterator.hasNext())
        {
          Object localObject2 = (Class)localIterator.next();
          try
          {
            localObject2 = ((Class)localObject2).getField(paramString);
            if (localObject1 == null)
            {
              localObject1 = localObject2;
            }
            else
            {
              localObject2 = new java/lang/IllegalArgumentException;
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localStringBuilder.append("Reference to field ");
              localStringBuilder.append(paramString);
              localStringBuilder.append(" is ambiguous relative to ");
              localStringBuilder.append(paramClass);
              localStringBuilder.append("; a matching field exists on two or more implemented interfaces.");
              ((IllegalArgumentException)localObject2).<init>(localStringBuilder.toString());
              throw ((Throwable)localObject2);
            }
          }
          catch (NoSuchFieldException localNoSuchFieldException2) {}
        }
        return (Field)localObject1;
      }
      throw new IllegalArgumentException("The field name must not be null");
    }
    throw new IllegalArgumentException("The class must not be null");
  }
  
  public static Object readDeclaredField(Object paramObject, String paramString)
    throws IllegalAccessException
  {
    return readDeclaredField(paramObject, paramString, false);
  }
  
  public static Object readDeclaredField(Object paramObject, String paramString, boolean paramBoolean)
    throws IllegalAccessException
  {
    if (paramObject != null)
    {
      Class localClass = paramObject.getClass();
      Field localField = getDeclaredField(localClass, paramString, paramBoolean);
      if (localField != null) {
        return readField(localField, paramObject);
      }
      paramObject = new StringBuilder();
      ((StringBuilder)paramObject).append("Cannot locate declared field ");
      ((StringBuilder)paramObject).append(localClass.getName());
      ((StringBuilder)paramObject).append(".");
      ((StringBuilder)paramObject).append(paramString);
      throw new IllegalArgumentException(((StringBuilder)paramObject).toString());
    }
    throw new IllegalArgumentException("target object must not be null");
  }
  
  public static Object readDeclaredStaticField(Class<?> paramClass, String paramString)
    throws IllegalAccessException
  {
    return readDeclaredStaticField(paramClass, paramString, false);
  }
  
  public static Object readDeclaredStaticField(Class<?> paramClass, String paramString, boolean paramBoolean)
    throws IllegalAccessException
  {
    Object localObject = getDeclaredField(paramClass, paramString, paramBoolean);
    if (localObject != null) {
      return readStaticField((Field)localObject, false);
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Cannot locate declared field ");
    ((StringBuilder)localObject).append(paramClass.getName());
    ((StringBuilder)localObject).append(".");
    ((StringBuilder)localObject).append(paramString);
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public static Object readField(Object paramObject, String paramString)
    throws IllegalAccessException
  {
    return readField(paramObject, paramString, false);
  }
  
  public static Object readField(Object paramObject, String paramString, boolean paramBoolean)
    throws IllegalAccessException
  {
    if (paramObject != null)
    {
      Class localClass = paramObject.getClass();
      Field localField = getField(localClass, paramString, paramBoolean);
      if (localField != null) {
        return readField(localField, paramObject);
      }
      paramObject = new StringBuilder();
      ((StringBuilder)paramObject).append("Cannot locate field ");
      ((StringBuilder)paramObject).append(paramString);
      ((StringBuilder)paramObject).append(" on ");
      ((StringBuilder)paramObject).append(localClass);
      throw new IllegalArgumentException(((StringBuilder)paramObject).toString());
    }
    throw new IllegalArgumentException("target object must not be null");
  }
  
  public static Object readField(Field paramField, Object paramObject)
    throws IllegalAccessException
  {
    return readField(paramField, paramObject, false);
  }
  
  public static Object readField(Field paramField, Object paramObject, boolean paramBoolean)
    throws IllegalAccessException
  {
    if (paramField != null)
    {
      if ((paramBoolean) && (!paramField.isAccessible())) {
        paramField.setAccessible(true);
      } else {
        MemberUtils.setAccessibleWorkaround(paramField);
      }
      return paramField.get(paramObject);
    }
    throw new IllegalArgumentException("The field must not be null");
  }
  
  public static Object readStaticField(Class<?> paramClass, String paramString)
    throws IllegalAccessException
  {
    return readStaticField(paramClass, paramString, false);
  }
  
  public static Object readStaticField(Class<?> paramClass, String paramString, boolean paramBoolean)
    throws IllegalAccessException
  {
    Object localObject = getField(paramClass, paramString, paramBoolean);
    if (localObject != null) {
      return readStaticField((Field)localObject, false);
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Cannot locate field ");
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(" on ");
    ((StringBuilder)localObject).append(paramClass);
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public static Object readStaticField(Field paramField)
    throws IllegalAccessException
  {
    return readStaticField(paramField, false);
  }
  
  public static Object readStaticField(Field paramField, boolean paramBoolean)
    throws IllegalAccessException
  {
    if (paramField != null)
    {
      if (Modifier.isStatic(paramField.getModifiers())) {
        return readField(paramField, null, paramBoolean);
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("The field '");
      localStringBuilder.append(paramField.getName());
      localStringBuilder.append("' is not static");
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    throw new IllegalArgumentException("The field must not be null");
  }
  
  public static void writeDeclaredField(Object paramObject1, String paramString, Object paramObject2)
    throws IllegalAccessException
  {
    writeDeclaredField(paramObject1, paramString, paramObject2, false);
  }
  
  public static void writeDeclaredField(Object paramObject1, String paramString, Object paramObject2, boolean paramBoolean)
    throws IllegalAccessException
  {
    if (paramObject1 != null)
    {
      Class localClass = paramObject1.getClass();
      Field localField = getDeclaredField(localClass, paramString, paramBoolean);
      if (localField != null)
      {
        writeField(localField, paramObject1, paramObject2);
        return;
      }
      paramObject1 = new StringBuilder();
      ((StringBuilder)paramObject1).append("Cannot locate declared field ");
      ((StringBuilder)paramObject1).append(localClass.getName());
      ((StringBuilder)paramObject1).append(".");
      ((StringBuilder)paramObject1).append(paramString);
      throw new IllegalArgumentException(((StringBuilder)paramObject1).toString());
    }
    throw new IllegalArgumentException("target object must not be null");
  }
  
  public static void writeDeclaredStaticField(Class<?> paramClass, String paramString, Object paramObject)
    throws IllegalAccessException
  {
    writeDeclaredStaticField(paramClass, paramString, paramObject, false);
  }
  
  public static void writeDeclaredStaticField(Class<?> paramClass, String paramString, Object paramObject, boolean paramBoolean)
    throws IllegalAccessException
  {
    Field localField = getDeclaredField(paramClass, paramString, paramBoolean);
    if (localField != null)
    {
      writeField(localField, null, paramObject);
      return;
    }
    paramObject = new StringBuilder();
    ((StringBuilder)paramObject).append("Cannot locate declared field ");
    ((StringBuilder)paramObject).append(paramClass.getName());
    ((StringBuilder)paramObject).append(".");
    ((StringBuilder)paramObject).append(paramString);
    throw new IllegalArgumentException(((StringBuilder)paramObject).toString());
  }
  
  public static void writeField(Object paramObject1, String paramString, Object paramObject2)
    throws IllegalAccessException
  {
    writeField(paramObject1, paramString, paramObject2, false);
  }
  
  public static void writeField(Object paramObject1, String paramString, Object paramObject2, boolean paramBoolean)
    throws IllegalAccessException
  {
    if (paramObject1 != null)
    {
      Class localClass = paramObject1.getClass();
      Field localField = getField(localClass, paramString, paramBoolean);
      if (localField != null)
      {
        writeField(localField, paramObject1, paramObject2);
        return;
      }
      paramObject1 = new StringBuilder();
      ((StringBuilder)paramObject1).append("Cannot locate declared field ");
      ((StringBuilder)paramObject1).append(localClass.getName());
      ((StringBuilder)paramObject1).append(".");
      ((StringBuilder)paramObject1).append(paramString);
      throw new IllegalArgumentException(((StringBuilder)paramObject1).toString());
    }
    throw new IllegalArgumentException("target object must not be null");
  }
  
  public static void writeField(Field paramField, Object paramObject1, Object paramObject2)
    throws IllegalAccessException
  {
    writeField(paramField, paramObject1, paramObject2, false);
  }
  
  public static void writeField(Field paramField, Object paramObject1, Object paramObject2, boolean paramBoolean)
    throws IllegalAccessException
  {
    if (paramField != null)
    {
      if ((paramBoolean) && (!paramField.isAccessible())) {
        paramField.setAccessible(true);
      } else {
        MemberUtils.setAccessibleWorkaround(paramField);
      }
      paramField.set(paramObject1, paramObject2);
      return;
    }
    throw new IllegalArgumentException("The field must not be null");
  }
  
  public static void writeStaticField(Class<?> paramClass, String paramString, Object paramObject)
    throws IllegalAccessException
  {
    writeStaticField(paramClass, paramString, paramObject, false);
  }
  
  public static void writeStaticField(Class<?> paramClass, String paramString, Object paramObject, boolean paramBoolean)
    throws IllegalAccessException
  {
    Field localField = getField(paramClass, paramString, paramBoolean);
    if (localField != null)
    {
      writeStaticField(localField, paramObject);
      return;
    }
    paramObject = new StringBuilder();
    ((StringBuilder)paramObject).append("Cannot locate field ");
    ((StringBuilder)paramObject).append(paramString);
    ((StringBuilder)paramObject).append(" on ");
    ((StringBuilder)paramObject).append(paramClass);
    throw new IllegalArgumentException(((StringBuilder)paramObject).toString());
  }
  
  public static void writeStaticField(Field paramField, Object paramObject)
    throws IllegalAccessException
  {
    writeStaticField(paramField, paramObject, false);
  }
  
  public static void writeStaticField(Field paramField, Object paramObject, boolean paramBoolean)
    throws IllegalAccessException
  {
    if (paramField != null)
    {
      if (Modifier.isStatic(paramField.getModifiers()))
      {
        writeField(paramField, null, paramObject, paramBoolean);
        return;
      }
      paramObject = new StringBuilder();
      ((StringBuilder)paramObject).append("The field '");
      ((StringBuilder)paramObject).append(paramField.getName());
      ((StringBuilder)paramObject).append("' is not static");
      throw new IllegalArgumentException(((StringBuilder)paramObject).toString());
    }
    throw new IllegalArgumentException("The field must not be null");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/reflect/FieldUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */