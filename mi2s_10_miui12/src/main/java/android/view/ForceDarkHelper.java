package android.view;

import android.app.IAppDarkModeObserver.Stub;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BaseCanvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.ColorSpace.Connector;
import android.graphics.ColorSpace.Named;
import android.graphics.MiuiCanvas;
import android.graphics.Paint;
import android.graphics.RenderNode;
import android.os.IBinder;
import android.os.MiuiBinderProxy;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.TextView;
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
  private boolean mIsAppDarkModeEnable = true;
  private boolean mIsDarkModeEnabled;
  private boolean mIsDarkModeSupported;
  private boolean mIsForceDarkEnabled;
  private boolean mIsInputMethod;
  private ColorSpace.Connector mLabToRgb = ColorSpace.connect(ColorSpace.get(ColorSpace.Named.CIE_LAB), ColorSpace.get(ColorSpace.Named.SRGB));
  private ColorSpace.Connector mRgbToLab = ColorSpace.connect(ColorSpace.get(ColorSpace.Named.SRGB), ColorSpace.get(ColorSpace.Named.CIE_LAB));
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
          ForceDarkHelper localForceDarkHelper = new android/view/ForceDarkHelper;
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
        str = paramContext.getPackageName();
        int i = -1;
        int j = str.hashCode();
        if (j != -796004189)
        {
          break label51;
          if (j != 270694045) {
            break label79;
          }
        }
        label51:
        while (!str.equals("com.baidu.searchbox"))
        {
          while (!str.equals("com.UCMobile")) {}
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
        String str = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("loadWebViewNightModeMethod ");
        localStringBuilder.append(paramContext.getPackageName());
        Log.e(str, localStringBuilder.toString(), localException);
      }
      return;
    }
  }
  
  public void changePaintWhenDrawBitmap(BaseCanvas paramBaseCanvas, Paint paramPaint)
  {
    paramBaseCanvas = (MiuiCanvas)paramBaseCanvas;
    if (paramBaseCanvas.isForceDarkAllowed()) {
      paramBaseCanvas.isHardwareAccelerated();
    }
  }
  
  public void changePaintWhenDrawPatch(BaseCanvas paramBaseCanvas, Paint paramPaint)
  {
    paramBaseCanvas = (MiuiCanvas)paramBaseCanvas;
    if ((paramBaseCanvas.isForceDarkAllowed()) && (!paramBaseCanvas.isHardwareAccelerated())) {
      makeDark(paramPaint);
    }
  }
  
  public void changePaintWhenDrawRect(BaseCanvas paramBaseCanvas, Paint paramPaint)
  {
    paramBaseCanvas = (MiuiCanvas)paramBaseCanvas;
    if ((paramBaseCanvas.isForceDarkAllowed()) && (!paramBaseCanvas.isHardwareAccelerated()) && (makeDark(paramPaint))) {
      paramBaseCanvas.setHasForceDark(true);
    }
  }
  
  public void changePaintWhenDrawText(BaseCanvas paramBaseCanvas, Paint paramPaint)
  {
    paramBaseCanvas = (MiuiCanvas)paramBaseCanvas;
    if ((paramBaseCanvas.isForceDarkAllowed()) && (paramBaseCanvas.hasForceDark())) {
      makeLight(paramPaint);
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
      paramLayoutParams = TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(this.mContext.getPackageName());
      localStringBuilder.append(" force enable hardware acceleration");
      Log.d(paramLayoutParams, localStringBuilder.toString());
      return true;
    }
    return bool;
  }
  
  public void initialize(Context paramContext)
  {
    SystemClock.uptimeMillis();
    this.mContext = paramContext;
    int i = paramContext.getResources().getConfiguration().uiMode;
    boolean bool1 = true;
    boolean bool2;
    if ((i & 0x30) == 32) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mIsDarkModeEnabled = bool2;
    Object localObject = paramContext.getPackageName();
    if ((!((String)localObject).equals("com.xiaomi.vipaccount")) && (!getDarkModeAppSetting((String)localObject))) {
      bool2 = false;
    } else {
      bool2 = true;
    }
    this.mIsAppDarkModeEnable = bool2;
    if ((!((String)localObject).contains("baidu.input")) && (!((String)localObject).contains(".inputmethod")) && (!((String)localObject).contains("com.tencent.qqpinyin"))) {
      bool2 = false;
    } else {
      bool2 = true;
    }
    this.mIsInputMethod = bool2;
    if (!this.mIsInputMethod)
    {
      localObject = paramContext.getResources().getAssets();
      if ((localObject instanceof AssetManager))
      {
        if ((((AssetManager)localObject).isDarkModeSupported()) && (!paramContext.getApplicationInfo().isSystemApp()) && (!paramContext.getApplicationInfo().isUpdatedSystemApp())) {
          bool2 = bool1;
        } else {
          bool2 = false;
        }
        this.mIsDarkModeSupported = bool2;
      }
    }
    loadWebViewNightModeMethod(paramContext);
  }
  
  void injectViewWhenUpdateDisplayListIfDirty(View paramView)
  {
    if ((paramView.isForceDarkAllowed()) && (this.mIsForceDarkEnabled))
    {
      String str = paramView.getContext().getPackageName();
      if ((!paramView.isOpaque()) && (!(paramView instanceof ViewGroup)) && ("tv.danmaku.bili".equals(str))) {
        paramView.mRenderNode.setForceDarkAllowed(false);
      }
      return;
    }
  }
  
  public boolean isDarkModeEnabled()
  {
    return this.mIsDarkModeEnabled;
  }
  
  public boolean isDarkModeSupported()
  {
    return this.mIsDarkModeSupported;
  }
  
  public boolean makeDark(Paint paramPaint)
  {
    if (paramPaint == null) {
      return false;
    }
    long l = paramPaint.getColorLong();
    float[] arrayOfFloat = this.mRgbToLab.transform(Color.red(l), Color.green(l), Color.blue(l));
    float f = Math.min(110.0F - arrayOfFloat[0], 100.0F);
    if (f < arrayOfFloat[0])
    {
      arrayOfFloat[0] = f;
      arrayOfFloat = this.mLabToRgb.transform(arrayOfFloat);
      paramPaint.setColor(Color.pack(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], Color.alpha(l)));
      return true;
    }
    return false;
  }
  
  public boolean makeLight(Paint paramPaint)
  {
    if (paramPaint == null) {
      return false;
    }
    long l = paramPaint.getColorLong();
    float[] arrayOfFloat = this.mRgbToLab.transform(Color.red(l), Color.green(l), Color.blue(l));
    float f = Math.min(110.0F - arrayOfFloat[0], 100.0F);
    if (f > arrayOfFloat[0])
    {
      arrayOfFloat[0] = f;
      arrayOfFloat = this.mLabToRgb.transform(arrayOfFloat);
      paramPaint.setColor(Color.pack(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], Color.alpha(l)));
      return true;
    }
    return false;
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
    if (this.mIsForceDarkEnabled != paramBoolean)
    {
      this.mIsForceDarkEnabled = paramBoolean;
      updateWebView(this.mView);
    }
  }
  
  public void updateForceDarkForCanvas(boolean paramBoolean, BaseCanvas paramBaseCanvas)
  {
    paramBaseCanvas = (MiuiCanvas)paramBaseCanvas;
    if ((this.mIsForceDarkEnabled) && (paramBoolean)) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    }
    paramBaseCanvas.setForceDarkAllowed(paramBoolean);
    paramBaseCanvas.setForceDark(this.mIsForceDarkEnabled);
  }
  
  void updateForceDarkForRenderNode(RenderNode paramRenderNode, BaseCanvas paramBaseCanvas)
  {
    if (((MiuiCanvas)paramBaseCanvas).hasForceDark()) {
      paramRenderNode.setForceDarkAllowed(false);
    }
  }
  
  public void updateForceDarkForView(View paramView)
  {
    if ((paramView.isForceDarkAllowed()) && (this.mIsForceDarkEnabled))
    {
      if (paramView.getId() != -1) {
        try
        {
          String str1 = paramView.getResources().getResourceEntryName(paramView.getId());
          if (str1 != null)
          {
            str1 = str1.toLowerCase();
            if ((!str1.contains("bg")) && (!str1.contains("background")))
            {
              if ((str1.contains("btn")) || (str1.contains("button"))) {
                paramView.mRenderNode.setUsageHint(101);
              }
            }
            else {
              paramView.mRenderNode.setUsageHint(1);
            }
          }
        }
        catch (Exception localException)
        {
          Log.e(TAG, "updateForceDarkForView fail to get view name");
        }
      }
      String str2 = paramView.getClass().getName();
      if (str2.contains("Blur")) {
        paramView.mRenderNode.setUsageHint(1);
      } else if ((!(paramView instanceof TextView)) && (!str2.contains("Button")))
      {
        if (str2.contains("LottieAnimationView")) {
          paramView.mRenderNode.setUsageHint(1);
        }
      }
      else {
        paramView.mRenderNode.setUsageHint(101);
      }
      if ((paramView.getContext().getPackageName().equals("com.sina.weibo")) && (str2.contains("SplitDraggableImageView"))) {
        paramView.mRenderNode.setForceDarkAllowed(false);
      }
      return;
    }
  }
  
  void updateForceDarkMode(ViewRootImpl paramViewRootImpl)
  {
    boolean bool1 = false;
    Context localContext = paramViewRootImpl.mContext;
    boolean bool2 = this.mIsDarkModeEnabled;
    boolean bool3 = true;
    if ((bool2) || ((localContext.getResources().getConfiguration().uiMode & 0x30) == 32))
    {
      boolean bool4 = SystemProperties.getBoolean("debug.hwui.force_dark", false);
      TypedArray localTypedArray = localContext.obtainStyledAttributes(R.styleable.Theme);
      if ((!localTypedArray.getBoolean(279, true)) && ((localContext.getApplicationInfo().isSystemApp()) || (localContext.getApplicationInfo().isUpdatedSystemApp()))) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      bool2 = bool1;
      bool1 = bool2;
      if (bool2)
      {
        if ((bool4) && ((!this.mIsDarkModeSupported) || ((localContext.getResources().getConfiguration().uiMode & 0x30) != 32))) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        bool1 = localTypedArray.getBoolean(278, bool1);
      }
      localTypedArray.recycle();
    }
    if ((bool1) && (this.mIsAppDarkModeEnable) && (!this.mIsInputMethod)) {
      bool1 = bool3;
    } else {
      bool1 = false;
    }
    this.mIsForceDarkEnabled = bool1;
    if (paramViewRootImpl.mAttachInfo.mThreadedRenderer != null) {
      paramViewRootImpl.mAttachInfo.mThreadedRenderer.setForceDark(this.mIsForceDarkEnabled);
    }
    if (paramViewRootImpl.mView != null) {
      paramViewRootImpl.invalidateWorld(paramViewRootImpl.mView);
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
          this.mBaiduWebViewSetNightModeEnabledExt.invoke(this.mBaiduWebViewGetSettingsExt.invoke(paramView, null), new Object[] { Boolean.valueOf(this.mIsForceDarkEnabled) });
        }
      }
    }
    catch (Exception paramView)
    {
      Log.e(TAG, "setWebViewNightMode ", paramView);
    }
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
  
  public static abstract interface AssetManager
  {
    public abstract boolean isDarkModeSupported();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ForceDarkHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */