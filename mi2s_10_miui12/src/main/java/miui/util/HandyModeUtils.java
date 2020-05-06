package miui.util;

import android.content.ContentResolver;
import android.content.Context;
import android.os.SystemProperties;
import android.provider.MiuiSettings.System;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Display;
import android.view.WindowManager;
import miui.os.Build;

public class HandyModeUtils
{
  public static final boolean DEFAULT_IS_ENTER_DIRECT = false;
  static boolean SUPPORTED = SystemProperties.getBoolean("ro.miui.has_handy_mode_sf", false);
  private static volatile HandyModeUtils sInstance;
  private Context mContext;
  private float mScreenSize;
  
  private HandyModeUtils(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    if (this.mContext == null) {
      this.mContext = paramContext;
    }
    this.mScreenSize = getScreenSize();
  }
  
  private float calcScreenSizeToScale(float paramFloat)
  {
    return paramFloat / this.mScreenSize;
  }
  
  private float getDefaultScreenSize()
  {
    float f;
    if (getScreenSize() > 4.5F) {
      f = 4.0F;
    } else {
      f = 3.5F;
    }
    return f;
  }
  
  public static HandyModeUtils getInstance(Context paramContext)
  {
    if (sInstance == null) {
      try
      {
        if (sInstance == null)
        {
          HandyModeUtils localHandyModeUtils = new miui/util/HandyModeUtils;
          localHandyModeUtils.<init>(paramContext);
          sInstance = localHandyModeUtils;
        }
      }
      finally {}
    }
    return sInstance;
  }
  
  private float getScreenSize()
  {
    WindowManager localWindowManager = (WindowManager)this.mContext.getSystemService("window");
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
    float f1 = localDisplayMetrics.widthPixels / localDisplayMetrics.xdpi;
    float f2 = localDisplayMetrics.heightPixels / localDisplayMetrics.ydpi;
    return FloatMath.sqrt(f1 * f1 + f2 * f2);
  }
  
  public static boolean isFeatureVisible()
  {
    boolean bool;
    if ((SUPPORTED) && (!Build.IS_TABLET)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public float getScale()
  {
    return calcScreenSizeToScale(getSize());
  }
  
  public float getSize()
  {
    float f1 = getDefaultScreenSize();
    float f2 = Settings.System.getFloatForUser(this.mContext.getContentResolver(), "handy_mode_size", f1, 0);
    float f3 = f2;
    if (!isValidSize(f2)) {
      f3 = f1;
    }
    return f3;
  }
  
  public boolean hasShowed()
  {
    return true;
  }
  
  public boolean isEnable()
  {
    boolean bool;
    if ((isFeatureVisible()) && (isHandyModeEnabled())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isEnterDirect()
  {
    return true;
  }
  
  public boolean isHandyModeEnabled()
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    boolean bool = false;
    if (Settings.System.getIntForUser(localContentResolver, "handy_mode_state", 0, 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isValidSize(float paramFloat)
  {
    boolean bool;
    if (calcScreenSizeToScale(paramFloat) < 0.88F) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void setEnterDirect(boolean paramBoolean)
  {
    MiuiSettings.System.putBoolean(this.mContext.getContentResolver(), "handy_mode_enter_direct", paramBoolean);
  }
  
  public void setHandyModeStateToSettings(boolean paramBoolean)
  {
    MiuiSettings.System.putBooleanForUser(this.mContext.getContentResolver(), "handy_mode_state", paramBoolean, 0);
  }
  
  public void setSize(float paramFloat)
  {
    Settings.System.putFloatForUser(this.mContext.getContentResolver(), "handy_mode_size", paramFloat, 0);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/HandyModeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */