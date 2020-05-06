package d.a.d;

import d.a.d;
import d.a.g.C0575b;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f8694a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0575b[] f8695b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ g f8696c;

    e(g gVar, d dVar, C0575b[] bVarArr) {
        this.f8696c = gVar;
        this.f8694a = dVar;
        this.f8695b = bVarArr;
    }

    public void run() {
        this.f8694a.getAnimTask().b(this.f8695b);
    }
}
