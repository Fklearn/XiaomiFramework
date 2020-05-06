package d.a.d;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class b extends Handler {
    b(Looper looper) {
        super(looper);
    }

    public void handleMessage(Message message) {
        if (message.what == 0) {
            g.f();
        } else {
            super.handleMessage(message);
        }
    }
}
