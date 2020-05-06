package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;

/* renamed from: com.miui.gamebooster.ui.q  reason: case insensitive filesystem */
class C0445q implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f5098a;

    C0445q(N n) {
        this.f5098a = n;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        C0373d.j(MiStat.Event.CLICK, "cancel");
    }
}
