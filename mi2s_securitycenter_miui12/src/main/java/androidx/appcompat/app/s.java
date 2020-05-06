package androidx.appcompat.app;

import android.view.View;
import androidx.core.view.E;
import androidx.core.view.F;

class s extends F {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t f322a;

    s(t tVar) {
        this.f322a = tVar;
    }

    public void onAnimationEnd(View view) {
        this.f322a.f323a.u.setAlpha(1.0f);
        this.f322a.f323a.x.a((E) null);
        this.f322a.f323a.x = null;
    }

    public void onAnimationStart(View view) {
        this.f322a.f323a.u.setVisibility(0);
    }
}
