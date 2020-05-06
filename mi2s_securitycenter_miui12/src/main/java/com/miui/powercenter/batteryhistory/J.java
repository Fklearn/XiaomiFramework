package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class J extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram f6837a;

    J(BatteryLevelHistogram batteryLevelHistogram) {
        this.f6837a = batteryLevelHistogram;
    }

    public void onAnimationEnd(Animator animator) {
        this.f6837a.f6815d.setVisibility(8);
    }
}
