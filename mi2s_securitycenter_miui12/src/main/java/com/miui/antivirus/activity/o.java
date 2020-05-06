package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.b.a.b;

class o implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2728a;

    o(MainActivity mainActivity) {
        this.f2728a = mainActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2728a.o();
        b.a.i("cancel");
        b.a.h("cancel");
    }
}
