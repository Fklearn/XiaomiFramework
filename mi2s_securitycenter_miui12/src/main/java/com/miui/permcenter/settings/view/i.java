package com.miui.permcenter.settings.view;

import android.animation.ValueAnimator;

class i implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionTotalView f6600a;

    i(PermissionTotalView permissionTotalView) {
        this.f6600a = permissionTotalView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        int unused = this.f6600a.U = intValue;
        this.f6600a.h(intValue);
    }
}
