package com.miui.applicationlock;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import com.miui.applicationlock.c.o;

/* renamed from: com.miui.applicationlock.d  reason: case insensitive filesystem */
class C0269d implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3340a;

    C0269d(C0312y yVar) {
        this.f3340a = yVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Activity activity;
        String str;
        if (TransitionHelper.a(this.f3340a.getActivity()) || !this.f3340a.l.c()) {
            activity = this.f3340a.z;
            str = "com.android.settings.NewFingerprintInternalActivity";
        } else {
            activity = this.f3340a.z;
            str = "com.android.settings.MiuiSecurityChooseUnlock";
        }
        this.f3340a.startActivityForResult(o.a((Context) activity, "com.android.settings", str), 30);
    }
}
