package com.miui.permcenter.permissions;

import com.miui.permcenter.permissions.AppSensitivePermsEditorPreference;

class m implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ com.miui.permcenter.privacymanager.b.m f6274a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppSensitivePermsEditorPreference.a f6275b;

    m(AppSensitivePermsEditorPreference.a aVar, com.miui.permcenter.privacymanager.b.m mVar) {
        this.f6275b = aVar;
        this.f6274a = mVar;
    }

    public void run() {
        this.f6274a.a();
    }
}
