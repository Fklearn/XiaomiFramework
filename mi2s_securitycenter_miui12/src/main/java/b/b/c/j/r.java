package b.b.c.j;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import b.c.a.a.a.b.c;
import b.c.a.b.a.e;
import b.c.a.b.a.g;
import b.c.a.b.d;
import b.c.a.b.f;
import b.c.a.b.g.a;
import b.c.a.b.h;
import com.miui.securitycenter.Application;

public class r {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f1757a;

    /* renamed from: b  reason: collision with root package name */
    public static final d f1758b;

    /* renamed from: c  reason: collision with root package name */
    public static final d f1759c;

    /* renamed from: d  reason: collision with root package name */
    public static final d f1760d;
    public static final d e;
    public static final d f;
    public static final d g;
    public static final d h;

    static {
        d.a aVar = new d.a();
        aVar.a(true);
        aVar.b(false);
        f1758b = aVar.a();
        d.a aVar2 = new d.a();
        aVar2.a(true);
        aVar2.b(false);
        aVar2.a(b.c.a.b.a.d.EXACTLY);
        f1759c = aVar2.a();
        d.a aVar3 = new d.a();
        aVar3.a(true);
        aVar3.b(true);
        aVar3.c(true);
        f1760d = aVar3.a();
        d.a aVar4 = new d.a();
        aVar4.a(true);
        aVar4.b(false);
        aVar4.c(true);
        e = aVar4.a();
        d.a aVar5 = new d.a();
        aVar5.a(true);
        aVar5.b(false);
        aVar5.c(true);
        f = aVar5.a();
        d.a aVar6 = new d.a();
        aVar6.a(true);
        aVar6.b(true);
        aVar6.a((a) new p());
        aVar6.c(true);
        g = aVar6.a();
        d.a aVar7 = new d.a();
        aVar7.a(true);
        aVar7.b(false);
        aVar7.a((a) new q());
        aVar7.c(true);
        h = aVar7.a();
    }

    public static Bitmap a(Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return createBitmap;
    }

    public static Bitmap a(String str, e eVar) {
        return b().a(str, eVar);
    }

    public static Bitmap a(String str, d dVar) {
        return b().a(str, dVar);
    }

    public static synchronized void a() {
        synchronized (r.class) {
            if (!f1757a) {
                if (!Application.d().e()) {
                    h.a aVar = new h.a(Application.d());
                    aVar.b(3);
                    aVar.a((b.c.a.a.a.b.a) new c());
                    aVar.a(52428800);
                    aVar.a(g.LIFO);
                    f.a().a(aVar.a());
                    f1757a = true;
                    return;
                }
                throw new RuntimeException("ImageLoader don't run in remote process");
            }
        }
    }

    public static void a(String str, ImageView imageView) {
        b().a(str, imageView, f1760d);
    }

    public static void a(String str, ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
        b().a(str, imageView, f1760d);
    }

    public static void a(String str, ImageView imageView, d dVar) {
        b().a(str, imageView, dVar);
    }

    public static void a(String str, ImageView imageView, d dVar, int i) {
        imageView.setImageResource(i);
        b().a(str, imageView, dVar);
    }

    public static void a(String str, ImageView imageView, d dVar, Drawable drawable) {
        imageView.setImageDrawable(drawable);
        b().a(str, imageView, dVar);
    }

    public static void a(String str, ImageView imageView, d dVar, b.c.a.b.f.a aVar) {
        b().a(str, imageView, dVar, aVar);
    }

    public static void a(String str, b.c.a.b.e.a aVar, d dVar, b.c.a.b.f.a aVar2) {
        b().a(str, aVar, dVar, aVar2);
    }

    public static f b() {
        a();
        return f.a();
    }
}
