package b.d.d.c;

import b.d.d.e.b;
import b.d.d.e.c;

public class a {
    public static double a(double d2, c cVar, c cVar2) {
        if (cVar.a() == cVar2.a()) {
            if (cVar instanceof b) {
                cVar = ((b) cVar).b();
            }
            if (cVar2 instanceof b) {
                cVar2 = ((b) cVar2).b();
            }
            double d3 = 0.0d;
            for (int i = 0; i < cVar.a(); i++) {
                double a2 = cVar.a(i) - cVar2.a(i);
                if (Math.abs(a2) > 1.0E-6d) {
                    d3 += Math.pow(a2, 2.0d);
                }
            }
            return Math.exp(d3 * -1.0d * d2);
        }
        throw new RuntimeException("v1.size() != v2.size()");
    }
}
