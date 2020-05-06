package com.miui.luckymoney.utils;

import android.content.Context;
import android.text.format.Time;
import com.miui.luckymoney.config.CommonConfig;
import java.util.Calendar;

public class DateUtil {
    public static final long MILLIS_IN_ONE_DAY = 86400000;
    public static final long MILLIS_IN_ONE_HOUR = 3600000;
    public static final long MILLIS_IN_ONE_MINUTE = 60000;

    public static long getMillisUsingHM(int i, int i2) {
        return (((long) i) * 3600000) + (((long) i2) * 60000);
    }

    public static long getTodayTimeMillis() {
        Calendar instance = Calendar.getInstance();
        int i = instance.get(1);
        int i2 = instance.get(2);
        int i3 = instance.get(5);
        Time time = new Time("UTC");
        instance.set(i, i2, i3, 0, 0, 0);
        time.set(instance.getTimeInMillis());
        return time.toMillis(true);
    }

    public static boolean isTheSameDay(long j, long j2) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        int i = instance.get(1);
        int i2 = instance.get(2);
        int i3 = instance.get(5);
        instance.setTimeInMillis(j2);
        return i == instance.get(1) && i2 == instance.get(2) && i3 == instance.get(5);
    }

    public static boolean isTipsTimeEnable(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        CommonConfig instance = CommonConfig.getInstance(context);
        return currentTimeMillis >= instance.getFloatTipsStartTime() && currentTimeMillis < instance.getFloatTipsStopTime() + instance.getFloatTipsDuration();
    }
}
