package com.miui.applicationlock;

import android.content.DialogInterface;

class Oa implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3200a;

    Oa(bb bbVar) {
        this.f3200a = bbVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3200a.k.setChecked(true);
    }
}
