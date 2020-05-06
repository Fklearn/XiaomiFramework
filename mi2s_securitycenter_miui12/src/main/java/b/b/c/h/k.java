package b.b.c.h;

import android.net.ConnectivityManager;
import android.util.Log;

class k extends ConnectivityManager.OnStartTetheringCallback {
    k() {
    }

    public void onTetheringFailed() {
        super.onTetheringFailed();
        Log.d(l.f1739a, "onTetheringFailed");
    }

    public void onTetheringStarted() {
        super.onTetheringStarted();
        Log.d(l.f1739a, "onTetheringStarted");
    }
}
