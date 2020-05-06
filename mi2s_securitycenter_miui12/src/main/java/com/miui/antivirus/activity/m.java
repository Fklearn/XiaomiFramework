package com.miui.antivirus.activity;

import android.content.DialogInterface;

class m implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2725a;

    m(MainActivity mainActivity) {
        this.f2725a = mainActivity;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f2725a.o();
    }
}
