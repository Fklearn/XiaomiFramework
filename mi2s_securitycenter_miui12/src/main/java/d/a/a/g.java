package d.a.a;

import d.a.g.C0575b;
import d.a.i.b;
import java.util.ArrayList;
import java.util.List;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static a<Long> f8628a = new b();

    /* renamed from: b  reason: collision with root package name */
    private static a<Integer> f8629b = new c();

    /* renamed from: c  reason: collision with root package name */
    private static a<Long> f8630c = new d();

    /* renamed from: d  reason: collision with root package name */
    private static a<Float> f8631d = new e();
    private static a<Long> e = new f();
    public List<a> f = new ArrayList();

    private interface a<T> {
        T a(a aVar, C0575b bVar, T t);
    }

    public static g a(a... aVarArr) {
        g gVar = new g();
        for (a a2 : aVarArr) {
            gVar.a(a2);
        }
        return gVar;
    }

    private <T> T a(Object obj, C0575b bVar, a<T> aVar, T t) {
        Object obj2;
        for (a next : this.f) {
            if (obj == null || (obj2 = next.g) == null || !a(obj2, obj)) {
                if (d.a.i.a.a((T[]) next.i)) {
                    t = aVar.a(next, (C0575b) null, t);
                } else if (d.a.i.a.a((T[]) next.i, bVar)) {
                    t = aVar.a(next, bVar, t);
                }
            }
        }
        return t;
    }

    private static boolean a(a aVar, a aVar2) {
        return aVar == null ? aVar2 != null : d.a.i.a.a((T[]) aVar.i) && !d.a.i.a.a((T[]) aVar2.i);
    }

    private static boolean a(a aVar, C0575b bVar) {
        return !d.a.i.a.a((T[]) aVar.i) && !d.a.i.a.a((T[]) aVar.i, bVar);
    }

    private boolean a(Object obj, Object obj2) {
        return obj.getClass() == obj2.getClass() && obj.toString().equals(obj2.toString());
    }

    public long a(Object obj, C0575b bVar) {
        return ((Long) a(obj, bVar, f8628a, 0L)).longValue();
    }

    public a a() {
        return this.f.get(0);
    }

    public b.a a(C0575b bVar) {
        b.a aVar = null;
        a aVar2 = null;
        for (a next : this.f) {
            if (next.f8626c != null && next.e <= 0.0d && !a(next, bVar)) {
                if (aVar == null || a(aVar2, next) || b.a(next.f8626c.f8781a)) {
                    aVar = next.f8626c;
                    aVar2 = next;
                }
            }
        }
        return aVar == null ? d.a.i.a.f8776a : aVar;
    }

    public b.a a(C0575b bVar, double d2, double d3) {
        for (a next : this.f) {
            if (!(next.f8626c == null || next.e == 0.0d || a(next, bVar))) {
                double d4 = next.e;
                if (d4 > d2 && d4 <= d3) {
                    return next.f8626c;
                }
            }
        }
        return null;
    }

    public void a(a aVar) {
        if (aVar != null && !this.f.contains(aVar)) {
            this.f.add(new a(aVar));
        }
    }

    public void a(g gVar) {
        if (gVar != null) {
            for (a a2 : gVar.f) {
                a(a2);
            }
        }
    }

    public long b(Object obj, C0575b bVar) {
        return ((Long) a(obj, bVar, f8630c, 0L)).longValue();
    }

    public float c(Object obj, C0575b bVar) {
        return ((Float) a(obj, bVar, f8631d, Float.valueOf(Float.MAX_VALUE))).floatValue();
    }

    public long d(Object obj, C0575b bVar) {
        return ((Long) a(obj, bVar, e, 0L)).longValue();
    }

    public int e(Object obj, C0575b bVar) {
        return ((Integer) a(obj, bVar, f8629b, 0)).intValue();
    }
}
