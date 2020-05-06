package com.miui.securityscan.b;

import com.miui.securityscan.L;
import com.miui.securityscan.scanner.C0558e;
import com.miui.securityscan.scanner.O;
import java.lang.ref.WeakReference;

public class k implements O.d {

    /* renamed from: a  reason: collision with root package name */
    private WeakReference<L> f7616a;

    public k(L l) {
        this.f7616a = new WeakReference<>(l);
    }

    public void a(C0558e eVar) {
    }

    public void b() {
    }

    public void c() {
        L l = (L) this.f7616a.get();
        if (l != null) {
            l.t();
        }
    }
}
