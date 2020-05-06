package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;
import android.graphics.LinearGradient;
import android.graphics.Shader;

class f implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7128a;

    f(MainBatteryView mainBatteryView) {
        this.f7128a = mainBatteryView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        MainBatteryView mainBatteryView = this.f7128a;
        float b2 = ((((float) mainBatteryView.f7113a) * this.f7128a.f7115c) / 100.0f) - this.f7128a.f7116d;
        LinearGradient unused = mainBatteryView.k = new LinearGradient(b2, 0.0f, (((float) this.f7128a.f7113a) * this.f7128a.f7115c) / 100.0f, 0.0f, new int[]{-12594584, intValue}, (float[]) null, Shader.TileMode.CLAMP);
        this.f7128a.h.setShader(this.f7128a.k);
        this.f7128a.invalidate();
    }
}
