package com.miui.antivirus.service;

import b.b.b.a.b;
import com.miui.guardprovider.aidl.UpdateInfo;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ UpdateInfo f2904a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ m f2905b;

    l(m mVar, UpdateInfo updateInfo) {
        this.f2905b = mVar;
        this.f2904a = updateInfo;
    }

    public void run() {
        String str;
        String str2;
        UpdateInfo updateInfo = this.f2904a;
        int i = updateInfo.updateResult;
        if (i == 0 || i == 3) {
            this.f2905b.f2906c.a(System.currentTimeMillis(), this.f2904a.engineName);
            this.f2905b.f2906c.b(System.currentTimeMillis());
            str2 = this.f2904a.engineName;
            str = "success";
        } else if (i == 2) {
            str2 = updateInfo.engineName;
            str = "fail";
        } else {
            return;
        }
        b.a.a(str2, str);
    }
}
