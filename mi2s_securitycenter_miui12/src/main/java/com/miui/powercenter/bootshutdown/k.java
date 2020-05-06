package com.miui.powercenter.bootshutdown;

import android.content.DialogInterface;

class k implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f6960a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RepeatPreference f6961b;

    k(RepeatPreference repeatPreference, c cVar) {
        this.f6961b = repeatPreference;
        this.f6960a = cVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f6961b.f6946d.a(this.f6960a);
        this.f6961b.f();
    }
}
