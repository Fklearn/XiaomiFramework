package android.webkit;

import android.annotation.UnsupportedAppUsage;
import java.io.InputStream;
import java.util.Map;

@Deprecated
public final class PluginData
{
  private long mContentLength;
  private Map<String, String[]> mHeaders;
  private int mStatusCode;
  private InputStream mStream;
  
  @Deprecated
  @UnsupportedAppUsage
  public PluginData(InputStream paramInputStream, long paramLong, Map<String, String[]> paramMap, int paramInt)
  {
    this.mStream = paramInputStream;
    this.mContentLength = paramLong;
    this.mHeaders = paramMap;
    this.mStatusCode = paramInt;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public long getContentLength()
  {
    return this.mContentLength;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public Map<String, String[]> getHeaders()
  {
    return this.mHeaders;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public InputStream getInputStream()
  {
    return this.mStream;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public int getStatusCode()
  {
    return this.mStatusCode;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/PluginData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */