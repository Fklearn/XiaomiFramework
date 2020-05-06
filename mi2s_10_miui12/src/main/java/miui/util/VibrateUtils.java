package miui.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.VibrationEffect.Prebaked;
import android.os.Vibrator;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VibrateUtils
{
  private static String TAG = "VibrateUtils";
  private static AudioAttributes mHapticAttributes = new AudioAttributes.Builder().setUsage(13).build();
  
  public static VibrationEffect getVibrationEffect(int paramInt)
  {
    Object localObject = null;
    try
    {
      VibrationEffect localVibrationEffect = VibrationEffect.get(paramInt);
      localObject = localVibrationEffect;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.i(TAG, localIllegalArgumentException.getMessage());
    }
    return (VibrationEffect)localObject;
  }
  
  private static void invokeSetInfiniteStrength(VibrationEffect paramVibrationEffect, double paramDouble)
  {
    try
    {
      Method localMethod = VibrationEffect.Prebaked.class.getMethod("setInfiniteStrength", new Class[] { Double.TYPE });
      localMethod.setAccessible(true);
      localMethod.invoke(paramVibrationEffect, new Object[] { Double.valueOf(paramDouble) });
    }
    catch (InvocationTargetException paramVibrationEffect)
    {
      Log.e(TAG, paramVibrationEffect.getMessage());
    }
    catch (IllegalAccessException paramVibrationEffect)
    {
      Log.e(TAG, paramVibrationEffect.getMessage());
    }
    catch (NoSuchMethodException paramVibrationEffect)
    {
      Log.i(TAG, paramVibrationEffect.getMessage());
    }
  }
  
  public static void vibrate(Vibrator paramVibrator, boolean paramBoolean, long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    if (paramBoolean)
    {
      VibrationEffect localVibrationEffect = getVibrationEffect((int)paramArrayOfLong[0]);
      if (localVibrationEffect != null)
      {
        if (((localVibrationEffect instanceof VibrationEffect.Prebaked)) && (paramInt1 != paramInt2))
        {
          double d = 0.0D;
          if (paramInt1 != 0)
          {
            if (paramInt1 != 1)
            {
              if (paramInt1 == 2) {
                d = 1.0D;
              }
            }
            else {
              d = 0.625D;
            }
          }
          else {
            d = 0.375D;
          }
          invokeSetInfiniteStrength(localVibrationEffect, d);
        }
        paramVibrator.vibrate(localVibrationEffect, mHapticAttributes);
        return;
      }
    }
    if (paramArrayOfLong.length == 1) {
      paramVibrator.vibrate(paramArrayOfLong[0]);
    } else {
      paramVibrator.vibrate(paramArrayOfLong, -1);
    }
  }
  
  public static boolean vibrateExt(Vibrator paramVibrator, int paramInt)
  {
    paramVibrator.vibrate(VibrationEffect.get(paramInt), mHapticAttributes);
    return true;
  }
  
  public static boolean vibrateExt(Vibrator paramVibrator, Uri paramUri, Context paramContext)
  {
    paramUri = VibrationEffect.get(paramUri, paramContext);
    if (paramUri != null)
    {
      paramVibrator.vibrate(paramUri, mHapticAttributes);
      return true;
    }
    return false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/VibrateUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */