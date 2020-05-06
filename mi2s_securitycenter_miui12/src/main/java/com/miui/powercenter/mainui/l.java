package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;

class l implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7134a;

    l(MainBatteryView mainBatteryView) {
        this.f7134a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f7134a.m = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.f7134a.f.setColor(this.f7134a.m);
        this.f7134a.invalidate();
    }
}
