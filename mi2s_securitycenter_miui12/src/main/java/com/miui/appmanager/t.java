package com.miui.appmanager;

import android.content.DialogInterface;

class t implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3688a;

    t(AppManagerMainActivity appManagerMainActivity) {
        this.f3688a = appManagerMainActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3688a.B();
    }
}
