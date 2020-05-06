package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;

class r implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f5101a;

    r(N n) {
        this.f5101a = n;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f5101a.f();
        C0373d.o(MiStat.Event.CLICK, "renew_now");
    }
}
