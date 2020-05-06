package com.miui.superpower.statusbar;

import android.view.View;

class i implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WifiViewLinearLayout f8177a;

    i(WifiViewLinearLayout wifiViewLinearLayout) {
        this.f8177a = wifiViewLinearLayout;
    }

    public void onClick(View view) {
        if (this.f8177a.isClickable()) {
            this.f8177a.a();
        }
    }
}
