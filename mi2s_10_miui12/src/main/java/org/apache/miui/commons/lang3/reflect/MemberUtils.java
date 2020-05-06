package org.apache.miui.commons.lang3.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import org.apache.miui.commons.lang3.ClassUtils;

public abstract class MemberUtils
{
  private static final int ACCESS_TEST = 7;
  private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = { Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE };
  
  public static int compareParameterTypes(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, Class<?>[] paramArrayOfClass3)
  {
    float f1 = getTotalTransformationCost(paramArrayOfClass3, paramArrayOfClass1);
    float f2 = getTotalTransformationCost(paramArrayOfClass3, paramArrayOfClass2);
    int i;
    if (f1 < f2) {
      i = -1;
    } else if (f2 < f1) {
      i = 1;
    } else {
      i = 0;
    }
    return i;
  }
  
  private static float getObjectTransformationCost(Class<?> paramClass1, Class<?> paramClass2)
  {
    if (paramClass2.isPrimitive()) {
      return getPrimitivePromotionCost(paramClass1, paramClass2);
    }
    float f1 = 0.0F;
    float f2;
    for (;;)
    {
      f2 = f1;
      if (paramClass1 == null) {
        break;
      }
      f2 = f1;
      if (paramClass2.equals(paramClass1)) {
        break;
      }
      if ((paramClass2.isInterface()) && (ClassUtils.isAssignable(paramClass1, paramClass2)))
      {
        f2 = f1 + 0.25F;
        break;
      }
      f1 += 1.0F;
      paramClass1 = paramClass1.getSuperclass();
    }
    f1 = f2;
    if (paramClass1 == null) {
      f1 = f2 + 1.5F;
    }
    return f1;
  }
  
  private static float getPrimitivePromotionCost(Class<?> paramClass1, Class<?> paramClass2)
  {
    float f1 = 0.0F;
    Class<?> localClass = paramClass1;
    paramClass1 = localClass;
    if (!localClass.isPrimitive())
    {
      f1 = 0.0F + 0.1F;
      paramClass1 = ClassUtils.wrapperToPrimitive(localClass);
    }
    int i = 0;
    localClass = paramClass1;
    float f2 = f1;
    while (localClass != paramClass2)
    {
      Class[] arrayOfClass = ORDERED_PRIMITIVE_TYPES;
      if (i >= arrayOfClass.length) {
        break;
      }
      f1 = f2;
      paramClass1 = localClass;
      if (localClass == arrayOfClass[i])
      {
        f2 += 0.1F;
        f1 = f2;
        paramClass1 = localClass;
        if (i < arrayOfClass.length - 1)
        {
          paramClass1 = arrayOfClass[(i + 1)];
          f1 = f2;
        }
      }
      i++;
      f2 = f1;
      localClass = paramClass1;
    }
    return f2;
  }
  
  private static float getTotalTransformationCost(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2)
  {
    float f = 0.0F;
    for (int i = 0; i < paramArrayOfClass1.length; i++) {
      f += getObjectTransformationCost(paramArrayOfClass1[i], paramArrayOfClass2[i]);
    }
    return f;
  }
  
  static boolean isAccessible(Member paramMember)
  {
    boolean bool;
    if ((paramMember != null) && (Modifier.isPublic(paramMember.getModifiers())) && (!paramMember.isSynthetic())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static boolean isPackageAccess(int paramInt)
  {
    boolean bool;
    if ((paramInt & 0x7) == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static void setAccessibleWorkaround(AccessibleObject paramAccessibleObject)
  {
    if ((paramAccessibleObject != null) && (!paramAccessibleObject.isAccessible()))
    {
      Member localMember = (Member)paramAccessibleObject;
      if ((Modifier.isPublic(localMember.getModifiers())) && (isPackageAccess(localMember.getDeclaringClass().getModifiers()))) {
        try
        {
          paramAccessibleObject.setAccessible(true);
        }
        catch (SecurityException paramAccessibleObject) {}
      }
      return;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/reflect/MemberUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */