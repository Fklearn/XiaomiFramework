package d.a.f;

import android.os.SystemClock;
import d.a.f.b;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b.d f8746a;

    c(b.d dVar) {
        this.f8746a = dVar;
    }

    public void run() {
        long unused = this.f8746a.f8743d = SystemClock.uptimeMillis();
        this.f8746a.f8740a.a();
    }
}
