package com.miui.gamebooster.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class J extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ W f4140a;

    J(W w) {
        this.f4140a = w;
    }

    public void onReceive(Context context, Intent intent) {
        this.f4140a.b();
    }
}
