package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import android.widget.CheckBox;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;

/* renamed from: com.miui.gamebooster.ui.l  reason: case insensitive filesystem */
class C0435l implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CheckBox f5083a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f5084b;

    C0435l(N n, CheckBox checkBox) {
        this.f5084b = n;
        this.f5083a = checkBox;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        C0373d.r(MiStat.Event.CLICK, "cancle");
        if (this.f5083a.isChecked()) {
            C0373d.r(MiStat.Event.CLICK, "not_remind");
            b.b("gamebooster_netbooster_wifi_open_nomore", true);
        }
        this.f5084b.o();
    }
}
