package d.a.f;

import d.a.d;
import d.a.g.C0575b;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private double f8748a = Double.MAX_VALUE;

    /* renamed from: b  reason: collision with root package name */
    private float f8749b;

    /* renamed from: c  reason: collision with root package name */
    private float f8750c;

    public e(d dVar, C0575b bVar) {
        this.f8749b = dVar.getMinVisibleChange(bVar) * 0.75f;
        this.f8750c = this.f8749b * 16.666666f;
    }

    private boolean b(double d2, double d3) {
        return Math.abs(this.f8748a) == 3.4028234663852886E38d || Math.abs(d2 - d3) < ((double) this.f8749b);
    }

    public void a(double d2) {
        this.f8748a = d2;
    }

    public boolean a(double d2, double d3) {
        return b(d2, this.f8748a) && Math.abs(d3) < ((double) this.f8750c);
    }
}
