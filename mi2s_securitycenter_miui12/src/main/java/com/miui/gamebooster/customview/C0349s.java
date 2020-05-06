package com.miui.gamebooster.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/* renamed from: com.miui.gamebooster.customview.s  reason: case insensitive filesystem */
class C0349s extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f4226a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoxView f4227b;

    C0349s(GameBoxView gameBoxView, View view) {
        this.f4227b = gameBoxView;
        this.f4226a = view;
    }

    public void onAnimationEnd(Animator animator) {
        this.f4226a.setVisibility(8);
    }
}
