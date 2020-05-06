package com.miui.powercenter.batteryhistory;

import android.os.AsyncTask;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.W;

class V implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0514s.a f6851a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ W.a f6852b;

    V(W.a aVar, C0514s.a aVar2) {
        this.f6852b = aVar;
        this.f6851a = aVar2;
    }

    public void run() {
        if (W.this.f6854b != null) {
            W.this.f6854b.a(this.f6851a.f6922a, AsyncTask.THREAD_POOL_EXECUTOR);
            W.this.f6854b.invalidate();
        }
    }
}
