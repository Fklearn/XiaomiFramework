package d.a.h;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.util.Log;
import d.a.a.g;
import d.a.d;
import d.a.f.a;
import d.a.f.e;
import d.a.f.f;
import d.a.f.h;
import d.a.f.i;
import d.a.g.C0575b;
import d.a.g.C0576c;
import d.a.i.b;
import java.util.Arrays;

public class c {

    /* renamed from: a  reason: collision with root package name */
    float[] f8766a = new float[0];

    /* renamed from: b  reason: collision with root package name */
    int[] f8767b = new int[0];

    /* renamed from: c  reason: collision with root package name */
    private double[] f8768c = {0.0d, 0.0d};

    /* renamed from: d  reason: collision with root package name */
    e f8769d;
    private boolean e;
    private g f;
    d g;
    Object h;
    C0575b i;
    boolean j;
    private TimeInterpolator k;
    private long l;
    private long m;
    private d.a.f.g n;
    private int o;

    public c(Object obj, C0575b bVar) {
        this.h = obj;
        this.i = bVar;
        this.j = this.i instanceof C0576c;
    }

    private d.a.f.g a(b.a aVar) {
        float[] b2 = b(aVar);
        int i2 = aVar.f8781a;
        if (i2 == -4) {
            return new f(b2[0]);
        }
        if (i2 == -3) {
            return new a(b2[0]);
        }
        if (i2 != -2) {
            return null;
        }
        return new i(b2[0], b2[1]);
    }

    private void a(float f2, double d2) {
        double[] dArr = this.f8768c;
        double d3 = dArr[0];
        double d4 = dArr[1];
        float f3 = f2;
        a(dArr, f2, d2);
        b.a a2 = this.f.a(this.i, d3, this.f8768c[0]);
        if (a2 != null) {
            c(a2);
        }
        double[] dArr2 = this.f8768c;
        dArr2[0] = d3;
        dArr2[1] = d4;
    }

    private void a(double[] dArr, float f2, double d2) {
        double b2 = b(d2);
        double a2 = a(dArr[0]);
        dArr[1] = this.n.a(dArr[1], f2, b2, a2);
        dArr[0] = c(a2 + (dArr[1] * ((double) f2)));
    }

    private double b(double d2, double d3) {
        return b(d2) - a(d3);
    }

    private void b(long j2) {
        this.o++;
        v();
        float f2 = ((float) j2) / 1000.0f;
        double r = r();
        a(this.f8768c, f2, r);
        double[] dArr = this.f8768c;
        this.e = a(dArr[0], dArr[1]);
        if (this.e) {
            this.g.setVelocity(this.i, this.f8768c[1]);
            d(this.f8768c[0]);
            a(f2, r);
            return;
        }
        q();
    }

    private float[] b(b.a aVar) {
        if (aVar.f8782b.length == 0) {
            int i2 = aVar.f8781a;
            if (i2 == -4) {
                return new float[]{0.4761905f};
            } else if (i2 == -2) {
                return this.g.getVelocity(this.i) > 0.0d ? new float[]{0.65f, 0.35f} : new float[]{1.0f, 0.35f};
            }
        }
        return aVar.f8782b;
    }

    private void c(b.a aVar) {
        Log.d("miuix_anim", this + ".setEase, " + this.i.getName() + ", ease = " + aVar);
        if (b.a(aVar.f8781a)) {
            this.n = a(aVar);
            if (this.f8769d == null) {
                this.f8769d = new e(this.g, this.i);
            }
        } else if (aVar instanceof b.C0075b) {
            b.C0075b bVar = (b.C0075b) aVar;
            this.k = b.a(bVar);
            this.l = bVar.f8783c;
        }
    }

    private void d(double d2) {
        if (this.j) {
            this.g.setIntValue((C0576c) this.i, (int) d2);
            return;
        }
        d dVar = this.g;
        C0575b bVar = this.i;
        dVar.setValue(bVar, dVar.shouldUseIntValue(bVar) ? (float) ((int) d2) : (float) d2);
    }

    private void q() {
        if (!h.a(this.n)) {
            t();
            return;
        }
        double r = r();
        double b2 = b(r, this.f8768c[0]);
        double ceil = Math.ceil(Math.abs(b2) / ((double) this.g.getMinVisibleChange(this.i)));
        if (ceil < 3.0d) {
            Log.d("miuix_anim", "doFinishProcess, " + this.i + ", ratio = " + ceil + ", targetValue = " + r + ", value = " + this.f8768c[0] + ", diff = " + b2 + ", frameCount = " + this.o);
            t();
            return;
        }
        Log.d("miuix_anim", "doFinishProcess, start spring back");
        c(d.a.i.a.f8776a);
        this.e = true;
    }

    private double r() {
        return this.j ? (double) i() : (double) j();
    }

    private void s() {
        e eVar = this.f8769d;
        if (eVar != null) {
            eVar.a(r());
        }
    }

