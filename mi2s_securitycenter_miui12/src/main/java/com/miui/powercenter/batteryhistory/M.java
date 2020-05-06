package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import com.miui.powercenter.batteryhistory.BatteryLevelHistogram;

class M implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram.b f6843a;

    M(BatteryLevelHistogram.b bVar) {
        this.f6843a = bVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f6843a.G = (int) valueAnimator.getCurrentPlayTime();
        this.f6843a.postInvalidate();
    }
}
