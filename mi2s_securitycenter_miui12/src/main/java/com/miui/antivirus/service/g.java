package com.miui.antivirus.service;

import com.miui.guardprovider.aidl.IAntiVirusServer;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IAntiVirusServer f2896a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ h f2897b;

    g(h hVar, IAntiVirusServer iAntiVirusServer) {
        this.f2897b = hVar;
        this.f2896a = iAntiVirusServer;
    }

    public void run() {
        GuardService.this.f2875d.a(this.f2896a);
    }
}
