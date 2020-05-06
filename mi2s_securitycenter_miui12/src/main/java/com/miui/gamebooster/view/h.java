package com.miui.gamebooster.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class h extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DragGridView f5278a;

    h(DragGridView dragGridView) {
        this.f5278a = dragGridView;
    }

    public void onAnimationEnd(Animator animator) {
        boolean unused = this.f5278a.v = true;
    }

    public void onAnimationStart(Animator animator) {
        boolean unused = this.f5278a.v = false;
    }
}
