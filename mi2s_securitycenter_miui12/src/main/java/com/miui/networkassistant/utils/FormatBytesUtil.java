package com.miui.networkassistant.utils;

import android.content.Context;
import com.miui.securitycenter.R;
import com.xiaomi.stat.d;
import miui.cloud.CloudPushConstants;

public class FormatBytesUtil {
    public static final long GB = 1073741824;
    public static final long KB = 1024;
    public static final long MB = 1048576;

    public static String formatBytes(Context context, long j) {
        double d2;
        String str;
        int i = 1;
        if (j >= 1073741824) {
            d2 = (((double) j) * 1.0d) / 1.073741824E9d;
            str = getGBString(context);
            i = 2;
        } else if (j >= 1048576) {
            d2 = (((double) j) * 1.0d) / 1048576.0d;
            str = getMBString(context);
        } else if (j >= 1024) {
            d2 = (((double) j) * 1.0d) / 1024.0d;
            str = getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            str = getBString(context);
        }
        return textFormat(context, d2, str, i);
    }

    public static String formatBytes(Context context, long j, int i) {
        double d2;
        String str;
        if (j >= 1073741824) {
            d2 = (((double) j) * 1.0d) / 1.073741824E9d;
            str = getGBString(context);
        } else if (j >= 1048576) {
            d2 = (((double) j) * 1.0d) / 1048576.0d;
            str = getMBString(context);
        } else if (j >= 1024) {
            d2 = (((double) j) * 1.0d) / 1024.0d;
            str = getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            str = getBString(context);
        }
        return textFormat(context, d2, str, i);
    }

    public static String formatBytesByMB(Context context, long j) {
        return textFormat(context, (((double) j) * 1.0d) / 1048576.0d, getMBString(context), 0);
    }

    public static String formatBytesNoUint(Context context, long j) {
        double d2;
        double d3;
        double d4;
        if (j >= 1073741824) {
            d3 = ((double) j) * 1.0d;
            d4 = 1.073741824E9d;
        } else if (j > 1048576) {
            d3 = ((double) j) * 1.0d;
            d4 = 1048576.0d;
        } else if (j > 1024) {
            d3 = ((double) j) * 1.0d;
            d4 = 1024.0d;
        } else {
            d2 = ((double) j) * 1.0d;
            return textFormat(context, d2, (String) null, 1);
        }
        d2 = d3 / d4;
        return textFormat(context, d2, (String) null, 1);
    }

    public static String[] formatBytesSplited(Context context, long j) {
        double d2;
        int i = 2;
        String[] strArr = new String[2];
        if (j >= 1073741824) {
            d2 = (((double) j) * 1.0d) / 1.073741824E9d;
            strArr[1] = getGBString(context);
        } else if (j >= 1048576) {
            d2 = (((double) j) * 1.0d) / 1048576.0d;
            strArr[1] = getMBString(context);
        } else if (j >= 1024) {
            d2 = (((double) j) * 1.0d) / 1024.0d;
            strArr[1] = getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            strArr[1] = getBString(context);
            i = 0;
        }
        strArr[0] = textFormat(context, d2, (String) null, i);
        return strArr;
    }

    public static String formatBytesWithUintLong(Context context, long j) {
        return String.valueOf(j / 1048576) + getMBString(context);
    }

    public static long formatMaxBytes(long j) {
        if (j >= 1073741824) {
            return 1073741824;
        }
        if (j > 1048576) {
            return 1048576;
        }
        return j > 1024 ? 1024 : 1;
    }

    public static String[] formatSpeed(Context context, long j) {
        String[] strArr = new String[2];
        String string = context.getString(R.string.kilobyte_per_second);
        float f = ((float) j) / 1024.0f;
        if (f > 999.0f) {
            string = context.getString(R.string.megabyte_per_second);
            f /= 1024.0f;
        }
        strArr[1] = string;
        if (f < 10.0f) {
            strArr[0] = String.format("%.2f", new Object[]{Float.valueOf(f)});
        } else if (f < 100.0f) {
            strArr[0] = String.format("%.1f", new Object[]{Float.valueOf(f)});
        } else {
            strArr[0] = String.format("%.0f", new Object[]{Float.valueOf(f)});
        }
        return strArr;
    }

    public static String formatUniteUnit(Context context, long j, long j2) {
        return textFormat(context, (((double) j) * 1.0d) / ((double) j2), (String) null, 1);
    }

    public static String getBString(Context context) {
        return context.getString(R.string.byteShort);
    }

    public static long getBytesByUnit(float f, String str) {
        float f2;
        String lowerCase = str.toLowerCase();
        if (lowerCase.contains("k")) {
            f2 = 1024.0f;
        } else if (lowerCase.contains(d.V)) {
            f2 = 1048576.0f;
        } else if (!lowerCase.contains(CloudPushConstants.WATERMARK_TYPE.GLOBAL)) {
            return (long) f;
        } else {
            f2 = 1.07374182E9f;
        }
        return (long) (f * f2);
    }

    public static String getGBString(Context context) {
        return context.getString(R.string.gigabyteShort);
    }

    public static String getKBString(Context context) {
        return context.getString(R.string.kilobyteShort);
    }

    public static String getMBString(Context context) {
        return context.getString(R.string.megabyteShort);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String textFormat(android.content.Context r5, double r6, java.lang.String r8, int r9) {
        /*
            r0 = 4652002910794678272(0x408f3c0000000000, double:999.5)
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            r1 = 0
            r2 = 1
            if (r0 > 0) goto L_0x004f
            java.lang.String r5 = getBString(r5)
            boolean r5 = r5.equals(r8)
            if (r5 == 0) goto L_0x0016
            goto L_0x004f
        L_0x0016:
            r3 = 4636702106982547456(0x4058e00000000000, double:99.5)
            int r5 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x002a
            java.lang.Object[] r5 = new java.lang.Object[r2]
            java.lang.Double r6 = java.lang.Double.valueOf(r6)
            r5[r1] = r6
            java.lang.String r6 = "%.01f"
            goto L_0x005a
        L_0x002a:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r0 = 16
            r5.<init>(r0)
            java.lang.String r0 = "%.0"
            r5.append(r0)
            r5.append(r9)
            r9 = 102(0x66, float:1.43E-43)
            r5.append(r9)
            java.lang.String r5 = r5.toString()
            java.lang.Object[] r9 = new java.lang.Object[r2]
            java.lang.Double r6 = java.lang.Double.valueOf(r6)
            r9[r1] = r6
            java.lang.String r5 = java.lang.String.format(r5, r9)
            goto L_0x005e
        L_0x004f:
            java.lang.Object[] r5 = new java.lang.Object[r2]
            int r6 = (int) r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r1] = r6
            java.lang.String r6 = "%d"
        L_0x005a:
            java.lang.String r5 = java.lang.String.format(r6, r5)
        L_0x005e:
            if (r8 == 0) goto L_0x006f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r6.append(r8)
            java.lang.String r5 = r6.toString()
        L_0x006f:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.utils.FormatBytesUtil.textFormat(android.content.Context, double, java.lang.String, int):java.lang.String");
    }

    private static String[] textFormats(Context context, double d2, String str, int i) {
        String[] strArr = new String[2];
        if (d2 > 999.5d || getBString(context).equals(str)) {
            strArr[0] = String.format("%d", new Object[]{Integer.valueOf((int) d2)});
        } else if (d2 > 99.5d) {
            strArr[0] = String.format("%.01f", new Object[]{Double.valueOf(d2)});
        } else {
            StringBuilder sb = new StringBuilder(16);
            sb.append("%.0");
            sb.append(i);
            sb.append('f');
            strArr[0] = String.format(sb.toString(), new Object[]{Double.valueOf(d2)});
        }
        strArr[1] = str;
        return strArr;
    }

    public static String[] trafficFormat(Context context, long j) {
        double d2;
        String str;
        int i = 1;
        if (j >= 1073741824) {
            d2 = (((double) j) * 1.0d) / 1.073741824E9d;
            str = getGBString(context);
            i = 2;
        } else if (j >= 1048576) {
            d2 = (((double) j) * 1.0d) / 1048576.0d;
            str = getMBString(context);
        } else if (j >= 1024) {
            d2 = (((double) j) * 1.0d) / 1024.0d;
            str = getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            str = getBString(context);
        }
        return textFormats(context, d2, str, i);
    }

    public static String[] trafficUnitArray(Context context) {
        return new String[]{context.getString(R.string.megabyteShort), context.getString(R.string.gigabyteShort)};
    }
}
