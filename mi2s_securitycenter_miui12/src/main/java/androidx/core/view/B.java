package androidx.core.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

class B extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ E f774a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f775b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ D f776c;

    B(D d2, E e, View view) {
        this.f776c = d2;
        this.f774a = e;
        this.f775b = view;
    }

    public void onAnimationCancel(Animator animator) {
        this.f774a.onAnimationCancel(this.f775b);
    }

    public void onAnimationEnd(Animator animator) {
        this.f774a.onAnimationEnd(this.f775b);
    }

    public void onAnimationStart(Animator animator) {
        this.f774a.onAnimationStart(this.f775b);
    }
}
