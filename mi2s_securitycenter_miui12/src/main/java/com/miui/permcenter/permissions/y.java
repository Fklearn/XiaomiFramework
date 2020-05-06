package com.miui.permcenter.permissions;

import android.view.View;
import com.miui.permcenter.a;
import com.miui.permcenter.permissions.PermissionAppsEditorActivity;

class y implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f6304a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PermissionAppsEditorActivity.a f6305b;

    y(PermissionAppsEditorActivity.a aVar, int i) {
        this.f6305b = aVar;
        this.f6304a = i;
    }

    public void onClick(View view) {
        this.f6305b.e.a(this.f6304a, view, (a) this.f6305b.f6233d.get(this.f6304a));
    }
}
