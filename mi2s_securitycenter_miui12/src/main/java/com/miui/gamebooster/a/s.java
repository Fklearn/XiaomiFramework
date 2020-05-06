package com.miui.gamebooster.a;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import com.miui.gamebooster.globalgame.util.b;

class s implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f4061a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ v f4062b;

    s(v vVar, View view) {
        this.f4062b = vVar;
        this.f4061a = view;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int i;
        try {
            i = Color.parseColor("#" + Integer.toHexString(((Integer) valueAnimator.getAnimatedValue()).intValue()) + "f7f7f7");
        } catch (IllegalArgumentException e) {
            b.b(e);
            i = -1;
        }
        if (i != -1) {
            this.f4061a.setBackgroundColor(i);
        }
    }
}
