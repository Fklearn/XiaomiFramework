package d.g.b;

import android.content.Context;
import android.view.animation.AnimationUtils;
import d.g.a.a.a.s;
import d.g.a.a.a.t;
import d.g.a.a.a.v;
import d.g.a.a.a.x;
import d.g.a.a.a.y;
import d.g.b.d;

class b extends d.a implements t.b {
    private v q = new v();
    private x r = new x(this.q);
    /* access modifiers changed from: private */
    public t s;
    /* access modifiers changed from: private */
    public a t;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        s<?> f8846a;

        /* renamed from: b  reason: collision with root package name */
        int f8847b;

        /* renamed from: c  reason: collision with root package name */
        private final int f8848c;

        /* renamed from: d  reason: collision with root package name */
        private final int f8849d;
        float e;
        int f;
        private C0078b g;
        /* access modifiers changed from: private */
        public float h;
        /* access modifiers changed from: private */
        public float i;
        private long j;
        private C0077a k = new C0077a(this, (a) null);

        /* renamed from: d.g.b.b$a$a  reason: collision with other inner class name */
        private class C0077a implements s.c {
            private C0077a() {
            }

            /* synthetic */ C0077a(a aVar, a aVar2) {
                this();
            }

            public void a(s sVar, float f, float f2) {
                a aVar = a.this;
                aVar.e = f2;
                aVar.f = aVar.f8847b + ((int) f);
                c.b("%s updating value(%f), velocity(%f), min(%f), max(%f)", sVar.getClass().getSimpleName(), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(a.this.h), Float.valueOf(a.this.i));
            }
        }

        /* renamed from: d.g.b.b$a$b  reason: collision with other inner class name */
        interface C0078b {
            boolean a(float f, float f2);
        }

        a(s<?> sVar, int i2, float f2) {
            this.f8846a = sVar;
            this.f8846a.b(-3.4028235E38f);
            this.f8846a.a(Float.MAX_VALUE);
            this.f8847b = i2;
            this.e = f2;
            int i3 = Integer.MAX_VALUE;
            int i4 = Integer.MIN_VALUE;
            if (i2 > 0) {
                i4 = Integer.MIN_VALUE + i2;
            } else if (i2 < 0) {
                i3 = Integer.MAX_VALUE + i2;
            }
            this.f8848c = i4;
            this.f8849d = i3;
            this.f8846a.e(0.0f);
            this.f8846a.f(f2);
        }

        /* access modifiers changed from: package-private */
        public int a(int i2) {
            return i2 - this.f8847b;
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.j = 0;
            this.f8846a.a();
            this.f8846a.b((s.c) this.k);
        }

        /* access modifiers changed from: package-private */
        public void a(C0078b bVar) {
            this.g = bVar;
        }

        /* access modifiers changed from: package-private */
        public void b(int i2) {
            int i3 = this.f8849d;
            if (i2 > i3) {
                i2 = i3;
            }
            float max = (float) Math.max(i2 - this.f8847b, 0);
            this.f8846a.a(max);
            this.i = max;
        }

        /* access modifiers changed from: package-private */
        public boolean b() {
            C0078b bVar = this.g;
            if (bVar != null) {
                return bVar.a((float) this.f, this.e);
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public s<?> c() {
            return this.f8846a;
        }

        /* access modifiers changed from: package-private */
        public void c(int i2) {
            int i3 = this.f8848c;
            if (i2 < i3) {
                i2 = i3;
            }
            float min = (float) Math.min(i2 - this.f8847b, 0);
            this.f8846a.b(min);
            this.h = min;
        }

        /* access modifiers changed from: package-private */
        public void d() {
            this.f8846a.a((s.c) this.k);
            this.f8846a.a(true);
            this.j = 0;
        }

        /* access modifiers changed from: package-private */
        public boolean e() {
            long j2 = this.j;
            long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            if (currentAnimationTimeMillis == j2) {
                c.b("update done in this frame, dropping current update request");
                return !this.f8846a.c();
            }
            boolean doAnimationFrame = this.f8846a.doAnimationFrame(currentAnimationTimeMillis);
            if (doAnimationFrame) {
                c.b("%s finishing value(%d) velocity(%f)", this.f8846a.getClass().getSimpleName(), Integer.valueOf(this.f), Float.valueOf(this.e));
                this.f8846a.b((s.c) this.k);
                this.j = 0;
            }
            this.j = currentAnimationTimeMillis;
            return doAnimationFrame;
        }
    }

    b(Context context) {
        super(context);
        this.r.a(new y());
        this.r.c(0.5f);
        this.r.d().a(0.97f);
        this.r.d().c(130.5f);
        this.r.d().a(1000.0d);
        this.s = new t(this.q, this);
        this.s.c(0.5f);
        this.s.i(0.4761905f);
    }

    /* access modifiers changed from: private */
    public void a(int i, int i2, float f, int i3, int i4) {
        if (f > 8000.0f) {
            c.a("%f is too fast for spring, slow down", Float.valueOf(f));
            f = 8000.0f;
        }
        a(false);
        a(f);
        a(AnimationUtils.currentAnimationTimeMillis());
        b(i2);
        f(i2);
        c(Integer.MAX_VALUE);
        d(i3);
        g(i);
        this.t = new a(this.r, i2, f);
        this.r.d().b((float) this.t.a(i3));
        if (i4 != 0) {
            if (f < 0.0f) {
                this.t.c(i3 - i4);
                this.t.b(Math.max(i3, i2));
            } else {
                this.t.c(Math.min(i3, i2));
                this.t.b(i3 + i4);
            }
        }
        this.t.d();
    }

    private void b(int i, int i2, int i3, int i4, int i5) {
        int i6;
        int i7;
        this.s.e(0.0f);
        float f = (float) i2;
        this.s.f(f);
        long e = ((long) i) + ((long) this.s.e());
        if (e > ((long) i4)) {
            i7 = (int) this.s.h((float) (i4 - i));
            i6 = i4;
        } else if (e < ((long) i3)) {
            i7 = (int) this.s.h((float) (i3 - i));
            i6 = i3;
        } else {
            i6 = (int) e;
            i7 = (int) this.s.d();
        }
        a(false);
        a(f);
        a(AnimationUtils.currentAnimationTimeMillis());
        b(i);
        f(i);
        c(i7);
        d(i6);
        g(0);
        int min = Math.min(i3, i);
        int max = Math.max(i4, i);
        this.t = new a(this.s, i, f);
        this.t.a((a.C0078b) new a(this, i3, i4, i5));
        this.t.c(min);
        this.t.b(max);
        this.t.d();
    }

    private void c(int i, int i2, int i3, int i4, int i5) {
        float f;
        int i6;
        boolean z = false;
        c.a("startAfterEdge: start(%d) velocity(%d) boundary(%d, %d) over(%d)", Integer.valueOf(i), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i5));
        if (i <= i2 || i >= i3) {
            boolean z2 = i > i3;
            int i7 = z2 ? i3 : i2;
            int i8 = i - i7;
            if (i4 != 0 && Integer.signum(i8) * i4 >= 0) {
                z = true;
            }
            if (z) {
                c.a("spring forward");
                i6 = 2;
                f = (float) i4;
            } else {
                this.s.e((float) i);
                f = (float) i4;
                this.s.f(f);
                float e = this.s.e();
                if ((!z2 || e >= ((float) i3)) && (z2 || e <= ((float) i2))) {
                    c.a("spring backward");
                    i6 = 1;
                } else {
                    c.a("fling to content");
                    b(i, i4, i2, i3, i5);
                    return;
                }
            }
            a(i6, i, f, i7, i5);
            return;
        }
        a(true);
    }

    /* access modifiers changed from: private */
    public void i() {
        if (this.t != null) {
            c.a("resetting current handler: state(%d), anim(%s), value(%d), velocity(%f)", Integer.valueOf(f()), this.t.c().getClass().getSimpleName(), Integer.valueOf(this.t.f), Float.valueOf(this.t.e));
            this.t.a();
            this.t = null;
        }
    }

    public void a(double d2) {
        float f;
        y yVar;
        if (Math.abs(d2) <= 5000.0d) {
            yVar = this.r.d();
            f = 246.7f;
        } else {
            yVar = this.r.d();
            f = 130.5f;
        }
        yVar.c(f);
    }

    public void a(int i) {
        e(i);
    }

    /* access modifiers changed from: package-private */
    public void a(int i, int i2, int i3) {
        if (f() == 0) {
            if (this.t != null) {
                i();
            }
            c(i, i2, i2, (int) c(), i3);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i, int i2, int i3, int i4, int i5) {
        c.a("FLING: start(%d) velocity(%d) boundary(%d, %d) over(%d)", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5));
        i();
        if (i2 == 0) {
            b(i);
            f(i);
            d(i);
            c(0);
            a(true);
            return;
        }
        a((double) i2);
        if (i > i4 || i < i3) {
            c(i, i3, i4, i2, i5);
        } else {
            b(i, i2, i3, i4, i5);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean a() {
        a aVar = this.t;
        if (aVar == null || !aVar.b()) {
            return false;
        }
        c.a("checking have more work when finish");
        h();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void b() {
        c.a("finish scroller");
        b(e());
        a(true);
        i();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean b(int r9, int r10, int r11) {
        /*
            r8 = this;
            r0 = 3
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r6 = 0
            r0[r6] = r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r7 = 1
            r0[r7] = r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r11)
            r3 = 2
            r0[r3] = r1
            java.lang.String r1 = "SPRING_BACK start(%d) boundary(%d, %d)"
            d.g.b.c.a(r1, r0)
            d.g.b.b$a r0 = r8.t
            if (r0 == 0) goto L_0x0024
            r8.i()
        L_0x0024:
            if (r9 >= r10) goto L_0x0030
            r1 = 1
            r3 = 0
            r5 = 0
            r0 = r8
            r2 = r9
            r4 = r10
        L_0x002c:
            r0.a((int) r1, (int) r2, (float) r3, (int) r4, (int) r5)
            goto L_0x0048
        L_0x0030:
            if (r9 <= r11) goto L_0x0039
            r1 = 1
            r3 = 0
            r5 = 0
            r0 = r8
            r2 = r9
            r4 = r11
            goto L_0x002c
        L_0x0039:
            r8.b((int) r9)
            r8.f((int) r9)
            r8.d((int) r9)
            r8.c((int) r6)
            r8.a((boolean) r7)
        L_0x0048:
            boolean r0 = r8.g()
            if (r0 != 0) goto L_0x004f
            r6 = r7
        L_0x004f:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: d.g.b.b.b(int, int, int):boolean");
    }

    /* access modifiers changed from: package-private */
    public void e(int i) {
        super.e(i);
    }

    /* access modifiers changed from: package-private */
    public boolean h() {
        a aVar = this.t;
        if (aVar == null) {
            c.a("no handler found, aborting");
            return false;
        }
        boolean e = aVar.e();
        b(this.t.f);
        a(this.t.e);
        if (f() == 2 && Math.signum((float) this.t.f) * Math.signum(this.t.e) < 0.0f) {
            c.a("State Changed: BALLISTIC -> CUBIC");
            g(1);
        }
        return !e;
    }
}
