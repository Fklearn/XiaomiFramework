package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import com.miui.powercenter.batteryhistory.BatteryLevelHistogram;

class O implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram.b f6845a;

    O(BatteryLevelHistogram.b bVar) {
        this.f6845a = bVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f6845a.G = (int) valueAnimator.getCurrentPlayTime();
        this.f6845a.postInvalidate();
    }
}
