package androidx.appcompat.view.menu;

import android.view.View;
import android.view.ViewTreeObserver;

class e implements View.OnAttachStateChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CascadingMenuPopup f372a;

    e(CascadingMenuPopup cascadingMenuPopup) {
        this.f372a = cascadingMenuPopup;
    }

    public void onViewAttachedToWindow(View view) {
    }

    public void onViewDetachedFromWindow(View view) {
        ViewTreeObserver viewTreeObserver = this.f372a.z;
        if (viewTreeObserver != null) {
            if (!viewTreeObserver.isAlive()) {
                this.f372a.z = view.getViewTreeObserver();
            }
            CascadingMenuPopup cascadingMenuPopup = this.f372a;
            cascadingMenuPopup.z.removeGlobalOnLayoutListener(cascadingMenuPopup.k);
        }
        view.removeOnAttachStateChangeListener(this);
    }
}
