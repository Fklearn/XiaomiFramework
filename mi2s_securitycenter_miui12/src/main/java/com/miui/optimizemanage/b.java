package com.miui.optimizemanage;

import android.animation.ValueAnimator;

class b implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f5867a;

    b(m mVar) {
        this.f5867a = mVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.f5867a.f5941a.setAlpha(floatValue);
        this.f5867a.f5942b.setAlpha(floatValue);
        this.f5867a.f5943c.setAlpha(floatValue);
        this.f5867a.h.setAlpha(floatValue);
    }
}
