package b.c.a.b.e;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import b.c.a.b.a.e;
import b.c.a.b.a.i;

public class c implements a {

    /* renamed from: a  reason: collision with root package name */
    protected final String f2023a;

    /* renamed from: b  reason: collision with root package name */
    protected final e f2024b;

    /* renamed from: c  reason: collision with root package name */
    protected final i f2025c;

    public c(e eVar, i iVar) {
        this((String) null, eVar, iVar);
    }

    public c(String str, e eVar, i iVar) {
        if (eVar == null) {
            throw new IllegalArgumentException("imageSize must not be null");
        } else if (iVar != null) {
            this.f2023a = str;
            this.f2024b = eVar;
            this.f2025c = iVar;
        } else {
            throw new IllegalArgumentException("scaleType must not be null");
        }
    }

    public int a() {
        return this.f2024b.a();
    }

    public boolean a(Bitmap bitmap) {
        return true;
    }

    public boolean a(Drawable drawable) {
        return true;
    }

    public View b() {
        return null;
    }

    public int c() {
        return this.f2024b.b();
    }

    public boolean d() {
        return false;
    }

    public i e() {
        return this.f2025c;
    }

    public int getId() {
        return TextUtils.isEmpty(this.f2023a) ? super.hashCode() : this.f2023a.hashCode();
    }
}
