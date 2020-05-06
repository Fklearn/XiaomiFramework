package miui.util;

import android.content.Context;
import android.media.ExtraRingtoneManager;
import android.net.Uri;
import android.provider.MiuiSettings.System;
import android.provider.Settings.System;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;

public class SimRingtoneUtils
{
  private static boolean canSlotSettingRingtoneType(int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = bool1;
    if (paramInt != 1)
    {
      bool2 = bool1;
      if (paramInt != 8) {
        if (paramInt == 16) {
          bool2 = bool1;
        } else {
          bool2 = false;
        }
      }
    }
    return bool2;
  }
  
  public static Uri getDefaultRingtoneUri(Context paramContext, int paramInt)
  {
    return getDefaultSoundUriBySlot(paramContext, 1, paramInt);
  }
  
  public static Uri getDefaultSmsDeliveredUri(Context paramContext, int paramInt)
  {
    return getDefaultSoundUriBySlot(paramContext, 8, paramInt);
  }
  
  public static Uri getDefaultSmsReceivedUri(Context paramContext, int paramInt)
  {
    return getDefaultSoundUriBySlot(paramContext, 16, paramInt);
  }
  
  public static Uri getDefaultSoundUri(Context paramContext, int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 4)
        {
          if (paramInt != 8)
          {
            if (paramInt != 16)
            {
              if (paramInt != 32)
              {
                if (paramInt != 64)
                {
                  if (paramInt != 128)
                  {
                    if (paramInt != 256)
                    {
                      if (paramInt != 512)
                      {
                        if (paramInt != 1024)
                        {
                          if (paramInt != 2048) {
                            return null;
                          }
                          return MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_2;
                        }
                        return MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_1;
                      }
                      return MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_2;
                    }
                    return MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_1;
                  }
                  return MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_2;
                }
                return MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_1;
              }
              return null;
            }
            return MiuiSettings.System.DEFAULT_SMS_RECEIVED_RINGTONE_URI;
          }
          return MiuiSettings.System.DEFAULT_SMS_DELIVERED_RINGTONE_URI;
        }
        return Settings.System.DEFAULT_ALARM_ALERT_URI;
      }
      return Settings.System.DEFAULT_NOTIFICATION_URI;
    }
    return Settings.System.DEFAULT_RINGTONE_URI;
  }
  
  private static Uri getDefaultSoundUriBySlot(Context paramContext, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    if (paramInt2 != SubscriptionManager.INVALID_SLOT_ID)
    {
      i = paramInt1;
      if (!isDefaultSoundUniform(paramContext, paramInt1)) {
        i = getExtraRingtoneTypeBySlot(paramInt1, paramInt2);
      }
    }
    return ExtraRingtoneManager.getDefaultSoundActualUri(paramContext, i);
  }
  
  public static int getExtraRingtoneTypeBySlot(int paramInt1, int paramInt2)
  {
    if ((paramInt2 >= 0) && (paramInt2 < TelephonyManager.getDefault().getPhoneCount()))
    {
      if (paramInt1 == 1)
      {
        if (paramInt2 == 0) {
          paramInt1 = 64;
        } else {
          paramInt1 = 128;
        }
        return paramInt1;
      }
      if (paramInt1 == 8)
      {
        if (paramInt2 == 0) {
          paramInt1 = 256;
        } else {
          paramInt1 = 512;
        }
        return paramInt1;
      }
      if (paramInt1 == 16)
      {
        if (paramInt2 == 0) {
          paramInt1 = 1024;
        } else {
          paramInt1 = 2048;
        }
        return paramInt1;
      }
      return paramInt1;
    }
    return paramInt1;
  }
  
  private static String getSoundUniformSettingName(int paramInt)
  {
    if (paramInt == 1) {
      return "ringtone_sound_use_uniform";
    }
    if (paramInt == 8) {
      return "sms_delivered_sound_use_uniform";
    }
    if (paramInt == 16) {
      return "sms_received_sound_use_uniform";
    }
    return null;
  }
  
  public static boolean isDefaultSoundUniform(Context paramContext, int paramInt)
  {
    boolean bool1 = canSlotSettingRingtoneType(paramInt);
    boolean bool2 = true;
    if (bool1)
    {
      if (Settings.System.getInt(paramContext.getContentResolver(), getSoundUniformSettingName(paramInt), 1) != 1) {
        bool2 = false;
      }
      return bool2;
    }
    return true;
  }
  
  public static void setDefaultSoundUniform(Context paramContext, int paramInt, boolean paramBoolean)
  {
    if (canSlotSettingRingtoneType(paramInt)) {
      Settings.System.putInt(paramContext.getContentResolver(), getSoundUniformSettingName(paramInt), paramBoolean);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/SimRingtoneUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */