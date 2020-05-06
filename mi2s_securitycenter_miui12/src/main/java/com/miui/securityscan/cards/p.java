package com.miui.securityscan.cards;

import com.miui.securityscan.L;
import com.miui.securityscan.cards.k;
import java.lang.ref.WeakReference;

public class p implements k.a {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7684a;

    public p(L l) {
        this.f7684a = new WeakReference<>(l);
    }

    public void a(String str) {
        L l = (L) this.f7684a.get();
        if (l != null) {
            c.a(l.F, str);
            c.a(l.G, str);
        }
    }
}
