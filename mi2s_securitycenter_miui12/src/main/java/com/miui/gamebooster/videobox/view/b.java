package com.miui.gamebooster.videobox.view;

import android.view.View;

class b implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DetailSettingsLayout f5240a;

    b(DetailSettingsLayout detailSettingsLayout) {
        this.f5240a = detailSettingsLayout;
    }

    public void onClick(View view) {
        if (this.f5240a.j != null) {
            this.f5240a.j.c(this.f5240a.f);
        }
    }
}
