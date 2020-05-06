package com.miui.gamebooster.service;

import com.miui.gamebooster.service.GameBoosterService;

/* renamed from: com.miui.gamebooster.service.j  reason: case insensitive filesystem */
class C0409j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f4820a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoosterService.GameBoosterBinder f4821b;

    C0409j(GameBoosterService.GameBoosterBinder gameBoosterBinder, String str) {
        this.f4821b = gameBoosterBinder;
        this.f4820a = str;
    }

    public void run() {
        if (GameBoosterService.this.s.booleanValue()) {
            this.f4821b.a(this.f4820a);
        }
    }
}
