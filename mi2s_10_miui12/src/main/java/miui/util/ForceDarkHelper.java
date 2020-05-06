package miui.util;

import android.app.IAppDarkModeObserver.Stub;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.IBinder;
import android.os.MiuiBinderProxy;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import com.android.internal.R.styleable;
import java.lang.reflect.Method;
import miui.security.ISecurityManager;
import miui.security.ISecurityManager.Stub;

public class ForceDarkHelper
{
  private static final boolean DEBUG = false;
  private static String TAG = "ForceDarkHelper";
  private static volatile ForceDarkHelper sInstance;
  private boolean mAppDarkModeObserverRegisted;
  private Method mBaiduWebViewGetSettingsExt;
  private Method mBaiduWebViewSetNightModeEnabledExt;
  private Context mContext;
  private boolean mForceDark;
  private boolean mIsAppDarkModeEnable;
  private boolean mIsDarkModeEnabled;
  private boolean mIsDarkModeSupported;
  private Method mTencentWebViewMethod;
  private Method mUCWebViewMethod;
  private View mView;
  
  private boolean getDarkModeAppSetting(String paramString)
  {
    IBinder localIBinder = ServiceManager.getService("security");
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (localIBinder != null) {
      try
      {
        bool2 = ISecurityManager.Stub.asInterface(localIBinder).getAppDarkMode(paramString);
      }
      catch (Exception paramString)
      {
        Log.e(TAG, "start window getDarkModeAppSetting error: ", paramString);
        bool2 = bool1;
      }
    }
    return bool2;
  }
  
  public static ForceDarkHelper getInstance()
  {
    if (sInstance == null) {
      try
      {
        if (sInstance == null)
        {
          ForceDarkHelper localForceDarkHelper = new miui/util/ForceDarkHelper;
          localForceDarkHelper.<init>();
          sInstance = localForceDarkHelper;
        }
      }
      finally {}
    }
    return sInstance;
  }
  
  private void loadWebViewNightModeMethod(Context paramContext)
  {
    if ((this.mUCWebViewMethod == null) && ((this.mBaiduWebViewGetSettingsExt == null) || (this.mBaiduWebViewSetNightModeEnabledExt == null)))
    {
      try
      {
        String str1 = paramContext.getPackageName();
        int i = -1;
        int j = str1.hashCode();
        if (j != -796004189)
        {
          break label51;
          if (j != 270694045) {
            break label79;
          }
        }
        label51:
        while (!str1.equals("com.baidu.searchbox"))
        {
          while (!str1.equals("com.UCMobile")) {}
          i = 1;
          break;
        }
        i = 0;
        label79:
        if (i != 0)
        {
          if (i == 1) {
            this.mUCWebViewMethod = Class.forName("com.uc.webview.export.extension.UCSettings", true, paramContext.getClassLoader()).getMethod("setGlobalBoolValue", new Class[] { String.class, Boolean.TYPE });
          }
        }
        else
        {
          this.mBaiduWebViewGetSettingsExt = Class.forName("com.baidu.browser.sailor.BdSailorWebView", true, paramContext.getClassLoader()).getMethod("getSettingsExt", null);
          this.mBaiduWebViewSetNightModeEnabledExt = Class.forName("com.baidu.browser.sailor.ISailorWebSettingsExt", true, paramContext.getClassLoader()).getMethod("setNightModeEnabledExt", new Class[] { Boolean.TYPE });
        }
      }
      catch (Exception localException)
      {
        String str2 = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("loadWebViewNightModeMethod ");
        localStringBuilder.append(paramContext.getPackageName());
        Log.e(str2, localStringBuilder.toString(), localException);
      }
      return;
    }
  }
  
