package com.miui.permcenter;

import android.content.DialogInterface;
import com.miui.permcenter.SettingsAcitivty;

class q implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsAcitivty.a f6486a;

    q(SettingsAcitivty.a aVar) {
        this.f6486a = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f6486a.f6035b.setChecked(true);
    }
}
