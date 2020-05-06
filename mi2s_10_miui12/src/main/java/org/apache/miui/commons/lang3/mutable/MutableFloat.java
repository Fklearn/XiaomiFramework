package org.apache.miui.commons.lang3.mutable;

public class MutableFloat
  extends Number
  implements Comparable<MutableFloat>, Mutable<Number>
{
  private static final long serialVersionUID = 5787169186L;
  private float value;
  
  public MutableFloat() {}
  
  public MutableFloat(float paramFloat)
  {
    this.value = paramFloat;
  }
  
  public MutableFloat(Number paramNumber)
  {
    this.value = paramNumber.floatValue();
  }
  
  public MutableFloat(String paramString)
    throws NumberFormatException
  {
    this.value = Float.parseFloat(paramString);
  }
  
  public void add(float paramFloat)
  {
    this.value += paramFloat;
  }
  
  public void add(Number paramNumber)
  {
    this.value += paramNumber.floatValue();
  }
  
  public int compareTo(MutableFloat paramMutableFloat)
  {
    float f = paramMutableFloat.value;
    return Float.compare(this.value, f);
  }
  
  public void decrement()
  {
    this.value -= 1.0F;
  }
  
  public double doubleValue()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool;
    if (((paramObject instanceof MutableFloat)) && (Float.floatToIntBits(((MutableFloat)paramObject).value) == Float.floatToIntBits(this.value))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public float floatValue()
  {
    return this.value;
  }
  
  public Float getValue()
  {
    return Float.valueOf(this.value);
  }
  
  public int hashCode()
  {
    return Float.floatToIntBits(this.value);
  }
  
  public void increment()
  {
    this.value += 1.0F;
  }
  
  public int intValue()
  {
    return (int)this.value;
  }
  
  public boolean isInfinite()
  {
    return Float.isInfinite(this.value);
  }
  
  public boolean isNaN()
  {
    return Float.isNaN(this.value);
  }
  
  public long longValue()
  {
    return this.value;
  }
  
  public void setValue(float paramFloat)
  {
    this.value = paramFloat;
  }
  
  public void setValue(Number paramNumber)
  {
    this.value = paramNumber.floatValue();
  }
  
  public void subtract(float paramFloat)
  {
    this.value -= paramFloat;
  }
  
  public void subtract(Number paramNumber)
  {
    this.value -= paramNumber.floatValue();
  }
  
  public Float toFloat()
  {
    return Float.valueOf(floatValue());
  }
  
  public String toString()
  {
    return String.valueOf(this.value);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/mutable/MutableFloat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */