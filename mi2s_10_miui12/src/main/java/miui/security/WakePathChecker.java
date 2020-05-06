package miui.security;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;
import com.android.internal.app.IWakePathCallback;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;
import miui.securityspace.XSpaceUserHandle;

public class WakePathChecker
{
  private static final int CALL_LIST_LOG_MAP_MAX_SIZE = 200;
  private static final int GET_CONTENT_PROVIDER_RULE_INFOS_LIST_INDEX = 2;
  private static final int GET_CONTENT_PROVIDER_RULE_INFOS_WHITELIST_INDEX = 6;
  private static final int RULE_INFOS_LIST_COUNT = 8;
  private static final int SEND_BROADCAST_RULE_INFOS_LIST_INDEX = 1;
  private static final int SEND_BROADCAST_RULE_INFOS_WHITELIST_INDEX = 5;
  private static final int START_ACTIVITY_RULE_INFOS_LIST_INDEX = 0;
  private static final int START_ACTIVITY_RULE_INFOS_WHITELIST_INDEX = 4;
  private static final int START_SERVICE_RULE_INFOS_LIST_INDEX = 3;
  private static final int START_SERVICE_RULE_INFOS_WHITELIST_INDEX = 7;
  private static final String TAG = WakePathChecker.class.getSimpleName();
  public static final int WAKEPATH_CONFIRM_DIALOG_WHITELIST_TYPE_CALLEE = 1;
  public static final int WAKEPATH_CONFIRM_DIALOG_WHITELIST_TYPE_CALLER = 2;
  private static WakePathChecker sInstance;
  private IAppOpsService mAppOpsService;
  private List<String> mBindServiceCheckActions = new ArrayList();
  Object mCallListLogLocker = new Object();
  Map<Integer, WakePathRuleInfo> mCallListLogMap;
  private IWakePathCallback mCallback;
  List<String> mLauncherPackageNames = new ArrayList();
  private IPackageManager mPackageManager;
  private boolean mSupportWakePathV2 = false;
  boolean mTrackCallListLogEnabled = Build.IS_STABLE_VERSION ^ true;
  private boolean mUpdatePkgsEnable = false;
  private List<String> mUpdatePkgsList = new ArrayList();
  private Map<Integer, WakePathRuleData> mUserWakePathRuleDataMap = new HashMap();
  private List<String> mWakePathCallerWhiteList = new ArrayList();
  private List<String> mWakePathConfirmDialogCallerWhitelist = new ArrayList();
  private List<String> mWakePathConfirmDialogWhitelist = new ArrayList();
  
  private WakePathChecker()
  {
    if (this.mTrackCallListLogEnabled) {
      this.mCallListLogMap = new HashMap(200);
    }
    this.mWakePathConfirmDialogWhitelist.add("com.mfashiongallery.express");
    this.mWakePathConfirmDialogWhitelist.add("com.mi.dlabs.vr.thor");
    this.mWakePathCallerWhiteList.add("com.miui.home");
    this.mWakePathCallerWhiteList.add("com.miui.securitycenter");
    this.mBindServiceCheckActions.add("miui.action.CAMERA_EMPTY_SERVICE");
    this.mBindServiceCheckActions.add("android.media.browse.MediaBrowserService");
    this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
    this.mPackageManager = AppGlobals.getPackageManager();
    try
    {
      if (this.mPackageManager.getPackageInfo("com.lbe.security.miui", 0, 0).versionCode >= 126) {
        this.mSupportWakePathV2 = true;
      }
    }
    catch (Exception localException)
    {
      Log.e(TAG, "get lbeInfo exception!", localException);
    }
  }
  
  public static WakePathChecker getInstance()
  {
    try
    {
      if (sInstance == null)
      {
        localWakePathChecker = new miui/security/WakePathChecker;
        localWakePathChecker.<init>();
        sInstance = localWakePathChecker;
      }
      WakePathChecker localWakePathChecker = sInstance;
      return localWakePathChecker;
    }
    finally {}
  }
  
  private WakePathRuleData getWakePathRuleDataByUser(int paramInt)
  {
    int i;
    if (!XSpaceUserHandle.isXSpaceUserId(paramInt))
    {
      i = paramInt;
      if (paramInt != -1) {}
    }
    else
    {
      i = 0;
    }
    synchronized (this.mUserWakePathRuleDataMap)
    {
      WakePathRuleData localWakePathRuleData1 = (WakePathRuleData)this.mUserWakePathRuleDataMap.get(Integer.valueOf(i));
      WakePathRuleData localWakePathRuleData2 = localWakePathRuleData1;
      if (localWakePathRuleData1 == null)
      {
        localWakePathRuleData2 = new miui/security/WakePathChecker$WakePathRuleData;
        localWakePathRuleData2.<init>(this);
        this.mUserWakePathRuleDataMap.put(Integer.valueOf(i), localWakePathRuleData2);
      }
      return localWakePathRuleData2;
    }
  }
  
