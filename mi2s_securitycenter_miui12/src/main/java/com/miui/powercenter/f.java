package com.miui.powercenter;

import android.content.DialogInterface;

class f implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f7061a;

    f(j jVar) {
        this.f7061a = jVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f7061a.f7072a.f6640a.setSaveModeStatus(false);
        this.f7061a.f7072a.e.setChecked(false);
        dialogInterface.dismiss();
    }
}
