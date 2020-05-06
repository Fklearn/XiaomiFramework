package b.d.d.e;

import java.io.Serializable;

public class a extends c implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private final double[] f2159a;

    public a(double[] dArr) {
        if (dArr != null) {
            this.f2159a = dArr;
            return;
        }
        throw new RuntimeException("入参 = null");
    }

    public double a(int i) {
        return this.f2159a[i];
    }

    public int a() {
        return this.f2159a.length;
    }
}
