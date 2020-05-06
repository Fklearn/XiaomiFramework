package com.miui.permcenter.settings;

import android.view.View;

class u implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyProvisionActivity f6576a;

    u(PrivacyProvisionActivity privacyProvisionActivity) {
        this.f6576a = privacyProvisionActivity;
    }

    public void onClick(View view) {
        if (!this.f6576a.f6508a.isPlaying()) {
            this.f6576a.f6508a.start();
        }
    }
}
