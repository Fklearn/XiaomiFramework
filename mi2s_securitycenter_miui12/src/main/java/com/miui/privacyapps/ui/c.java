package com.miui.privacyapps.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class c extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyAppsManageActivity f7396a;

    c(PrivacyAppsManageActivity privacyAppsManageActivity) {
        this.f7396a = privacyAppsManageActivity;
    }

    public void onReceive(Context context, Intent intent) {
        this.f7396a.finish();
    }
}
