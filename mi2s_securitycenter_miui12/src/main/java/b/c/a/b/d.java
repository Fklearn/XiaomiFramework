package b.c.a.b;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;

public final class d {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public final int f2006a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public final int f2007b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public final int f2008c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public final Drawable f2009d;
    /* access modifiers changed from: private */
    public final Drawable e;
    /* access modifiers changed from: private */
    public final Drawable f;
    /* access modifiers changed from: private */
    public final boolean g;
    /* access modifiers changed from: private */
    public final boolean h;
    /* access modifiers changed from: private */
    public final boolean i;
    /* access modifiers changed from: private */
    public final b.c.a.b.a.d j;
    /* access modifiers changed from: private */
    public final BitmapFactory.Options k;
    /* access modifiers changed from: private */
    public final int l;
    /* access modifiers changed from: private */
    public final boolean m;
    /* access modifiers changed from: private */
    public final Object n;
    /* access modifiers changed from: private */
    public final b.c.a.b.g.a o;
    /* access modifiers changed from: private */
    public final b.c.a.b.g.a p;
    /* access modifiers changed from: private */
    public final b.c.a.b.c.a q;
    /* access modifiers changed from: private */
    public final Handler r;
    /* access modifiers changed from: private */
    public final boolean s;

    public static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public int f2010a = 0;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public int f2011b = 0;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public int f2012c = 0;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public Drawable f2013d = null;
        /* access modifiers changed from: private */
        public Drawable e = null;
        /* access modifiers changed from: private */
        public Drawable f = null;
        /* access modifiers changed from: private */
        public boolean g = false;
        /* access modifiers changed from: private */
        public boolean h = false;
        /* access modifiers changed from: private */
        public boolean i = false;
        /* access modifiers changed from: private */
        public b.c.a.b.a.d j = b.c.a.b.a.d.IN_SAMPLE_POWER_OF_2;
        /* access modifiers changed from: private */
        public BitmapFactory.Options k = new BitmapFactory.Options();
        /* access modifiers changed from: private */
        public int l = 0;
        /* access modifiers changed from: private */
        public boolean m = false;
        /* access modifiers changed from: private */
        public Object n = null;
        /* access modifiers changed from: private */
        public b.c.a.b.g.a o = null;
        /* access modifiers changed from: private */
        public b.c.a.b.g.a p = null;
        /* access modifiers changed from: private */
        public b.c.a.b.c.a q = a.a();
        /* access modifiers changed from: private */
        public Handler r = null;
        /* access modifiers changed from: private */
        public boolean s = false;

        public a a(int i2) {
            this.f2011b = i2;
            return this;
        }

        public a a(Bitmap.Config config) {
            if (config != null) {
                this.k.inPreferredConfig = config;
                return this;
            }
            throw new IllegalArgumentException("bitmapConfig can't be null");
        }

        public a a(b.c.a.b.a.d dVar) {
            this.j = dVar;
            return this;
        }

        public a a(b.c.a.b.c.a aVar) {
            if (aVar != null) {
                this.q = aVar;
                return this;
            }
            throw new IllegalArgumentException("displayer can't be null");
        }

        public a a(d dVar) {
            this.f2010a = dVar.f2006a;
            this.f2011b = dVar.f2007b;
            this.f2012c = dVar.f2008c;
            this.f2013d = dVar.f2009d;
            this.e = dVar.e;
            this.f = dVar.f;
            this.g = dVar.g;
            this.h = dVar.h;
            this.i = dVar.i;
            this.j = dVar.j;
            this.k = dVar.k;
            this.l = dVar.l;
            this.m = dVar.m;
            this.n = dVar.n;
            this.o = dVar.o;
            this.p = dVar.p;
            this.q = dVar.q;
            this.r = dVar.r;
            this.s = dVar.s;
            return this;
        }

        public a a(b.c.a.b.g.a aVar) {
            this.o = aVar;
            return this;
        }

        public a a(boolean z) {
            this.h = z;
            return this;
        }

        public d a() {
            return new d(this);
        }

        public a b(int i2) {
            this.f2012c = i2;
            return this;
        }

        public a b(boolean z) {
            this.i = z;
            return this;
        }

        public a c(int i2) {
            this.f2010a = i2;
            return this;
        }

        public a c(boolean z) {
            this.m = z;
            return this;
        }

        /* access modifiers changed from: package-private */
        public a d(boolean z) {
            this.s = z;
            return this;
        }
    }

    private d(a aVar) {
        this.f2006a = aVar.f2010a;
        this.f2007b = aVar.f2011b;
        this.f2008c = aVar.f2012c;
        this.f2009d = aVar.f2013d;
        this.e = aVar.e;
        this.f = aVar.f;
        this.g = aVar.g;
        this.h = aVar.h;
        this.i = aVar.i;
        this.j = aVar.j;
        this.k = aVar.k;
        this.l = aVar.l;
        this.m = aVar.m;
        this.n = aVar.n;
        this.o = aVar.o;
        this.p = aVar.p;
        this.q = aVar.q;
        this.r = aVar.r;
        this.s = aVar.s;
    }

    public static d a() {
        return new a().a();
    }

    public Drawable a(Resources resources) {
        int i2 = this.f2007b;
        return i2 != 0 ? resources.getDrawable(i2) : this.e;
    }

    public BitmapFactory.Options b() {
        return this.k;
    }

    public Drawable b(Resources resources) {
        int i2 = this.f2008c;
        return i2 != 0 ? resources.getDrawable(i2) : this.f;
    }

    public int c() {
        return this.l;
    }

    public Drawable c(Resources resources) {
        int i2 = this.f2006a;
        return i2 != 0 ? resources.getDrawable(i2) : this.f2009d;
    }

    public b.c.a.b.c.a d() {
        return this.q;
    }

    public Object e() {
        return this.n;
    }

    public Handler f() {
        return this.r;
    }

    public b.c.a.b.a.d g() {
        return this.j;
    }

    public b.c.a.b.g.a h() {
        return this.p;
    }

    public b.c.a.b.g.a i() {
        return this.o;
    }

    public boolean j() {
        return this.h;
    }

    public boolean k() {
        return this.i;
    }

    public boolean l() {
        return this.m;
    }

    public boolean m() {
        return this.g;
    }

    /* access modifiers changed from: package-private */
    public boolean n() {
        return this.s;
    }

    public boolean o() {
        return this.l > 0;
    }

    public boolean p() {
        return this.p != null;
    }

    public boolean q() {
        return this.o != null;
    }

    public boolean r() {
        return (this.e == null && this.f2007b == 0) ? false : true;
    }

    public boolean s() {
        return (this.f == null && this.f2008c == 0) ? false : true;
    }

    public boolean t() {
        return (this.f2009d == null && this.f2006a == 0) ? false : true;
    }
}
