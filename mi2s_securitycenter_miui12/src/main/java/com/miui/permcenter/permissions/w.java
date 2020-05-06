package com.miui.permcenter.permissions;

import android.content.DialogInterface;

class w implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f6300a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PermissionAppsEditorActivity f6301b;

    w(PermissionAppsEditorActivity permissionAppsEditorActivity, boolean z) {
        this.f6301b = permissionAppsEditorActivity;
        this.f6300a = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f6301b.a(this.f6300a);
    }
}
