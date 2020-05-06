package com.miui.securitycenter.activity;

import android.content.DialogInterface;

class a implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ThirdDesktopAlertActivity f7467a;

    a(ThirdDesktopAlertActivity thirdDesktopAlertActivity) {
        this.f7467a = thirdDesktopAlertActivity;
    }

    public void onCancel(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
        this.f7467a.finish();
    }
}
