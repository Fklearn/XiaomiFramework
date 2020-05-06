package org.apache.miui.commons.lang3.mutable;

import java.io.Serializable;

public class MutableObject<T>
  implements Mutable<T>, Serializable
{
  private static final long serialVersionUID = 86241875189L;
  private T value;
  
  public MutableObject() {}
  
  public MutableObject(T paramT)
  {
    this.value = paramT;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (getClass() == paramObject.getClass())
    {
      paramObject = (MutableObject)paramObject;
      return this.value.equals(((MutableObject)paramObject).value);
    }
    return false;
  }
  
  public T getValue()
  {
    return (T)this.value;
  }
  
  public int hashCode()
  {
    Object localObject = this.value;
    int i;
    if (localObject == null) {
      i = 0;
    } else {
      i = localObject.hashCode();
    }
    return i;
  }
  
  public void setValue(T paramT)
  {
    this.value = paramT;
  }
  
  public String toString()
  {
    Object localObject = this.value;
    if (localObject == null) {
      localObject = "null";
    } else {
      localObject = localObject.toString();
    }
    return (String)localObject;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/mutable/MutableObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */