package b.d.a.a.a.a.a;

import b.d.a.a.b.c;
import b.d.a.a.b.d;

public class b extends b.d.a.a.a.a.b {

    /* renamed from: c  reason: collision with root package name */
    private c[] f2084c;

    public b(c[] cVarArr) {
        this.f2086a = "ComplementNB";
        this.f2084c = cVarArr;
        this.f2087b = cVarArr.length;
    }

    public double[] a(c cVar) {
        double[] dArr = new double[this.f2087b];
        for (int i = 0; i < dArr.length; i++) {
            dArr[i] = d.a(this.f2084c[i], cVar);
        }
        double d2 = Double.MIN_VALUE;
        for (double d3 : dArr) {
            if (d3 > d2) {
                d2 = d3;
            }
        }
        double d4 = 0.0d;
        for (double d5 : dArr) {
            d4 += Math.exp(d5 - d2);
        }
        double log = Math.log(d4) + d2;
        for (int i2 = 0; i2 < dArr.length; i2++) {
            dArr[i2] = Math.exp(dArr[i2] - log);
        }
        return dArr;
    }
}
