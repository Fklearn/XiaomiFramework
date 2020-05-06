package miui.securitycenter.powercenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class BatterySipper
  implements Comparable<BatterySipper>
{
  static final int AMBIENT_DISPLAY = 11;
  static final int APP = 6;
  static final int BLUETOOTH = 4;
  static final int CAMERA = 9;
  static final int CELL = 1;
  static final int FLASHLIGHT = 7;
  static final int IDLE = 0;
  static final int OTHER = 10;
  static final int PHONE = 2;
  static final int SCREEN = 5;
  static final int USER = 8;
  static final int WIFI = 3;
  long cpuFgTime;
  long cpuTime;
  String defaultPackageName;
  int drainType;
  long gpsTime;
  long mobileRxBytes;
  long mobileTxBytes;
  String name;
  double noCoveragePercent;
  int uid = -1;
  long usageTime;
  double value;
  long wakeLockTime;
  long wifiRunningTime;
  long wifiRxBytes;
  long wifiTxBytes;
  
  public BatterySipper(Context paramContext, int paramInt1, int paramInt2, double paramDouble)
  {
    this.drainType = paramInt1;
    this.value = paramDouble;
    this.uid = paramInt2;
    getNameAndPackageName(paramContext);
  }
  
  public BatterySipper(Context paramContext, String paramString, double paramDouble)
  {
    this.defaultPackageName = paramString;
    this.uid = 1000;
    this.drainType = 6;
    this.value = paramDouble;
    getOfficialName(paramContext, paramString);
  }
  
  private void getNameAndPackageName(Context paramContext)
  {
    Object localObject1 = paramContext.getPackageManager();
    paramContext = ((PackageManager)localObject1).getPackagesForUid(this.uid);
    if (paramContext == null) {
      return;
    }
    if (paramContext.length == 1)
    {
      try
      {
        localObject1 = ((PackageManager)localObject1).getApplicationLabel(((PackageManager)localObject1).getApplicationInfo(paramContext[0], 0));
        if (localObject1 != null) {
          this.name = ((CharSequence)localObject1).toString();
        }
        this.defaultPackageName = paramContext[0];
      }
      catch (PackageManager.NameNotFoundException paramContext) {}
    }
    else
    {
      int i = paramContext.length;
      for (int j = 0; j < i; j++)
      {
        String str = paramContext[j];
        try
        {
          Object localObject2 = ((PackageManager)localObject1).getPackageInfo(str, 0);
          if (((PackageInfo)localObject2).sharedUserLabel != 0)
          {
            localObject2 = ((PackageManager)localObject1).getText(str, ((PackageInfo)localObject2).sharedUserLabel, ((PackageInfo)localObject2).applicationInfo);
            if (localObject2 != null) {
              this.name = ((CharSequence)localObject2).toString();
            }
            this.defaultPackageName = str;
            break;
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
      }
    }
  }
  
  private void getOfficialName(Context paramContext, String paramString)
  {
    paramContext = paramContext.getPackageManager();
    int i = 0;
    int j = 0;
    try
    {
      CharSequence localCharSequence = paramContext.getApplicationLabel(paramContext.getApplicationInfo(paramString, 0));
      if (localCharSequence != null)
      {
        this.name = localCharSequence.toString();
        j = 1;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      j = i;
    }
    i = j;
    if (j == 0) {
      try
      {
        PackageInfo localPackageInfo = paramContext.getPackageInfo(paramString, 0);
        i = j;
        if (localPackageInfo.sharedUserLabel != 0)
        {
          paramContext = paramContext.getText(paramString, localPackageInfo.sharedUserLabel, localPackageInfo.applicationInfo);
          i = j;
          if (paramContext != null)
          {
            this.name = paramContext.toString();
            i = 1;
          }
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        i = j;
      }
    }
    if (i == 0) {
      this.name = this.defaultPackageName;
    }
  }
  
  public int compareTo(BatterySipper paramBatterySipper)
  {
    return Double.compare(paramBatterySipper.getSortValue(), getSortValue());
  }
  
  public int getDrainType()
  {
    return this.drainType;
  }
  
  public Object getObjectValue(String paramString)
  {
    if (paramString.equals("name")) {
      return this.name;
    }
    if (paramString.equals("uid")) {
      return Integer.valueOf(this.uid);
    }
    if (paramString.equals("value")) {
      return Double.valueOf(this.value);
    }
    if (paramString.equals("drainType")) {
      return Integer.valueOf(this.drainType);
    }
    if (paramString.equals("usageTime")) {
      return Long.valueOf(this.usageTime);
    }
    if (paramString.equals("cpuTime")) {
      return Long.valueOf(this.cpuTime);
    }
    if (paramString.equals("gpsTime")) {
      return Long.valueOf(this.gpsTime);
    }
    if (paramString.equals("wifiRunningTime")) {
      return Long.valueOf(this.wifiRunningTime);
    }
    if (paramString.equals("cpuFgTime")) {
      return Long.valueOf(this.cpuFgTime);
    }
    if (paramString.equals("wakeLockTime")) {
      return Long.valueOf(this.wakeLockTime);
    }
    if (paramString.equals("mobileRxBytes")) {
      return Long.valueOf(this.mobileRxBytes);
    }
    if (paramString.equals("mobileTxBytes")) {
      return Long.valueOf(this.mobileTxBytes);
    }
    if (paramString.equals("noCoveragePercent")) {
      return Double.valueOf(this.noCoveragePercent);
    }
    if (paramString.equals("defaultPackageName")) {
      return this.defaultPackageName;
    }
    if (paramString.equals("wifiRxBytes")) {
      return Long.valueOf(this.wifiRxBytes);
    }
    if (paramString.equals("wifiTxBytes")) {
      return Long.valueOf(this.wifiTxBytes);
    }
    return null;
  }
  
  public String getPackageName()
  {
    return this.defaultPackageName;
  }
  
  public double getSortValue()
  {
    return this.value;
  }
  
  public int getUid()
  {
    return this.uid;
  }
  
  public double getValue()
  {
    return this.value;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/powercenter/BatterySipper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */