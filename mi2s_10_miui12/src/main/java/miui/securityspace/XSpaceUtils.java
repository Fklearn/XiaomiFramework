package miui.securityspace;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.ParceledListSlice;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.MiuiSettings.SettingsCloudData;
import android.text.TextUtils;
import android.util.Log;
import com.miui.enterprise.ApplicationHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public class XSpaceUtils
{
  public static final String EXTRA_XSPACE_ACTUAL_USERID = "userId";
  public static final String EXTRA_XSPACE_CACHED_CALLING_RELATION = "calling_relation";
  private static final String TAG = "XSpaceUtils";
  private static final String XSPACE_BLACK_APPS_KEY = "pkgName";
  private static final String XSPACE_CLOUD_CONTROL_MODULE_NAME = "XSpace";
  private static final String XSPACE_WHITE_APPS_KEY = "whiteList";
  
  public static ArrayList<String> getXSpaceBlackApps(Context paramContext)
  {
    Object localObject1 = new ArrayList();
    Object localObject2 = localObject1;
    Object localObject3 = localObject1;
    try
    {
      String str = MiuiSettings.SettingsCloudData.getCloudDataString(paramContext.getContentResolver(), "XSpace", "pkgName", null);
      localObject2 = localObject1;
      localObject3 = localObject1;
      Object localObject4;
      if (!TextUtils.isEmpty(str))
      {
        localObject2 = localObject1;
        localObject3 = localObject1;
        localObject4 = new org/json/JSONArray;
        localObject2 = localObject1;
        localObject3 = localObject1;
        ((JSONArray)localObject4).<init>(str);
        for (int i = 0;; i++)
        {
          localObject2 = localObject1;
          localObject3 = localObject1;
          if (i >= ((JSONArray)localObject4).length()) {
            break;
          }
          localObject2 = localObject1;
          localObject3 = localObject1;
          ((ArrayList)localObject1).add(((JSONArray)localObject4).getString(i));
        }
      }
      else
      {
        localObject2 = localObject1;
        localObject3 = localObject1;
        localObject1 = XSpaceConstant.XSPACE_DEFAULT_BLACK_LIST;
      }
      localObject2 = localObject1;
      localObject3 = localObject1;
      paramContext = ApplicationHelper.getXSpaceBlackApps(paramContext).iterator();
      for (;;)
      {
        localObject2 = localObject1;
        localObject3 = localObject1;
        if (!paramContext.hasNext()) {
          break;
        }
        localObject2 = localObject1;
        localObject3 = localObject1;
        localObject4 = (String)paramContext.next();
        localObject2 = localObject1;
        localObject3 = localObject1;
        if (!((ArrayList)localObject1).contains(localObject4))
        {
          localObject2 = localObject1;
          localObject3 = localObject1;
          ((ArrayList)localObject1).add(localObject4);
        }
      }
    }
    catch (Exception paramContext)
    {
      Log.e("XSpaceUtils", "Exception when get XSpaceBlackApps :", paramContext);
      localObject1 = localObject2;
    }
    catch (JSONException paramContext)
    {
      Log.e("XSpaceUtils", "JSONException when get XSpaceBlackApps :", paramContext);
      localObject1 = localObject3;
    }
    return (ArrayList<String>)localObject1;
  }
  
  public static ArrayList<String> getXSpaceSupportPackages(Context paramContext)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = getXSpaceBlackApps(paramContext);
    Object localObject = null;
    try
    {
      ParceledListSlice localParceledListSlice = AppGlobals.getPackageManager().getInstalledPackages(0, 0);
      localObject = localParceledListSlice;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    if (localObject != null)
    {
      Iterator localIterator = ((ParceledListSlice)localObject).getList().iterator();
      while (localIterator.hasNext())
      {
        localObject = (PackageInfo)localIterator.next();
        if ((localArrayList2 == null) || (!localArrayList2.contains(((PackageInfo)localObject).packageName))) {
          if ((!isSystemApp(((PackageInfo)localObject).applicationInfo)) && (pkgHasIcon(paramContext, ((PackageInfo)localObject).packageName))) {
            localArrayList1.add(((PackageInfo)localObject).packageName);
          }
        }
      }
    }
    return localArrayList1;
  }
  
  public static ArrayList<String> getXSpaceWhiteApps(Context paramContext)
  {
    Object localObject1 = new ArrayList();
    Object localObject2 = localObject1;
    Object localObject3 = localObject1;
    try
    {
      String str = MiuiSettings.SettingsCloudData.getCloudDataString(paramContext.getContentResolver(), "XSpace", "whiteList", null);
      localObject2 = localObject1;
      localObject3 = localObject1;
      if (!TextUtils.isEmpty(str))
      {
        localObject2 = localObject1;
        localObject3 = localObject1;
        JSONArray localJSONArray = new org/json/JSONArray;
        localObject2 = localObject1;
        localObject3 = localObject1;
        localJSONArray.<init>(str);
        for (int i = 0;; i++)
        {
          localObject2 = localObject1;
          localObject3 = localObject1;
          if (i >= localJSONArray.length()) {
            break;
          }
          localObject2 = localObject1;
          localObject3 = localObject1;
          ((ArrayList)localObject1).add(localJSONArray.getString(i));
        }
      }
      else
      {
        localObject2 = localObject1;
        localObject3 = localObject1;
        localObject1 = XSpaceConstant.XSPACE_WHITELIST;
      }
      localObject2 = localObject1;
      localObject3 = localObject1;
      ((ArrayList)localObject1).removeAll(ApplicationHelper.getXSpaceBlackApps(paramContext));
    }
    catch (Exception paramContext)
    {
      Log.e("XSpaceUtils", "Exception when get XSpaceWhiteApps :", paramContext);
      localObject1 = localObject2;
    }
    catch (JSONException paramContext)
    {
      Log.e("XSpaceUtils", "JSONException when get XSpaceWhiteApps :", paramContext);
      localObject1 = localObject3;
    }
    return (ArrayList<String>)localObject1;
  }
  
  public static boolean isAppInXSpaceSupportList(Context paramContext, String paramString)
  {
    if ((paramContext != null) && (paramString != null))
    {
      paramContext = getXSpaceSupportPackages(paramContext);
      if (paramContext != null) {
        return paramContext.contains(paramString);
      }
      return false;
    }
    return false;
  }
  
  public static boolean isAppInXSpaceWhiltList(Context paramContext, String paramString)
  {
    paramContext = getXSpaceWhiteApps(paramContext);
    if ((paramContext != null) && (!paramContext.isEmpty()) && (paramString != null)) {
      return paramContext.contains(paramString);
    }
    return false;
  }
  
  public static boolean isAppInXSpaceWhiltList(String paramString)
  {
    return XSpaceConstant.XSPACE_WHITELIST.contains(paramString);
  }
  
  public static boolean isSystemApp(ApplicationInfo paramApplicationInfo)
  {
    int i = paramApplicationInfo.flags;
    boolean bool1 = true;
    boolean bool2 = bool1;
    if ((i & 0x1) <= 0) {
      if (paramApplicationInfo.uid < 10000) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
    }
    return bool2;
  }
  
  public static boolean pkgHasIcon(Context paramContext, String paramString)
  {
    return ((LauncherApps)paramContext.getSystemService("launcherapps")).getActivityList(paramString, UserHandle.OWNER).isEmpty() ^ true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securityspace/XSpaceUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */