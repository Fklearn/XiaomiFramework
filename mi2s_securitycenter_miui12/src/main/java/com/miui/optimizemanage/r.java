package com.miui.optimizemanage;

import com.miui.optimizemanage.d.c;

class r implements c.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f5986a;

    r(v vVar) {
        this.f5986a = vVar;
    }

    public void a(float f) {
        float f2 = ((double) f) <= 0.58d ? 1.0f : f * 1.7f;
        this.f5986a.f6009d.setScaleX(f2);
        this.f5986a.f6009d.setScaleY(f2);
        this.f5986a.e.setScaleX(f2);
        this.f5986a.e.setScaleY(f2);
    }
}
