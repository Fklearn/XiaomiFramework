package miui.theme;

import android.miui.Shell;
import android.os.SELinux;
import android.util.Log;
import java.io.File;
import miui.content.res.ThemeResources;

public class ThemePermissionUtils
{
  private static final String THEME_FILE_CONTEXT = "u:object_r:theme_data_file:s0";
  
  public static boolean updateFilePermissionWithThemeContext(String paramString)
  {
    return updateFilePermissionWithThemeContext(paramString, true);
  }
  
  public static boolean updateFilePermissionWithThemeContext(String paramString, boolean paramBoolean)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    if (paramString == null) {
      return false;
    }
    Object localObject = null;
    int i = -1;
    if (paramString.startsWith("/data/system/theme/"))
    {
      localObject = new File("/data/system/theme/");
      i = 509;
    }
    else if (paramString.startsWith(ThemeResources.THEME_MAGIC_PATH))
    {
      localObject = new File(ThemeResources.THEME_MAGIC_PATH);
      i = 509;
    }
    File localFile = new File(paramString);
    if ((localObject != null) && (localFile.exists()) && (!localFile.getAbsolutePath().equals(((File)localObject).getAbsolutePath())))
    {
      boolean bool3 = false;
      boolean bool4;
      if (!paramBoolean)
      {
        bool4 = bool2;
        paramBoolean = bool3;
      }
      try
      {
        if (SELinux.setFileContext(paramString, "u:object_r:theme_data_file:s0"))
        {
          bool4 = bool2;
          if (0 != 0) {
            bool4 = true;
          }
        }
        paramBoolean = bool4;
        localObject = new java/lang/StringBuilder;
        paramBoolean = bool4;
        ((StringBuilder)localObject).<init>();
        paramBoolean = bool4;
        ((StringBuilder)localObject).append("system user update theme file: ");
        paramBoolean = bool4;
        ((StringBuilder)localObject).append(paramString);
        paramBoolean = bool4;
        ((StringBuilder)localObject).append("  ");
        paramBoolean = bool4;
        ((StringBuilder)localObject).append(bool4);
        paramBoolean = bool4;
        Log.i("Theme", ((StringBuilder)localObject).toString());
        break label291;
        paramBoolean = bool3;
        bool4 = Shell.chown(paramString, 9801, 9801);
        paramBoolean = bool4;
        if ((Shell.chmod(paramString, i)) && (bool4)) {
          paramBoolean = true;
        } else {
          paramBoolean = false;
        }
        bool4 = paramBoolean;
        paramBoolean = bool4;
        bool2 = Shell.setfilecon(paramString, "u:object_r:theme_data_file:s0");
        paramBoolean = bool1;
        if (bool2)
        {
          paramBoolean = bool1;
          if (bool4) {
            paramBoolean = true;
          }
        }
        bool4 = paramBoolean;
        label291:
        paramBoolean = bool4;
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("occur exception when updating theme file: ");
        localStringBuilder.append(paramString);
        Log.i("Theme", localStringBuilder.toString());
      }
      return paramBoolean;
    }
    return false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/theme/ThemePermissionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */