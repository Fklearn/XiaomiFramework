package com.miui.permcenter.settings.view;

import android.animation.ValueAnimator;

class d implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionTotalView f6595a;

    d(PermissionTotalView permissionTotalView) {
        this.f6595a = permissionTotalView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f6595a.g(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
