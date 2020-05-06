package com.miui.optimizecenter.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

class p extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ StorageActivity f5766a;

    p(StorageActivity storageActivity) {
        this.f5766a = storageActivity;
    }

    public /* synthetic */ void a() {
        this.f5766a.m();
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("StorageActivity", "Volume Changed: " + action);
        if ("android.intent.action.MEDIA_UNMOUNTED".equals(action) || "android.intent.action.MEDIA_MOUNTED".equals(action) || "android.intent.action.MEDIA_BAD_REMOVAL".equals(action) || "android.intent.action.MEDIA_REMOVED".equals(action) || "android.intent.action.MEDIA_EJECT".equals(action) || "android.os.storage.action.VOLUME_STATE_CHANGED".equals(action)) {
            this.f5766a.runOnUiThread(new c(this));
        }
    }
}
