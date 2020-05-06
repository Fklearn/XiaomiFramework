package org.apache.miui.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.miui.commons.lang3.ClassUtils;

public class TypeUtils
{
  public static Map<TypeVariable<?>, Type> determineTypeArguments(Class<?> paramClass, ParameterizedType paramParameterizedType)
  {
    Object localObject = getRawType(paramParameterizedType);
    if (!isAssignable(paramClass, (Class)localObject)) {
      return null;
    }
    if (paramClass.equals(localObject)) {
      return getTypeArguments(paramParameterizedType, (Class)localObject, null);
    }
    localObject = getClosestParentType(paramClass, (Class)localObject);
    if ((localObject instanceof Class)) {
      return determineTypeArguments((Class)localObject, paramParameterizedType);
    }
    localObject = (ParameterizedType)localObject;
    paramParameterizedType = determineTypeArguments(getRawType((ParameterizedType)localObject), paramParameterizedType);
    mapTypeVariablesToArguments(paramClass, (ParameterizedType)localObject, paramParameterizedType);
    return paramParameterizedType;
  }
  
  public static Type getArrayComponentType(Type paramType)
  {
    boolean bool = paramType instanceof Class;
    Object localObject = null;
    if (bool)
    {
      Class localClass = (Class)paramType;
      paramType = (Type)localObject;
      if (localClass.isArray()) {
        paramType = localClass.getComponentType();
      }
      return paramType;
    }
    if ((paramType instanceof GenericArrayType)) {
      return ((GenericArrayType)paramType).getGenericComponentType();
    }
    return null;
  }
  
  private static Type getClosestParentType(Class<?> paramClass1, Class<?> paramClass2)
  {
    if (paramClass2.isInterface())
    {
      Type[] arrayOfType = paramClass1.getGenericInterfaces();
      Object localObject1 = null;
      int i = arrayOfType.length;
      int j = 0;
      while (j < i)
      {
        Type localType = arrayOfType[j];
        Class localClass;
        if ((localType instanceof ParameterizedType))
        {
          localClass = getRawType((ParameterizedType)localType);
        }
        else
        {
          if (!(localType instanceof Class)) {
            break label107;
          }
          localClass = (Class)localType;
        }
        Object localObject2 = localObject1;
        if (isAssignable(localClass, paramClass2))
        {
          localObject2 = localObject1;
          if (isAssignable((Type)localObject1, localClass)) {
            localObject2 = localType;
          }
        }
        j++;
        localObject1 = localObject2;
        continue;
        label107:
        paramClass1 = new StringBuilder();
        paramClass1.append("Unexpected generic interface type found: ");
        paramClass1.append(localType);
        throw new IllegalStateException(paramClass1.toString());
      }
      if (localObject1 != null) {
        return (Type)localObject1;
      }
    }
    return paramClass1.getGenericSuperclass();
  }
  
  public static Type[] getImplicitBounds(TypeVariable<?> paramTypeVariable)
  {
    paramTypeVariable = paramTypeVariable.getBounds();
    if (paramTypeVariable.length == 0)
    {
      paramTypeVariable = new Type[1];
      paramTypeVariable[0] = Object.class;
    }
    else
    {
      paramTypeVariable = normalizeUpperBounds(paramTypeVariable);
    }
    return paramTypeVariable;
  }
  
  public static Type[] getImplicitLowerBounds(WildcardType paramWildcardType)
  {
    paramWildcardType = paramWildcardType.getLowerBounds();
    if (paramWildcardType.length == 0)
    {
      paramWildcardType = new Type[1];
      paramWildcardType[0] = null;
    }
    return paramWildcardType;
  }
  
  public static Type[] getImplicitUpperBounds(WildcardType paramWildcardType)
  {
    paramWildcardType = paramWildcardType.getUpperBounds();
    if (paramWildcardType.length == 0)
    {
      paramWildcardType = new Type[1];
      paramWildcardType[0] = Object.class;
    }
    else
    {
      paramWildcardType = normalizeUpperBounds(paramWildcardType);
    }
    return paramWildcardType;
  }
  
  private static Class<?> getRawType(ParameterizedType paramParameterizedType)
  {
    Type localType = paramParameterizedType.getRawType();
    if ((localType instanceof Class)) {
      return (Class)localType;
    }
    paramParameterizedType = new StringBuilder();
    paramParameterizedType.append("Wait... What!? Type of rawType: ");
    paramParameterizedType.append(localType);
    throw new IllegalStateException(paramParameterizedType.toString());
  }
  
