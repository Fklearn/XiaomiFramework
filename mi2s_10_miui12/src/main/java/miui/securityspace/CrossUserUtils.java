package miui.securityspace;

import android.app.IUserSwitchObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import miui.security.ISecurityManager;
import miui.security.ISecurityManager.Stub;

public class CrossUserUtils
{
  public static final String ACTION_XSPACE_RESOLVER_ACTIVITY = "miui.intent.action.ACTION_XSPACE_RESOLVER_ACTIVITY";
  public static final String EXTRA_PICKED_USER_ID = "android.intent.extra.picked_user_id";
  public static final String EXTRA_XSPACE_RESOLVER_ACTIVITY_AIM_PACKAGE = "android.intent.extra.xspace_resolver_activity_aim_package";
  public static final String EXTRA_XSPACE_RESOLVER_ACTIVITY_CALLING_PACKAGE = "miui.intent.extra.xspace_resolver_activity_calling_package";
  public static final String EXTRA_XSPACE_RESOLVER_ACTIVITY_ORIGINAL_INTENT = "android.intent.extra.xspace_resolver_activity_original_intent";
  private static Map<String, String> noCheckContentProviderPermissionPkg;
  private static ArrayMap<Integer, WeakReference<Drawable>> sBitmapCache = new ArrayMap();
  private static ISecurityManager sISecurityManager = null;
  
  static
  {
    noCheckContentProviderPermissionPkg = new HashMap();
    noCheckContentProviderPermissionPkg.put("com.android.incallui", "contacts;com.android.contacts");
  }
  
  public static Uri addUserIdForUri(Uri paramUri, int paramInt)
  {
    return CrossUserUtilsCompat.addUserIdForUri(paramUri, paramInt);
  }
  
  public static Uri addUserIdForUri(Uri paramUri, Context paramContext, String paramString, Intent paramIntent)
  {
    return CrossUserUtilsCompat.addUserIdForUri(paramUri, paramContext, paramString, paramIntent);
  }
  
  public static boolean checkCrossPermission(String paramString, int paramInt)
  {
    return (paramString != null) && (noCheckContentProviderPermissionPkg.containsKey(paramString)) && (paramInt == 0);
  }
  
  public static boolean checkUidPermission(Context paramContext, String paramString)
  {
    return CrossUserUtilsCompat.checkUidPermission(paramContext, paramString);
  }
  
  static Drawable createDrawableWithCache(Context paramContext, Bitmap paramBitmap)
  {
    synchronized (sBitmapCache)
    {
      Object localObject = (WeakReference)sBitmapCache.get(Integer.valueOf(paramBitmap.hashCode()));
      if ((localObject != null) && (((WeakReference)localObject).get() != null))
      {
        paramContext = (Drawable)((WeakReference)localObject).get();
        return paramContext;
      }
      if (localObject != null) {
        recycleCacheMap();
      }
      localObject = new android/graphics/drawable/BitmapDrawable;
      ((BitmapDrawable)localObject).<init>(paramContext.getResources(), paramBitmap.copy(paramBitmap.getConfig(), true));
      paramContext = sBitmapCache;
      int i = paramBitmap.hashCode();
      paramBitmap = new java/lang/ref/WeakReference;
      paramBitmap.<init>(localObject);
      paramContext.put(Integer.valueOf(i), paramBitmap);
      return (Drawable)localObject;
    }
  }
  
  public static String getComponentStringWithUserId(ComponentName paramComponentName, int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramComponentName.flattenToShortString());
    localStringBuilder.append("_");
    localStringBuilder.append(paramInt);
    return localStringBuilder.toString();
  }
  
  public static String getComponentStringWithUserIdAndTaskId(ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramComponentName.flattenToShortString());
    localStringBuilder.append("_");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append("_");
    localStringBuilder.append(paramInt2);
    return localStringBuilder.toString();
  }
  
  public static int getCurrentUserId()
  {
    try
    {
      if (sISecurityManager == null) {
        sISecurityManager = ISecurityManager.Stub.asInterface(ServiceManager.getService("security"));
      }
      int i = sISecurityManager.getCurrentUserId();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    return 0;
  }
  
  public static Drawable getOriginalAppIcon(Context paramContext, String paramString)
  {
    return CrossUserUtilsCompat.getOriginalAppIcon(paramContext, paramString);
  }
  
  public static int getSecondSpaceId()
  {
    try
    {
      if (sISecurityManager == null) {
        sISecurityManager = ISecurityManager.Stub.asInterface(ServiceManager.getService("security"));
      }
      int i = sISecurityManager.getSecondSpaceId();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    return 55536;
  }
  
  public static boolean hasAirSpace(Context paramContext)
  {
    return false;
  }
  
  public static boolean hasSecondSpace(Context paramContext)
  {
    return CrossUserUtilsCompat.hasSecondSpace(paramContext);
  }
  
  public static boolean hasXSpaceUser(Context paramContext)
  {
    return CrossUserUtilsCompat.hasXSpaceUser(paramContext);
  }
  
  public static boolean isAirSpace(Context paramContext, int paramInt)
  {
    return false;
  }
  
  public static boolean needCheckUser(ProviderInfo paramProviderInfo, String paramString, int paramInt, boolean paramBoolean)
  {
    if ((paramInt == 0) && (XSpaceUserHandle.isXSpaceUserCalling())) {
      return false;
    }
    if ((paramBoolean) && (paramProviderInfo != null) && (paramString != null))
    {
      paramString = (String)noCheckContentProviderPermissionPkg.get(paramString);
      if ((paramString != null) && (paramString.equals(paramProviderInfo.authority))) {
        return false;
      }
    }
    return paramBoolean;
  }
  
  private static void recycleCacheMap()
  {
    synchronized (sBitmapCache)
    {
      Iterator localIterator = sBitmapCache.entrySet().iterator();
      while (localIterator.hasNext()) {
        if (((WeakReference)((Map.Entry)localIterator.next()).getValue()).get() == null) {
          localIterator.remove();
        }
      }
      return;
    }
  }
  
  public static void registerUserSwitchObserver(IUserSwitchObserver paramIUserSwitchObserver, String paramString)
    throws RemoteException
  {
    CrossUserUtilsCompat.registerUserSwitchObserver(paramIUserSwitchObserver, paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securityspace/CrossUserUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */