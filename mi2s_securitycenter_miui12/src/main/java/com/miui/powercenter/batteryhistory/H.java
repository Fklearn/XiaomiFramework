package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;

class H extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelChart.a f6835a;

    H(BatteryLevelChart.a aVar) {
        this.f6835a = aVar;
    }

    public void onAnimationEnd(Animator animator) {
        boolean unused = this.f6835a.K = true;
        boolean unused2 = this.f6835a.L = false;
        this.f6835a.invalidate();
        this.f6835a.g();
    }
}
