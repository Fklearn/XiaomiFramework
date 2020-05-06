package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class L extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram f6842a;

    L(BatteryLevelHistogram batteryLevelHistogram) {
        this.f6842a = batteryLevelHistogram;
    }

    public void onAnimationEnd(Animator animator) {
        float unused = this.f6842a.k = -1.0f;
    }
}
