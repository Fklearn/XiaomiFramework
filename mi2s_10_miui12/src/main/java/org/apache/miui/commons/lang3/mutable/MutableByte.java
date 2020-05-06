package org.apache.miui.commons.lang3.mutable;

public class MutableByte
  extends Number
  implements Comparable<MutableByte>, Mutable<Number>
{
  private static final long serialVersionUID = -1585823265L;
  private byte value;
  
  public MutableByte() {}
  
  public MutableByte(byte paramByte)
  {
    this.value = ((byte)paramByte);
  }
  
  public MutableByte(Number paramNumber)
  {
    this.value = paramNumber.byteValue();
  }
  
  public MutableByte(String paramString)
    throws NumberFormatException
  {
    this.value = Byte.parseByte(paramString);
  }
  
  public void add(byte paramByte)
  {
    this.value = ((byte)(byte)(this.value + paramByte));
  }
  
  public void add(Number paramNumber)
  {
    this.value = ((byte)(byte)(this.value + paramNumber.byteValue()));
  }
  
  public byte byteValue()
  {
    return this.value;
  }
  
  public int compareTo(MutableByte paramMutableByte)
  {
    int i = paramMutableByte.value;
    int j = this.value;
    if (j < i) {
      i = -1;
    } else if (j == i) {
      i = 0;
    } else {
      i = 1;
    }
    return i;
  }
  
  public void decrement()
  {
    this.value = ((byte)(byte)(this.value - 1));
  }
  
  public double doubleValue()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof MutableByte;
    boolean bool2 = false;
    if (bool1)
    {
      if (this.value == ((MutableByte)paramObject).byteValue()) {
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
  
  public Byte getValue()
  {
    return Byte.valueOf(this.value);
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  public void increment()
  {
    this.value = ((byte)(byte)(this.value + 1));
  }
  
  public int intValue()
  {
    return this.value;
  }
  
  public long longValue()
  {
    return this.value;
  }
  
  public void setValue(byte paramByte)
  {
    this.value = ((byte)paramByte);
  }
  
  public void setValue(Number paramNumber)
  {
    this.value = paramNumber.byteValue();
  }
  
  public void subtract(byte paramByte)
  {
    this.value = ((byte)(byte)(this.value - paramByte));
  }
  
  public void subtract(Number paramNumber)
  {
    this.value = ((byte)(byte)(this.value - paramNumber.byteValue()));
  }
  
  public Byte toByte()
  {
    return Byte.valueOf(byteValue());
  }
  
  public String toString()
  {
    return String.valueOf(this.value);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/mutable/MutableByte.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */