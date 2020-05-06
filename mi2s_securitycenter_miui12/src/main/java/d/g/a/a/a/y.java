package d.g.a.a.a;

import d.g.a.a.a.s;

public final class y implements w {

    /* renamed from: a  reason: collision with root package name */
    double f8838a = Math.sqrt(1500.0d);

    /* renamed from: b  reason: collision with root package name */
    double f8839b = 0.5d;

    /* renamed from: c  reason: collision with root package name */
    double f8840c = 1000.0d;

    /* renamed from: d  reason: collision with root package name */
    private boolean f8841d = false;
    private double e;
    private double f;
    private double g;
    private double h;
    private double i;
    private double j = Double.MAX_VALUE;
    private final s.a k = new s.a();

    private void b() {
        if (!this.f8841d) {
            if (this.j != Double.MAX_VALUE) {
                double d2 = this.f8839b;
                if (d2 > 1.0d) {
                    double d3 = this.f8838a;
                    this.g = ((-d2) * d3) + (d3 * Math.sqrt((d2 * d2) - 1.0d));
                    double d4 = this.f8839b;
                    double d5 = this.f8838a;
                    this.h = ((-d4) * d5) - (d5 * Math.sqrt((d4 * d4) - 1.0d));
                } else if (d2 >= 0.0d && d2 < 1.0d) {
                    this.i = this.f8838a * Math.sqrt(1.0d - (d2 * d2));
                }
                this.f8841d = true;
                return;
            }
            throw new IllegalStateException("Error: Final position of the spring must be set before the animation starts");
        }
    }

    public float a() {
        return (float) this.j;
    }

    /* access modifiers changed from: package-private */
    public s.a a(double d2, double d3, long j2) {
        double d4;
        double d5;
        b();
        double d6 = ((double) j2) / this.f8840c;
        double d7 = d2 - this.j;
        double d8 = this.f8839b;
        if (d8 > 1.0d) {
            double d9 = this.h;
            double d10 = this.g;
            double d11 = d7 - (((d9 * d7) - d3) / (d9 - d10));
            double d12 = ((d7 * d9) - d3) / (d9 - d10);
            d5 = (Math.pow(2.718281828459045d, d9 * d6) * d11) + (Math.pow(2.718281828459045d, this.g * d6) * d12);
            double d13 = this.h;
            double pow = d11 * d13 * Math.pow(2.718281828459045d, d13 * d6);
            double d14 = this.g;
            d4 = pow + (d12 * d14 * Math.pow(2.718281828459045d, d14 * d6));
        } else if (d8 == 1.0d) {
            double d15 = this.f8838a;
            double d16 = d3 + (d15 * d7);
            double d17 = d7 + (d16 * d6);
            d5 = Math.pow(2.718281828459045d, (-d15) * d6) * d17;
            double pow2 = d17 * Math.pow(2.718281828459045d, (-this.f8838a) * d6);
            double d18 = this.f8838a;
            d4 = (d16 * Math.pow(2.718281828459045d, (-d18) * d6)) + (pow2 * (-d18));
        } else {
            double d19 = 1.0d / this.i;
            double d20 = this.f8838a;
            double d21 = d19 * ((d8 * d20 * d7) + d3);
            double pow3 = Math.pow(2.718281828459045d, (-d8) * d20 * d6) * ((Math.cos(this.i * d6) * d7) + (Math.sin(this.i * d6) * d21));
            double d22 = this.f8838a;
            double d23 = this.f8839b;
            double pow4 = Math.pow(2.718281828459045d, (-d23) * d22 * d6);
            double d24 = this.i;
            double d25 = pow3;
            double sin = (-d24) * d7 * Math.sin(d24 * d6);
            double d26 = this.i;
            d4 = ((-d22) * pow3 * d23) + (pow4 * (sin + (d21 * d26 * Math.cos(d26 * d6))));
            d5 = d25;
        }
        double d27 = 0.0d;
        if (Math.abs(d5) < 0.6000000238418579d) {
            d4 = 0.0d;
        } else {
            d27 = d5;
        }
        s.a aVar = this.k;
        aVar.f8830a = (float) (d27 + this.j);
        aVar.f8831b = (float) d4;
        return aVar;
    }

    public y a(double d2) {
        this.f8840c = d2;
        return this;
    }

    public y a(float f2) {
        if (f2 >= 0.0f) {
            this.f8839b = (double) f2;
            this.f8841d = false;
            return this;
        }
        throw new IllegalArgumentException("Damping ratio must be non-negative");
    }

    public boolean a(float f2, float f3) {
        return ((double) Math.abs(f3)) < this.f && ((double) Math.abs(f2 - a())) < this.e;
    }

    public y b(float f2) {
        this.j = (double) f2;
        return this;
    }

    /* access modifiers changed from: package-private */
    public void b(double d2) {
        this.e = Math.abs(d2);
        this.f = this.e * 62.5d;
    }

    public y c(float f2) {
        if (f2 > 0.0f) {
            this.f8838a = Math.sqrt((double) f2);
            this.f8841d = false;
            return this;
        }
        throw new IllegalArgumentException("Spring stiffness constant must be positive.");
    }
}
