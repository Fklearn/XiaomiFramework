package com.miui.antivirus.activity;

import b.b.c.j.d;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;

/* renamed from: com.miui.antivirus.activity.f  reason: case insensitive filesystem */
class C0234f implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2718a;

    C0234f(MainActivity mainActivity) {
        this.f2718a = mainActivity;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        boolean unused = this.f2718a.e = false;
        d.a(new C0233e(this, iAntiVirusServer));
    }
}
