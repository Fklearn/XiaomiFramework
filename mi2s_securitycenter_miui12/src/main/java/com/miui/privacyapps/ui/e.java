package com.miui.privacyapps.ui;

import android.content.DialogInterface;
import b.b.k.c;

class e implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f7399a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ n f7400b;

    e(n nVar, c cVar) {
        this.f7400b = nVar;
        this.f7399a = cVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f7400b.b(this.f7399a);
    }
}
