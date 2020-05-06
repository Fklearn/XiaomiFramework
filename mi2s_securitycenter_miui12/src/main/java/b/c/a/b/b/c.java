package b.c.a.b.b;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.Build;
import b.c.a.b.a.d;
import b.c.a.b.a.e;
import b.c.a.b.a.i;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private final String f1996a;

    /* renamed from: b  reason: collision with root package name */
    private final String f1997b;

    /* renamed from: c  reason: collision with root package name */
    private final String f1998c;

    /* renamed from: d  reason: collision with root package name */
    private final e f1999d;
    private final d e;
    private final i f;
    private final b.c.a.b.d.d g;
    private final Object h;
    private final boolean i;
    private final BitmapFactory.Options j = new BitmapFactory.Options();

    public c(String str, String str2, String str3, e eVar, i iVar, b.c.a.b.d.d dVar, b.c.a.b.d dVar2) {
        this.f1996a = str;
        this.f1997b = str2;
        this.f1998c = str3;
        this.f1999d = eVar;
        this.e = dVar2.g();
        this.f = iVar;
        this.g = dVar;
        this.h = dVar2.e();
        this.i = dVar2.l();
        a(dVar2.b(), this.j);
    }

    private void a(BitmapFactory.Options options, BitmapFactory.Options options2) {
        options2.inDensity = options.inDensity;
        options2.inDither = options.inDither;
        options2.inInputShareable = options.inInputShareable;
        options2.inJustDecodeBounds = options.inJustDecodeBounds;
        options2.inPreferredConfig = options.inPreferredConfig;
        options2.inPurgeable = options.inPurgeable;
        options2.inSampleSize = options.inSampleSize;
        options2.inScaled = options.inScaled;
        options2.inScreenDensity = options.inScreenDensity;
        options2.inTargetDensity = options.inTargetDensity;
        options2.inTempStorage = options.inTempStorage;
        if (Build.VERSION.SDK_INT >= 10) {
            b(options, options2);
        }
        if (Build.VERSION.SDK_INT >= 11) {
            c(options, options2);
        }
    }

    @TargetApi(10)
    private void b(BitmapFactory.Options options, BitmapFactory.Options options2) {
        options2.inPreferQualityOverSpeed = options.inPreferQualityOverSpeed;
    }

    @TargetApi(11)
    private void c(BitmapFactory.Options options, BitmapFactory.Options options2) {
        options2.inBitmap = options.inBitmap;
        options2.inMutable = options.inMutable;
    }

    public BitmapFactory.Options a() {
        return this.j;
    }

    public b.c.a.b.d.d b() {
        return this.g;
    }

    public Object c() {
        return this.h;
    }

    public String d() {
        return this.f1996a;
    }

    public d e() {
        return this.e;
    }

    public String f() {
        return this.f1997b;
    }

    public e g() {
        return this.f1999d;
    }

    public i h() {
        return this.f;
    }

    public boolean i() {
        return this.i;
    }
}
