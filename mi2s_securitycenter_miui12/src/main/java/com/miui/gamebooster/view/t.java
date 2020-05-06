package com.miui.gamebooster.view;

import android.animation.ValueAnimator;
import android.graphics.Point;

class t implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IncomingCallFloatBall f5324a;

    t(IncomingCallFloatBall incomingCallFloatBall) {
        this.f5324a = incomingCallFloatBall;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        Point point = (Point) valueAnimator.getAnimatedValue();
        this.f5324a.a(point.x, point.y);
    }
}
