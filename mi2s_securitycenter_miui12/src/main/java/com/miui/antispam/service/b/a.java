package com.miui.antispam.service.b;

import android.util.Log;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b f2414a;

    a(b bVar) {
        this.f2414a = bVar;
    }

    public void run() {
        Log.i("CloudPhoneListService", "start doUpdate ...");
        this.f2414a.d();
        Log.i("CloudPhoneListService", "end doUpdate ...");
        this.f2414a.b(false);
    }
}
