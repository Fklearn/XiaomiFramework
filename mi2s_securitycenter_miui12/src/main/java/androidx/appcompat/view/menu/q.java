package androidx.appcompat.view.menu;

import android.widget.PopupWindow;

class q implements PopupWindow.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f409a;

    q(r rVar) {
        this.f409a = rVar;
    }

    public void onDismiss() {
        this.f409a.d();
    }
}
