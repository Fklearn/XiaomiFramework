package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import b.b.c.j.B;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0393y;
import com.xiaomi.stat.MiStat;
import miui.app.AlertDialog;

class L implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4922a;

    L(N n) {
        this.f4922a = n;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        C0373d.n(MiStat.Event.CLICK, "cancle");
        if (((AlertDialog) dialogInterface).isChecked()) {
            C0373d.n(MiStat.Event.CLICK, "not_remind");
            b.b("gamebooster_netbooster_open_nomore", true);
        }
        if (this.f4922a.z != null) {
            C0393y.a(this.f4922a.mAppContext.getApplicationContext(), ((GameBoosterRealMainActivity) this.f4922a.getActivity()).n(), this.f4922a.z.packageName, B.e(this.f4922a.z.uid / DefaultOggSeeker.MATCH_BYTE_RANGE));
        }
    }
}
