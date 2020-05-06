package com.miui.gamebooster.service;

import android.util.Log;
import com.miui.gamebooster.service.GameBoxWindowManagerService;
import com.miui.gamebooster.videobox.settings.f;

class H implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4777a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f4778b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService.GameBoosterWindowBinder f4779c;

    H(GameBoxWindowManagerService.GameBoosterWindowBinder gameBoosterWindowBinder, boolean z, boolean z2) {
        this.f4779c = gameBoosterWindowBinder;
        this.f4777a = z;
        this.f4778b = z2;
    }

    public void run() {
        GameBoxWindowManagerService.this.f4774c.d(this.f4777a);
        Log.i("GameBoxWindowManager", "run: slip=" + this.f4777a + "\tstartFreeFrom=" + this.f4778b);
        if (this.f4777a && !this.f4778b) {
            GameBoxWindowManagerService.this.f4774c.h();
            GameBoxWindowManagerService.this.f4774c.a(GameBoxWindowManagerService.this.h != 3 || f.e() == 0, true);
        } else if (!this.f4777a) {
            GameBoxWindowManagerService.this.f4774c.i();
        }
    }
}
