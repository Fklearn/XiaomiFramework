package com.miui.antivirus.service;

import b.b.c.j.d;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;

class f implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GuardService f2895a;

    f(GuardService guardService) {
        this.f2895a = guardService;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        d.a(new e(this, iAntiVirusServer));
    }
}
