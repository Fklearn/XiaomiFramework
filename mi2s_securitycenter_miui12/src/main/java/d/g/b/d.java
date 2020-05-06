package d.g.b;

import android.content.Context;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private int f8853a;

    /* renamed from: b  reason: collision with root package name */
    private final a f8854b;

    /* renamed from: c  reason: collision with root package name */
    private final a f8855c;

    /* renamed from: d  reason: collision with root package name */
    private Interpolator f8856d;
    private final boolean e;

    static class a {

        /* renamed from: a  reason: collision with root package name */
        private static float f8857a = ((float) (Math.log(0.78d) / Math.log(0.9d)));

        /* renamed from: b  reason: collision with root package name */
        private static final float[] f8858b = new float[101];

        /* renamed from: c  reason: collision with root package name */
        private static final float[] f8859c = new float[101];

        /* renamed from: d  reason: collision with root package name */
        private Context f8860d;
        private int e;
        /* access modifiers changed from: private */
        public int f;
        /* access modifiers changed from: private */
        public int g;
        private int h;
        /* access modifiers changed from: private */
        public float i;
        private float j;
        /* access modifiers changed from: private */
        public long k;
        /* access modifiers changed from: private */
        public int l;
        /* access modifiers changed from: private */
        public boolean m;
        private float n = ViewConfiguration.getScrollFriction();
        private int o = 0;
        private float p;

        static {
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            float f8;
            float f9;
            float f10;
            float f11;
            float f12 = 0.0f;
            float f13 = 0.0f;
            for (int i2 = 0; i2 < 100; i2++) {
                float f14 = ((float) i2) / 100.0f;
                float f15 = 1.0f;
                while (true) {
                    f2 = 2.0f;
                    f3 = ((f15 - f12) / 2.0f) + f12;
                    f4 = 3.0f;
                    f5 = 1.0f - f3;
                    f6 = f3 * 3.0f * f5;
                    f7 = f3 * f3 * f3;
                    float f16 = (((f5 * 0.175f) + (f3 * 0.35000002f)) * f6) + f7;
                    float f17 = f16;
                    if (((double) Math.abs(f16 - f14)) < 1.0E-5d) {
                        break;
                    } else if (f17 > f14) {
                        f15 = f3;
                    } else {
                        f12 = f3;
                    }
                }
                f8858b[i2] = (f6 * ((f5 * 0.5f) + f3)) + f7;
                float f18 = 1.0f;
                while (true) {
                    f8 = ((f18 - f13) / f2) + f13;
                    f9 = 1.0f - f8;
                    f10 = f8 * f4 * f9;
                    f11 = f8 * f8 * f8;
                    float f19 = (((f9 * 0.5f) + f8) * f10) + f11;
                    if (((double) Math.abs(f19 - f14)) < 1.0E-5d) {
                        break;
                    }
                    if (f19 > f14) {
                        f18 = f8;
                    } else {
                        f13 = f8;
                    }
                    f2 = 2.0f;
                    f4 = 3.0f;
                }
                f8859c[i2] = (f10 * ((f9 * 0.175f) + (f8 * 0.35000002f))) + f11;
            }
            float[] fArr = f8858b;
            f8859c[100] = 1.0f;
            fArr[100] = 1.0f;
        }

        a(Context context) {
            this.f8860d = context;
            this.m = true;
            this.p = context.getResources().getDisplayMetrics().density * 160.0f * 386.0878f * 0.84f;
        }

        /* access modifiers changed from: package-private */
        public final void a(float f2) {
            this.i = f2;
        }

        /* access modifiers changed from: package-private */
        public void a(int i2, int i3, int i4) {
            throw null;
        }

        /* access modifiers changed from: package-private */
        public void a(int i2, int i3, int i4, int i5, int i6) {
            throw null;
        }

        /* access modifiers changed from: package-private */
        public final void a(long j2) {
            this.k = j2;
        }

        /* access modifiers changed from: package-private */
        public final void a(boolean z) {
            this.m = z;
        }

        /* access modifiers changed from: package-private */
        public boolean a() {
            throw null;
        }

        /* access modifiers changed from: package-private */
        public void b() {
            throw null;
        }

        /* access modifiers changed from: package-private */
        public void b(float f2) {
            int i2 = this.e;
            this.f = i2 + Math.round(f2 * ((float) (this.g - i2)));
        }

        /* access modifiers changed from: package-private */
        public final void b(int i2) {
            this.f = i2;
        }

        /* access modifiers changed from: package-private */
        public boolean b(int i2, int i3, int i4) {
            throw null;
        }

        /* access modifiers changed from: package-private */
        public final float c() {
            return this.i;
        }

        /* access modifiers changed from: package-private */
        public final void c(int i2) {
            this.l = i2;
        }

        /* access modifiers changed from: package-private */
        public void c(int i2, int i3, int i4) {
            this.m = false;
            this.e = i2;
            this.f = i2;
            this.g = i2 + i3;
            this.k = AnimationUtils.currentAnimationTimeMillis();
            this.l = i4;
            this.j = 0.0f;
            this.h = 0;
        }

        /* access modifiers changed from: package-private */
        public final int d() {
            return this.f;
        }

        /* access modifiers changed from: package-private */
        public final void d(int i2) {
            this.g = i2;
        }

        /* access modifiers changed from: package-private */
        public final int e() {
            return this.g;
        }

        /* access modifiers changed from: package-private */
        public void e(int i2) {
            this.g = i2;
            this.m = false;
        }

        /* access modifiers changed from: package-private */
        public final int f() {
            return this.o;
        }

        /* access modifiers changed from: package-private */
        public final void f(int i2) {
            this.e = i2;
        }

        /* access modifiers changed from: package-private */
        public final void g(int i2) {
            this.o = i2;
        }

        /* access modifiers changed from: package-private */
        public final boolean g() {
            return this.m;
        }

        /* access modifiers changed from: package-private */
        public boolean h() {
            throw null;
        }
    }

    static class b implements Interpolator {

        /* renamed from: a  reason: collision with root package name */
        private static final float f8861a = (1.0f / a(1.0f));

        /* renamed from: b  reason: collision with root package name */
        private static final float f8862b = (1.0f - (f8861a * a(1.0f)));

        b() {
        }

        private static float a(float f) {
            float f2 = f * 8.0f;
            return f2 < 1.0f ? f2 - (1.0f - ((float) Math.exp((double) (-f2)))) : ((1.0f - ((float) Math.exp((double) (1.0f - f2)))) * 0.63212055f) + 0.36787945f;
        }

        public float getInterpolation(float f) {
            float a2 = f8861a * a(f);
            return a2 > 0.0f ? a2 + f8862b : a2;
        }
    }

    public d(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public d(Context context, Interpolator interpolator, boolean z) {
        this.f8856d = interpolator == null ? new b() : interpolator;
        this.e = z;
        this.f8854b = new b(context);
        this.f8855c = new b(context);
    }

    public void a() {
        this.f8854b.b();
        this.f8855c.b();
    }

    public void a(int i, int i2, int i3) {
        this.f8854b.a(i, i2, i3);
    }

    public void a(int i, int i2, int i3, int i4, int i5) {
        this.f8853a = 0;
        this.f8854b.c(i, i3, i5);
        this.f8855c.c(i2, i4, i5);
    }

    public void a(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        a(i, i2, i3, i4, i5, i6, i7, i8, 0, 0);
    }

    public void a(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        int i11;
        int i12;
        int i13;
        int i14;
        if (!this.e || j()) {
            i14 = i3;
        } else {
            float c2 = this.f8854b.i;
            float c3 = this.f8855c.i;
            i14 = i3;
            float f = (float) i14;
            if (Math.signum(f) == Math.signum(c2)) {
                i13 = i4;
                float f2 = (float) i13;
                if (Math.signum(f2) == Math.signum(c3)) {
                    i12 = (int) (f2 + c3);
                    i11 = (int) (f + c2);
                    this.f8853a = 1;
                    this.f8854b.a(i, i11, i5, i6, i9);
                    this.f8855c.a(i2, i12, i7, i8, i10);
                }
                i12 = i13;
                i11 = i14;
                this.f8853a = 1;
                this.f8854b.a(i, i11, i5, i6, i9);
                this.f8855c.a(i2, i12, i7, i8, i10);
            }
        }
        i13 = i4;
        i12 = i13;
        i11 = i14;
        this.f8853a = 1;
        this.f8854b.a(i, i11, i5, i6, i9);
        this.f8855c.a(i2, i12, i7, i8, i10);
    }

    public boolean a(int i, int i2, int i3, int i4, int i5, int i6) {
        this.f8853a = 1;
        return this.f8854b.b(i, i3, i4) || this.f8855c.b(i2, i5, i6);
    }

    public void b(int i, int i2, int i3) {
        this.f8855c.a(i, i2, i3);
    }

    public boolean b() {
        if (j()) {
            return false;
        }
        int i = this.f8853a;
        if (i == 0) {
            long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis() - this.f8854b.k;
            int e2 = this.f8854b.l;
            if (currentAnimationTimeMillis < ((long) e2)) {
                float interpolation = this.f8856d.getInterpolation(((float) currentAnimationTimeMillis) / ((float) e2));
                this.f8854b.b(interpolation);
                this.f8855c.b(interpolation);
            } else {
                a();
            }
        } else if (i == 1) {
            if (!this.f8854b.m && !this.f8854b.h() && !this.f8854b.a()) {
                this.f8854b.b();
            }
            if (!this.f8855c.m && !this.f8855c.h() && !this.f8855c.a()) {
                this.f8855c.b();
            }
        }
        return true;
    }

    public float c() {
        return (float) Math.hypot((double) this.f8854b.i, (double) this.f8855c.i);
    }

    public float d() {
        return this.f8854b.i;
    }

    public float e() {
        return this.f8855c.i;
    }

    public final int f() {
        return this.f8854b.f;
    }

    public final int g() {
        return this.f8855c.f;
    }

    public final int h() {
        return this.f8854b.g;
    }

    public final int i() {
        return this.f8855c.g;
    }

    public final boolean j() {
        return this.f8854b.m && this.f8855c.m;
    }
}
