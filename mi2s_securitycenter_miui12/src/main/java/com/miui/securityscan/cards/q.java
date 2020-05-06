package com.miui.securityscan.cards;

import com.miui.securityscan.L;
import com.miui.securityscan.cards.g;
import java.lang.ref.WeakReference;

public class q implements g.a {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7685a;

    public q(L l) {
        this.f7685a = new WeakReference<>(l);
    }

    public void a(String str, int i, int i2) {
        L l = (L) this.f7685a.get();
        if (l != null) {
            l.a(l.F, str, i, i2);
            l.a(l.G, str, i, i2);
        }
    }
}
