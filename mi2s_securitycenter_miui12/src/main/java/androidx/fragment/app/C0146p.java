package androidx.fragment.app;

import android.view.ViewGroup;
import android.view.animation.Animation;

/* renamed from: androidx.fragment.app.p  reason: case insensitive filesystem */
class C0146p implements Animation.AnimationListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewGroup f925a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Fragment f926b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ t f927c;

    C0146p(t tVar, ViewGroup viewGroup, Fragment fragment) {
        this.f927c = tVar;
        this.f925a = viewGroup;
        this.f926b = fragment;
    }

    public void onAnimationEnd(Animation animation) {
        this.f925a.post(new C0145o(this));
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }
}
