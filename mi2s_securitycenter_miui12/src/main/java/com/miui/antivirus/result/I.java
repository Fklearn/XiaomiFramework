package com.miui.antivirus.result;

import com.miui.securityscan.cards.k;
import java.util.Iterator;

class I implements k.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ K f2804a;

    I(K k) {
        this.f2804a = k;
    }

    public void a(String str) {
        Iterator it = this.f2804a.g.iterator();
        while (it.hasNext()) {
            C0238a aVar = (C0238a) it.next();
            if ((aVar instanceof C0243f) && str.equals(((C0243f) aVar).l())) {
                this.f2804a.f();
                return;
            }
        }
    }
}
