package com.miui.optimizemanage;

import com.miui.optimizemanage.d.c;

class s implements c.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f5987a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ v f5988b;

    s(v vVar, int i) {
        this.f5988b = vVar;
        this.f5987a = i;
    }

    public void a(float f) {
        int i = this.f5987a;
        float f2 = (float) (i + ((int) (((float) (0 - i)) * f)));
        this.f5988b.f6009d.setTranslationY(f2);
        this.f5988b.e.setTranslationY(f2);
    }
}
