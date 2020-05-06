package b.b.b;

import android.os.Bundle;
import b.b.b.t;

class s implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t.a f1586a;

    s(t.a aVar) {
        this.f1586a = aVar;
    }

    public void run() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("virus_info_key", this.f1586a.e);
        t.b(this.f1586a.f1590d, bundle);
    }
}
