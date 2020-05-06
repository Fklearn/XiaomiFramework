package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.C0172m;

/* renamed from: androidx.recyclerview.widget.l  reason: case insensitive filesystem */
class C0171l extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0172m.a f1217a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ViewPropertyAnimator f1218b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ View f1219c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0172m f1220d;

    C0171l(C0172m mVar, C0172m.a aVar, ViewPropertyAnimator viewPropertyAnimator, View view) {
        this.f1220d = mVar;
        this.f1217a = aVar;
        this.f1218b = viewPropertyAnimator;
        this.f1219c = view;
    }

    public void onAnimationEnd(Animator animator) {
        this.f1218b.setListener((Animator.AnimatorListener) null);
        this.f1219c.setAlpha(1.0f);
        this.f1219c.setTranslationX(0.0f);
        this.f1219c.setTranslationY(0.0f);
        this.f1220d.a(this.f1217a.f1222b, false);
        this.f1220d.s.remove(this.f1217a.f1222b);
        this.f1220d.j();
    }

    public void onAnimationStart(Animator animator) {
        this.f1220d.b(this.f1217a.f1222b, false);
    }
}
