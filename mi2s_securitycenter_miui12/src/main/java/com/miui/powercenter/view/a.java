package com.miui.powercenter.view;

import android.animation.ValueAnimator;

/* compiled from: lambda */
public final /* synthetic */ class a implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    private final /* synthetic */ ShadowButton f7358a;

    public /* synthetic */ a(ShadowButton shadowButton) {
        this.f7358a = shadowButton;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f7358a.a(valueAnimator);
    }
}
