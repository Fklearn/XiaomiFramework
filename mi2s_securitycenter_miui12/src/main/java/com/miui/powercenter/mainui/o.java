package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;

class o implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7138a;

    o(MainBatteryView mainBatteryView) {
        this.f7138a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float unused = this.f7138a.u = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        boolean unused2 = this.f7138a.s = false;
        this.f7138a.invalidate();
    }
}
