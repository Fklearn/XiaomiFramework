package d.g.a.a.a;

import d.g.a.a.a.s;

public final class t extends s<t> {
    private final a B = new a();
    private b C;

    static final class a implements w {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public float f8832a = -4.2f;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public float f8833b;

        /* renamed from: c  reason: collision with root package name */
        private final s.a f8834c = new s.a();

        /* renamed from: d  reason: collision with root package name */
        private double f8835d;
        private final float e = 1000.0f;

        a() {
        }

        /* access modifiers changed from: package-private */
        public s.a a(float f, float f2, long j) {
            float min = ((float) Math.min(j, 16)) / 1000.0f;
            double pow = Math.pow(1.0d - this.f8835d, (double) min);
            s.a aVar = this.f8834c;
            aVar.f8831b = (float) (((double) f2) * pow);
            float f3 = aVar.f8831b;
            aVar.f8830a = f + (min * f3);
            if (a(aVar.f8830a, f3)) {
                this.f8834c.f8831b = 0.0f;
            }
            return this.f8834c;
        }

        /* access modifiers changed from: package-private */
        public void a(float f) {
            this.f8832a = f * -4.2f;
            this.f8835d = 1.0d - Math.pow(2.718281828459045d, (double) this.f8832a);
        }

        public boolean a(float f, float f2) {
            return Math.abs(f2) < this.f8833b;
        }

        /* access modifiers changed from: package-private */
        public void b(float f) {
            this.f8833b = f * 62.5f;
        }
    }

    public interface b {
        void a(int i);
    }

    public t(v vVar, b bVar) {
        super(vVar);
        this.B.b(b());
        this.C = bVar;
    }

    private float j(float f) {
        return (float) ((Math.log((double) (f / this.o)) * 1000.0d) / ((double) this.B.f8832a));
    }

    public t a(float f) {
        super.a(f);
        return this;
    }

    /* access modifiers changed from: package-private */
    public boolean a(float f, float f2) {
        return f >= this.u || f <= this.v || this.B.a(f, f2);
    }

    /* access modifiers changed from: package-private */
    public boolean a(long j) {
        s.a a2 = this.B.a(this.p, this.o, j);
        this.p = a2.f8830a;
        this.o = a2.f8831b;
        float f = this.p;
        float f2 = this.v;
        if (f < f2) {
            this.p = f2;
            return true;
        }
        float f3 = this.u;
        if (f > f3) {
            this.p = f3;
            return true;
        } else if (!a(f, this.o)) {
            return false;
        } else {
            this.C.a((int) this.p);
            return true;
        }
    }

    public t b(float f) {
        super.b(f);
        return this;
    }

    public float d() {
        return j(Math.signum(this.o) * this.B.f8833b);
    }

    public float e() {
        return (this.p - (this.o / this.B.f8832a)) + ((Math.signum(this.o) * this.B.f8833b) / this.B.f8832a);
    }

    public t f(float f) {
        super.f(f);
        return this;
    }

    /* access modifiers changed from: package-private */
    public void g(float f) {
        this.B.b(f);
    }

    public float h(float f) {
        return j(((f - this.p) + (this.o / this.B.f8832a)) * this.B.f8832a);
    }

    public t i(float f) {
        if (f > 0.0f) {
            this.B.a(f);
            return this;
        }
        throw new IllegalArgumentException("Friction must be positive");
    }
}
