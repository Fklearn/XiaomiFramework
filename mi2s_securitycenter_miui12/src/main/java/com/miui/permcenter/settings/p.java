package com.miui.permcenter.settings;

import android.content.DialogInterface;

class p implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyMonitorOpenActivity f6560a;

    p(PrivacyMonitorOpenActivity privacyMonitorOpenActivity) {
        this.f6560a = privacyMonitorOpenActivity;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f6560a.finish();
    }
}
