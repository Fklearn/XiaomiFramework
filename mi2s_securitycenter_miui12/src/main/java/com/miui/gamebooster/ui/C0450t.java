package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;
import miui.app.AlertDialog;

/* renamed from: com.miui.gamebooster.ui.t  reason: case insensitive filesystem */
class C0450t implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f5107a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f5108b;

    C0450t(N n, boolean z) {
        this.f5108b = n;
        this.f5107a = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        String str;
        if (((AlertDialog) dialogInterface).isChecked()) {
            if (!this.f5107a) {
                C0373d.l(MiStat.Event.CLICK, "not_remind");
                str = "gamebooster_free_send_netbooster_open_nomore";
            } else {
                C0373d.h(MiStat.Event.CLICK, "not_remind");
                str = "gt_xunyou_net_booster_try_again_dialog_show_again";
            }
            b.b(str, true);
        }
        this.f5108b.f();
        if (!this.f5107a) {
            C0373d.l(MiStat.Event.CLICK, "open_now");
        } else {
            C0373d.h(MiStat.Event.CLICK, "open_now");
        }
    }
}
