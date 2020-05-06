package androidx.preference;

import android.os.Handler;
import android.os.Message;

class o extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f1043a;

    o(r rVar) {
        this.f1043a = rVar;
    }

    public void handleMessage(Message message) {
        if (message.what == 1) {
            this.f1043a.bindPreferences();
        }
    }
}
