package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.gamebooster.d.g;
import com.miui.securitycenter.h;

/* renamed from: com.miui.gamebooster.ui.o  reason: case insensitive filesystem */
class C0441o implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f5091a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f5092b;

    C0441o(N n, g gVar) {
        this.f5092b = n;
        this.f5091a = gVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        h.b(true);
        int i2 = C.f4863b[this.f5091a.ordinal()];
        if (i2 == 1) {
            this.f5092b.m();
        } else if (i2 == 2) {
            this.f5092b.h();
        }
    }
}
