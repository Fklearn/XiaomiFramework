package com.miui.securityscan.model.manualitem;

import android.content.Context;
import b.b.c.j.A;
import com.miui.securitycenter.R;
import com.miui.securityscan.MainActivity;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7791a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ DarkModel f7792b;

    d(DarkModel darkModel, Context context) {
        this.f7792b = darkModel;
        this.f7791a = context;
    }

    public void run() {
        A.a(this.f7791a, (int) R.string.toast_dark_mode_fixed);
        Context context = this.f7791a;
        if (context instanceof MainActivity) {
            ((MainActivity) context).o();
        }
    }
}
