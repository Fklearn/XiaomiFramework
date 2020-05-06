package android.webkit;

import android.content.Context;

@Deprecated
public final class CookieSyncManager
  extends WebSyncManager
{
  private static boolean sGetInstanceAllowed = false;
  private static final Object sLock = new Object();
  private static CookieSyncManager sRef;
  
  private CookieSyncManager()
  {
    super(null, null);
  }
  
  private static void checkInstanceIsAllowed()
  {
    if (sGetInstanceAllowed) {
      return;
    }
    throw new IllegalStateException("CookieSyncManager::createInstance() needs to be called before CookieSyncManager::getInstance()");
  }
  
  public static CookieSyncManager createInstance(Context paramContext)
  {
    Object localObject = sLock;
    if (paramContext != null) {
      try
      {
        setGetInstanceIsAllowed();
        paramContext = getInstance();
        return paramContext;
      }
      finally
      {
        break label37;
      }
    }
    paramContext = new java/lang/IllegalArgumentException;
    paramContext.<init>("Invalid context argument");
    throw paramContext;
    label37:
    throw paramContext;
  }
  
  public static CookieSyncManager getInstance()
  {
    synchronized (sLock)
    {
      checkInstanceIsAllowed();
      if (sRef == null)
      {
        localCookieSyncManager = new android/webkit/CookieSyncManager;
        localCookieSyncManager.<init>();
        sRef = localCookieSyncManager;
      }
      CookieSyncManager localCookieSyncManager = sRef;
      return localCookieSyncManager;
    }
  }
  
  static void setGetInstanceIsAllowed()
  {
    sGetInstanceAllowed = true;
  }
  
  @Deprecated
  public void resetSync() {}
  
  @Deprecated
  public void startSync() {}
  
  @Deprecated
  public void stopSync() {}
  
  @Deprecated
  public void sync()
  {
    CookieManager.getInstance().flush();
  }
  
  @Deprecated
  protected void syncFromRamToFlash()
  {
    CookieManager.getInstance().flush();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/CookieSyncManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */