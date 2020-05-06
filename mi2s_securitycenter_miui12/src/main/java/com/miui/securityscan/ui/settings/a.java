package com.miui.securityscan.ui.settings;

import android.content.Context;
import com.miui.securityscan.i.p;

class a extends Thread {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f8026a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ b f8027b;

    a(b bVar, boolean z) {
        this.f8027b = bVar;
        this.f8026a = z;
    }

    public void run() {
        super.run();
        Context applicationContext = this.f8027b.r.getApplicationContext();
        if (!this.f8027b.s.isFinishing() && !this.f8027b.s.isDestroyed()) {
            p.a(applicationContext, this.f8027b.r.getPackageName(), this.f8026a);
        }
    }
}
