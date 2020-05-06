package com.miui.superpower.statusbar;

import android.view.View;

class j implements View.OnLongClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WifiViewLinearLayout f8197a;

    j(WifiViewLinearLayout wifiViewLinearLayout) {
        this.f8197a = wifiViewLinearLayout;
    }

    public boolean onLongClick(View view) {
        this.f8197a.a();
        return true;
    }
}
