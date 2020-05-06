package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.common.persistence.b;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.d.f;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;
import miui.app.AlertDialog;

class K implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4920a;

    K(N n) {
        this.f4920a = n;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        C0373d.n(MiStat.Event.CLICK, "open_now");
        if (((AlertDialog) dialogInterface).isChecked()) {
            C0373d.n(MiStat.Event.CLICK, "not_remind");
            b.b("gamebooster_netbooster_open_nomore", true);
        }
        a.T(true);
        this.f4920a.f4942d.a(f.OPEN);
        N n = this.f4920a;
        n.b(n.G);
    }
}
