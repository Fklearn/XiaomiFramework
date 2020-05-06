package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;

/* renamed from: com.miui.gamebooster.ui.s  reason: case insensitive filesystem */
class C0448s implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f5103a;

    C0448s(N n) {
        this.f5103a = n;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        C0373d.o(MiStat.Event.CLICK, "cancel");
    }
}
