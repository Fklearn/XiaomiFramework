package miui.util;

import miui.os.MiuiInit;

public class PreinstallAppUtils
{
  public static boolean isPreinstalledPackage(String paramString)
  {
    return MiuiInit.isPreinstalledPackage(paramString);
  }
  
  public static boolean supportSignVerifyInCust()
  {
    return FeatureParser.getBoolean("support_sign_verify_in_cust", false);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/PreinstallAppUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */