package com.market.sdk.utils;

import android.content.Context;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static Context f2245a;

    static {
        Class<?> a2 = d.a("android.app.ActivityThread");
        f2245a = (Context) d.a(a2, a2, "currentApplication", "()Landroid/app/Application;", new Object[0]);
    }

    public static Context a() {
        return f2245a;
    }

    public static void a(Context context) {
        f2245a = context;
    }
}
