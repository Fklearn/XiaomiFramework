package b.d.c.a.a.a.b.a;

import b.d.c.a.a.a.a.b;

public class a extends b {

    /* renamed from: c  reason: collision with root package name */
    protected Float f2144c = null;

    public boolean a(Object obj) {
        super.a(obj);
        if (this.f2144c != null) {
            return true;
        }
        throw new IllegalStateException("Sensitivity must be set");
    }

    public b b(Float f) {
        if (f.floatValue() > 0.0f) {
            this.f2144c = f;
            return this;
        }
        throw new IllegalArgumentException("Sensitivity must be strictly positive");
    }

    public String toString() {
        String str;
        String bVar = super.toString();
        StringBuilder sb = new StringBuilder();
        sb.append(bVar);
        if (this.f2144c != null) {
            str = ".set_sensitivity(" + this.f2144c + ")";
        } else {
            str = "";
        }
        sb.append(str);
        return sb.toString();
    }
}
