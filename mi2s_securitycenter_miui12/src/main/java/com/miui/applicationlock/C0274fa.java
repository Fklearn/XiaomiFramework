package com.miui.applicationlock;

import android.content.DialogInterface;

/* renamed from: com.miui.applicationlock.fa  reason: case insensitive filesystem */
class C0274fa implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3347a;

    C0274fa(ConfirmAccessControl confirmAccessControl) {
        this.f3347a = confirmAccessControl;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        if (!this.f3347a.X) {
            ConfirmAccessControl confirmAccessControl = this.f3347a;
            confirmAccessControl.b(confirmAccessControl.B);
        }
    }
}
