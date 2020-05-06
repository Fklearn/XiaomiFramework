package com.miui.applicationlock;

import android.content.DialogInterface;

class Ma implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3190a;

    Ma(bb bbVar) {
        this.f3190a = bbVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f3190a.l.a(this.f3190a.l.b());
        this.f3190a.p.setChecked(this.f3190a.l.b() != null);
    }
}
