package androidx.appcompat.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

/* renamed from: androidx.appcompat.widget.d  reason: case insensitive filesystem */
class C0093d extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionBarOverlayLayout f599a;

    C0093d(ActionBarOverlayLayout actionBarOverlayLayout) {
        this.f599a = actionBarOverlayLayout;
    }

    public void onAnimationCancel(Animator animator) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.f599a;
        actionBarOverlayLayout.x = null;
        actionBarOverlayLayout.l = false;
    }

    public void onAnimationEnd(Animator animator) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.f599a;
        actionBarOverlayLayout.x = null;
        actionBarOverlayLayout.l = false;
    }
}
