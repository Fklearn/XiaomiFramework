package android.webkit;

import android.annotation.SystemApi;
import android.net.WebAddress;

public abstract class CookieManager
{
  public static boolean allowFileSchemeCookies()
  {
    return getInstance().allowFileSchemeCookiesImpl();
  }
  
  public static CookieManager getInstance()
  {
    return WebViewFactory.getProvider().getCookieManager();
  }
  
  public static void setAcceptFileSchemeCookies(boolean paramBoolean)
  {
    getInstance().setAcceptFileSchemeCookiesImpl(paramBoolean);
  }
  
  public abstract boolean acceptCookie();
  
  public abstract boolean acceptThirdPartyCookies(WebView paramWebView);
  
  @SystemApi
  protected abstract boolean allowFileSchemeCookiesImpl();
  
  protected Object clone()
    throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException("doesn't implement Cloneable");
  }
  
  public abstract void flush();
  
  @SystemApi
  public String getCookie(WebAddress paramWebAddress)
  {
    try
    {
      paramWebAddress = getCookie(paramWebAddress.toString());
      return paramWebAddress;
    }
    finally
    {
      paramWebAddress = finally;
      throw paramWebAddress;
    }
  }
  
  public abstract String getCookie(String paramString);
  
  @SystemApi
  public abstract String getCookie(String paramString, boolean paramBoolean);
  
  public abstract boolean hasCookies();
  
  @SystemApi
  public abstract boolean hasCookies(boolean paramBoolean);
  
  @Deprecated
  public abstract void removeAllCookie();
  
  public abstract void removeAllCookies(ValueCallback<Boolean> paramValueCallback);
  
  @Deprecated
  public abstract void removeExpiredCookie();
  
  @Deprecated
  public abstract void removeSessionCookie();
  
  public abstract void removeSessionCookies(ValueCallback<Boolean> paramValueCallback);
  
  public abstract void setAcceptCookie(boolean paramBoolean);
  
  @SystemApi
  protected abstract void setAcceptFileSchemeCookiesImpl(boolean paramBoolean);
  
  public abstract void setAcceptThirdPartyCookies(WebView paramWebView, boolean paramBoolean);
  
  public abstract void setCookie(String paramString1, String paramString2);
  
  public abstract void setCookie(String paramString1, String paramString2, ValueCallback<Boolean> paramValueCallback);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/CookieManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */