package com.miui.gamebooster.ui;

import android.content.Context;
import android.content.DialogInterface;
import com.miui.gamebooster.m.ba;

class I implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4915a;

    I(N n) {
        this.f4915a = n;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        ba.a((Context) this.f4915a.getActivity(), (Boolean) false);
    }
}
