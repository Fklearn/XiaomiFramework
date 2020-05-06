package b.c.a.b;

import android.graphics.Bitmap;
import android.os.Handler;
import b.c.a.b.a.f;
import b.c.a.c.d;

final class p implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    private final j f2066a;

    /* renamed from: b  reason: collision with root package name */
    private final Bitmap f2067b;

    /* renamed from: c  reason: collision with root package name */
    private final k f2068c;

    /* renamed from: d  reason: collision with root package name */
    private final Handler f2069d;

    public p(j jVar, Bitmap bitmap, k kVar, Handler handler) {
        this.f2066a = jVar;
        this.f2067b = bitmap;
        this.f2068c = kVar;
        this.f2069d = handler;
    }

    public void run() {
        d.a("PostProcess image before displaying [%s]", this.f2068c.f2051b);
        o.a(new b(this.f2068c.e.h().process(this.f2067b), this.f2068c, this.f2066a, f.MEMORY_CACHE), this.f2068c.e.n(), this.f2069d, this.f2066a);
    }
}
