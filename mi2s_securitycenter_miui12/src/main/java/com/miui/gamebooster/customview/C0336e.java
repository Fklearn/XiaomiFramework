package com.miui.gamebooster.customview;

import android.animation.Animator;

/* renamed from: com.miui.gamebooster.customview.e  reason: case insensitive filesystem */
class C0336e implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AuditionView f4191a;

    C0336e(AuditionView auditionView) {
        this.f4191a = auditionView;
    }

    public void onAnimationCancel(Animator animator) {
        this.f4191a.m.setAlpha(255);
        this.f4191a.m.a(1.0f);
        this.f4191a.f4106d.setImageDrawable(this.f4191a.m);
    }

    public void onAnimationEnd(Animator animator) {
        this.f4191a.m.setAlpha(255);
        this.f4191a.m.a(1.0f);
        this.f4191a.f4106d.setImageDrawable(this.f4191a.m);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
