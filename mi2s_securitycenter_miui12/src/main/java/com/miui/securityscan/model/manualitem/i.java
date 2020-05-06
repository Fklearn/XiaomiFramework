package com.miui.securityscan.model.manualitem;

import android.content.Context;
import b.b.c.j.A;
import com.miui.securitycenter.R;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7801a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ VirusCloudScanModel f7802b;

    i(VirusCloudScanModel virusCloudScanModel, Context context) {
        this.f7802b = virusCloudScanModel;
        this.f7801a = context;
    }

    public void run() {
        A.a(this.f7801a, (int) R.string.toast_cleaner_cloudscan);
    }
}
