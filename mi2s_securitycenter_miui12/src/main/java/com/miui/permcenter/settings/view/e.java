package com.miui.permcenter.settings.view;

import android.animation.ValueAnimator;

class e implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionTotalView f6596a;

    e(PermissionTotalView permissionTotalView) {
        this.f6596a = permissionTotalView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int unused = this.f6596a.ha = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.f6596a.invalidate();
    }
}
