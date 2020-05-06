package com.miui.gamebooster.ui;

import android.content.Context;
import android.util.Log;
import android.widget.CompoundButton;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0384o;

class bb implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WifiBoosterDetail f5051a;

    bb(WifiBoosterDetail wifiBoosterDetail) {
        this.f5051a = wifiBoosterDetail;
    }

    /* JADX WARNING: type inference failed for: r4v12, types: [android.content.Context, com.miui.gamebooster.ui.WifiBoosterDetail] */
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (this.f5051a.i.equals("action_detail_wifibooster")) {
            try {
                this.f5051a.f5031b.setSettingEx("xunyou", "xunyou_wifi_accel_switch", String.valueOf(z));
                a.U(z);
            } catch (Exception e) {
                Log.i(WifiBoosterDetail.f5030a, e.toString());
            }
            if (z) {
                b.b.o.f.c.a.a((Context) this.f5051a).a(true);
            }
        } else if (this.f5051a.i.equals("action_handsfree_mute")) {
            a.O(z);
        } else if (this.f5051a.i.equals("action_detail_gwsd")) {
            a.K(z);
            if (!z) {
                C0384o.b(this.f5051a.getContentResolver(), "gb_gwsd", 0, -2);
            }
        }
    }
}
