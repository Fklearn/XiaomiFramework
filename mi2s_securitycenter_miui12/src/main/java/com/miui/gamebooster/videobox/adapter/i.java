package com.miui.gamebooster.videobox.adapter;

import android.animation.Animator;
import android.view.View;

class i implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f5173a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f5174b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ k f5175c;

    i(k kVar, View view, View view2) {
        this.f5175c = kVar;
        this.f5173a = view;
        this.f5174b = view2;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        View view = this.f5174b;
        if (view != null) {
            view.setVisibility(4);
        }
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
        View view = this.f5173a;
        if (view != null) {
            view.setAlpha(0.0f);
            this.f5173a.setVisibility(0);
        }
    }
}
