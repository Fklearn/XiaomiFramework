package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;

class j implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7132a;

    j(MainBatteryView mainBatteryView) {
        this.f7132a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f7132a.m = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.f7132a.f.setColor(this.f7132a.m);
        this.f7132a.invalidate();
    }
}
