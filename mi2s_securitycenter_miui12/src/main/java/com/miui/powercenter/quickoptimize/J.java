package com.miui.powercenter.quickoptimize;

import android.animation.Animator;
import android.util.Log;

class J implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ScanResultFrame f7199a;

    J(ScanResultFrame scanResultFrame) {
        this.f7199a = scanResultFrame;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        Log.i("ScanResultFrame", "animation header view end");
        if (this.f7199a.e == null) {
            this.f7199a.f7210c.setVisibility(8);
            this.f7199a.f7211d.a(true);
            return;
        }
        this.f7199a.n.setVisibility(8);
        this.f7199a.e.a(true);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
