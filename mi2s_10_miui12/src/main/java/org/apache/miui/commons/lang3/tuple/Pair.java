package org.apache.miui.commons.lang3.tuple;

import java.io.Serializable;
import java.util.Map.Entry;
import org.apache.miui.commons.lang3.ObjectUtils;
import org.apache.miui.commons.lang3.builder.CompareToBuilder;

public abstract class Pair<L, R>
  implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable
{
  private static final long serialVersionUID = 4954918890077093841L;
  
  public static <L, R> Pair<L, R> of(L paramL, R paramR)
  {
    return new ImmutablePair(paramL, paramR);
  }
  
  public int compareTo(Pair<L, R> paramPair)
  {
    return new CompareToBuilder().append(getLeft(), paramPair.getLeft()).append(getRight(), paramPair.getRight()).toComparison();
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Map.Entry))
    {
      paramObject = (Map.Entry)paramObject;
      if ((!ObjectUtils.equals(getKey(), ((Map.Entry)paramObject).getKey())) || (!ObjectUtils.equals(getValue(), ((Map.Entry)paramObject).getValue()))) {
        bool = false;
      }
      return bool;
    }
    return false;
  }
  
  public final L getKey()
  {
    return (L)getLeft();
  }
  
  public abstract L getLeft();
  
  public abstract R getRight();
  
  public R getValue()
  {
    return (R)getRight();
  }
  
  public int hashCode()
  {
    Object localObject = getKey();
    int i = 0;
    int j;
    if (localObject == null) {
      j = 0;
    } else {
      j = getKey().hashCode();
    }
    if (getValue() != null) {
      i = getValue().hashCode();
    }
    return j ^ i;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('(');
    localStringBuilder.append(getLeft());
    localStringBuilder.append(',');
    localStringBuilder.append(getRight());
    localStringBuilder.append(')');
    return localStringBuilder.toString();
  }
  
  public String toString(String paramString)
  {
    return String.format(paramString, new Object[] { getLeft(), getRight() });
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/tuple/Pair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */