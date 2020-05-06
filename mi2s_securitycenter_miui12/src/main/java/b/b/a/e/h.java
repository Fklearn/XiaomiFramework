package b.b.a.e;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import b.b.a.e.i;

class h extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f1434a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    h(i iVar, Looper looper) {
        super(looper);
        this.f1434a = iVar;
    }

    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            i.f fVar = (i.f) message.obj;
            fVar.f1446c.a(fVar.f1444a, fVar.f1445b);
            this.f1434a.e.put(fVar.f1444a, fVar.f1445b);
        } else if (i == 2) {
            this.f1434a.d();
            for (int i2 = 0; i2 < this.f1434a.i.size(); i2++) {
                ((i.a) this.f1434a.i.get(i2)).a();
            }
        }
    }
}
