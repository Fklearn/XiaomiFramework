package androidx.core.app;

import androidx.core.app.e;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e.a f683a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Object f684b;

    b(e.a aVar, Object obj) {
        this.f683a = aVar;
        this.f684b = obj;
    }

    public void run() {
        this.f683a.f693a = this.f684b;
    }
}
