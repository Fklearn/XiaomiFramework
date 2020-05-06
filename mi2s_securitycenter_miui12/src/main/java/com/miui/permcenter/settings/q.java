package com.miui.permcenter.settings;

import android.content.DialogInterface;

class q implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyMonitorOpenActivity f6561a;

    q(PrivacyMonitorOpenActivity privacyMonitorOpenActivity) {
        this.f6561a = privacyMonitorOpenActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f6561a.finish();
    }
}
