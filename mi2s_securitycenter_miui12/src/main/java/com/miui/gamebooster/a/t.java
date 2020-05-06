package com.miui.gamebooster.a;

import android.animation.Animator;
import android.view.View;

class t implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f4063a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f4064b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ v f4065c;

    t(v vVar, View view, int i) {
        this.f4065c = vVar;
        this.f4063a = view;
        this.f4064b = i;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f4063a.setBackgroundColor(this.f4064b);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
