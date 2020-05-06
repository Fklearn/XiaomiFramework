package miui.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.media.AudioSystem;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.provider.MiuiSettings.AntiSpam;
import android.provider.MiuiSettings.SilenceMode;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;

public class QuietUtils
{
  private static ArrayList<String> AUTHORIZE_PACKAGE = new ArrayList(Arrays.asList(new String[] { "android", "com.android.deskclock", "com.android.providers.telephony" }));
  private static ArrayList<String> PHONE_AND_SMS_PACKAGE = new ArrayList(Arrays.asList(new String[] { "com.android.phone", "com.android.incallui", "com.android.server.telecom", "com.miui.voip", "com.android.mms" }));
  private static final String TAG = "QuietUtils";
  public static final int TYPE_AUDIO_MANAGER = 8;
  public static final int TYPE_MEDIA_PLAYER = 7;
  public static final int TYPE_NOTIFICATION = 5;
  public static final int TYPE_POWER_MANAGER = 1;
  public static final int TYPE_POWER_MANAGER_SERVICE = 3;
  public static final int TYPE_POWER_MANAGER_WAKEUP = 2;
  public static final int TYPE_SOUND_POOL = 6;
  public static final int TYPE_VIBRATOR = 4;
  public static final String ZENMODE_TYPE_ALLW_FROM = "5";
  public static final String ZENMODE_TYPE_CALL_STATUS = "3";
  public static final String ZENMODE_TYPE_EVENT_STATUS = "4";
  public static final String ZENMODE_TYPE_MESSAGE_STATUS = "2";
  public static final String ZENMODE_TYPE_STATUS = "1";
  
  private static boolean checkAuthorizePackage(Context paramContext, String paramString, boolean paramBoolean)
  {
    if (AUTHORIZE_PACKAGE.contains(paramString)) {
      return true;
    }
    return (paramBoolean) && (isTopActivity(paramContext, paramString));
  }
  
  public static boolean checkNewQuiet(int paramInt1, int paramInt2, String paramString, CharSequence paramCharSequence)
  {
    if (Build.VERSION.SDK_INT < 21) {
      return false;
    }
    paramCharSequence = ActivityThread.currentApplication();
    paramString = (PowerManager)paramCharSequence.getSystemService("power");
    paramString = paramCharSequence.getPackageName();
    int i = MiuiSettings.SilenceMode.getZenMode(paramCharSequence);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("type:");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(", flags:");
    localStringBuilder.append(paramInt2);
    localStringBuilder.append(", cpkg:");
    localStringBuilder.append(paramString);
    Log.i("QuietUtils", localStringBuilder.toString());
    if (paramInt1 != 2)
    {
      if (paramInt1 != 4)
      {
        if (paramInt1 != 6)
        {
          if ((paramInt1 == 8) && ((paramInt2 == 5) || (paramInt2 == 2)) && (checkNewZenModeEnable(paramCharSequence, paramString)) && (!"com.android.phone".equals(paramString)) && (!"com.miui.voip".equals(paramString)) && (!"android".equals(paramString)) && (AudioSystem.getDevicesForStream(2) == 2))
          {
            Log.d("QuietUtils", "speaker volume is 0");
            return true;
          }
        }
        else if (checkNewZenModeEnable(paramCharSequence, paramString)) {
          return true;
        }
      }
      else if ((checkNewZenModeEnable(paramCharSequence, paramString)) && (i == 3)) {
        return true;
      }
    }
    else if ((checkNewZenModeEnable(paramCharSequence, paramString)) && (!"android".equals(paramString)) && (!"com.android.deskclock".equals(paramString)))
    {
      paramCharSequence = new StringBuilder();
      paramCharSequence.append("POWER_MANAGER_WAKEUP pkg:");
      paramCharSequence.append(paramString);
      Log.w("QuietUtils", paramCharSequence.toString());
      return true;
    }
    return false;
  }
  
  private static boolean checkNewZenModeEnable(Context paramContext, String paramString)
  {
    return MiuiSettings.SilenceMode.isSilenceModeEnable(paramContext);
  }
  
