package android.view.inspector;

import java.util.Set;
import java.util.function.IntFunction;

public abstract interface PropertyMapper
{
  public abstract int mapBoolean(String paramString, int paramInt);
  
  public abstract int mapByte(String paramString, int paramInt);
  
  public abstract int mapChar(String paramString, int paramInt);
  
  public abstract int mapColor(String paramString, int paramInt);
  
  public abstract int mapDouble(String paramString, int paramInt);
  
  public abstract int mapFloat(String paramString, int paramInt);
  
  public abstract int mapGravity(String paramString, int paramInt);
  
  public abstract int mapInt(String paramString, int paramInt);
  
  public abstract int mapIntEnum(String paramString, int paramInt, IntFunction<String> paramIntFunction);
  
  public abstract int mapIntFlag(String paramString, int paramInt, IntFunction<Set<String>> paramIntFunction);
  
  public abstract int mapLong(String paramString, int paramInt);
  
  public abstract int mapObject(String paramString, int paramInt);
  
  public abstract int mapResourceId(String paramString, int paramInt);
  
  public abstract int mapShort(String paramString, int paramInt);
  
  public static class PropertyConflictException
    extends RuntimeException
  {
    public PropertyConflictException(String paramString1, String paramString2, String paramString3)
    {
      super();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inspector/PropertyMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */