package org.apache.miui.commons.lang3;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.miui.commons.lang3.builder.ToStringBuilder;
import org.apache.miui.commons.lang3.builder.ToStringStyle;

public class AnnotationUtils
{
  private static final ToStringStyle TO_STRING_STYLE = new ToStringStyle()
  {
    private static final long serialVersionUID = 1L;
    
    protected void appendDetail(StringBuffer paramAnonymousStringBuffer, String paramAnonymousString, Object paramAnonymousObject)
    {
      Object localObject = paramAnonymousObject;
      if ((paramAnonymousObject instanceof Annotation)) {
        localObject = AnnotationUtils.toString((Annotation)paramAnonymousObject);
      }
      super.appendDetail(paramAnonymousStringBuffer, paramAnonymousString, localObject);
    }
    
    protected String getShortClassName(Class<?> paramAnonymousClass)
    {
      Object localObject = null;
      Iterator localIterator = ClassUtils.getAllInterfaces(paramAnonymousClass).iterator();
      for (;;)
      {
        paramAnonymousClass = (Class<?>)localObject;
        if (!localIterator.hasNext()) {
          break;
        }
        paramAnonymousClass = (Class)localIterator.next();
        if (Annotation.class.isAssignableFrom(paramAnonymousClass)) {
          break;
        }
      }
      if (paramAnonymousClass == null) {
        paramAnonymousClass = "";
      } else {
        paramAnonymousClass = paramAnonymousClass.getName();
      }
      return new StringBuilder(paramAnonymousClass).insert(0, '@').toString();
    }
  };
  
