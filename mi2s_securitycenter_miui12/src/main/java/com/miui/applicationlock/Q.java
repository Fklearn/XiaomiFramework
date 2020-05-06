package com.miui.applicationlock;

class Q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3208a;

    Q(ConfirmAccessControl confirmAccessControl) {
        this.f3208a = confirmAccessControl;
    }

    public void run() {
        if (this.f3208a.Ka.b()) {
            this.f3208a.Ka.a(this.f3208a.Wa);
        }
    }
}
