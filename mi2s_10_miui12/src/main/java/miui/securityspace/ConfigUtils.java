package miui.securityspace;

import android.os.Build.VERSION;
import miui.util.FeatureParser;

public class ConfigUtils
{
  private static boolean isKitKat()
  {
    boolean bool;
    if (Build.VERSION.SDK_INT == 19) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private static boolean isPad()
  {
    return FeatureParser.getBoolean("is_pad", false);
  }
  
  public static boolean isSupportSecuritySpace()
  {
    boolean bool;
    if ((!isPad()) && (!isKitKat())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isSupportXSpace()
  {
    return isKitKat() ^ true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securityspace/ConfigUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */