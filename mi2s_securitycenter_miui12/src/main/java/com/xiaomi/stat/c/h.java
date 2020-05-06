package com.xiaomi.stat.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class h extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f8488a;

    h(g gVar) {
        this.f8488a = gVar;
    }

    public void onReceive(Context context, Intent intent) {
        this.f8488a.sendEmptyMessage(3);
    }
}
