package com.miui.appmanager;

import android.content.Context;
import android.content.pm.LauncherApps;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private LauncherApps f3673a;

    /* renamed from: b  reason: collision with root package name */
    private LauncherApps.Callback f3674b;

    public g(Context context) {
        this.f3673a = (LauncherApps) context.getSystemService("launcherapps");
    }

    public void a() {
        LauncherApps.Callback callback = this.f3674b;
        if (callback != null) {
            this.f3673a.unregisterCallback(callback);
        }
    }

    public void a(h hVar) {
        LauncherApps launcherApps = this.f3673a;
        C0323f fVar = new C0323f(this, hVar);
        this.f3674b = fVar;
        launcherApps.registerCallback(fVar);
    }
}
