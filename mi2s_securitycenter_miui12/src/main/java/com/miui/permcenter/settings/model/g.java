package com.miui.permcenter.settings.model;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class g extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionUseTotalPreference f6549a;

    g(PermissionUseTotalPreference permissionUseTotalPreference) {
        this.f6549a = permissionUseTotalPreference;
    }

    public void onAnimationStart(Animator animator) {
        this.f6549a.m.a();
    }
}
