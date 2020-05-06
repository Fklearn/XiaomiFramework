package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import android.util.Log;
import android.widget.CheckBox;
import com.miui.common.persistence.b;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;

class M implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CheckBox f4924a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f4925b;

    M(N n, CheckBox checkBox) {
        this.f4925b = n;
        this.f4924a = checkBox;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        C0373d.r(MiStat.Event.CLICK, "open_now");
        if (this.f4924a.isChecked()) {
            C0373d.r(MiStat.Event.CLICK, "not_remind");
            b.b("gamebooster_netbooster_wifi_open_nomore", true);
        }
        try {
            this.f4925b.o.setSettingEx("xunyou", "xunyou_wifi_accel_switch", "true");
            a.U(true);
        } catch (Exception e) {
            Log.i(N.f4939a, e.toString());
        }
        b.b.o.f.c.a.a(this.f4925b.mAppContext).a(true);
        this.f4925b.o();
    }
}
