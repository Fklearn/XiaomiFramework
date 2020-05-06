package androidx.lifecycle;

import androidx.lifecycle.f;

class CompositeGeneratedAdaptersObserver implements g {

    /* renamed from: a  reason: collision with root package name */
    private final d[] f961a;

    CompositeGeneratedAdaptersObserver(d[] dVarArr) {
        this.f961a = dVarArr;
    }

    public void a(i iVar, f.a aVar) {
        o oVar = new o();
        for (d a2 : this.f961a) {
            a2.a(iVar, aVar, false, oVar);
        }
        for (d a3 : this.f961a) {
            a3.a(iVar, aVar, true, oVar);
        }
    }
}
