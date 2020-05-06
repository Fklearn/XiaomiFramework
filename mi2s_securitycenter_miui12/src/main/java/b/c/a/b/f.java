package b.c.a.b;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import b.c.a.b.a.e;
import b.c.a.b.a.i;
import b.c.a.b.d;
import b.c.a.b.e.b;
import b.c.a.b.e.c;
import b.c.a.b.f.a;
import b.c.a.b.f.d;

public class f {

    /* renamed from: a  reason: collision with root package name */
    public static final String f2028a = "f";

    /* renamed from: b  reason: collision with root package name */
    private static volatile f f2029b;

    /* renamed from: c  reason: collision with root package name */
    private h f2030c;

    /* renamed from: d  reason: collision with root package name */
    private j f2031d;
    private a e = new d();

    private static class a extends d {

        /* renamed from: a  reason: collision with root package name */
        private Bitmap f2032a;

        private a() {
        }

        public Bitmap a() {
            return this.f2032a;
        }

        public void a(String str, View view, Bitmap bitmap) {
            this.f2032a = bitmap;
        }
    }

    protected f() {
    }

    private static Handler a(d dVar) {
        Handler f = dVar.f();
        if (dVar.n()) {
            return null;
        }
        return (f == null && Looper.myLooper() == Looper.getMainLooper()) ? new Handler() : f;
    }

    public static f a() {
        if (f2029b == null) {
            synchronized (f.class) {
                if (f2029b == null) {
                    f2029b = new f();
                }
            }
        }
        return f2029b;
    }

    private void d() {
        if (this.f2030c == null) {
            throw new IllegalStateException("ImageLoader must be init with configuration before using");
        }
    }

    public Bitmap a(String str, e eVar) {
        return a(str, eVar, (d) null);
    }

    public Bitmap a(String str, e eVar, d dVar) {
        if (dVar == null) {
            dVar = this.f2030c.r;
        }
        d.a aVar = new d.a();
        aVar.a(dVar);
        aVar.d(true);
        d a2 = aVar.a();
        a aVar2 = new a();
        a(str, eVar, a2, (a) aVar2);
        return aVar2.a();
    }

    public Bitmap a(String str, d dVar) {
        return a(str, (e) null, dVar);
    }

    public synchronized void a(h hVar) {
        if (hVar == null) {
            throw new IllegalArgumentException("ImageLoader configuration can not be initialized with null");
        } else if (this.f2030c == null) {
            b.c.a.c.d.a("Initialize ImageLoader with configuration", new Object[0]);
            this.f2031d = new j(hVar);
            this.f2030c = hVar;
        } else {
            b.c.a.c.d.d("Try to initialize ImageLoader which had already been initialized before. To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.", new Object[0]);
        }
    }

    public void a(String str, ImageView imageView, d dVar) {
        a(str, (b.c.a.b.e.a) new b(imageView), dVar, (a) null, (b.c.a.b.f.b) null);
    }

    public void a(String str, ImageView imageView, d dVar, a aVar) {
        a(str, imageView, dVar, aVar, (b.c.a.b.f.b) null);
    }

    public void a(String str, ImageView imageView, d dVar, a aVar, b.c.a.b.f.b bVar) {
        a(str, (b.c.a.b.e.a) new b(imageView), dVar, aVar, bVar);
    }

    public void a(String str, e eVar, d dVar, a aVar) {
        a(str, eVar, dVar, aVar, (b.c.a.b.f.b) null);
    }

    public void a(String str, e eVar, d dVar, a aVar, b.c.a.b.f.b bVar) {
        d();
        if (eVar == null) {
            eVar = this.f2030c.a();
        }
        if (dVar == null) {
            dVar = this.f2030c.r;
        }
        String str2 = str;
        a(str2, (b.c.a.b.e.a) new c(str, eVar, i.CROP), dVar, aVar, bVar);
    }

    public void a(String str, b.c.a.b.e.a aVar, d dVar, a aVar2) {
        a(str, aVar, dVar, aVar2, (b.c.a.b.f.b) null);
    }

    public void a(String str, b.c.a.b.e.a aVar, d dVar, a aVar2, b.c.a.b.f.b bVar) {
        d();
        if (aVar != null) {
            if (aVar2 == null) {
                aVar2 = this.e;
            }
            a aVar3 = aVar2;
            if (dVar == null) {
                dVar = this.f2030c.r;
            }
            if (TextUtils.isEmpty(str)) {
                this.f2031d.a(aVar);
                aVar3.a(str, aVar.b());
                if (dVar.r()) {
                    aVar.a(dVar.a(this.f2030c.f2034a));
                } else {
                    aVar.a((Drawable) null);
                }
                aVar3.a(str, aVar.b(), (Bitmap) null);
                return;
            }
            e a2 = b.c.a.c.b.a(aVar, this.f2030c.a());
            String a3 = b.c.a.c.f.a(str, a2);
            this.f2031d.a(aVar, a3);
            aVar3.a(str, aVar.b());
            Bitmap bitmap = this.f2030c.n.get(a3);
            if (bitmap == null || bitmap.isRecycled()) {
                if (dVar.t()) {
                    aVar.a(dVar.c(this.f2030c.f2034a));
                } else if (dVar.m()) {
                    aVar.a((Drawable) null);
                }
                o oVar = new o(this.f2031d, new k(str, aVar, a2, a3, dVar, aVar3, bVar, this.f2031d.a(str)), a(dVar));
                if (dVar.n()) {
                    oVar.run();
                } else {
                    this.f2031d.a(oVar);
                }
            } else {
                b.c.a.c.d.a("Load image from memory cache [%s]", a3);
                if (dVar.p()) {
                    p pVar = new p(this.f2031d, bitmap, new k(str, aVar, a2, a3, dVar, aVar3, bVar, this.f2031d.a(str)), a(dVar));
                    if (dVar.n()) {
                        pVar.run();
                    } else {
                        this.f2031d.a(pVar);
                    }
                } else {
                    dVar.d().a(bitmap, aVar, b.c.a.b.a.f.MEMORY_CACHE);
                    aVar3.a(str, aVar.b(), bitmap);
                }
            }
        } else {
            throw new IllegalArgumentException("Wrong arguments were passed to displayImage() method (ImageView reference must not be null)");
        }
    }

    public void b() {
        this.f2031d.e();
    }

    public void c() {
        this.f2031d.f();
    }
}
