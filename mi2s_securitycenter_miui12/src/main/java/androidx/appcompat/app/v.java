package androidx.appcompat.app;

import android.view.View;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatDelegateImpl;
import androidx.core.view.E;
import androidx.core.view.F;
import androidx.core.view.ViewCompat;

class v extends F {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatDelegateImpl.b f325a;

    v(AppCompatDelegateImpl.b bVar) {
        this.f325a = bVar;
    }

    public void onAnimationEnd(View view) {
        AppCompatDelegateImpl.this.u.setVisibility(8);
        AppCompatDelegateImpl appCompatDelegateImpl = AppCompatDelegateImpl.this;
        PopupWindow popupWindow = appCompatDelegateImpl.v;
        if (popupWindow != null) {
            popupWindow.dismiss();
        } else if (appCompatDelegateImpl.u.getParent() instanceof View) {
            ViewCompat.v((View) AppCompatDelegateImpl.this.u.getParent());
        }
        AppCompatDelegateImpl.this.u.removeAllViews();
        AppCompatDelegateImpl.this.x.a((E) null);
        AppCompatDelegateImpl.this.x = null;
    }
}
