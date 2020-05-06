package com.miui.powercenter.view;

import android.animation.ValueAnimator;

/* compiled from: lambda */
public final /* synthetic */ class b implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    private final /* synthetic */ ShadowButton f7359a;

    public /* synthetic */ b(ShadowButton shadowButton) {
        this.f7359a = shadowButton;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f7359a.b(valueAnimator);
    }
}