  public boolean enableHardwareAccelerationIfNeeded(WindowManager.LayoutParams paramLayoutParams)
  {
    boolean bool;
    if ((paramLayoutParams.flags & 0x1000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    if ((!bool) && (this.mIsDarkModeEnabled) && (SystemProperties.getBoolean("debug.hwui.force_dark", false)) && (!this.mIsDarkModeSupported) && (!this.mContext.getApplicationInfo().isSystemApp()) && (!this.mContext.getApplicationInfo().isUpdatedSystemApp()))
    {
      String str = TAG;
      paramLayoutParams = new StringBuilder();
      paramLayoutParams.append(this.mContext.getPackageName());
      paramLayoutParams.append(" force enable hardware acceleration");
      Log.d(str, paramLayoutParams.toString());
      return true;
    }
    return bool;
  }
  
  public void initialize(Context paramContext)
  {
    SystemClock.uptimeMillis();
    this.mContext = paramContext;
    int i = paramContext.getResources().getConfiguration().uiMode;
    boolean bool1 = false;
    boolean bool2;
    if ((i & 0x30) == 32) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mIsDarkModeEnabled = bool2;
    this.mIsAppDarkModeEnable = getDarkModeAppSetting(paramContext.getPackageName());
    int j = 0;
    i = 0;
    Configuration[] arrayOfConfiguration = paramContext.getResources().getSizeConfigurations();
    if (arrayOfConfiguration != null)
    {
      int k = arrayOfConfiguration.length - 1;
      for (;;)
      {
        j = i;
        if (k < 0) {
          break;
        }
        j = i;
        if ((arrayOfConfiguration[k].uiMode & 0x30) == 32) {
          j = i + 1;
        }
        k--;
        i = j;
      }
    }
    if ((j >= 2) && (!paramContext.getApplicationInfo().isSystemApp()) && (!paramContext.getApplicationInfo().isUpdatedSystemApp())) {
      bool2 = true;
    } else {
      bool2 = bool1;
    }
    this.mIsDarkModeSupported = bool2;
    loadWebViewNightModeMethod(paramContext);
  }
  
  public boolean isDarkModeEnabled()
  {
    return this.mIsDarkModeEnabled;
  }
  
  public boolean isDarkModeSupported()
  {
    return this.mIsDarkModeSupported;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    boolean bool;
    if ((paramConfiguration.uiMode & 0x30) == 32) {
      bool = true;
    } else {
      bool = false;
    }
    this.mIsDarkModeEnabled = bool;
  }
  
  public void registAppDarkModeObserver(Context paramContext)
  {
    if (this.mAppDarkModeObserverRegisted) {
      return;
    }
    new MiuiBinderProxy(ServiceManager.getService("uimode"), "android.app.IUiModeManager").callOneWayTransact(16777213, new Object[] { new AppDarkModeObserver(null).asBinder(), paramContext.getPackageName(), paramContext.getApplicationInfo().processName, Integer.valueOf(paramContext.getUserId()) });
    this.mAppDarkModeObserverRegisted = true;
  }
  
  public void updateForceDark(boolean paramBoolean)
  {
    if (this.mForceDark != paramBoolean)
    {
      this.mForceDark = paramBoolean;
      updateWebView(this.mView);
    }
  }
  
  public void updateWebView(View paramView)
  {
    if (((this.mUCWebViewMethod == null) && (this.mBaiduWebViewGetSettingsExt == null) && (this.mBaiduWebViewSetNightModeEnabledExt == null)) || (paramView == null)) {
      return;
    }
    SystemClock.uptimeMillis();
    try
    {
      Object localObject = paramView.getClass().getSuperclass();
      if ((localObject != null) && (("com.uc.webview.browser.BrowserWebView".equals(((Class)localObject).getName())) || ("com.uc.webview.export.WebView".equals(((Class)localObject).getName())) || ("com.baidu.browser.sailor.BdSailorWebView".equals(((Class)localObject).getName()))))
      {
        this.mView = paramView;
        localObject = this.mUCWebViewMethod;
        if ((this.mBaiduWebViewGetSettingsExt != null) && (this.mBaiduWebViewSetNightModeEnabledExt != null)) {
          this.mBaiduWebViewSetNightModeEnabledExt.invoke(this.mBaiduWebViewGetSettingsExt.invoke(paramView, null), new Object[] { Boolean.valueOf(this.mForceDark) });
        }
      }
    }
    catch (Exception paramView)
    {
      Log.e(TAG, "setWebViewNightMode ", paramView);
    }
  }
  
  public boolean useForceDark(Context paramContext)
  {
    boolean bool1 = false;
    boolean bool2 = this.mIsDarkModeEnabled;
    boolean bool3 = true;
    if ((bool2) || ((paramContext.getResources().getConfiguration().uiMode & 0x30) == 32))
    {
      boolean bool4 = SystemProperties.getBoolean("debug.hwui.force_dark", false);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(R.styleable.Theme);
      if ((!localTypedArray.getBoolean(279, true)) && ((paramContext.getApplicationInfo().isSystemApp()) || (paramContext.getApplicationInfo().isUpdatedSystemApp()))) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      bool2 = bool1;
      bool1 = bool2;
      if (bool2)
      {
        if ((bool4) && ((!this.mIsDarkModeSupported) || ((paramContext.getResources().getConfiguration().uiMode & 0x30) != 32))) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        bool1 = localTypedArray.getBoolean(278, bool1);
      }
      localTypedArray.recycle();
    }
    if ((bool1) && (this.mIsAppDarkModeEnable)) {
      bool1 = bool3;
    } else {
      bool1 = false;
    }
    return bool1;
  }
  
  private class AppDarkModeObserver
    extends IAppDarkModeObserver.Stub
  {
    private AppDarkModeObserver() {}
    
    public void onAppDarkModeChanged(boolean paramBoolean)
      throws RemoteException
    {
      String str = ForceDarkHelper.TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(ForceDarkHelper.this.mContext.getPackageName());
      localStringBuilder.append(" ");
      localStringBuilder.append(ForceDarkHelper.this.mContext.getUserId());
      localStringBuilder.append(" onAppDarkModeChanged ");
      localStringBuilder.append(paramBoolean);
      Log.d(str, localStringBuilder.toString());
      ForceDarkHelper.access$202(ForceDarkHelper.this, paramBoolean);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/ForceDarkHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */