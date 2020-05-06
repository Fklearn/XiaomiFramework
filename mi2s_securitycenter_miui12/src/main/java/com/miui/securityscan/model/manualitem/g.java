package com.miui.securityscan.model.manualitem;

import android.content.Context;
import b.b.c.j.A;
import com.miui.securitycenter.R;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7797a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SecurityNotificationModel f7798b;

    g(SecurityNotificationModel securityNotificationModel, Context context) {
        this.f7798b = securityNotificationModel;
        this.f7797a = context;
    }

    public void run() {
        A.a(this.f7797a, (int) R.string.toast_show_notification);
    }
}
