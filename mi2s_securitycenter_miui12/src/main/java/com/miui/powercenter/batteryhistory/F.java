package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;

class F extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelChart.a f6830a;

    F(BatteryLevelChart.a aVar) {
        this.f6830a = aVar;
    }

    public void onAnimationEnd(Animator animator) {
        boolean unused = this.f6830a.K = false;
        this.f6830a.h();
    }
}
