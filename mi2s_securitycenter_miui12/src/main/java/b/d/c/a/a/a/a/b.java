package b.d.c.a.a.a.a;

public abstract class b implements a {

    /* renamed from: a  reason: collision with root package name */
    protected Float f2140a = null;

    /* renamed from: b  reason: collision with root package name */
    protected Float f2141b = null;

    public b a(float f, float f2) {
        if (f < 0.0f) {
            throw new IllegalArgumentException("Epsilon must be non-negative");
        } else if (f2 < 0.0f || f2 > 1.0f) {
            throw new IllegalArgumentException("Delta must be in [0, 1]");
        } else if (0.0f != f + f2) {
            this.f2141b = Float.valueOf(f);
            this.f2140a = Float.valueOf(f2);
            return this;
        } else {
            throw new IllegalArgumentException("Epsilon and Delta cannot both be zero");
        }
    }

    public b a(Float f) {
        a(f.floatValue(), 0.0f);
        return this;
    }

    public boolean a(Object obj) {
        if (this.f2141b != null) {
            return true;
        }
        throw new IllegalArgumentException("Epsilon must be set");
    }

    public String toString() {
        return "DPMechanism{_delta=" + this.f2140a + ", _epsilon=" + this.f2141b + '}';
    }
}
