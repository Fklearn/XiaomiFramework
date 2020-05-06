package com.miui.optimizemanage;

import android.animation.ValueAnimator;

class u implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f6005a;

    u(v vVar) {
        this.f6005a = vVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.f6005a.f6008c.setAlpha(floatValue);
        this.f6005a.f6007b.setAlpha(floatValue);
    }
}
