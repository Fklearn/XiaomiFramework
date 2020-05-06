package miui.util;

import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.MiuiNotification;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.miui.AppOpsUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;
import miui.securityspace.XSpaceUserHandle;

public class NotificationFilterHelper
{
  private static final String APP_NOTIFICATION = "app_notification";
  public static final int BACKUP_CHANNEL_IMPORTANCE_DEFAULT = 3;
  private static final String CHANNEL_FLAG = "_channel_flag";
  private static final String CHANNEL_IMPORTANCE_BACKUP = "_channel_importance_backup";
  public static final int DISABLE_ALL = 3;
  public static final int DISABLE_FLOATING = 1;
  public static final int ENABLE = 2;
  public static final String IMPORTANCE = "_importance";
  public static final int IMPORTANCE_DEFAULT = 0;
  public static final int IMPORTANCE_HIGH = 1;
  public static final int IMPORTANCE_LOW = -1;
  public static final String KEYGUARD = "_keyguard";
  private static final String KEY_FLOAT_VERSION = "sysui_float_version";
  public static final String KEY_FLOAT_WHITELIST = "float_whitelist";
  private static final String KEY_KEYGUARD_VERSION = "sysui_keyguard_version";
  public static final String KEY_KEYGUARD_WHITELIST = "keyguard_whitelist";
  public static final String LED = "_led";
  public static final String MESSAGE = "_message";
  public static final int NONE = 0;
  public static final String SOUND = "_sound";
  private static final String SYSTEMUI_PACKAGE_NAME = "com.android.systemui";
  private static final String TAG = "NotificationFilterHelper";
  public static final String VIBRATE = "_vibrate";
  private static INotificationManager nm;
  private static long sFloatVersionCode;
  private static ArrayList<String> sFloatWhiteList = new ArrayList();
  private static HashMap<String, Boolean> sIsSystemApp;
  private static long sKeyguardVersionCode;
  private static ArrayList<String> sKeyguardWhiteList = new ArrayList();
  private static HashSet<String> sNotificationCanBeBlockedList;
  private static HashSet<String> sNotificationCannotSetImportanceList;
  private static HashSet<String> sNotificationForcedEnabledAllChannelList;
  private static HashMap<String, HashSet<String>> sNotificationForcedEnabledChannelList;
  private static HashSet<String> sNotificationForcedEnabledPkgList;
  
  static
  {
    sFloatVersionCode = -1L;
    sKeyguardVersionCode = -1L;
    nm = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    sIsSystemApp = new HashMap();
    sNotificationCanBeBlockedList = new HashSet();
    sNotificationForcedEnabledPkgList = new HashSet();
    sNotificationForcedEnabledAllChannelList = new HashSet();
    sNotificationForcedEnabledChannelList = new HashMap();
    sNotificationCannotSetImportanceList = new HashSet();
    sNotificationCanBeBlockedList.add("com.miui.fm");
    sNotificationCanBeBlockedList.add("com.miui.antispam");
    sNotificationCanBeBlockedList.add("com.miui.securitycenter");
    sNotificationForcedEnabledPkgList.add("android");
    sNotificationForcedEnabledPkgList.add("com.android.incallui");
    sNotificationForcedEnabledPkgList.add("com.android.deskclock");
    sNotificationForcedEnabledPkgList.add("com.android.mms");
    sNotificationForcedEnabledPkgList.add("com.android.bluetooth");
    sNotificationForcedEnabledPkgList.add("com.android.updater");
    sNotificationForcedEnabledPkgList.add("com.android.providers.downloads");
    sNotificationForcedEnabledPkgList.add("com.miui.hybrid");
    sNotificationForcedEnabledAllChannelList.add("com.xiaomi.xmsf");
    HashSet localHashSet = new HashSet();
    localHashSet.add("com.miui.securitycenter");
    sNotificationForcedEnabledChannelList.put("com.miui.securitycenter", localHashSet);
    sNotificationCannotSetImportanceList.add("com.android.incallui");
  }
  
  private static boolean areNotificationsEnabled(Context paramContext, String paramString)
  {
    return areNotificationsEnabled(paramContext, paramString, getAppUid(paramContext, paramString));
  }
  
  private static boolean areNotificationsEnabled(Context paramContext, String paramString, int paramInt)
  {
    boolean bool1 = true;
    boolean bool2;
    try
    {
      bool2 = nm.areNotificationsEnabledForPackage(paramString, paramInt);
    }
    catch (RemoteException paramContext)
    {
      bool2 = bool1;
    }
    return bool2;
  }
  
