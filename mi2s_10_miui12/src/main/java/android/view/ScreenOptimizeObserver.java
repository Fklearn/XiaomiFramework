package android.view;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.MiuiSettings.ScreenEffect;
import android.provider.Settings.System;
import android.util.Slog;

public class ScreenOptimizeObserver
  extends ContentObserver
{
  private static final String TAG = "ScreenOptimizeObserver";
  private static final int WCG_MODE = 1;
  private Callback mCallback;
  private Context mContext;
  private ThreadedRenderer mThreadedRenderer;
  
  public ScreenOptimizeObserver(Context paramContext, Handler paramHandler, Callback paramCallback)
  {
    super(paramHandler);
    this.mContext = paramContext;
    this.mCallback = paramCallback;
    paramContext = this.mContext;
    this.mThreadedRenderer = ThreadedRenderer.create(paramContext, false, paramContext.getPackageName());
    this.mCallback.screenOptimizeSettingsChanged(this.mThreadedRenderer, isWCGEnabled() ^ true);
    register();
  }
  
  public Callback getCallback()
  {
    return this.mCallback;
  }
  
  public int getColorSpaceMode()
  {
    return Settings.System.getInt(this.mContext.getContentResolver(), "color_space_mode", 0);
  }
  
  public int getScreenMode()
  {
    int i;
    if ((MiuiSettings.ScreenEffect.SCREEN_EFFECT_SUPPORTED & 0x1) != 0) {
      i = 1;
    } else {
      i = 2;
    }
    return Settings.System.getInt(this.mContext.getContentResolver(), "screen_optimize_mode", i);
  }
  
  public boolean isWCGEnabled()
  {
    int i = getScreenMode();
    boolean bool = true;
    if (i != 3)
    {
      if (i != 4) {
        return false;
      }
      if (getColorSpaceMode() != 1) {
        bool = false;
      }
      return bool;
    }
    return true;
  }
  
  public void onChange(boolean paramBoolean)
  {
    Callback localCallback = getCallback();
    if (localCallback != null) {
      localCallback.screenOptimizeSettingsChanged(this.mThreadedRenderer, isWCGEnabled() ^ true);
    }
  }
  
  public void register()
  {
    Slog.d("ScreenOptimizeObserver", "register");
    Uri localUri1 = Settings.System.getUriFor("screen_optimize_mode");
    Uri localUri2 = Settings.System.getUriFor("color_space_mode");
    this.mContext.getContentResolver().registerContentObserver(localUri1, false, this);
    this.mContext.getContentResolver().registerContentObserver(localUri2, false, this);
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void unregister()
  {
    Slog.d("ScreenOptimizeObserver", "unregister");
    this.mContext.getContentResolver().unregisterContentObserver(this);
  }
  
  public static abstract interface Callback
  {
    public abstract void screenOptimizeSettingsChanged(ThreadedRenderer paramThreadedRenderer, boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ScreenOptimizeObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */