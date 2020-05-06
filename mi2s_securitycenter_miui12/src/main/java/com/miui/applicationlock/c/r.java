package com.miui.applicationlock.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

class r extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s f3321a;

    r(s sVar) {
        this.f3321a = sVar;
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("BackgroundManager", "update wallpage");
        this.f3321a.a();
    }
}
