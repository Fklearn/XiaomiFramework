package b.c.a.b;

import android.graphics.Bitmap;
import b.c.a.b.a.f;
import b.c.a.b.e.a;
import b.c.a.c.d;

final class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    private final Bitmap f1987a;

    /* renamed from: b  reason: collision with root package name */
    private final String f1988b;

    /* renamed from: c  reason: collision with root package name */
    private final a f1989c;

    /* renamed from: d  reason: collision with root package name */
    private final String f1990d;
    private final b.c.a.b.c.a e;
    private final b.c.a.b.f.a f;
    private final j g;
    private final f h;

    public b(Bitmap bitmap, k kVar, j jVar, f fVar) {
        this.f1987a = bitmap;
        this.f1988b = kVar.f2050a;
        this.f1989c = kVar.f2052c;
        this.f1990d = kVar.f2051b;
        this.e = kVar.e.d();
        this.f = kVar.f;
        this.g = jVar;
        this.h = fVar;
    }

    private boolean a() {
        return !this.f1990d.equals(this.g.b(this.f1989c));
    }

    public void run() {
        Object[] objArr;
        String str;
        if (this.f1989c.d()) {
            objArr = new Object[]{this.f1990d};
            str = "ImageAware was collected by GC. Task is cancelled. [%s]";
        } else if (a()) {
            objArr = new Object[]{this.f1990d};
            str = "ImageAware is reused for another image. Task is cancelled. [%s]";
        } else {
            d.a("Display image in ImageAware (loaded from %1$s) [%2$s]", this.h, this.f1990d);
            this.e.a(this.f1987a, this.f1989c, this.h);
            this.g.a(this.f1989c);
            this.f.a(this.f1988b, this.f1989c.b(), this.f1987a);
            return;
        }
        d.a(str, objArr);
        this.f.b(this.f1988b, this.f1989c.b());
    }
}
