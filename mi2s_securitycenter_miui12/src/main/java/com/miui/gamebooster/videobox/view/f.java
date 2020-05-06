package com.miui.gamebooster.videobox.view;

import android.view.View;

class f implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsDescLayout f5243a;

    f(SettingsDescLayout settingsDescLayout) {
        this.f5243a = settingsDescLayout;
    }

    public void onClick(View view) {
        this.f5243a.setVisibility(8);
        if (this.f5243a.f != null) {
            this.f5243a.f.b(this.f5243a.e);
        }
    }
}
