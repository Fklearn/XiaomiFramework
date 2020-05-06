package com.miui.optimizecenter.widget.storage;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private long f5857a;

    /* renamed from: b  reason: collision with root package name */
    private long f5858b;

    /* renamed from: c  reason: collision with root package name */
    private long f5859c;

    /* renamed from: d  reason: collision with root package name */
    private long f5860d;
    private long e;
    private long f;
    private long g;
    private long h;
    private long i;

    public d(long j) {
        this.f5857a = j;
    }

    public long a() {
        return this.f5857a;
    }

    public long a(b bVar) {
        switch (c.f5856a[bVar.ordinal()]) {
            case 1:
                return this.f5858b;
            case 2:
                return this.g;
            case 3:
                return this.h;
            case 4:
                return this.f5860d;
            case 5:
                return this.e;
            case 6:
                return this.f;
            case 7:
                return this.i;
            case 8:
                return this.f5859c;
            case 9:
                return this.f5857a;
            default:
                return 0;
        }
    }

    public d a(long j) {
        this.g = Math.max(j, 0);
        return this;
    }

    public void a(d dVar) {
        if (dVar != null) {
            this.f5857a = dVar.f5857a;
            b(dVar.f5859c);
            e(dVar.f5858b);
            d(dVar.f5860d);
            h(dVar.f);
            g(dVar.e);
            c(dVar.h);
            f(dVar.i);
            a(dVar.g);
        }
    }

    public d b(long j) {
        this.f5859c = Math.max(j, 0);
        return this;
    }

    public d c(long j) {
        this.h = Math.max(j, 0);
        return this;
    }

    public d d(long j) {
        this.f5860d = Math.max(j, 0);
        return this;
    }

    public d e(long j) {
        this.f5858b = Math.max(j, 0);
        return this;
    }

    public d f(long j) {
        this.i = Math.max(j, 0);
        return this;
    }

    public d g(long j) {
        this.e = Math.max(j, 0);
        return this;
    }

    public d h(long j) {
        this.f = Math.max(j, 0);
        return this;
    }

    public String toString() {
        return "StorageInfo{total=" + this.f5857a + ", other=" + this.f5858b + ", appData=" + this.f5859c + ", image=" + this.f5860d + ", video=" + this.e + ", voice=" + this.f + ", apk=" + this.g + ", file=" + this.h + ", system=" + this.i + '}';
    }
}
