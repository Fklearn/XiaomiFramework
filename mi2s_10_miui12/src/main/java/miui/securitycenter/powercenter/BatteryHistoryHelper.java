package miui.securitycenter.powercenter;

import android.os.BatteryStats;
import android.os.BatteryStats.HistoryItem;
import android.os.SystemClock;

public class BatteryHistoryHelper
{
  private BatteryStats mStats;
  private BatteryStats.HistoryItem rec = new BatteryStats.HistoryItem();
  
  public void finishIterate()
  {
    BatteryStats localBatteryStats = this.mStats;
    if (localBatteryStats == null) {
      return;
    }
    localBatteryStats.finishIteratingHistoryLocked();
  }
  
  public long getBatteryUsageRealtime()
  {
    BatteryStats localBatteryStats = this.mStats;
    if (localBatteryStats == null) {
      return 0L;
    }
    return localBatteryStats.computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000L, 0);
  }
  
  public boolean getNextHistoryItem(HistoryItemWrapper paramHistoryItemWrapper)
  {
    BatteryStats localBatteryStats = this.mStats;
    boolean bool1 = false;
    if (localBatteryStats == null) {
      return false;
    }
    if (!localBatteryStats.getNextHistoryLocked(this.rec)) {
      return false;
    }
    paramHistoryItemWrapper.cmd = ((byte)this.rec.cmd);
    paramHistoryItemWrapper.time = this.rec.time;
    paramHistoryItemWrapper.batteryLevel = ((byte)this.rec.batteryLevel);
    paramHistoryItemWrapper.batteryStatus = ((byte)this.rec.batteryStatus);
    paramHistoryItemWrapper.batteryHealth = ((byte)this.rec.batteryHealth);
    paramHistoryItemWrapper.batteryPlugType = ((byte)this.rec.batteryPlugType);
    paramHistoryItemWrapper.batteryTemperature = ((short)this.rec.batteryTemperature);
    paramHistoryItemWrapper.batteryVoltage = ((char)this.rec.batteryVoltage);
    if ((this.rec.states2 & 0x20000000) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    paramHistoryItemWrapper.wifiOn = bool2;
    if ((this.rec.states & 0x20000000) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    paramHistoryItemWrapper.gpsOn = bool2;
    if ((this.rec.states & 0x80000) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    paramHistoryItemWrapper.charging = bool2;
    if ((this.rec.states & 0x100000) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    paramHistoryItemWrapper.screenOn = bool2;
    if ((this.rec.states & 0x40000000) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    paramHistoryItemWrapper.wakelockOn = bool2;
    boolean bool2 = bool1;
    if ((this.rec.states & 0x80000000) != 0) {
      bool2 = true;
    }
    paramHistoryItemWrapper.cpuRunning = bool2;
    int i;
    if ((this.rec.states & 0x1C0) >> 6 == 3) {
      i = 0;
    } else if ((this.rec.states & 0x200000) != 0) {
      i = 1;
    } else {
      i = ((this.rec.states & 0x38) >> 3) + 2;
    }
    paramHistoryItemWrapper.phoneSignalStrength = i;
    return true;
  }
  
  public long getScreenOnTime()
  {
    if (this.mStats == null) {
      return 0L;
    }
    long l = SystemClock.elapsedRealtime();
    l = this.mStats.getBatteryRealtime(l * 1000L);
    return this.mStats.getScreenOnTime(l, 0);
  }
  
  public void refreshHistory()
  {
    this.mStats = BatteryStatsUtils.getBatteryStats();
  }
  
  public boolean startIterate()
  {
    BatteryStats localBatteryStats = this.mStats;
    if (localBatteryStats == null) {
      return false;
    }
    return localBatteryStats.startIteratingHistoryLocked();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/powercenter/BatteryHistoryHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */