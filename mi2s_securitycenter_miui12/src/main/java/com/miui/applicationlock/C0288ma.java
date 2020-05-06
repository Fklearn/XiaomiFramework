package com.miui.applicationlock;

import android.view.View;

/* renamed from: com.miui.applicationlock.ma  reason: case insensitive filesystem */
class C0288ma implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3364a;

    C0288ma(ConfirmAccessControl confirmAccessControl) {
        this.f3364a = confirmAccessControl;
    }

    public void onClick(View view) {
        this.f3364a.finish();
    }
}
