package android.webkit;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.os.UserManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserPackage
{
  public static final int MINIMUM_SUPPORTED_SDK = 29;
  private final PackageInfo mPackageInfo;
  private final UserInfo mUserInfo;
  
  public UserPackage(UserInfo paramUserInfo, PackageInfo paramPackageInfo)
  {
    this.mUserInfo = paramUserInfo;
    this.mPackageInfo = paramPackageInfo;
  }
  
  private static List<UserInfo> getAllUsers(Context paramContext)
  {
    return ((UserManager)paramContext.getSystemService("user")).getUsers(false);
  }
  
  public static List<UserPackage> getPackageInfosAllUsers(Context paramContext, String paramString, int paramInt)
  {
    Object localObject = getAllUsers(paramContext);
    ArrayList localArrayList = new ArrayList(((List)localObject).size());
    Iterator localIterator = ((List)localObject).iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      localObject = null;
      try
      {
        PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfoAsUser(paramString, paramInt, localUserInfo.id);
        localObject = localPackageInfo;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
      localArrayList.add(new UserPackage(localUserInfo, (PackageInfo)localObject));
    }
    return localArrayList;
  }
  
  public static boolean hasCorrectTargetSdkVersion(PackageInfo paramPackageInfo)
  {
    boolean bool;
    if (paramPackageInfo.applicationInfo.targetSdkVersion >= 29) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public PackageInfo getPackageInfo()
  {
    return this.mPackageInfo;
  }
  
  public UserInfo getUserInfo()
  {
    return this.mUserInfo;
  }
  
  public boolean isEnabledPackage()
  {
    PackageInfo localPackageInfo = this.mPackageInfo;
    if (localPackageInfo == null) {
      return false;
    }
    return localPackageInfo.applicationInfo.enabled;
  }
  
  public boolean isInstalledPackage()
  {
    PackageInfo localPackageInfo = this.mPackageInfo;
    boolean bool1 = false;
    if (localPackageInfo == null) {
      return false;
    }
    boolean bool2 = bool1;
    if ((localPackageInfo.applicationInfo.flags & 0x800000) != 0)
    {
      bool2 = bool1;
      if ((this.mPackageInfo.applicationInfo.privateFlags & 0x1) == 0) {
        bool2 = true;
      }
    }
    return bool2;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/UserPackage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */