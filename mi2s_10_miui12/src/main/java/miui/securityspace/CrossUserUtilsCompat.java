package miui.securityspace;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.IUserSwitchObserver;
import android.app.MiuiThemeHelper;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import java.util.Iterator;
import java.util.List;

public class CrossUserUtilsCompat
{
  public static final int FLAG_XSPACE_PROFILE = 8388608;
  public static final int OWNER_SHARED_USER_GID = UserHandle.getUserGid(0);
  private static final String TAG = "CrossUserUtilsCompat";
  public static final int XSPACE_SHARED_USER_GID = UserHandle.getUserGid(999);
  
  public static Uri addUserIdForUri(Uri paramUri, int paramInt)
  {
    if (paramInt != -1) {
      paramUri = ContentProvider.maybeAddUserId(paramUri, paramInt);
    }
    return paramUri;
  }
  
  public static Uri addUserIdForUri(Uri paramUri, Context paramContext, String paramString, Intent paramIntent)
  {
    int i = paramIntent.getIntExtra("android.intent.extra.picked_user_id", -1);
    if ((i != -1) && (checkUidPermission(paramContext, paramString))) {
      return addUserIdForUri(paramUri, i);
    }
    return paramUri;
  }
  
  public static boolean checkUidPermission(Context paramContext, String paramString)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getApplicationInfo(paramString, 0);
      if (paramContext != null)
      {
        int i = UserHandle.getAppId(paramContext.uid);
        return i <= 1000;
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext.printStackTrace();
    }
    catch (NullPointerException paramContext)
    {
      paramContext.printStackTrace();
    }
    return false;
  }
  
  public static Drawable getOriginalAppIcon(Context paramContext, String paramString)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(paramString, 0);
      paramString = MiuiThemeHelper.getDrawable(localPackageManager, paramString, localApplicationInfo.name, localApplicationInfo.icon, localApplicationInfo);
      paramContext = paramString;
      if (paramString == null) {
        paramContext = localPackageManager.loadUnbadgedItemIcon(localApplicationInfo, localApplicationInfo);
      }
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  public static Drawable getXSpaceIcon(Context paramContext, Drawable paramDrawable, UserHandle paramUserHandle)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    Drawable localDrawable = paramDrawable;
    if ((paramDrawable instanceof BitmapDrawable)) {
      localDrawable = CrossUserUtils.createDrawableWithCache(paramContext, ((BitmapDrawable)paramDrawable).getBitmap());
    }
    return localPackageManager.getUserBadgedIcon(localDrawable, paramUserHandle);
  }
  
  static boolean hasSecondSpace(Context paramContext)
  {
    boolean bool;
    if (Settings.Secure.getInt(paramContext.getContentResolver(), "second_user_id", 55536) != 55536) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static boolean hasXSpaceUser(Context paramContext)
  {
    paramContext = ((UserManager)paramContext.getSystemService("user")).getProfiles(0).iterator();
    while (paramContext.hasNext()) {
      if (XSpaceUserHandle.isXSpaceUser((UserInfo)paramContext.next())) {
        return true;
      }
    }
    return false;
  }
  
  public static void registerUserSwitchObserver(IUserSwitchObserver paramIUserSwitchObserver, String paramString)
    throws RemoteException
  {
    ActivityManager.getService().registerUserSwitchObserver(paramIUserSwitchObserver, paramString);
  }
  
  /* Error */
  public static void startActivityAsCaller(android.app.Activity paramActivity, Intent paramIntent, android.os.Bundle paramBundle, boolean paramBoolean, int paramInt)
  {
    // Byte code:
    //   0: invokestatic 190	android/os/StrictMode:disableDeathOnFileUriExposure	()V
    //   3: aload_0
    //   4: aload_1
    //   5: aload_2
    //   6: aconst_null
    //   7: iload_3
    //   8: iload 4
    //   10: invokevirtual 195	android/app/Activity:startActivityAsCaller	(Landroid/content/Intent;Landroid/os/Bundle;Landroid/os/IBinder;ZI)V
    //   13: invokestatic 198	android/os/StrictMode:enableDeathOnFileUriExposure	()V
    //   16: goto +42 -> 58
    //   19: astore_0
    //   20: goto +39 -> 59
    //   23: astore_1
    //   24: new 200	java/lang/StringBuilder
    //   27: astore_0
    //   28: aload_0
    //   29: invokespecial 201	java/lang/StringBuilder:<init>	()V
    //   32: aload_0
    //   33: ldc -53
    //   35: invokevirtual 207	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: pop
    //   39: aload_0
    //   40: aload_1
    //   41: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   44: pop
    //   45: ldc 12
    //   47: aload_0
    //   48: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   51: invokestatic 220	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   54: pop
    //   55: goto -42 -> 13
    //   58: return
    //   59: invokestatic 198	android/os/StrictMode:enableDeathOnFileUriExposure	()V
    //   62: aload_0
    //   63: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	64	0	paramActivity	android.app.Activity
    //   0	64	1	paramIntent	Intent
    //   0	64	2	paramBundle	android.os.Bundle
    //   0	64	3	paramBoolean	boolean
    //   0	64	4	paramInt	int
    // Exception table:
    //   from	to	target	type
    //   3	13	19	finally
    //   24	55	19	finally
    //   3	13	23	android/content/ActivityNotFoundException
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securityspace/CrossUserUtilsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */