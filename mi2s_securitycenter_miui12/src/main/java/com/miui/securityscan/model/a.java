package com.miui.securityscan.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.securityscan.model.ModelUpdater;

class a extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ModelUpdater f7763a;

    a(ModelUpdater modelUpdater) {
        this.f7763a = modelUpdater;
    }

    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("com.miui.securitycenter.action.ITEM_UPDATE")) {
            return;
        }
        if (this.f7763a.mIsDownloading) {
            Log.i(ModelUpdater.TAG, "ModelUpdater is downloading...");
            return;
        }
        ModelUpdater modelUpdater = this.f7763a;
        new ModelUpdater.a(modelUpdater.mContext).executeOnExecutor(ModelUpdater.mThreadPool, new Void[0]);
    }
}
