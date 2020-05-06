package com.miui.antivirus.service;

import com.miui.guardprovider.aidl.IAntiVirusServer;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IAntiVirusServer f2899a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f2900b;

    i(j jVar, IAntiVirusServer iAntiVirusServer) {
        this.f2900b = jVar;
        this.f2899a = iAntiVirusServer;
    }

    public void run() {
        GuardService.this.i.a(this.f2899a, this.f2900b.f2901a);
    }
}
