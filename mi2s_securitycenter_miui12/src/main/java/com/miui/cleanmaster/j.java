package com.miui.cleanmaster;

import android.util.Log;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f3753a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ InstallCallBack f3754b;

    j(InstallCallBack installCallBack, int i) {
        this.f3754b = installCallBack;
        this.f3753a = i;
    }

    public void run() {
        if (this.f3754b.f3735a == null) {
            Log.i("InstallCallBack", "mCallBack is Null.");
            return;
        }
        i a2 = this.f3754b.f3735a;
        boolean z = true;
        if (1 != this.f3753a) {
            z = false;
        }
        a2.a(z, this.f3753a);
    }
}
