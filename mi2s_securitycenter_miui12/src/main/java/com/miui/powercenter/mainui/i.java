package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;

class i implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7131a;

    i(MainBatteryView mainBatteryView) {
        this.f7131a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f7131a.n = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.f7131a.g.setColor(this.f7131a.n);
        if (this.f7131a.l != null) {
            this.f7131a.l.setTextColor(this.f7131a.n);
        }
        this.f7131a.invalidate();
    }
}
