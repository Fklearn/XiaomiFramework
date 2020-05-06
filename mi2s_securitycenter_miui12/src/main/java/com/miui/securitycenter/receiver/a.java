package com.miui.securitycenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import b.b.b.d.n;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.activityutil.ActivityUtil;
import com.miui.securitycenter.h;
import miui.os.Build;

public class a extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("ConnectivityChangeReceiver", "receive broadcast");
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("android.net.conn.CONNECTIVITY_CHANGE"));
            ActivityUtil.setAllowNetworking(context, h.i());
            ActivityUtil.delayedUpload(context, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            if (!Build.IS_INTERNATIONAL_BUILD) {
                n.m(context);
            }
        }
    }
}
