package android.webkit;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.ChildZygoteProcess;
import android.os.Process;
import android.os.ZygoteProcess;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;

public class WebViewZygote
{
  private static final String LOGTAG = "WebViewZygote";
  private static final String[] WEBVIEW_ZYGOTE_PROCESS_NAME = { "webview_zygote" };
  private static final Object sLock = new Object();
  @GuardedBy({"sLock"})
  private static boolean sMultiprocessEnabled = false;
  @GuardedBy({"sLock"})
  private static PackageInfo sPackage;
  @GuardedBy({"sLock"})
  private static ChildZygoteProcess sZygote;
  
  @GuardedBy({"sLock"})
  private static void connectToZygoteIfNeededLocked()
  {
    if (sZygote != null) {
      return;
    }
    Object localObject = sPackage;
    if (localObject == null)
    {
      Log.e("WebViewZygote", "Cannot connect to zygote, no package specified");
      return;
    }
    try
    {
      localObject = ((PackageInfo)localObject).applicationInfo.primaryCpuAbi;
      sZygote = Process.ZYGOTE_PROCESS.startChildZygote("com.android.internal.os.WebViewZygoteInit", "webview_zygote", 1053, 1053, null, 0, "webview_zygote", (String)localObject, TextUtils.join(",", Build.SUPPORTED_ABIS), null, 99000, Integer.MAX_VALUE);
      ZygoteProcess.waitForConnectionToZygote(sZygote.getPrimarySocketAddress());
      sZygote.preloadApp(sPackage.applicationInfo, (String)localObject);
    }
    catch (Exception localException)
    {
      Log.e("WebViewZygote", "Error connecting to webview zygote", localException);
      stopZygoteLocked();
    }
  }
  
  public static String getPackageName()
  {
    synchronized (sLock)
    {
      String str = sPackage.packageName;
      return str;
    }
  }
  
  public static ZygoteProcess getProcess()
  {
    synchronized (sLock)
    {
      if ((sZygote != null) && (isWebViewZygoteAlive()))
      {
        localChildZygoteProcess = sZygote;
        return localChildZygoteProcess;
      }
      if (sZygote != null)
      {
        Log.e("WebViewZygote", "webview_zygote is gone, need to be restarted");
        sZygote.close();
        sZygote = null;
      }
      connectToZygoteIfNeededLocked();
      ChildZygoteProcess localChildZygoteProcess = sZygote;
      return localChildZygoteProcess;
    }
  }
  
  public static boolean isMultiprocessEnabled()
  {
    synchronized (sLock)
    {
      boolean bool;
      if ((sMultiprocessEnabled) && (sPackage != null)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  private static boolean isWebViewZygoteAlive()
  {
    int[] arrayOfInt = Process.getPidsForCommands(WEBVIEW_ZYGOTE_PROCESS_NAME);
    boolean bool = true;
    if ((arrayOfInt == null) || (arrayOfInt.length < 1)) {
      bool = false;
    }
    return bool;
  }
  
  static void onWebViewProviderChanged(PackageInfo paramPackageInfo)
  {
    synchronized (sLock)
    {
      sPackage = paramPackageInfo;
      if (!sMultiprocessEnabled) {
        return;
      }
      stopZygoteLocked();
      return;
    }
  }
  
  public static void setMultiprocessEnabled(boolean paramBoolean)
  {
    synchronized (sLock)
    {
      sMultiprocessEnabled = paramBoolean;
      if (!paramBoolean) {
        stopZygoteLocked();
      }
      return;
    }
  }
  
  @GuardedBy({"sLock"})
  private static void stopZygoteLocked()
  {
    ChildZygoteProcess localChildZygoteProcess = sZygote;
    if (localChildZygoteProcess != null)
    {
      localChildZygoteProcess.close();
      Process.killProcess(sZygote.getPid());
      sZygote = null;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewZygote.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */