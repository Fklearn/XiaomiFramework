package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: androidx.recyclerview.widget.j  reason: case insensitive filesystem */
class C0169j extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView.u f1209a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f1210b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ View f1211c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ int f1212d;
    final /* synthetic */ ViewPropertyAnimator e;
    final /* synthetic */ C0172m f;

    C0169j(C0172m mVar, RecyclerView.u uVar, int i, View view, int i2, ViewPropertyAnimator viewPropertyAnimator) {
        this.f = mVar;
        this.f1209a = uVar;
        this.f1210b = i;
        this.f1211c = view;
        this.f1212d = i2;
        this.e = viewPropertyAnimator;
    }

    public void onAnimationCancel(Animator animator) {
        if (this.f1210b != 0) {
            this.f1211c.setTranslationX(0.0f);
        }
        if (this.f1212d != 0) {
            this.f1211c.setTranslationY(0.0f);
        }
    }

    public void onAnimationEnd(Animator animator) {
        this.e.setListener((Animator.AnimatorListener) null);
        this.f.j(this.f1209a);
        this.f.q.remove(this.f1209a);
        this.f.j();
    }

    public void onAnimationStart(Animator animator) {
        this.f.k(this.f1209a);
    }
}
