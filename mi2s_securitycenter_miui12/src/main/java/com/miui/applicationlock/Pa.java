package com.miui.applicationlock;

import android.content.DialogInterface;

class Pa implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3203a;

    Pa(bb bbVar) {
        this.f3203a = bbVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f3203a.k.setChecked(true);
    }
}
