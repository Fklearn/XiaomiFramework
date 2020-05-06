package com.miui.gamebooster.service;

import android.util.Log;
import com.miui.gamebooster.m.na;

class B implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4752a;

    B(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4752a = gameBoxWindowManagerService;
    }

    public void run() {
        int g = na.g(this.f4752a);
        if (this.f4752a.f4775d != null && this.f4752a.i != g) {
            this.f4752a.o();
            Log.i("GameBoxWindowManager", "WINDOWTYPE_FIRSTENTERDIALOG_reCreateWindowView");
        }
    }
}