  public static Class<?> getRawType(Type paramType1, Type paramType2)
  {
    if ((paramType1 instanceof Class)) {
      return (Class)paramType1;
    }
    if ((paramType1 instanceof ParameterizedType)) {
      return getRawType((ParameterizedType)paramType1);
    }
    if ((paramType1 instanceof TypeVariable))
    {
      if (paramType2 == null) {
        return null;
      }
      Object localObject = ((TypeVariable)paramType1).getGenericDeclaration();
      if (!(localObject instanceof Class)) {
        return null;
      }
      localObject = getTypeArguments(paramType2, (Class)localObject);
      if (localObject == null) {
        return null;
      }
      paramType1 = (Type)((Map)localObject).get(paramType1);
      if (paramType1 == null) {
        return null;
      }
      return getRawType(paramType1, paramType2);
    }
    if ((paramType1 instanceof GenericArrayType)) {
      return Array.newInstance(getRawType(((GenericArrayType)paramType1).getGenericComponentType(), paramType2), 0).getClass();
    }
    if ((paramType1 instanceof WildcardType)) {
      return null;
    }
    paramType2 = new StringBuilder();
    paramType2.append("unknown type: ");
    paramType2.append(paramType1);
    throw new IllegalArgumentException(paramType2.toString());
  }
  
