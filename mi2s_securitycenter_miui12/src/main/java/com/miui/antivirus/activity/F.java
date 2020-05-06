package com.miui.antivirus.activity;

import com.miui.antivirus.activity.SettingsActivity;
import com.miui.guardprovider.aidl.UpdateInfo;

class F implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsActivity.c f2660a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ UpdateInfo f2661b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ SettingsActivity.e f2662c;

    F(SettingsActivity.e eVar, SettingsActivity.c cVar, UpdateInfo updateInfo) {
        this.f2662c = eVar;
        this.f2660a = cVar;
        this.f2661b = updateInfo;
    }

    public void run() {
        this.f2660a.a(this.f2661b);
    }
}
