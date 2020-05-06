package com.miui.antivirus.service;

import b.b.c.j.d;
import com.miui.antivirus.service.GuardService;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;

class h implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GuardService.b f2898a;

    h(GuardService.b bVar) {
        this.f2898a = bVar;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        d.a(new g(this, iAntiVirusServer));
    }
}
