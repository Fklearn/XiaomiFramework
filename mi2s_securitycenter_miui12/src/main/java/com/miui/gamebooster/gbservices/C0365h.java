package com.miui.gamebooster.gbservices;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

/* renamed from: com.miui.gamebooster.gbservices.h  reason: case insensitive filesystem */
class C0365h extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AntiMsgAccessibilityService f4355a;

    C0365h(AntiMsgAccessibilityService antiMsgAccessibilityService) {
        this.f4355a = antiMsgAccessibilityService;
    }

    public void onAnimationEnd(Animator animator) {
        this.f4355a.e();
    }
}
