package miui.securitycenter.powercenter;

import android.os.Build;

public class HistoryItemWrapper {
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
    byte cmd = CMD_NULL;
    boolean cpuRunning;
    int eventCode;
    EventTag eventTag;
    boolean gpsOn;
    int phoneSignalStrength;
    boolean screenOn;
    int states;
    int states2;
    long time;
    boolean wakelockOn;
    boolean wifiOn;

    public static final class EventTag {
        public String string;
        public int uid;
    }

    public Object getObjectValue(String str) {
        if (str.equals("time")) {
            return Long.valueOf(this.time);
        }
        if (str.equals("cmd")) {
            return Integer.valueOf(this.cmd);
        }
        if (str.equals("batteryLevel")) {
            return Integer.valueOf(this.batteryLevel);
        }
        if (str.equals("batteryStatus")) {
            return Integer.valueOf(this.batteryStatus);
        }
        if (str.equals("batteryHealth")) {
            return Integer.valueOf(this.batteryHealth);
        }
        if (str.equals("batteryPlugType")) {
            return Integer.valueOf(this.batteryPlugType);
        }
        if (str.equals("batteryTemperature")) {
            return Integer.valueOf(this.batteryTemperature);
        }
        if (str.equals("batteryVoltage")) {
            return Integer.valueOf(this.batteryVoltage);
        }
        if (str.equals("wifiOn")) {
            return Boolean.valueOf(this.wifiOn);
        }
        if (str.equals("gpsOn")) {
            return Boolean.valueOf(this.gpsOn);
        }
        if (str.equals("charging")) {
            return Boolean.valueOf(this.charging);
        }
        if (str.equals("screenOn")) {
            return Boolean.valueOf(this.screenOn);
        }
        if (str.equals("wakelockOn")) {
            return Boolean.valueOf(this.wakelockOn);
        }
        if (str.equals("phoneSignalStrength")) {
            return Integer.valueOf(this.phoneSignalStrength);
        }
        if (str.equals("states")) {
            return Integer.valueOf(this.states);
        }
        if (str.equals("states2")) {
            return Integer.valueOf(this.states2);
        }
        if (str.equals("eventCode")) {
            return Integer.valueOf(this.eventCode);
        }
        if (str.equals("eventTag")) {
            return this.eventTag;
        }
        if (str.equals("cpuRunning")) {
            return Boolean.valueOf(this.cpuRunning);
        }
        return null;
    }

    public long getTime() {
        return this.time;
    }

    public boolean isDeltaData() {
        return Build.VERSION.SDK_INT >= 21 ? this.cmd == 0 : this.cmd == 1;
    }

    public boolean isOverflow() {
        return this.cmd == 6;
    }
}
