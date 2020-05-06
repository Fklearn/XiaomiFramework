package com.miui.powercenter.powerui;

import android.app.AlertDialog;
import android.content.DialogInterface;

class f implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f7147a;

    f(h hVar) {
        this.f7147a = hVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        AlertDialog unused = this.f7147a.r = null;
    }
}
