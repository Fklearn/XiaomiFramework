package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.miui.powercenter.batteryhistory.BatteryLevelHistogram;

class S extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram.b f6849a;

    S(BatteryLevelHistogram.b bVar) {
        this.f6849a = bVar;
    }

    public void onAnimationEnd(Animator animator) {
        float unused = this.f6849a.H = -1.0f;
    }
}
