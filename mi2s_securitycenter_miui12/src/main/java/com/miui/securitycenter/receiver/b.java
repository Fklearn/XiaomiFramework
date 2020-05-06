package com.miui.securitycenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.securityscan.a.G;

public class b extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("ExternalComponentBroadcast", "receive broadcast");
        if ("com.miui.securitycenter.action.TRACK_CLOUD_COUNT".equals(intent.getAction())) {
            G.o();
        }
    }
}
