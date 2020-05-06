package d.a.d;

import android.util.ArrayMap;
import d.a.a.g;
import d.a.b.a;
import d.a.d;
import d.a.g.C0575b;
import d.a.g.C0576c;

class k {

    /* renamed from: a  reason: collision with root package name */
    d f8714a;

    /* renamed from: b  reason: collision with root package name */
    g f8715b;

    /* renamed from: c  reason: collision with root package name */
    Object f8716c;

    /* renamed from: d  reason: collision with root package name */
    ArrayMap<C0575b, Number> f8717d = new ArrayMap<>();
    ArrayMap<C0575b, Number> e = new ArrayMap<>();
    ArrayMap<C0575b, Long> f = new ArrayMap<>();

    k(d dVar, a aVar, a aVar2, g gVar) {
        this.f8714a = dVar;
        a(this.f8717d, aVar);
        a(this.e, aVar2);
        this.f8716c = aVar2.c();
        this.f8715b = gVar;
        aVar2.a(this.f8715b);
    }

    private Number a(a aVar, C0575b bVar) {
        return bVar instanceof C0576c ? Integer.valueOf(aVar.d(bVar)) : Float.valueOf(aVar.c(bVar));
    }

    private void a(ArrayMap<C0575b, Number> arrayMap, a aVar) {
        if (aVar != null) {
            for (C0575b next : aVar.d()) {
                arrayMap.put(next, a(aVar, next));
                long b2 = aVar.b(next);
                if (b2 != 0) {
                    this.f.put(next, Long.valueOf(b2));
                }
            }
        }
    }

    public String toString() {
        return "TransitionInfo{target=" + this.f8714a + ", from=" + d.a.i.a.a(this.f8717d, "    ") + ", to=" + d.a.i.a.a(this.e, "    ") + ", config=" + this.f8715b + '}';
    }
}
