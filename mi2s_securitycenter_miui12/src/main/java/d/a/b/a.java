package d.a.b;

import android.util.ArrayMap;
import d.a.a.g;
import d.a.d;
import d.a.d.j;
import d.a.g.B;
import d.a.g.C0575b;
import d.a.g.C0576c;
import java.util.Map;
import java.util.Set;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static int f8633a = 100;

    /* renamed from: b  reason: collision with root package name */
    public static final int f8634b = (f8633a + 1000000);

    /* renamed from: c  reason: collision with root package name */
    private Object f8635c;

    /* renamed from: d  reason: collision with root package name */
    private d.a.a.a f8636d = new d.a.a.a();
    private Map<C0575b, C0072a> e = new ArrayMap();

    /* renamed from: d.a.b.a$a  reason: collision with other inner class name */
    private static class C0072a {

        /* renamed from: a  reason: collision with root package name */
        float f8641a;

        /* renamed from: b  reason: collision with root package name */
        int f8642b;

        /* renamed from: c  reason: collision with root package name */
        boolean f8643c = true;

        /* renamed from: d  reason: collision with root package name */
        long f8644d;
        d.a.a.a e;

        C0072a() {
        }

        /* access modifiers changed from: package-private */
        public C0072a a(float f) {
            this.f8641a = f;
            return this;
        }

        /* access modifiers changed from: package-private */
        public C0072a a(int i) {
            this.f8642b = i;
            return this;
        }

        /* access modifiers changed from: package-private */
        public C0072a a(long j) {
            this.f8644d = j;
            return this;
        }

        public String toString() {
            return "StateValue{value=" + this.f8641a + ", intValue = " + this.f8642b + ", enable=" + this.f8643c + ", flags = " + this.f8644d + '}';
        }
    }

    public a(Object obj) {
        if (obj != null) {
            this.f8635c = obj;
            return;
        }
        throw new IllegalArgumentException("tag mustn't be null");
    }

    private static void a(a aVar, d dVar, C0575b bVar) {
        if (bVar instanceof C0576c) {
            aVar.a(bVar, dVar.getIntValue((C0576c) bVar), new long[0]);
        } else {
            aVar.a(bVar, dVar.getValue(bVar), new long[0]);
        }
    }

    public static void a(d dVar, a aVar, a aVar2) {
        for (C0575b next : aVar2.d()) {
            float f = aVar2.g(next).f8641a;
            if (!(f == 1000000.0f || f == ((float) f8634b) || aVar.a(next))) {
                a(aVar, dVar, next);
            }
        }
    }

    private C0072a g(C0575b bVar) {
        C0072a aVar = this.e.get(bVar);
        if (aVar != null) {
            return aVar;
        }
        C0072a aVar2 = new C0072a();
        this.e.put(bVar, aVar2);
        return aVar2;
    }

    public float a(d dVar, C0575b bVar) {
        C0072a aVar = this.e.get(bVar);
        if (aVar == null) {
            return Float.MAX_VALUE;
        }
        aVar.f8641a = j.a(dVar, bVar, aVar.f8641a);
        return aVar.f8641a;
    }

    public a a(B b2, int i, long... jArr) {
        a((C0575b) b2, i, jArr);
        return this;
    }

    public a a(C0575b bVar, float f, long... jArr) {
        C0072a aVar = this.e.get(bVar);
        if (aVar == null) {
            aVar = new C0072a();
            this.e.put(bVar, aVar);
        }
        aVar.a(f);
        aVar.a(jArr.length > 0 ? jArr[0] : 0);
        return this;
    }

    public a a(C0575b bVar, int i, long... jArr) {
        if (bVar instanceof C0576c) {
            C0072a aVar = this.e.get(bVar);
            if (aVar == null) {
                aVar = new C0072a();
                this.e.put(bVar, aVar);
            }
            aVar.a(i);
            aVar.a(jArr.length > 0 ? jArr[0] : 0);
        } else {
            a(bVar, (float) i, jArr);
        }
        return this;
    }

    public void a() {
        this.e.clear();
    }

    public void a(g gVar) {
        gVar.a(b());
        for (C0072a aVar : this.e.values()) {
            d.a.a.a aVar2 = aVar.e;
            if (aVar2 != null) {
                gVar.a(aVar2);
            }
        }
    }

    public boolean a(C0575b bVar) {
        return this.e.containsKey(bVar);
    }

    public boolean a(C0575b bVar, long j) {
        return d.a.i.a.a(g(bVar).f8644d, j);
    }

    public long b(C0575b bVar) {
        return g(bVar).f8644d;
    }

    public d.a.a.a b() {
        if (this.f8636d == null) {
            this.f8636d = new d.a.a.a();
        }
        return this.f8636d;
    }

    public float c(C0575b bVar) {
        C0072a aVar = this.e.get(bVar);
        if (aVar != null) {
            return aVar.f8641a;
        }
        return Float.MAX_VALUE;
    }

    public Object c() {
        return this.f8635c;
    }

    public int d(C0575b bVar) {
        C0072a aVar;
        if ((bVar instanceof C0576c) && (aVar = this.e.get(bVar)) != null) {
            return aVar.f8642b;
        }
        return Integer.MAX_VALUE;
    }

    public Set<C0575b> d() {
        return this.e.keySet();
    }

    public boolean e(C0575b bVar) {
        C0072a aVar = this.e.get(bVar);
        return aVar != null && aVar.f8643c;
    }

    public a f(C0575b bVar) {
        this.e.remove(bVar);
        return this;
    }

    public String toString() {
        return "\nAnimState{mTag='" + this.f8635c + '\'' + ", mMaps=" + d.a.i.a.a(this.e, "    ") + '}';
    }
}
