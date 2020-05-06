package org.apache.miui.commons.lang3.mutable;

public class MutableInt
  extends Number
  implements Comparable<MutableInt>, Mutable<Number>
{
  private static final long serialVersionUID = 512176391864L;
  private int value;
  
  public MutableInt() {}
  
  public MutableInt(int paramInt)
  {
    this.value = paramInt;
  }
  
  public MutableInt(Number paramNumber)
  {
    this.value = paramNumber.intValue();
  }
  
  public MutableInt(String paramString)
    throws NumberFormatException
  {
    this.value = Integer.parseInt(paramString);
  }
  
  public void add(int paramInt)
  {
    this.value += paramInt;
  }
  
  public void add(Number paramNumber)
  {
    this.value += paramNumber.intValue();
  }
  
  public int compareTo(MutableInt paramMutableInt)
  {
    int i = paramMutableInt.value;
    int j = this.value;
    if (j < i) {
      j = -1;
    } else if (j == i) {
      j = 0;
    } else {
      j = 1;
    }
    return j;
  }
  
  public void decrement()
  {
    this.value -= 1;
  }
  
  public double doubleValue()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof MutableInt;
    boolean bool2 = false;
    if (bool1)
    {
      if (this.value == ((MutableInt)paramObject).intValue()) {
        bool2 = true;
      }
      return bool2;
    }
    return false;
  }
  
  public float floatValue()
  {
    return this.value;
  }
  
  public Integer getValue()
  {
    return Integer.valueOf(this.value);
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  public void increment()
  {
    this.value += 1;
  }
  
  public int intValue()
  {
    return this.value;
  }
  
  public long longValue()
  {
    return this.value;
  }
  
  public void setValue(int paramInt)
  {
    this.value = paramInt;
  }
  
  public void setValue(Number paramNumber)
  {
    this.value = paramNumber.intValue();
  }
  
  public void subtract(int paramInt)
  {
    this.value -= paramInt;
  }
  
  public void subtract(Number paramNumber)
  {
    this.value -= paramNumber.intValue();
  }
  
  public Integer toInteger()
  {
    return Integer.valueOf(intValue());
  }
  
  public String toString()
  {
    return String.valueOf(this.value);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/mutable/MutableInt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */