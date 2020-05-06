package com.miui.optimizemanage;

import android.animation.ValueAnimator;

class g implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f5935a;

    g(m mVar) {
        this.f5935a = mVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        if (intValue == 1048576) {
            intValue = 0;
        }
        this.f5935a.a((long) intValue);
    }
}
