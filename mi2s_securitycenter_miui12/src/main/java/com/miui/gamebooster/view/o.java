package com.miui.gamebooster.view;

import android.animation.Animator;
import com.miui.gamebooster.view.n;

class o implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n.b f5322a;

    o(n.b bVar) {
        this.f5322a = bVar;
    }

    public void onAnimationCancel(Animator animator) {
        boolean unused = this.f5322a.f5320b = false;
    }

    public void onAnimationEnd(Animator animator) {
        boolean unused = this.f5322a.f5320b = false;
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
        boolean unused = this.f5322a.f5320b = true;
    }
}
