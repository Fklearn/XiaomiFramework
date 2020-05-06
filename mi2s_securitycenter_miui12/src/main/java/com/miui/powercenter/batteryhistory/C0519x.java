package com.miui.powercenter.batteryhistory;

import java.util.Calendar;

/* renamed from: com.miui.powercenter.batteryhistory.x  reason: case insensitive filesystem */
public class C0519x {
    public static String a(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        return instance.get(1) + "." + (instance.get(2) + 1) + "." + instance.get(5) + "  " + instance.get(11) + ":" + instance.get(12);
    }
}
