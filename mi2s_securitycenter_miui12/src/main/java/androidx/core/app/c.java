package androidx.core.app;

import android.app.Application;
import androidx.core.app.e;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Application f685a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e.a f686b;

    c(Application application, e.a aVar) {
        this.f685a = application;
        this.f686b = aVar;
    }

    public void run() {
        this.f685a.unregisterActivityLifecycleCallbacks(this.f686b);
    }
}
