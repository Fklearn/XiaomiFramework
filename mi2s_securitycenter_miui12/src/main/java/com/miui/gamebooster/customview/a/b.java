package com.miui.gamebooster.customview.a;

import android.animation.ValueAnimator;
import android.widget.RelativeLayout;

class b implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f4173a;

    b(d dVar) {
        this.f4173a = dVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.f4173a.r.getLayoutParams();
        if (this.f4173a.o) {
            layoutParams.setMargins(0, (int) floatValue, 0, 0);
        } else if (this.f4173a.p) {
            layoutParams.setMargins((int) floatValue, 0, 0, 0);
        } else {
            layoutParams.setMargins(0, 0, (int) floatValue, 0);
        }
        this.f4173a.r.setLayoutParams(layoutParams);
        this.f4173a.f.d().setmFirstExpand(true);
    }
}
