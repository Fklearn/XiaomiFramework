package miui.util;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.provider.MiuiSettings.SilenceMode;
import android.provider.MiuiSettings.System;
import android.provider.Settings.System;

public class AudioManagerHelper
{
  public static final int FLAG_ONLY_SET_VOLUME = 1048576;
  public static final int FLAG_SHOW_UI_WARNINGS = 1024;
  
  public static int getHiFiVolume(Context paramContext)
  {
    try
    {
      int i = Integer.valueOf(((AudioManager)paramContext.getSystemService("audio")).getParameters("hifi_volume").replace("hifi_volume=", "")).intValue();
      return i;
    }
    catch (Exception paramContext) {}
    return 0;
  }
  
  public static int getNewValidatedRingerModeForUser(Context paramContext, int paramInt1, int paramInt2)
  {
    paramInt2 = paramInt1;
    if (paramInt1 == 1) {
      paramInt2 = 0;
    }
    return paramInt2;
  }
  
  public static int getValidatedRingerMode(Context paramContext, int paramInt)
  {
    return getValidatedRingerModeForUser(paramContext, paramInt, -3);
  }
  
  public static int getValidatedRingerModeForUser(Context paramContext, int paramInt1, int paramInt2)
  {
    if (MiuiSettings.SilenceMode.isSupported) {
      return getNewValidatedRingerModeForUser(paramContext, paramInt1, paramInt2);
    }
    boolean bool = isVibrateEnabledForUser(paramContext, paramInt1, paramInt2);
    if (paramInt1 == 0)
    {
      if (bool) {
        return 1;
      }
    }
    else if ((1 == paramInt1) && (!bool)) {
      return 0;
    }
    return paramInt1;
  }
  
  public static boolean isHiFiMode(Context paramContext)
  {
    paramContext = (AudioManager)paramContext.getSystemService("audio");
    boolean bool1 = paramContext.isWiredHeadsetOn();
    boolean bool2 = paramContext.getParameters("hifi_mode").contains("true");
    if ((bool1) && (bool2)) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    return bool1;
  }
  
