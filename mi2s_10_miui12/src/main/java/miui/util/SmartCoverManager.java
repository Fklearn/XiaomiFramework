package miui.util;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings.System;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.Slog;
import java.io.PrintWriter;

public class SmartCoverManager
{
  private static final String ACTION_SMART_COVER_GUIDE = "miui.intent.action.SMART_COVER_GUIDE";
  private static final boolean IS_D4;
  private static final int LID_CLOSE_SCREEN_OFF_TIMEOUT_VALUE = 15000;
  private static final boolean MULTI;
  private static final String SETTINGS_PKG = "com.android.settings";
  private static final String SMART_COVER_GUIDE_ACTIVITY = "com.android.settings.MiuiSmartCoverGuideActivity";
  private static final String SUPPORT_MULTIPLE_SMALL_WIN_COVER = "support_multiple_small_win_cover";
  private static final String TAG = "SmartCoverManager";
  private ContentResolver mContentResolver;
  private Context mContext;
  private int mCurrentUserId;
  private boolean mNeedResetTimeout = false;
  private PowerManager mPowerManager;
  private boolean mSmartCoverLidOpen = true;
  private int mSmartCoverMode;
  
  static
  {
    boolean bool = false;
    MULTI = FeatureParser.getBoolean("support_multiple_small_win_cover", false);
    if (("oxygen".equals(Build.DEVICE)) || ("oxygen".equals(Build.PRODUCT))) {
      bool = true;
    }
    IS_D4 = bool;
  }
  
  private boolean checkSmartCoverEnable()
  {
    this.mSmartCoverMode = SystemProperties.getInt("persist.sys.smartcover_mode", -1);
    if (this.mSmartCoverMode == 0)
    {
      this.mSmartCoverLidOpen = true;
      return false;
    }
    return true;
  }
  
  public static boolean deviceDisableKeysWhenLidClose()
  {
    boolean bool = true;
    if (IS_D4)
    {
      bool = false;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Device: ");
      localStringBuilder.append(Build.DEVICE);
      localStringBuilder.append(" not disable keys when LidClose.");
      Slog.i("SmartCoverManager", localStringBuilder.toString());
    }
    return bool;
  }
  
  private boolean enableInSmallWinMode(boolean paramBoolean)
  {
    int i = this.mSmartCoverMode;
    boolean bool1 = true;
    boolean bool2;
    if (2 <= i) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if ((paramBoolean) && (bool2)) {
      paramBoolean = bool1;
    } else {
      paramBoolean = false;
    }
    MiuiSettings.System.putBooleanForUser(localContentResolver, "is_small_window", paramBoolean, this.mCurrentUserId);
    return bool2;
  }
  
  private void guideSmartCoverSettingIfNeeded(boolean paramBoolean)
  {
    if ((!paramBoolean) && (MULTI))
    {
      if ((MiuiSettings.System.getBooleanForUser(this.mContentResolver, "smart_cover_key", true, 0)) && (isDeviceProvisioned(this.mContext)))
      {
        MiuiSettings.System.putBooleanForUser(this.mContentResolver, "smart_cover_key", false, 0);
        this.mContext.startActivity(new Intent("miui.intent.action.SMART_COVER_GUIDE").setComponent(new ComponentName("com.android.settings", "com.android.settings.MiuiSmartCoverGuideActivity")).setFlags(268435456));
      }
      return;
    }
  }
  
