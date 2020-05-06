package b.d.c.a.a.a.a;

public class c {

    /* renamed from: a  reason: collision with root package name */
    public Float f2142a;

    /* renamed from: b  reason: collision with root package name */
    public Float f2143b;

    public c(Object obj) {
        if (obj instanceof b) {
            this.f2142a = null;
            this.f2143b = null;
            return;
        }
        throw new IllegalArgumentException("TruncationAndFoldingMachine must be implemented alongside a :class:`.DPMechanism`");
    }

    public Object a(Float f, Float f2) {
        if (f.floatValue() <= f2.floatValue()) {
            this.f2142a = f;
            this.f2143b = f2;
            return this;
        }
        throw new IllegalArgumentException("Lower bound must not be greater than upper bound");
    }

    public boolean a(Object obj) {
        if (this.f2142a != null && this.f2143b != null) {
            return true;
        }
        throw new IllegalStateException("Upper and lower bounds must be set");
    }

    public String toString() {
        if (this.f2142a == null) {
            return "";
        }
        return ".set_bounds(" + this.f2142a.toString() + ", " + this.f2143b + ")";
    }
}
