package com.miui.powercenter.mainui;

import android.animation.Animator;

class c implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivityView f7123a;

    c(MainActivityView mainActivityView) {
        this.f7123a = mainActivityView;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.f7123a.f7112d.setVisibility(4);
        this.f7123a.n.setIsShowSecondTitle(false);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
        this.f7123a.f.sendEmptyMessage(1024);
    }
}
