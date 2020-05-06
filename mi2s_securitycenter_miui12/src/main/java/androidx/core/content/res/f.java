package androidx.core.content.res;

import androidx.core.content.res.g;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f718a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g.a f719b;

    f(g.a aVar, int i) {
        this.f719b = aVar;
        this.f718a = i;
    }

    public void run() {
        this.f719b.a(this.f718a);
    }
}
