package org.ifaa.android.manager;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.SystemProperties;

public abstract class IFAAManager
{
  private static final int IFAA_VERSION_V2 = 2;
  private static final int IFAA_VERSION_V3 = 3;
  private static final int IFAA_VERSION_V4 = 4;
  static int sIfaaVer;
  static boolean sIsFod = SystemProperties.getBoolean("ro.hardware.fp.fod", false);
  
  static
  {
    sIfaaVer = 1;
    if (Build.VERSION.SDK_INT >= 28) {
      sIfaaVer = 4;
    } else if (sIsFod) {
      sIfaaVer = 3;
    } else if (Build.VERSION.SDK_INT >= 24) {
      sIfaaVer = 2;
    } else {
      System.loadLibrary("teeclientjni");
    }
  }
  
  public abstract String getDeviceModel();
  
  public abstract int getSupportBIOTypes(Context paramContext);
  
  public abstract int getVersion();
  
  public native byte[] processCmd(Context paramContext, byte[] paramArrayOfByte);
  
  public abstract int startBIOManager(Context paramContext, int paramInt);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/IFAAManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */