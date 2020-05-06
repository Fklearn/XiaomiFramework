package com.miui.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class a extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b f7437a;

    a(b bVar) {
        this.f7437a = bVar;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("action_update_sc_network_allow")) {
            b.b(context);
        }
    }
}
