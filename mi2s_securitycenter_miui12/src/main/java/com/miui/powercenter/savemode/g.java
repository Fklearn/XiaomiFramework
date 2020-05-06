package com.miui.powercenter.savemode;

import android.content.DialogInterface;
import com.miui.powercenter.savemode.e;

class g implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f7288a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e.b f7289b;

    g(e.b bVar, e eVar) {
        this.f7289b = bVar;
        this.f7288a = eVar;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f7288a.i.setChecked(true);
    }
}
