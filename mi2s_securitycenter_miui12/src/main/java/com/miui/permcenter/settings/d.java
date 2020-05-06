package com.miui.permcenter.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class d extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f6511a;

    d(j jVar) {
        this.f6511a = jVar;
    }

    public void onAnimationEnd(Animator animator) {
        this.f6511a.h.sendEmptyMessageDelayed(1, 1000);
    }
}
