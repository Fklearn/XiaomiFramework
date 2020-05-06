package com.miui.applicationlock.widget;

import android.view.animation.Animation;

class w implements Animation.AnimationListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f3458a;

    w(x xVar) {
        this.f3458a = xVar;
    }

    public void onAnimationEnd(Animation animation) {
        this.f3458a.i();
        this.f3458a.f3462d.setEnabled(true);
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }
}