  public static boolean canNotificationSetImportance(String paramString)
  {
    return sNotificationCannotSetImportanceList.contains(paramString) ^ true;
  }
  
  public static boolean canSendNotifications(Context paramContext, String paramString)
  {
    boolean bool = true;
    if (getAppFlag(paramContext, paramString, true) == 3) {
      bool = false;
    }
    return bool;
  }
  
  public static boolean canSystemNotificationBeBlocked(String paramString)
  {
    return sNotificationCanBeBlockedList.contains(paramString);
  }
  
  private static boolean containNonBlockableChannel(String paramString)
  {
    return sNotificationForcedEnabledChannelList.containsKey(paramString);
  }
  
  public static void enableNotifications(Context paramContext, String paramString, int paramInt, boolean paramBoolean)
  {
    if ((!paramBoolean) && (isNotificationForcedFor(paramContext, paramString))) {
      return;
    }
    try
    {
      Object localObject;
      if (XSpaceUserHandle.isUidBelongtoXSpace(paramInt))
      {
        resolveAssociatedUid(paramContext, UserHandle.OWNER, paramString, paramBoolean);
      }
      else if ((paramContext.getUserId() == 0) && (XSpaceUserHandle.isAppInXSpace(paramContext, paramString)))
      {
        localObject = new android/os/UserHandle;
        ((UserHandle)localObject).<init>(999);
        resolveAssociatedUid(paramContext, (UserHandle)localObject, paramString, paramBoolean);
      }
      nm.setNotificationsEnabledForPackage(paramString, paramInt, paramBoolean);
      try
      {
        if ((!Build.IS_TABLET) || (Build.VERSION.SDK_INT >= 26))
        {
          localObject = paramContext.getPackageManager();
          paramInt = 0;
          localObject = ((PackageManager)localObject).getApplicationInfo(paramString, 0);
          if ((localObject != null) && ((((ApplicationInfo)localObject).flags & 0x1) == 0))
          {
            if (!paramBoolean) {
              paramInt = 1;
            }
            AppOpsUtils.setMode(paramContext, 11, paramString, paramInt);
          }
        }
      }
      catch (PackageManager.NameNotFoundException paramContext) {}
    }
    catch (RemoteException paramContext) {}
  }
  
  public static void enableNotifications(Context paramContext, String paramString, boolean paramBoolean)
  {
    enableNotifications(paramContext, paramString, getAppUid(paramContext, paramString), paramBoolean);
  }
  
  private static void enableStatusIcon(Context paramContext, String paramString, int paramInt)
  {
    getSharedPreferences(paramContext).edit().putInt(paramString, paramInt).apply();
  }
  
  public static void enableStatusIcon(Context paramContext, String paramString1, String paramString2, boolean paramBoolean)
  {
    int i = 1;
    paramString1 = String.format("%s_%s%s", new Object[] { paramString1, paramString2, "_channel_flag" });
    if (paramBoolean) {
      i = 2;
    }
    enableStatusIcon(paramContext, paramString1, i);
  }
  
