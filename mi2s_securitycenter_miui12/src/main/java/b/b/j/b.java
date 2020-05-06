package b.b.j;

import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;

public class b extends Handler {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<h> f1787a;

    public b(h hVar) {
        this.f1787a = new WeakReference<>(hVar);
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        h hVar = (h) this.f1787a.get();
        if (hVar != null && message.what == 108) {
            hVar.a();
        }
    }
}
