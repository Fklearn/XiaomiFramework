package b.b.j;

import android.os.Build;
import android.view.ViewTreeObserver;

class f implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f1818a;

    f(h hVar) {
        this.f1818a = hVar;
    }

    public void onGlobalLayout() {
        h hVar = this.f1818a;
        int unused = hVar.p = hVar.f1820a.getHeight();
        if (Build.VERSION.SDK_INT > 16) {
            this.f1818a.f1820a.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            this.f1818a.f1820a.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }
}
