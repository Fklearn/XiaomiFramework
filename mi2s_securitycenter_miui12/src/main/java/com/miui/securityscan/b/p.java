package com.miui.securityscan.b;

import com.miui.securityscan.L;
import java.lang.ref.WeakReference;

public class p implements n {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7622a;

    /* renamed from: b  reason: collision with root package name */
    public boolean f7623b;

    public p(L l) {
        this.f7622a = new WeakReference<>(l);
    }

    public void a() {
    }

    public void b() {
    }

    public void c() {
        L l = (L) this.f7622a.get();
        if (l != null) {
            l.m.post(new o(this, l));
        }
    }
}
