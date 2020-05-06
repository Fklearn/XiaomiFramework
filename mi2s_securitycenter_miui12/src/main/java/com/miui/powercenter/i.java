package com.miui.powercenter;

import android.content.DialogInterface;

class i implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f7071a;

    i(j jVar) {
        this.f7071a = jVar;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f7071a.f7072a.f.setChecked(false);
    }
}
