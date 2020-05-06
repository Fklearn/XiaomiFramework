package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;

class q extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewGroup f928a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f929b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Fragment f930c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ t f931d;

    q(t tVar, ViewGroup viewGroup, View view, Fragment fragment) {
        this.f931d = tVar;
        this.f928a = viewGroup;
        this.f929b = view;
        this.f930c = fragment;
    }

    public void onAnimationEnd(Animator animator) {
        this.f928a.endViewTransition(this.f929b);
        Animator j = this.f930c.j();
        this.f930c.a((Animator) null);
        if (j != null && this.f928a.indexOfChild(this.f929b) < 0) {
            t tVar = this.f931d;
            Fragment fragment = this.f930c;
            tVar.a(fragment, fragment.C(), 0, 0, false);
        }
    }
}
