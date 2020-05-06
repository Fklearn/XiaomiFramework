package com.miui.powercenter.bootshutdown;

import android.content.Context;
import com.miui.powercenter.utils.h;
import com.miui.powercenter.utils.s;
import com.miui.powercenter.y;
import java.util.Calendar;

public class m {
    private static int a(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTimeInMillis(System.currentTimeMillis());
        int i = instance.get(7);
        int i2 = instance2.get(7);
        int i3 = instance.get(11);
        int i4 = instance2.get(11);
        return ((i < i2 || ((i == i2 && i3 < i4) || (i == i2 && i3 == i4 && instance.get(12) < instance2.get(12)))) ? instance.get(7) + 7 : instance.get(7)) - instance2.get(7);
    }

    public static n a() {
        n nVar = new n();
        nVar.a(b());
        nVar.b(d());
        nVar.a(c());
        nVar.b(e());
        return nVar;
    }

    public static void a(Context context, int i, Calendar calendar, boolean z) {
        h.a(context, i, calendar);
        if (z) {
            y.f(calendar.getTimeInMillis());
        } else {
            y.g(calendar.getTimeInMillis());
        }
    }

    private static int b() {
        return a(y.p());
    }

    private static String c() {
        int o = y.o();
        return s.a(o / 60, o % 60);
    }

    private static int d() {
        return a(y.q());
    }

    private static String e() {
        int t = y.t();
        return s.a(t / 60, t % 60);
    }
}
