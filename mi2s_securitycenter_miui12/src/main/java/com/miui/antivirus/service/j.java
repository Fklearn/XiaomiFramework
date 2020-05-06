package com.miui.antivirus.service;

import android.net.wifi.WifiInfo;
import b.b.c.j.d;
import com.miui.antivirus.service.GuardService;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;

class j implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WifiInfo f2901a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GuardService.b f2902b;

    j(GuardService.b bVar, WifiInfo wifiInfo) {
        this.f2902b = bVar;
        this.f2901a = wifiInfo;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        d.a(new i(this, iAntiVirusServer));
    }
}
