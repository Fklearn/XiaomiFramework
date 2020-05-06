package miui.securitycenter.powercenter;

import android.content.Context;

public class BatterySipper implements Comparable<BatterySipper> {
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

    public BatterySipper(Context context, int i, int i2, double d2) {
    }

    private void getNameAndPackageName(Context context) {
    }

    public int compareTo(BatterySipper batterySipper) {
        return Double.compare(batterySipper.getSortValue(), getSortValue());
    }

    public int getDrainType() {
        return this.drainType;
    }

    public Object getObjectValue(String str) {
        return null;
    }

    public String getPackageName() {
        return this.defaultPackageName;
    }

    public double getSortValue() {
        return this.value;
    }

    public int getUid() {
        return this.uid;
    }

    public double getValue() {
        return this.value;
    }
}
