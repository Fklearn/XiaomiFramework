package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;
import com.miui.powercenter.mainui.MainBatteryView;

class e implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView.a f7126a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7127b;

    e(MainBatteryView mainBatteryView, MainBatteryView.a aVar) {
        this.f7127b = mainBatteryView;
        this.f7126a = aVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f7126a.b(((Float) valueAnimator.getAnimatedValue()).floatValue());
        this.f7127b.invalidate();
    }
}
