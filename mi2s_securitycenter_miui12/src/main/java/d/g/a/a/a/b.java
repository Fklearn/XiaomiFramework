package d.g.a.a.a;

import android.os.SystemClock;
import d.g.a.a.a.a;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ a.d f8822a;

    b(a.d dVar) {
        this.f8822a = dVar;
    }

    public void run() {
        this.f8822a.f8819d = SystemClock.uptimeMillis();
        this.f8822a.f8816a.a();
    }
}
