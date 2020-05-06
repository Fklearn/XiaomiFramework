package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: androidx.recyclerview.widget.h  reason: case insensitive filesystem */
class C0167h extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.u f1201a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ViewPropertyAnimator f1202b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ View f1203c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0172m f1204d;

    C0167h(C0172m mVar, RecyclerView.u uVar, ViewPropertyAnimator viewPropertyAnimator, View view) {
        this.f1204d = mVar;
        this.f1201a = uVar;
        this.f1202b = viewPropertyAnimator;
        this.f1203c = view;
    }

    public void onAnimationEnd(Animator animator) {
        this.f1202b.setListener((Animator.AnimatorListener) null);
        this.f1203c.setAlpha(1.0f);
        this.f1204d.l(this.f1201a);
        this.f1204d.r.remove(this.f1201a);
        this.f1204d.j();
    }

    public void onAnimationStart(Animator animator) {
        this.f1204d.m(this.f1201a);
    }
}
