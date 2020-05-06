package com.miui.powercenter.powerui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;

class d implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f7145a;

    d(h hVar) {
        this.f7145a = hVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        AlertDialog unused = this.f7145a.q = null;
        TextView unused2 = this.f7145a.u = null;
        k.c(this.f7145a.e);
    }
}
