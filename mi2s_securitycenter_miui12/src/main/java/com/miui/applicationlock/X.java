package com.miui.applicationlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.networkassistant.config.Constants;

class X extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3231a;

    X(ConfirmAccessControl confirmAccessControl) {
        this.f3231a = confirmAccessControl;
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("ConfirmAccessControl", " onReceive : " + intent.getAction());
        if ("miui.intent.action.APP_LOCK_CLEAR_STATE".equals(intent.getAction())) {
            this.f3231a.P();
            Log.d("ConfirmAccessControl", "unregisterFingerprint 1");
        } else if (Constants.System.ACTION_USER_PRESENT.equals(intent.getAction())) {
            ConfirmAccessControl confirmAccessControl = this.f3231a;
            if (!confirmAccessControl.V && !confirmAccessControl.F && !this.f3231a.F()) {
                Log.d("ConfirmAccessControl", "onReceive register finger");
                this.f3231a.c(150);
            }
        }
    }
}
