package com.miui.powercenter.autotask;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class AutoTaskIntentService extends IntentService {
    public AutoTaskIntentService() {
        super("AutoTaskIntentService");
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        if ("com.miui.powercenter.action.TASK_UPDATE".equals(intent.getAction()) || "com.miui.powercenter.action.TASK_DELETE".equals(intent.getAction())) {
            intent.setClass(this, C0479h.class);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
