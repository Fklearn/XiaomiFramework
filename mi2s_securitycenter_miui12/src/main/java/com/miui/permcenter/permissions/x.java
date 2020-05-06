package com.miui.permcenter.permissions;

import com.miui.permcenter.a;
import com.miui.permcenter.n;

class x implements n.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ a f6302a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PermissionAppsEditorActivity f6303b;

    x(PermissionAppsEditorActivity permissionAppsEditorActivity, a aVar) {
        this.f6303b = permissionAppsEditorActivity;
        this.f6302a = aVar;
    }

    public void a(String str, int i) {
        this.f6302a.f().put(Long.valueOf(this.f6303b.f6226a), Integer.valueOf(i));
        this.f6303b.f6227b.notifyDataSetChanged();
    }
}