  public static void enableStatusIcon(Context paramContext, String paramString, boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 2;
    } else {
      i = 1;
    }
    enableStatusIcon(paramContext, paramString, i);
  }
  
  public static int getAppFlag(Context paramContext, String paramString, int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramBoolean = areNotificationsEnabled(paramContext, paramString, paramInt);
    } else {
      paramBoolean = true;
    }
    int i;
    if (paramBoolean)
    {
      i = getSharedPreferences(paramContext).getInt(paramString, 0);
      paramInt = i;
      if (i == 0) {
        paramInt = getDefaultFlag(paramContext, paramString, false);
      }
      i = paramInt;
      if (paramInt == 0) {
        i = 1;
      }
    }
    else
    {
      i = 3;
    }
    return i;
  }
  
  public static int getAppFlag(Context paramContext, String paramString, boolean paramBoolean)
  {
    return getAppFlag(paramContext, paramString, getAppUid(paramContext, paramString), paramBoolean);
  }
  
  private static int getAppUid(Context paramContext, String paramString)
  {
    int i = 0;
    int j;
    try
    {
      j = paramContext.getPackageManager().getApplicationInfo(paramString, 0).uid;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      j = i;
    }
    return j;
  }
  
  public static int getBackupChannelImportance(Context paramContext, String paramString1, String paramString2)
  {
    paramContext = getSharedPreferences(paramContext);
    paramString1 = getChannelKey(paramString1, paramString2, "_channel_importance_backup");
    int i = 3;
    int j = paramContext.getInt(paramString1, 3);
    if (j != 0) {
      i = j;
    }
    return i;
  }
  
  public static int getChannelFlag(Context paramContext, String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramBoolean = areNotificationsEnabled(paramContext, paramString1, paramInt);
    } else {
      paramBoolean = true;
    }
    int i;
    if (paramBoolean)
    {
      i = getSharedPreferences(paramContext).getInt(String.format("%s_%s%s", new Object[] { paramString1, paramString2, "_channel_flag" }), 0);
      paramInt = i;
      if (i == 0) {
        paramInt = getDefaultFlag(paramContext, paramString1, false);
      }
      i = paramInt;
      if (paramInt == 0) {
        i = 1;
      }
    }
    else
    {
      i = 3;
    }
    return i;
  }
  
  private static String getChannelKey(String paramString1, String paramString2, String paramString3)
  {
    return String.format("%s_%s%s", new Object[] { paramString1, paramString2, paramString3 });
  }
  
  public static Uri getCustomSoundUri(Context paramContext, Uri paramUri, StatusBarNotification paramStatusBarNotification)
  {
    return paramUri;
  }
  
  private static int getDefaultFlag(Context paramContext, String paramString, boolean paramBoolean)
  {
    initFilterList(paramContext);
    int i = 2;
    if (!paramBoolean)
    {
      if (!sFloatWhiteList.contains(paramString)) {
        i = 0;
      }
      return i;
    }
    if (!sKeyguardWhiteList.contains(paramString)) {
      i = 0;
    }
    return i;
  }
  
  public static int getImportance(Context paramContext, String paramString)
  {
    SharedPreferences localSharedPreferences = getSharedPreferences(paramContext);
    paramContext = new StringBuilder();
    paramContext.append(paramString);
    paramContext.append("_importance");
    return localSharedPreferences.getInt(paramContext.toString(), 0);
  }
  
  public static HashSet<String> getNotificationForcedEnabledList()
  {
    return sNotificationForcedEnabledPkgList;
  }
  
  private static String getRealPackageName(StatusBarNotification paramStatusBarNotification)
  {
    CharSequence localCharSequence;
    if (paramStatusBarNotification.getNotification().extras.containsKey("miui.targetPkg")) {
      localCharSequence = paramStatusBarNotification.getNotification().extras.getCharSequence("miui.targetPkg");
    } else {
      localCharSequence = paramStatusBarNotification.getNotification().extraNotification.getTargetPkg();
    }
    if (TextUtils.isEmpty(localCharSequence)) {
      paramStatusBarNotification = paramStatusBarNotification.getPackageName();
    } else {
      paramStatusBarNotification = localCharSequence.toString();
    }
    return paramStatusBarNotification;
  }
  
  public static SharedPreferences getSharedPreferences(Context paramContext)
  {
    Context localContext1 = paramContext;
    Context localContext2;
    if (XSpaceUserHandle.isXSpaceUserId(paramContext.getUserId())) {
      try
      {
        localContext1 = paramContext.createPackageContextAsUser("com.android.systemui", 2, UserHandle.OWNER);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("NotificationFilterHelper", "Can't find pkg: com.android.systemui", localNameNotFoundException);
        localContext2 = paramContext;
      }
    }
    paramContext = localContext2;
    if (!localContext2.getPackageName().equals("com.android.systemui")) {
      try
      {
        paramContext = localContext2.createPackageContext("com.android.systemui", 2);
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        paramContext.printStackTrace();
        paramContext = localContext2;
      }
    }
    return paramContext.getSharedPreferences("app_notification", 4);
  }
  
  private static Set<String> getWhiteListFromCache(Context paramContext, String paramString)
  {
    Object localObject = null;
    try
    {
      paramContext = getSharedPreferences(paramContext).getStringSet(paramString, null);
    }
    catch (Exception paramContext)
    {
      paramContext = new StringBuilder();
      paramContext.append("error get whiteList: ");
      paramContext.append(paramString);
      Log.e("NotificationFilterHelper", paramContext.toString());
      paramContext = (Context)localObject;
    }
    return paramContext;
  }
  
  private static void initFilterList(Context paramContext)
  {
    long l = Settings.Global.getLong(paramContext.getContentResolver(), "sysui_float_version", -1L);
    Object localObject1;
    Object localObject2;
    if ((sFloatWhiteList.isEmpty()) || (l != sFloatVersionCode))
    {
      sFloatVersionCode = l;
      localObject1 = new ArrayList();
      localObject2 = getWhiteListFromCache(paramContext, "float_whitelist");
      if ((localObject2 != null) && (!((Set)localObject2).isEmpty())) {
        ((ArrayList)localObject1).addAll((Collection)localObject2);
      } else {
        ((ArrayList)localObject1).addAll(Arrays.asList(paramContext.getResources().getStringArray(285343773)));
      }
      sFloatWhiteList = (ArrayList)localObject1;
    }
    l = Settings.Global.getLong(paramContext.getContentResolver(), "sysui_keyguard_version", -1L);
    if ((sKeyguardWhiteList.isEmpty()) || (l != sKeyguardVersionCode))
    {
      sKeyguardVersionCode = l;
      localObject2 = new ArrayList();
      localObject1 = getWhiteListFromCache(paramContext, "keyguard_whitelist");
      if ((localObject1 != null) && (!((Set)localObject1).isEmpty())) {
        ((ArrayList)localObject2).addAll((Collection)localObject1);
      } else {
        ((ArrayList)localObject2).addAll(Arrays.asList(paramContext.getResources().getStringArray(285343775)));
      }
      sKeyguardWhiteList = (ArrayList)localObject2;
    }
  }
  
  public static boolean isAllowed(Context paramContext, StatusBarNotification paramStatusBarNotification, String paramString)
  {
    return isAllowed(paramContext, getRealPackageName(paramStatusBarNotification), paramString);
  }
  
  public static boolean isAllowed(Context paramContext, String paramString1, String paramString2)
  {
    boolean bool1 = "_keyguard".equals(paramString2);
    boolean bool2 = false;
    boolean bool3 = false;
    StringBuilder localStringBuilder;
    if ((bool1) || ("_sound".equals(paramString2)))
    {
      localObject = getSharedPreferences(paramContext);
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString1);
      localStringBuilder.append(paramString2);
      if (!((SharedPreferences)localObject).contains(localStringBuilder.toString()))
      {
        if (getDefaultFlag(paramContext, paramString1, true) == 2) {
          bool3 = true;
        }
        return bool3;
      }
    }
    if ("_led".equals(paramString2))
    {
      localObject = getSharedPreferences(paramContext);
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString1);
      localStringBuilder.append(paramString2);
      if (!((SharedPreferences)localObject).contains(localStringBuilder.toString()))
      {
        bool3 = bool2;
        if (getDefaultFlag(paramContext, paramString1, false) == 2) {
          bool3 = true;
        }
        return bool3;
      }
    }
    paramContext = getSharedPreferences(paramContext);
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString1);
    ((StringBuilder)localObject).append(paramString2);
    paramString1 = ((StringBuilder)localObject).toString();
    if ((paramContext.contains(paramString1)) && (!(paramContext.getAll().get(paramString1) instanceof Boolean)))
    {
      Log.e("NotificationFilterHelper", "got non boolean values, return true");
      return true;
    }
    return paramContext.getBoolean(paramString1, true);
  }
  
  public static boolean isAllowedWithChannel(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    boolean bool1 = "_keyguard".equals(paramString3);
    boolean bool2 = false;
    boolean bool3 = false;
    if (((bool1) || ("_sound".equals(paramString3))) && (!getSharedPreferences(paramContext).contains(getChannelKey(paramString1, paramString2, paramString3))))
    {
      if (getDefaultFlag(paramContext, paramString1, true) == 2) {
        bool3 = true;
      }
      return bool3;
    }
    if (("_led".equals(paramString3)) && (!getSharedPreferences(paramContext).contains(getChannelKey(paramString1, paramString2, paramString3))))
    {
      bool3 = bool2;
      if (getDefaultFlag(paramContext, paramString1, false) == 2) {
        bool3 = true;
      }
      return bool3;
    }
    return getSharedPreferences(paramContext).getBoolean(getChannelKey(paramString1, paramString2, paramString3), true);
  }
  
  private static boolean isNotificationForcedEnabled(Context paramContext, String paramString)
  {
    if (sNotificationForcedEnabledPkgList.contains(paramString)) {
      return true;
    }
    if (canSystemNotificationBeBlocked(paramString)) {
      return false;
    }
    int i = UserHandle.getAppId(getAppUid(paramContext, paramString));
    return (i == 1000) || (i == 1001) || (i == 0);
  }
  
  public static boolean isNotificationForcedEnabled(Context paramContext, String paramString1, String paramString2)
  {
    boolean bool;
    if ((!isNotificationForcedEnabled(paramContext, paramString1)) && ((sNotificationForcedEnabledChannelList.get(paramString1) == null) || (!((HashSet)sNotificationForcedEnabledChannelList.get(paramString1)).contains(paramString2)))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isNotificationForcedFor(Context paramContext, String paramString)
  {
    boolean bool;
    if ((!isNotificationForcedEnabled(paramContext, paramString)) && (!containNonBlockableChannel(paramString)) && (!sNotificationForcedEnabledAllChannelList.contains(paramString))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isSystemApp(String paramString, PackageManager paramPackageManager)
  {
    Boolean localBoolean1 = (Boolean)sIsSystemApp.get(paramString);
    Boolean localBoolean2 = localBoolean1;
    if (localBoolean1 == null)
    {
      localBoolean2 = null;
      boolean bool1 = false;
      try
      {
        paramPackageManager = paramPackageManager.getApplicationInfo(paramString, 0);
      }
      catch (PackageManager.NameNotFoundException paramPackageManager)
      {
        paramPackageManager = localBoolean2;
      }
      boolean bool2 = bool1;
      if (paramPackageManager != null)
      {
        bool2 = bool1;
        if ((paramPackageManager.flags & 0x1) != 0) {
          bool2 = true;
        }
      }
      localBoolean2 = Boolean.valueOf(bool2);
      sIsSystemApp.put(paramString, localBoolean2);
    }
    return localBoolean2.booleanValue();
  }
  
  private static void resolveAssociatedUid(Context paramContext, UserHandle paramUserHandle, String paramString, boolean paramBoolean)
  {
    try
    {
      int i = getAppUid(paramContext.createPackageContextAsUser(paramString, 2, paramUserHandle), paramString);
      nm.setNotificationsEnabledForPackage(paramString, i, paramBoolean);
    }
    catch (PackageManager.NameNotFoundException paramUserHandle)
    {
      paramContext = new StringBuilder();
      paramContext.append("Can't find pkg: ");
      paramContext.append(paramString);
      Log.e("NotificationFilterHelper", paramContext.toString(), paramUserHandle);
    }
    catch (RemoteException paramContext)
    {
      Log.e("NotificationFilterHelper", "Can't talk to NotificationManagerService", paramContext);
    }
  }
  
  public static void saveBackupChannelImportance(Context paramContext, String paramString1, String paramString2, int paramInt)
  {
    getSharedPreferences(paramContext).edit().putInt(getChannelKey(paramString1, paramString2, "_channel_importance_backup"), paramInt).apply();
  }
  
  public static void setAllow(Context paramContext, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    getSharedPreferences(paramContext).edit().putBoolean(getChannelKey(paramString1, paramString2, paramString3), paramBoolean).apply();
  }
  
  public static void setAllow(Context paramContext, String paramString1, String paramString2, boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = getSharedPreferences(paramContext).edit();
    paramContext = new StringBuilder();
    paramContext.append(paramString1);
    paramContext.append(paramString2);
    localEditor.putBoolean(paramContext.toString(), paramBoolean).apply();
  }
  
  public static void setImportance(Context paramContext, String paramString, int paramInt)
  {
    paramContext = getSharedPreferences(paramContext).edit();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("_importance");
    paramContext.putInt(localStringBuilder.toString(), paramInt).apply();
  }
  
  public static void updateFloatWhiteList(Context paramContext, List<String> paramList)
  {
    Settings.Global.putLong(paramContext.getContentResolver(), "sysui_float_version", System.currentTimeMillis());
    getSharedPreferences(paramContext).edit().putStringSet("float_whitelist", new HashSet(paramList)).apply();
  }
  
  public static void updateKeyguardWhitelist(Context paramContext, List<String> paramList)
  {
    Settings.Global.putLong(paramContext.getContentResolver(), "sysui_keyguard_version", System.currentTimeMillis());
    getSharedPreferences(paramContext).edit().putStringSet("keyguard_whitelist", new HashSet(paramList)).apply();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/NotificationFilterHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */