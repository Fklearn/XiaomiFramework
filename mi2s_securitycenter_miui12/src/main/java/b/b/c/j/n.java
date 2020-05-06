package b.b.c.j;

import android.content.Context;
import com.google.android.exoplayer2.C;
import com.miui.networkassistant.utils.FormatBytesUtil;
import java.util.Locale;

public class n {
    /* JADX WARNING: Removed duplicated region for block: B:11:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0055  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String a(double r5, java.lang.String r7, int r8) {
        /*
            r0 = 4652002910794678272(0x408f3c0000000000, double:999.5)
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            r1 = 0
            r2 = 1
            if (r0 <= 0) goto L_0x001b
            java.lang.Object[] r8 = new java.lang.Object[r2]
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r8[r1] = r5
            java.lang.String r5 = "%d"
        L_0x0016:
            java.lang.String r5 = java.lang.String.format(r5, r8)
            goto L_0x0053
        L_0x001b:
            r3 = 4636702106982547456(0x4058e00000000000, double:99.5)
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x002f
            java.lang.Object[] r8 = new java.lang.Object[r2]
            java.lang.Double r5 = java.lang.Double.valueOf(r5)
            r8[r1] = r5
            java.lang.String r5 = "%.01f"
            goto L_0x0016
        L_0x002f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r3 = 16
            r0.<init>(r3)
            java.lang.String r3 = "%.0"
            r0.append(r3)
            r0.append(r8)
            r8 = 102(0x66, float:1.43E-43)
            r0.append(r8)
            java.lang.String r8 = r0.toString()
            java.lang.Object[] r0 = new java.lang.Object[r2]
            java.lang.Double r5 = java.lang.Double.valueOf(r5)
            r0[r1] = r5
            java.lang.String r5 = java.lang.String.format(r8, r0)
        L_0x0053:
            if (r7 == 0) goto L_0x0064
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r6.append(r7)
            java.lang.String r5 = r6.toString()
        L_0x0064:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.j.n.a(double, java.lang.String, int):java.lang.String");
    }

    public static String a(long j) {
        long j2 = j / 1024;
        if (j2 < 1024) {
            return j2 + "M";
        }
        long j3 = (j2 * 10) / 1024;
        long j4 = j3 / 10;
        long j5 = j3 % 10;
        if (j5 != 0) {
            return j4 + "." + j5 + "G";
        }
        return j4 + "G";
    }

    private static String a(Context context, double d2, String str, int i) {
        StringBuilder sb = new StringBuilder(16);
        sb.append("%.0");
        sb.append(i);
        sb.append('f');
        String format = String.format(Locale.getDefault(), sb.toString(), new Object[]{Double.valueOf(d2)});
        if (str == null) {
            return format;
        }
        return format + str;
    }

    public static String a(Context context, long j, int i) {
        double d2;
        String str;
        if (j >= 1073741824) {
            d2 = (((double) j) * 1.0d) / 1.073741824E9d;
            str = FormatBytesUtil.getGBString(context);
        } else if (j >= 1048576) {
            d2 = (((double) j) * 1.0d) / 1048576.0d;
            str = FormatBytesUtil.getMBString(context);
        } else if (j >= 1024) {
            d2 = (((double) j) * 1.0d) / 1024.0d;
            str = FormatBytesUtil.getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            str = FormatBytesUtil.getBString(context);
        }
        return a(d2, str, i);
    }

    public static String a(Context context, long j, boolean z) {
        double d2;
        String str;
        int i = 0;
        if (j >= 1073741824) {
            d2 = (((double) j) * 1.0d) / 1.073741824E9d;
            str = FormatBytesUtil.getGBString(context);
            i = 1;
        } else if (j >= 1048576) {
            d2 = (((double) j) * 1.0d) / 1048576.0d;
            str = FormatBytesUtil.getMBString(context);
        } else if (j >= 1024) {
            d2 = (((double) j) * 1.0d) / 1024.0d;
            str = FormatBytesUtil.getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            str = FormatBytesUtil.getBString(context);
        }
        if (z) {
            d2 = Math.ceil(d2);
        }
        return a(context, d2, str, i);
    }

    public static String b(Context context, long j, int i) {
        double d2;
        String str;
        if (j >= 1073741824) {
            d2 = (((double) j) * 1.0d) / 1.073741824E9d;
            if (i == 0) {
                if (((double) Math.round(d2 * 10.0d)) / 10.0d >= Math.floor(d2) + 0.10000000149011612d) {
                    i = 1;
                }
            }
            str = FormatBytesUtil.getGBString(context);
        } else if (j >= 1048576) {
            d2 = (((double) j) * 1.0d) / 1048576.0d;
            str = FormatBytesUtil.getMBString(context);
        } else if (j >= 1024) {
            d2 = (((double) j) * 1.0d) / 1024.0d;
            str = FormatBytesUtil.getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            str = FormatBytesUtil.getBString(context);
        }
        return a(context, d2, str, i);
    }

    public static String[] c(Context context, long j, int i) {
        double d2;
        String[] strArr = new String[2];
        if (j >= 1073741824) {
            d2 = (((double) j) * 1.0d) / 1.073741824E9d;
            if (i == 0) {
                if (((double) Math.round(d2 * 10.0d)) / 10.0d >= Math.floor(d2) + 0.10000000149011612d) {
                    i = 1;
                }
            }
            strArr[1] = FormatBytesUtil.getGBString(context);
        } else if (j >= 1048576) {
            d2 = (((double) j) * 1.0d) / 1048576.0d;
            strArr[1] = FormatBytesUtil.getMBString(context);
        } else if (j >= 1024) {
            d2 = (((double) j) * 1.0d) / 1024.0d;
            strArr[1] = FormatBytesUtil.getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            strArr[1] = FormatBytesUtil.getBString(context);
        }
        strArr[0] = a(context, d2, (String) null, i);
        return strArr;
    }

    public static String d(Context context, long j, int i) {
        double d2;
        String str;
        if (j >= C.NANOS_PER_SECOND) {
            d2 = (((double) j) * 1.0d) / 1.0E9d;
            str = FormatBytesUtil.getGBString(context);
            i = 1;
        } else if (j >= 1000000) {
            d2 = (((double) j) * 1.0d) / 1000000.0d;
            str = FormatBytesUtil.getMBString(context);
        } else if (j >= 1000) {
            d2 = (((double) j) * 1.0d) / 1000.0d;
            str = FormatBytesUtil.getKBString(context);
        } else {
            d2 = ((double) j) * 1.0d;
            str = FormatBytesUtil.getBString(context);
        }
        return a(context, d2, str, i);
    }
}