    private void t() {
        this.g.setVelocity(this.i, 0.0d);
        if (h.a(this.n)) {
            d(r());
        } else {
            d(this.f8768c[0]);
        }
    }

    private void u() {
        this.e = this.m < this.l;
        float a2 = a(!this.e ? 1.0f : this.k.getInterpolation(((float) this.m) / ((float) this.l)));
        if (this.i instanceof C0576c) {
            this.g.setIntValue((C0576c) this.i, (int) c((double) ((Integer) f().evaluate(a2, Integer.valueOf(this.f8767b[0]), Integer.valueOf(this.f8767b[1]))).intValue()));
        } else {
            this.g.setValue(this.i, (float) c((double) ((Float) f().evaluate(a2, Float.valueOf(this.f8766a[0]), Float.valueOf(this.f8766a[1]))).floatValue()));
        }
    }

    private void v() {
        if (this.n != null) {
            double intValue = this.j ? (double) this.g.getIntValue((C0576c) this.i) : (double) this.g.getValue(this.i);
            if (!(this.j || this.g.shouldUseIntValue(this.i)) || Math.abs(this.f8768c[0] - intValue) > 1.0d) {
                this.f8768c[0] = intValue;
            }
            this.f8768c[1] = this.g.getVelocity(this.i);
        }
    }

    /* access modifiers changed from: protected */
    public double a(double d2) {
        return d2;
    }

    /* access modifiers changed from: protected */
    public float a(float f2) {
        return f2;
    }

    public void a() {
        if (this.e) {
            this.e = false;
            l();
        }
    }

    public void a(long j2) {
        if (this.e) {
            this.m += j2;
            if (this.n != null) {
                b(j2);
            } else if (this.k != null) {
                u();
            }
            n();
            if (!this.e) {
                l();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void a(g gVar) {
    }

    public void a(d dVar) {
        this.g = dVar;
    }

    public void a(float... fArr) {
        if (fArr.length == 1) {
            this.f8766a = new float[]{e(), fArr[0]};
        } else {
            this.f8766a = fArr;
        }
        s();
    }

    public void a(int... iArr) {
        if (iArr.length == 1) {
            this.f8767b = new int[]{d(), iArr[0]};
        } else {
            this.f8767b = iArr;
        }
        s();
    }

    /* access modifiers changed from: protected */
    public boolean a(double d2, double d3) {
        return !this.f8769d.a(d2, d3);
    }

    /* access modifiers changed from: protected */
    public double b(double d2) {
        return d2;
    }

    public void b() {
        this.e = false;
        Arrays.fill(this.f8768c, 0.0d);
        this.f = null;
        this.f8766a = null;
        this.f8767b = null;
        this.k = null;
        this.n = null;
        this.f8769d = null;
        this.m = 0;
    }

    public final void b(g gVar) {
        this.f = gVar;
        c(this.f.a(this.i));
        a(gVar);
    }

    /* access modifiers changed from: protected */
    public double c(double d2) {
        return d2;
    }

    public void c() {
        if (this.i instanceof C0576c) {
            int i2 = i();
            if (i2 != Integer.MAX_VALUE) {
                this.g.setIntValue((C0576c) this.i, i2);
            }
        } else {
            float j2 = j();
            if (j2 != Float.MAX_VALUE) {
                this.g.setValue(this.i, j2);
            }
        }
        a();
    }

    public int d() {
        C0575b bVar = this.i;
        if (bVar instanceof C0576c) {
            return this.g.getIntValue((C0576c) bVar);
        }
        return Integer.MAX_VALUE;
    }

    public float e() {
        return this.g.getValue(this.i);
    }

    /* access modifiers changed from: protected */
    public TypeEvaluator f() {
        return this.i instanceof C0576c ? new IntEvaluator() : new FloatEvaluator();
    }

    public long g() {
        return this.m;
    }

    public d h() {
        return this.g;
    }

    public int i() {
        int[] iArr = this.f8767b;
        if (iArr == null || iArr.length == 0) {
            return Integer.MAX_VALUE;
        }
        return iArr.length > 1 ? iArr[1] : iArr[0];
    }

    public float j() {
        float[] fArr = this.f8766a;
        if (fArr == null || fArr.length == 0) {
            return Float.MAX_VALUE;
        }
        return fArr.length > 1 ? fArr[1] : fArr[0];
    }

    public boolean k() {
        return this.e;
    }

    /* access modifiers changed from: protected */
    public void l() {
    }

    /* access modifiers changed from: protected */
    public void m() {
    }

    /* access modifiers changed from: protected */
    public void n() {
    }

    public void o() {
        this.m = 0;
    }

    public void p() {
        if (this.e) {
            return;
        }
        if (this.k != null || this.n != null) {
            this.e = true;
            this.m = 0;
            m();
            s();
        }
    }
}
