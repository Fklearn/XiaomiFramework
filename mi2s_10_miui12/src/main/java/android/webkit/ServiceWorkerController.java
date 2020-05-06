package android.webkit;

public abstract class ServiceWorkerController
{
  public static ServiceWorkerController getInstance()
  {
    return WebViewFactory.getProvider().getServiceWorkerController();
  }
  
  public abstract ServiceWorkerWebSettings getServiceWorkerWebSettings();
  
  public abstract void setServiceWorkerClient(ServiceWorkerClient paramServiceWorkerClient);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/ServiceWorkerController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */