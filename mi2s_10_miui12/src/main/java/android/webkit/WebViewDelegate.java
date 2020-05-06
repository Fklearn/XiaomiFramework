package android.webkit;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.app.ActivityThread;
import android.app.Application;
import android.app.ResourcesManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RecordingCanvas;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.internal.util.ArrayUtils;

@SystemApi
public final class WebViewDelegate
{
  public void addWebViewAssetPath(Context paramContext)
  {
    String[] arrayOfString = WebViewFactory.getLoadedPackageInfo().applicationInfo.getAllApkPaths();
    ApplicationInfo localApplicationInfo = paramContext.getApplicationInfo();
    paramContext = localApplicationInfo.sharedLibraryFiles;
    int i = arrayOfString.length;
    for (int j = 0; j < i; j++) {
      paramContext = (String[])ArrayUtils.appendElement(String.class, paramContext, arrayOfString[j]);
    }
    if (paramContext != localApplicationInfo.sharedLibraryFiles)
    {
      localApplicationInfo.sharedLibraryFiles = paramContext;
      ResourcesManager.getInstance().appendLibAssetsForMainAssetPath(localApplicationInfo.getBaseResourcePath(), arrayOfString);
    }
  }
  
  @Deprecated
  public void callDrawGlFunction(Canvas paramCanvas, long paramLong)
  {
    if ((paramCanvas instanceof RecordingCanvas))
    {
      ((RecordingCanvas)paramCanvas).drawGLFunctor2(paramLong, null);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramCanvas.getClass().getName());
    localStringBuilder.append(" is not a DisplayList canvas");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  @Deprecated
  public void callDrawGlFunction(Canvas paramCanvas, long paramLong, Runnable paramRunnable)
  {
    if ((paramCanvas instanceof RecordingCanvas))
    {
      ((RecordingCanvas)paramCanvas).drawGLFunctor2(paramLong, paramRunnable);
      return;
    }
    paramRunnable = new StringBuilder();
    paramRunnable.append(paramCanvas.getClass().getName());
    paramRunnable.append(" is not a DisplayList canvas");
    throw new IllegalArgumentException(paramRunnable.toString());
  }
  
  @Deprecated
  public boolean canInvokeDrawGlFunctor(View paramView)
  {
    return true;
  }
  
  @Deprecated
  public void detachDrawGlFunctor(View paramView, long paramLong)
  {
    paramView = paramView.getViewRootImpl();
    if ((paramLong != 0L) && (paramView != null)) {
      paramView.detachFunctor(paramLong);
    }
  }
  
  public void drawWebViewFunctor(Canvas paramCanvas, int paramInt)
  {
    if ((paramCanvas instanceof RecordingCanvas))
    {
      ((RecordingCanvas)paramCanvas).drawWebViewFunctor(paramInt);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramCanvas.getClass().getName());
    localStringBuilder.append(" is not a RecordingCanvas canvas");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public Application getApplication()
  {
    return ActivityThread.currentApplication();
  }
  
  public String getDataDirectorySuffix()
  {
    return WebViewFactory.getDataDirectorySuffix();
  }
  
  public String getErrorString(Context paramContext, int paramInt)
  {
    return LegacyErrorStrings.getString(paramInt, paramContext);
  }
  
  public int getPackageId(Resources paramResources, String paramString)
  {
    paramResources = paramResources.getAssets().getAssignedPackageIdentifiers();
    for (int i = 0; i < paramResources.size(); i++) {
      if (paramString.equals((String)paramResources.valueAt(i))) {
        return paramResources.keyAt(i);
      }
    }
    paramResources = new StringBuilder();
    paramResources.append("Package not found: ");
    paramResources.append(paramString);
    throw new RuntimeException(paramResources.toString());
  }
  
  @Deprecated
  public void invokeDrawGlFunctor(View paramView, long paramLong, boolean paramBoolean)
  {
    ViewRootImpl.invokeFunctor(paramLong, paramBoolean);
  }
  
  public boolean isMultiProcessEnabled()
  {
    try
    {
      boolean bool = WebViewFactory.getUpdateService().isMultiProcessEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isTraceTagEnabled()
  {
    return Trace.isTagEnabled(16L);
  }
  
  public void setOnTraceEnabledChangeListener(final OnTraceEnabledChangeListener paramOnTraceEnabledChangeListener)
  {
    SystemProperties.addChangeCallback(new Runnable()
    {
      public void run()
      {
        paramOnTraceEnabledChangeListener.onTraceEnabledChange(WebViewDelegate.this.isTraceTagEnabled());
      }
    });
  }
  
  public static abstract interface OnTraceEnabledChangeListener
  {
    public abstract void onTraceEnabledChange(boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */