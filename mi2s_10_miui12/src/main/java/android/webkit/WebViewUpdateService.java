package android.webkit;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.os.RemoteException;

@SystemApi
public final class WebViewUpdateService
{
  public static WebViewProviderInfo[] getAllWebViewPackages()
  {
    Object localObject = getUpdateService();
    if (localObject == null) {
      return new WebViewProviderInfo[0];
    }
    try
    {
      localObject = ((IWebViewUpdateService)localObject).getAllWebViewPackages();
      return (WebViewProviderInfo[])localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public static String getCurrentWebViewPackageName()
  {
    Object localObject = getUpdateService();
    if (localObject == null) {
      return null;
    }
    try
    {
      localObject = ((IWebViewUpdateService)localObject).getCurrentWebViewPackageName();
      return (String)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private static IWebViewUpdateService getUpdateService()
  {
    return WebViewFactory.getUpdateService();
  }
  
  public static WebViewProviderInfo[] getValidWebViewPackages()
  {
    Object localObject = getUpdateService();
    if (localObject == null) {
      return new WebViewProviderInfo[0];
    }
    try
    {
      localObject = ((IWebViewUpdateService)localObject).getValidWebViewPackages();
      return (WebViewProviderInfo[])localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewUpdateService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */