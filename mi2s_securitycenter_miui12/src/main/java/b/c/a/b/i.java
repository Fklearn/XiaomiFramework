package b.c.a.b;

import java.io.File;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f2044a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f2045b;

    i(j jVar, o oVar) {
        this.f2045b = jVar;
        this.f2044a = oVar;
    }

    public void run() {
        File file = this.f2045b.f2046a.o.get(this.f2044a.a());
        boolean z = file != null && file.exists();
        this.f2045b.h();
        (z ? this.f2045b.f2048c : this.f2045b.f2047b).execute(this.f2044a);
    }
}
