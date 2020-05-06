package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;

class r extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewGroup f932a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f933b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Fragment f934c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ t f935d;

    r(t tVar, ViewGroup viewGroup, View view, Fragment fragment) {
        this.f935d = tVar;
        this.f932a = viewGroup;
        this.f933b = view;
        this.f934c = fragment;
    }

    public void onAnimationEnd(Animator animator) {
        this.f932a.endViewTransition(this.f933b);
        animator.removeListener(this);
        Fragment fragment = this.f934c;
        View view = fragment.H;
        if (view != null && fragment.z) {
            view.setVisibility(8);
        }
    }
}
