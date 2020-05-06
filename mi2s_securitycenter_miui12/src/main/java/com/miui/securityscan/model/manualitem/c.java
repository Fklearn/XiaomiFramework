package com.miui.securityscan.model.manualitem;

import android.content.Context;
import b.b.c.j.A;
import com.miui.securitycenter.R;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7789a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CleanerDbUpdateModel f7790b;

    c(CleanerDbUpdateModel cleanerDbUpdateModel, Context context) {
        this.f7790b = cleanerDbUpdateModel;
        this.f7789a = context;
    }

    public void run() {
        A.a(this.f7789a, (int) R.string.toast_garbage_lib);
    }
}
