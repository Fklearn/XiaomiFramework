package com.miui.permcenter.settings.view;

import android.animation.Animator;

class f extends j {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionTotalView f6597a;

    f(PermissionTotalView permissionTotalView) {
        this.f6597a = permissionTotalView;
    }

    public void onAnimationEnd(Animator animator) {
        boolean unused = this.f6597a.Q = true;
        int unused2 = this.f6597a.U = 255;
        this.f6597a.W.postDelayed(this.f6597a, 1500);
    }
}
