package com.miui.gamebooster.service;

class A implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4751a;

    A(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4751a = gameBoxWindowManagerService;
    }

    public void run() {
        if (this.f4751a.f4775d != null) {
            this.f4751a.f4775d.a();
        }
    }
}
