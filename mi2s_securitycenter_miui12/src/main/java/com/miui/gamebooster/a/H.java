package com.miui.gamebooster.a;

import com.miui.gamebooster.a.I;

class H implements I.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ I f4023a;

    H(I i) {
        this.f4023a = i;
    }

    public void a(int i) {
        this.f4023a.a(i);
        if (this.f4023a.f4026c != null) {
            this.f4023a.f4026c.a(i);
        }
    }

    public void a(int i, boolean z) {
        boolean unused = this.f4023a.f4025b = z;
        if (this.f4023a.f4026c != null) {
            this.f4023a.f4026c.a(i, z);
        }
        this.f4023a.a(i);
        this.f4023a.notifyDataSetChanged();
    }

    public void b(int i, boolean z) {
        this.f4023a.a(i, z);
        if (this.f4023a.f4026c != null) {
            this.f4023a.f4026c.b(i, z);
        }
        this.f4023a.notifyDataSetChanged();
    }
}
