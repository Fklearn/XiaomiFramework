package org.apache.miui.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.miui.commons.lang3.ArrayUtils;

public class ReflectionToStringBuilder
  extends ToStringBuilder
{
  private boolean appendStatics = false;
  private boolean appendTransients = false;
  protected String[] excludeFieldNames;
  private Class<?> upToClass = null;
  
  public ReflectionToStringBuilder(Object paramObject)
  {
    super(paramObject);
  }
  
  public ReflectionToStringBuilder(Object paramObject, ToStringStyle paramToStringStyle)
  {
    super(paramObject, paramToStringStyle);
  }
  
  public ReflectionToStringBuilder(Object paramObject, ToStringStyle paramToStringStyle, StringBuffer paramStringBuffer)
  {
    super(paramObject, paramToStringStyle, paramStringBuffer);
  }
  
  public <T> ReflectionToStringBuilder(T paramT, ToStringStyle paramToStringStyle, StringBuffer paramStringBuffer, Class<? super T> paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramT, paramToStringStyle, paramStringBuffer);
    setUpToClass(paramClass);
    setAppendTransients(paramBoolean1);
    setAppendStatics(paramBoolean2);
  }
  
  static String[] toNoNullStringArray(Collection<String> paramCollection)
  {
    if (paramCollection == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return toNoNullStringArray(paramCollection.toArray());
  }
  
  static String[] toNoNullStringArray(Object[] paramArrayOfObject)
  {
    ArrayList localArrayList = new ArrayList(paramArrayOfObject.length);
    int i = paramArrayOfObject.length;
    for (int j = 0; j < i; j++)
    {
      Object localObject = paramArrayOfObject[j];
      if (localObject != null) {
        localArrayList.add(localObject.toString());
      }
    }
    return (String[])localArrayList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
  }
  
  public static String toString(Object paramObject)
  {
    return toString(paramObject, null, false, false, null);
  }
  
  public static String toString(Object paramObject, ToStringStyle paramToStringStyle)
  {
    return toString(paramObject, paramToStringStyle, false, false, null);
  }
  
  public static String toString(Object paramObject, ToStringStyle paramToStringStyle, boolean paramBoolean)
  {
    return toString(paramObject, paramToStringStyle, paramBoolean, false, null);
  }
  
  public static String toString(Object paramObject, ToStringStyle paramToStringStyle, boolean paramBoolean1, boolean paramBoolean2)
  {
    return toString(paramObject, paramToStringStyle, paramBoolean1, paramBoolean2, null);
  }
  
  public static <T> String toString(T paramT, ToStringStyle paramToStringStyle, boolean paramBoolean1, boolean paramBoolean2, Class<? super T> paramClass)
  {
    return new ReflectionToStringBuilder(paramT, paramToStringStyle, null, paramClass, paramBoolean1, paramBoolean2).toString();
  }
  
  public static String toStringExclude(Object paramObject, Collection<String> paramCollection)
  {
    return toStringExclude(paramObject, toNoNullStringArray(paramCollection));
  }
  
  public static String toStringExclude(Object paramObject, String... paramVarArgs)
  {
    return new ReflectionToStringBuilder(paramObject).setExcludeFieldNames(paramVarArgs).toString();
  }
  
  protected boolean accept(Field paramField)
  {
    if (paramField.getName().indexOf('$') != -1) {
      return false;
    }
    if ((Modifier.isTransient(paramField.getModifiers())) && (!isAppendTransients())) {
      return false;
    }
    if ((Modifier.isStatic(paramField.getModifiers())) && (!isAppendStatics())) {
      return false;
    }
    String[] arrayOfString = this.excludeFieldNames;
    return (arrayOfString == null) || (Arrays.binarySearch(arrayOfString, paramField.getName()) < 0);
  }
  
  protected void appendFieldsIn(Class<?> paramClass)
  {
    if (paramClass.isArray())
    {
      reflectionAppendArray(getObject());
      return;
    }
    Field[] arrayOfField = paramClass.getDeclaredFields();
    AccessibleObject.setAccessible(arrayOfField, true);
    int i = arrayOfField.length;
    for (int j = 0; j < i; j++)
    {
      paramClass = arrayOfField[j];
      String str = paramClass.getName();
      if (accept(paramClass)) {
        try
        {
          append(str, getValue(paramClass));
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          paramClass = new StringBuilder();
          paramClass.append("Unexpected IllegalAccessException: ");
          paramClass.append(localIllegalAccessException.getMessage());
          throw new InternalError(paramClass.toString());
        }
      }
    }
  }
  
  public String[] getExcludeFieldNames()
  {
    return (String[])this.excludeFieldNames.clone();
  }
  
  public Class<?> getUpToClass()
  {
    return this.upToClass;
  }
  
  protected Object getValue(Field paramField)
    throws IllegalArgumentException, IllegalAccessException
  {
    return paramField.get(getObject());
  }
  
  public boolean isAppendStatics()
  {
    return this.appendStatics;
  }
  
  public boolean isAppendTransients()
  {
    return this.appendTransients;
  }
  
  public ReflectionToStringBuilder reflectionAppendArray(Object paramObject)
  {
    getStyle().reflectionAppendArrayDetail(getStringBuffer(), null, paramObject);
    return this;
  }
  
  public void setAppendStatics(boolean paramBoolean)
  {
    this.appendStatics = paramBoolean;
  }
  
  public void setAppendTransients(boolean paramBoolean)
  {
    this.appendTransients = paramBoolean;
  }
  
  public ReflectionToStringBuilder setExcludeFieldNames(String... paramVarArgs)
  {
    if (paramVarArgs == null)
    {
      this.excludeFieldNames = null;
    }
    else
    {
      this.excludeFieldNames = toNoNullStringArray(paramVarArgs);
      Arrays.sort(this.excludeFieldNames);
    }
    return this;
  }
  
  public void setUpToClass(Class<?> paramClass)
  {
    if (paramClass != null)
    {
      Object localObject = getObject();
      if ((localObject != null) && (!paramClass.isInstance(localObject))) {
        throw new IllegalArgumentException("Specified class is not a superclass of the object");
      }
    }
    this.upToClass = paramClass;
  }
  
  public String toString()
  {
    if (getObject() == null) {
      return getStyle().getNullText();
    }
    Class localClass = getObject().getClass();
    appendFieldsIn(localClass);
    while ((localClass.getSuperclass() != null) && (localClass != getUpToClass()))
    {
      localClass = localClass.getSuperclass();
      appendFieldsIn(localClass);
    }
    return super.toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/builder/ReflectionToStringBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */