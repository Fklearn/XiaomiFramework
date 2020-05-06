package androidx.appcompat.widget;

import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import androidx.appcompat.widget.AppCompatSpinner;

class D implements PopupWindow.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewTreeObserver.OnGlobalLayoutListener f474a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppCompatSpinner.c f475b;

    D(AppCompatSpinner.c cVar, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        this.f475b = cVar;
        this.f474a = onGlobalLayoutListener;
    }

    public void onDismiss() {
        ViewTreeObserver viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.removeGlobalOnLayoutListener(this.f474a);
        }
    }
}
