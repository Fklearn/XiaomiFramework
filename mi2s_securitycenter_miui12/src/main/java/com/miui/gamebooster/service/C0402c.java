package com.miui.gamebooster.service;

import android.content.Context;

/* renamed from: com.miui.gamebooster.service.c  reason: case insensitive filesystem */
class C0402c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f4807a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f4808b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C0403d f4809c;

    C0402c(C0403d dVar, String str, int i) {
        this.f4809c = dVar;
        this.f4807a = str;
        this.f4808b = i;
    }

    public void run() {
        GameBoosterService gameBoosterService = this.f4809c.f4810a;
        if (r.a((Context) gameBoosterService, gameBoosterService.i).d()) {
            GameBoosterService gameBoosterService2 = this.f4809c.f4810a;
            r.a((Context) gameBoosterService2, gameBoosterService2.i).a(this.f4807a);
            GameBoosterService gameBoosterService3 = this.f4809c.f4810a;
            r.a((Context) gameBoosterService3, gameBoosterService3.i).c(this.f4808b);
            this.f4809c.f4810a.n();
        }
    }
}
