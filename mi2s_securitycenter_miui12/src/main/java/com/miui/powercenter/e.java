package com.miui.powercenter;

import android.content.DialogInterface;

class e implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f7059a;

    e(j jVar) {
        this.f7059a = jVar;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f7059a.f7072a.f6640a.setSaveModeStatus(false);
        this.f7059a.f7072a.e.setChecked(false);
    }
}
