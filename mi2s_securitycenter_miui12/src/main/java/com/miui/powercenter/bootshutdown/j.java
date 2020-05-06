package com.miui.powercenter.bootshutdown;

import android.content.DialogInterface;

class j implements DialogInterface.OnMultiChoiceClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f6958a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RepeatPreference f6959b;

    j(RepeatPreference repeatPreference, c cVar) {
        this.f6959b = repeatPreference;
        this.f6958a = cVar;
    }

    public void onClick(DialogInterface dialogInterface, int i, boolean z) {
        this.f6958a.a(i, z);
    }
}
