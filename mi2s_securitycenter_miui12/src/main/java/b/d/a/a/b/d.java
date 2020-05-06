package b.d.a.a.b;

public class d {
    private static double a(a aVar, a aVar2) {
        double d2 = 0.0d;
        for (int i = 0; i < aVar.a(); i++) {
            d2 += aVar.a(i) * aVar2.a(i);
        }
        return d2;
    }

    private static double a(a aVar, b bVar) {
        double d2 = 0.0d;
        for (int i = 0; i < bVar.c(); i++) {
            d2 += aVar.a(bVar.b()[i]) * ((double) bVar.e()[i]);
        }
        return d2;
    }

    public static double a(c cVar, c cVar2) {
        if (cVar.a() == cVar2.a()) {
            boolean z = cVar instanceof a;
            if (z && (cVar2 instanceof a)) {
                return a((a) cVar, (a) cVar2);
            }
            if (z && (cVar2 instanceof b)) {
                return a((a) cVar, (b) cVar2);
            }
            boolean z2 = cVar instanceof b;
            if (z2 && (cVar2 instanceof a)) {
                return a((a) cVar2, (b) cVar);
            }
            if (z2 && (cVar2 instanceof b)) {
                return a(((b) cVar).d(), (b) cVar2);
            }
            throw new RuntimeException("");
        }
        throw new RuntimeException("v1 & v2 size doesn't match,v1 size = " + cVar.a() + " v2 size = " + cVar2.a());
    }
}
