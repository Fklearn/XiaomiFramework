package com.miui.powercenter.powerui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;

class e implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f7146a;

    e(h hVar) {
        this.f7146a = hVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        AlertDialog unused = this.f7146a.p = null;
        TextView unused2 = this.f7146a.u = null;
    }
}
