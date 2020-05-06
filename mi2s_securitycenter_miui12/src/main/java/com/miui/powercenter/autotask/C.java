package com.miui.powercenter.autotask;

import android.content.DialogInterface;
import com.miui.powercenter.autotask.X;

class C implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ X.a f6692a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ D f6693b;

    C(D d2, X.a aVar) {
        this.f6693b = d2;
        this.f6692a = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.f6693b.f6703b.setOperation("brightness", Integer.valueOf(this.f6693b.f6704c.getProgress()));
        } else if (i == -2) {
            this.f6693b.f6703b.removeOperation("brightness");
        } else {
            return;
        }
        this.f6692a.a("brightness");
    }
}
