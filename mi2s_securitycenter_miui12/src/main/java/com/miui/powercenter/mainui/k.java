package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;
import android.graphics.Shader;

class k implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7133a;

    k(MainBatteryView mainBatteryView) {
        this.f7133a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f7133a.n = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.f7133a.g.setColor(this.f7133a.n);
        this.f7133a.h.setShader((Shader) null);
        this.f7133a.h.setColor(this.f7133a.n);
        if (this.f7133a.l != null) {
            this.f7133a.l.setTextColor(this.f7133a.n);
        }
        this.f7133a.invalidate();
    }
}
