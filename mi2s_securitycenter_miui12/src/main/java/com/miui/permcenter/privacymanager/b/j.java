package com.miui.permcenter.privacymanager.b;

import android.animation.Animator;

class j implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f6365a;

    j(m mVar) {
        this.f6365a = mVar;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        boolean unused = this.f6365a.v = true;
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
