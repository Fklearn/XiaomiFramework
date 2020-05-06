package miui.securitycenter.utils;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.StatusBarManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.os.Build.VERSION;
import android.os.RemoteException;
import android.os.UserHandle;

public class SecurityCenterHelper
{
  public static void collapseStatusPanels(Context paramContext)
  {
    ((StatusBarManager)paramContext.getSystemService("statusbar")).collapsePanels();
  }
  
  public static void forceStopPackage(ActivityManager paramActivityManager, String paramString)
  {
    paramActivityManager.forceStopPackage(paramString);
    try
    {
      if (UserHandle.myUserId() != 0) {
        return;
      }
      if (Build.VERSION.SDK_INT < 20) {
        return;
      }
      if (AppGlobals.getPackageManager().getApplicationInfo(paramString, 0, 999) != null) {
        ActivityManagerNative.getDefault().forceStopPackage(paramString, 999);
      }
    }
    catch (Exception paramActivityManager)
    {
      paramActivityManager.printStackTrace();
    }
  }
  
  public static XmlResourceParser getApnsXml(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getResources().getXml(18284544);
      return paramContext;
    }
    catch (Resources.NotFoundException paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  public static int getBrightnessDimInt(Context paramContext)
  {
    return paramContext.getResources().getInteger(285868036);
  }
  
  /* Error */
  public static String getLabel(Context paramContext, String paramString, android.content.pm.ApplicationInfo paramApplicationInfo)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 79	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   4: astore_3
    //   5: aconst_null
    //   6: astore 4
    //   8: aconst_null
    //   9: astore_0
    //   10: aconst_null
    //   11: astore 5
    //   13: aload_0
    //   14: astore 6
    //   16: aload 4
    //   18: astore 7
    //   20: new 98	android/content/res/AssetManager
    //   23: astore 8
    //   25: aload_0
    //   26: astore 6
    //   28: aload 4
    //   30: astore 7
    //   32: aload 8
    //   34: invokespecial 99	android/content/res/AssetManager:<init>	()V
    //   37: aload 8
    //   39: astore_0
    //   40: aload_0
    //   41: astore 6
    //   43: aload_0
    //   44: astore 7
    //   46: aload_0
    //   47: aload_1
    //   48: invokevirtual 103	android/content/res/AssetManager:addAssetPath	(Ljava/lang/String;)I
    //   51: pop
    //   52: aload_0
    //   53: astore 6
    //   55: aload_0
    //   56: astore 7
    //   58: new 82	android/content/res/Resources
    //   61: astore 8
    //   63: aload_0
    //   64: astore 6
    //   66: aload_0
    //   67: astore 7
    //   69: aload 8
    //   71: aload_0
    //   72: aload_3
    //   73: invokevirtual 107	android/content/res/Resources:getDisplayMetrics	()Landroid/util/DisplayMetrics;
    //   76: aload_3
    //   77: invokevirtual 111	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   80: invokespecial 114	android/content/res/Resources:<init>	(Landroid/content/res/AssetManager;Landroid/util/DisplayMetrics;Landroid/content/res/Configuration;)V
    //   83: aload_0
    //   84: astore 6
    //   86: aload_0
    //   87: astore 7
    //   89: aload_2
    //   90: getfield 119	android/content/pm/ApplicationInfo:labelRes	I
    //   93: istore 9
    //   95: aload 5
    //   97: astore_1
    //   98: iload 9
    //   100: ifeq +26 -> 126
    //   103: aload_0
    //   104: astore 6
    //   106: aload_0
    //   107: astore 7
    //   109: aload 8
    //   111: aload_2
    //   112: getfield 119	android/content/pm/ApplicationInfo:labelRes	I
    //   115: invokevirtual 123	android/content/res/Resources:getText	(I)Ljava/lang/CharSequence;
    //   118: astore_1
    //   119: goto +7 -> 126
    //   122: astore_1
    //   123: aload 5
    //   125: astore_1
    //   126: aload_1
    //   127: astore 5
    //   129: aload_1
    //   130: ifnonnull +44 -> 174
    //   133: aload_0
    //   134: astore 6
    //   136: aload_0
    //   137: astore 7
    //   139: aload_2
    //   140: getfield 127	android/content/pm/ApplicationInfo:nonLocalizedLabel	Ljava/lang/CharSequence;
    //   143: ifnull +17 -> 160
    //   146: aload_0
    //   147: astore 6
    //   149: aload_0
    //   150: astore 7
    //   152: aload_2
    //   153: getfield 127	android/content/pm/ApplicationInfo:nonLocalizedLabel	Ljava/lang/CharSequence;
    //   156: astore_1
    //   157: goto +14 -> 171
    //   160: aload_0
    //   161: astore 6
    //   163: aload_0
    //   164: astore 7
    //   166: aload_2
    //   167: getfield 131	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   170: astore_1
    //   171: aload_1
    //   172: astore 5
    //   174: aload_0
    //   175: astore 6
    //   177: aload_0
    //   178: astore 7
    //   180: aload 5
    //   182: invokeinterface 137 1 0
    //   187: astore_1
    //   188: aload_0
    //   189: invokevirtual 140	android/content/res/AssetManager:close	()V
    //   192: aload_1
    //   193: areturn
    //   194: astore_0
    //   195: goto +29 -> 224
    //   198: astore_0
    //   199: aload 7
    //   201: astore 6
    //   203: ldc -115
    //   205: ldc -113
    //   207: invokestatic 149	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   210: pop
    //   211: aload 7
    //   213: ifnull +8 -> 221
    //   216: aload 7
    //   218: invokevirtual 140	android/content/res/AssetManager:close	()V
    //   221: ldc -105
    //   223: areturn
    //   224: aload 6
    //   226: ifnull +8 -> 234
    //   229: aload 6
    //   231: invokevirtual 140	android/content/res/AssetManager:close	()V
    //   234: aload_0
    //   235: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	236	0	paramContext	Context
    //   0	236	1	paramString	String
    //   0	236	2	paramApplicationInfo	android.content.pm.ApplicationInfo
    //   4	73	3	localResources	Resources
    //   6	23	4	localObject1	Object
    //   11	170	5	str	String
    //   14	216	6	localObject2	Object
    //   18	199	7	localObject3	Object
    //   23	87	8	localObject4	Object
    //   93	6	9	i	int
    // Exception table:
    //   from	to	target	type
    //   109	119	122	android/content/res/Resources$NotFoundException
    //   20	25	194	finally
    //   32	37	194	finally
    //   46	52	194	finally
    //   58	63	194	finally
    //   69	83	194	finally
    //   89	95	194	finally
    //   109	119	194	finally
    //   139	146	194	finally
    //   152	157	194	finally
    //   166	171	194	finally
    //   180	188	194	finally
    //   203	211	194	finally
    //   20	25	198	java/lang/Exception
    //   32	37	198	java/lang/Exception
    //   46	52	198	java/lang/Exception
    //   58	63	198	java/lang/Exception
    //   69	83	198	java/lang/Exception
    //   89	95	198	java/lang/Exception
    //   109	119	198	java/lang/Exception
    //   139	146	198	java/lang/Exception
    //   152	157	198	java/lang/Exception
    //   166	171	198	java/lang/Exception
    //   180	188	198	java/lang/Exception
  }
  
  public static long[] getProcessPss(int[] paramArrayOfInt)
  {
    try
    {
      paramArrayOfInt = ActivityManagerNative.getDefault().getProcessPss(paramArrayOfInt);
      return paramArrayOfInt;
    }
    catch (RemoteException paramArrayOfInt)
    {
      paramArrayOfInt.printStackTrace();
    }
    return null;
  }
  
  public static UserHandle getUserAll()
  {
    return UserHandle.ALL;
  }
  
  public static int getUserId(int paramInt)
  {
    return UserHandle.getUserId(paramInt);
  }
  
  public static boolean isAutomaticBrightnessAvailable(Context paramContext)
  {
    return paramContext.getResources().getBoolean(285474817);
  }
  
  public static boolean packageHasActiveAdmins(DevicePolicyManager paramDevicePolicyManager, String paramString)
  {
    return paramDevicePolicyManager.packageHasActiveAdmins(paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/utils/SecurityCenterHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */