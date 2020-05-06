package com.miui.permcenter.settings.model;

import android.widget.PopupWindow;

class j implements PopupWindow.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionUseTotalPreference f6552a;

    j(PermissionUseTotalPreference permissionUseTotalPreference) {
        this.f6552a = permissionUseTotalPreference;
    }

    public void onDismiss() {
        this.f6552a.g.setVisibility(8);
        this.f6552a.s.a();
    }
}
