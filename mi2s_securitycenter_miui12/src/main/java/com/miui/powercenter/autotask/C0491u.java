package com.miui.powercenter.autotask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/* renamed from: com.miui.powercenter.autotask.u  reason: case insensitive filesystem */
class C0491u extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AutoTaskManageActivity f6767a;

    C0491u(AutoTaskManageActivity autoTaskManageActivity) {
        this.f6767a = autoTaskManageActivity;
    }

    public void onReceive(Context context, Intent intent) {
        if ("com.miui.powercenter.action.TASK_DELETE".equals(intent.getAction())) {
            this.f6767a.getLoaderManager().restartLoader(300, (Bundle) null, this.f6767a);
        }
    }
}
