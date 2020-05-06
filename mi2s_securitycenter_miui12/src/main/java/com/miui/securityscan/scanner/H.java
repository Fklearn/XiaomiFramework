package com.miui.securityscan.scanner;

import com.miui.securityscan.b.g;
import com.miui.securityscan.b.n;
import com.miui.securityscan.model.GroupModel;
import java.util.List;

class H implements g {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n f7835a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ O f7836b;

    H(O o, n nVar) {
        this.f7836b = o;
        this.f7835a = nVar;
    }

    public void a() {
        n nVar = this.f7835a;
        if (nVar != null) {
            nVar.b();
        }
    }

    public void a(int i, int i2, Object obj) {
        if (this.f7836b.f7850b) {
            throw new InterruptedException();
        }
    }

    public void a(List<GroupModel> list, int i) {
        if (list != null) {
            this.f7836b.h.a(list);
        }
        n nVar = this.f7835a;
        if (nVar != null) {
            nVar.c();
        }
    }

    public void b() {
        n nVar = this.f7835a;
        if (nVar != null) {
            nVar.a();
        }
    }
}
