package com.miui.applicationlock;

import android.view.View;
import com.miui.applicationlock.c.o;

/* renamed from: com.miui.applicationlock.ca  reason: case insensitive filesystem */
class C0268ca implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3339a;

    C0268ca(ConfirmAccessControl confirmAccessControl) {
        this.f3339a = confirmAccessControl;
    }

    public void run() {
        o.a((View) this.f3339a.G);
    }
}
