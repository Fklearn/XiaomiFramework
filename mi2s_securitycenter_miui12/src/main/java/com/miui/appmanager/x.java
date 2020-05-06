package com.miui.appmanager;

import com.miui.appmanager.a.a;
import com.miui.appmanager.widget.d;

class x implements d.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ y f3732a;

    x(y yVar) {
        this.f3732a = yVar;
    }

    public void a(d dVar, int i) {
        if (this.f3732a.f3733a.G != i) {
            int unused = this.f3732a.f3733a.G = i;
            boolean unused2 = this.f3732a.f3733a.Q = true;
            a.e(this.f3732a.f3733a.r());
        }
    }

    public void onDismiss() {
        if (this.f3732a.f3733a.Q) {
            this.f3732a.f3733a.I.a(this.f3732a.f3733a.A[this.f3732a.f3733a.G]);
            this.f3732a.f3733a.F();
            boolean unused = this.f3732a.f3733a.Q = false;
            return;
        }
        this.f3732a.f3733a.l.notifyDataSetChanged();
    }

    public void onShow() {
    }
}
