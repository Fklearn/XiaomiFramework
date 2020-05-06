package com.miui.permcenter.permissions;

import android.content.DialogInterface;

/* renamed from: com.miui.permcenter.permissions.e  reason: case insensitive filesystem */
class C0468e implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppPermissionsTabActivity f6260a;

    C0468e(AppPermissionsTabActivity appPermissionsTabActivity) {
        this.f6260a = appPermissionsTabActivity;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        int unused = this.f6260a.f6196a = 0;
    }
}
