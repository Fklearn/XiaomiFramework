package com.miui.applicationlock;

import android.content.DialogInterface;
import com.miui.applicationlock.c.o;

class Na implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3198a;

    Na(bb bbVar) {
        this.f3198a = bbVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        o.c(this.f3198a.I.getApplicationContext());
        this.f3198a.a(true);
        this.f3198a.m();
        this.f3198a.getActivity().finish();
    }
}
