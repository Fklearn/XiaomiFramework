package com.miui.securityscan.b;

import com.miui.securityscan.L;
import java.lang.ref.WeakReference;

public class m implements n {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7619a;

    public m(L l) {
        this.f7619a = new WeakReference<>(l);
    }

    public void a() {
        L l = (L) this.f7619a.get();
        if (l != null) {
            l.ra = false;
        }
    }

    public void b() {
        L l = (L) this.f7619a.get();
        if (l != null) {
            l.b();
        }
    }

    public void c() {
        L l = (L) this.f7619a.get();
        if (l != null) {
            l.ra = true;
            l.m.post(new l(this, l));
        }
    }
}
