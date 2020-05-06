package com.miui.antivirus.result;

import com.miui.securityscan.cards.g;
import java.util.Iterator;

class J implements g.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ K f2805a;

    J(K k) {
        this.f2805a = k;
    }

    public void a(String str, int i, int i2) {
        Iterator it = this.f2805a.g.iterator();
        while (it.hasNext()) {
            C0238a aVar = (C0238a) it.next();
            if ((aVar instanceof C0243f) && str.equals(((C0243f) aVar).l())) {
                this.f2805a.f();
                return;
            }
        }
    }
}
