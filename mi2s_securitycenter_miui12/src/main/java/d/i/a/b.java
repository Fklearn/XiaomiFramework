package d.i.a;

import d.i.a.c;

class b extends c.a {
    final /* synthetic */ c e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(c cVar, int i) {
        super(i);
        this.e = cVar;
    }

    /* access modifiers changed from: protected */
    public boolean a() {
        return this.e.b();
    }

    /* access modifiers changed from: protected */
    public int b() {
        return this.e.c();
    }
}
