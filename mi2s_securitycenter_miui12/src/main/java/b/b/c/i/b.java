package b.b.c.i;

import android.os.Handler;
import android.os.Message;

public class b extends Handler {
    public void a(int i, Object obj) {
        sendMessage(obtainMessage(i, obj));
    }

    public void a(int i, Object obj, int i2) {
        sendMessageDelayed(obtainMessage(i, obj), (long) i2);
    }

    public void handleMessage(Message message) {
    }
}
