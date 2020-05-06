package com.miui.superpower.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

class c extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f8123a;

    c(f fVar) {
        this.f8123a = fVar;
    }

    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra("PREF_KEY_APP_PKG_NAME");
        Message obtain = Message.obtain();
        obtain.obj = stringExtra;
        obtain.what = 5;
        this.f8123a.f.sendMessage(obtain);
    }
}
