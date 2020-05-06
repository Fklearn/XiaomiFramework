package androidx.appcompat.app;

import android.view.View;
import androidx.core.view.E;
import androidx.core.view.F;
import androidx.core.view.ViewCompat;

class u extends F {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatDelegateImpl f324a;

    u(AppCompatDelegateImpl appCompatDelegateImpl) {
        this.f324a = appCompatDelegateImpl;
    }

    public void onAnimationEnd(View view) {
        this.f324a.u.setAlpha(1.0f);
        this.f324a.x.a((E) null);
        this.f324a.x = null;
    }

    public void onAnimationStart(View view) {
        this.f324a.u.setVisibility(0);
        this.f324a.u.sendAccessibilityEvent(32);
        if (this.f324a.u.getParent() instanceof View) {
            ViewCompat.v((View) this.f324a.u.getParent());
        }
    }
}
