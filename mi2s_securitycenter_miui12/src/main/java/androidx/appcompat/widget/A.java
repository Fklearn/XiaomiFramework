package androidx.appcompat.widget;

import android.os.Build;
import android.view.ViewTreeObserver;

class A implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatSpinner f420a;

    A(AppCompatSpinner appCompatSpinner) {
        this.f420a = appCompatSpinner;
    }

    public void onGlobalLayout() {
        if (!this.f420a.getInternalPopup().isShowing()) {
            this.f420a.a();
        }
        ViewTreeObserver viewTreeObserver = this.f420a.getViewTreeObserver();
        if (viewTreeObserver == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            viewTreeObserver.removeOnGlobalLayoutListener(this);
        } else {
            viewTreeObserver.removeGlobalOnLayoutListener(this);
        }
    }
}
