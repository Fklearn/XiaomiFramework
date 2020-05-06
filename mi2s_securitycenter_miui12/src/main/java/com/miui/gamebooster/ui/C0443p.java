package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;

/* renamed from: com.miui.gamebooster.ui.p  reason: case insensitive filesystem */
class C0443p implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f5095a;

    C0443p(N n) {
        this.f5095a = n;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f5095a.f();
        C0373d.j(MiStat.Event.CLICK, "renew_now");
    }
}
