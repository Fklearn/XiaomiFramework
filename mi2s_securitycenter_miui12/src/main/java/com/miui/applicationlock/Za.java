package com.miui.applicationlock;

import android.content.DialogInterface;

class Za implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3239a;

    Za(bb bbVar) {
        this.f3239a = bbVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f3239a.e();
    }
}
