package com.miui.powercenter.e;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.powercenter.utils.o;

public class a extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private boolean f7060a = true;

    public void onReceive(Context context, Intent intent) {
        if ("miui.intent.action.HANG_UP_CHANGED".equals(intent.getAction())) {
            boolean booleanExtra = intent.getBooleanExtra("hang_up_enable", false);
            if (booleanExtra) {
                if (o.l(context)) {
                    this.f7060a = true;
                    return;
                }
                this.f7060a = false;
            } else if (this.f7060a) {
                return;
            }
            o.a(context, booleanExtra);
            Log.i("HangUpChangeReceiver", "receive broadcast ACTION_HANG_UP_CHANGED " + booleanExtra);
        }
    }
}
