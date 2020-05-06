package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.b.a.b;

class k implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2723a;

    k(MainActivity mainActivity) {
        this.f2723a = mainActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2723a.o();
        b.a.d("cancel");
    }
}
