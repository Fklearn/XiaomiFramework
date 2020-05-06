package com.miui.permcenter;

import android.content.DialogInterface;
import com.miui.permcenter.SettingsAcitivty;

class r implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsAcitivty.a f6487a;

    r(SettingsAcitivty.a aVar) {
        this.f6487a = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        n.a(this.f6487a.f6034a, false);
    }
}
