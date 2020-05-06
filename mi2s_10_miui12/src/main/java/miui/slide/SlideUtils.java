package miui.slide;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.os.Process;
import com.android.server.LocalServices;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SlideUtils
{
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {
      try
      {
        paramCloseable.close();
      }
      catch (IOException paramCloseable) {}
    }
  }
  
  public static Intent getLaunchIntentForPackageAsUser(String paramString, int paramInt)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.INFO");
    localIntent.setPackage(paramString);
    List localList1 = queryIntentActivitiesAsUser(localIntent, 0, paramInt);
    List localList2;
    if (localList1 != null)
    {
      localList2 = localList1;
      if (localList1.size() > 0) {}
    }
    else
    {
      localIntent.removeCategory("android.intent.category.INFO");
      localIntent.addCategory("android.intent.category.LAUNCHER");
      localIntent.setPackage(paramString);
      localList2 = queryIntentActivitiesAsUser(localIntent, 0, paramInt);
    }
    if ((localList2 != null) && (localList2.size() > 0))
    {
      paramString = new Intent(localIntent);
      paramString.setFlags(268435456);
      paramString.setClassName(((ResolveInfo)localList2.get(0)).activityInfo.packageName, ((ResolveInfo)localList2.get(0)).activityInfo.name);
      return paramString;
    }
    return null;
  }
  
  public static String getProcessName(Context paramContext, int paramInt)
  {
    paramContext = (ActivityManager)paramContext.getSystemService("activity");
    paramContext = paramContext.getRunningAppProcesses().iterator();
    while (paramContext.hasNext())
    {
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)paramContext.next();
      if (localRunningAppProcessInfo.pid == paramInt) {
        return localRunningAppProcessInfo.processName;
      }
    }
    return null;
  }
  
  public static List<ResolveInfo> queryIntentActivitiesAsUser(Intent paramIntent, int paramInt1, int paramInt2)
  {
    paramIntent = ((PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class)).queryIntentActivities(paramIntent, paramInt1, Process.myUid(), paramInt2);
    if (paramIntent == null) {
      return Collections.emptyList();
    }
    return paramIntent;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */