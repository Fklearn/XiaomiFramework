package com.miui.privacyapps.ui;

import b.b.k.c;
import com.miui.privacyapps.ui.p;

class k implements p.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n f7406a;

    k(n nVar) {
        this.f7406a = nVar;
    }

    public void a(int i, c cVar) {
        int a2 = this.f7406a.l.a();
        boolean c2 = this.f7406a.l.c();
        if (a2 != 0 || !c2) {
            this.f7406a.b(cVar);
            this.f7406a.d();
            if (c2) {
                this.f7406a.l.b(false);
                return;
            }
            return;
        }
        this.f7406a.a(cVar);
    }
}
