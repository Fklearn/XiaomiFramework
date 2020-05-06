package androidx.appcompat.app;

import android.view.View;
import androidx.core.view.F;

class E extends F {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ G f282a;

    E(G g) {
        this.f282a = g;
    }

    public void onAnimationEnd(View view) {
        G g = this.f282a;
        g.A = null;
        g.g.requestLayout();
    }
}
