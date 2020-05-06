package android.webkit;

public abstract class ServiceWorkerWebSettings
{
  public abstract boolean getAllowContentAccess();
  
  public abstract boolean getAllowFileAccess();
  
  public abstract boolean getBlockNetworkLoads();
  
  public abstract int getCacheMode();
  
  public abstract void setAllowContentAccess(boolean paramBoolean);
  
  public abstract void setAllowFileAccess(boolean paramBoolean);
  
  public abstract void setBlockNetworkLoads(boolean paramBoolean);
  
  public abstract void setCacheMode(int paramInt);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/ServiceWorkerWebSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */