package com.miui.permcenter.settings;

import android.animation.ValueAnimator;

class e implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f6512a;

    e(j jVar) {
        this.f6512a = jVar;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (((Float) valueAnimator.getAnimatedValue()).floatValue() < 0.05f && !this.f6512a.l) {
            boolean unused = this.f6512a.l = true;
            j jVar = this.f6512a;
            jVar.k++;
            jVar.k %= 3;
            jVar.o.setImageResource(this.f6512a.m[this.f6512a.k]);
        }
    }
}
