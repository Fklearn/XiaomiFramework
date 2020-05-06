package com.miui.common.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import java.util.Locale;

class f extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ScoreTextView f3800a;

    f(ScoreTextView scoreTextView) {
        this.f3800a = scoreTextView;
    }

    public void onAnimationCancel(Animator animator) {
        this.f3800a.a((CharSequence) String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(this.f3800a.f3793b)}));
    }

    public void onAnimationEnd(Animator animator) {
        this.f3800a.a((CharSequence) String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(this.f3800a.f3793b)}));
    }
}
