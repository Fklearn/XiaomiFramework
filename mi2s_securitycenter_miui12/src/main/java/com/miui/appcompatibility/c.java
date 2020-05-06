package com.miui.appcompatibility;

import android.content.Context;
import b.b.c.j.z;
import com.miui.securitycenter.h;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3075a;

    c(Context context) {
        this.f3075a = context;
    }

    public void run() {
        if (z.a(h.d()) >= 3) {
            if (com.miui.securityscan.i.c.g(this.f3075a)) {
                d.b(this.f3075a).b();
            }
            h.b(System.currentTimeMillis());
        }
    }
}
