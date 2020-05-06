package d.a.d;

import d.a.g.C0575b;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d.a.d f8691a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0575b[] f8692b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ g f8693c;

    d(g gVar, d.a.d dVar, C0575b[] bVarArr) {
        this.f8693c = gVar;
        this.f8691a = dVar;
        this.f8692b = bVarArr;
    }

    public void run() {
        this.f8691a.getAnimTask().a(this.f8692b);
    }
}
