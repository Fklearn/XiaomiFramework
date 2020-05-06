package androidx.appcompat.app;

import android.view.View;
import androidx.core.view.G;

class F implements G {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ G f283a;

    F(G g) {
        this.f283a = g;
    }

    public void onAnimationUpdate(View view) {
        ((View) this.f283a.g.getParent()).invalidate();
    }
}
