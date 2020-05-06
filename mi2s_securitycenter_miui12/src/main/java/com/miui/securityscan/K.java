package com.miui.securityscan;

import android.animation.ValueAnimator;
import com.miui.securityscan.L;
import com.miui.securityscan.scanner.C0568o;

class K implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L.a f7556a;

    K(L.a aVar) {
        this.f7556a = aVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        L.this.s.a(C0568o.CLEAR_ACCELERATION, (int) (valueAnimator.getAnimatedFraction() * 100.0f));
    }
}
