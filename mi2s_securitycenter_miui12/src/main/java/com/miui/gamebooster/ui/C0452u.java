package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;
import miui.app.AlertDialog;

/* renamed from: com.miui.gamebooster.ui.u  reason: case insensitive filesystem */
class C0452u implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f5110a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f5111b;

    C0452u(N n, boolean z) {
        this.f5111b = n;
        this.f5110a = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        String str;
        if (((AlertDialog) dialogInterface).isChecked()) {
            if (!this.f5110a) {
                C0373d.l(MiStat.Event.CLICK, "not_remind");
                str = "gamebooster_free_send_netbooster_open_nomore";
            } else {
                C0373d.h(MiStat.Event.CLICK, "not_remind");
                str = "gt_xunyou_net_booster_try_again_dialog_show_again";
            }
            b.b(str, true);
        }
        if (!this.f5110a) {
            C0373d.l(MiStat.Event.CLICK, "cancle");
        } else {
            C0373d.h(MiStat.Event.CLICK, "cancle");
        }
    }
}
