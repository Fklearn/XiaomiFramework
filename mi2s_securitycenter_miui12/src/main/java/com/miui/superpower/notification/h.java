package com.miui.superpower.notification;

import com.miui.powercenter.utils.o;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SuperPowerTileService f8132a;

    h(SuperPowerTileService superPowerTileService) {
        this.f8132a = superPowerTileService;
    }

    public void run() {
        com.miui.superpower.b.h.a("tile");
        o.a(this.f8132a, true, true);
    }
}
