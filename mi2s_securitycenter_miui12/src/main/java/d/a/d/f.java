package d.a.d;

import d.a.a.g;
import d.a.b.a;
import d.a.d;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f8697a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ a f8698b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ a f8699c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ g f8700d;
    final /* synthetic */ g e;

    f(g gVar, d dVar, a aVar, a aVar2, g gVar2) {
        this.e = gVar;
        this.f8697a = dVar;
        this.f8698b = aVar;
        this.f8699c = aVar2;
        this.f8700d = gVar2;
    }

    public void run() {
        g.a(this.f8697a, this.e.f8703c, this.f8698b, this.f8699c, this.f8700d);
        this.e.e();
    }
}
