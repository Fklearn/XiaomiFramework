package com.miui.gamebooster.mutiwindow;

import android.content.Context;
import android.util.Log;
import b.b.c.f.a;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static a f4622a;

    /* renamed from: b  reason: collision with root package name */
    private b.b.c.f.a f4623b;

    private a(Context context) {
        this.f4623b = b.b.c.f.a.a(context);
    }

    public static synchronized a a(Context context) {
        a aVar;
        synchronized (a.class) {
            if (f4622a == null) {
                f4622a = new a(context.getApplicationContext());
            }
            aVar = f4622a;
        }
        return aVar;
    }

    public void a() {
        this.f4623b.b("com.miui.gamebooster.mutiwindow.FreeformWindowService");
    }

    public void a(a.C0027a aVar) {
        boolean a2 = this.f4623b.a("com.miui.gamebooster.mutiwindow.FreeformWindowService", "com.miui.securitycenter", aVar);
        Log.i("FWBinderManager", "bindFreeformWindowService: isSuccess=" + a2);
    }
}
