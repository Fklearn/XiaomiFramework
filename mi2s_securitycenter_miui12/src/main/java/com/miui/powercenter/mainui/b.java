package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;

class b implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivityView f7122a;

    b(MainActivityView mainActivityView) {
        this.f7122a = mainActivityView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f7122a.a(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