  private void handleLidSwitchChanged(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mSmartCoverLidOpen = paramBoolean1;
    if (!paramBoolean1) {
      setScreenOffByLid(true);
    }
    if (paramBoolean2) {
      this.mContext.sendBroadcastAsUser(new Intent("miui.intent.action.SMART_COVER").putExtra("is_smart_cover_open", paramBoolean1), UserHandle.CURRENT);
    }
    if (enableInSmallWinMode(paramBoolean1 ^ true)) {
      if (!paramBoolean1) {
        this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 3, 0);
      } else {
        this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 9, "lid_switch_open");
      }
    }
    updateScreenOffTimeoutIfNeeded(paramBoolean1);
  }
  
  private static boolean isDeviceProvisioned(Context paramContext)
  {
    paramContext = paramContext.getContentResolver();
    boolean bool = false;
    if (Settings.Global.getInt(paramContext, "device_provisioned", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void setScreenOffByLid(boolean paramBoolean)
  {
    String str;
    if (paramBoolean) {
      str = "true";
    } else {
      str = "false";
    }
    try
    {
      SystemProperties.set("sys.keyguard.screen_off_by_lid", str);
    }
    catch (RuntimeException localRuntimeException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Set screen off by lid:");
      localStringBuilder.append(localRuntimeException);
      Slog.e("SmartCoverManager", localStringBuilder.toString());
    }
  }
  
  private void triggerScreenOffTimeout(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContentResolver;
    int i;
    if (paramBoolean) {
      i = 15000;
    } else {
      i = Integer.MAX_VALUE;
    }
    Settings.System.putInt(localContentResolver, "screen_off_timeout", i);
    this.mNeedResetTimeout = paramBoolean;
    MiuiSettings.System.putBooleanForUser(this.mContentResolver, "need_reset_screen_off_timeout", this.mNeedResetTimeout, -2);
  }
  
  private void updateScreenOffTimeoutIfNeeded(boolean paramBoolean)
  {
    int i;
    if (2147483647L == Settings.System.getLong(this.mContentResolver, "screen_off_timeout", 15000L)) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i != 0) && (!paramBoolean)) {
      triggerScreenOffTimeout(true);
    } else if ((paramBoolean) && (this.mNeedResetTimeout)) {
      triggerScreenOffTimeout(false);
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Smart Cover Mode:");
    paramString = new StringBuilder();
    paramString.append("  mSmartCoverLidOpen=");
    paramString.append(this.mSmartCoverLidOpen);
    paramPrintWriter.println(paramString.toString());
    paramString = new StringBuilder();
    paramString.append("  mSmartCoverMode=");
    paramString.append(this.mSmartCoverMode);
    paramPrintWriter.println(paramString.toString());
  }
  
  public boolean enableLidAfterBoot(int paramInt)
  {
    checkSmartCoverEnable();
    int i = this.mSmartCoverMode;
    boolean bool1 = true;
    if (-1 == i) {
      MiuiSettings.System.setSmartCoverMode(MULTI ^ true);
    }
    boolean bool2 = false;
    boolean bool3;
    if (2 <= this.mSmartCoverMode) {
      bool3 = true;
    } else {
      bool3 = false;
    }
    if (bool3) {
      if (paramInt == 0) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
    }
    enableInSmallWinMode(bool2);
    if (MiuiSettings.System.getBooleanForUser(this.mContentResolver, "need_reset_screen_off_timeout", false, -2))
    {
      Settings.System.putInt(this.mContentResolver, "screen_off_timeout", Integer.MAX_VALUE);
      MiuiSettings.System.putBooleanForUser(this.mContentResolver, "need_reset_screen_off_timeout", false, -2);
    }
    return bool3;
  }
  
  public boolean getSmartCoverLidOpen()
  {
    return this.mSmartCoverLidOpen;
  }
  
  public int getSmartCoverMode()
  {
    return this.mSmartCoverMode;
  }
  
  public void init(Context paramContext, PowerManager paramPowerManager)
  {
    this.mContext = paramContext;
    this.mContentResolver = paramContext.getContentResolver();
    this.mPowerManager = paramPowerManager;
    this.mCurrentUserId = 0;
    checkSmartCoverEnable();
  }
  
  public boolean notifyLidSwitchChanged(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = checkSmartCoverEnable();
    guideSmartCoverSettingIfNeeded(bool);
    if (!bool) {
      return false;
    }
    handleLidSwitchChanged(paramBoolean1, paramBoolean2);
    return true;
  }
  
  public boolean notifyScreenTurningOn()
  {
    boolean bool1 = this.mSmartCoverLidOpen;
    boolean bool2 = false;
    if (bool1) {
      setScreenOffByLid(false);
    }
    bool1 = bool2;
    if (!this.mSmartCoverLidOpen)
    {
      Context localContext = this.mContext;
      bool1 = bool2;
      if (localContext != null)
      {
        bool1 = bool2;
        if (MiuiSettings.System.isInSmallWindowMode(localContext)) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public void onUserSwitch(int paramInt)
  {
    if (this.mCurrentUserId == paramInt) {
      return;
    }
    this.mCurrentUserId = paramInt;
    enableInSmallWinMode(this.mSmartCoverLidOpen ^ true);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/SmartCoverManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */