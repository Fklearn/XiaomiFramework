package com.miui.powercenter.quickoptimize;

import android.content.Context;
import java.util.List;

public class o {

    /* renamed from: a  reason: collision with root package name */
    private static long f7237a = 180000;

    /* renamed from: b  reason: collision with root package name */
    private static long f7238b = 600000;

    /* renamed from: c  reason: collision with root package name */
    private static long f7239c = 1200000;

    /* renamed from: d  reason: collision with root package name */
    private static long f7240d = 900000;
    private static long e = 300000;
    private static long f = 900000;
    private static long g = 300000;
    private static long h = 600000;
    private static long i = 600000;
    private static long j = 600000;
    private static long k = 300000;

    public static int a(m mVar) {
        Object obj = mVar.f7233c;
        if (obj == null || !(obj instanceof List)) {
            return 1;
        }
        return ((List) obj).size();
    }

    public static long a(Context context, m mVar) {
        long j2;
        switch (mVar.f7231a) {
            case 1:
                j2 = f7237a * ((long) a(mVar));
                break;
            case 2:
                j2 = f7238b;
                break;
            case 3:
                j2 = f7240d;
                break;
            case 4:
                j2 = e;
                break;
            case 6:
                j2 = g;
                break;
            case 7:
                j2 = h;
                break;
            case 8:
                j2 = f7239c;
                break;
            case 9:
                j2 = f;
                break;
            case 10:
                j2 = i;
                break;
            case 11:
                j2 = j;
                break;
            case 12:
                j2 = k;
                break;
            default:
                j2 = 0;
                break;
        }
        long e2 = (j2 * ((long) com.miui.powercenter.utils.o.e(context))) / 100;
        if (e2 < 60000) {
            return 60000;
        }
        return e2;
    }
}
