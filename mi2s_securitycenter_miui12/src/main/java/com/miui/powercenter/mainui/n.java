package com.miui.powercenter.mainui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class n extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f7136a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MainBatteryView f7137b;

    n(MainBatteryView mainBatteryView, int i) {
        this.f7137b = mainBatteryView;
        this.f7136a = i;
    }

    public void onAnimationEnd(Animator animator) {
        boolean unused = this.f7137b.s = true;
        int unused2 = this.f7137b.t = this.f7136a;
        this.f7137b.invalidate();
    }
}
