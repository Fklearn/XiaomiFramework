package com.miui.superpower.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import com.miui.superpower.statusbar.Clock;

class c extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Clock.a f8165a;

    c(Clock.a aVar) {
        this.f8165a = aVar;
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.TIME_SET".equals(intent.getAction())) {
            boolean unused = this.f8165a.f8146b = DateFormat.is24HourFormat(context);
        }
        this.f8165a.f8145a.a();
    }
}
