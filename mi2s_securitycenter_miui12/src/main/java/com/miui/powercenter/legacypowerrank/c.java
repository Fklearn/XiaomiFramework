package com.miui.powercenter.legacypowerrank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.miui.powercenter.legacypowerrank.PowerDetailActivity;

class c extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerDetailActivity.a f7087a;

    c(PowerDetailActivity.a aVar) {
        this.f7087a = aVar;
    }

    public void onReceive(Context context, Intent intent) {
        boolean unused = this.f7087a.k = getResultCode() != 0;
    }
}
