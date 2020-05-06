package com.miui.gamebooster.gbservices;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

/* renamed from: com.miui.gamebooster.gbservices.g  reason: case insensitive filesystem */
class C0364g extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AntiMsgAccessibilityService f4354a;

    C0364g(AntiMsgAccessibilityService antiMsgAccessibilityService) {
        this.f4354a = antiMsgAccessibilityService;
    }

    public void onAnimationEnd(Animator animator) {
        this.f4354a.e();
    }
}
