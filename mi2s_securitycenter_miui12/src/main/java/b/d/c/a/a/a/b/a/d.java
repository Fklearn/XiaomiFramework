package b.d.c.a.a.a.b.a;

import b.d.c.a.a.a.a.c;

public class d extends a {

    /* renamed from: d  reason: collision with root package name */
    protected c f2149d = new c(this);

    public Object a(Float f, Float f2) {
        c cVar = this.f2149d;
        cVar.a(f, f2);
        return cVar;
    }

    public boolean a(Object obj) {
        super.a(obj);
        this.f2149d.a(obj);
        return true;
    }

    public String toString() {
        String aVar = super.toString();
        return aVar + this.f2149d.toString();
    }
}
