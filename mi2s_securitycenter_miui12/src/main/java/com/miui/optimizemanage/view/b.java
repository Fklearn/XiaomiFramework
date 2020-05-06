package com.miui.optimizemanage.view;

import android.animation.ValueAnimator;

class b implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ OptimizeMainView f6027a;

    b(OptimizeMainView optimizeMainView) {
        this.f6027a = optimizeMainView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f6027a.f6016a.setRenderState(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
