package com.miui.antivirus.activity;

import android.content.DialogInterface;
import com.miui.securitycenter.h;

class w implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2739a;

    w(MainActivity mainActivity) {
        this.f2739a = mainActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        h.b(true);
        this.f2739a.F();
    }
}
