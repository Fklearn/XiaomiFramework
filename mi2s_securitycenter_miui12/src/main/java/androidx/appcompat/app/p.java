package androidx.appcompat.app;

import android.view.View;
import androidx.core.view.H;
import androidx.core.view.ViewCompat;
import androidx.core.view.q;

class p implements q {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatDelegateImpl f319a;

    p(AppCompatDelegateImpl appCompatDelegateImpl) {
        this.f319a = appCompatDelegateImpl;
    }

    public H a(View view, H h) {
        int d2 = h.d();
        int j = this.f319a.j(d2);
        if (d2 != j) {
            h = h.a(h.b(), j, h.c(), h.a());
        }
        return ViewCompat.b(view, h);
    }
}
