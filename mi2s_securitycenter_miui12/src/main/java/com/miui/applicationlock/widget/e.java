package com.miui.applicationlock.widget;

import android.view.View;
import android.view.animation.Animation;

class e implements Animation.AnimationListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f3432a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MiuiNumericInputView f3433b;

    e(MiuiNumericInputView miuiNumericInputView, View view) {
        this.f3433b = miuiNumericInputView;
        this.f3432a = view;
    }

    public void onAnimationEnd(Animation animation) {
        this.f3432a.setVisibility(0);
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }
}
