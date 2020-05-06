package com.miui.permcenter.settings.view;

import android.animation.Animator;

class h extends j {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionTotalView f6599a;

    h(PermissionTotalView permissionTotalView) {
        this.f6599a = permissionTotalView;
    }

    public void onAnimationEnd(Animator animator) {
        this.f6599a.e();
        int unused = this.f6599a.U = 0;
        boolean unused2 = this.f6599a.R = false;
    }
}
