package miui.security.appcompatibility;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.List;
import miui.security.SecurityManager;

public class AppCompatibilityManager
{
  public static final String ACTION_BROADCAST_APPCOMPATIBILITY_UPDATE = "com.miui.action.appcompatibility.update";
  public static final String DEVICE_NAME = Build.DEVICE;
  private static final String INTENT_ACTION_SHOW_DIALOG_FOR_APPSTORE = "com.miui.appcompatibility.LaunchDialog.appstore";
  private static final String INTENT_ACTION_SHOW_DIALOG_FOR_LAUNCHER = "com.miui.appcompatibility.LaunchDialog.launcher";
  private static final String INTENT_EXTRA_APPNAME = "app_name";
  
  public static Intent getAppErrorTipsDialogIntentForApptore(String paramString)
  {
    Intent localIntent = new Intent("com.miui.appcompatibility.LaunchDialog.appstore");
    if (!TextUtils.isEmpty(paramString)) {
      localIntent.putExtra("app_name", paramString);
    }
    return localIntent;
  }
  
  public static Intent getAppErrorTipsDialogIntentForLauncher(String paramString)
  {
    Intent localIntent = new Intent("com.miui.appcompatibility.LaunchDialog.launcher");
    if (!TextUtils.isEmpty(paramString)) {
      localIntent.putExtra("app_name", paramString);
    }
    return localIntent;
  }
  
  public static List<String> getIncompatibleAppList(Context paramContext)
  {
    return getSecurityManager(paramContext).getIncompatibleAppList();
  }
  
  private static SecurityManager getSecurityManager(Context paramContext)
  {
    return (SecurityManager)paramContext.getSystemService("security");
  }
  
  public static boolean isAppCompatible(Context paramContext, String paramString)
  {
    paramContext = getSecurityManager(paramContext).getIncompatibleAppList().iterator();
    while (paramContext.hasNext()) {
      if (((String)paramContext.next()).equals(paramString)) {
        return false;
      }
    }
    return true;
  }
  
  public static void setIncompatibleAppList(Context paramContext, List<String> paramList)
  {
    if (paramList != null) {
      getSecurityManager(paramContext).setIncompatibleAppList(paramList);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/appcompatibility/AppCompatibilityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */