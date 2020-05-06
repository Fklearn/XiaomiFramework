package b.b.b.c;

import android.database.ContentObserver;
import android.os.Handler;
import b.b.c.j.d;

class b extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f1488a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(e eVar, Handler handler) {
        super(handler);
        this.f1488a = eVar;
    }

    public void onChange(boolean z) {
        d.a(new a(this));
        this.f1488a.j.set(System.currentTimeMillis());
    }
}