  private static boolean annotationArrayMemberEquals(Annotation[] paramArrayOfAnnotation1, Annotation[] paramArrayOfAnnotation2)
  {
    if (paramArrayOfAnnotation1.length != paramArrayOfAnnotation2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfAnnotation1.length; i++) {
      if (!equals(paramArrayOfAnnotation1[i], paramArrayOfAnnotation2[i])) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean arrayMemberEquals(Class<?> paramClass, Object paramObject1, Object paramObject2)
  {
    if (paramClass.isAnnotation()) {
      return annotationArrayMemberEquals((Annotation[])paramObject1, (Annotation[])paramObject2);
    }
    if (paramClass.equals(Byte.TYPE)) {
      return Arrays.equals((byte[])paramObject1, (byte[])paramObject2);
    }
    if (paramClass.equals(Short.TYPE)) {
      return Arrays.equals((short[])paramObject1, (short[])paramObject2);
    }
    if (paramClass.equals(Integer.TYPE)) {
      return Arrays.equals((int[])paramObject1, (int[])paramObject2);
    }
    if (paramClass.equals(Character.TYPE)) {
      return Arrays.equals((char[])paramObject1, (char[])paramObject2);
    }
    if (paramClass.equals(Long.TYPE)) {
      return Arrays.equals((long[])paramObject1, (long[])paramObject2);
    }
    if (paramClass.equals(Float.TYPE)) {
      return Arrays.equals((float[])paramObject1, (float[])paramObject2);
    }
    if (paramClass.equals(Double.TYPE)) {
      return Arrays.equals((double[])paramObject1, (double[])paramObject2);
    }
    if (paramClass.equals(Boolean.TYPE)) {
      return Arrays.equals((boolean[])paramObject1, (boolean[])paramObject2);
    }
    return Arrays.equals((Object[])paramObject1, (Object[])paramObject2);
  }
  
  private static int arrayMemberHash(Class<?> paramClass, Object paramObject)
  {
    if (paramClass.equals(Byte.TYPE)) {
      return Arrays.hashCode((byte[])paramObject);
    }
    if (paramClass.equals(Short.TYPE)) {
      return Arrays.hashCode((short[])paramObject);
    }
    if (paramClass.equals(Integer.TYPE)) {
      return Arrays.hashCode((int[])paramObject);
    }
    if (paramClass.equals(Character.TYPE)) {
      return Arrays.hashCode((char[])paramObject);
    }
    if (paramClass.equals(Long.TYPE)) {
      return Arrays.hashCode((long[])paramObject);
    }
    if (paramClass.equals(Float.TYPE)) {
      return Arrays.hashCode((float[])paramObject);
    }
    if (paramClass.equals(Double.TYPE)) {
      return Arrays.hashCode((double[])paramObject);
    }
    if (paramClass.equals(Boolean.TYPE)) {
      return Arrays.hashCode((boolean[])paramObject);
    }
    return Arrays.hashCode((Object[])paramObject);
  }
  
  public static boolean equals(Annotation paramAnnotation1, Annotation paramAnnotation2)
  {
    if (paramAnnotation1 == paramAnnotation2) {
      return true;
    }
    if ((paramAnnotation1 != null) && (paramAnnotation2 != null))
    {
      Object localObject1 = paramAnnotation1.annotationType();
      Object localObject2 = paramAnnotation2.annotationType();
      Validate.notNull(localObject1, "Annotation %s with null annotationType()", new Object[] { paramAnnotation1 });
      Validate.notNull(localObject2, "Annotation %s with null annotationType()", new Object[] { paramAnnotation2 });
      if (!localObject1.equals(localObject2)) {
        return false;
      }
      try
      {
        for (Object localObject3 : ((Class)localObject1).getDeclaredMethods()) {
          if ((((Method)localObject3).getParameterTypes().length == 0) && (isValidAnnotationMemberType(((Method)localObject3).getReturnType())))
          {
            Object localObject4 = ((Method)localObject3).invoke(paramAnnotation1, new Object[0]);
            localObject1 = ((Method)localObject3).invoke(paramAnnotation2, new Object[0]);
            boolean bool = memberEquals(((Method)localObject3).getReturnType(), localObject4, localObject1);
            if (!bool) {
              return false;
            }
          }
        }
        return true;
      }
      catch (InvocationTargetException paramAnnotation1)
      {
        return false;
      }
      catch (IllegalAccessException paramAnnotation1)
      {
        return false;
      }
    }
    return false;
  }
  
  public static int hashCode(Annotation paramAnnotation)
  {
    Method[] arrayOfMethod = paramAnnotation.annotationType().getDeclaredMethods();
    int i = arrayOfMethod.length;
    int j = 0;
    int k = 0;
    while (k < i)
    {
      Method localMethod = arrayOfMethod[k];
      try
      {
        Object localObject = localMethod.invoke(paramAnnotation, new Object[0]);
        if (localObject != null)
        {
          j += hashMember(localMethod.getName(), localObject);
          k++;
        }
        else
        {
          paramAnnotation = new java/lang/IllegalStateException;
          paramAnnotation.<init>(String.format("Annotation method %s returned null", new Object[] { localMethod }));
          throw paramAnnotation;
        }
      }
      catch (Exception paramAnnotation)
      {
        throw new RuntimeException(paramAnnotation);
      }
      catch (RuntimeException paramAnnotation)
      {
        throw paramAnnotation;
      }
    }
    return j;
  }
  
  private static int hashMember(String paramString, Object paramObject)
  {
    int i = paramString.hashCode() * 127;
    if (paramObject.getClass().isArray()) {
      return arrayMemberHash(paramObject.getClass().getComponentType(), paramObject) ^ i;
    }
    if ((paramObject instanceof Annotation)) {
      return hashCode((Annotation)paramObject) ^ i;
    }
    return paramObject.hashCode() ^ i;
  }
  
  public static boolean isValidAnnotationMemberType(Class<?> paramClass)
  {
    boolean bool = false;
    if (paramClass == null) {
      return false;
    }
    Object localObject = paramClass;
    if (paramClass.isArray()) {
      localObject = paramClass.getComponentType();
    }
    if ((((Class)localObject).isPrimitive()) || (((Class)localObject).isEnum()) || (((Class)localObject).isAnnotation()) || (String.class.equals(localObject)) || (Class.class.equals(localObject))) {
      bool = true;
    }
    return bool;
  }
  
  private static boolean memberEquals(Class<?> paramClass, Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == paramObject2) {
      return true;
    }
    if ((paramObject1 != null) && (paramObject2 != null))
    {
      if (paramClass.isArray()) {
        return arrayMemberEquals(paramClass.getComponentType(), paramObject1, paramObject2);
      }
      if (paramClass.isAnnotation()) {
        return equals((Annotation)paramObject1, (Annotation)paramObject2);
      }
      return paramObject1.equals(paramObject2);
    }
    return false;
  }
  
  public static String toString(Annotation paramAnnotation)
  {
    ToStringBuilder localToStringBuilder = new ToStringBuilder(paramAnnotation, TO_STRING_STYLE);
    Method[] arrayOfMethod = paramAnnotation.annotationType().getDeclaredMethods();
    int i = arrayOfMethod.length;
    int j = 0;
    while (j < i)
    {
      Method localMethod = arrayOfMethod[j];
      if (localMethod.getParameterTypes().length <= 0) {}
      try
      {
        localToStringBuilder.append(localMethod.getName(), localMethod.invoke(paramAnnotation, new Object[0]));
        j++;
      }
      catch (Exception paramAnnotation)
      {
        throw new RuntimeException(paramAnnotation);
      }
      catch (RuntimeException paramAnnotation)
      {
        throw paramAnnotation;
      }
    }
    return localToStringBuilder.build();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/AnnotationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */