package b.c.a.b;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import b.c.a.b.a.e;
import b.c.a.b.a.g;
import b.c.a.b.d.d;
import b.c.a.c.f;
import java.io.InputStream;
import java.util.concurrent.Executor;

public final class h {

    /* renamed from: a  reason: collision with root package name */
    final Resources f2034a;

    /* renamed from: b  reason: collision with root package name */
    final int f2035b;

    /* renamed from: c  reason: collision with root package name */
    final int f2036c;

    /* renamed from: d  reason: collision with root package name */
    final int f2037d;
    final int e;
    final b.c.a.b.g.a f;
    final Executor g;
    final Executor h;
    final boolean i;
    final boolean j;
    final int k;
    final int l;
    final g m;
    final b.c.a.a.b.a n;
    final b.c.a.a.a.a o;
    final d p;
    final b.c.a.b.b.b q;
    final d r;
    final d s;
    final d t;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public static final g f2038a = g.FIFO;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public Context f2039b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public int f2040c = 0;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public int f2041d = 0;
        /* access modifiers changed from: private */
        public int e = 0;
        /* access modifiers changed from: private */
        public int f = 0;
        /* access modifiers changed from: private */
        public b.c.a.b.g.a g = null;
        /* access modifiers changed from: private */
        public Executor h = null;
        /* access modifiers changed from: private */
        public Executor i = null;
        /* access modifiers changed from: private */
        public boolean j = false;
        /* access modifiers changed from: private */
        public boolean k = false;
        /* access modifiers changed from: private */
        public int l = 3;
        /* access modifiers changed from: private */
        public int m = 3;
        private boolean n = false;
        /* access modifiers changed from: private */
        public g o = f2038a;
        private int p = 0;
        private long q = 0;
        private int r = 0;
        /* access modifiers changed from: private */
        public b.c.a.a.b.a s = null;
        /* access modifiers changed from: private */
        public b.c.a.a.a.a t = null;
        private b.c.a.a.a.b.a u = null;
        /* access modifiers changed from: private */
        public d v = null;
        /* access modifiers changed from: private */
        public b.c.a.b.b.b w;
        /* access modifiers changed from: private */
        public d x = null;
        /* access modifiers changed from: private */
        public boolean y = false;

        public a(Context context) {
            this.f2039b = context.getApplicationContext();
        }

        private void b() {
            if (this.h == null) {
                this.h = a.a(this.l, this.m, this.o);
            } else {
                this.j = true;
            }
            if (this.i == null) {
                this.i = a.a(this.l, this.m, this.o);
            } else {
                this.k = true;
            }
            if (this.t == null) {
                if (this.u == null) {
                    this.u = a.b();
                }
                this.t = a.a(this.f2039b, this.u, this.q, this.r);
            }
            if (this.s == null) {
                this.s = a.a(this.f2039b, this.p);
            }
            if (this.n) {
                this.s = new b.c.a.a.b.a.a(this.s, f.a());
            }
            if (this.v == null) {
                this.v = a.a(this.f2039b);
            }
            if (this.w == null) {
                this.w = a.a(this.y);
            }
            if (this.x == null) {
                this.x = d.a();
            }
        }

        public a a(int i2) {
            if (i2 > 0) {
                if (this.t != null) {
                    b.c.a.c.d.d("diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other", new Object[0]);
                }
                this.q = (long) i2;
                return this;
            }
            throw new IllegalArgumentException("maxCacheSize must be a positive number");
        }

        public a a(b.c.a.a.a.b.a aVar) {
            if (this.t != null) {
                b.c.a.c.d.d("diskCache() and diskCacheFileNameGenerator() calls overlap each other", new Object[0]);
            }
            this.u = aVar;
            return this;
        }

        public a a(g gVar) {
            if (!(this.h == null && this.i == null)) {
                b.c.a.c.d.d("threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.", new Object[0]);
            }
            this.o = gVar;
            return this;
        }

        public h a() {
            b();
            return new h(this, (g) null);
        }

        public a b(int i2) {
            if (!(this.h == null && this.i == null)) {
                b.c.a.c.d.d("threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.", new Object[0]);
            }
            int i3 = 1;
            if (i2 >= 1) {
                i3 = 10;
                if (i2 <= 10) {
                    this.m = i2;
                    return this;
                }
            }
            this.m = i3;
            return this;
        }
    }

    private static class b implements d {

        /* renamed from: a  reason: collision with root package name */
        private final d f2042a;

        public b(d dVar) {
            this.f2042a = dVar;
        }

        public InputStream a(String str, Object obj) {
            int i = g.f2033a[d.a.b(str).ordinal()];
            if (i != 1 && i != 2) {
                return this.f2042a.a(str, obj);
            }
            throw new IllegalStateException();
        }
    }

    private static class c implements d {

        /* renamed from: a  reason: collision with root package name */
        private final d f2043a;

        public c(d dVar) {
            this.f2043a = dVar;
        }

        public InputStream a(String str, Object obj) {
            InputStream a2 = this.f2043a.a(str, obj);
            int i = g.f2033a[d.a.b(str).ordinal()];
            return (i == 1 || i == 2) ? new b.c.a.b.a.c(a2) : a2;
        }
    }

    private h(a aVar) {
        this.f2034a = aVar.f2039b.getResources();
        this.f2035b = aVar.f2040c;
        this.f2036c = aVar.f2041d;
        this.f2037d = aVar.e;
        this.e = aVar.f;
        this.f = aVar.g;
        this.g = aVar.h;
        this.h = aVar.i;
        this.k = aVar.l;
        this.l = aVar.m;
        this.m = aVar.o;
        this.o = aVar.t;
        this.n = aVar.s;
        this.r = aVar.x;
        this.p = aVar.v;
        this.q = aVar.w;
        this.i = aVar.j;
        this.j = aVar.k;
        this.s = new b(this.p);
        this.t = new c(this.p);
        b.c.a.c.d.a(aVar.y);
    }

    /* synthetic */ h(a aVar, g gVar) {
        this(aVar);
    }

    /* access modifiers changed from: package-private */
    public e a() {
        DisplayMetrics displayMetrics = this.f2034a.getDisplayMetrics();
        int i2 = this.f2035b;
        if (i2 <= 0) {
            i2 = displayMetrics.widthPixels;
        }
        int i3 = this.f2036c;
        if (i3 <= 0) {
            i3 = displayMetrics.heightPixels;
        }
        return new e(i2, i3);
    }
}
