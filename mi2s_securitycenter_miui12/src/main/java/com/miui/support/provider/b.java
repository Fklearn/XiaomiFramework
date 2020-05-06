package com.miui.support.provider;

import android.content.Context;
import b.b.o.g.c;

public final class b {
    public static void a(Context context, boolean z) {
        c.a.a("android.provider.MiuiSettings$AntiVirus").b("setInstallMonitorEnabled", new Class[]{Context.class, Boolean.TYPE}, context, Boolean.valueOf(z));
    }

    public static boolean a(Context context) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$AntiVirus");
        a2.b("isInstallMonitorEnabled", new Class[]{Context.class}, context);
        return a2.a();
    }
}
