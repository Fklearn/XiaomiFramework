package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;
import android.view.View;

/* renamed from: com.miui.gamebooster.customview.t  reason: case insensitive filesystem */
class C0350t implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f4228a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoxView f4229b;

    C0350t(GameBoxView gameBoxView, View view) {
        this.f4229b = gameBoxView;
        this.f4228a = view;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f4229b.a(this.f4228a, ((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
