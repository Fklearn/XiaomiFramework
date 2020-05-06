package com.miui.powercenter;

import android.content.DialogInterface;

class u implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f7294a;

    u(x xVar) {
        this.f7294a = xVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        int unused = this.f7294a.z = i;
        y.c(this.f7294a.A[i] * 60);
        this.f7294a.f7368c.a(this.f7294a.B[i]);
        dialogInterface.dismiss();
    }
}
