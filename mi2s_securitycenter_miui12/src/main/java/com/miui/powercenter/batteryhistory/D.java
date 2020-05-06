package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;

class D extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelChart.a f6825a;

    D(BatteryLevelChart.a aVar) {
        this.f6825a = aVar;
    }

    public void onAnimationEnd(Animator animator) {
        this.f6825a.h();
    }
}
