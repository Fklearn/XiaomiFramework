package androidx.appcompat.widget;

import android.view.View;

class S implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ U f532a;

    S(U u) {
        this.f532a = u;
    }

    public void run() {
        View d2 = this.f532a.d();
        if (d2 != null && d2.getWindowToken() != null) {
            this.f532a.b();
        }
    }
}
