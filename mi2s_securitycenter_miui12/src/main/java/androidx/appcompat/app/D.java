package androidx.appcompat.app;

import android.view.View;
import androidx.appcompat.widget.ActionBarOverlayLayout;
import androidx.core.view.F;
import androidx.core.view.ViewCompat;

class D extends F {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ G f281a;

    D(G g) {
        this.f281a = g;
    }

    public void onAnimationEnd(View view) {
        View view2;
        G g = this.f281a;
        if (g.v && (view2 = g.j) != null) {
            view2.setTranslationY(0.0f);
            this.f281a.g.setTranslationY(0.0f);
        }
        this.f281a.g.setVisibility(8);
        this.f281a.g.setTransitioning(false);
        G g2 = this.f281a;
        g2.A = null;
        g2.l();
        ActionBarOverlayLayout actionBarOverlayLayout = this.f281a.f;
        if (actionBarOverlayLayout != null) {
            ViewCompat.v(actionBarOverlayLayout);
        }
    }
}
