package com.miui.privacyapps.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class a extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyAppsActivity f7394a;

    a(PrivacyAppsActivity privacyAppsActivity) {
        this.f7394a = privacyAppsActivity;
    }

    public void onReceive(Context context, Intent intent) {
        this.f7394a.finish();
    }
}
