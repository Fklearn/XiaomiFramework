package android.webkit;

import android.annotation.SystemApi;
import java.util.Set;

public class GeolocationPermissions
{
  public static GeolocationPermissions getInstance()
  {
    return WebViewFactory.getProvider().getGeolocationPermissions();
  }
  
  public void allow(String paramString) {}
  
  public void clear(String paramString) {}
  
  public void clearAll() {}
  
  public void getAllowed(String paramString, ValueCallback<Boolean> paramValueCallback) {}
  
  public void getOrigins(ValueCallback<Set<String>> paramValueCallback) {}
  
  public static abstract interface Callback
  {
    public abstract void invoke(String paramString, boolean paramBoolean1, boolean paramBoolean2);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/GeolocationPermissions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */