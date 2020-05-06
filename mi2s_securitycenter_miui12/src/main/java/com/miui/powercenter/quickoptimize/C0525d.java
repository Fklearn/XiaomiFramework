package com.miui.powercenter.quickoptimize;

import android.animation.ValueAnimator;

/* renamed from: com.miui.powercenter.quickoptimize.d  reason: case insensitive filesystem */
class C0525d implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainContentFrame f7216a;

    C0525d(MainContentFrame mainContentFrame) {
        this.f7216a = mainContentFrame;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f7216a.n.setstate(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
