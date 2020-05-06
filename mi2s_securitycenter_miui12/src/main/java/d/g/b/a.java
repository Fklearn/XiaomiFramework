package d.g.b;

import d.g.b.b;

class a implements b.a.C0078b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f8842a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f8843b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f8844c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ b f8845d;

    a(b bVar, int i, int i2, int i3) {
        this.f8845d = bVar;
        this.f8842a = i;
        this.f8843b = i2;
        this.f8844c = i3;
    }

    public boolean a(float f, float f2) {
        c.a("fling finished: value(%f), velocity(%f), scroller boundary(%d, %d)", Float.valueOf(f), Float.valueOf(f2), Integer.valueOf(this.f8842a), Integer.valueOf(this.f8843b));
        this.f8845d.s.e((float) this.f8845d.t.f);
        this.f8845d.s.f(this.f8845d.t.e);
        float e = this.f8845d.s.e();
        if (((int) f) == 0 || (e <= ((float) this.f8843b) && e >= ((float) this.f8842a))) {
            c.a("fling finished, no more work.");
            return false;
        }
        c.a("fling destination beyound boundary, start spring");
        this.f8845d.i();
        b bVar = this.f8845d;
        bVar.a(2, bVar.d(), this.f8845d.c(), this.f8845d.e(), this.f8844c);
        return true;
    }
}
