package com.miui.applicationlock;

import android.content.DialogInterface;
import android.provider.Settings;
import com.miui.applicationlock.a.i;

class Ua implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3225a;

    Ua(bb bbVar) {
        this.f3225a = bbVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Settings.Secure.putInt(this.f3225a.I.getContentResolver(), i.f3250a, 1);
        this.f3225a.f3264b.setChecked(false);
        this.f3225a.h.a();
    }
}
