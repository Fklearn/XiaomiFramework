package android.webkit;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Map;

public class WebResourceResponse
{
  private String mEncoding;
  @UnsupportedAppUsage
  private boolean mImmutable;
  private InputStream mInputStream;
  private String mMimeType;
  private String mReasonPhrase;
  private Map<String, String> mResponseHeaders;
  @UnsupportedAppUsage
  private int mStatusCode;
  
  public WebResourceResponse(String paramString1, String paramString2, int paramInt, String paramString3, Map<String, String> paramMap, InputStream paramInputStream)
  {
    this(paramString1, paramString2, paramInputStream);
    setStatusCodeAndReasonPhrase(paramInt, paramString3);
    setResponseHeaders(paramMap);
  }
  
  public WebResourceResponse(String paramString1, String paramString2, InputStream paramInputStream)
  {
    this.mMimeType = paramString1;
    this.mEncoding = paramString2;
    setData(paramInputStream);
  }
  
  @SystemApi
  public WebResourceResponse(boolean paramBoolean, String paramString1, String paramString2, int paramInt, String paramString3, Map<String, String> paramMap, InputStream paramInputStream)
  {
    this.mImmutable = paramBoolean;
    this.mMimeType = paramString1;
    this.mEncoding = paramString2;
    this.mStatusCode = paramInt;
    this.mReasonPhrase = paramString3;
    this.mResponseHeaders = paramMap;
    this.mInputStream = paramInputStream;
  }
  
  private void checkImmutable()
  {
    if (!this.mImmutable) {
      return;
    }
    throw new IllegalStateException("This WebResourceResponse instance is immutable");
  }
  
  public InputStream getData()
  {
    return this.mInputStream;
  }
  
  public String getEncoding()
  {
    return this.mEncoding;
  }
  
  public String getMimeType()
  {
    return this.mMimeType;
  }
  
  public String getReasonPhrase()
  {
    return this.mReasonPhrase;
  }
  
  public Map<String, String> getResponseHeaders()
  {
    return this.mResponseHeaders;
  }
  
  public int getStatusCode()
  {
    return this.mStatusCode;
  }
  
  public void setData(InputStream paramInputStream)
  {
    checkImmutable();
    if ((paramInputStream != null) && (StringBufferInputStream.class.isAssignableFrom(paramInputStream.getClass()))) {
      throw new IllegalArgumentException("StringBufferInputStream is deprecated and must not be passed to a WebResourceResponse");
    }
    this.mInputStream = paramInputStream;
  }
  
  public void setEncoding(String paramString)
  {
    checkImmutable();
    this.mEncoding = paramString;
  }
  
  public void setMimeType(String paramString)
  {
    checkImmutable();
    this.mMimeType = paramString;
  }
  
  public void setResponseHeaders(Map<String, String> paramMap)
  {
    checkImmutable();
    this.mResponseHeaders = paramMap;
  }
  
  public void setStatusCodeAndReasonPhrase(int paramInt, String paramString)
  {
    checkImmutable();
    if (paramInt >= 100)
    {
      if (paramInt <= 599)
      {
        if ((paramInt > 299) && (paramInt < 400)) {
          throw new IllegalArgumentException("statusCode can't be in the [300, 399] range.");
        }
        if (paramString != null)
        {
          if (!paramString.trim().isEmpty())
          {
            int i = 0;
            while (i < paramString.length()) {
              if (paramString.charAt(i) <= '') {
                i++;
              } else {
                throw new IllegalArgumentException("reasonPhrase can't contain non-ASCII characters.");
              }
            }
            this.mStatusCode = paramInt;
            this.mReasonPhrase = paramString;
            return;
          }
          throw new IllegalArgumentException("reasonPhrase can't be empty.");
        }
        throw new IllegalArgumentException("reasonPhrase can't be null.");
      }
      throw new IllegalArgumentException("statusCode can't be greater than 599.");
    }
    throw new IllegalArgumentException("statusCode can't be less than 100.");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebResourceResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */