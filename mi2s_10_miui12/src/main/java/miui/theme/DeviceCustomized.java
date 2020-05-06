package miui.theme;

import android.content.Context;
import android.os.FileUtils;
import android.os.SystemProperties;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import java.io.File;
import miui.util.FeatureParser;

public class DeviceCustomized
{
  private static void createThemeRuntimeFolder()
  {
    new File("/data/system/theme/compatibility-v12/").mkdirs();
    FileUtils.setPermissions("/data/system/theme/", 493, -1, -1);
    FileUtils.setPermissions("/data/system/theme/compatibility-v12/", 493, -1, -1);
  }
  
  private static boolean isDeviceIsProvisioned(Context paramContext)
  {
    paramContext = paramContext.getContentResolver();
    boolean bool = false;
    if (Settings.Secure.getInt(paramContext, "device_provisioned", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static void setCustomizedWallpaper(Context paramContext, File paramFile)
  {
    if ((paramContext != null) && (paramFile != null))
    {
      String str = SystemProperties.get("sys.panel.color", "");
      if ((!TextUtils.isEmpty(str)) && (!isDeviceIsProvisioned(paramContext)))
      {
        paramContext = new StringBuilder();
        paramContext.append("customized_wallpaper_");
        paramContext.append(str);
        paramContext = FeatureParser.getString(paramContext.toString());
        if ((!TextUtils.isEmpty(paramContext)) && (new File(paramContext).exists())) {
          FileUtils.copyFile(new File(paramContext), paramFile);
        }
        paramContext = new StringBuilder();
        paramContext.append("customized_lockscreen_");
        paramContext.append(str);
        paramContext = FeatureParser.getString(paramContext.toString());
        if ((!TextUtils.isEmpty(paramContext)) && (new File(paramContext).exists()))
        {
          createThemeRuntimeFolder();
          FileUtils.copyFile(new File(paramContext), new File("/data/system/theme/lock_wallpaper"));
        }
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/theme/DeviceCustomized.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */