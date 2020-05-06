package miui.securitycenter.powercenter;

import android.content.Context;
import android.os.BatteryStats.Uid;
import android.text.TextUtils;

class BatterySipperHelper
{
  static BatterySipper addBatterySipper(BatterySipper paramBatterySipper, com.android.internal.os.BatterySipper paramBatterySipper1)
  {
    paramBatterySipper.usageTime += paramBatterySipper1.usageTimeMs;
    paramBatterySipper.value += paramBatterySipper1.totalPowerMah;
    return paramBatterySipper;
  }
  
  static BatterySipper makeBatterySipper(Context paramContext, int paramInt, com.android.internal.os.BatterySipper paramBatterySipper)
  {
    if (paramBatterySipper == null) {
      return new BatterySipper(paramContext, paramInt, -1, 0.0D);
    }
    int i = -1;
    if (paramBatterySipper.uidObj != null) {
      i = UidUtils.getRealUid(paramBatterySipper.uidObj.getUid());
    }
    paramContext = new BatterySipper(paramContext, paramInt, i, paramBatterySipper.totalPowerMah);
    paramContext.usageTime = paramBatterySipper.usageTimeMs;
    paramContext.cpuTime = paramBatterySipper.cpuTimeMs;
    paramContext.gpsTime = paramBatterySipper.gpsTimeMs;
    paramContext.wifiRunningTime = paramBatterySipper.wifiRunningTimeMs;
    paramContext.cpuFgTime = paramBatterySipper.cpuFgTimeMs;
    paramContext.wakeLockTime = paramBatterySipper.wakeLockTimeMs;
    paramContext.noCoveragePercent = paramBatterySipper.noCoveragePercent;
    paramContext.mobileRxBytes = paramBatterySipper.mobileRxBytes;
    paramContext.mobileTxBytes = paramBatterySipper.mobileTxBytes;
    paramContext.wifiRxBytes = paramBatterySipper.wifiRxBytes;
    paramContext.wifiTxBytes = paramBatterySipper.wifiTxBytes;
    if (TextUtils.isEmpty(paramContext.name)) {
      paramContext.name = paramBatterySipper.packageWithHighestDrain;
    }
    return paramContext;
  }
  
  static BatterySipper makeBatterySipperForSystemApp(Context paramContext, com.android.internal.os.BatterySipper paramBatterySipper)
  {
    if (paramBatterySipper == null) {
      return null;
    }
    paramContext = new BatterySipper(paramContext, paramBatterySipper.packageWithHighestDrain, paramBatterySipper.totalPowerMah);
    paramContext.usageTime = paramBatterySipper.usageTimeMs;
    paramContext.cpuTime = paramBatterySipper.cpuTimeMs;
    paramContext.gpsTime = paramBatterySipper.gpsTimeMs;
    paramContext.wifiRunningTime = paramBatterySipper.wifiRunningTimeMs;
    paramContext.cpuFgTime = paramBatterySipper.cpuFgTimeMs;
    paramContext.wakeLockTime = paramBatterySipper.wakeLockTimeMs;
    paramContext.noCoveragePercent = paramBatterySipper.noCoveragePercent;
    paramContext.mobileRxBytes = paramBatterySipper.mobileRxBytes;
    paramContext.mobileTxBytes = paramBatterySipper.mobileTxBytes;
    paramContext.wifiRxBytes = paramBatterySipper.wifiRxBytes;
    paramContext.wifiTxBytes = paramBatterySipper.wifiTxBytes;
    return paramContext;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/powercenter/BatterySipperHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */