package com.miui.securityscan.scanner;

import android.util.Log;
import b.b.c.f.a;
import com.miui.securityscan.b.g;
import com.miui.securityscan.scanner.U;

class Q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f7862a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f7863b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ U f7864c;

    Q(U u, g gVar, boolean z) {
        this.f7864c = u;
        this.f7862a = gVar;
        this.f7863b = z;
    }

    public void run() {
        Log.d("SystemCheckManager", "SystemCheckManager startScan run()");
        this.f7864c.f7877d.a("com.miui.guardprovider.action.antivirusservice", "com.miui.guardprovider", (a.C0027a) new U.a(this.f7864c.f7875b, this.f7862a, this.f7863b));
    }
}
