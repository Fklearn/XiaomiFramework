package com.miui.applicationlock;

class W implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3229a;

    W(ConfirmAccessControl confirmAccessControl) {
        this.f3229a = confirmAccessControl;
    }

    public void run() {
        this.f3229a.finish();
    }
}
