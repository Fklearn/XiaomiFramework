package com.miui.gamebooster.ui;

import android.content.Context;
import b.b.c.c.a;
import com.miui.gamebooster.m.C0378i;
import com.miui.gamebooster.model.C0399e;
import java.util.List;

class fb implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ib f5064a;

    fb(ib ibVar) {
        this.f5064a = ibVar;
    }

    public void run() {
        if (this.f5064a.i instanceof a) {
            List<C0399e> a2 = C0378i.a((Context) this.f5064a.i, this.f5064a.f());
            this.f5064a.h.clear();
            if (a2 != null && a2.size() > 0) {
                this.f5064a.h.addAll(a2);
            }
            this.f5064a.i.runOnUiThread(new eb(this));
        }
    }
}
