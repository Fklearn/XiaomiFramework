package com.miui.permcenter.settings.view;

import android.animation.ValueAnimator;

class g implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionTotalView f6598a;

    g(PermissionTotalView permissionTotalView) {
        this.f6598a = permissionTotalView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        int unused = this.f6598a.U = intValue;
        this.f6598a.h(intValue);
    }
}
