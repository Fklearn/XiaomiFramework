package com.miui.applicationlock;

import android.content.DialogInterface;

class La implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3188a;

    La(bb bbVar) {
        this.f3188a = bbVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3188a.l.a((String) null);
        this.f3188a.p.setChecked(false);
    }
}