  public static boolean checkQuiet(int paramInt1, int paramInt2, String paramString, CharSequence paramCharSequence)
  {
    if (MiuiSettings.SilenceMode.isSupported) {
      return checkNewQuiet(paramInt1, paramInt2, paramString, paramCharSequence);
    }
    Application localApplication = ActivityThread.currentApplication();
    PowerManager localPowerManager = (PowerManager)localApplication.getSystemService("power");
    Object localObject = localApplication.getPackageName();
    switch (paramInt1)
    {
    case 3: 
    default: 
      break;
    case 8: 
      if (((paramInt2 == 5) || (paramInt2 == 2)) && (!"android".equals(localObject)) && (!"com.android.systemui".equals(localObject)) && (checkZenmod(localApplication, (String)localObject)))
      {
        if (!localPowerManager.isScreenOn())
        {
          paramString = new StringBuilder();
          paramString.append("AUDIO_MANAGER pkg:");
          paramString.append((String)localObject);
          Log.w("QuietUtils", paramString.toString());
          return true;
        }
        if (!checkAuthorizePackage(localApplication, (String)localObject, true))
        {
          paramString = new StringBuilder();
          paramString.append("AUDIO_MANAGER pkg:");
          paramString.append((String)localObject);
          Log.w("QuietUtils", paramString.toString());
          return true;
        }
      }
      break;
    case 7: 
      if ((paramInt2 == 2) && (checkZenmod(localApplication, (String)localObject)))
      {
        if (!localPowerManager.isScreenOn())
        {
          paramString = new StringBuilder();
          paramString.append("MEDIA_PLAYER pkg:");
          paramString.append((String)localObject);
          Log.w("QuietUtils", paramString.toString());
          return true;
        }
        if (!checkAuthorizePackage(localApplication, (String)localObject, true))
        {
          paramString = new StringBuilder();
          paramString.append("MEDIA_PLAYER pkg:");
          paramString.append((String)localObject);
          Log.w("QuietUtils", paramString.toString());
          return true;
        }
      }
      break;
    case 6: 
      if (checkZenmod(localApplication, (String)localObject))
      {
        if (!localPowerManager.isScreenOn())
        {
          paramString = new StringBuilder();
          paramString.append("SOUND_POOL pkg:");
          paramString.append((String)localObject);
          Log.w("QuietUtils", paramString.toString());
          return true;
        }
        if (!checkAuthorizePackage(localApplication, (String)localObject, true))
        {
          paramString = new StringBuilder();
          paramString.append("SOUND_POOL pkg:");
          paramString.append((String)localObject);
          Log.w("QuietUtils", paramString.toString());
          return true;
        }
      }
      break;
    case 5: 
      if (paramCharSequence != null) {
        localObject = paramCharSequence.toString();
      } else if (paramString != null) {
        localObject = paramString;
      } else {
        localObject = "";
      }
      if (checkZenmod(localApplication, (String)localObject))
      {
        if (!localPowerManager.isScreenOn())
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("NOTIFICATION pkg:");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append(" targetPkg:");
          ((StringBuilder)localObject).append(paramCharSequence);
          Log.w("QuietUtils", ((StringBuilder)localObject).toString());
          return true;
        }
        if (paramCharSequence != null)
        {
          if (!isTopActivity(localApplication, paramCharSequence.toString()))
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("NOTIFICATION pkg:");
            ((StringBuilder)localObject).append(paramString);
            ((StringBuilder)localObject).append(" targetPkg:");
            ((StringBuilder)localObject).append(paramCharSequence);
            Log.w("QuietUtils", ((StringBuilder)localObject).toString());
            return true;
          }
        }
        else if ((paramString != null) && (!isTopActivity(localApplication, paramString.toString())))
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("NOTIFICATION pkg:");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append(" targetPkg:");
          ((StringBuilder)localObject).append(paramCharSequence);
          Log.w("QuietUtils", ((StringBuilder)localObject).toString());
          return true;
        }
      }
      break;
    case 4: 
      if ((checkZenmod(localApplication, (String)localObject)) && (!"com.android.deskclock".equals(localObject)) && (!"android".equals(localObject)) && ((!"com.android.cellbroadcastreceiver".equals(localObject)) || (!Build.checkRegion("CL"))))
      {
        if (!localPowerManager.isScreenOn())
        {
          paramString = new StringBuilder();
          paramString.append("VIBRATOR pkg:");
          paramString.append((String)localObject);
          Log.w("QuietUtils", paramString.toString());
          return true;
        }
        if (!isTopActivity(localApplication, (String)localObject))
        {
          paramString = new StringBuilder();
          paramString.append("VIBRATOR pkg:");
          paramString.append((String)localObject);
          Log.w("QuietUtils", paramString.toString());
          return true;
        }
      }
      break;
    case 2: 
      if ((checkZenmod(localApplication, (String)localObject)) && (!"android".equals(localObject)) && (!"com.android.deskclock".equals(localObject)))
      {
        paramString = new StringBuilder();
        paramString.append("POWER_MANAGER_WAKEUP pkg:");
        paramString.append((String)localObject);
        Log.w("QuietUtils", paramString.toString());
        return true;
      }
      break;
    case 1: 
      if ((((0x10000000 & paramInt2) != 0) || (paramInt2 == 26) || (paramInt2 == 10) || (paramInt2 == 6) || (paramInt2 == 1)) && (!"android".equals(localObject)) && (!"com.android.deskclock".equals(localObject)) && (!"com.google.android.talk".equals(localObject)) && (checkZenmod(localApplication, (String)localObject)))
      {
        paramString = new StringBuilder();
        paramString.append("POWER_MANAGER pkg:");
        paramString.append((String)localObject);
        Log.w("QuietUtils", paramString.toString());
        return true;
      }
      break;
    }
    return false;
  }
  
  private static boolean checkZenmod(Context paramContext, String paramString)
  {
    int i = Build.VERSION.SDK_INT;
    boolean bool1 = true;
    boolean bool2 = true;
    if (i < 21)
    {
      if ((!MiuiSettings.AntiSpam.isQuietModeEnable(paramContext)) || ("com.android.phone".equals(paramString)) || ("com.miui.voip".equals(paramString))) {
        bool2 = false;
      }
      return bool2;
    }
    if ((MiuiSettings.AntiSpam.isQuietModeEnable(paramContext)) && (!PHONE_AND_SMS_PACKAGE.contains(paramString)) && (!isZenmode(paramContext, "4"))) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    return bool2;
  }
  
  private static boolean isTopActivity(Context paramContext, String paramString)
  {
    try
    {
      paramContext = ((ActivityManager)paramContext.getSystemService("activity")).getRunningAppProcesses().iterator();
      while (paramContext.hasNext())
      {
        Object localObject = (ActivityManager.RunningAppProcessInfo)paramContext.next();
        if (((ActivityManager.RunningAppProcessInfo)localObject).importance == 100)
        {
          localObject = ((ActivityManager.RunningAppProcessInfo)localObject).pkgList;
          int i = localObject.length;
          for (int j = 0; j < i; j++)
          {
            boolean bool = localObject[j].equals(paramString);
            if (bool) {
              return true;
            }
          }
        }
      }
    }
    catch (SecurityException paramString)
    {
      paramContext = new StringBuilder();
      paramContext.append("Fail to get RunningProcessInfo:");
      paramContext.append(paramString.toString());
      Log.i("QuietUtils", paramContext.toString());
    }
    return false;
  }
  
  /* Error */
  private static boolean isZenmode(Context paramContext, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 282	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: ldc_w 284
    //   7: invokestatic 290	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   10: aload_1
    //   11: invokestatic 294	android/net/Uri:withAppendedPath	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   14: aconst_null
    //   15: aconst_null
    //   16: aconst_null
    //   17: aconst_null
    //   18: invokevirtual 300	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   21: astore_0
    //   22: aload_0
    //   23: ifnull +15 -> 38
    //   26: aload_0
    //   27: invokeinterface 305 1 0
    //   32: goto +4 -> 36
    //   35: astore_0
    //   36: iconst_1
    //   37: ireturn
    //   38: aload_0
    //   39: ifnull +68 -> 107
    //   42: aload_0
    //   43: invokeinterface 305 1 0
    //   48: goto +59 -> 107
    //   51: astore_0
    //   52: goto -4 -> 48
    //   55: astore_0
    //   56: goto +53 -> 109
    //   59: astore_0
    //   60: new 128	java/lang/StringBuilder
    //   63: astore_1
    //   64: aload_1
    //   65: invokespecial 129	java/lang/StringBuilder:<init>	()V
    //   68: aload_1
    //   69: ldc_w 307
    //   72: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: pop
    //   76: aload_1
    //   77: aload_0
    //   78: invokevirtual 308	java/lang/Exception:toString	()Ljava/lang/String;
    //   81: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: pop
    //   85: ldc 12
    //   87: aload_1
    //   88: invokevirtual 145	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: invokestatic 311	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   94: pop
    //   95: iconst_0
    //   96: ifeq +11 -> 107
    //   99: new 313	java/lang/NullPointerException
    //   102: dup
    //   103: invokespecial 314	java/lang/NullPointerException:<init>	()V
    //   106: athrow
    //   107: iconst_0
    //   108: ireturn
    //   109: iconst_0
    //   110: ifeq +12 -> 122
    //   113: new 313	java/lang/NullPointerException
    //   116: dup
    //   117: invokespecial 314	java/lang/NullPointerException:<init>	()V
    //   120: athrow
    //   121: astore_1
    //   122: aload_0
    //   123: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	124	0	paramContext	Context
    //   0	124	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   26	32	35	java/lang/Exception
    //   42	48	51	java/lang/Exception
    //   99	107	51	java/lang/Exception
    //   0	22	55	finally
    //   60	95	55	finally
    //   0	22	59	java/lang/Exception
    //   113	121	121	java/lang/Exception
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/QuietUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */