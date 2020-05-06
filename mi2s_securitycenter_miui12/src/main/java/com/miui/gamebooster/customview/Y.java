package com.miui.gamebooster.customview;

import android.animation.Animator;

class Y implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VoiceModeView f4167a;

    Y(VoiceModeView voiceModeView) {
        this.f4167a = voiceModeView;
    }

    public void onAnimationCancel(Animator animator) {
        this.f4167a.f4161d.b(0);
    }

    public void onAnimationEnd(Animator animator) {
        this.f4167a.f4161d.b(0);
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
