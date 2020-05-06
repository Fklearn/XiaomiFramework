package com.miui.powercenter.autotask;

import android.content.DialogInterface;

class J implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ OperationEditActivity f6714a;

    J(OperationEditActivity operationEditActivity) {
        this.f6714a = operationEditActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (-1 == i) {
            this.f6714a.finish();
        }
    }
}
