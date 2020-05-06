package b.b.n;

import android.content.Context;
import b.b.p.f;
import b.b.p.g;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f1867a;

    i(Context context) {
        this.f1867a = context;
    }

    public void run() {
        g.a(this.f1867a);
        h hVar = new h(this.f1867a, "applicationlock");
        h hVar2 = new h(this.f1867a, "securitycenterScan");
        f a2 = f.a(this.f1867a);
        a2.a(l.f1873b, (f.c) hVar);
        a2.a(l.f1872a, (f.c) hVar2);
        a2.b(l.f1873b);
        a2.b(l.f1872a);
    }
}
