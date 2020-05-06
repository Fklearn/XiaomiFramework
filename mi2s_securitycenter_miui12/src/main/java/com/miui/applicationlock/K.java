package com.miui.applicationlock;

import com.miui.applicationlock.ChooseAccessControl;

class K implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ChooseAccessControl f3185a;

    K(ChooseAccessControl chooseAccessControl) {
        this.f3185a = chooseAccessControl;
    }

    public void run() {
        this.f3185a.a(ChooseAccessControl.c.NeedToConfirm);
    }
}
