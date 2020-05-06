package com.miui.gamebooster.m;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import b.b.c.f.a;

/* renamed from: com.miui.gamebooster.m.v  reason: case insensitive filesystem */
public class C0390v {

    /* renamed from: a  reason: collision with root package name */
    private static C0390v f4521a;

    /* renamed from: b  reason: collision with root package name */
    private a f4522b;

    /* renamed from: c  reason: collision with root package name */
    private Context f4523c;

    /* renamed from: d  reason: collision with root package name */
    private ContentResolver f4524d;

    private C0390v(Context context) {
        this.f4523c = context;
        this.f4522b = a.a(context);
        this.f4524d = context.getApplicationContext().getContentResolver();
    }

    public static synchronized C0390v a(Context context) {
        C0390v vVar;
        synchronized (C0390v.class) {
            if (f4521a == null) {
                f4521a = new C0390v(context.getApplicationContext());
            }
            vVar = f4521a;
        }
        return vVar;
    }

    public void a() {
        this.f4522b.b("com.miui.gamebooster.service.GameBoosterServices");
    }

    public void a(a.C0027a aVar) {
        Log.i("GameBoosterManager", "getBindGameService :" + String.valueOf(this.f4522b.a("com.miui.gamebooster.service.GameBoosterServices", "com.miui.securitycenter", aVar)));
    }
}
