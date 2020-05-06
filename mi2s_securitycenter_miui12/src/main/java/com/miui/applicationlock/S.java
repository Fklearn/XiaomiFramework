package com.miui.applicationlock;

class S implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3213a;

    S(ConfirmAccessControl confirmAccessControl) {
        this.f3213a = confirmAccessControl;
    }

    public void run() {
        this.f3213a.L();
    }
}
