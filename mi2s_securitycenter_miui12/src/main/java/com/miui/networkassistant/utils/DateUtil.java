package com.miui.networkassistant.utils;

import android.content.Context;
import android.text.format.Time;
import com.miui.securitycenter.R;
import com.miui.warningcenter.WarningCenterAlertAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static final int DATE_FORMAT_ALL = 1;
    public static final int DATE_FORMAT_DAY = 2;
    public static final int DATE_FORMAT_HH_MM = 3;
    public static final int DATE_FORMAT_MM_DD_HH_MM = 4;
    public static final int DATE_FORMAT_YYYY = 6;
    public static final int DATE_FORMAT_YYYY_MM = 5;
    public static final int DAY_IN_ONE_YEAR = 365;
    public static final long MILLIS_IN_ONE_DAY = 86400000;
    public static final long MILLIS_IN_ONE_HOUR = 3600000;
    public static final long MILLIS_IN_ONE_MINUTE = 60000;

    private DateUtil() {
    }

    public static String dayInterval(long j, int i) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j + (((long) i) * 86400000));
        return String.format("%02d-%02d", new Object[]{Integer.valueOf(instance.get(2) + 1), Integer.valueOf(instance.get(5))});
    }

    public static String formatDataTime(long j, int i) {
        SimpleDateFormat dateFormat = getDateFormat(i);
        if (dateFormat != null) {
            return dateFormat.format(new Date(j));
        }
        return null;
    }

    public static String formatDataTime(long j, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(new Date(j));
    }

    public static String formatDate(long j) {
        return new SimpleDateFormat(WarningCenterAlertAdapter.FORMAT_TIME).format(new Date(j));
    }

    public static int getActualMaxDayOfMonth() {
        return Calendar.getInstance().getActualMaximum(5);
    }

    public static SimpleDateFormat getDateFormat(int i) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        switch (i) {
            case 1:
                return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            case 2:
                return new SimpleDateFormat("yyyy/MM/dd");
            case 3:
                return new SimpleDateFormat(WarningCenterAlertAdapter.FORMAT_TIME);
            case 4:
                return new SimpleDateFormat("MM/dd HH:mm");
            case 5:
                return new SimpleDateFormat("yyyy-MM");
            case 6:
                return new SimpleDateFormat("yyyy");
            default:
                return simpleDateFormat;
        }
    }

    private static int getDayInterval(long j, long j2, TimeZone timeZone) {
        long rawOffset = (long) (timeZone.getRawOffset() / 1000);
        return Time.getJulianDay(j, rawOffset) - Time.getJulianDay(j2, rawOffset);
    }

    public static int getDayOfMonth() {
        return Calendar.getInstance().get(5);
    }

    public static int getDayOfWeek() {
        return Calendar.getInstance().get(7);
    }

    public static String getFormatTime(int i, int i2) {
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
    }

    public static String getFormatedTime(Context context, long j) {
        if (j < 60000) {
            return String.format(context.getString(R.string.timeshow_s), new Object[]{Long.valueOf(j / 1000)});
        } else if (j < 3600000) {
            return String.format(context.getString(R.string.timeshow_m), new Object[]{Long.valueOf(j / 60000)});
        } else if (j < 36000000) {
            return String.format(context.getString(R.string.timeshow_h), new Object[]{Double.valueOf((((double) j) * 1.0d) / 3600000.0d), Long.valueOf((j % 3600000) / 60000)});
        } else {
            return String.format(context.getString(R.string.timeshow_h_int), new Object[]{Long.valueOf(j / 3600000)});
        }
    }

    public static int getFromNowDayInterval(long j) {
        return getDayInterval(System.currentTimeMillis(), j, TimeZone.getDefault());
    }

    public static int getHourInMilliTime(long j) {
        return (int) ((j % 86400000) / 3600000);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0048  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static long getLastMonthBeginTimeMillis(int r11) {
        /*
            java.util.Calendar r7 = java.util.Calendar.getInstance()
            r0 = 1
            int r1 = r7.get(r0)
            r2 = 2
            int r2 = r7.get(r2)
            r8 = 5
            int r3 = r7.get(r8)
            r4 = 11
            if (r3 >= r11) goto L_0x002c
            if (r2 != 0) goto L_0x0020
            r0 = 10
            int r1 = r1 + -1
            r10 = r0
            r9 = r1
            goto L_0x0036
        L_0x0020:
            if (r2 != r0) goto L_0x0027
            int r1 = r1 + -1
        L_0x0024:
            r9 = r1
            r10 = r4
            goto L_0x0036
        L_0x0027:
            int r2 = r2 + -2
            r9 = r1
            r10 = r2
            goto L_0x0036
        L_0x002c:
            if (r2 != 0) goto L_0x0030
            int r1 = r1 + -1
        L_0x0030:
            if (r2 != 0) goto L_0x0033
            goto L_0x0024
        L_0x0033:
            int r4 = r2 + -1
            goto L_0x0024
        L_0x0036:
            r3 = 1
            r4 = 0
            r5 = 0
            r6 = 0
            r0 = r7
            r1 = r9
            r2 = r10
            r0.set(r1, r2, r3, r4, r5, r6)
            int r0 = r7.getActualMaximum(r8)
            if (r11 < r0) goto L_0x0048
            r3 = r0
            goto L_0x0049
        L_0x0048:
            r3 = r11
        L_0x0049:
            r4 = 0
            r5 = 0
            r6 = 0
            r0 = r7
            r1 = r9
            r2 = r10
            r0.set(r1, r2, r3, r4, r5, r6)
            long r0 = r7.getTimeInMillis()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.utils.DateUtil.getLastMonthBeginTimeMillis(int):long");
    }

    public static long getMillisUsingHM(int i, int i2) {
        return (((long) i) * 3600000) + (((long) i2) * 60000);
    }

    public static int getMinuteInMilliTime(long j) {
        return (int) ((j % 3600000) / 60000);
    }

    public static long getNowTimeMillis() {
        Calendar instance = Calendar.getInstance();
        Time time = new Time("UTC");
        time.set(instance.getTimeInMillis());
        return time.toMillis(true);
    }

    public static long getPreMonthTimeMillis() {
        Calendar instance = Calendar.getInstance();
        instance.set(instance.get(1), instance.get(2) - 1, 0, 0, 0, 0);
        return instance.getTimeInMillis();
    }

    public static long getSomedayTimeMillis(int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        Time time = new Time("UTC");
        instance.set(i, i2 - 1, i3, 0, 0, 0);
        time.set(instance.getTimeInMillis());
        return time.toMillis(true);
    }

    public static long getThisMonthBeginTimeMillis(int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        Calendar instance = Calendar.getInstance();
        int i6 = instance.get(1);
        int i7 = instance.get(2);
        int i8 = instance.get(5);
        int actualMaximum = instance.getActualMaximum(5);
        if (i8 < i) {
            if (i7 == 0) {
                i6--;
                i5 = 11;
            } else {
                i5 = i7 - 1;
            }
            int i9 = i5;
            i3 = i6;
            i2 = i9;
            instance.set(i3, i2, 1, 0, 0, 0);
            i4 = instance.getActualMaximum(5);
        } else {
            i2 = i7;
            i3 = i6;
            i4 = actualMaximum;
        }
        instance.set(i3, i2, i >= i4 ? i4 : i, 0, 0, 0);
        return instance.getTimeInMillis();
    }

    public static long getThisMonthEndTimeMillis(int i) {
        int i2;
        Calendar instance = Calendar.getInstance();
        int i3 = instance.get(1);
        int i4 = instance.get(2);
        int i5 = instance.get(5);
        instance.set(i3, i4 + 1, 0, 0, 0, 0);
        int actualMaximum = instance.getActualMaximum(5);
        if (i5 < i) {
            if (i4 == 0) {
                i3--;
                i4 = 11;
            } else {
                i4--;
            }
            i2 = i3;
            instance.set(i2, i4, 1, 0, 0, 0);
            actualMaximum = instance.getActualMaximum(5);
        } else {
            i2 = i3;
        }
        instance.set(i2, i4 + 1, i >= actualMaximum ? actualMaximum : i, 0, 0, 0);
        return instance.getTimeInMillis();
    }

    public static long getThisWeekBeginTimeMillis() {
        return getTodayTimeMillis() - (((long) ((getDayOfWeek() + 5) % 7)) * 86400000);
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

    public static long getYesterdayTimeMillis() {
        Time time = new Time("UTC");
        time.set(getTodayTimeMillis() - 86400000);
        return time.toMillis(true);
    }

    public static boolean isCurrentCycleMonth(long j, int i) {
        return j > getThisMonthBeginTimeMillis(i) && j < getThisMonthEndTimeMillis(i);
    }

    public static boolean isCycleDay(int i) {
        return Calendar.getInstance().get(5) == i;
    }

    public static boolean isLargerOffsetDay(long j, long j2, int i) {
        return Math.abs(j - j2) > ((long) i) * 86400000;
    }

    public static boolean isLastDayOfMonth() {
        Calendar instance = Calendar.getInstance();
        return instance.get(5) == instance.getActualMaximum(5);
    }

    public static boolean isSundayOfThisWeek() {
        return getDayOfWeek() == 1;
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

    public static boolean isTheSameMonth(long j, long j2) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        int i = instance.get(1);
        int i2 = instance.get(2);
        instance.setTimeInMillis(j2);
        return i == instance.get(1) && i2 == instance.get(2);
    }

    public static String timeInterval(long j, int i) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j + (((long) i) * 3600000));
        int i2 = instance.get(11);
        return String.format("%02d:00 ~ %02d:00", new Object[]{Integer.valueOf(i2), Integer.valueOf(i2 + 1)});
    }
}