  public static boolean isNewSilentEnabled(Context paramContext)
  {
    boolean bool;
    if (MiuiSettings.SilenceMode.getZenMode(paramContext) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNewVibrateEnabled(Context paramContext)
  {
    return isVibrateEnabled(paramContext, MiuiSettings.SilenceMode.getZenMode(paramContext));
  }
  
  public static boolean isNewVibrateEnabledForUser(Context paramContext, int paramInt)
  {
    return isVibrateEnabledForUser(paramContext, MiuiSettings.SilenceMode.getZenMode(paramContext), paramInt);
  }
  
  public static boolean isSilentEnabled(Context paramContext)
  {
    if (MiuiSettings.SilenceMode.isSupported) {
      return isNewSilentEnabled(paramContext);
    }
    boolean bool;
    if (((AudioManager)paramContext.getSystemService("audio")).getRingerMode() != 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isVibrateEnabled(Context paramContext)
  {
    if (MiuiSettings.SilenceMode.isSupported) {
      return isNewVibrateEnabled(paramContext);
    }
    return isVibrateEnabled(paramContext, ((AudioManager)paramContext.getSystemService("audio")).getRingerMode());
  }
  
  public static boolean isVibrateEnabled(Context paramContext, int paramInt)
  {
    return isVibrateEnabledForUser(paramContext, paramInt, -3);
  }
  
  public static boolean isVibrateEnabledForUser(Context paramContext, int paramInt)
  {
    if (MiuiSettings.SilenceMode.isSupported) {
      return isNewVibrateEnabledForUser(paramContext, paramInt);
    }
    return isVibrateEnabledForUser(paramContext, ((AudioManager)paramContext.getSystemService("audio")).getRingerMode(), paramInt);
  }
  
  public static boolean isVibrateEnabledForUser(Context paramContext, int paramInt1, int paramInt2)
  {
    boolean bool1 = ((Vibrator)paramContext.getSystemService("vibrator")).hasVibrator();
    boolean bool2 = MiuiSettings.SilenceMode.isSupported;
    boolean bool3 = false;
    boolean bool4 = false;
    int i;
    if (bool2) {
      i = 0;
    } else {
      i = 2;
    }
    if (i != paramInt1)
    {
      bool2 = bool4;
      if (bool1)
      {
        bool2 = bool4;
        if (Settings.System.getIntForUser(paramContext.getContentResolver(), "vibrate_in_silent", 1, paramInt2) == 1) {
          bool2 = true;
        }
      }
      return bool2;
    }
    bool2 = bool3;
    if (bool1)
    {
      bool2 = bool3;
      if (Settings.System.getIntForUser(paramContext.getContentResolver(), "vibrate_in_normal", MiuiSettings.System.VIBRATE_IN_NORMAL_DEFAULT, paramInt2) == 1) {
        bool2 = true;
      }
    }
    return bool2;
  }
  
  public static void newToggleSilentForUser(Context paramContext, int paramInt1, int paramInt2)
  {
    if (isSilentEnabled(paramContext)) {
      paramInt1 = 0;
    } else {
      paramInt1 = MiuiSettings.SilenceMode.getLastestQuietMode(paramContext);
    }
    MiuiSettings.SilenceMode.setSilenceMode(paramContext, paramInt1, null);
  }
  
  public static void setHiFiVolume(Context paramContext, int paramInt)
  {
    paramContext = (AudioManager)paramContext.getSystemService("audio");
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("hifi_volume=");
    localStringBuilder.append(paramInt);
    paramContext.setParameters(localStringBuilder.toString());
  }
  
  public static void setVibrateSetting(Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    setVibrateSettingForUser(paramContext, paramBoolean1, paramBoolean2, -3);
  }
  
  public static void setVibrateSettingForUser(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    String str;
    if (paramBoolean2) {
      str = "vibrate_in_silent";
    } else {
      str = "vibrate_in_normal";
    }
    Settings.System.putIntForUser(localContentResolver, str, paramBoolean1, paramInt);
    if (MiuiSettings.SilenceMode.isSupported) {
      validateRingerMode(paramContext, paramBoolean1, paramBoolean2);
    } else {
      validateRingerMode(paramContext, paramInt);
    }
    syncVibrateWhenRinging(paramContext, paramBoolean1, paramBoolean2, paramInt);
  }
  
  private static void syncVibrateWhenRinging(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    if (paramBoolean2) {
      return;
    }
    if (Build.VERSION.SDK_INT >= 23) {
      Settings.System.putIntForUser(paramContext.getContentResolver(), "vibrate_when_ringing", paramBoolean1, paramInt);
    }
  }
  
  public static void toggleSilent(Context paramContext, int paramInt)
  {
    toggleSilentForUser(paramContext, paramInt, -3);
  }
  
  public static void toggleSilentForUser(Context paramContext, int paramInt1, int paramInt2)
  {
    if (MiuiSettings.SilenceMode.isSupported)
    {
      newToggleSilentForUser(paramContext, paramInt1, paramInt2);
      return;
    }
    AudioManager localAudioManager = (AudioManager)paramContext.getSystemService("audio");
    if (2 == localAudioManager.getRingerMode())
    {
      if (isVibrateEnabledForUser(paramContext, 0, paramInt2)) {
        paramInt2 = 1;
      } else {
        paramInt2 = 0;
      }
    }
    else {
      paramInt2 = 2;
    }
    localAudioManager.setRingerMode(paramInt2);
    if (paramInt1 != 0) {
      localAudioManager.adjustStreamVolume(2, 0, paramInt1);
    }
  }
  
  public static void toggleVibrateSetting(Context paramContext)
  {
    toggleVibrateSettingForUser(paramContext, -3);
  }
  
  public static void toggleVibrateSettingForUser(Context paramContext, int paramInt)
  {
    setVibrateSettingForUser(paramContext, isVibrateEnabledForUser(paramContext, paramInt) ^ true, isSilentEnabled(paramContext), paramInt);
  }
  
  private static void validateRingerMode(Context paramContext, int paramInt)
  {
    AudioManager localAudioManager = (AudioManager)paramContext.getSystemService("audio");
    int i = localAudioManager.getRingerMode();
    paramInt = getValidatedRingerModeForUser(paramContext, i, paramInt);
    if (i != paramInt) {
      localAudioManager.setRingerMode(paramInt);
    }
  }
  
  private static void validateRingerMode(Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean2) {
      return;
    }
    if (MiuiSettings.SilenceMode.getZenMode(paramContext) == 4) {
      ((AudioManager)paramContext.getSystemService("audio")).setRingerMode(paramBoolean1);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/AudioManagerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */