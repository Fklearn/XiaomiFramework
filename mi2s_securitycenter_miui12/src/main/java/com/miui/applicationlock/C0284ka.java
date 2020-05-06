package com.miui.applicationlock;

import android.animation.ValueAnimator;
import android.view.Window;
import android.view.WindowManager;

/* renamed from: com.miui.applicationlock.ka  reason: case insensitive filesystem */
class C0284ka implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Window f3358a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3359b;

    C0284ka(ConfirmAccessControl confirmAccessControl, Window window) {
        this.f3359b = confirmAccessControl;
        this.f3358a = window;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        WindowManager.LayoutParams attributes = this.f3358a.getAttributes();
        attributes.alpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.f3358a.setAttributes(attributes);
    }
}
