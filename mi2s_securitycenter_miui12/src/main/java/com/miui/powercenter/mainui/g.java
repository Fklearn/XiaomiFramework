package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;

class g implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7129a;

    g(MainBatteryView mainBatteryView) {
        this.f7129a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f7129a.n = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.f7129a.g.setColor(this.f7129a.n);
        if (this.f7129a.l != null) {
            this.f7129a.l.setTextColor(this.f7129a.n);
        }
        this.f7129a.invalidate();
    }
}
