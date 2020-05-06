package com.miui.applicationlock;

/* renamed from: com.miui.applicationlock.ea  reason: case insensitive filesystem */
class C0272ea implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3345a;

    C0272ea(ConfirmAccessControl confirmAccessControl) {
        this.f3345a = confirmAccessControl;
    }

    public void run() {
        ConfirmAccessControl confirmAccessControl = this.f3345a;
        confirmAccessControl.b(confirmAccessControl.B);
    }
}
