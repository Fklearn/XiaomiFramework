package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import com.miui.antispam.ui.activity.BackSoundActivity;
import miuix.preference.RadioButtonPreference;

/* renamed from: com.miui.antispam.ui.activity.o  reason: case insensitive filesystem */
class C0221o implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RadioButtonPreference f2605a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ BackSoundActivity.a f2606b;

    C0221o(BackSoundActivity.a aVar, RadioButtonPreference radioButtonPreference) {
        this.f2606b = aVar;
        this.f2605a = radioButtonPreference;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f2606b.a(this.f2605a);
    }
}
