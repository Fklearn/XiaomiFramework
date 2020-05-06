package b.c.a.b;

import b.c.a.b.a.b;

class m implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b.a f2057a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Throwable f2058b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ o f2059c;

    m(o oVar, b.a aVar, Throwable th) {
        this.f2059c = oVar;
        this.f2057a = aVar;
        this.f2058b = th;
    }

    public void run() {
        if (this.f2059c.m.s()) {
            o oVar = this.f2059c;
            oVar.k.a(oVar.m.b(oVar.f2064d.f2034a));
        }
        o oVar2 = this.f2059c;
        oVar2.n.a(oVar2.i, oVar2.k.b(), new b(this.f2057a, this.f2058b));
    }
}
