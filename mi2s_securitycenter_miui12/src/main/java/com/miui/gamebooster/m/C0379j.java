package com.miui.gamebooster.m;

import android.app.Activity;
import android.support.annotation.Nullable;
import com.miui.gamebooster.c.a;

/* renamed from: com.miui.gamebooster.m.j  reason: case insensitive filesystem */
public class C0379j {
    public static void a(boolean z, @Nullable Activity activity) {
        if (activity != null) {
            if (!z) {
                C0384o.b(activity.getApplicationContext().getContentResolver(), "gb_handsfree", 0, -2);
            }
            a.N(z);
        }
    }
}
