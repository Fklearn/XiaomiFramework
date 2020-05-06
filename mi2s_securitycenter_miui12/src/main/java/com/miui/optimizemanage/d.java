package com.miui.optimizemanage;

import android.animation.ValueAnimator;

class d implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f5926a;

    d(m mVar) {
        this.f5926a = mVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f5926a.j.setAnimProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
