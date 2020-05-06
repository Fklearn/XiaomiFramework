package com.miui.applicationlock;

import android.content.DialogInterface;
import com.miui.applicationlock.c.K;

class Ka implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3186a;

    Ka(bb bbVar) {
        this.f3186a = bbVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3186a.l.a(K.d(this.f3186a.I));
        this.f3186a.p.setChecked(true);
        this.f3186a.C.setOnDismissListener((DialogInterface.OnDismissListener) null);
    }
}
