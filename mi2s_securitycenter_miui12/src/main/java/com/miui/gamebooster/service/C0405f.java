package com.miui.gamebooster.service;

import android.content.Context;
import android.util.Log;

/* renamed from: com.miui.gamebooster.service.f  reason: case insensitive filesystem */
class C0405f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4812a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f4813b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ GameBoosterService f4814c;

    C0405f(GameBoosterService gameBoosterService, boolean z, Context context) {
        this.f4814c = gameBoosterService;
        this.f4812a = z;
        this.f4813b = context;
    }

    public void run() {
        try {
            if (this.f4814c.n != null && this.f4814c.A) {
                this.f4814c.unbindService(this.f4814c.B);
                boolean unused = this.f4814c.A = false;
                if (this.f4812a) {
                    this.f4814c.a(this.f4813b, false);
                }
            }
        } catch (Exception e) {
            Log.i("GameBoosterService", e.toString());
        }
    }
}
