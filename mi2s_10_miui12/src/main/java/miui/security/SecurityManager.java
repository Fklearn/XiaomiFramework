package miui.security;

import android.app.Activity;
import android.app.AppGlobals;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.miui.AppOpsUtils;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IWakePathCallback;
import java.lang.reflect.Method;
import java.util.List;
import miui.os.Build;
import miui.securityspace.XSpaceUserHandle;
import miui.util.FeatureParser;

public class SecurityManager
{
  public static final int FLAG_AC_ENABLED = 1;
  public static final int FLAG_AC_PACKAGE_CANCELED = 8;
  public static final int FLAG_AC_PACKAGE_ENABLED = 2;
  public static final int FLAG_AC_PACKAGE_PASSED = 4;
  public static final int MODE_EACH = 0;
  public static final int MODE_LOCK_SCREEN = 1;
  public static final int MODE_TIME_OUT = 2;
  private static final String PACKAGE_SECURITYCENTER = "com.miui.securitycenter";
  public static final String SKIP_INTERCEPT = "skip_interception";
  public static final String SKIP_INTERCEPT_ACTIVITY_GALLERY_EDIT = "com.miui.gallery.editor.photo.screen.home.ScreenEditorActivity";
  public static final String SKIP_INTERCEPT_ACTIVITY_GALLERY_EXTRA = "com.miui.gallery.activity.ExternalPhotoPageActivity";
  public static final String SKIP_INTERCEPT_PACKAGE = "com.miui.gallery";
  private static final String START_ACTIVITY_CALLEE_PKGNAME = "CalleePkgName";
  private static final String START_ACTIVITY_CALLER_PKGNAME = "CallerPkgName";
  private static final String START_ACTIVITY_CALLER_UID = "callerUserId";
  private static final String START_ACTIVITY_USERID = "UserId";
  private static final String TAG = "SecurityManager";
  private static Method sActivityFinishMethod;
  private final ISecurityManager mService;
  
  static
  {
    if (Build.VERSION.SDK_INT > 19) {
      try
      {
        int i = Build.VERSION.SDK_INT;
        if (i >= 24) {
          sActivityFinishMethod = Activity.class.getDeclaredMethod("finish", new Class[] { Integer.TYPE });
        } else {
          sActivityFinishMethod = Activity.class.getDeclaredMethod("finish", new Class[] { Boolean.TYPE });
        }
        sActivityFinishMethod.setAccessible(true);
      }
      catch (Exception localException)
      {
        Log.e("SecurityManager", " SecurityManager static init error", localException);
      }
    }
    if (Build.VERSION.SDK_INT == 19) {
      System.loadLibrary("sechook");
    }
  }
  
  public SecurityManager(ISecurityManager paramISecurityManager)
  {
    this.mService = paramISecurityManager;
  }
  
  private void activityFinish(Activity paramActivity)
  {
    paramActivity.finish();
    if (paramActivity.isFinishing()) {
      return;
    }
    try
    {
      if (sActivityFinishMethod != null)
      {
        if (Build.VERSION.SDK_INT >= 24) {
          sActivityFinishMethod.invoke(paramActivity, new Object[] { Integer.valueOf(0) });
        } else {
          sActivityFinishMethod.invoke(paramActivity, new Object[] { Boolean.valueOf(false) });
        }
      }
      else if (paramActivity.getParent() == null) {
        paramActivity.finishAffinity();
      }
    }
    catch (Exception paramActivity)
    {
      Log.e("SecurityManager", " FinishMethod.invoke error ", paramActivity);
    }
  }
  
