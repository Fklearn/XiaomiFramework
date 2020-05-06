package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.C0172m;

/* renamed from: androidx.recyclerview.widget.k  reason: case insensitive filesystem */
class C0170k extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0172m.a f1213a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ViewPropertyAnimator f1214b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ View f1215c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0172m f1216d;

    C0170k(C0172m mVar, C0172m.a aVar, ViewPropertyAnimator viewPropertyAnimator, View view) {
        this.f1216d = mVar;
        this.f1213a = aVar;
        this.f1214b = viewPropertyAnimator;
        this.f1215c = view;
    }

    public void onAnimationEnd(Animator animator) {
        this.f1214b.setListener((Animator.AnimatorListener) null);
        this.f1215c.setAlpha(1.0f);
        this.f1215c.setTranslationX(0.0f);
        this.f1215c.setTranslationY(0.0f);
        this.f1216d.a(this.f1213a.f1221a, true);
        this.f1216d.s.remove(this.f1213a.f1221a);
        this.f1216d.j();
    }

    public void onAnimationStart(Animator animator) {
        this.f1216d.b(this.f1213a.f1221a, true);
    }
}
