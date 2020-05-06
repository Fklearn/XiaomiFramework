package com.miui.permcenter.privacymanager.b;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

class k extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f6366a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ m f6367b;

    k(m mVar, View view) {
        this.f6367b = mVar;
        this.f6366a = view;
    }

    public void onAnimationEnd(Animator animator) {
        ViewGroup viewGroup;
        View view;
        super.onAnimationEnd(animator);
        Window window = ((Activity) this.f6367b.getContext()).getWindow();
        if (window != null && (viewGroup = (ViewGroup) window.getDecorView()) != null && (view = this.f6366a) != null) {
            viewGroup.removeView(view);
        }
    }
}
