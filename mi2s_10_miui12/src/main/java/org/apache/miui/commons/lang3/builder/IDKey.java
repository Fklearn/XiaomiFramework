package org.apache.miui.commons.lang3.builder;

final class IDKey
{
  private final int id;
  private final Object value;
  
  public IDKey(Object paramObject)
  {
    this.id = System.identityHashCode(paramObject);
    this.value = paramObject;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof IDKey;
    boolean bool2 = false;
    if (!bool1) {
      return false;
    }
    paramObject = (IDKey)paramObject;
    if (this.id != ((IDKey)paramObject).id) {
      return false;
    }
    if (this.value == ((IDKey)paramObject).value) {
      bool2 = true;
    }
    return bool2;
  }
  
  public int hashCode()
  {
    return this.id;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/builder/IDKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */