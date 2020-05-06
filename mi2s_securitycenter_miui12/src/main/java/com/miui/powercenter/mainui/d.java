package com.miui.powercenter.mainui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class d extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f7124a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7125b;

    d(MainBatteryView mainBatteryView, int i) {
        this.f7125b = mainBatteryView;
        this.f7124a = i;
    }

    public void onAnimationEnd(Animator animator) {
        boolean unused = this.f7125b.s = true;
        int unused2 = this.f7125b.t = this.f7124a;
        this.f7125b.invalidate();
    }
}
