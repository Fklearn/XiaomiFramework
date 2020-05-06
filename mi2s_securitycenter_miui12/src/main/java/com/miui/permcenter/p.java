package com.miui.permcenter;

import android.content.DialogInterface;
import com.miui.permcenter.SettingsAcitivty;

class p implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsAcitivty.a f6187a;

    p(SettingsAcitivty.a aVar) {
        this.f6187a = aVar;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f6187a.f6035b.setChecked(true);
    }
}
