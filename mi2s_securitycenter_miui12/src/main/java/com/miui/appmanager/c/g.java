package com.miui.appmanager.c;

import com.miui.appmanager.AppManagerMainActivity;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3636a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ i f3637b;

    g(i iVar, AppManagerMainActivity appManagerMainActivity) {
        this.f3637b = iVar;
        this.f3636a = appManagerMainActivity;
    }

    public void run() {
        this.f3636a.n();
    }
}
