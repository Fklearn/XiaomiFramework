package com.miui.optimizemanage;

import com.miui.optimizemanage.d.c;

class l implements c.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f5940a;

    l(m mVar) {
        this.f5940a = mVar;
    }

    public void a(float f) {
        float f2 = (float) ((int) (((float) (-this.f5940a.v)) * f));
        this.f5940a.j.setTranslationY(f2);
        this.f5940a.f5941a.setTranslationY(f2);
        this.f5940a.f5942b.setTranslationY(f2);
        this.f5940a.f5943c.setTranslationY(f2);
        if (this.f5940a.o != null) {
            this.f5940a.o.a((int) (f * ((float) this.f5940a.u)));
        }
    }
}
