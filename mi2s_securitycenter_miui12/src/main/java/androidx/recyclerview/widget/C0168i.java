package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: androidx.recyclerview.widget.i  reason: case insensitive filesystem */
class C0168i extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.u f1205a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f1206b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ViewPropertyAnimator f1207c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0172m f1208d;

    C0168i(C0172m mVar, RecyclerView.u uVar, View view, ViewPropertyAnimator viewPropertyAnimator) {
        this.f1208d = mVar;
        this.f1205a = uVar;
        this.f1206b = view;
        this.f1207c = viewPropertyAnimator;
    }

    public void onAnimationCancel(Animator animator) {
        this.f1206b.setAlpha(1.0f);
    }

    public void onAnimationEnd(Animator animator) {
        this.f1207c.setListener((Animator.AnimatorListener) null);
        this.f1208d.h(this.f1205a);
        this.f1208d.p.remove(this.f1205a);
        this.f1208d.j();
    }

    public void onAnimationStart(Animator animator) {
        this.f1208d.i(this.f1205a);
    }
}
