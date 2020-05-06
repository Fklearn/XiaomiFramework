package com.miui.powercenter.batteryhistory;

import android.content.Context;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.s;
import com.miui.superpower.b.i;
import java.util.List;

/* renamed from: com.miui.powercenter.batteryhistory.y  reason: case insensitive filesystem */
public class C0520y {
    public static long a(Context context, List<aa> list) {
        return ((long) ((o.m(context) ? i.a(context, 0, 0) : o.l(context) ? s.a(context, o.c(context), o.e(context), 1) : s.a(context, o.c(context), o.e(context), 0)) * 60)) * 1000;
    }
}
