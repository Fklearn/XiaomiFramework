package b.d.b.c;

import android.os.Looper;

public class h {
    public static void a() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalArgumentException("You must call this method on the worker thread");
        }
    }
}
