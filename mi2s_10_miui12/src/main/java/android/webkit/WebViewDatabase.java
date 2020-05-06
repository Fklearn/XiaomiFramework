package android.webkit;

import android.content.Context;

public abstract class WebViewDatabase
{
  protected static final String LOGTAG = "webviewdatabase";
  
  public static WebViewDatabase getInstance(Context paramContext)
  {
    return WebViewFactory.getProvider().getWebViewDatabase(paramContext);
  }
  
  @Deprecated
  public abstract void clearFormData();
  
  public abstract void clearHttpAuthUsernamePassword();
  
  @Deprecated
  public abstract void clearUsernamePassword();
  
  public abstract String[] getHttpAuthUsernamePassword(String paramString1, String paramString2);
  
  @Deprecated
  public abstract boolean hasFormData();
  
  public abstract boolean hasHttpAuthUsernamePassword();
  
  @Deprecated
  public abstract boolean hasUsernamePassword();
  
  public abstract void setHttpAuthUsernamePassword(String paramString1, String paramString2, String paramString3, String paramString4);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */