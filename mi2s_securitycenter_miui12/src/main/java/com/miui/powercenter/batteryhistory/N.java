package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.miui.powercenter.batteryhistory.BatteryLevelHistogram;

class N extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram.b f6844a;

    N(BatteryLevelHistogram.b bVar) {
        this.f6844a = bVar;
    }

    public void onAnimationEnd(Animator animator) {
        BatteryLevelHistogram.this.c();
        int unused = this.f6844a.G = -1;
    }
}
