package androidx.core.view;

import android.animation.ValueAnimator;
import android.view.View;

class C implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ G f777a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f778b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ D f779c;

    C(D d2, G g, View view) {
        this.f779c = d2;
        this.f777a = g;
        this.f778b = view;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f777a.onAnimationUpdate(this.f778b);
    }
}
