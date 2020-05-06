package com.miui.cleanmaster;

import android.content.Context;
import b.b.c.j.x;
import miui.os.Build;

public class e {
    public static boolean a(Context context) {
        int e = x.e(context, "com.miui.cleanmaster");
        return Build.IS_INTERNATIONAL_BUILD ? e >= 341 : e >= 100341;
    }
}
