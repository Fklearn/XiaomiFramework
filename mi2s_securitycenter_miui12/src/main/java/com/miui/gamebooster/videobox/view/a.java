package com.miui.gamebooster.videobox.view;

import android.view.View;

class a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DetailSettingsLayout f5239a;

    a(DetailSettingsLayout detailSettingsLayout) {
        this.f5239a = detailSettingsLayout;
    }

    public void onClick(View view) {
        if (this.f5239a.j != null) {
            this.f5239a.j.a(this.f5239a.f);
        }
    }
}
