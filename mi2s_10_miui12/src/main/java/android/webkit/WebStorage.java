package android.webkit;

import android.annotation.SystemApi;
import java.util.Map;

public class WebStorage
{
  public static WebStorage getInstance()
  {
    return WebViewFactory.getProvider().getWebStorage();
  }
  
  public void deleteAllData() {}
  
  public void deleteOrigin(String paramString) {}
  
  public void getOrigins(ValueCallback<Map> paramValueCallback) {}
  
  public void getQuotaForOrigin(String paramString, ValueCallback<Long> paramValueCallback) {}
  
  public void getUsageForOrigin(String paramString, ValueCallback<Long> paramValueCallback) {}
  
  @Deprecated
  public void setQuotaForOrigin(String paramString, long paramLong) {}
  
  public static class Origin
  {
    private String mOrigin = null;
    private long mQuota = 0L;
    private long mUsage = 0L;
    
    @SystemApi
    protected Origin(String paramString, long paramLong1, long paramLong2)
    {
      this.mOrigin = paramString;
      this.mQuota = paramLong1;
      this.mUsage = paramLong2;
    }
    
    public String getOrigin()
    {
      return this.mOrigin;
    }
    
    public long getQuota()
    {
      return this.mQuota;
    }
    
    public long getUsage()
    {
      return this.mUsage;
    }
  }
  
  @Deprecated
  public static abstract interface QuotaUpdater
  {
    public abstract void updateQuota(long paramLong);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */