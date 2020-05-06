package com.miui.antivirus.ui;

import android.animation.Animator;
import android.widget.ImageView;

class i implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Boolean f2966a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f2967b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f2968c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ MainContentFrame f2969d;

    i(MainContentFrame mainContentFrame, Boolean bool, boolean z, int i) {
        this.f2969d = mainContentFrame;
        this.f2966a = bool;
        this.f2967b = z;
        this.f2968c = i;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        int i = 0;
        this.f2969d.f2928c.setVisibility(0);
        this.f2969d.f2929d.setVisibility(0);
        this.f2969d.e.setVisibility(8);
        this.f2969d.f.setVisibility(8);
        this.f2969d.h.setVisibility(8);
        this.f2969d.g.setClickable(this.f2966a.booleanValue());
        ImageView i2 = this.f2969d.i;
        if (!this.f2966a.booleanValue()) {
            i = 8;
        }
        i2.setVisibility(i);
        this.f2969d.j.animate().alpha(0.0f).setDuration(750).start();
        this.f2969d.a(this.f2967b, this.f2968c, this.f2966a.booleanValue());
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
