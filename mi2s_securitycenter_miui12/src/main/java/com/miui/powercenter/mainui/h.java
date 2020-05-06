package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;

class h implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7130a;

    h(MainBatteryView mainBatteryView) {
        this.f7130a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f7130a.m = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.f7130a.f.setColor(this.f7130a.m);
        this.f7130a.invalidate();
    }
}
