package com.miui.antivirus.ui;

import android.animation.ValueAnimator;

class j implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainContentFrame f2970a;

    j(MainContentFrame mainContentFrame) {
        this.f2970a = mainContentFrame;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f2970a.j.setRenderState(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
