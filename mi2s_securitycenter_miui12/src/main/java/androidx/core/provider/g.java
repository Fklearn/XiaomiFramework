package androidx.core.provider;

import android.os.Handler;
import android.os.Message;

class g implements Handler.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ k f759a;

    g(k kVar) {
        this.f759a = kVar;
    }

    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            this.f759a.a();
            return true;
        } else if (i != 1) {
            return true;
        } else {
            this.f759a.a((Runnable) message.obj);
            return true;
        }
    }
}
