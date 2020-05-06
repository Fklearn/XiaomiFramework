package com.miui.powercenter.batteryhistory;

import android.content.Context;
import android.util.Log;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.s;
import com.miui.powercenter.y;
import java.util.List;

/* renamed from: com.miui.powercenter.batteryhistory.e  reason: case insensitive filesystem */
public class C0501e {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f6878a = false;

    /* renamed from: com.miui.powercenter.batteryhistory.e$a */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public long f6879a;

        /* renamed from: b  reason: collision with root package name */
        public long f6880b;

        /* renamed from: c  reason: collision with root package name */
        public long f6881c;

        /* renamed from: d  reason: collision with root package name */
        public int f6882d;
        public int e;
        public boolean f;
        public int g;
        public boolean h;
    }

    private static long a(int i) {
        if (i == 1) {
            return y.f();
        }
        if (i == 2) {
            return y.g();
        }
        return 0;
    }

    private static long a(Context context) {
        return (c(context) / 3) + ((b(context) / 3) * 2);
    }

    public static a a(Context context, List<aa> list) {
        a b2 = b(context, list);
        if (b2.f6879a != 0) {
            a(context, b2);
        }
        float e = ((float) (100 - o.e(context))) / 100.0f;
        int i = b2.e - b2.f6882d;
        if (b2.f6879a == 0 || i < 20) {
            float f = (float) (i + 10);
            float f2 = ((float) i) / f;
            float f3 = 10.0f / f;
            long a2 = a(b2.g);
            if (a2 != 0) {
                long j = (long) (((float) a2) * e);
                if (b2.f6879a != 0) {
                    if (f6878a) {
                        Log.i("BatteryChargeTimeHelper", "Use mixed(calc) charge time,  " + e + " " + s.a(b2.f6879a) + " " + f2 + " " + s.a(j) + " " + f3);
                    }
                    b2.f6879a = (long) ((((float) b2.f6879a) * f2) + (((float) j) * f3));
                    Log.i("BatteryChargeTimeHelper", "Mixed(calc) charge time, " + e + " " + s.a(b2.f6879a));
                } else {
                    Log.i("BatteryChargeTimeHelper", "Use history time, " + e + " " + s.a(j));
                    b2.f6879a = j;
                }
            } else {
                long a3 = (long) (((float) a(context)) * e);
                if (b2.f6879a != 0) {
                    if (f6878a) {
                        Log.i("BatteryChargeTimeHelper", "Use mixed(default) charge time, " + e + " " + s.a(b2.f6879a) + " " + f2 + " " + s.a(a3) + " " + f3);
                    }
                    b2.f6879a = (long) ((((float) b2.f6879a) * f2) + (((float) a3) * f3));
                    Log.i("BatteryChargeTimeHelper", "Mixed(default) charge time " + e + " " + s.a(b2.f6879a));
                } else {
                    Log.i("BatteryChargeTimeHelper", "Use default time,  " + e + " " + s.a(a3));
                    b2.f6879a = a3;
                }
                b2.h = true;
            }
            return b2;
        }
        a(context, b2);
        if (b2.f6879a < 60000) {
            b2.f6879a = 60000;
        }
        Log.i("BatteryChargeTimeHelper", "Left charge time, " + e + " " + s.a(b2.f6879a));
        return b2;
    }

    private static void a(Context context, a aVar) {
        float e = ((float) (100 - o.e(context))) / 100.0f;
        long c2 = (long) (((float) c(context)) * e);
        if (aVar.f6879a < c2) {
            Log.w("BatteryChargeTimeHelper", "Use min, leftChargeTime " + s.a(aVar.f6879a) + " minChargeTime " + s.a(c2));
            aVar.f6879a = c2;
        } else {
            long b2 = (long) (((float) b(context)) * e);
            if (aVar.f6879a > b2) {
                Log.w("BatteryChargeTimeHelper", "Use max, leftChargeTime " + s.a(aVar.f6879a) + " maxChargeTime " + s.a(b2));
                aVar.f6879a = b2;
            } else {
                return;
            }
        }
        aVar.f = true;
    }

    private static long b(Context context) {
        return (((((long) o.c(context)) * 60) * 60) * 1000) / ((long) (o.f(context) == 2 ? 450 : 900));
    }

    private static a b(Context context, List<aa> list) {
        byte b2;
        byte b3;
        byte b4;
        long j = 0;
        long j2 = 0;
        boolean z = false;
        long j3 = 0;
        byte b5 = 0;
        loop0:
        while (true) {
            b3 = 0;
            b4 = 0;
            byte b6 = 0;
            for (aa next : list) {
                long a2 = next.a();
                byte b7 = next.f6866c;
                byte b8 = next.f6867d;
                byte b9 = next.f;
                if (next.b()) {
                    if (!z) {
                        if (b8 == 2) {
                            z = true;
                            b6 = b7;
                            b2 = b9;
                            j = a2;
                            j2 = 0;
                            j3 = 0;
                            b3 = 0;
                            b4 = 0;
                        }
                    } else if (b8 == 2 || b8 == 5) {
                        if (b3 == 0 && b7 > b6) {
                            b3 = b7;
                            j3 = a2;
                        }
                        if (b7 > b4) {
                            b4 = b7;
                            j2 = a2;
                        }
                    } else {
                        b5 = b9;
                        j = 0;
                        j2 = 0;
                        z = false;
                        j3 = 0;
                    }
                }
                b2 = b9;
            }
            break loop0;
        }
        a aVar = new a();
        if (j != 0 && j2 > j) {
            aVar.f6880b = j2 - j;
        }
        if (j3 != 0 && j2 > j3) {
            aVar.f6881c = j2 - j3;
        }
        aVar.f6882d = b3;
        aVar.e = b4;
        int i = 0;
        aVar.f = false;
        aVar.g = b2;
        aVar.h = false;
        if (b3 != 0) {
            i = b4 - b3;
        }
        long j4 = aVar.f6881c;
        if (j4 <= 180000 || i < 2) {
            aVar.f6879a = 0;
        } else {
            aVar.f6879a = (j4 * ((long) (100 - o.e(context)))) / ((long) i);
        }
        return aVar;
    }

    private static long c(Context context) {
        return (((((long) o.c(context)) * 60) * 60) * 1000) / ((long) (o.f(context) == 2 ? 1300 : 2700));
    }
}
