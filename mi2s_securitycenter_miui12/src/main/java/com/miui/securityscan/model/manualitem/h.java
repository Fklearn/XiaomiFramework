package com.miui.securityscan.model.manualitem;

import android.content.Context;
import b.b.c.j.A;
import com.miui.securitycenter.R;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7799a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ TelecomOperatorModel f7800b;

    h(TelecomOperatorModel telecomOperatorModel, Context context) {
        this.f7800b = telecomOperatorModel;
        this.f7799a = context;
    }

    public void run() {
        A.a(this.f7799a, (int) R.string.toast_auto_traffic_correct);
    }
}
