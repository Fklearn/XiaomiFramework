package miui.security;

import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.IApplicationThread;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;
import android.content.pm.PackageParser.PackageParserException;
import android.miui.Shell;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import java.io.File;
import java.lang.reflect.Method;

public class SecurityManagerCompat
{
  private static final String ACTION_CANCEL_POWEROFF_ALARM = "org.codeaurora.poweroffalarm.action.CANCEL_ALARM";
  private static final String ACTION_SET_POWEROFF_ALARM = "org.codeaurora.poweroffalarm.action.SET_ALARM";
  public static final String LEADCORE = "leadcore";
  public static final String MTK = "mediatek";
  private static final String PINECONE = "pinecone";
  private static final String POWER_OFF_ALARM_PACKAGE = "com.qualcomm.qti.poweroffalarm";
  private static final int PRE_SCHEDULE_POWER_OFF_ALARM = 7;
  private static final int RTC_POWEROFF_WAKEUP_MTK = 8;
  private static final int RTC_POWEROFF_WAKEUP_QCOM_M = 5;
  private static final String TAG = "SecurityManagerCompat";
  private static final String TIME = "time";
  public static final String WAKEALARM_PATH_OF_LEADCORE = "/sys/comip/rtc_alarm";
  private static final String WAKEALARM_PATH_OF_PINECONE = "/sys/class/rtc/rtc1/wakealarm";
  public static final String WAKEALARM_PATH_OF_QCOM = "/sys/class/rtc/rtc0/wakealarm";
  
  public static void checkAppHidden(PackageManager paramPackageManager, String paramString, UserHandle paramUserHandle)
  {
    if (paramPackageManager.getApplicationHiddenSettingAsUser(paramString, paramUserHandle)) {
      paramPackageManager.setApplicationHiddenSettingAsUser(paramString, false, paramUserHandle);
    }
  }
  
  public static PackageParser createPackageParser(String paramString)
  {
    return new PackageParser();
  }
  
  public static PackageParser.Package parsePackage(PackageParser paramPackageParser, String paramString)
  {
    try
    {
      File localFile = new java/io/File;
      localFile.<init>(paramString);
      paramPackageParser = paramPackageParser.parsePackage(localFile, 0);
      return paramPackageParser;
    }
    catch (PackageParser.PackageParserException paramPackageParser)
    {
      paramPackageParser.printStackTrace();
    }
    return null;
  }
  
  private static void sendCancelBootAlarm(Context paramContext, long paramLong)
  {
    Intent localIntent = new Intent("org.codeaurora.poweroffalarm.action.CANCEL_ALARM");
    localIntent.addFlags(268435456);
    localIntent.setPackage("com.qualcomm.qti.poweroffalarm");
    localIntent.putExtra("time", 1000L * paramLong);
    paramContext.sendBroadcast(localIntent);
    Log.d("SecurityManagerCompat", "Send cancel poweroff alarm broadcast");
  }
  
  private static void sendSetBootAlarm(Context paramContext, long paramLong)
  {
    Intent localIntent = new Intent("org.codeaurora.poweroffalarm.action.SET_ALARM");
    localIntent.addFlags(268435456);
    localIntent.setPackage("com.qualcomm.qti.poweroffalarm");
    localIntent.putExtra("time", 1000L * paramLong);
    paramContext.sendBroadcast(localIntent);
    Log.d("SecurityManagerCompat", "Send set poweroff alarm broadcast");
  }
  
  public static void startActvity(Context paramContext, IApplicationThread paramIApplicationThread, IBinder paramIBinder, String paramString, Intent paramIntent)
  {
    try
    {
      ActivityManagerNative.getDefault().startActivity(paramIApplicationThread, null, paramIntent, paramIntent.resolveTypeIfNeeded(paramContext.getContentResolver()), paramIBinder, paramString, -1, 0, null, null);
    }
    catch (RemoteException paramContext) {}
  }
  
  public static void startActvityAsUser(Context paramContext, IApplicationThread paramIApplicationThread, IBinder paramIBinder, String paramString, Intent paramIntent, int paramInt)
  {
    try
    {
      IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
      paramContext = paramContext.getContentResolver();
      try
      {
        localIActivityManager.startActivityAsUser(paramIApplicationThread, null, paramIntent, paramIntent.resolveTypeIfNeeded(paramContext), paramIBinder, paramString, -1, 0, null, null, paramInt);
      }
      catch (RemoteException paramContext) {}
      return;
    }
    catch (RemoteException paramContext) {}
  }
  
  public static void writeBootTime(Context paramContext, String paramString, long paramLong)
  {
    if (paramString.equals("mediatek"))
    {
      writeMTKBootTime(paramContext, paramLong);
    }
    else if (paramString.equals("leadcore"))
    {
      Shell.write("/sys/comip/rtc_alarm", String.valueOf(paramLong));
    }
    else if (paramString.equals("pinecone"))
    {
      Shell.write("/sys/class/rtc/rtc1/wakealarm", String.valueOf(0));
      Shell.write("/sys/class/rtc/rtc1/wakealarm", String.valueOf(paramLong));
    }
    else
    {
      writeQcomBootTime(paramContext, paramLong);
    }
  }
  
  private static void writeMTKBootTime(Context paramContext, long paramLong)
  {
    AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
    paramContext = PendingIntent.getBroadcast(paramContext, 0, new Intent(), 0);
    if (paramLong == 0L)
    {
      try
      {
        paramContext = localAlarmManager.getClass().getDeclaredMethod("cancelPoweroffAlarm", new Class[] { String.class });
        paramContext.setAccessible(true);
        paramContext.invoke(localAlarmManager, new Object[] { "android" });
      }
      catch (Exception paramContext)
      {
        Log.e("SecurityManagerCompat", "cancelPoweroffAlarm: ", paramContext);
      }
    }
    else
    {
      int i;
      if (Build.VERSION.SDK_INT > 26) {
        i = 7;
      } else {
        i = 8;
      }
      localAlarmManager.set(i, 1000L * paramLong, paramContext);
    }
  }
  
  private static void writeQcomBootTime(Context paramContext, long paramLong)
  {
    if ((Build.VERSION.SDK_INT < 26) && (new File("/sys/class/rtc/rtc0/wakealarm").exists()))
    {
      Shell.write("/sys/class/rtc/rtc0/wakealarm", String.valueOf(paramLong));
      Log.d("SecurityManagerCompat", "Wake up time updated to wakealarm");
    }
    else if (Build.VERSION.SDK_INT < 26)
    {
      AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
      paramContext = PendingIntent.getBroadcast(paramContext, 0, new Intent(), 134217728);
      try
      {
        localAlarmManager.setExact(5, 1000L * paramLong, paramContext);
      }
      catch (Exception paramContext)
      {
        Log.e("SecurityManagerCompat", "alarm type 5 not supported", paramContext);
      }
    }
    else
    {
      sendCancelBootAlarm(paramContext, paramLong);
      sendSetBootAlarm(paramContext, paramLong);
      if ((Build.VERSION.SDK_INT > 27) && (paramLong == 0L)) {
        sendCancelBootAlarm(paramContext, paramLong);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/SecurityManagerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */