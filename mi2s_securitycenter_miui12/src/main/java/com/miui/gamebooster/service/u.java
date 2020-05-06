package com.miui.gamebooster.service;

import com.miui.gamebooster.service.IGameBoosterTelecomeManager;

class u extends IGameBoosterTelecomeManager.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterTelecomManager f4837a;

    u(GameBoosterTelecomManager gameBoosterTelecomManager) {
        this.f4837a = gameBoosterTelecomManager;
    }

    public void P() {
        this.f4837a.f4763a.post(new s(this));
    }

    public void u() {
        this.f4837a.f4763a.post(new t(this));
    }
}
