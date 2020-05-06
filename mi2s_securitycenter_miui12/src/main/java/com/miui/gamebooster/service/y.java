package com.miui.gamebooster.service;

class y implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4841a;

    y(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4841a = gameBoxWindowManagerService;
    }

    public void run() {
        GameBoxWindowManagerService gameBoxWindowManagerService = this.f4841a;
        if (gameBoxWindowManagerService.e) {
            gameBoxWindowManagerService.f4774c.b();
        }
    }
}
