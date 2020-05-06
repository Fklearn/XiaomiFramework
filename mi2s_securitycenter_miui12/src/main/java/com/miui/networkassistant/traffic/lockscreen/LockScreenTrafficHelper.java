package com.miui.networkassistant.traffic.lockscreen;

public class LockScreenTrafficHelper {
    public static int getLockScreenLevel(long j) {
        if (j <= 524288000) {
            return 0;
        }
        if (j <= 1073741824) {
            return 1;
        }
        if (j <= 5368709120L) {
            return 2;
        }
        return j <= 10737418240L ? 3 : 4;
    }

    public static long getWarningLimitBytes(int i, long j) {
        if (i == 0) {
            return 102400;
        }
        if (i == 1) {
            return 512000;
        }
        if (i == 2) {
            return 1048576;
        }
        if (i == 3) {
            return 2097152;
        }
        if (i != 4) {
            return getWarningLimitBytes(j);
        }
        return 5242880;
    }

    public static long getWarningLimitBytes(long j) {
        if (j <= 524288000) {
            return 102400;
        }
        if (j <= 1073741824) {
            return 512000;
        }
        if (j <= 5368709120L) {
            return 1048576;
        }
        return j <= 10737418240L ? 2097152 : 5242880;
    }
}
