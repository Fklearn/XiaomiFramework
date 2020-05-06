package com.miui.powercenter.batteryhistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.miui.powercenter.batteryhistory.BatteryLevelHistogram;
import com.miui.powercenter.batteryhistory.a.a;

class P extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ a.C0061a f6846a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram.b f6847b;

    P(BatteryLevelHistogram.b bVar, a.C0061a aVar) {
        this.f6847b = bVar;
        this.f6846a = aVar;
    }

    public void onAnimationEnd(Animator animator) {
        this.f6846a.a();
    }
}
