package com.miui.powercenter.quickoptimize;

import android.animation.Animator;

class K implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f7200a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ScanResultFrame f7201b;

    K(ScanResultFrame scanResultFrame, m mVar) {
        this.f7201b = scanResultFrame;
        this.f7200a = mVar;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f7201b.a(this.f7200a);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
