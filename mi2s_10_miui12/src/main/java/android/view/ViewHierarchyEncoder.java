package android.view;

import android.annotation.UnsupportedAppUsage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ViewHierarchyEncoder
{
  private static final byte SIG_BOOLEAN = 90;
  private static final byte SIG_BYTE = 66;
  private static final byte SIG_DOUBLE = 68;
  private static final short SIG_END_MAP = 0;
  private static final byte SIG_FLOAT = 70;
  private static final byte SIG_INT = 73;
  private static final byte SIG_LONG = 74;
  private static final byte SIG_MAP = 77;
  private static final byte SIG_SHORT = 83;
  private static final byte SIG_STRING = 82;
  private Charset mCharset = Charset.forName("utf-8");
  private short mPropertyId = (short)1;
  private final Map<String, Short> mPropertyNames = new HashMap(200);
  private final DataOutputStream mStream;
  
  public ViewHierarchyEncoder(ByteArrayOutputStream paramByteArrayOutputStream)
  {
    this.mStream = new DataOutputStream(paramByteArrayOutputStream);
  }
  
  private short createPropertyIndex(String paramString)
  {
    Short localShort1 = (Short)this.mPropertyNames.get(paramString);
    Short localShort2 = localShort1;
    if (localShort1 == null)
    {
      short s = this.mPropertyId;
      this.mPropertyId = ((short)(short)(s + 1));
      localShort2 = Short.valueOf(s);
      this.mPropertyNames.put(paramString, localShort2);
    }
    return localShort2.shortValue();
  }
  
  private void endPropertyMap()
  {
    writeShort((short)0);
  }
  
  private void startPropertyMap()
  {
    try
    {
      this.mStream.write(77);
    }
    catch (IOException localIOException) {}
  }
  
  private void writeBoolean(boolean paramBoolean)
  {
    try
    {
      this.mStream.write(90);
      DataOutputStream localDataOutputStream = this.mStream;
      int i;
      if (paramBoolean) {
        i = 1;
      } else {
        i = 0;
      }
      localDataOutputStream.write(i);
    }
    catch (IOException localIOException) {}
  }
  
  private void writeFloat(float paramFloat)
  {
    try
    {
      this.mStream.write(70);
      this.mStream.writeFloat(paramFloat);
    }
    catch (IOException localIOException) {}
  }
  
  private void writeInt(int paramInt)
  {
    try
    {
      this.mStream.write(73);
      this.mStream.writeInt(paramInt);
    }
    catch (IOException localIOException) {}
  }
  
  private void writeShort(short paramShort)
  {
    try
    {
      this.mStream.write(83);
      this.mStream.writeShort(paramShort);
    }
    catch (IOException localIOException) {}
  }
  
  private void writeString(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    try
    {
      this.mStream.write(82);
      paramString = str.getBytes(this.mCharset);
      int i = (short)Math.min(paramString.length, 32767);
      this.mStream.writeShort(i);
      this.mStream.write(paramString, 0, i);
    }
    catch (IOException paramString) {}
  }
  
  @UnsupportedAppUsage
  public void addProperty(String paramString, float paramFloat)
  {
    writeShort(createPropertyIndex(paramString));
    writeFloat(paramFloat);
  }
  
  @UnsupportedAppUsage
  public void addProperty(String paramString, int paramInt)
  {
    writeShort(createPropertyIndex(paramString));
    writeInt(paramInt);
  }
  
  @UnsupportedAppUsage
  public void addProperty(String paramString1, String paramString2)
  {
    writeShort(createPropertyIndex(paramString1));
    writeString(paramString2);
  }
  
  public void addProperty(String paramString, short paramShort)
  {
    writeShort(createPropertyIndex(paramString));
    writeShort(paramShort);
  }
  
  @UnsupportedAppUsage
  public void addProperty(String paramString, boolean paramBoolean)
  {
    writeShort(createPropertyIndex(paramString));
    writeBoolean(paramBoolean);
  }
  
  public void addPropertyKey(String paramString)
  {
    writeShort(createPropertyIndex(paramString));
  }
  
  public void beginObject(Object paramObject)
  {
    startPropertyMap();
    addProperty("meta:__name__", paramObject.getClass().getName());
    addProperty("meta:__hash__", paramObject.hashCode());
  }
  
  public void endObject()
  {
    endPropertyMap();
  }
  
  public void endStream()
  {
    startPropertyMap();
    addProperty("__name__", "propertyIndex");
    Iterator localIterator = this.mPropertyNames.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      writeShort(((Short)localEntry.getValue()).shortValue());
      writeString((String)localEntry.getKey());
    }
    endPropertyMap();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewHierarchyEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */