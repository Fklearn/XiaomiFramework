package android.webkit;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.Handler;

@Deprecated
abstract class WebSyncManager
  implements Runnable
{
  protected static final String LOGTAG = "websync";
  protected WebViewDatabase mDataBase;
  @UnsupportedAppUsage
  protected Handler mHandler;
  
  protected WebSyncManager(Context paramContext, String paramString) {}
  
  protected Object clone()
    throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException("doesn't implement Cloneable");
  }
  
  protected void onSyncInit() {}
  
  public void resetSync() {}
  
  public void run() {}
  
  public void startSync() {}
  
  public void stopSync() {}
  
  public void sync() {}
  
  @UnsupportedAppUsage
  abstract void syncFromRamToFlash();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebSyncManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */