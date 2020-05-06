package com.miui.gamebooster.gamead;

import com.miui.gamebooster.ui.GameBoosterRealMainActivity;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f4296a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d f4297b;

    c(d dVar, GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f4297b = dVar;
        this.f4296a = gameBoosterRealMainActivity;
    }

    public void run() {
        this.f4296a.a((e) this.f4297b);
    }
}
