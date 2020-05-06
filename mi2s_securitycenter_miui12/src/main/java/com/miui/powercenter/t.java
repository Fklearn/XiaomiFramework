package com.miui.powercenter;

import android.content.DialogInterface;

class t implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f7293a;

    t(x xVar) {
        this.f7293a = xVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        int unused = this.f7293a.v = i;
        y.d(this.f7293a.w[i] * 60);
        this.f7293a.e.a(this.f7293a.x[i]);
        dialogInterface.dismiss();
    }
}
