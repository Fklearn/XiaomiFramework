package com.miui.securityscan.model.manualitem;

import android.content.Context;
import b.b.c.j.A;
import com.miui.securitycenter.R;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7787a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CleanerCloudScanModel f7788b;

    b(CleanerCloudScanModel cleanerCloudScanModel, Context context) {
        this.f7788b = cleanerCloudScanModel;
        this.f7787a = context;
    }

    public void run() {
        A.a(this.f7787a, (int) R.string.toast_cleaner_cloudscan);
    }
}