  public static boolean checkCallingPackage(Context paramContext, String[] paramArrayOfString)
  {
    int i = Binder.getCallingUid();
    String[] arrayOfString = paramContext.getPackageManager().getPackagesForUid(i);
    if (arrayOfString == null) {
      return false;
    }
    int j = arrayOfString.length;
    for (i = 0; i < j; i++)
    {
      paramContext = arrayOfString[i];
      int k = paramArrayOfString.length;
      for (int m = 0; m < k; m++) {
        if (paramContext.equals(paramArrayOfString[m])) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static void checkTime(long paramLong, String paramString)
  {
    long l = SystemClock.elapsedRealtime();
    if (l - paramLong > 100L)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("MIUILOG-checkTime:Slow operation: ");
      localStringBuilder.append(l - paramLong);
      localStringBuilder.append("ms so far, now at ");
      localStringBuilder.append(paramString);
      Slog.w("SecurityManager", localStringBuilder.toString());
    }
  }
  
  public static Intent getCheckAccessIntent(boolean paramBoolean1, String paramString, Intent paramIntent, int paramInt1, boolean paramBoolean2, int paramInt2, Bundle paramBundle)
  {
    Object localObject1 = "miui.intent.action.CHECK_ACCESS_CONTROL";
    if (!paramBoolean1) {
      localObject1 = "com.miui.gamebooster.action.ACCESS_WINDOWCALLACTIVITY";
    }
    String str = "com.miui.securitycenter";
    Object localObject2 = localObject1;
    localObject1 = str;
    if (FeatureParser.getBoolean("is_pad", false))
    {
      localObject2 = "android.app.action.CHECK_ACCESS_CONTROL_PAD";
      localObject1 = "com.android.settings";
    }
    localObject2 = new Intent((String)localObject2);
    ((Intent)localObject2).putExtra("android.intent.extra.shortcut.NAME", paramString);
    ((Intent)localObject2).addFlags(8388608);
    ((Intent)localObject2).setPackage((String)localObject1);
    if (paramIntent != null)
    {
      if ((paramIntent.getFlags() & 0x2000000) != 0) {
        ((Intent)localObject2).addFlags(33554432);
      }
      paramIntent.addFlags(16777216);
      if (paramBoolean2)
      {
        if (paramInt1 >= 0) {
          paramIntent.addFlags(33554432);
        }
        if ((paramIntent.getFlags() & 0x10000000) == 0)
        {
          ((Intent)localObject2).addFlags(536870912);
        }
        else
        {
          ((Intent)localObject2).addFlags(268435456);
          ((Intent)localObject2).addFlags(134217728);
        }
      }
      else
      {
        paramIntent.addFlags(268435456);
        ((Intent)localObject2).addFlags(134217728);
      }
      ((Intent)localObject2).putExtra("android.intent.extra.INTENT", paramIntent);
    }
    else
    {
      ((Intent)localObject2).addFlags(536870912);
    }
    if (paramInt2 == 999) {
      ((Intent)localObject2).putExtra("originating_uid", paramInt2);
    }
    if (paramBundle != null) {
      ((Intent)localObject2).putExtras(paramBundle);
    }
    return (Intent)localObject2;
  }
  
  public static int getUserHandle(int paramInt)
  {
    if (XSpaceUserHandle.isXSpaceUserId(paramInt)) {
      return 0;
    }
    return paramInt;
  }
  
  private static native void hook();
  
  public static void init()
  {
    if (Build.VERSION.SDK_INT == 19) {
      hook();
    }
  }
  
  public int activityResume(Intent paramIntent)
  {
    try
    {
      int i = this.mService.activityResume(paramIntent);
      return i;
    }
    catch (Exception paramIntent)
    {
      paramIntent.printStackTrace();
    }
    return 0;
  }
  
  public void addAccessControlPass(String paramString)
  {
    try
    {
      this.mService.addAccessControlPass(paramString);
    }
    catch (RemoteException paramString) {}
  }
  
  public void addAccessControlPassForUser(String paramString, int paramInt)
  {
    try
    {
      this.mService.addAccessControlPassForUser(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("addAccessControlPassForUser exception", paramString);
    }
  }
  
  public boolean addMiuiFirewallSharedUid(int paramInt)
  {
    try
    {
      boolean bool = this.mService.addMiuiFirewallSharedUid(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public boolean areNotificationsEnabledForPackage(String paramString, int paramInt)
  {
    try
    {
      boolean bool = this.mService.areNotificationsEnabledForPackage(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public void checkAccessControl(Activity paramActivity, int paramInt)
  {
    if ((paramActivity != null) && (paramActivity.getParent() == null))
    {
      String str = paramActivity.getPackageName();
      Intent localIntent1 = new Intent();
      localIntent1.setComponent(new ComponentName(str, paramActivity.getClass().getName()));
      if ((!"com.miui.gallery.activity.ExternalPhotoPageActivity".equals(paramActivity.getClass().getName())) && (!"com.miui.gallery.editor.photo.screen.home.ScreenEditorActivity".equals(paramActivity.getClass().getName()))) {
        i = 0;
      } else {
        i = 1;
      }
      if (("com.miui.gallery".equals(str)) && (i != 0)) {
        try
        {
          localIntent2 = paramActivity.getIntent();
          if (localIntent2 == null) {}
        }
        finally
        {
          Intent localIntent2;
          Log.e("SecurityManager", "checkAccessControl sourceIntent", localThrowable);
        }
      }
      int i = activityResume(localIntent1);
      if (((i & 0x1) != 0) && ((i & 0x2) != 0))
      {
        if ((i & 0x8) != 0)
        {
          paramActivity.setResult(0);
          activityFinish(paramActivity);
          return;
        }
        if ((i & 0x4) != 0) {
          return;
        }
        localIntent1 = getCheckAccessIntent(true, str, null, -1, true, paramInt, null);
        localIntent1.putExtra("android.app.extra.PROTECTED_APP_TOKEN", paramActivity.getActivityToken());
        try
        {
          paramActivity.startActivityForResult(localIntent1, -1, null);
        }
        catch (ActivityNotFoundException paramActivity)
        {
          Log.e("SecurityManager", "checkAccessControl", paramActivity);
        }
      }
      else {}
    }
  }
  
  public boolean checkAccessControlPass(String paramString)
  {
    return checkAccessControlPass(paramString, null);
  }
  
  public boolean checkAccessControlPass(String paramString, Intent paramIntent)
  {
    boolean bool1 = false;
    boolean bool2;
    try
    {
      bool2 = this.mService.checkAccessControlPass(paramString, paramIntent);
    }
    catch (RemoteException paramString)
    {
      bool2 = bool1;
    }
    return bool2;
  }
  
  public boolean checkAccessControlPassAsUser(String paramString, int paramInt)
  {
    return checkAccessControlPassAsUser(paramString, null, paramInt);
  }
  
  public boolean checkAccessControlPassAsUser(String paramString, Intent paramIntent, int paramInt)
  {
    boolean bool1 = false;
    try
    {
      boolean bool2 = this.mService.checkAccessControlPassAsUser(paramString, paramIntent, paramInt);
      bool1 = bool2;
    }
    catch (RemoteException paramString) {}
    return bool1;
  }
  
  public boolean checkAccessControlPassword(String paramString1, String paramString2)
  {
    try
    {
      boolean bool = this.mService.checkAccessControlPassword(paramString1, paramString2, UserHandle.myUserId());
      return bool;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("security manager has died", paramString1);
    }
  }
  
  public boolean checkAllowStartActivity(String paramString1, String paramString2, Intent paramIntent, int paramInt1, int paramInt2)
  {
    long l = SystemClock.elapsedRealtime();
    boolean bool1 = false;
    try
    {
      boolean bool2 = this.mService.checkAllowStartActivity(paramString1, paramString2, paramIntent, paramInt1, paramInt2);
      bool1 = bool2;
    }
    catch (RemoteException paramString1) {}
    checkTime(l, "checkAllowStartActivity");
    return bool1;
  }
  
  public boolean checkGameBoosterAntimsgPassAsUser(String paramString, Intent paramIntent, int paramInt)
  {
    try
    {
      boolean bool = this.mService.checkGameBoosterAntimsgPassAsUser(paramString, paramIntent, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("checkGameBoosterAntimsgPassAsUser exception", paramString);
    }
  }
  
  public boolean checkSmsBlocked(Intent paramIntent)
  {
    try
    {
      boolean bool = this.mService.checkSmsBlocked(paramIntent);
      return bool;
    }
    catch (RemoteException paramIntent)
    {
      paramIntent.printStackTrace();
    }
    return false;
  }
  
  public void finishAccessControl(String paramString)
  {
    try
    {
      this.mService.finishAccessControl(paramString, UserHandle.myUserId());
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public void finishAccessControl(String paramString, int paramInt)
  {
    try
    {
      this.mService.finishAccessControl(paramString, paramInt);
      return;
    }
    catch (Exception paramString)
    {
      throw new RuntimeException("finishAccessControl has failed", paramString);
    }
  }
  
  public String getAccessControlPasswordType()
  {
    try
    {
      String str = this.mService.getAccessControlPasswordType(UserHandle.myUserId());
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public List<String> getAllPrivacyApps(int paramInt)
  {
    try
    {
      List localList = this.mService.getAllPrivacyApps(paramInt);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public boolean getAppDarkMode(String paramString)
  {
    try
    {
      boolean bool = this.mService.getAppDarkMode(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("getAppDarkMode exception", paramString);
    }
  }
  
  public boolean getAppDarkModeForUser(String paramString, int paramInt)
  {
    try
    {
      boolean bool = this.mService.getAppDarkModeForUser(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("getAppDarkModeForUser exception", paramString);
    }
  }
  
  public boolean getAppPermissionControlOpen(int paramInt)
  {
    boolean bool = false;
    try
    {
      paramInt = this.mService.getAppPermissionControlOpen(paramInt);
      if (paramInt != 0) {
        bool = true;
      }
      return bool;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public boolean getApplicationAccessControlEnabled(String paramString)
  {
    boolean bool1 = false;
    boolean bool2;
    try
    {
      bool2 = this.mService.getApplicationAccessControlEnabled(paramString);
    }
    catch (RemoteException paramString)
    {
      bool2 = bool1;
    }
    return bool2;
  }
  
  public boolean getApplicationAccessControlEnabledAsUser(String paramString, int paramInt)
  {
    boolean bool1 = false;
    try
    {
      boolean bool2 = this.mService.getApplicationAccessControlEnabledAsUser(paramString, paramInt);
      bool1 = bool2;
    }
    catch (RemoteException paramString) {}
    return bool1;
  }
  
  public boolean getApplicationChildrenControlEnabled(String paramString)
  {
    boolean bool1 = false;
    boolean bool2;
    try
    {
      bool2 = this.mService.getApplicationChildrenControlEnabled(paramString);
    }
    catch (RemoteException paramString)
    {
      bool2 = bool1;
    }
    return bool2;
  }
  
  public boolean getApplicationMaskNotificationEnabledAsUser(String paramString, int paramInt)
  {
    try
    {
      boolean bool = this.mService.getApplicationMaskNotificationEnabledAsUser(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("getApplicationMaskNotificationEnabledAsUser exception", paramString);
    }
  }
  
  public Intent getCheckAccessControlIntent(Context paramContext, String paramString, Intent paramIntent, int paramInt)
  {
    boolean bool;
    if ((paramContext != null) && ((paramContext instanceof Activity))) {
      bool = true;
    } else {
      bool = false;
    }
    return getCheckAccessControlIntent(paramContext, paramString, paramIntent, bool, -1, paramInt, null);
  }
  
  public Intent getCheckAccessControlIntent(Context paramContext, String paramString, Intent paramIntent, boolean paramBoolean, int paramInt1, int paramInt2, Bundle paramBundle)
  {
    if ((paramContext != null) && (isAccessControlActived(paramContext, paramInt2)))
    {
      try
      {
        paramContext = AppGlobals.getPackageManager().getApplicationInfo(paramString, 0, paramInt2);
      }
      catch (Exception paramContext)
      {
        paramContext = null;
      }
      if (paramContext == null) {
        return null;
      }
      if (((paramIntent == null) || (paramContext.uid != Binder.getCallingUid())) && (!checkAccessControlPassAsUser(paramString, paramIntent, paramInt2)) && ((paramIntent == null) || ((paramIntent.getFlags() & 0x100000) == 0))) {
        return getCheckAccessIntent(true, paramString, paramIntent, paramInt1, paramBoolean, paramInt2, paramBundle);
      }
      return null;
    }
    return null;
  }
  
  public Intent getCheckGameBoosterAntimsgIntent(Context paramContext, String paramString1, String paramString2, Intent paramIntent, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if ((paramContext != null) && (!"com.miui.securitycenter".equals(paramString1)) && (isGameBoosterActived(paramInt2)))
    {
      try
      {
        paramContext = AppGlobals.getPackageManager().getApplicationInfo(paramString2, 0, paramInt2);
      }
      catch (Exception paramContext)
      {
        paramContext = null;
      }
      if (paramContext == null) {
        return null;
      }
      if (checkGameBoosterAntimsgPassAsUser(paramString2, paramIntent, paramInt2)) {
        return null;
      }
      return getCheckAccessIntent(false, paramString2, paramIntent, paramInt1, paramBoolean, paramInt2, null);
    }
    return null;
  }
  
  public Intent getCheckIntent(Context paramContext, ApplicationInfo paramApplicationInfo, String paramString1, String paramString2, Intent paramIntent, boolean paramBoolean1, int paramInt1, boolean paramBoolean2, int paramInt2, int paramInt3, Bundle paramBundle)
  {
    Intent localIntent = AppRunningControlManager.getBlockActivityIntent(paramContext, paramString2, paramIntent, paramBoolean1, paramInt1);
    if (localIntent != null) {
      return localIntent;
    }
    paramApplicationInfo = getCheckStartActivityIntent(paramContext, paramApplicationInfo, paramString1, paramString2, paramIntent, paramBoolean1, paramInt1, paramBoolean2, paramInt2, paramInt3);
    if (paramApplicationInfo != null) {
      return paramApplicationInfo;
    }
    paramApplicationInfo = getCheckGameBoosterAntimsgIntent(paramContext, paramString1, paramString2, paramIntent, paramBoolean1, paramInt1, paramInt2);
    if (paramApplicationInfo != null) {
      return paramApplicationInfo;
    }
    return getCheckAccessControlIntent(paramContext, paramString2, paramIntent, paramBoolean1, paramInt1, paramInt2, paramBundle);
  }
  
  public Intent getCheckStartActivityIntent(Context paramContext, ApplicationInfo paramApplicationInfo, String paramString1, String paramString2, Intent paramIntent, boolean paramBoolean1, int paramInt1, boolean paramBoolean2, int paramInt2, int paramInt3)
  {
    long l = SystemClock.elapsedRealtime();
    if ((paramIntent != null) && (!AppOpsUtils.isXOptMode()) && (!Build.IS_INTERNATIONAL_BUILD))
    {
      if (paramBoolean2)
      {
        WakePathChecker.getInstance().recordWakePathCall(paramString1, paramString2, 1, UserHandle.getUserId(paramInt3), paramInt2, true);
        return null;
      }
      if (paramApplicationInfo == null)
      {
        if (TextUtils.isEmpty(paramString1)) {
          return null;
        }
        try
        {
          int i = UserHandle.getUserId(paramInt3);
          paramContext = AppGlobals.getPackageManager();
          try
          {
            paramContext = paramContext.getApplicationInfo(paramString1, 0, i);
          }
          catch (Exception paramContext) {}
          Log.e("SecurityManager", "getCheckStartActivityIntent", paramContext);
        }
        catch (Exception paramContext) {}
        paramContext = paramApplicationInfo;
        if (paramContext == null) {
          return null;
        }
      }
      else
      {
        paramContext = paramApplicationInfo;
      }
      if (((paramContext.flags & 0x81) == 0) && (paramContext.uid >= 10000))
      {
        try
        {
          paramApplicationInfo = AppGlobals.getPackageManager();
          try
          {
            paramApplicationInfo = paramApplicationInfo.getApplicationInfo(paramString2, 0, paramInt2);
          }
          catch (Exception paramApplicationInfo) {}
          Log.e("SecurityManager", "getCheckStartActivityIntent", paramApplicationInfo);
        }
        catch (Exception paramApplicationInfo) {}
        paramApplicationInfo = null;
        if (paramApplicationInfo == null) {
          return null;
        }
        if (((paramApplicationInfo.flags & 0x81) == 0) && (paramApplicationInfo.uid >= 10000))
        {
          if ((TextUtils.equals(paramContext.packageName, paramApplicationInfo.packageName)) && ((paramIntent.getFlags() & 0x100000) != 0)) {
            WakePathChecker.getInstance().recordWakePathCall("recentTask", paramString2, 1, UserHandle.myUserId(), paramInt2, true);
          }
          if ((!TextUtils.equals(paramContext.packageName, paramApplicationInfo.packageName)) && (!checkAllowStartActivity(paramContext.packageName, paramString2, paramIntent, paramInt3, paramInt2)))
          {
            if (FeatureParser.getBoolean("is_pad", false)) {
              return null;
            }
            paramString1 = new Intent("android.app.action.CHECK_ALLOW_START_ACTIVITY");
            paramString1.putExtra("CallerPkgName", paramContext.packageName);
            paramString1.putExtra("CalleePkgName", paramApplicationInfo.packageName);
            paramString1.putExtra("UserId", paramInt2);
            paramString1.putExtra("callerUserId", paramContext.uid);
            paramString1.addFlags(8388608);
            paramString1.setPackage("com.miui.securitycenter");
            if ((paramIntent.getFlags() & 0x2000000) != 0) {
              paramString1.addFlags(33554432);
            }
            paramIntent.addFlags(16777216);
            if (paramBoolean1)
            {
              if (paramInt1 >= 0) {
                paramIntent.addFlags(33554432);
              }
            }
            else {
              paramIntent.addFlags(268435456);
            }
            paramString1.putExtra("android.intent.extra.INTENT", paramIntent);
            checkTime(l, "getCheckStartActivityIntent");
            return paramString1;
          }
          return null;
        }
        return null;
      }
      WakePathChecker.getInstance().recordWakePathCall(paramContext.packageName, paramString2, 1, UserHandle.getUserId(paramInt3), paramInt2, true);
      return null;
    }
    return null;
  }
  
  public List<String> getIncompatibleAppList()
  {
    try
    {
      List localList = this.mService.getIncompatibleAppList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public String getPackageNameByPid(int paramInt)
  {
    try
    {
      String str = this.mService.getPackageNameByPid(paramInt);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    return null;
  }
  
  public int getPermissionFlagsAsUser(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      paramInt = this.mService.getPermissionFlagsAsUser(paramString1, paramString2, paramInt);
      return paramInt;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("security manager has died", paramString1);
    }
  }
  
  public boolean getStickWindowName(String paramString)
  {
    try
    {
      boolean bool = this.mService.getStickWindowName(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public int getSysAppCracked()
  {
    try
    {
      int i = this.mService.getSysAppCracked();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public IBinder getTopActivity()
  {
    try
    {
      IBinder localIBinder = this.mService.getTopActivity();
      return localIBinder;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public ParceledListSlice getWakePathCallListLog()
  {
    try
    {
      ParceledListSlice localParceledListSlice = this.mService.getWakePathCallListLog();
      return localParceledListSlice;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public List<WakePathComponent> getWakePathComponents(String paramString)
  {
    try
    {
      paramString = this.mService.getWakePathComponents(paramString);
      if (paramString == null) {
        return null;
      }
      paramString = paramString.getList();
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died.", paramString);
    }
  }
  
  public long getWakeUpTime(String paramString)
  {
    try
    {
      long l = this.mService.getWakeUpTime(paramString);
      return l;
    }
    catch (RemoteException paramString) {}
    return 0L;
  }
  
  public void grantInstallPermission(String paramString1, String paramString2)
  {
    try
    {
      this.mService.grantInstallPermission(paramString1, paramString2);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("security manager has died", paramString1);
    }
  }
  
  public void grantRuntimePermission(String paramString)
  {
    try
    {
      this.mService.grantRuntimePermission(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public void grantRuntimePermissionAsUser(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      this.mService.grantRuntimePermissionAsUser(paramString1, paramString2, paramInt);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("security manager has died", paramString1);
    }
  }
  
  public boolean haveAccessControlPassword()
  {
    try
    {
      boolean bool = this.mService.haveAccessControlPassword(UserHandle.myUserId());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public boolean isAccessControlActived(Context paramContext)
  {
    return isAccessControlActived(paramContext, UserHandle.getCallingUserId());
  }
  
  public boolean isAccessControlActived(Context paramContext, int paramInt)
  {
    paramInt = getUserHandle(paramInt);
    paramContext = paramContext.getContentResolver();
    boolean bool = false;
    if (1 == Settings.Secure.getIntForUser(paramContext, "access_control_lock_enabled", 0, paramInt)) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isAllowStartService(Intent paramIntent, int paramInt)
  {
    try
    {
      boolean bool = this.mService.isAllowStartService(paramIntent, paramInt);
      return bool;
    }
    catch (RemoteException paramIntent)
    {
      throw new RuntimeException("security manager has died", paramIntent);
    }
  }
  
  public boolean isAppHide()
  {
    try
    {
      boolean bool = this.mService.isAppHide();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public boolean isAppPrivacyEnabled(String paramString)
  {
    try
    {
      boolean bool = this.mService.isAppPrivacyEnabled(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public boolean isFunctionOpen()
  {
    try
    {
      boolean bool = this.mService.isFunctionOpen();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public boolean isGameBoosterActived(int paramInt)
  {
    try
    {
      boolean bool = this.mService.getGameMode(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("isGameBoosterActived exception", localRemoteException);
    }
  }
  
  public boolean isPrivacyApp(String paramString, int paramInt)
  {
    try
    {
      boolean bool = this.mService.isPrivacyApp(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public boolean isRestrictedAppNet(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageName();
      boolean bool = this.mService.isRestrictedAppNet(paramContext);
      return bool;
    }
    catch (RemoteException paramContext)
    {
      throw new RuntimeException("security manager has died", paramContext);
    }
  }
  
  public boolean isValidDevice()
  {
    try
    {
      boolean bool = this.mService.isValidDevice();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public void killNativePackageProcesses(int paramInt, String paramString)
  {
    try
    {
      this.mService.killNativePackageProcesses(paramInt, paramString);
    }
    catch (RemoteException paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public int moveTaskToStack(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("moveTaskToStack ");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(" to ");
    localStringBuilder.append(paramInt2);
    localStringBuilder.append(", isOnTop:");
    localStringBuilder.append(paramBoolean);
    Log.i("SecurityManager", localStringBuilder.toString());
    try
    {
      paramInt1 = this.mService.moveTaskToStack(paramInt1, paramInt2, paramBoolean);
      return paramInt1;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public boolean needFinishAccessControl(IBinder paramIBinder)
  {
    try
    {
      boolean bool = this.mService.needFinishAccessControl(paramIBinder);
      return bool;
    }
    catch (Exception paramIBinder)
    {
      paramIBinder.printStackTrace();
    }
    return false;
  }
  
  public void pushUpdatePkgsData(List<String> paramList, boolean paramBoolean)
  {
    try
    {
      this.mService.pushUpdatePkgsData(paramList, paramBoolean);
      return;
    }
    catch (RemoteException paramList)
    {
      throw new RuntimeException("security manager has died", paramList);
    }
  }
  
  public void pushWakePathConfirmDialogWhiteList(int paramInt, List<String> paramList)
  {
    try
    {
      this.mService.pushWakePathConfirmDialogWhiteList(paramInt, paramList);
    }
    catch (RemoteException paramList)
    {
      Log.e("SecurityManager", "pushWakePathConfirmDialogWhiteList", paramList);
    }
  }
  
  public void pushWakePathData(int paramInt1, ParceledListSlice paramParceledListSlice, int paramInt2)
  {
    try
    {
      this.mService.pushWakePathData(paramInt1, paramParceledListSlice, paramInt2);
    }
    catch (RemoteException paramParceledListSlice) {}
  }
  
  public void pushWakePathWhiteList(List<String> paramList, int paramInt)
  {
    try
    {
      this.mService.pushWakePathWhiteList(paramList, paramInt);
    }
    catch (RemoteException paramList) {}
  }
  
  public boolean putSystemDataStringFile(String paramString1, String paramString2)
  {
    try
    {
      boolean bool = this.mService.putSystemDataStringFile(paramString1, paramString2);
      return bool;
    }
    catch (RemoteException paramString1) {}
    return false;
  }
  
  public String readSystemDataStringFile(String paramString)
  {
    try
    {
      paramString = this.mService.readSystemDataStringFile(paramString);
      return paramString;
    }
    catch (RemoteException paramString) {}
    return null;
  }
  
  public void registerWakePathCallback(IWakePathCallback paramIWakePathCallback)
  {
    try
    {
      this.mService.registerWakePathCallback(paramIWakePathCallback);
    }
    catch (Exception paramIWakePathCallback) {}
  }
  
  public void removeAccessControlPass(String paramString)
  {
    try
    {
      this.mService.removeAccessControlPass(paramString);
    }
    catch (RemoteException paramString) {}
  }
  
  public void removeAccessControlPassAsUser(String paramString, int paramInt)
  {
    try
    {
      this.mService.removeAccessControlPassAsUser(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public void removeWakePathData(int paramInt)
  {
    try
    {
      this.mService.removeWakePathData(paramInt);
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public int resizeTask(int paramInt1, Rect paramRect, int paramInt2)
  {
    Log.i("SecurityManager", "resizeTask");
    try
    {
      paramInt1 = this.mService.resizeTask(paramInt1, paramRect, paramInt2);
      return paramInt1;
    }
    catch (RemoteException paramRect)
    {
      throw new RuntimeException("security manager has died", paramRect);
    }
  }
  
  public void revokeRuntimePermissionAsUser(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      this.mService.revokeRuntimePermissionAsUser(paramString1, paramString2, paramInt);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("security manager has died", paramString1);
    }
  }
  
  public void revokeRuntimePermissionAsUserNotKill(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      this.mService.revokeRuntimePermissionAsUserNotKill(paramString1, paramString2, paramInt);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("security manager has died", paramString1);
    }
  }
  
  public void saveIcon(String paramString, Bitmap paramBitmap)
  {
    try
    {
      this.mService.saveIcon(paramString, paramBitmap);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("saveIcon exception", paramString);
    }
  }
  
  public void setAccessControlPassword(String paramString1, String paramString2)
  {
    try
    {
      this.mService.setAccessControlPassword(paramString1, paramString2, UserHandle.myUserId());
      return;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("security manager has died", paramString1);
    }
  }
  
  public void setAppDarkModeForUser(String paramString, boolean paramBoolean, int paramInt)
  {
    try
    {
      this.mService.setAppDarkModeForUser(paramString, paramBoolean, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("setAppDarkModeForUser exception", paramString);
    }
  }
  
  public boolean setAppHide(boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.mService.setAppHide(paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public void setAppPermissionControlOpen(int paramInt)
  {
    try
    {
      this.mService.setAppPermissionControlOpen(paramInt);
    }
    catch (Exception localException) {}
  }
  
  public void setAppPrivacyStatus(String paramString, boolean paramBoolean)
  {
    try
    {
      this.mService.setAppPrivacyStatus(paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public void setApplicationAccessControlEnabled(String paramString, boolean paramBoolean)
  {
    try
    {
      this.mService.setApplicationAccessControlEnabled(paramString, paramBoolean);
    }
    catch (RemoteException paramString) {}
  }
  
  public void setApplicationAccessControlEnabledForUser(String paramString, boolean paramBoolean, int paramInt)
  {
    try
    {
      this.mService.setApplicationAccessControlEnabledForUser(paramString, paramBoolean, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("setApplicationAccessControlEnabledForUser exception", paramString);
    }
  }
  
  public void setApplicationChildrenControlEnabled(String paramString, boolean paramBoolean)
  {
    try
    {
      this.mService.setApplicationChildrenControlEnabled(paramString, paramBoolean);
    }
    catch (RemoteException paramString) {}
  }
  
  public void setApplicationMaskNotificationEnabledForUser(String paramString, boolean paramBoolean, int paramInt)
  {
    try
    {
      this.mService.setApplicationMaskNotificationEnabledForUser(paramString, paramBoolean, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("setApplicationMaskNotificationEnabledForUser exception", paramString);
    }
  }
  
  public void setCoreRuntimePermissionEnabled(boolean paramBoolean, int paramInt)
  {
    try
    {
      this.mService.setCoreRuntimePermissionEnabled(paramBoolean, paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public boolean setCurrentNetworkState(int paramInt)
  {
    try
    {
      boolean bool = this.mService.setCurrentNetworkState(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public void setGameBoosterIBinder(IBinder paramIBinder, int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.setGameBoosterIBinder(paramIBinder, paramInt, paramBoolean);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw new RuntimeException("setGameBoosterIBinder exception", paramIBinder);
    }
  }
  
  public void setIncompatibleAppList(List<String> paramList)
  {
    try
    {
      this.mService.setIncompatibleAppList(paramList);
      return;
    }
    catch (RemoteException paramList)
    {
      throw new RuntimeException("security manager has died", paramList);
    }
  }
  
  public boolean setMiuiFirewallRule(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      boolean bool = this.mService.setMiuiFirewallRule(paramString, paramInt1, paramInt2, paramInt3);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public void setNotificationsEnabledForPackage(String paramString, int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.setNotificationsEnabledForPackage(paramString, paramInt, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public void setPrivacyApp(String paramString, int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.setPrivacyApp(paramString, paramInt, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public void setStickWindowName(String paramString)
  {
    try
    {
      this.mService.setStickWindowName(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("security manager has died", paramString);
    }
  }
  
  public void setTrackWakePathCallListLogEnabled(boolean paramBoolean)
  {
    try
    {
      this.mService.setTrackWakePathCallListLogEnabled(paramBoolean);
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setWakeUpTime(String paramString, long paramLong)
  {
    try
    {
      this.mService.setWakeUpTime(paramString, paramLong);
    }
    catch (RemoteException paramString) {}
  }
  
  public boolean startInterceptSmsBySender(Context paramContext, String paramString, int paramInt)
  {
    try
    {
      paramContext = paramContext.getPackageName();
      boolean bool = this.mService.startInterceptSmsBySender(paramContext, paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramContext)
    {
      paramContext.printStackTrace();
    }
    return false;
  }
  
  public boolean stopInterceptSmsBySender()
  {
    try
    {
      boolean bool = this.mService.stopInterceptSmsBySender();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    return false;
  }
  
  public void updateLauncherPackageNames()
  {
    try
    {
      this.mService.updateLauncherPackageNames();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public void updateLedStatus(boolean paramBoolean)
  {
    try
    {
      this.mService.updateLedStatus(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public void updatePermissionFlagsAsUser(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      this.mService.updatePermissionFlagsAsUser(paramString1, paramString2, paramInt1, paramInt2, paramInt3);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw new RuntimeException("security manager has died", paramString1);
    }
  }
  
  public void watchGreenGuardProcess()
  {
    try
    {
      this.mService.watchGreenGuardProcess();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
  
  public boolean writeAppHideConfig(boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.mService.writeAppHideConfig(paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("security manager has died", localRemoteException);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/SecurityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */