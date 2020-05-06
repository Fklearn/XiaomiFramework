package b.d.e;

import android.content.Context;
import b.d.e.b.d;

public abstract class e {

    /* renamed from: a  reason: collision with root package name */
    protected d f2214a;

    protected static boolean a(b bVar) {
        return (bVar.f() & 134217728) != 0;
    }

    protected static boolean b(b bVar) {
        return (bVar.f() & 1) != 0;
    }

    /* access modifiers changed from: protected */
    public String a(Context context) {
        return c.a(context);
    }

    /* access modifiers changed from: protected */
    public void a(b bVar, Context context) {
        String a2;
        if (this.f2214a == null && (a2 = a(context)) != null) {
            this.f2214a = new d();
            this.f2214a.a(a2);
        }
        d dVar = this.f2214a;
        if (dVar != null && dVar.a()) {
            bVar.b(this.f2214a.a(bVar));
        }
    }

    /* access modifiers changed from: protected */
    public double[] a(double[] dArr, b bVar) {
        int f = bVar.f();
        int b2 = d.b(f);
        int a2 = d.a(f);
        if ((f & 268435455) == 0 || b2 == a2) {
            return dArr;
        }
        double[] dArr2 = new double[dArr.length];
        System.arraycopy(dArr, 0, dArr2, 0, dArr.length);
        double d2 = (((double) (a2 - b2)) * (dArr2[0] - dArr2[1])) / 4.0d;
        for (int i = 0; i < dArr2.length; i++) {
            dArr2[i] = dArr2[i] + d2;
        }
        return dArr2;
    }

    /* access modifiers changed from: protected */
    public abstract boolean b(b bVar, Context context);

    public void c(b bVar, Context context) {
        if (i.a(bVar.d())) {
            bVar.a(1);
        }
        if (b(bVar, context)) {
            bVar.a(2);
        }
    }
}
