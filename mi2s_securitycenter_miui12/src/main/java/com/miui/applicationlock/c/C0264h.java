package com.miui.applicationlock.c;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import b.b.c.j.B;
import b.b.o.b.a.a;

/* renamed from: com.miui.applicationlock.c.h  reason: case insensitive filesystem */
class C0264h extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3306a;

    C0264h(Context context) {
        this.f3306a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        int l = o.l();
        if (!C0267k.h(this.f3306a) || l >= 1) {
            return null;
        }
        for (ApplicationInfo next : a.a(0, B.j())) {
            if (next != null && o.f3320d.contains(next.packageName)) {
                C0267k.d(this.f3306a);
                o.f(l + 1);
            }
        }
        return null;
    }
}
