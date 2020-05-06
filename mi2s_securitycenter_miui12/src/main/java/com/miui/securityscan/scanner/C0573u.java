package com.miui.securityscan.scanner;

import com.miui.securityscan.L;
import com.miui.securityscan.scanner.O;
import java.lang.ref.WeakReference;

/* renamed from: com.miui.securityscan.scanner.u  reason: case insensitive filesystem */
public class C0573u implements O.e {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7929a;

    public C0573u(L l) {
        this.f7929a = new WeakReference<>(l);
    }

    public void a() {
        L l = (L) this.f7929a.get();
        if (l != null) {
            l.b();
        }
    }
}
