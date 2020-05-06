package com.miui.powercenter.savemode;

import android.content.DialogInterface;
import com.miui.powercenter.savemode.e;

class h implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f7290a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e.b f7291b;

    h(e.b bVar, e eVar) {
        this.f7291b = bVar;
        this.f7290a = eVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        this.f7290a.i.setChecked(true);
    }
}
