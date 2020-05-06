package com.miui.antivirus.service;

import com.miui.guardprovider.aidl.IAntiVirusServer;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IAntiVirusServer f2893a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ f f2894b;

    e(f fVar, IAntiVirusServer iAntiVirusServer) {
        this.f2894b = fVar;
        this.f2893a = iAntiVirusServer;
    }

    public void run() {
        this.f2894b.f2895a.f2875d.a(this.f2893a, this.f2894b.f2895a.f2874c);
    }
}