  private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> paramClass1, Class<?> paramClass2, Map<TypeVariable<?>, Type> paramMap)
  {
    if (!isAssignable(paramClass1, paramClass2)) {
      return null;
    }
    Object localObject = paramClass1;
    if (paramClass1.isPrimitive())
    {
      if (paramClass2.isPrimitive()) {
        return new HashMap();
      }
      localObject = ClassUtils.primitiveToWrapper(paramClass1);
    }
    if (paramMap == null) {
      paramClass1 = new HashMap();
    } else {
      paramClass1 = new HashMap(paramMap);
    }
    if ((((Class)localObject).getTypeParameters().length <= 0) && (!paramClass2.equals(localObject))) {
      return getTypeArguments(getClosestParentType((Class)localObject, paramClass2), paramClass2, paramClass1);
    }
    return paramClass1;
  }
  
  public static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType paramParameterizedType)
  {
    return getTypeArguments(paramParameterizedType, getRawType(paramParameterizedType), null);
  }
  
  private static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType paramParameterizedType, Class<?> paramClass, Map<TypeVariable<?>, Type> paramMap)
  {
    Class localClass = getRawType(paramParameterizedType);
    if (!isAssignable(localClass, paramClass)) {
      return null;
    }
    Object localObject1 = paramParameterizedType.getOwnerType();
    if ((localObject1 instanceof ParameterizedType))
    {
      localObject1 = (ParameterizedType)localObject1;
      paramMap = getTypeArguments((ParameterizedType)localObject1, getRawType((ParameterizedType)localObject1), paramMap);
    }
    else if (paramMap == null)
    {
      paramMap = new HashMap();
    }
    else
    {
      paramMap = new HashMap(paramMap);
    }
    Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
    localObject1 = localClass.getTypeParameters();
    for (int i = 0; i < localObject1.length; i++)
    {
      paramParameterizedType = arrayOfType[i];
      Object localObject2 = localObject1[i];
      if (paramMap.containsKey(paramParameterizedType)) {
        paramParameterizedType = (Type)paramMap.get(paramParameterizedType);
      }
      paramMap.put(localObject2, paramParameterizedType);
    }
    if (paramClass.equals(localClass)) {
      return paramMap;
    }
    return getTypeArguments(getClosestParentType(localClass, paramClass), paramClass, paramMap);
  }
  
  public static Map<TypeVariable<?>, Type> getTypeArguments(Type paramType, Class<?> paramClass)
  {
    return getTypeArguments(paramType, paramClass, null);
  }
  
  private static Map<TypeVariable<?>, Type> getTypeArguments(Type paramType, Class<?> paramClass, Map<TypeVariable<?>, Type> paramMap)
  {
    if ((paramType instanceof Class)) {
      return getTypeArguments((Class)paramType, paramClass, paramMap);
    }
    if ((paramType instanceof ParameterizedType)) {
      return getTypeArguments((ParameterizedType)paramType, paramClass, paramMap);
    }
    if ((paramType instanceof GenericArrayType))
    {
      paramType = ((GenericArrayType)paramType).getGenericComponentType();
      if (paramClass.isArray()) {
        paramClass = paramClass.getComponentType();
      }
      return getTypeArguments(paramType, paramClass, paramMap);
    }
    boolean bool = paramType instanceof WildcardType;
    int i = 0;
    int j = 0;
    Type localType;
    if (bool)
    {
      paramType = getImplicitUpperBounds((WildcardType)paramType);
      i = paramType.length;
      while (j < i)
      {
        localType = paramType[j];
        if (isAssignable(localType, paramClass)) {
          return getTypeArguments(localType, paramClass, paramMap);
        }
        j++;
      }
      return null;
    }
    if ((paramType instanceof TypeVariable))
    {
      paramType = getImplicitBounds((TypeVariable)paramType);
      int k = paramType.length;
      for (j = i; j < k; j++)
      {
        localType = paramType[j];
        if (isAssignable(localType, paramClass)) {
          return getTypeArguments(localType, paramClass, paramMap);
        }
      }
      return null;
    }
    paramClass = new StringBuilder();
    paramClass.append("found an unhandled type: ");
    paramClass.append(paramType);
    throw new IllegalStateException(paramClass.toString());
  }
  
  public static boolean isArrayType(Type paramType)
  {
    boolean bool;
    if ((!(paramType instanceof GenericArrayType)) && ((!(paramType instanceof Class)) || (!((Class)paramType).isArray()))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private static boolean isAssignable(Type paramType, Class<?> paramClass)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    if (paramType == null)
    {
      bool1 = bool2;
      if (paramClass != null) {
        if (!paramClass.isPrimitive()) {
          bool1 = bool2;
        } else {
          bool1 = false;
        }
      }
      return bool1;
    }
    if (paramClass == null) {
      return false;
    }
    if (paramClass.equals(paramType)) {
      return true;
    }
    if ((paramType instanceof Class)) {
      return ClassUtils.isAssignable((Class)paramType, paramClass);
    }
    if ((paramType instanceof ParameterizedType)) {
      return isAssignable(getRawType((ParameterizedType)paramType), paramClass);
    }
    if ((paramType instanceof TypeVariable))
    {
      paramType = ((TypeVariable)paramType).getBounds();
      int i = paramType.length;
      for (int j = 0; j < i; j++) {
        if (isAssignable(paramType[j], paramClass)) {
          return true;
        }
      }
      return false;
    }
    if ((paramType instanceof GenericArrayType))
    {
      if ((!paramClass.equals(Object.class)) && ((!paramClass.isArray()) || (!isAssignable(((GenericArrayType)paramType).getGenericComponentType(), paramClass.getComponentType())))) {
        bool1 = false;
      }
      return bool1;
    }
    if ((paramType instanceof WildcardType)) {
      return false;
    }
    paramClass = new StringBuilder();
    paramClass.append("found an unhandled type: ");
    paramClass.append(paramType);
    throw new IllegalStateException(paramClass.toString());
  }
  
  private static boolean isAssignable(Type paramType, GenericArrayType paramGenericArrayType, Map<TypeVariable<?>, Type> paramMap)
  {
    boolean bool = true;
    if (paramType == null) {
      return true;
    }
    if (paramGenericArrayType == null) {
      return false;
    }
    if (paramGenericArrayType.equals(paramType)) {
      return true;
    }
    Type localType = paramGenericArrayType.getGenericComponentType();
    if ((paramType instanceof Class))
    {
      paramType = (Class)paramType;
      if ((!paramType.isArray()) || (!isAssignable(paramType.getComponentType(), localType, paramMap))) {
        bool = false;
      }
      return bool;
    }
    if ((paramType instanceof GenericArrayType)) {
      return isAssignable(((GenericArrayType)paramType).getGenericComponentType(), localType, paramMap);
    }
    int i;
    int j;
    if ((paramType instanceof WildcardType))
    {
      paramType = getImplicitUpperBounds((WildcardType)paramType);
      i = paramType.length;
      for (j = 0; j < i; j++) {
        if (isAssignable(paramType[j], paramGenericArrayType)) {
          return true;
        }
      }
      return false;
    }
    if ((paramType instanceof TypeVariable))
    {
      paramType = getImplicitBounds((TypeVariable)paramType);
      i = paramType.length;
      for (j = 0; j < i; j++) {
        if (isAssignable(paramType[j], paramGenericArrayType)) {
          return true;
        }
      }
      return false;
    }
    if ((paramType instanceof ParameterizedType)) {
      return false;
    }
    paramGenericArrayType = new StringBuilder();
    paramGenericArrayType.append("found an unhandled type: ");
    paramGenericArrayType.append(paramType);
    throw new IllegalStateException(paramGenericArrayType.toString());
  }
  
  private static boolean isAssignable(Type paramType, ParameterizedType paramParameterizedType, Map<TypeVariable<?>, Type> paramMap)
  {
    if (paramType == null) {
      return true;
    }
    if (paramParameterizedType == null) {
      return false;
    }
    if (paramParameterizedType.equals(paramType)) {
      return true;
    }
    Object localObject1 = getRawType(paramParameterizedType);
    paramType = getTypeArguments(paramType, (Class)localObject1, null);
    if (paramType == null) {
      return false;
    }
    if (paramType.isEmpty()) {
      return true;
    }
    localObject1 = getTypeArguments(paramParameterizedType, (Class)localObject1, paramMap).entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (Map.Entry)((Iterator)localObject1).next();
      paramParameterizedType = (Type)((Map.Entry)localObject2).getValue();
      localObject2 = (Type)paramType.get(((Map.Entry)localObject2).getKey());
      if ((localObject2 != null) && (!paramParameterizedType.equals(localObject2)) && ((!(paramParameterizedType instanceof WildcardType)) || (!isAssignable((Type)localObject2, paramParameterizedType, paramMap)))) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isAssignable(Type paramType1, Type paramType2)
  {
    return isAssignable(paramType1, paramType2, null);
  }
  
  private static boolean isAssignable(Type paramType1, Type paramType2, Map<TypeVariable<?>, Type> paramMap)
  {
    if ((paramType2 != null) && (!(paramType2 instanceof Class)))
    {
      if ((paramType2 instanceof ParameterizedType)) {
        return isAssignable(paramType1, (ParameterizedType)paramType2, paramMap);
      }
      if ((paramType2 instanceof GenericArrayType)) {
        return isAssignable(paramType1, (GenericArrayType)paramType2, paramMap);
      }
      if ((paramType2 instanceof WildcardType)) {
        return isAssignable(paramType1, (WildcardType)paramType2, paramMap);
      }
      if ((paramType2 instanceof TypeVariable)) {
        return isAssignable(paramType1, (TypeVariable)paramType2, paramMap);
      }
      paramType1 = new StringBuilder();
      paramType1.append("found an unhandled type: ");
      paramType1.append(paramType2);
      throw new IllegalStateException(paramType1.toString());
    }
    return isAssignable(paramType1, (Class)paramType2);
  }
  
  private static boolean isAssignable(Type paramType, TypeVariable<?> paramTypeVariable, Map<TypeVariable<?>, Type> paramMap)
  {
    if (paramType == null) {
      return true;
    }
    if (paramTypeVariable == null) {
      return false;
    }
    if (paramTypeVariable.equals(paramType)) {
      return true;
    }
    if ((paramType instanceof TypeVariable))
    {
      Type[] arrayOfType = getImplicitBounds((TypeVariable)paramType);
      int i = arrayOfType.length;
      for (int j = 0; j < i; j++) {
        if (isAssignable(arrayOfType[j], paramTypeVariable, paramMap)) {
          return true;
        }
      }
    }
    if ((!(paramType instanceof Class)) && (!(paramType instanceof ParameterizedType)) && (!(paramType instanceof GenericArrayType)) && (!(paramType instanceof WildcardType)))
    {
      paramTypeVariable = new StringBuilder();
      paramTypeVariable.append("found an unhandled type: ");
      paramTypeVariable.append(paramType);
      throw new IllegalStateException(paramTypeVariable.toString());
    }
    return false;
  }
  
  private static boolean isAssignable(Type paramType, WildcardType paramWildcardType, Map<TypeVariable<?>, Type> paramMap)
  {
    if (paramType == null) {
      return true;
    }
    if (paramWildcardType == null) {
      return false;
    }
    if (paramWildcardType.equals(paramType)) {
      return true;
    }
    Object localObject = getImplicitUpperBounds(paramWildcardType);
    paramWildcardType = getImplicitLowerBounds(paramWildcardType);
    if ((paramType instanceof WildcardType))
    {
      paramType = (WildcardType)paramType;
      Type[] arrayOfType = getImplicitUpperBounds(paramType);
      paramType = getImplicitLowerBounds(paramType);
      int i = localObject.length;
      int k;
      for (j = 0; j < i; j++)
      {
        Type localType = substituteTypeVariables(localObject[j], paramMap);
        k = arrayOfType.length;
        for (m = 0; m < k; m++) {
          if (!isAssignable(arrayOfType[m], localType, paramMap)) {
            return false;
          }
        }
      }
      i = paramWildcardType.length;
      for (j = 0; j < i; j++)
      {
        localObject = substituteTypeVariables(paramWildcardType[j], paramMap);
        k = paramType.length;
        for (m = 0; m < k; m++) {
          if (!isAssignable((Type)localObject, paramType[m], paramMap)) {
            return false;
          }
        }
      }
      return true;
    }
    int m = localObject.length;
    for (int j = 0; j < m; j++) {
      if (!isAssignable(paramType, substituteTypeVariables(localObject[j], paramMap), paramMap)) {
        return false;
      }
    }
    m = paramWildcardType.length;
    for (j = 0; j < m; j++) {
      if (!isAssignable(substituteTypeVariables(paramWildcardType[j], paramMap), paramType, paramMap)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isInstance(Object paramObject, Type paramType)
  {
    boolean bool = false;
    if (paramType == null) {
      return false;
    }
    if (paramObject == null)
    {
      if ((!(paramType instanceof Class)) || (!((Class)paramType).isPrimitive())) {
        bool = true;
      }
    }
    else {
      bool = isAssignable(paramObject.getClass(), paramType, null);
    }
    return bool;
  }
  
  private static <T> void mapTypeVariablesToArguments(Class<T> paramClass, ParameterizedType paramParameterizedType, Map<TypeVariable<?>, Type> paramMap)
  {
    Object localObject1 = paramParameterizedType.getOwnerType();
    if ((localObject1 instanceof ParameterizedType)) {
      mapTypeVariablesToArguments(paramClass, (ParameterizedType)localObject1, paramMap);
    }
    localObject1 = paramParameterizedType.getActualTypeArguments();
    paramParameterizedType = getRawType(paramParameterizedType).getTypeParameters();
    List localList = Arrays.asList(paramClass.getTypeParameters());
    for (int i = 0; i < localObject1.length; i++)
    {
      Object localObject2 = paramParameterizedType[i];
      paramClass = localObject1[i];
      if ((localList.contains(paramClass)) && (paramMap.containsKey(localObject2))) {
        paramMap.put((TypeVariable)paramClass, (Type)paramMap.get(localObject2));
      }
    }
  }
  
  public static Type[] normalizeUpperBounds(Type[] paramArrayOfType)
  {
    if (paramArrayOfType.length < 2) {
      return paramArrayOfType;
    }
    HashSet localHashSet = new HashSet(paramArrayOfType.length);
    int i = paramArrayOfType.length;
    for (int j = 0; j < i; j++)
    {
      Type localType1 = paramArrayOfType[j];
      int k = 0;
      int m = paramArrayOfType.length;
      int i1;
      for (int n = 0;; n++)
      {
        i1 = k;
        if (n >= m) {
          break;
        }
        Type localType2 = paramArrayOfType[n];
        if ((localType1 != localType2) && (isAssignable(localType2, localType1, null)))
        {
          i1 = 1;
          break;
        }
      }
      if (i1 == 0) {
        localHashSet.add(localType1);
      }
    }
    return (Type[])localHashSet.toArray(new Type[localHashSet.size()]);
  }
  
  private static Type substituteTypeVariables(Type paramType, Map<TypeVariable<?>, Type> paramMap)
  {
    if (((paramType instanceof TypeVariable)) && (paramMap != null))
    {
      paramMap = (Type)paramMap.get(paramType);
      if (paramMap != null) {
        return paramMap;
      }
      paramMap = new StringBuilder();
      paramMap.append("missing assignment type for type variable ");
      paramMap.append(paramType);
      throw new IllegalArgumentException(paramMap.toString());
    }
    return paramType;
  }
  
  public static boolean typesSatisfyVariables(Map<TypeVariable<?>, Type> paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = (Map.Entry)localIterator.next();
      Object localObject2 = (TypeVariable)((Map.Entry)localObject1).getKey();
      localObject1 = (Type)((Map.Entry)localObject1).getValue();
      localObject2 = getImplicitBounds((TypeVariable)localObject2);
      int i = localObject2.length;
      for (int j = 0; j < i; j++) {
        if (!isAssignable((Type)localObject1, substituteTypeVariables(localObject2[j], paramMap), paramMap)) {
          return false;
        }
      }
    }
    return true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/reflect/TypeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */