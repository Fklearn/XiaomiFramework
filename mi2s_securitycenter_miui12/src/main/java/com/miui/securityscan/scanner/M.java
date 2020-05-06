package com.miui.securityscan.scanner;

import com.miui.securityscan.b.d;
import com.miui.securityscan.scanner.C0564k;
import java.util.List;
import miui.util.Log;

class M extends C0564k.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f7844a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ List f7845b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ O f7846c;

    M(O o, d dVar, List list) {
        this.f7846c = o;
        this.f7844a = dVar;
        this.f7845b = list;
    }

    public void d() {
        super.d();
        Log.d("SecurityManager", "startOptimizeMemoryAfterScanMemory onFinishCleanup() callback");
        this.f7846c.l.post(new L(this));
    }

    public void f() {
        super.f();
    }
}
