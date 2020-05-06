package android.view.inspector;

import android.graphics.Color;

public abstract interface PropertyReader
{
  public abstract void readBoolean(int paramInt, boolean paramBoolean);
  
  public abstract void readByte(int paramInt, byte paramByte);
  
  public abstract void readChar(int paramInt, char paramChar);
  
  public abstract void readColor(int paramInt1, int paramInt2);
  
  public abstract void readColor(int paramInt, long paramLong);
  
  public abstract void readColor(int paramInt, Color paramColor);
  
  public abstract void readDouble(int paramInt, double paramDouble);
  
  public abstract void readFloat(int paramInt, float paramFloat);
  
  public abstract void readGravity(int paramInt1, int paramInt2);
  
  public abstract void readInt(int paramInt1, int paramInt2);
  
  public abstract void readIntEnum(int paramInt1, int paramInt2);
  
  public abstract void readIntFlag(int paramInt1, int paramInt2);
  
  public abstract void readLong(int paramInt, long paramLong);
  
  public abstract void readObject(int paramInt, Object paramObject);
  
  public abstract void readResourceId(int paramInt1, int paramInt2);
  
  public abstract void readShort(int paramInt, short paramShort);
  
  public static class PropertyTypeMismatchException
    extends RuntimeException
  {
    public PropertyTypeMismatchException(int paramInt, String paramString1, String paramString2)
    {
      super();
    }
    
    public PropertyTypeMismatchException(int paramInt, String paramString1, String paramString2, String paramString3)
    {
      super();
    }
    
    private static String formatMessage(int paramInt, String paramString1, String paramString2, String paramString3)
    {
      if (paramString3 == null) {
        return String.format("Attempted to read property with ID 0x%08X as type %s, but the ID is of type %s.", new Object[] { Integer.valueOf(paramInt), paramString1, paramString2 });
      }
      return String.format("Attempted to read property \"%s\" with ID 0x%08X as type %s, but the ID is of type %s.", new Object[] { paramString3, Integer.valueOf(paramInt), paramString1, paramString2 });
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inspector/PropertyReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */