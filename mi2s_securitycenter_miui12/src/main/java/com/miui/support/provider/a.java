package com.miui.support.provider;

import android.content.Context;
import b.b.a.e.c;
import b.b.o.g.c;

public final class a {
    public static void a(Context context, boolean z) {
        c.a(context, 1, z);
        if (!c.e(context)) {
            c.a(context, 2, z);
        }
    }

    public static boolean a(Context context) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$AntiSpam");
        a2.b("isAntiSpam", new Class[]{Context.class}, context);
        return a2.a();
    }
}
