package com.miui.securityscan.model.manualitem;

import android.content.Context;
import b.b.c.j.A;
import com.miui.securitycenter.R;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7795a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ NetworkSwitchModel f7796b;

    f(NetworkSwitchModel networkSwitchModel, Context context) {
        this.f7796b = networkSwitchModel;
        this.f7795a = context;
    }

    public void run() {
        A.a(this.f7795a, (int) R.string.toast_network_switch);
    }
}
