package com.miui.appmanager;

import android.content.pm.LauncherApps;
import android.os.UserHandle;

/* renamed from: com.miui.appmanager.f  reason: case insensitive filesystem */
class C0323f extends LauncherApps.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f3671a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g f3672b;

    C0323f(g gVar, h hVar) {
        this.f3672b = gVar;
        this.f3671a = hVar;
    }

    public void onPackageAdded(String str, UserHandle userHandle) {
    }

    public void onPackageChanged(String str, UserHandle userHandle) {
    }

    public void onPackageRemoved(String str, UserHandle userHandle) {
        this.f3671a.a(str, userHandle);
    }

    public void onPackagesAvailable(String[] strArr, UserHandle userHandle, boolean z) {
    }

    public void onPackagesUnavailable(String[] strArr, UserHandle userHandle, boolean z) {
    }
}
