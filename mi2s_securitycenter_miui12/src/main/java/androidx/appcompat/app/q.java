package androidx.appcompat.app;

import android.graphics.Rect;
import androidx.appcompat.widget.P;

class q implements P.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatDelegateImpl f320a;

    q(AppCompatDelegateImpl appCompatDelegateImpl) {
        this.f320a = appCompatDelegateImpl;
    }

    public void a(Rect rect) {
        rect.top = this.f320a.j(rect.top);
    }
}
