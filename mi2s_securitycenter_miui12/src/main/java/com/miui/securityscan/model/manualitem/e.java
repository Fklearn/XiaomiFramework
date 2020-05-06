package com.miui.securityscan.model.manualitem;

import android.content.Context;
import b.b.c.j.A;
import com.miui.securitycenter.R;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7793a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ DataNotificationModel f7794b;

    e(DataNotificationModel dataNotificationModel, Context context) {
        this.f7794b = dataNotificationModel;
        this.f7793a = context;
    }

    public void run() {
        A.a(this.f7793a, (int) R.string.toast_flow_notification);
    }
}
