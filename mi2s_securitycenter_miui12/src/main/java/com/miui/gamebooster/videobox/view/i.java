package com.miui.gamebooster.videobox.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class i extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    private boolean f5246a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SlidingButton f5247b;

    i(SlidingButton slidingButton) {
        this.f5247b = slidingButton;
    }

    public void onAnimationCancel(Animator animator) {
        this.f5246a = true;
    }

    public void onAnimationEnd(Animator animator) {
        boolean z = false;
        boolean unused = this.f5247b.u = false;
        if (!this.f5246a) {
            Animator unused2 = this.f5247b.t = null;
            if (this.f5247b.l >= this.f5247b.k) {
                z = true;
            }
            if (z != this.f5247b.isChecked()) {
                this.f5247b.setChecked(z);
                this.f5247b.b();
            }
        }
    }

    public void onAnimationStart(Animator animator) {
        this.f5246a = false;
        boolean unused = this.f5247b.u = true;
    }
}
