package com.miui.support.provider;

import android.content.Context;
import android.text.TextUtils;
import b.b.o.g.c;

public final class e {
    public static String a(Context context) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$VirtualSim");
        a2.b("getVirtualSimImsi", new Class[]{Context.class}, context);
        return a2.f();
    }

    public static int b(Context context) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$VirtualSim");
        a2.b("getVirtualSimSlotId", new Class[]{Context.class}, context);
        return a2.c();
    }

    public static int c(Context context) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$VirtualSim");
        a2.b("getVirtualSimStatus", new Class[]{Context.class}, context);
        return a2.c();
    }

    public static boolean d(Context context) {
        return !TextUtils.isEmpty(a(context));
    }
}
