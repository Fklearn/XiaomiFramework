package com.miui.gamebooster.gbservices;

import com.miui.gamebooster.service.IGameBoosterTelecomeManager;

class z implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C f4392a;

    z(C c2) {
        this.f4392a = c2;
    }

    public void run() {
        try {
            if (this.f4392a.i != null) {
                this.f4392a.i.u();
            }
            this.f4392a.f4317b.unbindService(this.f4392a.l);
            IGameBoosterTelecomeManager unused = this.f4392a.i = null;
        } catch (Exception unused2) {
        }
    }
}
