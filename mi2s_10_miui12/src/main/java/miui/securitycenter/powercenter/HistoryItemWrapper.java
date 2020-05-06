package miui.securitycenter.powercenter;

import android.os.Build.VERSION;

public class HistoryItemWrapper
{
  static final byte CMD_CURRENT_TIME = 5;
  static final byte CMD_NULL = -1;
  static final byte CMD_OVERFLOW = 6;
  static final byte CMD_RESET = 7;
  static final byte CMD_SHUTDOWN = 8;
  static final byte CMD_START = 4;
  static final byte CMD_UPDATE = 0;
  byte batteryHealth;
  byte batteryLevel;
  byte batteryPlugType;
  byte batteryStatus;
  short batteryTemperature;
  char batteryVoltage;
  boolean charging;
  byte cmd = (byte)-1;
  boolean cpuRunning;
  boolean gpsOn;
  int phoneSignalStrength;
  boolean screenOn;
  long time;
  boolean wakelockOn;
  boolean wifiOn;
  
  public Object getObjectValue(String paramString)
  {
    if (paramString.equals("time")) {
      return Long.valueOf(this.time);
    }
    if (paramString.equals("cmd")) {
      return Integer.valueOf(this.cmd);
    }
    if (paramString.equals("batteryLevel")) {
      return Integer.valueOf(this.batteryLevel);
    }
    if (paramString.equals("batteryStatus")) {
      return Integer.valueOf(this.batteryStatus);
    }
    if (paramString.equals("batteryHealth")) {
      return Integer.valueOf(this.batteryHealth);
    }
    if (paramString.equals("batteryPlugType")) {
      return Integer.valueOf(this.batteryPlugType);
    }
    if (paramString.equals("batteryTemperature")) {
      return Integer.valueOf(this.batteryTemperature);
    }
    if (paramString.equals("batteryVoltage")) {
      return Integer.valueOf(this.batteryVoltage);
    }
    if (paramString.equals("wifiOn")) {
      return Boolean.valueOf(this.wifiOn);
    }
    if (paramString.equals("gpsOn")) {
      return Boolean.valueOf(this.gpsOn);
    }
    if (paramString.equals("charging")) {
      return Boolean.valueOf(this.charging);
    }
    if (paramString.equals("screenOn")) {
      return Boolean.valueOf(this.screenOn);
    }
    if (paramString.equals("wakelockOn")) {
      return Boolean.valueOf(this.wakelockOn);
    }
    if (paramString.equals("phoneSignalStrength")) {
      return Integer.valueOf(this.phoneSignalStrength);
    }
    if (paramString.equals("cpuRunning")) {
      return Boolean.valueOf(this.cpuRunning);
    }
    return null;
  }
  
  public long getTime()
  {
    return this.time;
  }
  
  public boolean isDeltaData()
  {
    int i = Build.VERSION.SDK_INT;
    boolean bool1 = false;
    boolean bool2 = false;
    if (i >= 21)
    {
      if (this.cmd == 0) {
        bool2 = true;
      }
      return bool2;
    }
    bool2 = bool1;
    if (this.cmd == 1) {
      bool2 = true;
    }
    return bool2;
  }
  
  public boolean isOverflow()
  {
    boolean bool;
    if (this.cmd == 6) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/powercenter/HistoryItemWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */