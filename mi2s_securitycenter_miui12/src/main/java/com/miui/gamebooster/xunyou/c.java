package com.miui.gamebooster.xunyou;

import java.lang.ref.WeakReference;

public class c implements a {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<a> f5403a;

    public c(a aVar) {
        this.f5403a = new WeakReference<>(aVar);
    }

    public void a(String str) {
        a aVar = (a) this.f5403a.get();
        if (aVar != null) {
            aVar.a(str);
        }
    }

    public void b() {
        a aVar = (a) this.f5403a.get();
        if (aVar != null) {
            aVar.b();
        }
    }

    public void d() {
        a aVar = (a) this.f5403a.get();
        if (aVar != null) {
            aVar.d();
        }
    }
}
