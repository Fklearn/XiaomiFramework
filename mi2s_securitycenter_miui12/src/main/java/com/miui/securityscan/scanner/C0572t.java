package com.miui.securityscan.scanner;

import com.miui.securityscan.L;
import com.miui.securityscan.scanner.O;
import java.lang.ref.WeakReference;

/* renamed from: com.miui.securityscan.scanner.t  reason: case insensitive filesystem */
public class C0572t implements O.d {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7927a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public WeakReference<C0568o> f7928b;

    public C0572t(L l) {
        this.f7927a = new WeakReference<>(l);
    }

    public void a(C0558e eVar) {
        L l = (L) this.f7927a.get();
        if (l != null && l.isAdded()) {
            l.m.post(new r(this, l, eVar));
        }
    }

    public void a(WeakReference<C0568o> weakReference) {
        this.f7928b = weakReference;
    }

    public void b() {
    }

    public void c() {
        L l = (L) this.f7927a.get();
        if (l != null && l.isAdded()) {
            l.m.post(new C0571s(this, l));
        }
    }
}
