package miui.util;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;

public class FingerprintHelper
{
  public static boolean isFingerprintHardwareDetected(Context paramContext)
  {
    paramContext = (FingerprintManager)paramContext.getSystemService("fingerprint");
    boolean bool;
    if (paramContext == null) {
      bool = false;
    } else {
      bool = paramContext.isHardwareDetected();
    }
    return bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/FingerprintHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */