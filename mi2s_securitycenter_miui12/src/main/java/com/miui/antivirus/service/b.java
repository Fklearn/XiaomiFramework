package com.miui.antivirus.service;

import android.content.Context;
import android.net.wifi.WifiInfo;
import com.miui.antivirus.ui.y;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WifiInfo f2889a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ DialogService f2890b;

    b(DialogService dialogService, WifiInfo wifiInfo) {
        this.f2890b = dialogService;
        this.f2889a = wifiInfo;
    }

    public void run() {
        y.a((Context) this.f2890b).a(this.f2889a);
    }
}
