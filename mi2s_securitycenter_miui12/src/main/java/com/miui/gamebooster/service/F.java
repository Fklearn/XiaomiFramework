package com.miui.gamebooster.service;

import android.hardware.display.DisplayManager;
import android.util.Log;

class F implements DisplayManager.DisplayListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4756a;

    F(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4756a = gameBoxWindowManagerService;
    }

    public void onDisplayAdded(int i) {
    }

    public void onDisplayChanged(int i) {
        Log.i("GameBoxWindowManager", "onDisplayChanged:" + this.f4756a.h);
        int b2 = this.f4756a.h;
        if (b2 != 1) {
            if (b2 == 2) {
                this.f4756a.o();
                return;
            } else if (b2 != 3 || System.currentTimeMillis() - this.f4756a.f4774c.c() <= 1500) {
                return;
            }
        }
        this.f4756a.m();
    }

    public void onDisplayRemoved(int i) {
    }
}
