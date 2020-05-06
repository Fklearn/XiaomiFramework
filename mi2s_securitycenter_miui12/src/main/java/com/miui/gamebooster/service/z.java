package com.miui.gamebooster.service;

import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.a;

class z implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4842a;

    z(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4842a = gameBoxWindowManagerService;
    }

    public void run() {
        if (this.f4842a.h == 3 && this.f4842a.e) {
            a.a(f.b());
        }
    }
}
