package com.miui.privacyapps.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class j extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n f7405a;

    j(n nVar) {
        this.f7405a = nVar;
    }

    public void onReceive(Context context, Intent intent) {
        this.f7405a.q.finish();
    }
}
