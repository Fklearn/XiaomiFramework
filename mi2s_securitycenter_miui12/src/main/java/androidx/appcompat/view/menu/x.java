package androidx.appcompat.view.menu;

import android.view.View;
import android.view.ViewTreeObserver;

class x implements View.OnAttachStateChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ y f416a;

    x(y yVar) {
        this.f416a = yVar;
    }

    public void onViewAttachedToWindow(View view) {
    }

    public void onViewDetachedFromWindow(View view) {
        ViewTreeObserver viewTreeObserver = this.f416a.q;
        if (viewTreeObserver != null) {
            if (!viewTreeObserver.isAlive()) {
                this.f416a.q = view.getViewTreeObserver();
            }
            y yVar = this.f416a;
            yVar.q.removeGlobalOnLayoutListener(yVar.k);
        }
        view.removeOnAttachStateChangeListener(this);
    }
}
