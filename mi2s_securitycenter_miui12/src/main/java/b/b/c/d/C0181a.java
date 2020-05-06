package b.b.c.d;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/* renamed from: b.b.c.d.a  reason: case insensitive filesystem */
class C0181a extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0184d f1669a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0181a(C0184d dVar, Looper looper) {
        super(looper);
        this.f1669a = dVar;
    }

    public void handleMessage(Message message) {
        this.f1669a.a("VIEW", (C0184d) message.obj);
    }
}
