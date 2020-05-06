package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.b.a.b;

class p implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2729a;

    p(MainActivity mainActivity) {
        this.f2729a = mainActivity;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f2729a.o();
        b.a.i("cancel");
    }
}
