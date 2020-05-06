package com.miui.gamebooster.ui;

import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0373d;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.securitycenter.h;
import com.xiaomi.stat.MiStat;
import java.util.Date;

class Y implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f5036a;

    Y(GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f5036a = gameBoosterRealMainActivity;
    }

    public void run() {
        b.b("key_gamebooster_red_point_press", DateUtil.getDateFormat(2).format(new Date()));
        C0373d.e(MiStat.Event.CLICK, "homepage_sign_in");
        if (h.i()) {
            this.f5036a.m();
            this.f5036a.r();
            return;
        }
        this.f5036a.q();
    }
}
