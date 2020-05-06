package com.miui.securitycenter.activity;

import android.content.DialogInterface;

class b implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ThirdDesktopAlertActivity f7468a;

    b(ThirdDesktopAlertActivity thirdDesktopAlertActivity) {
        this.f7468a = thirdDesktopAlertActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        this.f7468a.finish();
    }
}
