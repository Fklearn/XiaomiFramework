package androidx.appcompat.app;

import androidx.core.view.D;
import androidx.core.view.E;
import androidx.core.view.ViewCompat;

class t implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatDelegateImpl f323a;

    t(AppCompatDelegateImpl appCompatDelegateImpl) {
        this.f323a = appCompatDelegateImpl;
    }

    public void run() {
        AppCompatDelegateImpl appCompatDelegateImpl = this.f323a;
        appCompatDelegateImpl.v.showAtLocation(appCompatDelegateImpl.u, 55, 0, 0);
        this.f323a.m();
        if (this.f323a.u()) {
            this.f323a.u.setAlpha(0.0f);
            AppCompatDelegateImpl appCompatDelegateImpl2 = this.f323a;
            D a2 = ViewCompat.a(appCompatDelegateImpl2.u);
            a2.a(1.0f);
            appCompatDelegateImpl2.x = a2;
            this.f323a.x.a((E) new s(this));
            return;
        }
        this.f323a.u.setAlpha(1.0f);
        this.f323a.u.setVisibility(0);
    }
}
