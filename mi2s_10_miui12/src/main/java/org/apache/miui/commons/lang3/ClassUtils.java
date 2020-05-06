package org.apache.miui.commons.lang3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassUtils
{
  public static final String INNER_CLASS_SEPARATOR;
  public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
  public static final String PACKAGE_SEPARATOR = String.valueOf('.');
  public static final char PACKAGE_SEPARATOR_CHAR = '.';
  private static final Map<String, String> abbreviationMap;
  private static final Map<Class<?>, Class<?>> primitiveWrapperMap;
  private static final Map<String, String> reverseAbbreviationMap;
  private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap;
  
  static
  {
    INNER_CLASS_SEPARATOR = String.valueOf('$');
    primitiveWrapperMap = new HashMap();
    primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
    primitiveWrapperMap.put(Byte.TYPE, Byte.class);
    primitiveWrapperMap.put(Character.TYPE, Character.class);
    primitiveWrapperMap.put(Short.TYPE, Short.class);
    primitiveWrapperMap.put(Integer.TYPE, Integer.class);
    primitiveWrapperMap.put(Long.TYPE, Long.class);
    primitiveWrapperMap.put(Double.TYPE, Double.class);
    primitiveWrapperMap.put(Float.TYPE, Float.class);
    primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    wrapperPrimitiveMap = new HashMap();
    Iterator localIterator = primitiveWrapperMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Class localClass1 = (Class)localIterator.next();
      Class localClass2 = (Class)primitiveWrapperMap.get(localClass1);
      if (!localClass1.equals(localClass2)) {
        wrapperPrimitiveMap.put(localClass2, localClass1);
      }
    }
    abbreviationMap = new HashMap();
    reverseAbbreviationMap = new HashMap();
    addAbbreviation("int", "I");
    addAbbreviation("boolean", "Z");
    addAbbreviation("float", "F");
    addAbbreviation("long", "J");
    addAbbreviation("short", "S");
    addAbbreviation("byte", "B");
    addAbbreviation("double", "D");
    addAbbreviation("char", "C");
  }
  
  private static void addAbbreviation(String paramString1, String paramString2)
  {
    abbreviationMap.put(paramString1, paramString2);
    reverseAbbreviationMap.put(paramString2, paramString1);
  }
  
  public static List<Class<?>> convertClassNamesToClasses(List<String> paramList)
  {
    if (paramList == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      String str = (String)paramList.next();
      try
      {
        localArrayList.add(Class.forName(str));
      }
      catch (Exception localException)
      {
        localArrayList.add(null);
      }
    }
    return localArrayList;
  }
  
  public static List<String> convertClassesToClassNames(List<Class<?>> paramList)
  {
    if (paramList == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      paramList = (Class)localIterator.next();
      if (paramList == null) {
        localArrayList.add(null);
      } else {
        localArrayList.add(paramList.getName());
      }
    }
    return localArrayList;
  }
  
  public static List<Class<?>> getAllInterfaces(Class<?> paramClass)
  {
    if (paramClass == null) {
      return null;
    }
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    getAllInterfaces(paramClass, localLinkedHashSet);
    return new ArrayList(localLinkedHashSet);
  }
  
  private static void getAllInterfaces(Class<?> paramClass, HashSet<Class<?>> paramHashSet)
  {
    while (paramClass != null)
    {
      for (Class localClass : paramClass.getInterfaces()) {
        if (paramHashSet.add(localClass)) {
          getAllInterfaces(localClass, paramHashSet);
        }
      }
      paramClass = paramClass.getSuperclass();
    }
  }
  
  public static List<Class<?>> getAllSuperclasses(Class<?> paramClass)
  {
    if (paramClass == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    for (paramClass = paramClass.getSuperclass(); paramClass != null; paramClass = paramClass.getSuperclass()) {
      localArrayList.add(paramClass);
    }
    return localArrayList;
  }
  
  private static String getCanonicalName(String paramString)
  {
    String str = StringUtils.deleteWhitespace(paramString);
    if (str == null) {
      return null;
    }
    int i = 0;
    while (str.startsWith("["))
    {
      i++;
      str = str.substring(1);
    }
    if (i < 1) {
      return str;
    }
    if (str.startsWith("L"))
    {
      if (str.endsWith(";")) {
        j = str.length() - 1;
      } else {
        j = str.length();
      }
      paramString = str.substring(1, j);
    }
    else
    {
      paramString = str;
      if (str.length() > 0) {
        paramString = (String)reverseAbbreviationMap.get(str.substring(0, 1));
      }
    }
    paramString = new StringBuilder(paramString);
    for (int j = 0; j < i; j++) {
      paramString.append("[]");
    }
    return paramString.toString();
  }
  
  public static Class<?> getClass(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    return getClass(paramClassLoader, paramString, true);
  }
  
  public static Class<?> getClass(ClassLoader paramClassLoader, String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    try
    {
      Object localObject;
      if (abbreviationMap.containsKey(paramString))
      {
        localObject = new java/lang/StringBuilder;
        ((StringBuilder)localObject).<init>();
        ((StringBuilder)localObject).append("[");
        ((StringBuilder)localObject).append((String)abbreviationMap.get(paramString));
        localObject = Class.forName(((StringBuilder)localObject).toString(), paramBoolean, paramClassLoader).getComponentType();
        paramClassLoader = (ClassLoader)localObject;
      }
      else
      {
        localObject = Class.forName(toCanonicalName(paramString), paramBoolean, paramClassLoader);
        paramClassLoader = (ClassLoader)localObject;
      }
      return paramClassLoader;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      int i = paramString.lastIndexOf('.');
      if (i != -1) {
        try
        {
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append(paramString.substring(0, i));
          localStringBuilder.append('$');
          localStringBuilder.append(paramString.substring(i + 1));
          paramClassLoader = getClass(paramClassLoader, localStringBuilder.toString(), paramBoolean);
          return paramClassLoader;
        }
        catch (ClassNotFoundException paramClassLoader) {}
      }
      throw localClassNotFoundException;
    }
  }
  
  public static Class<?> getClass(String paramString)
    throws ClassNotFoundException
  {
    return getClass(paramString, true);
  }
  
  public static Class<?> getClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    if (localClassLoader == null) {
      localClassLoader = ClassUtils.class.getClassLoader();
    }
    return getClass(localClassLoader, paramString, paramBoolean);
  }
  
  public static String getPackageCanonicalName(Class<?> paramClass)
  {
    if (paramClass == null) {
      return "";
    }
    return getPackageCanonicalName(paramClass.getName());
  }
  
  public static String getPackageCanonicalName(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      return paramString;
    }
    return getPackageCanonicalName(paramObject.getClass().getName());
  }
  
  public static String getPackageCanonicalName(String paramString)
  {
    return getPackageName(getCanonicalName(paramString));
  }
  
  public static String getPackageName(Class<?> paramClass)
  {
    if (paramClass == null) {
      return "";
    }
    return getPackageName(paramClass.getName());
  }
  
  public static String getPackageName(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      return paramString;
    }
    return getPackageName(paramObject.getClass());
  }
  
  public static String getPackageName(String paramString)
  {
    if (paramString != null)
    {
      String str = paramString;
      if (paramString.length() != 0)
      {
        while (str.charAt(0) == '[') {
          str = str.substring(1);
        }
        paramString = str;
        if (str.charAt(0) == 'L')
        {
          paramString = str;
          if (str.charAt(str.length() - 1) == ';') {
            paramString = str.substring(1);
          }
        }
        int i = paramString.lastIndexOf('.');
        if (i == -1) {
          return "";
        }
        return paramString.substring(0, i);
      }
    }
    return "";
  }
  
  public static Method getPublicMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    throws SecurityException, NoSuchMethodException
  {
    Object localObject = paramClass.getMethod(paramString, paramVarArgs);
    if (Modifier.isPublic(((Method)localObject).getDeclaringClass().getModifiers())) {
      return (Method)localObject;
    }
    localObject = new ArrayList();
    ((List)localObject).addAll(getAllInterfaces(paramClass));
    ((List)localObject).addAll(getAllSuperclasses(paramClass));
    paramClass = ((List)localObject).iterator();
    while (paramClass.hasNext())
    {
      localObject = (Class)paramClass.next();
      if (Modifier.isPublic(((Class)localObject).getModifiers())) {
        try
        {
          localObject = ((Class)localObject).getMethod(paramString, paramVarArgs);
          if (Modifier.isPublic(((Method)localObject).getDeclaringClass().getModifiers())) {
            return (Method)localObject;
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException) {}
      }
    }
    paramClass = new StringBuilder();
    paramClass.append("Can't find a public method for ");
    paramClass.append(paramString);
    paramClass.append(" ");
    paramClass.append(ArrayUtils.toString(paramVarArgs));
    throw new NoSuchMethodException(paramClass.toString());
  }
  
  public static String getShortCanonicalName(Class<?> paramClass)
  {
    if (paramClass == null) {
      return "";
    }
    return getShortCanonicalName(paramClass.getName());
  }
  
  public static String getShortCanonicalName(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      return paramString;
    }
    return getShortCanonicalName(paramObject.getClass().getName());
  }
  
  public static String getShortCanonicalName(String paramString)
  {
    return getShortClassName(getCanonicalName(paramString));
  }
  
  public static String getShortClassName(Class<?> paramClass)
  {
    if (paramClass == null) {
      return "";
    }
    return getShortClassName(paramClass.getName());
  }
  
  public static String getShortClassName(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      return paramString;
    }
    return getShortClassName(paramObject.getClass());
  }
  
  public static String getShortClassName(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    if (paramString.length() == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    boolean bool = paramString.startsWith("[");
    int i = 0;
    Object localObject = paramString;
    if (bool)
    {
      while (paramString.charAt(0) == '[')
      {
        paramString = paramString.substring(1);
        localStringBuilder.append("[]");
      }
      localObject = paramString;
      if (paramString.charAt(0) == 'L')
      {
        localObject = paramString;
        if (paramString.charAt(paramString.length() - 1) == ';') {
          localObject = paramString.substring(1, paramString.length() - 1);
        }
      }
    }
    paramString = (String)localObject;
    if (reverseAbbreviationMap.containsKey(localObject)) {
      paramString = (String)reverseAbbreviationMap.get(localObject);
    }
    int j = paramString.lastIndexOf('.');
    if (j != -1) {
      i = j + 1;
    }
    i = paramString.indexOf('$', i);
    localObject = paramString.substring(j + 1);
    paramString = (String)localObject;
    if (i != -1) {
      paramString = ((String)localObject).replace('$', '.');
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(localStringBuilder);
    return ((StringBuilder)localObject).toString();
  }
  
  public static String getSimpleName(Class<?> paramClass)
  {
    if (paramClass == null) {
      return "";
    }
    return paramClass.getSimpleName();
  }
  
  public static String getSimpleName(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      return paramString;
    }
    return getSimpleName(paramObject.getClass());
  }
  
  public static boolean isAssignable(Class<?> paramClass1, Class<?> paramClass2)
  {
    return isAssignable(paramClass1, paramClass2, SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
  }
  
  public static boolean isAssignable(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    if (paramClass2 == null) {
      return false;
    }
    if (paramClass1 == null) {
      return paramClass2.isPrimitive() ^ true;
    }
    Object localObject = paramClass1;
    if (paramBoolean)
    {
      Class<?> localClass = paramClass1;
      if (paramClass1.isPrimitive())
      {
        localClass = paramClass1;
        if (!paramClass2.isPrimitive())
        {
          paramClass1 = primitiveToWrapper(paramClass1);
          localClass = paramClass1;
          if (paramClass1 == null) {
            return false;
          }
        }
      }
      localObject = localClass;
      if (paramClass2.isPrimitive())
      {
        localObject = localClass;
        if (!localClass.isPrimitive())
        {
          paramClass1 = wrapperToPrimitive(localClass);
          localObject = paramClass1;
          if (paramClass1 == null) {
            return false;
          }
        }
      }
    }
    if (localObject.equals(paramClass2)) {
      return true;
    }
    if (((Class)localObject).isPrimitive())
    {
      if (!paramClass2.isPrimitive()) {
        return false;
      }
      if (Integer.TYPE.equals(localObject))
      {
        if ((!Long.TYPE.equals(paramClass2)) && (!Float.TYPE.equals(paramClass2)))
        {
          paramBoolean = bool5;
          if (!Double.TYPE.equals(paramClass2)) {}
        }
        else
        {
          paramBoolean = true;
        }
        return paramBoolean;
      }
      if (Long.TYPE.equals(localObject))
      {
        if (!Float.TYPE.equals(paramClass2))
        {
          paramBoolean = bool1;
          if (!Double.TYPE.equals(paramClass2)) {}
        }
        else
        {
          paramBoolean = true;
        }
        return paramBoolean;
      }
      if (Boolean.TYPE.equals(localObject)) {
        return false;
      }
      if (Double.TYPE.equals(localObject)) {
        return false;
      }
      if (Float.TYPE.equals(localObject)) {
        return Double.TYPE.equals(paramClass2);
      }
      if (Character.TYPE.equals(localObject))
      {
        if ((!Integer.TYPE.equals(paramClass2)) && (!Long.TYPE.equals(paramClass2)) && (!Float.TYPE.equals(paramClass2)))
        {
          paramBoolean = bool2;
          if (!Double.TYPE.equals(paramClass2)) {}
        }
        else
        {
          paramBoolean = true;
        }
        return paramBoolean;
      }
      if (Short.TYPE.equals(localObject))
      {
        if ((!Integer.TYPE.equals(paramClass2)) && (!Long.TYPE.equals(paramClass2)) && (!Float.TYPE.equals(paramClass2)))
        {
          paramBoolean = bool3;
          if (!Double.TYPE.equals(paramClass2)) {}
        }
        else
        {
          paramBoolean = true;
        }
        return paramBoolean;
      }
      if (Byte.TYPE.equals(localObject))
      {
        if ((!Short.TYPE.equals(paramClass2)) && (!Integer.TYPE.equals(paramClass2)) && (!Long.TYPE.equals(paramClass2)) && (!Float.TYPE.equals(paramClass2)))
        {
          paramBoolean = bool4;
          if (!Double.TYPE.equals(paramClass2)) {}
        }
        else
        {
          paramBoolean = true;
        }
        return paramBoolean;
      }
      return false;
    }
    return paramClass2.isAssignableFrom((Class)localObject);
  }
  
  public static boolean isAssignable(Class<?>[] paramArrayOfClass1, Class<?>... paramVarArgs)
  {
    return isAssignable(paramArrayOfClass1, paramVarArgs, SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
  }
  
  public static boolean isAssignable(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, boolean paramBoolean)
  {
    if (!ArrayUtils.isSameLength(paramArrayOfClass1, paramArrayOfClass2)) {
      return false;
    }
    Object localObject = paramArrayOfClass1;
    if (paramArrayOfClass1 == null) {
      localObject = ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    paramArrayOfClass1 = paramArrayOfClass2;
    if (paramArrayOfClass2 == null) {
      paramArrayOfClass1 = ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    for (int i = 0; i < localObject.length; i++) {
      if (!isAssignable(localObject[i], paramArrayOfClass1[i], paramBoolean)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isInnerClass(Class<?> paramClass)
  {
    boolean bool;
    if ((paramClass != null) && (paramClass.getEnclosingClass() != null)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isPrimitiveOrWrapper(Class<?> paramClass)
  {
    boolean bool = false;
    if (paramClass == null) {
      return false;
    }
    if ((paramClass.isPrimitive()) || (isPrimitiveWrapper(paramClass))) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isPrimitiveWrapper(Class<?> paramClass)
  {
    return wrapperPrimitiveMap.containsKey(paramClass);
  }
  
  public static Class<?> primitiveToWrapper(Class<?> paramClass)
  {
    Class<?> localClass = paramClass;
    Object localObject = localClass;
    if (paramClass != null)
    {
      localObject = localClass;
      if (paramClass.isPrimitive()) {
        localObject = (Class)primitiveWrapperMap.get(paramClass);
      }
    }
    return (Class<?>)localObject;
  }
  
  public static Class<?>[] primitivesToWrappers(Class<?>... paramVarArgs)
  {
    if (paramVarArgs == null) {
      return null;
    }
    if (paramVarArgs.length == 0) {
      return paramVarArgs;
    }
    Class[] arrayOfClass = new Class[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++) {
      arrayOfClass[i] = primitiveToWrapper(paramVarArgs[i]);
    }
    return arrayOfClass;
  }
  
  private static String toCanonicalName(String paramString)
  {
    paramString = StringUtils.deleteWhitespace(paramString);
    if (paramString != null)
    {
      Object localObject = paramString;
      if (paramString.endsWith("[]"))
      {
        localObject = new StringBuilder();
        while (paramString.endsWith("[]"))
        {
          paramString = paramString.substring(0, paramString.length() - 2);
          ((StringBuilder)localObject).append("[");
        }
        String str = (String)abbreviationMap.get(paramString);
        if (str != null)
        {
          ((StringBuilder)localObject).append(str);
        }
        else
        {
          ((StringBuilder)localObject).append("L");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append(";");
        }
        localObject = ((StringBuilder)localObject).toString();
      }
      return (String)localObject;
    }
    throw new NullPointerException("className must not be null.");
  }
  
  public static Class<?>[] toClass(Object... paramVarArgs)
  {
    if (paramVarArgs == null) {
      return null;
    }
    if (paramVarArgs.length == 0) {
      return ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    Class[] arrayOfClass = new Class[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++)
    {
      Class localClass;
      if (paramVarArgs[i] == null) {
        localClass = null;
      } else {
        localClass = paramVarArgs[i].getClass();
      }
      arrayOfClass[i] = localClass;
    }
    return arrayOfClass;
  }
  
  public static Class<?> wrapperToPrimitive(Class<?> paramClass)
  {
    return (Class)wrapperPrimitiveMap.get(paramClass);
  }
  
  public static Class<?>[] wrappersToPrimitives(Class<?>... paramVarArgs)
  {
    if (paramVarArgs == null) {
      return null;
    }
    if (paramVarArgs.length == 0) {
      return paramVarArgs;
    }
    Class[] arrayOfClass = new Class[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++) {
      arrayOfClass[i] = wrapperToPrimitive(paramVarArgs[i]);
    }
    return arrayOfClass;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/ClassUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */