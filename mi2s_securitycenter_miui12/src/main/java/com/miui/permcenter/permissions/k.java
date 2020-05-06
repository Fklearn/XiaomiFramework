package com.miui.permcenter.permissions;

import android.widget.RelativeLayout;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RelativeLayout f6272a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppPermissionsUseActivity f6273b;

    k(AppPermissionsUseActivity appPermissionsUseActivity, RelativeLayout relativeLayout) {
        this.f6273b = appPermissionsUseActivity;
        this.f6272a = relativeLayout;
    }

    public void run() {
        this.f6273b.runOnUiThread(new j(this));
    }
}
