package d.a.h;

import android.animation.TypeEvaluator;
import d.a.g.C0575b;

public class a extends c {
    private float p;

    a(Object obj, C0575b bVar) {
        super(obj, bVar);
    }

    /* access modifiers changed from: protected */
    public double a(double d2) {
        return (double) this.p;
    }

    /* access modifiers changed from: protected */
    public float a(float f) {
        if (f > 1.0f) {
            return 1.0f;
        }
        if (f < 0.0f) {
            return 0.0f;
        }
        return f;
    }

    /* access modifiers changed from: protected */
    public boolean a(double d2, double d3) {
        this.f8769d.a(1.0d);
        return !this.f8769d.a((double) this.p, d3);
    }

    /* access modifiers changed from: protected */
    public double b(double d2) {
        return 1.0d;
    }

    /* access modifiers changed from: protected */
    public double c(double d2) {
        this.p = a((float) d2);
        return (double) ((Integer) f().evaluate(this.p, Integer.valueOf(this.f8767b[0]), Integer.valueOf(this.f8767b[1]))).intValue();
    }

    /* access modifiers changed from: protected */
    public TypeEvaluator f() {
        return d.a.i.a.f8777b;
    }

    /* access modifiers changed from: protected */
    public void m() {
        this.p = 0.0f;
    }
}
