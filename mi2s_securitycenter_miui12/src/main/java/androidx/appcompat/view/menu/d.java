package androidx.appcompat.view.menu;

import android.view.View;
import android.view.ViewTreeObserver;
import androidx.appcompat.view.menu.CascadingMenuPopup;

class d implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CascadingMenuPopup f371a;

    d(CascadingMenuPopup cascadingMenuPopup) {
        this.f371a = cascadingMenuPopup;
    }

    public void onGlobalLayout() {
        if (this.f371a.isShowing() && this.f371a.j.size() > 0 && !this.f371a.j.get(0).f350a.g()) {
            View view = this.f371a.q;
            if (view == null || !view.isShown()) {
                this.f371a.dismiss();
                return;
            }
            for (CascadingMenuPopup.a aVar : this.f371a.j) {
                aVar.f350a.b();
            }
        }
    }
}
