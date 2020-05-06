package miuix.springback.view;

import android.view.animation.AnimationUtils;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private long f8949a;

    /* renamed from: b  reason: collision with root package name */
    private long f8950b;

    /* renamed from: c  reason: collision with root package name */
    private double f8951c;

    /* renamed from: d  reason: collision with root package name */
    private double f8952d;
    private b e;
    private double f;
    private double g;
    private double h;
    private double i;
    private double j;
    private double k;
    private double l;
    private double m;
    private int n;
    private boolean o = true;
    private boolean p;
    private int q;

    public void a(float f2, float f3, float f4, float f5, float f6, int i2, boolean z) {
        this.o = false;
        this.p = false;
        double d2 = (double) f2;
        this.g = d2;
        this.h = d2;
        this.f = (double) f3;
        double d3 = (double) f4;
        this.j = d3;
        this.k = d3;
        this.f8952d = (double) ((int) this.j);
        this.i = (double) f5;
        double d4 = (double) f6;
        this.l = d4;
        this.m = d4;
        this.e = (Math.abs(this.m) <= 5000.0d || z) ? new b(1.0f, 0.4f) : new b(1.0f, 0.55f);
        this.n = i2;
        this.f8949a = AnimationUtils.currentAnimationTimeMillis();
    }

    public void a(int i2) {
        this.q = i2;
    }

    public boolean a() {
        if (this.e == null || this.o) {
            return false;
        }
        int i2 = this.q;
        if (i2 != 0) {
            if (this.n == 1) {
                this.f8951c = (double) i2;
                this.g = (double) i2;
            } else {
                this.f8952d = (double) i2;
                this.j = (double) i2;
            }
            this.q = 0;
            return true;
        } else if (this.p) {
            this.o = true;
            return true;
        } else {
            this.f8950b = AnimationUtils.currentAnimationTimeMillis();
            float min = Math.min(((float) (this.f8950b - this.f8949a)) / 1000.0f, 0.016f);
            if (min == 0.0f) {
                min = 0.016f;
            }
            this.f8949a = this.f8950b;
            if (this.n == 2) {
                double a2 = this.e.a(this.m, min, this.i, this.j);
                this.f8952d = this.j + (((double) min) * a2);
                this.m = a2;
                if (a(this.f8952d, this.k, this.i)) {
                    this.p = true;
                    this.f8952d = this.i;
                } else {
                    this.j = this.f8952d;
                }
            } else {
                double a3 = this.e.a(this.m, min, this.f, this.g);
                this.f8951c = this.g + (((double) min) * a3);
                this.m = a3;
                if (a(this.f8951c, this.h, this.f)) {
                    this.p = true;
                    this.f8951c = this.f;
                } else {
                    this.g = this.f8951c;
                }
            }
            return true;
        }
    }

    public boolean a(double d2, double d3, double d4) {
        if (d3 < d4 && d2 > d4) {
            return true;
        }
        int i2 = (d3 > d4 ? 1 : (d3 == d4 ? 0 : -1));
        if (i2 <= 0 || d2 >= d4) {
            return (i2 == 0 && Math.signum(this.l) != Math.signum(d2)) || Math.abs(d2 - d4) < 1.0d;
        }
        return true;
    }

    public final void b() {
        this.o = true;
        this.q = 0;
    }

    public final int c() {
        return (int) this.f8951c;
    }

    public final int d() {
        return (int) this.f8952d;
    }

    public final boolean e() {
        return this.o;
    }
}
