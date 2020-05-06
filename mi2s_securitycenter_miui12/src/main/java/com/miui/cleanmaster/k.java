package com.miui.cleanmaster;

import android.util.Log;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f3755a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ InstallCallbackV28 f3756b;

    k(InstallCallbackV28 installCallbackV28, int i) {
        this.f3756b = installCallbackV28;
        this.f3755a = i;
    }

    public void run() {
        if (this.f3756b.f3736a == null) {
            Log.i("InstallCallbackV28", "mCallBack is Null.");
            return;
        }
        i a2 = this.f3756b.f3736a;
        boolean z = true;
        if (1 != this.f3755a) {
            z = false;
        }
        a2.a(z, this.f3755a);
    }
}
