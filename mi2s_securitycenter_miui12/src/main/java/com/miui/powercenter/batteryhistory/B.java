package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;

class B extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelChart.a f6782a;

    B(BatteryLevelChart.a aVar) {
        this.f6782a = aVar;
    }

    public void onAnimationEnd(Animator animator) {
        BatteryLevelChart.this.b();
        float unused = this.f6782a.aa = 1.0f;
    }
}
