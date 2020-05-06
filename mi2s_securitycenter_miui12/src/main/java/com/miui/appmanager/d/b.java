package com.miui.appmanager.d;

import android.content.Context;
import android.os.AsyncTask;
import b.b.c.j.x;
import com.miui.appmanager.AppManageUtils;

class b extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3667a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f3668b;

    b(c cVar, Context context) {
        this.f3668b = cVar;
        this.f3667a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        if (x.h(this.f3667a, "com.xiaomi.smarthome") && System.currentTimeMillis() - c.f3669a >= 20000) {
            AppManageUtils.i(this.f3667a);
            long unused = c.f3669a = System.currentTimeMillis();
        }
        return null;
    }
}
