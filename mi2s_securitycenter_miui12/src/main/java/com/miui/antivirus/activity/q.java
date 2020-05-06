package com.miui.antivirus.activity;

import android.content.DialogInterface;

class q implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2730a;

    q(MainActivity mainActivity) {
        this.f2730a = mainActivity;
    }

    public void onCancel(DialogInterface dialogInterface) {
        if (this.f2730a.h.compareAndSet(false, true)) {
            this.f2730a.o();
        }
    }
}
