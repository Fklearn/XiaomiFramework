package d.g.a.a.a;

import d.g.a.a.a.s;

public final class x extends s<x> {
    private y B = null;
    private float C = Float.MAX_VALUE;
    private boolean D = false;

    public x(v vVar) {
        super(vVar);
    }

    private void e() {
        y yVar = this.B;
        if (yVar != null) {
            double a2 = (double) yVar.a();
            if (a2 > ((double) this.u)) {
                throw new UnsupportedOperationException("Final position of the spring cannot be greater than the max value.");
            } else if (a2 < ((double) this.v)) {
                throw new UnsupportedOperationException("Final position of the spring cannot be less than the min value.");
            }
        } else {
            throw new UnsupportedOperationException("Incomplete SpringAnimation: Either final position or a spring force needs to be set.");
        }
    }

    public x a(y yVar) {
        this.B = yVar;
        return this;
    }

    public void a(boolean z) {
        e();
        this.B.b((double) b());
        super.a(z);
    }

    /* access modifiers changed from: package-private */
    public boolean a(float f, float f2) {
        return this.B.a(f, f2);
    }

    /* access modifiers changed from: package-private */
    public boolean a(long j) {
        long j2;
        double d2;
        double d3;
        y yVar;
        if (this.D) {
            float f = this.C;
            if (f != Float.MAX_VALUE) {
                this.B.b(f);
                this.C = Float.MAX_VALUE;
            }
            this.p = this.B.a();
            this.o = 0.0f;
            this.D = false;
            return true;
        }
        if (this.C != Float.MAX_VALUE) {
            this.B.a();
            j2 = j / 2;
            s.a a2 = this.B.a((double) this.p, (double) this.o, j2);
            this.B.b(this.C);
            this.C = Float.MAX_VALUE;
            yVar = this.B;
            d3 = (double) a2.f8830a;
            d2 = (double) a2.f8831b;
        } else {
            yVar = this.B;
            d3 = (double) this.p;
            d2 = (double) this.o;
            j2 = j;
        }
        s.a a3 = yVar.a(d3, d2, j2);
        this.p = a3.f8830a;
        this.o = a3.f8831b;
        this.p = Math.max(this.p, this.v);
        this.p = Math.min(this.p, this.u);
        if (!a(this.p, this.o)) {
            return false;
        }
        this.p = this.B.a();
        this.o = 0.0f;
        return true;
    }

    public y d() {
        return this.B;
    }

    /* access modifiers changed from: package-private */
    public void g(float f) {
    }
}
