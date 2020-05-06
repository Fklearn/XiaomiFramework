package com.miui.appcompatibility;

import android.os.IBinder;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IBinder f3091a;

    k(IBinder iBinder) {
        this.f3091a = iBinder;
    }

    public void run() {
        m.b(this.f3091a);
    }
}
