package miui.securityspace;

import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;

public class XSpaceUserHandle
{
  public static final String EXTRA_AUTH_CALL_XSPACE = "android.intent.extra.auth_to_call_xspace";
  public static final int FLAG_XSPACE_PROFILE = 8388608;
  public static final int OWNER_SHARED_USER_GID = CrossUserUtilsCompat.OWNER_SHARED_USER_GID;
  public static final int USER_XSPACE = 999;
  public static final int XSPACE_ICON_MASK_ID = 285671602;
  public static final int XSPACE_SHARED_USER_GID = CrossUserUtilsCompat.XSPACE_SHARED_USER_GID;
  
  public static int checkAndGetXSpaceUserId(int paramInt1, int paramInt2)
  {
    if (isXSpaceUserFlag(paramInt1)) {
      return 999;
    }
    if (isXSpaceUserId(paramInt2)) {
      return paramInt2 + 1;
    }
    return paramInt2;
  }
  
  public static Drawable getXSpaceIcon(Context paramContext, Drawable paramDrawable)
  {
    return getXSpaceIcon(paramContext, paramDrawable, new UserHandle(999));
  }
  
  public static Drawable getXSpaceIcon(Context paramContext, Drawable paramDrawable, int paramInt)
  {
    return getXSpaceIcon(paramContext, paramDrawable, new UserHandle(UserHandle.getUserId(paramInt)));
  }
  
  public static Drawable getXSpaceIcon(Context paramContext, Drawable paramDrawable, UserHandle paramUserHandle)
  {
    if (isXSpaceUser(paramUserHandle)) {
      return CrossUserUtilsCompat.getXSpaceIcon(paramContext, paramDrawable, paramUserHandle);
    }
    return paramDrawable;
  }
  
  public static boolean isAppInXSpace(Context paramContext, String paramString)
  {
    boolean bool = false;
    if ((paramContext != null) && (paramString != null) && (paramContext.getApplicationContext() != null) && (paramContext.getApplicationContext().getContentResolver() != null)) {
      try
      {
        paramContext = IPackageManager.Stub.asInterface(ServiceManager.getService("package")).getPackageInfo(paramString, 0, 999);
        if (paramContext != null) {
          bool = true;
        }
        return bool;
      }
      catch (RemoteException paramContext)
      {
        return false;
      }
    }
    return false;
  }
  
  public static boolean isSelfXSpaceUser()
  {
    return isXSpaceUserId(UserHandle.getUserId(Process.myUid()));
  }
  
  public static boolean isUidBelongtoXSpace(int paramInt)
  {
    return isXSpaceUserId(UserHandle.getUserId(paramInt));
  }
  
  public static boolean isXSpaceUser(UserInfo paramUserInfo)
  {
    boolean bool;
    if (paramUserInfo != null) {
      bool = isXSpaceUserFlag(paramUserInfo.flags);
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isXSpaceUser(UserHandle paramUserHandle)
  {
    boolean bool;
    if (paramUserHandle != null) {
      bool = isXSpaceUserId(paramUserHandle.getIdentifier());
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isXSpaceUserCalling()
  {
    return isXSpaceUserId(UserHandle.getCallingUserId());
  }
  
  public static boolean isXSpaceUserFlag(int paramInt)
  {
    boolean bool;
    if ((paramInt & 0x800000) == 8388608) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isXSpaceUserId(int paramInt)
  {
    boolean bool;
    if (paramInt == 999) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securityspace/XSpaceUserHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */