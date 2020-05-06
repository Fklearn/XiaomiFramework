package com.miui.antivirus.activity;

import android.content.DialogInterface;
import com.miui.securitycenter.h;

class x implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2740a;

    x(MainActivity mainActivity) {
        this.f2740a = mainActivity;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        if (!h.i()) {
            this.f2740a.finish();
        }
    }
}
