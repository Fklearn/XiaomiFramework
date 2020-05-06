package com.miui.gamebooster.model;

import android.view.View;
import b.b.c.c.a;
import com.miui.gamebooster.m.C0378i;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.m.I;
import com.miui.gamebooster.model.C;

class B implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t f4535a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f4536b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C.a f4537c;

    B(C.a aVar, t tVar, View view) {
        this.f4537c = aVar;
        this.f4535a = tVar;
        this.f4536b = view;
    }

    public void run() {
        String b2 = C0382m.b(this.f4535a.c());
        I.a(b2);
        this.f4535a.f(b2);
        if (C0378i.b(this.f4537c.f4539a, this.f4535a) && (this.f4537c.f4539a instanceof a)) {
            ((a) this.f4537c.f4539a).runOnUiThread(new A(this));
        }
    }
}
