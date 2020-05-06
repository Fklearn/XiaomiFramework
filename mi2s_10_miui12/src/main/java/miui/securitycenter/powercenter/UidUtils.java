package miui.securitycenter.powercenter;

import android.os.UserHandle;

class UidUtils
{
  static int getRealUid(int paramInt)
  {
    if (isSharedGid(paramInt)) {
      return UserHandle.getUid(0, UserHandle.getAppIdFromSharedAppGid(paramInt));
    }
    return paramInt;
  }
  
  private static boolean isSharedGid(int paramInt)
  {
    boolean bool;
    if (UserHandle.getAppIdFromSharedAppGid(paramInt) > 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/powercenter/UidUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */