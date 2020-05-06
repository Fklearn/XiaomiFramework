package com.miui.powercenter;

import android.content.DialogInterface;

class v implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int[] f7323a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String[] f7324b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ x f7325c;

    v(x xVar, int[] iArr, String[] strArr) {
        this.f7325c = xVar;
        this.f7323a = iArr;
        this.f7324b = strArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f7325c.d(this.f7323a[i]);
        this.f7325c.k.a(this.f7324b[i]);
        dialogInterface.dismiss();
    }
}
