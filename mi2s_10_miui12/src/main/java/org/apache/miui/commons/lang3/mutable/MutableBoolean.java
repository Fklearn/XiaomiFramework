package org.apache.miui.commons.lang3.mutable;

import java.io.Serializable;

public class MutableBoolean
  implements Mutable<Boolean>, Serializable, Comparable<MutableBoolean>
{
  private static final long serialVersionUID = -4830728138360036487L;
  private boolean value;
  
  public MutableBoolean() {}
  
  public MutableBoolean(Boolean paramBoolean)
  {
    this.value = paramBoolean.booleanValue();
  }
  
  public MutableBoolean(boolean paramBoolean)
  {
    this.value = paramBoolean;
  }
  
  public boolean booleanValue()
  {
    return this.value;
  }
  
  public int compareTo(MutableBoolean paramMutableBoolean)
  {
    boolean bool1 = paramMutableBoolean.value;
    boolean bool2 = this.value;
    int i;
    if (bool2 == bool1) {
      i = 0;
    } else if (bool2) {
      i = 1;
    } else {
      i = -1;
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof MutableBoolean;
    boolean bool2 = false;
    if (bool1)
    {
      if (this.value == ((MutableBoolean)paramObject).booleanValue()) {
        bool2 = true;
      }
      return bool2;
    }
    return false;
  }
  
  public Boolean getValue()
  {
    return Boolean.valueOf(this.value);
  }
  
  public int hashCode()
  {
    Boolean localBoolean;
    if (this.value) {
      localBoolean = Boolean.TRUE;
    } else {
      localBoolean = Boolean.FALSE;
    }
    return localBoolean.hashCode();
  }
  
  public boolean isFalse()
  {
    return this.value ^ true;
  }
  
  public boolean isTrue()
  {
    boolean bool1 = this.value;
    boolean bool2 = true;
    if (bool1 != true) {
      bool2 = false;
    }
    return bool2;
  }
  
  public void setValue(Boolean paramBoolean)
  {
    this.value = paramBoolean.booleanValue();
  }
  
  public void setValue(boolean paramBoolean)
  {
    this.value = paramBoolean;
  }
  
  public Boolean toBoolean()
  {
    return Boolean.valueOf(booleanValue());
  }
  
  public String toString()
  {
    return String.valueOf(this.value);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/mutable/MutableBoolean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */