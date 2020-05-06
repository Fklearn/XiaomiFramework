package com.miui.gamebooster.service;

import android.content.Context;
import com.miui.gamebooster.service.GameBoosterService;

/* renamed from: com.miui.gamebooster.service.k  reason: case insensitive filesystem */
class C0410k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f4822a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoosterService.GameBoosterBinder f4823b;

    C0410k(GameBoosterService.GameBoosterBinder gameBoosterBinder, int i) {
        this.f4823b = gameBoosterBinder;
        this.f4822a = i;
    }

    public void run() {
        GameBoosterService gameBoosterService = GameBoosterService.this;
        r.a((Context) gameBoosterService, gameBoosterService.i).b(this.f4822a);
    }
}
