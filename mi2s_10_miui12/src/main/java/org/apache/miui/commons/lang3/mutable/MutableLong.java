package org.apache.miui.commons.lang3.mutable;

public class MutableLong
  extends Number
  implements Comparable<MutableLong>, Mutable<Number>
{
  private static final long serialVersionUID = 62986528375L;
  private long value;
  
  public MutableLong() {}
  
  public MutableLong(long paramLong)
  {
    this.value = paramLong;
  }
  
  public MutableLong(Number paramNumber)
  {
    this.value = paramNumber.longValue();
  }
  
  public MutableLong(String paramString)
    throws NumberFormatException
  {
    this.value = Long.parseLong(paramString);
  }
  
  public void add(long paramLong)
  {
    this.value += paramLong;
  }
  
  public void add(Number paramNumber)
  {
    this.value += paramNumber.longValue();
  }
  
  public int compareTo(MutableLong paramMutableLong)
  {
    long l1 = paramMutableLong.value;
    long l2 = this.value;
    int i;
    if (l2 < l1) {
      i = -1;
    } else if (l2 == l1) {
      i = 0;
    } else {
      i = 1;
    }
    return i;
  }
  
  public void decrement()
  {
    this.value -= 1L;
  }
  
  public double doubleValue()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof MutableLong;
    boolean bool2 = false;
    if (bool1)
    {
      if (this.value == ((MutableLong)paramObject).longValue()) {
        bool2 = true;
      }
      return bool2;
    }
    return false;
  }
  
  public float floatValue()
  {
    return (float)this.value;
  }
  
  public Long getValue()
  {
    return Long.valueOf(this.value);
  }
  
  public int hashCode()
  {
    long l = this.value;
    return (int)(l ^ l >>> 32);
  }
  
  public void increment()
  {
    this.value += 1L;
  }
  
  public int intValue()
  {
    return (int)this.value;
  }
  
  public long longValue()
  {
    return this.value;
  }
  
  public void setValue(long paramLong)
  {
    this.value = paramLong;
  }
  
  public void setValue(Number paramNumber)
  {
    this.value = paramNumber.longValue();
  }
  
  public void subtract(long paramLong)
  {
    this.value -= paramLong;
  }
  
  public void subtract(Number paramNumber)
  {
    this.value -= paramNumber.longValue();
  }
  
  public Long toLong()
  {
    return Long.valueOf(longValue());
  }
  
  public String toString()
  {
    return String.valueOf(this.value);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/mutable/MutableLong.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */