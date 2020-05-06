package android.webkit;

import android.annotation.UnsupportedAppUsage;
import java.util.Map;

@Deprecated
public abstract interface UrlInterceptHandler
{
  @Deprecated
  @UnsupportedAppUsage
  public abstract PluginData getPluginData(String paramString, Map<String, String> paramMap);
  
  @Deprecated
  @UnsupportedAppUsage
  public abstract CacheManager.CacheResult service(String paramString, Map<String, String> paramMap);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/UrlInterceptHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */