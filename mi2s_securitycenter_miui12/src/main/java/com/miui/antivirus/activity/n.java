package com.miui.antivirus.activity;

import android.content.Context;
import android.content.DialogInterface;
import b.b.b.a.b;
import com.miui.securitycenter.h;

class n implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f2726a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MainActivity f2727b;

    n(MainActivity mainActivity, Context context) {
        this.f2727b = mainActivity;
        this.f2726a = context;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (!h.i()) {
            h.b(true);
            b.a.h("open");
        }
        this.f2727b.a(this.f2726a);
        b.a.i("update");
    }
}
