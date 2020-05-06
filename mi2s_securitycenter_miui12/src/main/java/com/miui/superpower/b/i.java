package com.miui.superpower.b;

import android.content.Context;
import b.b.c.j.f;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;
import java.util.Locale;

public class i {
    public static int a(Context context, int i, int i2) {
        if (i2 == 0) {
            i2 = o.e(context);
        }
        if (i == 0) {
            i = o.c(context);
        }
        return (int) (((double) (((float) ((i * i2) * 60)) / ((f.c(context) ? b.b() : b.a()).floatValue() * 100.0f))) * 1.2d);
    }

    public static String b(Context context, int i, int i2) {
        int a2 = a(context, i, i2);
        return String.format(Locale.getDefault(), context.getResources().getString(R.string.superpower_progress_left_time), new Object[]{Integer.valueOf(a2 / 60), Integer.valueOf(a2 % 60)});
    }
}
