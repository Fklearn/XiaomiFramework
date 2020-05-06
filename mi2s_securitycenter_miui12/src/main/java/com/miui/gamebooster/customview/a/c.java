package com.miui.gamebooster.customview.a;

import android.animation.Animator;
import android.util.Log;

class c implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f4174a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d f4175b;

    c(d dVar, int i) {
        this.f4175b = dVar;
        this.f4174a = i;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        Log.i("GameBoxTouchListener", "onAnimationEnd: " + this.f4174a);
        if (this.f4174a != 0) {
            this.f4175b.f.j();
        }
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
