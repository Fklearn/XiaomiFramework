package org.apache.miui.commons.lang3.mutable;

public class MutableShort
  extends Number
  implements Comparable<MutableShort>, Mutable<Number>
{
  private static final long serialVersionUID = -2135791679L;
  private short value;
  
  public MutableShort() {}
  
  public MutableShort(Number paramNumber)
  {
    this.value = paramNumber.shortValue();
  }
  
  public MutableShort(String paramString)
    throws NumberFormatException
  {
    this.value = Short.parseShort(paramString);
  }
  
  public MutableShort(short paramShort)
  {
    this.value = ((short)paramShort);
  }
  
  public void add(Number paramNumber)
  {
    this.value = ((short)(short)(this.value + paramNumber.shortValue()));
  }
  
  public void add(short paramShort)
  {
    this.value = ((short)(short)(this.value + paramShort));
  }
  
  public int compareTo(MutableShort paramMutableShort)
  {
    int i = paramMutableShort.value;
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
    this.value = ((short)(short)(this.value - 1));
  }
  
  public double doubleValue()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof MutableShort;
    boolean bool2 = false;
    if (bool1)
    {
      if (this.value == ((MutableShort)paramObject).shortValue()) {
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
  
  public Short getValue()
  {
    return Short.valueOf(this.value);
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  public void increment()
  {
    this.value = ((short)(short)(this.value + 1));
  }
  
  public int intValue()
  {
    return this.value;
  }
  
  public long longValue()
  {
    return this.value;
  }
  
  public void setValue(Number paramNumber)
  {
    this.value = paramNumber.shortValue();
  }
  
  public void setValue(short paramShort)
  {
    this.value = ((short)paramShort);
  }
  
  public short shortValue()
  {
    return this.value;
  }
  
  public void subtract(Number paramNumber)
  {
    this.value = ((short)(short)(this.value - paramNumber.shortValue()));
  }
  
  public void subtract(short paramShort)
  {
    this.value = ((short)(short)(this.value - paramShort));
  }
  
  public Short toShort()
  {
    return Short.valueOf(shortValue());
  }
  
  public String toString()
  {
    return String.valueOf(this.value);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/mutable/MutableShort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */