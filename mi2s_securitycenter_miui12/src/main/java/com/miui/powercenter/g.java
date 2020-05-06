package com.miui.powercenter;

import android.content.DialogInterface;

class g implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7066a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f7067b;

    g(j jVar, boolean z) {
        this.f7067b = jVar;
        this.f7066a = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f7067b.f7072a.f6640a.setSaveModeStatus(this.f7066a);
        this.f7067b.f7072a.a(this.f7066a);
        dialogInterface.dismiss();
    }
}
