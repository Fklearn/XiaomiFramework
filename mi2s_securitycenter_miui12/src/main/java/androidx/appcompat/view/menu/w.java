package androidx.appcompat.view.menu;

import android.view.View;
import android.view.ViewTreeObserver;

class w implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ y f415a;

    w(y yVar) {
        this.f415a = yVar;
    }

    public void onGlobalLayout() {
        if (this.f415a.isShowing() && !this.f415a.j.g()) {
            View view = this.f415a.o;
            if (view == null || !view.isShown()) {
                this.f415a.dismiss();
            } else {
                this.f415a.j.b();
            }
        }
    }
}
