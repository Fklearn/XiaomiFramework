package d.a.b;

import d.a.d;
import d.a.e.k;
import d.a.e.l;
import d.a.g.B;
import d.a.g.C0575b;
import d.a.j;

class g extends k {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f8659a;

    g(j jVar) {
        this.f8659a = jVar;
    }

    public void onBegin(Object obj, l lVar) {
        j.a aVar = j.a.DOWN;
        if (obj == aVar && !this.f8659a.a(aVar)) {
            C0575b bVar = lVar.f8730a;
            if (bVar == B.f8757d || bVar == B.e) {
                d target = this.f8659a.f8645a.getTarget();
                float max = Math.max(target.getValue(6), target.getValue(5));
                float max2 = Math.max((max - this.f8659a.f8668c) / max, 0.9f);
                lVar.f.a(max2);
            }
        }
    }
}
