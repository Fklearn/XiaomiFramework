package b.d.a.a.b;

import java.io.Serializable;

public class a extends c implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private final float[] f2097a;

    public a(float[] fArr) {
        if (fArr != null) {
            this.f2097a = fArr;
            return;
        }
        throw new RuntimeException("入参 = null");
    }

    public double a(int i) {
        return (double) this.f2097a[i];
    }

    public int a() {
        return this.f2097a.length;
    }
}