  private boolean matchWakePathRuleInfos(WakePathRuleData paramWakePathRuleData, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
  {
    paramInt2 = wakeTypeToRuleInfosListIndex(paramInt1);
    if ((paramInt2 >= 0) && (paramInt2 < 8))
    {
      paramWakePathRuleData = (List)paramWakePathRuleData.mWakePathRuleInfosList.get(paramInt2);
      if ((paramWakePathRuleData != null) && (paramWakePathRuleData.size() != 0))
      {
        int i = paramWakePathRuleData.size();
        for (paramInt2 = 0; paramInt2 < i; paramInt2++) {
          if (((WakePathRuleInfo)paramWakePathRuleData.get(paramInt2)).equals(paramString1, paramString2, paramString3, paramString4, paramInt1)) {
            return true;
          }
        }
        return false;
      }
      return false;
    }
    Slog.e(TAG, "MIUILOG-WAKEPATH invalid parameter");
    return false;
  }
  
  private boolean shouldBlockServiceAndProvider(String paramString1, String paramString2, int paramInt)
  {
    String str = null;
    Object localObject = null;
    boolean bool = false;
    try
    {
      paramString1 = this.mPackageManager.getPackageInfo(paramString1, 0, paramInt);
      str = paramString1;
      paramString2 = this.mPackageManager.getPackageInfo(paramString2, 0, paramInt);
    }
    catch (Exception paramString1)
    {
      Log.e(TAG, "get PackageInfo exception!", paramString1);
      paramString2 = (String)localObject;
      paramString1 = str;
    }
    if ((paramString1 != null) && (paramString2 != null))
    {
      paramInt = UserHandle.getAppId(paramString1.applicationInfo.uid);
      int i = UserHandle.getAppId(paramString2.applicationInfo.uid);
      if ((paramInt > 10000) && (i > 10000) && (!paramString1.applicationInfo.isSystemApp()) && (!paramString2.applicationInfo.isSystemApp())) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  private void trackCallListInfo(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    int i;
    Object localObject2;
    Object localObject3;
    synchronized (this.mCallListLogLocker)
    {
      if ((!TextUtils.isEmpty(paramString3)) && (!TextUtils.isEmpty(paramString4))) {
        if (this.mCallListLogMap != null)
        {
          if (this.mCallListLogMap.size() >= 200) {
            return;
          }
          i = WakePathRuleInfo.getHashCode(paramString1, paramString2, paramString3, paramString4);
          if (i == 0)
          {
            localObject2 = TAG;
            localObject3 = new java/lang/StringBuilder;
            ((StringBuilder)localObject3).<init>();
            ((StringBuilder)localObject3).append("MIUILOG-WAKEPATH trackCallListInfo: hashCode == 0,(action =");
          }
        }
      }
    }
    throw paramString1;
  }
  
  private void updateLauncherPackageNames(Context arg1)
  {
    Object localObject1 = new ArrayList();
    Object localObject3 = new Intent("android.intent.action.MAIN");
    ((Intent)localObject3).addCategory("android.intent.category.HOME");
    try
    {
      localObject3 = ???.getPackageManager().queryIntentActivities((Intent)localObject3, 0).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        ??? = (ResolveInfo)((Iterator)localObject3).next();
        ((List)localObject1).add(???.activityInfo.packageName);
        String str = TAG;
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localStringBuilder.append("updateLauncherPackageNames =");
        localStringBuilder.append(???.activityInfo.packageName);
        Slog.i(str, localStringBuilder.toString());
      }
    }
    catch (Exception ???)
    {
      Slog.e(TAG, "updateLauncherPackageNames", ???);
    }
    synchronized (this.mLauncherPackageNames)
    {
      this.mLauncherPackageNames.clear();
      if (((List)localObject1).size() > 0)
      {
        localObject1 = ((List)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject3 = (String)((Iterator)localObject1).next();
          this.mLauncherPackageNames.add(localObject3);
        }
      }
      return;
    }
  }
  
  private int wakeTypeToRuleInfosListIndex(int paramInt)
  {
    int i = -1;
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 4)
        {
          if (paramInt != 8)
          {
            if (paramInt != 32)
            {
              if (paramInt != 64)
              {
                if (paramInt != 128)
                {
                  if (paramInt != 256) {
                    paramInt = i;
                  } else {
                    paramInt = 7;
                  }
                }
                else {
                  paramInt = 6;
                }
              }
              else {
                paramInt = 5;
              }
            }
            else {
              paramInt = 4;
            }
          }
          else {
            paramInt = 3;
          }
        }
        else {
          paramInt = 2;
        }
      }
      else {
        paramInt = 1;
      }
    }
    else {
      paramInt = 0;
    }
    return paramInt;
  }
  
  public boolean checkAllowStartActivity(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    if ((!TextUtils.isEmpty(paramString1)) && (!TextUtils.isEmpty(paramString2))) {
      synchronized (this.mWakePathConfirmDialogWhitelist)
      {
        if (this.mWakePathConfirmDialogWhitelist.contains(paramString2)) {
          return true;
        }
        synchronized (this.mLauncherPackageNames)
        {
          if (this.mLauncherPackageNames.contains(paramString1)) {
            return true;
          }
          synchronized (this.mWakePathConfirmDialogCallerWhitelist)
          {
            if (this.mWakePathConfirmDialogCallerWhitelist.contains(paramString1)) {
              return true;
            }
            synchronized (getWakePathRuleDataByUser(paramInt2))
            {
              if ((((WakePathRuleData)???).mWakePathWhiteList != null) && (((WakePathRuleData)???).mWakePathWhiteList.size() > 0) && (((WakePathRuleData)???).mWakePathWhiteList.contains(paramString2)))
              {
                recordWakePathCall(paramString1, paramString2, 1, UserHandle.getUserId(paramInt1), paramInt2, true);
                return true;
              }
              if (((WakePathRuleData)???).mAllowedStartActivityRulesMap != null)
              {
                List localList = (List)((WakePathRuleData)???).mAllowedStartActivityRulesMap.get(paramString2);
                if ((localList != null) && (localList.contains(paramString1)))
                {
                  recordWakePathCall(paramString1, paramString2, 1, UserHandle.getUserId(paramInt1), paramInt2, true);
                  return true;
                }
              }
              return false;
            }
          }
        }
      }
    }
    return true;
  }
  
  public boolean checkBroadcastWakePath(Intent paramIntent, String paramString, ApplicationInfo paramApplicationInfo, ResolveInfo paramResolveInfo, int paramInt)
  {
    if ((paramIntent != null) && (!TextUtils.isEmpty(paramString)))
    {
      String str1 = "";
      String str2 = "";
      int i = -1;
      int j = -1;
      String str3 = paramIntent.getAction();
      if (paramIntent.getComponent() != null)
      {
        str2 = paramIntent.getComponent().getClassName();
        str1 = paramIntent.getComponent().getPackageName();
      }
      String str4 = str1;
      paramIntent = str2;
      int k = j;
      if (paramResolveInfo != null)
      {
        str4 = str1;
        paramIntent = str2;
        k = j;
        if (paramResolveInfo.activityInfo != null)
        {
          k = j;
          if (paramResolveInfo.activityInfo.applicationInfo != null)
          {
            str1 = paramResolveInfo.activityInfo.applicationInfo.packageName;
            k = paramResolveInfo.activityInfo.applicationInfo.uid;
          }
          paramIntent = paramResolveInfo.activityInfo.name;
          str4 = str1;
        }
      }
      j = i;
      if (paramApplicationInfo != null) {
        j = paramApplicationInfo.uid;
      }
      if (TextUtils.equals(str4, paramString)) {
        return true;
      }
      return true ^ matchWakePathRule(str3, paramIntent, paramString, str4, j, k, 2, paramInt);
    }
    return true;
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    if (paramPrintWriter == null) {
      return;
    }
    paramPrintWriter.println("========================================WAKEPATH DUMP BEGIN========================================");
    try
    {
      synchronized (this.mUserWakePathRuleDataMap)
      {
        if (this.mUserWakePathRuleDataMap.size() > 0)
        {
          Iterator localIterator = this.mUserWakePathRuleDataMap.keySet().iterator();
          while (localIterator.hasNext())
          {
            Integer localInteger = (Integer)localIterator.next();
            WakePathRuleData localWakePathRuleData = (WakePathRuleData)this.mUserWakePathRuleDataMap.get(localInteger);
            paramPrintWriter.println("----------------------------------------");
            if (localWakePathRuleData != null)
            {
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localStringBuilder.append("userId=");
              localStringBuilder.append(localInteger);
              paramPrintWriter.println(localStringBuilder.toString());
              if (localWakePathRuleData.mWakePathWhiteList != null)
              {
                localStringBuilder = new java/lang/StringBuilder;
                localStringBuilder.<init>();
                localStringBuilder.append("whitelist=");
                localStringBuilder.append(localWakePathRuleData.mWakePathWhiteList.toString());
                paramPrintWriter.println(localStringBuilder.toString());
              }
              else
              {
                paramPrintWriter.println("whitelist is null.");
              }
              for (int i = 0; i < 8; i++) {
                if (localWakePathRuleData.mWakePathRuleInfosList.get(i) == null)
                {
                  localStringBuilder = new java/lang/StringBuilder;
                  localStringBuilder.<init>();
                  localStringBuilder.append("rule info index=");
                  localStringBuilder.append(i);
                  localStringBuilder.append(" size=0");
                  paramPrintWriter.println(localStringBuilder.toString());
                }
                else
                {
                  localStringBuilder = new java/lang/StringBuilder;
                  localStringBuilder.<init>();
                  localStringBuilder.append("rule info index=");
                  localStringBuilder.append(i);
                  localStringBuilder.append(" size=");
                  localStringBuilder.append(((List)localWakePathRuleData.mWakePathRuleInfosList.get(i)).size());
                  paramPrintWriter.println(localStringBuilder.toString());
                }
              }
            }
          }
        }
      }
      return;
    }
    catch (Exception localException)
    {
      Slog.e(TAG, "dump", localException);
      paramPrintWriter.println("========================================WAKEPATH DUMP END========================================");
    }
  }
  
  public ParceledListSlice getWakePathCallListLog()
  {
    Object localObject1 = null;
    Object localObject2 = null;
    if (this.mTrackCallListLogEnabled)
    {
      Object localObject3 = this.mCallListLogLocker;
      localObject1 = localObject2;
      try
      {
        if (this.mCallListLogMap != null)
        {
          localObject1 = new java/util/ArrayList;
          ((ArrayList)localObject1).<init>(this.mCallListLogMap.values());
          this.mCallListLogMap.clear();
        }
      }
      finally {}
    }
    if (localList == null) {
      return null;
    }
    return new ParceledListSlice(localList);
  }
  
  public void init(Context paramContext)
  {
    updateLauncherPackageNames(paramContext);
  }
  
  public boolean matchWakePathRule(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3)
  {
    return matchWakePathRule(paramString1, paramString2, paramString3, paramString4, -1, paramInt1, paramInt2, paramInt3);
  }
  
  /* Error */
  public boolean matchWakePathRule(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    // Byte code:
    //   0: aload 4
    //   2: iload 7
    //   4: invokestatic 487	miui/security/AppRunningControlManager:matchRule	(Ljava/lang/String;I)Z
    //   7: ifeq +129 -> 136
    //   10: getstatic 71	miui/security/WakePathChecker:TAG	Ljava/lang/String;
    //   13: astore 9
    //   15: new 282	java/lang/StringBuilder
    //   18: dup
    //   19: invokespecial 283	java/lang/StringBuilder:<init>	()V
    //   22: astore 10
    //   24: aload 10
    //   26: ldc_w 489
    //   29: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   32: pop
    //   33: aload 10
    //   35: iload 8
    //   37: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   40: pop
    //   41: aload 10
    //   43: ldc_w 491
    //   46: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: pop
    //   50: aload 10
    //   52: aload_3
    //   53: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: pop
    //   57: aload 10
    //   59: ldc_w 493
    //   62: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   65: pop
    //   66: aload 10
    //   68: aload 4
    //   70: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: pop
    //   74: aload 10
    //   76: ldc_w 495
    //   79: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: pop
    //   83: aload 10
    //   85: aload_2
    //   86: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: pop
    //   90: aload 10
    //   92: ldc_w 497
    //   95: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   98: pop
    //   99: aload 10
    //   101: aload_1
    //   102: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: pop
    //   106: aload 10
    //   108: ldc_w 297
    //   111: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: pop
    //   115: aload 10
    //   117: iload 7
    //   119: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   122: pop
    //   123: aload 9
    //   125: aload 10
    //   127: invokevirtual 305	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   130: invokestatic 316	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   133: pop
    //   134: iconst_1
    //   135: ireturn
    //   136: aload_0
    //   137: getfield 95	miui/security/WakePathChecker:mTrackCallListLogEnabled	Z
    //   140: ifeq +14 -> 154
    //   143: aload_0
    //   144: aload_1
    //   145: aload_2
    //   146: aload_3
    //   147: aload 4
    //   149: iload 7
    //   151: invokespecial 499	miui/security/WakePathChecker:trackCallListInfo	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V
    //   154: aload_0
    //   155: iload 8
    //   157: invokespecial 383	miui/security/WakePathChecker:getWakePathRuleDataByUser	(I)Lmiui/security/WakePathChecker$WakePathRuleData;
    //   160: astore 10
    //   162: aload 10
    //   164: monitorenter
    //   165: aload_0
    //   166: getfield 104	miui/security/WakePathChecker:mWakePathCallerWhiteList	Ljava/util/List;
    //   169: aload_3
    //   170: invokeinterface 381 2 0
    //   175: ifne +1028 -> 1203
    //   178: aload 10
    //   180: getfield 386	miui/security/WakePathChecker$WakePathRuleData:mWakePathWhiteList	Ljava/util/List;
    //   183: astore 9
    //   185: aload 9
    //   187: ifnull +45 -> 232
    //   190: aload 10
    //   192: getfield 386	miui/security/WakePathChecker$WakePathRuleData:mWakePathWhiteList	Ljava/util/List;
    //   195: invokeinterface 232 1 0
    //   200: ifle +32 -> 232
    //   203: aload 10
    //   205: getfield 386	miui/security/WakePathChecker$WakePathRuleData:mWakePathWhiteList	Ljava/util/List;
    //   208: aload 4
    //   210: invokeinterface 381 2 0
    //   215: istore 11
    //   217: iload 11
    //   219: ifeq +13 -> 232
    //   222: goto +981 -> 1203
    //   225: astore_1
    //   226: aload 10
    //   228: astore_2
    //   229: goto +1014 -> 1243
    //   232: iload 7
    //   234: bipush 8
    //   236: if_icmpne +276 -> 512
    //   239: iload 6
    //   241: ifle +271 -> 512
    //   244: aload 4
    //   246: ifnull +266 -> 512
    //   249: aload_0
    //   250: getfield 108	miui/security/WakePathChecker:mBindServiceCheckActions	Ljava/util/List;
    //   253: aload_1
    //   254: invokeinterface 381 2 0
    //   259: ifeq +246 -> 505
    //   262: invokestatic 504	android/miui/AppOpsUtils:isXOptMode	()Z
    //   265: ifne +237 -> 502
    //   268: aload_0
    //   269: getfield 149	miui/security/WakePathChecker:mAppOpsService	Lcom/android/internal/app/IAppOpsService;
    //   272: ifnull +230 -> 502
    //   275: invokestatic 510	android/os/Binder:clearCallingIdentity	()J
    //   278: lstore 12
    //   280: aload_0
    //   281: getfield 149	miui/security/WakePathChecker:mAppOpsService	Lcom/android/internal/app/IAppOpsService;
    //   284: sipush 10008
    //   287: iload 6
    //   289: aload 4
    //   291: invokeinterface 516 4 0
    //   296: ifeq +164 -> 460
    //   299: iload 5
    //   301: invokestatic 389	android/os/UserHandle:getUserId	(I)I
    //   304: istore 14
    //   306: aload_0
    //   307: aload_3
    //   308: aload 4
    //   310: iload 7
    //   312: iload 14
    //   314: iload 8
    //   316: iconst_0
    //   317: invokevirtual 393	miui/security/WakePathChecker:recordWakePathCall	(Ljava/lang/String;Ljava/lang/String;IIIZ)V
    //   320: getstatic 71	miui/security/WakePathChecker:TAG	Ljava/lang/String;
    //   323: astore 9
    //   325: new 282	java/lang/StringBuilder
    //   328: astore 15
    //   330: aload 15
    //   332: invokespecial 283	java/lang/StringBuilder:<init>	()V
    //   335: aload 15
    //   337: ldc_w 518
    //   340: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   343: pop
    //   344: aload 15
    //   346: iload 8
    //   348: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   351: pop
    //   352: aload 15
    //   354: ldc_w 491
    //   357: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   360: pop
    //   361: aload 15
    //   363: aload_3
    //   364: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   367: pop
    //   368: aload 15
    //   370: ldc_w 493
    //   373: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   376: pop
    //   377: aload 15
    //   379: aload 4
    //   381: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   384: pop
    //   385: aload 15
    //   387: ldc_w 495
    //   390: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   393: pop
    //   394: aload 15
    //   396: aload_2
    //   397: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   400: pop
    //   401: aload 15
    //   403: ldc_w 497
    //   406: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   409: pop
    //   410: aload 15
    //   412: aload_1
    //   413: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   416: pop
    //   417: aload 15
    //   419: ldc_w 297
    //   422: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   425: pop
    //   426: aload 15
    //   428: iload 7
    //   430: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   433: pop
    //   434: aload 9
    //   436: aload 15
    //   438: invokevirtual 305	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   441: invokestatic 316	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   444: pop
    //   445: lload 12
    //   447: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   450: aload 10
    //   452: monitorexit
    //   453: iconst_1
    //   454: ireturn
    //   455: astore 9
    //   457: goto +17 -> 474
    //   460: lload 12
    //   462: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   465: goto +47 -> 512
    //   468: astore_1
    //   469: goto +26 -> 495
    //   472: astore 9
    //   474: getstatic 71	miui/security/WakePathChecker:TAG	Ljava/lang/String;
    //   477: ldc_w 523
    //   480: aload 9
    //   482: invokestatic 178	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   485: pop
    //   486: lload 12
    //   488: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   491: goto -26 -> 465
    //   494: astore_1
    //   495: lload 12
    //   497: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   500: aload_1
    //   501: athrow
    //   502: goto +10 -> 512
    //   505: goto +7 -> 512
    //   508: astore_1
    //   509: goto +731 -> 1240
    //   512: aload 10
    //   514: astore 9
    //   516: getstatic 526	miui/os/Build:IS_INTERNATIONAL_BUILD	Z
    //   519: ifne +400 -> 919
    //   522: aload_0
    //   523: getfield 86	miui/security/WakePathChecker:mSupportWakePathV2	Z
    //   526: istore 11
    //   528: iload 11
    //   530: ifeq +389 -> 919
    //   533: iload 7
    //   535: bipush 8
    //   537: if_icmpeq +21 -> 558
    //   540: iload 7
    //   542: iconst_4
    //   543: if_icmpeq +15 -> 558
    //   546: iload 7
    //   548: iconst_2
    //   549: if_icmpne +6 -> 555
    //   552: goto +6 -> 558
    //   555: goto +364 -> 919
    //   558: iload 6
    //   560: ifle +359 -> 919
    //   563: aload 4
    //   565: ifnull +354 -> 919
    //   568: invokestatic 504	android/miui/AppOpsUtils:isXOptMode	()Z
    //   571: ifne +338 -> 909
    //   574: aload_0
    //   575: getfield 149	miui/security/WakePathChecker:mAppOpsService	Lcom/android/internal/app/IAppOpsService;
    //   578: ifnull +331 -> 909
    //   581: aload_0
    //   582: getfield 157	miui/security/WakePathChecker:mPackageManager	Landroid/content/pm/IPackageManager;
    //   585: ifnull +324 -> 909
    //   588: invokestatic 510	android/os/Binder:clearCallingIdentity	()J
    //   591: lstore 12
    //   593: aload_0
    //   594: aload_3
    //   595: aload 4
    //   597: iload 8
    //   599: invokespecial 528	miui/security/WakePathChecker:shouldBlockServiceAndProvider	(Ljava/lang/String;Ljava/lang/String;I)Z
    //   602: ifeq +249 -> 851
    //   605: aload_0
    //   606: getfield 149	miui/security/WakePathChecker:mAppOpsService	Lcom/android/internal/app/IAppOpsService;
    //   609: sipush 10008
    //   612: iload 6
    //   614: aload 4
    //   616: invokeinterface 516 4 0
    //   621: istore 6
    //   623: iload 6
    //   625: ifeq +223 -> 848
    //   628: aload_0
    //   629: aload 9
    //   631: aload_1
    //   632: aload_2
    //   633: aload_3
    //   634: aload 4
    //   636: iload 7
    //   638: iconst_5
    //   639: ishl
    //   640: iload 8
    //   642: invokespecial 530	miui/security/WakePathChecker:matchWakePathRuleInfos	(Lmiui/security/WakePathChecker$WakePathRuleData;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)Z
    //   645: ifeq +38 -> 683
    //   648: aload_0
    //   649: aload_3
    //   650: aload 4
    //   652: iload 7
    //   654: iload 5
    //   656: invokestatic 389	android/os/UserHandle:getUserId	(I)I
    //   659: iload 8
    //   661: iconst_1
    //   662: invokevirtual 393	miui/security/WakePathChecker:recordWakePathCall	(Ljava/lang/String;Ljava/lang/String;IIIZ)V
    //   665: aload 9
    //   667: astore 10
    //   669: lload 12
    //   671: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   674: aload 9
    //   676: astore 10
    //   678: aload 9
    //   680: monitorexit
    //   681: iconst_0
    //   682: ireturn
    //   683: aload_0
    //   684: aload_3
    //   685: aload 4
    //   687: iload 7
    //   689: iload 5
    //   691: invokestatic 389	android/os/UserHandle:getUserId	(I)I
    //   694: iload 8
    //   696: iconst_0
    //   697: invokevirtual 393	miui/security/WakePathChecker:recordWakePathCall	(Ljava/lang/String;Ljava/lang/String;IIIZ)V
    //   700: getstatic 71	miui/security/WakePathChecker:TAG	Ljava/lang/String;
    //   703: astore 10
    //   705: new 282	java/lang/StringBuilder
    //   708: astore 15
    //   710: aload 15
    //   712: invokespecial 283	java/lang/StringBuilder:<init>	()V
    //   715: aload 15
    //   717: ldc_w 532
    //   720: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   723: pop
    //   724: aload 15
    //   726: iload 8
    //   728: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   731: pop
    //   732: aload 15
    //   734: ldc_w 491
    //   737: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   740: pop
    //   741: aload 15
    //   743: aload_3
    //   744: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   747: pop
    //   748: aload 15
    //   750: ldc_w 493
    //   753: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   756: pop
    //   757: aload 15
    //   759: aload 4
    //   761: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   764: pop
    //   765: aload 15
    //   767: ldc_w 495
    //   770: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   773: pop
    //   774: aload 15
    //   776: aload_2
    //   777: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   780: pop
    //   781: aload 15
    //   783: ldc_w 497
    //   786: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   789: pop
    //   790: aload 15
    //   792: aload_1
    //   793: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   796: pop
    //   797: aload 15
    //   799: ldc_w 297
    //   802: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   805: pop
    //   806: aload 15
    //   808: iload 7
    //   810: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   813: pop
    //   814: aload 10
    //   816: aload 15
    //   818: invokevirtual 305	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   821: invokestatic 316	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   824: pop
    //   825: aload 9
    //   827: astore 10
    //   829: lload 12
    //   831: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   834: aload 9
    //   836: astore 10
    //   838: aload 9
    //   840: monitorexit
    //   841: iconst_1
    //   842: ireturn
    //   843: astore 10
    //   845: goto +24 -> 869
    //   848: goto +3 -> 851
    //   851: aload 9
    //   853: astore 10
    //   855: lload 12
    //   857: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   860: goto +59 -> 919
    //   863: astore_1
    //   864: goto +30 -> 894
    //   867: astore 10
    //   869: getstatic 71	miui/security/WakePathChecker:TAG	Ljava/lang/String;
    //   872: ldc_w 523
    //   875: aload 10
    //   877: invokestatic 178	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   880: pop
    //   881: aload 9
    //   883: astore 10
    //   885: lload 12
    //   887: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   890: goto -30 -> 860
    //   893: astore_1
    //   894: aload 9
    //   896: astore 10
    //   898: lload 12
    //   900: invokestatic 522	android/os/Binder:restoreCallingIdentity	(J)V
    //   903: aload 9
    //   905: astore 10
    //   907: aload_1
    //   908: athrow
    //   909: goto +10 -> 919
    //   912: astore_1
    //   913: aload 9
    //   915: astore_2
    //   916: goto +327 -> 1243
    //   919: aload 9
    //   921: astore 10
    //   923: aload_0
    //   924: aload 9
    //   926: aload_1
    //   927: aload_2
    //   928: aload_3
    //   929: aload 4
    //   931: iload 7
    //   933: iload 8
    //   935: invokespecial 530	miui/security/WakePathChecker:matchWakePathRuleInfos	(Lmiui/security/WakePathChecker$WakePathRuleData;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)Z
    //   938: ifeq +222 -> 1160
    //   941: aload 9
    //   943: astore 10
    //   945: aload_0
    //   946: aload_3
    //   947: aload 4
    //   949: iload 7
    //   951: iload 5
    //   953: invokestatic 389	android/os/UserHandle:getUserId	(I)I
    //   956: iload 8
    //   958: iconst_0
    //   959: invokevirtual 393	miui/security/WakePathChecker:recordWakePathCall	(Ljava/lang/String;Ljava/lang/String;IIIZ)V
    //   962: aload 9
    //   964: astore 10
    //   966: getstatic 71	miui/security/WakePathChecker:TAG	Ljava/lang/String;
    //   969: astore 16
    //   971: aload 9
    //   973: astore 10
    //   975: new 282	java/lang/StringBuilder
    //   978: astore 15
    //   980: aload 9
    //   982: astore 10
    //   984: aload 15
    //   986: invokespecial 283	java/lang/StringBuilder:<init>	()V
    //   989: aload 9
    //   991: astore 10
    //   993: aload 15
    //   995: ldc_w 534
    //   998: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1001: pop
    //   1002: aload 9
    //   1004: astore 10
    //   1006: aload 15
    //   1008: iload 8
    //   1010: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1013: pop
    //   1014: aload 9
    //   1016: astore 10
    //   1018: aload 15
    //   1020: ldc_w 491
    //   1023: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1026: pop
    //   1027: aload 9
    //   1029: astore 10
    //   1031: aload 15
    //   1033: aload_3
    //   1034: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1037: pop
    //   1038: aload 9
    //   1040: astore 10
    //   1042: aload 15
    //   1044: ldc_w 493
    //   1047: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1050: pop
    //   1051: aload 9
    //   1053: astore 10
    //   1055: aload 15
    //   1057: aload 4
    //   1059: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1062: pop
    //   1063: aload 9
    //   1065: astore 10
    //   1067: aload 15
    //   1069: ldc_w 495
    //   1072: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1075: pop
    //   1076: aload 9
    //   1078: astore 10
    //   1080: aload 15
    //   1082: aload_2
    //   1083: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1086: pop
    //   1087: aload 9
    //   1089: astore 10
    //   1091: aload 15
    //   1093: ldc_w 497
    //   1096: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1099: pop
    //   1100: aload 9
    //   1102: astore 10
    //   1104: aload 15
    //   1106: aload_1
    //   1107: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1110: pop
    //   1111: aload 9
    //   1113: astore 10
    //   1115: aload 15
    //   1117: ldc_w 297
    //   1120: invokevirtual 289	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1123: pop
    //   1124: aload 9
    //   1126: astore 10
    //   1128: aload 15
    //   1130: iload 7
    //   1132: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1135: pop
    //   1136: aload 9
    //   1138: astore 10
    //   1140: aload 16
    //   1142: aload 15
    //   1144: invokevirtual 305	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1147: invokestatic 316	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1150: pop
    //   1151: aload 9
    //   1153: astore 10
    //   1155: aload 9
    //   1157: monitorexit
    //   1158: iconst_1
    //   1159: ireturn
    //   1160: iload 7
    //   1162: iconst_1
    //   1163: if_icmpeq +24 -> 1187
    //   1166: aload 9
    //   1168: astore 10
    //   1170: aload_0
    //   1171: aload_3
    //   1172: aload 4
    //   1174: iload 7
    //   1176: iload 5
    //   1178: invokestatic 389	android/os/UserHandle:getUserId	(I)I
    //   1181: iload 8
    //   1183: iconst_1
    //   1184: invokevirtual 393	miui/security/WakePathChecker:recordWakePathCall	(Ljava/lang/String;Ljava/lang/String;IIIZ)V
    //   1187: aload 9
    //   1189: astore 10
    //   1191: aload 9
    //   1193: monitorexit
    //   1194: iconst_0
    //   1195: ireturn
    //   1196: aload 10
    //   1198: astore_2
    //   1199: astore_1
    //   1200: goto +43 -> 1243
    //   1203: aload 10
    //   1205: astore_1
    //   1206: iload 7
    //   1208: iconst_1
    //   1209: if_icmpeq +23 -> 1232
    //   1212: aload_1
    //   1213: astore 10
    //   1215: aload_0
    //   1216: aload_3
    //   1217: aload 4
    //   1219: iload 7
    //   1221: iload 5
    //   1223: invokestatic 389	android/os/UserHandle:getUserId	(I)I
    //   1226: iload 8
    //   1228: iconst_1
    //   1229: invokevirtual 393	miui/security/WakePathChecker:recordWakePathCall	(Ljava/lang/String;Ljava/lang/String;IIIZ)V
    //   1232: aload_1
    //   1233: astore 10
    //   1235: aload_1
    //   1236: monitorexit
    //   1237: iconst_0
    //   1238: ireturn
    //   1239: astore_1
    //   1240: aload 10
    //   1242: astore_2
    //   1243: aload_2
    //   1244: astore 10
    //   1246: aload_2
    //   1247: monitorexit
    //   1248: aload_1
    //   1249: athrow
    //   1250: astore_1
    //   1251: aload 10
    //   1253: astore_2
    //   1254: goto -11 -> 1243
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1257	0	this	WakePathChecker
    //   0	1257	1	paramString1	String
    //   0	1257	2	paramString2	String
    //   0	1257	3	paramString3	String
    //   0	1257	4	paramString4	String
    //   0	1257	5	paramInt1	int
    //   0	1257	6	paramInt2	int
    //   0	1257	7	paramInt3	int
    //   0	1257	8	paramInt4	int
    //   13	422	9	localObject1	Object
    //   455	1	9	localException1	Exception
    //   472	9	9	localException2	Exception
    //   514	678	9	localObject2	Object
    //   22	815	10	localObject3	Object
    //   843	1	10	localException3	Exception
    //   853	1	10	localObject4	Object
    //   867	9	10	localException4	Exception
    //   883	369	10	localObject5	Object
    //   215	314	11	bool	boolean
    //   278	621	12	l	long
    //   304	9	14	i	int
    //   328	815	15	localStringBuilder	StringBuilder
    //   969	172	16	str	String
    // Exception table:
    //   from	to	target	type
    //   190	217	225	finally
    //   306	445	455	java/lang/Exception
    //   280	306	468	finally
    //   280	306	472	java/lang/Exception
    //   306	445	494	finally
    //   474	486	494	finally
    //   249	280	508	finally
    //   628	665	843	java/lang/Exception
    //   683	825	843	java/lang/Exception
    //   593	623	863	finally
    //   593	623	867	java/lang/Exception
    //   628	665	893	finally
    //   683	825	893	finally
    //   869	881	893	finally
    //   568	593	912	finally
    //   445	453	1196	finally
    //   460	465	1196	finally
    //   486	491	1196	finally
    //   495	502	1196	finally
    //   516	528	1196	finally
    //   165	185	1239	finally
    //   669	674	1250	finally
    //   678	681	1250	finally
    //   829	834	1250	finally
    //   838	841	1250	finally
    //   855	860	1250	finally
    //   885	890	1250	finally
    //   898	903	1250	finally
    //   907	909	1250	finally
    //   923	941	1250	finally
    //   945	962	1250	finally
    //   966	971	1250	finally
    //   975	980	1250	finally
    //   984	989	1250	finally
    //   993	1002	1250	finally
    //   1006	1014	1250	finally
    //   1018	1027	1250	finally
    //   1031	1038	1250	finally
    //   1042	1051	1250	finally
    //   1055	1063	1250	finally
    //   1067	1076	1250	finally
    //   1080	1087	1250	finally
    //   1091	1100	1250	finally
    //   1104	1111	1250	finally
    //   1115	1124	1250	finally
    //   1128	1136	1250	finally
    //   1140	1151	1250	finally
    //   1155	1158	1250	finally
    //   1170	1187	1250	finally
    //   1191	1194	1250	finally
    //   1215	1232	1250	finally
    //   1235	1237	1250	finally
    //   1246	1248	1250	finally
  }
  
  public void onPackageAdded(final Context paramContext)
  {
    new Thread()
    {
      public void run()
      {
        WakePathChecker.this.updateLauncherPackageNames(paramContext);
      }
    }.start();
  }
  
  public void pushUpdatePkgsData(List<String> paramList, boolean paramBoolean)
  {
    this.mUpdatePkgsEnable = paramBoolean;
    if ((paramList != null) && (paramList.size() != 0)) {
      synchronized (this.mUpdatePkgsList)
      {
        this.mUpdatePkgsList.clear();
        this.mUpdatePkgsList.addAll(paramList);
        return;
      }
    }
  }
  
  public void pushWakePathConfirmDialogWhiteList(int paramInt, List<String> paramList)
  {
    if ((paramList != null) && (paramList.size() != 0))
    {
      if (paramInt == 1) {
        synchronized (this.mWakePathConfirmDialogWhitelist)
        {
          this.mWakePathConfirmDialogWhitelist.clear();
          this.mWakePathConfirmDialogWhitelist.addAll(paramList);
        }
      }
      if (paramInt == 2) {
        synchronized (this.mWakePathConfirmDialogCallerWhitelist)
        {
          this.mWakePathConfirmDialogCallerWhitelist.clear();
          this.mWakePathConfirmDialogCallerWhitelist.addAll(paramList);
        }
      }
      return;
    }
  }
  
  public void pushWakePathRuleInfos(int paramInt1, List<WakePathRuleInfo> paramList, int paramInt2)
  {
    Object localObject1 = TAG;
    Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("MIUILOG-WAKEPATH pushWakePathRuleInfos: wakeType=");
    ((StringBuilder)localObject2).append(paramInt1);
    ((StringBuilder)localObject2).append(" userId=");
    ((StringBuilder)localObject2).append(paramInt2);
    ((StringBuilder)localObject2).append(" size=");
    int i;
    if (paramList == null) {
      i = 0;
    } else {
      i = paramList.size();
    }
    ((StringBuilder)localObject2).append(i);
    Slog.i((String)localObject1, ((StringBuilder)localObject2).toString());
    WakePathRuleData localWakePathRuleData = getWakePathRuleDataByUser(paramInt2);
    if (paramInt1 == 17) {}
    try
    {
      localObject1 = new java/util/HashMap;
      ((HashMap)localObject1).<init>();
      localWakePathRuleData.mAllowedStartActivityRulesMap = ((Map)localObject1);
      if (paramList != null)
      {
        for (paramInt1 = 0; paramInt1 < paramList.size(); paramInt1++)
        {
          WakePathRuleInfo localWakePathRuleInfo = (WakePathRuleInfo)paramList.get(paramInt1);
          localObject2 = (List)localWakePathRuleData.mAllowedStartActivityRulesMap.get(localWakePathRuleInfo.getCalleeExpress());
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            localObject1 = new java/util/ArrayList;
            ((ArrayList)localObject1).<init>();
            localWakePathRuleData.mAllowedStartActivityRulesMap.put(localWakePathRuleInfo.getCalleeExpress(), localObject1);
          }
          ((List)localObject1).add(localWakePathRuleInfo.getCallerExpress());
        }
        break label261;
        paramInt1 = wakeTypeToRuleInfosListIndex(paramInt1);
        if ((paramInt1 >= 0) && (paramInt1 < 8)) {
          localWakePathRuleData.mWakePathRuleInfosList.set(paramInt1, paramList);
        }
      }
      label261:
      return;
    }
    finally {}
  }
  
  public void pushWakePathWhiteList(List<String> paramList, int paramInt)
  {
    String str = TAG;
    ??? = new StringBuilder();
    ((StringBuilder)???).append("MIUILOG-WAKEPATH pushWakePathWhiteList: userId=");
    ((StringBuilder)???).append(paramInt);
    ((StringBuilder)???).append(" size=");
    int i;
    if (paramList == null) {
      i = 0;
    } else {
      i = paramList.size();
    }
    ((StringBuilder)???).append(i);
    Slog.i(str, ((StringBuilder)???).toString());
    synchronized (getWakePathRuleDataByUser(paramInt))
    {
      ((WakePathRuleData)???).mWakePathWhiteList = paramList;
      return;
    }
  }
  
  public void recordWakePathCall(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if ((!TextUtils.equals(paramString1, paramString2)) && (!"android".equals(paramString1)))
    {
      IWakePathCallback localIWakePathCallback = this.mCallback;
      if (localIWakePathCallback != null)
      {
        if (paramBoolean) {}
        try
        {
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append(paramString1);
          localStringBuilder.append("@");
          localStringBuilder.append(paramInt2);
          localIWakePathCallback.onAllowCall(localStringBuilder.toString(), paramString2, paramInt1, paramInt3);
          break label140;
          localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append(paramString1);
          localStringBuilder.append("@");
          localStringBuilder.append(paramInt2);
          localIWakePathCallback.onRejectCall(localStringBuilder.toString(), paramString2, paramInt1, paramInt3);
        }
        catch (RemoteException paramString1)
        {
          label140:
          paramString1.printStackTrace();
        }
      }
      return;
    }
  }
  
  public void registerWakePathCallback(IWakePathCallback paramIWakePathCallback)
  {
    this.mCallback = paramIWakePathCallback;
  }
  
  public void removeWakePathData(int paramInt)
  {
    ??? = TAG;
    Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("MIUILOG-WAKEPATH removeWakePathData: userId=");
    ((StringBuilder)localObject2).append(paramInt);
    Slog.i((String)???, ((StringBuilder)localObject2).toString());
    if ((paramInt != 0) && (!XSpaceUserHandle.isXSpaceUserId(paramInt))) {
      synchronized (this.mUserWakePathRuleDataMap)
      {
        localObject2 = (WakePathRuleData)this.mUserWakePathRuleDataMap.get(Integer.valueOf(paramInt));
        if (localObject2 != null) {
          this.mUserWakePathRuleDataMap.remove(localObject2);
        }
        return;
      }
    }
  }
  
  public void setTrackWakePathCallListLogEnabled(boolean paramBoolean)
  {
    if (paramBoolean == true) {
      return;
    }
    this.mTrackCallListLogEnabled = paramBoolean;
    if (!this.mTrackCallListLogEnabled) {
      synchronized (this.mCallListLogLocker)
      {
        if (this.mCallListLogMap != null)
        {
          this.mCallListLogMap.clear();
          this.mCallListLogMap = null;
        }
      }
    }
  }
  
  public void updatePath(Intent paramIntent, ComponentInfo paramComponentInfo, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 1) && (paramInt2 == 0) && (!Build.IS_INTERNATIONAL_BUILD))
    {
      if (this.mCallback != null)
      {
        if (!this.mUpdatePkgsEnable) {
          return;
        }
        synchronized (this.mUpdatePkgsList)
        {
          if (!this.mUpdatePkgsList.contains(paramComponentInfo.packageName)) {
            return;
          }
          try
          {
            this.mCallback.onUpdateCall(0, paramIntent, paramComponentInfo.packageName);
          }
          catch (Exception paramIntent)
          {
            ??? = TAG;
            paramComponentInfo = new StringBuilder();
            paramComponentInfo.append("updatePath error:");
            paramComponentInfo.append(paramIntent.toString());
            Log.d((String)???, paramComponentInfo.toString());
          }
        }
      }
      return;
    }
  }
  
  private class WakePathRuleData
  {
    Map<String, List<String>> mAllowedStartActivityRulesMap;
    List<List<WakePathRuleInfo>> mWakePathRuleInfosList = new ArrayList(8);
    List<String> mWakePathWhiteList;
    
    WakePathRuleData()
    {
      for (int i = 0; i < 8; i++) {
        this.mWakePathRuleInfosList.add(null);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/WakePathChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */