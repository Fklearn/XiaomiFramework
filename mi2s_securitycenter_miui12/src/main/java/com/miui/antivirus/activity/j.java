package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.b.a.b;

class j implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2722a;

    j(MainActivity mainActivity) {
        this.f2722a = mainActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2722a.G();
        b.a.d("continue");
    }
}
